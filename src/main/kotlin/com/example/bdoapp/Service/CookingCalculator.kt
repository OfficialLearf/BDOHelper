package com.example.bdoapp.Service

import com.example.bdoapp.Model.Recipe
import com.example.bdoapp.Service.RecipeScraper


class CookingCalculator {
    fun calculateAllIngredients(recipe: Recipe,
                                recipes: List<Recipe>,
                                allIngredients: MutableMap<String,Int> = mutableMapOf(),
                                multiplier: Int = 1,
    ) : Map<String,Int> {
        for((name,quantity) in recipe.ingredients) {
            val subRecipe = recipes.find { it.name == name }
            if(subRecipe != null)
            {
                calculateAllIngredients(
                    subRecipe,
                    recipes,
                    allIngredients,
                    multiplier*quantity
                )
            }
            else {
                allIngredients[name] = allIngredients.getOrDefault(name,0) + quantity * multiplier
            }
        }

        return allIngredients
    }
}