package com.example.bdoapp.Util

import BossSpawn
import bossSchedule
import com.example.bdoapp.UI.DiscordEmbed
import com.example.bdoapp.UI.DiscordWebhookPayload
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.prefs.Preferences
import kotlin.concurrent.timerTask

object NotificationService {
    private val notificationPrefs = Preferences.userRoot().node("/com.example.bdoapp/notifications")
    private val mainPrefs = Preferences.userRoot().node("/com.example.bdoapp")
    private const val WEBHOOK_URL_KEY = "discord_webhook_url"
    private val sentNotifications = mutableSetOf<String>()
    private var webHookURL: String? = null
    private val alertTriggers = listOf(9L, 4L, 1L, 0L)

    fun start() {
        this.webHookURL = mainPrefs[WEBHOOK_URL_KEY, null]
        println("NotificationService started. Webhook URL: $webHookURL")
        Timer("BossCheckTimer", true).schedule(timerTask {
            checkForUpcomingBosses()
        }, 5000L, 30_000L)
    }

    private fun checkForUpcomingBosses() {
        if (webHookURL.isNullOrBlank()) {
            return
        }

        val now = LocalDateTime.now()

        bossSchedule.forEach { spawn ->
            val spawnDateTime = getNextSpawnDateTime(spawn)

            val minutesRaw = ChronoUnit.MINUTES.between(now, spawnDateTime)

            for (triggerMinutesRaw in alertTriggers) {
                if (minutesRaw == triggerMinutesRaw) {

                    val minutesUntilDisplay = minutesRaw + 1

                    val notificationId = "${spawn.day}-${spawn.time}-${spawn.bossName}-${triggerMinutesRaw}m"

                    val isEnabled = notificationPrefs.getBoolean(spawn.bossName, true)

                    if (isEnabled && !sentNotifications.contains(notificationId)) {
                        println("✅ Sending ${minutesUntilDisplay}m notification for ${spawn.bossName}")
                        sendBossNotification(spawn, minutesUntilDisplay)
                        sentNotifications.add(notificationId)
                    }
                }
            }
        }
        if (now.minute == 0) {
            sentNotifications.clear()
        }
    }

    private fun sendBossNotification(spawn: BossSpawn, minutesUntilDisplay: Long) {

        val title: String
        var description = ""

        if (minutesUntilDisplay > 0) {
            title = "${spawn.bossName} Spawning Soon!"
            description = "Spawns in **$minutesUntilDisplay minutes**."
        } else {
            title = "${spawn.bossName} HAS SPAWNED!"
        }


        val payload = DiscordWebhookPayload(
            username = "BDO Boss Alert",
            embeds = listOf(
                DiscordEmbed(
                    title = title,
                    description = description,
                    color = if (minutesUntilDisplay > 0) 15158332 else 3066993
                )
            )
        )

        Thread {
            try {
                val client = OkHttpClient()
                val jsonBody = Json.encodeToString(payload)
                val request = Request.Builder()
                    .url(webHookURL!!)
                    .post(jsonBody.toRequestBody("application/json; charset=utf-8".toMediaType()))
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        println("Error sending notification: ${response.message}")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun getNextSpawnDateTime(spawn: BossSpawn): LocalDateTime {
        val now = LocalDateTime.now()
        var targetDate = now.toLocalDate()
        while (targetDate.dayOfWeek != spawn.day) {
            targetDate = targetDate.plusDays(1)
        }
        var spawnDateTime = LocalDateTime.of(targetDate, spawn.time)
        if (spawnDateTime.isBefore(now)) {
            spawnDateTime = spawnDateTime.plusWeeks(1)
        }
        return spawnDateTime
    }
}