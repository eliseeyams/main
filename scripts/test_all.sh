#!/usr/bin/env bash
set -euo pipefail
source "$(dirname "$0")/common.sh"

log "Running unit + integration tests…"
# Maven:
mvn -B -DskipTests=false test
# optional: falls du Integrationstests getrennt hast (failsafe):
mvn -B verify
log "All tests passed."
