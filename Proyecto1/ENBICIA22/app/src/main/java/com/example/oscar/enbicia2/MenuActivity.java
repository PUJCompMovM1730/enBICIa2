package com.example.oscar.enbicia2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class MenuActivity extends AppCompatActivity implements  View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //Listener ImageButton
        findViewById(R.id.lin_perfil_menu).setOnClickListener(this);
        findViewById(R.id.lin_mensajes_menu).setOnClickListener(this);
        findViewById(R.id.lin_grupos_menu).setOnClickListener(this);
        findViewById(R.id.lin_amigos_menu).setOnClickListener(this);
        findViewById(R.id.lin_planeados_menu).setOnClickListener(this);
        findViewById(R.id.lin_marcador_menu).setOnClickListener(this);

        //Listener Button
        findViewById(R.id.lin_ruta_menu).setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_general_menu_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemClicked = item.getItemId();
        if(itemClicked == R.id.it_exit_menu){
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if( i == R.id.lin_perfil_menu ) {
            Intent intent= new Intent(getBaseContext(),ProfileActivity.class);
            startActivity(intent);
        }
        if( i == R.id.lin_amigos_menu) {
            Intent intent= new Intent(getBaseContext(),FriendsActivity.class);
            startActivity(intent);
        }
        if( i == R.id.lin_grupos_menu) {
            Intent intent= new Intent(getBaseContext(),GroupActivity.class);
            startActivity(intent);
        }
        if( i == R.id.lin_marcador_menu ) {
            Intent intent= new Intent(getBaseContext(),MarkActivity.class);
            startActivity(intent);
        }
        if( i == R.id.lin_mensajes_menu ) {
            Intent intent= new Intent(getBaseContext(),ChatActivity.class);
            startActivity(intent);
        }
        if( i == R.id.lin_planeados_menu ) {
            Intent intent= new Intent(getBaseContext(),TourActivity.class);
            startActivity(intent);
        }
        if( i == R.id.lin_ruta_menu ) {
            Intent intent= new Intent(getBaseContext(),RoutesActivity.class);
            startActivity(intent);
        }


    }
}
