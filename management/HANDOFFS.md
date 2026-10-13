# Handoffs

Append newest at the top. Every tool switch gets one entry.

```text
TASK ID:
CURRENT OWNER:
NEXT OWNER:
OBJECTIVE:
CURRENT STATUS:
FILES MODIFIED:
DECISIONS MADE:
TESTS RUN:
KNOWN ISSUES:
REMAINING WORK:
NEXT ACTION:
```

---
TASK ID: T-302 review → T-303, T-306
CURRENT OWNER: Claude
NEXT OWNER: Codex (resumes 07:00 IST, usage limit)
OBJECTIVE: Review + merge puzzle-core; queue follow-ups.
CURRENT STATUS: T-302 APPROVED, merged to main (66d7bf6). Schema r3 (NOT_INTERACTIVE). T-306 created; T-303 got item 6 (daemon JVM file).
FILES MODIFIED: docs/tech/LEVEL_SCHEMA.md, management/*
DECISIONS MADE: D-017 backend path, D-018 schema r3
TESTS RUN: none by Claude (no Android SDK here); code read in full: models, runtime, replayer, validator, test list, sample levels.
KNOWN ISSUES: Codex left uncommitted management edits (HANDOFFS/STATUS/TASKS) + untracked gradle/gradle-daemon-jvm.properties on task/T-302-puzzle-core.
REMAINING WORK: T-303, T-306.
NEXT ACTION: Codex: commit or drop its uncommitted management notes on the T-302 branch (main already has the final T-302 record), then `git checkout main && git pull`-equivalent (local main is current), start T-303, then T-306.

---
TASK ID: T-301 review · T-302 unblock · T-303 new
CURRENT OWNER: Claude
NEXT OWNER: Codex
OBJECTIVE: Merge skeleton, resolve schema blockers.
CURRENT STATUS: T-301 approved + merged to main. LEVEL_SCHEMA.md r2 + ARCHITECTURE.md §2/§5 updated on main. T-302 unblocked. T-303 follow-ups added.
FILES MODIFIED: docs/tech/LEVEL_SCHEMA.md, docs/tech/ARCHITECTURE.md, PROJECT_RULES.md (agent commit trailer), management/*
DECISIONS MADE: D-015 schema r2; D-016 Navigation3 1.2.0 (pending Codex verification)
TESTS RUN: none by Claude (no Android SDK in Claude's environment); relied on Codex's real build output for T-301.
KNOWN ISSUES: launch on device not yet verified (T-005).
REMAINING WORK: T-302, then T-303.
NEXT ACTION: Codex: on task/T-302-puzzle-core run `git merge main`; on TASKS.md/HANDOFFS.md conflicts keep main's version and re-add anything of yours; then implement.

---
TASK ID: T-201 → T-301, T-302
CURRENT OWNER: Claude
NEXT OWNER: Codex
OBJECTIVE: Build the project skeleton and the puzzle-core engine per approved architecture.
CURRENT STATUS: MVP, stack, ads policy, languages approved (D-009..D-012). Architecture + schema written (T-201, in owner review).
FILES MODIFIED: docs/tech/ARCHITECTURE.md, docs/tech/LEVEL_SCHEMA.md, docs/product/MVP.md, management/*
DECISIONS MADE: D-009..D-012 approved; D-013 (applicationId) pending owner.
TESTS RUN: n/a (docs)
KNOWN ISSUES: Folder must be renamed to brain-game before T-301. Git not yet set up. Codex's sandbox may need permission to run Gradle / download dependencies.
REMAINING WORK: T-301 then T-302.
NEXT ACTION: Codex reads ARCHITECTURE.md + LEVEL_SCHEMA.md, does T-301, then T-302.

---
TASK ID: T-101..T-104
CURRENT OWNER: Claude
NEXT OWNER: Owner (approvals), then Claude (T-201, T-202)
OBJECTIVE: Lean research + MVP proposal.
CURRENT STATUS: Drafts done, IN_REVIEW by owner.
FILES MODIFIED: docs/research/competitor-analysis.md, docs/research/opportunity-assessment.md, docs/game-design/PUZZLE_TAXONOMY.md, docs/tech/RUNTIME_OPTIONS.md, docs/product/MVP.md, management/*
DECISIONS MADE: None final — recommendations only (see MVP.md §11).
TESTS RUN: n/a (docs). Facts checked against cited sources.
KNOWN ISSUES: No reliable India eCPM figure found; must measure.
REMAINING WORK: Owner decisions in MVP.md §11.
NEXT ACTION: Owner answers 5 decisions.

---
TASK ID: T-004
CURRENT OWNER: Codex
NEXT OWNER: Claude
OBJECTIVE: Inventory the Mac's Android development tooling without installing or changing it.
CURRENT STATUS: COMPLETED; ready for Claude review.
FILES MODIFIED: docs/tech/ENVIRONMENT.md, management/TASKS.md, management/HANDOFFS.md
DECISIONS MADE: None.
TESTS RUN: Read-only version/path/platform checks for macOS, JDKs, Android Studio, Android SDK, Gradle, adb, emulator, AVDs, system images, and connected devices. No code tests exist for this task.
KNOWN ISSUES: Android tools are not on PATH; no global Gradle or project wrapper; emulator version check reports missing neon support; no connected device/running emulator.
REMAINING WORK: Claude reviews T-004 findings.
NEXT ACTION: Claude reviews docs/tech/ENVIRONMENT.md and raises any follow-up task if needed.

---
TASK ID: T-001 → T-004
CURRENT OWNER: Claude
NEXT OWNER: Codex
OBJECTIVE: Shared setup so Claude, Codex and owner work from one folder.
CURRENT STATUS: Rules + management files created, waiting on owner review.
FILES MODIFIED: PROJECT_RULES.md, AGENTS.md, CLAUDE.md, README.md, .gitignore, agents/ROLES.md, management/*, docs/MASTER_SPEC.md
DECISIONS MADE: D-001..D-006
TESTS RUN: none (no code)
KNOWN ISSUES: No Git yet.
REMAINING WORK: Codex does T-004 (environment check, no installs).
NEXT ACTION: Codex reads AGENTS.md → PROJECT_RULES.md → T-004.
