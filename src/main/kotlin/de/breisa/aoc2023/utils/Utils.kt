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

/**
 * Splits a list at the first element matching a given predicate.
 */
fun <T>List<T>.splitAtFirstOrNull(predicate: (T)->Boolean): Pair<List<T>, List<T>>? {
    val first = this.indexOfFirst { predicate(it) }
    if (first == -1) return null
    return Pair(this.take(first), this.drop(first + 1))
}

/**
 * Repeats a list to form a new list.
 */
fun <T>List<T>.repeat(n: Int) = (0..< size * n).map { this[it % size] }

/**
 * Calculates the least common multiple of a list of Longs.
 */
fun List<Long>.leastCommonMultiple() = fold(1L, ::lcm)

private fun lcm(a: Long, b: Long): Long = a * (b / gcd(a, b))

private fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)