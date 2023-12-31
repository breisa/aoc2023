package de.breisa.aoc2023.days

import de.breisa.aoc2023.utils.getResourceAsText
import kotlin.math.pow

/**
 * https://adventofcode.com/2023/day/4
 */
class Day04: Day<Int>(
    number = 4,
    firstExample = """
        Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53
        Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19
        Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1
        Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83
        Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36
        Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11
    """.trimIndent(),
    actualPuzzle = getResourceAsText("/day04/puzzle.txt")
) {

    override fun solveFirstPart(puzzle: String): Int =
        parseScratchCards(puzzle).map { card ->
            card.countCorrectNumbers()
        }.sumOf { 2.0.pow(it - 1).toInt() }

    override fun solveSecondPart(puzzle: String): Int {
        return secondFast(puzzle)
    }

    private fun secondNaive(puzzle: String): Int {
        val cards = parseScratchCards(puzzle).sortedBy { it.id }.toMutableList()
        var i = 0
        do {
            val it = cards[i]
            cards.addAll(cards.slice(it.id ..< it.id + it.countCorrectNumbers()))
        } while(++i < cards.size)
        return i
    }

    /**
     * each card can only influence the amount of cards with higher ids
     * therefore, when traversing low to high the amount of the current card is already known
     * each correct number on the current card increases the amount of the corresponding card by exactly the amount of the current card
     * => 1000x speedup
     */
    private fun secondFast(puzzle: String): Int {
        val cards = parseScratchCards(puzzle).sortedBy { it.id }
        cards.forEachIndexed { index, card ->
            (card.id ..< card.id + card.countCorrectNumbers()).forEach { cards[it].amount += card.amount }
        }
        return cards.sumOf { it.amount }
    }

    private fun parseScratchCards(puzzle: String): List<ScratchCard> {
        val cardLine = Regex("Card +(\\d+): +((?:\\d+ +)+)\\|((?: +\\d+)+)")
        return puzzle.lines()
            .mapNotNull { cardLine.matchEntire(it)?.groupValues }
            .map { ScratchCard(
                id = it[1].toInt(),
                winningNumbers = it[2].toIntList(),
                cardNumbers = it[3].toIntList()
            ) }
    }

    private fun String.toIntList(): List<Int> = this.trim().split(Regex(" +")).map(String::toInt)

    class ScratchCard(val id: Int, val winningNumbers: List<Int>, val cardNumbers: List<Int>, var amount: Int = 1) {
        fun countCorrectNumbers() = cardNumbers.count { it in winningNumbers }
    }

}