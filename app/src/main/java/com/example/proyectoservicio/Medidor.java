package com.example.proyectoservicio;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Medidor implements Serializable {
    private int cantCirculos;
    private List<Double> classes;
    private String nombre;

    private String nombreModelReloj;
    private String nombreModelContraReloj;
    private File modelFileReloj;
    private File modelFileContraReloj;

    public Medidor(){
        cantCirculos=0;
        classes = new ArrayList<>();
        nombre = "";
        nombreModelReloj ="";
        nombreModelContraReloj ="";

    }
    public File getModelFileReloj() {
        return modelFileReloj;
    }

    public void setModelFileReloj(File modelFileReloj) {
        this.modelFileReloj = modelFileReloj;
    }

    public File getModelFileContraReloj() {
        return modelFileContraReloj;
    }

    public void setModelFileContraReloj(File modelFileContraReloj) {
        this.modelFileContraReloj = modelFileContraReloj;
    }
    public String getNombreModelReloj() {
        return nombreModelReloj;
    }

    public void setNombreModelReloj(String nombreModelReloj) {
        this.nombreModelReloj = nombreModelReloj;
    }

    public String getNombreModelContraReloj() {
        return nombreModelContraReloj;
    }

    public void setNombreModelContraReloj(String nombreModelContraReloj) {
        this.nombreModelContraReloj = nombreModelContraReloj;
    }
    public int getCantCirculos() {
        return cantCirculos;
    }

    public void setCantCirculos(int cantCirculos) {
        this.cantCirculos = cantCirculos;
    }

    public List<Double> getClasses() {
        return classes;
    }

    public void setClasses(List<Double> classes) {
        this.classes = classes;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
