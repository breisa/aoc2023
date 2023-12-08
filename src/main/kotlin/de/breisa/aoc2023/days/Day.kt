package de.breisa.aoc2023.days

import kotlin.time.measureTimedValue

abstract class Day(
    val number: Int,
    val firstExample: String,
    val secondExample: String = firstExample,
    val actualPuzzle: String
) {

    fun solvePuzzles() {
        println("running day number $number...")
        println("solving part 1:")
        println("the example solution is ${solveFirstPart(firstExample)}")
        val (firstActualSolution, firstDuration) = measureTimedValue { solveFirstPart(actualPuzzle) }
        println("the first solution of the actual puzzle is $firstActualSolution ($firstDuration)")
        println("solving part2:")
        println("the example solution is ${solveSecondPart(secondExample)}")
        val (secondActualSolution, secondDuration) = measureTimedValue { solveSecondPart(actualPuzzle) }
        println("the second solution of the actual puzzle is $secondActualSolution ($secondDuration)")
    }

    abstract fun solveFirstPart(puzzle: String): Long

    abstract fun solveSecondPart(puzzle: String): Long
}