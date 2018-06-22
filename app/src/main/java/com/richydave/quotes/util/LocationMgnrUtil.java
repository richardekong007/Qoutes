package com.richydave.quotes.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;


public class LocationMgnrUtil {

    private static Location lastKnownLocation;

    private static Location location;

    private static final int LAPSE_TIME = 60 * 1000;


    public static void init(Context context, LocationManager locationManager) {
        if (locationManager == null) {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }

        LocationListener listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (isPreferedLocation(location, lastKnownLocation)) {
                    setLocation(location);
                } else {
                    setLocation(lastKnownLocation);
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };
        requestLocationUpdates(context, locationManager, listener);
    }

    private static void requestLocationUpdates(Context context, LocationManager locationManager, LocationListener locationListener) {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            String locationProvider = LocationManager.NETWORK_PROVIDER;
            lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
            locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);
        }
    }

    private static boolean isPreferedLocation(Location newLocation, Location lastKnownLocation) {

        if (lastKnownLocation == null) {
            return true;
        }
        long timeDifference = newLocation.getTime() - lastKnownLocation.getTime();
        boolean isSignificantlyNewer = timeDifference > LAPSE_TIME;
        boolean isSignificantlyOlder = timeDifference < -LAPSE_TIME;
        boolean isNewer = timeDifference > 0;

        if (isSignificantlyNewer) {
            return true;
        } else if (isSignificantlyOlder) {
            return false;
        }

        int accuracyDiff = (int) (newLocation.getAccuracy() - lastKnownLocation.getAccuracy());
        boolean isLessAccurate = accuracyDiff > 0;
        boolean isMoreAccurate = accuracyDiff < 0;
        boolean isSignificantlyLessAccurate = accuracyDiff > 200;
        boolean areFromSameProvider = areFromSameProvider(newLocation.getProvider(), lastKnownLocation.getProvider());

        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && areFromSameProvider) {
            return true;
        }

        return false;
    }

    private static boolean areFromSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        } else {
            return provider1.equals(provider2);
        }
    }

    private static void stopLocationUpdate(LocationManager locationManager, LocationListener listener) {
        locationManager.removeUpdates(listener);
    }

    private static void setLocation(Location locationValue) {
        location = locationValue;
    }

    public static Location getLocation() {
        return location;
    }
}