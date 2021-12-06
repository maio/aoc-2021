package cz.maio.aoc2021

import cz.maio.ResourcesUtils.resourceLines
import org.approvaltests.Approvals
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

private data class AgeCount(val age: Int, val countInGroup: Long)

class Day06Test {
    private val squidGroups = resourceLines("/input-06.txt")
        .first()
        .toSquidGroupsByAge() // Map<Age, Count>

    private fun String.toSquidGroupsByAge() = split(",")
        .map { it.toInt() }
        .frequencies()
        .mapValues { it.value.toLong() }

    private val gen = generateSequence(squidGroups) { current ->
        val nextGenEntries = current.flatMap { (age, count) ->
            when (age) {
                // spawning
                0 -> listOf(
                    AgeCount(6, count), // reset existing
                    AgeCount(8, count)  // spawn new
                )
                else -> listOf(AgeCount(age - 1, count))
            }
        }

        nextGenEntries
            .groupBy({ it.age }, { value -> value.countInGroup })
            .mapValues { it.value.sum() }
    }

    @Test
    fun `part 1`() {
        val generations = gen.take(81).toList()

        Approvals.verify(
            generations.take(18).joinToString("\n") {
                it.entries.flatMap { entry ->
                    (1..entry.value).map {
                        entry.key
                    }
                }.joinToString(",")
            }
        )

        assertThat(generations.last().map { it.value }.sum()).isEqualTo(366057)
    }

    @Test
    fun `part 2`() {
        assertThat(gen.take(257).last().map { it.value }.sum()).isEqualTo(1653559299811)
    }
}

