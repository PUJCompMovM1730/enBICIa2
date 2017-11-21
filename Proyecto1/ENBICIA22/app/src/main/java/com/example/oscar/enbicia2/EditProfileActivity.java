package com.example.oscar.enbicia2;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.clases.Ciclista;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "EditProfileActivity";

    EditText name, mail, cell, age;
    Button cancel, edit, save, photo;
    Uri imageUri, downloadPhoto;
    UploadTask uploadTask;

    String name_prev, email_prev, cell_prev, age_prev;

    private DatabaseReference mCurrentUserReference;

    public static final int IMAGE_REQUEST = 100;
    public static final int CAMERA_REQUEST = 20;
    private StorageReference mStorageRef, imageRef;
    private ProgressDialog progressDialog;
    private View parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCurrentUserReference = FirebaseDatabase.getInstance().getReference().child("usuarios").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mStorageRef = FirebaseStorage.getInstance().getReference();

        parentLayout = findViewById(R.id.coordinator_edit_profile);

        name = findViewById(R.id.et_eprofile_name);
        mail = findViewById(R.id.et_eprofile_email);
        cell = findViewById(R.id.et_eprofile_cell);
        age = findViewById(R.id.et_eprofile_age);

        save = findViewById(R.id.btn_eprofile_save);
        cancel = findViewById(R.id.btn_eprofile_cancel);
        edit = findViewById(R.id.btn_eprofile_edit);
        photo = findViewById(R.id.btn_eprofile_photo);
        //TODO: Implementar editar password.
        // Quitar para la ultima entrega
        findViewById(R.id.btn_eprofile_password).setVisibility(View.GONE);

        age.setOnClickListener(this);
        edit.setOnClickListener(this);
        cancel.setOnClickListener(this);
        save.setOnClickListener(this);

        save.setVisibility(View.GONE);
        cancel.setVisibility(View.GONE);
        photo.setVisibility(View.GONE);

        boolean hasPermissionGallery = (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermissionGallery) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    IMAGE_REQUEST);
        } else {
            photo.setOnClickListener(this);
        }

        boolean hasPermissionCamera = (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermissionCamera) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA},
                    CAMERA_REQUEST);
        } else {
            photo.setOnClickListener(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando...");
        progressDialog.show();
        ValueEventListener datosListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Ciclista current = dataSnapshot.getValue(Ciclista.class);
                if (current.getName() != null) {
                    name_prev = current.getName();
                    name.setText(current.getName());
                }
                if (current.getName() != null) {
                    email_prev = current.getEmail();
                    mail.setText(current.getEmail());
                }
                if (current.getName() != null) {
                    cell_prev = current.getNumero_celular();
                    cell.setText(current.getNumero_celular());
                }
                if (current.getDate_birth() != 0) {
                    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(current.getDate_birth());
                    age_prev = formatter.format(calendar.getTime());
                    age.setText(formatter.format(calendar.getTime()));
                } else {
                    age.setText("");
                }
                StorageReference pathReference = mStorageRef.child("usuarios/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/photo/fotoPerfil.jpg");
                try {
                    final File localPhoto = File.createTempFile("profile", "jpg");
                    pathReference.getFile(localPhoto).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                findViewById(R.id.toolbar_layout).setBackground(Drawable.createFromPath(localPhoto.getAbsolutePath()));
                                progressDialog.dismiss();
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mCurrentUserReference.addListenerForSingleValueEvent(datosListener);
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

    private void choosePhoto() {
        final CharSequence[] options = {"Camara", "Galeria", "Cancelar"};
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
        builder.setTitle("Cambiar Foto");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (options[i].equals("Camara")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "fotoPerfil.jpg");
                    imageUri = Uri.fromFile(f);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, CAMERA_REQUEST);
                } else if (options[i].equals("Galeria")) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, IMAGE_REQUEST);
                } else if (options[i].equals("Cancelar")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CAMERA_REQUEST: {
                InputStream istream = null;
                if (resultCode == RESULT_OK) {
                    imageRef = mStorageRef.child("usuarios").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("photo").child(imageUri.getLastPathSegment());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        findViewById(R.id.toolbar_layout).setBackground(Drawable.createFromPath(imageUri.getPath()));
                    }
                }
            }
            case IMAGE_REQUEST: {
                if (resultCode == RESULT_OK) {
                    try {
                        if (data != null) {
                            imageUri = data.getData();
                            File f = new File(getRealPathFromURI(imageUri));
                            imageRef = mStorageRef.child("usuarios").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("photo").child("fotoPerfil.jpg");
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                findViewById(R.id.toolbar_layout).setBackground(Drawable.createFromPath(f.getAbsolutePath()));
                            }
                        }
                    } catch (Exception e) {
                        Log.d(TAG, e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case IMAGE_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(parentLayout, "¡Permiso concedido!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Snackbar.make(parentLayout, "¡La aplicación no puede usar la galeria!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
            case CAMERA_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(parentLayout, "¡Permiso concedido!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Snackbar.make(parentLayout, "¡La aplicación no puede usar la cámara!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        }
    }

    private void uploadFireBase() {
        uploadTask = imageRef.putFile(imageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(parentLayout, "¡Hubo error subiendo la foto!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                downloadPhoto = taskSnapshot.getDownloadUrl();
            }
        });
    }

    private String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.btn_eprofile_edit) {
            cancel.setVisibility(View.VISIBLE);
            save.setVisibility(View.VISIBLE);
            photo.setVisibility(View.VISIBLE);
            edit.setVisibility(View.GONE);
            name.setEnabled(true);
            mail.setEnabled(true);
            cell.setEnabled(true);
            age.setEnabled(true);
        } else if (i == R.id.btn_eprofile_cancel) {
            cancel.setVisibility(View.GONE);
            save.setVisibility(View.GONE);
            photo.setVisibility(View.GONE);
            edit.setVisibility(View.VISIBLE);
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
            if (!name.getText().toString().isEmpty())
                updates.put("name", name.getText().toString());
            if (!mail.getText().toString().isEmpty())
                updates.put("email", mail.getText().toString());
            if (!cell.getText().toString().isEmpty())
                updates.put("numero_celular", cell.getText().toString());
            if (!age.getText().toString().isEmpty()) {
                try {
                    DateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date date = sourceFormat.parse(age.getText().toString());
                    updates.put("date_birth", date.getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (!updates.isEmpty()) {
                FirebaseDatabase.getInstance().getReference().child("usuarios").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(updates);
            }
            uploadFireBase();
            Snackbar.make(view, "¡Se ha guardado la información!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else if (i == R.id.et_eprofile_age) {
            showDatePickerDialog();
        } else if (i == R.id.btn_eprofile_photo) {
            choosePhoto();
        }
    }
}
