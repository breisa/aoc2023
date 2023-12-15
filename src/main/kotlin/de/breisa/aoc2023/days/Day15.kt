package de.breisa.aoc2023.days

import de.breisa.aoc2023.utils.getResourceAsText

/**
 * https://adventofcode.com/2023/day/15
 */
class Day15: Day<Int>(
    number = 15,
    firstExample = """
        rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7
    """.trimIndent(),
    actualPuzzle = getResourceAsText("/day15/puzzle.txt")
) {
    override fun solveFirstPart(puzzle: String): Int = parseSteps(puzzle).sumOf { step -> hash(step.label) }

    override fun solveSecondPart(puzzle: String): Int {
        val boxes = Array<MutableList<Lens>>(256) { mutableListOf() }
        parseSteps(puzzle).forEach { step ->
            val box = boxes[hash(step.label)]
            val indexOfLens = box.indexOfFirst { it.label == step.label }
            when (step.operation) {
                '=' -> {
                    val lens = Lens(step.label, step.focalLength ?: error("illegal input '$step'"))
                    if (indexOfLens != -1) { // lens with the label is already present
                        box.removeAt(indexOfLens)
                        box.add(indexOfLens, lens)
                    } else {
                        box.add(lens)
                    }
                }
                '-' -> {
                    if (indexOfLens != -1) { // lens with the label is already present
                        box.removeAt(indexOfLens)
                    }
                }
            }
        }
        return boxes.mapIndexed { boxIndex, box ->
            box.mapIndexed { lensIndex, lens ->
                (1 + boxIndex) * (1 + lensIndex) * lens.focalLength
            }.sum()
        }.sum()
    }

    private fun hash(input: String): Int = input.fold(0) { total, c ->
        ((total + c.code) * 17) % 256
    }

    private val stepRegex = Regex("([a-z]+)(=|-)(\\d*)")

    private fun parseSteps(puzzle: String): List<Step> = puzzle.split(",")
        .map { step ->
            stepRegex.matchEntire(step)?.groupValues?.drop(1) ?: error("illegal input '$step'")
        }.map { (label, operation, focalLength) ->
            Step(label, operation.first(), focalLength.toIntOrNull())
        }

    private data class Lens(val label: String, val focalLength: Int)

    private data class Step(val label: String, val operation: Char, val focalLength: Int?)
}