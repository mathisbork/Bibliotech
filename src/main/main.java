import java.sql.Connection;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Connection connected = DbManager.getInstance().getConnection();

        if (connected != null) {
            try {
                LivreDAO livreDAO = new LivreDAO(connected);
                livreDAO.listerTousLesLivres();

                System.out.println("Chargement des livres...");
                List<Livre> bibliotheque = livreDAO.getAllLivres();
                System.out.println(bibliotheque.size() + " livres chargés.");

                Scanner scanner = new Scanner(System.in);
                System.out.print("Entrez un genre pour filtrer (ex: Roman, Science Fiction...) : ");
                String genreSaisi = scanner.nextLine();

                System.out.println("\n--- Livres du genre : " + genreSaisi + " ---");

                bibliotheque.stream()
                        .filter(livre -> livre.getGenre() != null && livre.getGenre().equalsIgnoreCase(genreSaisi))
                        .forEach(livre -> System.out.println(livre.getTitre() + " - " + livre.getAuteur().getNom()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}