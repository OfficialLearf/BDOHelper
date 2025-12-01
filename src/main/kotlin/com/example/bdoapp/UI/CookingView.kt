package com.example.bdoapp.UI

import com.example.bdoapp.Util.NavigationManager

class CookingView(navigation: NavigationManager) : BaseRecipeView(navigation) {
    override val viewTitle = "🍳 Cooking Calculator"
    override val listHeader = "Cooking Recipes"
    override val searchPrompt = "Search recipes..."
    override val isAlchemy = false
}
