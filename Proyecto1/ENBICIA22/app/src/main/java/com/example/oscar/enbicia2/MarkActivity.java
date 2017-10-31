package com.example.oscar.enbicia2;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.clases.Constants;
import com.example.clases.EnBiciaa2;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class MarkActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    private String TAG = "MarkActivity";
    private ImageButton rent, thief, store, workshop;
    private PlaceAutocompleteFragment autocompleteFragmentTarget;
    private EditText nombre_sitio;
    private Marker marker_target;
    private LatLng target;
    private GoogleMap mMap;
    private int indx_categoria;
    private EnBiciaa2 enBICIa2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapMark);
        mapFragment.getMapAsync(this);

        enBICIa2 = new EnBiciaa2();
        target = null;
        marker_target = null;
        indx_categoria = 0;

       //Find ids
        nombre_sitio = findViewById(R.id.etMarkNombreUbicacion);
        rent=(ImageButton) findViewById(R.id.imb_mark_rent);
        store=(ImageButton) findViewById(R.id.imb_mark_store);
        thief=(ImageButton) findViewById(R.id.imb_mark_thief);
        workshop=(ImageButton) findViewById(R.id.imb_mark_workshop);


        //Listener ImageButton
        rent.setOnClickListener(this);
        thief.setOnClickListener(this);
        store.setOnClickListener(this);
        workshop.setOnClickListener(this);

        findViewById(R.id.btnMarkGuardarZona).setOnClickListener(this);

        autocompleteFragmentTarget = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_target_mark);
        ViewGroup viewGroupTarget = (ViewGroup) autocompleteFragmentTarget.getView();
        EditText editTextTarget = viewGroupTarget.findViewById(R.id.place_autocomplete_search_input);
        editTextTarget.setTextSize(getResources().getDimension(R.dimen.search_edit_size));
        editTextTarget.setHint(getResources().getString(R.string.define_location));
        ImageButton imageClearButtonTarget = viewGroupTarget.findViewById(R.id.place_autocomplete_clear_button);
        Log.d("SEARCH: ", "Componente: " + viewGroupTarget.findViewById(R.id.place_autocomplete_search_button));
        imageClearButtonTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewGroup viewGroupTarget = (ViewGroup) autocompleteFragmentTarget.getView();
                EditText editTextTarget = viewGroupTarget.findViewById(R.id.place_autocomplete_search_input);
                editTextTarget.getText().clear();
                ImageButton imageClearButtonTarget = viewGroupTarget.findViewById(R.id.place_autocomplete_clear_button);
                imageClearButtonTarget.setVisibility(View.GONE);
                if(target != null) marker_target.remove();
                target = null;
                //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), Constants.getBoundsZoomLevel(bounds, width, height)));
            }
        });

        autocompleteFragmentTarget.getView().setBackgroundResource(R.drawable.autocomplete_fragment_background);
        autocompleteFragmentTarget.setBoundsBias(new LatLngBounds(
                new LatLng(Constants.lowerLeftLatitude, Constants.lowerLeftLongitude),
                new LatLng(Constants.upperRightLatitude, Constants.upperRigthLongitude)));

        autocompleteFragmentTarget.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                target = place.getLatLng();
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(target);
                if(marker_target != null)
                    marker_target.remove();
                marker_target = mMap.addMarker(new MarkerOptions().position(target).title("Zona").icon(BitmapDescriptorFactory.fromResource(R.drawable.location)));
                LatLngBounds bounds = builder.build();
                int width = findViewById(R.id.mapMark).getWidth();
                int height = findViewById(R.id.mapMark).getHeight();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), Constants.getBoundsZoomLevel(bounds, width, height)));
            }

            @Override
            public void onError(Status status) {
                Toast.makeText(getBaseContext(), "Error utilizando busqueda", Toast.LENGTH_SHORT).show();
                Log.i("PLACE SELECT", "An error occurred: " + status);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if( i == R.id.imb_mark_rent ) {
            rent.setColorFilter(Color.rgb(20,206,206));
            thief.setColorFilter(Color.rgb(0,0,0) );
            store.setColorFilter(Color.rgb(0,0,0));
            workshop.setColorFilter(Color.rgb(0,0,0));
            indx_categoria = 0;
        }
        else if( i == R.id.imb_mark_thief ) {
            rent.setColorFilter(Color.rgb(0,0,0));
            thief.setColorFilter(Color.rgb(206,20,20) );
            store.setColorFilter(Color.rgb(0,0,0));
            workshop.setColorFilter(Color.rgb(0,0,0) );
            indx_categoria = 1;
        }
        else if( i == R.id.imb_mark_store ) {
            rent.setColorFilter(Color.rgb(0,0,0));
            thief.setColorFilter(Color.rgb(0,0,0) );
            store.setColorFilter(Color.rgb(230,159,0));
            workshop.setColorFilter(Color.rgb(0,0,0));
            indx_categoria = 2;
        }
        else if( i == R.id.imb_mark_workshop ) {
            rent.setColorFilter(Color.rgb(0,0,0));
            thief.setColorFilter(Color.rgb(0,0,0) );
            store.setColorFilter(Color.rgb(0,0,0));
            workshop.setColorFilter(Color.rgb(181,166,209) );
            indx_categoria = 3;
        }else if( i == R.id.btnMarkGuardarZona){
            enBICIa2.agregarSitioInteresFireBase(nombre_sitio.getText().toString(), target.latitude, target.longitude, indx_categoria);
            Snackbar.make(view, "El marcador se ha agregado correctamente", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            nombre_sitio.getText().clear();
            rent.setColorFilter(Color.rgb(0,0,0));
            thief.setColorFilter(Color.rgb(0,0,0) );
            store.setColorFilter(Color.rgb(0,0,0));
            workshop.setColorFilter(Color.rgb(0,0,0));
            ((EditText)autocompleteFragmentTarget.getView().findViewById(R.id.place_autocomplete_search_input)).getText().clear();
        }
    }

}
