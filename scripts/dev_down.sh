#!/usr/bin/env bash
set -euo pipefail
source "$(dirname "$0")/common.sh"

log "Stopping dev environment…"
docker compose -f "${COMPOSE_FILE}" down -v
log "Done."
