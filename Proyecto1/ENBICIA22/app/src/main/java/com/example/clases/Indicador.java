package com.example.clases;

/**
 * Created by juanpablorn30 on 3/10/17.
 */

public class Indicador {

    private int duracion;
    private String frecuencia;

    // TODO: No se como colocarle al atributo
    private Punto sin_nombre;

    public Indicador(int duracion, String frecuencia) {
        this.duracion = duracion;
        this.frecuencia = frecuencia;
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
}
