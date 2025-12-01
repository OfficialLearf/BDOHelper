package com.example.bdoapp.UI

import com.example.bdoapp.Util.NavigationManager

class AlchemyView(navigation: NavigationManager) : BaseRecipeView(navigation) {
    override val viewTitle = "⚗ Alchemy Calculator"
    override val listHeader = "Alchemy Recipes"
    override val searchPrompt = "Search alchemy recipes..."
    override val isAlchemy = true
}