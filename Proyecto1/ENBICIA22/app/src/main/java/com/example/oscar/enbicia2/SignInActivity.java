package com.example.oscar.enbicia2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Button bsignin= (Button) findViewById(R.id.registrar);
        Button bvolver= (Button) findViewById(R.id.volver);

        bsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent= new Intent(getBaseContext(),MenuActivity.class);
                startActivity(intent);
            }
        });
        bvolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent= new Intent(getBaseContext(),LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
