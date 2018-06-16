package com.richydave.qoutes.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.richydave.qoutes.R;
import com.richydave.qoutes.adapter.QuoteRecordsAdapter;
import com.richydave.qoutes.model.Quote;
import com.richydave.qoutes.network.Api;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class OnlineQuotesFragment extends Fragment {

    private QuoteRecordsAdapter adapter;

    private CompositeDisposable disposable = new CompositeDisposable();

    @BindView(R.id.records)
    RecyclerView onlineRecordsRecyclerView;


    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){

        View view = inflater.inflate(R.layout.fragment_online_records, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    private void init(){

    }

    private void getQuotes(){
        disposable.add(Api.getInstance().getWebService().getOnlineQuotes()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<List<Quote>>(){
                        @Override
                        public void onSuccess(List<Quote> value) {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    }));
    }


}
