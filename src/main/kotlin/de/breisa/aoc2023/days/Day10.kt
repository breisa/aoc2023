package de.breisa.aoc2023.days

import de.breisa.aoc2023.utils.Grid
import de.breisa.aoc2023.utils.GridPosition
import de.breisa.aoc2023.utils.getResourceAsText

/**
 * https://adventofcode.com/2023/day/10
 */
class Day10: Day<Int>(
    number = 10,
    firstExample = """
        .....
        .S-7.
        .|.|.
        .L-J.
        .....
    """.trimIndent(),
    secondExample = """
        ..........
        .S------7.
        .|F----7|.
        .||....||.
        .||....||.
        .|L-7F-J|.
        .|..||..|.
        .L--JL--J.
        ..........
        """.trimIndent(),
    actualPuzzle = getResourceAsText("/day10/puzzle.txt")
) {
    override fun solveFirstPart(puzzle: String): Int = Grid.parse(puzzle, String::first).findLoop().size / 2

    override fun solveSecondPart(puzzle: String): Int {
        val smallGrid = Grid.parse(puzzle, String::first)
        // remove everything besides the loop
        val loop = smallGrid.findLoop().toSet()
        smallGrid.positions.filter { it !in loop }.forEach { smallGrid.set(it, '.') }
        // enlarge the map to allow the flood-fill to pass through 'empty' gaps
        val bigGrid = blowUp(smallGrid)
        // fill the outside of the loop
        floodFill(bigGrid, GridPosition(0, 0), '.', 'O')
        // count positions that did not get filled (= inner tiles)
        return smallGrid.positions.count { pos ->
            pos.getBlownUpPositions().all { bigGrid.get(it) == '.' }
        }
    }

    private fun Grid<Char>.findLoop(): List<GridPosition> {
        val (start, _) = this.positionedValues.first { (_, value) -> value == 'S' }
        return findPathUsingDFS(this, from = start, to = start) ?: error("no path")
    }

    private fun findPathUsingDFS(grid: Grid<Char>, from: GridPosition, to: GridPosition): List<GridPosition>? {
        val visitNext = mutableListOf(from)
        val alreadyVisited = mutableSetOf<GridPosition>()
        val positionBefore = mutableMapOf<GridPosition, GridPosition>()
        while (visitNext.isNotEmpty()) {
            val current = visitNext.removeFirst()
            val neighbors = grid.getNeighbors(current)
            if (neighbors.contains(to) && positionBefore[current] != to) {
                val path = generateSequence(current) { positionBefore[it] }.toMutableList()
                path.add(0, to)
                return path.reversed()
            }
            neighbors.filter { it !in alreadyVisited }.forEach { next ->
                visitNext.add(0, next)
                positionBefore[next] = current
            }
            alreadyVisited.add(current)
        }
        return null
    }

    private fun Grid<Char>.getNeighbors(p: GridPosition) = getAllNeighbors(p).filter { getAllNeighbors(it).contains(p) }

    private fun Grid<Char>.getAllNeighbors(p: GridPosition): List<GridPosition> {
        return when (get(p)) {
            '|' -> listOf(p.north, p.south)
            '-' -> listOf(p.west, p.east)
            'L' -> listOf(p.north, p.east)
            'J' -> listOf(p.north, p.west)
            '7' -> listOf(p.west, p.south)
            'F' -> listOf(p.east, p.south)
            'S' -> listOf(p.north, p.east, p.south, p.west)
            else -> emptyList()
        }.filter { contains(it) }
    }

    private fun floodFill(grid: Grid<Char>, start: GridPosition, from: Char, to: Char) {
        val toFill = mutableListOf(start)
        while (toFill.isNotEmpty()) {
            val current = toFill.removeFirst()
            if (grid.get(current) != from) continue
            grid.set(current, to)
            listOf(current.north, current.east, current.south, current.west)
                .filter { grid.contains(it) }
                .forEach { toFill.add(it) }
        }
    }

    private fun blowUp(smallGrid: Grid<Char>): Grid<Char> {
        val bigPuzzle = (".".repeat(smallGrid.width*3) + "\n").repeat(smallGrid.height*3).dropLast(1)
        val bigGrid = Grid.parse(bigPuzzle, String::first)
        smallGrid.positionedValues.forEach { (position, smallChar) ->
            val bigChar = blowUp(smallChar)
            position.getBlownUpPositions().forEach { subPos ->
                bigGrid.set(subPos, bigChar.get2D(subPos.x - position.x*3, subPos.y - position.y*3))
            }
        }
        return bigGrid
    }

    private fun blowUp(c: Char) = when (c) {
        '|' -> ".|.\n.|.\n.|."
        '-' -> "...\n---\n..."
        'L' -> ".|.\n.L-\n..."
        'J' -> ".|.\n-J.\n..."
        '7' -> "...\n-7.\n.|."
        'F' -> "...\n.F-\n.|."
        'S' -> ".|.\n-S-\n.|."
        else -> "...\n...\n..."
    }

    private fun GridPosition.getBlownUpPositions() =
        (0..2).flatMap { i -> (0..2).map { j -> GridPosition(x*3+j, y*3+i) } }

    private fun String.get2D(x: Int, y: Int): Char = this.lines()[y][x]
}