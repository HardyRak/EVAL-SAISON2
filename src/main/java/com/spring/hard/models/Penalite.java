package com.spring.hard.models;

import java.sql.Date;
import java.time.LocalTime;
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
@Table(name = "penalite")
@Getter
@Setter
public class Penalite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id_penalite;
    @ManyToOne
    @JoinColumn(name = "id_equipe", nullable = false)
    Equipe equipe;
    @ManyToOne
    @JoinColumn(name = "id_etape", nullable = false)
    Etapes etape;
    LocalTime temps;
    int etat;
}
