package cz.maio.aoc2021

import cz.maio.ResourcesUtils.resourceLines
import org.approvaltests.Approvals
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


private data class Point(val x: Int, val y: Int) : Comparable<Point> {
    companion object {
        fun fromString(s: String): Point = s.split(",")
            .map(String::toInt)
            .let { (x, y) -> Point(x, y) }
    }

    override fun compareTo(other: Point) = when (val cx = x.compareTo(other.x)) {
        0 -> y.compareTo(other.y)
        else -> cx
    }
}

private data class Line(val from: Point, val to: Point) {
    val points: List<Point>
        get() {
            if (from == to) return listOf(from)

            val dx = to.x.compareTo(from.x)
            val dy = to.y.compareTo(from.y)

            return generateSequence(from) {
                it.copy(x = it.x + dx, y = it.y + dy)
            }.takeWhile {
                it != to
            }.toList() + to
        }

    fun isHorizontalOrVertical(): Boolean {
        return from.x == to.x || from.y == to.y
    }

    companion object {
        fun fromString(s: String): Line =
            s.split(" -> ")
                .map(Point::fromString)
                .let { (from, to) -> Line(from, to) }
    }
}

class Day05Test {
    private val lines = resourceLines("/input-05.txt").map(Line::fromString)

    @Test
    fun `part 1`() {
        val frequencies = lines
            .filter { it.isHorizontalOrVertical() }
            .flatMap { it.points }
            .frequencies()

        val result = frequencies.filterValues { it > 1 }.size
        assertThat(result).isEqualTo(5169)
    }

    @Test
    fun `part 2`() {
        val frequencies = lines
            .flatMap { it.points }
            .frequencies()

        val result = frequencies.filterValues { it > 1 }.size
        assertThat(result).isEqualTo(22083)
    }


    @Test
    fun parsing() {
        assertThat(Point.fromString("1,2")).isEqualTo(Point(1, 2))
        assertThat(Line.fromString("1,2 -> 3,4")).isEqualTo(Line(Point(1, 2), Point(3, 4)))
        assertThat(Line.fromString("1,2 -> 3,4")).isEqualTo(Line(Point(1, 2), Point(3, 4)))

        Approvals.verify(
            """
            |${
                lines.joinToString("\n--\n") {
                    """
                    |$it
                    |
                    |  ${it.points.sorted()}
                    """.trimMargin()
                }
            }
            """.trimMargin()
        )
    }

    @Test
    fun `line points`() {
        assertThat(Line.fromString("0,0 -> 1,1").points).isEqualTo(
            listOf(
                Point(0, 0),
                Point(1, 1)
            )
        )
    }
}
