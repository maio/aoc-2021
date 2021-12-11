package cz.maio.aoc2021

import cz.maio.ResourcesUtils.resourceLines
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

typealias PositionXY = Pair<Int, Int>

val PositionXY.up get() = first to second - 1
val PositionXY.down get() = first to second + 1
val PositionXY.left get() = first - 1 to second
val PositionXY.right get() = first + 1 to second
val PositionXY.neighboursUpDownLeftRight get() = setOf(up, down, left, right)

fun Map<PositionXY, Int>.minAround(p: PositionXY) = listOfNotNull(
    get(p.up), get(p.down),
    get(p.left), get(p.right),
).min()

class Day09Test {
    private val heightmap = resourceLines("/input-09.txt").mapIndexed { rowId, row ->
        row.toList().mapIndexed { colId, colItem ->
            (colId to rowId) to colItem.digitToInt()
        }
    }.flatten().toMap()

    private val mins = heightmap.filterKeys {
        val minAround = heightmap.minAround(it)
        heightmap.getValue(it) < minAround
    }

    @Test
    fun `part 1`() {
        val result = mins.values.sumOf { it + 1 }
        assertThat(result).isEqualTo(512)
    }

    @Test
    fun `part 2`() {
        val result = mins.keys.map {
            heightmap.basinOf(it)
        }.toSet().sortedBy { -it.size }.take(3)

        assertThat(result.map { it.size }).isEqualTo(listOf(121, 116, 114))
        println(121 * 116 * 114)
    }
}

private fun Map<PositionXY, Int>.basinOf(position: PositionXY): Set<PositionXY> {
    val heightmap = this
    var originalBasin = setOf(position)
    var newBasin = originalBasin

    do {
        originalBasin = newBasin
        newBasin = originalBasin.flatMap {
            it.neighboursUpDownLeftRight.filter { position ->
                (heightmap[position] ?: 9) < 9
            }
        }.toSet() + originalBasin
    } while (newBasin != originalBasin)

    return newBasin
}
