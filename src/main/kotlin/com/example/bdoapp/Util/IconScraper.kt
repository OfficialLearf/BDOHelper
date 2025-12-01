package com.example.bdoapp.Util

import com.example.bdoapp.Model.Recipe
import org.jsoup.Jsoup
import java.io.File
import java.io.FileOutputStream
import java.net.URI

class IconScraper {
    private val baseUrl = "https://incendar.com"
    private val alchemyUrl = "$baseUrl/bdoalchemyrecipes.php"
    private val cookingUrl = "$baseUrl/bdocookingrecipes.php"
    private val iconCacheDir = "src/main/resources/icons"

    init {
        File(iconCacheDir).mkdirs()
    }

    private fun scrapeAllIconsFromPage(pageUrl: String): Map<String, String> {
        val iconMap = mutableMapOf<String, String>()

        try {
            println("Scraping page: $pageUrl")
            val doc = Jsoup.connect(pageUrl)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .timeout(10000)
                .get()

            val images = doc.select("img[src*='/bdo/']")
            println("Found ${images.size} BDO item images on the page")

            images.forEach { img ->
                val src = img.attr("src")
                val alt = img.attr("alt")
                val title = img.attr("title")

                var fullUrl = when {
                    src.startsWith("./") -> src.replace("./", "$baseUrl/")
                    src.startsWith("/") -> "$baseUrl$src"
                    src.startsWith("http") -> src
                    else -> "$baseUrl/$src"
                }


                fullUrl = fullUrl.replace("//images", "/images")


                val itemName = when {
                    alt.isNotBlank() -> alt.trim()
                    title.isNotBlank() -> title.trim()
                    else -> null
                }

                if (itemName != null && fullUrl.isNotBlank()) {
                    iconMap[itemName] = fullUrl
                    val simplifiedNames = createNameVariations(itemName)
                    simplifiedNames.forEach { simplifiedName ->
                        if (simplifiedName.isNotBlank()) {
                            iconMap[simplifiedName] = fullUrl
                        }
                    }
                }
            }

            println("Mapped ${iconMap.size} total icons from page")
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
    private fun createNameVariations(rawName: String): List<String> {
        val variations = mutableListOf<String>()
        val baseName = rawName
            .replace(" ingredient recipe", "")
            .replace(" ingredient", "")
            .replace(" recipe", "")
            .replace(" material", "")
            .trim()

        if (baseName != rawName && baseName.isNotBlank()) {
            variations.add(baseName)
        }
        if (baseName.contains("\\d".toRegex())) {
            val withoutNumbers = baseName.replace("\\s*\\d+".toRegex(), "").trim()
            if (withoutNumbers.isNotBlank() && withoutNumbers != baseName) {
                variations.add(withoutNumbers)
            }
        }

        return variations
    }

    private fun findIconUrl(iconMap: Map<String, String>, itemName: String): String? {
        iconMap[itemName]?.let { return it }
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
        for ((key, url) in iconMap) {
            if (key.contains(itemName, ignoreCase = true)) {
                println("  Found partial match: '$itemName' -> '$key'")
                return url
            }
        }
        for ((key, url) in iconMap) {
            if (itemName.contains(key, ignoreCase = true)) {
                println("  Found reverse partial match: '$itemName' -> '$key'")
                return url
            }
        }

        return null
    }

    private fun downloadIcon(iconUrl: String, itemName: String): String? {
        try {
            val safeFileName = itemName
                .replace(Regex("[^a-zA-Z0-9_\\-' ]"), "")
                .replace(" ", "_")
                .take(100) // Limit filename length

            val extension = iconUrl.substringAfterLast(".", "png").take(4)
            val localPath = "$iconCacheDir/${safeFileName}.$extension"

            val file = File(localPath)

            if (file.exists()) {
                println("✓ Already cached: $itemName")
                return localPath
            }
            println("⬇ Downloading: $itemName")
            URI(iconUrl).toURL().openStream().use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }

            Thread.sleep(300)

            return localPath
        } catch (e: Exception) {
            println("✗ Error downloading $itemName: ${e.message}")
        }
        return null
    }

    fun downloadAllCookingIcons(recipes: List<Recipe>) {
        println("\n=== Starting Cooking Icon Download ===")
        val iconMap = scrapeAllIconsFromPage(cookingUrl)

        var downloaded = 0
        var cached = 0
        var missing = 0

        val allItems = mutableSetOf<String>()

        recipes.forEach { recipe ->
            allItems.add(recipe.name)
            allItems.addAll(recipe.ingredients.keys)
        }

        println("Need icons for ${allItems.size} unique items\n")

        allItems.forEach { itemName ->
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

    fun downloadAllAlchemyIcons(recipes: List<Recipe>) {
        println("\n=== Starting Alchemy Icon Download ===")
        val iconMap = scrapeAllIconsFromPage(alchemyUrl)

        var downloaded = 0
        var cached = 0
        var missing = 0

        val allItems = mutableSetOf<String>()

        recipes.forEach { recipe ->
            allItems.add(recipe.name)
            allItems.addAll(recipe.ingredients.keys)
        }

        println("Need icons for ${allItems.size} unique items\n")

        allItems.forEach { itemName ->
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
    fun downloadAllIcons(cookingRecipes: List<Recipe>, alchemyRecipes: List<Recipe>) {
        downloadAllCookingIcons(cookingRecipes)
        downloadAllAlchemyIcons(alchemyRecipes)
    }
}