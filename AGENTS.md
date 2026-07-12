# AGENTS.md — Lyo Client Samples

Follow human-facing docs: `DEVELOPMENT.md` (build/run/test) and `CONTRIBUTING.md` (rules).

Agent-specific notes:

- In `lyo-client-samples`, `rewrite-maven-plugin:apply` is bound to the `validate`
  phase and rewrites sources in place. When running tests, skip it to avoid
  unintended edits to the working tree:
      mvn test -Drewrite.skip=true
