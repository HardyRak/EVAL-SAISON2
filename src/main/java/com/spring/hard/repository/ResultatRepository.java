package com.spring.hard.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.spring.hard.models.Coureur;
import com.spring.hard.models.Equipe;
import com.spring.hard.models.Etapes;
import com.spring.hard.models.Resultat;
import com.spring.hard.vieuw.ClassementEquipe;

@Repository
public interface ResultatRepository extends JpaRepository<Resultat, Long> {
    Page<Resultat> findAll(Pageable pageable);
    List<Resultat> findByEtape(Etapes etapes,Sort sort);
    List<Resultat> findByEquipe(Equipe equipe,Sort sort);
    Resultat findByCoureurAndEtape(Coureur courseCoureur,Etapes etapes);
    List<Resultat> findByEtapeAndEquipe(Etapes etapes,Equipe equipe);
    List<Resultat> findByEtapeAndEquipe(Etapes etapes,Equipe equipe,Sort sort);
    List<Resultat> findByEtapeAndTempsArriveIsNull(Etapes etapes);
    List<Resultat> findByEtapeAndTempsArriveIsNotNull(Etapes etape);

    @Query(value = "SELECT r.id_coureur, SUM(r.point) as point, CAST(SUM(r.temps) AS VARCHAR) AS temps FROM Resultat r WHERE r.id_equipe = :id_equipe GROUP BY r.id_coureur ORDER BY point desc", nativeQuery = true)
    List<Object[]> getClassementEquipe(@Param("id_equipe") int id_equipe);

    @Query(value = "SELECT id_equipe, SUM(point) AS total_points FROM resultat GROUP BY id_equipe ORDER BY total_points desc;",nativeQuery = true)
    List<Object[]> getClassementDesEquipe();

    @Query(value = "WITH resultRank AS (" +
    "SELECT " +
    "id_resultat, " +
    "temps, " +
    "resultat.id_coureur, " +
    "id_etape, " +
    "id_equipe, " +
    "id_categorie, " +
    "DENSE_RANK() OVER (ORDER BY resultat.temps ASC) AS rank " +
    "FROM resultat " +
    "JOIN categorie_coureur ON resultat.id_coureur = categorie_coureur.id_coureur " +
    "WHERE id_etape = :id_etape AND id_categorie = :id_categorie" +
    ")," +
    "pointRank AS (" +
    "SELECT point, classement " +
    "FROM point" +
    ")" +
    "SELECT " +
    "resultRank.id_equipe, " +
    "MIN(resultRank.temps) AS min_temps, " +
    "MAX(resultRank.temps) AS max_temps, " +
    "COALESCE(SUM(pointRank.point), 0) AS total_point " +
    "FROM resultRank " +
    "LEFT JOIN pointRank ON resultRank.rank = pointRank.classement " +
    "GROUP BY resultRank.id_equipe " +
    "ORDER BY min_temps", nativeQuery = true)
    List<Object[]> getClassementCategorie(@Param("id_etape") int id_etape, @Param("id_categorie") int id_categorie);
}