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
        TODO: Agregar método de agregar item a recorridos.
        TODO: Agregar método de eliminar item a recorridos.
     */
    private List<RecorridoOrganizado> recorridos;

    /*
        TODO: Agregar método de agregar indicadores.
        TODO: Agregar método de eliminar indicadores.
     */
    private List<Indicador> indicadores;

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

    public List<RecorridoOrganizado> getRecorridos() {
        return recorridos;
    }

    public void setRecorridos(List<RecorridoOrganizado> recorridos) {
        this.recorridos = recorridos;
    }

    public List<Indicador> getIndicadores() {
        return indicadores;
    }

    public void setIndicadores(List<Indicador> indicadores) {
        this.indicadores = indicadores;
    }
}
