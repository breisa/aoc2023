package de.breisa.aoc2023.days

import de.breisa.aoc2023.core.getResourceAsText

/**
 * https://adventofcode.com/2023/day/3
 */
class Day03: Day<Int>(
    number = 3,
    firstExample = """
        467..114..
        ...*......
        ..35..633.
        ......#...
        617*......
        .....+.58.
        ..592.....
        ......755.
        ...$.*....
        .664.598..
    """.trimIndent(),
    actualPuzzle = getResourceAsText("/day03/puzzle.txt")
) {

    override fun solveFirstPart(puzzle: String): Int {
        val schematic = parseSchematic(puzzle)
        return parseSchematic(puzzle)
            .findAll(Regex("[^\\d\\.]"))
            .flatMap { schematic.getAdjacentPositions(it) }
            .filter { schematic.get(it).isDigit() }
            .map { schematic.getNumberStart(it) }
            .distinct()
            .sumOf { schematic.getNumberAt(it) }
    }

    override fun solveSecondPart(puzzle: String): Int {
        val schematic = parseSchematic(puzzle)
        var gearRatioSum = 0
        schematic.findAll(Regex("\\*")).forEach { gear ->
            val adjacentNumbers = schematic.getAdjacentPositions(gear)
                .filter { schematic.get(it).isDigit() }
                .map { schematic.getNumberStart(it) }
                .distinct()
                .map { schematic.getNumberAt(it) }
            if (adjacentNumbers.size == 2) {
                gearRatioSum += adjacentNumbers.reduce(Int::times)
            }
        }
        return gearRatioSum
    }

    private fun parseSchematic(schematic: String): Schematic {
        val lines = schematic.lines()
        return Schematic(
            width = lines.first().length,
            height = lines.size,
            grid = Array(lines.size) {
                lines[it].toCharArray()
            }
        )
    }

    data class Position(val x: Int, val y: Int)

    class Schematic(val width: Int, val height: Int, val grid: Array<CharArray>) {

        fun findAll(character: Regex): List<Position> {
            val matches = mutableListOf<Position>()
            for (y in grid.indices) {
                for (x in grid[y].indices) {
                    if (character.matches(grid[y][x].toString())) {
                        matches.add(Position(x,y))
                    }
                }
            }
            return matches
        }

        fun getAdjacentPositions(p: Position): List<Position> = listOf(
            p.copy(x = p.x - 1),
            p.copy(x = p.x + 1),
            p.copy(             y = p.y - 1),
            p.copy(x = p.x - 1, y = p.y - 1),
            p.copy(x = p.x    , y = p.y - 1),
            p.copy(x = p.x + 1, y = p.y - 1),
            p.copy(x = p.x - 1, y = p.y + 1),
            p.copy(x = p.x    , y = p.y + 1),
            p.copy(x = p.x + 1, y = p.y + 1)
        ).filter { isOnGrid(it) }

        fun getNumberStart(somewhereInNumber: Position): Position {
            var start = somewhereInNumber.x
            while (start > 0 && grid[somewhereInNumber.y][start-1].isDigit()) {
                start--
            }
            return somewhereInNumber.copy(x = start)
        }

        fun getNumberAt(position: Position) =
            grid[position.y].joinToString("").substring(position.x).takeWhile { it.isDigit() }.toInt()

        fun isOnGrid(position: Position) = position.x in 0..< width && position.y in 0 ..< height

        fun get(position: Position) = grid[position.y][position.x]

    }

}