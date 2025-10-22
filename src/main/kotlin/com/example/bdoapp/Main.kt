package com.example.bdoapp

import com.example.bdoapp.Util.IconScraper
import com.example.bdoapp.UI.MainMenuView
import com.example.bdoapp.UI.AppStyles
import com.example.bdoapp.Util.NavigationManager
import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage
import java.io.File
import javax.imageio.ImageIO
import java.awt.image.BufferedImage



class Main : Application() {
    override fun start(primaryStage: Stage) {
        val navigation = NavigationManager(primaryStage)

        primaryStage.title = "BDO Helper App"

        // Pass the navigation manager to MainMenuView
        val mainMenuView = MainMenuView(navigation)
        val scene = Scene(mainMenuView.createContent(), 1200.0, 700.0)

        // Add the stylesheet
        scene.stylesheets.add(
            "data:text/css;base64," +
                    java.util.Base64.getEncoder().encodeToString(
                        AppStyles.getStylesheet().toByteArray()
                    )
        )

        primaryStage.scene = scene
        primaryStage.show()
    }
}

fun main() {
    Application.launch(Main::class.java)
    //val iconScraper = IconScraper()

    // Get cooking recipes
    //val cookingScraper = RecipeScraper().apply { useAlchemyURL = false }
    //val cookingRecipes = cookingScraper.getRecipes()

    // Get alchemy recipes
    //val alchemyScraper = RecipeScraper().apply { useAlchemyURL = true }
    //val alchemyRecipes = alchemyScraper.getRecipes()

    // Download all icons
    //iconScraper.downloadAllIcons(cookingRecipes, alchemyRecipes)

}