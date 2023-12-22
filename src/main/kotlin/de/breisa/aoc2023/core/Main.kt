package de.breisa.aoc2023.core

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.restrictTo
import de.breisa.aoc2023.days.*

private val days = listOf(
    Day01(), Day02(), Day03(), Day04(), Day05(), Day06(), Day07(), Day08(),
    Day09(), Day10(), Day11(), Day12(), Day13(), Day14(), Day15(), Day16(),
    Day18(), Day19(), Day20()
)

fun main(args: Array<String>) = Main().main(args)

class Main: CliktCommand(name = "AdventOfCode2023") {
    private val day by option("-d", "--day", help = "Number of the day to execute")
        .int().restrictTo(1..24)
        .check("this day is not implemented yet") { n -> n in days.map { it.number } }

    private val all by option("-a", "--all", help = "Execute all days").flag(default = false)

    private val latest by option("-l", "--latest", help = "Execute the latest day").flag(default = false)

    private val benchmark by option("-b", "--benchmark", help = "Benchmark the runtime of the selected days").flag(default = false)

    override fun run() {
        if (all) {
            execute(days)
        } else if (latest) {
            execute(listOf(days.last()))
        } else if (day != null) {
            execute(listOf(days.first { it.number == day }))
        } else {
            echoFormattedHelp()
        }
    }

    private fun execute(days: List<Day<out Any>>) {
        days.forEach { day ->
            if (benchmark) day.benchmarkImplementation() else day.solvePuzzles()
        }
    }
}