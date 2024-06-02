package com.spring.hard.models;

import java.sql.Date;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "coureur")
@Getter
@Setter
public class Coureur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id_coureur;
    String nom;
    int dossard;
    String genre;
    Date naissance;
    @ManyToOne
    @JoinColumn(name = "id_equipe", nullable = false)
    Equipe equipe;
}
