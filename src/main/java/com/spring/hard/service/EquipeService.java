package com.spring.hard.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spring.hard.models.Equipe;
import com.spring.hard.repository.EquipeRepository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import java.util.List;
import org.springframework.data.domain.Sort;

@Service
public class EquipeService {
    @Autowired
    private EquipeRepository repository;

    public Equipe saveOrUpdate(Equipe entity) {
        return repository.save(entity);
    }

    public Optional<Equipe> getById(Long id) {
        return repository.findById(id);
    }

    public Page<Equipe> getAll(int page, int size, String fieldTri) {
        Sort sort = null;
        if (fieldTri != null && !fieldTri.isEmpty()) {
            String[] split = fieldTri.split(",");
            sort = Sort.by(Sort.Direction.fromString(split[1]), split[0]);
            return repository.findAll(PageRequest.of(page, size, sort));
        }
        return repository.findAll(PageRequest.of(page, size));
    }

    public List<Equipe> getAll() {
        return repository.findAll();
    }

    public Equipe authentification(String authetification,String motDepasse){  
        return repository.findByAuthentificationAndMotDePasse(authetification, motDepasse);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
