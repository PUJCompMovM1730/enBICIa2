package com.example.oscar.enbicia2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.clases.GroupsAdapter;
import com.example.clases.Grupo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GroupActivity extends AppCompatActivity {

    private String TAG = "GroupActivity";
    private DatabaseReference mCurrentUserReference;
    private ValueEventListener mGruposListener;
    private HashMap<String, Grupo> grupos;
    private List<Grupo> gruposList;
    private GroupsAdapter adapter;
    Button newGroup, searchGroup;
    ListView listTrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        mCurrentUserReference = FirebaseDatabase.getInstance().getReference().child("usuarios").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("grupos");
        grupos = new HashMap<>();
        newGroup = (Button)findViewById(R.id.btn_group_new);
        listTrip = (ListView)findViewById(R.id.list_group_trip);
        newGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newGroup();
            }
        });
        gruposList = new ArrayList<>();
        adapter = new GroupsAdapter(gruposList, this, TAG);
        listTrip.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ValueEventListener gruposListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                grupos.clear();
                gruposList.clear();
                for(DataSnapshot aux : dataSnapshot.getChildren()){
                    final String key_group = (String) aux.getValue();
                    DatabaseReference groupReference = FirebaseDatabase.getInstance().getReference().child("groups").child(key_group);
                    groupReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Grupo current = dataSnapshot.getValue(Grupo.class);
                            current.setgId(key_group);
                            grupos.put(current.getgId(), current);
                            gruposList.add(current);
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
        mCurrentUserReference.addValueEventListener(gruposListener);
        mGruposListener = gruposListener;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mGruposListener != null) mCurrentUserReference.removeEventListener(mGruposListener);
    }

    void newGroup(){
        Intent intent = new Intent(getBaseContext(), NewTripActivity.class);
        startActivity(intent);
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
            Intent intent = new Intent(getBaseContext(), SearchGroupActivity.class);
            ArrayList<String> keys = new ArrayList<>();
            for(String aux : grupos.keySet()){
                keys.add(aux);
            }
            intent.putExtra("grupos", keys);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

}
