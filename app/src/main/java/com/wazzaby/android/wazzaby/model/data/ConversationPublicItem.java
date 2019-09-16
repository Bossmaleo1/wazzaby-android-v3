package com.wazzaby.android.wazzaby.model.data;

import android.content.Context;

/**
 * Created by bossmaleo on 09/11/17.
 */

public class ConversationPublicItem {

    private String nameMembreProb;
    private String Datetime;
    private String ImageID;
    private String Contenu;
    private int IconComment;
    private String commentnumber;
    private int ID_EME;
    private int ID;
    private Context context;
    private String etat_photo_status;
    private String status_photo;
    private int anonymous;
    private boolean visibility;
    private int countjaime;
    private int countjaimepas;
    private int id_recepteur;
    private int checkmention;
    private int id_checkmention;
    private int id_photo;
    private String pushkey_recepteur;


    public ConversationPublicItem(Context context,int ID_EME,int ID,String Contenu,String nameMembreProb,String datetime,String commentnumber
            ,String ImageID,int IconComment,String etat_photo_status,String status_photo
            ,int anonymous,boolean visibility,int countjaime,int countjaimepas,int id_recepteur,int checkmention
            ,int id_checkmention,int id_photo,String pushkey_recepteur)
    {
        this.Contenu = Contenu;
        this.nameMembreProb = nameMembreProb;
        this.Datetime = datetime;
        this.commentnumber = commentnumber;
        this.ImageID = ImageID;
        this.IconComment = IconComment;
        this.ID = ID;
        this.ID_EME = ID_EME;
        this.context = context;
        this.etat_photo_status = etat_photo_status;
        this.status_photo = status_photo;
        this.anonymous = anonymous;
        this.visibility = visibility;
        this.countjaime = countjaime;
        this.countjaimepas = countjaimepas;
        this.id_recepteur = id_recepteur;
        this.checkmention = checkmention;
        this.id_checkmention = id_checkmention;
        this.id_photo = id_photo;
        this.pushkey_recepteur = pushkey_recepteur;
    }

    public Context getContext1() { return context; }

    public void setContext1(Context context) {
        this.context = context;
    }

    public String getCommentnumber() {
        return commentnumber;
    }

    public void setCommentnumber(String commentnumber) {
        this.commentnumber = commentnumber;
    }

    public void setContenu(String contenu) {
        Contenu = contenu;
    }

    public String getContenu() {
        return Contenu;
    }

    public void setImageID(String imageID) {
        ImageID = imageID;
    }

    public String getImageID() {
        return ImageID;
    }

    public int getIconComment() {
        return IconComment;
    }

    public String getDatetime() {
        return Datetime;
    }

    public String getNameMembreProb() {
        return nameMembreProb;
    }

    public void setDatetime(String datetime) {
        Datetime = datetime;
    }

    public void setIconComment(int iconComment) {
        IconComment = iconComment;
    }

    public void setNameMembreProb(String nameMembreProb) {
        this.nameMembreProb = nameMembreProb;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getID_EME() {
        return ID_EME;
    }

    public int getID() {
        return ID;
    }

    public void setID_EME(int ID_EME) {
        this.ID_EME = ID_EME;
    }

    public String getEtat_photo_status() {
        return etat_photo_status;
    }

    public String getStatus_photo() {
        return status_photo;
    }


    public void setEtat_photo_status(String etat_photo_status) {
        this.etat_photo_status = etat_photo_status;
    }

    public void setStatus_photo(String status_photo) {
        this.status_photo = status_photo;
    }

    public Context getContext() {
        return context;
    }

    public int getAnonymous() {
        return anonymous;
    }

    public int getCountjaime() {
        return countjaime;
    }

    public int getCountjaimepas() {
        return countjaimepas;
    }

    public int getId_checkmention() {
        return id_checkmention;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public int getId_photo() {
        return id_photo;
    }

    public boolean isVisibility() {
        return visibility;
    }

    public int getId_recepteur() {
        return id_recepteur;
    }

    public int getCheckmention() {
        return checkmention;
    }

    public void setAnonymous(int anonymous) {
        this.anonymous = anonymous;
    }

    public void setCheckmention(int checkmention) {
        this.checkmention = checkmention;
    }

    public void setCountjaime(int countjaime) {
        this.countjaime = countjaime;
    }

    public void setCountjaimepas(int countjaimepas) {
        this.countjaimepas = countjaimepas;
    }

    public void setId_checkmention(int id_checkmention) {
        this.id_checkmention = id_checkmention;
    }

    public void setId_photo(int id_photo) {
        this.id_photo = id_photo;
    }

    public void setId_recepteur(int id_recepteur) {
        this.id_recepteur = id_recepteur;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    public String getPushkey_recepteur() {
        return pushkey_recepteur;
    }

    public void setPushkey_recepteur(String pushkey_recepteur) {
        this.pushkey_recepteur = pushkey_recepteur;
    }
}
