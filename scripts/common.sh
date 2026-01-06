#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
REPORT_DIR="${ROOT_DIR}/reports"
COMPOSE_FILE="${ROOT_DIR}/docker-compose.yml"

mkdir -p "${REPORT_DIR}"

log()  { echo -e "▶ $*"; }
warn() { echo -e "⚠ $*" >&2; }
die()  { echo -e "✖ $*" >&2; exit 1; }
