#!/usr/bin/env bash
set -euo pipefail
TMP=$(mktemp -d)
java -jar openapi-generator-cli.jar generate \
  -i contrats.json \
  -g spring \
  --library spring-boot \
  --additional-properties=interfaceOnly=true,useSpringBoot3=true,skipDefaultInterface=true \
  -o "$TMP"

DEST=backend/src/main/java/org/openapitools
rm -rf "$DEST/api" "$DEST/model"
cp -r "$TMP/src/main/java/org/openapitools/api"   "$DEST/"
cp -r "$TMP/src/main/java/org/openapitools/model" "$DEST/"
rm -rf "$TMP"
echo "Régénéré : api + model"
