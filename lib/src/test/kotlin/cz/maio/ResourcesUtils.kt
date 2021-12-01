package cz.maio

import java.io.File

object ResourcesUtils {
    fun resourceLines(name: String): List<String> {
        val resource = javaClass.getResource(name)
        val file = File(resource.toURI())
        return file.readLines()
    }
}