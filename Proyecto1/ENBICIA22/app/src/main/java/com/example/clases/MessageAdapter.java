package com.example.clases;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.oscar.enbicia2.ChatDetailActivity;
import com.example.oscar.enbicia2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by juanpablorn30 on 16/11/17.
 */

public class MessageAdapter extends ArrayAdapter<ChatMessage>{

    private String TAG = "MessageAdapter";
    private List<ChatMessage> dataSet;
    private List<String> friendUid;
    private List<Ciclista> friends;

    Context mContext;

    private static class ViewHolder {
        TextView txtName;
        TextView txtMessage;
        ImageView imgPhoto;
    }

    public MessageAdapter(List<ChatMessage> data,List<String> friendUid,List<Ciclista> friends, Context context) {
        super(context, R.layout.list_messages, data);
        this.dataSet = data;
        this.friendUid = friendUid;
        this.mContext = context;
        this.friends = friends;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage dataModel = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_messages, parent, false);
            viewHolder.txtName = convertView.findViewById(R.id.txt_adapter_name);
            viewHolder.txtMessage = convertView.findViewById(R.id.txt_adapter_message);
            viewHolder.imgPhoto = convertView.findViewById(R.id.img_adapterlist_photo);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // TODO: AGREGAR FOTO ACA
        Ciclista actual = findUid(friendUid.get(position));
        if(actual != null){
            if(actual.getName().isEmpty()){
                viewHolder.txtName.setText(actual.getEmail());
            }else{
                viewHolder.txtName.setText(actual.getName());
            }
            viewHolder.txtMessage.setText(dataModel.getMessageText());
        }
        return convertView;
    }

    private Ciclista findUid(String uid){
        for(Ciclista curr : friends){
            if(curr.getUid().equals(uid)) return curr;
        }
        return null;
    }
}
