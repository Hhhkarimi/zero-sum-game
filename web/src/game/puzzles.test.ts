import { describe, expect, it } from "vitest";
import { executeMove, gridValues, isZeroBoard } from "./engine";
import { getDailyBoard, getPuzzleBoard, PUZZLES } from "./puzzles";
import type { Direction, Grid } from "./types";

const DIRECTIONS: Direction[] = ["up", "down", "left", "right"];

function key(grid: Grid): string {
  return gridValues(grid).flat().join(",");
}

function shortestSolution(start: Grid, maxDepth = 14): Direction[] | null {
  const queue: Array<{ grid: Grid; moves: Direction[] }> = [
    { grid: start, moves: [] },
  ];
  const visited = new Set([key(start)]);

  for (let cursor = 0; cursor < queue.length; cursor += 1) {
    const current = queue[cursor];
    if (isZeroBoard(current.grid)) return current.moves;
    if (current.moves.length >= maxDepth) continue;

    for (const direction of DIRECTIONS) {
      const result = executeMove(current.grid, direction);
      if (!result.moved) continue;
      const nextKey = key(result.grid);
      if (visited.has(nextKey)) continue;
      visited.add(nextKey);
      queue.push({
        grid: result.grid,
        moves: [...current.moves, direction],
      });
    }
  }

  return null;
}

describe("puzzle catalog", () => {
  it.each(PUZZLES)("puzzle $id has a verified solution", (puzzle) => {
    const solution = shortestSolution(getPuzzleBoard(puzzle.id));
    expect(solution, `Puzzle ${puzzle.id} is not solvable`).not.toBeNull();
    expect(solution?.length).toBeLessThanOrEqual(puzzle.targetMoves);
  });

  it("builds the same daily board for the same local date", () => {
    const date = new Date(2026, 7, 30, 12);
    expect(gridValues(getDailyBoard(date))).toEqual(gridValues(getDailyBoard(date)));
  });
});
