package com.example.clases;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by juanpablorn30 on 2/10/17.
 */

public class Recorrido {

    private String estado;
    private Date fecha_hora;
    private Punto puntoInicio;
    private Punto puntoFin;
    private Ciclista organizador;
    private List<Ciclista> grupo;

    public Recorrido(String estado, Punto puntoInicio, Punto puntoFin, Ciclista organizador) {
        this.estado = estado;
        this.fecha_hora = Calendar.getInstance().getTime();
        this.puntoInicio = puntoInicio;
        this.puntoFin = puntoFin;
        this.organizador = organizador;
    }

    public Recorrido(String estado, Date fecha_hora, Punto puntoInicio, Punto puntoFin, Ciclista organizador, List<Ciclista> grupo) {
        this.estado = estado;
        this.fecha_hora = fecha_hora;
        this.puntoInicio = puntoInicio;
        this.puntoFin = puntoFin;
        this.organizador = organizador;
        this.grupo = new ArrayList<>();
    }

    //TODO: Cambiar Ciclista por datos.
    public void agregarCiclista(Ciclista actual){
        this.grupo.add(actual);
    }

    //TODO: Cambiar Ciclista por datos.
    public boolean eliminarCiclista(Ciclista actual){
        return this.grupo.remove(actual);
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFecha_hora() {
        return fecha_hora;
    }

    public void setFecha_hora(Date fecha_hora) {
        this.fecha_hora = fecha_hora;
    }

    public Punto getPuntoInicio() {
        return puntoInicio;
    }

    public void setPuntoInicio(Punto puntoInicio) {
        this.puntoInicio = puntoInicio;
    }

    public Punto getPuntoFin() {
        return puntoFin;
    }

    public void setPuntoFin(Punto puntoFin) {
        this.puntoFin = puntoFin;
    }

    public Ciclista getOrganizador() {
        return organizador;
    }

    public void setOrganizador(Ciclista organizador) {
        this.organizador = organizador;
    }
}
