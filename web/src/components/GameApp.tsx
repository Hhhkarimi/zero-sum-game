"use client";

import { useCallback, useEffect, useMemo, useState } from "react";
import { boardSum } from "@/game/engine";
import { getPuzzle, PUZZLES } from "@/game/puzzles";
import type { GameMode, Grid } from "@/game/types";
import { useGame } from "@/game/use-game";
import { GameBoard } from "./GameBoard";
import { Icon } from "./Icon";
import { Modal } from "./Modal";

type ModalName = "help" | "settings" | "puzzles" | null;

const MODES: Array<{ id: GameMode; label: string }> = [
  { id: "classic", label: "بی‌نهایت" },
  { id: "daily", label: "روزانه" },
  { id: "puzzle", label: "معما" },
];

function signed(value: number): string {
  if (value > 0) return `+${value}`;
  if (value < 0) return `−${Math.abs(value)}`;
  return "۰";
}

function shareText(grid: Grid, moves: number, score: number, date: string) {
  const board = grid
    .map((row) =>
      row
        .map((tile) => (tile ? (tile.value > 0 ? "🟦" : "🟥") : "⬜"))
        .join(""),
    )
    .join("\n");

  return [
    "نقطه صفر | چالش روزانه",
    date,
    `${moves.toLocaleString("fa-IR")} حرکت · ${score.toLocaleString("fa-IR")} امتیاز`,
    "",
    board,
    "",
    "#نقطه_صفر #ZeroSum",
  ].join("\n");
}

function Toggle({
  checked,
  label,
  description,
  onChange,
}: {
  checked: boolean;
  label: string;
  description: string;
  onChange: (checked: boolean) => void;
}) {
  return (
    <div className="settingRow">
      <span>
        <strong>{label}</strong>
        <small>{description}</small>
      </span>
      <button
        aria-checked={checked}
        className={`switch${checked ? " switchOn" : ""}`}
        role="switch"
        type="button"
        onClick={() => onChange(!checked)}
      >
        <span />
        <span className="srOnly">{checked ? "روشن" : "خاموش"}</span>
      </button>
    </div>
  );
}

export function GameApp() {
  const {
    state,
    canUndo,
    move,
    undo,
    restart,
    switchMode,
    selectPuzzle,
    nextPuzzle,
    setSoundEnabled,
    setHapticsEnabled,
  } = useGame();
  const [modal, setModal] = useState<ModalName>(null);
  const [dismissedResultId, setDismissedResultId] = useState(-1);
  const [toast, setToast] = useState<string | null>(null);
  const currentPuzzle = useMemo(() => getPuzzle(state.puzzleId), [state.puzzleId]);

  useEffect(() => {
    if (!toast) return;
    const timer = window.setTimeout(() => setToast(null), 2400);
    return () => window.clearTimeout(timer);
  }, [toast]);

  const closeModal = useCallback(() => setModal(null), []);

  const changeMode = (mode: GameMode) => {
    if (mode === state.mode) {
      if (mode === "puzzle") setModal("puzzles");
      return;
    }
    setModal(null);
    switchMode(mode, state.puzzleId);
  };

  const shareDaily = async () => {
    const text = shareText(state.grid, state.moves, state.score, state.dailyLabel);

    try {
      if (navigator.share) {
        await navigator.share({ title: "نقطه صفر", text });
        return;
      }
      await navigator.clipboard.writeText(text);
      setToast("نتیجه کپی شد");
    } catch (error) {
      if (error instanceof DOMException && error.name === "AbortError") return;
      setToast("اشتراک‌گذاری در این مرورگر در دسترس نیست");
    }
  };

  const balance = boardSum(state.grid);
  const resultOpen =
    (state.isWon || state.isGameOver) && dismissedResultId !== state.feedbackId;

  return (
    <main className="appShell">
      <header className="topBar">
        <a className="brand" href="#game" aria-label="نقطه صفر، ابتدای بازی">
          <span className="brandMark" aria-hidden="true">
            <i>+</i>
            <i>−</i>
          </span>
          <span>
            <strong>نقطه صفر</strong>
            <small>ZERO SUM</small>
          </span>
        </a>
        <div className="topActions">
          <button className="topTextButton" type="button" onClick={() => setModal("help")}>
            روش بازی
          </button>
          <button className="iconButton" type="button" onClick={() => setModal("settings")}>
            <Icon name="settings" width="21" height="21" />
            <span className="srOnly">تنظیمات</span>
          </button>
        </div>
      </header>

      <section className="gameLayout" id="game">
        <div className="playColumn">
          <div className="playHeader">
            <div>
              <p className="eyebrow">بازی جمع و علامت</p>
              <h1>صفحه را به صفر برسان.</h1>
            </div>
            <div className="balancePill" data-balanced={balance === 0}>
              <span>تراز</span>
              <strong dir="ltr">{signed(balance)}</strong>
            </div>
          </div>

          <div className="modeTabs" role="tablist" aria-label="حالت بازی">
            {MODES.map((mode) => (
              <button
                aria-selected={state.mode === mode.id}
                className={state.mode === mode.id ? "active" : ""}
                key={mode.id}
                role="tab"
                type="button"
                onClick={() => changeMode(mode.id)}
              >
                {mode.label}
              </button>
            ))}
          </div>

          <div className="scoreStrip">
            <div>
              <span>امتیاز</span>
              <strong>{state.score.toLocaleString("fa-IR")}</strong>
            </div>
            <div>
              <span>رکورد</span>
              <strong>{state.bestScore.toLocaleString("fa-IR")}</strong>
            </div>
            <div>
              <span>حرکت</span>
              <strong>{state.moves.toLocaleString("fa-IR")}</strong>
            </div>
            <div>
              <span>صفرها</span>
              <strong>{state.clears.toLocaleString("fa-IR")}</strong>
            </div>
          </div>

          <GameBoard
            disabled={state.isGameOver || state.isWon}
            feedback={state.lastFeedback}
            feedbackId={state.feedbackId}
            grid={state.grid}
            onMove={move}
            scoreGain={state.lastScoreGain}
            showGestureHint={!state.hasSeenGesture && state.moves === 0}
          />

          <div className="controlRow" aria-label="ابزارهای بازی">
            <button type="button" disabled={!canUndo} onClick={undo}>
              <Icon name="undo" width="19" height="19" />
              <span>برگشت</span>
            </button>
            <button type="button" onClick={restart}>
              <Icon name="restart" width="19" height="19" />
              <span>شروع دوباره</span>
            </button>
            {state.mode === "daily" && (
              <button type="button" onClick={shareDaily}>
                <Icon name="share" width="19" height="19" />
                <span>اشتراک</span>
              </button>
            )}
            <button type="button" onClick={() => setModal("help")}>
              <Icon name="help" width="19" height="19" />
              <span>راهنما</span>
            </button>
          </div>
          <p className="inputHint">
            روی صفحه به چهار جهت بکشید. روی لپ‌تاپ از کلیدهای جهت‌دار یا WASD استفاده کنید.
          </p>
        </div>

        <aside className="sideColumn">
          <section className="modeCard">
            <div className="modeCardTop">
              <span className={`modeBadge modeBadge-${state.mode}`}>
                {MODES.find((mode) => mode.id === state.mode)?.label}
              </span>
              {state.combo > 1 && <span className="comboBadge">×{state.combo}</span>}
            </div>

            {state.mode === "classic" && (
              <>
                <h2>هر حرکت، یک کاشی تازه</h2>
                <p>
                  علامت‌های مخالف را کم کنید و مقدارهای برابر را بسازید. اگر صفحه کاملاً خالی شود، بازی تمام است.
                </p>
              </>
            )}

            {state.mode === "daily" && (
              <>
                <p className="dateLabel">{state.dailyLabel}</p>
                <h2>چیدمان امروز</h2>
                <p>کاشی تازه اضافه نمی‌شود. مسیر کوتاه‌تر، نتیجه بهتر.</p>
              </>
            )}

            {state.mode === "puzzle" && (
              <>
                <p className="dateLabel">مرحله {currentPuzzle.id.toLocaleString("fa-IR")}</p>
                <h2>{currentPuzzle.title}</h2>
                <p>{currentPuzzle.description}</p>
                <div className="puzzleTarget">
                  <span>هدف پیشنهادی</span>
                  <strong>{currentPuzzle.targetMoves.toLocaleString("fa-IR")} حرکت</strong>
                </div>
                <button className="secondaryButton" type="button" onClick={() => setModal("puzzles")}>
                  انتخاب مرحله
                </button>
              </>
            )}
          </section>

          <section className="rulesCard" aria-labelledby="rules-title">
            <div className="sectionHeading">
              <p className="eyebrow">سه قانون</p>
              <h2 id="rules-title">ساده، ولی حساب‌شده</h2>
            </div>
            <div className="equationList" dir="ltr">
              <div>
                <span className="miniTile positive">+2</span>
                <span className="miniTile negative">−2</span>
                <b>→</b>
                <em>0</em>
                <small>صفر</small>
              </div>
              <div>
                <span className="miniTile positive">+2</span>
                <span className="miniTile positive">+2</span>
                <b>→</b>
                <span className="miniTile positive">+4</span>
                <small>ادغام</small>
              </div>
              <div>
                <span className="miniTile positive">+8</span>
                <span className="miniTile negative">−2</span>
                <b>→</b>
                <span className="miniTile positive">+6</span>
                <small>کاهش</small>
              </div>
            </div>
          </section>

          <section className="progressCard">
            <div>
              <span>بیشترین کاشی</span>
              <strong dir="ltr">{state.highestMagnitude.toLocaleString("en-US")}</strong>
            </div>
            <div>
              <span>معماهای حل‌شده</span>
              <strong>
                {state.completedPuzzles.length.toLocaleString("fa-IR")}
                <small> / {PUZZLES.length.toLocaleString("fa-IR")}</small>
              </strong>
            </div>
          </section>
        </aside>
      </section>

      <footer className="footer">
        <span>نقطه صفر</span>
        <span>بازی با لمس، ماوس و صفحه‌کلید</span>
      </footer>

      {modal === "help" && (
        <Modal title="روش بازی" eyebrow="کمتر از یک دقیقه" onClose={closeModal}>
          <div className="helpBody">
            <p>
              صفحه را به چپ، راست، بالا یا پایین بکشید. همه کاشی‌ها هم‌زمان در همان جهت حرکت می‌کنند.
            </p>
            <div className="helpRule">
              <b>علامت مخالف، مقدار برابر</b>
              <span dir="ltr">+4 · −4 → 0</span>
              <p>هر دو کاشی حذف می‌شوند.</p>
            </div>
            <div className="helpRule">
              <b>علامت و مقدار برابر</b>
              <span dir="ltr">−4 · −4 → −8</span>
              <p>دو کاشی به یک مقدار بزرگ‌تر تبدیل می‌شوند.</p>
            </div>
            <div className="helpRule">
              <b>علامت مخالف، مقدار متفاوت</b>
              <span dir="ltr">+8 · −2 → +6</span>
              <p>مقدار کوچک‌تر از مقدار بزرگ‌تر کم می‌شود.</p>
            </div>
            <button className="primaryButton" type="button" onClick={closeModal}>
              شروع بازی
            </button>
          </div>
        </Modal>
      )}

      {modal === "settings" && (
        <Modal title="تنظیمات" onClose={closeModal}>
          <div className="settingsBody">
            <Toggle
              checked={state.soundEnabled}
              description="صدای کوتاه برای حرکت و برخورد"
              label="صدا"
              onChange={setSoundEnabled}
            />
            <Toggle
              checked={state.hapticsEnabled}
              description="لرزش کوتاه در دستگاه‌های پشتیبانی‌شده"
              label="بازخورد لمسی"
              onChange={setHapticsEnabled}
            />
            <p className="settingsNote">رکورد و مراحل حل‌شده فقط روی همین مرورگر ذخیره می‌شوند.</p>
          </div>
        </Modal>
      )}

      {modal === "puzzles" && (
        <Modal title="انتخاب معما" eyebrow="۱۰ چیدمان" wide onClose={closeModal}>
          <div className="puzzleGrid">
            {PUZZLES.map((puzzle) => {
              const completed = state.completedPuzzles.includes(puzzle.id);
              const selected = state.mode === "puzzle" && state.puzzleId === puzzle.id;
              return (
                <button
                  className={`puzzleItem${selected ? " selected" : ""}`}
                  key={puzzle.id}
                  type="button"
                  onClick={() => {
                    selectPuzzle(puzzle.id);
                    closeModal();
                  }}
                >
                  <span className="puzzleNumber">{puzzle.id.toLocaleString("fa-IR")}</span>
                  <span>
                    <strong>{puzzle.title}</strong>
                    <small>{puzzle.targetMoves.toLocaleString("fa-IR")} حرکت</small>
                  </span>
                  {completed && <Icon name="check" width="20" height="20" />}
                </button>
              );
            })}
          </div>
        </Modal>
      )}

      {resultOpen && (
        <Modal
          title={state.isWon ? "صفحه صفر شد" : "حرکت دیگری نمانده"}
          eyebrow={state.isWon ? "تمام" : "پایان بازی"}
          onClose={() => setDismissedResultId(state.feedbackId)}
        >
          <div className="resultBody">
            <div className={`resultMark${state.isWon ? " won" : ""}`} aria-hidden="true">
              {state.isWon ? "۰" : signed(balance)}
            </div>
            <p>
              {state.isWon
                ? `${state.moves.toLocaleString("fa-IR")} حرکت و ${state.score.toLocaleString("fa-IR")} امتیاز.`
                : `امتیاز این دور ${state.score.toLocaleString("fa-IR")} شد.`}
            </p>
            <div className="resultActions">
              {state.mode === "puzzle" && state.isWon && (
                <button
                  className="primaryButton"
                  type="button"
                  onClick={() => {
                    nextPuzzle();
                    closeModal();
                  }}
                >
                  معمای بعدی
                </button>
              )}
              {state.mode === "daily" && (
                <button className="primaryButton" type="button" onClick={shareDaily}>
                  اشتراک نتیجه
                </button>
              )}
              <button
                className={state.mode === "classic" ? "primaryButton" : "secondaryButton"}
                type="button"
                onClick={() => {
                  restart();
                  closeModal();
                }}
              >
                دوباره
              </button>
            </div>
          </div>
        </Modal>
      )}

      {toast && (
        <div className="toast" role="status">
          {toast}
        </div>
      )}
    </main>
  );
}
