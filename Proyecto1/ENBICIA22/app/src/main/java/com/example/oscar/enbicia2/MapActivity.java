package com.example.oscar.enbicia2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.clases.Constants;
import com.example.clases.DataParser;
import com.example.clases.SitioInteres;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.PolyUtil;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private static final int REQUEST_CHECK_SETTINGS_GPS = 2;

    private Bitmap image;
    private String TAG = "MapActivity";
    public List<Polyline> route;
    private List<Marker> marcadores;
    private List<Circle> zonas_peligro;
    private List<SitioInteres> puntos_peligro;
    private Marker marker_target;
    private Marker marker_source;
    private Location location;
    private LatLng source;
    private LatLng target;

    private DatabaseReference mSitioInteresReference;
    private ValueEventListener mSitioInteresListener;

    private GoogleApiClient googleApiClient;
    private GoogleMap mMap;
    private ShareDialog shareDialog;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Bundle bundle = getIntent().getParcelableExtra("bundle");
        source = bundle.getParcelable("from_position");
        target = bundle.getParcelable("to_position");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_start_route);
        mapFragment.getMapAsync(this);

        marcadores = new ArrayList<>();
        puntos_peligro = new ArrayList<>();
        zonas_peligro = new ArrayList<>();
        mSitioInteresReference = FirebaseDatabase.getInstance().getReference().child("sitio_interes");
        route = new ArrayList<>();
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        findViewById(R.id.btnRoutesFinish).setOnClickListener(this);
        findViewById(R.id.btnClima).setOnClickListener(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng bogota = new LatLng(4.65, -74.05);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(bogota));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(Constants.ZOOM_CITY));

        ValueEventListener sitioInteresListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                puntos_peligro.clear();
                marcadores.clear();
                for(DataSnapshot x : dataSnapshot.getChildren()){
                    SitioInteres curr_sitioInteres = x.getValue(SitioInteres.class);
                    LatLng posAux = new LatLng(curr_sitioInteres.getLatitud(), curr_sitioInteres.getLongitud());
                    if(curr_sitioInteres.getTipo().equals("Ladron")) puntos_peligro.add(curr_sitioInteres);
                    else marcadores.add(mMap.addMarker(new MarkerOptions().position(posAux).title(curr_sitioInteres.getNombre()).icon(BitmapDescriptorFactory.fromResource(selectImage(curr_sitioInteres.getTipo())))));
                    actualizarPuntosPeligro();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mSitioInteresReference.addValueEventListener(sitioInteresListener);
        mSitioInteresListener = sitioInteresListener;
        marker_source = mMap.addMarker(new MarkerOptions().position(source).title("Mi posición").snippet("Hola").icon(BitmapDescriptorFactory.fromResource(R.drawable.route_ciclist)));
        marker_target = mMap.addMarker(new MarkerOptions().position(target).title("Destino").icon(BitmapDescriptorFactory.fromResource(R.drawable.route_finish)));
        takeSnapshot("");
        searchLocation(source, target);
    }

    private void checkPermission() {
        int hasPermissionLocation = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasPermissionLocation != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Si desea ver su posición active el permiso.", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            getLocation();
        }
        return;
    }

    private void actualizarPuntosPeligro(){
        Date now = new Date();
        boolean[] visited = new boolean[puntos_peligro.size()];
        for(int i = 0 ;  i< visited.length ; i++) visited[i] = false;
        for(int i = 0 ; i < puntos_peligro.size() ; i++){
            if(puntos_peligro.get(i).getFecha() + Constants.TIME_LIMIT < now.getTime())
                visited[i] = true;
        }
        for(int i = 0 ; i < puntos_peligro.size() ; i++){
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            if(!visited[i]){
                visited[i] = true;
                SitioInteres inicio = puntos_peligro.get(i);
                Location one = new Location("");
                one.setLatitude(inicio.getLatitud());
                one.setLongitude(inicio.getLongitud());
                builder.include(new LatLng(inicio.getLatitud(), inicio.getLongitud()));
                for(int j = i+1 ; j < puntos_peligro.size() ; j++){
                    SitioInteres fin = puntos_peligro.get(j);
                    if(!visited[j]){
                        Location two = new Location("");
                        two.setLatitude(fin.getLatitud());
                        two.setLongitude(fin.getLongitud());
                        double distance = (one.distanceTo(two));
                        if(distance < Constants.RADIUS_COMUN){
                            visited[j] = true;
                            builder.include(new LatLng(fin.getLatitud(), fin.getLongitud()));
                        }
                    }
                }
                LatLngBounds bounds = builder.build();
                marcadores.add(mMap.addMarker(new MarkerOptions().position(bounds.getCenter()).title("Peligro").icon(BitmapDescriptorFactory.fromResource(selectImage("Ladron")))));
                zonas_peligro.add(mMap.addCircle(new CircleOptions().center(bounds.getCenter()).radius(Constants.RADIUS_CIRCLE).strokeColor(ResourcesCompat.getColor(getResources(), R.color.circle_background, null)).fillColor(ResourcesCompat.getColor(getResources(), R.color.circle_background, null))));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (REQUEST_CODE_ASK_PERMISSIONS == requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS_GPS: {
                if (resultCode == RESULT_OK) {
                    getLocation();
                } else {
                    Toast.makeText(this, "Sin	acceso a	localizacion,	hardware	deshabilitado!", Toast.LENGTH_LONG).show();
                    finish();
                }
                return;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Entre al onResume");
    }

    private void getLocation() {
        // https://developers.google.com/android/reference/com/google/android/gms/location/LocationRequest
        Log.d(TAG, "Entre al get location");
        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {
                Log.d(TAG, "Google Api Connected");
                int permissionLocation = ContextCompat.checkSelfPermission(MapActivity.this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setInterval(2000);
                    locationRequest.setFastestInterval(1000);
                    locationRequest.setSmallestDisplacement(0.25F);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi
                            .requestLocationUpdates(googleApiClient, locationRequest, this);
                    PendingResult<LocationSettingsResult> result =
                            LocationServices.SettingsApi
                                    .checkLocationSettings(googleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

                        @Override
                        public void onResult(LocationSettingsResult result) {
                            final Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    int permissionLocation = ContextCompat
                                            .checkSelfPermission(MapActivity.this,
                                                    android.Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        Log.d(TAG, "Entre al location");
                                        location = LocationServices.FusedLocationApi
                                                .getLastLocation(googleApiClient);
                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    try {
                                        status.startResolutionForResult(MapActivity.this,
                                                REQUEST_CHECK_SETTINGS_GPS);
                                    } catch (IntentSender.SendIntentException e) {
                                        // Ignore the error.
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    Toast.makeText(getBaseContext(), "No hay acceso a la localización", Toast.LENGTH_SHORT).show();
                                    finish();
                                    break;
                            }
                        }
                    });
                }
            }
        }
    }

    private void takeSnapshot(final String key) {
        if (mMap == null) {
            return ;
        }
        final GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                // Callback is called from the main thread, so we can modify the ImageView safely.
                image = snapshot;

            }
        };
        mMap.snapshot(callback);
    }

    private void dialog() {
        final CharSequence[] options = {"Share","Terminar viaje", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
        builder.setTitle("Publicar");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (options[i].equals("Share")) {
                    SharePhoto photo = new SharePhoto.Builder()
                            .setBitmap(image)
                            .build();
                    SharePhotoContent content = new SharePhotoContent.Builder()
                            .addPhoto(photo)
                            .build();
                    /*ShareButton shareButton = (ShareButton)findViewById(R.id.fb_share_button);
                    shareButton.setShareContent(content);*/
                } else if (options[i].equals("Cancel")) {
                    dialogInterface.dismiss();
                }else{
                    Intent intent = new Intent(getBaseContext(), MenuActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.btnRoutesFinish){
            Log.d(TAG, "Tome foto");
            dialog();
            /*Intent intent = new Intent(getBaseContext(), MenuActivity.class);
            startActivity(intent);
            finish();*/
        }else if(id == R.id.btnClima){
            Intent intent = new Intent(getBaseContext(), ClimaActivity.class);
            intent.putExtra("latitud", String.valueOf(source.latitude));
            intent.putExtra("longitud", String.valueOf(source.longitude));
            startActivity(intent);
        }
    }

    public void startRoute(){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(source);
        LatLngBounds bounds = builder.build();
        int width = findViewById(R.id.map_start_route).getWidth();
        int height = findViewById(R.id.map_start_route).getHeight();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), Constants.getBoundsZoomLevel(bounds, width, height)));
    }

    private void erasePath(){
        if(!route.isEmpty()){
            for (Polyline aux : route){
                aux.remove();
            }
        }
    }

    public void searchLocation(LatLng source, LatLng target) {
        erasePath();
        Location one = new Location("");
        one.setLatitude(source.latitude);
        one.setLongitude(source.longitude);
        Location two = new Location("");
        two.setLatitude(target.latitude);
        two.setLongitude(target.longitude);
        double auxil = (one.distanceTo(two) / 1000.0);
        String urlSource_Target = getUrl(source, target);
        Log.d("URLJSON", urlSource_Target.toString());
        FetchUrl FetchUrlSource_Target = new FetchUrl();
        FetchUrlSource_Target.execute(urlSource_Target);
        Toast.makeText(this, "La distancia entre los puntos es: " + String.format("%.2g%n", auxil) + " km", Toast.LENGTH_SHORT).show();
        startRoute();
    }

    private int selectImage(String name){
        if(name.equals("Ladron")) return R.drawable.peque_ladron;
        if(name.equals("Alquiler")) return R.drawable.peque_alquilar;
        if(name.equals("Tienda")) return R.drawable.peque_compra;
        if(name.equals("Taller")) return R.drawable.peque_arreglar;
        return -1;
    }

    private String getUrl(LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String parameters = str_origin + "&" + str_dest;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
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
        checkPermission();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        if (location != null) {
            Log.d(TAG, "Entre al onLocationChanged");
            source = new LatLng(location.getLatitude(), location.getLongitude());
            if(marker_target == null){
                if (marker_source != null) marker_source.remove();
                marker_source = mMap.addMarker(new MarkerOptions().position(source).title("Mi posición").snippet("Hola").icon(BitmapDescriptorFactory.fromResource(R.drawable.route_ciclist)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(source));
                mMap.moveCamera(CameraUpdateFactory.zoomTo(Constants.ZOOM_BUILDINGS));
            }
            /*if(!route.isEmpty()){
                if(!recalculate()){
                    Log.d(TAG, "Entre a recalcular");
                    marker_source.remove();
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(source);
                    builder.include(target);
                    LatLngBounds bounds = builder.build();
                    int width = findViewById(R.id.map).getWidth();
                    int height = findViewById(R.id.map).getHeight();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), Constants.getBoundsZoomLevel(bounds, width, height)));
                    marker_source = mMap.addMarker(new MarkerOptions().position(source).title("Inicio").icon(BitmapDescriptorFactory.fromResource(R.drawable.route_start)));
                    searchLocation(source, target);
                }
            }*/
        }
    }

    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);

        }

        private String downloadUrl(String strUrl) throws IOException {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(strUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                iStream = urlConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
                StringBuffer sb = new StringBuffer();
                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                data = sb.toString();
                Log.d("downloadUrl", data.toString());
                br.close();

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            } finally {
                iStream.close();
                urlConnection.disconnect();
            }
            return data;
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                routes = parser.parse(jObject);
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask", routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);
                Log.d("onPostExecute", "onPostExecute lineoptions decoded");

            }
            if (lineOptions != null) {
                route.add(mMap.addPolyline(lineOptions));
            } else {
                Log.d("onPostExecute", "without Polylines drawn");
            }
        }
    }
}
