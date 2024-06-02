package com.spring.hard.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.hard.models.Coureur;
import com.spring.hard.models.Course;
import com.spring.hard.models.Equipe;
import com.spring.hard.models.Etapes;
import com.spring.hard.models.Resultat;
import com.spring.hard.repository.ResultatRepository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import java.util.List;
import org.springframework.data.domain.Sort;

@Service
public class ResultatService {
    @Autowired
    private ResultatRepository repository;

    public Resultat saveOrUpdate(Resultat entity) {
        return repository.save(entity);
    }

    public Optional<Resultat> getById(Long id) {
        return repository.findById(id);
    }

    public Page<Resultat> getAll(int page, int size, String fieldTri) {
        Sort sort = null;
        if (fieldTri != null && !fieldTri.isEmpty()) {
            String[] split = fieldTri.split(",");
            sort = Sort.by(Sort.Direction.fromString(split[1]), split[0]);
            return repository.findAll(PageRequest.of(page, size, sort));
        }
        return repository.findAll(PageRequest.of(page, size));
    }

    public List<Resultat> getAll() {
        return repository.findAll();
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public List<Resultat> getByEtape(Etapes etapes){
        return repository.findByEtape(etapes,Sort.by("tempsArrive").descending());
    }

    public List<Resultat> getByEquipe(Equipe equipe){
        return repository.findByEquipe(equipe,Sort.by("tempsArrive").descending());
    }

    public Resultat getByCoureurEtEtape(Coureur coureur,Etapes etapes){
        return repository.findByCoureurAndEtape(coureur, etapes);
    }

    public List<Resultat> getByEtapeAndEquipe(Etapes etapes,Equipe equipe){
        return repository.findByEtapeAndEquipe(etapes, equipe);
    }

    public List<Resultat> getByEtapeAndTempsArriveIsNull(Etapes etapes){
        return repository.findByEtapeAndTempsArriveIsNull(etapes);
    }

    public List<Resultat> getByEtapeAndTempsArriveIsNotNull(Etapes etapes){
        return repository.findByEtapeAndTempsArriveIsNotNull(etapes);
    }

}
