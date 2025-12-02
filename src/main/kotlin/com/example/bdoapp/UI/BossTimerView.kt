package com.example.bdoapp.UI

import BossSpawn
import bossSchedule
import com.example.bdoapp.Util.NavigationManager
import javafx.animation.AnimationTimer
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.geometry.Insets
import javafx.geometry.Pos
import kotlinx.serialization.Serializable
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.util.prefs.Preferences // NEW: Import for saving data

class BossTimerView(private val navigation: NavigationManager) {

    private lateinit var bossListContainer: VBox
    private var selectedDay: DayOfWeek = LocalDate.now().dayOfWeek
    private lateinit var nextBossLabel: Label
    private var updateTimer: AnimationTimer? = null
    private val prefs: Preferences
    private var webHookURL: String? = null
    private val PREFS_NODE_PATH = "/com.example.bdoapp"
    private val WEBHOOK_URL_KEY = "discord_webhook_url"
    private val weeklyPrefs = Preferences.userRoot().node("/com.example.bdoapp/weekly_checklist")
    private val LAST_RESET_WEEK_KEY = "last_reset_week"

    init {
        prefs = Preferences.userRoot().node(PREFS_NODE_PATH)
        webHookURL = prefs[WEBHOOK_URL_KEY,null]
        checkForWeeklyReset()
        println("Loaded Webhook URL: $webHookURL")
    }

    fun createContent(): BorderPane {
        val root = BorderPane()
        root.styleClass.add("main-container")
        val header = createHeader()
        root.top = header
        val content = createMainContent()
        root.center = content
        val weeklyChecklist = createWeeklyChecklist()
        root.bottom = weeklyChecklist
        startUpdateTimer()

        return root
    }

    private fun createHeader(): VBox {
        val header = VBox(15.0)
        header.padding = Insets(20.0)
        header.style = "-fx-background-color: #242424; -fx-border-color: #333333; -fx-border-width: 0 0 2 0;"


        val topBar = HBox(20.0)
        topBar.alignment = Pos.CENTER_LEFT

        val titleLabel = Label("⚔ Boss Timer").apply {
            styleClass.add("title-label")
        }

        val spacer = Region().apply {
            HBox.setHgrow(this, Priority.ALWAYS)
        }

        val settingsButton = Button("⚙").apply {
            style = "-fx-font-size: 20px; -fx-background-color: transparent; -fx-text-fill: #95a5a6; -fx-cursor: hand;"
            setOnAction {
                showWebhookUrlDialog()
            }
        }
        val notificationButton = Button("Notifications").apply {
            style = "-fx-font-size: 20px; -fx-background-color: transparent; -fx-text-fill: #95a5a6; -fx-cursor: hand;"
            setOnAction {
                showNotificationSettingsDialog()
            }
        }

        val backButton = Button("← Back to Menu").apply {
            styleClass.add("btn-secondary")
            setOnAction { navigation.showMainMenu() }
        }

        topBar.children.addAll(titleLabel, spacer, notificationButton, settingsButton, backButton)


        nextBossLabel = Label("Next: Calculating...").apply {
            style = "-fx-font-size: 16px; -fx-text-fill: #e74c3c; -fx-font-weight: bold;"
        }


        val daySelector = createDaySelector()

        header.children.addAll(topBar, nextBossLabel, daySelector)
        return header
    }

    private val STYLESELECTED = "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-cursor: hand;"
    private val STYLEDEFAULT = "-fx-background-color: #2c2c2c; -fx-text-fill: #bdc3c7; -fx-background-radius: 6px; -fx-cursor: hand; -fx-border-color: #3a3a3a; -fx-border-width: 1px; -fx-border-radius: 6px;"

    private fun createDaySelector(): HBox {
        val container = HBox(10.0).apply {
            alignment = Pos.CENTER
            padding = Insets(10.0, 0.0, 0.0, 0.0)
        }

        val days = listOf(
            DayOfWeek.MONDAY to "MON", DayOfWeek.TUESDAY to "TUE",
            DayOfWeek.WEDNESDAY to "WED", DayOfWeek.THURSDAY to "THU",
            DayOfWeek.FRIDAY to "FRI", DayOfWeek.SATURDAY to "SAT",
            DayOfWeek.SUNDAY to "SUN"
        )

        days.forEach { (day, label) ->
            val button = Button(label).apply {
                prefWidth = 100.0
                prefHeight = 40.0
                userData = day
            }

            button.setOnAction {
                selectedDay = day
                updateBossList()
                updateButtonStyles(container)
            }

            container.children.add(button)
        }

        // Set initial styles
        updateButtonStyles(container)

        return container
    }

    private fun updateButtonStyles(container: HBox) {
        container.children.filterIsInstance<Button>().forEach { button ->
            val buttonDay = button.userData as? DayOfWeek
            button.style = if (buttonDay == selectedDay) {
                STYLESELECTED
            } else {
                STYLEDEFAULT
            }
        }
    }


    private fun createMainContent(): VBox {
        val content = VBox(0.0)
        content.padding = Insets(20.0)
        content.styleClass.add("main-container")

        bossListContainer = VBox(12.0)

        val scrollPane = ScrollPane(bossListContainer).apply {
            isFitToWidth = true
            styleClass.add("boss-scroll")
            style = "-fx-background: transparent; -fx-background-color: transparent;"
            VBox.setVgrow(this, Priority.ALWAYS)
        }

        content.children.add(scrollPane)

        updateBossList()

        return content
    }


    private fun createBossSpawnCard(spawn: BossSpawn): HBox {
        val card = HBox(20.0)
        card.alignment = Pos.CENTER_LEFT
        card.padding = Insets(20.0)
        card.styleClass.add("card")

        val timeBox = VBox(5.0).apply {
            alignment = Pos.CENTER
            prefWidth = 80.0
        }
        val timeLabel = Label(spawn.time.format(DateTimeFormatter.ofPattern("HH:mm"))).apply {
            style = "-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;"
        }
        val countdownLabel = Label(getCountdownText(spawn)).apply {
            style = "-fx-font-size: 11px; -fx-text-fill: #95a5a6;"
        }
        timeBox.children.addAll(timeLabel, countdownLabel)

        val bossBox = VBox(8.0).apply {
            HBox.setHgrow(this, Priority.ALWAYS)
        }
        val bossLabel = Label(spawn.bossName).apply {
            style = "-fx-font-size: 15px; -fx-text-fill: #ecf0f1; -fx-font-weight: bold;"
        }
        bossBox.children.add(bossLabel)

        card.children.addAll(timeBox, bossBox)

        return card
    }

    private fun createWeeklyChecklist(): VBox {
        val container = VBox(10.0)
        container.padding = Insets(15.0, 20.0, 20.0, 20.0)
        container.style = "-fx-background-color: #242424; -fx-border-color: #333333; -fx-border-width: 2 0 0 0;"

        val title = Label("📅 Weekly Checklist (Resets Thursday)").apply {
            styleClass.add("section-title")
            style = "-fx-text-fill: #e74c3c;"
        }

        val checkboxContainer = FlowPane(15.0, 10.0)

        weeklyTasks.forEach { taskName ->
            val checkbox = CheckBox(taskName).apply {
                isSelected = weeklyPrefs.getBoolean(taskName, false)
                applyCustomStyling()
                setOnAction {
                    weeklyPrefs.putBoolean(taskName, isSelected)
                    weeklyPrefs.flush()
                }
            }

            checkboxContainer.children.add(checkbox)
        }

        container.children.addAll(title, Separator(), checkboxContainer)
        return container
    }


    private fun checkForWeeklyReset() {
        val today = LocalDate.now()

        val resetDayOfWeek = DayOfWeek.THURSDAY


        val thisWeekResetDay = today.with(TemporalAdjusters.previousOrSame(resetDayOfWeek))

        val currentWeekId = thisWeekResetDay.year * 100 + thisWeekResetDay.dayOfYear / 7

        val lastResetWeekId = weeklyPrefs.getInt(LAST_RESET_WEEK_KEY, 0)

        if (currentWeekId > lastResetWeekId) {
            println("✅ Performing weekly reset for checklist...")

            weeklyTasks.forEach { taskName ->
                weeklyPrefs.putBoolean(taskName, false)
            }

            weeklyPrefs.putInt(LAST_RESET_WEEK_KEY, currentWeekId)
            weeklyPrefs.flush()
        }
    }
    private fun getCountdownText(spawn: BossSpawn): String {
        val now = LocalDateTime.now()
        val spawnDateTime = getNextSpawnDateTime(spawn)

        val minutesUntil = ChronoUnit.MINUTES.between(now, spawnDateTime)

        return when {
            minutesUntil < 0 -> "Spawned"
            minutesUntil < 60 -> "in ${minutesUntil}m"
            minutesUntil < 1440 -> "in ${minutesUntil / 60}h ${minutesUntil % 60}m"
            else -> "in ${minutesUntil / 1440}d"
        }
    }

    private fun getNextSpawnDateTime(spawn: BossSpawn): LocalDateTime {
        val now = LocalDateTime.now()
        var targetDate = LocalDate.now()

        while (targetDate.dayOfWeek != spawn.day) {
            targetDate = targetDate.plusDays(1)
        }

        var spawnDateTime = LocalDateTime.of(targetDate, spawn.time)


        if (spawnDateTime.isBefore(now) && targetDate == LocalDate.now()) {
            targetDate = targetDate.plusWeeks(1)
            spawnDateTime = LocalDateTime.of(targetDate, spawn.time)
        }

        return spawnDateTime
    }

    private fun updateBossList() {
        bossListContainer.children.clear()

        val todaySpawns = bossSchedule.filter { it.day == selectedDay }
        val sortedSpawns = todaySpawns.sortedBy { it.time }

        if (sortedSpawns.isEmpty()) {
            val emptyLabel = Label("No boss spawns scheduled for this day").apply {
                style = "-fx-font-size: 16px; -fx-text-fill: #95a5a6; -fx-padding: 40px;"
            }
            bossListContainer.children.add(emptyLabel)
        } else {
            sortedSpawns.forEach { spawn ->
                bossListContainer.children.add(createBossSpawnCard(spawn))
            }
        }

        updateNextBossLabel()
    }

    private fun updateNextBossLabel() {
        val now = LocalDateTime.now()


        val allUpcomingSpawns = bossSchedule
            .map { spawn -> spawn to getNextSpawnDateTime(spawn) } // Pair spawn with its next date/time
            .filter { (_, dateTime) -> dateTime.isAfter(now) } // Filter out past spawns
            .sortedBy { (_, dateTime) -> dateTime } // Sort chronologically

        if (allUpcomingSpawns.isNotEmpty()) {
            val nextDateTime = allUpcomingSpawns.first().second
            val nextGroupOfSpawns = allUpcomingSpawns
                .takeWhile { (_, dateTime) -> dateTime == nextDateTime }
                .map { (spawn, _) -> spawn }
            val bossNames = nextGroupOfSpawns.joinToString(", ") { it.bossName }
            val minutesUntil = ChronoUnit.MINUTES.between(now, nextDateTime)
            val timeStr = nextDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
            val dayStr =
                if (nextDateTime.toLocalDate() == LocalDate.now()) "Today" else nextDateTime.dayOfWeek.name.lowercase()
                    .replaceFirstChar { it.uppercase() }

            nextBossLabel.text = "⏰ Next: $bossNames at $timeStr ($dayStr) - in ${minutesUntil}m"
        } else {
            nextBossLabel.text = "⏰ No upcoming bosses"
        }
    }

    private fun startUpdateTimer() {
        updateTimer = object : AnimationTimer() {
            private var lastUpdate = 0L
            override fun handle(now: Long) {
                if (now - lastUpdate >= 60_000_000_000L) {
                    updateNextBossLabel()
                    updateBossList()
                    lastUpdate = now
                }
            }
        }
        updateTimer?.start()
    }

    private fun showNotificationSettingsDialog() {
        val dialog = Dialog<ButtonType>()
        dialog.title = "⚙️ App Settings"
        dialog.headerText = "Manage your notification preferences and webhook URL."
        dialog.applyCustomStyling()

        val prefs = Preferences.userRoot().node("/com.example.bdoapp/notifications")
        val uniqueBosses = bossSchedule.map { it.bossName }.distinct().sorted()

        val checkboxes = uniqueBosses.map { bossName ->
            CheckBox(bossName).apply {
                isSelected = prefs.getBoolean(bossName, true)
                styleClass.add("settings-checkbox") // Add a style class
            }
        }

        checkboxes.forEach { checkbox ->
            checkbox.setOnAction {
                prefs.putBoolean(checkbox.text, checkbox.isSelected)
                prefs.flush()
            }
        }

        val setWebhookButton = Button("Set Webhook URL").apply { styleClass.add("btn-primary") }
        val selectAllButton = Button("Select All").apply { styleClass.add("btn-secondary") }
        val deselectAllButton = Button("Deselect All").apply { styleClass.add("btn-secondary") }

        val topButtonBar = HBox(10.0, selectAllButton, deselectAllButton)
        topButtonBar.alignment = Pos.CENTER_LEFT


        setWebhookButton.setOnAction { showWebhookUrlDialog() }
        selectAllButton.setOnAction {
            checkboxes.forEach { it.isSelected = true; prefs.putBoolean(it.text, true) }
            prefs.flush()
        }
        deselectAllButton.setOnAction {
            checkboxes.forEach { it.isSelected = false; prefs.putBoolean(it.text, false) }
            prefs.flush()
        }

        val separator = Separator().apply { padding = Insets(10.0, 0.0, 10.0, 0.0) }

        val settingsList = VBox(12.0).apply {
            styleClass.add("settings-list")
            children.addAll(checkboxes)
        }

        val scrollPane = ScrollPane(settingsList).apply {
            isFitToWidth = true
            styleClass.add("transparent-scroll-pane")
            prefHeight = 350.0
        }

        val content = VBox(15.0, setWebhookButton, separator, topButtonBar, scrollPane).apply {
            padding = Insets(20.0)
        }
        dialog.dialogPane.content = content
        dialog.dialogPane.buttonTypes.add(ButtonType.CLOSE)
        dialog.showAndWait()
    }

    private fun showWebhookUrlDialog() {
        val dialog = TextInputDialog(webHookURL ?: "")
        dialog.title = "Discord Settings"
        dialog.headerText = "Configure your Discord Webhook URL"
        dialog.contentText = "URL:"
        dialog.applyCustomStyling()

        dialog.showAndWait().ifPresent { url ->
            if (url.startsWith("https://discord.com/api/webhooks/") || url.isEmpty()) {
                prefs.put(WEBHOOK_URL_KEY, url)
                prefs.flush()
                this.webHookURL = url

                val confirmation = Alert(Alert.AlertType.INFORMATION, "Webhook URL saved successfully!")
                confirmation.applyCustomStyling()
                confirmation.showAndWait()

            } else {
                val error = Alert(Alert.AlertType.ERROR, "Invalid URL format.")
                error.applyCustomStyling()
                error.showAndWait()
            }
        }
    }
}
@Serializable
data class DiscordEmbed(
    val title: String,
    val description: String,
    val color: Int
)

@Serializable
data class DiscordWebhookPayload(
    val username: String,
    val embeds: List<DiscordEmbed>,
)
private val weeklyTasks = listOf(
    "Garmoth (1)",
    "Garmoth (2)",
    "Garmoth (3)",
    "Sangoon",
    "Uturi",
    "Bulgasal",
    "Golden Pig King"
)
private fun Dialog<*>.applyCustomStyling() {
    val stylesheet = javaClass.getResource("/dialog-style.css")?.toExternalForm()
    if (stylesheet != null) {
        this.dialogPane.stylesheets.add(stylesheet)
    } else {
        println("Dialog stylesheet not found")
    }
}
private fun CheckBox.applyCustomStyling() {
    val stylesheet = javaClass.getResource("/dialog-style.css")?.toExternalForm()
    if (stylesheet != null) {
        this.stylesheets.add(stylesheet)
        this.styleClass.add("settings-checkbox")
    } else {
        println("Dialog stylesheet not found")
    }
}