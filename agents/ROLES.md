# Agent Role Cards

These are "hats" a tool (Claude, Codex, local LLM) or the owner wears for a task. They are NOT separate always-running bots.
Nothing runs between sessions. Each task in `management/TASKS.md` names the role + the tool doing it.
Full role descriptions: `docs/MASTER_SPEC.md` Part 5.

| Role | Usually done by | Focus | Main outputs |
|---|---|---|---|
| Super Agent | Claude | Plan, assign, review, integrate, report to owner | `management/*` |
| Market Research | Claude | Competitors, reviews, gaps (facts with sources) | `docs/research/` |
| Product Strategy | Claude | Audience, core loop, MVP scope, metrics | `docs/product/` |
| Game Design | Claude (+ owner) | Puzzle families, mechanics, difficulty, hints | `docs/game-design/` |
| UI/UX Design | Claude (+ owner approval) | 3 visual directions → 1 design system; no generic AI look | `docs/design/` |
| Android Architecture | Claude | App structure, modules, state, navigation | `docs/tech/` |
| Game Engine / Physics | Claude design, Codex build | Puzzle runtime, level schema, gestures, physics choice | `docs/tech/`, code |
| Android Dev | Codex (+ owner) | Implement scoped features | code + tests |
| QA | Codex | Unit/UI tests, bug reports with repro steps | tests, reports |
| AI Generation / Validation | Claude (later milestones) | Level generator + validator | later |
| Backend / DevOps | Later, on approval | Remote levels, CI | later |
| Monetization / Growth / Analytics | Claude (research only until launch) | Ads/IAP model, ASO, Reels/Shorts plan | `docs/product/` |
| Critical Review | Claude (fresh pass) or Codex | Challenge assumptions, find weak spots | review notes |
| Low-risk helper | Local/free LLM (later) | Boilerplate, formatting, simple tests | reviewed before merge |
