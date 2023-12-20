package de.breisa.aoc2023.utils

/**
 * A 2d vector.
 */
data class Vec2(val x: Double, val y: Double) {
    operator fun plus(other: Vec2) = Vec2(this.x + other.x, this.y + other.y)
    operator fun minus(other: Vec2) = Vec2(this.x - other.x, this.y - other.y)
    operator fun times(scalar: Double) = Vec2(scalar * x, scalar * y)
}

operator fun Double.times(vector: Vec2) = vector * this