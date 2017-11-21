package com.example.oscar.enbicia2;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.clases.Constants;
import com.example.clases.DataParser;
import com.example.clases.Grupo;
import com.example.clases.Punto;
import com.example.clases.RecorridoGrupal;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewTripActivity extends AppCompatActivity implements OnMapReadyCallback {

    public List<Polyline> route;
    private DatabaseReference databaseReference;
    private StorageReference mStorageRef;
    Button newTrip;
    EditText tripName, tripDescription, tripOrigin, tripDestination, tripDate, tripTime;
    Punto pInicio, pFin;
    String dateSel, timeSel;
    TextInputLayout tinTime, tinDate;
    GoogleMap mMap;
    Marker mOrigen, mDestino;
    LatLng lOrigen, lDestino;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_trip);
        route = new ArrayList<>();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_newtrip_map);
        mapFragment.getMapAsync(this);
        newTrip = (Button)findViewById(R.id.btn_trip_guardar);
        tripName = (EditText) findViewById(R.id.edit_trip_name);
        tripDescription = (EditText) findViewById(R.id.edit_trip_description);
        tripOrigin = (EditText) findViewById(R.id.edit_trip_origin);
        tripDestination = (EditText) findViewById(R.id.edit_trip_destination);
        tripDate = (EditText) findViewById(R.id.edit_trip_date);
        tripTime = (EditText) findViewById(R.id.edit_trip_time);
        tinDate = (TextInputLayout) findViewById(R.id.tin_trip_date);
        tinTime = (TextInputLayout) findViewById(R.id.tin_trip_time);
        newTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTrip();
            }
        });
        databaseReference = FirebaseDatabase.getInstance().getReference(Constants.PATH_GROUPS);
        tinTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker();
            }
        });
        tinDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });
        tripTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker();
            }
        });
        tripDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });
        tripOrigin.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                lOrigen = buscarDireccion(tripOrigin.getText().toString());
                if (lOrigen != null){
                    pInicio = new Punto(null, null, lOrigen.latitude, lOrigen.longitude, null);
                }
                if(mOrigen != null)
                    mOrigen.remove();
                mOrigen = mMap.addMarker(new MarkerOptions().position(lOrigen).title("Inicio").icon(BitmapDescriptorFactory.fromResource(R.drawable.route_start)));
                searchLocation(lOrigen, lDestino);
                handled = true;
            }
            return handled;
            }
        });
        tripDestination.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                lDestino = buscarDireccion(tripDestination.getText().toString());
                if (lDestino != null){
                    pFin = new Punto(null, null, lDestino.latitude, lDestino.longitude, null);
                }
                if(mDestino != null)
                    mDestino.remove();
                mDestino = mMap.addMarker(new MarkerOptions().position(lDestino).title("Fin").icon(BitmapDescriptorFactory.fromResource(R.drawable.route_start)));
                searchLocation(lOrigen, lDestino);
                handled = true;
            }
            return handled;
            }
        });
    }

    void showTimePicker(){
        Calendar mcurrentTime = Calendar.getInstance();
        int hora = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minuto = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog timePicker;
        timePicker = new TimePickerDialog(NewTripActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                timeSel = hour + ":" + minute;
                tripTime.setText(timeSel);
            }
        }, hora, minuto, true);
        timePicker.setTitle("Seleccione la hora");
        timePicker.show();
    }

    void showDatePicker(){
        Calendar mcurrentTime = Calendar.getInstance();
        int anio = mcurrentTime.get(Calendar.YEAR);
        int mes = mcurrentTime.get(Calendar.MONTH);
        int dia = mcurrentTime.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePicker;
        datePicker = new DatePickerDialog(NewTripActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                dateSel = day + "/" + (month+1) + "/" + year;
                tripDate.setText(dateSel);
            }
        }, anio, mes, dia);
        datePicker.setTitle("Seleccione el dia");
        datePicker.show();
    }

    LatLng buscarDireccion(String direccion){
        Geocoder mGeocoder = new Geocoder(getBaseContext());
        if (!direccion.isEmpty()) {
            try {
                List<Address> addresses = mGeocoder.getFromLocationName(direccion, 2,
                        Constants.lowerLeftLatitude, Constants.lowerLeftLongitude, Constants.upperRightLatitude, Constants.upperRigthLongitude);
                if (addresses != null && !addresses.isEmpty()) {
                    Address addressResult = addresses.get(0);
                    LatLng posBuscada = new LatLng(addressResult.getLatitude(), addressResult.getLongitude());
                    Toast.makeText(NewTripActivity.this, "Dirección aceptada", Toast.LENGTH_SHORT).show();
                    return posBuscada;
                } else {
                    Toast.makeText(NewTripActivity.this, "Dirección no encontrada", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    void addTrip(){
        try {
            verificarDatos(tripName);
            verificarDatos(tripOrigin);
            verificarDatos(tripDestination);
            verificarDatos(tripDate);
            verificarDatos(tripTime);
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            SimpleDateFormat formater = new SimpleDateFormat("d/M/yyyy H:m");
            Date dateTrip = (Date)formater.parse(dateSel + " " + timeSel);
            Grupo group = new Grupo(tripName.getText().toString(), tripDescription.getText().toString(), user.getUid());
            Log.d("ALGO ", String.valueOf(dateTrip.getTime()));
            group.agregarRecorrido(pInicio, pFin, dateTrip);
            String key = FirebaseDatabase.getInstance().getReference().push().getKey();
            group.setgId(key);
            databaseReference = FirebaseDatabase.getInstance().getReference(Constants.PATH_GROUPS + key);
            databaseReference.setValue(group);
            takeSnapshot(key);
            group.agregarParticipante(key, user.getUid());
            Toast.makeText(getBaseContext(), "Grupo creado", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(NewTripActivity.this, GroupActivity.class);
            startActivity(intent);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng bogota = new LatLng(4.65, -74.05);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(bogota));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(Constants.ZOOM_CITY));
    }

    public void searchLocation(LatLng source, LatLng target) {
        erasePath();
        if (source != null && target != null) {
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

    boolean verificarDatos(EditText field){
        if (field.getText().toString().isEmpty()){
            field.setError("Campo requerido");
            return false;
        }
        return true;
    }

    private void erasePath(){
        if(!route.isEmpty()){
            for (Polyline aux : route){
                aux.remove();
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
                save(snapshot, key);
            }
        };
        mMap.snapshot(callback);
    }

    private void save(Bitmap bitmap, String key){
        StorageReference storageRef = mStorageRef.child("grupo/"+key+".jpg");
        // Get the data from an ImageView as bytes

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
            }
        });
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
