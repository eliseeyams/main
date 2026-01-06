#!/usr/bin/env bash
set -euo pipefail
source "$(dirname "$0")/common.sh"

log "CI-local: format/lint (optional) + tests + security scans"
"${ROOT_DIR}/scripts/test_all.sh"
"${ROOT_DIR}/scripts/security_scan.sh"
log "CI-local finished."
