package com.richydave.qoutes.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import com.richydave.qoutes.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ViewQuoteFragment extends Fragment {

    @BindView(R.id.avatar)
    CircleImageView avatar;

    @BindView(R.id.quote)
    TextView quoteText;

    @BindView(R.id.birth_place)
    AppCompatButton BirthPlaceButton;

    public View onCreate(LayoutInflater inflater, ViewGroup container, Bundle saveinstanceState) {

        View view = inflater.inflate(R.layout.fragment_view_quote, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.birth_place)
    public void onBirthPlaceClick(){

    }
}
