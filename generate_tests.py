#!/usr/bin/env python3
"""
generate_tests.py : vérifie l'état des tests du backend et génère la suite MockMvc
(séances 1 et 2) en une seule commande.

Usage (depuis la racine du dépôt ou depuis backend/) :
    python3 generate_tests.py --check      # rapport seul, aucune écriture
    python3 generate_tests.py --dry-run    # montre ce qui serait créé
    python3 generate_tests.py              # crée les fichiers manquants (n'écrase rien)
    python3 generate_tests.py --force      # écrase les fichiers existants (sauvegarde .bak)
"""
import argparse
import re
import sys
from pathlib import Path

# ----------------------------------------------------------------------------
# Contenu des fichiers générés
# ----------------------------------------------------------------------------

HEADER = '''@SpringBootTest(classes = BackendApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")'''

FILES = {}

FILES["src/test/resources/application-test.yml"] = '''spring:
  datasource:
    url: "jdbc:sqlite::memory:"
    hikari:
      maximum-pool-size: 1
      max-lifetime: 0          # une base en mémoire disparaît avec sa connexion
app:
  rate-limit:
    max-requests-per-minute: 1000
  security:
    bootstrap-key: test-key
'''

FILES["src/test/java/sn/uadb/TestSupport.java"] = '''package sn.uadb;

import org.springframework.test.web.servlet.request.RequestPostProcessor;

public final class TestSupport {

    public static final String API_KEY = "test-key";
    public static final String API_KEY_HEADER = "X-API-Key";

    private TestSupport() {}

    /** Simule un appel venant d'une adresse IP donnée (le rate limiter compte par IP). */
    public static RequestPostProcessor fromIp(String ip) {
        return request -> {
            request.setRemoteAddr(ip);
            return request;
        };
    }
}
'''

FILES["src/test/java/sn/uadb/filter/RequestIdFilterTest.java"] = '''package sn.uadb.filter;

import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sn.uadb.TestSupport.fromIp;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.openapitools.BackendApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

''' + HEADER + '''
class RequestIdFilterTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void generatesUuidWhenHeaderIsAbsent() throws Exception {
        String id = mockMvc.perform(get("/api/hello").with(fromIp("10.1.0.1")))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Request-Id"))
                .andReturn().getResponse().getHeader("X-Request-Id");

        assertDoesNotThrow(() -> UUID.fromString(id));
    }

    @Test
    void keepsValidClientProvidedId() throws Exception {
        mockMvc.perform(get("/api/hello").with(fromIp("10.1.0.2")).header("X-Request-Id", "test-123"))
                .andExpect(header().string("X-Request-Id", "test-123"));
    }

    @Test
    void replacesInvalidClientProvidedId() throws Exception {
        mockMvc.perform(get("/api/hello").with(fromIp("10.1.0.3")).header("X-Request-Id", "bad id!"))
                .andExpect(header().string("X-Request-Id", not("bad id!")));
    }
}
'''

FILES["src/test/java/sn/uadb/filter/SecurityHeadersFilterTest.java"] = '''package sn.uadb.filter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static sn.uadb.TestSupport.fromIp;

import org.junit.jupiter.api.Test;
import org.openapitools.BackendApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

''' + HEADER + '''
class SecurityHeadersFilterTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void addsSecurityHeaders() throws Exception {
        mockMvc.perform(get("/api/hello").with(fromIp("10.3.0.1")))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(header().string("X-Frame-Options", "DENY"))
                .andExpect(header().string("Cache-Control", "no-store"));
    }

    @Test
    void hstsIsAbsentOverPlainHttp() throws Exception {
        mockMvc.perform(get("/api/hello").with(fromIp("10.3.0.2")))
                .andExpect(header().doesNotExist("Strict-Transport-Security"));
    }

    @Test
    void hstsIsPresentOverHttps() throws Exception {
        mockMvc.perform(get("/api/hello").with(fromIp("10.3.0.3")).secure(true))
                .andExpect(header().exists("Strict-Transport-Security"));
    }
}
'''

FILES["src/test/java/sn/uadb/filter/MaintenanceFilterTest.java"] = '''package sn.uadb.filter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sn.uadb.TestSupport.fromIp;

import org.junit.jupiter.api.Test;
import org.openapitools.BackendApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = BackendApplication.class, properties = "app.maintenance.enabled=true")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MaintenanceFilterTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void returns503JsonWhenMaintenanceIsEnabled() throws Exception {
        mockMvc.perform(get("/api/hello").with(fromIp("10.4.0.1")))
                .andExpect(status().isServiceUnavailable())
                .andExpect(header().string("Retry-After", "300"))
                .andExpect(jsonPath("$.error").value(503))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }
}
'''

FILES["src/test/java/sn/uadb/filter/ApiKeyFilterTest.java"] = '''package sn.uadb.filter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sn.uadb.TestSupport.API_KEY;
import static sn.uadb.TestSupport.API_KEY_HEADER;
import static sn.uadb.TestSupport.fromIp;

import org.junit.jupiter.api.Test;
import org.openapitools.BackendApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import sn.uadb.repository.ApiKeyRepository;

''' + HEADER + '''
class ApiKeyFilterTest {

    private static final String URL = "/api/v1/students/2300001";

    @Autowired MockMvc mockMvc;
    @Autowired ApiKeyRepository apiKeyRepository;
    @Autowired JdbcClient jdbc;

    @Test
    void missingKeyGives401() throws Exception {
        mockMvc.perform(get(URL).with(fromIp("10.5.0.1")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value(401))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    void wrongKeyGives401() throws Exception {
        mockMvc.perform(get(URL).with(fromIp("10.5.0.2")).header(API_KEY_HEADER, "mauvaise"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value(401));
    }

    @Test
    void validKeyGives200() throws Exception {
        mockMvc.perform(get(URL).with(fromIp("10.5.0.3")).header(API_KEY_HEADER, API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matricule").value("2300001"));
    }

    @Test
    void helloEndpointIsPublic() throws Exception {
        mockMvc.perform(get("/api/hello").with(fromIp("10.5.0.4")))
                .andExpect(status().isOk());
    }

    @Test
    void revokedKeyIsRejectedImmediately() throws Exception {
        apiKeyRepository.insertIfAbsent("cle-a-revoquer", "revoked-owner");

        mockMvc.perform(get(URL).with(fromIp("10.5.0.5")).header(API_KEY_HEADER, "cle-a-revoquer"))
                .andExpect(status().isOk());

        jdbc.sql("UPDATE api_key SET active = 0 WHERE owner = :o").param("o", "revoked-owner").update();

        mockMvc.perform(get(URL).with(fromIp("10.5.0.5")).header(API_KEY_HEADER, "cle-a-revoquer"))
                .andExpect(status().isUnauthorized());
    }
}
'''

FILES["src/test/java/sn/uadb/filter/RateLimitingFilterTest.java"] = '''package sn.uadb.filter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sn.uadb.TestSupport.fromIp;

import org.junit.jupiter.api.Test;
import org.openapitools.BackendApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = BackendApplication.class, properties = "app.rate-limit.max-requests-per-minute=4")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RateLimitingFilterTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void fifthRequestInTheSameMinuteIsRejected() throws Exception {
        for (int i = 1; i <= 4; i++) {
            mockMvc.perform(get("/api/hello").with(fromIp("10.6.0.1")))
                    .andExpect(status().isOk());
        }

        String retryAfter = mockMvc.perform(get("/api/hello").with(fromIp("10.6.0.1")))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().exists("Retry-After"))
                .andExpect(jsonPath("$.error").value(429))
                .andExpect(jsonPath("$.message").value("Rate limit exceeded. Please try again later."))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andReturn().getResponse().getHeader("Retry-After");

        long seconds = Long.parseLong(retryAfter);
        assertTrue(seconds >= 1 && seconds <= 60, "Retry-After doit être entre 1 et 60 s, reçu " + seconds);
    }

    @Test
    void limitIsPerIpAddress() throws Exception {
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/api/hello").with(fromIp("10.6.0.2")));
        }
        // La première IP est bloquée, une autre ne l'est pas
        mockMvc.perform(get("/api/hello").with(fromIp("10.6.0.3")))
                .andExpect(status().isOk());
    }
}
'''

FILES["src/test/java/sn/uadb/controller/StudentsApiTest.java"] = '''package sn.uadb.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sn.uadb.TestSupport.API_KEY;
import static sn.uadb.TestSupport.API_KEY_HEADER;
import static sn.uadb.TestSupport.fromIp;

import org.junit.jupiter.api.Test;
import org.openapitools.BackendApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

''' + HEADER + '''
class StudentsApiTest {

    @Autowired
    MockMvc mockMvc;

    private ResultActions call(String matricule, String ip) throws Exception {
        return mockMvc.perform(get("/api/v1/students/{m}", matricule)
                .with(fromIp(ip)).header(API_KEY_HEADER, API_KEY));
    }

    @Test
    void housedStudentIsReturnedWithAccommodation() throws Exception {
        call("2300001", "10.7.0.1")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastname").value("Diop"))
                .andExpect(jsonPath("$.ufr").value("SATIC"))
                .andExpect(jsonPath("$.logementCrous.pavillon").value("B"))
                .andExpect(jsonPath("$.logementCrous.numeroChambre").value("102"))
                .andExpect(jsonPath("$.logementCrous.lit").value("A"));
    }

    @Test
    void studentWithoutAccommodationHasNoLogement() throws Exception {
        call("2200003", "10.7.0.2")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matricule").value("2200003"))
                .andExpect(jsonPath("$.logementCrous").doesNotExist());
    }

    @Test
    void unknownStudentGives404InUniformFormat() throws Exception {
        call("9999999", "10.7.0.3")
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(404))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    void invalidMatriculeGives400InUniformFormat() throws Exception {
        call("abc", "10.7.0.4")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(400))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    void sqlInjectionAttemptIsRejectedAsInvalidFormat() throws Exception {
        call("1' OR '1'='1", "10.7.0.5")
                .andExpect(status().isBadRequest());
    }
}
'''

# ----------------------------------------------------------------------------
# Contrôles
# ----------------------------------------------------------------------------

PROD_CLASSES = [
    "src/main/java/org/openapitools/BackendApplication.java",
    "src/main/java/sn/uadb/config/FilterConfig.java",
    "src/main/java/sn/uadb/error/ErrorResponseWriter.java",
    "src/main/java/sn/uadb/filter/RequestIdFilter.java",
    "src/main/java/sn/uadb/filter/RequestLoggingFilter.java",
    "src/main/java/sn/uadb/filter/SecurityHeadersFilter.java",
    "src/main/java/sn/uadb/filter/MaintenanceFilter.java",
    "src/main/java/sn/uadb/filter/RateLimitingFilter.java",
    "src/main/java/sn/uadb/filter/ApiKeyFilter.java",
    "src/main/java/sn/uadb/repository/ApiKeyRepository.java",
    "src/main/java/sn/uadb/repository/EtudiantRepository.java",
    "src/main/java/sn/uadb/controller/ApiController.java",
    "src/main/java/sn/uadb/controller/ApiExceptionHandler.java",
]

POM_DEPENDENCIES = ["spring-boot-starter-test", "spring-boot-starter-webmvc-test"]

VALIDATION_HANDLER_HINT = """\
    @ExceptionHandler({HandlerMethodValidationException.class, ConstraintViolationException.class})
    public ResponseEntity<ApiError> handleValidation(Exception ex) {
        ApiError err = new ApiError();
        err.setError(HttpStatus.BAD_REQUEST.value());
        err.setMessage("Format de matricule invalide (doit comporter exactement 7 chiffres).");
        err.setTimestamp(OffsetDateTime.now());
        return ResponseEntity.badRequest().body(err);
    }"""


def find_backend(start: Path) -> Path:
    """Trouve le dossier backend (celui qui contient pom.xml et src/main/java)."""
    candidates = [start, start / "backend"] + list(start.parents)
    for c in candidates:
        if (c / "pom.xml").is_file() and (c / "src" / "main" / "java").is_dir():
            return c
    sys.exit("Erreur : pom.xml introuvable. Lancez le script depuis la racine du dépôt ou depuis backend/.")


def count_tests(path: Path) -> int:
    return len(re.findall(r"^\s*@Test\b", path.read_text(encoding="utf-8"), re.MULTILINE))


def report_existing_tests(backend: Path) -> dict:
    test_dir = backend / "src" / "test" / "java"
    existing = {}
    print("== Tests existants dans le projet ==")
    if not test_dir.is_dir():
        print("  Aucun dossier src/test/java.")
        return existing
    java_files = sorted(test_dir.rglob("*.java"))
    if not java_files:
        print("  Dossier présent, mais aucun fichier de test.")
    total = 0
    for f in java_files:
        n = count_tests(f)
        total += n
        rel = f.relative_to(backend).as_posix()
        existing[rel] = n
        print(f"  {rel}  ({n} test(s))")
    if java_files:
        print(f"  Total : {total} test(s) dans {len(java_files)} fichier(s).")
    return existing


def check_prerequisites(backend: Path) -> None:
    print("\n== Prérequis ==")
    pom = (backend / "pom.xml").read_text(encoding="utf-8")
    for dep in POM_DEPENDENCIES:
        print(f"  [{'OK' if dep in pom else 'MANQUE'}] dépendance {dep} dans pom.xml")
    for dep in ("spring-boot-starter-jdbc", "sqlite-jdbc"):
        print(f"  [{'OK' if dep in pom else 'MANQUE'}] dépendance {dep} dans pom.xml")

    missing = [c for c in PROD_CLASSES if not (backend / c).is_file()]
    if missing:
        print("  Classes de production absentes (les tests correspondants ne compileront pas) :")
        for m in missing:
            print(f"    - {m}")
    else:
        print("  [OK] toutes les classes de production attendues sont présentes")

    handler = backend / "src/main/java/sn/uadb/controller/ApiExceptionHandler.java"
    if handler.is_file():
        text = handler.read_text(encoding="utf-8")
        if "HandlerMethodValidationException" not in text and "ConstraintViolationException" not in text:
            print("\n  ATTENTION : ApiExceptionHandler ne gère pas les erreurs de validation (@Pattern).")
            print("  Le test 400 (matricule 'abc') risque d'échouer. Handler à ajouter :\n")
            print(VALIDATION_HANDLER_HINT)
            print("\n  Imports : jakarta.validation.ConstraintViolationException,")
            print("            org.springframework.http.HttpStatus,")
            print("            org.springframework.web.method.annotation.HandlerMethodValidationException")


def main() -> int:
    ap = argparse.ArgumentParser(description="Vérifie et génère les tests du backend.")
    mode = ap.add_mutually_exclusive_group()
    mode.add_argument("--check", action="store_true", help="rapport seul, aucune écriture")
    mode.add_argument("--dry-run", action="store_true", help="affiche ce qui serait écrit")
    ap.add_argument("--force", action="store_true", help="écrase les fichiers existants (sauvegarde .bak)")
    args = ap.parse_args()

    backend = find_backend(Path.cwd())
    print(f"Backend détecté : {backend}\n")

    existing = report_existing_tests(backend)
    check_prerequisites(backend)

    print("\n== Fichiers de la suite ==")
    to_write, skipped, identical = [], [], []
    for rel, content in FILES.items():
        target = backend / rel
        if target.is_file():
            if target.read_text(encoding="utf-8") == content:
                identical.append(rel)
                print(f"  [identique]  {rel}")
            elif args.force:
                to_write.append(rel)
                print(f"  [à écraser]  {rel}")
            else:
                skipped.append(rel)
                print(f"  [existe]     {rel}  (différent, conservé ; --force pour écraser)")
        else:
            to_write.append(rel)
            print(f"  [manquant]   {rel}")

    if args.check:
        print(f"\nRésumé : {len(identical)} identique(s), {len(skipped)} différent(s), "
              f"{sum(1 for r in to_write if not (backend / r).is_file())} manquant(s).")
        return 1 if to_write else 0

    if args.dry_run:
        print(f"\nDry-run : {len(to_write)} fichier(s) seraient écrits, rien n'a été modifié.")
        return 0

    written = 0
    for rel in to_write:
        target = backend / rel
        target.parent.mkdir(parents=True, exist_ok=True)
        if target.is_file():
            target.replace(target.with_suffix(target.suffix + ".bak"))
        target.write_text(FILES[rel], encoding="utf-8", newline="\n")
        written += 1
        print(f"  écrit : {rel}")

    print(f"\nTerminé : {written} fichier(s) écrit(s), {len(skipped)} conservé(s), {len(identical)} déjà à jour.")
    if written:
        print("\nÉtapes suivantes :")
        print("  cd " + str(backend))
        print("  mvn test")
        print("\nPensez à ajouter '*.bak' au .gitignore si vous avez utilisé --force.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
