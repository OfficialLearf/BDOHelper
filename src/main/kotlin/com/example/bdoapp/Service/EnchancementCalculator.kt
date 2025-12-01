package com.example.bdoapp.Service

import com.example.bdoapp.Model.EnchancementChance
import kotlin.math.ceil

class EnhancementCalculator {

    private val marketService = ArshaMarketService()
    private val ratesSovereign = mapOf("PRI" to 0.0855, "DUO" to 0.0412, "TRI" to 0.02, "TET" to 0.0091, "PEN" to 0.00469, "HEX" to 0.00273, "SEP" to 0.0016, "OCT" to 0.001075, "NOV" to 0.000485, "DEC" to 0.000242)
    private val ratesEdana = mapOf("PRI" to 0.053, "DUO" to 0.0295, "TRI" to 0.0165, "TET" to 0.011, "PEN" to 0.00595, "HEX" to 0.00238, "SEP" to 0.0019, "OCT" to 0.00156, "NOV" to 0.0007, "DEC" to 0.00046)
    private val ratesKharazad = mapOf("PRI" to 0.163, "DUO" to 0.073, "TRI" to 0.0457, "TET" to 0.0289, "PEN" to 0.0191, "HEX" to 0.0129, "SEP" to 0.0088, "OCT" to 0.0057, "NOV" to 0.0032, "DEC" to 0.00172)
    private val ratesPreonne = mapOf("PRI" to 0.25, "DUO" to 0.20, "TRI" to 0.15, "TET" to 0.13, "PEN" to 0.11, "HEX" to 0.10, "SEP" to 0.09, "OCT" to 0.085, "NOV" to 0.08, "DEC" to 0.075)
    private val ratesManosAcc = mapOf("PRI" to 0.75, "DUO" to 0.45, "TRI" to 0.30, "TET" to 0.15, "PEN" to 0.05)
    private val ratesManosClothes = mapOf("PRI" to 0.30, "DUO" to 0.25, "TRI" to 0.20, "TET" to 0.15, "PEN" to 0.06)
    private val ratesDeboreka = mapOf("PRI" to 0.25, "DUO" to 0.10, "TRI" to 0.075, "TET" to 0.025, "PEN" to 0.005)
    private val ratesFallenGod = mapOf("PRI" to 0.02, "DUO" to 0.01, "TRI" to 0.005, "TET" to 0.002, "PEN" to 0.000025)
    private val ratesBlackstar = mapOf("PRI" to 0.25, "DUO" to 0.1063, "TRI" to 0.0340, "TET" to 0.0051, "PEN" to 0.0020)
    private val ratesStandardBoss = mapOf("PRI" to 0.25, "DUO" to 0.175, "TRI" to 0.10, "TET" to 0.05, "PEN" to 0.015)
    private val CRON_STONE_COST = 3_000_000L
    private val DEFAULT_ACCESSORY_BASE_PRICE = 300_000_000L

    suspend fun calculate(
        itemType: String,
        item: String,
        currentLevel: String,
        targetLevel: String,
        failstacks: Int,
        useCrons: Boolean
    ): String {

        val prices = marketService.getMarketPrices()
        val targetKey = getTargetKey(targetLevel)
        val (baseRate, _) = getRateConfig(item, targetKey)
        val fsBonus = baseRate * 0.10 * failstacks
        val successRate = (baseRate + fsBonus).coerceIn(0.0, 0.90)
        val failRate = 1.0 - successRate
        val expectedAttempts = if (successRate > 0) ceil(1.0 / successRate).toInt() else 0

        val (stoneName, stoneCostTotal, stoneQty) = calculateStoneCost(item, targetKey, prices)

        val isAccessory = itemType.equals("Accessories", ignoreCase = true) || item.contains("Necklace") || item.contains("Ring") || item.contains("Belt") || item.contains("Earring")
        val repairCost = if (!isAccessory) (prices.memoryFragment ?: 0L) * 10 else 0L

        val cronsNeeded = getCronCount(item, targetKey)
        val cronCost = if (useCrons) cronsNeeded * CRON_STONE_COST else 0L

        val clickCost = repairCost + stoneCostTotal + cronCost
        val baseItemCost = if (isAccessory) DEFAULT_ACCESSORY_BASE_PRICE else 0L
        val riskCost = if (!useCrons && isAccessory) (baseItemCost * failRate).toLong() else 0L

        val totalPerAttempt = clickCost + riskCost
        val totalExpected = totalPerAttempt * expectedAttempts

        return """
            ════════════════════════════════════════
              Enhancing: $item ($targetLevel)
            ════════════════════════════════════════
            
            📊 RATES (FS: $failstacks):
              • Chance: ${(successRate * 100).format(2)}%
              • Avg Attempts: $expectedAttempts
            
            💎 CLICK MATERIALS:
              • Material: $stoneName x$stoneQty
              • Mat Cost: ${format(stoneCostTotal)}
              • Crons: $cronsNeeded (${format(cronCost)})
            
            💰 FINANCIAL SUMMARY:
              • Cost Per Click: ${format(totalPerAttempt)}
              • Expected Total: ${format(totalExpected)}
        """.trimIndent()
    }

    //CRON STONE COUNTS FOR ALL OF THE CATEGORIES (No unified info, needs to be looked up individually)
    private val cronTable: Map<String, Map<String, Int>> = mapOf(
        "edana" to mapOf(
            "DUO" to 890, "TRI" to 910, "TET" to 940, "PEN" to 970, "HEX" to 1320,
            "SEP" to 1760, "OCT" to 2250, "NOV" to 2760, "DEC" to 3280
        ),
        "kharazad" to mapOf(
            "DUO" to 120, "TRI" to 280, "TET" to 540, "PEN" to 840, "HEX" to 1090,
            "SEP" to 1480, "OCT" to 1880, "NOV" to 2850, "DEC" to 3650
        ),
        "sovereign" to mapOf(
            "PRI" to 160, "DUO" to 320, "TRI" to 560, "TET" to 780, "PEN" to 970,
            "HEX" to 1350, "SEP" to 1550, "OCT" to 2250, "NOV" to 2760, "DEC" to 3920
        ),
        "preonne" to mapOf(
            "DUO" to 360, "TRI" to 670, "TET" to 990, "PEN" to 1430, "HEX" to 1890,
            "SEP" to 2390, "OCT" to 2690, "NOV" to 2750, "DEC" to 2810
        ),
        "fallen" to mapOf(
            "DUO" to 1500, "TRI" to 2100, "TET" to 2700, "PEN" to 4000
        ),
        "deboreka" to mapOf(
            "DUO" to 288, "TRI" to 865, "TET" to 2405, "PEN" to 11548
        ),
        "blackstar" to mapOf(
            "TRI" to 100, "TET" to 591, "PEN" to 3670
        ),
        "manos" to mapOf(   // külön kezeled majd clothes-re!
            "DUO" to 80, "TRI" to 275, "TET" to 1100, "PEN" to 2200
        ),
        "manos_clothes" to mapOf(
            "TRI" to 60, "TET" to 355, "PEN" to 1680
        )
    )

    private fun getCronCount(item: String, targetKey: String): Int {
        val key = when {
            "manos" in item.lowercase() && "clothes" in item.lowercase() -> "manos_clothes"
            else -> cronTable.keys.firstOrNull { item.lowercase().contains(it) }
        } ?: return 0

        return cronTable[key]?.get(targetKey.uppercase()) ?: 0
    }
    private fun getTargetKey(targetLevel: String): String {
        return when {
            targetLevel.contains("PRI") -> "PRI"
            targetLevel.contains("DUO") -> "DUO"
            targetLevel.contains("TRI") -> "TRI"
            targetLevel.contains("TET") -> "TET"
            targetLevel.contains("PEN") -> "PEN"
            targetLevel.contains("HEX") -> "HEX"
            targetLevel.contains("SEP") -> "SEP"
            targetLevel.contains("OCT") -> "OCT"
            targetLevel.contains("NOV") -> "NOV"
            targetLevel.contains("DEC") -> "DEC"
            else -> "PRI"
        }
    }

    private fun getRateConfig(item: String, targetKey: String): Pair<Double, Boolean> {
        val i = item.lowercase()
        val rateMap = when {
            i.contains("sovereign") -> ratesSovereign
            i.contains("edana") -> ratesEdana
            i.contains("kharazad") -> ratesKharazad
            i.contains("preonne") -> ratesPreonne
            i.contains("manos") && i.contains("clothes") -> ratesManosClothes
            i.contains("manos") -> ratesManosAcc
            i.contains("fallen") || i.contains("labreska") || i.contains("dahn") -> ratesFallenGod
            i.contains("blackstar") -> ratesBlackstar
            i.contains("deboreka") -> ratesDeboreka
            else -> ratesStandardBoss
        }
        return Pair(rateMap[targetKey] ?: 0.05, false)
    }

    private fun calculateStoneCost(
        item: String,
        targetKey: String,
        prices: MarketPrices
    ): Triple<String, Long, Int> {

        val i = item.lowercase()

        val rule = stoneAmounts.entries
            .firstOrNull { i.contains(it.key) }
            ?.value
            ?: EnchancementChance.Default

        return rule.calculate(targetKey, prices)
    }

    private val stoneAmounts = mapOf(
        "edana" to EnchancementChance.Edana,
        "manos" to  EnchancementChance.Manos,
        "preonne" to EnchancementChance.Preonne,
        "kharazad" to EnchancementChance.Kharazad,
        "sovereign" to EnchancementChance.Sovereign,
        "fallen" to EnchancementChance.Fallen,
        "blackstar" to EnchancementChance.Blackstar
    )
    private fun format(amount: Long): String {
        return when {
            amount >= 1_000_000_000 -> "%.2fB".format(amount / 1_000_000_000.0)
            amount >= 1_000_000 -> "%.1fM".format(amount / 1_000_000.0)
            amount >= 1_000 -> "%.1fK".format(amount / 1_000.0)
            else -> "$amount"
        }
    }
    private fun Double.format(digits: Int) = "%.${digits}f".format(this)
}