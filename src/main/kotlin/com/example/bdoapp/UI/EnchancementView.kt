package com.example.bdoapp.UI

import com.example.bdoapp.Util.NavigationManager
import com.example.bdoapp.Service.EnhancementCalculator
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.geometry.Pos
import javafx.collections.FXCollections
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.javafx.JavaFx

class EnhancementView(private val navigation: NavigationManager) {

    private val calculator = EnhancementCalculator()
    private val uiScope = CoroutineScope(Dispatchers.JavaFx)

    // UI Variables
    private lateinit var itemTypeComboBox: ComboBox<String>
    private lateinit var itemComboBox: ComboBox<String>
    private lateinit var targetLevelComboBox: ComboBox<String>
    private lateinit var failstackField: TextField
    private lateinit var useCronsCheckBox: CheckBox
    private lateinit var resultArea: TextArea

    // DATA: Item Mapping
    private val itemsByType = mapOf(
        "Weapons" to listOf("Blackstar", "Sovereign"),
        "Armor" to listOf("Blackstar", "Fallen God", "Edania Armor"),
        "Accessories" to listOf("Deboreka", "Kharazad"),
        "Lifeskill" to listOf("Manos", "Preonne", "Manos Clothes")
    )

    // DATA: Level Lists
    private val standardLevels = listOf("PRI", "DUO", "TRI", "TET", "PEN")
    private val extendedLevels = standardLevels + listOf("HEX", "SEP", "OCT", "NOV", "DEC")

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
        header.padding = javafx.geometry.Insets(20.0)
        header.alignment = Pos.CENTER_LEFT
        header.style = "-fx-background-color: #242424; -fx-border-color: #333333; -fx-border-width: 0 0 2 0;"

        val titleLabel = Label("✨ Enhancement Calculator").apply { styleClass.add("title-label") }
        val spacer = Region().apply { HBox.setHgrow(this, Priority.ALWAYS) }
        val backButton = Button("← Back to Menu").apply {
            styleClass.add("btn-secondary")
            setOnAction { navigation.showMainMenu() }
        }

        header.children.addAll(titleLabel, spacer, backButton)
        return header
    }

    private fun createMainContent(): HBox {
        val mainContent = HBox(20.0)
        mainContent.padding = javafx.geometry.Insets(20.0)

        val leftSide = createInputPanel()
        HBox.setHgrow(leftSide, Priority.SOMETIMES)

        val rightSide = createResultsPanel()
        HBox.setHgrow(rightSide, Priority.ALWAYS)

        mainContent.children.addAll(leftSide, rightSide)
        return mainContent
    }

    private fun createInputPanel(): VBox {
        val panel = VBox(20.0)
        panel.prefWidth = 400.0
        panel.styleClass.add("card")
        panel.padding = javafx.geometry.Insets(20.0)

        val itemTypeLabel = Label("Category:").apply { styleClass.add("label-text") }
        itemTypeComboBox = ComboBox(FXCollections.observableArrayList(itemsByType.keys)).apply {
            value = "Weapons"
            prefWidth = Double.MAX_VALUE
            setOnAction {
                val newItems = itemsByType[value] ?: emptyList()
                itemComboBox.items = FXCollections.observableArrayList(newItems)
                itemComboBox.selectionModel.selectFirst()
            }
        }

        val itemLabel = Label("Item:").apply { styleClass.add("label-text") }
        itemComboBox = ComboBox(FXCollections.observableArrayList(itemsByType["Weapons"])).apply {
            value = items[0]
            prefWidth = Double.MAX_VALUE
            setOnAction { updateTargetLevels(this.value) }
        }

        val targetLabel = Label("Target Level:").apply { styleClass.add("label-text") }
        targetLevelComboBox = ComboBox<String>().apply { prefWidth = Double.MAX_VALUE }
        updateTargetLevels(itemComboBox.value)

        val fsLabel = Label("Failstacks:").apply { styleClass.add("label-text") }
        failstackField = TextField("100").apply { prefWidth = Double.MAX_VALUE }

        useCronsCheckBox = CheckBox("Use Cron Stones").apply { styleClass.add("label-text") }

        val calculateButton = Button("Calculate Prices").apply {
            styleClass.add("btn-primary")
            prefWidth = Double.MAX_VALUE
            setOnAction { performCalculation() }
        }

        panel.children.addAll(
            Label("Settings").apply { styleClass.add("section-title") },
            Separator(),
            itemTypeLabel, itemTypeComboBox,
            itemLabel, itemComboBox,
            targetLabel, targetLevelComboBox,
            fsLabel, failstackField,
            useCronsCheckBox,
            Region().apply { prefHeight = 20.0 },
            calculateButton
        )
        return panel
    }

    private fun createResultsPanel(): VBox {
        val panel = VBox(15.0)
        panel.styleClass.add("card")
        panel.padding = javafx.geometry.Insets(20.0)

        // CHANGED: "Analysis Results" -> "Enhancement Breakdown"
        val title = Label("Enhancement Breakdown").apply { styleClass.add("section-title") }

        resultArea = TextArea().apply {
            isEditable = false
            styleClass.add("text-area")
            VBox.setVgrow(this, Priority.ALWAYS)
        }

        panel.children.addAll(title, resultArea)
        return panel
    }

    private fun updateTargetLevels(selectedItem: String?) {
        if (selectedItem == null) return

        val i = selectedItem.lowercase()
        val isExtended = i.contains("sovereign") || i.contains("kharazad") || i.contains("preonne") || i.contains("edania")

        val currentLevels = if (isExtended) extendedLevels else standardLevels
        val currentSelection = targetLevelComboBox.value

        targetLevelComboBox.items = FXCollections.observableArrayList(currentLevels)

        if (currentLevels.contains(currentSelection)) {
            targetLevelComboBox.value = currentSelection
        } else {
            targetLevelComboBox.value = "PEN"
        }
    }

    private fun performCalculation() {
        // CHANGED: Removed "analyzing" language
        resultArea.text = "🔄 Connecting to Market API...\nFetching live prices..."

        uiScope.launch {
            try {
                val target = targetLevelComboBox.value
                val previous = getPreviousLevel(target)

                val result = calculator.calculate(
                    itemType = itemTypeComboBox.value,
                    item = itemComboBox.value ?: "Unknown",
                    currentLevel = previous,
                    targetLevel = target,
                    failstacks = failstackField.text.toIntOrNull() ?: 0,
                    useCrons = useCronsCheckBox.isSelected
                )
                resultArea.text = result
            } catch (e: Exception) {
                resultArea.text = "Error: ${e.message}\n${e.stackTraceToString()}"
            }
        }
    }

    private fun getPreviousLevel(target: String): String {
        val index = extendedLevels.indexOf(target)
        return if (index > 0) extendedLevels[index - 1] else "Base"
    }
}