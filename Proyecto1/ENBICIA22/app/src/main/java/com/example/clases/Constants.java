package com.example.clases;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

/**
 * Created by juanpablorn30 on 26/10/17.
 */

public final class Constants {

    // [BEGIN declare_enBICIa2]
    public static EnBiciaa2 enBICIa2;
    // [END declare_enBICIa2]

    /**
     *  Values of zoom in google maps api
     */
    public static final int ZOOM_WORLD = 1;
    public static final int ZOOM_CONTINENT = 5;
    public static final int ZOOM_CITY = 10;
    public static final int ZOOM_STREETS = 15;
    public static final int ZOOM_BUILDINGS = 20;
    final static int ZOOM_MAX = 21;
    final static int GLOBE_WIDTH = 256;

    public static int getBoundsZoomLevel(LatLngBounds bounds,
                                         int width, int height) {
        LatLng northeast = bounds.northeast;
        LatLng southwest = bounds.southwest;
        double latFraction = (latRad(northeast.latitude) - latRad(southwest.latitude)) / Math.PI;
        double lngDiff = northeast.longitude - southwest.longitude;
        double lngFraction = ((lngDiff < 0) ? (lngDiff + 360) : lngDiff) / 360;
        double latZoom = zoom(height, GLOBE_WIDTH, latFraction);
        double lngZoom = zoom(width, GLOBE_WIDTH, lngFraction);
        double zoom = Math.min(Math.min(latZoom, lngZoom),ZOOM_MAX) - 1.0;
        if(zoom < 0.0) zoom = 0.0;
        return (int)(zoom);
    }
    private static double latRad(double lat) {
        double sin = Math.sin(lat * Math.PI / 180);
        double radX2 = Math.log((1 + sin) / (1 - sin)) / 2;
        return Math.max(Math.min(radX2, Math.PI), -Math.PI) / 2;
    }
    private static double zoom(double mapPx, double worldPx, double fraction) {
        final double LN2 = .693147180559945309417;
        return (Math.log(mapPx / worldPx / fraction) / LN2);
    }

    // [BEGIN declare_bogota_bounds]
    public static final double lowerLeftLatitude = 4.466214;
    public static final double lowerLeftLongitude = -74.225923;
    public static final double upperRightLatitude = 4.825517;
    public static final double upperRigthLongitude = -73.996583;
    // [END declare_bogota_bounds]

    //[BEGIN declare_estados_recorrido]
    public static final String FINALIZADO = "finalizado";
    public static final String ACTIVO = "activo";
    //[END declare_estados_recorrido]

    // [BEGIN path_elements_firebase]
    public static final String PATH_FIREBASE = "https://enbicia2-ccad2.firebaseio.com/";
    public static final String PATH_USUARIOS="usuarios/";
    public static final String PATH_SITIO_INTERES="sitio_interes/";
    public static final String PATH_MESSAGES="messages/";
    // [END path_elements_firebase]

    // [BEGIN declare_tag]
    public static final String TAG_CLASS = "enBICIa2";
    // [END declare_tag]

    public static final Long TIME_LIMIT = 300000L;
    public static final Long RADIUS_CIRCLE = 200L;
    public static final Long RADIUS_COMUN = 400L;

    public static int mNotificationId = 1;
}
