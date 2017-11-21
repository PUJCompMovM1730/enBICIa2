package com.example.clases;


import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Grupo {

    private String gId;
    private String nombre;
    private String descripcion;
    private String organizador;
    private Recorrido recorrido;
    private Map<String, String> participantes;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    public Grupo() {
    }

    public Grupo(String nombre, String descripcion, String organizador) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.organizador = organizador;
        this.participantes = new HashMap<>();
        database = FirebaseDatabase.getInstance();
    }

    public void agregarRecorrido(Punto inicio, Punto fin, Date fecha){
        Recorrido aux = new Recorrido(Constants.ACTIVO, fecha, inicio, fin, null, null);
        this.recorrido = aux;
    }

    public void agregarParticipante(String grupoId, String participanteId){
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        myRef.child(Constants.PATH_GROUPS).child(grupoId).child("participantes").push().setValue(participanteId);
        myRef.child(Constants.PATH_USUARIOS).child(participanteId).child("grupos").push().setValue(grupoId);
    }

    public void eliminarParticipante(final String grupoId, final String participanteId){
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("usuarios").child(participanteId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Ciclista ciclista = dataSnapshot.getValue(Ciclista.class);
                for(String key : ciclista.getGrupos().keySet()){
                    if(ciclista.getGrupos().get(key).equals(grupoId)){
                        reference.child("grupos").child(key).setValue(null);
                        //reference.child(grupoId).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final DatabaseReference referenceGroup = FirebaseDatabase.getInstance().getReference().child("groups").child(grupoId);
        referenceGroup.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Grupo grupo = dataSnapshot.getValue(Grupo.class);
                for(String key : grupo.getParticipantes().keySet()){
                    if(grupo.getParticipantes().get(key).equals(participanteId)){
                        referenceGroup.child("participantes").child(key).setValue(null);
                        //referenceGroup.child(participanteId).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getOrganizador() {
        return organizador;
    }

    public void setOrganizador(String organizador) {
        this.organizador = organizador;
    }

    public Recorrido getRecorrido() {
        return recorrido;
    }

    public void setRecorrido(Recorrido recorrido) {
        this.recorrido = recorrido;
    }

    public Map<String, String> getParticipantes() {
        return participantes;
    }

    public void setParticipantes(Map<String, String> participantes) {
        this.participantes = participantes;
    }

    public String getgId() {
        return gId;
    }

    public void setgId(String gId) {
        this.gId = gId;
    }
}
