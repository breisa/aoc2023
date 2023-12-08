package de.breisa.aoc2023.core

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.restrictTo
import de.breisa.aoc2023.days.*

private val days = listOf(Day01(), Day02(), Day03(), Day04(), Day05(), Day06(), Day07(), Day08())

fun main(args: Array<String>) = Main().main(args)

class Main: CliktCommand(name = "adventofcode.jar") {
    val day by option("-d", "--day", help = "Number of the day to execute")
        .int().restrictTo(1..24)
        .check("this day is not implemented yet") { n -> n in days.map { it.number } }

    val all by option("-a", "--all", help = "Execute all days").flag(default = false)

    val latest by option("-l", "--latest", help = "Execute the latest day").flag(default = false)

    override fun run() {
        if (all) {
            days.forEach { it.solvePuzzles() }
        } else if (latest) {
            days.last().solvePuzzles()
        } else if (day != null) {
            days.first { it.number == day }.solvePuzzles()
        } else {
            echoFormattedHelp()
        }
    }
}