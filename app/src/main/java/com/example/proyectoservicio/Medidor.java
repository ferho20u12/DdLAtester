package com.example.proyectoservicio;

import java.util.ArrayList;
import java.util.List;

public class Medidor {
    private int cantCirculos;
    private List<Double> classes;
    private String nombre;
    private String urlModelReloj;
    private String urlModelContraReloj;

    public Medidor(){
        cantCirculos=0;
        classes = new ArrayList<>();
        nombre = "";
        urlModelReloj ="";
        urlModelContraReloj ="";
    }

    public String getUrlModelReloj() {
        return urlModelReloj;
    }

    public void setUrlModelReloj(String urlModelReloj) {
        this.urlModelReloj = urlModelReloj;
    }

    public String getUrlModelContraReloj() {
        return urlModelContraReloj;
    }

    public void setUrlModelContraReloj(String urlModelContraReloj) {
        this.urlModelContraReloj = urlModelContraReloj;
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
