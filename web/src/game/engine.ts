import {
  BOARD_SIZE,
  type ClearEvent,
  type Direction,
  type Grid,
  type MoveResult,
  type Tile,
} from "./types";

export type RandomSource = () => number;

let nextTileId = 1000;

function tileId(): number {
  nextTileId += 1;
  return nextTileId;
}

export function emptyGrid(): Grid {
  return Array.from({ length: BOARD_SIZE }, () =>
    Array<Tile | null>(BOARD_SIZE).fill(null),
  );
}

export function cloneGrid(grid: Grid): Grid {
  return grid.map((row) => row.map((tile) => (tile ? { ...tile } : null)));
}

export function createBoard(values: number[][]): Grid {
  const grid = emptyGrid();

  for (let row = 0; row < BOARD_SIZE; row += 1) {
    for (let col = 0; col < BOARD_SIZE; col += 1) {
      const value = values[row]?.[col] ?? 0;
      if (value !== 0) {
        grid[row][col] = {
          id: tileId(),
          value,
          row,
          col,
          isNew: true,
        };
      }
    }
  }

  return grid;
}

export function createSeededRandom(seed: number): RandomSource {
  let state = seed >>> 0;
  return () => {
    state += 0x6d2b79f5;
    let value = state;
    value = Math.imul(value ^ (value >>> 15), value | 1);
    value ^= value + Math.imul(value ^ (value >>> 7), value | 61);
    return ((value ^ (value >>> 14)) >>> 0) / 4294967296;
  };
}

export function spawnRandomTile(
  sourceGrid: Grid,
  random: RandomSource = Math.random,
): Grid {
  const grid = cloneGrid(sourceGrid);
  const emptyCells: Array<[number, number]> = [];

  for (let row = 0; row < BOARD_SIZE; row += 1) {
    for (let col = 0; col < BOARD_SIZE; col += 1) {
      if (!grid[row][col]) emptyCells.push([row, col]);
    }
  }

  if (emptyCells.length === 0) return grid;

  const index = Math.min(
    emptyCells.length - 1,
    Math.floor(random() * emptyCells.length),
  );
  const [row, col] = emptyCells[index];
  const magnitude = random() < 0.7 ? 2 : 4;
  const sign = random() < 0.5 ? 1 : -1;

  grid[row][col] = {
    id: tileId(),
    value: sign * magnitude,
    row,
    col,
    isNew: true,
  };

  return grid;
}

export function createInitialBoard(random: RandomSource = Math.random): Grid {
  return spawnRandomTile(spawnRandomTile(emptyGrid(), random), random);
}

function lineCoordinates(
  direction: Direction,
  line: number,
): Array<[number, number]> {
  return Array.from({ length: BOARD_SIZE }, (_, offset) => {
    switch (direction) {
      case "left":
        return [line, offset];
      case "right":
        return [line, BOARD_SIZE - 1 - offset];
      case "up":
        return [offset, line];
      case "down":
        return [BOARD_SIZE - 1 - offset, line];
    }
  });
}

function valuesSignature(grid: Grid): string {
  return grid
    .flat()
    .map((tile) => tile?.value ?? 0)
    .join(",");
}

export function executeMove(grid: Grid, direction: Direction): MoveResult {
  const nextGrid = emptyGrid();
  const clears: ClearEvent[] = [];
  let scoreEarned = 0;
  let mergeCount = 0;
  let reductionCount = 0;

  for (let line = 0; line < BOARD_SIZE; line += 1) {
    const coordinates = lineCoordinates(direction, line);
    const tiles = coordinates
      .map(([row, col]) => grid[row][col])
      .filter((tile): tile is Tile => tile !== null);
    const resolved: Tile[] = [];

    for (let index = 0; index < tiles.length; ) {
      const current = tiles[index];
      const next = tiles[index + 1];

      if (next && current.value === -next.value) {
        const [row, col] = coordinates[resolved.length];
        const magnitude = Math.abs(current.value);
        scoreEarned += magnitude * 4;
        clears.push({ id: tileId(), row, col, magnitude });
        index += 2;
        continue;
      }

      if (next && current.value === next.value) {
        const value = current.value * 2;
        scoreEarned += Math.abs(value);
        mergeCount += 1;
        resolved.push({
          id: tileId(),
          value,
          row: 0,
          col: 0,
          isMerged: true,
        });
        index += 2;
        continue;
      }

      if (next && Math.sign(current.value) !== Math.sign(next.value)) {
        const value = current.value + next.value;
        scoreEarned += Math.abs(current.value) + Math.abs(next.value);
        reductionCount += 1;
        resolved.push({
          id: tileId(),
          value,
          row: 0,
          col: 0,
          isMerged: true,
        });
        index += 2;
        continue;
      }

      resolved.push({ ...current, isNew: false, isMerged: false });
      index += 1;
    }

    resolved.forEach((tile, offset) => {
      const [row, col] = coordinates[offset];
      nextGrid[row][col] = { ...tile, row, col };
    });
  }

  return {
    grid: nextGrid,
    moved: valuesSignature(grid) !== valuesSignature(nextGrid),
    scoreEarned,
    clears,
    mergeCount,
    reductionCount,
  };
}

export function isZeroBoard(grid: Grid): boolean {
  return grid.every((row) => row.every((tile) => tile === null));
}

function canInteract(first: number, second: number): boolean {
  return first === second || Math.sign(first) !== Math.sign(second);
}

export function isGameOver(grid: Grid): boolean {
  if (grid.some((row) => row.some((tile) => tile === null))) return false;

  for (let row = 0; row < BOARD_SIZE; row += 1) {
    for (let col = 0; col < BOARD_SIZE; col += 1) {
      const current = grid[row][col];
      if (!current) continue;

      const right = grid[row]?.[col + 1];
      const below = grid[row + 1]?.[col];
      if (right && canInteract(current.value, right.value)) return false;
      if (below && canInteract(current.value, below.value)) return false;
    }
  }

  return true;
}

export function boardSum(grid: Grid): number {
  return grid.flat().reduce((sum, tile) => sum + (tile?.value ?? 0), 0);
}

export function highestMagnitude(grid: Grid): number {
  return grid
    .flat()
    .reduce((highest, tile) => Math.max(highest, Math.abs(tile?.value ?? 0)), 0);
}

export function gridValues(grid: Grid): number[][] {
  return grid.map((row) => row.map((tile) => tile?.value ?? 0));
}
