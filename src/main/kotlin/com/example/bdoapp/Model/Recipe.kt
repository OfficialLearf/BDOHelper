package com.example.bdoapp.Model
import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    val name: String,
    val ingredients: Map<String, Int> // Ingredient name -> Amount
) {
    // Helper function to get total ingredient count
    fun getTotalIngredients(): Int = ingredients.values.sum()

    // Check if recipe contains an ingredient
    fun hasIngredient(ingredientName: String): Boolean = ingredients.containsKey(ingredientName)

    // Get amount of specific ingredient
    fun getIngredientAmount(ingredientName: String): Int = ingredients[ingredientName] ?: 0
}