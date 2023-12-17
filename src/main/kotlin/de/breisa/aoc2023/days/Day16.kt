package de.breisa.aoc2023.days

import de.breisa.aoc2023.utils.Direction
import de.breisa.aoc2023.utils.Direction.*
import de.breisa.aoc2023.utils.Grid
import de.breisa.aoc2023.utils.GridPosition
import de.breisa.aoc2023.utils.getResourceAsText

/**
 * https://adventofcode.com/2023/day/16
 */
class Day16: Day<Int>(
    number = 16,
    firstExample = """
        .|...\....
        |.-.\.....
        .....|-...
        ........|.
        ..........
        .........\
        ..../.\\..
        .-.-/..|..
        .|....-|.\
        ..//.|....
    """.trimIndent(),
    actualPuzzle = getResourceAsText("/day16/puzzle.txt")
) {
    override fun solveFirstPart(puzzle: String): Int =
        parseGrid(puzzle).countEnergizedTiles(GridPosition(0, 0) to EAST)

    override fun solveSecondPart(puzzle: String): Int {
        val grid = parseGrid(puzzle)
        val startConfigurations = buildList {
            grid.columnIndices.forEach { col ->
                add(GridPosition(col, 0) to SOUTH)
                add(GridPosition(col, grid.height-1) to NORTH)
            }
            grid.rowIndices.forEach { row ->
                add(GridPosition(0, row) to EAST)
                add(GridPosition(grid.width-1, row) to WEST)
            }
        }
        return startConfigurations.maxOf { config -> grid.copy(Tile::copy).countEnergizedTiles(config) }
    }

    private fun Grid<Tile>.countEnergizedTiles(start: Configuration): Int {
        this.castBeam(start)
        return this.values.count { it.energized }
    }

    private fun Grid<Tile>.castBeam(start: Configuration) {
        val pickUpPoints = mutableListOf(start)
        while (pickUpPoints.isNotEmpty()) {
            var (position, direction) = pickUpPoints.removeFirst()
            while (contains(position)) {
                val cell = get(position)
                if (cell.visited[direction.ordinal]) break
                cell.energized = true
                cell.visited[direction.ordinal] = true
                direction = when (cell.symbol to direction) {
                    '/'  to EAST  -> NORTH
                    '/'  to WEST  -> SOUTH
                    '/'  to NORTH -> EAST
                    '/'  to SOUTH -> WEST
                    '\\' to EAST  -> SOUTH
                    '\\' to WEST  -> NORTH
                    '\\' to NORTH -> WEST
                    '\\' to SOUTH -> EAST
                    '|'  to EAST  -> SOUTH.also { pickUpPoints.add(position to NORTH) }
                    '|'  to WEST  -> NORTH.also { pickUpPoints.add(position to SOUTH) }
                    '-'  to SOUTH -> WEST.also { pickUpPoints.add(position to EAST) }
                    '-'  to NORTH -> EAST.also { pickUpPoints.add(position to WEST) }
                    else -> direction
                }
                position = position.moveTo(direction)
            }
        }
    }

    private fun parseGrid(puzzle: String) = Grid.parse(puzzle) { cell -> Tile(cell.first()) }

    private class Tile(val symbol: Char) {
        var energized = false
        var visited = Array(4) {false}

        fun copy() = Tile(symbol).also {
            it.energized = energized
            it.visited = visited.copyOf()
        }

        override fun toString() = "$symbol"
    }
}

private typealias Configuration = Pair<GridPosition, Direction>