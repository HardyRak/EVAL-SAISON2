package com.spring.hard.vieuw;

import com.spring.hard.models.Equipe;

public class ClassementGEquipe {
    Equipe equipe;
    int point;
    boolean ex;
    
    public Equipe getEquipe() {
        return equipe;
    }
    public void setEquipe(Equipe equipe) {
        this.equipe = equipe;
    }
    public int getPoint() {
        return point;
    }
    public void setPoint(int point) {
        this.point = point;
    }
    public boolean isEx() {
        return ex;
    }
    public void setEx(boolean ex) {
        this.ex = ex;
    }
}
