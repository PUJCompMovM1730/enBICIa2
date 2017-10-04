package com.example.clases;

import java.util.Date;

/**
 * Created by juanpablorn30 on 3/10/17.
 */

public class Mensaje {

    private Date fechaEnvio;
    private String contenido;

    public Mensaje(Date fechaEnvio, String contenido) {
        this.fechaEnvio = fechaEnvio;
        this.contenido = contenido;
    }

    public Date getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(Date fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }
}
