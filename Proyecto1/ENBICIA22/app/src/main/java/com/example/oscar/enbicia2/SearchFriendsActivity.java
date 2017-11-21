package com.example.oscar.enbicia2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;

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

public class SearchFriendsActivity extends AppCompatActivity {

    private String TAG = "SearchFriendsActivity";
    private EditText etFriendsSearch;
    private ListView listView;
    private List<Ciclista> search;
    private Map<String, File> profile_photo;
    private List<String> amigosUid;
    private DatabaseReference mCurrentUserReference;
    private StorageReference mStorageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friends);

        mCurrentUserReference = FirebaseDatabase.getInstance().getReference().child("usuarios");
        mStorageRef = FirebaseStorage.getInstance().getReference();

        amigosUid = getIntent().getStringArrayListExtra("amigos");
        if (amigosUid == null) {
            throw new IllegalArgumentException("Must pass AMIGOS");
        }
        search = new ArrayList<>();
        profile_photo = new HashMap<>();

        listView = findViewById(R.id.list_friends_search);
        etFriendsSearch = findViewById(R.id.edit_search_name);
        etFriendsSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                search.clear();
                if (etFriendsSearch.getText().length() > 0) {
                    mCurrentUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (final DataSnapshot user : dataSnapshot.getChildren()) {
                                Ciclista ciclista = user.getValue(Ciclista.class);
                                String text = etFriendsSearch.getText().toString().toLowerCase();
                                if (!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(user.getKey())) {
                                    if (ciclista.getEmail().toLowerCase().startsWith(text) && !alreadyFriend(user.getKey())) {
                                        ciclista.setUid(user.getKey());
                                        final String key = user.getKey();
                                        search.add(ciclista);
                                        StorageReference pathReference = null;
                                        try {
                                            pathReference = mStorageRef.child("usuarios/" + user.getKey() + "/photo/fotoPerfil.jpg");
                                        } catch (Exception e) {
                                            Log.d(TAG, e.getMessage());
                                        }
                                        if (pathReference != null) {
                                            final File localPhoto;
                                            try {
                                                localPhoto = File.createTempFile("profile" + user.getKey(), "jpg");
                                                pathReference.getFile(localPhoto).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                        profile_photo.put(key, localPhoto);
                                                    }
                                                });
                                            } catch (IOException e) {
                                                Log.d(TAG, e.getMessage());
                                            }
                                        }
                                    }
                                }
                                FriendsAdapter adapter = new FriendsAdapter(search, getBaseContext(), TAG, profile_photo);
                                listView.setAdapter(adapter);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    FriendsAdapter adapter = new FriendsAdapter(search, getBaseContext(), TAG, profile_photo);
                    listView.setAdapter(adapter);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private boolean alreadyFriend(String Uid) {
        for (String aux : amigosUid) {
            if (aux.equals(Uid)) return true;
        }
        return false;
    }
}
