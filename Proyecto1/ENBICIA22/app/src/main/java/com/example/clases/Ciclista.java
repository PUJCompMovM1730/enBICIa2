package com.example.clases;

import android.os.Parcel;
import android.os.Parcelable;
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
import java.util.Objects;

/**
 * Created by juanpablorn30 on 2/10/17.
 */

public class Ciclista extends Usuario implements Parcelable{

    private String estado;
    private Long cant_recorridos;
    private String numero_celular;
    private Long cant_amigos;
    private String Uid;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    /*
        TODO: Agregar método de agregar amigo.
        TODO: Agregar método de eliminar amigo.
     */
    private Map<String, String> amigos;

    private List<Recorrido> historial;
    private Map<String, String> recorridos;
    private Map<String, String> grupos;

    /*
        TODO: Agregar método de agregar a creados.
        TODO: Agregar método de eliminar de creados.
    */
    private List<Recorrido> creados;

    /*
        TODO: Agregar método de recibir mensajes.
        TODO: Agregar método de eliminar mensajes.
     */
    private List<ChatMessage> bandejaEntrada;

    /*
        TODO: Agregar método de enviar mensajes.
        TODO: Agregar método de eliminar mensajes.
     */
    private List<ChatMessage> bandejaSalida;

    public Ciclista() {
        super();
    }

    public Ciclista(String name, String email, Date date_birth) {
        super(name, email, date_birth);
        database = FirebaseDatabase.getInstance();
        this.cant_recorridos = 0L;
        this.cant_amigos = 0L;
        this.numero_celular = "";
        this.estado = "";
        this.amigos = new HashMap<>();
    }

    public Ciclista(String name, String email, Date date_birth, String numero_celular) {
        super(name, email, date_birth);
        database = FirebaseDatabase.getInstance();
        this.cant_recorridos = 0L;
        this.cant_amigos = 0L;
        this.numero_celular = numero_celular;
        this.estado = "";
        this.amigos = new HashMap<>();
    }

    public void agregarAmigoFireBase(String Uid, String UidAmigo){
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        myRef.child(Constants.PATH_USUARIOS).child(Uid).child("amigos").push().setValue(UidAmigo);
        myRef.child(Constants.PATH_USUARIOS).child(UidAmigo).child("amigos").push().setValue(Uid);
    }

    public void eliminarAmigoFireBase(String Uid, String UidAmigo){
        DatabaseReference referenceUsuario = FirebaseDatabase.getInstance().getReference().child("usuarios").child(Uid);
        DatabaseReference referenceAmigo = FirebaseDatabase.getInstance().getReference().child("usuarios").child(UidAmigo);
        addListenerForErase(referenceUsuario, Uid, UidAmigo);
        addListenerForErase(referenceAmigo, UidAmigo, Uid);
    }

    private void addListenerForErase(final DatabaseReference reference, String Uid, final String UidAmigo){
        ValueEventListener ciclistasListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Ciclista ciclista = dataSnapshot.getValue(Ciclista.class);
                for(String key : ciclista.getAmigos().keySet()){
                    if(ciclista.getAmigos().get(key).equals(UidAmigo)){
                        reference.child("amigos").child(key).setValue(null);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        reference.addListenerForSingleValueEvent(ciclistasListener);
    }

    public void agregarHistorial(Punto puntoInicio, Punto puntoFin){
        //this.historial.add(new Recorrido(Constants.FINALIZADO, puntoInicio, puntoFin, this));
    }

    //TODO: Pensar como eliminar el recorrido, con que criterio.
    //TODO: ¿Interfaz debe tener botón de eliminar?. ¿Se puede eliminar?.
    public void eliminarHistorial(){

    }

    public void enviarMensaje(String contenido, Ciclista receptor){
        bandejaSalida.add(new ChatMessage(contenido, receptor.getUid()));
    }

    //[BEGIN GETTER AND SETTER Usuario]
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getDate_birth() {
        if(date_birth == null) return 0L;
        return date_birth.getTime();
    }

    public void setDate_birth(Long date_birth) {
        this.date_birth = new Date(date_birth);
    }
    //[END GETTER AND SETTER Usuario]


    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Long getCant_recorridos() {
        return cant_recorridos;
    }

    public void setCant_recorridos(Long cant_recorridos) {
        this.cant_recorridos = cant_recorridos;
    }

    public String getNumero_celular() {
        return numero_celular;
    }

    public void setNumero_celular(String numero_celular) {
        this.numero_celular = numero_celular;
    }

    public Long getCant_amigos() {
        return cant_amigos;
    }

    public void setCant_amigos(Long cant_amigos) {
        this.cant_amigos = cant_amigos;
    }

    public Map<String, String> getAmigos() {
        return amigos;
    }

    public void setAmigos(Map<String, String> amigos) {
        this.amigos = amigos;
    }

    public List<Recorrido> getHistorial() {
        return historial;
    }

    public List<Recorrido> getCreados() {
        return creados;
    }

    public List<ChatMessage> getBandejaEntrada() {
        return bandejaEntrada;
    }

    public List<ChatMessage> getBandejaSalida() {
        return bandejaSalida;
    }

    public Map<String, String> getRecorridos() {
        return recorridos;
    }

    public void setRecorridos(Map<String, String> recorridos) {
        this.recorridos = recorridos;
    }

    public Ciclista(Parcel in){
        String[] data = new String[3];
        in.readStringArray(data);
        this.setEmail(data[0]);
        this.setUid(data[1]);
        this.setName(data[2]);
    }

    public static final Parcelable.Creator<Ciclista> CREATOR
            = new Parcelable.Creator<Ciclista>() {
        public Ciclista createFromParcel(Parcel in) {
            return new Ciclista(in);
        }

        public Ciclista[] newArray(int size) {
            return new Ciclista[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
            parcel.writeStringArray(new String[]{
                    this.getEmail(),
                    this.getUid(),
                    this.getName(),
            });
    }

    public Map<String, String> getGrupos() {
        return grupos;
    }

    public void setGrupos(Map<String, String> grupos) {
        this.grupos = grupos;
    }
}
