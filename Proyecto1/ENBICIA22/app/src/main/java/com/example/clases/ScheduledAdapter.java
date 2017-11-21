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

import com.example.oscar.enbicia2.ListScheduledActivity;
import com.example.oscar.enbicia2.R;
import com.example.oscar.enbicia2.SearchRouteActivity;
import com.example.oscar.enbicia2.ViewScheduledActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

/**
 * Created by juanpablorn30 on 30/10/17.
 */

public class ScheduledAdapter extends ArrayAdapter<RecorridoGrupal> implements View.OnClickListener {

    private String TAG = "ScheduledAdapter";
    private List<RecorridoGrupal> dataSet;
    private List<String> rutaId;
    Context mContext;
    private String name;

    private static class ViewHolder {

        TextView txtName;
        Button btnAgregar;
        Button btnEliminar;
        Button btnVer;
        ImageView imgPhoto;
    }

    public ScheduledAdapter(List<RecorridoGrupal> data,List<String> rutaId, Context context, String name) {
        super(context, R.layout.list_add_route, data);
        this.dataSet = data;
        this.mContext = context;
        this.name = name;
        this.rutaId = rutaId;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        int position = (Integer) view.getTag();
        RecorridoGrupal aux = getItem(position);
        if (id == R.id.btn_adapter2_add) {
            aux.agregarRecorridoFireBase(aux, FirebaseAuth.getInstance().getCurrentUser().getUid());
            rutaId.add(dataSet.get(position).getId());
            dataSet.remove(position);
            notifyDataSetChanged();
            Snackbar.make(view, "¡Ruta añadida exitosamente!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else if (id == R.id.btn_adapter2_delete) {
            aux.eliminarRecorridoFireBase(aux, FirebaseAuth.getInstance().getCurrentUser().getUid());
            dataSet.remove(position);
            notifyDataSetChanged();
            Snackbar.make(view, "¡Ruta eliminado exitosamente!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }else if (id == R.id.btn_adapter2_watch) {
            Intent intent= new Intent(getContext(), ViewScheduledActivity.class);
            intent.putExtra("recorrido", aux.getId());
            String tipo = "";
            if(aux.getTipo() != null) tipo = aux.getTipo();
            intent.putExtra("tipo", tipo);
            mContext.startActivity(intent);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RecorridoGrupal dataModel = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_add_route, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.txt_adapter2_name);
            viewHolder.btnAgregar = convertView.findViewById(R.id.btn_adapter2_add);
            viewHolder.btnEliminar = convertView.findViewById(R.id.btn_adapter2_delete);
            viewHolder.btnVer = convertView.findViewById(R.id.btn_adapter2_watch);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.txtName.setText(dataModel.getNombre());
        viewHolder.btnAgregar.setOnClickListener(this);
        viewHolder.btnEliminar.setOnClickListener(this);
        viewHolder.btnVer.setOnClickListener(this);
        viewHolder.btnAgregar.setTag(position);
        viewHolder.btnEliminar.setTag(position);
        viewHolder.btnVer.setTag(position);
        if (name.equals("SearchRoute")) {
            viewHolder.btnEliminar.setVisibility(View.GONE);
        } else {
            viewHolder.btnAgregar.setVisibility(View.GONE);
        }
        return convertView;
    }
}
