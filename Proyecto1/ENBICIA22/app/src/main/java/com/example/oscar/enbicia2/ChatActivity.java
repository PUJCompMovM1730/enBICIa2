package com.example.oscar.enbicia2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.clases.ChatMessage;
import com.example.clases.Ciclista;
import com.example.clases.Constants;
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

public class ChatActivity extends AppCompatActivity implements ListView.OnItemClickListener {

    private String TAG = "ChatActivity";

    private DatabaseReference mMessageReference;

    private ListView listView;
    private Map<String, ChatMessage> inbox;
    private List<String> inboxUid;
    private List<ChatMessage> message_list;
    private List<Ciclista> friends;
    private ValueEventListener mInboxListener;
    private DatabaseReference mCurrentUserReference;
    private MessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        listView = findViewById(R.id.list_chat_messages);
        mMessageReference = FirebaseDatabase.getInstance().getReference(Constants.PATH_MESSAGES).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mCurrentUserReference = FirebaseDatabase.getInstance().getReference().child("usuarios");
        inbox = new HashMap<>();
        message_list = new ArrayList<>();
        inboxUid = new ArrayList<>();
        friends = new ArrayList<>();
        adapter = new MessageAdapter(message_list, inboxUid, friends, this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ValueEventListener inboxListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                inbox.clear();
                message_list.clear();
                inboxUid.clear();
                friends.clear();
                adapter.notifyDataSetChanged();
                for (DataSnapshot keys_receiver : dataSnapshot.getChildren()) {
                    ChatMessage last = null;
                    for (DataSnapshot curr_message : keys_receiver.getChildren()) {
                        ChatMessage chatMessage = curr_message.getValue(ChatMessage.class);
                        last = chatMessage;
                    }
                    if (last != null) {
                        inbox.put(keys_receiver.getKey(), last);
                    }
                }
                cargarMensajes();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        };
        mMessageReference.addValueEventListener(inboxListener);
        mInboxListener = inboxListener;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mInboxListener != null) mMessageReference.removeEventListener(mInboxListener);
    }

    private void cargarMensajes(){
        Set<String> key = inbox.keySet();
        for (String key_aux : key) {
            message_list.add(inbox.get(key_aux));
            inboxUid.add(key_aux);
        }
        for(String curr_key : inboxUid){
            mCurrentUserReference.child(curr_key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Ciclista current = dataSnapshot.getValue(Ciclista.class);
                    current.setUid(dataSnapshot.getKey());
                    friends.add(current);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(getBaseContext(), ChatDetailActivity.class);
        intent.putExtra("friend", friends.get(i));
        startActivity(intent);
    }
}
