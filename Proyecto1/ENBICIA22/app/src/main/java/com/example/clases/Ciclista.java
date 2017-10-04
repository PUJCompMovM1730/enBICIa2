package com.example.clases;

import java.util.Date;
import java.util.List;

/**
 * Created by juanpablorn30 on 2/10/17.
 */

public class Ciclista extends Usuario{

    /*
        TODO: Agregar método de agregar item carrito compras.
        TODO: Agregar método de eliminar item carrito compras.
     */
    private List<DetalleCompra> carritoCompras;

    /*
        TODO: Agregar método de agregar amigo.
        TODO: Agregar método de eliminar amigo.
     */
    private List<Ciclista> amigos;

    /*
        TODO: Agregar método de agregar al historial (Debe ser automatico).
        TODO: Agregar método de eliminar del historial.
        TODO: ¿Interfaz debe tener botón de eliminar?
     */
    private List<Recorrido> historial;

    /*
        TODO: Agregar método de agregar a creados.
        TODO: Agregar método de eliminar de creados.
     */
    private List<Recorrido> creados;

    /**
     * @param password
     * @param email
     * @param date_birth
     * @param username
     */
    public Ciclista(String password, String email, Date date_birth, String username) {
        super(password, email, date_birth, username);
    }


}
