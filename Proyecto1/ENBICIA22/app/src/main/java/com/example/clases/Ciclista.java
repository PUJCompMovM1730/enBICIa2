package com.example.clases;

import java.util.Date;
import java.util.List;

/**
 * Created by juanpablorn30 on 2/10/17.
 */

public class Ciclista extends Usuario{

    /*
        TODO: Agregar método de agregar amigo.
        TODO: Agregar método de eliminar amigo.
     */
    private List<Ciclista> amigos;

    private List<Recorrido> historial;

    /*
        TODO: Agregar método de agregar a creados.
        TODO: Agregar método de eliminar de creados.
    */
    private List<Recorrido> creados;

    /*
        TODO: Agregar método de recibir mensajes.
        TODO: Agregar método de eliminar mensajes.
     */
    private List<Mensaje> bandejaEntrada;

    /*
        TODO: Agregar método de enviar mensajes.
        TODO: Agregar método de eliminar mensajes.
     */
    private List<Mensaje> bandejaSalida;

    /**
     * @param password
     * @param email
     * @param date_birth
     * @param username
     */
    public Ciclista(String password, String email, Date date_birth, String username) {
        super(password, email, date_birth, username);
    }

    public void agregarHistorial(Punto puntoInicio, Punto puntoFin){
        this.historial.add(new Recorrido(Constants.FINALIZADO, puntoInicio, puntoFin, this));
    }

    //TODO: Pensar como eliminar el recorrido, con que criterio.
    //TODO: ¿Interfaz debe tener botón de eliminar?. ¿Se puede eliminar?.
    public void eliminarHistorial(){

    }

    public void enviarMensaje(String contenido, Ciclista receptor){
        bandejaSalida.add(new Mensaje(contenido, receptor, this));
    }

    public List<Ciclista> getAmigos() {
        return amigos;
    }

    public List<Recorrido> getHistorial() {
        return historial;
    }

    public List<Recorrido> getCreados() {
        return creados;
    }

    public List<Mensaje> getBandejaEntrada() {
        return bandejaEntrada;
    }

    public List<Mensaje> getBandejaSalida() {
        return bandejaSalida;
    }
}
