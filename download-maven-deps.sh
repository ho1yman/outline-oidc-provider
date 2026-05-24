#!/bin/bash
set -e
cd "$(dirname "$0")"

echo "==> Pre-downloading Maven dependencies to ./maven-cache ..."

# Create empty frontend/dist to satisfy pom.xml resource reference
mkdir -p frontend/dist

docker run --rm \
  -v "$(pwd)/backend/pom.xml:/app/pom.xml:ro" \
  -v "$(pwd)/maven-cache:/root/.m2" \
  maven:3.9-eclipse-temurin-17 \
  sh -c "cd /app && mvn dependency:go-offline -B"

echo "==> Done. Maven dependencies cached in ./maven-cache"
