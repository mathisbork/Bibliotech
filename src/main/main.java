package main;

import config.DbManager;
import dao.LivreDAO;
import exception.LivreIndisponibleException;

import java.sql.Connection;
import java.util.InputMismatchException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.MissingResourceException;

public class Main {
    public static void main(String[] args) {
        Connection connected = DbManager.getInstance().getConnection();

        if (connected != null) {
            try (Scanner scanner = new Scanner(System.in)) {

                System.out.println("1. Francais");
                System.out.println("2. English");
                System.out.print("Choice: ");

                int langChoice = 1;
                if (scanner.hasNextInt()) {
                    langChoice = scanner.nextInt();
                    scanner.nextLine();
                } else {
                    scanner.nextLine();
                }

                Locale locale;
                if (langChoice == 2) {
                    locale = new Locale("en", "US");
                } else {
                    locale = new Locale("fr", "FR");
                }

                ResourceBundle bundle;
                try {
                    bundle = ResourceBundle.getBundle("messages", locale);
                } catch (MissingResourceException e) {
                    System.err.println("Erreur : Fichier de langue introuvable.");
                    return;
                }

                LivreDAO dao = new LivreDAO(connected);
                dao.setBundle(bundle);

                int choix = 0;
                do {
                    System.out.println("\n=== " + bundle.getString("app.bienvenue") + " ===");
                    System.out.println(bundle.getString("menu.choix1"));
                    System.out.println(bundle.getString("menu.choix2"));
                    System.out.println(bundle.getString("menu.choix3"));
                    System.out.println(bundle.getString("menu.stats"));
                    System.out.println(bundle.getString("menu.csv"));

                    System.out.println(bundle.getString("menu.quitter"));
                    System.out.print(bundle.getString("menu.votre_choix") + " ");

                    try {
                        choix = scanner.nextInt();
                        scanner.nextLine();
                    } catch (InputMismatchException e) {
                        scanner.nextLine();
                        System.out.println(">>> " + bundle.getString("msg.erreur") + " Saisie invalide.");
                        choix = 0;
                        continue;
                    }

                    switch (choix) {
                        case 1:
                            dao.listerTousLesLivres();
                            break;

                        case 2:
                            System.out.print(bundle.getString("prompt.genre") + " ");
                            String g = scanner.nextLine();

                            if (dao.existeGenre(g)) {
                                System.out.println("--- Resultats ---");
                                dao.getAllLivres().stream()
                                        .filter(l -> l.getGenre() != null && l.getGenre().equalsIgnoreCase(g))
                                        .forEach(
                                                l -> System.out.println(l.getTitre() + " - " + l.getAuteur().getNom()));
                            } else {
                                System.out.println(">>> " + bundle.getString("msg.erreur") + " Le genre '" + g
                                        + "' n'existe pas.");
                            }
                            break;

                        case 3:
                            try {
                                System.out.print(bundle.getString("prompt.id_livre") + " ");
                                int idL = scanner.nextInt();
                                System.out.print(bundle.getString("prompt.id_inscrit") + " ");
                                int idU = scanner.nextInt();
                                System.out.print(bundle.getString("prompt.duree") + " ");
                                int d = scanner.nextInt();
                                scanner.nextLine();

                                dao.emprunterLivre(idL, idU, d);

                            } catch (InputMismatchException e) {
                                System.out.println(
                                        ">>> " + bundle.getString("msg.erreur") + " Entrez un nombre entier !");
                                scanner.nextLine();
                            } catch (LivreIndisponibleException e) {
                                System.out.println("[!] " + bundle.getString("msg.erreur") + " " + e.getMessage());
                            }
                            break;

                        case 4:
                            dao.genererStatistiques();
                            break;

                        case 5:
                            dao.exporterCSV();
                            break;

                        case -1:
                            System.out.println(bundle.getString("msg.bye"));
                            break;

                        default:
                            System.out.println("Choix invalide.");
                    }

                } while (choix != -1);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Impossible de se connecter a la base de donnees.");
        }
    }
}