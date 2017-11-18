package com.example.oscar.enbicia2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.clases.ChatMessage;
import com.example.clases.Ciclista;
import com.example.clases.Constants;
import com.example.clases.FriendsAdapter;
import com.example.clases.MessageAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChatActivity extends AppCompatActivity implements ListView.OnItemClickListener{

    private String TAG = "ChatActivity";

    private DatabaseReference mMessageReference;

    private ListView listView;
    private Map<String, ChatMessage> inbox;
    private List<String> inboxUid;
    private ValueEventListener mInboxListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        listView = findViewById(R.id.list_chat_messages);
        mMessageReference = FirebaseDatabase.getInstance().getReference(Constants.PATH_MESSAGES).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        inbox = new HashMap<>();
        inboxUid = new ArrayList<>();
        listView.setOnItemClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ValueEventListener inboxListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                inbox.clear();
                for(DataSnapshot keys_receiver : dataSnapshot.getChildren()){
                    ChatMessage last = null;
                    for(DataSnapshot curr_message : keys_receiver.getChildren()){
                        ChatMessage chatMessage = curr_message.getValue(ChatMessage.class);
                        last = chatMessage;
                    }
                    if(last != null){
                        inbox.put(keys_receiver.getKey(), last);
                    }
                }
                cargarMensajes();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mMessageReference.addValueEventListener(inboxListener);
        mInboxListener = inboxListener;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mInboxListener != null) mMessageReference.removeEventListener(mInboxListener);
    }

    private void cargarMensajes(){
        List<ChatMessage> aux = new ArrayList<>();
        Set<String> key = inbox.keySet();
        for(String key_aux: key){
            aux.add(inbox.get(key_aux));
            inboxUid.add(key_aux);
        }
        MessageAdapter adapter = new MessageAdapter(aux,inboxUid, this);
        listView.setAdapter(adapter);
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(getBaseContext(), ChatDetailActivity.class);
        intent.putExtra("friendUid", inboxUid.get(i));
        startActivity(intent);
    }
}
