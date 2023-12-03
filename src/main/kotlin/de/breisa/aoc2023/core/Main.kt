package de.breisa.aoc2023.core

import de.breisa.aoc2023.days.Day01
import de.breisa.aoc2023.days.Day02

fun main(args: Array<String>) {
    val days = listOf(Day01(), Day02())
    days.last().solvePuzzles()
}
