# Level Schema v1 (T-201, revised for T-302)

Date: 2026-10-14 · Revision: r3 (r2 resolved Codex T-302 blockers, D-015; r3 adds interactivity rule, D-018)
Owner of truth: this file. `:puzzle-core` models must match it exactly. **Levels are data only** — no scripts, no expressions.

## 0. Encoding conventions (apply everywhere)
- Every polymorphic object — **trigger, condition, action, solution step** — is a JSON object with a string `"type"` discriminator plus flat fields. No keyed shorthand like `{ "stateEquals": {...} }`.
- Unknown `type` values, unknown fields, or missing required fields → validation issue (level rejected). Parse with `ignoreUnknownKeys = false`.
- Ids: `[a-z0-9][a-z0-9._-]*`. Object ids unique within a level. Level ids unique across the catalog.
- State values: `boolean`, `integer` or `string` only.

## 1. Top level
```json
{
  "schemaVersion": 1,
  "id": "teaser-0001",
  "family": "teaser",                       // teaser | combine | matchstick | oddoneout
  "runtime": "native-v1",
  "minAppVersion": 1,
  "difficulty": 2,                          // 1..5
  "languageDependent": true,
  "locales": ["en", "hi"],                  // must include "en"
  "text": { "prompt": "lvl.teaser-0001.prompt" },
  "hints": [ Hint, Hint, Hint ],            // exactly 3
  "state": { "flipped": false, "taps": 0 }, // initial state (teaser/combine; optional, default {})
  "scene": Scene,                           // teaser/combine: required · matchstick/oddoneout: absent
  "rules": [ Rule ],                        // teaser/combine: required, non-empty · others: absent
  "win": Condition,                         // optional declarative win check
  "params": { ... },                        // matchstick/oddoneout: required · others: absent
  "solution": [ SolutionStep ],             // teaser/combine: required · others: absent
  "altSolutions": [ [ SolutionStep ] ],     // optional
  "meta": { "author": "claude", "tags": ["wordplay"], "createdAt": "2026-10-14" }  // optional, free-form strings
}
```
Teaser/combine levels must be winnable: at least one `win` action in `rules` **or** a top-level `win` condition.

**Hint**: `{ "textKey": "lvl.x.h1" }` with optional `"highlight": "<objectId>"` and optional `"showSolution": true` (only allowed on the 3rd hint).

## 2. Scene and objects
```json
"scene": {
  "width": 1000, "height": 1600,            // v1: exactly 1000 x 1600 (portrait virtual units)
  "background": "bg.paper",                 // asset id of kind "background"
  "objects": [
    { "id": "word-number", "kind": "text", "textKey": "lvl.teaser-0001.word",
      "x": 520, "y": 300, "w": 260, "h": 90, "draggable": true, "snapBack": false },
    { "id": "n-9", "kind": "text", "text": "9", "x": 200, "y": 800, "w": 140, "h": 140, "tappable": true },
    { "id": "sun", "kind": "sprite", "asset": "obj.sun", "x": 800, "y": 200, "w": 160, "h": 160, "draggable": true, "z": 2 },
    { "id": "box", "kind": "shape", "shape": "rect", "color": "ink", "x": 500, "y": 1200, "w": 300, "h": 200 }
  ]
}
```
| Field | Rule |
|---|---|
| `id`, `kind`, `x`, `y`, `w`, `h` | required. `x,y` = center. `w,h > 0`. Bounds must lie inside the scene. |
| `kind: sprite` | requires `asset` (asset kind `sprite`) |
| `kind: text` | requires exactly one of `textKey` (localized) or `text` (literal — only digits, math symbols, single emoji) |
| `kind: shape` | requires `shape` ∈ `rect`, `circle`, `line` and `color` (design token name; validator checks it's non-empty, token list comes with the design system) |
| optional | `rotation` (deg, visual only), `z` (int, default 0), `visible` (default true), `tappable` (default false), `draggable` (default false), `snapBack` (default false), `tags` (string list) |

## 3. Rules
```json
{ "id": "r-win", "on": Trigger, "if": [ Condition ], "do": [ Action ], "once": false }
```
- `id` required, unique within the level. `on` required. `if` optional (list, all must be true; default `[]`). `do` required, non-empty. `once` optional (default false; if true the rule fires at most once).

### 3.1 Triggers (`on`) — canonical encodings
| type | fields | fires when |
|---|---|---|
| `tap` | `target` | the object is tapped |
| `longPress` | `target` | the object is long-pressed |
| `dragEnd` | `target`, optional `dropOn` (object id \| `"any"` \| `"none"`) | a drag of `target` ends inside the scene. `dropOn` omitted = any drop. `"any"` = dropped onto some object. `"none"` = dropped on empty space. |
| `dragOut` | `target`, `edge` ∈ `top`,`bottom`,`left`,`right`,`any` | a drag ends with the object's center outside the scene |
| `multiTouch` | `count` (2..5), optional `target` | at least `count` fingers are down at once (on `target` if given) |
| `device` | `event` ∈ `shake`,`flipUpsideDown`,`faceDown`,`tiltLeft`,`tiltRight`,`volumeUp`,`volumeDown`,`chargerConnected` | the app reports that device event |
| `timer` | `id` | a timer started by `startTimer` fires |

`dragStart`, `dragMove`, `hintRequested`, `start`, `pause`, `resume` are **runtime inputs only** (§6). They are NOT valid rule triggers.

**Interactivity rule (r3):** `tap` and `longPress` triggers may only target objects with `tappable: true`; `dragEnd`/`dragOut` triggers and `drag` solution steps may only target objects with `draggable: true`. Otherwise the validator reports `NOT_INTERACTIVE` (a rule that can never fire is a level bug).

### 3.2 Conditions — canonical encodings
| type | fields |
|---|---|
| `stateEquals` | `key`, `value` (bool/int/string, must match the declared type of `key`) |
| `stateAtLeast` | `key` (integer state), `value` (int) |
| `objectVisible` | `target` |
| `objectOverlaps` | `a`, `b` (axis-aligned bounds intersect; rotation ignored in v1) |
| `allOf` | `conditions` (non-empty list) |
| `anyOf` | `conditions` (non-empty list) |
| `not` | `condition` |

Example: `{ "type": "allOf", "conditions": [ { "type": "stateEquals", "key": "flipped", "value": true }, { "type": "objectVisible", "target": "key" } ] }`

### 3.3 Actions — canonical encodings
| type | fields | effect |
|---|---|---|
| `setState` | `key`, `value` | state change (key must be declared in `state`, same type) |
| `incState` | `key`, optional `by` (int, default 1) | integer state += by |
| `show` / `hide` | `target` | visibility |
| `moveTo` | `target`, `x`, `y`, optional `ms` (default 300) | position changes immediately in state; `ms` is an animation hint for the renderer |
| `swapAsset` | `target`, `asset` | sprite object changes asset |
| `animate` | `target`, `anim` ∈ `wiggle`,`pop`,`spin`,`bounce`,`fadeIn`,`fadeOut` | effect only, no state change |
| `sound` | `id` (asset kind `sound`) | effect only |
| `haptic` | `id` ∈ `light`,`medium`,`success`,`error` | effect only |
| `feedback` | `kind` ∈ `nope`,`close`,`yes` | effect only |
| `startTimer` | `id`, `ms` (> 0) | effect ScheduleTimer |
| `cancelTimer` | `id` | effect CancelTimer |
| `win` | — | session → COMPLETED |

## 4. Win
Optional top-level `"win": Condition` — evaluated after every processed input. `win` action or a true `win` condition → COMPLETED.

## 5. Solution steps (for replay tests)
| type | fields | replayed as |
|---|---|---|
| `tap` | `target` | Tap |
| `longPress` | `target` | LongPress |
| `drag` | `target` + either `toObject` (drop at that object's center) or `x`,`y` | DragStart → DragEnd at the point |
| `multiTouch` | `count`, optional `target` | MultiTouch |
| `device` | `event` | Device |
| `timer` | `id` | TimerFired |
Replay = `start` then each step; level must end COMPLETED. Same for every `altSolutions` entry.

## 6. Runtime semantics (`PuzzleSession`)
- **Inputs** (Kotlin API, not JSON): `Start`, `Pause`, `Resume`, `Tap(id)`, `LongPress(id)`, `DragStart(id)`, `DragMove(id, x, y)`, `DragEnd(id, x, y)`, `MultiTouch(count, id?)`, `Device(event)`, `TimerFired(id)`, `HintRequested`.
- Inputs carry object ids; the app hit-tests using the core helper `Scene.hitTest(x, y)` = topmost (`z`, then later in list) **visible** object whose axis-aligned bounds contain the point.
- `Tap` and `LongPress` are only accepted for visible `tappable` objects.
- Only `Start` is accepted in READY; gameplay inputs only in PLAYING; everything else ignored (no state change, logged).
- `DragStart/DragMove` only for `draggable` visible objects; they move the object (state) and fire no rules.
- `DragEnd(id, x, y)`: object center set to (x, y). If the center is outside the scene → fire `dragOut` with the edge crossed (if two, the one with the larger overshoot). Otherwise fire `dragEnd` with `dropOn` = topmost visible other object whose bounds contain the dragged object's center (or none). If **no rule matched** and the object has `snapBack: true`, it returns to its drag-start position.
- **Rule order:** for a fired trigger, rules are checked in file order. A rule matches if its trigger matches and all its `if` conditions are true **at the moment it is checked**; its actions apply immediately, in order (later rules see the new state). `once` rules are disabled after firing. Then the `win` condition is checked.
- After COMPLETED, all inputs are ignored.
- `HintRequested` (PLAYING only): `hintIndex = min(hintIndex + 1, 3)`; effect `ShowHint(index)`; if that hint has `showSolution`, also effect `ShowSolution`. Never fires rules.
- Timers: core never reads a clock. `startTimer` → effect `ScheduleTimer(id, ms)`; the app later sends `TimerFired(id)`. Cancelled or unknown timers are ignored.
- No randomness inside the core; generators take an explicit seed.

## 7. Family params (models + shape validation only in T-302)
**Matchstick**: `{ "equation": "6+4=4", "moves": 1, "allowedOps": ["+","-","="] }` — `equation` non-empty; `moves` 1..3; `allowedOps` non-empty subset of `+ - x =`. Solver + "≥ 1 solution" check come in the matchstick task (T-305), not T-302.
**Odd One Out**: `{ "seed": 81231, "rows": 6, "cols": 5, "base": "shape.circle", "variation": { "type": "rotation", "delta": 8 }, "timeLimitMs": null }` — rows/cols 2..10; `variation.type` ∈ `rotation`,`color`,`size`,`shape`; `delta` > 0. Generator comes later.

## 8. Strings & assets (the catalogs the validator needs)
- Strings: `content/strings/<locale>.json` → flat object `{ "key": "text" }`.
- Assets: `content/assets.json`:
```json
{ "schemaVersion": 1,
  "assets": {
    "bg.paper": { "kind": "background", "file": "bg/paper.webp" },
    "obj.sun":  { "kind": "sprite",     "file": "sprites/sun.xml" },
    "sfx.pop":  { "kind": "sound",      "file": "sfx/pop.ogg" } } }
```
The core checks ids and kinds only; file existence is checked app-side later.

## 9. Validator contract
```kotlin
data class ValidationContext(
    val supportedSchemaVersions: Set<Int>,          // v1: setOf(1)
    val supportedRuntimes: Set<String>,             // v1: setOf("native-v1")
    val appVersion: Int,                            // versionCode; level.minAppVersion must be <= this
    val assets: Map<String, AssetKind>,             // from content/assets.json
    val strings: Map<String, Set<String>>,          // locale -> keys, from content/strings/*.json
)
enum class AssetKind { BACKGROUND, SPRITE, SOUND }
data class ValidationIssue(val code: String, val path: String, val message: String)
fun validate(levelJson: String, ctx: ValidationContext): List<ValidationIssue>   // empty = valid; never throws
```
Issue codes (minimum): `PARSE_ERROR`, `UNSUPPORTED_SCHEMA`, `UNSUPPORTED_RUNTIME`, `APP_TOO_OLD`, `MISSING_FIELD`, `FIELD_NOT_ALLOWED`, `DUPLICATE_ID`, `UNKNOWN_REFERENCE` (object/state/timer), `UNKNOWN_ASSET`, `WRONG_ASSET_KIND`, `MISSING_TEXT` (key missing for a declared locale), `MISSING_EN_LOCALE`, `OUT_OF_BOUNDS`, `INVALID_VALUE`, `TYPE_MISMATCH` (state), `NOT_INTERACTIVE`, `NOT_WINNABLE` (no win action/condition), `HINT_COUNT`, `SOLUTION_FAILED` (replay didn't reach COMPLETED).
`path` is a JSON-pointer-like string, e.g. `/rules/2/do/0/target`.
The app builds the context from assets at startup; tests build it from `app/src/main/assets/content/`.

## 10. Complete example (combine family)
```json
{
  "schemaVersion": 1, "id": "combine-0001", "family": "combine", "runtime": "native-v1",
  "minAppVersion": 1, "difficulty": 1, "languageDependent": false, "locales": ["en", "hi"],
  "text": { "prompt": "lvl.combine-0001.prompt" },
  "hints": [
    { "textKey": "lvl.combine-0001.h1" },
    { "textKey": "lvl.combine-0001.h2", "highlight": "fire" },
    { "textKey": "lvl.combine-0001.h3", "showSolution": true } ],
  "state": { "melted": false },
  "scene": { "width": 1000, "height": 1600, "background": "bg.paper", "objects": [
    { "id": "ice",  "kind": "sprite", "asset": "obj.ice",  "x": 500, "y": 900, "w": 220, "h": 220 },
    { "id": "fire", "kind": "sprite", "asset": "obj.fire", "x": 500, "y": 1350, "w": 160, "h": 160, "draggable": true, "snapBack": true },
    { "id": "water", "kind": "sprite", "asset": "obj.water", "x": 500, "y": 900, "w": 220, "h": 220, "visible": false } ] },
  "rules": [
    { "id": "melt", "on": { "type": "dragEnd", "target": "fire", "dropOn": "ice" },
      "do": [ { "type": "hide", "target": "ice" }, { "type": "show", "target": "water" },
              { "type": "setState", "key": "melted", "value": true }, { "type": "sound", "id": "sfx.pop" } ] },
    { "id": "miss", "on": { "type": "dragEnd", "target": "fire", "dropOn": "none" },
      "do": [ { "type": "feedback", "kind": "nope" } ] } ],
  "win": { "type": "stateEquals", "key": "melted", "value": true },
  "solution": [ { "type": "drag", "target": "fire", "toObject": "ice" } ]
}
```
