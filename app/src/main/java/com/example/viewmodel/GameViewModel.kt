package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioHapticsEngine
import com.example.engine.ZeroSumEngine
import com.example.model.GameHistorySnapshot
import com.example.model.GameMode
import com.example.model.GameState
import com.example.model.MoveDirection
import com.example.model.PuzzleLevel
import com.example.model.Tile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("zerosum_prefs", Context.MODE_PRIVATE)
    private val audioHaptics = AudioHapticsEngine(application)

    private val undoStack = mutableListOf<GameHistorySnapshot>()

    private val _gameState = MutableStateFlow(
        GameState(
            grid = ZeroSumEngine.createInitialBoard(),
            bestScore = prefs.getInt("best_score", 0),
            soundEnabled = prefs.getBoolean("sound_enabled", true),
            hapticEnabled = prefs.getBoolean("haptic_enabled", true),
            dailyDateString = ZeroSumEngine.getTodayDateFormatted()
        )
    )
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _puzzleLevels = MutableStateFlow(ZeroSumEngine.puzzleLevels)
    val puzzleLevels: StateFlow<List<PuzzleLevel>> = _puzzleLevels.asStateFlow()

    init {
        audioHaptics.setSoundEnabled(_gameState.value.soundEnabled)
        audioHaptics.setHapticEnabled(_gameState.value.hapticEnabled)
        loadPuzzleProgress()
    }

    private fun loadPuzzleProgress() {
        val completedSet = prefs.getStringSet("completed_puzzles", emptySet()) ?: emptySet()
        _puzzleLevels.update { list ->
            list.map { level ->
                if (completedSet.contains(level.id.toString())) {
                    level.copy(isCompleted = true)
                } else {
                    level
                }
            }
        }
    }

    fun makeMove(direction: MoveDirection) {
        val state = _gameState.value
        if (state.isGameOver || state.isZeroBoardVictory) return

        // Execute board move in the engine
        val result = ZeroSumEngine.executeMove(state.grid, direction)
        if (!result.moved) return

        // Push snapshot to undo stack before committing changes
        saveUndoSnapshot(state)

        val newGridMutable = result.newGrid.map { it.toMutableList() }.toMutableList()

        // In Classic and Daily modes, spawn a new tile if grid is not completely empty
        val isZeroBoardAfterMove = ZeroSumEngine.isZeroBoard(newGridMutable)
        if (!isZeroBoardAfterMove && state.gameMode == GameMode.CLASSIC) {
            ZeroSumEngine.spawnRandomTile(newGridMutable)
        }

        val finalizedGrid = newGridMutable.map { it.toList() }
        val isZeroVictory = ZeroSumEngine.isZeroBoard(finalizedGrid)
        val isGameOver = !isZeroVictory && ZeroSumEngine.isGameOver(finalizedGrid)

        // Calculate scores and combos
        val hasAnnihilation = result.annihilations.isNotEmpty()
        val newCombo = if (hasAnnihilation) state.comboCount + 1 else 0
        val comboMultiplier = if (newCombo > 1) newCombo else 1
        val finalScoreGained = result.scoreEarned * comboMultiplier
        val newScore = state.score + finalScoreGained
        val newBest = Math.max(newScore, state.bestScore)

        if (newBest > state.bestScore) {
            prefs.edit().putInt("best_score", newBest).apply()
        }

        // Trigger Sound and Haptic Effects
        if (isZeroVictory) {
            audioHaptics.playVictory()
            if (state.gameMode == GameMode.PUZZLE) {
                markPuzzleCompleted(state.currentPuzzleId)
            }
        } else if (hasAnnihilation) {
            audioHaptics.playAnnihilation()
            if (newCombo > 1) {
                audioHaptics.playCombo(newCombo)
            }
        } else if (result.fusionCount > 0) {
            audioHaptics.playFusion()
        } else if (result.reductionCount > 0) {
            audioHaptics.playReduction()
        } else {
            audioHaptics.playMove()
        }

        if (isGameOver) {
            audioHaptics.playGameOver()
        }

        // Find highest tile
        var maxTileVal = state.highestTileValue
        for (r in 0 until 4) {
            for (c in 0 until 4) {
                finalizedGrid[r][c]?.let {
                    if (it.magnitude > maxTileVal) {
                        maxTileVal = it.magnitude
                    }
                }
            }
        }

        _gameState.update {
            it.copy(
                grid = finalizedGrid,
                score = newScore,
                bestScore = newBest,
                annihilations = it.annihilations + result.annihilations.size,
                moves = it.moves + 1,
                comboCount = newCombo,
                isGameOver = isGameOver,
                isZeroBoardVictory = isZeroVictory,
                canUndo = undoStack.isNotEmpty(),
                lastMoveDirection = direction,
                highestTileValue = maxTileVal,
                recentAnnihilations = result.annihilations
            )
        }
    }

    private fun saveUndoSnapshot(state: GameState) {
        if (undoStack.size >= 8) {
            undoStack.removeAt(0)
        }
        undoStack.add(
            GameHistorySnapshot(
                grid = state.grid,
                score = state.score,
                annihilations = state.annihilations,
                moves = state.moves,
                comboCount = state.comboCount
            )
        )
    }

    fun undo() {
        if (undoStack.isEmpty()) return
        val lastSnapshot = undoStack.removeAt(undoStack.size - 1)
        audioHaptics.playMove()
        _gameState.update {
            it.copy(
                grid = lastSnapshot.grid,
                score = lastSnapshot.score,
                annihilations = lastSnapshot.annihilations,
                moves = lastSnapshot.moves,
                comboCount = lastSnapshot.comboCount,
                isGameOver = false,
                isZeroBoardVictory = false,
                canUndo = undoStack.isNotEmpty(),
                recentAnnihilations = emptyList()
            )
        }
    }

    fun switchMode(mode: GameMode) {
        undoStack.clear()
        when (mode) {
            GameMode.CLASSIC -> startClassicGame()
            GameMode.DAILY -> startDailyGame()
            GameMode.PUZZLE -> startPuzzleLevel(_gameState.value.currentPuzzleId)
        }
    }

    fun restartCurrentGame() {
        undoStack.clear()
        when (_gameState.value.gameMode) {
            GameMode.CLASSIC -> startClassicGame()
            GameMode.DAILY -> startDailyGame()
            GameMode.PUZZLE -> startPuzzleLevel(_gameState.value.currentPuzzleId)
        }
    }

    fun startClassicGame() {
        _gameState.update {
            it.copy(
                grid = ZeroSumEngine.createInitialBoard(),
                score = 0,
                annihilations = 0,
                moves = 0,
                comboCount = 0,
                isGameOver = false,
                isZeroBoardVictory = false,
                gameMode = GameMode.CLASSIC,
                canUndo = false,
                recentAnnihilations = emptyList()
            )
        }
    }

    fun startDailyGame() {
        val todaySeed = ZeroSumEngine.getTodaySeed()
        val dailyBoard = ZeroSumEngine.generateDailyBoard(todaySeed)
        _gameState.update {
            it.copy(
                grid = dailyBoard,
                score = 0,
                annihilations = 0,
                moves = 0,
                comboCount = 0,
                isGameOver = false,
                isZeroBoardVictory = false,
                gameMode = GameMode.DAILY,
                dailyDateString = ZeroSumEngine.getTodayDateFormatted(),
                canUndo = false,
                recentAnnihilations = emptyList()
            )
        }
    }

    fun startPuzzleLevel(levelId: Int) {
        val level = _puzzleLevels.value.find { it.id == levelId } ?: _puzzleLevels.value.first()
        val puzzleBoard = ZeroSumEngine.loadPuzzleBoard(level)
        _gameState.update {
            it.copy(
                grid = puzzleBoard,
                score = 0,
                annihilations = 0,
                moves = 0,
                comboCount = 0,
                isGameOver = false,
                isZeroBoardVictory = false,
                gameMode = GameMode.PUZZLE,
                currentPuzzleId = level.id,
                canUndo = false,
                recentAnnihilations = emptyList()
            )
        }
    }

    private fun markPuzzleCompleted(puzzleId: Int) {
        val completedSet = prefs.getStringSet("completed_puzzles", emptySet())?.toMutableSet() ?: mutableSetOf()
        completedSet.add(puzzleId.toString())
        prefs.edit().putStringSet("completed_puzzles", completedSet).apply()
        loadPuzzleProgress()
    }

    fun nextPuzzleLevel() {
        val nextId = (_gameState.value.currentPuzzleId % _puzzleLevels.value.size) + 1
        startPuzzleLevel(nextId)
    }

    fun toggleSound() {
        val newState = !_gameState.value.soundEnabled
        prefs.edit().putBoolean("sound_enabled", newState).apply()
        audioHaptics.setSoundEnabled(newState)
        _gameState.update { it.copy(soundEnabled = newState) }
    }

    fun toggleHaptic() {
        val newState = !_gameState.value.hapticEnabled
        prefs.edit().putBoolean("haptic_enabled", newState).apply()
        audioHaptics.setHapticEnabled(newState)
        _gameState.update { it.copy(hapticEnabled = newState) }
    }
}
