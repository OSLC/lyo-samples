# Contributing — Lyo Client Samples

Thanks for contributing! Please all changes work against the latest SNAPSHOT of Eclipse Lyo.

## Before you submit

- Run `mvn test` in the affected module and make sure it builds and (where applicable)
  tests pass.
- Prefer snapshot-based tests when introducing changes needed to work well with some
  external OSLC system.
- Keep `DEVELOPMENT.md` (build/test instructions) and `AGENTS.md` (agent-specific
  conventions) in sync when you change the build or test setup in a significant way.

## Updating docs

- User-facing docs should go to the README.md
- Dev-facing docs (useful for both devs and agents) should go to the DEVELOPMENT.md
- Universal contribution docs go here (CONTRIBUTING.md)
- Agent-specific docs go to AGENTS.md
