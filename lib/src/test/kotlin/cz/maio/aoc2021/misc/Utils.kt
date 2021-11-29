package cz.maio.aoc2021.misc

import java.io.File

object ResourcesUtils {
    fun readLinesFromResource(name: String): List<String> {
        val resource = javaClass.getResource(name) ?: error("Failed to get resource '$name'")
        val file = File(resource.toURI())
        return file.readLines()
    }
}