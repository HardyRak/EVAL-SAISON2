package com.spring.hard.models;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "equipe")
@Getter
@Setter
public class Equipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id_equipe;
    private String authentification;
    private String motDePasse;
    private int  isAdmin;
    @OneToMany(mappedBy = "equipe",fetch = FetchType.EAGER)
    private List<Coureur> coureurs;
}