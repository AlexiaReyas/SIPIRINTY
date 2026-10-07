# Synthese des sprints du mini-framework

Ce document rassemble les attentes du professeur et l'etat actuel du projet.

## Vue d'ensemble

| Sprint | Objectif principal | Etat dans le projet |
|---|---|---|
| Sprint 0 | Recevoir une requete avec un FrontController | Realise dans `sprint-0` |
| Sprint 1 | Detecter les classes `@Controller` | Realise dans `sprint-1` |
| Sprint 2 | Associer une URL a une methode avec `@Url` | Realise dans `sprint-2` |
| Sprint 3 | Distinguer les methodes HTTP GET et POST | Realise dans `sprint-3` |
| Sprint 4 | Aucune version identifiable dans le depot | A clarifier |
| Sprint 5 | Executer les methodes et afficher des vues JSP | Realise dans `sprint-5` |
| Sprint 5-bis | Ameliorer l'integration avec le contexte Spring | Present dans `sprint-5-bis` |
| Sprint 6 | Retourner des donnees JSON avec `@WebApi` | Realise et valide dans `sprint-7-bis` |
| Sprint 7 | Binder des parametres simples d'un formulaire | Present dans `sprint-7` et `sprint-7-bis` |
| Sprint 7-bis | Binder automatiquement un objet depuis un formulaire | Realise et valide dans `sprint-7-bis` |
| Sprint main | Regrouper les fonctionnalites du framework | Version de consolidation |

---

## Sprint 0 : reception d'une requete

### Ce que le professeur attend

Le framework doit posseder un `FrontController` capable d'intercepter les requetes HTTP `GET` et `POST`.

Il doit :

- recuperer l'URL demandee ;
- afficher cette URL ;
- servir de point d'entree unique de l'application.

### Demonstration attendue

Une requete vers une URL doit afficher une information similaire a :

```text
URL : /test
```

### Etat actuel

Realise dans `sprint-0/framework-sprint0`.

---

## Sprint 1 : detection des controleurs

### Ce que le professeur attend

Le framework doit introduire l'annotation `@Controller` et scanner un package pour trouver automatiquement les classes qui utilisent cette annotation.

Il doit :

- scanner le package de l'application ;
- trouver les classes `@Controller` ;
- enregistrer les controleurs trouves ;
- afficher ou exploiter la liste des controleurs.

### Demonstration attendue

Par exemple :

```text
Found controller: demo.ClientController
```

### Etat actuel

Realise dans `sprint-1/framework-sprint1`.

---

## Sprint 2 : mapping des URLs

### Ce que le professeur attend

Le framework doit introduire l'annotation `@Url` afin d'associer une URL a une methode d'un controleur.

Exemple :

```java
@Controller
public class ClientController {
    @Url("/clients")
    public String clients() {
        return "Liste des clients";
    }
}
```

Le framework doit :

- lire les annotations `@Url` ;
- memoriser l'URL, la classe et la methode ;
- retrouver le mapping lorsqu'une requete arrive ;
- signaler lorsqu'aucun mapping n'existe.

### Demonstration attendue

```text
Class: demo.ClientController
Method: clients
```

### Etat actuel

Realise dans `sprint-2/framework-sprint2`.

---

## Sprint 3 : gestion des methodes HTTP

### Ce que le professeur attend

Le mapping doit tenir compte de la methode HTTP et pas seulement de l'URL.

Exemples :

```java
@Url(value = "/clients", method = "GET")
@Url(value = "/clients", method = "POST")
```

Le framework doit distinguer :

```text
GET /clients
POST /clients
```

Une meme URL peut donc appeler deux methodes differentes selon la methode HTTP utilisee.

### Etat actuel

Realise dans `sprint-3/framework-sprint3` avec la classe `UrlMethod`.

---

## Sprint 4 : aucune specification disponible

Aucun dossier `sprint-4` n'est present dans le projet et aucune consigne de sprint 4 n'apparait dans `sujet.MD`.

Il n'est donc pas possible de confirmer une fonctionnalite precise attendue pour ce sprint a partir du depot actuel.

### Etat actuel

A clarifier avec le professeur si ce sprint doit etre presente ou s'il a ete remplace par une autre version.

---

## Sprint 5 : execution des controleurs et affichage des vues

### Ce que le professeur attend

Le framework doit aller au-dela de la detection des routes et executer reellement la methode du controleur.

Il doit :

- retrouver la methode correspondant a l'URL ;
- creer le controleur ;
- invoquer la methode par reflection ;
- recuperer son resultat ;
- afficher une vue JSP.

Le type `ModAndView` permet de retourner :

- le nom de la vue ;
- les donnees a transmettre a la vue.

Exemple :

```java
return new ModAndView("employe-form");
```

### Demonstration attendue

Une URL doit ouvrir une page JSP et non plus afficher uniquement le nom du mapping.

### Etat actuel

Realise dans `sprint-5/framework-sprint5`.

---

## Sprint 5-bis : integration avec le contexte Spring

### Ce que le code montre

Cette version ajoute la possibilite de recuperer un `ApplicationContext` Spring et de le transmettre a une methode de controleur qui le demande.

Le framework continue aussi a gerer :

- les controleurs ;
- les routes GET et POST ;
- l'invocation par reflection ;
- les objets `ModAndView` ;
- les vues JSP.

### Etat actuel

Present dans `sprint-5-bis/framework-sprint5-bis`.

Cette fonctionnalite n'est pas detaillee explicitement dans `sujet.MD`, donc il faut la presenter comme une amelioration technique du framework, sauf consigne supplementaire du professeur.

---

## Sprint 6 : API JSON avec `@WebApi`

### Ce que le professeur attend

Le sujet demande de creer une annotation `@WebApi`.

Lorsqu'une methode de controleur porte cette annotation, son resultat doit etre transforme en JSON au lieu d'etre envoye vers une vue JSP.

Exemple :

```java
@WebApi
@Url(value = "/clients", method = "GET")
public List<Client> clients() {
    return List.of(
        new Client(1, "Alexia"),
        new Client(2, "Demo")
    );
}
```

Le framework doit :

- detecter `@WebApi` ;
- executer la methode ;
- convertir le resultat avec Jackson ;
- retourner une reponse `application/json`.

### Demonstration attendue

URL :

```text
http://localhost:8080/client-demo/clients
```

Resultat attendu :

```json
[
  {"id":1,"nom":"Alexia"},
  {"id":2,"nom":"Demo"}
]
```

### Etat actuel

Realise et valide dans `sprint-7-bis`.

Le sujet mentionne ensuite comme suites possibles :

- upload de fichiers ;
- session ;
- roles et autorisations.

Ces fonctionnalites ne sont pas documentees en detail ni validees dans la version actuelle.

---

## Sprint 7 : binding des parametres simples

### Ce que le professeur attend

Les donnees saisies dans une vue doivent pouvoir etre envoyees au controleur.

Exemple de methode :

```java
public ModAndView save(String nom, int age, double salaire)
```

Le framework doit :

- recuperer les parametres avec `request.getParameter(...)` ;
- reconnaitre les noms des parametres ;
- convertir les valeurs texte en `String`, `int`, `double`, etc. ;
- transmettre les valeurs a la methode ;
- gerer les parametres absents avec `null` ou une valeur par defaut.

### Parcours attendu

```text
Formulaire -> POST -> FrontController -> methode save(...)
```

### Etat actuel

La fonctionnalite est presente dans la version `sprint-7` et conservee dans `sprint-7-bis`.

---

## Sprint 7-bis : binding automatique d'un objet

### Ce que le professeur attend

Le sujet demande le binding d'un objet lorsqu'une action recoit un parametre objet.

Au lieu d'ecrire :

```java
save(String nom, int age, double salaire)
```

le controleur doit pouvoir recevoir directement :

```java
save(Employe employe)
```

Le framework doit :

1. inspecter les parametres de la methode ;
2. detecter qu'un parametre est un objet ;
3. creer l'objet avec son constructeur vide ;
4. parcourir ses proprietes ;
5. chercher les parametres HTTP portant les memes noms ;
6. convertir les valeurs ;
7. appeler les setters ;
8. invoquer la methode avec l'objet rempli.

### Demonstration attendue

1. Ouvrir :

```text
http://localhost:8080/client-demo/employe/form
```

2. Remplir les champs :

- Nom ;
- Age ;
- Salaire.

3. Cliquer sur `Enregistrer`.

4. Verifier que le formulaire envoie un `POST` vers :

```text
/client-demo/employe/save
```

5. Verifier que le controleur recoit un objet `Employe` rempli.

6. Afficher la page de succes avec les valeurs saisies.

### Parcours technique

```text
GET /employe/form
    -> EmployeController.form()
    -> employe-form.jsp

POST /employe/save
    -> FrontController
    -> creation automatique de Employe
    -> setNom(...), setAge(...), setSalaire(...)
    -> EmployeController.save(Employe employe)
    -> employe-success.jsp
```

### Etat actuel

Realise dans `sprint-7-bis`.

Les routes validees sont :

```text
GET  http://localhost:8080/client-demo/employe/form
POST http://localhost:8080/client-demo/employe/save
```

La route API suivante fonctionne aussi :

```text
GET http://localhost:8080/client-demo/clients
```

---

## Sprint main : consolidation

Le dossier `sprint-main` semble correspondre a une version de regroupement des fonctionnalites du framework.

La demonstration finale doit reprendre :

- le `FrontController` ;
- les annotations `@Controller` et `@Url` ;
- le mapping par methode HTTP ;
- l'invocation des controleurs ;
- les vues JSP ;
- `@WebApi` et les reponses JSON ;
- le binding des parametres simples ;
- le binding automatique d'objets.

Aucune specification independante du sprint main n'est presente dans le depot.

---

## Fonctionnalites effectivement validees dans la version courante

Les verifications effectuees sur `sprint-7-bis` ont confirme :

- compilation du framework avec Maven ;
- compilation du client WAR ;
- lancement avec Tomcat 9.0.106 ;
- reponse JSON `200 OK` sur `/clients` ;
- affichage `200 OK` du formulaire `/employe/form` ;
- presence de la logique de binding objet dans `FrontController` ;
- redirection du formulaire vers `/employe/save` ;
- affichage des donnees de l'employe dans `employe-success.jsp`.

L'URL racine suivante :

```text
http://localhost:8080/client-demo/
```

n'est pas une page d'accueil. Aucune route `/` n'est declaree dans les controleurs. Le message `No mapping found for URL: /` est donc normal ; il faut utiliser les routes fonctionnelles indiquees ci-dessus.

## Points a clarifier avec le professeur

- La fonctionnalite exacte du sprint 4, absent du depot.
- Si le sprint 5-bis doit etre presente comme une fonctionnalite Spring ou seulement comme une version intermediaire.
- Si l'upload, les sessions et les roles doivent etre implementes dans une prochaine etape.
- Si une page d'accueil `/` est attendue ou si les routes de demonstration suffisent.
