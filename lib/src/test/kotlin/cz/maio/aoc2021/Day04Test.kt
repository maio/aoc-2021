package cz.maio.aoc2021

import cz.maio.ResourcesUtils.resourceLines
import org.approvaltests.Approvals
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Board(private val rows: List<List<Int>>) {
    val MARK = null
    private var remainingInRows: List<List<Int?>> = rows
    private val remainingInColumns get() = remainingInRows.transpose()

    companion object {
        fun fromString(s: String): Board {
            return s.split("\n")
                .map { it.trim().split("""\s+""".toRegex()).map { it.toInt() } }
                .let { Board(it) }
        }
    }

    override fun toString(): String {
        return rows.toString()
    }

    fun isWinning(): Boolean {
        return remainingInRows.isWinning() || remainingInColumns.isWinning()
    }

    private fun List<List<Int?>>.isWinning() = any { it.mapNotNull { it }.isEmpty() }

    fun play(number: Int) {
        remainingInRows = remainingInRows.map { row ->
            row.map { if (it == number) MARK else it }
        }
    }

    fun sumOfUnmarked(): Int {
        return remainingInRows.flatMap { it }.mapNotNull { it }.sum()
    }
}

class Day04Test {
    private val lines = resourceLines("/input-04.txt")
    private val numbers = lines.first().split(",").map { it.toInt() }
    private val boards = lines.drop(2).joinToString("\n").split("\n\n").map { Board.fromString(it) }

    @Test
    fun `board test`() {
        val board = Board.fromString(
            """
                14 21 17 24  4
                10 16 15  9 19
                18  8 23 26 20
                22 11 13  6  5
                 2  0 12  3  7
             """.trimIndent()
        )

        assertThat(board.isWinning()).isFalse

        listOf(7, 4, 9, 5, 11, 17, 23, 2, 0, 14, 21).forEach {
            board.play(it)
            assertThat(board.isWinning()).isFalse()
        }

        board.play(24)
        assertThat(board.isWinning()).isTrue

        assertThat(board.sumOfUnmarked()).isEqualTo(188)
    }

    @Test
    fun `parse input`() {
        Approvals.verify(
            """
            |$numbers
            |
            |# of boards ${boards.size}
            |
            |${boards.joinToString("\n---\n")}
        """.trimMargin()
        )
    }

    @Test
    fun `part 1`() {
        numbers.forEach { number ->
            boards.forEach {
                it.play(number)
                if (it.isWinning()) {
                    println(it.sumOfUnmarked() * number)
                    return
                }
            }
        }
    }

    @Test
    fun `part 2`() {
        var remainingBoards = boards

        numbers.forEach { number ->
            remainingBoards.forEach {
                it.play(number)

                if (remainingBoards.size == 1 && it.isWinning()) {
                    println(it.sumOfUnmarked() * number)
                    return
                }
            }

            remainingBoards = remainingBoards.filter { !it.isWinning() }
        }
    }
}
