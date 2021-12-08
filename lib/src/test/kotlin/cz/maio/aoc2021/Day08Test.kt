package cz.maio.aoc2021

import cz.maio.ResourcesUtils.resourceLines
import org.approvaltests.Approvals
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.*

private data class Entry(
    val signals: List<SortedSet<Char>>,
    val outputs: List<SortedSet<Char>>
) {
    companion object {
        fun fromString(it: String): Entry {
            val (signals, outputs) = it.split(" | ").map { it.split(" ") }
            return Entry(signals.toCharSets(), outputs.toCharSets())
        }
    }

    val unknownSoFar = signals.toMutableSet()

    fun consume(pred: (SortedSet<Char>) -> Boolean) = unknownSoFar
        .single { pred(it) }
        .also { unknownSoFar.remove(it) }

    val n1 = consume { it.size == 2 }
    val n4 = consume { it.size == 4 }
    val n7 = consume { it.size == 3 }
    val n8 = consume { it.size == 7 }

/*
  0:      1:      2:      3:      4:
 aaaa    ....    aaaa    aaaa    ....
b    c  .    c  .    c  .    c  b    c
b    c  .    c  .    c  .    c  b    c
 ....    ....    dddd    dddd    dddd
e    f  .    f  e    .  .    f  .    f
e    f  .    f  e    .  .    f  .    f
 gggg    ....    gggg    gggg    ....

  5:      6:      7:      8:      9:
 aaaa    aaaa    aaaa    aaaa    aaaa
b    .  b    .  .    c  b    c  b    c
b    .  b    .  .    c  b    c  b    c
 dddd    dddd    ....    dddd    dddd
.    f  e    f  .    f  e    f  .    f
.    f  e    f  .    f  e    f  .    f
 gggg    gggg    ....    gggg    gggg
 */

    val n9 = consume { it.size == 6 && it.containsAll(n4) }
    val n0 = consume { it.size == 6 && it.containsAll(n1) }
    val n6 = consume { it.size == 6 }
    val n3 = consume { it.size == 5 && it.containsAll(n1) }
    val n2 = consume { it.size == 5 && it.containsAll(n0.subtract(n6)) }
    val n5 = consume { true }

    val outputToDigit = listOf(n0, n1, n2, n3, n4, n5, n6, n7, n8, n9)
        .zip(0..9)
        .toMap()

    val value = outputs.map { outputToDigit[it] }.joinToString("").toInt()
}

class Day08Test {
    private val entries = resourceLines("/input-08.txt").map(Entry::fromString)

    @Test
    fun `part 1`() {
        Approvals.verify(entries.joinToString("\n\n"))

        val result = entries.flatMap { it.outputs }.count { setOf(2, 3, 4, 7).contains(it.size) }

        assertThat(result).isEqualTo(274)
    }

    @Test
    fun `part 2`() {
        assertThat(entries.sumOf { it.value }).isEqualTo(1012089)
    }
}

private fun Iterable<String>.toCharSets() = map { it.toList().toSortedSet() }
