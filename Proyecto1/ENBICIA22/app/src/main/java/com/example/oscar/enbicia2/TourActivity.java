package com.example.oscar.enbicia2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class TourActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tours);

        LinearLayout lrecorrido= (LinearLayout) findViewById(R.id.tour1);
        lrecorrido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getBaseContext(),TourDetailActivity.class);
                startActivity(intent);
            }
        });

        ImageButton bmenu= (ImageButton) findViewById(R.id.atras);
        bmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getBaseContext(),MenuActivity.class);
                startActivity(intent);
            }
        });
    }
}
