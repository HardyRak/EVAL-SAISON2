package com.spring.hard.function;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.opencsv.CSVReader;
import com.spring.hard.csvObject.EtapeCsv;
import com.spring.hard.csvObject.PointCsv;
import com.spring.hard.csvObject.ResultatCsv;
import com.spring.hard.errorControle.CsvException;

public class Function {

    public static java.sql.Date stringToDate(String date) {
        List<SimpleDateFormat> dateFormats = new ArrayList<SimpleDateFormat>() {{
            add(new SimpleDateFormat("yyyy-MM-dd"));
            add(new SimpleDateFormat("dd/MM/yyyy"));
            add(new SimpleDateFormat("MM-dd-yyyy"));
            add(new SimpleDateFormat("dd-MM-yyyy"));
            add(new SimpleDateFormat("yyyy/MM/dd"));
            add(new SimpleDateFormat("MM/dd/yyyy"));
            add(new SimpleDateFormat("MM-dd-yyyy HH:mm:ss"));
        }};

        for (SimpleDateFormat sdf : dateFormats) {
            try {
                sdf.setLenient(false);
                java.util.Date parsedDate = sdf.parse(date);
                return new java.sql.Date(parsedDate.getTime());
            } catch (ParseException e) {
                
            }
        }
        throw new IllegalArgumentException("Format de date invalide: " + date);
    }

    public static Date getCurrenDate(){
        Calendar calendar = Calendar.getInstance();
        java.util.Date utilDate = calendar.getTime();
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
        return sqlDate;
    }

    public static void writeFile(String text, String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
                writer.write(text);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static int countLines(String filePath) {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                count++;
            }
        } catch (IOException e) {
            return 0;
        }
        return count;
    }

    public static List<String> readLines(String filePath) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace(); 
        }
        return lines;
    }

    public static void deleteLine(int lineNumber, String filePath) {
        List<String> linesToKeep = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int currentLine = 1;
            while ((line = reader.readLine()) != null) {
                if (currentLine != lineNumber) {
                    linesToKeep.add(line);
                }
                currentLine++;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String line : linesToKeep) {
                writer.write(line);
                writer.newLine(); 
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int verifiIfExisteFirstElement(String filePath, String word) {
        int lineNumber = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++; 
                String[] wordsInLine = line.split(" ");
                if (wordsInLine.length > 0 && wordsInLine[0].equals(word)) {
                    return lineNumber;
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); 
        }
        return 0;
    }

    public static void modifierLigne(String filePath, String nouveauTxt, int numero) {
        File inputFile = new File(filePath);
        File tempFile = new File("temp.txt");
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            String line;
            int currentLine = 1;
            while ((line = reader.readLine()) != null) {
                if (currentLine == numero) {
                    writer.write(nouveauTxt);
                    writer.newLine();
                } else {
                    writer.write(line);
                    writer.newLine();
                }
                currentLine++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (inputFile.delete()) {
            if (!tempFile.renameTo(inputFile)) {
                System.out.println("Échec du remplacement du fichier.");
            }
        } else {
            System.out.println("Échec de suppression du fichier d'origine.");
        }
    }  

    public static String getLinePerNumeroLine(String filePath, int numero) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int currentLine = 1;
            while ((line = reader.readLine()) != null) {
                if (currentLine == numero) {
                    return line;
                }
                currentLine++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("Le fichier a été supprimé avec succès.");
            } else {
                System.out.println("Échec de la suppression du fichier.");
            }
        } else {
            System.out.println("Le fichier n'existe pas.");
        }
    }

    public static void saveFile(MultipartFile file, String savePath) throws IOException {
        Path filePath = Paths.get(savePath);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
    }
    
    public static List<EtapeCsv> importCSVEtape(MultipartFile file) throws CsvException {
        List<EtapeCsv> etape = new ArrayList<>();
        List<String> erreur = new ArrayList<>();
        
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] nextLine;
            boolean isFirstLine = true;
            int i = 1;
            
            while ((nextLine = reader.readNext()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                try {
                    EtapeCsv line = new EtapeCsv();
                    line.setEtape(nextLine[0]);
                    line.setLongueur(nextLine[1]);
                    line.setNbCoureur(nextLine[2]);
                    line.setRang(nextLine[3]);
                    line.setDepart(nextLine[4]+" "+nextLine[5]);
                    etape.add(line);
                } catch (Exception e) {
                    erreur.add(e.getMessage() + " ligne numero:" + i +" du csv etape");
                    i++;
                    continue;
                }
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (erreur.size() > 0) {
            CsvException csvException = new CsvException();
            csvException.setError(erreur);
            throw csvException;
        }
        return etape;
    }
    
    public static List<PointCsv> importCSVPoint(MultipartFile file) throws CsvException {
        List<PointCsv> point = new ArrayList<>();
        List<String> erreur = new ArrayList<>();
        
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] nextLine;
            boolean isFirstLine = true;
            int i = 1;
            
            while ((nextLine = reader.readNext()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                try {
                    PointCsv line = new PointCsv();
                    line.setClassement(nextLine[0]);
                    line.setPoint(nextLine[1]);
                    point.add(line);
                } catch (Exception e) {
                    erreur.add(e.getMessage() + " ligne numero:" + i +" du csv point");
                    i++;
                    continue;
                }
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (erreur.size() > 0) {
            CsvException csvException = new CsvException();
            csvException.setError(erreur);
            throw csvException;
        }
        return point;
    }

    public static List<ResultatCsv> importCSVResultat(MultipartFile file) throws CsvException {
        List<ResultatCsv> resultat = new ArrayList<>();
        List<String> erreur = new ArrayList<>();
        
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] nextLine;
            boolean isFirstLine = true;
            int i = 1;
            
            while ((nextLine = reader.readNext()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                try {
                    ResultatCsv line = new ResultatCsv();
                    line.setEtape_rang(nextLine[0]);
                    line.setDossard(nextLine[1]);
                    line.setNom(nextLine[2]);
                    line.setGenre(nextLine[3]);
                    line.setNaissance(nextLine[4]);
                    line.setEquipe(nextLine[5]);
                    line.setArrive(nextLine[6]);
                    resultat.add(line);
                } catch (Exception e) {
                    erreur.add(e.getMessage() + " ligne numero:" + i +" du csv resultat");
                    i++;
                    continue;
                }
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (erreur.size() > 0) {
            CsvException csvException = new CsvException();
            csvException.setError(erreur);
            throw csvException;
        }
        return resultat;
    }

    // public static List<Devis> importCSVDevis(MultipartFile file) throws CsvException {
    //     List<Devis> dataDevis = new ArrayList<>();
    //     List<String> erreur = new ArrayList<>();
        
    //     try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
    //         String[] nextLine;
    //         boolean isFirstLine = true;
    //         int i = 1;
            
    //         while ((nextLine = reader.readNext()) != null) {
    //             if (isFirstLine) {
    //                 isFirstLine = false;
    //                 continue;
    //             }
    //             try {
    //                 Devis line = new Devis();
    //                 line.setClient(nextLine[0]);
    //                 line.setRef_devis(nextLine[1]);
    //                 line.setType_maison(nextLine[2]);
    //                 line.setFinition(nextLine[3]);
    //                 line.setTaux_finition(nextLine[4]);
    //                 line.setDate_devis(Function.stringToDate(nextLine[5]));
    //                 line.setDate_debut(Function.stringToDate(nextLine[6]));
    //                 line.setLieu(nextLine[7]);
    //                 dataDevis.add(line);
    //             } catch (Exception e) {
    //                 erreur.add(e.getMessage() + " ligne numero:" + i);
    //                 i++;
    //                 continue;
    //             }
    //             i++;
    //         }
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    //     if (erreur.size() > 0) {
    //         CsvException csvException = new CsvException();
    //         csvException.setError(erreur);
    //         throw csvException;
    //     }
    //     return dataDevis;
    // }

    public static java.sql.Date addDaysToDate(java.sql.Date date, int daysToAdd) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, daysToAdd);
        java.util.Date newDate = calendar.getTime();
        return new java.sql.Date(newDate.getTime());
    }

    public static ZonedDateTime stringToZoneDateTime(String dateTime) {
        List<DateTimeFormatter> formatters = Arrays.asList(
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")
        );
    
        ZonedDateTime zdt = null;
        for (DateTimeFormatter formatter : formatters) {
            try {
                LocalDateTime ldt = LocalDateTime.parse(dateTime, formatter);
                zdt = ZonedDateTime.of(ldt, ZoneId.systemDefault());
                break;
            } catch (DateTimeParseException e) {
            }
        }
        if (zdt == null) {
            throw new IllegalArgumentException("Invalid date and time format: " + dateTime);
        }
        return zdt;
    }

}