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
    private String organizador;
    private List<String> grupo;

    public Recorrido(){super(); grupo=new ArrayList<>();}

    public Recorrido(String estado, Punto puntoInicio, Punto puntoFin, String organizador, Date fecha_hora) {
        this.estado = estado;
        this.fecha_hora = Calendar.getInstance().getTime();
        this.puntoInicio = puntoInicio;
        this.puntoFin = puntoFin;
        this.organizador = organizador;
        this.fecha_hora=fecha_hora;
    }

    public Recorrido(String estado, Date fecha_hora, Punto puntoInicio, Punto puntoFin, String organizador, List<String> grupo) {
        this.estado = estado;
        this.fecha_hora = fecha_hora;
        this.puntoInicio = puntoInicio;
        this.puntoFin = puntoFin;
        this.organizador = organizador;
        this.grupo = new ArrayList<>();
    }



    //TODO: Cambiar Ciclista por datos.
    public void agregarCiclista(String actual){
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

    public Long getFecha_hora() {
        return fecha_hora.getTime();
    }

    public void setFecha_hora(Long fecha_hora) {
        this.fecha_hora = new Date(fecha_hora);
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

    public String getOrganizador() {
        return organizador;
    }

    public void setOrganizador(String organizador) {
        this.organizador = organizador;
    }

    public List<String> getGrupo() {
        return grupo;
    }

    public void setGrupo(List<String> grupo) {
        this.grupo = grupo;
    }
}