package com.example.clases;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by juanpablorn30 on 3/10/17.
 */

public class Mensaje {

    private Date fechaEnvio;
    private String contenido;

    // TODO: Mirar si pueden haber chats grupales. En el caso que si, cambiar por una lista.
    private Ciclista receptor;
    private Ciclista emisor;

    public Mensaje(String contenido, Ciclista receptor, Ciclista emisor) {
        this.fechaEnvio = Calendar.getInstance().getTime();;
        this.contenido = contenido;
        this.receptor = receptor;
        this.emisor = emisor;
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

    public Ciclista getReceptor() {
        return receptor;
    }

    public void setReceptor(Ciclista receptor) {
        this.receptor = receptor;
    }

    public Ciclista getEmisor() {
        return emisor;
    }

    public void setEmisor(Ciclista emisor) {
        this.emisor = emisor;
    }
}
