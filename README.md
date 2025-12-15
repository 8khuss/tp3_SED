
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

La base de données H2 sera automatiquement créée lors du premier lancement.

---

## 3. Documentation de l'API (Utilisation)

### A. Gestion des Chaussettes (`/api/chaussettes`)

#### 1. Ajouter une paire de chaussettes
* **Méthode :** `POST`
* **URL :** `/api/chaussettes`
* **Corps de requête :**
    ```json
    {
      "couleur": "rouge",
      "taille": "medium",
      "typeTissu": "coton",
      "prix": 7.50
    }
    ```
* **Code retour :** `201 Created`

#### 2. Modifier une paire de chaussettes
* **Méthode :** `PUT`
* **URL :** `/api/chaussettes?identifiant=1`
* **Corps de requête :**
    ```json
    {
      "couleur": "bleu",
      "taille": "large",
      "typeTissu": "laine",
      "prix": 12.00
    }
    ```
* **Code retour :** `200 OK` ou `404 Not Found`

#### 3. Supprimer une paire de chaussettes
* **Méthode :** `DELETE`
* **URL :** `/api/chaussettes?identifiant=1`
* **Code retour :** `200 OK`

#### 4. Lister / Rechercher des chaussettes
* **Méthode :** `GET`
* **URL (Tout l'inventaire) :** `/api/chaussettes`
* **URL (Recherche par ID) :** `/api/chaussettes?identifiant=1`
* **URL (Recherche par critères) :** `/api/chaussettes?couleur=rouge&taille=medium`

---

### B. Gestion des Ventes (`/api/ventes`)

#### 1. Créer une vente
* **Méthode :** `POST`
* **URL :** `/api/ventes`
* **Corps de requête (IDs des chaussettes à vendre) :**
    ```json
    {
      "ids": [1, 2, 5]
    }
    ```
* **Code retour :** `201 Created`
* **Réponse :**
    ```json
    {
      "identifiant": 10,
      "dateVente": "2023-10-27 14:00:00",
      "total": 25.50,
      "chaussettes": [...]
    }
    ```

#### 2. Ajouter des chaussettes à une vente existante
* **Méthode :** `POST`
* **URL :** `/api/ventes/ajouter?identifiant=10`
* **Corps de requête :**
    ```json
    {
      "ids": [8]
    }
    ```

#### 3. Annuler une vente
* **Méthode :** `DELETE`
* **URL :** `/api/ventes?identifiant=10`
* **Description :** Supprime la vente et remet les chaussettes associées dans l'inventaire.

#### 4. Lister / Rechercher des ventes
* **Méthode :** `GET`
* **URL (Toutes les ventes) :** `/api/ventes`
* **URL (Recherche par dates) :** `/api/ventes?minDate=2023-01-01&maxDate=2023-12-31`
    * Format des dates : `YYYY-MM-DD`


## 4. Diagrammes de Séquences (format PNG dans le dossier)
