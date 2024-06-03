package com.spring.hard.controleur;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.spring.hard.models.Categorie;
import com.spring.hard.models.Course;
import com.spring.hard.models.Equipe;
import com.spring.hard.models.Etapes;
import com.spring.hard.models.Resultat;
import com.spring.hard.service.CategorieService;
import com.spring.hard.service.CourseService;
import com.spring.hard.service.EquipeService;
import com.spring.hard.service.ResultatService;
import com.spring.hard.vieuw.ClassementGEquipe;

import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;

@Controller
public class ResultatControleur {
    @Autowired
    private ResultatService service;
    @Autowired
    private CourseService courseService;
    @Autowired
    private EquipeService equipeService;
    @Autowired
    private HttpSession httpSession;
    @Autowired
    private CategorieService categorieService;
    @CrossOrigin(origins = "*")
    @GetMapping("/api/resultat")
    public ResponseEntity<List<Resultat>> getAllResultatApi() {
        List<Resultat> entities = service.getAll();
        return new ResponseEntity<>(entities, HttpStatus.OK);
    }

    @GetMapping("/resultat")
    public String getAllresultat(@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue="") String tri, Model model) {
        Page<Resultat> entities = service.getAll(page,5,tri);
        model.addAttribute("resultats",entities);
        return "pages/resultat/liste";
    }

    @GetMapping("/resultat/ajout")
    public String formInsert(Model model) {
        return "pages/resultat/ajout";
    }

    @GetMapping("/classement/etape")
    public String classement(Model model){
        Course course=courseService.getById((long)2).get();
        List<Etapes> etapes=course.getEtapes();
        for (int i = 0; i < etapes.size(); i++) {
            etapes.get(i).setResultats(service.getByEtape(etapes.get(i)));
        }
        model.addAttribute("etapes",etapes);
        return "pages/all/classementEtape";
    }

    @GetMapping("/classement/etape/equipe")
    public String classementParCategorie(Model model,@RequestParam String id_categorie){
        if(Integer.parseInt(id_categorie)<=0){
            return "redirect:/classement/equipe/generale";
        }
        Course course=courseService.getById((long)2).get();
        List<Etapes> etapes=course.getEtapes();
        for (int i = 0; i < etapes.size(); i++) {
            etapes.get(i).setResultats(service.getByEtape(etapes.get(i)));
            etapes.get(i).setClassementGEquipes(service.getClassementParCategorie((int)etapes.get(i).getId_etape(), Integer.parseInt(id_categorie)));
        }
        model.addAttribute("categorie",categorieService.getById(Long.parseLong(id_categorie)).get());
        model.addAttribute("etapes",etapes);
        return "pages/all/classementCategorie";
    }

    @GetMapping("/classement/categorie/generale")
    public String classementParCategorieGenerale(Model model,@RequestParam String id_categorie){
        List<ClassementGEquipe> classementGEquipes=service.getClassementGeneralCategorie(Integer.parseInt(id_categorie));
        model.addAttribute("classement",classementGEquipes);
        return "pages/all/classementCategorieGenerale";
    }

    @GetMapping("/classement/admin/equipe")
    public String classementEquipe(Model model){
        List<Equipe> equipes=equipeService.getAll();
        model.addAttribute("equipes",equipes);
        return Index.redirectLogin(httpSession.getAttribute("admin"), "admin",  "pages/admin/listeEquipe");
    }

    @GetMapping("/classement/equipe")
    public String resultat(Model model,@RequestParam String id_equipe){
        Equipe equipe=equipeService.getById((long)Integer.parseInt(id_equipe)).get();
        List<Resultat> resultats=service.getClassementEquipe((int)equipe.getId_equipe());
        model.addAttribute("resultats",resultats);
        return "pages/all/resultEquipe";
    }

    @GetMapping("/classement/user/equipe")
    public String classementEquipeUser(Model model){
        Equipe equipe=(Equipe)httpSession.getAttribute("user");
        List<Resultat> resultats = service.getClassementEquipe((int)equipe.getId_equipe());
        List<Equipe> equipes=equipeService.getAll();
        model.addAttribute("equipes",equipes);
        model.addAttribute("resultats",resultats);
        return Index.redirectLogin(httpSession.getAttribute("user"), "user",  "pages/client/listeEquipe");
    }

    @PostMapping("/validation/coureur")
    public String validationTempsCoureur(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime tempsArrive,
            @RequestParam int id_resultat) {

        Resultat resultat = service.getById((long) id_resultat).get();
        ZonedDateTime zonedDateTime = tempsArrive.atZone(ZoneId.systemDefault());
        resultat.setTempsArrive(zonedDateTime);
        try{
            service.saveOrUpdate(resultat);
        }catch(Exception e){
            return "redirect:/formUpdate?msg="+e.getCause().getCause().getLocalizedMessage()+"&&etape=" + resultat.getEtape().getId_etape();
        }
        return "redirect:/formUpdate?etape=" + resultat.getEtape().getId_etape();
    }

    @GetMapping("/classement/equipe/generale")
    public String classementDesEquipe(Model model){
        List<Categorie> categories=categorieService.getAll();
        model.addAttribute("categories",categories);
        model.addAttribute("classement",service.getResultatDesEquipe());
        return "/pages/all/classementDesEquipe";
    }
}
