package com.example.bdoapp

import com.example.bdoapp.UI.MainMenuView
import com.example.bdoapp.UI.AppStyles
import com.example.bdoapp.Util.NavigationManager
import com.example.bdoapp.Util.NotificationService
import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage


class Main : Application() {
    override fun start(primaryStage: Stage) {
        val navigation = NavigationManager(primaryStage)
        NotificationService.start()
        primaryStage.title = "BDO Helper App"

        val mainMenuView = MainMenuView(navigation)
        val scene = Scene(mainMenuView.createContent(), 1200.0, 700.0)

        scene.stylesheets.add(
            "data:text/css;base64," +
                    java.util.Base64.getEncoder().encodeToString(
                        AppStyles.getStylesheet().toByteArray()
                    )
        )
        val stylesheet = javaClass.getResource("/dialog-style.css")?.toExternalForm()
        if (stylesheet != null) {
            scene.stylesheets.add(stylesheet)
        } else {
            println("ERROR: The main stylesheet (dialog-style.css) was not found!")
        }

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