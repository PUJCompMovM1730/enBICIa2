package com.example.clases;

import java.util.List;

/**
 * Created by juanpablorn30 on 3/10/17.
 */

public class Punto {

    private String ciudad;
    private String direccion;
    private double latitud;
    private double longitud;
    private String nombre;

    /*
        TODO: Agregar método de agregar peligro.
        TODO: ¿Agregar método de eliminar amigo? Estos no deberían tener una duración.
        TODO: En la clase peligro no debería haber un attr duración ?
     */
    private List<Peligro> peligros;

    public Punto(String ciudad, String direccion, double latitud, double longitud, String nombre) {
        this.ciudad = ciudad;
        this.direccion = direccion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.nombre = nombre;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Peligro> getPeligros() {
        return peligros;
    }
}
