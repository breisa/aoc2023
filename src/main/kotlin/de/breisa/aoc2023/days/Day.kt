package de.breisa.aoc2023.days

import kotlin.time.Duration
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

private const val WARMUP_RUNS = 10
private const val BENCHMARK_RUNS = 100

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

    fun benchmarkImplementation() {
        prettyPrintln("benchmarking day number $number:")
        val firstPart = measureTimes(::solveFirstPart)
        val secondPart = measureTimes(::solveSecondPart)
        prettyPrintln("part 1 runtime over $BENCHMARK_RUNS runs:  average: ${firstPart.average()} min: ${firstPart.min()} max: ${firstPart.max()}")
        prettyPrintln("part 2 runtime over $BENCHMARK_RUNS runs:  average: ${secondPart.average()} min: ${secondPart.min()} max: ${secondPart.max()}\n")
    }

    private fun measureTimes(part: (String)->T): List<Duration> {
        prettyPrint("warming up")
        repeat(WARMUP_RUNS) {
            part(actualPuzzle)
            print(".")
        }
        println()
        prettyPrint("benchmarking")
        return buildList {
            repeat(BENCHMARK_RUNS) {
                add(measureTime { part(actualPuzzle) })
                print(".")
            }
            println()
        }
    }

    private fun List<Duration>.average(): Duration = this.fold(Duration.ZERO) { a, b -> a + b } / this.size

    private fun prettyPrintln(text: String) = prettyPrint(text + "\n")

    private fun prettyPrint(text: String) = print("\uD83C\uDF84 $text")

    abstract fun solveFirstPart(puzzle: String): T

    abstract fun solveSecondPart(puzzle: String): T
}