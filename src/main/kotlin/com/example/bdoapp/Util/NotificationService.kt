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

    fun start() {
        this.webHookURL = mainPrefs.get(WEBHOOK_URL_KEY, null)
        println("NotificationService started. Webhook URL: $webHookURL")
        Timer("BossCheckTimer", true).schedule(timerTask {
            checkForUpcomingBosses()
        }, 5000L, 60_000L) // Start after 5s, then repeat every 60s
    }

    private fun checkForUpcomingBosses() {
        if (webHookURL.isNullOrBlank()) {
            return
        }

        val now = LocalDateTime.now()
        val notificationWindowStart = now.plusMinutes(9)
        val notificationWindowEnd = now.plusMinutes(10)

        bossSchedule.forEach { spawn ->
            val spawnDateTime = getNextSpawnDateTime(spawn)

            if (spawnDateTime.isAfter(notificationWindowStart) && spawnDateTime.isBefore(notificationWindowEnd)) {

                val isEnabled = notificationPrefs.getBoolean(spawn.bossName, true)
                val notificationId = "${spawn.day}-${spawn.time}-${spawn.bossName}"

                if (isEnabled && !sentNotifications.contains(notificationId)) {
                    println("✅ Sending notification for ${spawn.bossName}")
                    sendBossNotification(spawn, spawnDateTime)
                    sentNotifications.add(notificationId)
                }
            }
        }

        if (now.minute == 0) {
            sentNotifications.clear()
        }
    }

    private fun sendBossNotification(spawn: BossSpawn, spawnDateTime: LocalDateTime) {
        val minutesUntil = ChronoUnit.MINUTES.between(LocalDateTime.now(), spawnDateTime) + 1

        val payload = DiscordWebhookPayload(
            username = "BDO Boss Alerter",
            embeds = listOf(
                DiscordEmbed(
                    title = "🔥 ${spawn.bossName} Spawning Soon!",
                    description = "Spawns in approximately **$minutesUntil minutes**.",
                    color = 15158332
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