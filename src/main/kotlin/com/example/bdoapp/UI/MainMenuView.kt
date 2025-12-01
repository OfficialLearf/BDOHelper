package com.example.bdoapp.UI

import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import javafx.scene.layout.Region
import javafx.geometry.Pos
import javafx.geometry.Insets
import com.example.bdoapp.Util.NavigationManager

class MainMenuView(private val navigation: NavigationManager) {

    fun createContent(): VBox {
        return VBox(30.0).apply {
            alignment = Pos.CENTER
            padding = Insets(40.0)
            styleClass.add("main-container")


            val titleLabel = Label("BDO Helper App").apply {
                styleClass.add("title-label")
            }

            val subtitleLabel = Label("Your Black Desert Online Companion").apply {
                styleClass.add("subtitle-label")
            }

            val spacer1 = Region().apply {
                prefHeight = 20.0
            }

            val cookingButton = createMenuButton("🍳 Cooking Calculator", "Calculate recipes and ingredients") {
                navigation.showCookingCalculator()
            }

            val alchemyButton = createMenuButton("⚗ Alchemy Calculator", "Plan your alchemy crafting") {
                navigation.showAlchemyCalculator()
            }
            val bossTimerButton = createMenuButton("⚔ Boss Timers", "Never miss a boss spawn") {
                navigation.showBossTimer()
            }
            val enhancementButton = createMenuButton("✨ Enhancement Calculator", "Calculate enhancement costs")
            {
                navigation.showEnhancementCalculator()
            }

            val exitButton = Button("Exit Application").apply {
                prefWidth = 250.0
                prefHeight = 40.0
                styleClass.add("btn-danger")
                setOnAction {
                    javafx.application.Platform.exit()
                }
            }

            children.addAll(
                titleLabel,
                subtitleLabel,
                spacer1,
                cookingButton,
                alchemyButton,
                bossTimerButton,
                enhancementButton,
                exitButton
            )
        }
    }

    private fun createMenuButton(title: String, description: String, disabled: Boolean = false, action: () -> Unit = {}): VBox {
        return VBox(8.0).apply {
            prefWidth = 400.0
            padding = Insets(20.0)
            styleClass.add("menu-button")
            alignment = Pos.CENTER_LEFT

            if (disabled) {
                opacity = 0.5
            }

            val titleLabel = Label(title).apply {
                style = "-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #ecf0f1;"
            }

            val descLabel = Label(description).apply {
                style = "-fx-font-size: 13px; -fx-text-fill: #95a5a6;"
            }

            children.addAll(titleLabel, descLabel)

            if (!disabled) {
                setOnMouseClicked { action() }
            }
        }
    }
}