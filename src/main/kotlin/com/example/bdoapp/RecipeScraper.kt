package com.example.bdoapp.Service

import com.example.bdoapp.Model.Recipe
import org.jsoup.Jsoup

class RecipeScraper {

    var useAlchemyURL: Boolean = true // set true to scrape alchemy recipes

    fun getRecipes(): List<Recipe> {
        // Load from appropriate cache
        val cachedRecipes = if (useAlchemyURL) {
            AlchemyRecipeCache.loadRecipes()
        } else {
            RecipeCache.loadRecipes()
        }

        if (cachedRecipes != null) return cachedRecipes

        // Scrape recipes
        val recipes = scrapeRecipes(if (useAlchemyURL) "https://incendar.com/bdoalchemyrecipes.php"
        else "https://incendar.com/bdocookingrecipes.php")

        // Save to appropriate cache
        if (useAlchemyURL) {
            AlchemyRecipeCache.saveRecipes(recipes)
        } else {
            RecipeCache.saveRecipes(recipes)
        }

        return recipes
    }

    private fun scrapeRecipes(url: String): List<Recipe> {
        val recipes = mutableListOf<Recipe>()

        try {
            println("🔍 Scraping BDO recipes from: $url")
            val doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .timeout(10000)
                .get()

            val recipeTable = doc.select("table.bluetable").first()
            if (recipeTable != null) {
                val rows = recipeTable.select("tr")
                for (row in rows) {
                    val cells = row.select("td")
                    if (cells.size >= 2) {
                        val recipeName = cells[0].select("a[href] b").text().trim()
                        if (recipeName.isNotEmpty()) {
                            val ingredients = mutableMapOf<String, Int>()
                            for (i in 1 until cells.size) {
                                val ingredientCell = cells[i]
                                val amountText = ingredientCell.select("b").text().trim()
                                val ingredientName = ingredientCell.select("a[href]").text().trim()
                                if (amountText.isNotEmpty() && ingredientName.isNotEmpty()) {
                                    try {
                                        val amount = amountText.toInt()
                                        ingredients[ingredientName] = amount
                                    } catch (e: NumberFormatException) {
                                        println("Could not parse amount: '$amountText' for $ingredientName")
                                    }
                                }
                            }
                            recipes.add(Recipe(recipeName, ingredients))
                        }
                    }
                }
            }

            println("✅ Successfully scraped ${recipes.size} recipes from $url")

        } catch (e: Exception) {
            println("❌ Error scraping recipes: ${e.message}")
        }

        return recipes
    }
}
