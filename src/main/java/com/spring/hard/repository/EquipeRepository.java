package com.spring.hard.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.spring.hard.models.Equipe;
import org.springframework.data.repository.query.Param;

@Repository
public interface EquipeRepository extends JpaRepository<Equipe, Long> {
    Page<Equipe> findAll(Pageable pageable);
    Equipe findByAuthentificationAndMotDePasse(String authentification,String motDepasse);
    @Query(value = "SELECT c.authentification,c.is_admin,c.mot_de_passe,c.id_equipe FROM equipe c where c.is_admin<>1",nativeQuery = true)
    List<Equipe> findAll();
}