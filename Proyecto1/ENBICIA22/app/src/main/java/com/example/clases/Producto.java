package com.example.clases;

import java.util.List;

/**
 * Created by juanpablorn30 on 3/10/17.
 */

public class Producto {

    private int cantidad;
    private String descripcion;
    private double descuento;
    private String nombre;
    private int precio;
    //TODO: ¿El producto no tiene imagen?

    /*
        TODO: Cambiarle el nombre al atributo
        TODO: Agregar método de agregar detalles compra.
        TODO: Agregar método de eliminar detalles compra.
     */
    private List<DetalleCompra> xx;

    public Producto(int cantidad, String descripcion, double descuento, String nombre, int precio) {
        this.cantidad = cantidad;
        this.descripcion = descripcion;
        this.descuento = descuento;
        this.nombre = nombre;
        this.precio = precio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }
}
