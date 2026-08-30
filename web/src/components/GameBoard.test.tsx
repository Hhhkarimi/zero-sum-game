import { fireEvent, render, screen } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import { createBoard } from "@/game/engine";
import { GameBoard } from "./GameBoard";

describe("GameBoard", () => {
  it("announces signed tiles and turns a drag into one move", () => {
    const onMove = vi.fn(() => true);
    render(
      <GameBoard
        disabled={false}
        feedback={null}
        feedbackId={0}
        grid={createBoard([
          [2, 0, 0, -2],
          [0, 0, 0, 0],
          [0, 0, 0, 0],
          [0, 0, 0, 0],
        ])}
        onMove={onMove}
        scoreGain={0}
        showGestureHint={false}
      />,
    );

    expect(screen.getByLabelText("مثبت 2")).toBeInTheDocument();
    expect(screen.getByLabelText("منفی 2")).toBeInTheDocument();

    const board = screen.getByRole("application");
    fireEvent.pointerDown(board, { clientX: 160, clientY: 90, pointerId: 1 });
    fireEvent.pointerUp(board, { clientX: 50, clientY: 92, pointerId: 1 });
    expect(onMove).toHaveBeenCalledWith("left");
  });
});
