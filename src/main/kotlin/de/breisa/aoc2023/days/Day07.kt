package de.breisa.aoc2023.days

import de.breisa.aoc2023.core.getResourceAsText

/**
 * https://adventofcode.com/2023/day/7
 */
class Day07: Day<Int>(
    number = 7,
    firstExample = """
        32T3K 765
        T55J5 684
        KK677 28
        KTJJT 220
        QQQJA 483
    """.trimIndent(),
    actualPuzzle = getResourceAsText("/day07/puzzle.txt")
) {

    override fun solveFirstPart(puzzle: String): Int = CamelCards().parseHands(puzzle).totalWinnings()

    override fun solveSecondPart(puzzle: String): Int = CamelCardsWithJoker().parseHands(puzzle).totalWinnings()

    private fun List<Hand>.totalWinnings(): Int = sorted().mapIndexed { index, hand -> (index + 1) * hand.bid }.sum()

    private class Card(val symbol: Char, val ordinal: Int) : Comparable<Card> {
        override fun compareTo(other: Card): Int = this.ordinal.compareTo(other.ordinal)
        override fun toString(): String = symbol.uppercase()
    }

    private data class Hand(val cards: List<Card>, val bid: Int, val type: HandType) : Comparable<Hand> {
        override fun compareTo(other: Hand) = if (this.type != other.type) {
            this.type.compareTo(other.type)
        } else {
            this.cards.zip(other.cards).firstOrNull() { (t, o) -> t != o }
                ?.let { (t, o) -> t.compareTo(o) }
                ?: 0
        }
    }

    private enum class HandType {
        HIGH_CARD, ONE_PAIR, TWO_PAIR, THREE_OF_A_KIND, FULL_HOUSE, FOUR_OF_A_KIND, FIVE_OF_A_KIND;
    }

    private abstract class CardGame(cardString: String) {
        val cards = cardString.mapIndexed { index, symbol -> Card(symbol, index) }

        fun parseHands(puzzle: String) = puzzle.lines()
            .map { line -> line.split(" ") }
            .map { (hand, bid) ->
                val cards = hand.map { parseCard(it) }
                Hand(cards, bid.toInt(), determineType(cards))
            }

        private fun parseCard(symbol: Char): Card = cards.find { symbol == it.symbol } ?: error("invalid card symbol")

        abstract fun determineType(hand: List<Card>): HandType
    }

    private class CamelCards : CardGame("23456789TJQKA") {
        override fun determineType(hand: List<Card>): HandType {
            if (hand.size != 5) error("incorrect number of cards")
            val cardSums = cards.map { card -> hand.count { it.symbol == card.symbol } }.sortedDescending()
            val (a, b) = cardSums
            return when {
                a == 5 -> HandType.FIVE_OF_A_KIND
                a == 4 -> HandType.FOUR_OF_A_KIND
                a == 3 && b == 2 -> HandType.FULL_HOUSE
                a == 3 -> HandType.THREE_OF_A_KIND
                a == 2 && b == 2 -> HandType.TWO_PAIR
                a == 2 -> HandType.ONE_PAIR
                else -> HandType.HIGH_CARD
            }
        }
    }

    private class CamelCardsWithJoker: CardGame("J23456789TQKA") {
        private val camelCards = CamelCards()

        override fun determineType(hand: List<Card>): HandType {
            val typeWithoutJokers = camelCards.determineType(hand)
            val result = when (hand.count { it.symbol == 'J' }) {
                5 -> HandType.FIVE_OF_A_KIND
                4 -> HandType.FIVE_OF_A_KIND
                3 -> when (typeWithoutJokers) {
                    HandType.FULL_HOUSE -> HandType.FIVE_OF_A_KIND
                    else -> HandType.FOUR_OF_A_KIND
                }
                2 -> when (typeWithoutJokers) {
                    HandType.FULL_HOUSE -> HandType.FIVE_OF_A_KIND
                    HandType.TWO_PAIR -> HandType.FOUR_OF_A_KIND
                    else -> HandType.THREE_OF_A_KIND
                }
                1 -> when (typeWithoutJokers) {
                    HandType.FOUR_OF_A_KIND -> HandType.FIVE_OF_A_KIND
                    HandType.THREE_OF_A_KIND -> HandType.FOUR_OF_A_KIND
                    HandType.TWO_PAIR -> HandType.FULL_HOUSE
                    HandType.ONE_PAIR -> HandType.THREE_OF_A_KIND
                    else -> HandType.ONE_PAIR
                }
                else -> typeWithoutJokers
            }
            return result
        }
    }
}