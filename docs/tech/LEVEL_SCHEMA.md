# Level Schema v1 (T-201)

Date: 2026-10-14 · Status: draft · Owner of truth: `:puzzle-core` models must match this file.
Levels are **data only**. No scripts, no expressions evaluated as code.

## 1. Top level
```json
{
  "schemaVersion": 1,
  "id": "teaser-0001",
  "family": "teaser",                 // teaser | combine | matchstick | oddoneout
  "runtime": "native-v1",             // must be a runtime the app ships
  "minAppVersion": 1,
  "difficulty": 2,                    // 1 (easy) .. 5 (hard)
  "languageDependent": true,          // wordplay? then needs per-language variant
  "locales": ["en", "hi"],            // languages where this level is playable
  "text": { "prompt": "lvl.teaser-0001.prompt" },
  "hints": [
    { "textKey": "lvl.teaser-0001.h1" },
    { "textKey": "lvl.teaser-0001.h2", "highlight": "word-number" },
    { "textKey": "lvl.teaser-0001.h3", "showSolution": true }
  ],
  "scene": { ... },                   // teaser / combine
  "rules": [ ... ],                   // teaser / combine
  "win":   { ... },                   // teaser / combine
  "params": { ... },                  // matchstick / oddoneout
  "solution": [ ... ],                // REQUIRED for teaser/combine: input events that solve it
  "meta": { "author": "claude", "tags": ["wordplay"], "createdAt": "2026-10-14" }
}
```

## 2. Scene (teaser / combine)
```json
"scene": {
  "width": 1000, "height": 1600,
  "background": "bg.paper",
  "objects": [
    { "id": "word-number", "kind": "text", "textKey": "lvl.teaser-0001.word",
      "x": 520, "y": 300, "w": 260, "h": 90, "draggable": true },
    { "id": "n-9",  "kind": "text", "text": "9",  "x": 200, "y": 800, "w": 120, "h": 120, "tappable": true },
    { "id": "n-12", "kind": "text", "text": "12", "x": 500, "y": 800, "w": 120, "h": 120, "tappable": true },
    { "id": "sun", "kind": "sprite", "asset": "obj.sun", "x": 800, "y": 200, "w": 160, "h": 160,
      "draggable": true, "z": 2, "visible": true }
  ]
}
```
- `kind`: `sprite` (asset id) · `shape` (`shape: rect|circle|line`, `color` token) · `text` (`textKey` or literal `text` for numbers/symbols only).
- Positions are centers in virtual units. Optional: `rotation`, `z`, `visible`, `draggable`, `tappable`, `tags`.

## 3. Rules = event → conditions → actions
```json
"rules": [
  { "on": { "type": "dragEnd", "target": "word-number", "dropOn": "any" },
    "do": [ { "type": "win" } ] },
  { "on": { "type": "tap", "target": "n-12" },
    "do": [ { "type": "feedback", "kind": "nope" }, { "type": "haptic", "id": "light" } ] },
  { "on": { "type": "device", "event": "flipUpsideDown" },
    "do": [ { "type": "setState", "key": "flipped", "value": true }, { "type": "show", "target": "hidden-key" } ] }
]
```
**Event types:** `tap` · `longPress` · `dragEnd` (`dropOn`: object id | `any` | `none`) · `dragOut` (`edge`: top/bottom/left/right) · `multiTouch` (`count`) · `device` (`shake`, `flipUpsideDown`, `faceDown`, `tiltLeft`, `tiltRight`, `volumeUp`, `volumeDown`, `chargerConnected`) · `timer` (`id`, `ms`).
**Conditions:** `stateEquals {key,value}` · `objectVisible {target}` · `objectOverlaps {a,b}` · `allOf [...]` · `anyOf [...]` · `not {...}`.
**Actions:** `setState` · `show` · `hide` · `moveTo {target,x,y,ms}` · `swapAsset {target,asset}` · `animate {target,anim}` · `sound {id}` · `haptic {id}` · `feedback {kind: nope|close|yes}` · `startTimer {id,ms}` · `win`.
Unknown types = validation error.

## 4. Win
Either a `win` action in a rule, or a declarative check evaluated after each event:
```json
"win": { "allOf": [ { "stateEquals": { "key": "flipped", "value": true } } ] }
```

## 5. Solution (for automatic testing)
```json
"solution": [ { "type": "dragEnd", "target": "word-number", "dropOn": "any" } ]
```
`:puzzle-core` replays it in tests; the level must reach COMPLETED. Alternate valid solutions → add `"altSolutions": [[...], ...]`.

## 6. Family params
**Matchstick**
```json
"params": { "equation": "6+4=4", "moves": 1, "allowedOps": ["+","-","="] }
```
The solver finds **all** valid answers → every valid answer is accepted (fairness). Validator rejects levels with zero solutions.

**Odd One Out**
```json
"params": { "seed": 81231, "rows": 6, "cols": 5, "base": "shape.circle",
            "variation": { "type": "rotation", "delta": 8 }, "timeLimitMs": null }
```
Generated deterministically from `seed`. Daily Challenge = seed from the date.

## 7. Strings
`content/strings/en.json`
```json
{ "lvl.teaser-0001.prompt": "Find the biggest number", "lvl.teaser-0001.word": "number" }
```
Every key used by a level must exist for each locale in `locales`.

## 8. Validator checklist (`LevelValidator`)
schemaVersion supported · runtime supported · unique object ids · all references (targets, assets, text keys per locale) exist · only whitelisted types · objects inside scene bounds · touch targets ≥ 48dp-equivalent · `solution` replays to COMPLETED · matchstick has ≥1 solution · 3 hints present.
