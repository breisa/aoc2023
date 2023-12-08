package de.breisa.aoc2023.days

import de.breisa.aoc2023.core.getResourceAsText

/**
 * https://adventofcode.com/2023/day/1
 */
class Day01: Day(
    number = 1,
    firstExample = """
        1abc2
        pqr3stu8vwx
        a1b2c3d4e5f
        treb7uchet
    """.trimIndent(),
    secondExample = """
        two1nine
        eightwothree
        abcone2threexyz
        xtwone3four
        4nineeightseven2
        zoneight234
        7pqrstsixteen
    """.trimIndent(),
    actualPuzzle = getResourceAsText("/day01/puzzle.txt")
) {

    override fun solveFirstPart(puzzle: String): Long = puzzle
        .lines()
        .map { it.replace(Regex("\\D"), "") }
        .filterNot { it == "" }
        .sumOf { (it.first().digitToInt() * 10) + it.last().digitToInt() }.toLong()

    override fun solveSecondPart(puzzle: String): Long = puzzle
        .lines()
        .filterNot { it == "" }
        .sumOf { solveSecondPartLine(it) }

    private val digitTextToValue = mapOf(
        "one" to 1, "two" to 2, "three" to 3, "four" to 4, "five" to 5, "six" to 6, "seven" to 7, "eight" to 8, "nine" to 9,
        "1" to 1, "2" to 2, "3" to 3, "4" to 4, "5" to 5, "6" to 6, "7" to 7, "8" to 8, "9" to 9,
    )

    private fun solveSecondPartLine(line: String): Long {
        val first = digitTextToValue[line.findAnyOf(digitTextToValue.keys)?.second]
        val last = digitTextToValue[line.findLastAnyOf(digitTextToValue.keys)?.second]
        if (first == null || last == null) error("no digit in line '$line'")
        return (first * 10 + last).toLong()
    }

}