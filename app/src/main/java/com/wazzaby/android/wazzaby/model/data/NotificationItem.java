package com.wazzaby.android.wazzaby.model.data;

import android.os.Parcel;
import android.os.Parcelable;

public class NotificationItem implements Parcelable {

    private int id_messagepublic;
    private String updated_messagepublic;
    private String libelle;
    private String updated;
    private int etat;
    private int id_type;
    private  int expediteur_id;
    private  int notification_id;
    private int id_libelle;
    private String name_messagepublic;
    private String nom;
    private String prenom;
    private String photo;
    private int countjaime;
    private int countjaimepas;
    private int checkmention;
    private int id_checkmention;
    private String user_photo_messagepublic;
    private String status_text_content_messagepublic;
    private String etat_photo_status_messagepublic;
    private String status_photo_messagepublic;

    public NotificationItem() {

    }

    public NotificationItem(Parcel parcel){
        this.id_messagepublic = parcel.readInt();
        this.updated_messagepublic = parcel.readString();
        this.libelle = parcel.readString();
        this.updated = parcel.readString();
        this.etat = parcel.readInt();
        this.id_type = parcel.readInt();
        this.expediteur_id = parcel.readInt();
        this.notification_id = parcel.readInt();
        this.id_libelle = parcel.readInt();
        this.name_messagepublic = parcel.readString();
        this.nom = parcel.readString();
        this.prenom = parcel.readString();
        this.photo = parcel.readString();
        this.countjaime = parcel.readInt();
        this.countjaimepas = parcel.readInt();
        this.checkmention = parcel.readInt();
        this.id_checkmention = parcel.readInt();
        this.user_photo_messagepublic = parcel.readString();
        this.status_text_content_messagepublic = parcel.readString();
        this.etat_photo_status_messagepublic = parcel.readString();
        this.status_photo_messagepublic = parcel.readString();
    }

    public NotificationItem(int id_messagepublic, String updated_messagepublic, String libelle, String updated, int etat, int id_type,int expediteur_id,
            int notification_id, int id_libelle, String name_messagepublic, String nom, String prenom, String photo, int countjaime, int countjaimepas,
            int checkmention, int id_checkmention,String user_photo_messagepublic,
            String status_text_content_messagepublic,
            String etat_photo_status_messagepublic,
            String status_photo_messagepublic) {

            this.id_messagepublic = id_messagepublic;
            this.updated_messagepublic = updated_messagepublic;
            this.libelle = libelle;
            this.updated = updated;
            this.etat = etat;
            this.id_type = id_type;
            this.expediteur_id = expediteur_id;
            this.notification_id = notification_id;
            this.id_libelle = id_libelle;
            this.name_messagepublic = name_messagepublic;
            this.nom = nom;
            this.prenom = prenom;
            this.photo = photo;
            this.countjaime = countjaime;
            this.countjaimepas = countjaimepas;
            this.checkmention = checkmention;
            this.id_checkmention = id_checkmention;
            this.user_photo_messagepublic = user_photo_messagepublic;
            this.status_text_content_messagepublic = status_text_content_messagepublic;
            this.etat_photo_status_messagepublic = etat_photo_status_messagepublic;
            this.status_photo_messagepublic = status_photo_messagepublic;

    }



    public String getUpdated() {
        return updated;
    }

    public int getExpediteur_id() {
        return expediteur_id;
    }

    public int getId_messagepublic() {
        return id_messagepublic;
    }

    public int getId_type() {
        return id_type;
    }

    public int getNotification_id() {
        return notification_id;
    }

    public int getEtat() {
        return etat;
    }

    public String getLibelle() {
        return libelle;
    }

    public String getNom() {
        return nom;
    }

    public String getUpdated_messagepublic() {
        return updated_messagepublic;
    }

    public int getId_libelle() {
        return id_libelle;
    }

    public String getPrenom() {
        return prenom;
    }

    public int getCheckmention() {
        return checkmention;
    }

    public int getCountjaime() {
        return countjaime;
    }

    public String getName_messagepublic() {
        return name_messagepublic;
    }

    public int getCountjaimepas() {
        return countjaimepas;
    }

    public int getId_checkmention() {
        return id_checkmention;
    }

    public String getEtat_photo_status_messagepublic() {
        return etat_photo_status_messagepublic;
    }

    public String getPhoto() {
        return photo;
    }

    public String getStatus_photo_messagepublic() {
        return status_photo_messagepublic;
    }

    public String getStatus_text_content_messagepublic() {
        return status_text_content_messagepublic;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public void setEtat(int etat) {
        this.etat = etat;
    }

    public void setExpediteur_id(int expediteur_id) {
        this.expediteur_id = expediteur_id;
    }

    public void setId_libelle(int id_libelle) {
        this.id_libelle = id_libelle;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setId_messagepublic(int id_messagepublic) {
        this.id_messagepublic = id_messagepublic;
    }

    public void setId_type(int id_type) {
        this.id_type = id_type;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public void setNotification_id(int notification_id) {
        this.notification_id = notification_id;
    }

    public void setName_messagepublic(String name_messagepublic) {
        this.name_messagepublic = name_messagepublic;
    }

    public void setUpdated_messagepublic(String updated_messagepublic) {
        this.updated_messagepublic = updated_messagepublic;
    }

    public String getUser_photo_messagepublic() {
        return user_photo_messagepublic;
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

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setEtat_photo_status_messagepublic(String etat_photo_status_messagepublic) {
        this.etat_photo_status_messagepublic = etat_photo_status_messagepublic;
    }

    public void setId_checkmention(int id_checkmention) {
        this.id_checkmention = id_checkmention;
    }

    public void setStatus_photo_messagepublic(String status_photo_messagepublic) {
        this.status_photo_messagepublic = status_photo_messagepublic;
    }

    public void setStatus_text_content_messagepublic(String status_text_content_messagepublic) {
        this.status_text_content_messagepublic = status_text_content_messagepublic;
    }

    public void setUser_photo_messagepublic(String user_photo_messagepublic) {
        this.user_photo_messagepublic = user_photo_messagepublic;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(id_messagepublic);
        dest.writeString(updated_messagepublic);
        dest.writeString(libelle);
        dest.writeString(updated);
        dest.writeInt(etat);
        dest.writeInt(id_type);
        dest.writeInt(expediteur_id);
        dest.writeInt(notification_id);
        dest.writeInt(id_libelle);
        dest.writeString(name_messagepublic);
        dest.writeString(nom);
        dest.writeString(prenom);
        dest.writeString(photo);
        dest.writeInt(countjaime);
        dest.writeInt(countjaimepas);
        dest.writeInt(checkmention);
        dest.writeInt(id_checkmention);
        dest.writeString(user_photo_messagepublic);
        dest.writeString(status_text_content_messagepublic);
        dest.writeString(etat_photo_status_messagepublic);
        dest.writeString(status_photo_messagepublic);
    }

    public static final Parcelable.Creator<NotificationItem> CREATOR = new Parcelable.Creator<NotificationItem>(){

        @Override
        public NotificationItem createFromParcel(Parcel parcel) {
            return new NotificationItem(parcel);
        }

        @Override
        public NotificationItem[] newArray(int size) {
            return new NotificationItem[0];
        }
    };

}
