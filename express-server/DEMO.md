Excellent ! Maintenant que **GripMock est démarré et que son interface Web est accessible**, nous allons tester le service `greeter.Greeter/SayHello` directement depuis l'interface.

## 1. Accéder à l'interface GripMock

Ouvre dans ton navigateur :

[http://localhost:4771](http://localhost:4771)

L'interface permet de gérer les stubs (réponses simulées) et de consulter les requêtes reçues.

## 2. Créer un stub pour SayHello

Dans l'interface, ouvre la section **Stubs**, puis utilise l'option de création d'un stub, si elle est disponible.

Renseigne les informations suivantes :

| Champ         | Valeur                        |
| ------------- | ----------------------------- |
| Service       | `greeter.Greeter`             |
| Method        | `SayHello`                    |
| Input matcher | `equals`                      |
| Request       | `{"name":"Maodo"}`            |
| Response      | `{"message":"Hello, Maodo!"}` |

Si l'interface propose un éditeur JSON, utilise ce contenu :

```

{
  "service": "greeter.Greeter",
  "method": "SayHello",
  "input": {
    "equals": {
      "name": "Maodo"
    }
  },
  "output": {
    "data": {
      "message": "Hello, Maodo!"
    }
  }
}
```

Enregistre le stub.

## 3. Exécuter le test

L'interface GripMock sert principalement à configurer et observer les simulations. Selon la version, elle peut ne pas proposer de bouton permettant d'invoquer directement une méthode gRPC.

Pour déclencher le test, utilise alors un deuxième terminal :

```
grpcurl -plaintext \
  -d '{"name":"Maodo"}' \
  localhost:4770 \
  greeter.Greeter/SayHello
```

Résultat attendu :

```
{
  "message": "Hello, Maodo!"
}
```

Retourne ensuite dans l'interface Web et consulte la section des requêtes ou de l'historique, si elle est proposée, pour vérifier que l'appel a été reçu et associé au stub.

## 4. Tester un autre scénario

Crée un deuxième stub avec `name = Khar` et la réponse `Hello, Khar!`, puis appelle :

```
grpcurl -plaintext \
  -d '{"name":"Khar"}' \
  localhost:4770 \
  greeter.Greeter/SayHello
```

Tu pourras ainsi vérifier que GripMock sélectionne la réponse en fonction du contenu de la requête.

**Important :** GripMock ne lance pas ton code JavaScript `sayHello()`. Il simule le service gRPC à partir des réponses que tu configures.

Si tu m'envoies une capture d'écran de l'interface GripMock v3.23.8, je pourrai te guider précisément dans les menus pour créer le stub et vérifier son exécution, sans passer par `grpcurl` si l'interface propose cette fonctionnalité.