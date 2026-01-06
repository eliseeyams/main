#!/usr/bin/env bash
set -euo pipefail
source "$(dirname "$0")/common.sh"

log "Security scans → output to ${REPORT_DIR}"

log "1) Secret scan (gitleaks)…"
docker run --rm -v "${ROOT_DIR}:/repo" -w /repo zricethezav/gitleaks:latest \
  detect --source=/repo --report-format sarif --report-path /repo/reports/gitleaks.sarif || true

log "2) Vulnerability + config scan (trivy fs)…"
docker run --rm -v "${ROOT_DIR}:/repo" -w /repo aquasec/trivy:latest \
  fs --format sarif --output /repo/reports/trivy-fs.sarif /repo || true

log "3) Dependency vulnerabilities (OWASP dependency-check)…"
docker run --rm -v "${ROOT_DIR}:/src" -v "${REPORT_DIR}:/report" owasp/dependency-check:latest \
  --scan /src --format "ALL" --out /report || true

log "Done. Reports in ./reports"
warn "Hinweis: Scans returnen hier absichtlich nicht-fatal (|| true), damit du Reports immer bekommst."
