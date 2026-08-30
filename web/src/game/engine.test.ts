import { describe, expect, it } from "vitest";
import {
  createBoard,
  createInitialBoard,
  createSeededRandom,
  executeMove,
  gridValues,
  isGameOver,
  isZeroBoard,
} from "./engine";

describe("zero-sum engine", () => {
  it("compacts a line without changing its values", () => {
    const board = createBoard([
      [0, 2, 0, 4],
      [0, 0, 0, 0],
      [0, 0, 0, 0],
      [0, 0, 0, 0],
    ]);

    const result = executeMove(board, "left");

    expect(result.moved).toBe(true);
    expect(gridValues(result.grid)[0]).toEqual([2, 4, 0, 0]);
    expect(result.scoreEarned).toBe(0);
  });

  it("clears equal values with opposite signs", () => {
    const result = executeMove(
      createBoard([
        [2, 0, 0, -2],
        [0, 0, 0, 0],
        [0, 0, 0, 0],
        [0, 0, 0, 0],
      ]),
      "left",
    );

    expect(isZeroBoard(result.grid)).toBe(true);
    expect(result.clears).toHaveLength(1);
    expect(result.scoreEarned).toBe(8);
  });

  it("merges equal values with the same sign", () => {
    const result = executeMove(
      createBoard([
        [-4, -4, 0, 0],
        [0, 0, 0, 0],
        [0, 0, 0, 0],
        [0, 0, 0, 0],
      ]),
      "left",
    );

    expect(gridValues(result.grid)[0]).toEqual([-8, 0, 0, 0]);
    expect(result.mergeCount).toBe(1);
    expect(result.scoreEarned).toBe(8);
  });

  it("subtracts different values with opposite signs", () => {
    const result = executeMove(
      createBoard([
        [8, -2, 0, 0],
        [0, 0, 0, 0],
        [0, 0, 0, 0],
        [0, 0, 0, 0],
      ]),
      "left",
    );

    expect(gridValues(result.grid)[0]).toEqual([6, 0, 0, 0]);
    expect(result.reductionCount).toBe(1);
    expect(result.scoreEarned).toBe(10);
  });

  it("does not merge the same output twice in one move", () => {
    const result = executeMove(
      createBoard([
        [2, 2, 4, 0],
        [0, 0, 0, 0],
        [0, 0, 0, 0],
        [0, 0, 0, 0],
      ]),
      "left",
    );

    expect(gridValues(result.grid)[0]).toEqual([4, 4, 0, 0]);
  });

  it("detects a full board with no legal interaction", () => {
    const board = createBoard([
      [2, 4, 8, 16],
      [32, 64, 128, 256],
      [4, 8, 16, 32],
      [64, 128, 256, 512],
    ]);

    expect(isGameOver(board)).toBe(true);
  });

  it("keeps a full board playable when neighbors can interact", () => {
    const board = createBoard([
      [2, -4, 8, 16],
      [32, 64, 128, 256],
      [4, 8, 16, 32],
      [64, 128, 256, 512],
    ]);

    expect(isGameOver(board)).toBe(false);
  });

  it("creates repeatable boards from a seeded random source", () => {
    const first = gridValues(createInitialBoard(createSeededRandom(42)));
    const second = gridValues(createInitialBoard(createSeededRandom(42)));

    expect(first).toEqual(second);
  });
});
