package com.example.bdoapp.Model
import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    val name: String,
    val ingredients: Map<String, Int> // Ingredient name -> Amount
) {
    fun getTotalIngredients(): Int = ingredients.values.sum()

    fun hasIngredient(ingredientName: String): Boolean = ingredients.containsKey(ingredientName)

    fun getIngredientAmount(ingredientName: String): Int = ingredients[ingredientName] ?: 0
}