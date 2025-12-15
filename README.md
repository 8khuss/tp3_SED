
# Hautlesbas
=======
TP1_SED
FAIT PAR HOCINE ACHOUR
420-337-AL_A25
(DEBUT: MARDI 7 OCTOBRE)
(FIN: LUNDI 13 OCTOBRE)

L'UTILISATION DE MAVEN:
Maven gère le build et les dépendances via pom.xml.

mvn -v                 # vérifier l’installation
mvn clean compile      # compiler
mvn test               # exécuter les tests (si présents)
mvn package            # créer le JAR dans target/



LANCER L'APPLICATION
Le serveur HTTP (Java com.sun.net.httpserver) démarre sur http://localhost:8080

REQUETES (CHAUSSETTES)
| Méthode | Chemin                            | Description                            |
| ------: | --------------------------------- | -------------------------------------- |
|     GET | `/api/chaussettes`                | Lister toutes les chaussettes          |
|     GET | `/api/chaussettes?identifiant=ID` | Obtenir une chaussette par identifiant |
|    POST | `/api/chaussettes`                | Ajouter une chaussette                 |
|     PUT | `/api/chaussettes?identifiant=ID` | Modifier une chaussette                |
|  DELETE | `/api/chaussettes?identifiant=ID` | Supprimer une chaussette               |

EXEMPLE SCHEMA JSON(POST/PUT)
{
    "couleur": "noir",
    "taille": "M",
    "typeTissu": "coton",
    "prix": 7.5
}

REQUETES (VENTES)
| Méthode | Chemin                          | Description                                                 |
| ------: | ------------------------------- | ----------------------------------------------------------- |
|     GET | `/api/ventes`                   | Lister toutes les ventes                                    |
|    POST | `/api/ventes`                   | Créer une vente avec des IDs de chaussettes disponibles     |
|    POST | `/api/ventes/{idVente}/ajouter` | Ajouter des chaussettes à une vente existante               |
|  DELETE | `/api/ventes/{idVente}`         | Annuler une vente (remet les chaussettes dans l’inventaire) |

EXEMPLE SCHEMA JSON(POST/PUT)
{
"ids": [1, 2, 3]
}
>>>>>>> 859d5ae (Fin du tp1)
