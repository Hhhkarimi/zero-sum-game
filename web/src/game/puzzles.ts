import { createBoard } from "./engine";
import type { Grid, PuzzleLevel } from "./types";

export const PUZZLES: PuzzleLevel[] = [
  {
    id: 1,
    title: "اولین صفر",
    description: "دو کاشی هم‌اندازه با علامت مخالف را به هم برسانید.",
    initialBoard: [
      [2, 0, 0, -2],
      [0, 0, 0, 0],
      [0, 0, 0, 0],
      [0, 0, 0, 0],
    ],
    targetMoves: 1,
  },
  {
    id: 2,
    title: "دو برخورد",
    description: "دو جفت مخالف را با کمترین حرکت صفر کنید.",
    initialBoard: [
      [4, 0, 0, -4],
      [0, 0, 0, 0],
      [0, 0, 0, 0],
      [-2, 0, 0, 2],
    ],
    targetMoves: 1,
  },
  {
    id: 3,
    title: "بساز و صفر کن",
    description: "دو +۲ را به +۴ تبدیل کنید، سپس آن را با −۴ صفر کنید.",
    initialBoard: [
      [2, 0, 2, 0],
      [0, 0, 0, 0],
      [0, 0, 0, -4],
      [0, 0, 0, 0],
    ],
    targetMoves: 2,
  },
  {
    id: 4,
    title: "کاهش حساب‌شده",
    description: "+۸ را با −۴ و دو −۲ به صفر برسانید.",
    initialBoard: [
      [8, 0, -4, 0],
      [0, -2, 0, -2],
      [0, 0, 0, 0],
      [0, 0, 0, 0],
    ],
    targetMoves: 4,
  },
  {
    id: 5,
    title: "سه گوشه",
    description: "+۴ و دو −۲ را در یک مسیر جمع کنید.",
    initialBoard: [
      [4, 0, 0, -2],
      [0, 0, 0, 0],
      [0, 0, 0, 0],
      [-2, 0, 0, 0],
    ],
    targetMoves: 3,
  },
  {
    id: 6,
    title: "زنجیره منفی",
    description: "دو −۴ را یکی کنید و ترکیب نهایی را به صفر برسانید.",
    initialBoard: [
      [-8, 0, 0, 16],
      [0, -4, 0, 0],
      [0, 0, -4, 0],
      [0, 0, 0, 0],
    ],
    targetMoves: 5,
  },
  {
    id: 7,
    title: "تراز شانزده",
    description: "+۱۶ را با چهار کاشی منفی تراز کنید.",
    initialBoard: [
      [16, 0, -8, 0],
      [0, -4, 0, -2],
      [0, 0, -2, 0],
      [0, 0, 0, 0],
    ],
    targetMoves: 5,
  },
  {
    id: 8,
    title: "تله ستونی",
    description: "ستون‌ها را طوری حرکت دهید که هیچ کاشی جدا نماند.",
    initialBoard: [
      [2, -4, 8, -16],
      [-2, 0, 0, 0],
      [0, 4, 0, 0],
      [0, 0, -8, 16],
    ],
    targetMoves: 6,
  },
  {
    id: 9,
    title: "زنجیره سی‌ودو",
    description: "+۳۲ را با زنجیره کاشی‌های منفی به صفر برسانید.",
    initialBoard: [
      [32, 0, 0, -16],
      [0, -8, 0, -4],
      [0, 0, -2, -2],
      [0, 0, 0, 0],
    ],
    targetMoves: 6,
  },
  {
    id: 10,
    title: "تعادل نهایی",
    description: "دو ردیف مخالف را با ترتیب درست کاملاً صفر کنید.",
    initialBoard: [
      [16, -8, 4, -2],
      [-16, 8, -4, 2],
      [0, 0, 0, 0],
      [0, 0, 0, 0],
    ],
    targetMoves: 4,
  },
];

const DAILY_BOARDS = PUZZLES.map((puzzle) => puzzle.initialBoard);

function rotate(values: number[][]): number[][] {
  return values[0].map((_, col) =>
    values.map((row) => row[col]).reverse(),
  );
}

function mirror(values: number[][]): number[][] {
  return values.map((row) => [...row].reverse());
}

export function localDateSeed(date = new Date()): number {
  return (
    date.getFullYear() * 10000 +
    (date.getMonth() + 1) * 100 +
    date.getDate()
  );
}

export function dailyLabel(date = new Date()): string {
  return new Intl.DateTimeFormat("fa-IR", {
    year: "numeric",
    month: "long",
    day: "numeric",
  }).format(date);
}

export function getDailyBoard(date = new Date()): Grid {
  const seed = localDateSeed(date);
  let values = DAILY_BOARDS[seed % DAILY_BOARDS.length].map((row) => [...row]);
  const rotations = Math.floor(seed / 10) % 4;

  for (let index = 0; index < rotations; index += 1) values = rotate(values);
  if (seed % 2 === 0) values = mirror(values);

  return createBoard(values);
}

export function getPuzzle(id: number): PuzzleLevel {
  return PUZZLES.find((puzzle) => puzzle.id === id) ?? PUZZLES[0];
}

export function getPuzzleBoard(id: number): Grid {
  return createBoard(getPuzzle(id).initialBoard);
}
