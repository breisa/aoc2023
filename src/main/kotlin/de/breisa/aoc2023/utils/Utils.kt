package de.breisa.aoc2023.utils

/**
 * Loads a text resource using the class loader.
 */
fun getResourceAsText(path: String): String =
    object {}.javaClass.getResource(path)?.readText() ?: error("failed to load '$path'")

/**
 * Transposes a 2d grid consisting of lists.
 *
 * Assumes all rows are of equal length.
 */
fun <T>List<List<T>>.transpose(): List<List<T>> = this.first().indices.map { col -> this.indices.map { row -> this[row][col] } }

/**
 * Returns the sublist between two indices including the lower and excluding the higher one.
 */
fun <T>List<T>.subListBetween(index1: Int, index2: Int) = listOf(index1, index2).sorted().let { (a, b) -> subList(a, b) }
