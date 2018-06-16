package com.richydave.qoutes.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.richydave.qoutes.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class LocalQuotesFragment extends Fragment {

    @BindView(R.id.records)
    RecyclerView localRecordsRecyclerView;

    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        View view = inflater.inflate(R.layout.fragment_local_records,container, false);
        ButterKnife.bind(this,view);
        return view;
    }
}
