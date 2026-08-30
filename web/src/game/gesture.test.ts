import { describe, expect, it } from "vitest";
import { directionFromGesture, directionFromKey } from "./gesture";

describe("gesture controls", () => {
  it.each([
    [60, 4, "right"],
    [-60, 4, "left"],
    [3, -70, "up"],
    [3, 70, "down"],
  ] as const)("maps %i,%i to %s", (x, y, expected) => {
    expect(directionFromGesture(x, y)).toBe(expected);
  });

  it("ignores accidental taps and tiny drags", () => {
    expect(directionFromGesture(12, 9)).toBeNull();
  });

  it("supports arrows and WASD", () => {
    expect(directionFromKey("ArrowLeft")).toBe("left");
    expect(directionFromKey("W")).toBe("up");
    expect(directionFromKey("Enter")).toBeNull();
  });
});
