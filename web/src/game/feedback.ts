import type { FeedbackKind } from "./types";

let audioContext: AudioContext | null = null;

const FREQUENCIES: Record<FeedbackKind, number[]> = {
  move: [180],
  clear: [420, 640],
  merge: [260, 330],
  reduce: [220, 180],
  win: [392, 523, 659],
  gameover: [220, 165],
};

const VIBRATIONS: Record<FeedbackKind, number | number[]> = {
  move: 8,
  clear: [18, 30, 26],
  merge: 14,
  reduce: 12,
  win: [24, 40, 24, 40, 40],
  gameover: [40, 50, 80],
};

export function playFeedback(
  kind: FeedbackKind,
  soundEnabled: boolean,
  hapticsEnabled: boolean,
): void {
  if (typeof window === "undefined") return;

  if (hapticsEnabled && "vibrate" in navigator) {
    navigator.vibrate(VIBRATIONS[kind]);
  }

  if (!soundEnabled || !("AudioContext" in window)) return;

  try {
    audioContext ??= new AudioContext();
    const context = audioContext;
    const startAt = context.currentTime;

    FREQUENCIES[kind].forEach((frequency, index) => {
      const oscillator = context.createOscillator();
      const gain = context.createGain();
      const noteStart = startAt + index * 0.065;

      oscillator.type = "sine";
      oscillator.frequency.setValueAtTime(frequency, noteStart);
      gain.gain.setValueAtTime(0.0001, noteStart);
      gain.gain.exponentialRampToValueAtTime(0.055, noteStart + 0.01);
      gain.gain.exponentialRampToValueAtTime(0.0001, noteStart + 0.09);
      oscillator.connect(gain);
      gain.connect(context.destination);
      oscillator.start(noteStart);
      oscillator.stop(noteStart + 0.1);
    });
  } catch {
    // Audio is optional and may be blocked by the browser.
  }
}
