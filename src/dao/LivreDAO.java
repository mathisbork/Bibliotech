import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
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

    public void emprunterLivre(int idLivre, int idInscrit) {
        String sqlSearchExemplaire = "SELECT ref_e FROM exemplaire WHERE id_l = ? LIMIT 1";

        String sqlInsertEmprunt = "INSERT INTO emprunt (ref_e, id_i, date_em) VALUES (?, ?, ?)";

        try {
            int idExemplaireTrouve = -1;

            try (PreparedStatement stmtSearch = connection.prepareStatement(sqlSearchExemplaire)) {
                stmtSearch.setInt(1, idLivre);
                ResultSet rs = stmtSearch.executeQuery();

                if (rs.next()) {
                    idExemplaireTrouve = rs.getInt("ref_e");
                }
            }

            if (idExemplaireTrouve != -1) {
                try (PreparedStatement stmtInsert = connection.prepareStatement(sqlInsertEmprunt)) {
                    stmtInsert.setInt(1, idExemplaireTrouve);
                    stmtInsert.setInt(2, idInscrit);
                    stmtInsert.setDate(3, java.sql.Date.valueOf(java.time.LocalDate.now()));

                    int rows = stmtInsert.executeUpdate();
                    if (rows > 0) {
                        System.out.println("Succès : L'exemplaire n°" + idExemplaireTrouve + " (du livre " + idLivre
                                + ") a été emprunté.");
                    }
                }
            } else {
                System.out.println("Désolé, aucun exemplaire n'a été trouvé pour le livre ID " + idLivre);
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL : " + e.getMessage());
        }
    }
}