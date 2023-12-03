package de.breisa.aoc2023.days

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
        println("the first solution of the actual puzzle is ${solveFirstPart(actualPuzzle)}")
        println("solving part2:")
        println("the example solution is ${solveSecondPart(secondExample)}")
        println("the second solution of the actual puzzle is ${solveSecondPart(actualPuzzle)}")
    }

    abstract fun solveFirstPart(puzzle: String): Int

    abstract fun solveSecondPart(puzzle: String): Int
}