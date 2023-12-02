package de.breisa.aoc2023.core

fun getResourceAsText(path: String): String =
    object {}.javaClass.getResource(path)?.readText() ?: error("failed to load '$path'")
