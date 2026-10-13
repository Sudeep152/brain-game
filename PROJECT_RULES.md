# PROJECT_RULES.md — Canonical Rules (PROJECT_PUZZLE)

> This is the ONE authoritative rules file. `AGENTS.md` and `CLAUDE.md` only point here.
> If a rule changes, change it here. Full original vision: `docs/MASTER_SPEC.md` (read only when needed — it is long).

## 1. What we are building
- Android-only brain puzzle game (tricky teasers, visual, drag-drop, drawing, physics-lite, logic, hidden object, etc.).
- Codename `PROJECT_PUZZLE`. Never hardcode the name — keep it in one place (string resources / brand config).
- Preferred stack: Kotlin + Jetpack Compose + Android APIs. No Unity/Godot/other engine without research + owner approval.
- Must feel like a crafted game, not a quiz app and not "AI-generated looking" UI.

## 2. People and roles
| Who | Role |
|---|---|
| **Owner (Shashank)** | Final authority. Approves decisions, builds/tests in Android Studio on real devices, may take any task. |
| **Claude** | Super Agent: planning, task registry, research, architecture, design direction, code review, integration. |
| **Codex** | Implementation of clearly scoped tasks, tests, environment checks. |
| **Local / free LLMs (later)** | Only small, low-risk, well-defined tasks (boilerplate, formatting, simple tests). Output always reviewed. |

Role cards for specialist hats (research, game design, Android, QA, …): `agents/ROLES.md`.

## 3. Source of truth
- **This folder is the only source of truth.** Chat history of any tool is NOT a record. If it matters, write it in a file.
- Decisions → `management/DECISIONS.md`. Tasks → `management/TASKS.md`. Status → `management/STATUS.md`.
- Handoffs between tools → `management/HANDOFFS.md` (template inside).

## 4. Every session — start
1. Read `PROJECT_RULES.md` (this file).
2. Read `management/STATUS.md` and `management/TASKS.md`.
3. Find YOUR assigned task. If you have none, stop and ask the owner. Do not invent work.
4. Read only the docs/files that task needs. Do not load the whole repo.

## 5. Every session — end
1. Update your task in `management/TASKS.md` (status, files touched, evidence).
2. Update `management/STATUS.md` (short).
3. Add a handoff entry in `management/HANDOFFS.md` if someone else continues.
4. Log any decision in `management/DECISIONS.md`.

## 6. Working together without stepping on each other
- One task = one owner. Claim a task (set status `IN_PROGRESS` + your name) **before** editing.
- Only edit files listed under your task's `FILES OWNED`. Need another file? Note it in the task and ask.
- Repo: local folder `brain-game` + GitHub `Sudeep152/brain-game` (private). `main` must always build and pass tests.
- **Code work = one branch per task:** `task/T-xxx-short-name` from latest `main`. Small commits with clear messages (`T-301: add Gradle skeleton`).
- When done: set task `IN_REVIEW`; Claude reviews the branch; after review it's merged into `main` (owner can veto). Owner pushes to GitHub (or the agent pushes if it has access).
- Docs/management-only updates by Claude may be committed directly on `main` if no code files are touched.
- Before starting: `git status` must be clean and you must be on the right branch. Never commit on someone else's task branch.
- Never force-push, rebase shared branches, reset, or delete others' work. Never commit secrets.
- Two agents may work at the same time only on different branches with non-overlapping `FILES OWNED`.
- Never overwrite or "clean up" the owner's code without asking.

## 7. Approval gates (owner must say yes first)
Final tech stack · new game engine · major architecture change · visual direction · any paid service or recurring cost · any marketing spend · production deploy · Play Store release · monetization change · scope change · removing major features · destructive repo/database operations.

## 8. Cost rules (budget is tight)
- Spend tokens only on what the current task needs. Short status reports, no long essays for routine work.
- No paid APIs, services, assets or tools without owner approval.
- Use the cheapest tool that can do the job reliably; don't give risky work to weak models.
- Log notable AI usage in `management/AI_USAGE.md` only when real numbers exist. Never invent numbers.

## 9. Engineering standards (short)
- Idiomatic Kotlin, coroutines/Flow, immutable UI state, state hoisting in Compose, no main-thread blocking, no global mutable state.
- Don't over-engineer: no interface for every class, no abstraction "for later".
- Follow existing patterns in the codebase (DI, state, navigation). Don't introduce competing ones.
- Level data is **data only** (JSON). Downloaded/remote levels may never contain executable code.
- Product name, colors, typography, spacing come from the shared design system — never invented per screen.

## 10. Honesty rules
- Never claim tests passed unless they were actually run. Paste the real result.
- Never mark a task COMPLETED unless: implemented, acceptance criteria met, tests run, docs updated, limits noted.
- Never fabricate progress, revenue, usage or research. Separate facts (with source) from guesses.

## 11. Task statuses
BACKLOG · READY · ASSIGNED · IN_PROGRESS · BLOCKED · IN_REVIEW · CHANGES_REQUESTED · COMPLETED · CANCELLED
Owner-taken tasks: `ASSIGNED TO: OWNER` (OWNER_ASSIGNED) — agents don't touch those files.

## 12. Team working agreement (applies to Claude, Codex, and every future agent or LLM)

### 12.1 We discuss — nobody blindly agrees
- We are one team. **Do not simply agree with the owner (or with another agent) to be agreeable.**
- Before recommending or accepting an idea, **do proper research** (sources, data, code checks) and give an honest view: what is good, what is risky, what you'd do differently, and why.
- If you disagree, say so clearly and respectfully, with evidence and a better alternative.
- A decision is final only when it has been **discussed and both sides agree**. Record it in `management/DECISIONS.md`.
- If we still disagree after discussion, write down both positions in `DECISIONS.md`; the owner makes the final call (owner keeps final authority).
- Never change agreed scope, architecture or plans silently because someone "asked once" — confirm through discussion first.

### 12.2 Agent-to-agent messages go through the owner
- Agents (Claude, Codex, others) cannot talk to each other directly. The owner carries messages.
- When — and **only when** — something important must reach another agent (a new task, a rule change, a blocker, a review result that needs action), put it at the **very end of your reply** in this exact format so the owner can copy-paste it:

```text
📨 MESSAGE FOR <Codex | Claude | AgentName>
(copy-paste everything inside this block)
<short, self-contained message: task ID, what to do, which files, acceptance criteria>
```

- Don't send a message for routine updates, chit-chat, or things already written in the repo files the other agent will read anyway.
- The message must be self-contained. The important content must also be written in the repo (`management/TASKS.md` / `management/HANDOFFS.md`), so nothing depends on chat history.
- Tell the owner in one line why the message matters.
