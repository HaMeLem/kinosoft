#!/usr/bin/env bash
set -euo pipefail

compose_file="$(cd "$(dirname "$0")/.." && pwd)/docker-compose.yml"

docker compose -f "$compose_file" up --detach --wait postgres

docker compose -f "$compose_file" exec -T postgres \
  psql -U postgres -d postgres -c \
  "SELECT pg_terminate_backend(pid)
   FROM pg_stat_activity
   WHERE datname = 'kinosoft_test' AND pid <> pg_backend_pid();"

docker compose -f "$compose_file" exec -T postgres \
  psql -U postgres -d postgres -c "DROP DATABASE IF EXISTS kinosoft_test;"

docker compose -f "$compose_file" exec -T postgres \
  psql -U postgres -d postgres -c "CREATE DATABASE kinosoft_test;"

docker compose -f "$compose_file" exec -T postgres \
  psql -U postgres -d kinosoft_test -c \
  "CREATE SCHEMA filmauswahl;
   CREATE SCHEMA kartenverkauf;
   CREATE SCHEMA einlasskontrolle;
   CREATE SCHEMA nutzerverwaltung;"
