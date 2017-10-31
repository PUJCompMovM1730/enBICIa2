package com.example.clases;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.oscar.enbicia2.RoutesActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by juanpablorn30 on 3/10/17.
 */

public class EnBiciaa2 {


    private Map<String, Usuario> usuarios;
    private List<SitioInteres> sitioInteres;

    private boolean finish;

    //[BEGIN declase_firebase_database]
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    //[END declare_firebase_database ]

    public EnBiciaa2() {
        database = FirebaseDatabase.getInstance();
        usuarios = new HashMap<>();
        sitioInteres = new ArrayList<>();
    }

    public void agregarSitioInteresFireBase(String nombre, double latitud, double longitud, int indx){
        String tipo = "";
        switch (indx){
            case 0: tipo = "Ladron";
                break;
            case 1: tipo = "Alquiler";
                break;
            case 2: tipo = "Tienda";
                break;
            case 3: tipo = "Taller";
                break;
        }
        Log.d(Constants.TAG_CLASS, "Agregando sitio");
        myRef = database.getReference();
        String key = myRef.push().getKey();
        Log.d(Constants.TAG_CLASS, "La llave es: " + key);
        myRef = database.getReference(Constants.PATH_SITIO_INTERES + key);
        myRef.setValue(new SitioInteres(nombre, latitud, longitud, tipo));
    }

    public void agregarCiclistaFireBase(String Uid, String name, String email, Date date_birth){
        myRef = database.getReference(Constants.PATH_USUARIOS + Uid);
        myRef.setValue(new Ciclista(name, email, date_birth));
    }

    public void agregarCiclistaFireBase(String Uid, String name, String email, Date date_birth, String cell){
        myRef = database.getReference(Constants.PATH_USUARIOS + Uid);
        myRef.setValue(new Ciclista(name, email, date_birth, cell));
    }

    public Map<String, Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(Map<String, Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public List<SitioInteres> getSitioInteres() {
        return sitioInteres;
    }

    public void setSitioInteres(List<SitioInteres> sitioInteres) {
        this.sitioInteres = sitioInteres;
    }
}
