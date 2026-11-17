# Récapitulatif technique — gRPC Node.js, Server Reflection et GripMock

Projet : `cs-uadb-web-advanced-2026-2027` · 16 novembre 2026 · État final : tests fonctionnels validés

Ce document récapitule les erreurs rencontrées, leurs causes, les corrections appliquées et les commandes de validation. Il peut être intégré directement à la documentation finale du projet.

## 1. Architecture finale

```
<svg viewBox="0 0 340 215" width="100%" xmlns="http://www.w3.org/2000/svg"><rect x="112" y="5" width="116" height="37" rx="8" fill="#64748b" fill-opacity=".14" stroke="#64748b" /><text x="170" y="20" font-size="12" font-weight="600" text-anchor="middle" fill="currentColor">grpcurl</text><text x="170" y="33" font-size="9" text-anchor="middle" fill="currentColor">Client de test</text><path d="M170 42 V66 M170 66 H81 V87 M170 66 H260 V87" fill="none" stroke="#64748b" stroke-width="1.5" /><path d="M77 82 L81 88 L85 82 M256 82 L260 88 L264 82" fill="none" stroke="#64748b" stroke-width="1.5" /><rect x="7" y="89" width="148" height="74" rx="8" fill="#0d9488" fill-opacity=".09" stroke="#0d9488" /><text x="81" y="108" text-anchor="middle" font-size="12" font-weight="600" fill="currentColor">Node.js gRPC</text><text x="81" y="126" text-anchor="middle" font-size="11" fill="currentColor">Port 50051</text><text x="81" y="145" text-anchor="middle" font-size="10" fill="currentColor">Implémentation réelle</text><rect x="185" y="89" width="148" height="74" rx="8" fill="#2563eb" fill-opacity=".09" stroke="#2563eb" /><text x="259" y="108" text-anchor="middle" font-size="12" font-weight="600" fill="currentColor">GripMock</text><text x="259" y="126" text-anchor="middle" font-size="11" fill="currentColor">gRPC : 4770</text><text x="259" y="145" text-anchor="middle" font-size="10" fill="currentColor">Réponses simulées</text><path d="M259 163 V179" stroke="#64748b" stroke-width="1.5" /><rect x="194" y="180" width="130" height="30" rx="7" fill="#64748b" fill-opacity=".12" stroke="#64748b" /><text x="259" y="199" text-anchor="middle" font-size="11" fill="currentColor">Web UI : 4771</text></svg>
```

Les deux serveurs utilisent le contrat `greeter.proto`, mais fonctionnent indépendamment.

## 2. Historique des erreurs et corrections

| N° | Erreur rencontrée                          | Cause identifiée                                                 | Correctif                                             |
| -- | ------------------------------------------ | ---------------------------------------------------------------- | ----------------------------------------------------- |
| 1  | `serverReflection is not a function`       | Fonction inexistante dans l'API du package installé              | Utiliser `addReflection()`                            |
| 2  | `Invalid wire type`                        | Passage d'un `.proto` texte au lieu d'un descriptor binaire      | Générer `greeter.pb` avec `protoc`                    |
| 3  | `ERR_INVALID_ARG_VALUE`                    | Passage d'un `Buffer` alors que la bibliothèque attend un chemin | Utiliser `addReflection(server, DESCRIPTOR_PATH)`     |
| 4  | `Symbol not found` avec `grpcurl describe` | Slash `/` utilisé au lieu du point `.`                           | `describe greeter.Greeter.SayHello`                   |
| 5  | `buf.build 404 Not Found`                  | Chemin local interprété comme une référence distante             | Utiliser le chemin du fichier dans le conteneur       |
| 6  | `open /proto/greeter.proto: no such file`  | Volume Docker pointant vers un mauvais répertoire                | Monter le répertoire `contrats` avec un chemin absolu |
| 7  | `No matching stub found`                   | Aucun stub ne correspond à la requête                            | Créer un stub `input.equals`                          |
| 8  | Réponse `{}`                               | Réponse simulée vide ou mal configurée                           | Vérifier `output.data.message`                        |
| 9  | `Closest match` incorrect                  | Configuration complète du stub insérée dans `input.equals`       | Séparer les champs Input et Output dans l'UI          |

## 3. Configuration finale du serveur Node.js

Le fichier `express-server/grpc-server.js` utilise les bibliothèques :

- `@grpc/grpc-js` pour le serveur gRPC ;
- `@grpc/proto-loader` pour charger le contrat ;
- `grpc-server-reflection` pour exposer les descriptors.

La correction essentielle est :

```
const { addReflection } = require('grpc-server-reflection');

addReflection(server, DESCRIPTOR_PATH);
```

Le descriptor est généré avec :

```
cd ~/cs-uadb-web-advanced-2026-2027/express-server

mkdir -p descriptors

protoc \
  --proto_path=../contrats \
  --include_imports \
  --descriptor_set_out=descriptors/greeter.pb \
  greeter.proto
```

Démarrage :

```
node grpc-server.js
```

Port utilisé : `50051`.

## 4. Configuration finale de GripMock

Commande Docker fonctionnelle, avec le chemin du projet utilisé pendant les tests :

```
docker run --rm \
  --name gripmock \
  -p 4770:4770 \
  -p 4771:4771 \
  --mount type=bind,source=/home/dmaodo/cs-uadb-web-advanced-2026-2027/contrats,target=/proto,readonly \
  bavix/gripmock:v3.23.8 \
  /proto/greeter.proto
```

L'interface de gestion est accessible à l'adresse localhost:4771.

### Configuration du stub validé

Dans l'interface, les champs doivent être séparés :

**Input → Equals**

```
{
  "name": "Khar"
}
```

**Output → Data**

```
{
  "message": "Hello, Khar!"
}
```

La configuration complète d'un stub est :

```
{
  "service": "greeter.Greeter",
  "method": "SayHello",
  "input": {
    "equals": {
      "name": "Khar"
    }
  },
  "output": {
    "data": {
      "message": "Hello, Khar!"
    }
  }
}
```

Ce JSON complet est adapté à un fichier de stub ou à un éditeur qui accepte la configuration entière, mais pas au seul champ `Input → Equals`.

## 5. Tests de validation

### Test du véritable serveur gRPC

```
grpcurl -plaintext \
  -d '{"name":"Maodo"}' \
  localhost:50051 \
  greeter.Greeter/SayHello
```

Résultat obtenu :

```
{
  "message": "Hello, Maodo!"
}
```

### Test du serveur GripMock

```
grpcurl -plaintext \
  -d '{"name":"Khar"}' \
  localhost:4770 \
  greeter.Greeter/SayHello
```

Résultat final obtenu :

```
{
  "message": "Hello, Khar!"
}
```

### Introspection du service

```
grpcurl -plaintext localhost:50051 list

grpcurl -plaintext localhost:50051 describe greeter.Greeter

grpcurl -plaintext localhost:50051 describe greeter.Greeter.SayHello
```

La commande `describe` utilise des points pour les noms Protobuf, tandis que l'invocation RPC utilise un slash entre le service et la méthode.

## 6. Bilan de la recette

Tests fonctionnels finaux

# Validés

---

Serveur Node.js — `SayHello` (Réussi)

Reflection et découverte gRPC (Réussi)

GripMock et interface Web (Réussi)

Stub `Khar` et réponse simulée (Réussi)

## 7. Recommandations pour finaliser la documentation

Pour rendre le projet facilement reproductible par les étudiants, il reste pertinent de :

1. Ajouter un `README.md` présentant l'installation, les ports, les commandes et les tests.
2. Versionner le contrat `contrats/greeter.proto` et les stubs JSON.
3. Ajouter un fichier `compose.yaml` pour lancer GripMock sans ressaisir la commande Docker.
4. Prévoir une commande de génération automatique du descriptor après toute modification du `.proto`.
5. Ajouter un test automatisé vérifiant la réponse `Hello, Khar!`.

**Conclusion :** le parcours a permis de valider deux approches complémentaires : l'implémentation réelle d'un service gRPC avec Node.js et la simulation de ce même contrat avec GripMock. Les erreurs rencontrées constituent également des cas pédagogiques utiles pour comprendre les descriptors Protobuf, la réflexion gRPC, les volumes Docker et les règles de correspondance des stubs.

Ce récapitulatif peut constituer la section « Dépannage et validation » du README final.