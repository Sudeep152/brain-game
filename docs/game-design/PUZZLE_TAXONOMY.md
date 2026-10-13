# Puzzle Taxonomy & MVP Shortlist (T-102)

Date: 2026-10-14 · Author: Claude (Game Design hat) · Status: draft for owner review

## 1. How families are scored
- **Fun / wow** — does it create an "aha!" moment?
- **Build cost** — engineering for the mechanic (once).
- **Content cost** — effort per extra level (art + design).
- **Generatable** — can levels later be produced procedurally / with AI + validation?
- **Language-light** — works without much text (cheap to localize, works globally).

## 2. Families

| # | Family | Example | Fun | Build | Content/level | Generatable | Lang-light | MVP? |
|---|---|---|---|---|---|---|---|---|
| 1 | **Trick Teasers** | "Find the biggest number" → drag the word "number" itself; tap the sun, it's the answer | ★★★ | Low–Med | Med (handcrafted ideas) | Low (AI ideation + human review) | Med | **YES — core** |
| 2 | **Device Tricks** (mechanic inside #1) | Turn phone upside down, shake, tilt, cover the screen, plug charger, change volume | ★★★ | Low per sensor | Low | Low | High | **YES — inside #1** |
| 3 | **Matchstick Math** | Move 1 stick to fix `6 + 4 = 4` | ★★ | Med (snap-to-slot dragging) | Very low | **High** (solver-verified) | High | **YES** |
| 4 | **Odd One Out / Spot It** | Find the one shape/colour/rotation that differs; difficulty via subtlety & grid size | ★★ | Low | Very low | **High** | High | **YES — powers Daily Challenge** |
| 5 | **Combine & Drag** | Drag fire onto ice to get water; move cloud to reveal key | ★★★ | Med (overlap rules) | Med (needs objects) | Med (recipe graphs) | High | **YES (small set)** |
| 6 | Draw the Missing Part (DOP-style) | Draw the missing leg of a chair | ★★★ | Med–High (stroke validation) | **High** (custom art each level) | Med | High | v1.1 |
| 7 | Physics (draw line / cut rope / drop ball) | Draw a ramp so ball reaches cup | ★★★ | High (physics engine) | Med | Med (simulation-verified) | High | v1.1+ |
| 8 | Memory & Sequence | Repeat a light pattern | ★ | Low | Very low | High | High | Later (filler) |
| 9 | Optical Illusions | Which line is longer? | ★★ | Low | Med | Low | High | Mix into #1 |
| 10 | Hidden Object | Find 5 keys in a room | ★★ | Low | **High** (rich scenes) | Low | High | Later |
| 11 | Escape Room (multi-step) | Chain 4 puzzles to open a door | ★★★ | Med (state chains) | **High** | Low | Med | Later (chapter finales) |
| 12 | Logic grids / riddles (text) | Classic word riddles | ★ | Low | Low | Med | **Low** (text-heavy) | No — feels like a quiz app |

## 3. MVP shortlist (recommendation)
1. **Trick Teasers + Device Tricks** — the personality of the game; the "share this with a friend" moments.
2. **Matchstick Math** — proves the procedural pipeline idea; near-zero art.
3. **Odd One Out** — procedural, scales in difficulty, perfect for a **Daily Challenge** with a shareable result card.
4. **Combine & Drag** — small handcrafted set to show object interaction variety.

Why not Draw/Physics in MVP: highest art + engineering cost; better added in v1.1 once the runtime and art style are stable.

## 4. Design rules for every level (fairness — top competitor complaint)
- One consistent **interaction language**: draggable things look draggable (subtle lift/shadow on touch), tappable things react on tap. Never require an action the screen gives zero affordance for — except clearly-labelled "trick" levels, whose hint teaches it.
- **Accept all reasonable answers.** Each level lists alternate valid solutions; playtesting adds more.
- **3-step hints**: nudge → strong hint → show solution.
- No fail-punishment loops: wrong taps give a playful reaction, not a lost life.
- Never two levels of the same family back-to-back (keeps variety high).
