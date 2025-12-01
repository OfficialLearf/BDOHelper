package com.example.bdoapp.Util

import com.example.bdoapp.UI.AlchemyView
import com.example.bdoapp.UI.AppStyles
import javafx.stage.Stage
import javafx.scene.Scene
import com.example.bdoapp.UI.MainMenuView
import com.example.bdoapp.UI.CookingView
import com.example.bdoapp.UI.BossTimerView
import com.example.bdoapp.UI.EnhancementView

class NavigationManager(private val primaryStage: Stage) {

    private fun applyStylesheet(scene: Scene) {
        scene.stylesheets.add(
            "data:text/css;base64," +
                    java.util.Base64.getEncoder().encodeToString(
                        AppStyles.getStylesheet().toByteArray()
                    )
        )
    }

    fun showMainMenu() {
        val mainMenuView = MainMenuView(this)
        val scene = Scene(mainMenuView.createContent(), 1200.0, 700.0)
        applyStylesheet(scene)
        primaryStage.scene = scene
    }

    fun showCookingCalculator() {
        val cookingView = CookingView(this)
        val scene = Scene(cookingView.createContent(), 1200.0, 700.0)
        applyStylesheet(scene)
        primaryStage.scene = scene
    }

    fun showAlchemyCalculator() {
        val alchemyView = AlchemyView(this)
        val scene = Scene(alchemyView.createContent(), 1200.0, 700.0)
        applyStylesheet(scene)
        primaryStage.scene = scene
    }
    fun showBossTimer() {
        val bossTimerView = BossTimerView(this)
        val scene = Scene(bossTimerView.createContent(), 1200.0, 700.0)
        applyStylesheet(scene)
        primaryStage.scene = scene
    }
    fun showEnhancementCalculator() {
        val enhancementView = EnhancementView(this)
        val scene = Scene(enhancementView.createContent(), 1200.0, 700.0)
        applyStylesheet(scene)
        primaryStage.scene = scene
    }
}