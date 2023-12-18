package de.breisa.aoc2023.days

import de.breisa.aoc2023.utils.Grid
import de.breisa.aoc2023.utils.getResourceAsText

class Day17:Day<Int>(
    number = 17,
    firstExample = """
        2413432311323
        3215453535623
        3255245654254
        3446585845452
        4546657867536
        1438598798454
        4457876987766
        3637877979653
        4654967986887
        4564679986453
        1224686865563
        2546548887735
        4322674655533
    """.trimIndent(),
    actualPuzzle = getResourceAsText("/day17/puzzle.txt")
) {
    override fun solveFirstPart(puzzle: String): Int {
        val heatMap = parseHeatMap(puzzle)
        println(heatMap)
        TODO("Not yet implemented")
    }

    override fun solveSecondPart(puzzle: String): Int {
        TODO("Not yet implemented")
    }

    private fun parseHeatMap(puzzle: String) = Grid.parse(puzzle) { cell -> cell.toInt() }

}