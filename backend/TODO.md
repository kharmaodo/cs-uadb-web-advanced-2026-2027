## 1. Filtres utiles 

Par ordre de difficulté et d'intérêt pédagogique.

**Niveau 1 : faciles, effet visible immédiat**

| Filtre | Ce qu'il apprend |
|---|---|
| **Logging des requêtes** (méthode, URI, statut, durée en ms) | Cycle `avant / chain.doFilter / après` |
| **Request ID / Correlation ID** : génère un UUID, le met dans le MDC et dans l'en-tête `X-Request-Id` de la réponse | Traçabilité, MDC SLF4J |
| **En-têtes de sécurité** : `X-Content-Type-Options: nosniff`, `X-Frame-Options: DENY`, `Cache-Control: no-store`, `Strict-Transport-Security` | Bonnes pratiques OWASP |
| **Mode maintenance** : renvoie 503 JSON si un flag de configuration est actif | Lecture de `@Value`, réponse d'erreur personnalisée |

**Niveau 2 : sécurité de l'API (très cohérent avec ton contrat)**

| Filtre | Ce qu'il apprend |
|---|---|
| **Authentification par clé d'API** (`X-API-Key`), 401 JSON sinon | Sécuriser sans Spring Security, lien avec `securitySchemes` d'OpenAPI |
| **Liste blanche / noire d'IP** | Contrôle d'accès réseau |
| **Limite de taille du body** (413 si trop gros) | Protection contre les abus |
| **Validation des en-têtes** (`Content-Type`, `Accept`) : 415 ou 406 | Respect du contrat HTTP |
| **CORS** via `CorsFilter` ou `WebMvcConfigurer` | Problème classique avec un frontend séparé |

**Niveau 3 : avancés**

| Filtre | Ce qu'il apprend |
|---|---|
| **Authentification JWT** (extraction, validation, 401/403) | Passage naturel vers Spring Security |
| **Rate limiting par clé d'API ou utilisateur** avec la bibliothèque **Bucket4j** (token bucket) | Comparer avec leur version artisanale |
| **Idempotence** (`Idempotency-Key` pour les POST) | Fiabilité des API |
| **Cache / ETag** (`ShallowEtagHeaderFilter`, 304) | Performance HTTP |
| **Audit** (qui a appelé quoi, quand) | Traçabilité, lien avec une base de données |

**Concepts à faire distinguer :**

- **Filter** (Servlet) : bas niveau, agit sur toute requête HTTP, avant Spring MVC.
- **HandlerInterceptor** : niveau Spring MVC, a accès au contrôleur ciblé (`preHandle`, `postHandle`).
- **@RestControllerAdvice** : gestion centralisée des exceptions levées par les contrôleurs.
- **Ordre des filtres** : `setOrder()`. Par exemple Request ID, puis logging, puis rate limiting, puis authentification.

**Atelier possible en 3 séances :**
1. Request ID + logging + en-têtes de sécurité (niveau 1).
2. Clé d'API + format d'erreur uniforme (JSON `{error, message, timestamp}` partout, filtres et contrôleurs).
3. Rate limiting par clé d'API avec Bucket4j, puis mise à jour du contrat OpenAPI (réponses 401 et 429 documentées, en-tête `Retry-After`).

