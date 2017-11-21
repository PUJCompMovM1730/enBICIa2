package com.example.oscar.enbicia2;

import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.clases.Ciclista;
import com.example.clases.Constants;
import com.example.clases.Grupo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.support.design.widget.Snackbar.make;
import static android.widget.Toast.LENGTH_LONG;

public class GroupTripActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private ValueEventListener mGrupoListener;

    String key;
    TextView gName, gDescription, gAdmin, gStart, gEnd, gDate;
    ListView gParticipantes;
    Button addFriend, exitGroup;
    ImageView tripImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_trip);
        key = getIntent().getStringExtra("grupoId");
        gName = findViewById(R.id.txt_group_name);
        gDescription = findViewById(R.id.txt_group_description);
        gAdmin = findViewById(R.id.txt_group_admin);
        gStart = findViewById(R.id.txt_group_start);
        gEnd = findViewById(R.id.txt_group_end);
        gDate = findViewById(R.id.txt_group_date);
        gParticipantes = findViewById(R.id.list_group_members);
        addFriend = findViewById(R.id.btn_group_addFriend);
        exitGroup = findViewById(R.id.btn_group_exitGroup);
        tripImg = findViewById(R.id.img_group_trip);
    }

    @Override
    protected void onStart() {
        super.onStart();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("grupos").child(key);
        ValueEventListener gruposListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    final Grupo group = dataSnapshot.getValue(Grupo.class);
                    DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("usuarios").child(group.getOrganizador());
                    userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Ciclista current = dataSnapshot.getValue(Ciclista.class);
                            if (current.getName() != null){
                                fill(group, current.getName());
                            } else {
                                fill(group, current.getEmail());
                            }
                            Log.d("ESTO ENTRO JA", "SISISISISSISISISIS");
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }catch (Exception e){
                    Log.d(Constants.TAG_CLASS, e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        myRef.addValueEventListener(gruposListener);
        mGrupoListener = gruposListener;
    }

    private void fill(Grupo rg, String organizador) {
        // TODO
        //StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("ruta-programada").child(rg.getId()+".jpg");
        //Glide.with(this /* context */).using(new FirebaseImageLoader()).load(storageRef).into(tripImg);

        gAdmin.setText(organizador);
        gStart.setText(getAddress(rg.getRecorrido().getPuntoInicio().getLatitud(),rg.getRecorrido().getPuntoInicio().getLongitud()));
        gEnd.setText(getAddress(rg.getRecorrido().getPuntoFin().getLatitud(),rg.getRecorrido().getPuntoFin().getLongitud()));
        gName.setText(rg.getNombre());
        gDate.setText(rg.getRecorrido().getFecha_hora().toString());
        gDescription.setText(rg.getDescripcion());
    }

    public String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(GroupTripActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            return addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
            make(findViewById(R.id.lin_mark_parent),  e.getMessage(), LENGTH_LONG)
                    .setAction("Action", null).show();
        }
        return "";
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGrupoListener != null) myRef.removeEventListener(mGrupoListener);
    }

}
