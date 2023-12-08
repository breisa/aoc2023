package de.breisa.aoc2023.days

import de.breisa.aoc2023.core.getResourceAsText
import kotlin.math.min

/**
 * https://adventofcode.com/2023/day/5
 */
class Day05: Day(
    number = 5,
    firstExample = """
        seeds: 79 14 55 13

        seed-to-soil map:
        50 98 2
        52 50 48

        soil-to-fertilizer map:
        0 15 37
        37 52 2
        39 0 15

        fertilizer-to-water map:
        49 53 8
        0 11 42
        42 0 7
        57 7 4

        water-to-light map:
        88 18 7
        18 25 70

        light-to-temperature map:
        45 77 23
        81 45 19
        68 64 13

        temperature-to-humidity map:
        0 69 1
        1 0 69

        humidity-to-location map:
        60 56 37
        56 93 4
    """.trimIndent(),
    actualPuzzle = getResourceAsText("/day05/puzzle.txt")
) {

    override fun solveFirstPart(puzzle: String): Long {
        val seeds = parseSeeds1(puzzle)
        val maps = parseMaps(puzzle)
        val getLocationForSeed = composeMaps("seed", "location", maps)
        return seeds.minOf { getLocationForSeed(it) }.toLong()
    }

    override fun solveSecondPart(puzzle: String) = solveSecondPartFast(puzzle).toLong()

    /**
     * fast algorithm: apply the functions/mappings to ranges instead of individual values
     * runs in  13ms -> speedup of ~43000
     */
    private fun solveSecondPartFast(puzzle: String): Int {
        val seedRanges = parseSeeds2(puzzle).sortedBy { it.first }
        val gardenMaps = parseMaps(puzzle).map { addMissingMappings(it) }
        var currentRanges = seedRanges
        gardenMaps.forEach { gardenMap ->
            currentRanges = mapRanges(currentRanges, gardenMap)
        }
        return currentRanges.minOf { it.start }.toInt()
    }

    private fun mapRanges(ranges: List<LongRange>, gardenMap: GardenMap): List<LongRange> {
        return ranges.flatMap { range ->
            gardenMap.mappings
                .map { it.apply(range) }
                .filter { it != LongRange.EMPTY }
        }
    }

    /**
     * Adds LongMappings so the whole range from Long.MIN_VALUE to Long.MAX_VALUE is covered by the mappings.
     * This makes the intersection calculations easier.
     * Assumes the provided LongMappings do not overlap in the domain.
     * Assumes the LongMappings do never include Long.MIN_VALUE/MAX_VALUE.
     */
    private fun addMissingMappings(gardenMap: GardenMap): GardenMap {
        val mappings = gardenMap.mappings.sortedBy { it.from.start }
        var continueAt = Long.MIN_VALUE
        var index = 0
        val newMappings = mutableListOf<LongMapping>()
        while (index < mappings.size) {
            if (continueAt < mappings[index].from.start) {
                val range = continueAt ..< mappings[index].from.start
                newMappings.add(LongMapping(range, range))
            }
            newMappings.add(mappings[index])
            continueAt = mappings[index].from.endInclusive + 1
            index++
        }
        val range = continueAt .. Long.MAX_VALUE
        newMappings.add(LongMapping(range, range))
        return GardenMap(gardenMap.sourceCategory, gardenMap.destinationCategory, newMappings)
    }

    /**
     * naive/slow algorithm: apply the functions/mapping to every number in the seed ranges
     * works but took 10 minutes on my machine
     */
    private fun solveSecondPartNaive(puzzle: String): Int {
        val seedRanges = parseSeeds2(puzzle).sortedBy { it.first }
        val maps = parseMaps(puzzle)
        val getLocationForSeed = composeMaps("seed", "location", maps)
        return seedRanges.map { it.minOf { getLocationForSeed(it) } }.min().toInt()
    }

    private fun composeMaps(sourceCategory: String, destinationCategory: String, maps: List<GardenMap>): (Long) -> Long {
        var src = sourceCategory
        var composition = { x: Long -> x }
        while (src != destinationCategory) {
            val gm = maps.first { it.sourceCategory == src }
            composition = compose(composition, gm::apply)
            src = gm.destinationCategory
        }
        return composition
    }

    private fun compose(first: (x: Long)->Long, then: (y: Long)-> Long): (Long) -> Long {
        return { z: Long -> then(first(z)) }
    }

    private fun parseSeeds1(puzzle: String) = puzzle.lines().first().split(" ").drop(1).map(String::toLong)

    private fun parseSeeds2(puzzle: String) = parseSeeds1(puzzle).chunked(2).map { (start, length) -> start ..< (start+length) }

    private fun parseMaps(puzzle: String) =
        puzzle.split("\n\n").drop(1)
            .map {
                val mapLines = it.lines()
                val (sourceCategory, _, destinationCategory) = mapLines.first().takeWhile { it != ' ' }.split("-")
                val mappings = mapLines.drop(1).map { line ->
                    line.split(" ")
                        .map(String::toLong)
                        .let { (dest, src, len) -> LongMapping(src..<src + len, dest..<dest + len) }
                }.sortedBy { it.from.first }
                GardenMap(sourceCategory, destinationCategory, mappings)
            }

    data class GardenMap(val sourceCategory: String, val destinationCategory: String, val mappings: List<LongMapping>) {
        fun apply(x: Long) = mappings.firstOrNull { x in it.from }?.apply(x) ?: x
    }

    data class LongMapping(val from: LongRange, val to: LongRange) {
        fun apply(x: Long) = if (x in from) (x - from.first + to.first) else x
        fun apply(x: LongRange): LongRange {
            val intersection = x.rangeIntersect(from)
            return if (intersection != LongRange.EMPTY) apply(intersection.start)..apply(intersection.endInclusive) else LongRange.EMPTY
        }

        fun LongRange.rangeIntersect(other: LongRange) =
            if (other.start > this.endInclusive || this.start > other.endInclusive)
                LongRange.EMPTY
            else
                kotlin.math.max(this.start, other.start) .. min(this.endInclusive, other.endInclusive)

    }

}