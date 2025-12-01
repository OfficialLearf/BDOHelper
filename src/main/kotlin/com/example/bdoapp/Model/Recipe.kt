package com.example.bdoapp.Model
import kotlinx.serialization.Serializable


//Alchemy,Cooking recipe data class
@Serializable
data class Recipe(
    val name: String,
    val ingredients: Map<String, Int> //Map<IngredientName,Amount>
) {
    fun getTotalIngredients(): Int = ingredients.values.sum()

    fun hasIngredient(ingredientName: String): Boolean = ingredients.containsKey(ingredientName)

    fun getIngredientAmount(ingredientName: String): Int = ingredients[ingredientName] ?: 0
}