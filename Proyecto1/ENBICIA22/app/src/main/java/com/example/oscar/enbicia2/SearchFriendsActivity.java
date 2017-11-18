package com.example.oscar.enbicia2;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;

import com.example.clases.Ciclista;
import com.example.clases.Constants;
import com.example.clases.EnBiciaa2;
import com.example.clases.FriendsAdapter;
import com.google.firebase.auth.FirebaseAuth;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SearchFriendsActivity extends AppCompatActivity {

    private String TAG = "SearchFriendsActivity";
    private EditText etFriendsSearch;
    private ListView listView;
    private List<Ciclista> search;
    private List<String> amigosUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friends);

        amigosUid = getIntent().getStringArrayListExtra("amigos");
        if(amigosUid == null){
            throw new IllegalArgumentException("Must pass AMIGOS");
        }
        search = new ArrayList<>();

        listView = findViewById(R.id.list_friends_search);
        etFriendsSearch = findViewById(R.id.edit_search_name);
        etFriendsSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                search.clear();
                searchPeople(etFriendsSearch.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void searchPeople(String text){
        if(text.isEmpty()){
            search.clear();
            FriendsAdapter adapter = new FriendsAdapter(search, getBaseContext(), TAG);
            listView.setAdapter(adapter);
            return;
        }
        if(!Constants.enBICIa2.getUsuarios().isEmpty())
        {
            text = text.toLowerCase();
            Set<String> key = Constants.enBICIa2.getUsuarios().keySet();
            for(String aux : key){
                if(!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(aux)){
                    if(Constants.enBICIa2.getUsuarios().get(aux).getEmail().toLowerCase().startsWith(text) && !alreadyFriend(aux)){
                        search.add((Ciclista) Constants.enBICIa2.getUsuarios().get(aux));
                    }
                }
            }
            FriendsAdapter adapter = new FriendsAdapter(search, getBaseContext(), TAG);
            listView.setAdapter(adapter);
        }
    }

    private boolean alreadyFriend(String Uid){
        for(String aux : amigosUid){
            if(aux.equals(Uid)) return true;
        }
        return false;
    }
}
