package de.breisa.aoc2023.core

import de.breisa.aoc2023.days.*

fun main(args: Array<String>) {
    val days = listOf(Day01(), Day02(), Day03(), Day04(), Day05(), Day06(), Day07())
    days.last().solvePuzzles()
}
