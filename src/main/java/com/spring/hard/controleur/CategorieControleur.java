package com.spring.hard.controleur;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.google.gson.Gson;
import com.spring.hard.models.Categorie;
import com.spring.hard.service.CategorieService;
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
public class CategorieControleur {
    @Autowired
    private CategorieService service;
    @CrossOrigin(origins = "*")
    @GetMapping("/api/categorie")
    public ResponseEntity<List<Categorie>> getAllCategorieApi() {
        List<Categorie> entities = service.getAll();
        return new ResponseEntity<>(entities, HttpStatus.OK);
    }

    @GetMapping("/categorie")
    public String getAllcategorie(@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue="") String tri, Model model) {
        Page<Categorie> entities = service.getAll(page,5,tri);
        model.addAttribute("categories",entities);
        return "pages/categorie/liste";
    }

    @GetMapping("/categorie/ajout")
    public String formInsert(Model model) {
        return "pages/categorie/ajout";
    }
}
