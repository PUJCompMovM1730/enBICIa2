package com.example.oscar.enbicia2;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        ImageButton batras= (ImageButton) findViewById(R.id.atras);
        batras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ImageButton bperfil= (ImageButton) findViewById(R.id.perfil);
        bperfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getBaseContext(),profile.class);
                startActivity(intent);
            }
        });

        ImageButton brecorrido= (ImageButton) findViewById(R.id.recorrido);
        brecorrido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getBaseContext(),Routes.class);
                startActivity(intent);
            }
        });
        ImageButton bruta= (ImageButton) findViewById(R.id.ruta);
        bruta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getBaseContext(),tours.class);
                startActivity(intent);
            }
        });
        ImageButton btienda= (ImageButton) findViewById(R.id.tienda);
        btienda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getBaseContext(),tienda.class);
                startActivity(intent);
            }
        });
    }
}
