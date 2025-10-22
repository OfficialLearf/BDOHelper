package com.example.bdoapp.Util

import com.example.bdoapp.Model.Recipe
import org.jsoup.Jsoup
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class IconScraper {
    private val baseUrl = "https://incendar.com"
    private val alchemyUrl = "$baseUrl/bdoalchemyrecipes.php"
    private val cookingUrl = "$baseUrl/bdocookingrecipes.php"
    private val iconCacheDir = "src/main/resources/icons"

    init {
        File(iconCacheDir).mkdirs()
    }

    // Create a map of item names to icon URLs by parsing the page structure
    private fun scrapeAllIconsFromPage(pageUrl: String): Map<String, String> {
        val iconMap = mutableMapOf<String, String>()

        try {
            println("Scraping page: $pageUrl")
            val doc = Jsoup.connect(pageUrl)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .timeout(10000)
                .get()

            // Get ALL images from the bdo directory
            val images = doc.select("img[src*='/bdo/']")
            println("Found ${images.size} BDO item images on the page")

            images.forEach { img ->
                val src = img.attr("src")
                val alt = img.attr("alt")
                val title = img.attr("title")

                // Convert relative URL to absolute
                var fullUrl = when {
                    src.startsWith("./") -> src.replace("./", "$baseUrl/")
                    src.startsWith("/") -> "$baseUrl$src"
                    src.startsWith("http") -> src
                    else -> "$baseUrl/$src"
                }

                // Clean up any double slashes
                fullUrl = fullUrl.replace("//images", "/images")

                // Get item name - be less restrictive
                val itemName = when {
                    alt.isNotBlank() -> alt.trim()
                    title.isNotBlank() -> title.trim()
                    else -> null
                }

                if (itemName != null && fullUrl.isNotBlank()) {
                    // Store the original name
                    iconMap[itemName] = fullUrl

                    // === NEW: Create simplified name variations ===
                    val simplifiedNames = createNameVariations(itemName)
                    simplifiedNames.forEach { simplifiedName ->
                        if (simplifiedName.isNotBlank()) {
                            iconMap[simplifiedName] = fullUrl
                        }
                    }
                }
            }

            println("Mapped ${iconMap.size} total icons from page")

            // Debug: print first 10 mappings
            println("First 10 mappings:")
            iconMap.entries.take(10).forEach { (name, url) ->
                println("  '$name' -> ${url.substringAfterLast("/")}")
            }

        } catch (e: Exception) {
            println("Error scraping page $pageUrl: ${e.message}")
            e.printStackTrace()
        }

        return iconMap
    }

    // === NEW: Helper function to create name variations ===
    private fun createNameVariations(rawName: String): List<String> {
        val variations = mutableListOf<String>()

        // Remove common suffixes
        val baseName = rawName
            .replace(" ingredient recipe", "")
            .replace(" ingredient", "")
            .replace(" recipe", "")
            .replace(" material", "")
            .trim()

        if (baseName != rawName && baseName.isNotBlank()) {
            variations.add(baseName)
        }

        // Also try removing numbers if present (like "Blood 2" -> "Blood")
        if (baseName.contains("\\d".toRegex())) {
            val withoutNumbers = baseName.replace("\\s*\\d+".toRegex(), "").trim()
            if (withoutNumbers.isNotBlank() && withoutNumbers != baseName) {
                variations.add(withoutNumbers)
            }
        }

        return variations
    }

    // === NEW: Flexible icon URL finder ===
    private fun findIconUrl(iconMap: Map<String, String>, itemName: String): String? {
        // 1. Direct match
        iconMap[itemName]?.let { return it }

        // 2. Try common variations
        val variations = listOf(
            "$itemName ingredient",
            "$itemName ingredient recipe",
            "$itemName recipe",
            "$itemName material",
            itemName.replace(" ", "_"),
            itemName.replace("_", " ")
        )

        for (variation in variations) {
            iconMap[variation]?.let { return it }
        }

        // 3. Try partial matching (if itemName is "Blood 2" and map has "Blood 2 ingredient")
        for ((key, url) in iconMap) {
            if (key.contains(itemName, ignoreCase = true)) {
                println("  Found partial match: '$itemName' -> '$key'")
                return url
            }
        }

        // 4. Try the reverse (if itemName is "Blood" and map has "Blood 2 ingredient")
        for ((key, url) in iconMap) {
            if (itemName.contains(key, ignoreCase = true)) {
                println("  Found reverse partial match: '$itemName' -> '$key'")
                return url
            }
        }

        return null
    }

    // Download a single icon
    private fun downloadIcon(iconUrl: String, itemName: String): String? {
        try {
            val safeFileName = itemName
                .replace(Regex("[^a-zA-Z0-9_\\-' ]"), "")
                .replace(" ", "_")
                .take(100) // Limit filename length

            val extension = iconUrl.substringAfterLast(".", "png").take(4)
            val localPath = "$iconCacheDir/${safeFileName}.$extension"

            val file = File(localPath)

            // Return if already cached
            if (file.exists()) {
                println("✓ Already cached: $itemName")
                return localPath
            }

            // Download the icon
            println("⬇ Downloading: $itemName")
            URL(iconUrl).openStream().use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }

            // Add delay to be respectful
            Thread.sleep(300)

            return localPath
        } catch (e: Exception) {
            println("✗ Error downloading $itemName: ${e.message}")
        }
        return null
    }

    // Download all cooking icons
    fun downloadAllCookingIcons(recipes: List<Recipe>) {
        println("\n=== Starting Cooking Icon Download ===")
        val iconMap = scrapeAllIconsFromPage(cookingUrl)

        var downloaded = 0
        var cached = 0
        var missing = 0

        val allItems = mutableSetOf<String>()

        // Collect all unique items (recipes + ingredients)
        recipes.forEach { recipe ->
            allItems.add(recipe.name)
            allItems.addAll(recipe.ingredients.keys)
        }

        println("Need icons for ${allItems.size} unique items\n")

        allItems.forEach { itemName ->
            // === CHANGED: Use flexible finder instead of direct map lookup ===
            val iconUrl = findIconUrl(iconMap, itemName)
            if (iconUrl != null) {
                val result = downloadIcon(iconUrl, itemName)
                if (result != null) {
                    if (File(result).length() > 0) {
                        downloaded++
                    } else {
                        cached++
                    }
                }
            } else {
                println("⚠ No icon found for: $itemName")
                missing++
            }
        }

        println("\nCooking Icons: $downloaded downloaded, $cached cached, $missing missing")
    }

    // Download all alchemy icons
    fun downloadAllAlchemyIcons(recipes: List<Recipe>) {
        println("\n=== Starting Alchemy Icon Download ===")
        val iconMap = scrapeAllIconsFromPage(alchemyUrl)

        var downloaded = 0
        var cached = 0
        var missing = 0

        val allItems = mutableSetOf<String>()

        // Collect all unique items
        recipes.forEach { recipe ->
            allItems.add(recipe.name)
            allItems.addAll(recipe.ingredients.keys)
        }

        println("Need icons for ${allItems.size} unique items\n")

        allItems.forEach { itemName ->
            // === CHANGED: Use flexible finder instead of direct map lookup ===
            val iconUrl = findIconUrl(iconMap, itemName)
            if (iconUrl != null) {
                val result = downloadIcon(iconUrl, itemName)
                if (result != null) {
                    if (File(result).length() > 0) {
                        downloaded++
                    } else {
                        cached++
                    }
                }
            } else {
                println("⚠ No icon found for: $itemName")
                missing++
            }
        }

        println("\nAlchemy Icons: $downloaded downloaded, $cached cached, $missing missing")
    }

    // Download all icons
    fun downloadAllIcons(cookingRecipes: List<Recipe>, alchemyRecipes: List<Recipe>) {
        println("\n========================================")
        println("       Icon Download Process")
        println("========================================")

        downloadAllCookingIcons(cookingRecipes)
        downloadAllAlchemyIcons(alchemyRecipes)

        println("\n========================================")
        println("       Icon Download Complete!")
        println("========================================\n")
    }
}