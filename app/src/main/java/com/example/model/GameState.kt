package com.example.model

enum class GameMode(val titleFa: String, val titleEn: String) {
    CLASSIC("بی‌نهایت", "Classic Endless"),
    DAILY("چالش روزانه", "Daily Challenge"),
    PUZZLE("معماهای صفر", "Zero Puzzles")
}

enum class MoveDirection {
    UP, DOWN, LEFT, RIGHT
}

data class Tile(
    val id: Long,
    val value: Int, // Positive = Matter, Negative = Antimatter (e.g. +2, -2, +4, -4, etc.)
    val row: Int,
    val col: Int,
    val isNew: Boolean = false,
    val isMerged: Boolean = false,
    val isAnnihilated: Boolean = false
) {
    val isMatter: Boolean get() = value > 0
    val isAntimatter: Boolean get() = value < 0
    val magnitude: Int get() = Math.abs(value)
}

data class Particle(
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val color: Long,
    val size: Float,
    val alpha: Float,
    val life: Float,
    val maxLife: Float
)

data class AnnihilationEvent(
    val id: Long,
    val row: Int,
    val col: Int,
    val magnitude: Int,
    val timestamp: Long = System.currentTimeMillis()
)

data class PuzzleLevel(
    val id: Int,
    val titleFa: String,
    val descriptionFa: String,
    val initialBoard: List<List<Int>>,
    val targetMoves: Int,
    val isCompleted: Boolean = false,
    val stars: Int = 0
)

data class GameHistorySnapshot(
    val grid: List<List<Tile?>>,
    val score: Int,
    val annihilations: Int,
    val moves: Int,
    val comboCount: Int
)

data class GameState(
    val grid: List<List<Tile?>> = List(4) { List(4) { null } },
    val score: Int = 0,
    val bestScore: Int = 0,
    val annihilations: Int = 0,
    val moves: Int = 0,
    val comboCount: Int = 0,
    val isGameOver: Boolean = false,
    val isZeroBoardVictory: Boolean = false,
    val gameMode: GameMode = GameMode.CLASSIC,
    val currentPuzzleId: Int = 1,
    val dailyDateString: String = "",
    val dailyCompleted: Boolean = false,
    val canUndo: Boolean = false,
    val soundEnabled: Boolean = true,
    val hapticEnabled: Boolean = true,
    val lastMoveDirection: MoveDirection? = null,
    val highestTileValue: Int = 2,
    val recentAnnihilations: List<AnnihilationEvent> = emptyList()
)
