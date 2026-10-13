# Visual Directions (T-202)

Date: 2026-10-14 · Author: Claude (UI/UX hat) · Status: **IN REVIEW — owner must pick one (approval gate)**
Mockups (3 directions × Home / Puzzle / Solved, phone 390×844): https://claude.ai/artifact/PKkC31ojwPZiqzXtJMoyPn (private to owner)

All three follow the same rules: one shared token set, no gradients/glass/emoji, touch targets ≥ 44 px, text contrast ≥ 4.5:1, fonts that also cover Hindi (Devanagari), hints = "unlock after a short video" (fair-ads policy D-012).

| | A · Ink & Paper | B · Toybox | C · Poster Type |
|---|---|---|---|
| Idea | Puzzle sketched on paper: hand-lettered prompts, ink outlines, sticky-note hints, red "SOLVED" stamp | Puzzles as chunky toys on a night-blue table; pressable 3D buttons; candy colors | Bold graphic poster; typography itself is the toy (giant numbers/words) |
| Feel | witty, warm, human | playful, tactile, energetic | clever, confident, grown-up |
| Audience fit | broad 13+, family | broadest casual (kids → adults) | teens/adults, design-savvy; strongest on Reels |
| Type | Kalam (hand) + Hind (UI) | Baloo 2 | Anek Latin + Anek Devanagari |
| Colors | paper #F4EFE4 · ink #1F1B16 · red #C8402A · highlighter #F6D64A | table #22306E · panel #2E3F8F · sun #FFC93C · red #FF5A4E · mint #3DD6A3 | paper #F7F6F2 · black #111111 · blue #2F5BFF |
| Motion | wobble-in, ink draw-on, stamp thud | springy bounce, squash on tap, 3D press | snappy kinetic type, hard cuts |
| Object puzzles (combine, odd-one-out) | line-art objects — easy, consistent | chunky vector toys — drawable in Compose Canvas from simple shapes | needs a flat pictogram style added — weakest fit |
| Text/number teasers, matchsticks | strong | good (matchsticks = wooden toy sticks) | strongest |
| Build cost | low–medium | medium | lowest |
| Differentiation vs Brain Out / Brain Test / DOP | **weak** — closest to their doodle look | medium — looks like a "real game", not a quiz | **strong** — nobody in the genre looks like this |
| Main risk | looks like a clone | can read as "for kids" | can feel cold; humor must come from copy |

## Claude's recommendation (for discussion — not decided)
**B · Toybox**, with C as runner-up.
- 2 of our 4 MVP families are object-based (Combine & Drag, Odd One Out), and matchsticks become toy sticks; B handles all four best.
- Chunky shapes = circles, rounded rects, triangles → can be drawn in code (Compose Canvas), so "medium" art cost stays manageable in 15 days and stays consistent.
- High-contrast, bright objects read well in small Reels/Shorts thumbnails and on cheap phones in sunlight.
- Mitigate the "kiddy" risk with grown-up copy and restraint (one accent per screen).
Why not C: most distinctive and cheapest, but object puzzles would need a second illustration language, and casual players may not read it as a game. Why not A: closest to competitors' look — weak differentiation.

## After the owner picks
1. Lock tokens (colors, type scale, spacing, radii, motion) in `docs/design/DESIGN_SYSTEM.md` + `app/.../ui/design/`.
2. T-304 renderer uses only those tokens.
3. Each major screen goes through the 6 design reviews (MASTER_SPEC 5.4) with real screenshots.
