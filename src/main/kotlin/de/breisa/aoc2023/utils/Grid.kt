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

    val positions get() = positionsInOrder(Direction.WEST to Direction.EAST, Direction.NORTH to Direction.SOUTH)
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

    fun swap(a: GridPosition, b: GridPosition) {
        val temp = get(b)
        set(b, get(a))
        set(a, temp)
    }

    fun contains(p: GridPosition) = p.row in rowIndices && p.column in columnIndices

    fun transpose() = Grid(grid.transpose())

    fun clone() = Grid(grid)

    /**
     * Returns all positions of the grid in the specified order.
     *
     * Example: To iterate from left to right and top to bottom call positionsInOrder(WEST to EAST, NORTH to SOUTH).
     */
    fun positionsInOrder(first: MovingDirection, second: MovingDirection) : List<GridPosition> {
        with(Direction) { check(
            ( (first.start in NORTH_SOUTH_AXIS && first.end in NORTH_SOUTH_AXIS) && (second.start in EAST_WEST_AXIS && second.end in EAST_WEST_AXIS) ) ||
            ( (second.start in NORTH_SOUTH_AXIS && second.end in NORTH_SOUTH_AXIS) && (first.start in EAST_WEST_AXIS && first.end in EAST_WEST_AXIS) )
        ) }
        val boundaries = listOf(0, width-1, height-1, 0)
        val innerRange = range(boundaries[first.start.ordinal], boundaries[first.end.ordinal])
        val outerRange = range(boundaries[second.start.ordinal], boundaries[second.end.ordinal])
        return if (first.start in Direction.NORTH_SOUTH_AXIS) {
            outerRange.flatMap { outer -> innerRange.map { inner -> GridPosition(outer, inner) } }
        } else {
            outerRange.flatMap { outer -> innerRange.map { inner -> GridPosition(inner, outer) } }
        }
    }

    private fun range(from: Int, to: Int) = if (from <= to) from..to else from downTo to

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
    val row    get() = y
    val column get() = x

    val north get() = copy(y = y - 1)
    val east  get() = copy(x = x + 1)
    val south get() = copy(y = y + 1)
    val west  get() = copy(x = x - 1)

    fun moveTo(direction: Direction) = when (direction) {
        Direction.NORTH -> north
        Direction.EAST  -> east
        Direction.SOUTH -> south
        Direction.WEST  -> west
    }
}

enum class Direction {
    NORTH, EAST, SOUTH, WEST;

    companion object {
        val NORTH_SOUTH_AXIS = setOf(NORTH, SOUTH)
        val EAST_WEST_AXIS = setOf(EAST, WEST)
    }
}

typealias MovingDirection = Pair<Direction, Direction>
val MovingDirection.start get() = this.first
val MovingDirection.end get() = this.second