package com.spring.hard.models;

import java.sql.Date;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "resultat")
@Getter
@Setter
public class Resultat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id_resultat;
    @ManyToOne
    @JoinColumn(name = "id_etape", nullable = false)
    Etapes etape;
    @ManyToOne
    @JoinColumn(name = "id_coureur", nullable = false)
    Coureur coureur;
    ZonedDateTime tempsArrive;
    LocalTime temps;
    @ManyToOne
    @JoinColumn(name = "id_equipe",nullable = false)
    Equipe equipe;
    int point;

}
