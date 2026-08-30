import type { Preferences } from "./types";

const STORAGE_KEY = "zero-sum-game:v1";

export const DEFAULT_PREFERENCES: Preferences = {
  bestScore: 0,
  completedPuzzles: [],
  soundEnabled: true,
  hapticsEnabled: true,
  hasSeenGesture: false,
};

export function loadPreferences(): Preferences {
  if (typeof window === "undefined") return DEFAULT_PREFERENCES;

  try {
    const raw = window.localStorage.getItem(STORAGE_KEY);
    if (!raw) return DEFAULT_PREFERENCES;
    const parsed = JSON.parse(raw) as Partial<Preferences>;

    return {
      bestScore:
        typeof parsed.bestScore === "number" && parsed.bestScore >= 0
          ? parsed.bestScore
          : 0,
      completedPuzzles: Array.isArray(parsed.completedPuzzles)
        ? parsed.completedPuzzles.filter(
            (id): id is number => Number.isInteger(id) && id > 0,
          )
        : [],
      soundEnabled: parsed.soundEnabled !== false,
      hapticsEnabled: parsed.hapticsEnabled !== false,
      hasSeenGesture: parsed.hasSeenGesture === true,
    };
  } catch {
    return DEFAULT_PREFERENCES;
  }
}

export function savePreferences(preferences: Preferences): void {
  if (typeof window === "undefined") return;

  try {
    window.localStorage.setItem(STORAGE_KEY, JSON.stringify(preferences));
  } catch {
    // Storage can be unavailable in private browsing. The game still works.
  }
}
