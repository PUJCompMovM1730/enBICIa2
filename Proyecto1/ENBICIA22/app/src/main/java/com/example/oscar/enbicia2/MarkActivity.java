package com.example.oscar.enbicia2;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.clases.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
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


import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.support.design.widget.Snackbar.*;


public class MarkActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private ImageButton rent, thief, store, workshop;
    private PlaceAutocompleteFragment autocompleteFragmentTarget;
    private EditText nombre_sitio;
    private EditText editTextTarget;
    private Marker marker_target;
    private LatLng target;
    private GoogleMap mMap;
    private int indx_categoria;
    private GoogleApiClient googleApiClient;

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark);

        setUpGClient();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapMark);
        mapFragment.getMapAsync(this);
        target = null;
        marker_target = null;
        indx_categoria = -1;

        //Find ids
        nombre_sitio = findViewById(R.id.etMarkNombreUbicacion);
        rent = findViewById(R.id.imb_mark_rent);
        store = findViewById(R.id.imb_mark_store);
        thief = findViewById(R.id.imb_mark_thief);
        workshop = findViewById(R.id.imb_mark_workshop);


        //Listener ImageButton
        rent.setOnClickListener(this);
        thief.setOnClickListener(this);
        store.setOnClickListener(this);
        workshop.setOnClickListener(this);

        findViewById(R.id.btnMarkGuardarZona).setOnClickListener(this);

        autocompleteFragmentTarget = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_target_mark);
        ViewGroup viewGroupTarget = (ViewGroup) autocompleteFragmentTarget.getView();
        editTextTarget = viewGroupTarget.findViewById(R.id.place_autocomplete_search_input);
        editTextTarget.setTextSize(getResources().getDimension(R.dimen.search_edit_size));
        editTextTarget.setHint(getResources().getString(R.string.define_location));
        ImageButton imageClearButtonTarget = viewGroupTarget.findViewById(R.id.place_autocomplete_clear_button);
        Log.d("SEARCH: ", "Componente: " + viewGroupTarget.findViewById(R.id.place_autocomplete_search_button));
        imageClearButtonTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewGroup viewGroupTarget = (ViewGroup) autocompleteFragmentTarget.getView();
                editTextTarget = viewGroupTarget.findViewById(R.id.place_autocomplete_search_input);
                editTextTarget.getText().clear();
                ImageButton imageClearButtonTarget = viewGroupTarget.findViewById(R.id.place_autocomplete_clear_button);
                imageClearButtonTarget.setVisibility(View.GONE);
                if (target != null) marker_target.remove();
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
                if (marker_target != null)
                    marker_target.remove();

                marker_target = mMap.addMarker(new MarkerOptions().position(target).title("Zona").draggable(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.location)));
                LatLngBounds bounds = builder.build();
                int width = findViewById(R.id.mapMark).getWidth();
                int height = findViewById(R.id.mapMark).getHeight();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), Constants.getBoundsZoomLevel(bounds, width, height)));

            }

            @Override
            public void onError(Status status) {

                make(findViewById(R.id.lin_mark_parent), "No se ha seleccionado todos los datos", LENGTH_LONG)
                        .setAction("Action", null).show();
                Log.i("PLACE SELECT", "An error occurred: " + status);
            }
        });

    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        rent.setColorFilter(Color.rgb(0, 0, 0));
        thief.setColorFilter(Color.rgb(0, 0, 0));
        store.setColorFilter(Color.rgb(0, 0, 0));
        workshop.setColorFilter(Color.rgb(0, 0, 0));
        if (i == R.id.imb_mark_rent) {
            rent.setColorFilter(Color.rgb(20, 206, 206));
            indx_categoria = 1;
        } else if (i == R.id.imb_mark_thief) {

            thief.setColorFilter(Color.rgb(206, 20, 20));
            indx_categoria = 0;
        } else if (i == R.id.imb_mark_store) {
            store.setColorFilter(Color.rgb(230, 159, 0));
            indx_categoria = 2;
        } else if (i == R.id.imb_mark_workshop) {
            workshop.setColorFilter(Color.rgb(181, 166, 209));
            indx_categoria = 3;
        } else if (i == R.id.btnMarkGuardarZona) {
            if (nombre_sitio.getText().toString().isEmpty() || editTextTarget.getText().toString() == null || indx_categoria == -1) {
                make(view, "No se ha seleccionado todos los datos", LENGTH_LONG)
                        .setAction("Action", null).show();
            } else {
                Constants.enBICIa2.agregarSitioInteresFireBase(nombre_sitio.getText().toString(), marker_target.getPosition().latitude, marker_target.getPosition().longitude, indx_categoria);
                make(view, "El marcador se ha agregado correctamente", LENGTH_LONG)
                        .setAction("Action", null).show();
                nombre_sitio.getText().clear();
                rent.setColorFilter(Color.rgb(0, 0, 0));
                thief.setColorFilter(Color.rgb(0, 0, 0));
                store.setColorFilter(Color.rgb(0, 0, 0));
                workshop.setColorFilter(Color.rgb(0, 0, 0));
                ((EditText) autocompleteFragmentTarget.getView().findViewById(R.id.place_autocomplete_search_input)).getText().clear();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng bogota = new LatLng(4.65, -74.05);
        checkPermission();

        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(bogota));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(Constants.ZOOM_CITY));

        //Permite saber cuando se ha dado click en el botón de lolalización
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                @SuppressLint("MissingPermission")

                Location currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                LatLng latlng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                builder.include(latlng);
                if (marker_target != null)
                    marker_target.remove();

                marker_target = mMap.addMarker(new MarkerOptions().position(latlng).title("Zona").draggable(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.location)));
                LatLngBounds bounds = builder.build();
                int width = findViewById(R.id.mapMark).getWidth();
                int height = findViewById(R.id.mapMark).getHeight();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), Constants.getBoundsZoomLevel(bounds, width, height)));
                editTextTarget.setText(getAddress(latlng.latitude, latlng.longitude));
                return false;
            }
        });

        //Permite saber cuando se mueve el marcador de posición
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                LatLng pos = marker.getPosition();

                editTextTarget.setText(getAddress(pos.latitude, pos.longitude));
            }
        });

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (REQUEST_CODE_ASK_PERMISSIONS == requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    private void checkPermission() {
        int hasPermissionLocation = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasPermissionLocation != PackageManager.PERMISSION_GRANTED) {
            make(findViewById(R.id.lin_mark_parent), "Si desea ver su posición active el permiso.", LENGTH_LONG)
                    .setAction("Action", null).show();
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS);
        } else {

            mMap.setMyLocationEnabled(true);
        }

    }


    public String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(MarkActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            return addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
            make(findViewById(R.id.lin_mark_parent), e.getMessage(), LENGTH_LONG)
                    .setAction("Action", null).show();

        }
        return "";
    }


    private synchronized void setUpGClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}