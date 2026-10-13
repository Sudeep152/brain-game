# Task Registry

Statuses: BACKLOG · READY · ASSIGNED · IN_PROGRESS · BLOCKED · IN_REVIEW · CHANGES_REQUESTED · COMPLETED · CANCELLED
Owner tasks: `ASSIGNED TO: OWNER` (OWNER_ASSIGNED). Claim a task before editing. Keep entries short.

---
## M0 — Shared setup

### T-001 · Create shared rules + management files
- ASSIGNED TO: Claude · STATUS: IN_REVIEW · PRIORITY: P0 · MILESTONE: M0
- FILES OWNED: PROJECT_RULES.md, AGENTS.md, CLAUDE.md, README.md, .gitignore, agents/ROLES.md, management/*, docs/MASTER_SPEC.md
- ACCEPTANCE: Codex + Claude both start from the same rules; owner reviews.
- REVIEWER: Owner · EVIDENCE: files created 2026-10-14 (Day 0)

### T-002 · Rename folder to `brain-game` and re-point tools
- ASSIGNED TO: OWNER · STATUS: ASSIGNED · PRIORITY: P0 · MILESTONE: M0
- DESCRIPTION: Rename "Brain Game AI Dev" → `brain-game` (spaces break native/NDK builds). Then re-select the folder in Codex project and in the Claude chat.
- ACCEPTANCE: Both tools open the renamed folder.

### T-003 · Git + private GitHub repo
- ASSIGNED TO: OWNER · STATUS: BACKLOG · PRIORITY: P1 · MILESTONE: M0
- DESCRIPTION: `git init`, first commit, private GitHub repo. Until done: one tool edits at a time.
- ACCEPTANCE: Repo exists, `.gitignore` in place, first commit pushed.

### T-005 · Confirm emulator / test device works
- ASSIGNED TO: OWNER · STATUS: ASSIGNED · PRIORITY: P1 · MILESTONE: M0
- DESCRIPTION: In Android Studio, launch AVD Pixel_10a once. Also note if you have a physical Android phone for testing (model + Android version).
- ACCEPTANCE: Emulator boots or issue noted in docs/tech/ENVIRONMENT.md.

### T-004 · Codex onboarding + environment check
- ASSIGNED TO: Codex · STATUS: COMPLETED · PRIORITY: P0 · MILESTONE: M0
- DESCRIPTION: Read AGENTS.md + PROJECT_RULES.md. Check (do NOT install anything) what's on the Mac: JDK version, Android SDK path/platforms, Android Studio version, Gradle, adb, connected devices/emulators. Write findings.
- FILES OWNED: docs/tech/ENVIRONMENT.md (create), management/HANDOFFS.md (append), this task entry
- ACCEPTANCE: ENVIRONMENT.md lists real command outputs; handoff entry added; no other files touched.
- REVIEWER: Claude
- FILES TOUCHED: docs/tech/ENVIRONMENT.md, management/TASKS.md, management/HANDOFFS.md
- EVIDENCE: Real outputs recorded in docs/tech/ENVIRONMENT.md on 2026-10-14; Codex → Claude handoff added; no installs or package changes performed.
- REVIEW (Claude, 2026-10-14): ACCEPTED. Stayed in scope, real outputs, no extra files. Follow-ups: emulator "neon" error is likely Codex's sandbox shell, not the Mac — owner to confirm by launching AVD Pixel_10a from Android Studio (T-005). Gradle wrapper will come with project creation.

---
## M1 — Lean research (Claude, after T-002)

### T-101 · Competitor & market snapshot (lean)
- ASSIGNED TO: Claude · STATUS: COMPLETED · PRIORITY: P0 · MILESTONE: M1
- DESCRIPTION: Top tricky/brain puzzle games on Play (Brain Out, Brain Test, DOP, Tricky Puzzle-style), what players love/hate (reviews), monetization pattern, gaps. Facts with sources.
- FILES OWNED: docs/research/competitor-analysis.md, docs/research/opportunity-assessment.md
- EVIDENCE: written 2026-10-14 with sources; reviewer: Owner

### T-102 · Puzzle taxonomy + MVP puzzle families shortlist
- ASSIGNED TO: Claude · STATUS: COMPLETED · PRIORITY: P0 · MILESTONE: M1
- DESCRIPTION: Classify puzzle families by build cost, fun, and future auto-generation feasibility. Pick 4–5 for MVP.
- FILES OWNED: docs/game-design/PUZZLE_TAXONOMY.md

### T-103 · Runtime tech evaluation
- ASSIGNED TO: Claude · STATUS: COMPLETED · PRIORITY: P0 · MILESTONE: M1
- DESCRIPTION: Compose + Canvas vs. native physics lib (e.g., Box2D/JBox2D-style) vs. Unity-as-a-library. Recommendation sized to a 10–15 day MVP.
- FILES OWNED: docs/tech/RUNTIME_OPTIONS.md

### T-104 · MVP proposal (scope, levels, monetization basics, plan)
- ASSIGNED TO: Claude · STATUS: COMPLETED · DEPENDS ON: T-101..T-103 · MILESTONE: M2 · REVIEWER: Owner
- FILES OWNED: docs/product/MVP.md
- ACCEPTANCE: Owner approves or edits.

### T-105 · Critical review of T-101..T-104
- ASSIGNED TO: Claude (fresh pass) · STATUS: BACKLOG · DEPENDS ON: T-104 · NOTE: do before alpha upload

---
## Next (after owner approves T-104)

### T-201 · Architecture + level JSON schema v1
- ASSIGNED TO: Claude · STATUS: IN_REVIEW · DEPENDS ON: T-104 approval · MILESTONE: M3
- FILES OWNED: docs/tech/ARCHITECTURE.md, docs/tech/LEVEL_SCHEMA.md
- EVIDENCE: written 2026-10-14; reviewer: Owner (Codex may raise issues while implementing T-302)

### T-202 · Three visual directions → owner picks one
- ASSIGNED TO: Claude (UI/UX hat) · STATUS: READY · DEPENDS ON: T-104 approval · MILESTONE: M3
- FILES OWNED: docs/design/VISUAL_DIRECTIONS.md (+ mockups)

### T-203 · Play Console account + 12 closed testers lined up
- ASSIGNED TO: OWNER · STATUS: BACKLOG · MILESTONE: M3
- DESCRIPTION: Needed so alpha can enter closed testing ~day 6 (14-day rule).

### T-204 · Final applicationId (Play identity, cannot change after first upload)
- ASSIGNED TO: OWNER · STATUS: BACKLOG · MILESTONE: M3
- DESCRIPTION: Pick the permanent applicationId (e.g. `com.<yourname>.<something>`) before first Play upload. Placeholder `com.example.projectpuzzle` is rejected by Play on purpose. (D-013)

---
## M4 — Build (Codex)

### T-301 · Android project skeleton
- ASSIGNED TO: Codex · STATUS: IN_REVIEW · PRIORITY: P0 · MILESTONE: M4
- DEPENDS ON: T-002 (folder renamed), T-201
- DESCRIPTION: Create Gradle project per docs/tech/ARCHITECTURE.md §5: modules `:app` (Android, Compose) and `:puzzle-core` (pure Kotlin/JVM, no Android deps). Version catalog, Gradle wrapper, JDK 21 toolchain, latest STABLE libs only (Compose, Navigation3, Hilt+KSP, Room+KSP, DataStore, kotlinx.serialization). minSdk 26; compile/target = newest stable API installed. applicationId `com.example.projectpuzzle` (placeholder). App name only in `app_name` + a `Brand` object. Placeholder screens Home / Play / Settings wired with Navigation3. English + Hindi `values`/`values-hi` for the few UI strings. Placeholder theme in `ui/design/` (tokens file; real visual direction comes in T-202 — keep it plain, don't invent a style).
- FILES OWNED: settings.gradle.kts, build.gradle.kts, gradle.properties, gradle/**, gradlew, gradlew.bat, app/**, puzzle-core/build.gradle.kts
- ACCEPTANCE: `./gradlew :app:assembleDebug` and `./gradlew :puzzle-core:test` succeed (paste real output tail in EVIDENCE); app launches to Home on emulator if available; no alpha/beta libs; no ads/analytics SDKs yet.
- REVIEWER: Claude
- FILES TOUCHED: settings.gradle.kts, build.gradle.kts, gradle.properties, gradle/**, gradlew, gradlew.bat, app/**, puzzle-core/build.gradle.kts, this task entry
- EVIDENCE (2026-10-14): `ANDROID_HOME=/Users/shashank/Library/Android/sdk ./gradlew :app:assembleDebug :puzzle-core:test`
  ```text
  > Task :puzzle-core:test NO-SOURCE
  > Task :app:assembleDebug
  BUILD SUCCESSFUL in 47s
  42 actionable tasks: 42 executed
  Configuration cache entry stored.
  ```
- DEPENDENCY CHECK: `:app:dependencies --configuration debugRuntimeClasspath` succeeded; automated case-insensitive scan found 0 `alpha`/`beta`/`rcN` matches. Navigation3 uses stable 1.1.7 because the documented 1.2.0 is currently RC, not stable.
- LIMIT: `adb devices -l` found no attached device/emulator, so launch-to-Home was not run.

### T-302 · puzzle-core: schema models, validator, session (reducer)
- ASSIGNED TO: Codex · STATUS: BLOCKED · PRIORITY: P0 · MILESTONE: M4 · DEPENDS ON: T-301
- DESCRIPTION: Implement docs/tech/LEVEL_SCHEMA.md in `:puzzle-core`: kotlinx.serialization models; `LevelValidator` (checklist §8, except touch-target size); `PuzzleSession` pure reducer with state machine (ARCHITECTURE §2), generic rules engine (events/conditions/actions §3–4); solution replay. Matchstick/oddoneout params: models only (logic comes later). Add 2 sample levels (teaser + combine) + en/hi strings under `app/src/main/assets/content/` and a test that loads every level in that folder, validates it and replays its solution to COMPLETED.
- FILES OWNED: puzzle-core/src/**, app/src/main/assets/content/**
- ACCEPTANCE: unit tests for reducer transitions, each event/condition/action type, validator failures, and sample-level replay — all passing (real output). If the schema has a gap or contradiction, don't invent: note it and message Claude.
- REVIEWER: Claude
- BLOCKER (Codex, 2026-10-14): `LEVEL_SCHEMA.md` does not define the JSON field/name or one canonical encoding for rule conditions (the condition list uses `stateEquals {…}`, while actions/events use a `type` discriminator and `win` uses keyed objects). `ARCHITECTURE.md` adds `dragStart`, `dragMove`, and `hintRequested`, but the schema event whitelist omits them. The validator requires matchstick levels to have ≥1 solution, while T-302 explicitly limits matchstick to models only and defers logic. Asset/text-reference validation also needs an explicit validation context/catalog contract. Awaiting Claude's schema clarification; no puzzle-core source was invented.
