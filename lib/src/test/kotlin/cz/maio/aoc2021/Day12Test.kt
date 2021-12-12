package cz.maio.aoc2021

import cz.maio.ResourcesUtils.resourceLines
import org.approvaltests.Approvals
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

data class CaveEntry(val c1: Cave, val c2: Cave) {
    companion object {
        fun fromString(s: String) = s.split("-").map { it.toCave() }.let { (c1, c2) ->
            CaveEntry(c1, c2)
        }
    }
}

@JvmInline
value class Cave(val id: String) {
    enum class CaveSize {
        BIG,
        SMALL
    }

    val isEnd get() = id == "end"
    val size get() = if (id == id.lowercase()) CaveSize.SMALL else CaveSize.BIG
}

private fun String.toCave() = Cave(this)

data class CavePath(
    val path: List<Cave> = emptyList(),
    val rules: Rules = Rules.V1
) : Iterable<Cave> {
    enum class Rules {
        V1,
        V2
    }

    operator fun plus(cave: Cave) = this.copy(path = path + cave)
    override fun iterator() = path.iterator()
    fun canVisit(cave: Cave): Boolean {
        return when (rules) {
            Rules.V1 -> when (cave.size) {
                Cave.CaveSize.BIG -> true
                Cave.CaveSize.SMALL -> !path.contains(cave)
            }
            Rules.V2 -> when (cave.size) {
                Cave.CaveSize.BIG -> true
                Cave.CaveSize.SMALL -> {
                    val visitsCount = path.count { it == cave }
                    val alreadyVisitedSmallTwice =
                        path.filter { it.size == Cave.CaveSize.SMALL }.frequencies().containsValue(2)

                    when (cave.id) {
                        "start" -> false
                        else -> when (visitsCount) {
                            0 -> true
                            1 -> when (alreadyVisitedSmallTwice) {
                                true -> false
                                false -> true
                            }
                            else -> false
                        }
                    }
                }
            }
        }
    }

    override fun toString(): String {
        return path.joinToString(",") { it.id }
    }
}

class Day12Test {
    private val inputPairs = resourceLines("/input-12.txt").map(CaveEntry::fromString)

    @Test
    fun sample() {
        val paths = walk(resourceLines("/input-12-sample.txt").map(CaveEntry::fromString), CavePath.Rules.V1)

        assertThat(paths).hasSize(226)
        Approvals.verify(paths.map { it.toString() }.sorted().joinToString("\n"))
    }

    private fun walk(input: List<CaveEntry>, rules: CavePath.Rules): List<CavePath> {
        val map = input.flatMap {
            listOf(it.c1 to it.c2, it.c2 to it.c1)
        }.groupBy {
            it.first
        }.mapValues { it.value.map { it.second } }

        return walk(map, "start".toCave(), CavePath(emptyList(), rules))
    }

    private fun walk(map: Map<Cave, List<Cave>>, current: Cave, soFar: CavePath = CavePath()): List<CavePath> {
        val path = soFar + current
        if (current.isEnd) {
            return listOf(path)
        }

        val next = map.getValue(current)

        return next.filter { path.canVisit(it) }.flatMap {
            walk(map, it, path)
        }
    }

    @Test
    fun `part 1`() {
        val paths = walk(inputPairs, CavePath.Rules.V1)
        assertThat(paths).hasSize(4970)
    }

    @Test
    fun `part 2 - sample`() {
        val paths = walk(resourceLines("/input-12-sample1.txt").map(CaveEntry::fromString), CavePath.Rules.V2)
        Approvals.verify(paths.map { it.toString() }.sorted().joinToString("\n"))
    }

    @Test
    fun `part 2`() {
        val paths = walk(resourceLines("/input-12.txt").map(CaveEntry::fromString), CavePath.Rules.V2)

        assertThat(paths.size).isEqualTo(137948)
    }
}

