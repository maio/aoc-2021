package cz.maio.aoc2021

import cz.maio.ResourcesUtils.resourceLines
import org.approvaltests.Approvals
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

enum class Direction { forward, down, up }

data class Command(
    val direction: Direction,
    val value: Int
)

data class Position(
    val x: Int = 0,
    val depth: Int = 0
) {
    fun applyCommand(command: Command): Position {
        return when (command.direction) {
            Direction.forward -> copy(x = x + command.value)
            Direction.down -> copy(depth = depth + command.value)
            Direction.up -> copy(depth = depth - command.value)
        }
    }
}

data class Position2(
    val aim: Int = 0,
    val x: Int = 0,
    val depth: Int = 0
) {
    fun applyEntry(command: Command): Position2 {
        return when (command.direction) {
            Direction.forward -> copy(x = x + command.value, depth = depth + command.value * aim)
            Direction.down -> copy(aim = aim + command.value)
            Direction.up -> copy(aim = aim - command.value)
        }
    }
}

class Day02Test {
    private val entries = resourceLines("/input-02.txt").map {
        val (cmd, value) = it.split(" ")
        Command(Direction.valueOf(cmd), value.toInt())
    }

    @Test
    fun parsing() {
        Approvals.verify(entries.joinToString("\n\n"))
    }

    @Test
    fun `part 1`() {
        val finalPosition = entries.fold(Position(), Position::applyCommand)

        assertThat(finalPosition.x * finalPosition.depth).isEqualTo(2322630)
    }

    @Test
    fun `part 2`() {
        val finalPosition = entries.fold(Position2(), Position2::applyEntry)

        assertThat(finalPosition.x * finalPosition.depth).isEqualTo(2105273490)
    }
}
