package com.example.oscar.enbicia2;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.clases.Ciclista;
import com.example.clases.Constants;
import com.example.clases.Empresa;
import com.example.clases.FriendsAdapter;
import com.example.clases.RecorridoGrupal;
import com.example.oscar.enbicia2.R;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.support.design.widget.Snackbar.LENGTH_LONG;
import static android.support.design.widget.Snackbar.make;

public class ViewScheduledActivity extends AppCompatActivity implements View.OnClickListener{

    private String TAG = "ViewScheduledActivity";

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private ValueEventListener mRecorridoListener;

    private TextView nombre, creador, inicio, fin, fecha, hora;
    private ListView listParticipants;
    private List<Ciclista> usuariosList;
    private FriendsAdapter adapter;
    private DatabaseReference mUserReference;

    private String key;
    private String tipo;
    private RecorridoGrupal route;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_scheduled);

        key = getIntent().getStringExtra("recorrido");
        tipo = getIntent().getStringExtra("tipo");
        Log.d(TAG, "La key: " + key);
        Log.d(TAG, "El tipo es: " + tipo);
        mUserReference = FirebaseDatabase.getInstance().getReference().child("usuarios");

        nombre = findViewById(R.id.tv_VS_name);
        creador = findViewById(R.id.tv_VS_createdby);
        inicio = findViewById(R.id.tv_VS_start);
        fin = findViewById(R.id.tv_VS_target);
        fecha = findViewById(R.id.tv_VS_date);
        hora = findViewById(R.id.tv_VS_hour);

        usuariosList = new ArrayList<>();
        listParticipants = findViewById(R.id.list_view_scheduled_participants);
        adapter = new FriendsAdapter(usuariosList, this, TAG, null);
        listParticipants.setAdapter(adapter);
        findViewById(R.id.btn_comenzar_ruta).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        database = FirebaseDatabase.getInstance();
        if(tipo.equals("usuario")){
            myRef = database.getReference().child("recorridos").child(key);
        }else{
            myRef = database.getReference().child("recorridos_empresas").child(key);
        }
        ValueEventListener sitioInteresListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(Constants.TAG_CLASS, "Recibi el update " + dataSnapshot.getRef().toString());
                route = dataSnapshot.getValue(RecorridoGrupal.class);
                DatabaseReference ScheduledReference;
                if(tipo.equals("usuario")){
                    ScheduledReference = FirebaseDatabase.getInstance().getReference().child("usuarios").child(route.getOrganizador());
                }else{
                    ScheduledReference = FirebaseDatabase.getInstance().getReference().child("empresas").child(route.getOrganizador());
                }
                Log.d(TAG, ScheduledReference.getRef().toString());
                Log.d(TAG, route.getOrganizador());
                ScheduledReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(tipo.equals("usuario")){
                            try{
                                Ciclista current = dataSnapshot.getValue(Ciclista.class);
                                if (current.getName() != null)
                                    fill(route, current.getName());
                                else
                                    fill(route, current.getEmail());
                            }catch (Exception e){
                                Log.d(TAG, e.getMessage());
                            }
                        }else{
                            try{
                                Empresa current = dataSnapshot.getValue(Empresa.class);
                                fill(route, current.getEmail());
                            }catch (Exception e){
                                Log.d(TAG, e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, databaseError.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        };
        myRef.addValueEventListener(sitioInteresListener);
        mRecorridoListener = sitioInteresListener;

        ValueEventListener usuarioListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot usuario : dataSnapshot.getChildren()) {
                    Ciclista curr = usuario.getValue(Ciclista.class);
                    curr.setUid(usuario.getKey());
                    if (curr.getRecorridos() != null) {
                        if (curr.getRecorridos().containsKey(key)) {
                            usuariosList.add(curr);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mUserReference.addValueEventListener(usuarioListener);
    }

    private void fill(RecorridoGrupal rg, String name) {

        // Reference to an image file in Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("ruta-programada").child(rg.getId() + ".jpg");
        ImageView imageView = findViewById(R.id.IV_CSRoute_map);
        Glide.with(this /* context */)
                .using(new FirebaseImageLoader())
                .load(storageRef)
                .into(imageView);

        creador.setText(name);
        inicio.setText(getAddress(rg.getPuntoInicio().getLatitud(), rg.getPuntoInicio().getLongitud()));
        fin.setText(getAddress(rg.getPuntoFin().getLatitud(), rg.getPuntoFin().getLongitud()));
        nombre.setText(rg.getNombre());
        Date date = new Date(rg.getFecha_hora());
        Log.d(TAG, String.valueOf(date.getTime()));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
        fecha.setText(sdf.format(date));
        hora.setText(sdf1.format(date));
    }

    public String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(ViewScheduledActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            return addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
            make(findViewById(R.id.lin_mark_parent), e.getMessage(), LENGTH_LONG)
                    .setAction("Action", null).show();

        }
        return "";
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mRecorridoListener != null) myRef.removeEventListener(mRecorridoListener);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.btn_comenzar_ruta){
            Intent intent = new Intent(getBaseContext(), MapActivity.class);
            Bundle args = new Bundle();
            LatLng source = new LatLng(route.getPuntoInicio().getLatitud(),route.getPuntoInicio().getLongitud());
            LatLng target = new LatLng(route.getPuntoFin().getLatitud(),route.getPuntoFin().getLongitud());
            args.putParcelable("from_position", source);
            args.putParcelable("to_position", target);
            intent.putExtra("bundle", args);
            Log.d(TAG, "Ire a map");
            startActivity(intent);
        }
    }
}
