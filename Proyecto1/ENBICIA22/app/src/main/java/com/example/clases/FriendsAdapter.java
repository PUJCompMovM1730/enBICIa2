package com.example.clases;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
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

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by juanpablorn30 on 30/10/17.
 */

public class FriendsAdapter extends ArrayAdapter<Ciclista> implements View.OnClickListener {

    private String TAG = "FriendsAdapter";
    private List<Ciclista> dataSet;
    private Map<String, File> profile_photo;
    Context mContext;
    private String name;

    private static class ViewHolder {
        TextView txtName;
        Button btnAgregar;
        Button btnEliminar;
        Button btnMensaje;
        ImageView imgPhoto;
    }

    public FriendsAdapter(List<Ciclista> data, Context context, String name, Map<String, File> profile_photo) {
        super(context, R.layout.list_search_friends, data);
        this.dataSet = data;
        this.mContext = context;
        this.name = name;
        this.profile_photo = profile_photo;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_adapter_add) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            int position = (Integer) view.getTag();
            Ciclista aux = dataSet.get(position);
            aux.agregarAmigoFireBase(user.getUid(), aux.getUid());
            dataSet.remove(position);
            Snackbar.make(view, "¡Amigo añadido exitosamente!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            notifyDataSetChanged();
        } else if (id == R.id.btn_adapter_delete) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            int position = (Integer) view.getTag();
            Ciclista aux = dataSet.get(position);
            aux.eliminarAmigoFireBase(user.getUid(), aux.getUid());
            dataSet.remove(position);
            Snackbar.make(view, "¡Amigo eliminado exitosamente!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            notifyDataSetChanged();
        } else if (id == R.id.btn_adapter_message) {
            Intent intent = new Intent(mContext, ChatDetailActivity.class);
            intent.putExtra("friendUid", dataSet.get((Integer) view.getTag()).getUid());
            mContext.startActivity(intent);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Ciclista dataModel = dataSet.get(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_search_friends, parent, false);
            viewHolder.txtName = convertView.findViewById(R.id.txt_adapter_name);
            viewHolder.btnAgregar = convertView.findViewById(R.id.btn_adapter_add);
            viewHolder.btnEliminar = convertView.findViewById(R.id.btn_adapter_delete);
            viewHolder.btnMensaje = convertView.findViewById(R.id.btn_adapter_message);
            viewHolder.imgPhoto = convertView.findViewById(R.id.img_adapterlist_photo);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        try {
            if (profile_photo != null && profile_photo.containsKey(dataModel.getUid())) {
                Log.d(TAG, dataModel.getUid());
                Log.d(TAG, dataModel.getName());
                if (profile_photo.get(dataModel.getUid()).exists()){
                    Log.d(TAG, profile_photo.get(dataModel.getUid()).getAbsolutePath());
                    viewHolder.imgPhoto.setImageBitmap(BitmapFactory.decodeFile(profile_photo.get(dataModel.getUid()).getAbsolutePath()));
                }
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }
        if (dataModel.getName().isEmpty())
            viewHolder.txtName.setText(dataModel.getEmail());
        else
            viewHolder.txtName.setText(dataModel.getName());
        viewHolder.btnAgregar.setOnClickListener(this);
        viewHolder.btnEliminar.setOnClickListener(this);
        viewHolder.btnMensaje.setOnClickListener(this);
        viewHolder.btnAgregar.setTag(position);
        viewHolder.btnEliminar.setTag(position);
        viewHolder.btnMensaje.setTag(position);
        if (name.equals("SearchFriendsActivity")) {
            viewHolder.btnEliminar.setVisibility(View.GONE);
            viewHolder.btnMensaje.setVisibility(View.GONE);
        }else if(name.equals("ViewScheduledActivity")){
            viewHolder.btnEliminar.setVisibility(View.GONE);
            viewHolder.btnMensaje.setVisibility(View.GONE);
            viewHolder.btnAgregar.setVisibility(View.GONE);
        }else {
            viewHolder.btnAgregar.setVisibility(View.GONE);
        }

        return convertView;
    }
}
