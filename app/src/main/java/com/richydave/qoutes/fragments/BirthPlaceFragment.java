package com.richydave.qoutes.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.richydave.qoutes.R;

import butterknife.ButterKnife;

public class BirthPlaceFragment extends Fragment {

    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        View view = inflater.inflate(R.layout.fragment_birth_place, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
