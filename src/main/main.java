import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        Connection connected = DbManager.getInstance().getConnection();

        if (connected != null) {
            System.out.println("Félicitations ! La connexion à la base de données est active.");
                try {
                    LivreDAO livreDAO = new LivreDAO(connected);
                } catch (Exception e) {
                    System.out.println("Une erreur s'est produite lors de l'initialisation du LivreDAO : " + e.getMessage());
                }
        } else {
            System.out.println("Hum, la connexion semble être nulle...");
        }
    }
}
