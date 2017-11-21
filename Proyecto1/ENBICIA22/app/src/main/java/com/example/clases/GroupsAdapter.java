package com.example.clases;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.oscar.enbicia2.GroupTripActivity;
import com.example.oscar.enbicia2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.List;

public class GroupsAdapter extends ArrayAdapter<Grupo> implements View.OnClickListener{

    private String TAG = "GroupsAdapter";
    private List<Grupo> dataSet;
    Context mContext;
    private String name;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        Button btnUnsuscribe;
        Button btnSuscribe;
    }

    public GroupsAdapter(List<Grupo> data, Context context, String name) {
        super(context, R.layout.list_group, data);
        this.dataSet = data;
        this.mContext = context;
        this.name = name;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_groups_suscribe) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            int position = (Integer) view.getTag();
            Grupo aux = getItem(position);
            aux.agregarParticipante(aux.getgId(), user.getUid());
            dataSet.remove(position);
            Snackbar.make(view, "¡Suscripción exitosa!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            notifyDataSetChanged();
        } else if (id == R.id.btn_groups_unsuscribe) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            int position = (Integer) view.getTag();
            Grupo aux = getItem(position);
            aux.eliminarParticipante(aux.getgId(), user.getUid());
            dataSet.remove(position);
            Snackbar.make(view, "¡Has dejado de seguir al grupo!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            notifyDataSetChanged();
        } else if (id == R.id.txt_adapter_name){
            if (!name.equals("SearchGroupActivity")){
                Intent intent = new Intent(mContext, GroupTripActivity.class);
                int position = (Integer) view.getTag();
                Grupo aux = getItem(position);
                intent.putExtra("grupoId", aux.getgId());
                mContext.startActivity(intent);
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Grupo dataModel = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_group, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.txt_adapter_name);
            viewHolder.btnUnsuscribe = convertView.findViewById(R.id.btn_groups_unsuscribe);
            viewHolder.btnSuscribe = convertView.findViewById(R.id.btn_groups_suscribe);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.txtName.setText(dataModel.getNombre());
        viewHolder.btnSuscribe.setOnClickListener(this);
        viewHolder.txtName.setOnClickListener(this);
        viewHolder.btnUnsuscribe.setOnClickListener(this);
        viewHolder.btnSuscribe.setTag(position);
        viewHolder.btnUnsuscribe.setTag(position);
        viewHolder.txtName.setTag(position);
        if (name.equals("SearchGroupActivity")) {
            viewHolder.btnUnsuscribe.setVisibility(View.GONE);
        } else {
            viewHolder.btnSuscribe.setVisibility(View.GONE);
        }
        return convertView;
    }


}
