package com.example.pokerdice.domain

object AiStrategy {

    fun decideHolds(currentDice: List<DiceFace>): List<Boolean> {
        val handResult = PokerDiceEvaluator.evaluate(currentDice)
        val counts = currentDice.groupingBy { it }.eachCount()

        return when (handResult.rank) {
            HandRank.FIVE_OF_A_KIND, HandRank.FOUR_OF_A_KIND, HandRank.FULL_HOUSE, HandRank.STRAIGHT -> {
                List(5) { true }
            }

            HandRank.THREE_OF_A_KIND, HandRank.TWO_PAIR, HandRank.ONE_PAIR -> {
                currentDice.map { die ->
                    (counts[die] ?: 0) >= 2
                }
            }

            HandRank.BUST -> {
                List(5) { false }
            }
        }
    }

    fun shouldStopEarly(currentDice: List<DiceFace>): Boolean {
        val rank = PokerDiceEvaluator.evaluate(currentDice).rank
        return rank.value >= HandRank.STRAIGHT.value
    }
}