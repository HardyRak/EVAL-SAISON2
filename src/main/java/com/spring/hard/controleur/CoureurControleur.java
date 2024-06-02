package com.spring.hard.controleur;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.google.gson.Gson;
import com.spring.hard.models.Coureur;
import com.spring.hard.service.CoureurService;
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
public class CoureurControleur {
    @Autowired
    private CoureurService service;
    @CrossOrigin(origins = "*")
    @GetMapping("/api/coureur")
    public ResponseEntity<List<Coureur>> getAllCoureurApi() {
        List<Coureur> entities = service.getAll();
        return new ResponseEntity<>(entities, HttpStatus.OK);
    }

    @GetMapping("/coureur")
    public String getAllcoureur(@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue="") String tri, Model model) {
        Page<Coureur> entities = service.getAll(page,5,tri);
        model.addAttribute("coureurs",entities);
        return "pages/coureur/liste";
    }

    @GetMapping("/coureur/ajout")
    public String formInsert(Model model) {
        return "pages/coureur/ajout";
    }
}
