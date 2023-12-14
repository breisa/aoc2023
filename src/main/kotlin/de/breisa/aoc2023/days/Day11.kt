package de.breisa.aoc2023.days

import de.breisa.aoc2023.utils.getResourceAsText
import de.breisa.aoc2023.utils.Grid
import de.breisa.aoc2023.utils.GridPosition
import de.breisa.aoc2023.utils.subListBetween
import kotlin.math.abs

/**
 * https://adventofcode.com/2023/day/11
 */
class Day11: Day<Long>(
    number = 11,
    firstExample = """
        ...#......
        .......#..
        #.........
        ..........
        ......#...
        .#........
        .........#
        ..........
        .......#..
        #...#.....
    """.trimIndent(),
    actualPuzzle = getResourceAsText("/day11/puzzle.txt")
) {
    override fun solveFirstPart(puzzle: String): Long {
        val grid = Grid.parse(puzzle, cellParser = String::first).expandGrid()
        val galaxies = findGalaxyPositions(grid)
        return galaxies.getUniquePairs(grid).sumOf { (g1, g2) -> (abs(g1.x - g2.x) + abs(g1.y - g2.y)).toLong() }
    }

    override fun solveSecondPart(puzzle: String): Long {
        val grid = Grid.parse(puzzle, cellParser = String::first)
        val galaxies = findGalaxyPositions(grid)
        val rowWeights = grid.rows.map { r -> r.all { it == '.' } }.map { empty -> if (empty) 1000000 else 1 }
        val columnWeights = grid.columns.map { c -> c.all { it == '.' } }.map { empty -> if (empty) 1000000 else 1 }
        return galaxies.getUniquePairs(grid).sumOf { (g1, g2) ->
            val rows = rowWeights.subListBetween(g1.row, g2.row).sum()
            val columns = columnWeights.subListBetween(g1.column, g2.column).sum()
            (rows + columns).toLong()
        }
    }

    private fun findGalaxyPositions(grid: Grid<Char>) =
        grid.positionedValues.filter { (_, value) -> value == '#' }.map { (position, _) -> position }

    private fun List<GridPosition>.getUniquePairs(grid: Grid<Char>) =
        flatMap { g1 -> this.filter { g2 -> g1.index(grid) > g2.index(grid) }.map { b -> g1 to b } }

    private fun <T>GridPosition.index(grid: Grid<T>) = row * grid.width + column

    private fun <T>Grid<T>.expandGrid(): Grid<T> {
        val columnEmptiness = this.columns.map { column -> column.all { it == '.' } }
        return Grid(this.rows.flatMap { row ->
            val newRow = row.flatMapIndexed { index, symbol ->
                if (columnEmptiness[index]) listOf(symbol, symbol) else listOf(symbol)
            }
            if (row.all { it == '.' }) listOf(newRow, newRow) else listOf(newRow)
        })
    }
}