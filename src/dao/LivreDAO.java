import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class LivreDAO {
    private final Connection connection;
    private ResourceBundle bundle; // Pour la traduction

    public LivreDAO(Connection connection) {
        this.connection = connection;
    }

    // Pour recevoir la langue depuis le Main
    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    // Petite méthode pour éviter les crashs si une traduction manque
    private String getText(String key) {
        if (bundle != null && bundle.containsKey(key))
            return bundle.getString(key);
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
                System.out.println("[" + rs.getInt("id_l") + "] " + rs.getString("titre_l") +
                        " (" + rs.getInt("annee_l") + ") - " +
                        rs.getString("prenom_a") + " " + rs.getString("nom_a"));
            }
        } catch (SQLException e) {
            System.err.println(getText("msg.erreur_sql") + " " + e.getMessage());
        }
    }

    public List<Livre> getAllLivres() {
        List<Livre> liste = new ArrayList<>();
        // On récupère aussi le genre (libelle_t)
        String sql = "SELECT l.id_l, l.titre_l, l.annee_l, a.id_a, a.nom_a, a.prenom_a, t.libelle_t " +
                "FROM livre l " +
                "JOIN rediger r ON l.id_l = r.id_l " +
                "JOIN auteur a ON r.id_a = a.id_a " +
                "JOIN typelivre t ON l.id_t = t.id_t";

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Auteur aut = new Auteur(rs.getString("nom_a"), rs.getString("prenom_a"), rs.getInt("id_a"), null);
                Livre liv = new Livre(rs.getString("titre_l"), rs.getInt("id_l"), 0, aut);

                liv.setGenre(rs.getString("libelle_t")); // On remplit le genre
                liv.setDateParution(LocalDate.of(rs.getInt("annee_l"), 1, 1));

                liste.add(liv);
            }
        } catch (SQLException e) {
            System.err.println(getText("msg.erreur_sql") + " " + e.getMessage());
        }
        return liste;
    }

    public void emprunterLivre(int idLivre, int idInscrit, int nbJours) {
        // 1. Chercher un exemplaire (ref_e) DISPONIBLE
        String sqlSearch = "SELECT ref_e FROM exemplaire WHERE id_l = ? AND ref_e NOT IN (SELECT ref_e FROM emprunt)";

        // 2. Insérer l'emprunt (avec date_em et delais_em)
        String sqlInsert = "INSERT INTO emprunt (ref_e, id_i, date_em, delais_em) VALUES (?, ?, ?, ?)";

        try {
            String refExemplaire = null;
            try (PreparedStatement stmt = connection.prepareStatement(sqlSearch)) {
                stmt.setInt(1, idLivre);
                ResultSet rs = stmt.executeQuery();
                if (rs.next())
                    refExemplaire = rs.getString("ref_e");
            }

            if (refExemplaire != null) {
                try (PreparedStatement stmt = connection.prepareStatement(sqlInsert)) {
                    stmt.setString(1, refExemplaire);
                    stmt.setInt(2, idInscrit);
                    stmt.setDate(3, Date.valueOf(LocalDate.now()));
                    stmt.setInt(4, nbJours);
                    stmt.executeUpdate();
                    System.out.println(getText("msg.succes") + " (Exemplaire : " + refExemplaire + ")");
                }
            } else {
                System.out.println(getText("msg.aucun_livre"));
            }
        } catch (SQLException e) {
            System.err.println(getText("msg.erreur_sql") + " " + e.getMessage());
        }
    }
}