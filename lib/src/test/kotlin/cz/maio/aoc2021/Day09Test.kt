package cz.maio.aoc2021

import cz.maio.ResourcesUtils.resourceLines
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

typealias Position3 = Pair<Int, Int>

val Position3.up get() = first to second - 1
val Position3.down get() = first to second + 1
val Position3.left get() = first - 1 to second
val Position3.right get() = first + 1 to second
val Position3.neighbours get() = setOf(up, down, left, right)

fun Map<Position3, Int>.minAround(p: Position3) = listOfNotNull(
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

private fun Map<Position3, Int>.basinOf(position: Position3): Set<Position3> {
    val heightmap = this
    var originalBasin = setOf(position)
    var newBasin = originalBasin

    do {
        originalBasin = newBasin
        newBasin = originalBasin.flatMap {
            it.neighbours.filter { position ->
                (heightmap[position] ?: 9) < 9
            }
        }.toSet() + originalBasin
    } while (newBasin != originalBasin)

    return newBasin
}
