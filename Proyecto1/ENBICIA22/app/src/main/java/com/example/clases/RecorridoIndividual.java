package com.example.clases;

import java.util.Date;

/**
 * Created by juanpablorn30 on 2/10/17.
 */

public class RecorridoIndividual{

    private String informeClima;
    private String distanciaRecorrida;
    private int tiempoTranscurrido;

    public RecorridoIndividual(String estado, Punto puntoInicio, Punto puntoFin, Ciclista organizador, String informeClima, String distanciaRecorrida, int tiempoTranscurrido) {
        this.informeClima = informeClima;
        this.distanciaRecorrida = distanciaRecorrida;
        this.tiempoTranscurrido = tiempoTranscurrido;
    }

    public String getInformeClima() {
        return informeClima;
    }

    public void setInformeClima(String informeClima) {
        this.informeClima = informeClima;
    }

    public String getDistanciaRecorrida() {
        return distanciaRecorrida;
    }

    public void setDistanciaRecorrida(String distanciaRecorrida) {
        this.distanciaRecorrida = distanciaRecorrida;
    }

    public int getTiempoTranscurrido() {
        return tiempoTranscurrido;
    }

    public void setTiempoTranscurrido(int tiempoTranscurrido) {
        this.tiempoTranscurrido = tiempoTranscurrido;
    }
}
