package com.spring.hard.controleur;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.google.gson.Gson;
import com.spring.hard.models.Coureur;
import com.spring.hard.models.Course;
import com.spring.hard.models.Equipe;
import com.spring.hard.models.Etapes;
import com.spring.hard.models.Resultat;
import com.spring.hard.service.CoureurService;
import com.spring.hard.service.CourseService;
import com.spring.hard.service.EtapesService;
import com.spring.hard.service.ResultatService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import java.util.HashSet;
import java.util.Set;

@Controller
public class EtapesControleur {
    @Autowired
    private EtapesService service;
    @Autowired
    private HttpSession session;
    @Autowired
    private CourseService courseService;
    @Autowired
    private CoureurService coureurService;
    @Autowired
    private ResultatService resultatService;
    @CrossOrigin(origins = "*")
    @GetMapping("/api/etapes")
    public ResponseEntity<List<Etapes>> getAllEtapesApi() {
        List<Etapes> entities = service.getAll();
        return new ResponseEntity<>(entities, HttpStatus.OK);
    }

    @GetMapping("/etapes")
    public String getAlletapes(@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue="") String tri, Model model) {
        Page<Etapes> entities = service.getAll(page,5,tri);
        model.addAttribute("etapess",entities);
        return "pages/etapes/liste";
    }

    @GetMapping("/etapes/ajout")
    public String formInsert(Model model) {
        return "pages/etapes/ajout";
    }

    @GetMapping("/etapeEquipe")
    public String listeEtapeEquipe(Model model){
        Course cours=courseService.getById((long)2).get();
        model.addAttribute("etapes", cours.getEtapes());
        return Index.redirectLogin(session.getAttribute("user"), "user", "pages/client/etapeListe");
    }

    @GetMapping("/formAffect")
    public String formulaireAffect(Model model,@RequestParam String etape){
        Etapes etapes=service.getById((long)Integer.parseInt(etape)).get();
        int nombre=etapes.getNombreEquipe();
        Equipe equipe=(Equipe)session.getAttribute("user");
        List<Resultat> coureurAffect=resultatService.getByEtapeAndEquipe(etapes, equipe);
        if(coureurAffect.size()>=nombre){
            return Index.redirectLogin(session.getAttribute("user"), "user", "redirect:/coureur/etape?erreur=Nombre de coureur deja complet dans l'etape "+etapes.getNom());
        }
        model.addAttribute("coureurs",equipe.getCoureurs());
        model.addAttribute("etape",etapes);
        model.addAttribute("coureurAffect",coureurAffect);
        return Index.redirectLogin(session.getAttribute("user"), "user", "pages/client/FormJoueur");
    }

    @GetMapping("/coureur/etape")
    public String getCoureurEtape(Model model){
        List<Etapes> etapes=service.getAll();
        Equipe equipe=(Equipe)session.getAttribute("user");
        for (int i = 0; i < etapes.size(); i++) {
            etapes.get(i).setResultats(resultatService.getByCoureurAndEtapeOrder(etapes.get(i), equipe));
        }
        model.addAttribute("etapes",etapes);
        return Index.redirectLogin(session.getAttribute("user"), "user", "pages/client/CoureurEtape");
    }

    @PostMapping("/valideJoueur")
    public String valideJoueur(@RequestParam String[] coureur,@RequestParam String id_etape){
        Equipe equipe=(Equipe)session.getAttribute("user");
        Etapes etape=service.getById((long)Integer.parseInt(id_etape)).get();
        List<Resultat> deja=resultatService.getByEtapeAndEquipe(etape, equipe);
        if((coureur.length-deja.size()>0)){
            for (int i = 0; i < coureur.length; i++) {
                Coureur coureurs=coureurService.getById((long)Integer.parseInt(coureur[i])).get();
                Resultat valide=resultatService.getByCoureurEtEtape(coureurs, etape);
                if(valide==null){
                    Resultat result=new Resultat();
                    result.setEtape(etape);
                    result.setCoureur(coureurs);
                    result.setTemps(null);
                    result.setPoint(0);
                    result.setTempsArrive(null);
                    result.setEquipe(coureurs.getEquipe());
                    resultatService.saveOrUpdate(result);
                }else{
                    return "redirect:/formAffect?etape="+id_etape+"&&msg=Ce coureur est deja dans la liste";
                }
            }
            return "redirect:/formAffect?scc=Validation reussi&&etape="+id_etape;
        }
        return "redirect:/formAffect?msg=Nombre de coureur max deja atteint&&etape="+id_etape;
    }

    @GetMapping("/etapeAdmin")
    public String listeEtapeAdmin(Model model){
        Course cours=courseService.getById((long)2).get();
        model.addAttribute("etapes", service.getAll());
        return Index.redirectLogin(session.getAttribute("admin"), "admin", "pages/admin/etapeListe");
    }

    @GetMapping("/formUpdate")
    public String formUpdateTemps(Model model,@RequestParam String etape){
        Etapes etapes=service.getById((long)Integer.parseInt(etape)).get();
        List<Resultat> resultats=resultatService.getByEtapeAndTempsArriveIsNull(etapes);
        List<Resultat> resultatsArrive=resultatService.getByEtapeAndTempsArriveIsNotNull(etapes);
        ZonedDateTime currentDateTime = ZonedDateTime.now(ZoneId.of("America/New_York"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);
        model.addAttribute("currentDateTime", formattedDateTime);
        model.addAttribute("encour",resultats);
        model.addAttribute("arrive",resultatsArrive);
        return Index.redirectLogin(session.getAttribute("admin"), "admin", "pages/admin/formUpdate");
    }

}
