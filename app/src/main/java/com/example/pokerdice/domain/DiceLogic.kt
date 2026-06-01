package com.example.pokerdice.domain

enum class DiceFace(val value: Int, val label: String) {
    NINE(1, "9"),
    TEN(2, "10"),
    JACK(3, "J"),
    QUEEN(4, "Q"),
    KING(5, "K"),
    ACE(6, "A");

    companion object {
        fun fromInt(value: Int): DiceFace = entries.first { it.value == value }
    }
}

enum class HandRank(val value: Int) {
    BUST(0),
    ONE_PAIR(1),
    TWO_PAIR(2),
    THREE_OF_A_KIND(3),
    STRAIGHT(4),
    FULL_HOUSE(5),
    FOUR_OF_A_KIND(6),
    FIVE_OF_A_KIND(7)
}

data class HandResult(val rank: HandRank, val description: String)

object PokerDiceEvaluator {

    fun evaluate(dice: List<DiceFace>): HandResult {
        if (dice.isEmpty()) return HandResult(HandRank.BUST, "Bust")

        val counts = dice.groupingBy { it }.eachCount()
        val countsValues = counts.values.sortedDescending()

        return when {
            countsValues[0] == 5 -> HandResult(HandRank.FIVE_OF_A_KIND, "Five of a Kind")
            countsValues[0] == 4 -> HandResult(HandRank.FOUR_OF_A_KIND, "Four of a Kind")
            countsValues[0] == 3 && countsValues[1] == 2 -> HandResult(HandRank.FULL_HOUSE, "Full House")
            isStraight(dice) -> HandResult(HandRank.STRAIGHT, "Straight")
            countsValues[0] == 3 -> HandResult(HandRank.THREE_OF_A_KIND, "Three of a Kind")
            countsValues[0] == 2 && countsValues[1] == 2 -> HandResult(HandRank.TWO_PAIR, "Two Pair")
            countsValues[0] == 2 -> HandResult(HandRank.ONE_PAIR, "One Pair")
            else -> HandResult(HandRank.BUST, "Bust (${dice.maxByOrNull { it.value }?.label} High)")
        }
    }

    private fun isStraight(dice: List<DiceFace>): Boolean {
        val distinctSorted = dice.map { it.value }.distinct().sorted()
        return distinctSorted.size == 5 && (distinctSorted.last() - distinctSorted.first() == 4)
    }

    fun compare(hand1: List<DiceFace>, hand2: List<DiceFace>): Int {
        val res1 = evaluate(hand1)
        val res2 = evaluate(hand2)

        if (res1.rank != res2.rank) {
            return res1.rank.value - res2.rank.value
        }

        return hand1.sumOf { it.value } - hand2.sumOf { it.value }
    }
}