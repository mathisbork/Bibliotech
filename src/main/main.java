import java.sql.Connection;

public class main {
    public static void main(String[] args) {
        Connection coco = DbManager.getInstance().getConnection();

        if (coco != null) {
            System.out.println("Félicitations ! La connexion à la base de données est active.");
        } else {
            System.out.println("Hum, la connexion semble être nulle...");
        }
    }
}
