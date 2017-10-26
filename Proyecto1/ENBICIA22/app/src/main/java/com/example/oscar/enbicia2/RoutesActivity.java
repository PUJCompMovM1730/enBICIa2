package com.example.oscar.enbicia2;

import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.clases.Constants;
import com.example.clases.DataParser;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RoutesActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    private FirebaseAuth mAuth;
    public List<Polyline> route;
    private FusedLocationProviderClient mFusedLocationClient;
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private static final int REQUEST_CHECK_SETTINGS = 2;

    private EditText direction;
    private Geocoder mGeocoder;
    private Marker auxMark;
    private LatLng actual;

    public static final double lowerLeftLatitude = 4.466214;
    public static final double lowerLeftLongitude =  -74.225923;
    public static final double upperRightLatitude = 4.825517;
    public static final double upperRigthLongitude = -73.996583;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        FloatingActionButton fmenu = (FloatingActionButton) findViewById(R.id.menu);
        fmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getBaseContext(),MenuActivity.class);
                startActivity(intent);
            }
        });

        direction = (EditText) findViewById(R.id.et_Routes_PuntoPartida);
        mAuth = FirebaseAuth.getInstance();
        mGeocoder = new Geocoder(getBaseContext());
        auxMark=null;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        route = new ArrayList<>();

        direction.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int st, int ct,
                                          int af) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.v("Press: " , "Cualquier tecla");
                if (s.length() < 1 || start >= s.length() || start < 0)
                    return;

                // If it was Enter
                if (s.subSequence(start, start + 1).toString().equalsIgnoreCase("\n")) {
                    Log.v("Press: " , "Enter");
                    // Change text to show without '\n'
                    String s_text = start > 0 ? s.subSequence(0, start).toString() : "";
                    s_text += start < s.length() ? s.subSequence(start + 1, s.length()).toString() : "";
                    direction.setText(s_text);

                    // Move cursor to the end of the line
                    direction.setSelection(s_text.length());

                    searchLocation();
                }
            }
        });
        findViewById(R.id.btnRoutesSearch).setOnClickListener(this);
    }

    private void checkPermission() {
        int hasWriteContactsPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Si desea ver su posición active el permiso.",Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS);

        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS);

        }
        return;
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
        checkPermission();
        mMap = googleMap;

        LatLng bogota = new LatLng(4.65, -74.05);

        mMap.addMarker(new MarkerOptions().position(new LatLng(4.647752, -74.101672)).title("Gran Estación").snippet("Centro comercial").icon(BitmapDescriptorFactory.fromResource(R.drawable.store)));

        establecimientos(4.6269739, -74.0821102, "Green Wheels", "8:00 am a 5:00pm");
        establecimientos(4.7575693, -74.0465737, "La Bicicletería", "9:00 am a 6:00pm");
        establecimientos(4.6255434, -74.1233942, "Ekon", "8:00 am a 8:00pm");
        establecimientos(4.6072001, -74.0915225, "El mono megatienda", "7:00 am a 5:00pm");
        establecimientos(4.597785, -74.0787264, "Bike Suite", "8:00 am a 5:00pm");

        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(bogota));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(Constants.ZOOM_CITY));
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (REQUEST_CODE_ASK_PERMISSIONS == requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                verify();
            }
        }
        else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("V:", "Entró al activity");
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS: {
                Log.i("V:", "Entró al activity2");
                if (resultCode == RESULT_OK) {
                    getLocation();
                } else {
                    Toast.makeText(this, "Sin	acceso a	localizacion,	hardware	deshabilitado!", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void establecimientos(double lat, double lon, String title, String info) {
        LatLng gw = new LatLng(lat, lon);
        mMap.addMarker(new MarkerOptions().position(gw).title(title).snippet(info).icon(BitmapDescriptorFactory.fromResource(R.drawable.store)));
    }

    public void verify() {
        Log.i("V:  ", "Entró al verify");
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(new LocationRequest());
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getLocation();
            }
        });
        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case CommonStatusCodes.RESOLUTION_REQUIRED: //	Location	settings	are	not	satisfied,	but	this	can	be	fixed	by	showing	the	user	a	dialog.
                        try {//	Show	the	dialog	by	calling	startResolutionForResult(),	and	check	the	result	in	onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            Log.i("V:", "Entre al aca");
                            resolvable.startResolutionForResult(RoutesActivity.this, REQUEST_CHECK_SETTINGS);

                        } catch (IntentSender.SendIntentException sendEx) { //	Ignore	the	error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE: //	Location	settings	are	not	satisfied.	No	way	to	fix	the	settings	so	we	won't	show	the	dialog.
                        break;
                }
            }
        });
    }

    @SuppressWarnings("MissingPermission")
    private void getLocation() {
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    actual = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(actual).title("Mi posición").snippet("Hola").icon(BitmapDescriptorFactory.fromResource(R.drawable.guy)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(actual));
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(Constants.ZOOM_BUILDINGS));
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    getLocation();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnRoutesSearch) {
            searchLocation();
        }
    }

    public void searchLocation(){
        String addressString = direction.getText().toString();
        if (!addressString.isEmpty()) {
            Log.i("HOLÁ: " , "Adentro");
            try {
                List<Address> addresses = mGeocoder.getFromLocationName( addressString, 2, lowerLeftLatitude, lowerLeftLongitude, upperRightLatitude, upperRigthLongitude);
                if (addresses != null && !addresses.isEmpty()) {
                    Address addressResult = addresses.get(0);
                    LatLng position = new LatLng(addressResult.getLatitude(), addressResult.getLongitude());
                    if (mMap != null) {
                        if(auxMark!=null)
                            auxMark.remove();
                        if(!route.isEmpty()){
                            for(Polyline aux : route){
                                aux.remove();
                            }
                        }
                        auxMark=mMap.addMarker(new MarkerOptions().position(position).title("Resultado").icon(BitmapDescriptorFactory.fromResource(R.drawable.location)));
                        Location one= new Location("");
                        one.setLatitude(actual.latitude);
                        one.setLongitude(actual.longitude);
                        Location two= new Location("");
                        two.setLatitude(position.latitude);
                        two.setLongitude(position.longitude);
                        double auxil= (one.distanceTo(two)/1000.0);
                        String url = getUrl(actual, position);
                        Log.d("URLJSON", url.toString());
                        FetchUrl FetchUrl = new FetchUrl();
                        FetchUrl.execute(url);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(actual));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(Constants.ZOOM_CITY));
                        Toast.makeText(this, "La distancia entre los puntos es: "+String.format("%.2g%n",auxil)+" km", Toast.LENGTH_SHORT ).show();

                    }
                } else {
                    Toast.makeText(RoutesActivity.this, "Dirección no encontrada", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(RoutesActivity.this, "La dirección esta vacía", Toast.LENGTH_SHORT).show();
        }
    }

    private String getUrl(LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String parameters = str_origin + "&" + str_dest;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
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
                Log.d("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
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
                Log.d("onPostExecute","onPostExecute lineoptions decoded");

            }
            if(lineOptions != null) {
                route.add(mMap.addPolyline(lineOptions));
            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
        }
    }
}
