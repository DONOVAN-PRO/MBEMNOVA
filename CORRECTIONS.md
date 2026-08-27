# TontinePro — Corrections apportées

Ce document liste tous les bugs trouvés dans le projet original et les corrections
appliquées. À garder pour ta note d'intention ou pour comprendre ce qui a changé.

## 🔴 Bugs critiques (empêchaient l'application de fonctionner sur Render)

### 1. Casse des dossiers/fichiers de templates
Le dossier `Templates/` (majuscule) et les fichiers (`Login.html`, `Listes.html`, etc.)
ne correspondaient ni au dossier attendu par Spring Boot (`templates/`, minuscule),
ni aux noms de vues retournés par les contrôleurs (`"membres/liste"`, `"tontines/detail"`...).
Windows ignore la casse des fichiers, donc ça fonctionnait en local — **mais Render
tourne sous Linux, où la casse compte**. Résultat : 100 % des pages auraient renvoyé
une erreur 500 "template introuvable" en production.
**Correction :** toute l'arborescence a été renommée en minuscules, avec des noms de
fichiers strictement identiques aux vues retournées par les contrôleurs.

### 2. `StatistiqueController` incompatible avec son propre template
Le contrôleur ajoutait des attributs séparés (`totalCollecte`, `tauxRetard`,
`statistiquesMembres`...) mais `Tontines/Statistiques.html` attendait un seul objet
`${statistiques}`. Il existait un fichier orphelin `Statistique.html` (à la racine
des templates, jamais utilisé par aucun contrôleur) qui, lui, correspondait bien au
modèle attendu.
**Correction :** c'est ce fichier orphelin qui a été déplacé vers
`templates/tontines/statistiques.html` ; l'ancien fichier cassé a été supprimé.

### 3. Mauvais nom de package dans `membres/formulaire.html`
`T(com.tontinepro.entity.Membre.StatutMembre)` référençait un package qui n'existe
pas dans ton projet (le vrai package est `com.MBEMNOVA.Tontine.Entity`). Cette page
plantait à chaque affichage (création et modification d'un membre).
**Correction :** référence corrigée vers `com.MBEMNOVA.Tontine.Entity.Membre.StatutMembre`.

### 4. Déconnexion cassée sur toutes les pages
Tous les liens "Déconnexion" étaient des `<a href="/logout">` (requête GET). Spring
Security exige un POST par défaut pour `/logout` — ces liens ne faisaient donc rien.
**Correction :** remplacés par des formulaires `<form method="post">` sur les 4 pages
concernées (dashboard, membres/liste, tontines/liste, tontines/detail).

### 5. Formulaire d'inscription d'un membre à une tontine : absent
`tontines/detail.html` permettait de retirer un membre mais aucun formulaire ne
permettait d'en inscrire un, alors que l'endpoint `POST /tontines/{id}/membres`
existait déjà côté contrôleur — la fonctionnalité était inutilisable depuis l'interface.
**Correction :** formulaire ajouté (sélection du membre, ordre de tour, date
d'adhésion), avec les attributs de modèle nécessaires ajoutés dans `TontineController`.

### 6. Validation `@NotNull` sur `tontineId` qui échouait à chaque soumission
`AdhesionDTO.tontineId` et `CotisationDTO.tontineId` étaient marqués `@NotNull`,
mais ce champ est toujours fourni par l'URL (`/tontines/{id}/...`), jamais par le
formulaire. Or la validation Bean Validation (`@Valid`) s'exécute **avant** que le
contrôleur ait pu injecter cette valeur — la validation échouait donc systématiquement,
et les soumissions étaient silencieusement rejetées.
**Correction :** `@NotNull` retiré de ces deux champs (avec commentaire explicatif),
la présence de la tontine restant vérifiée côté service via `findById(...)`.

### 7. Sélecteur de statut cassé dans le formulaire de cotisation
`<option value="payé">` / `value="en retard"` ne correspondaient pas aux constantes
d'enum réelles (`PAYE`, `EN_RETARD`) → erreur de conversion à chaque soumission.
**Correction :** génération dynamique des options à partir de l'enum.

### 8. Comparaison d'enum ratée dans `tours/liste.html`
`th:if="${tour.statut == 'à venir'}"` comparait l'enum à une chaîne qui ne
correspondait jamais à la constante réelle `A_VENIR` → le bouton "Verser" n'apparaissait
jamais, rendant impossible le versement d'un tour depuis l'interface.
**Correction :** comparaison via `tour.statut.name() == 'A_VENIR'`. La référence
`${tontine.id}` (absente du modèle de ce contrôleur) a aussi été corrigée en
`${tontineId}`.

### 9. Formulaires de cotisation : page blanche garantie
`CotisationController` n'ajoutait jamais l'attribut `tontine` au modèle, alors que
`cotisations/liste.html` et `cotisations/formulaire.html` l'utilisaient
(`${tontine.id}`) → erreur d'évaluation sur objet `null`, page en erreur 500 à
chaque visite.
**Correction :** `TontineService` injecté dans `CotisationController`, attribut
`tontine` ajouté dans les trois méthodes concernées (`liste`, `nouveau`, `creer`
en cas d'erreur de validation).

## 🟠 Bugs logiques

### 10. Calcul du montant reçu par membre incorrect
`StatistiqueService.calculerTotalRecu()` multipliait le montant de cotisation par
`tours.size()` (nombre total de tours déjà générés dans la tontine) au lieu du
nombre de membres inscrits. Le solde net affiché se serait dégradé de façon
croissante et fausse à chaque nouveau tour généré.
**Correction :** multiplication par le nombre de membres inscrits (`adhesions.size()`),
qui correspond réellement à ce qu'un bénéficiaire touche à un cycle donné (chaque
membre cotise pour lui).

### 11. Impossible d'enregistrer une cotisation "en retard"
`Cotisation.datePaiement` et `CotisationDTO.datePaiement` étaient `@NotNull`,
alors qu'une cotisation en retard n'a par définition pas encore de date de
paiement. Cela empêchait d'utiliser le statut `EN_RETARD` tel que voulu par le sujet.
**Correction :** contrainte `@NotNull` retirée sur ce champ (entité + DTO), colonne
rendue nullable.

## 🟡 Sécurité / configuration

### 12. Identifiants de base de données en dur, committés sur GitHub
`application.properties` contenait un mot de passe réel de ta base PostgreSQL Render,
en clair, dans le code versionné — ce que le sujet interdit explicitement.
**Correction :** fichier de config fusionné en un seul `application.yml`, qui ne lit
plus que des variables d'environnement, sans valeur par défaut sensible.
**⚠️ Action de ta part : ce mot de passe a été exposé sur GitHub. Va le régénérer
dans le tableau de bord Render dès que possible, indépendamment de cette correction.**

### 13. Dockerfile avec un stage de build dupliqué
Le `Dockerfile` exécutait `./mvnw clean package` **deux fois** dans deux stages
différents (le deuxième build n'étant même pas utilisé par l'image finale) — ce qui
double inutilement le temps de build sur Render.
**Correction :** Dockerfile simplifié en 2 stages propres (build puis exécution),
avec une image d'exécution allégée (JRE seul au lieu du JDK complet).

## ✅ Robustesse (pas des bugs actifs, mais des pièges désamorcés)

### 14. `@Builder` sans `@Builder.Default`
Les champs `statut` de `Membre`, `Tontine`, `Cotisation` et `Tour` avaient une
valeur par défaut (`= StatutMembre.ACTIF`, etc.) mais Lombok **ignore silencieusement**
les initialiseurs de champ dans un `@Builder` tant que `@Builder.Default` n'est pas
ajouté. Le code actuel n'utilisait pas les builders sur ces entités (donc pas de
bug déclenché aujourd'hui), mais c'était une bombe à retardement pour toute évolution
future du code.
**Correction :** `@Builder.Default` ajouté sur les 4 champs concernés.

---

## Ce qui n'a pas été touché
Les entités, repositories et la logique métier globale (génération des tours,
calcul des taux de retard, structure DTO/Service/Contrôleur) étaient déjà bien conçus
et respectent la séparation en couches demandée par le sujet — je n'ai touché qu'aux
bugs listés ci-dessus.
