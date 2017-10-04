package com.example.clases;

import java.util.Date;

/**
 * Created by juanpablorn30 on 3/10/17.
 */

public class RecorridoOrganizado extends Recorrido {

    private String descripcion;

    /*
        TODO: Agregar metodo para asignar organizador. ¿O es en el constructor?
     */
    private Empresa organizador;

    public RecorridoOrganizado(String estado, Date fecha_hora, Punto puntoInicio, Punto puntoFin, String descripcion) {
        super(estado, fecha_hora, puntoInicio, puntoFin);
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
