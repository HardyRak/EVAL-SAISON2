package com.spring.hard.models;

import java.sql.Date;
import java.time.ZonedDateTime;
import java.util.List;

import com.spring.hard.vieuw.ClassementGEquipe;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "etapes")
@Getter
@Setter
public class Etapes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id_etape;
    String nom;
    double longueur;
    int nombreEquipe;
    ZonedDateTime tempsDepart;
    int rang;
    @ManyToOne
    @JoinColumn(name = "id_course", nullable = false)
    Course course;
    @OneToMany(mappedBy = "etape",fetch = FetchType.EAGER)
    List<Resultat> resultats;
    
    @Transient
    List<ClassementGEquipe> classementGEquipes;

}
