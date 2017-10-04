package com.example.clases;

import java.util.Date;

/**
 * Created by juanpablorn30 on 2/10/17.
 */

public class RecorridoGrupal extends Recorrido{

    private String frecuencia;
    private String mensaje;
    private String nombre;

    public RecorridoGrupal(String estado, Date fecha_hora, Punto puntoInicio, Punto puntoFin, Ciclista organizador, String frecuencia, String mensaje, String nombre) {
        super(estado, fecha_hora, puntoInicio, puntoFin, organizador);
        this.frecuencia = frecuencia;
        this.mensaje = mensaje;
        this.nombre = nombre;
    }

    public String getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(String frecuencia) {
        this.frecuencia = frecuencia;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
