package cz.maio.aoc2021

import cz.maio.ResourcesUtils
import org.approvaltests.Approvals
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.math.pow

class Day03Test {
    private val rows = ResourcesUtils.resourceLines("/input-03.txt").map {
        it.toCharArray().map { c -> c.digitToInt() }
    }

    private val columns = rows.transpose()

    @Test
    fun parsing() {
        Approvals.verify(columns.joinToString("\n"))
    }

    @Test
    fun `part 1`() {
        val (gamma, epsilon) = columns.map { column ->
            column.mostCommonValue()
        }.let { gammaList ->
            gammaList.toDecimal() to gammaList.invert().toDecimal()
        }

        assertThat(gamma * epsilon).isEqualTo(3374136)
    }

    @Test
    fun `part 2`() {
        val or = computeRatingForCriteria(rows) { it.oxygenBitCriteria() }
        val co2r = computeRatingForCriteria(rows) { it.co2BitCriteria() }

        assertThat(or * co2r).isEqualTo(4432698)
    }

    @Test
    fun `test utils`() {
        assertThat(listOf(1, 0, 1, 0, 1).frequencies()).isEqualTo(mapOf(1 to 3, 0 to 2))
        assertThat(listOf(1, 0, 1, 0, 1).mostCommonValue()).isEqualTo(1)
    }
}

private fun computeRatingForCriteria(rows: List<List<Int>>, computeCriteria: (Map<Int, Int>) -> Int): Int {
    var remainingNumbers = rows
    val columns = rows.transpose()

    columns.indices.forEach { columnId ->
        val currentCol = remainingNumbers.transpose()[columnId]

        val bitCriteria = computeCriteria(currentCol.frequencies())

        remainingNumbers = remainingNumbers.filter {
            it[columnId] == bitCriteria
        }

        remainingNumbers.singleOrNull()?.let {
            return it.toDecimal()
        }
    }

    error("Not found")
}

private fun Map<Int, Int>.oxygenBitCriteria() = if (getValue(1) >= getValue(0)) 1 else 0
private fun Map<Int, Int>.co2BitCriteria() = if (getValue(1) >= getValue(0)) 0 else 1
private fun <E> List<E>.invert(): List<Int> = map { if (it == 0) 1 else 0 }

fun <T> List<T>.mostCommonValue() = frequencies().entries.sortedBy { it.value }.last().key

fun List<Int>.toDecimal() = reversed().mapIndexed { index, i ->
    require(i == 0 || i == 1)
    i * 2.toDouble().pow(index)
}.sum().toInt()

fun <E> List<E>.frequencies() = groupingBy { it }.eachCount()
fun <T> List<List<T>>.transpose() = (0 until this[0].size).map { colId ->
    this.map { it[colId] }
}
