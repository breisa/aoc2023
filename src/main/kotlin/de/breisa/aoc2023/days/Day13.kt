package de.breisa.aoc2023.days

import de.breisa.aoc2023.utils.Grid
import de.breisa.aoc2023.utils.getResourceAsText
import kotlin.math.min

/**
 * https://adventofcode.com/2023/day/13
 */
class Day13: Day<Int>(
    number = 13,
    firstExample = """
        #.##..##.
        ..#.##.#.
        ##......#
        ##......#
        ..#.##.#.
        ..##..##.
        #.#.##.#.

        #...##..#
        #....#..#
        ..##..###
        #####.##.
        #####.##.
        ..##..###
        #....#..#
    """.trimIndent(),
    actualPuzzle = getResourceAsText("/day13/puzzle.txt")
) {
    override fun solveFirstPart(puzzle: String): Int = getReflectionSum(puzzle, desiredSmudges = 0)

    override fun solveSecondPart(puzzle: String): Int = getReflectionSum(puzzle, desiredSmudges = 1)

    private fun getReflectionSum(puzzle: String, desiredSmudges: Int): Int = parseMirrors(puzzle).sumOf { mirror ->
        val horizontal = getReflections(mirror.rows.toLongs(), desiredSmudges)
        val vertical = getReflections(mirror.columns.toLongs(), desiredSmudges)
        getReflectionIndex(vertical, desiredSmudges) + getReflectionIndex(horizontal, desiredSmudges) * 100
    }

    private fun getReflections(longs: List<Long>, maxErrors: Int = 0): List<Reflection> {
        return buildList {
            for ((index, pair) in longs.zipWithNext().withIndex()) {
                if (pair.first.differentBits(pair.second) <= maxErrors) {
                    val relevantPairs = longs.subListCenteredAfter(index).let { relevant ->
                        relevant.zip(relevant.asReversed()).take(relevant.size / 2)
                    }
                    val errors = relevantPairs.fold(0) { error, (l, r) ->
                        error + l.differentBits(r)
                    }
                    if (errors <= maxErrors) add(Reflection(index, errors))
                }
            }
        }
    }

    data class Reflection(val indexBeforeCenter: Int, val errors: Int)

    private fun getReflectionIndex(reflections: List<Reflection>, desiredSmudges: Int): Int {
        val r = reflections.firstOrNull { it.errors == desiredSmudges } ?: return 0
        return r.indexBeforeCenter + 1
    }

    private fun parseMirrors(puzzle: String) = puzzle.split("\n\n").map { mirror ->
        Grid.parse(mirror) { when( val s = it.first() ) {
            '.' -> '0'
            '#' -> '1'
            else -> error("invalid symbol '$s'")
        } }
    }

    private fun Long.differentBits(other: Long) = (this xor other).countOneBits()

    private fun List<List<Char>>.toLongs() = this.map { row ->
        row.joinToString("").also { check(it.length <= Long.SIZE_BITS) }.toLong(2)
    }

    private fun <T>List<T>.subListCenteredAfter(indexBeforeCenter: Int): List<T> {
        val width = min(indexBeforeCenter + 1, lastIndex - indexBeforeCenter)
        val start = indexBeforeCenter - width + 1
        val end = indexBeforeCenter + width + 1
        return subList(start, end)
    }
}