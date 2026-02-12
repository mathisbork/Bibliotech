DESCRIPTION
Application de gestion de bibliotheque en Java (Console) connectee a une base de donnees MySQL.
Ce projet implemente les architectures DAO et Singleton, et utilise les fonctionnalites avancees de Java (Streams, Lambdas, NIO.2, Internationalisation).

PREREQUIS TECHNIQUES
1. Java SE Development Kit (JDK 8 ou superieur).
2. Serveur MySQL lance localement.
3. Le fichier 'mysql-connector-j-X.X.X.jar' doit etre ajoute au Classpath (Bibliotheques referencees).

INSTALLATION ET CONFIGURATION
1. Base de donnees :
   - Importez le script 'livres.sql' fourni dans votre outil MySQL (phpMyAdmin, Workbench).
   - Cela creera la base et les tables necessaires.

2. Configuration de la connexion :
   - Ouvrez le fichier 'src/ressources/db.properties'.
   - Modifiez les valeurs 'db.user' et 'db.password' pour qu'elles correspondent a votre installation MySQL locale.

LANCEMENT DU PROJET
1. Ouvrez le projet dans votre IDE (VS Code, IntelliJ, Eclipse).
2. Assurez-vous que le dossier 'src' est bien marque comme dossier source.
3. Lancez l'execution du fichier : 'src/main/Main.java'.
4. Suivez les instructions dans la console (choix de la langue puis navigation dans le menu).

FONCTIONNALITES DU MENU (DETAILS)

Option 1 : Lister tous les livres
- Recupere l'ensemble des livres et des auteurs via une jointure SQL.
- Affiche les resultats dans la console sous forme de liste formatee.

Option 2 : Rechercher par genre
- Demande a l'utilisateur de saisir un genre (ex: Roman, SF).
- Verifie d'abord en base si ce genre existe.
- Si oui, utilise l'API Stream de Java pour filtrer la liste des livres et n'afficher que ceux correspondants.

Option 3 : Emprunter un livre
- Permet d'enregistrer un nouvel emprunt pour un inscrit.
- Verifie la disponibilite d'un exemplaire physique.
- Si aucun exemplaire n'est disponible, le programme leve et attrape une exception personnalisee (LivreIndisponibleException).
- En cas de succes ou d'echec, une ligne est ecrite dans le fichier 'journal.log' (racine du projet) via l'API NIO.2.

Option 4 : Statistiques (Bonus)
- Utilise les 'Collectors' de l'API Stream pour regrouper les livres par genre.
- Calcule et affiche le nombre total de livres pour chaque categorie.

Option 5 : Exporter en CSV (Bonus)
- Genere un fichier 'livres.csv' a la racine du projet.
- Ce fichier contient la liste complete des livres au format standard (separateur point-virgule), ouvrable dans Excel.

STRUCTURE DU PROJET
- src/config : Gestion de la connexion BDD (Singleton).
- src/dao    : Couche d'acces aux donnees et logique metier.
- src/model  : Classes representant les tables (Livre, Auteur).
- src/main   : Point d'entree et gestion de l'interface utilisateur.
- src/exception : Gestion des exceptions personnalisees.
- src/ressources : Fichiers de configuration et de traduction.
