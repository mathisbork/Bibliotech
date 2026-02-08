import java.sql.Connection;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Connexion à la base de données
        Connection connected = DbManager.getInstance().getConnection();

        if (connected != null) {
            System.out.println("✅ Connexion à la base de données active.");

            try {
                // Instanciation unique du DAO et du Scanner
                LivreDAO livreManager = new LivreDAO(connected); 
                Scanner scanner = new Scanner(System.in);

                // ===================================================
                // PARTIE 1 : Lister les livres
                // ===================================================
                System.out.println("\n=== 1. LISTE DES LIVRES ===");
                livreManager.listerTousLesLivres();

                // ===================================================
                // PARTIE 2 : Filtrage par Genre
                // ===================================================
                System.out.println("\n=== 2. RECHERCHE PAR GENRE ===");
                System.out.println("Chargement de la bibliothèque en mémoire...");
                
                List<Livre> bibliothequeComplete = livreManager.getAllLivres();
                System.out.println(bibliothequeComplete.size() + " livres chargés.");

                System.out.print("Entrez un genre pour filtrer (ex: Roman, SF...) : ");
                String genreRecherche = scanner.nextLine();

                System.out.println("\n--- Résultats pour : " + genreRecherche + " ---");

                bibliothequeComplete.stream()
                        .filter(livre -> livre.getGenre() != null && livre.getGenre().equalsIgnoreCase(genreRecherche))
                        .forEach(livre -> System.out.println(livre.getTitre() + " - " + livre.getAuteur().getNom()));
                
                // ===================================================
                // PARTIE 3 : Emprunter un livre
                // ===================================================
                System.out.println("\n=== 3. EMPRUNTER UN LIVRE ===");

                System.out.print("Entrez l'ID du livre à emprunter : ");
                int idLivreEmprunt = scanner.nextInt();

                System.out.print("Entrez votre ID d'adhérent  : ");
                int idInscrit = scanner.nextInt();

                livreManager.emprunterLivre(idLivreEmprunt, idInscrit);

                System.out.println("\nFin du programme.");
                scanner.close();

            } catch (Exception e) {
                System.err.println("Une erreur est survenue : " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("echec de la connexion à la base de données.");
        }
    }
}