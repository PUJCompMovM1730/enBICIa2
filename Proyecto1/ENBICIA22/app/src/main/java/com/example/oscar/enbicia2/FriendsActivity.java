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

public class FriendsActivity extends AppCompatActivity{

    private String TAG = "FriendsActivity";

    private DatabaseReference mCurrentUserReference;
    private ValueEventListener mAmigosListener;

    private ListView listView;
    private TextView txtNoFriends;

    private HashMap<String, Ciclista> amigos;
    private List<Ciclista> amigosList;
    private FriendsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        mCurrentUserReference = FirebaseDatabase.getInstance().getReference().child("usuarios").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("amigos");

        amigos = new HashMap<>();
        txtNoFriends = findViewById(R.id.txtFriendsNoFriends);
        txtNoFriends.setVisibility(View.GONE);
        txtNoFriends.setText("Aún no tienes amigos");
        listView = findViewById(R.id.list_friends_list);
        amigosList = new ArrayList<>();
        adapter = new FriendsAdapter(amigosList, this, TAG);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ValueEventListener amigosListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                amigos.clear();
                amigosList.clear();
                for(DataSnapshot aux : dataSnapshot.getChildren()){
                    final String key_friend = (String) aux.getValue();
                    DatabaseReference friendReference = FirebaseDatabase.getInstance().getReference().child("usuarios").child(key_friend);
                    friendReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Ciclista current = dataSnapshot.getValue(Ciclista.class);
                            current.setUid(key_friend);
                            amigos.put(current.getUid(), current);
                            amigosList.add(current);
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_friends_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemClicked = item.getItemId();
        if(itemClicked == R.id.it_friends_search){
            Intent intent = new Intent(getBaseContext(), SearchFriendsActivity.class);
            ArrayList<String> keys = new ArrayList<>();
            for(String aux : amigos.keySet()){
                keys.add(aux);
            }
            intent.putExtra("amigos", keys);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
