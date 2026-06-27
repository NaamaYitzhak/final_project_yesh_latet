package me.naama.yeshlatet;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class LocationMapHelper {

    public static final int LOCATION_PERMISSION_REQUEST = 500;

    public static void setupMap(Activity activity, WebView mapWebView) {
        WebSettings settings = mapWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    LOCATION_PERMISSION_REQUEST
            );

            loadDefaultMap(mapWebView);
            return;
        }

        loadDeviceLocation(activity, mapWebView);
    }

    public static void loadDeviceLocation(Activity activity, WebView mapWebView) {
        try {
            LocationManager locationManager =
                    (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

            Location location = null;

            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (location == null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            }

            if (location != null) {
                loadMapAt(mapWebView, location.getLatitude(), location.getLongitude());
            } else {
                loadDefaultMap(mapWebView);
            }

        } catch (Exception e) {
            loadDefaultMap(mapWebView);
        }
    }

    private static void loadMapAt(WebView mapWebView, double lat, double lon) {
        double delta = 0.01;

        double left = lon - delta;
        double right = lon + delta;
        double top = lat + delta;
        double bottom = lat - delta;

        String url =
                "https://www.openstreetmap.org/export/embed.html" +
                        "?bbox=" + left + "%2C" + bottom + "%2C" + right + "%2C" + top +
                        "&layer=mapnik" +
                        "&marker=" + lat + "%2C" + lon;

        mapWebView.loadUrl(url);
    }

    private static void loadDefaultMap(WebView mapWebView) {
        // Default location: Israel center area
        loadMapAt(mapWebView, 31.7683, 35.2137);
    }
}