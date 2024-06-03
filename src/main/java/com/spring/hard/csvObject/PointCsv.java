package com.spring.hard.csvObject;

public class PointCsv {
    int classement;
    int point;
    
    public int getClassement() {
        return classement;
    }
    public void setClassement(String classement) throws Exception {
        int classementD=0;
        try{
            classementD=Integer.parseInt(classement);
            this.classement=classementD;
        }catch(Exception e){
            throw new Exception("Verifier donnee du colonne classement");
        }
    }
    public int getPoint() {
        return point;
    }
    public void setPoint(String point) throws Exception {
        int pointD=0;
        try{
            pointD=Integer.parseInt(point);
            this.point = pointD;            
        }catch(Exception e){
            throw new Exception("Verifier donnee du colonne point");
        }
    }
}
