package com.example.clases;

import java.util.Date;

/**
 * Created by juanpablorn30 on 3/10/17.
 */

public class Peligro {

    private String descripcion;
    private Date fecha_hora;
    private String tipo;

    public Peligro(String descripcion, Date fecha_hora, String tipo) {
        this.descripcion = descripcion;
        this.fecha_hora = fecha_hora;
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFecha_hora() {
        return fecha_hora;
    }

    public void setFecha_hora(Date fecha_hora) {
        this.fecha_hora = fecha_hora;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
