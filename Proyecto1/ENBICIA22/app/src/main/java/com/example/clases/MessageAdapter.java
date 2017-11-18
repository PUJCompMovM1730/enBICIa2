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

import java.util.List;

/**
 * Created by juanpablorn30 on 16/11/17.
 */

public class MessageAdapter extends ArrayAdapter<ChatMessage>{

    private String TAG = "MessageAdapter";
    private List<ChatMessage> dataSet;
    private List<String> friendUid;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtMessage;
        ImageView imgPhoto;
    }

    public MessageAdapter(List<ChatMessage> data,List<String> friendUid, Context context) {
        super(context, R.layout.list_messages, data);
        this.dataSet = data;
        this.friendUid = friendUid;
        this.mContext = context;
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
        // TODO: BORRAR ESTO
        Ciclista actual = (Ciclista) Constants.enBICIa2.getUsuarios().get(friendUid.get(position));
        if(actual.getName().isEmpty()){
            viewHolder.txtName.setText(actual.getEmail());
        }else{
            viewHolder.txtName.setText(actual.getName());
        }
        viewHolder.txtMessage.setText(dataModel.getMessageText());
        return convertView;
    }
}
