package com.example.proyectoservicio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Medidor implements Serializable {
    private int cantCirculos;
    private List<Double> classes;
    private String nombre;

    private String nombreModelReloj;
    private String nombreModelContraReloj;
    private String pathModelReloj;// este sera el url de nuestro modelo guardado en el local
    private String pathModelContraReloj;

    public Medidor(){
        cantCirculos=0;
        classes = new ArrayList<>();
        nombre = "";
        nombreModelReloj ="";
        nombreModelContraReloj ="";
        pathModelReloj ="";
        pathModelContraReloj ="";
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


    public String getPathModelReloj() {
        return pathModelReloj;
    }

    public void setPathModelReloj(String pathModelReloj) {
        this.pathModelReloj = pathModelReloj;
    }

    public String getPathModelContraReloj() {
        return pathModelContraReloj;
    }

    public void setPathModelContraReloj(String pathModelContraReloj) {
        this.pathModelContraReloj = pathModelContraReloj;
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
