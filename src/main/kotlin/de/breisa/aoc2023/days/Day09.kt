package de.breisa.aoc2023.days

import de.breisa.aoc2023.utils.getResourceAsText

/**
 * https://adventofcode.com/2023/day/9
 */
class Day09: Day<Int>(
    number = 9,
    firstExample = """
        0 3 6 9 12 15
        1 3 6 10 15 21
        10 13 16 21 30 45
    """.trimIndent(),
    actualPuzzle = getResourceAsText("/day09/puzzle.txt")
) {
    override fun solveFirstPart(puzzle: String): Int = parseHistories(puzzle).sumOf { history ->
        generateAllDifferences(history).sumOf { it.last() }
    }

    override fun solveSecondPart(puzzle: String): Int = parseHistories(puzzle).map { history ->
        generateAllDifferences(history).map { it.first() }.reversed().fold(0) { a, b -> b - a }
    }.sum()

    private fun generateAllDifferences(history: List<Int>): List<List<Int>> = buildList {
        var lastLayer = history
        add(lastLayer)
        while (!lastLayer.all { it == 0 }) {
            lastLayer = lastLayer.calculateDifferences()
            add(lastLayer)
        }
    }

    private fun List<Int>.calculateDifferences(): List<Int> = this.zipWithNext { l, r -> r - l }

    private fun parseHistories(puzzle: String): List<List<Int>> = puzzle.lines().map { it.split(" ").map { it.toInt() } }

}