package com.example.bdoapp.Util

import com.example.bdoapp.UI.AlchemyView
import javafx.stage.Stage
import javafx.scene.Scene
import javafx.scene.Parent
import com.example.bdoapp.UI.MainMenuView
import com.example.bdoapp.UI.CookingView
import com.example.bdoapp.UI.BossTimerView
import com.example.bdoapp.UI.EnhancementView

class NavigationManager(private val primaryStage: Stage) {
    private fun applyStylesheet(scene: Scene) {
        val dialogStyle = javaClass.getResource("/dialog-style.css")?.toExternalForm()
            if (dialogStyle != null) {
                scene.stylesheets.add(dialogStyle)
            } else {
                println("css not found")
            }
        }

    private fun updateScene(content: Parent) {
        val currentScene = primaryStage.scene

        if (currentScene == null) {
            val newScene = Scene(content, 1200.0,800.0)
            applyStylesheet(newScene)
            primaryStage.scene = newScene
        } else {
            currentScene.root = content
        }
    }

    fun showMainMenu() {
        val view = MainMenuView(this)
        updateScene(view.createContent())
    }

    fun showCookingCalculator() {
        val view = CookingView(this)
        updateScene(view.createContent())
    }

    fun showAlchemyCalculator() {
        val view = AlchemyView(this)
        updateScene(view.createContent())
    }

    fun showBossTimer() {
        val view = BossTimerView(this)
        updateScene(view.createContent())
    }

    fun showEnhancementCalculator() {
        val view = EnhancementView(this)
        updateScene(view.createContent())
    }
}