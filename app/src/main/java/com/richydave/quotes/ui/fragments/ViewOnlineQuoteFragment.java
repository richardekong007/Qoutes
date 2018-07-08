package com.richydave.quotes.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.richydave.quotes.Constant;
import com.richydave.quotes.R;
import com.richydave.quotes.ui.Dialogs.ErrorDialog;
import com.richydave.quotes.ui.menu.PopupMenuBuilder;
import com.richydave.quotes.util.FragmentUtil;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ViewQuoteFragment extends Fragment {

    private String birthPlace;

    private LatLng birthCoordinates;

    private LatLng quoteCoordinates;

    private String quoteTag;

    @BindView(R.id.avatar)
    CircleImageView avatar;

    @BindView(R.id.author_name)
    TextView authorNameText;

    @BindView(R.id.quote)
    TextView quoteText;

    @BindView(R.id.places)
    AppCompatButton placeButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        View view = inflater.inflate(R.layout.fragment_view_quote, container, false);
        ButterKnife.bind(this, view);
        init();
        setRetainInstance(true);
        return view;
    }

    private void init() {
        Bundle quoteDetails;
        if (getArguments() != null) {
                quoteDetails = getArguments();
                Glide.with(getActivity())
                        .load(quoteDetails.get(Constant.PHOTO_URI))
                        .into(avatar);
                authorNameText.setText(quoteDetails.getString(Constant.AUTHOR));
                quoteText.setText(quoteDetails.getString(Constant.STATEMENT));
                birthPlace = quoteDetails.getString(Constant.BIRTH_PLACE);
                if (quoteDetails.containsKey(Constant.ONLINE_QUOTE)) {
                    quoteTag = Constant.ONLINE_QUOTE;
                    birthCoordinates = quoteDetails.getParcelable(Constant.LOCATION);
                }
                if (quoteDetails.containsKey(Constant.LOCAL_QUOTE)) {
                    quoteTag = Constant.LOCAL_QUOTE;
                    quoteCoordinates = quoteDetails.getParcelable(Constant.LOCATION);
                }
        }
    }

    @OnClick(R.id.places)
    public void onPlaceClick() {
        showPlacesMenu();
    }

    private void onBirthPlaceClick() {
        Bundle argument = passBirthPlaceDetail();
        if (!argument.isEmpty()) {
            FragmentUtil.replaceFragment(getFragmentManager(), new PlacesFragment(), argument, true);
        }
    }

    private void onQuotePlaceClick() {
        Bundle argument = passQuotePlaceBundle();
        if (!argument.isEmpty()) {
            FragmentUtil.replaceFragment(getFragmentManager(), new PlacesFragment(), argument, true);
        }
    }

    private Bundle passBirthPlaceDetail() {
        Bundle birthPlaceBundle = new Bundle();
        if (birthPlace != null && birthCoordinates != null) {
            birthPlaceBundle.putString(Constant.BIRTH_PLACE, birthPlace);
            if (quoteTag.equals(Constant.ONLINE_QUOTE)) {
                birthPlaceBundle.putString(Constant.ONLINE_QUOTE, quoteTag);
                birthPlaceBundle.putParcelable(Constant.LOCATION, birthCoordinates);
            }
        }
        return birthPlaceBundle;
    }

    private Bundle passQuotePlaceBundle() {
        Bundle quotePlaceBundle = new Bundle();
        try {
            if (quoteTag.equals(Constant.LOCAL_QUOTE)) {
                quotePlaceBundle.putString(Constant.LOCAL_QUOTE, quoteTag);
                quotePlaceBundle.putParcelable(Constant.LOCATION, quoteCoordinates);
            } else {
                new ErrorDialog(getActivity(), getString(R.string.location_error_title), getString(R.string.Location_error_message))
                        .build()
                        .setPositiveButton(getString(R.string.close), ((dialog, which) -> dialog.dismiss()))
                        .show();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return quotePlaceBundle;
    }

    private void showPlacesMenu() {
        PopupMenuBuilder popupMenuBuilder = new PopupMenuBuilder(getActivity(), placeButton, R.menu.places_menu);
        popupMenuBuilder.getInstance()
                .setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.birth_place:
                            passBirthPlaceDetail();
                            return true;
                        case R.id.quote_place:
                            passQuotePlaceBundle();
                            return true;
                        default:
                            return false;
                    }
                });
    }
}
