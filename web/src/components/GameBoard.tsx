"use client";

import { useEffect, useRef, type CSSProperties, type PointerEvent } from "react";
import { boardSum } from "@/game/engine";
import { directionFromGesture, directionFromKey } from "@/game/gesture";
import type { Direction, FeedbackKind, Grid } from "@/game/types";

interface GameBoardProps {
  grid: Grid;
  disabled: boolean;
  feedback: FeedbackKind | null;
  feedbackId: number;
  scoreGain: number;
  showGestureHint: boolean;
  onMove: (direction: Direction) => boolean;
}

const cellPosition = (index: number) =>
  index === 0
    ? "10px"
    : `calc(${index * 25}% + ${(4 - index) * 2.5}px)`;

function tileStyle(row: number, col: number): CSSProperties {
  return {
    top: cellPosition(row),
    left: cellPosition(col),
  };
}

function signed(value: number): string {
  return value > 0 ? `+${value}` : `−${Math.abs(value)}`;
}

export function GameBoard({
  grid,
  disabled,
  feedback,
  feedbackId,
  scoreGain,
  showGestureHint,
  onMove,
}: GameBoardProps) {
  const pointerStart = useRef<{ x: number; y: number } | null>(null);
  const boardRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const onKeyDown = (event: KeyboardEvent) => {
      if (disabled) return;
      const target = event.target as HTMLElement | null;
      if (target?.closest("button, input, textarea, select, [role='dialog']")) return;
      const direction = directionFromKey(event.key);
      if (!direction) return;
      event.preventDefault();
      onMove(direction);
    };

    window.addEventListener("keydown", onKeyDown, { passive: false });
    return () => window.removeEventListener("keydown", onKeyDown);
  }, [disabled, onMove]);

  const onPointerDown = (event: PointerEvent<HTMLDivElement>) => {
    if (disabled) return;
    pointerStart.current = { x: event.clientX, y: event.clientY };
    event.currentTarget.setPointerCapture?.(event.pointerId);
    boardRef.current?.classList.add("isDragging");
  };

  const finishPointer = (event: PointerEvent<HTMLDivElement>) => {
    boardRef.current?.classList.remove("isDragging");
    const start = pointerStart.current;
    pointerStart.current = null;
    if (!start || disabled) return;

    const direction = directionFromGesture(
      event.clientX - start.x,
      event.clientY - start.y,
    );
    if (direction) onMove(direction);
  };

  const cancelPointer = () => {
    boardRef.current?.classList.remove("isDragging");
    pointerStart.current = null;
  };

  const sum = boardSum(grid);

  return (
    <div className="boardFrame">
      <div className="boardMeta" aria-live="polite">
        <span>جمع صفحه</span>
        <strong dir="ltr">{sum > 0 ? `+${sum}` : sum}</strong>
      </div>
      <div
        ref={boardRef}
        className={`gameBoard${feedback ? ` feedback-${feedback}` : ""}`}
        data-feedback-id={feedbackId}
        aria-disabled={disabled}
        aria-keyshortcuts="ArrowUp ArrowDown ArrowLeft ArrowRight W A S D"
        aria-label="صفحه بازی چهار در چهار. با کشیدن یا کلیدهای جهت‌دار حرکت کنید."
        onPointerDown={onPointerDown}
        onPointerUp={finishPointer}
        onPointerCancel={cancelPointer}
        role="application"
        tabIndex={0}
      >
        <div className="boardCells" aria-hidden="true">
          {Array.from({ length: 16 }, (_, index) => (
            <span className="boardCell" key={index} />
          ))}
        </div>

        <div className="tileLayer" aria-live="polite">
          {grid.flat().map((tile) =>
            tile ? (
              <div
                className={`tile tile-${tile.value > 0 ? "positive" : "negative"}${
                  tile.isNew ? " tileNew" : ""
                }${tile.isMerged ? " tileMerged" : ""}${
                  Math.abs(tile.value) >= 128 ? " tileCompact" : ""
                }`}
                key={tile.id}
                data-sign={tile.value > 0 ? "+" : "−"}
                style={tileStyle(tile.row, tile.col)}
                aria-label={tile.value > 0 ? `مثبت ${tile.value}` : `منفی ${Math.abs(tile.value)}`}
              >
                <span dir="ltr">{signed(tile.value)}</span>
              </div>
            ) : null,
          )}
        </div>

        {scoreGain > 0 && (
          <span className="scoreBurst" key={feedbackId} aria-hidden="true">
            +{scoreGain.toLocaleString("fa-IR")}
          </span>
        )}

        {showGestureHint && (
          <div className="gestureHint" aria-hidden="true">
            <span className="gestureLine" />
            <span>روی صفحه بکشید</span>
          </div>
        )}
      </div>
    </div>
  );
}
