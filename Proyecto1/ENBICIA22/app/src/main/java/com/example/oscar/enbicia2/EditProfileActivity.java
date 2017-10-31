package com.example.oscar.enbicia2;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.clases.Ciclista;
import com.example.clases.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "EditProfileActivity";

    EditText name, mail, cell, age;
    Button cancel, edit, save;

    String name_prev, email_prev, cell_prev, age_prev;

    private DatabaseReference mCurrentUserReference;
    private ValueEventListener mAmigosListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        try{
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

            mCurrentUserReference = FirebaseDatabase.getInstance().getReference().child("usuarios").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            name = (EditText) findViewById(R.id.et_eprofile_name);
            mail = (EditText) findViewById(R.id.et_eprofile_email);
            cell = (EditText) findViewById(R.id.et_eprofile_cell);
            age = (EditText) findViewById(R.id.et_eprofile_age);

            save = (Button) findViewById(R.id.btn_eprofile_save);
            cancel = (Button) findViewById(R.id.btn_eprofile_cancel);
            edit = (Button) findViewById(R.id.btn_eprofile_edit);

            // Quitar para la ultima entrega
            findViewById(R.id.btn_eprofile_password).setVisibility(View.GONE);
            findViewById(R.id.btn_eprofile_photo).setVisibility(View.GONE);

            age.setOnClickListener(this);
            edit.setOnClickListener(this);
            cancel.setOnClickListener(this);
            save.setOnClickListener(this);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        ValueEventListener amigosListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Ciclista current = dataSnapshot.getValue(Ciclista.class);
                if( current.getName() != null)
                {
                    name_prev = current.getName();
                    name.setText(current.getName());
                }
                if( current.getName() != null)
                {
                    email_prev = current.getEmail();
                    mail.setText(current.getEmail());
                }
                if( current.getName() != null)
                {
                    cell_prev = current.getNumero_celular();
                    cell.setText(current.getNumero_celular());
                }
                if( current.getDate_birth() != null){

                    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(current.getDate_birth());
                    age_prev = formatter.format(calendar.getTime());
                    age.setText(formatter.format(calendar.getTime()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mCurrentUserReference.addValueEventListener(amigosListener);
        mAmigosListener = amigosListener;
    }

    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                final String selectedDate = day + "/" + (month + 1) + "/" + year;
                age.setText(selectedDate);
            }
        });
        newFragment.show(this.getSupportFragmentManager(), "datePicker");
    }


    @Override
    public void onClick(View view) {
        try {
            int i = view.getId();
            if (i == R.id.btn_eprofile_edit) {
                cancel.setEnabled(true);
                save.setEnabled(true);
                edit.setEnabled(true);
                name.setEnabled(true);
                mail.setEnabled(true);
                cell.setEnabled(true);
                age.setEnabled(true);
            } else if (i == R.id.btn_eprofile_cancel) {
                cancel.setEnabled(true);
                save.setEnabled(false);
                edit.setEnabled(true);
                name.setEnabled(false);
                mail.setEnabled(false);
                cell.setEnabled(false);
                age.setEnabled(false);
                name.setText(name_prev);
                mail.setText(email_prev);
                cell.setText(cell_prev);
                age.setText(age_prev);
            } else if (i == R.id.btn_eprofile_save) {
                Map<String, Object> updates = new HashMap<>();
                if(!name.getText().toString().isEmpty())
                    updates.put("name", name.getText().toString());
                if(!mail.getText().toString().isEmpty())
                    updates.put("email", mail.getText().toString());
                if(!cell.getText().toString().isEmpty())
                    updates.put("numero_celular", cell.getText().toString());
                if(!age.getText().toString().isEmpty())
                {
                    DateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date date = sourceFormat.parse(age.getText().toString());
                    updates.put("date_birth", date.getTime());
                }
                if(!updates.isEmpty()){
                    FirebaseDatabase.getInstance().getReference().child("usuarios").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(updates);
                }
            } else if (i == R.id.et_eprofile_age) {
                showDatePickerDialog();
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }
    }
}
