package com.example.bdoapp.UI

import com.example.bdoapp.Model.BossSpawn
import com.example.bdoapp.Model.bossSchedule
import com.example.bdoapp.Util.NavigationManager
import javafx.animation.AnimationTimer
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.collections.FXCollections
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class BossTimerView(private val navigation: NavigationManager) {

    private val completedBosses = mutableSetOf<String>() // Track completed boss spawns
    private lateinit var bossListContainer: VBox
    private var selectedDay: DayOfWeek = LocalDate.now().dayOfWeek
    private lateinit var nextBossLabel: Label
    private var updateTimer: AnimationTimer? = null

    fun createContent(): BorderPane {
        val root = BorderPane()
        root.styleClass.add("main-container")

        // Header
        val header = createHeader()
        root.top = header

        // Main content
        val content = createMainContent()
        root.center = content

        // Start the update timer
        startUpdateTimer()

        return root
    }

    private fun createHeader(): VBox {
        val header = VBox(15.0)
        header.padding = Insets(20.0)
        header.style = "-fx-background-color: #242424; -fx-border-color: #333333; -fx-border-width: 0 0 2 0;"

        // Top bar with title and back button
        val topBar = HBox(20.0)
        topBar.alignment = Pos.CENTER_LEFT

        val titleLabel = Label("⚔️ Boss Timer").apply {
            styleClass.add("title-label")
        }

        val spacer = Region().apply {
            HBox.setHgrow(this, Priority.ALWAYS)
        }

        val settingsButton = Button("⚙").apply {
            style = "-fx-font-size: 20px; -fx-background-color: transparent; -fx-text-fill: #95a5a6; -fx-cursor: hand;"
            setOnAction {
                // Navigate to settings/notification management
                println("Open boss notification settings")
            }
        }

        val backButton = Button("← Back to Menu").apply {
            styleClass.add("btn-secondary")
            setOnAction { navigation.showMainMenu() }
        }

        topBar.children.addAll(titleLabel, spacer, settingsButton, backButton)

        // Next boss indicator
        nextBossLabel = Label("Next: Calculating...").apply {
            style = "-fx-font-size: 16px; -fx-text-fill: #e74c3c; -fx-font-weight: bold;"
        }

        // Day selector
        val daySelector = createDaySelector()

        header.children.addAll(topBar, nextBossLabel, daySelector)
        return header
    }

    private fun createDaySelector(): HBox {
        val container = HBox(10.0)
        container.alignment = Pos.CENTER
        container.padding = Insets(10.0, 0.0, 0.0, 0.0)

        val days = listOf(
            DayOfWeek.MONDAY to "MON",
            DayOfWeek.TUESDAY to "TUE",
            DayOfWeek.WEDNESDAY to "WED",
            DayOfWeek.THURSDAY to "THU",
            DayOfWeek.FRIDAY to "FRI",
            DayOfWeek.SATURDAY to "SAT",
            DayOfWeek.SUNDAY to "SUN"
        )

        days.forEach { (day, label) ->
            val button = Button(label).apply {
                prefWidth = 70.0
                prefHeight = 40.0

                if (day == selectedDay) {
                    style = "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-cursor: hand;"
                } else {
                    style = "-fx-background-color: #2c2c2c; -fx-text-fill: #bdc3c7; -fx-background-radius: 6px; -fx-cursor: hand; -fx-border-color: #3a3a3a; -fx-border-width: 1px; -fx-border-radius: 6px;"
                }

                setOnAction {
                    selectedDay = day
                    updateBossList()
                    // Update all day buttons
                    container.children.forEach { node ->
                        if (node is Button) {
                            val btnDay = days.find { it.second == node.text }?.first
                            if (btnDay == selectedDay) {
                                node.style = "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-cursor: hand;"
                            } else {
                                node.style = "-fx-background-color: #2c2c2c; -fx-text-fill: #bdc3c7; -fx-background-radius: 6px; -fx-cursor: hand; -fx-border-color: #3a3a3a; -fx-border-width: 1px; -fx-border-radius: 6px;"
                            }
                        }
                    }
                }
            }
            container.children.add(button)
        }

        return container
    }

    private fun createMainContent(): VBox {
        val content = VBox(0.0)
        content.padding = Insets(20.0)
        content.styleClass.add("main-container")

        // Boss list container
        bossListContainer = VBox(12.0)

        val scrollPane = ScrollPane(bossListContainer).apply {
            isFitToWidth = true
            styleClass.add("boss-scroll")
            style = "-fx-background: transparent; -fx-background-color: transparent;"
            VBox.setVgrow(this, Priority.ALWAYS)
        }

        // Discord sync button at bottom
        val discordButton = Button("🔗 Sync to Discord").apply {
            prefWidth = 250.0
            prefHeight = 45.0
            styleClass.add("btn-primary")
            setOnAction {
                syncToDiscord()
            }
        }

        val buttonContainer = HBox(discordButton).apply {
            alignment = Pos.CENTER
            padding = Insets(15.0, 0.0, 0.0, 0.0)
        }

        content.children.addAll(scrollPane, buttonContainer)

        updateBossList()

        return content
    }

    private fun createBossSpawnCard(spawn: BossSpawn): HBox {
        val card = HBox(20.0)
        card.alignment = Pos.CENTER_LEFT
        card.padding = Insets(20.0)
        card.styleClass.add("card")

        val spawnKey = "${spawn.day}-${spawn.time}"
        val isCompleted = completedBosses.contains(spawnKey)

        if (isCompleted) {
            card.opacity = 0.5
        }

        // Time section
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

        // Boss names section
        val bossBox = VBox(8.0).apply {
            HBox.setHgrow(this, Priority.ALWAYS)
        }

        spawn.bosses.forEach { bossName ->
            val bossLabel = Label(bossName).apply {
                style = "-fx-font-size: 15px; -fx-text-fill: #ecf0f1; -fx-font-weight: bold;"
            }
            bossBox.children.add(bossLabel)
        }

        // Completion checkbox
        val checkbox = CheckBox().apply {
            isSelected = isCompleted
            styleClass.add("check-box")

            setOnAction {
                if (isSelected) {
                    completedBosses.add(spawnKey)
                    card.opacity = 0.5
                } else {
                    completedBosses.remove(spawnKey)
                    card.opacity = 1.0
                }
            }
        }

        val checkboxContainer = VBox(checkbox).apply {
            alignment = Pos.CENTER
            padding = Insets(0.0, 5.0, 0.0, 0.0)
        }

        card.children.addAll(timeBox, bossBox, checkboxContainer)

        // Hover effect
        card.setOnMouseEntered {
            if (!isCompleted) {
                card.style = "-fx-background-color: #333333; -fx-background-radius: 8px; -fx-border-color: #e74c3c; -fx-border-radius: 8px; -fx-border-width: 2px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 2);"
            }
        }
        card.setOnMouseExited {
            if (!isCompleted) {
                card.styleClass.clear()
                card.styleClass.add("card")
                if (isCompleted) {
                    card.opacity = 0.5
                }
            }
        }

        return card
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

        // Find the next occurrence of this day
        while (targetDate.dayOfWeek != spawn.day) {
            targetDate = targetDate.plusDays(1)
        }

        var spawnDateTime = LocalDateTime.of(targetDate, spawn.time)

        // If it's today but already passed, go to next week
        if (spawnDateTime.isBefore(now) && targetDate == LocalDate.now()) {
            targetDate = targetDate.plusWeeks(1)
            spawnDateTime = LocalDateTime.of(targetDate, spawn.time)
        }

        return spawnDateTime
    }

    private fun updateBossList() {
        bossListContainer.children.clear()

        val todaySpawns = bossSchedule.filter { it.day == selectedDay }
        val now = LocalDateTime.now()

        // Sort by time
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
        val allSpawns = bossSchedule.map { spawn ->
            val spawnDateTime = getNextSpawnDateTime(spawn)
            spawn to spawnDateTime
        }.filter { (_, dateTime) ->
            dateTime.isAfter(now)
        }.sortedBy { (_, dateTime) ->
            dateTime
        }

        if (allSpawns.isNotEmpty()) {
            val (nextSpawn, nextDateTime) = allSpawns.first()
            val minutesUntil = ChronoUnit.MINUTES.between(now, nextDateTime)
            val timeStr = nextDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
            val dayStr = if (nextDateTime.toLocalDate() == LocalDate.now()) "Today" else nextDateTime.dayOfWeek.name.lowercase().capitalize()

            nextBossLabel.text = "⏰ Next: ${nextSpawn.bosses.joinToString(", ")} at $timeStr ($dayStr) - in ${minutesUntil}m"
        } else {
            nextBossLabel.text = "⏰ No upcoming bosses"
        }
    }

    private fun startUpdateTimer() {
        updateTimer = object : AnimationTimer() {
            private var lastUpdate = 0L

            override fun handle(now: Long) {
                // Update every 60 seconds
                if (now - lastUpdate >= 60_000_000_000L) {
                    updateNextBossLabel()
                    // Update countdown labels
                    updateBossList()
                    lastUpdate = now
                }
            }
        }
        updateTimer?.start()
    }

    private fun syncToDiscord() {
        val completedToday = bossSchedule
            .filter { it.day == selectedDay }
            .filter { spawn ->
                val key = "${spawn.day}-${spawn.time}"
                completedBosses.contains(key)
            }

        val message = if (completedToday.isNotEmpty()) {
            "✅ Completed bosses for ${selectedDay}:\n" +
                    completedToday.joinToString("\n") { spawn ->
                        "${spawn.time} - ${spawn.bosses.joinToString(", ")}"
                    }
        } else {
            "No completed bosses for ${selectedDay}"
        }

        println("Discord sync: $message")

        // TODO: Implement actual Discord webhook
        val alert = Alert(Alert.AlertType.INFORMATION).apply {
            title = "Discord Sync"
            headerText = "Boss completion synced!"
            contentText = message
        }
        alert.showAndWait()
    }
}