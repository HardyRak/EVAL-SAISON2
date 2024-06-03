package com.spring.hard.csvObject;

import java.time.ZonedDateTime;

import com.spring.hard.function.Function;

public class EtapeCsv {
    String etape;
    double longueur;
    int nbCoureur;
    int rang;
    ZonedDateTime depart;

    public String getEtape() {
        return etape;
    }
    public void setEtape(String etape) {
        this.etape = etape;
    }

    public double getLongueur() {
        return longueur;
    }
    public void setLongueur(String longueur) throws Exception {
        double longueurD=0;
        try {
            longueur=longueur.trim().replace(",", ".");
            longueurD=Double.parseDouble(longueur);
            this.longueur = longueurD;
        } catch (Exception e) {
            throw new Exception("Verifier donnee du colonne longueur");
        }
    }

    public int getNbCoureur() {
        return nbCoureur;
    }
    public void setNbCoureur(String nbCoureur) throws Exception {
        int nbCoureurD=0;
        try {
            nbCoureurD=Integer.parseInt(nbCoureur);
            this.nbCoureur = nbCoureurD;
        } catch (Exception e) {
            throw new Exception("Verifier donnee du colonne nombre de coureur");
        }
    }

    public int getRang() {
        return rang;
    }
    public void setRang(String rang) throws Exception {
        int rangD=0;
        try{
            rangD=Integer.parseInt(rang);
            this.rang=rangD;
        }catch(Exception e){
            throw new Exception("Verifier donnee du colonne rang");
        }
    }

    public ZonedDateTime getDepart() {
        return depart;
    }
    public void setDepart(String depart) throws Exception {
        try{
            ZonedDateTime departD = Function.stringToZoneDateTime(depart);
            this.depart=departD;
        }catch(Exception e){
            throw new Exception("Verifier donnee du colonne depart "+e.getMessage());
        }
    }
    
}
