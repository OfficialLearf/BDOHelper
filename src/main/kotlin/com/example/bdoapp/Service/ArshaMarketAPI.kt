package com.example.bdoapp.Service

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

//Items needed to calculate prices
//If more ID's are needed this is the site: https://bdocodex.com/us/items/powerup/?sl=1
data class MarketPrices(
    val primordialBlackStone: Long? = 0L,
    val memoryFragment: Long? = 0L,
    val sharpBlackCrystal: Long? = 0L,
    val caphrasStone: Long? = 0L,
    val essenceOfDawn: Long? = 0L,
    val concBlackGem: Long? = 0L
)

class ArshaMarketService(private val dispatcher: CoroutineDispatcher = Dispatchers.IO) {
    private val IDs = listOf(
        820934, //Primordial Black Stone
        44195,  //Memory Fragment
        4998,   //Sharp Black Crystal Shard
        721003, //Caphras Stone
        820979, //Essence of Dawn
        4987    //Concentrated Magical Black Gem
    )
    private val region = "eu"
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    suspend fun getMarketPrices(): MarketPrices = withContext(dispatcher) {
        val validPrices = mutableMapOf<Int, Long>()
        val batchResult = fetchBatch(IDs)

        if (batchResult != null) {
            validPrices.putAll(batchResult)
        } else {
            for (id in IDs) {
                fetchSingle(id)?.let { validPrices[id] = it }
            }
        }

        MarketPrices(
            primordialBlackStone = validPrices[820934],
            memoryFragment = validPrices[44195],
            sharpBlackCrystal = validPrices[4998],
            caphrasStone = validPrices[721003],
            essenceOfDawn = validPrices[820979],
            concBlackGem = validPrices[4987]
        )
    }

    private fun fetchBatch(ids: List<Int>): Map<Int, Long>? {
        val idString = ids.joinToString(",")
        val url = "https://api.arsha.io/v2/$region/GetWorldMarketSearchList?ids=$idString&lang=en"
        val request = Request.Builder().url(url).build()

        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return null
                val body = response.body?.string()?.trim() ?: return null
                parseMarketResponse(body)
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun parseMarketResponse(jsonBody: String): Map<Int, Long> {
        return if (jsonBody.startsWith("{")) {
            val obj = JSONObject(jsonBody)
            if (obj.has("id")) mapOf(obj.getInt("id") to obj.getLong("basePrice")) else emptyMap()
        } else {
            parseMarketList(JSONArray(jsonBody))
        }
    }

    private fun parseMarketList(arr: JSONArray): Map<Int, Long> {
        val result = mutableMapOf<Int, Long>()
        for (i in 0 until arr.length()) {
            val item = arr.getJSONObject(i)
            if (item.has("id") && item.has("basePrice")) {
                result[item.getInt("id")] = item.getLong("basePrice")
            }
        }
        return result
    }

    private fun fetchSingle(id: Int): Long? {
        val url = "https://api.arsha.io/v2/$region/GetWorldMarketSearchList?ids=$id&lang=en"
        val request = Request.Builder().url(url).build()
        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return null
                val body = response.body?.string() ?: return null
                if (body.startsWith("[")) {
                    val arr = JSONArray(body)
                    if (arr.length() > 0) arr.getJSONObject(0).optLong("basePrice", 0) else 0L
                } else {
                    JSONObject(body).optLong("basePrice", 0)
                }
            }
        } catch (_: Exception) { null }
    }
}