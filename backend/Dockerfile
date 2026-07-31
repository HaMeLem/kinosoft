# Multi-Stage-Build des Backends direkt aus dem Quellcode.
#
# Dies ist der einzige Weg, das Backend-Image zu bauen: Er wird von
# docker-compose (build:-Block) genutzt und baut das Spring-Boot-Backend
# vollstaendig im Container - es wird KEIN JDK/Maven auf dem Host benoetigt.
#
# Build-Context ist das backend/-Verzeichnis (siehe docker-compose.yml); das
# Backend ist ein eigenstaendiges Maven-Projekt mit eigenem Wrapper.

# ---- Stage 1: Build ----
FROM maven:3.9.11-eclipse-temurin-25 AS build
WORKDIR /workspace

# Maven-Wrapper und POM zuerst kopieren (bessere Layer-Cache-Nutzung).
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Abhaengigkeiten vorab aufloesen (wird gecacht, solange sich das POM nicht aendert).
RUN ./mvnw -q dependency:go-offline -DskipTests

# Quellcode kopieren und bauen (Tests ueberspringen). Das repackage-Goal ist ueber
# den spring-boot-starter-parent an die package-Phase gebunden und erzeugt das
# ausfuehrbare Fat-JAR fuer `java -jar`.
COPY src src
RUN ./mvnw clean package -DskipTests

# ---- Stage 2: Runtime ----
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /workspace/target/*.jar app.jar

# Als nicht-privilegierten Nutzer laufen (kein root im Container). Das JAR wird
# nur gelesen; die H2-DB liegt in-memory, daher werden keine beschreibbaren
# Verzeichnisse benoetigt. Port 8080 ist unprivilegiert (>1024).
RUN groupadd --system spring \
 && useradd --system --gid spring --no-create-home spring
USER spring:spring

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
