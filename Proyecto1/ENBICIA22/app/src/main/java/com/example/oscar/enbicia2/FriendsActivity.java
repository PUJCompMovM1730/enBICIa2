package com.example.oscar.enbicia2;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.clases.Ciclista;
import com.example.clases.Constants;
import com.example.clases.EnBiciaa2;
import com.example.clases.FriendsAdapter;
import com.example.clases.Usuario;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FriendsActivity extends AppCompatActivity implements View.OnClickListener{

    private String TAG = "FriendsActivity";

    private DatabaseReference mCurrentUserReference;
    private ValueEventListener mAmigosListener;

    private ListView listView;
    private TextView txtNoFriends;

    private HashMap<String, Ciclista> amigos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        // Initialize Database
        mCurrentUserReference = FirebaseDatabase.getInstance().getReference().child("usuarios").child(FirebaseAuth.getInstance().getCurrentUser().getUid());


        amigos = new HashMap<>();
        txtNoFriends = findViewById(R.id.txtFriendsNoFriends);
        txtNoFriends.setVisibility(View.GONE);
        txtNoFriends.setText("Aún no tienes amigos");
        listView = findViewById(R.id.listFriendsList);
        findViewById(R.id.btnFriendsSearch).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        ValueEventListener amigosListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                amigos.clear();
                Ciclista current = dataSnapshot.getValue(Ciclista.class);
                if(current.getAmigos() == null) Log.d(TAG, "Estoy en null");
                if (current.getAmigos() != null) {
                    Set<String> keys = current.getAmigos().keySet();

                    for (String aux : keys) {
                        String UidAmigo = current.getAmigos().get(aux);
                        Ciclista auxCiclista = (Ciclista) Constants.enBICIa2.getUsuarios().get(UidAmigo);
                        amigos.put(auxCiclista.getUid(), auxCiclista);
                    }
                    cargarAmigos();
                    txtNoFriends.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                } else {
                    txtNoFriends.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mCurrentUserReference.addValueEventListener(amigosListener);
        mAmigosListener = amigosListener;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAmigosListener != null) mCurrentUserReference.removeEventListener(mAmigosListener);
    }

    private void cargarAmigos(){
        Log.d(TAG, "Llamaron a cargar amigos");
        List<Ciclista> aux = new ArrayList<>();
        Set<String> key = amigos.keySet();
        for(String key_aux: key){
            aux.add(amigos.get(key_aux));
        }
        Log.d(TAG, "EL tam es: " + aux.size());
        FriendsAdapter adapter = new FriendsAdapter(aux, getBaseContext(), TAG);
        listView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.btnFriendsSearch){
            try{
                Intent intent = new Intent(getBaseContext(), SearchFriendsActivity.class);
                Bundle bundle = new Bundle();
                ArrayList<String> keys = new ArrayList<>();
                for(String aux : amigos.keySet()){
                    keys.add(aux);
                }
                intent.putExtra("amigos", keys);
                startActivity(intent);

            }catch (Exception e){
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
