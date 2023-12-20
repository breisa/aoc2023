package de.breisa.aoc2023.days

import de.breisa.aoc2023.utils.getResourceAsText

private val workflowPattern = Regex("([a-z]+)\\{((?:[xmas][><]\\d+:[a-zAR]+,)+)([a-zAR]+)\\}")
private val rulePattern = Regex("([xmas])([><])(\\d+):([a-zAR]+)")
private val partsPattern = Regex("\\{x=(\\d+),m=(\\d+),a=(\\d+),s=(\\d+)\\}")

/**
 * https://adventofcode.com/2023/day/19
 */
class Day19: Day<Long>(
    number = 19,
    firstExample = """
        px{a<2006:qkq,m>2090:A,rfg}
        pv{a>1716:R,A}
        lnx{m>1548:A,A}
        rfg{s<537:gd,x>2440:R,A}
        qs{s>3448:A,lnx}
        qkq{x<1416:A,crn}
        crn{x>2662:A,R}
        in{s<1351:px,qqz}
        qqz{s>2770:qs,m<1801:hdj,R}
        gd{a>3333:R,R}
        hdj{m>838:A,pv}

        {x=787,m=2655,a=1222,s=2876}
        {x=1679,m=44,a=2067,s=496}
        {x=2036,m=264,a=79,s=2244}
        {x=2461,m=1339,a=466,s=291}
        {x=2127,m=1623,a=2188,s=1013}
    """.trimIndent(),
    actualPuzzle = getResourceAsText("/day19/puzzle.txt")
) {
    override fun solveFirstPart(puzzle: String): Long {
        val (workflowMap, parts) = parsePuzzle(puzzle)
        return parts.filter { part -> workflowMap.accepts(part) }
            .sumOf { part -> (part.x + part.m + part.a + part.s).toLong() }
    }

    override fun solveSecondPart(puzzle: String): Long {
        val (workflowMap, _) = parsePuzzle(puzzle)
        val pathRules = findRulesOfPaths("in", workflowMap)
        return pathRules.sumOf { rules ->
            val attributeRanges = rules.getAttributeRanges()
            if (attributeRanges.size == 4) {
                attributeRanges.values.map { it.size.toLong() }.reduce(Long::times)
            } else { // some attributes do not have an allowed range on this path
                0L
            }
        }
    }

    private fun List<Rule>.getAttributeRanges() = listOf("x", "m", "a", "s").mapNotNull { attribute ->
        val attributeRules = this.filter { rule -> rule.attribute == attribute }
        val lowBound = attributeRules.filter { it.operator == ">" }.maxOfOrNull { it.constant } ?: 0
        val highBound = attributeRules.filter { it.operator == "<" }.minOfOrNull { it.constant } ?: 4001
        val range = (lowBound + 1)..< highBound
        if (range.isEmpty()) null else attribute to range
    }.toMap()

    private val IntRange.size get() = this.last - this.first + 1

    private fun findRulesOfPaths(workflowName: String, workflowMap: Map<String, Workflow>, constraints: MutableList<Rule> = mutableListOf()): List<List<Rule>> {
        when (workflowName) {
            "R" -> return emptyList()
            "A" -> return listOf(constraints.toList())
        }
        val results = mutableListOf<List<Rule>>()
        val workflow = workflowMap[workflowName] ?: error("cant find workflow $workflowName")
        for (r in workflow.rules) {
            constraints.add(r)
            results += findRulesOfPaths(r.result, workflowMap, constraints)
            constraints.removeLast()
            constraints.add(r.negate())
        }
        results += findRulesOfPaths(workflow.defaultResult, workflowMap, constraints)
        repeat(workflow.rules.size) {
            constraints.removeLast()
        }
        return results
    }

    private fun Map<String, Workflow>.accepts(part: Part): Boolean {
        var name = "in"
        while (name != "A" && name != "R") {
            name = this[name]?.process(part) ?: error("unknown workflow")
        }
        return name == "A"
    }

    private fun parsePuzzle(puzzle: String): Pair<Map<String, Workflow>, List<Part>> {
        puzzle.split("\n\n").let { (workflowLines, partLines) ->
            val workflows = workflowLines.lines()
                .matchEach(workflowPattern)
                .map { (name, rulesString, defaultResult) ->
                    val rules = rulesString.split(",").dropLast(1)
                        .matchEach(rulePattern)
                        .map { (attribute, operator, constant, result) ->
                            Rule(
                                attribute = attribute,
                                operator = operator,
                                constant = constant.toInt(),
                                result = result
                            )
                        }
                    Workflow(name, rules, defaultResult)
                }.associateBy { workflow -> workflow.name }
            val parts = partLines.lines()
                .matchEach(partsPattern)
                .map { attributes -> attributes.map { n -> n.toInt() } }
                .map { (x, m, a, s) -> Part(x, m, a, s) }
            return Pair(workflows, parts)
        }
    }

    private fun List<String>.matchEach(pattern: Regex) = this.mapNotNull { pattern.matchEntire(it)?.groupValues?.drop(1) }

    private class Rule(val attribute: String, val operator: String, val constant: Int, val result: String) {
        val attributeSelector = when (attribute) {
            "x" -> Part::x
            "m" -> Part::m
            "a" -> Part::a
            "s" -> Part::s
            else -> error("syntax error")
        }

        fun process(part: Part): String? = when (operator) {
            ">" -> if (attributeSelector(part) > constant) result else null
            "<" -> if (attributeSelector(part) < constant) result else null
            else -> error("invalid operator")
        }

        fun negate(): Rule {
            check(constant != Int.MIN_VALUE && constant != Int.MAX_VALUE) { "negating min/max Int values not implemented" }
            return when (operator) {
                ">" -> Rule(attribute, "<", constant + 1, result)
                "<" -> Rule(attribute, ">", constant - 1, result)
                else -> error("invalid operator")
            }
        }

        override fun toString(): String = "$attribute$operator$constant"
    }

    private class Workflow(val name: String, val rules: List<Rule>, val defaultResult: String) {
        fun process(part: Part): String = rules.firstNotNullOfOrNull { rule -> rule.process(part) } ?: defaultResult
    }

    private data class Part(val x: Int, val m: Int, val a: Int, val s: Int)
}