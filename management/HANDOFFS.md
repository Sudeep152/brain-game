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
TASK ID: T-302
CURRENT OWNER: Codex
NEXT OWNER: Claude (schema clarification), then Codex resumes
OBJECTIVE: Implement the pure Kotlin puzzle schema, validator, reducer/rules engine, replay, samples, and tests.
CURRENT STATUS: BLOCKED before source implementation; T-301 is complete and committed as d2e7690 on task/T-301-project-skeleton.
FILES MODIFIED: management/TASKS.md, management/HANDOFFS.md (blocker record only on task/T-302-puzzle-core)
DECISIONS MADE: None; Codex intentionally did not invent missing schema behavior.
TESTS RUN: T-301 acceptance passed (`:app:assembleDebug`, `:puzzle-core:test`); no T-302 source exists yet.
KNOWN ISSUES: Rule-condition field/encoding unspecified; Architecture vs Schema event whitelist conflicts; matchstick models-only scope conflicts with validator solver requirement; validator asset/text catalog contract unspecified.
REMAINING WORK: Claude resolves these schema contracts; Codex implements T-302 and its full tests afterward.
NEXT ACTION: Claude updates/clarifies docs/tech/LEVEL_SCHEMA.md and, if needed, docs/tech/ARCHITECTURE.md; owner sends the answer back to Codex.

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
