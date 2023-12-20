package de.breisa.aoc2023.utils

/**
 * A polygon defined by a list of vectors.
 */
data class Polygon(val points: List<Vec2>) {
    val area: Double get() {
        var total = 0.0
        for (i in points.indices) {
            total += points[i].x * (points[(i+1).mod(points.size)].y - points[(i-1).mod(points.size)].y)
        }
        return total / 2.0
    }

    val isClockwise: Boolean get() = (area >= 0.0)
}