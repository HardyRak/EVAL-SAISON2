package com.spring.hard.controleur;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.google.gson.Gson;
import com.spring.hard.errorControle.CsvException;
import com.spring.hard.function.Function;
import com.spring.hard.models.Equipe;
import com.spring.hard.service.CSVService;
import com.spring.hard.service.EquipeService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class Index {
    private final CSVService CSVservice;
    @Autowired
    private HttpSession httpSession;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private EquipeService equipeService;

    public Index(CSVService csvervice){
        this.CSVservice=csvervice;
    }

    public static String redirectLogin(Object session,String profil,String page){
        if(profil.equals("admin") && session==null){
            return "redirect:/loginAdmin?msg=Vous n est pas connecté en tant qu'admin";
        }else if(profil.equals("user") && session==null){
            return "redirect:/login?msg=Vous n'est pas connecte";
        }
        return page;
    }

    @GetMapping("/")
    public String getIndex() {
        return redirectLogin(httpSession.getAttribute("user"), "user", "/index");
    }

    @GetMapping("/Admin")
    public String getIndexAdmin() {
        return redirectLogin(httpSession.getAttribute("admin"), "admin", "indexAdmin");
    }

    @GetMapping("/login")
    public String getLogin() {
        return "login";
    }

    @GetMapping("/loginAdmin")
    public String getLoginAdmin() {
        return "loginAdmin";
    }

    @PostMapping("/logon")
    public String getUserLogin(@RequestParam String  authentification,@RequestParam String password){
        Equipe user=equipeService.authentification(authentification, password);
        if(user!=null){
            if(user.getIsAdmin()==0){
                httpSession.setAttribute("user", user);
                return "redirect:/";    
            }
        }
        return "redirect:/login?msg=Authentification incorrecte";
    }

    @PostMapping("/logonAdmin")
    public String getAdminLogin(@RequestParam String authentification,@RequestParam String password){
        Equipe user=equipeService.authentification(authentification, password);
        if(user!=null){
            if(user.getIsAdmin()==1){
                httpSession.setAttribute("admin", user);
                return "redirect:/Admin";   
            }         
        }
        return "redirect:/loginAdmin?msg=Authetification incorrect";
    }

    @GetMapping("/CSV")
    public String pageImport(){
        return "pages/importCSV";
    }

    @GetMapping("/CSV/point")
    public String pageImportpoint(){
        return Index.redirectLogin(httpSession.getAttribute("admin"), "admin", "pages/importCSVPoint");
    }

    @GetMapping("TableExportPdf")
    public String tableExport(){
        return "pages/admin/detailDevis";
    }

    @GetMapping("form")
    public String formulaire(){
        return "pages/admin/formulaire";
    }

    @PostMapping("/import/csv")
    @Transactional
    public void importCSV(@RequestParam("csv1") MultipartFile csv1,@RequestParam("csv2") MultipartFile csv2, HttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        response.setContentType("application/json");
        
        try {
            CSVservice.csvToBaseEtape(csv1);
             CSVservice.csvDevisToBaseResultat(csv2);
            // Function.saveFile(csv1, "uploads" + csv1.getOriginalFilename());
            // Function.saveFile(csv1, "uploads" + csv2.getOriginalFilename());
            
            Map<String, String> successMessage = new HashMap<>();
            successMessage.put("message", "Importation reussi");
            
            response.setStatus(HttpStatus.OK.value());
            response.getWriter().write(gson.toJson(successMessage));
        } catch (CsvException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Erreur lors de l'importation");
            errorResponse.put("errors", e.getError());
            System.out.println(e.getError());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.getWriter().write(gson.toJson(errorResponse));
        }
    }

    @PostMapping("/import/csv/point")
    @Transactional
    public void importCSVPoint(@RequestParam("csv") MultipartFile csv,HttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        response.setContentType("application/json");
        
        try {
            CSVservice.csvToBasePoint(csv);
            Map<String, String> successMessage = new HashMap<>();
            successMessage.put("message", "Importation reussi");
            response.setStatus(HttpStatus.OK.value());
            response.getWriter().write(gson.toJson(successMessage));
        } catch (CsvException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Erreur lors de l'importation");
            errorResponse.put("errors", e.getError());
            System.out.println(e.getError());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.getWriter().write(gson.toJson(errorResponse));
        }
    }

    @GetMapping("/board")
    public String board(Model model){
        return "pages/dashboard";
    }

    @GetMapping("/truncate")
    @ResponseBody
    public void truncateDatabase() {
        String query = "DO $$DECLARE \n" +
                " r RECORD;\n" +
                " BEGIN\n" +
                "EXECUTE 'SET session_replication_role = replica';\n" +
                "FOR r IN (SELECT tablename FROM pg_tables WHERE schemaname = 'public' AND tablename NOT IN ('categorie','equipe', 'course'))\n" +
                " LOOP\n" +
                "EXECUTE 'TRUNCATE TABLE ' || quote_ident(r.tablename) || ' CASCADE';\n" +
                " END LOOP;\n" +
                " EXECUTE 'SET session_replication_role = DEFAULT';\n" +
                " END\n" +
                "$$;";
        String deleteUser = "DELETE FROM Equipe WHERE is_admin = 0;";
        String resetSequences = "DO $$\n" +
                "DECLARE\n" +
                "    r RECORD;\n" +
                "BEGIN\n" +
                "    FOR r IN (SELECT sequence_name, sequence_schema \n" +
                "              FROM information_schema.sequences\n" +
                "              WHERE sequence_schema = 'public' AND sequence_name IN ('course_id_seq'))\n" +
                "    LOOP\n" +
                "        EXECUTE format('ALTER SEQUENCE %I.%I RESTART WITH 2', r.sequence_schema, r.sequence_name);\n" +
                "    END LOOP;\n" +
                "END $$;";
    
        jdbcTemplate.execute(query);
        jdbcTemplate.execute(deleteUser);
        jdbcTemplate.execute(resetSequences);
    }

    @GetMapping("/generateCateg")
    @ResponseBody
    public void generationCategorie() {
        String query = "select updateCategorieCoureur()";
        jdbcTemplate.execute(query);
    }

}
