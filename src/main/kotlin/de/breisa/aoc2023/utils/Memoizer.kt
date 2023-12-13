package de.breisa.aoc2023.utils

/**
 * A wrapper for HashMap to ease the implementation of memoization.
 */
class Memoizer<R>() {
    var hits = 0L
    var misses = 0L
    val memos: HashMap<String, R> = HashMap()

    fun getMemoOrNull(vararg arguments: Any): R? {
        val result = memos[getKey(arguments)]
        if (result == null) misses++ else hits++
        return result
    }

    fun memoize(result: R, vararg arguments: Any): R {
        memos[getKey(arguments)] = result
        return result
    }

    private fun getKey(arguments: Array<out Any>) = arguments.joinToString(":")
}