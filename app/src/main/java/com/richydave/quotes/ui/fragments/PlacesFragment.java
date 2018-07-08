package com.richydave.quotes.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.richydave.quotes.Constant;
import com.richydave.quotes.R;
import com.richydave.quotes.util.LocationUtil;
import butterknife.ButterKnife;
import static com.richydave.quotes.Constant.BEARING;
import static com.richydave.quotes.Constant.BIRTH_PLACE;
import static com.richydave.quotes.Constant.LOCAL_LOCATION;
import static com.richydave.quotes.Constant.ONLINE_LOCATION;
import static com.richydave.quotes.Constant.TILT_ANGLE;
import static com.richydave.quotes.Constant.ZOOM_LEVEL;


public class PlacesFragment extends Fragment implements OnMapReadyCallback {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        View view = inflater.inflate(R.layout.fragment_google_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        ButterKnife.bind(this, view);
        setRetainInstance(true);
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

        LatLng coordinates = configureGoogleMap(googleMap);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        CameraPosition cameraPosition = CameraPosition.builder()
                .target(coordinates)
                .zoom(ZOOM_LEVEL)
                .bearing(BEARING)
                .tilt(TILT_ANGLE)
                .build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    private LatLng configureGoogleMap(GoogleMap googleMap) {
        String birthPlace;
        LatLng coordinates = null;
        String quotePlace;
        try {
            Bundle locationDetails = getArguments();
            String quoteTag = locationDetails.getString(Constant.QUOTE_TAG);
            if (quoteTag.equals(Constant.ONLINE_QUOTE)) {
                birthPlace = locationDetails.getString(BIRTH_PLACE);
                coordinates = locationDetails.getParcelable(ONLINE_LOCATION);
                googleMap.addMarker(new MarkerOptions()
                        .position(coordinates)
                        .title(birthPlace));
            }
            if (quoteTag.equals(Constant.LOCAL_QUOTE)) {
                coordinates = locationDetails.getParcelable(LOCAL_LOCATION);
                quotePlace = LocationUtil.getPlaceFromLocation(getContext(), coordinates);
                googleMap.addMarker(new MarkerOptions()
                        .position(coordinates)
                        .title(quotePlace));
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return coordinates;
    }
}
