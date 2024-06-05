package com.spring.hard.service;
import org.hibernate.annotations.processing.SQL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.spring.hard.models.Equipe;
import com.spring.hard.models.Etapes;
import com.spring.hard.models.Penalite;
import com.spring.hard.repository.PenaliteRepository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.sql.PreparedStatement;
import java.sql.Time;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

@Service
public class PenaliteService {
    @Autowired
    private PenaliteRepository repository;
    @Autowired
    private 
    JdbcTemplate jdbcTemplate;
    public Penalite saveOrUpdate(Penalite entity) {
        return repository.save(entity);
    }

    public Optional<Penalite> getById(Long id) {
        return repository.findById(id);
    }

    public Page<Penalite> getAll(int page, int size, String fieldTri) {
        Sort sort = null;
        if (fieldTri != null && !fieldTri.isEmpty()) {
            String[] split = fieldTri.split(",");
            sort = Sort.by(Sort.Direction.fromString(split[1]), split[0]);
            return repository.findAll(PageRequest.of(page, size, sort));
        }
        return repository.findAll(PageRequest.of(page, size));
    }

    public List<Penalite> getAll() {
        return repository.findAll();
    }

    public List<Penalite> getAllNotRemove(){
        return repository.findByEtat(0);
    }
    
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public List<Penalite> getPenaliteEquipeEtape(Equipe equipe,Etapes etapes){
        return repository.findByEquipeAndEtapeAndEtat(equipe,etapes,0);
    }

    public void validationPenalite(int id_etape, int id_equipe, LocalTime temps) throws Throwable {
        String query = "SELECT addPenalite(?, ?, ?)";
        try{
            jdbcTemplate.update(query, id_etape, id_equipe, Time.valueOf(temps));
        }catch(DataAccessException e){
            throw e.getCause();
        }
    }

    public void validationSuppressionPenalite(int id_penalite) throws Throwable {
        String query = "SELECT removePenalite(?)";
        try{
            jdbcTemplate.update(query, id_penalite);
        }catch(DataAccessException e){
            throw e.getCause();
        }
    }

}
