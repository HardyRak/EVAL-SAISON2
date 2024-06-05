package com.spring.hard.vieuw;

import java.time.LocalTime;
import java.time.ZonedDateTime;

import com.spring.hard.models.Coureur;
import com.spring.hard.models.Equipe;
import com.spring.hard.models.Etapes;
import com.spring.hard.models.Resultat;

public class ResultatCoureur {
    public Resultat resultat;
    public int rang;
    public LocalTime penalite;
    public ZonedDateTime temps_arrivePenal;
    public int point;
    LocalTime temps_sans_penale;

    
    public Resultat getResultat() {
        return resultat;
    }
    public void setResultat(Resultat resultat) {
        this.resultat = resultat;
    }
    public LocalTime getPenalite() {
        return penalite;
    }
    public void setPenalite(LocalTime penalite) {
        this.penalite = penalite;
    }
    public ZonedDateTime getTemps_arrivePenal() {
        return temps_arrivePenal;
    }
    public void setTemps_arrivePenal(ZonedDateTime temps_arrivePenal) {
        this.temps_arrivePenal = temps_arrivePenal;
    }
    public int getRang() {
        return rang;
    }
    public void setRang(int rang) {
        this.rang = rang;
    }
    public int getPoint() {
        return point;
    }
    public void setPoint(int point) {
        this.point = point;
    }
    public LocalTime getTemps_sans_penale() {
        return temps_sans_penale;
    }
    public void setTemps_sans_penale(LocalTime temps_sans_penale) {
        this.temps_sans_penale = temps_sans_penale;
    }

    

}
