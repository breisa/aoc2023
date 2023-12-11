package de.breisa.aoc2023.days

import de.breisa.aoc2023.core.getResourceAsText

/**
 * https://adventofcode.com/2023/day/10
 */
class Day10: Day<Int>(
    number = 10,
    firstExample = """
        .....
        .S-7.
        .|.|.
        .L-J.
        .....
    """.trimIndent(),
    secondExample = """
        ..........
        .S------7.
        .|F----7|.
        .||....||.
        .||....||.
        .|L-7F-J|.
        .|..||..|.
        .L--JL--J.
        ..........
        """.trimIndent(),
    actualPuzzle = getResourceAsText("/day10/puzzle.txt")
) {
    override fun solveFirstPart(puzzle: String): Int = findLoop(PipeMap(puzzle)).size / 2

    override fun solveSecondPart(puzzle: String): Int {
        val smallMap = PipeMap(puzzle)
        // remove everything besides the loop
        val loop = findLoop(smallMap).toSet()
        smallMap.getAllPositions().filter { it !in loop }.forEach { p ->
            smallMap.set(p, '.')
        }
        // enlarge the map to allow the flood-fill to pass through 'empty' gaps
        val bigMap = blowUp(smallMap)
        // fill the outside of the loop
        floodFill(bigMap, Position(0, 0), '.', 'O')
        // count positions that did not get filled (= inner tiles)
        return smallMap.getAllPositions().count { pos ->
            pos.getBlownUpPositions().all { bigMap.get(it) == '.' }
        }
    }

    private fun findLoop(pipeMap: PipeMap): List<Position> {
        val start = pipeMap.startingPosition()
        return findPathUsingDFS(pipeMap, from = start, to = start) ?: error("no path")
    }

    private fun findPathUsingDFS(pipeMap: PipeMap, from: Position, to: Position): List<Position>? {
        val visitNext = mutableListOf(from)
        val alreadyVisited = mutableSetOf<Position>()
        val positionBefore = mutableMapOf<Position, Position>()
        while (visitNext.isNotEmpty()) {
            val current = visitNext.removeFirst()
            val neighbors = pipeMap.getNeighbors(current)
            if (neighbors.contains(to) && positionBefore[current] != to) {
                val path = generateSequence(current) { positionBefore[it] }.toMutableList()
                path.add(0, to)
                return path.reversed()
            }
            neighbors.filter { it !in alreadyVisited }.forEach { next ->
                visitNext.add(0, next)
                positionBefore[next] = current
            }
            alreadyVisited.add(current)
        }
        return null
    }

    private fun floodFill(pipeMap: PipeMap, start: Position, from: Char, to: Char) {
        val toFill = mutableListOf(start)
        while (toFill.isNotEmpty()) {
            val current = toFill.removeFirst()
            if (pipeMap.get(current) != from) continue
            pipeMap.set(current, to)
            listOf(current.north(), current.east(), current.south(), current.west())
                .filter { pipeMap.isOnMap(it) }
                .forEach { toFill.add(it) }
        }
    }

    private class PipeMap(puzzle: String) {
        private val map = puzzle.lines().map { it.toMutableList() }.toMutableList()
        val width = map[0].size
        val height = map.size

        fun startingPosition(): Position = getAllPositions().first { get(it) == 'S' }

        fun getNeighbors(p: Position) = getAllNeighbors(p).filter { getAllNeighbors(it).contains(p) }

        private fun getAllNeighbors(p: Position): List<Position> {
            return when (get(p)) {
                '|' -> listOf(p.north(), p.south())
                '-' -> listOf(p.west(), p.east())
                'L' -> listOf(p.north(), p.east())
                'J' -> listOf(p.north(), p.west())
                '7' -> listOf(p.west(), p.south())
                'F' -> listOf(p.east(), p.south())
                'S' -> listOf(p.north(), p.east(), p.south(), p.west())
                else -> emptyList()
            }.filter { isOnMap(it) }
        }

        fun isOnMap(p: Position) = p.x in map[0].indices && p.y in map.indices

        fun get(p: Position) = map[p.y][p.x]

        fun set(p: Position, c: Char) {
            map[p.y][p.x] = c
        }

        fun getAllPositions() = map.indices.flatMap { y -> map[y].indices.map { x -> Position(x, y) } }

        override fun toString(): String = map.joinToString("\n") { it.joinToString("") }
    }

    private data class Position(val x: Int, val y: Int) {
        fun west() = copy(x = x-1)
        fun east() = copy(x = x+1)
        fun north() = copy(y = y-1)
        fun south() = copy(y = y+1)
        fun getBlownUpPositions() = (0..2).flatMap { i -> (0..2).map { j -> Position(x*3+j, y*3+i) } }
    }

    private fun blowUp(smallMap: PipeMap): PipeMap {
        val bigPuzzle = (".".repeat(smallMap.width*3) + "\n").repeat(smallMap.height*3).dropLast(1)
        val bigMap = PipeMap(bigPuzzle)
        smallMap.getAllPositions().forEach { p ->
            val bigChar = blowUp(smallMap.get(p))
            p.getBlownUpPositions().forEach { blownPos ->
                bigMap.set(blownPos, bigChar.get2D(blownPos.x - p.x*3, blownPos.y - p.y*3))
            }
        }
        return bigMap
    }

    private fun blowUp(c: Char) = when (c) {
        '|' -> ".|.\n.|.\n.|."
        '-' -> "...\n---\n..."
        'L' -> ".|.\n.L-\n..."
        'J' -> ".|.\n-J.\n..."
        '7' -> "...\n-7.\n.|."
        'F' -> "...\n.F-\n.|."
        'S' -> ".|.\n-S-\n.|."
        else -> "...\n...\n..."
    }

    private fun String.get2D(x: Int, y: Int): Char = this.lines()[y][x]
}