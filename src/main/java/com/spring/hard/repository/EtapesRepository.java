package com.spring.hard.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.spring.hard.models.Course;
import com.spring.hard.models.Etapes;
import org.springframework.data.repository.query.Param;

@Repository
public interface EtapesRepository extends JpaRepository<Etapes, Long> {
    Page<Etapes> findAll(Pageable pageable);
}
