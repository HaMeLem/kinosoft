# Multi-Stage-Build des Frontends direkt aus dem Quellcode.
#
# Dies ist der einzige Weg, das Frontend-Image zu bauen: Er wird von
# docker-compose (build:-Block) genutzt und baut das Angular-Frontend
# vollstaendig im Container - es wird KEIN Node/npm auf dem Host benoetigt.
#
# Build-Context ist das frontend/-Verzeichnis (siehe docker-compose.yml).

# ---- Stage 1: Build ----
FROM node:24.11.0 AS build
WORKDIR /app

# Manifeste zuerst fuer besseres Layer-Caching kopieren.
COPY package.json package-lock.json ./
RUN npm ci

# Quellcode kopieren und produktiv bauen.
COPY . .
RUN npm run build

# ---- Stage 2: Runtime (nginx, nicht-privilegiert) ----
# nginx-unprivileged laeuft als Nutzer "nginx" (uid 101), nicht als root, und
# hat beschreibbare Temp-/PID-Pfade vorkonfiguriert. Als Nicht-root kann nginx
# Port 80 nicht binden, daher lauscht der Server auf 8080 (siehe nginx.conf);
# docker-compose mappt das nach aussen auf 8081.
FROM nginxinc/nginx-unprivileged:stable-alpine
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html
COPY nginx/nginx.conf /etc/nginx/conf.d/default.conf
