package de.breisa.aoc2023.days

import de.breisa.aoc2023.core.getResourceAsText

/**
 * https://adventofcode.com/2023/day/2
 */
class Day02: Day<Int>(
    number = 2,
    firstExample = """
        Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
        Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue
        Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red
        Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red
        Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green
    """.trimIndent(),
    actualPuzzle = getResourceAsText("/day02/puzzle.txt")
) {

    override fun solveFirstPart(puzzle: String): Int {
        val actualAmount = CubeAmount(red = 12, green = 13, blue = 14)
        return parseGameRecords(puzzle).filterNot { game ->
            game.samples.any { sample ->
                sample.red > actualAmount.red || sample.green > actualAmount.green || sample.blue > actualAmount.blue
            }
        }.sumOf { it.id }
    }

    override fun solveSecondPart(puzzle: String): Int = parseGameRecords(puzzle).sumOf { game ->
        game.samples.maxOf { it.red } * game.samples.maxOf { it.green } * game.samples.maxOf { it.blue }
    }

    private fun parseGameRecords(puzzle: String): List<Game> {
        val gameLine = Regex("Game (\\d+): (.+)")
        val cubeAmount = Regex("(\\d+) (red|green|blue)(, )?")
        val games = puzzle.lines()
            .filterNot { it == "" }
            .map { line ->
            val (_, id,rounds) = gameLine.matchEntire(line)?.groupValues ?: error("syntax error in line '$line'")
            val samples = rounds.split(";").map { sampleString ->
                val s = CubeAmount()
                cubeAmount.findAll(sampleString.trim()).forEach {
                    val (_, amount, color) = it.groupValues
                    when(color) {
                        "red" -> s.red = amount.toInt()
                        "green" -> s.green = amount.toInt()
                        "blue" -> s.blue = amount.toInt()
                    }
                }
                s
            }
            Game(id.toInt(), samples)
        }
        return games
    }

    data class Game(val id: Int, val samples: List<CubeAmount>)

    data class CubeAmount(var red: Int = 0, var green: Int = 0, var blue: Int = 0)

}