# openapi-spring

## Scripts utils

```bash
for i in 1 2 3; do curl -i http://localhost:8080/api/etudiants/2300001; done

```
Recuper le code existant
```bash
cd /workspaces/cs-uadb-web-advanced-2026-2027/backend

cat > dump-backend.sh <<'EOF'
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
EOF

bash dump-backend.sh


cd backend
nano dump-backend.sh        # colle le script, puis enregistre
bash dump-backend.sh
```


Mode maintenance : 

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--app.maintenance.enabled=true"
curl -i http://localhost:8080/api/hello
# attendu : 503, Retry-After: 300, {"error":503,"message":"Service en maintenance...","timestamp":"..."}
```

Rate limiting à 2 requêtes par minute pour la démonstration :
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--app.rate-limit.max-requests-per-minute=2"
for i in 1 2 3; do curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8080/api/hello; done
# attendu : 200, 200, 429
```

Dependances : 

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.50.3.0</version> <!-- vérifie la dernière version sur Maven Central -->
</dependency>
```

Contrôler et relancer

```java
mvn dependency:tree | grep -i "jdbc\|hikari\|sqlite"
```

Ignorer la base dans les commits !

```bash
printf '\n# SQLite\n*.db\n*.db-journal\n*.db-wal\n*.db-shm\n' >> .gitignore
```

Jeux de tests dans la base : 

```bash
sqlite3 uadb.db "SELECT matricule, lastname, ufr FROM etudiant;"
sqlite3 uadb.db "INSERT INTO etudiant (matricule, lastname, firstname, ufr) VALUES ('2400001','Sow','Awa','SEFS');"
curl -s http://localhost:8080/api/v1/students/2400001
```

Quelques tests : 

```bash
# 1. Sans clé : 401 JSON
curl -i http://localhost:8080/api/v1/students/2300001

# 2. Mauvaise clé : 401
curl -i -H "X-API-Key: mauvaise" http://localhost:8080/api/v1/students/2300001

# 3. Bonne clé : 200
curl -i -H "X-API-Key: cle-demo-uadb-2026" http://localhost:8080/api/v1/students/2300001

# 4. Endpoint public : 200 sans clé
curl -i http://localhost:8080/api/hello

# 5. Erreur métier : même format JSON qu'au filtre
curl -s -H "X-API-Key: cle-demo-uadb-2026" http://localhost:8080/api/v1/students/9999999
```

Attendu : 
```json
{"error":401,"message":"Clé d'API manquante (en-tête X-API-Key).","timestamp":"2026-11-12T..."}
```


Contrôle de la base et révocation immédiate: 
```bash
sqlite3 uadb.db "SELECT id, owner, active FROM api_key;"      # on ne voit que le hash
sqlite3 uadb.db "UPDATE api_key SET active=0 WHERE owner='bootstrap';"
curl -i -H "X-API-Key: cle-demo-uadb-2026" http://localhost:8080/api/v1/students/2300001   # 401 aussitôt
```

API KEY

```bash
curl -i -H "X-API-Key: cle-demo-uadb-2026" http://localhost:8080/api/v1/students/9999999
```