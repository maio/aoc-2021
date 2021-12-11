package cz.maio.aoc2021

import org.approvaltests.Approvals
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

data class Generation(
    private val map: Map<PositionXY, Energy>,
    val flashesCount: Int = 0
) : Iterable<Map.Entry<PositionXY, Energy>> {
    private val dimensions = Dimensions.of(map)

    fun nextGen(): Generation {
        var flashesCount = 0
        var (maybeNextGen, didFlash) = increaseBy { 1.toEnergy() }

        // spread energy from flashes (if any) to neighbours
        while (didFlash.isNotEmpty()) {
            flashesCount += didFlash.size

            val spreadEnergyTo = didFlash.flatMap { it.neighbours }.frequencies()
                .mapValues { it.value.toEnergy() }.withDefault { 0.toEnergy() }
            val next = maybeNextGen.increaseBy { pos -> spreadEnergyTo.getValue(pos) }
            maybeNextGen = next.first
            didFlash = next.second
        }

        return Generation(maybeNextGen.toNormalized().map, flashesCount)
    }

    private fun increaseBy(increaseForPositionFn: (PositionXY) -> Energy): Pair<Generation, Set<PositionXY>> {
        val didFlash = HashSet<PositionXY>()

        val next = associate { (pos, energyBefore) ->
            val next = energyBefore + increaseForPositionFn(pos)

            if (energyBefore <= 9 && next > 9) {
                didFlash.add(pos)
                pos to next
            } else {
                pos to next
            }
        }

        return Generation(next) to didFlash.toSet()
    }

    private fun toNormalized() = Generation(
        associate { (pos, energy) -> if (energy > 9) pos to 0.toEnergy() else pos to energy }
    )

    override fun toString() = buildString {
        (0..dimensions.height).forEach { y ->
            (0..dimensions.width).forEach { x ->
                append(map.getValue(PositionXY(x, y)))
            }
            appendLine()
        }
    }.trim()

    override fun iterator() = map.iterator()

    companion object {
        fun fromString(s: String) = Generation(s.split("\n").flatMapIndexed { y, row ->
            row.toList().mapIndexed { x, d ->
                PositionXY(x, y) to d.digitToInt().toEnergy()
            }
        }.toMap())
    }
}

@JvmInline
value class Energy(private val energy: Int) : Comparable<Int> {
    operator fun plus(other: Energy): Energy = Energy(energy + other.energy)
    override fun compareTo(other: Int) = energy.compareTo(other)
    override fun toString() = energy.toString()
}

private fun Int.toEnergy() = Energy(this)

class Day11Test {
    private val gen = Generation.fromString(
        """
            4764745784
            4643457176
            8322628477
            7617152546
            6137518165
            1556723176
            2187861886
            2553422625
            4817584638
            3754285662
        """.trimIndent()
    )

    private val generations = generateSequence(gen) { it.nextGen() }

    @Test
    fun `generations part 1`() {
        var flashes = 0L

        Approvals.verify(generations.take(101).mapIndexed { index, generation ->
            flashes += generation.flashesCount
            """
            |After step $index:
            |$generation
            """.trimMargin()
        }.joinToString("\n\n"))

        assertThat(flashes).isEqualTo(1588)
    }

    @Test
    fun `part 2`() {
        assertThat(generations.drop(1).takeWhile { it.flashesCount != 100 }.count() + 1).isEqualTo(517)
    }
}

private val PositionXY.x: Int get() = first
private val PositionXY.y: Int get() = second

val PositionXY.topRight get() = x + 1 to y - 1
val PositionXY.topLeft get() = x - 1 to y - 1
val PositionXY.bottomRight get() = x + 1 to y + 1
val PositionXY.bottomLeft get() = x - 1 to y + 1

val PositionXY.neighbours get() = setOf(up, down, left, right, topRight, topLeft, bottomRight, bottomLeft)

data class Dimensions(val width: Int, val height: Int) {
    companion object {
        fun <T> of(map: Map<PositionXY, T>): Dimensions {
            return Dimensions(
                map.keys.maxOf { it.x },
                map.keys.maxOf { it.y }
            )
        }
    }
}