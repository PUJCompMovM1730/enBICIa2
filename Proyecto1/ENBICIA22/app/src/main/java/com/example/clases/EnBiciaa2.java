package com.example.clases;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.example.oscar.enbicia2.R;
import com.example.oscar.enbicia2.RoutesActivity;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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

    //[BEGIN declase_firebase_database]
    private DatabaseReference mCiclistaReference;
    private DatabaseReference mSitioInteresReference;
    private DatabaseReference myRef;
    //[END declare_firebase_database ]


    private ValueEventListener mSitioInteresListener;
    private ValueEventListener mCiclistasListener;

    public EnBiciaa2() {
        mCiclistaReference = FirebaseDatabase.getInstance().getReference().child("usuarios");
        mSitioInteresReference = FirebaseDatabase.getInstance().getReference().child("sitio_interes");
        usuarios = new HashMap<>();
        sitioInteres = new ArrayList<>();
        cargarUsuarios();
        cargarSitioInteres();
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
        try{

        String key = FirebaseDatabase.getInstance().getReference().push().getKey();

            myRef = FirebaseDatabase.getInstance().getReference(Constants.PATH_SITIO_INTERES + key);
            myRef.setValue(new SitioInteres(nombre, latitud, longitud, tipo));
        }catch (Exception e){
            Log.d(Constants.TAG_CLASS, e.getMessage());
            e.printStackTrace();
        }
    }

    public void agregarCiclistaFireBase(String Uid, String name, String email, Date date_birth){
        myRef = FirebaseDatabase.getInstance().getReference(Constants.PATH_USUARIOS + Uid);
        myRef.setValue(new Ciclista(name, email, date_birth));
    }

    public void agregarCiclistaFireBase(String Uid, String name, String email, Date date_birth, String cell){
        myRef = FirebaseDatabase.getInstance().getReference(Constants.PATH_USUARIOS + Uid);
        myRef.setValue(new Ciclista(name, email, date_birth, cell));
    }

    public void cargarUsuarios(){
        ValueEventListener ciclistasListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usuarios.clear();
                Log.d(Constants.TAG_CLASS, "Recibi el update");
                for(DataSnapshot aux : dataSnapshot.getChildren()){
                    Ciclista ciclista = aux.getValue(Ciclista.class);
                    ciclista.setUid(aux.getKey());
                    usuarios.put(ciclista.getUid(), ciclista);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mCiclistaReference.addValueEventListener(ciclistasListener);
        mCiclistasListener = ciclistasListener;
    }

    public void cargarSitioInteres(){
        ValueEventListener sitioInteresListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                sitioInteres.clear();
                for(DataSnapshot aux : dataSnapshot.getChildren()){
                    SitioInteres aux_sitioInteres = aux.getValue(SitioInteres.class);
                    sitioInteres.add(aux_sitioInteres);
                    Log.d(Constants.TAG_CLASS, "Entre al sitio: " + aux_sitioInteres.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mSitioInteresReference.addValueEventListener(sitioInteresListener);
        mSitioInteresListener = sitioInteresListener;
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
