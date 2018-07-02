package com.richydave.quotes;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.activeandroid.app.Application;
import com.richydave.quotes.model.database.LocalQuote;
import com.richydave.quotes.model.database.UserCredential;

public class QuoteApp extends Application {

    @Override
    public void onCreate (){
        super.onCreate();
        Configuration.Builder configBuilder = new Configuration.Builder(this);
        configBuilder.addModelClass(LocalQuote.class);
        configBuilder.addModelClass(UserCredential.class);
        ActiveAndroid.initialize(this);
    }
}
