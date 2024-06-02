package com.spring.hard.service;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.stereotype.Service;

// import com.spring.hard.csvObject.Devis;
// import com.spring.hard.csvObject.MaisonTrav;
import com.spring.hard.errorControle.CsvException;
import com.spring.hard.function.Function;

@Service
public class CSVService {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    public void csvToBase(MultipartFile csv) throws CsvException {
        /*String tableName = csv.getOriginalFilename().split("\\.")[0] + "CSV";
        List<MaisonTrav> data = Function.importCSVMaisonTrav(csv);
        
        // Create table if not exists
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + tableName + " ("
                + "Type_maison VARCHAR(100),"
                + "Description VARCHAR(100),"
                + "Surface decimal(9,2),"
                + "Code_travaux VARCHAR(5),"
                + "Type_travaux VARCHAR(100),"
                + "Unite VARCHAR(5),"
                + "Prix_unitaire decimal(9,2),"
                + "Quantite decimal(9,2),"
                + "Dure_travaux int"
                + ")";
        jdbcTemplate.update(createTableQuery);
        
        String insertQuery = "INSERT INTO " + tableName + " (Type_maison, Description, Surface, Code_travaux, Type_travaux, Unite, Prix_unitaire, Quantite, Dure_travaux) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        jdbcTemplate.batchUpdate(insertQuery, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                MaisonTrav ligne = data.get(i);
                ps.setString(1, ligne.getType_maison());
                ps.setString(2, ligne.getDescription());
                ps.setDouble(3, ligne.getSurface());
                ps.setString(4, ligne.getCode_travaux());
                ps.setString(5, ligne.getType_travaux());
                ps.setString(6, ligne.getUnite());
                ps.setDouble(7, ligne.getPrix_unitaire());
                ps.setDouble(8, ligne.getQuantite());
                ps.setInt(9, ligne.getDure_travaux());
            }
            @Override
            public int getBatchSize() {
                return data.size();
            }
        });
        
        String maisonInsert = "INSERT INTO typemaison (intitule, surface, description, dure) "
                + "SELECT DISTINCT type_maison, CAST(surface AS decimal), description, 0 FROM " + tableName;
        jdbcTemplate.execute(maisonInsert);
    
        String travauxInsert = "INSERT INTO travaux (code, desigantion) "
                + "SELECT DISTINCT CAST(code_travaux AS int), type_travaux FROM " + tableName;
        jdbcTemplate.execute(travauxInsert);
    
        String partage = "INSERT INTO prestation (prix_unitaire, quantite, id_travaux, id_type, unite, dure) "
                + "SELECT CAST(prix_unitaire AS decimal), CAST(quantite AS decimal), travaux.id_travaux, typemaison.id_type, unite, CAST(dure_travaux AS int) "
                + "FROM " + tableName + " "
                + "JOIN travaux ON " + tableName + ".type_travaux = travaux.desigantion "
                + "JOIN typemaison ON " + tableName + ".type_maison = typemaison.intitule";
        jdbcTemplate.execute(partage);
        */
    }

    public void csvDevisToBase(MultipartFile csv) throws CsvException {
        /*
        String tableName = csv.getOriginalFilename().split("\\.")[0] + "CSV";
        List<Devis> data = Function.importCSVDevis(csv);
    
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + tableName + " ("
                + "client VARCHAR(100),"
                + "ref_devis VARCHAR(100),"
                + "type_maison VARCHAR(100),"
                + "finition VARCHAR(100),"
                + "taux_finition decimal(9,2),"
                + "date_devis date,"
                + "date_debut date,"
                + "lieu VARCHAR(100)"
                + ")";
        jdbcTemplate.update(createTableQuery);
    
        String insertQuery = "INSERT INTO " + tableName + " (client, ref_devis, type_maison, finition, taux_finition, date_devis, date_debut, lieu) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        jdbcTemplate.batchUpdate(insertQuery, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Devis ligne = data.get(i);
                ps.setString(1, ligne.getClient());
                ps.setString(2, ligne.getRef_devis());
                ps.setString(3, ligne.getType_maison());
                ps.setString(4, ligne.getFinition());
                ps.setDouble(5, ligne.getTaux_finition());
                ps.setDate(6, ligne.getDate_devis());
                ps.setDate(7,ligne.getDate_debut());
                ps.setString(8, ligne.getLieu());
            }
            
            @Override
            public int getBatchSize() {
                return data.size();
            }
        });
    
        String clientInsert = "INSERT INTO utilisateur (numero, isadmin) "
                + "SELECT DISTINCT client, 0 FROM " + tableName;
        jdbcTemplate.execute(clientInsert);
    
        String finitionInsert = "INSERT INTO finition (finition, taux) "
                + "SELECT DISTINCT finition, taux_finition FROM " + tableName;
        jdbcTemplate.execute(finitionInsert);
    
        String demandeInsert = "INSERT INTO demande (date_demande, date_realisation, id_type, id_utlisateur, id_finition, reference, lieu, etat, prix, prix_vente) "
                + "SELECT date_devis, date_debut, typemaison.id_type, utilisateur.id_utilisateur, finition.id_finition, ref_devis, lieu, 0, 0, 0 "
                + "FROM " + tableName + " "
                + "JOIN typemaison ON " + tableName + ".type_maison = typemaison.intitule "
                + "JOIN utilisateur ON " + tableName + ".client = utilisateur.numero "
                + "JOIN finition ON " + tableName + ".finition = finition.finition";
        jdbcTemplate.execute(demandeInsert);
         */
    }
    

}
