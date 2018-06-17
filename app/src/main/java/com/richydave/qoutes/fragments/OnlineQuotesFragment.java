package com.richydave.qoutes.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.richydave.qoutes.R;
import com.richydave.qoutes.adapter.QuoteRecordsAdapter;
import com.richydave.qoutes.model.Quote;
import com.richydave.qoutes.network.Api;
import com.richydave.qoutes.util.FragmentUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class OnlineQuotesFragment extends Fragment implements QuoteRecordsAdapter.ViewQuoteClickListener {

    private QuoteRecordsAdapter adapter;

    private CompositeDisposable disposable = new CompositeDisposable();

    private List<Quote> onlineQuotes = new ArrayList<>();

    @BindView(R.id.records)
    RecyclerView recyclerView;

    @BindView(R.id.progress_indicator)
    ProgressBar progressIndicator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        View view = inflater.inflate(R.layout.fragment_online_records, container, false);
        ButterKnife.bind(this, view);
        init();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }

    @Override
    public void onViewQuoteClick(Bundle args) {
        FragmentUtil.replaceFragment(getFragmentManager(), new ViewQuoteFragment(), args, true);
    }

    private void init() {
        //set up adapter
        adapter = new QuoteRecordsAdapter(onlineQuotes);
        adapter.setViewQuoteClickListener(this);
        // set up recyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        // and get online Quotes here
        getQuotes();
    }

    private void getQuotes() {
        setLoading(true);
        disposable.add(Api.getInstance().getWebService().getOnlineQuotes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<Quote>>() {
                    @Override
                    public void onSuccess(List<Quote> quotes) {
                        //TODO: update adapter here
                        setLoading(false);
                        onlineQuotes.clear();
                        onlineQuotes.addAll(quotes);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        setLoading(false);
                        e.printStackTrace();
                    }
                }));
    }

    private void setLoading(boolean loading) {

        progressIndicator.setVisibility(loading ? View.VISIBLE : View.GONE);

    }
}