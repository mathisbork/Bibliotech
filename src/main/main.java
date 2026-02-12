import java.sql.Connection;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Connection connected = DbManager.getInstance().getConnection();

        if (connected != null) {
            try (Scanner scanner = new Scanner(System.in)) {

                // --- 1. CHOIX DE LA LANGUE (I18N) ---
                System.out.println("1. Français");
                System.out.println("2. English");
                System.out.print("Choice: ");

                int langChoice = 0;
                if (scanner.hasNextInt()) {
                    langChoice = scanner.nextInt();
                    scanner.nextLine(); // Nettoyage tampon
                }

                Locale locale;
                if (langChoice == 2) {
                    locale = new Locale("en", "US"); // Anglais
                } else {
                    locale = Locale.FRANCE; // Français par défaut
                }

                // Chargement du fichier messages_fr.properties ou messages_en.properties
                ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);

                // Initialisation du DAO avec la langue
                LivreDAO livreManager = new LivreDAO(connected);
                livreManager.setBundle(bundle); // On donne le dictionnaire au DAO

                // --- BOUCLE PRINCIPALE ---
                int choixUtilisateur = 0;
                do {
                    // Affichage du menu traduit
                    System.out.println("\n=================================");
                    System.out.println("   " + bundle.getString("app.bienvenue"));
                    System.out.println("=================================");
                    System.out.println(bundle.getString("menu.choix1")); // 1. Lister...
                    System.out.println(bundle.getString("menu.choix2")); // 2. Rechercher...
                    System.out.println(bundle.getString("menu.choix3")); // 3. Emprunter...
                    System.out.println(bundle.getString("menu.quitter")); // -1. Quitter
                    System.out.print("\n" + bundle.getString("menu.votre_choix") + " ");

                    if (scanner.hasNextInt()) {
                        choixUtilisateur = scanner.nextInt();
                        scanner.nextLine();
                    } else {
                        scanner.nextLine();
                        continue;
                    }

                    switch (choixUtilisateur) {
                        case 1:
                            livreManager.listerTousLesLivres();
                            break;

                        case 2:
                            System.out.print(bundle.getString("prompt.genre") + " ");
                            String genre = scanner.nextLine();

                            List<Livre> bibliotheque = livreManager.getAllLivres();
                            bibliotheque.stream()
                                    .filter(l -> l.getGenre() != null && l.getGenre().equalsIgnoreCase(genre))
                                    .forEach(l -> System.out.println("📖 " + l.getTitre()));
                            break;

                        case 3:
                            System.out.print(bundle.getString("prompt.id_livre") + " ");
                            int idLivre = scanner.nextInt();

                            System.out.print(bundle.getString("prompt.id_inscrit") + " ");
                            int idInscrit = scanner.nextInt();

                            System.out.print(bundle.getString("prompt.duree") + " ");
                            int duree = scanner.nextInt();
                            scanner.nextLine();

                            livreManager.emprunterLivre(idLivre, idInscrit, duree);
                            break;

                        case -1:
                            System.out.println(bundle.getString("msg.bye"));
                            break;
                    }

                } while (choixUtilisateur != -1);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}