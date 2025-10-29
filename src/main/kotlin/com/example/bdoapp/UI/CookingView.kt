package com.example.bdoapp.UI

import com.example.bdoapp.Service.RecipeScraper
import com.example.bdoapp.Util.NavigationManager
import com.example.bdoapp.Service.CookingCalculator
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.image.ImageView
import javafx.scene.image.Image
import javafx.collections.FXCollections

class CookingView(private val navigation: NavigationManager) {

    private val recipeScraper = RecipeScraper().apply { useAlchemyURL = false }
    private val allRecipes = recipeScraper.getRecipes()
    val cookingCalculator = CookingCalculator()

    private lateinit var recipeListView: ListView<String>
    private lateinit var resultArea: TextArea
    private lateinit var countLabel: Label
    private lateinit var multiplierField: TextField

    fun createContent(): BorderPane {
        val root = BorderPane()
        root.styleClass.add("main-container")

        val header = createHeader()
        root.top = header

        val content = createMainContent()
        root.center = content

        return root
    }

    private fun createHeader(): HBox {
        val header = HBox(20.0)
        header.padding = Insets(20.0)
        header.alignment = Pos.CENTER_LEFT
        header.style = "-fx-background-color: #242424; -fx-border-color: #333333; -fx-border-width: 0 0 2 0;"

        val titleLabel = Label("🍳 Cooking Calculator").apply {
            styleClass.add("title-label")
        }

        val spacer = Region().apply {
            HBox.setHgrow(this, Priority.ALWAYS)
        }

        val backButton = Button("← Back to Menu").apply {
            styleClass.add("btn-secondary")
            setOnAction { navigation.showMainMenu() }
        }

        header.children.addAll(titleLabel, spacer, backButton)
        return header
    }

    private fun createMainContent(): HBox {
        val mainContent = HBox(20.0)
        mainContent.padding = Insets(20.0)

        val leftSide = createRecipeListPanel()
        HBox.setHgrow(leftSide, Priority.SOMETIMES)

        val rightSide = createRecipeDetailsPanel()
        HBox.setHgrow(rightSide, Priority.ALWAYS)

        mainContent.children.addAll(leftSide, rightSide)
        return mainContent
    }

    private fun createRecipeListPanel(): VBox {
        val panel = VBox(15.0)
        panel.prefWidth = 350.0
        panel.styleClass.add("card")
        panel.padding = Insets(20.0)

        val titleLabel = Label("Recipe List").apply {
            styleClass.add("section-title")
        }

        val searchField = TextField().apply {
            styleClass.add("text-field")
            promptText = "Search recipes..."
        }

        recipeListView = ListView<String>().apply {
            styleClass.add("list-view")
            items = FXCollections.observableArrayList(allRecipes.map { it.name })
            VBox.setVgrow(this, Priority.ALWAYS)

            setCellFactory {
                object : ListCell<String>() {
                    override fun updateItem(item: String?, empty: Boolean) {
                        super.updateItem(item, empty)

                        if (empty || item == null) {
                            graphic = null
                            text = null
                        } else {
                            val hbox = HBox(10.0).apply {
                                alignment = Pos.CENTER_LEFT
                                padding = Insets(5.0)
                                children.addAll(
                                    getIconImageView(item),
                                    Label(item).apply {
                                        style = "-fx-text-fill: #ecf0f1; -fx-font-size: 14px;"
                                    }
                                )
                            }
                            graphic = hbox
                            text = null
                        }
                    }
                }
            }
        }

        countLabel = Label("${allRecipes.size} recipes available").apply {
            styleClass.add("label-text")
        }

        searchField.textProperty().addListener { _, _, newValue ->
            if (newValue.isBlank()) {
                recipeListView.items = FXCollections.observableArrayList(allRecipes.map { it.name })
            } else {
                val filtered = allRecipes
                    .filter { it.name.contains(newValue, ignoreCase = true) }
                    .map { it.name }
                recipeListView.items = FXCollections.observableArrayList(filtered)
            }
            countLabel.text = "${recipeListView.items.size} recipes found"
        }

        recipeListView.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            if (newValue != null) {
                displayRecipeDetails(newValue, resultArea)
            }
        }

        panel.children.addAll(titleLabel, searchField, recipeListView, countLabel)
        return panel
    }

    private fun getIconImageView(itemName: String): ImageView {
        return ImageView().apply {
            fitWidth = 40.0
            fitHeight = 40.0
            isPreserveRatio = true

            try {
                val safeFileName = itemName.replace(Regex("[^a-zA-Z0-9_\\- ']"), "").replace(" ", "_")

                val extensions = listOf("png", "jpg", "jpeg", "gif")
                var loaded = false

                for (ext in extensions) {
                    val iconPath = "/icons/${safeFileName}.$ext"
                    val iconStream = javaClass.getResourceAsStream(iconPath)

                    if (iconStream != null) {
                        image = Image(iconStream)
                        loaded = true
                        break
                    }
                }

                if (!loaded) {

                    style = "-fx-background-color: #e74c3c; -fx-background-radius: 6px;"
                }
            } catch (e: Exception) {

                style = "-fx-background-color: #e74c3c; -fx-background-radius: 6px;"
            }
        }
    }

    private fun createRecipeDetailsPanel(): VBox {
        val panel = VBox(15.0)
        panel.styleClass.add("card")
        panel.padding = Insets(20.0)

        val headerBox = HBox(15.0).apply {
            alignment = Pos.CENTER_LEFT
        }

        val titleLabel = Label("Recipe Details").apply {
            styleClass.add("section-title")
            HBox.setHgrow(this, Priority.ALWAYS)
        }

        val multiplierLabel = Label("Quantity:").apply {
            styleClass.add("label-text")
        }

        multiplierField = TextField("1").apply {
            styleClass.add("text-field")
            prefWidth = 80.0
            textProperty().addListener { _, _, newValue ->

                if (!newValue.matches(Regex("\\d*"))) {
                    text = newValue.replace(Regex("[^\\d]"), "")
                }

                val selectedRecipe = recipeListView.selectionModel.selectedItem
                if (selectedRecipe != null) {
                    displayRecipeDetails(selectedRecipe, resultArea)
                }
            }
        }

        val calculateButton = Button("Calculate").apply {
            styleClass.add("btn-primary")
            setOnAction {
                val selectedRecipe = recipeListView.selectionModel.selectedItem
                if (selectedRecipe != null) {
                    displayRecipeDetails(selectedRecipe, resultArea)
                }
            }
        }

        headerBox.children.addAll(titleLabel, multiplierLabel, multiplierField, calculateButton)

        resultArea = TextArea().apply {
            styleClass.add("text-area")
            isEditable = false
            promptText = "Select a recipe from the list to view ingredients and calculations..."
            VBox.setVgrow(this, Priority.ALWAYS)
        }

        panel.children.addAll(headerBox, resultArea)
        return panel
    }

    private fun displayRecipeDetails(recipeName: String, resultArea: TextArea) {
        try {
            val recipe = allRecipes.find { it.name == recipeName }
            if (recipe == null) {
                resultArea.text = "Recipe '$recipeName' not found."
                return
            }


            val multiplier = multiplierField.text.toIntOrNull() ?: 1
            val safeMultiplier = if (multiplier < 1) 1 else multiplier

            val totalIngredients = cookingCalculator.calculateAllIngredients(recipe, allRecipes)

            val sb = StringBuilder()
            sb.appendLine("═══════════════════════════════════════")
            sb.appendLine("  ${recipe.name}")
            if (safeMultiplier > 1) {
                sb.appendLine("  Quantity: ${safeMultiplier}x")
            }
            sb.appendLine("═══════════════════════════════════════\n")

            sb.appendLine("📋 DIRECT INGREDIENTS:")
            sb.appendLine("───────────────────────────────────────")
            recipe.ingredients.forEach { (ingredientName, amount) ->
                val totalAmount = amount * safeMultiplier
                sb.appendLine("  • ${totalAmount}x $ingredientName")
            }

            sb.appendLine("\n📦 TOTAL MATERIALS NEEDED:")
            sb.appendLine("───────────────────────────────────────")
            sb.appendLine("(Including all sub-recipe ingredients)\n")
            totalIngredients.toSortedMap().forEach { (ingredientName, totalAmount) ->
                val multipliedAmount = totalAmount * safeMultiplier
                sb.appendLine("  • ${multipliedAmount}x $ingredientName")
            }

            sb.appendLine("\n═══════════════════════════════════════")

            resultArea.text = sb.toString()
        } catch (e: Exception) {
            resultArea.text = "❌ Error loading recipe: ${e.message}"
        }
    }
}