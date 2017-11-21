package com.example.clases;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.oscar.enbicia2.R;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by carlitos on 11/5/17.
 */

public class MessageDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String TAG = "MessageDetailAdapter";
    private List<ChatMessage> dataSet;
    private Ciclista friend;

    public static class ViewHolderSender extends RecyclerView.ViewHolder {

        private TextView mensaje;
        private TextView hora;

        public ViewHolderSender(View itemView) {
            super(itemView);
            mensaje = itemView.findViewById(R.id.text_mensaje_enviado);
            hora = itemView.findViewById(R.id.text_tiempo_mensaje);
        }
    }

    public static class ViewHolderReceiver extends RecyclerView.ViewHolder {

        private TextView nombre;
        private TextView mensaje;
        private TextView hora;

        public ViewHolderReceiver(View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.text_nombre_usuario);
            mensaje = itemView.findViewById(R.id.text_mensaje_llega);
            hora =itemView.findViewById(R.id.text_tiempo_mensaje);
        }
    }

    public MessageDetailAdapter(List<ChatMessage> data, Ciclista friend) {
        this.dataSet = data;
        this.friend= friend;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case 0:
                return new ViewHolderReceiver(LayoutInflater.from(parent.getContext()).inflate(R.layout.incoming_message,parent,false));
            case 2:
                return new ViewHolderSender(LayoutInflater.from(parent.getContext()).inflate(R.layout.message_sent,parent,false));
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage chatMessage = dataSet.get(position);
        if(chatMessage.getUidSender().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            return 2;
        }else{
            return 0;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatMessage chatMessage;
        Date d;
        SimpleDateFormat sdf;
        switch (holder.getItemViewType()){
            case 0:
                ViewHolderReceiver holder1 = (ViewHolderReceiver) holder;
                chatMessage = dataSet.get(position);
                d = new Date(chatMessage.getMessageTime());
                sdf = new SimpleDateFormat("hh:mm a");
                Log.d(TAG, friend.getName());
                Log.d(TAG, friend.getEmail());
                if(friend.getName().length() > 0)
                    holder1.nombre.setText(friend.getName());
                else
                    holder1.nombre.setText(friend.getEmail());
                holder1.mensaje.setText(chatMessage.getMessageText());
                holder1.hora.setText(sdf.format(d));
                break;
            case 2:
                ViewHolderSender holder2 = (ViewHolderSender) holder;
                chatMessage = dataSet.get(position);
                d = new Date(chatMessage.getMessageTime());
                sdf = new SimpleDateFormat("hh:mm a");
                holder2.mensaje.setText(chatMessage.getMessageText());
                holder2.hora.setText(sdf.format(d));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
