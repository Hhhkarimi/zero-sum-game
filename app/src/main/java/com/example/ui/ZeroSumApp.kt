package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.GameMode
import com.example.ui.components.ControlBar
import com.example.ui.components.DailyShareDialog
import com.example.ui.components.GameBoard
import com.example.ui.components.GameOverDialog
import com.example.ui.components.HeaderStats
import com.example.ui.components.HowToPlayDialog
import com.example.ui.components.PuzzleSelectorDialog
import com.example.ui.components.VictoryDialog
import com.example.ui.theme.CanvasBackground
import com.example.viewmodel.GameViewModel

@Composable
fun ZeroSumApp(
    viewModel: GameViewModel = viewModel()
) {
    val gameState by viewModel.gameState.collectAsState()
    val puzzleLevels by viewModel.puzzleLevels.collectAsState()

    var showHowToPlay by remember { mutableStateOf(false) }
    var showDailyShare by remember { mutableStateOf(false) }
    var showPuzzleSelector by remember { mutableStateOf(false) }

    // Use Persian RTL layout direction for natural RTL Persian typography
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            containerColor = CanvasBackground
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(CanvasBackground)
                    .padding(innerPadding),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 500.dp)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Header & Stats
                    HeaderStats(
                        gameState = gameState,
                        onModeSelected = { mode -> viewModel.switchMode(mode) },
                        onHowToPlayClick = { showHowToPlay = true },
                        onToggleSound = { viewModel.toggleSound() },
                        onToggleHaptic = { viewModel.toggleHaptic() },
                        onRestart = { viewModel.restartCurrentGame() }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Core Game Board with Swipe gestures & effects
                    GameBoard(
                        grid = gameState.grid,
                        recentAnnihilations = gameState.recentAnnihilations,
                        onSwipe = { direction -> viewModel.makeMove(direction) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Controls & D-Pad
                    ControlBar(
                        gameState = gameState,
                        onMove = { direction -> viewModel.makeMove(direction) },
                        onUndo = { viewModel.undo() },
                        onOpenPuzzleList = { showPuzzleSelector = true },
                        onShareDaily = { showDailyShare = true }
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Modal Dialogs
                if (showHowToPlay) {
                    HowToPlayDialog(onDismiss = { showHowToPlay = false })
                }

                if (showDailyShare) {
                    DailyShareDialog(
                        gameState = gameState,
                        onDismiss = { showDailyShare = false }
                    )
                }

                if (showPuzzleSelector) {
                    PuzzleSelectorDialog(
                        levels = puzzleLevels,
                        currentLevelId = gameState.currentPuzzleId,
                        onSelectLevel = { level -> viewModel.startPuzzleLevel(level.id) },
                        onDismiss = { showPuzzleSelector = false }
                    )
                }

                if (gameState.isGameOver) {
                    GameOverDialog(
                        gameState = gameState,
                        onRestart = { viewModel.restartCurrentGame() },
                        onUndo = { viewModel.undo() },
                        onDismiss = { /* Game over stays until action */ }
                    )
                }

                if (gameState.isZeroBoardVictory) {
                    VictoryDialog(
                        gameState = gameState,
                        onNextOrRestart = {
                            if (gameState.gameMode == GameMode.PUZZLE) {
                                viewModel.nextPuzzleLevel()
                            } else {
                                viewModel.restartCurrentGame()
                            }
                        },
                        onShare = { showDailyShare = true },
                        onDismiss = { /* Victory stays until action */ }
                    )
                }
            }
        }
    }
}

