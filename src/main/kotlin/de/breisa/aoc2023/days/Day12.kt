package de.breisa.aoc2023.days

import de.breisa.aoc2023.utils.Memoizer
import de.breisa.aoc2023.utils.getResourceAsText
import de.breisa.aoc2023.utils.repeat
import de.breisa.aoc2023.utils.splitAtFirstOrNull

/**
 * https://adventofcode.com/2023/day/12
 */
class Day12: Day<Long>(
    number = 12,
    firstExample = """
        ???.### 1,1,3
        .??..??...?##. 1,1,3
        ?#?#?#?#?#?#?#? 1,3,1,6
        ????.#...#... 4,1,1
        ????.######..#####. 1,6,5
        ?###???????? 3,2,1
    """.trimIndent(),
    actualPuzzle = getResourceAsText("/day12/puzzle.txt")
) {
    override fun solveFirstPart(puzzle: String): Long = parseSpringRows(puzzle)
        .sumOf { (springs, groups) ->
            gerNumberOfArrangements(springs, groups)
        }

    override fun solveSecondPart(puzzle: String): Long = parseSpringRows(puzzle)
        .map { unfoldSpringRow(it) }
        .sumOf { (springs, groups) ->
            gerNumberOfArrangements(springs, groups)
        }

    /**
     *  recursive solution with memoization
     *  reduces the problem size in every step by dropping 'matched' springs and trimming unnecessary springs
     *  tries to fail early for branches that make no sense to continue
     *  uses memoization to avoid recomputing subtrees of the decision tree
     */
    private fun gerNumberOfArrangements(springs: List<SpringState>, groups: List<Int>, memos: Memoizer<Long> = Memoizer()): Long {
        memos.getMemoOrNull(springs, groups)?.also { return it }
        if (springs.firstOrNull() == SpringState.OPERATIONAL) {
            // drop leading operational springs
            val newSprings = springs.dropWhile { it == SpringState.OPERATIONAL }
            if (newSprings.isEmpty()) return 0L
            return memos.memoize(result = gerNumberOfArrangements(newSprings, groups, memos), springs, groups)
        }
        val (length, continueIndex, terminated) = getFirstBrokenGroup(springs)
        if (length == groups.first()) {
            val newSprings = springs.drop(
                // if needed also drop the ? behind the group to force the needed . to terminate the group
                if (terminated) continueIndex else (continueIndex + 1)
            )
            val newGroups = groups.drop(1)
            if (newGroups.isEmpty()) {
                // no groups left
                return memos.memoize(
                    result = if (!newSprings.contains(SpringState.BROKEN)) 1L else 0L,
                    springs, groups
                )
            }
            // continue with the smaller problem
            return memos.memoize(result = gerNumberOfArrangements(newSprings, newGroups, memos), springs, groups)
        } else if (length > groups.first()) {
            // abort, the first group in springs is too long
            return memos.memoize(0L, springs, groups)
        } else {
            // the first group in springs is too short
            if (terminated) {
                // abort, it can not be made longer
                return memos.memoize(0L, springs, groups)
            }
            // try both possible values for the first ? in springs
            val (front, back) = springs.splitAtFirstOrNull { it == SpringState.UNKNOWN } ?: return 0L // abort, no ? left
            val broken = front + SpringState.BROKEN + back
            val operational = front + SpringState.OPERATIONAL + back
            return memos.memoize(
                result = gerNumberOfArrangements(broken, groups, memos) + gerNumberOfArrangements(operational, groups, memos),
                springs, groups
            )
        }
    }

    /**
     * Finds the first continuous group of broken springs that appears before the first unknown spring.
     * Returns the group's length, the first index after the group and whether it is terminated.
     */
    private fun getFirstBrokenGroup(springs: List<SpringState>): Triple<Int, Int, Boolean> {
        var groupLength = 0
        for (i in springs.indices) {
            when (springs[i]) {
                SpringState.UNKNOWN -> return Triple(groupLength, i, false) // group can be extended
                SpringState.BROKEN -> groupLength++
                SpringState.OPERATIONAL -> {
                    if (groupLength > 0) {
                        return Triple(groupLength, i, true)
                    }
                }
            }
        }
        return Triple(groupLength, springs.size, true)
    }

    private fun parseSpringRows(puzzle: String): List<Pair<List<SpringState>, List<Int>>> = puzzle.lines()
        .map { line -> line.split(" ") }
        .map { (springsText, groupsText) ->
            val springs = springsText.map { SpringState.fromChar(it) ?: error("syntax error") }
            val groups = groupsText.split(",").map { it.toInt() }
            Pair(springs, groups)
        }

    private fun unfoldSpringRow(springRow: Pair<List<SpringState>, List<Int>>): Pair<List<SpringState>, List<Int>> {
        val (springs, groups) = springRow
        return Pair((springs + SpringState.UNKNOWN).repeat(5).dropLast(1), groups.repeat(5))
    }

    private enum class SpringState(val symbol: Char) {
        OPERATIONAL('.'), BROKEN('#'), UNKNOWN('?');

        override fun toString(): String = this.symbol.toString()

        companion object {
            fun fromChar(c: Char) = entries.firstOrNull { it.symbol == c }
        }
    }
}