package com.example.clases;

import java.util.Date;
import java.util.List;

/**
 * Created by juanpablorn30 on 3/10/17.
 */

public class Empresa extends Usuario{

    private String NIT;
    private String sitioWeb;

    /*
        TODO: Agregar método de agregar item a la tienda.
        TODO: Agregar método de eliminar item a la tienda.
     */
    private List<Producto> tienda;

    /*
        TODO: Agregar método de agregar item a recorridos.
        TODO: Agregar método de eliminar item a recorridos.
     */
    private List<RecorridoOrganizado> recorridos;

    public Empresa(String password, String email, Date date_birth, String username, String NIT, String sitioWeb) {
        super(password, email, date_birth, username);
        this.NIT = NIT;
        this.sitioWeb = sitioWeb;
    }

    public String getNIT() {
        return NIT;
    }

    public void setNIT(String NIT) {
        this.NIT = NIT;
    }

    public String getSitioWeb() {
        return sitioWeb;
    }

    public void setSitioWeb(String sitioWeb) {
        this.sitioWeb = sitioWeb;
    }
}
