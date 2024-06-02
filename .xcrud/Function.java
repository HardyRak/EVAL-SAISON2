import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Function {
    public static String minFirst(String mot){
        return Character.toLowerCase(mot.charAt(0)) + mot.substring(1);
    }
    public static String majFirst(String mot){
        return Character.toUpperCase(mot.charAt(0)) + mot.substring(1);
    }
    public static void generateController(String name) throws IOException{
        File file=new File("conf/spring.io/controler.conf");
        StringBuilder code=new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file.getPath()))) {
            String ligne;
            while ((ligne = reader.readLine()) != null) {
                ligne=ligne.replace("$minus$", minFirst(name));
                ligne=ligne.replace("$majus$", name);
                code.append(ligne+"\n");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        File doc=new File("../src/main/java/com/spring/hard/controleur");
        doc.mkdirs();
        File controleur=new File(doc.getPath()+"/"+name+"Controleur.java");
        if(!controleur.exists()){
            controleur.createNewFile();
        }
        BufferedWriter writecode=new BufferedWriter(new FileWriter(controleur.getPath()));
        writecode.write(code.toString());
        writecode.close();
    }

    public static void generateConfig(String name,String action) throws IOException{
        File file=new File("conf/spring.io/"+action+".conf");
        StringBuilder code=new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file.getPath()))) {
            String ligne;
            while ((ligne = reader.readLine()) != null) {
                ligne=ligne.replace("$minus$", minFirst(name));
                ligne=ligne.replace("$majus$", name);
                code.append(ligne+"\n");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        File doc=new File("../src/main/java/com/spring/hard/"+action);
        doc.mkdirs();
        String nameJava=doc.getPath()+"/"+name+""+majFirst(action)+".java";;
        File javaFile=new File(nameJava);
        if(!javaFile.exists()){
            javaFile.createNewFile();
        }
        BufferedWriter writecode=new BufferedWriter(new FileWriter(javaFile.getPath()));
        writecode.write(code.toString());
        writecode.close();
    }

    public static void generate(String name) throws Exception{
        verifExiste(name);
        generateController(name);
        generateConfig(name,"service");
        generateConfig(name,"repository");
    }

    public  static void generateAll() throws Exception{
        File repoModel=new File("../src/main/java/com/spring/hard/models");
        File[] allModel=repoModel.listFiles();
        for (int i = 0; i < allModel.length; i++) {
            String[] spl=allModel[i].getName().split("[.]");
            String all=spl[0];
            verifExiste(all);
            generateController(all);
            generateConfig(all,"service");
            generateConfig(all,"repository");
        }
    }

    public static boolean verifExiste(String name) throws Exception{
        File repoModel=new File("../src/main/java/com/spring/hard/models");
        File[] allModel=repoModel.listFiles();
        for (int i = 0; i < allModel.length; i++) {
            String[] spl=allModel[i].getName().split("[.]");
            if(spl[0].equals(name)){
                return true;
            }
        }
        return false;
    }

}
