package cz.maio.aoc2021

import cz.maio.ResourcesUtils.resourceLines
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.math.absoluteValue

class Day07Test {
    private val positions = resourceLines("/input-07.txt").first().split(",").map { it.toInt() }

    private fun costForPart1(position: Int): Int {
        return positions.sumOf {
            (position - it).absoluteValue
        }
    }

    private fun costForPart2(position: Int): Int {
        return positions.sumOf {
            val diff = (position - it).absoluteValue
            (1..diff).sum()
        }
    }


    @Test
    fun `part 1`() {
        val min = positions.min()
        val max = positions.max()

        val costs = (min..max).associateBy {
            costForPart1(it)
        }

        val minCost = costs.keys.min()
        assertThat(minCost).isEqualTo(341534)
    }

    @Test
    fun `part 2`() {
        val min = positions.min()
        val max = positions.max()

        val useParallel = true

        val range = min..max

        val costs = if (useParallel) {
            range.toList().parallelStream().map {
                costForPart2(it) to it
            }.toList().toMap()
        } else {
            range.associateBy {
                costForPart2(it)
            }
        }

        val minCost = costs.keys.min()
        assertThat(minCost).isEqualTo(93397632)
    }
}

fun <T : Comparable<T>> Iterable<T>.min() = this.minOrNull() ?: error("Failed to determine min")
fun <T : Comparable<T>> Iterable<T>.max() = this.maxOrNull() ?: error("Failed to determine max")

