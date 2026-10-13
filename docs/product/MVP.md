# MVP Proposal (T-104)

Date: 2026-10-14 · Author: Claude · Status: **APPROVED by owner 2026-10-14** (D-010..D-012). Owner answers still open: §11 items 4–5.
Based on: docs/research/*, docs/game-design/PUZZLE_TAXONOMY.md, docs/tech/RUNTIME_OPTIONS.md

## 1. One-line pitch
A fair, polished brain-puzzle game where every level feels different — funny trick teasers, phone-trick puzzles, and never-ending logic puzzles — with ads that don't ruin the fun.

## 2. Target player
- Casual players 13+ who like Brain Out / Brain Test style "aha!" puzzles.
- English-first, language-light visuals → playable in India and higher-paying markets.
- Short sessions (2–10 min), play with family/friends, share puzzles.

## 3. Core loop
Open → play next level (≤60 s typical) → "aha!" + satisfying completion animation → next level.
Stuck → 3-step hint (rewarded ad) → solve → continue.
Daily → one Daily Challenge → shareable result card (WhatsApp/Instagram) → friends install.

## 4. MVP content (≈40 levels + infinite daily)
| Family | Levels | How made |
|---|---|---|
| Trick Teasers (incl. ~5 Device Tricks: flip, shake, tilt, volume, charger) | 18 | Handcrafted (Claude drafts, owner approves, playtest) |
| Matchstick Math | 10 | Generator + solver, curated by hand |
| Odd One Out | 6 + Daily Challenge | Procedural |
| Combine & Drag | 6 | Handcrafted |
Order mixes families so two of the same type never come back-to-back.

## 5. MVP features
- Home (continue / level map / daily), Level screen, Completion screen, Hint sheet, Settings (sound, haptics, privacy).
- Progress saved offline; whole game works offline (ads/daily need network only to load).
- Sound + haptics + completion animations (polish is part of the product, not "later").
- Analytics: Firebase Analytics + Crashlytics (free) — level start/complete/fail/hint, D1/D7 retention.
- Monetization (fair-ads policy, a hypothesis to measure):
  - Rewarded ad → unlock hint step.
  - Interstitial: **none in the first 10 levels**, then at most 1 per 3 completed levels, never right after a fail, always skippable.
  - "Remove ads" purchase → v1.1.

## 6. Out of MVP (later, already planned in MASTER_SPEC)
Drawing puzzles · physics puzzles · escape rooms · backend / remote levels · AI-generated levels (beyond the matchstick generator) · leaderboards · accounts · languages beyond English+Hindi · paid marketing.

## 6b. Languages (D-009, approved)
- i18n built in from day 1: no hard-coded text; levels use text keys + per-language tables; fonts cover Latin-ext, Devanagari, Cyrillic.
- MVP ships **English + Hindi** (owner reviews Hindi).
- Each level is tagged `languageDependent`. Language-dependent levels need a per-language variant or are hidden in that language.
- Next languages chosen from real analytics after launch (likely pt-BR, es-419, id). Every language: AI translation → native review → release.
- Store listing only in languages the game supports.

## 7. ⚠ Google Play 14-day rule changes the timeline
New personal developer accounts must run a **closed test with 12 testers for 14 continuous days** before going public (Google Play Console Help). So:
- We can **build** the MVP in 10–15 days, but **public launch ≈ day 20–25**.
- Trick: the 14-day clock starts when testers opt in, and builds can be updated during the test → **ship a rough playable alpha to closed testing by ~day 6** and keep updating it.

## 8. Plan (owner can adjust)
| Days | What | Who |
|---|---|---|
| 1–2 | Approve MVP · architecture + level JSON schema · 3 visual directions → pick 1 | Claude, Owner |
| 2–3 | Project skeleton, design tokens, runtime core (gestures, state machine, win check) | Codex (+Owner), Claude reviews |
| 3–5 | Teasers + Odd One Out working; first 10 levels | Codex, Claude writes level JSON |
| ~6 | **Alpha → Play closed testing, 12 testers opted in (14-day clock starts)** | Owner |
| 6–12 | Matchsticks, Combine, Daily Challenge + share card, hints, sounds, haptics, ads (test mode), analytics, 40 levels | Codex, Owner, Claude |
| 12–15 | Polish, device testing, bug fixes, store listing draft | All |
| ~20–25 | 14 days done → production review → public launch (owner approval) | Owner |

## 9. Success metrics (targets to measure, not facts)
- Closed test: ≥ 90% finish level 1, crash-free ≥ 99%, testers say levels feel fair.
- After launch (targets): D1 ≥ 35%, D7 ≥ 12%, average ≥ 2 sessions/day for active players, share-card usage measurable.

## 10. Costs (one-time / recurring)
- Google Play developer account: one-time US$25 (if not already owned).
- Firebase Analytics/Crashlytics, AdMob: free to use.
- No server in MVP → ₹0 hosting.
- AI tools: existing Claude + Codex subscriptions only.

## 11. Decisions needed from owner
1. Approve this MVP scope (families, ~40 levels, out-of-scope list).
2. Approve tech stack: Kotlin + Compose + custom Canvas runtime, JSON levels (RUNTIME_OPTIONS.md, option A).
3. Approve fair-ads policy as the starting hypothesis.
4. Play Console: do you already have a developer account? Personal or organization, and created when?
5. Can you line up 12+ testers (friends/family with Android phones) for the closed test?
