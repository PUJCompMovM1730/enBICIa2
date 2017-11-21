package com.example.clases;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.Map;

/**
 * Created by juanpablorn30 on 2/10/17.
 */

public class RecorridoGrupal extends Recorrido{

    private String id;
    private Integer frecuencia;
    private String mensaje;
    private String nombre;
    private Map<String, String> usuario;
    private String tipo;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    public RecorridoGrupal(){
        super();

    }

    public RecorridoGrupal(String estado, Punto puntoInicio, Punto puntoFin, String organizador, Integer frecuencia, String mensaje, String nombre, Date fecha) {
        super(estado, puntoInicio, puntoFin, organizador, fecha);
        database = FirebaseDatabase.getInstance();
        this.frecuencia = frecuencia;
        this.mensaje = mensaje;
        this.nombre = nombre;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(Integer frecuencia) {
        this.frecuencia = frecuencia;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void agregarRecorridoFireBase(RecorridoGrupal rg,  String Uid){
        Log.d(Constants.TAG_CLASS, rg.getTipo());
        DatabaseReference referenceUsuario = FirebaseDatabase.getInstance().getReference().child("usuarios").child(Uid).child("recorridos").child(rg.getId());
        referenceUsuario.setValue(rg.getId());
        if(rg.getTipo().equals("usuario")){
            referenceUsuario = FirebaseDatabase.getInstance().getReference().child("recorridos").child(rg.getId()).child("usuario").child(Uid);
            referenceUsuario.setValue(Uid);
        }else{
            referenceUsuario = FirebaseDatabase.getInstance().getReference().child("recorridos_empresas").child(rg.getId()).child("usuario").child(Uid);
            referenceUsuario.setValue(Uid);
        }
    }

    public void eliminarRecorridoFireBase(RecorridoGrupal rg, String UidAmigo){
        DatabaseReference referenceUsuario = FirebaseDatabase.getInstance().getReference().child("usuarios").child(UidAmigo).child("recorridos");
        referenceUsuario.child(rg.getId()).removeValue();

        referenceUsuario = FirebaseDatabase.getInstance().getReference().child("recorridos").child(rg.getId()).child("usuario");
        referenceUsuario.child(UidAmigo).removeValue();
    }

    public Map<String, String> getUsuario() {
        return usuario;
    }

    public void setUsuario(Map<String, String> usuario) {
        this.usuario = usuario;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}