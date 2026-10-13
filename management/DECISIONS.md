# Decision Log

| ID | Date | Decision | Why / trade-off | Owner approved |
|---|---|---|---|---|
| D-001 | 2026-10-14 | This folder = single source of truth. `PROJECT_RULES.md` canonical; `AGENTS.md` (Codex/others) and `CLAUDE.md` only point to it. | Tools can't see each other's chats. One rules file avoids conflicting copies. | Yes |
| D-002 | 2026-10-14 | Roles: Claude = Super Agent (plan/research/architecture/review); Codex = implementation; Owner = approvals + device testing; local/free LLMs later for low-risk tasks only. | Strong reasoning where mistakes are costly; cheaper tools for routine work. | Yes ("do your best") |
| D-003 | 2026-10-14 | Tight cost policy: no paid services/APIs/assets without approval; minimal token use. | Budget is limited at start. | Yes |
| D-004 | 2026-10-14 | Git/GitHub set up by owner later. Until then, one tool edits at a time; claim task first. | Prevents overwrites without version control. | Yes |
| D-005 | 2026-10-14 | Rename folder to `brain-game` (no spaces). | Spaces in paths break native/NDK builds. | Yes |
| D-006 | 2026-10-14 | MVP target 10–15 days; backend, AI generation, Unity deferred. | Owner timeline. Exact scope still pending (M2). | Timeline yes; scope pending |
| D-007 | 2026-10-14 | Team rule: no blind agreement. Research → honest view → discuss → decide together; log in DECISIONS. Owner has final say if still disagreed. (PROJECT_RULES §12.1) | Better decisions; avoids "yes-man" agents. | Yes (owner requested) |
| D-008 | 2026-10-14 | Agent-to-agent messages go via owner as a `📨 MESSAGE FOR <agent>` block at end of reply, only when important; content also written to repo. (PROJECT_RULES §12.2) | Tools can't talk directly; avoids message spam. | Yes (owner requested) |
| D-009 | 2026-10-14 | Multi-language. Build i18n into architecture from day 1; MVP ships English + Hindi; next languages chosen by data (likely pt-BR, es, id); every language needs native review before release. | Wordplay doesn't translate; each language multiplies QA; owner can review Hindi for free. Details MVP.md §6b. | Yes |
| D-010 | 2026-10-14 | MVP scope per docs/product/MVP.md (4 families, ~40 levels + daily, offline, out-of-scope list). | Fits 10–15 day build; content can grow later. | Yes |
| D-011 | 2026-10-14 | Tech stack: Kotlin + Jetpack Compose + custom Canvas puzzle runtime, JSON levels, no game engine for MVP (RUNTIME_OPTIONS.md option A). | Fastest, owner's expertise, small app; physics/Unity can plug in later via `runtime` field. | Yes |
| D-012 | 2026-10-14 | Fair-ads policy as starting hypothesis: rewarded ads for hints; no interstitials in first 10 levels; max 1 per 3 levels; never after a fail. | #1 competitor complaint is ads. Measure revenue vs retention. | Yes |
| D-013 | 2026-10-14 | **PENDING owner:** final applicationId. Placeholder `com.example.projectpuzzle` (Play rejects it, forcing a decision before upload). | applicationId = permanent Play identity. | Pending |
| D-014 | 2026-10-14 | Git workflow: branch per task, Claude reviews → merge to main, owner pushes/vetoes; Claude may commit docs/management-only changes on main. (PROJECT_RULES §6) | Repo + GitHub now exist; enables safe parallel work. | Yes (owner set up repo) |
