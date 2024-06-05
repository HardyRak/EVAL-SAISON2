package com.spring.hard.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.hard.models.Course;
import com.spring.hard.models.Etapes;
import com.spring.hard.models.Resultat;
import com.spring.hard.repository.EtapesRepository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import java.util.List;
import org.springframework.data.domain.Sort;

@Service
public class EtapesService {
    @Autowired
    private EtapesRepository repository;

    public Etapes saveOrUpdate(Etapes entity) {
        return repository.save(entity);
    }

    public Optional<Etapes> getById(Long id) {
        return repository.findById(id);
    }

    public Page<Etapes> getAll(int page, int size, String fieldTri) {
        Sort sort = null;
        if (fieldTri != null && !fieldTri.isEmpty()) {
            String[] split = fieldTri.split(",");
            sort = Sort.by(Sort.Direction.fromString(split[1]), split[0]);
            return repository.findAll(PageRequest.of(page, size, sort));
        }
        return repository.findAll(PageRequest.of(page, size));
    }

    public List<Etapes> getAll() {
        Sort sort=Sort.by(Sort.Direction.ASC,"rang");
        return repository.findAll(sort);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }


}
