import type { Direction } from "./types";

export function directionFromGesture(
  deltaX: number,
  deltaY: number,
  threshold = 24,
): Direction | null {
  if (Math.max(Math.abs(deltaX), Math.abs(deltaY)) < threshold) return null;

  if (Math.abs(deltaX) > Math.abs(deltaY)) {
    return deltaX > 0 ? "right" : "left";
  }

  return deltaY > 0 ? "down" : "up";
}

export function directionFromKey(key: string): Direction | null {
  const normalized = key.toLowerCase();
  const keyMap: Record<string, Direction> = {
    arrowup: "up",
    w: "up",
    arrowdown: "down",
    s: "down",
    arrowleft: "left",
    a: "left",
    arrowright: "right",
    d: "right",
  };

  return keyMap[normalized] ?? null;
}
