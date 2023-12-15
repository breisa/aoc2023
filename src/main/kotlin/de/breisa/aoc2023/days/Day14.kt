package de.breisa.aoc2023.days

import de.breisa.aoc2023.utils.Direction
import de.breisa.aoc2023.utils.Direction.*
import de.breisa.aoc2023.utils.Grid
import de.breisa.aoc2023.utils.getResourceAsText

/**
 * https://adventofcode.com/2023/day/14
 */
class Day14: Day<Int>(
    number = 14,
    firstExample = """
        O....#....
        O.OO#....#
        .....##...
        OO.#O....O
        .O.....O#.
        O.#..O.#.#
        ..O..#O..O
        .......O..
        #....###..
        #OO..#....
    """.trimIndent(),
    actualPuzzle = getResourceAsText("/day14/puzzle.txt")
) {
    /**
     * Just tilt the platform and calculate the total load.
     */
    override fun solveFirstPart(puzzle: String): Int {
        val platform = parsePlatform(puzzle)
        platform.tilt(NORTH)
        return getTotalLoad(platform)
    }

    /**
     * The tilt operation is deterministic.
     * Therefore, repeating it on the finite size platform eventually ends up in a cycle of states.
     * The relatively small size of the platform in combination with the high amount of randomly placed
     * obstacles make this happen reasonably fast.
     *  => iterate performTiltCycle() until we end up in a state we already saw
     *  => compute the state after 1 billion iterations based on the offset to the detected cycle
     */
    override fun solveSecondPart(puzzle: String): Int {
        val x = 1_000_000_000L
        val platform = parsePlatform(puzzle)
        val stateAfterXCycles = detectCycleAndGetStateAt(platform, x)
        return getTotalLoad(stateAfterXCycles)
    }

    private fun detectCycleAndGetStateAt(platform: Grid<Space>, query: Long): Grid<Space> {
        val previousStates = HashMap<String, Long>()
        var iteration = 0L
        while (true) {
            platform.performTiltCycle()
            val key = platform.toString(columnSeparator = "", rowSeparator = "\n", includeHeader = false)
            val prev = previousStates[key]
            if (prev != null) {
                // we already saw this state => this is the second start of the cycle
                // 0 1 2 3 4 5 6 7 8 ...
                // x y z a b c a b c ...
                //       |     |
                //    prev    iteration
                val cycleLength = iteration - prev
                val offset = (query - prev) % cycleLength
                val (statePuzzle, _) = previousStates.entries.first { it.value == (prev + offset - 1L) }
                return parsePlatform(statePuzzle)
            }
            previousStates[key] = iteration
            iteration++
        }
    }

    private fun Grid<Space>.performTiltCycle() {
        tilt(NORTH)
        tilt(WEST)
        tilt(SOUTH)
        tilt(EAST)
    }

    private fun Grid<Space>.tilt(direction: Direction) {
        val positions = when (direction) {
            NORTH -> positionsInOrder(NORTH to SOUTH, WEST  to EAST)
            EAST  -> positionsInOrder(EAST  to WEST,  NORTH to SOUTH)
            SOUTH -> positionsInOrder(SOUTH to NORTH, WEST  to EAST)
            WEST  -> positionsInOrder(WEST  to EAST,  NORTH to SOUTH)
        }
        positions.filter { get(it) == Space.ROUND_ROCK }.forEach { pos ->
            var current = pos
            var next = pos.moveTo(direction)
            while (contains(next) && get(next) == Space.EMPTY) {
                swap(current, next)
                current = next
                next = current.moveTo(direction)
            }
        }
    }

    private fun getTotalLoad(platform: Grid<Space>): Int = platform.rows.mapIndexed { rowIndex, row ->
        row.count { it == Space.ROUND_ROCK } * (platform.height - rowIndex)
    }.sum()

    private fun parsePlatform(puzzle: String): Grid<Space> = Grid.parse(puzzle) { cell ->
        Space.fromSymbol(cell.first()) ?: error("invalid symbol '$cell'")
    }

    private enum class Space(val symbol: Char) {
        EMPTY('.'), CUBE_ROCK('#'), ROUND_ROCK('O');

        override fun toString(): String = "$symbol"

        companion object {
            fun fromSymbol(symbol: Char) = entries.firstOrNull { it.symbol == symbol }
        }
    }
}