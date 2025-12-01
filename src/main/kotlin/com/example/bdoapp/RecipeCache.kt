package com.example.bdoapp.Service

import com.example.bdoapp.Model.Recipe
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

object RecipeCache {
    private const val CACHE_FILE = "recipes_cache.json"
    private val jsonFormat = Json { prettyPrint = true }

    fun saveRecipes(recipes: List<Recipe>) {
        try {
            val json = jsonFormat.encodeToString(recipes)
            File(CACHE_FILE).writeText(json)
            println("Saved ${recipes.size} recipes to cache")
        } catch (e: Exception) {
            println("Error saving cache: ${e.message}")
            e.printStackTrace()
        }
    }

    fun loadRecipes(): List<Recipe>? {
        return try {
            val file = File(CACHE_FILE)
            if (file.exists()) {
                val json = file.readText()
                val recipes = jsonFormat.decodeFromString<List<Recipe>>(json)
                println("Loaded ${recipes.size} recipes from cache")
                recipes
            } else {
                null
            }
        } catch (e: Exception) {
            println("Error loading cache: ${e.message}")
            null
        }
    }

    fun isCacheAvailable(): Boolean {
        return File(CACHE_FILE).exists()
    }
}
object AlchemyRecipeCache {
    private const val CACHE_FILE = "alchemy_recipes_cache.json"
    private val jsonFormat = Json {prettyPrint = true}

    fun saveRecipes(recipes: List<Recipe>) {
        try {
            val json = jsonFormat.encodeToString(recipes)
            File(CACHE_FILE).writeText(json)
            println("Saved ${recipes.size} alchemy recipes to cache")
        } catch (e: Exception) {
            println("Error saving alchemy cache: ${e.message}")
            e.printStackTrace()
        }
    }
    fun loadRecipes(): List<Recipe>? {
        return try {
            val file = File(CACHE_FILE)
            if(file.exists())
            {
                val json = file.readText()
                val recipes = jsonFormat.decodeFromString<List<Recipe>>(json)
                println("Loaded ${recipes.size} alchemy recipes from cache")
                recipes
            }
            else null
        } catch (e: Exception) {
            println("Error loading alchemy cache: ${e.message}")
            null
        }
        }
    }
