package com.wazzaby.android.wazzaby.model.data;

/**
 * Created by bossmaleo on 11/01/18.
 */

public class Categorie_prob {

    private int ID;
    private String Libelle;
    private String Lang;

    public Categorie_prob()
    {

    }

    public Categorie_prob(int id,String libelle,String lang)
    {
        this.ID = id;
        this.Libelle = libelle;
        this.Lang = lang;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getID() {
        return ID;
    }

    public String getLibelle() {
        return Libelle;
    }

    public void setLibelle(String libelle) {
        Libelle = libelle;
    }

    public String getLang() {
        return Lang;
    }

    public void setLang(String lang) {
        Lang = lang;
    }
}
