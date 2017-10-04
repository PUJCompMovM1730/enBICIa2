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

    public DetalleCompra(int cantidad, String estado, Date fecha_hora, String metodoPago) {
        this.cantidad = cantidad;
        this.estado = estado;
        //TODO: ¿La fecha y hora es la actual?
        this.fecha_hora = fecha_hora;
        this.metodoPago = metodoPago;
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
}
