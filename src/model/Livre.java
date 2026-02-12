package model;

import java.time.LocalDate;

public class Livre {
    private String titre;
    private int id;
    private int isbn;
    private Auteur auteur;
    private LocalDate dateParution;
    private String genre;

    public Livre() {
    }

    public Livre(String titre, int id, int isbn, Auteur auteur) {
        this.titre = titre;
        this.id = id;
        this.isbn = isbn;
        this.auteur = auteur;
        this.genre = genre;
        this.dateParution = dateParution;
    }

    @Override
    public String toString() {
        return titre + " (" + genre + ") - " + (auteur != null ? auteur.getNom() : "Inconnu");
    }

    public String getTitre() {
        return titre;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getId() {
        return id;
    }

    public int getIsbn() {
        return isbn;
    }

    public Auteur getAuteur() {
        return auteur;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIsbn(int isbn) {
        this.isbn = isbn;
    }

    public void setAuteur(Auteur auteur) {
        this.auteur = auteur;
    }

    public LocalDate getDateParution() {
        return dateParution;
    }

    public void setDateParution(LocalDate dateParution) {
        this.dateParution = dateParution;
    }

}
