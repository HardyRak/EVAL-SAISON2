package com.spring.hard.csvObject;

import java.sql.Date;
import java.time.ZonedDateTime;

import com.spring.hard.function.Function;

public class ResultatCsv {
    int etape_rang;
    int dossard;
    String nom;
    String genre;
    Date naissance;
    String equipe;
    ZonedDateTime arrive;

    public int getEtape_rang() {
        return etape_rang;
    }
    public void setEtape_rang(String etape_rang) throws Exception {
        int etape_rangD=0;
        try{
            etape_rangD=Integer.parseInt(etape_rang);
            this.etape_rang=etape_rangD;
        }catch(Exception e){
            throw new Exception("Verifier donnee du colonne rang");
        }
    }

    public int getDossard() {
        return dossard;
    }
    public void setDossard(String dossard) throws Exception {
        int dossardD=0;
        try{
            dossardD=Integer.parseInt(dossard);
            this.dossard=dossardD;
        }catch(Exception e){
            throw new Exception("Verifier donnee du colonne dossard");
        }
    }

    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getGenre() {
        return genre;
    }
    public void setGenre(String genre) throws Exception {
        if(!genre.trim().equals("M") && !genre.trim().equals("F")){
            throw new Exception("Genre invalide =>"+genre);
        }
        this.genre = genre;
    }

    public Date getNaissance() {
        return naissance;
    }
    public void setNaissance(String naissance) throws Exception {
        try{
            Date naissanceD=Function.stringToDate(naissance);
            this.naissance=naissanceD;
        }catch(Exception e){
            throw new Exception("Verifier donnee du colonne date de naissance");
        }
    }

    public String getEquipe() {
        return equipe;
    }
    public void setEquipe(String equipe) {
        this.equipe = equipe;
    }

    public ZonedDateTime getArrive() {
        return arrive;
    }
    public void setArrive(String arrive) throws Exception {
        try{
            ZonedDateTime arriveD = Function.stringToZoneDateTime(arrive);
            this.arrive=arriveD;
        }catch(Exception e){
            throw new Exception("Verifier donnee du colonne arrive");
        }
    }

}
