# Design System v1 — "Marker Doodle" (direction D)

Date: 2026-10-14 · Approved direction: **D · Marker Doodle** (owner, decision D-019)
Mockups: https://claude.ai/artifact/PKkC31ojwPZiqzXtJMoyPn (rows D: Home + Puzzle)
**Rule for every agent:** use only these tokens (code: `app/.../ui/design/`). New color, font size, radius or motion? Propose it here first.
**Parity rule:** this doc = the mockup = the code. Any visible difference between mockup and app screenshot is a bug — fix the code, or (if the mockup used an off-token value) fix the mockup, never leave them different. Every design review compares a real device screenshot side by side with the mockup.

## 1. The idea in one line
Hand-drawn ink outlines + bold marker colors printed slightly off-register, on warm dotted paper. Witty like a doodle, bright like a toy.

## 2. Signature devices (what makes it ours)
1. **Off-register fill:** a colored shape sits 6 dp right + 6 dp down from its ink outline (outline drawn on top, transparent inside). Used on cards, puzzle objects, badges. Never on body text.
2. **Ink outline:** 2.5 dp for UI, 3 dp for puzzle objects, color `ink`, round joins/caps.
3. **Pressable edge:** buttons have a solid ink "edge" below (offset 0, 5 dp, no blur). Pressed = content moves down 5 dp and the edge disappears.
4. **Dotted paper ground:** `paper` with 1 dp `paperDot` dots on a 22 dp grid.
5. **Hand-lettered display text** (Kalam) for prompts, level numbers, big moments; rounded sans (Baloo 2) for everything you read or tap.
No gradients, no blur shadows, no glass, no emoji in UI.

## 3. Color tokens
| Token | Hex | Use |
|---|---|---|
| `paper` | #FBF6EC | screen background |
| `paperDot` | #E6DCC8 | dot grid only |
| `surface` | #FFFFFF | puzzle board, secondary buttons |
| `ink` | #1F1B16 | outlines, primary text, icons |
| `inkSoft` | #5E574B | secondary text (6.4:1 on paper) |
| `red` | #E5482E | primary action fill (white text only ≥ 18.7 px bold), progress, "nope" |
| `sun` | #FFC93C | hero card fill, hint button, highlights (ink text only) |
| `mint` | #3DD6A3 | daily puzzle, success / "yes" (ink text only) |
| `sky` | #6EC3FF | settings, info, ice/water objects (ink text only) |
Rules: max 3 fill colors per screen besides paper/surface/ink. Meaning is never color-only (always shape, icon or text too). Puzzle levels may only use these + `ink`.

## 4. Typography
Fonts (both SIL OFL, both include Devanagari for Hindi) — **bundle in `res/font`** (offline-first):
- **Kalam Bold** — display only.
- **Baloo 2** 500 / 700 / 800 — UI. Ship as **3 static files** (`baloo2_medium/bold/extrabold.ttf`) generated from the official variable font with fontTools `instancer`. Do NOT use the variable file: Android ignored its weight axis and rendered everything at 400 (found in T-307 review, 2026-10-14).
| Style | Font | Size / line height | Use |
|---|---|---|---|
| `display` | Kalam 700 | 58 / 0.95 | Level number, solved headline |
| `stat` | Kalam 700 | 38 / 1.05 | Streak number, small stats |
| `title` | Kalam 700 | 32 / 1.15 | Puzzle prompt |
| `headline` | Kalam 700 | 24 / 1.1 | Card titles, top bar title |
| `button` | Baloo 2 800 | 23 / 1.0 | Primary button |
| `buttonSmall` | Baloo 2 700 | 18 / 1.0 | Secondary buttons |
| `body` | Baloo 2 500 | 17 / 1.35 | Descriptions |
<<<<<<< HEAD
| `label` | Baloo 2 700 | 13 / 1.2, +1 tracking, UPPERCASE (Latin only) | Small labels |
| `link` | Baloo 2 700 | 16 / 1.25, underlined | Text actions (48 dp touch area) |
=======
| `meta` | Baloo 2 700 | 15 / 1.2 | Chapter line, progress numbers, small stat captions ("day streak") — never uppercased |
| `label` | Baloo 2 700 | 13 / 1.2, +1 tracking, UPPERCASE (Latin only) | Category tags only (e.g. "DAILY PUZZLE") |
>>>>>>> task/T-307-design-system
| `caption` | Baloo 2 500 | 13 / 1.3 | Hint/ad note |
Hindi: no uppercase transform; line height × 1.15 for Devanagari; check that long Hindi strings wrap, never shrink below 13 sp. Respect system font scale up to 1.3×.

## 5. Spacing, size, shape
- Grid: 4 dp. Scale: 4 · 8 · 12 · 16 · 20 · 24 · 32.
- Screen padding: 22 dp horizontal, 20 dp top, 28 dp bottom. Section gap: 20 dp.
- Touch targets: **≥ 48 dp** (icon buttons 48×48). In puzzle scenes (1000-unit wide virtual canvas ≈ 390 dp) tappables must be **≥ 124 units** — add to validator later.
<<<<<<< HEAD
- Radii: chip 11 · icon button 16 · button 18 · card 26 · puzzle board 26.
- Primary button height 60 dp; secondary 58 dp. Icon glyph 22 dp. Count badge 22 dp. Progress bar 20 dp. Streak card 116 dp wide.
=======
- Radii: chip 11 · icon button 16 · button 18 · small card 20 · card 26 · puzzle board 26.
- Card padding: hero card 24 top / 20 sides / 24 bottom, gap 12 · small cards 16, gap 4. Off-register fill shift = 6 dp on every card (fill same size as outline).
- Icon buttons 48×48; when sitting next to a secondary button they are 58×58 to match its height. Every pressable has the 5 dp ink edge.
- Count badge: 22 dp circle, `ink` fill, 2 dp white border, white 13 sp bold number (white-on-red failed contrast).
- Primary button height 60 dp; secondary 58 dp.
>>>>>>> task/T-307-design-system

## 6. Components (build once, reuse)
`InkCard` (outline + off-register fill, color param) · `PressableButton` (primary red / secondary white / icon) · `HintButton` (sun + count badge) · `TopBar` (back · title · hint) · `ProgressBar` (ink outline, red fill) · `DottedPaper` background · `Badge`.

## 7. Motion (feel: bouncy, quick, hand-made)
| Moment | Motion | Haptic |
|---|---|---|
| Tap button | press down 5 dp, 80 ms; release spring (damping 0.6) | light |
| Screen/object appear | "wobble-in": rotate −3°→0°, scale 0.92→1, 220 ms | — |
| Drag lift | scale 1.08, rotate −6°, ink ellipse shadow under it | light |
| Wrong answer ("nope") | shake ±6 dp ×3, 240 ms, red flash on outline | light |
| Correct / solved | pop 1→1.15→1 + 6–8 doodle confetti (circles, triangles, squiggles in sun/mint/red/sky), 600 ms | success |
| Hint reveal | sticky note slides up, 200 ms | — |
Respect Android "remove animations" / animator scale = 0 → instant changes, keep haptics optional (setting).

## 8. Icons
Stroke icons, 24 grid, 2.4 dp stroke, round caps/joins, `ink`. No filled emoji-style icons.

## 9. Puzzle art rules
- Objects = simple shapes (circles, rounded rects, triangles, blobs) with 3 dp ink outline + off-register fill. Drawable in code (Compose Canvas) — prefer code over image files.
- One idea per object; max 6 interactive objects per scene in MVP.
- Draggable objects show lift; tappable objects react on press (squash 0.95).

## 10. Screens still to design in this style (before building them)
Solved screen · Hint sheet · Settings · Level list · Daily result + share card. (Home + Puzzle are in the mockups.)

## 11. Not in v1
Dark mode (tokens are named so it can be added) · mascot/character (possible later, needs owner approval) · sticker-collection rewards (idea from direction E, later).
