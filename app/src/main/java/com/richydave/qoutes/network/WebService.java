package com.richydave.qoutes.network;

import com.richydave.qoutes.model.Quote;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;

public interface WebService {
    //url endpoint
    @GET("bins/1fgujq")
    Single<List<Quote>> getOnlineQuotes();
}
