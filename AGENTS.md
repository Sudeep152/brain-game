# AGENTS.md — Entry point for Codex and any other coding agent

This file is read by Codex and by other agents/LLMs (including local models).
**It contains no rules of its own.** The rules live in `PROJECT_RULES.md`.

## Before doing anything
1. Read `PROJECT_RULES.md` — the canonical rules. Follow them exactly.
2. Read `management/STATUS.md` and `management/TASKS.md`.
3. Work only on a task assigned to you (look for `ASSIGNED TO: Codex` or the name the owner gave you).
4. No assigned task → do nothing and ask the owner.

## Key reminders
- This folder is the single source of truth, shared with Claude and the owner. Write important things in files.
- Until Git is set up, only one tool edits at a time. Claim the task (`IN_PROGRESS`) before editing.
- Only edit files listed in your task's `FILES OWNED`.
- Never claim tests passed without running them.
- End of session: update `management/TASKS.md`, `management/STATUS.md`, and add a handoff in `management/HANDOFFS.md`.

## For tools that don't auto-load this file (e.g. local LLMs)
Paste this as the first message: "Read AGENTS.md and PROJECT_RULES.md in this folder and follow them. Your task is T-xxx in management/TASKS.md."

## Team rules you must follow (details: PROJECT_RULES.md §12)
- Don't blindly agree with the owner or other agents — research, give an honest view, discuss; decisions are final only when agreed and logged.
- To send something important to Claude (or another agent), end your reply with a `📨 MESSAGE FOR Claude` block for the owner to copy-paste. Only when important.
