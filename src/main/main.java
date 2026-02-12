import config.DbManager;
import dao.LivreDAO;
import model.Livre;
import java.io.FileInputStream;
import java.sql.Connection;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Connection connected = DbManager.getInstance().getConnection();

        if (connected != null) {
            try (Scanner scanner = new Scanner(System.in)) {
                
                // --- 1. CHOIX DE LA LANGUE (Chargement Robuste) ---
                System.out.println("1. Français");
                System.out.println("2. English");
                System.out.print("Choice: ");
                
                int lang = 1;
                if (scanner.hasNextInt()) {
                    lang = scanner.nextInt();
                    scanner.nextLine(); // Vide le buffer
                }

                // On construit le chemin vers le fichier de langue
                String rootPath = System.getProperty("user.dir");
                String langFile = (lang == 2) ? "messages_en.properties" : "messages_fr.properties";
                // ATTENTION : Vérifie si tes fichiers sont dans 'src' ou 'src/ressources'
                String fullPath = rootPath + "/src/" + langFile; 

                ResourceBundle bundle;
                try (FileInputStream fis = new FileInputStream(fullPath)) {
                    bundle = new PropertyResourceBundle(fis);
                } catch (Exception e) {
                    System.err.println("Erreur langue (fichier introuvable) : " + fullPath);
                    return; // On arrête si pas de langue
                }

                LivreDAO dao = new LivreDAO(connected);
                dao.setBundle(bundle);

                // --- 2. BOUCLE PRINCIPALE ---
                int choix = 0;
                do {
                    System.out.println("\n=== " + bundle.getString("app.bienvenue") + " ===");
                    System.out.println(bundle.getString("menu.choix1"));
                    System.out.println(bundle.getString("menu.choix2"));
                    System.out.println(bundle.getString("menu.choix3"));
                    System.out.println(bundle.getString("menu.quitter"));
                    System.out.print(bundle.getString("menu.votre_choix") + " ");

                    if (scanner.hasNextInt()) {
                        choix = scanner.nextInt();
                        scanner.nextLine();
                    } else {
                        scanner.nextLine();
                        continue;
                    }

                    switch (choix) {
                        case 1: // Lister
                            dao.listerTousLesLivres();
                            break;
                            
                        case 2: // Filtrer (Stream)
                            System.out.print(bundle.getString("prompt.genre") + " ");
                            String g = scanner.nextLine();
                            System.out.println("--- Résultats ---");
                            dao.getAllLivres().stream()
                               .filter(l -> l.getGenre() != null && l.getGenre().equalsIgnoreCase(g))
                               .forEach(l -> System.out.println(l.getTitre() + " - " + l.getAuteur().getNom()));
                            break;
                            
                        case 3: // Emprunter
                            System.out.print(bundle.getString("prompt.id_livre") + " ");
                            int idL = scanner.nextInt();
                            System.out.print(bundle.getString("prompt.id_inscrit") + " ");
                            int idU = scanner.nextInt();
                            System.out.print(bundle.getString("prompt.duree") + " ");
                            int d = scanner.nextInt();
                            scanner.nextLine();
                            dao.emprunterLivre(idL, idU, d);
                            break;
                            
                        case -1:
                            System.out.println(bundle.getString("msg.bye"));
                            break;
                    }

                } while (choix != -1);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Erreur de connexion BDD.");
        }
    }
}