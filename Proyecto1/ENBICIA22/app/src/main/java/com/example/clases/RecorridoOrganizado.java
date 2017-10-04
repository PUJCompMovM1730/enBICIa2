package com.example.clases;

import java.util.Date;

/**
 * Created by juanpablorn30 on 3/10/17.
 */

public class RecorridoOrganizado extends Recorrido {

    private String descripcion;
    private Empresa organizador;

    //TODO: Como hacemos con el ciclista organizador si una empresa puede serlo

    public RecorridoOrganizado(String estado, Date fecha_hora, Punto puntoInicio, Punto puntoFin, Ciclista organizador, String descripcion, Empresa organizador1) {
        super(estado, fecha_hora, puntoInicio, puntoFin, organizador);
        this.descripcion = descripcion;
        this.organizador = organizador1;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

}
