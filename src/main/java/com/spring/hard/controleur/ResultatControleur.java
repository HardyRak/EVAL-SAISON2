package com.spring.hard.controleur;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.spring.hard.models.Course;
import com.spring.hard.models.Equipe;
import com.spring.hard.models.Etapes;
import com.spring.hard.models.Resultat;
import com.spring.hard.service.CourseService;
import com.spring.hard.service.EquipeService;
import com.spring.hard.service.ResultatService;

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
        Course course=courseService.getById((long)1).get();
        List<Etapes> etapes=course.getEtapes();
        for (int i = 0; i < etapes.size(); i++) {
            etapes.get(i).setResultats(service.getByEtape(etapes.get(i)));
        }
        model.addAttribute("etapes",etapes);
        return "pages/all/classementEtape";
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
        List<Resultat> resultats=service.getByEquipe(equipe);
        model.addAttribute("resultats",resultats);
        return "pages/all/resultEquipe";
    }

    @GetMapping("/classement/user/equipe")
    public String classementEquipeUser(Model model){
        Equipe equipe=(Equipe)httpSession.getAttribute("user");
        List<Resultat> resultats = service.getByEquipe(equipe);
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
}
