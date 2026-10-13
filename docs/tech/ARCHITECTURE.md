# Architecture v1 (T-201)

Date: 2026-10-14 · Author: Claude · Status: draft for owner review · Based on D-009..D-011

## 1. Big picture
```
┌──────────────────────────── :app (Android) ────────────────────────────┐
│ Compose screens (Home, Play, Complete, Settings) · Navigation3 · Hilt   │
│ Design system (ui/design: tokens, components)                           │
│ PuzzleCanvas  ← draws SceneState, turns touch/sensor input into events  │
│ Platform adapters: sensors, haptics, sound, ads*, analytics*            │
│ Data: Room (progress, daily) · DataStore (settings) · assets loader     │
└───────────────────────────────┬─────────────────────────────────────────┘
                                │ events in / state + effects out
┌───────────────────────── :puzzle-core (pure Kotlin) ────────────────────┐
│ Level schema models (kotlinx.serialization) · LevelValidator            │
│ PuzzleSession: state machine + rules engine (pure reducer)              │
│ Families: teaser/combine (generic rules) · matchstick (solver,          │
│ generator) · oddoneout (generator)                                      │
│ Replay/solution simulator (used by tests now, by backend later)         │
└─────────────────────────────────────────────────────────────────────────┘
* NoOp implementations until accounts are approved and set up.
```

**Key idea:** all game logic lives in `:puzzle-core`, a plain Kotlin module with **no Android dependency**.
- Every level can be auto-tested on the JVM by replaying its `solution` → reaches COMPLETED.
- The same code can later run on a server to validate AI-generated levels (MASTER_SPEC M9–M10) — no rewrite.
- `:app` only draws and forwards input.

## 2. Puzzle runtime
- **Pure reducer:** `session.handle(event) → (newState, effects)`. Deterministic, no clocks/randomness inside (seeds and time are passed in) → replayable, testable.
- **Runtime inputs** (Kotlin API): Start, Pause, Resume, Tap, LongPress, DragStart, DragMove, DragEnd, MultiTouch, Device, TimerFired, HintRequested. **Rule triggers** in level JSON are a smaller set (tap, longPress, dragEnd, dragOut, multiTouch, device, timer) — DragStart/DragMove/HintRequested never fire rules. Exact semantics: `LEVEL_SCHEMA.md` §6.
- **Effects (output):** PlaySound, Haptic, Animate, Feedback, ScheduleTimer, CancelTimer, ShowHint, ShowSolution, LevelCompleted. `:app` executes them.
- **State machine:** `NOT_LOADED → LOADING → READY → PLAYING ⇄ PAUSED → COMPLETED`; `LOADING → ERROR`; `PLAYING → FAILED` only for timed modes (daily). No other transitions allowed; illegal events are ignored and logged.
- **Coordinates:** each scene uses virtual units (default 1000 × 1600, portrait). Renderer scales to the screen with letterboxing → levels look identical on all phones. MVP is portrait-only.
- **Rendering:** Compose `Canvas` inside a `PuzzleCanvas` composable. Animation frames via `withFrameNanos`; logic is event-driven, not frame-driven. Physics runtime (JBox2D) can be added later behind `runtime: "physics-v1"`.

## 3. Content & languages
- Levels: `app/src/main/assets/content/levels/*.json` (schema: `LEVEL_SCHEMA.md`). Order: `content/order.json`.
- Level text: `content/strings/en.json`, `content/strings/hi.json` (key → text). Missing key in a language → fallback to English and logged; **validator fails the build** if a level marked playable in a locale lacks its keys.
- UI text: normal Android string resources (`values/`, `values-hi/`). Product name only in `app_name` + one `Brand` config object.
- Fonts: one family covering Latin-ext + Devanagari (chosen in visual-direction task).

## 4. Data
- **Room:** `level_progress(levelId, status, attempts, hintsUsed, firstCompletedAt)`, `daily_result(date, levelSeed, result, timeMs)`.
- **DataStore:** settings (sound, music, haptics, language override, ads/analytics consent flags).
- Offline-first; nothing requires network in MVP except loading ads.

## 5. Tech choices (latest **stable** only — no alpha/beta/rc)
| Area | Choice | Note |
|---|---|---|
| Language/UI | Kotlin 2.x, Jetpack Compose, Material 3 as base (heavily themed) | Compose 1.12.x stable (Sep 2026) |
| Navigation | Navigation3 | latest stable 1.2.0 (released stable 2026-09-23 per developer.android.com/jetpack/androidx/releases/navigation3); back stack via `rememberNavBackStack` |
| DI | Hilt + KSP | standard, Codex/owner familiar |
| JSON | kotlinx.serialization | works in pure-Kotlin core |
| DB | Room 2.8.x + KSP | progress + daily history |
| Settings | DataStore Preferences | |
| Tests | kotlin.test/JUnit on `:puzzle-core`; Compose UI tests later | |
| Build | Gradle wrapper + version catalog (`gradle/libs.versions.toml`), JDK 21 toolchain | owner has JBR 21 installed |
| SDK | minSdk 26, compile/target = newest **stable** API installed | Play target-API rules checked before upload |

## 6. Package / IDs
- applicationId placeholder: `com.example.projectpuzzle` — **Play rejects `com.example`**, so this forces a final decision (D-013) before the first upload. Kotlin namespace can differ from applicationId.

## 7. Security & safety
- Levels are **data only**: whitelisted event/condition/action types; unknown types → validation error, level skipped.
- No secrets in the repo. Ad/analytics IDs go in a git-ignored properties file when we get them.

## 8. Not now (ready for later)
Backend level API (same JSON + `minAppVersion` + per-language string packs) · AI generator (writes JSON, validated by `:puzzle-core`) · physics runtime · remote config.
