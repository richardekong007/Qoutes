package com.richydave.qoutes.fragments;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.richydave.qoutes.Constant;
import com.richydave.qoutes.R;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import butterknife.BindView;
import butterknife.ButterKnife;


public class BirthPlaceFragment extends Fragment implements OnMapReadyCallback {

    private static final int ZOOM_LEVEL = 15;

    private static final int BEARING = 0;

    private static final int TILT_ANGLE = 45;

    private SupportMapFragment mapFragment;

    private Address address;

    private LatLng coordinates;

    @BindView(R.id.progress_indicator)
    ProgressBar progressIndicator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        View view = inflater.inflate(R.layout.fragment_google_map, container, false);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(getActivity());

        switch (status) {
            case ConnectionResult.SERVICE_MISSING:
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
            case ConnectionResult.SERVICE_DISABLED:
                googleApiAvailability.showErrorNotification(getActivity(), status);
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        setLoading(true);
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(receiveLocationName(), 1);
            if (!addresses.isEmpty() || addresses.size() > 0) {
                double lat = addresses.get(0).getLatitude();
                double lng = addresses.get(0).getLongitude();
                setLoading(false);
                coordinates = new LatLng(lat, lng);
                googleMap.addMarker(new MarkerOptions().position(coordinates).title(receiveLocationName()));
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                CameraPosition cameraPosition = CameraPosition.builder()
                        .target(coordinates)
                        .zoom(ZOOM_LEVEL)
                        .bearing(BEARING)
                        .tilt(TILT_ANGLE)
                        .build();
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        } catch (IOException e) {
            setLoading(false);
            e.printStackTrace();
        }

    }

    private String receiveLocationName() {
        String locationName = "";
        if (getArguments() != null) {
            Bundle passedLocationName = getArguments();
            locationName = passedLocationName.getString(Constant.BIRTH_PLACE);
        }
        return locationName;
    }

    private void setLoading(boolean loading) {
        progressIndicator.setVisibility(loading ? View.VISIBLE : View.GONE);
    }
}
