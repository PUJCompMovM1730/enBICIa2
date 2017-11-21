package com.example.oscar.enbicia2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.clases.Constants;
import com.example.clases.FriendsAdapter;
import com.example.clases.GroupsAdapter;
import com.example.clases.Grupo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SearchGroupActivity extends AppCompatActivity {

    EditText groupName; ListView result;
    private String TAG = "SearchGroupActivity";
    private List<Grupo> search;
    private List<String> gruposId;
    private DatabaseReference mCurrentGroupReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_group);

        mCurrentGroupReference = FirebaseDatabase.getInstance().getReference().child("grupos");

        groupName = (EditText)findViewById(R.id.edit_sgroup_name);
        result = (ListView)findViewById(R.id.list_group_search);
        gruposId = getIntent().getStringArrayListExtra("grupos");
        if(gruposId == null){
            throw new IllegalArgumentException("Must pass GRUPOS");
        }
        search = new ArrayList<>();
        groupName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("SEARCH", "lalalalalalala ice creaam");
                search.clear();
                if (groupName.getText().length() > 0) {
                    mCurrentGroupReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d("SEARCH", "lalalalalalala ice critica");
                            for (final DataSnapshot group : dataSnapshot.getChildren()) {
                                Grupo grupo = group.getValue(Grupo.class);
                                String text = groupName.getText().toString().toLowerCase();
                                Log.d("SEARCH", "lalalalalalala ice tablon");
                                if (grupo.getNombre().toLowerCase().startsWith(text) && !alreadyMember(group.getKey())) {
                                    grupo.setgId(group.getKey());
                                    search.add(grupo);
                                    Log.d("SEARCH", "lalalalalalala ice curioso");
                                }
                                GroupsAdapter adapter = new GroupsAdapter(search, getBaseContext(), TAG);
                                result.setAdapter(adapter);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                } else {
                    GroupsAdapter adapter = new GroupsAdapter(search, getBaseContext(), TAG);
                    result.setAdapter(adapter);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private boolean alreadyMember(String gId){
        for(String aux : gruposId){
            if(aux.equals(gId)) return true;
        }
        return false;
    }

}
