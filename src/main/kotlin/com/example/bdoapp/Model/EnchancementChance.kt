package com.example.bdoapp.Model

import com.example.bdoapp.Service.MarketPrices

class EnchancementChance {
    sealed class StoneAmount {
        abstract fun calculate(targetKey: String, prices: MarketPrices): Triple<String, Long, Int>
    }

    object Edana: StoneAmount() {
        override fun calculate(targetKey: String, prices: MarketPrices): Triple<String, Long, Int> {
            val sharpCost = (prices.sharpBlackCrystal ?: 1_600_000L) * 3
            val caphrasCost = (prices.caphrasStone ?: 2_500_000L) * 20
            val total = sharpCost + caphrasCost
            return Triple("Edana's Black Stone", total, 1)
        }
    }

    object Manos: StoneAmount() {
        private val qtyTable = mapOf(
            "PRI" to 10, "DUO" to 11, "TRI" to 13,
            "TET" to 16, "PEN" to 20
        )

        override fun calculate(targetKey: String, prices: MarketPrices): Triple<String, Long, Int> {
            val qty = qtyTable[targetKey] ?: 10
            val price = prices.concBlackGem?.takeIf { it > 0 } ?: 2_500_000L
            return Triple("Conc Black Gem", price * qty, qty)
        }
    }

    object Preonne: StoneAmount() {
        private val qtyTable = mapOf(
            "PRI" to 15, "DUO" to 16, "TRI" to 17, "TET" to 18, "PEN" to 19,
            "HEX" to 20, "SEP" to 21, "OCT" to 22, "NOV" to 23, "DEC" to 25
        )

        override fun calculate(targetKey: String, prices: MarketPrices): Triple<String, Long, Int> {
            val qty = qtyTable[targetKey] ?: 15
            val price = prices.concBlackGem?.takeIf { it > 0 } ?: 2_500_000L
            return Triple("Conc Black Gem", price * qty, qty)
        }
    }

    object Kharazad: StoneAmount() {
        private val qtyTable = mapOf(
            "PRI" to 1, "DUO" to 2, "TRI" to 3, "TET" to 4, "PEN" to 6,
            "HEX" to 8, "SEP" to 10, "OCT" to 12, "NOV" to 15
        )

        override fun calculate(targetKey: String, prices: MarketPrices): Triple<String, Long, Int> {
            if (targetKey == "DEC") return Triple("Dawn Black Stone", 0, 1)
            val qty = qtyTable[targetKey] ?: 1
            val price = prices.essenceOfDawn?.takeIf { it > 0 } ?: 1_000_000_000L
            return Triple("Essence of Dawn", price * qty, qty)
        }
    }

    object Sovereign : StoneAmount() {
        override fun calculate(targetKey: String, prices: MarketPrices): Triple<String, Long, Int> {
            val cost =
                ((prices.sharpBlackCrystal ?: 0L) * 2) +
                        ((prices.caphrasStone ?: 0L) * 10) +
                        500_000_000L
            return Triple("Primordial Black Stone", cost, 1)
        }
    }

    object Fallen : StoneAmount() {
        override fun calculate(targetKey: String, prices: MarketPrices): Triple<String, Long, Int> {
            val cost =
                ((prices.caphrasStone ?: 0L) * 10) +
                        (prices.sharpBlackCrystal ?: 1_600_000L) +
                        3_000_000L
            return Triple("Flawless Chaotic Black Stone", cost, 1)
        }
    }

    object Blackstar : StoneAmount() {
        override fun calculate(targetKey: String, prices: MarketPrices): Triple<String, Long, Int> {
            val cost = ((prices.sharpBlackCrystal ?: 0L) * 2) + 2_000_000L
            return Triple("Flawless Magical Black Stone", cost, 1)
        }
    }

    object Default : StoneAmount() {
        override fun calculate(tx: String, prices: MarketPrices) =
            Triple("Black Stone", 2_500_000L, 1)
    }

}