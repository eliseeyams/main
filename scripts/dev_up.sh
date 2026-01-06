#!/usr/bin/env bash
set -euo pipefail
source "$(dirname "$0")/common.sh"

log "Starting dev environment with Docker Compose…"
docker compose -f "${COMPOSE_FILE}" up -d --build
log "Done."
