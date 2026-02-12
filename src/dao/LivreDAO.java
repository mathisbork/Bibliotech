import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class LivreDAO {
    private final Connection connection;
    private ResourceBundle bundle;

    public LivreDAO(Connection connection) {
        this.connection = connection;
    }

    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    private String getText(String key) {
        if (bundle != null && bundle.containsKey(key)) {
            return bundle.getString(key);
        }
        return key;
    }

    public void listerTousLesLivres() {
        String sql = "SELECT l.id_l, l.titre_l, l.annee_l, a.prenom_a, a.nom_a " +
                "FROM livre l " +
                "JOIN rediger r ON l.id_l = r.id_l " +
                "JOIN auteur a ON r.id_a = a.id_a";

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("--- " + getText("menu.choix1") + " ---");

            while (rs.next()) {
                int id = rs.getInt("id_l");
                String titre = rs.getString("titre_l");
                int annee = rs.getInt("annee_l");
                String prenom = rs.getString("prenom_a");
                String nom = rs.getString("nom_a");

                System.out.println("[" + id + "] " + titre + " (" + annee + ") - Auteur : " + prenom + " " + nom);
            }
        } catch (SQLException e) {
            System.err.println(getText("msg.erreur_sql") + " " + e.getMessage());
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
                liv.setDateParution(LocalDate.of(annee, 1, 1));

                liste.add(liv);
            }
        } catch (SQLException e) {
            System.err.println(getText("msg.erreur_sql") + " " + e.getMessage());
        }
        return liste;
    }

    public void emprunterLivre(int idLivre, int idInscrit, int nbJours) {

        String sqlSearch = "SELECT ref_e FROM exemplaire " +
                "WHERE id_l = ? " +
                "AND ref_e NOT IN (SELECT ref_e FROM emprunt)";

        String sqlInsert = "INSERT INTO emprunt (ref_e, id_i, date_em, delais_em) VALUES (?, ?, ?, ?)";

        try {
            String refExemplaire = null;

            try (PreparedStatement stmtSearch = connection.prepareStatement(sqlSearch)) {
                stmtSearch.setInt(1, idLivre);

                try (ResultSet rs = stmtSearch.executeQuery()) {
                    if (rs.next()) {
                        refExemplaire = rs.getString("ref_e");
                    }
                }
            }

            if (refExemplaire != null) {
                try (PreparedStatement stmtInsert = connection.prepareStatement(sqlInsert)) {
                    stmtInsert.setString(1, refExemplaire);
                    stmtInsert.setInt(2, idInscrit);
                    stmtInsert.setDate(3, Date.valueOf(LocalDate.now()));
                    stmtInsert.setInt(4, nbJours);

                    int rows = stmtInsert.executeUpdate();
                    if (rows > 0) {
                        System.out.println(getText("msg.succes") + " (Exemplaire : " + refExemplaire + ")");
                    }
                }
            } else {
                System.out.println(
                        getText("msg.erreur") + " Aucun exemplaire disponible pour le livre ID " + idLivre);
            }

        } catch (SQLException e) {
            System.err.println(getText("msg.erreur_sql") + " " + e.getMessage());
        }
    }
}