package cz.maio.aoc2021

import cz.maio.ResourcesUtils.resourceLines
import org.approvaltests.Approvals
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day01Test {
    private val lines = resourceLines("/input-01.txt").map { it.toInt() }

    @Test
    fun `part 1`() {
        var previous: Int? = null
        var count = 0

        lines.forEach { current ->
            if (current > (previous ?: current)) {
                count++
            }
            previous = current
        }

        assertThat(count).isEqualTo(1581)
    }

    @Test
    fun `part 2`() {
        val windows = lines.windowed(size = 3, step = 1)

        val windowsToCompare = windows.zip(windows.drop(1))

        val count = windowsToCompare.count { (previous, current) ->
            current.sum() > previous.sum()
        }

        Approvals.verify(
            """
                first 4 lines = ${lines.take(4)}
                ...
                last 4 lines = ${lines.takeLast(4)}
                
                first windows to compare: ${windowsToCompare.first()}
                ...
                last windows to compare: ${windowsToCompare.last()}
                
                result = $count
            """.trimIndent()
        )
    }
}
