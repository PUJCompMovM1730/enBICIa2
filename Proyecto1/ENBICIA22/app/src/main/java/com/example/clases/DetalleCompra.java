package com.example.clases;

import java.util.Date;
import java.util.List;

/**
 * Created by juanpablorn30 on 3/10/17.
 */

public class DetalleCompra {

    private int cantidad;
    private String estado;
    private Date fecha_hora;
    private String metodoPago;
    private Ciclista comprador;

    public DetalleCompra(int cantidad, String estado, Date fecha_hora, String metodoPago, Ciclista comprador) {
        this.cantidad = cantidad;
        this.estado = estado;
        //TODO: ¿La fecha y hora es la actual?
        this.fecha_hora = fecha_hora;
        this.metodoPago = metodoPago;
        this.comprador = comprador;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
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

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public Ciclista getComprador() {
        return comprador;
    }

    public void setComprador(Ciclista comprador) {
        this.comprador = comprador;
    }
}
