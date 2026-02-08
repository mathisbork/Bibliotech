import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LivreDAO {
    private final Connection connection;

    public LivreDAO(Connection connection) {
        this.connection = connection;
    }

    public void listerTousLesLivres() {
        String sql = "SELECT l.id_l, l.titre_l, l.annee_l, a.prenom_a, a.nom_a " +
                "FROM livre l " +
                "JOIN rediger r ON l.id_l = r.id_l " +
                "JOIN auteur a ON r.id_a = a.id_a";

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("--- Liste des Livres ---");

            while (rs.next()) {
                int id = rs.getInt("id_l");
                String titre = rs.getString("titre_l");
                int annee = rs.getInt("annee_l");
                String prenom = rs.getString("prenom_a");
                String nom = rs.getString("nom_a");

                System.out.println("[" + id + "] " + titre + " (" + annee + ") - Auteur : " + prenom + " " + nom);
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors du listing : " + e.getMessage());
        }
    }

    public List<Livre> getAllLivres() {
        List<Livre> liste = new ArrayList<>();
        String sql = "SELECT l.id_l, l.titre_l, l.annee_l, a.prenom_a, a.nom_a, a.id_a, t.libelle_t " +
                "FROM livre l " +
                "JOIN rediger r ON l.id_l = r.id_l " +
                "JOIN auteur a ON r.id_a = a.id_a " +
                "JOIN typelivre t ON l.id_t = t.id_t";

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Auteur aut = new Auteur();
                aut.setNom(rs.getString("nom_a"));
                aut.setPrenom(rs.getString("prenom_a"));
                aut.setId(rs.getInt("id_a"));

                Livre liv = new Livre(rs.getString("titre_l"), rs.getInt("id_l"), 0, aut);

                liv.setGenre(rs.getString("libelle_t"));

                int annee = rs.getInt("annee_l");
                liv.setDateParution(java.time.LocalDate.of(annee, 1, 1));

                liste.add(liv);
            }
        } catch (SQLException e) {
            System.err.println("Erreur : " + e.getMessage());
        }
        return liste;
    }
}