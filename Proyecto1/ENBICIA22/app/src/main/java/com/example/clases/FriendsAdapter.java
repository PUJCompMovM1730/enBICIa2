package com.example.clases;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.oscar.enbicia2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by juanpablorn30 on 30/10/17.
 */

public class FriendsAdapter extends ArrayAdapter<Ciclista> implements View.OnClickListener {

    private List<Ciclista> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        Button btnAgregar;
        Button btnEliminar;
    }

    public FriendsAdapter(List<Ciclista> data, Context context) {
        super(context, R.layout.list_search_friends, data);
        this.dataSet = data;
        this.mContext=context;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.btnAgregarAmigo){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            int position = (Integer) view.getTag();
            Ciclista aux = (Ciclista)getItem(position);
            Log.d(Constants.TAG_CLASS, "Pase aca");
            try{
                aux.agregarAmigoFireBase(user.getUid() , aux.getUid());

            }catch (Exception e){
                e.printStackTrace();
            }
        }else if(id == R.id.btnEliminarAmigo){

        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Ciclista dataModel = getItem(position);
        ViewHolder viewHolder;

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_search_friends, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.txtNombreAmigo);
            viewHolder.btnAgregar = convertView.findViewById(R.id.btnAgregarAmigo);
            viewHolder.btnEliminar = convertView.findViewById(R.id.btnEliminarAmigo);
            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }
        viewHolder.txtName.setText(dataModel.getName());
        viewHolder.btnAgregar.setOnClickListener(this);
        viewHolder.btnEliminar.setOnClickListener(this);
        viewHolder.btnAgregar.setTag(position);
        viewHolder.btnEliminar.setTag(position);
        return convertView;
    }
}
