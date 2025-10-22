package com.example.bdoapp.UI

import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import javafx.scene.layout.Region
import javafx.scene.layout.Priority
import javafx.geometry.Pos
import javafx.geometry.Insets
import com.example.bdoapp.Util.NavigationManager

class MainMenuView(private val navigation: NavigationManager) {

    fun createContent(): VBox {
        return VBox(30.0).apply {
            alignment = Pos.CENTER
            padding = Insets(40.0)
            styleClass.add("main-container")

            // Header section
            val titleLabel = Label("BDO Helper App").apply {
                styleClass.add("title-label")
            }

            val subtitleLabel = Label("Your Black Desert Online Companion").apply {
                styleClass.add("subtitle-label")
            }

            // Spacer
            val spacer1 = Region().apply {
                prefHeight = 20.0
            }

            // Active features section
            val activeLabel = Label("Active Features").apply {
                styleClass.add("section-title")
            }

            val cookingButton = createMenuButton("🍳 Cooking Calculator", "Calculate recipes and ingredients") {
                navigation.showCookingCalculator()
            }

            val alchemyButton = createMenuButton("⚗️ Alchemy Calculator", "Plan your alchemy crafting") {
                navigation.showAlchemyCalculator()
            }

            // Coming soon section
            val spacer2 = Region().apply {
                prefHeight = 20.0
            }

            val comingSoonLabel = Label("Coming Soon").apply {
                styleClass.add("section-title")
            }

            val gatheringButton = createMenuButton("🌾 Gathering Helper", "Track gathering resources", true)
            val bossTimerButton = createMenuButton("⚔️ Boss Timers", "Never miss a boss spawn") {
                navigation.showBossTimer()
            }
            val enhancementButton = createMenuButton("✨ Enhancement Calculator", "Calculate enhancement costs", true)
            val nodeMapButton = createMenuButton("🗺️ Node Map", "Manage your worker empire", true)

            // Bottom spacer to push exit button down
            val bottomSpacer = Region().apply {
                VBox.setVgrow(this, Priority.ALWAYS)
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
                activeLabel,
                cookingButton,
                alchemyButton,
                spacer2,
                comingSoonLabel,
                gatheringButton,
                bossTimerButton,
                enhancementButton,
                nodeMapButton,
                bottomSpacer,
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

            val statusLabel = Label(if (disabled) "COMING SOON" else "AVAILABLE").apply {
                style = if (disabled) {
                    "-fx-font-size: 11px; -fx-text-fill: #f39c12; -fx-font-weight: bold;"
                } else {
                    "-fx-font-size: 11px; -fx-text-fill: #27ae60; -fx-font-weight: bold;"
                }
            }

            children.addAll(titleLabel, descLabel, statusLabel)

            if (!disabled) {
                setOnMouseClicked { action() }
            }
        }
    }
}