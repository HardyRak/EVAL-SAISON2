package com.spring.hard.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.hard.function.Function;
import com.spring.hard.models.Coureur;
import com.spring.hard.models.Course;
import com.spring.hard.models.Equipe;
import com.spring.hard.models.Etapes;
import com.spring.hard.models.Penalite;
import com.spring.hard.models.Resultat;
import com.spring.hard.repository.ResultatRepository;
import com.spring.hard.vieuw.ClassementEquipe;
import com.spring.hard.vieuw.ClassementGEquipe;
import com.spring.hard.vieuw.ResultatCoureur;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.data.domain.Sort;

@Service
public class ResultatService {
    @Autowired
    private ResultatRepository repository;
    @Autowired
    private CoureurService coureurService;
    @Autowired
    private EquipeService equipeService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private PenaliteService penaliteService;

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
        return repository.findByEtape(etapes,Sort.by("point").descending());
    }

    public List<Resultat> getByEquipe(Equipe equipe){
        return repository.findByEquipe(equipe,Sort.by("point").descending());
    }

    public List<Resultat> getClassementEquipe(int id_equipe){
        List<Object[]> result=repository.getClassementEquipe(id_equipe);
        List<Resultat> resultats=new ArrayList<>();
        for (Object[] row : result) {
            Resultat resultat = new Resultat();
            resultat.setCoureur(coureurService.getById((long) row[0]).get());
            resultat.setPoint(Integer.parseInt( row[1].toString()));
            
            resultat.setTemps(LocalTime.parse(row[2].toString()));
        
            resultats.add(resultat);
        }
        return resultats;
    }

    public List<ClassementGEquipe> getResultatDesEquipe(){
        List<ClassementGEquipe> classement=new ArrayList<>();
        List<Object[]> classementBase=repository.getClassementDesEquipe();
        for (int i = 0; i < classementBase.size(); i++) {
            ClassementGEquipe classe=new ClassementGEquipe();
            classe.setEquipe(equipeService.getById((long)Integer.parseInt(classementBase.get(i)[0].toString())).get());
            classe.setPoint(Integer.parseInt(classementBase.get(i)[1].toString()));
            classement.add(classe);
        }
        return classement;
    }

    public List<ClassementGEquipe> getClassementParCategorie(int id_etape,int id_categorie){
        List<ClassementGEquipe> classementGEquipes=new ArrayList<>();
        List<Object[]> result=repository.getClassementCategorie(id_etape,id_categorie);
        for (int i = 0; i < result.size(); i++) {
            ClassementGEquipe classe=new ClassementGEquipe();
            classe.setEquipe(equipeService.getById((long)Integer.parseInt(result.get(i)[0].toString())).get());
            classe.setPoint(Integer.parseInt(result.get(i)[3].toString()));
            classementGEquipes.add(classe);
        }
        classementGEquipes.sort(Comparator.comparingInt(ClassementGEquipe::getPoint).reversed());
        return classementGEquipes;
    }

    public List<ClassementGEquipe> getClassementGeneralCategorie(int id_categorie) {
        Course course = courseService.getById((long) 2).get();
        List<Etapes> etapes = course.getEtapes();
        for (int i = 0; i < etapes.size(); i++) {
            etapes.get(i).setClassementGEquipes(this.getClassementParCategorie((int) etapes.get(i).getId_etape(), id_categorie));
        }

        List<Equipe> equipes = equipeService.getAll();
        List<ClassementGEquipe> result = new ArrayList<>();
        for (int i = 0; i < equipes.size(); i++) {
            int point = 0;
            for (int j = 0; j < etapes.size(); j++) {
                List<ClassementGEquipe> classetape = etapes.get(j).getClassementGEquipes();
                for (int j2 = 0; j2 < classetape.size(); j2++) {
                    if (classetape.get(j2).getEquipe().getId_equipe() == equipes.get(i).getId_equipe()) {
                        point += classetape.get(j2).getPoint();
                    }
                }
            }
            ClassementGEquipe classe = new ClassementGEquipe();
            classe.setEquipe(equipes.get(i));
            classe.setPoint(point);
            result.add(classe);
        }
        
        for (int i = 0; i < result.size(); i++) {
            for (int j = 0; j < result.size(); j++) {
                if(i!=j){
                    if(result.get(i).getPoint()==result.get(j).getPoint() && result.get(i).getPoint()!=0){
                        result.get(i).setEx(true);
                    }
                }
            }
        }

        result.sort(Comparator.comparingInt(ClassementGEquipe::getPoint).reversed());

        return result;
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
    
    public List<Resultat>  getByCoureurAndEtapeOrder(Etapes etapes,Equipe equipe){
        return repository.findByEtapeAndEquipe(etapes, equipe,Sort.by("temps").descending());
    }

    public List<ClassementGEquipe> getVainqueur(){
        List<ClassementGEquipe> classementGenerale=this.getResultatDesEquipe();
        List<ClassementGEquipe> vaincre= new ArrayList<>();
        ClassementGEquipe maxpt=classementGenerale.get(0);
        vaincre.add(maxpt);
        for (int i = 0; i < classementGenerale.size(); i++) {
            if(maxpt.getPoint()==classementGenerale.get(i).getPoint() && classementGenerale.get(i).getEquipe().getId_equipe()!=maxpt.getEquipe().getId_equipe()){
                vaincre.add(classementGenerale.get(i));
            }
        }
        return vaincre;
    }

    public List<ResultatCoureur> getResultatCoureur(Etapes etapes){
        List<Resultat> resultats=etapes.getResultats();
        List<ResultatCoureur> resultCoureurs=new ArrayList<>();

        for (int i = 0; i < resultats.size(); i++) {
            ResultatCoureur resultatCoureur=new ResultatCoureur();
            resultatCoureur.setResultat(resultats.get(i));
            List<Penalite> penalites=penaliteService.getPenaliteEquipeEtape(resultats.get(i).getEquipe(),etapes);
            System.out.println("================================="+penalites.size());
            LocalTime tempsPenal=LocalTime.parse("00:00:00");
            for (int j = 0; j < penalites.size(); j++) {
                tempsPenal=Function.addLocalTime(tempsPenal, penalites.get(j).getTemps());
            }
            resultatCoureur.setPenalite(tempsPenal);
            ZonedDateTime dateTonga=resultats.get(i).getTempsArrive();
            LocalTime tempsSansPenal=resultats.get(i).getTemps();
            for (int j = 0; j < penalites.size(); j++) {
                dateTonga=Function.subtractLocalTimeAndZone(dateTonga, penalites.get(j).getTemps());
                tempsSansPenal=Function.subtractLocalTime(tempsSansPenal, penalites.get(j).getTemps());
            }
            resultatCoureur.setTemps_sans_penale(tempsSansPenal);
            resultatCoureur.setPoint(resultats.get(i).getPoint());
            resultatCoureur.setTemps_arrivePenal(dateTonga);
            resultCoureurs.add(resultatCoureur);
        }
        resultCoureurs.sort(Comparator.comparingInt(ResultatCoureur::getPoint).reversed());
        for (int i = 0; i < resultCoureurs.size(); i++) {
            resultCoureurs.get(i).setRang(i+1);
        }
        return resultCoureurs;
    }

}
