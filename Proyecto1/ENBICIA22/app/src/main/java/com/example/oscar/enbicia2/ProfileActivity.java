package com.example.oscar.enbicia2;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.clases.Recorrido;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private DatabaseReference mdbRef, childRef;
    private StorageReference imaRef;
    private TextView amigos, recorridos;
    private ImageView foto;
    private ArrayList<Recorrido> rutas;
    private ListView listaRutas;
    private int x = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        amigos = (TextView) findViewById(R.id.tv_numamigos);
        recorridos = (TextView) findViewById(R.id.tv_numrutas);
        foto = (ImageView) findViewById(R.id.iv_fotoperfil);
        mdbRef = FirebaseDatabase.getInstance().getReference().child("usuarios").
                child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        imaRef = FirebaseStorage.getInstance().getReference().child("usuarios").
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("photo").child("fotoPerfil.jpg");
        Glide.with(getBaseContext()).using(new FirebaseImageLoader()).load(imaRef).fitCenter().into(foto);
        rutas = new ArrayList<>();
        listaRutas = (ListView) findViewById(R.id.list_rutas);
        childRef = FirebaseDatabase.getInstance().getReference().child("recorridos");
        contarAmigos();
    }

    private void contarAmigos() {
        if (mdbRef.child("amigos") != null) {
            mdbRef.child("amigos").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mdbRef.child("cant_amigos").setValue(dataSnapshot.getChildrenCount());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else { mdbRef.child("cant_amigos").setValue("0"); } mostrarAmigos();
    }

    private void cargarRutas() {
        final ArrayAdapter<Recorrido> arrayAdapter = new ArrayAdapter<Recorrido>(getBaseContext(), android.R.layout.simple_expandable_list_item_1,rutas);
        listaRutas.setAdapter(arrayAdapter);
        childRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void mostrarAmigos() {
        mdbRef.child("cant_amigos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object value = dataSnapshot.getValue();
                amigos.setText(value.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void mostrarRecorridos(int x) {
        recorridos.setText(Integer.toString(x));
    }

    @Override
    protected void onStart() {
        super.onStart();
        childRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot dt : dataSnapshot.getChildren()){
                    if(dt.child("organizador").equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        rutas.add(dt.getValue(Recorrido.class)); x++;
                    }
                }
                mostrarRecorridos(x);
                cargarRutas();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
