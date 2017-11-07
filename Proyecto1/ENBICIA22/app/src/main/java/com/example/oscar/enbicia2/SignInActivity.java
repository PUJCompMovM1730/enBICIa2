package com.example.oscar.enbicia2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clases.Constants;
import com.example.clases.EnBiciaa2;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    Button bsignin;
    TextView bvolver;

    EditText user,email,cell, pass1, pass2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //Instancia Firebase
        mAuth = FirebaseAuth.getInstance();

        //Creación e instancia Elements
        bsignin = (Button) findViewById(R.id.reg);
        bvolver = (TextView) findViewById(R.id.volver);

        // Button listeners
        findViewById(R.id.reg).setOnClickListener(this);
        findViewById(R.id.volver).setOnClickListener(this);

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    //Valida que el correo tenga un formato específico
    private final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    //Valida que la contraseña tenga un formato específico

    public static boolean isValidPassword(String pswd) {
        //check that there are letters
        if(!pswd.matches("[a-zA-Z]+"))
            return false;  //nope no letters, stop checking and fail!
        //check if there are any numbers
        if(!pswd.matches("[0-9]+"))
            return false;  //nope no numbers, stop checking and fail!
        //check any valid special characters
        //if(!pswd.matches("[.!#*()?,]+"))
          //  return false;  //nope no special chars, fail!

        //everything has passed so far, lets return valid
        return true;

    }

    public void create_account(){
        user = (EditText) findViewById(R.id.et_signIn_name);
        email = (EditText) findViewById(R.id.mail);
        cell = (EditText) findViewById(R.id.cellphone);
        pass1 = (EditText) findViewById(R.id.pass);
        pass2 = (EditText) findViewById(R.id.confirmpass);
        if(user.getText().toString().isEmpty()) {
           user.setError("Falta: Nombre de usuario");
            return;
        }
        if(email.getText().toString().isEmpty()) {
            email.setError("Falta: Correo");
            return;
        }
        if(!isValidEmail(email.getText().toString())) {
            email.setError("Correo inválido");
            return;
        }
        if(cell.getText().toString().isEmpty()) {
            cell.setError("Falta: Teléfono Celular");
            return;
        }
        if(pass1.getText().toString().isEmpty()) {
            pass1.setError("Falta: Contraseña");
            return;
        }
        if(pass2.getText().toString().isEmpty()) {
            pass2.setError("Falta: Confirmar contraseña");
            return;
        }
        if(!pass1.getText().toString().equals(pass2.getText().toString())){
            pass1.setError("Las contraseñas no son iguales");
            return;
        }

        /*if(!isValidPassword(pass1.getText().toString())){
            pass1.setError("La contraseña no es válida: 6 o más caracteres");
            return false;
        }*/

        mAuth.createUserWithEmailAndPassword(email.getText().toString(), pass1.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("sign_in", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Constants.enBICIa2 = new EnBiciaa2();
                            agregarCiclistaFireBase(user);
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("sign_in", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getBaseContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    //Valida si el usuario ya ha iniciado sesión
    private void updateUI(FirebaseUser currentUser) {
        Log.d("email-password:", "Entre a updateUI");
        Intent intent = new Intent(getBaseContext(), MenuActivity.class);
        if( currentUser != null ){
            startActivity(intent);
            finish();
        }
    }

    private void agregarCiclistaFireBase(FirebaseUser user){
        String email = user.getEmail();
        Date date_birth = null;
        String Uid = user.getUid();
        Constants.enBICIa2.agregarCiclistaFireBase(Uid, this.user.getText().toString(), email, date_birth, cell.getText().toString());
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(this,"click",Toast.LENGTH_SHORT);
        int i = view.getId();
        if( i == R.id.reg ) {
            create_account();
        } else if( i == R.id.volver ){

            Intent intent= new Intent(getBaseContext(),LoginActivity.class);
            startActivity(intent);
        }
    }
}
