package com.example.oscar.enbicia2;

import android.content.Intent;
import android.renderscript.Sampler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.clases.Ciclista;
import com.example.clases.Constants;
import com.example.clases.FriendsAdapter;
import com.example.clases.RecorridoGrupal;
import com.example.clases.ScheduledAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListScheduledActivity extends AppCompatActivity implements  View.OnClickListener{

    private String TAG = "ListScheduledActivity";

    private DatabaseReference mCurrentUserReference;
    private DatabaseReference mRecorridoEmpresaReference;
    private ValueEventListener mRecorridosListener;
    private ValueEventListener mRecorridosEmpresaListener;

    private ListView listView;
    private TextView txtNoRoute;

    private HashMap<String, RecorridoGrupal> recorridos;
    private List<RecorridoGrupal> recorridosList;
    private ScheduledAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_scheduled);

        mCurrentUserReference = FirebaseDatabase.getInstance().getReference().child("usuarios").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("recorridos");
        mRecorridoEmpresaReference = FirebaseDatabase.getInstance().getReference().child("usuarios").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("recorridos");

        recorridos = new HashMap<>();
        txtNoRoute = findViewById(R.id.txtnoRoute);
        txtNoRoute.setText("Aún no tienes rutas programadas");
        listView = findViewById(R.id.list_route_list);
        recorridosList = new ArrayList<>();
        adapter = new ScheduledAdapter(recorridosList, null, this, TAG);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               adapter.notifyDataSetChanged();

            }
        });
        findViewById(R.id.btn_LS_anadir).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        recorridos.clear();
        recorridosList.clear();
        ValueEventListener recorridoListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    for(DataSnapshot aux : dataSnapshot.getChildren()){
                        final String key_route = (String) aux.getValue();
                        DatabaseReference ScheduledReference = FirebaseDatabase.getInstance().getReference().child("recorridos").child(key_route);
                        ScheduledReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    RecorridoGrupal current = dataSnapshot.getValue(RecorridoGrupal.class);
                                    current.setId(key_route);
                                    current.setTipo("usuario");
                                    recorridos.put(current.getId(), current);
                                    recorridosList.add(current);
                                    adapter.notifyDataSetChanged();
                                    if(recorridosList.size() > 0) txtNoRoute.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d(TAG, "Entre al error");
                            }
                        });
                    }
                }catch (Exception e){
                    Log.d(Constants.TAG_CLASS, e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(Constants.TAG_CLASS, "Error: " + databaseError.getMessage());
            }
        };
        mCurrentUserReference.addValueEventListener(recorridoListener);
        mRecorridosListener = recorridoListener;

        ValueEventListener recorridoEmpresaListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    for(DataSnapshot aux : dataSnapshot.getChildren()){
                        final String key_route = (String) aux.getValue();
                        DatabaseReference ScheduledReference = FirebaseDatabase.getInstance().getReference().child("recorridos_empresas").child(key_route);
                        ScheduledReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    RecorridoGrupal current = dataSnapshot.getValue(RecorridoGrupal.class);
                                    current.setId(key_route);
                                    current.setTipo("empresa");
                                    recorridos.put(current.getId(), current);
                                    recorridosList.add(current);
                                    adapter.notifyDataSetChanged();
                                    if(recorridosList.size() > 0) txtNoRoute.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d(TAG, "Entre al error");
                            }
                        });
                    }
                }catch (Exception e){
                    Log.d(Constants.TAG_CLASS, e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(Constants.TAG_CLASS, "Error: " + databaseError.getMessage());
            }
        };
        mRecorridoEmpresaReference.addValueEventListener(recorridoEmpresaListener);
        mRecorridosEmpresaListener = recorridoEmpresaListener;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mRecorridosListener != null) mCurrentUserReference.removeEventListener(mRecorridosListener);
        if(mRecorridosEmpresaListener != null) mRecorridoEmpresaReference.removeEventListener(mRecorridosEmpresaListener);
    }

    public void onClick(View view) {
        int i = view.getId();
        if( i == R.id.btn_LS_anadir ) {
            Intent intent = new Intent(getApplicationContext(),CreateSRouteActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_friends_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemClicked = item.getItemId();
        if(itemClicked == R.id.it_friends_search){
            Intent intent = new Intent(getBaseContext(), SearchRouteActivity.class);
            ArrayList<String> keys = new ArrayList<>();
            for(String aux : recorridos.keySet()){
                keys.add(aux);
            }
            intent.putExtra("recorridos", keys);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
