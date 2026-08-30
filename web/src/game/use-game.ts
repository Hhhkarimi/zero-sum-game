"use client";

import { useCallback, useEffect, useRef, useState } from "react";
import {
  cloneGrid,
  createInitialBoard,
  createSeededRandom,
  executeMove,
  highestMagnitude,
  isGameOver,
  isZeroBoard,
  spawnRandomTile,
} from "./engine";
import { playFeedback } from "./feedback";
import {
  dailyLabel,
  getDailyBoard,
  getPuzzle,
  getPuzzleBoard,
  PUZZLES,
} from "./puzzles";
import {
  DEFAULT_PREFERENCES,
  loadPreferences,
  savePreferences,
} from "./storage";
import type {
  Direction,
  FeedbackKind,
  GameMode,
  GameSnapshot,
  GameState,
  Preferences,
} from "./types";

const MAX_UNDO = 8;

function initialState(): GameState {
  const grid = createInitialBoard(createSeededRandom(0x5eed2026));

  return {
    ...DEFAULT_PREFERENCES,
    grid,
    score: 0,
    clears: 0,
    moves: 0,
    combo: 0,
    highestMagnitude: highestMagnitude(grid),
    isGameOver: false,
    isWon: false,
    mode: "classic",
    puzzleId: 1,
    dailyLabel: dailyLabel(),
    lastFeedback: null,
    lastScoreGain: 0,
    feedbackId: 0,
  };
}

function preferencesFrom(state: GameState): Preferences {
  return {
    bestScore: state.bestScore,
    completedPuzzles: state.completedPuzzles,
    soundEnabled: state.soundEnabled,
    hapticsEnabled: state.hapticsEnabled,
    hasSeenGesture: state.hasSeenGesture,
  };
}

function snapshotFrom(state: GameState): GameSnapshot {
  return {
    grid: cloneGrid(state.grid),
    score: state.score,
    clears: state.clears,
    moves: state.moves,
    combo: state.combo,
    highestMagnitude: state.highestMagnitude,
  };
}

function feedbackForMove(
  won: boolean,
  gameOver: boolean,
  clears: number,
  merges: number,
  reductions: number,
): FeedbackKind {
  if (won) return "win";
  if (gameOver) return "gameover";
  if (clears > 0) return "clear";
  if (merges > 0) return "merge";
  if (reductions > 0) return "reduce";
  return "move";
}

export function useGame() {
  const [state, setState] = useState<GameState>(initialState);
  const [canUndo, setCanUndo] = useState(false);
  const stateRef = useRef(state);
  const undoStack = useRef<GameSnapshot[]>([]);

  const commit = useCallback((next: GameState) => {
    stateRef.current = next;
    setState(next);
  }, []);

  useEffect(() => {
    const stored = loadPreferences();
    const current = stateRef.current;
    commit({
      ...current,
      ...stored,
      bestScore: Math.max(current.bestScore, stored.bestScore),
    });
  }, [commit]);

  const move = useCallback(
    (direction: Direction): boolean => {
      const current = stateRef.current;
      if (current.isGameOver || current.isWon) return false;

      const result = executeMove(current.grid, direction);
      if (!result.moved) return false;

      undoStack.current = [
        ...undoStack.current.slice(-(MAX_UNDO - 1)),
        snapshotFrom(current),
      ];
      setCanUndo(true);

      let grid = result.grid;
      if (current.mode === "classic" && !isZeroBoard(grid)) {
        grid = spawnRandomTile(grid);
      }

      const won = isZeroBoard(grid);
      const gameOver = !won && isGameOver(grid);
      const nextCombo = result.clears.length > 0 ? current.combo + 1 : 0;
      const multiplier = Math.max(1, nextCombo);
      const scoreGain = result.scoreEarned * multiplier;
      const score = current.score + scoreGain;
      const bestScore = Math.max(current.bestScore, score);
      const feedback = feedbackForMove(
        won,
        gameOver,
        result.clears.length,
        result.mergeCount,
        result.reductionCount,
      );
      const completedPuzzles =
        won && current.mode === "puzzle"
          ? Array.from(new Set([...current.completedPuzzles, current.puzzleId])).sort(
              (a, b) => a - b,
            )
          : current.completedPuzzles;

      const next: GameState = {
        ...current,
        grid,
        score,
        bestScore,
        clears: current.clears + result.clears.length,
        moves: current.moves + 1,
        combo: nextCombo,
        highestMagnitude: Math.max(
          current.highestMagnitude,
          highestMagnitude(grid),
        ),
        isWon: won,
        isGameOver: gameOver,
        completedPuzzles,
        hasSeenGesture: true,
        lastFeedback: feedback,
        lastScoreGain: scoreGain,
        feedbackId: current.feedbackId + 1,
      };

      commit(next);
      savePreferences(preferencesFrom(next));
      playFeedback(feedback, current.soundEnabled, current.hapticsEnabled);
      return true;
    },
    [commit],
  );

  const resetForMode = useCallback(
    (mode: GameMode, puzzleId = stateRef.current.puzzleId) => {
      const current = stateRef.current;
      const grid =
        mode === "classic"
          ? createInitialBoard()
          : mode === "daily"
            ? getDailyBoard()
            : getPuzzleBoard(puzzleId);

      undoStack.current = [];
      setCanUndo(false);
      commit({
        ...current,
        grid,
        score: 0,
        clears: 0,
        moves: 0,
        combo: 0,
        highestMagnitude: highestMagnitude(grid),
        isGameOver: false,
        isWon: false,
        mode,
        puzzleId,
        dailyLabel: dailyLabel(),
        lastFeedback: null,
        lastScoreGain: 0,
        feedbackId: current.feedbackId + 1,
      });
    },
    [commit],
  );

  const restart = useCallback(() => {
    resetForMode(stateRef.current.mode, stateRef.current.puzzleId);
  }, [resetForMode]);

  const undo = useCallback((): boolean => {
    const previous = undoStack.current.pop();
    if (!previous) return false;

    const current = stateRef.current;
    const next: GameState = {
      ...current,
      ...previous,
      isGameOver: false,
      isWon: false,
      lastFeedback: "move",
      lastScoreGain: 0,
      feedbackId: current.feedbackId + 1,
    };

    commit(next);
    setCanUndo(undoStack.current.length > 0);
    playFeedback("move", current.soundEnabled, current.hapticsEnabled);
    return true;
  }, [commit]);

  const selectPuzzle = useCallback(
    (puzzleId: number) => resetForMode("puzzle", getPuzzle(puzzleId).id),
    [resetForMode],
  );

  const nextPuzzle = useCallback(() => {
    const currentId = stateRef.current.puzzleId;
    const nextId = currentId >= PUZZLES.length ? 1 : currentId + 1;
    selectPuzzle(nextId);
  }, [selectPuzzle]);

  const setPreference = useCallback(
    (key: "soundEnabled" | "hapticsEnabled", value: boolean) => {
      const next = { ...stateRef.current, [key]: value };
      commit(next);
      savePreferences(preferencesFrom(next));
    },
    [commit],
  );

  return {
    state,
    canUndo,
    move,
    undo,
    restart,
    switchMode: resetForMode,
    selectPuzzle,
    nextPuzzle,
    setSoundEnabled: (value: boolean) => setPreference("soundEnabled", value),
    setHapticsEnabled: (value: boolean) =>
      setPreference("hapticsEnabled", value),
  };
}
