package com.example.bdoapp.UI

import com.example.bdoapp.Service.RecipeScraper
import com.example.bdoapp.Util.NavigationManager
import com.example.bdoapp.Service.CookingCalculator
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.collections.FXCollections
import javafx.scene.image.ImageView
import javafx.scene.image.Image

abstract class BaseRecipeView(protected val navigation: NavigationManager) {

    protected abstract val viewTitle: String
    protected abstract val listHeader: String
    protected abstract val searchPrompt: String
    protected abstract val isAlchemy: Boolean

    private val recipeScraper by lazy {
        RecipeScraper().apply { useAlchemyURL = isAlchemy }
    }
    private val allRecipes by lazy { recipeScraper.getRecipes() }
    private val calculator = CookingCalculator() // Assuming this handles math for both

    private lateinit var recipeListView: ListView<String>
    private lateinit var resultArea: TextArea
    private lateinit var countLabel: Label
    private lateinit var multiplierField: TextField

    fun createContent(): BorderPane {
        val root = BorderPane()
        root.styleClass.add("main-container")
        root.top = createHeader()
        root.center = createMainContent()
        return root
    }

    private fun createHeader(): HBox {
        val header = HBox(20.0).apply {
            padding = Insets(20.0)
            alignment = Pos.CENTER_LEFT
            style = "-fx-background-color: #242424; -fx-border-color: #333333; -fx-border-width: 0 0 2 0;"
        }

        val titleLabel = Label(viewTitle).apply { // Uses abstract property
            styleClass.add("title-label")
        }

        val spacer = Region().apply { HBox.setHgrow(this, Priority.ALWAYS) }

        val backButton = Button("← Back to Menu").apply {
            styleClass.add("btn-secondary")
            setOnAction { navigation.showMainMenu() }
        }

        header.children.addAll(titleLabel, spacer, backButton)
        return header
    }

    private fun createMainContent(): HBox {
        val mainContent = HBox(20.0).apply { padding = Insets(20.0) }

        val leftSide = createRecipeListPanel()
        HBox.setHgrow(leftSide, Priority.SOMETIMES)

        val rightSide = createRecipeDetailsPanel()
        HBox.setHgrow(rightSide, Priority.ALWAYS)

        mainContent.children.addAll(leftSide, rightSide)
        return mainContent
    }

    private fun createRecipeListPanel(): VBox {
        val panel = VBox(15.0).apply {
            prefWidth = 350.0
            styleClass.add("card")
            padding = Insets(20.0)
        }

        val titleLabel = Label(listHeader).apply { styleClass.add("section-title") }

        val searchField = TextField().apply {
            styleClass.add("text-field")
            promptText = searchPrompt
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
                            graphic = null; text = null
                        } else {
                            graphic = HBox(10.0).apply {
                                alignment = Pos.CENTER_LEFT
                                padding = Insets(5.0)
                                children.addAll(
                                    getIconImageView(item),
                                    Label(item).apply { style = "-fx-text-fill: #ecf0f1; -fx-font-size: 14px;" }
                                )
                            }
                            text = null
                        }
                    }
                }
            }
        }

        countLabel = Label("${allRecipes.size} recipes available").apply { styleClass.add("label-text") }

        searchField.textProperty().addListener { _, _, newValue ->
            val filtered = if (newValue.isBlank()) allRecipes else allRecipes.filter { it.name.contains(newValue, ignoreCase = true) }
            recipeListView.items = FXCollections.observableArrayList(filtered.map { it.name })
            countLabel.text = "${recipeListView.items.size} recipes found"
        }

        recipeListView.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            if (newValue != null) displayRecipeDetails(newValue)
        }

        panel.children.addAll(titleLabel, searchField, recipeListView, countLabel)
        return panel
    }

    private fun getIconImageView(itemName: String): ImageView {
        return ImageView().apply {
            fitWidth = 40.0; fitHeight = 40.0; isPreserveRatio = true
            try {
                val safeFileName = itemName.replace(Regex("[^a-zA-Z0-9_\\- ']"), "").replace(" ", "_")
                var loaded = false
                for (ext in listOf("png", "jpg", "jpeg", "gif")) {
                    val iconStream = javaClass.getResourceAsStream("/icons/${safeFileName}.$ext")
                    if (iconStream != null) {
                        image = Image(iconStream)
                        loaded = true
                        break
                    }
                }
                if (!loaded) style = "-fx-background-color: #e74c3c; -fx-background-radius: 6px;"
            } catch (e: Exception) {
                style = "-fx-background-color: #e74c3c; -fx-background-radius: 6px;"
            }
        }
    }

    private fun createRecipeDetailsPanel(): VBox {
        val panel = VBox(15.0).apply {
            styleClass.add("card")
            padding = Insets(20.0)
        }

        val headerBox = HBox(15.0).apply { alignment = Pos.CENTER_LEFT }
        val titleLabel = Label("Recipe Details").apply {
            styleClass.add("section-title")
            HBox.setHgrow(this, Priority.ALWAYS)
        }

        multiplierField = TextField("1").apply {
            styleClass.add("text-field")
            prefWidth = 80.0
            textProperty().addListener { _, _, newValue ->
                if (!newValue.matches(Regex("\\d*"))) text = newValue.replace(Regex("\\D"), "")
                recipeListView.selectionModel.selectedItem?.let { displayRecipeDetails(it) }
            }
        }

        headerBox.children.addAll(titleLabel, Label("Quantity:").apply { styleClass.add("label-text") }, multiplierField)

        resultArea = TextArea().apply {
            styleClass.add("text-area")
            isEditable = false
            promptText = "Select a recipe from the list..."
            VBox.setVgrow(this, Priority.ALWAYS)
        }

        panel.children.addAll(headerBox, resultArea)
        return panel
    }

    private fun displayRecipeDetails(recipeName: String) {
        try {
            val recipe = allRecipes.find { it.name == recipeName }
            if (recipe == null) {
                resultArea.text = "Recipe '$recipeName' not found."
                return
            }

            val multiplier = (multiplierField.text.toIntOrNull() ?: 1).coerceAtLeast(1)
            val totalIngredients = calculator.calculateAllIngredients(recipe, allRecipes)

            val sb = StringBuilder()
            sb.appendLine("═══════════════════════════════════════")
            sb.appendLine("  ${recipe.name} ${if (multiplier > 1) "(x$multiplier)" else ""}")
            sb.appendLine("═══════════════════════════════════════\n")

            sb.appendLine("DIRECT INGREDIENTS:")
            sb.appendLine("───────────────────────────────────────")
            recipe.ingredients.forEach { (name, amount) ->
                sb.appendLine("  • ${amount * multiplier}x $name")
            }

            sb.appendLine("\nTOTAL MATERIALS NEEDED:")
            sb.appendLine("───────────────────────────────────────")
            totalIngredients.toSortedMap().forEach { (name, amount) ->
                sb.appendLine("  • ${amount * multiplier}x $name")
            }

            resultArea.text = sb.toString()
        } catch (e: Exception) {
            resultArea.text = "Error: ${e.message}"
        }
    }
}