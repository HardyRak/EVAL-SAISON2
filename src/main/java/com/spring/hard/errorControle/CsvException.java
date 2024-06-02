package com.spring.hard.errorControle;

import java.util.List;

public class CsvException extends Exception{
    private List<String> error;

    public List<String> getError() {
        return error;
    }

    public void setError(List<String> error) {
        this.error = error;
    }
    
}
