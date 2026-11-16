#!/usr/bin/env bash
OUT="dump-backend.txt"
: > "$OUT"

add() {
  [ -f "$1" ] && { echo; echo "=================== $1 ==================="; cat "$1"; } >> "$OUT"
}

add pom.xml
add .gitignore
add .openapi-generator-ignore

find src/main/resources -maxdepth 1 -type f \
  \( -name "application*.yml" -o -name "application*.yaml" -o -name "application*.properties" \) \
  | sort | while read -r f; do add "$f"; done

find src/main/java/sn -type f -name "*.java" | sort | while read -r f; do add "$f"; done
add src/main/java/org/openapitools/BackendApplication.java

find src/test -type f -name "*.java" 2>/dev/null | sort | while read -r f; do add "$f"; done

echo "Généré : $OUT ($(wc -l < "$OUT") lignes)"
grep '^=================== ' "$OUT" | sed 's/=//g'
