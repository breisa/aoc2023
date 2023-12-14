package de.breisa.aoc2023.utils

/**
 * A mutable 2-dimensional grid of objects with support for parsing from a string.
 */
class Grid<T>(data: Iterable<Iterable<T>>) {
    private val grid = data.map { row -> row.toMutableList() }.toMutableList()

    val width = grid.first().size
    val height = grid.size

    val rowIndices = grid.indices
    val columnIndices = grid.first().indices

    val positions get() = grid.indices.flatMap { y -> grid.first().indices.map { x -> GridPosition(x, y) } }
    val values get() = positions.map { get(it) }
    val positionedValues get() = positions.map { it to get(it) }

    val rows get() = grid.map { row -> row.toList() }
    val columns get() = grid.transpose().map { column -> column.toList() }

    init {
        grid.forEach { row -> check(row.size == width) { "all rows have to be equally long" } }
    }

    fun get(p: GridPosition) = grid[p.row][p.column]

    fun set(p: GridPosition, value: T) {
        grid[p.row][p.column] = value
    }

    fun contains(p: GridPosition) = p.row in rowIndices && p.column in columnIndices

    fun transpose() = Grid(grid.transpose())

    fun toString(columnSeparator: String = ", ", rowSeparator: String = "\n", includeHeader: Boolean = false): String {
        return (if (includeHeader) "Grid(width=$width, height=$height)\n" else "") +
                grid.joinToString(rowSeparator) { row -> row.joinToString(columnSeparator) }
    }

    override fun toString(): String = toString(includeHeader = true)

    companion object {
        fun <T>parse(data: String, columnSeparator: String = "", rowSeparator: String = "\n", cellParser: (String)->T): Grid<T> {
            return Grid(data.split(rowSeparator).map { row ->
                var cells = row.split(columnSeparator)
                if (columnSeparator == "") cells = cells.drop(1).dropLast(1)
                cells.map(cellParser)
            })
        }
    }
}

data class GridPosition(val x: Int, val y: Int) {
    val row get() = y
    val column get() = x
    val north get() = copy(y = y-1)
    val east get() = copy(x = x+1)
    val south get() = copy(y = y+1)
    val west get() = copy(x = x-1)
}