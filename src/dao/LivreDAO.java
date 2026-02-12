package dao;

import model.Auteur;
import model.Livre;
import exception.LivreIndisponibleException;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.IOException;

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

    private void logAction(String message) {
        Path path = Paths.get("journal.log");
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String ligne = "[" + timestamp + "] - " + message + System.lineSeparator();

        try {
            Files.write(path, ligne.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("ERREUR CRITIQUE LOG : " + e.getMessage());
        }
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
            System.err.println(">>> " + getText("msg.erreur_sql") + " " + e.getMessage());
        }
    }

    public List<Livre> getAllLivres() {
        List<Livre> liste = new ArrayList<>();
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
                liv.setGenre(rs.getString("libelle_t"));
                liv.setDateParution(LocalDate.of(rs.getInt("annee_l"), 1, 1));
                liste.add(liv);
            }
        } catch (SQLException e) {
            System.err.println(">>> " + getText("msg.erreur_sql") + " " + e.getMessage());
        }
        return liste;
    }

    public void emprunterLivre(int idLivre, int idInscrit, int nbJours) throws LivreIndisponibleException {
        String sqlSearch = "SELECT ref_e FROM exemplaire WHERE id_l = ? AND ref_e NOT IN (SELECT ref_e FROM emprunt)";
        String sqlInsert = "INSERT INTO emprunt (ref_e, id_i, date_em, delais_em) VALUES (?, ?, ?, ?)";

        try {
            String refExemplaire = null;
            try (PreparedStatement stmt = connection.prepareStatement(sqlSearch)) {
                stmt.setInt(1, idLivre);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    refExemplaire = rs.getString("ref_e");
                }
            }

            if (refExemplaire != null) {
                try (PreparedStatement stmt = connection.prepareStatement(sqlInsert)) {
                    stmt.setString(1, refExemplaire);
                    stmt.setInt(2, idInscrit);
                    stmt.setDate(3, Date.valueOf(LocalDate.now()));
                    stmt.setInt(4, nbJours);
                    stmt.executeUpdate();

                    System.out.println("[OK] " + getText("msg.succes") + " (Exemplaire : " + refExemplaire + ")");
                    logAction("Emprunt SUCCES : Livre " + idLivre + " (Ex: " + refExemplaire + ") par inscrit "
                            + idInscrit);
                }
            } else {
                logAction("Emprunt ECHEC : Livre " + idLivre + " indisponible.");
                throw new LivreIndisponibleException("Le livre (ID: " + idLivre + ") n'est pas disponible.");
            }
        } catch (SQLException e) {
            System.err.println(">>> " + getText("msg.erreur_sql") + " " + e.getMessage());
            logAction("Erreur SQL lors de l'emprunt : " + e.getMessage());
        }
    }

    public boolean existeGenre(String genre) {
        String sql = "SELECT COUNT(*) FROM typelivre WHERE LOWER(libelle_t) = LOWER(?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, genre);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println(">>> " + getText("msg.erreur_sql") + " " + e.getMessage());
        }
        return false;
    }

    public void genererStatistiques() {
        System.out.println("\n" + getText("msg.stats"));
        List<Livre> livres = getAllLivres();

        Map<String, Long> stats = livres.stream()
                .filter(l -> l.getGenre() != null)
                .collect(Collectors.groupingBy(Livre::getGenre, Collectors.counting()));

        stats.forEach((genre, count) -> System.out.println(" - " + genre + " : " + count + " livre(s)"));
    }

    public void exporterCSV() {
        List<Livre> livres = getAllLivres();
        StringBuilder sb = new StringBuilder();

        sb.append("ID;Titre;Auteur;Genre;Annee\n");

        for (Livre l : livres) {
            sb.append(l.getId()).append(";")
                    .append(l.getTitre()).append(";")
                    .append(l.getAuteur().getNom()).append(";")
                    .append(l.getGenre()).append(";")
                    .append(l.getDateParution().getYear()).append("\n");
        }

        Path path = Paths.get("livres.csv");
        try {
            Files.write(path, sb.toString().getBytes());
            System.out.println("[OK] " + getText("msg.csv_succes"));
            logAction("Export CSV effectue avec succes.");
        } catch (IOException e) {
            System.err.println(">>> " + getText("msg.csv_erreur") + " " + e.getMessage());
        }
    }
}