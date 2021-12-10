package cz.maio.aoc2021

import cz.maio.ResourcesUtils.resourceLines
import org.approvaltests.Approvals
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

val openToClose = "([{<".zip(")]}>").toMap()

class CorruptedLineError(
    val position: Int,
    val char: Char
) : Exception("Illegal character '$char' at position $position")

data class FixedNavigationLine(
    val original: String,
    val charsToFix: List<Char>
) {
    private val scoring = mapOf(
        ')' to 1L,
        ']' to 2L,
        '}' to 3L,
        '>' to 4L,
    )

    val score: Long
        get() = charsToFix.map {
            scoring.getValue(it)
        }.fold(0) { total, itemScore ->
            (total * 5) + itemScore
        }
}

fun processNavigationLine(line: String): FixedNavigationLine {
    val expectClosing = Stack<Char>()
    line.toList().forEachIndexed { index, c ->
        if (openToClose.contains(c)) {
            expectClosing.push(openToClose.getValue(c))
        } else {
            if (expectClosing.pop() != c) {
                throw CorruptedLineError(index, c)
            }
        }
    }

    return FixedNavigationLine(line, expectClosing.reversed())
}

class Day10Test {
    private val lines = resourceLines("/input-10.txt")

    @Test
    fun `part 1`() {
        val scoring = mapOf(
            ')' to 3L,
            ']' to 57L,
            '}' to 1197L,
            '>' to 25137L,
        )

        val illegalChars = lines.mapNotNull {
            try {
                processNavigationLine(it)
                null
            } catch (e: CorruptedLineError) {
                e.char
            }
        }

        val result = illegalChars.map { scoring.getValue(it) }
        assertThat(result.sum()).isEqualTo(367059)
    }

    @Test
    fun `part 2`() {
        val fixed = lines.mapNotNull {
            try {
                processNavigationLine(it)
            } catch (e: CorruptedLineError) {
                null
            }
        }.map { it.score }.sorted()

        assertThat(fixed[((fixed.size - 1) / 2)]).isEqualTo(1952146692)
    }


    @Nested
    inner class ValidateTest {
        @Test
        fun `validate lines`() {
            val checks = lines.joinToString("\n") {
                """$it => ${captureException { processNavigationLine(it) }}"""
            }

            Approvals.verify(checks)
        }

    }
}

fun captureException(fn: () -> FixedNavigationLine): String? {
    val fixed = try {
        fn()
    } catch (e: CorruptedLineError) {
        return e.message
    }

    return "OK - ${fixed.charsToFix.joinToString("")}"
}