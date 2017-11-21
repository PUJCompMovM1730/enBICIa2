package com.example.oscar.enbicia2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.clases.Ciclista;
import com.example.clases.FriendsAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendsActivity extends AppCompatActivity {

    private String TAG = "FriendsActivity";

    private DatabaseReference mCurrentUserReference;
    private ValueEventListener mAmigosListener;

    private ListView listView;
    private TextView txtNoFriends;

    private HashMap<String, Ciclista> amigos;
    private Map<String, File> profile_photo;
    private List<Ciclista> amigosList;
    private FriendsAdapter adapter;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        mCurrentUserReference = FirebaseDatabase.getInstance().getReference().child("usuarios").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("amigos");
        mStorageRef = FirebaseStorage.getInstance().getReference();

        amigos = new HashMap<>();
        profile_photo = new HashMap<>();
        txtNoFriends = findViewById(R.id.txtFriendsNoFriends);
        txtNoFriends.setVisibility(View.GONE);
        txtNoFriends.setText("Aún no tienes amigos");
        listView = findViewById(R.id.list_friends_list);
        amigosList = new ArrayList<>();
        adapter = new FriendsAdapter(amigosList, this, TAG, profile_photo);
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
                for (DataSnapshot aux : dataSnapshot.getChildren()) {
                    final String key_friend = (String) aux.getValue();
                    DatabaseReference friendReference = FirebaseDatabase.getInstance().getReference().child("usuarios").child(key_friend);
                    friendReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Ciclista current = dataSnapshot.getValue(Ciclista.class);
                            current.setUid(key_friend);
                            amigos.put(current.getUid(), current);
                            amigosList.add(current);
                            StorageReference pathReference = null;
                            try {
                                pathReference = mStorageRef.child("usuarios/" + current.getUid() + "/photo/fotoPerfil.jpg");
                            } catch (Exception e) {
                                adapter.notifyDataSetChanged();
                                Log.d(TAG, e.getMessage());
                            }
                            if (pathReference != null) {
                                final File localPhoto;
                                try {
                                    localPhoto = File.createTempFile("profile" + key_friend, "jpg");
                                    pathReference.getFile(localPhoto).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                            try {
                                                profile_photo.put(key_friend, localPhoto);
                                                adapter.notifyDataSetChanged();
                                            } catch (Exception e) {
                                                Log.d(TAG, e.getMessage());
                                            }
                                        }
                                    });
                                } catch (IOException e) {
                                    Log.d(TAG, e.getMessage());
                                }
                            }
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
        if (mAmigosListener != null) mCurrentUserReference.removeEventListener(mAmigosListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_friends_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemClicked = item.getItemId();
        if (itemClicked == R.id.it_friends_search) {
            Intent intent = new Intent(getBaseContext(), SearchFriendsActivity.class);
            ArrayList<String> keys = new ArrayList<>();
            for (String aux : amigos.keySet()) {
                keys.add(aux);
            }
            intent.putExtra("amigos", keys);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
