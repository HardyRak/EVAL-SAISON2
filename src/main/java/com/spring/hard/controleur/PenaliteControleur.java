package com.spring.hard.controleur;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.google.gson.Gson;
import com.spring.hard.models.Course;
import com.spring.hard.models.Equipe;
import com.spring.hard.models.Etapes;
import com.spring.hard.models.Penalite;
import com.spring.hard.service.CourseService;
import com.spring.hard.service.EquipeService;
import com.spring.hard.service.PenaliteService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import java.util.HashSet;
import java.util.Set;

@Controller
public class PenaliteControleur {
    @Autowired
    private PenaliteService service;
    @Autowired
    private HttpSession httpSession;
    @Autowired
    private EquipeService equipeService;
    @Autowired
    private CourseService courseService;
    @CrossOrigin(origins = "*")
    @GetMapping("/api/penalite")
    public ResponseEntity<List<Penalite>> getAllPenaliteApi() {
        List<Penalite> entities = service.getAll();
        return new ResponseEntity<>(entities, HttpStatus.OK);
    }

    @GetMapping("/penalite")
    public String getAllpenalite(@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue="") String tri, Model model) {
        Page<Penalite> entities = service.getAll(page,5,tri);
        model.addAttribute("penalites",entities);
        return "pages/penalite/liste";
    }

    @GetMapping("/penalite/ajout")
    public String formInsert(Model model) {
        return "pages/penalite/ajout";
    }

    @GetMapping("/penalite/liste")
    public String listePenalite(Model model){
        List<Penalite> penalites=service.getAllNotRemove();
        model.addAttribute("penalites",penalites);
        return Index.redirectLogin(httpSession.getAttribute("admin"), "admin", "pages/admin/listePenalite");
    }

    @GetMapping("/penalite/form")
    public String formPenalite(Model model){
        Course course=courseService.getById((long)2).get();
        List<Etapes> etapes=course.getEtapes();
        List<Equipe> equipe=equipeService.getAll();
        model.addAttribute("etapes",etapes);
        model.addAttribute("equipes",equipe);
        return Index.redirectLogin(httpSession.getAttribute("admin"), "admin", "pages/admin/formPenalite");
    }

    @PostMapping("/penalite/ajout")
    public String validationPenalite(Model model,@RequestParam String temps,@RequestParam int id_etape,@RequestParam int id_equipe){
        LocalTime localTime=LocalTime.parse(temps);
        try {
            service.validationPenalite(id_etape, id_equipe, localTime);
            List<Penalite> penalites=service.getAllNotRemove();
            model.addAttribute("penalites",penalites);
            return Index.redirectLogin(httpSession.getAttribute("admin"), "admin", "pages/admin/listePenalite");
        } catch (Throwable e) {
            e.printStackTrace();
            if (e.getMessage().contains("Un résultat a été retourné alors qu'aucun n'était attendu.")) {
                return "redirect:/penalite/liste";
            }else{
                return Index.redirectLogin(httpSession.getAttribute("admin"), "admin", "redirect:/penalite/form?erreur="+e.getMessage());
            }
        }
    }

    @GetMapping("/penalite/suppress")
    public String validationSupprePenalite(Model model, @RequestParam int id_penalite) {
        try {
            service.validationSuppressionPenalite(id_penalite);
            List<Penalite> penalites = service.getAllNotRemove();
            model.addAttribute("penalites", penalites);
            return Index.redirectLogin(httpSession.getAttribute("admin"), "admin", "pages/admin/listePenalite");
        } catch (Throwable e) {
            e.printStackTrace();
            if (e.getMessage().contains("Un résultat a été retourné alors qu'aucun n'était attendu.")) {
                return "redirect:/penalite/liste";
            } else {
                return "redirect:/penalite/liste?erreur=" + e.getMessage();
            }
        }
    }

}