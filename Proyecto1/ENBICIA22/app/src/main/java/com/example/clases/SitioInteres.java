package com.example.clases;

/**
 * Created by juanpablorn30 on 3/10/17.
 */

public class SitioInteres {

    private String nombre;
    private double latitud;
    private double longitud;
    private String tipo;

    public SitioInteres() {
    }

    public SitioInteres(String nombre, double latitud, double longitud, String tipo) {
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
