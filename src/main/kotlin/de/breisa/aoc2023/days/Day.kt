package de.breisa.aoc2023.days

import kotlin.time.measureTimedValue

abstract class Day<T>(
    val number: Int,
    val firstExample: String,
    val secondExample: String = firstExample,
    val actualPuzzle: String
) {

    fun solvePuzzles() {
        prettyPrintln("running day number $number...")
        prettyPrintln("solving part 1:")
        prettyPrintln("the example solution is ${solveFirstPart(firstExample)}")
        val (firstActualSolution, firstDuration) = measureTimedValue { solveFirstPart(actualPuzzle) }
        prettyPrintln("the first solution of the actual puzzle is $firstActualSolution ($firstDuration)")
        prettyPrintln("solving part2:")
        prettyPrintln("the example solution is ${solveSecondPart(secondExample)}")
        val (secondActualSolution, secondDuration) = measureTimedValue { solveSecondPart(actualPuzzle) }
        prettyPrintln("the second solution of the actual puzzle is $secondActualSolution ($secondDuration)\n")
    }

    private fun prettyPrintln(text: String) = println("\uD83C\uDF84 $text")

    abstract fun solveFirstPart(puzzle: String): T

    abstract fun solveSecondPart(puzzle: String): T
}