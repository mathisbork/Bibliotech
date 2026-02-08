import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LivreDAO {
    private final Connection connection;
    private final Statement stmt;

    public LivreDAO(Connection connection) throws SQLException {
        this.connection = connection;
        this.stmt = connection.createStatement();
        String sql = "SELECT * FROM livre";
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            int id = rs.getInt("id");
            String titre = rs.getString("titre");
            int isbn = rs.getInt("isbn");
            String auteurPrenom = rs.getString("auteur_prenom");
            String auteurNom = rs.getString("auteur_nom");
            System.out.println("ID: " + id + ", Titre: " + titre + ", ISBN: " + isbn + ", Auteur Prénom: " + auteurPrenom + ", Auteur Nom: " + auteurNom);
        }
    }
}
