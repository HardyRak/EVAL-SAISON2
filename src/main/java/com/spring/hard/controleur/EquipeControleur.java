package com.spring.hard.controleur;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.google.gson.Gson;
import com.spring.hard.models.Equipe;
import com.spring.hard.service.EquipeService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import java.util.HashSet;
import java.util.Set;

@Controller
public class EquipeControleur {
    @Autowired
    private EquipeService service;
    @CrossOrigin(origins = "*")
    @GetMapping("/api/Equipe")
    public ResponseEntity<List<Equipe>> getAllEquipeApi() {
        List<Equipe> entities = service.getAll();
        return new ResponseEntity<>(entities, HttpStatus.OK);
    }

    @GetMapping("/Equipe")
    public String getAllEquipe(@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue="") String tri, Model model) {
        Page<Equipe> entities = service.getAll(page,5,tri);
        model.addAttribute("Equipes",entities);
        return "pages/Equipe/liste";
    }

    @GetMapping("/Equipe/ajout")
    public String formInsert(Model model) {
        return "pages/Equipe/ajout";
    }
}
