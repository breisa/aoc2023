package de.breisa.aoc2023.days

import de.breisa.aoc2023.utils.getResourceAsText
import de.breisa.aoc2023.utils.leastCommonMultiple

/**
 * https://adventofcode.com/2023/day/8
 */
class Day08: Day<Long>(
    number = 8,
    firstExample = """
        RL

        AAA = (BBB, CCC)
        BBB = (DDD, EEE)
        CCC = (ZZZ, GGG)
        DDD = (DDD, DDD)
        EEE = (EEE, EEE)
        GGG = (GGG, GGG)
        ZZZ = (ZZZ, ZZZ)
    """.trimIndent(),
    secondExample = """
        LR

        11A = (11B, XXX)
        11B = (XXX, 11Z)
        11Z = (11B, XXX)
        22A = (22B, XXX)
        22B = (22C, 22C)
        22C = (22Z, 22Z)
        22Z = (22B, 22B)
        XXX = (XXX, XXX)
    """.trimIndent(),
    actualPuzzle = getResourceAsText("/day08/puzzle.txt")
) {

    /**
     * traverse the graph and count the edges
     */
    override fun solveFirstPart(puzzle: String): Long {
        val (directions, _, edges) = parseInstructions(puzzle)
        return countEdgesUntil("AAA", directions, edges) { it == "ZZZ" }
    }

    /**
     * each start node is on a cycle
     * each cycle contains only 1 end node
     * => the number of steps is the least common multiple of all cycle's periods
     */
    override fun solveSecondPart(puzzle: String): Long {
        val (directions, nodes, edges) = parseInstructions(puzzle)
        val startNodes = nodes.filter { it.last() == 'A' }
        val endNodes = nodes.filter { it.last() == 'Z' }.toSet()
        return startNodes.map { n ->
            countEdgesUntil(n, directions, edges) { it in endNodes }
        }.leastCommonMultiple()
    }

    private fun countEdgesUntil(startNode: String, directions: Sequence<Char>, edges: Map<String, String>, endCondition: (String)->Boolean): Long {
        var currentNode = startNode
        return directions.takeWhile { direction ->
            currentNode = edges.nextNode(currentNode, direction)
            !endCondition(currentNode)
        }.count() + 1L
    }

    private fun parseInstructions(puzzle: String): Triple<Sequence<Char>, Set<String>, Map<String, String>> =
        puzzle.split("\n\n").let { (instructions, graph) ->
            val directions = generateSequence(0) { (it + 1) % instructions.length }.map { instructions[it] }
            val nodes = mutableSetOf<String>()
            val edges = hashMapOf<String, String>()
            graph.filterNot { it in " ()" }.lines()
                 .map { it.split("=", ",") }
                 .forEach { (from, left, right) ->
                     nodes.add(from)
                     edges[edgeKey(from, 'L')] = left
                     edges[edgeKey(from, 'R')] = right
                 }
            Triple(directions, nodes, edges)
        }

    private fun Map<String, String>.nextNode(node: String, direction: Char) =
        this[edgeKey(node, direction)]?: error("incomplete graph")

    private fun edgeKey(node: String, direction: Char) = "$node-$direction"

}