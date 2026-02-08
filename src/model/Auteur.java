import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;

public class Auteur {
    private String nom;
    private String prenom;
    private int id;
    private LocalDate date_naissance;
    
    public Auteur() {
    }

    public Auteur(String nom, String prenom, int id, LocalDate date_naissance) {
        this.nom = nom;
        this.prenom = prenom;
        this.id = id;
        this.date_naissance = date_naissance;
    }

    public int CalculAge(){
        if(this.date_naissance != null) {
             long age = ChronoUnit.YEARS.between(this.date_naissance, LocalDate.now());
            return (int) age;
        }
        return 0;
    }

    

    public String getNom() {
        return nom;
    }
    public String getPrenom() {
        return prenom;
    }
    public int getId() {
        return id;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDate_naissance() {
        return date_naissance;
    }

    public void setDate_naissance(LocalDate date_naissance) {
        this.date_naissance = date_naissance;
    }

}
