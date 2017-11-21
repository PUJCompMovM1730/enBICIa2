package com.example.oscar.enbicia2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.clases.ChatMessage;
import com.example.clases.Ciclista;
import com.example.clases.Constants;
import com.example.clases.MessageDetailAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by juanpablorn30 on 15/11/17.
 */

public class ChatDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "ChatActivity";
    private EditText mensajeTexto;
    private RecyclerView rvMensajes;
    private MessageDetailAdapter adapter;

    private List<ChatMessage> listaMensajes;
    private Ciclista friend;

    private DatabaseReference databaseReferenceSender;
    private DatabaseReference databaseReferenceReceiver;
    private ChildEventListener mMensajesListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);
        mensajeTexto = findViewById(R.id.edit_chat_message);
        rvMensajes = findViewById(R.id.rv_chat_mensaje);
        rvMensajes.setHasFixedSize(true);
        findViewById(R.id.btn_chat_send).setOnClickListener(this);

        friend = getIntent().getParcelableExtra("friend");

        databaseReferenceSender = FirebaseDatabase.getInstance().getReference(Constants.PATH_MESSAGES).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(friend.getUid());
        databaseReferenceReceiver = FirebaseDatabase.getInstance().getReference(Constants.PATH_MESSAGES).child(friend.getUid()).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        listaMensajes = new ArrayList<>();
        adapter = new MessageDetailAdapter(listaMensajes, friend);
        rvMensajes.setLayoutManager(new LinearLayoutManager(this));
        rvMensajes.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        listaMensajes.clear();
        ChildEventListener mesaggeListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatMessage m = dataSnapshot.getValue(ChatMessage.class);
                listaMensajes.add(m);
                adapter.notifyItemInserted(listaMensajes.size() - 1);
                rvMensajes.scrollToPosition(listaMensajes.size() - 1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        };
        databaseReferenceSender.addChildEventListener(mesaggeListener);
        mMensajesListener = mesaggeListener;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mMensajesListener != null)
            databaseReferenceSender.removeEventListener(mMensajesListener);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_chat_send) {
            if (mensajeTexto.getText().length() > 0) {
                databaseReferenceSender.push().setValue(new ChatMessage(mensajeTexto.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getUid()));
                databaseReferenceReceiver.push().setValue(new ChatMessage(mensajeTexto.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getUid()));
                mensajeTexto.getText().clear();
            }
        }
    }
}
