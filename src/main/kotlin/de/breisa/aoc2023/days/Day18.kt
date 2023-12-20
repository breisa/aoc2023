package de.breisa.aoc2023.days

import de.breisa.aoc2023.utils.*
import de.breisa.aoc2023.utils.Direction.*
import kotlin.math.roundToLong

/**
 * https://adventofcode.com/2023/day/18
 */
class Day18:Day<Long>(
    number = 18,
    firstExample = """
        R 6 (#70c710)
        D 5 (#0dc571)
        L 2 (#5713f0)
        D 2 (#d2c081)
        R 2 (#59c680)
        D 2 (#411b91)
        L 5 (#8ceee2)
        U 2 (#caa173)
        L 1 (#1b58a2)
        U 2 (#caa171)
        R 2 (#7807d2)
        U 3 (#a77fa3)
        L 2 (#015232)
        U 2 (#7a21e3)
    """.trimIndent(),
    actualPuzzle = getResourceAsText("/day18/puzzle.txt")
) {
    override fun solveFirstPart(puzzle: String): Long = getAreaFromInstructions(parseDigPlan(puzzle))

    override fun solveSecondPart(puzzle: String): Long =
        getAreaFromInstructions(parseDigPlan(puzzle).reinterpretDigInstructions())

    /**
     * Calculate the area by constructing the outline polygon and using the shoelace formula.
     */
    private fun getAreaFromInstructions(instructions: List<DigInstruction>): Long {
        val outline = getOutlinePolygon(instructions)
        return outline.area.roundToLong()
    }

    private fun getOutlinePolygon(instructions: List<DigInstruction>): Polygon {
        // generate grid positions of corners from the instructions
        val start = Vec2(0.5, 0.5)
        var positions = instructions.runningFold(start) { currentPosition, instruction ->
            currentPosition + instruction.length.toDouble() * instruction.direction.toVec2()
        }
        // ensure the positions are clockwise
        if (!Polygon(positions).isClockwise) {
            positions = positions.asReversed()
        }
        // 'inflate' the polygon to get the real outline
        // the polygon described by the list of instructions misses an area of 0.5 grid-cell-widths all around the polygon
        // we move all corner points outwards based on the two adjacent directions and the fact that we move clockwise
        val upLeft    = Vec2(-0.5, -0.5)
        val upRight   = Vec2(+0.5, -0.5)
        val downLeft  = Vec2(-0.5, +0.5)
        val downRight = Vec2(+0.5, +0.5)
        val outlinePoints = instructions.indices.map { i -> (i-1).mod(instructions.size) to i }
            .map { (i, j) -> instructions[i].direction to instructions[j].direction}
            .zip(positions)
            .map { (directions, point) ->
                val offset = when (directions) {
                    NORTH to EAST -> upLeft
                    NORTH to WEST -> downLeft
                    WEST to NORTH -> downLeft
                    WEST to SOUTH -> downRight
                    EAST to NORTH -> upLeft
                    EAST to SOUTH -> upRight
                    SOUTH to WEST -> downRight
                    SOUTH to EAST -> upRight
                    else -> error("illegal corner at $point!")
                }
                point + offset
            }
        return Polygon(outlinePoints)
    }

    private fun parseDigPlan(puzzle: String): List<DigInstruction> {
        return puzzle.lines().map { line -> line.split(" ") }
            .map { (dir, length, color) ->
                val direction = when (dir) {
                    "R" -> EAST
                    "L" -> WEST
                    "U" -> NORTH
                    "D" -> SOUTH
                    else -> error("invalid direction '$dir'")
                }
                DigInstruction(direction, length.toInt(), color)
            }
    }

    private fun List<DigInstruction>.reinterpretDigInstructions() = this.map { instruction ->
        val length = instruction.color.drop(2).take(5).toInt(16)
        val direction = when (val dir = instruction.color.drop(7).take(1).first()) {
            '0' -> EAST
            '1' -> SOUTH
            '2' -> WEST
            '3' -> NORTH
            else -> error("illegal direction '$dir'")
        }
        DigInstruction(direction, length, instruction.color)
    }

    private fun Direction.toVec2() = when (this) {
        NORTH -> Vec2( 0.0, -1.0)
        EAST  -> Vec2( 1.0,  0.0)
        SOUTH -> Vec2( 0.0,  1.0)
        WEST  -> Vec2(-1.0,  0.0)
    }

    private data class DigInstruction(val direction: Direction, val length: Int, val color: String)
}