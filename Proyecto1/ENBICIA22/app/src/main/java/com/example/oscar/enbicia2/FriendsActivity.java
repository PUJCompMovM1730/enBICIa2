package com.example.oscar.enbicia2;

import android.content.Intent;
import android.os.AsyncTask;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class FriendsActivity extends AppCompatActivity{

    private String TAG = "FriendsActivity";

    private FirebaseAuth mAuth;
    private EnBiciaa2 enBICIa2;

    private EditText etFriendsSearch;
    private ListView listView;
    private List<Ciclista> search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        enBICIa2 = new EnBiciaa2();
        listView = findViewById(R.id.listFriendsSearch);
        etFriendsSearch = findViewById(R.id.etFriendsSearch);
        etFriendsSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                enBICIa2.getUsuarios().clear();
                String url = Constants.PATH_FIREBASE + "usuarios.json?auth=" + Constants.TOKEN_USER;
                FetchUrl fetchUrl = new FetchUrl();
                fetchUrl.execute(url);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void searchPeople(String text){
        List<Ciclista> ciclistas = new ArrayList<>();
        if(text.isEmpty()){
            enBICIa2.getUsuarios().clear();
            FriendsAdapter adapter = new FriendsAdapter(ciclistas, getBaseContext());
            listView.setAdapter(adapter);
            return;
        }
        text = text.toLowerCase();
        Set<String> key = enBICIa2.getUsuarios().keySet();
        for(String aux : key){
            if(!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(aux)){
                if(enBICIa2.getUsuarios().get(aux).getName().toLowerCase().contains(text)){
                    ((Ciclista)enBICIa2.getUsuarios().get(aux)).setUid(aux);
                    ciclistas.add((Ciclista) enBICIa2.getUsuarios().get(aux));
                }
            }
        }
        FriendsAdapter adapter = new FriendsAdapter(ciclistas, getBaseContext());
        listView.setAdapter(adapter);
    }

    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject json = new JSONObject(result);
                Iterator<?> keys = json.keys();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    Ciclista aux = new Ciclista();
                    JSONObject jsonObject = (JSONObject) json.get(key);
                    aux.setName(jsonObject.getString("name"));
                    enBICIa2.getUsuarios().put(key, aux);
                }
                searchPeople(etFriendsSearch.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private String downloadUrl(String strUrl) throws IOException {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(strUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                iStream = urlConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
                StringBuffer sb = new StringBuffer();
                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                data = sb.toString();
                Log.d("downloadUrl", data.toString());
                br.close();

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            } finally {
                iStream.close();
                urlConnection.disconnect();
            }
            return data;
        }
    }
}
