package com.example.clases;

/**
 * Created by juanpablorn30 on 3/10/17.
 */

public class SitioInteres {

    private String direccion;
    private String propietario;

    public SitioInteres(String direccion, String propietario) {
        this.direccion = direccion;
        this.propietario = propietario;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getPropietario() {
        return propietario;
    }

    public void setPropietario(String propietario) {
        this.propietario = propietario;
    }
}
