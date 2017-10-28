package com.example.clases;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * Created by juanpablorn30 on 26/10/17.
 */

public final class Constants {

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

    /**
     *  Values of restrict search in Bogota D.C
     */
    public static final double lowerLeftLatitude = 4.466214;
    public static final double lowerLeftLongitude = -74.225923;
    public static final double upperRightLatitude = 4.825517;
    public static final double upperRigthLongitude = -73.996583;

    /**
     * Values of state Recorrido
     */
    public static final String FINALIZADO = "finalizado";
    public static final String ACTIVO = "activo";

}
