package com.richydave.quotes.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import com.richydave.quotes.Constant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import static com.richydave.quotes.Constant.MIN_LOCATION_UPDATE_TIME;


public class LocationUtil {

    private static final int BUILD_VERSION = Build.VERSION.SDK_INT;

    private static final int MASHMALLOW = Build.VERSION_CODES.M;

    private static double latitude;

    private static double longitude;

    private static LocationListener locationListener;

    public static void requestLocationUpdate(Context context, LocationManager locationManager) {

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setCostAllowed(true);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent locationSettingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(locationSettingIntent);
            }
        };
        if (BUILD_VERSION >= MASHMALLOW) {

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET}, 104);
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_LOCATION_UPDATE_TIME, 0, locationListener);
        }
    }

    public static void stopLocationUpdate(LocationManager locationManager) {
        locationManager.removeUpdates(locationListener);
    }

    public static double getLatitude() {
        return latitude;
    }

    public static double getLongitude() {
        return longitude;
    }

    public static class ReverseGeoCodingTask extends AsyncTask<LatLng, Void, String> {

        Context mContext;

        private String place;

        private ReverseGeoCodingTask(Context context) {
            super();
            mContext = context;
        }

        @Override
        protected String doInBackground(LatLng... locations) {
            LatLng location = locations[0];
            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
            List<Address> addresses = new ArrayList<>();
            try {
                addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("IO EXCEPTION", e.getMessage());
            }
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                setPlace(String.format(Constant.ADDRESS_FORMAT,
                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                        address.getLocality(),
                        address.getCountryName()));
            }
            return place;
        }

        private void setPlace(String value) {
            place = value;
        }

    }

    public static String getPlaceFromLocation(Context context, LatLng coordinates) {
        String place = "";
        try {
            place = (new ReverseGeoCodingTask(context)).execute(coordinates).get();
        } catch (ExecutionException | InterruptedException | NullPointerException e) {
            e.printStackTrace();
        }
        return place;
    }
}
