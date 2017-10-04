package com.example.clases;

/**
 * Created by juanpablorn30 on 3/10/17.
 */

public class Indicador {

    private int duracion;
    private String frecuencia;
    private Punto punto;
    private Empresa empresa;

    public Indicador(int duracion, String frecuencia) {
        this.duracion = duracion;
        this.frecuencia = frecuencia;
    }

    public Indicador(int duracion, String frecuencia, Punto punto, Empresa empresa) {
        this.duracion = duracion;
        this.frecuencia = frecuencia;
        this.punto = punto;
        this.empresa = empresa;
    }

    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    public String getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(String frecuencia) {
        this.frecuencia = frecuencia;
    }

    public Punto getPunto() {
        return punto;
    }

    public void setPunto(Punto punto) {
        this.punto = punto;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }
}
