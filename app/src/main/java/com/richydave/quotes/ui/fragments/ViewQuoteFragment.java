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
import com.richydave.quotes.util.FragmentUtil;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ViewQuoteFragment extends Fragment {

    private String birthPlace;

    private LatLng coordinates;

    @BindView(R.id.avatar)
    CircleImageView avatar;

    @BindView(R.id.author_name)
    TextView authorNameText;

    @BindView(R.id.quote)
    TextView quoteText;

    @BindView(R.id.birth_place)
    AppCompatButton BirthPlaceButton;

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
            coordinates = quoteDetails.getParcelable(Constant.LOCATION);
        }
    }

    @OnClick(R.id.birth_place)
    public void onBirthPlaceClick() {
        Bundle argument = passBirthPlaceDetail();
        FragmentUtil.replaceFragment(getFragmentManager(), new BirthPlaceFragment(), argument, true);
    }

    private Bundle passBirthPlaceDetail() {
        Bundle birthPlaceBundle = new Bundle();
        if (birthPlace != null && coordinates != null) {
            birthPlaceBundle.putString(Constant.BIRTH_PLACE, birthPlace);
            birthPlaceBundle.putParcelable(Constant.LOCATION, coordinates);
        }
        return birthPlaceBundle;
    }
}
