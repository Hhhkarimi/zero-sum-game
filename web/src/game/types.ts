export const BOARD_SIZE = 4;

export type Direction = "up" | "down" | "left" | "right";
export type GameMode = "classic" | "daily" | "puzzle";
export type FeedbackKind =
  | "move"
  | "clear"
  | "merge"
  | "reduce"
  | "win"
  | "gameover";

export interface Tile {
  id: number;
  value: number;
  row: number;
  col: number;
  isNew?: boolean;
  isMerged?: boolean;
}

export type Grid = Array<Array<Tile | null>>;

export interface ClearEvent {
  id: number;
  row: number;
  col: number;
  magnitude: number;
}

export interface MoveResult {
  grid: Grid;
  moved: boolean;
  scoreEarned: number;
  clears: ClearEvent[];
  mergeCount: number;
  reductionCount: number;
}

export interface PuzzleLevel {
  id: number;
  title: string;
  description: string;
  initialBoard: number[][];
  targetMoves: number;
}

export interface Preferences {
  bestScore: number;
  completedPuzzles: number[];
  soundEnabled: boolean;
  hapticsEnabled: boolean;
  hasSeenGesture: boolean;
}

export interface GameState extends Preferences {
  grid: Grid;
  score: number;
  clears: number;
  moves: number;
  combo: number;
  highestMagnitude: number;
  isGameOver: boolean;
  isWon: boolean;
  mode: GameMode;
  puzzleId: number;
  dailyLabel: string;
  lastFeedback: FeedbackKind | null;
  lastScoreGain: number;
  feedbackId: number;
}

export interface GameSnapshot {
  grid: Grid;
  score: number;
  clears: number;
  moves: number;
  combo: number;
  highestMagnitude: number;
}
