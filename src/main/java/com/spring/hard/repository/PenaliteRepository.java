package com.spring.hard.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.spring.hard.models.Equipe;
import com.spring.hard.models.Etapes;
import com.spring.hard.models.Penalite;
import org.springframework.data.repository.query.Param;

@Repository
public interface PenaliteRepository extends JpaRepository<Penalite, Long> {
    Page<Penalite> findAll(Pageable pageable);
    List<Penalite> findByEquipeAndEtapeAndEtat(Equipe equipe,Etapes etapes,int etat);
    List<Penalite> findByEtat(int etat);
}
