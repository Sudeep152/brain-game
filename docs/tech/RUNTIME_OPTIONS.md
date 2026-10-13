# Runtime Technology Options (T-103)

Date: 2026-10-14 · Author: Claude (Game Engine Architect hat) · Status: recommendation, **needs owner approval** (approval gate: tech stack)

## 1. Options

| | A. Kotlin + Compose + Canvas (native) | B. A + JBox2D physics (later) | C. Unity as a Library inside the Android app | D. Full Unity / Godot game |
|---|---|---|---|---|
| Fits MVP families (teasers, matchsticks, odd-one-out, combine) | ✅ fully | ✅ | ✅ | ✅ |
| Physics puzzles | Simple gravity/tween only | ✅ real rigid-body physics | ✅ | ✅ |
| Owner's skills (experienced Android dev) | ✅ best | ✅ | ⚠ two toolchains | ⚠ new stack |
| Speed for 10–15 days | ✅ fastest | ✅ (added later) | ❌ slow integration | ❌ |
| App size | Small | +small (pure Java lib) | ❌ large Unity runtime (not benchmarked) | ❌ large |
| Sensors, haptics, Play Billing, AdMob | ✅ direct | ✅ | ⚠ bridging | ⚠ plugins |
| Codex/LLM help quality | ✅ very good for Kotlin/Compose | ✅ | ⚠ | ⚠ |
| Cost | Free | Free | Unity Personal free up to $200K revenue+funding [F1] | same |

Notes:
- PhysicsBox (Compose + JBox2D wrapper) exists, but its author says it is **not meant for games** [F2]. Boks2D (Kotlin binding to Box2D v3) is very young: v0.1.1, 31 stars, no activity in ~7 months [F3]. → If we need physics, use **JBox2D directly** inside our own game loop, or our own lightweight physics. Benchmark first (Physics Agent task, v1.1).
- Unity's runtime fee was cancelled; splash screen optional in Unity 6 [F1]. Unity is a legitimate later option *only* if physics-heavy content proves to be the hit.

## 2. Recommendation
**Option A for the MVP**, designed so B (and even C) can plug in later:
- Single-activity Compose app; each puzzle renders in a **custom game surface** (Compose `Canvas` + a frame loop via `withFrameNanos`), not ordinary UI widgets — keeps animation smooth and code clean.
- A small **puzzle runtime** in Kotlin: scene objects, gesture handling (tap / drag / long-press / multi-touch), sensor events, rules → win-condition evaluation, hint steps, state machine (`LOADING → READY → PLAYING ⇄ PAUSED → COMPLETED`).
- **Levels are JSON data** (versioned schema) bundled in the app for MVP; the same format can later be downloaded or AI-generated. Each level declares `runtime: "native-v1"` so a physics or Unity runtime can be added later without breaking old levels.
- Puzzle families that need custom logic (matchsticks, odd-one-out) are Kotlin "family modules" driven by level JSON parameters — data stays data, no code in levels.

## 3. Suggested app modules (lean)
```
app/            – shell, navigation, DI, theme
core/design/    – design system tokens & components (one source of colours/type/spacing)
core/data/      – progress (Room or DataStore), level loading
puzzle/runtime/ – scene, gestures, sensors, state machine, rules, hints
puzzle/families/– teasers, matchsticks, oddoneout, combine
content/levels/ – JSON levels + assets
```
Exact libraries (DI choice, Room vs DataStore, navigation) to be fixed in M3 with the owner.

## Sources
- [F1] Unity pricing/runtime fee page — https://unity.com/runtime-fee
- [F2] PhysicsBox article (ProAndroidDev, Mar 2026) — https://proandroiddev.com/physicsbox-adding-physics-to-compose-driven-projects-bc7926fe3b23
- [F3] Boks2D on klibs.io — https://klibs.io/project/joaomcl/Boks2d
