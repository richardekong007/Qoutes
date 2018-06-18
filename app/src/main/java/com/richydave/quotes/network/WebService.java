package com.richydave.quotes.network;

import com.richydave.quotes.model.Quote;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;

public interface WebService {
    //url endpoint
    @GET("bins/1hjozq")
    Single<List<Quote>> getOnlineQuotes();
}
