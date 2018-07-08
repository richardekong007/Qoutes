package com.richydave.quotes.ui.fragments;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.gms.maps.model.LatLng;
import com.richydave.quotes.Constant;
import com.richydave.quotes.R;
import com.richydave.quotes.adapter.LocalQuoteAdapter;
import com.richydave.quotes.model.database.LocalQuote;
import com.richydave.quotes.util.FragmentUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocalQuotesFragment extends Fragment implements LocalQuoteAdapter.ViewQuoteClickListener {

    @BindView(R.id.records)
    RecyclerView localRecordsRecyclerView;

    @BindView(R.id.progress_indicator)
    ProgressBar progressIndicator;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_records, container, false);
        ButterKnife.bind(this, view);
        setRetainInstance(true);
        init();
        return view;
    }

    @Override
    public void onViewQuoteClick(int id, LocalQuote quote) {
        Bundle args = new Bundle();
        if (quote != null) {

            LatLng location = new LatLng(quote.getLatitude(), quote.getLongitude());
            args.putString(Constant.PHOTO_URI, quote.getPhotoUrl());
            args.putString(Constant.AUTHOR, quote.getAuthorName());
            args.putString(Constant.STATEMENT, quote.getStatement());
            args.putParcelable(Constant.LOCAL_LOCATION, location);
            args.putString(Constant.QUOTE_TAG, Constant.LOCAL_QUOTE);
        }

        FragmentUtil.replaceFragment(getFragmentManager(), new ViewLocalQuoteFragment(), args, true);
    }

    public void init() {
        setLoading(true);
        new Handler().postDelayed(()->{
            setLoading(false);
            List<LocalQuote> localQuoteList = LocalQuote.getQuoteRecords();
            LocalQuoteAdapter localQuoteAdapter = new LocalQuoteAdapter(localQuoteList);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            localRecordsRecyclerView.setLayoutManager(linearLayoutManager);
            localRecordsRecyclerView.hasFixedSize();
            localRecordsRecyclerView.setAdapter(localQuoteAdapter);
            localQuoteAdapter.setViewQuoteClickListener(this);
        }, Constant.LOAD_TIME);

    }

    private void setLoading(boolean loading) {

        progressIndicator.setVisibility(loading ? View.VISIBLE : View.GONE);

    }
}
