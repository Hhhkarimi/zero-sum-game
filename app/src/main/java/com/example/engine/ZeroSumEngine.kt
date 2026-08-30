package com.example.engine

import com.example.model.AnnihilationEvent
import com.example.model.MoveDirection
import com.example.model.PuzzleLevel
import com.example.model.Tile
import java.util.Calendar
import java.util.Random

data class MoveResult(
    val newGrid: List<List<Tile?>>,
    val moved: Boolean,
    val scoreEarned: Int,
    val annihilations: List<AnnihilationEvent>,
    val fusionCount: Int,
    val reductionCount: Int
)

object ZeroSumEngine {

    private var nextTileId = 1000L

    fun generateTileId(): Long = ++nextTileId

    fun createInitialBoard(): List<List<Tile?>> {
        val board = MutableList(4) { MutableList<Tile?>(4) { null } }
        // Spawn 2 initial tiles
        spawnRandomTile(board)
        spawnRandomTile(board)
        return board.map { it.toList() }
    }

    fun spawnRandomTile(grid: MutableList<MutableList<Tile?>>): Tile? {
        val emptyCells = mutableListOf<Pair<Int, Int>>()
        for (r in 0 until 4) {
            for (c in 0 until 4) {
                if (grid[r][c] == null) {
                    emptyCells.add(r to c)
                }
            }
        }
        if (emptyCells.isEmpty()) return null

        val (r, c) = emptyCells.random()
        // 70% magnitude 2, 30% magnitude 4.
        val magnitude = if (Math.random() < 0.70) 2 else 4
        // 50% positive (Matter), 50% negative (Antimatter)
        val sign = if (Math.random() < 0.50) 1 else -1
        val value = sign * magnitude

        val tile = Tile(
            id = generateTileId(),
            value = value,
            row = r,
            col = c,
            isNew = true
        )
        grid[r][c] = tile
        return tile
    }

    fun executeMove(
        currentGrid: List<List<Tile?>>,
        direction: MoveDirection
    ): MoveResult {
        var scoreEarned = 0
        val annihilations = mutableListOf<AnnihilationEvent>()
        var fusions = 0
        var reductions = 0
        var hasChanged = false

        val newGrid = MutableList(4) { MutableList<Tile?>(4) { null } }

        for (i in 0 until 4) {
            // Extract the 4 tiles in the current line according to direction
            val originalLine = mutableListOf<Tile?>()
            for (j in 0 until 4) {
                val (r, c) = when (direction) {
                    MoveDirection.LEFT -> i to j
                    MoveDirection.RIGHT -> i to (3 - j)
                    MoveDirection.UP -> j to i
                    MoveDirection.DOWN -> (3 - j) to i
                }
                originalLine.add(currentGrid[r][c])
            }

            // Compact and resolve collisions in this line
            val nonNull = originalLine.filterNotNull()
            val resolvedLine = mutableListOf<Tile>()
            var idx = 0

            while (idx < nonNull.size) {
                val current = nonNull[idx]
                if (idx + 1 < nonNull.size) {
                    val next = nonNull[idx + 1]

                    if (current.value == -next.value) {
                        // 1. QUANTUM ANNIHILATION (+X and -X) -> 0
                        val mag = current.magnitude
                        scoreEarned += mag * 4 // Huge bonus for annihilation!
                        annihilations.add(
                            AnnihilationEvent(
                                id = generateTileId(),
                                row = when (direction) {
                                    MoveDirection.LEFT, MoveDirection.RIGHT -> i
                                    MoveDirection.UP -> resolvedLine.size
                                    MoveDirection.DOWN -> 3 - resolvedLine.size
                                },
                                col = when (direction) {
                                    MoveDirection.LEFT -> resolvedLine.size
                                    MoveDirection.RIGHT -> 3 - resolvedLine.size
                                    MoveDirection.UP, MoveDirection.DOWN -> i
                                },
                                magnitude = mag
                            )
                        )
                        hasChanged = true
                        idx += 2 // Both consumed!
                        continue
                    } else if (current.value == next.value) {
                        // 2. FUSION (Same signs and same values, e.g. +2 & +2 -> +4 or -4 & -4 -> -8)
                        val mergedValue = current.value * 2
                        scoreEarned += Math.abs(mergedValue)
                        fusions++
                        resolvedLine.add(
                            Tile(
                                id = generateTileId(),
                                value = mergedValue,
                                row = 0,
                                col = 0,
                                isMerged = true
                            )
                        )
                        hasChanged = true
                        idx += 2
                        continue
                    } else if ((current.isMatter && next.isAntimatter) || (current.isAntimatter && next.isMatter)) {
                        // 3. REDUCTION (Opposite signs, unequal magnitudes: +8 + -4 = +4, or -16 + +4 = -12)
                        val diffValue = current.value + next.value
                        scoreEarned += Math.abs(current.value) + Math.abs(next.value)
                        reductions++
                        resolvedLine.add(
                            Tile(
                                id = generateTileId(),
                                value = diffValue,
                                row = 0,
                                col = 0,
                                isMerged = true
                            )
                        )
                        hasChanged = true
                        idx += 2
                        continue
                    }
                }

                // No collision with next tile, just keep current
                resolvedLine.add(current.copy(isNew = false, isMerged = false))
                idx++
            }

            // Put resolved tiles back into newGrid with updated row/col
            for (j in 0 until 4) {
                val (r, c) = when (direction) {
                    MoveDirection.LEFT -> i to j
                    MoveDirection.RIGHT -> i to (3 - j)
                    MoveDirection.UP -> j to i
                    MoveDirection.DOWN -> (3 - j) to i
                }

                if (j < resolvedLine.size) {
                    val tile = resolvedLine[j].copy(row = r, col = c)
                    newGrid[r][c] = tile
                } else {
                    newGrid[r][c] = null
                }
            }

            // Check if positions changed even if no merges occurred
            for (j in 0 until 4) {
                val (r, c) = when (direction) {
                    MoveDirection.LEFT -> i to j
                    MoveDirection.RIGHT -> i to (3 - j)
                    MoveDirection.UP -> j to i
                    MoveDirection.DOWN -> (3 - j) to i
                }
                val oldTile = currentGrid[r][c]
                val newTile = newGrid[r][c]
                if (oldTile?.id != newTile?.id || oldTile?.value != newTile?.value) {
                    hasChanged = true
                }
            }
        }

        return MoveResult(
            newGrid = newGrid.map { it.toList() },
            moved = hasChanged,
            scoreEarned = scoreEarned,
            annihilations = annihilations,
            fusionCount = fusions,
            reductionCount = reductions
        )
    }

    fun isGameOver(grid: List<List<Tile?>>): Boolean {
        // If there's an empty cell, game is not over
        for (r in 0 until 4) {
            for (c in 0 until 4) {
                if (grid[r][c] == null) return false
            }
        }

        // If any adjacent tiles can fuse, reduce, or annihilate, game is not over
        for (r in 0 until 4) {
            for (c in 0 until 4) {
                val current = grid[r][c] ?: continue
                // Check right neighbor
                if (c + 1 < 4) {
                    val right = grid[r][c + 1]
                    if (right != null) {
                        if (canInteract(current.value, right.value)) return false
                    }
                }
                // Check bottom neighbor
                if (r + 1 < 4) {
                    val bottom = grid[r + 1][c]
                    if (bottom != null) {
                        if (canInteract(current.value, bottom.value)) return false
                    }
                }
            }
        }

        return true
    }

    private fun canInteract(v1: Int, v2: Int): Boolean {
        // Can fuse (v1 == v2) OR can annihilate/reduce (opposite signs: (v1 > 0 && v2 < 0) || (v1 < 0 && v2 > 0))
        if (v1 == v2) return true
        if ((v1 > 0 && v2 < 0) || (v1 < 0 && v2 > 0)) return true
        return false
    }

    fun isZeroBoard(grid: List<List<Tile?>>): Boolean {
        for (r in 0 until 4) {
            for (c in 0 until 4) {
                if (grid[r][c] != null) return false
            }
        }
        return true
    }

    // Daily Challenge Generator (Deterministic based on date seed)
    fun generateDailyBoard(dateSeed: Long): List<List<Tile?>> {
        val random = Random(dateSeed)
        val board = MutableList(4) { MutableList<Tile?>(4) { null } }
        
        // Spawn 6 to 8 paired/interacting tiles
        val tileValues = listOf(
            2, -2, 4, -4, 8, -8, 2, -2
        ).shuffled(random)

        val positions = mutableListOf<Pair<Int, Int>>()
        for (r in 0 until 4) {
            for (c in 0 until 4) {
                positions.add(r to c)
            }
        }
        positions.shuffle(random)

        for (i in 0 until Math.min(tileValues.size, 8)) {
            val (r, c) = positions[i]
            board[r][c] = Tile(
                id = generateTileId(),
                value = tileValues[i],
                row = r,
                col = c,
                isNew = true
            )
        }

        return board.map { it.toList() }
    }

    fun getTodaySeed(): Long {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)
        return (year * 10000L + month * 100L + day)
    }

    fun getTodayDateFormatted(): String {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)
        return "$year/$month/$day"
    }

    // 15 Handcrafted Zero Puzzle Levels
    val puzzleLevels: List<PuzzleLevel> = listOf(
        PuzzleLevel(
            id = 1,
            titleFa = "مرحله ۱: اولین برخورد کوانتومی",
            descriptionFa = "با ۲ حرکت، ماده و پادماده را به یکدیگر بکوبید تا صفحه کاملاً صفر شود.",
            initialBoard = listOf(
                listOf(2, 0, 0, -2),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0)
            ),
            targetMoves = 2
        ),
        PuzzleLevel(
            id = 2,
            titleFa = "مرحله ۲: واکنش دوگانه",
            descriptionFa = "دو جفت متضاد را نابود کنید تا به نقطه صفر برسید.",
            initialBoard = listOf(
                listOf(4, 0, 0, -4),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0),
                listOf(-2, 0, 0, 2)
            ),
            targetMoves = 3
        ),
        PuzzleLevel(
            id = 3,
            titleFa = "مرحله ۳: ساخت و تخریب (Fusion to Zero)",
            descriptionFa = "ابتدا دو عدد ۲+ را ادغام کرده و سپس با ۴- نابود کنید!",
            initialBoard = listOf(
                listOf(2, 0, 2, 0),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, -4),
                listOf(0, 0, 0, 0)
            ),
            targetMoves = 3
        ),
        PuzzleLevel(
            id = 4,
            titleFa = "مرحله ۴: تفریق استراتژیک",
            descriptionFa = "یک ۸+ و دو تا ۲- و یک ۴- در صفحه است. صفحه را به صفر مطلق برسانید.",
            initialBoard = listOf(
                listOf(8, 0, -4, 0),
                listOf(0, -2, 0, -2),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0)
            ),
            targetMoves = 4
        ),
        PuzzleLevel(
            id = 5,
            titleFa = "مرحله ۵: تقارن گوشه‌ها",
            descriptionFa = "گوشه‌های جدول را به مرکز هدایت کرده و تسویه کامل کنید.",
            initialBoard = listOf(
                listOf(4, 0, 0, -2),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0),
                listOf(-2, 0, 0, 0)
            ),
            targetMoves = 4
        ),
        PuzzleLevel(
            id = 6,
            titleFa = "مرحله ۶: گردباد پادماده",
            descriptionFa = "پادماده‌های قدرتمند را ادغام و با ماده نابود کنید.",
            initialBoard = listOf(
                listOf(-8, 0, 0, 16),
                listOf(0, -4, 0, 0),
                listOf(0, 0, -4, 0),
                listOf(0, 0, 0, 0)
            ),
            targetMoves = 5
        ),
        PuzzleLevel(
            id = 7,
            titleFa = "مرحله ۷: ماتریس ۱۶ کوانتومی",
            descriptionFa = "یک ۱۶+ و ترکیبی از پادماده‌های ۲-، ۴-، ۸- و ۲-!",
            initialBoard = listOf(
                listOf(16, 0, -8, 0),
                listOf(0, -4, 0, -2),
                listOf(0, 0, -2, 0),
                listOf(0, 0, 0, 0)
            ),
            targetMoves = 5
        ),
        PuzzleLevel(
            id = 8,
            titleFa = "مرحله ۸: تله ستونی",
            descriptionFa = "ستون‌ها را بدون گیر افتادن خلوت کنید.",
            initialBoard = listOf(
                listOf(2, -4, 8, -16),
                listOf(2, 0, 0, 0),
                listOf(0, 4, 0, 0),
                listOf(0, 0, -8, 16)
            ),
            targetMoves = 6
        ),
        PuzzleLevel(
            id = 9,
            titleFa = "مرحله ۹: سوپرنوا (Supernova)",
            descriptionFa = "یک عدد ۳۲+ و آرایه‌ای از منفی‌ها که باید زنجیره‌ای نابود شوند!",
            initialBoard = listOf(
                listOf(32, 0, 0, -16),
                listOf(0, -8, 0, -4),
                listOf(0, 0, -2, -2),
                listOf(0, 0, 0, 0)
            ),
            targetMoves = 6
        ),
        PuzzleLevel(
            id = 10,
            titleFa = "مرحله ۱۰: پادشاهی صفر",
            descriptionFa = "معمای نهایی پیشرفته: تعادل کامل بین دو بعد ماده و پادماده!",
            initialBoard = listOf(
                listOf(16, -8, 4, -2),
                listOf(-16, 8, -4, 2),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0)
            ),
            targetMoves = 4
        )
    )

    fun loadPuzzleBoard(level: PuzzleLevel): List<List<Tile?>> {
        val board = MutableList(4) { MutableList<Tile?>(4) { null } }
        for (r in 0 until 4) {
            for (c in 0 until 4) {
                val v = level.initialBoard.getOrNull(r)?.getOrNull(c) ?: 0
                if (v != 0) {
                    board[r][c] = Tile(
                        id = generateTileId(),
                        value = v,
                        row = r,
                        col = c,
                        isNew = true
                    )
                }
            }
        }
        return board.map { it.toList() }
    }
}
