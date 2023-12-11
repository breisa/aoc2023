package de.breisa.aoc2023.days

import de.breisa.aoc2023.utils.getResourceAsText
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * https://adventofcode.com/2023/day/6
 */
class Day06: Day<Int>(
    number = 6,
    firstExample = """
        Time:      7  15   30
        Distance:  9  40  200
    """.trimIndent(),
    actualPuzzle = getResourceAsText("/day06/puzzle.txt")
) {

    override fun solveFirstPart(puzzle: String) = parseRaces(puzzle).map { countWinningRaces(it) }.reduce(Int::times)

    override fun solveSecondPart(puzzle: String) = solveSecondPartFast(puzzle)

    private fun solveSecondPartFast(puzzle: String): Int {
        val race = parseLongRace(puzzle)
        // f(x) = (race.time - x) * x - race.distance = -1 * (x*x) + 1 * (race.time * x) - race.distance
        val a = -1.0
        val b = race.time.toDouble()
        val c = -1.0 * race.distance.toDouble()
        val x1 = ( -1.0*b + sqrt(b*b - 4.0 * a * c) ) / 2.0 * a
        val x2 = ( -1.0*b - sqrt(b*b - 4.0 * a * c) ) / 2.0 * a
        val winningRaces = (floor(x2) - ceil(x1) + 1.0).roundToInt()
        return winningRaces
    }

    private fun solveSecondPartNaive(puzzle: String) = countWinningRaces(parseLongRace(puzzle))

    private fun countWinningRaces(r: Race) = (0..r.time).count { holdTime -> distance(holdTime, r.time) > r.distance }

    private fun distance(holdTime: Long, totalTime: Long) = (totalTime - holdTime) * holdTime

    private fun parseRaces(puzzle: String): List<Race> =
        puzzle.lines().let { (times, distances) ->
            val timeValues = times.split(Regex(" +")).drop(1).map { it.toLong() }
            val distanceValues = distances.split(Regex(" +")).drop(1).map { it.toLong() }
            timeValues.zip(distanceValues).map { (time, distance) -> Race(time, distance) }
        }

    private fun parseLongRace(puzzle: String): Race =
        puzzle.replace(" ", "").lines()
            .map { it.split(":").last().toLong() }
            .let { (time, distance) -> Race(time, distance) }

    data class Race(val time: Long, val distance: Long)

}