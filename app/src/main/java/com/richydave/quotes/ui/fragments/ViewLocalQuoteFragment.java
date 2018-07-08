package com.richydave.quotes.ui.fragments;

import android.location.Address;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.richydave.quotes.Constant;
import com.richydave.quotes.R;
import com.richydave.quotes.ui.menu.PopupMenuBuilder;
import com.richydave.quotes.util.FragmentUtil;
import com.richydave.quotes.util.LocationUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ViewLocalQuoteFragment extends Fragment {

    private String birthPlace;

    private LatLng quoteCoordinates;

    private String quoteTag;

    @BindView(R.id.avatar)
    CircleImageView avatar;

    @BindView(R.id.author_name)
    TextView authorNameText;

    @BindView(R.id.quote)
    TextView quoteText;

    @BindView(R.id.quote_place)
    AppCompatButton placeButton;

    private CompositeDisposable disposable = new CompositeDisposable();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        View view = inflater.inflate(R.layout.fragment_view_local_quote, container, false);
        ButterKnife.bind(this, view);
        init();
        setRetainInstance(true);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

    private void init() {
        Bundle quoteDetails;
        try {
            quoteDetails = getArguments();
            Glide.with(getActivity())
                    .load(quoteDetails.get(Constant.PHOTO_URI))
                    .into(avatar);
            authorNameText.setText(quoteDetails.getString(Constant.AUTHOR));
            quoteText.setText(quoteDetails.getString(Constant.STATEMENT));
            birthPlace = quoteDetails.getString(Constant.BIRTH_PLACE);
            quoteTag = quoteDetails.getString(Constant.QUOTE_TAG);
            quoteCoordinates = quoteDetails.getParcelable(Constant.LOCAL_LOCATION);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    @OnClick(R.id.quote_place)
    public void onPlaceClick() {
        Bundle argument = passQuotePlaceBundle();
        if (!argument.isEmpty()) {
            FragmentUtil.replaceFragment(getFragmentManager(), new PlacesFragment(), argument, true);
        }
    }
    private Bundle passQuotePlaceBundle() {
        Bundle quotePlaceBundle = new Bundle();
        try {
            quotePlaceBundle.putParcelable(Constant.LOCAL_LOCATION, quoteCoordinates);
            quotePlaceBundle.putString(Constant.QUOTE_TAG, quoteTag);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return quotePlaceBundle;
    }

}
