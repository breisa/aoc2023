package de.breisa.aoc2023.days

import de.breisa.aoc2023.days.Day20.PulseType.*
import de.breisa.aoc2023.utils.getResourceAsText
import de.breisa.aoc2023.utils.leastCommonMultiple

/**
 * https://adventofcode.com/2023/day/20
 */
class Day20:Day<Long>(
    number = 20,
    firstExample = """
        broadcaster -> a
        %a -> inv, con
        &inv -> b
        %b -> con
        &con -> output
    """.trimIndent(),
    actualPuzzle = getResourceAsText("/day20/puzzle.txt")
) {
    override fun solveFirstPart(puzzle: String): Long {
        val simulator = parseConfiguration(puzzle)
        repeat(1000) {
            simulator.pressButton()
        }
        return simulator.totalPulseProduct
    }

    /**
     * first we generate a .dot file to visualize the modules
     * the visualization reveals there are four 12-bit counters that are incremented on each button press
     * when the counters reach a certain value they sent a signal towards the final conjunction
     * when all counters send a HIGH to it, rx gets its LOW signal
     * => find trigger period for each counter
     * => find the least common multiple of the periods
     */
    override fun solveSecondPart(puzzle: String): Long {
        val simulator = parseConfiguration(puzzle)
        if (simulator.modules.none { it.name == "rx" }) return 1234 // task only makes sense on the real input
        // generate .dot file for visualization
        // println(simulator.generateDotFile())
        // watch the final conjunction before rx to extract the periods of the counters
        val finalConjunction = simulator.modules.first { it.name == "rx" }.inputs.first() as Conjunction
        val numberOfCounters = finalConjunction.inputs.size
        val triggerPoints = finalConjunction.inputs.associateWith { mutableListOf<Long>() }
        var collectedTriggerPoints = 0
        var step = 0L
        finalConjunction.onIncomingPulse = { pulse ->
            if (pulse.type == HIGH) {
                triggerPoints[pulse.from]?.add(step)
                collectedTriggerPoints++
            }
        }
        // press the button until we triggered every counter twice
        while (collectedTriggerPoints < 2 * numberOfCounters) {
            simulator.pressButton()
            step++
        }
        // calculate the counter periods
        val counterPeriods = triggerPoints.values.map { triggers ->
            triggers.last() - triggers.first()
        }
        return counterPeriods.leastCommonMultiple()
    }

    private class Simulator(val modules: List<Module>) {
        private val button = Dummy("button")
        private val broadcaster = modules.first { it.name == "broadcaster" }
        private val counters = MutableList(PulseType.entries.size) { 0L }
        val totalPulseProduct: Long get() = counters[LOW.ordinal] * counters[HIGH.ordinal]

        fun pressButton() {
            broadcaster.incomingPulses.add(Pulse(button, broadcaster, LOW))
            counters[LOW.ordinal]++
            val moduleQueue = mutableListOf(broadcaster)
            while (moduleQueue.isNotEmpty()) {
                val module = moduleQueue.removeFirst()
                val emittedPulses = module.step()
                emittedPulses.forEach { pulse ->
                    moduleQueue.add(pulse.to)
                    pulse.to.incomingPulses.add(pulse)
                    counters[pulse.type.ordinal]++
                }
            }
        }

        fun generateDotFile(): String {
            val nodes = modules.joinToString("; ") { m ->
                """${m.name} [color="${ when(m) {
                    is FlipFlop -> "red"
                    is Conjunction -> "blue"
                    else -> "black"
                } }"]"""
            }
            val edges = modules.joinToString("; ") { m -> "${m.name} -> { ${m.outputs.joinToString { o -> o.name }} }" }
            return """
                digraph {
                    $nodes
                    $edges
                }
            """.trimIndent()
        }
    }

    private enum class PulseType { LOW, HIGH }

    private data class Pulse(val from: Module, val to: Module, val type: PulseType)

    private abstract class Module(val name: String) {
        val inputs = mutableListOf<Module>()
        val outputs = mutableListOf<Module>()
        val incomingPulses = mutableListOf<Pulse>()
        var onIncomingPulse: (Pulse)->Unit = {}

        fun step(): List<Pulse> {
            if (incomingPulses.isNotEmpty()) {
                val pulse = incomingPulses.removeFirst()
                onIncomingPulse(pulse)
                process(pulse)?.let { type ->
                    return outputs.map { out -> Pulse(this, out, type) }
                }
            }
            return emptyList()
        }

        abstract fun process(pulse: Pulse): PulseType?
    }

    private class Dummy(name: String): Module(name) {
        override fun process(pulse: Pulse): PulseType? = null
    }

    private class Broadcaster: Module("broadcaster") {
        override fun process(pulse: Pulse) = pulse.type
    }

    private class FlipFlop(name: String): Module(name) {
        private var turnedOn = false

        override fun process(pulse: Pulse): PulseType? {
            if (pulse.type == LOW) {
                turnedOn = !turnedOn
                return if (turnedOn) HIGH else LOW
            }
            return null
        }
    }

    private class Conjunction(name: String): Module(name) {
        private var memory = mutableMapOf<Module, PulseType>()

        override fun process(pulse: Pulse): PulseType {
            if (memory.isEmpty()) { // initialize memory on first call
                memory = inputs.associateWith { LOW }.toMutableMap()
            }
            memory[pulse.from] = pulse.type
            return if (memory.values.all { it == HIGH }) LOW else HIGH
        }
    }

    private fun parseConfiguration(puzzle: String): Simulator {
        val lines = puzzle.lines().map { line -> line.split(" -> ") }
        val modules = lines.map { (name, _) ->
            if (name == "broadcaster") {
                Broadcaster()
            } else {
                when (name.first()) {
                    '%' -> FlipFlop(name.drop(1))
                    '&' -> Conjunction(name.drop(1))
                    else -> error("invalid module name '$name'")
                }
            }
        }.associateBy { it.name }.toMutableMap()
        lines.forEach { (moduleName, outputList) ->
            val sourceName = moduleName.replace(Regex("[&%]"), "")
            val source = modules[sourceName] ?: error("")
            outputList.split(", ").map { outputName ->
                if (outputName !in modules) {
                    modules[outputName] = Dummy(outputName)
                }
                val destination = modules[outputName] ?: error("")
                source.outputs.add(destination)
                destination.inputs.add(source)
            }
        }
        return Simulator(modules.values.toList())
    }
}