package com.example.oscar.enbicia2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.clases.Ciclista;
import com.example.clases.Constants;
import com.example.clases.FriendsAdapter;
import com.example.clases.Recorrido;
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
import java.util.List;
import java.util.Set;

public class SearchRouteActivity extends AppCompatActivity {


    private String TAG = "SearchRoute";

    private DatabaseReference mCurrentUserReference;
    private ValueEventListener mRecorridosListener;

    private ListView listView;
    private TextView txtNoFriends, titulo;

    private Button btn;

    private List<String> rutaId;
    private HashMap<String, RecorridoGrupal> recorridos;
    private List<RecorridoGrupal> recorridosList;
    private ScheduledAdapter adapter;
    private DatabaseReference mRecorridosEmpresaReference;
    private ValueEventListener mRecorridosEmpresaListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_scheduled);

        mCurrentUserReference = FirebaseDatabase.getInstance().getReference().child("recorridos");
        mRecorridosEmpresaReference = FirebaseDatabase.getInstance().getReference().child("recorridos_empresas");
        rutaId = getIntent().getStringArrayListExtra("recorridos");

        titulo = findViewById(R.id.tv_LS_titulo);
        titulo.setText("Recorridos existentes");

        btn = findViewById(R.id.btn_LS_anadir);
        btn.setVisibility(View.GONE);

        recorridos = new HashMap<>();
        txtNoFriends = findViewById(R.id.txtnoRoute);
        txtNoFriends.setText("No hay rutas disponibles");
        listView = findViewById(R.id.list_route_list);
        recorridosList = new ArrayList<>();
        adapter = new ScheduledAdapter(recorridosList, rutaId, this, TAG);
        listView.setAdapter(adapter);
    }


    @Override
    protected void onStart() {
        super.onStart();
        recorridos.clear();
        recorridosList.clear();
        final ValueEventListener recorridoListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    Log.d(Constants.TAG_CLASS, "Recibi el update " + dataSnapshot.getRef().toString());
                    for (DataSnapshot aux : dataSnapshot.getChildren()) {
                        RecorridoGrupal current = aux.getValue(RecorridoGrupal.class);
                        current.setTipo("usuario");
                        if (!alreadyRoute(current.getId())) {
                            recorridos.put(current.getId(), current);
                            recorridosList.add(current);
                        }
                        adapter.notifyDataSetChanged();
                    }
                    if (recorridos.size() > 0) txtNoFriends.setVisibility(View.GONE);
                } catch (Exception e) {
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
                for(DataSnapshot recorrido : dataSnapshot.getChildren()){
                    RecorridoGrupal current = recorrido.getValue(RecorridoGrupal.class);
                    current.setId(recorrido.getKey());
                    current.setTipo("empresa");
                    Log.d(TAG, "Entre aca: EMPRESA");
                    if (!alreadyRoute(current.getId())) {
                        recorridos.put(current.getId(), current);
                        recorridosList.add(current);
                    }
                    adapter.notifyDataSetChanged();
                }
                if (recorridos.size() > 0) txtNoFriends.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mRecorridosEmpresaReference.addValueEventListener(recorridoEmpresaListener);
        mRecorridosListener = recorridoEmpresaListener;
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mRecorridosListener != null)
            mCurrentUserReference.removeEventListener(mRecorridosListener);

        if(mRecorridosEmpresaListener != null)
            mRecorridosEmpresaReference.removeEventListener(mRecorridosEmpresaListener);
    }

    private boolean alreadyRoute(String Uid) {
        for (String aux : rutaId) {
            if (aux.equals(Uid)) return true;
        }
        return false;
    }
}
