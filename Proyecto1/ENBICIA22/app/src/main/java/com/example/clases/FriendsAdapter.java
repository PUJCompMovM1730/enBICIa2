package com.example.clases;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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
import com.example.oscar.enbicia2.SearchFriendsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by juanpablorn30 on 30/10/17.
 */

public class FriendsAdapter extends ArrayAdapter<Ciclista> implements View.OnClickListener {

    private List<Ciclista> dataSet;
    Context mContext;
    private String name;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        Button btnAgregar;
        Button btnEliminar;
    }

    public FriendsAdapter(List<Ciclista> data, Context context, String name) {
        super(context, R.layout.list_search_friends, data);
        this.dataSet = data;
        this.mContext=context;
        this.name = name;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.btnAgregarAmigo){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            int position = (Integer) view.getTag();
            Ciclista aux = getItem(position);
            aux.agregarAmigoFireBase(user.getUid() , aux.getUid());
            dataSet.remove(position);
            Snackbar.make(view, "¡Amigo añadido exitosamente!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            notifyDataSetChanged();
        }else if(id == R.id.btnEliminarAmigo){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            int position = (Integer) view.getTag();
            Ciclista aux = getItem(position);
            aux.eliminarAmigoFireBase(user.getUid() , aux.getUid());
            dataSet.remove(position);
            Snackbar.make(view, "¡Amigo eliminado exitosamente!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            notifyDataSetChanged();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Ciclista dataModel = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_search_friends, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.txtNombreAmigo);
            viewHolder.btnAgregar = convertView.findViewById(R.id.btnAgregarAmigo);
            viewHolder.btnEliminar = convertView.findViewById(R.id.btnEliminarAmigo);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.txtName.setText(dataModel.getEmail());
        viewHolder.btnAgregar.setOnClickListener(this);
        viewHolder.btnEliminar.setOnClickListener(this);
        viewHolder.btnAgregar.setTag(position);
        viewHolder.btnEliminar.setTag(position);
        if(name.equals("SearchFriendsActivity")){
            viewHolder.btnEliminar.setVisibility(View.GONE);
        }
        else{
            viewHolder.btnAgregar.setVisibility(View.GONE);
        }

        return convertView;
    }
}
