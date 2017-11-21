package com.example.oscar.enbicia2;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.clases.Constants;
import com.example.clases.DataParser;
import com.example.clases.EnBiciaa2;
import com.example.clases.Punto;
import com.example.clases.RecorridoGrupal;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.support.design.widget.Snackbar.LENGTH_LONG;
import static android.support.design.widget.Snackbar.make;

public class CreateSRouteActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private AutoCompleteTextView hora, fecha, nombre,frecuencia;
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private String TAG="CSROUTE";

    public List<Polyline> route;
    private int indx_polyLine;
    private Marker marker_target;
    private Marker marker_source;
    private Marker marker_current;
    private EditText editTextTarget;
    private EditText editTextSource;
    private Location location;
    private LatLng source;
    private LatLng target;
    private StorageReference mStorageRef, imageRef;
    private int dia,mes,anio,horas,min;
    private DatabaseReference mCurrentUserReference;

    private PlaceAutocompleteFragment autocompleteFragmentSource;
    private PlaceAutocompleteFragment autocompleteFragmentTarget;

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private static final int ListAmiguitos= 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_sroute);


        mCurrentUserReference = FirebaseDatabase.getInstance().getReference().child("usuarios").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mStorageRef = FirebaseStorage.getInstance().getReference();

        setUpGClient();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_CSRoute_map);
        mapFragment.getMapAsync(this);

        nombre=findViewById(R.id.et_CSRoute_NombreUbicacion);
        frecuencia=findViewById(R.id.et_CSRoute_Frecuencia);
        // initiate the date picker and a button
        fecha = findViewById(R.id.et_CSRoute_fecha);
        hora =  findViewById(R.id.et_CSRoute_hora);

        // perform click event on edit text
       fecha.setOnClickListener(this);
       hora.setOnClickListener(this);

        marker_target = marker_source = marker_current = null;
        route = new ArrayList<>();

        findViewById(R.id.bt_CSRoute_adicionar).setOnClickListener(this);

        autocompleteFragmentSource = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_CSRoute_start);
        ViewGroup viewGroupSource = (ViewGroup) autocompleteFragmentSource.getView();
        editTextSource = viewGroupSource.findViewById(R.id.place_autocomplete_search_input);
        editTextSource.setTextSize(getResources().getDimension(R.dimen.search_edit_size));
        editTextSource.setHint(getResources().getString(R.string.location_source));
        ImageButton imageClearButtonSource = viewGroupSource.findViewById(R.id.place_autocomplete_clear_button);
        imageClearButtonSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                erasePath();
                ViewGroup viewGroupSource = (ViewGroup) autocompleteFragmentSource.getView();
                EditText editTextSource = viewGroupSource.findViewById(R.id.place_autocomplete_search_input);
                editTextSource.getText().clear();
                ImageButton imageClearButtonSource = viewGroupSource.findViewById(R.id.place_autocomplete_clear_button);
                imageClearButtonSource.setVisibility(View.GONE);
                Log.d(TAG, marker_source.getTitle());
                if(source != null) marker_source.remove();
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                if(target != null) builder.include(target);
                source = null;
                LatLngBounds bounds = builder.build();
                int width = findViewById(R.id.map_CSRoute_map).getWidth();
                int height = findViewById(R.id.map_CSRoute_map).getHeight();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), Constants.getBoundsZoomLevel(bounds, width, height)));
            }
        });
        autocompleteFragmentSource.getView().setBackgroundResource(R.drawable.autocomplete_fragment_background);

        autocompleteFragmentSource.setBoundsBias(new LatLngBounds(
                new LatLng(Constants.lowerLeftLatitude, Constants.lowerLeftLongitude),
                new LatLng(Constants.upperRightLatitude, Constants.upperRigthLongitude)));

        autocompleteFragmentSource.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                source = place.getLatLng();
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(source);
                if(marker_source != null)
                    marker_source.remove();
                marker_source = mMap.addMarker(new MarkerOptions().position(source).title("Inicio").icon(BitmapDescriptorFactory.fromResource(R.drawable.route_start)));
                if(target != null)
                    builder.include(target);
                LatLngBounds bounds = builder.build();
                int width = findViewById(R.id.map_CSRoute_map).getWidth();
                int height = findViewById(R.id.map_CSRoute_map).getHeight();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), Constants.getBoundsZoomLevel(bounds, width, height)));
                if(target != null && source != null) searchLocation(source, target);
            }

            @Override
            public void onError(Status status) {
                Toast.makeText(getBaseContext(), "Error utilizando busqueda", Toast.LENGTH_SHORT).show();
                Log.i("PLACE SELECT", "An error occurred: " + status);
            }
        });
        autocompleteFragmentTarget = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_CSRoute_finish);
        ViewGroup viewGroupTarget = (ViewGroup) autocompleteFragmentTarget.getView();
        editTextTarget = viewGroupTarget.findViewById(R.id.place_autocomplete_search_input);
        editTextTarget.setTextSize(getResources().getDimension(R.dimen.search_edit_size));
        editTextTarget.setHint(getResources().getString(R.string.location_target));
        ImageButton imageClearButtonTarget = viewGroupTarget.findViewById(R.id.place_autocomplete_clear_button);
        imageClearButtonTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                erasePath();
                ViewGroup viewGroupTarget = (ViewGroup) autocompleteFragmentTarget.getView();
                EditText editTextTarget = viewGroupTarget.findViewById(R.id.place_autocomplete_search_input);
                editTextTarget.getText().clear();
                ImageButton imageClearButtonTarget = viewGroupTarget.findViewById(R.id.place_autocomplete_clear_button);
                imageClearButtonTarget.setVisibility(View.GONE);
                if(target != null) marker_target.remove();
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                if(source != null) builder.include(source);
                target = null;
                LatLngBounds bounds = builder.build();
                int width = findViewById(R.id.map_CSRoute_map).getWidth();
                int height = findViewById(R.id.map_CSRoute_map).getHeight();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), Constants.getBoundsZoomLevel(bounds, width, height)));
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
                marker_target = mMap.addMarker(new MarkerOptions().position(target).title("Destino").icon(BitmapDescriptorFactory.fromResource(R.drawable.route_finish)));
                if(source != null)
                    builder.include(source);
                LatLngBounds bounds = builder.build();
                int width = findViewById(R.id.map_CSRoute_map).getWidth();
                int height = findViewById(R.id.map_CSRoute_map).getHeight();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), Constants.getBoundsZoomLevel(bounds, width, height)));
                if(target != null && source != null) searchLocation(source, target);
            }

            @Override
            public void onError(Status status) {
                Toast.makeText(getBaseContext(), "Error utilizando busqueda", Toast.LENGTH_SHORT).show();
                Log.i("PLACE SELECT", "An error occurred: " + status);
            }
        });



    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if( i == R.id.et_CSRoute_fecha) {
            // calender class's instance and get current date , month and year from calender
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR); // current year
            int mMonth = c.get(Calendar.MONTH); // current month
            int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
            // date picker dialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(CreateSRouteActivity.this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            // set day of month , month and year value in the edit text
                            anio=i;
                            mes=i1;
                            dia=i1;
                            fecha.setText(i2 + "/"+ (i1+1) + "/" + i);
                        }


                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        if(i== R.id.et_CSRoute_hora){
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(CreateSRouteActivity.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    if(selectedMinute<10)
                        hora.setText(selectedHour + ":0" + selectedMinute);
                    else
                        hora.setText(selectedHour + ":" + selectedMinute);
                    horas=selectedHour;
                    min=selectedMinute;
                }
            }, hour, minute, true);//Yes 24 hour time
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        }
        if(i==R.id.bt_CSRoute_adicionar){
            if(nombre.getText().toString().isEmpty()) {
                nombre.setError("Falta: Nombre del recorrido");
                return;
            }
            if(fecha.getText().toString().isEmpty()) {
                fecha.setError("Falta: Fecha");
                return;
            }
            if(hora.getText().toString().isEmpty()) {
                hora.setError("Falta: Hora");
                return;
            }
            if(frecuencia.getText().toString().isEmpty()) {
                frecuencia.setError("Falta: Frecuencia");
                return;
            }
            Punto pi= new Punto(null,null,marker_source.getPosition().latitude,marker_source.getPosition().longitude,null);
            Punto pf= new Punto(null,null,marker_target.getPosition().latitude,marker_target.getPosition().longitude,null);

            DateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date date = null;
            String date_aux = fecha.getText().toString() + " " + hora.getText().toString();
            Log.d(TAG, date_aux);
            try {
                date = sourceFormat.parse(date_aux);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //Date date= new Date(anio,mes,dia,horas,min);

            RecorridoGrupal rg= new RecorridoGrupal(null, pi,pf, mCurrentUserReference.getKey(),
                    Integer.parseInt(frecuencia.getText().toString()),null, nombre.getText().toString(),date);

            rg.setTipo("usuario");
            String key=Constants.enBICIa2.agregarRutaFireBase(rg);
            takeSnapshot(key);
            Snackbar.make(view, "Ruta creada exitosamente", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            finish();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_general_menu_drawer, menu);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i("Holá:",":O");
        mMap = googleMap;
        LatLng bogota = new LatLng(4.65, -74.05);


        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(bogota));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(Constants.ZOOM_CITY));


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
        }else if(itemClicked == R.id.it_settings_menu){
            Intent intent = new Intent(getBaseContext(), EditProfileActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    private void erasePath(){
        if(!route.isEmpty()){
            for (Polyline aux : route){
                aux.remove();
            }
        }
    }

    public String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(CreateSRouteActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            return addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
            make(findViewById(R.id.lin_mark_parent),  e.getMessage(), LENGTH_LONG)
                    .setAction("Action", null).show();

        }
        return "";
    }

    private String getUrl(LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String parameters = str_origin + "&" + str_dest;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
    }


    private void save(Bitmap bitmap, String key){
        StorageReference storageRef = mStorageRef.child("ruta-programada/"+key+".jpg");
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






    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

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
