package com.proyecto.jerbo.cursofacil.Class;

import java.io.File;
import java.io.Serializable;

public class Curso implements Serializable {
    private String name;
    private File path;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File getPath() {
        return path;
    }

    public void setPath(File path) {
        this.path = path;
    }

    public Curso(String name, File path) {
        this.name = name;
        this.path = path;
    }
}
