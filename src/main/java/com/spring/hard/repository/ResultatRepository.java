package com.spring.hard.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.spring.hard.models.Coureur;
import com.spring.hard.models.Equipe;
import com.spring.hard.models.Etapes;
import com.spring.hard.models.Resultat;

@Repository
public interface ResultatRepository extends JpaRepository<Resultat, Long> {
    Page<Resultat> findAll(Pageable pageable);
    List<Resultat> findByEtape(Etapes etapes,Sort sort);
    List<Resultat> findByEquipe(Equipe equipe,Sort sort);
    Resultat findByCoureurAndEtape(Coureur courseCoureur,Etapes etapes);
    List<Resultat> findByEtapeAndEquipe(Etapes etapes,Equipe equipe);
    List<Resultat> findByEtapeAndTempsArriveIsNull(Etapes etapes);
    List<Resultat> findByEtapeAndTempsArriveIsNotNull(Etapes etape);
}