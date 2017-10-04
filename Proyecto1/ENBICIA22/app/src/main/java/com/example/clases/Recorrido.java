package com.example.clases;

import java.util.Date;

/**
 * Created by juanpablorn30 on 2/10/17.
 */

public class Recorrido {

    private String estado;
    private Date fecha_hora;

    private Punto puntoInicio;
    private Punto puntoFin;

    /*
        TODO: Agregar metodo para asignar organizador. ¿O es en el constructor?. Puede ser NULL
     */
    private Ciclista organizador;

    /**
     * @param estado
     * @param fecha_hora
     * @param puntoInicio
     * @param puntoFin
     */
    public Recorrido(String estado, Date fecha_hora, Punto puntoInicio, Punto puntoFin) {
        this.estado = estado;
        this.fecha_hora = fecha_hora;
        this.puntoInicio = puntoInicio;
        this.puntoFin = puntoFin;
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
}
