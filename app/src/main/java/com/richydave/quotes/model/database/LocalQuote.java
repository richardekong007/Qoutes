package com.richydave.quotes.model.database;

import android.support.annotation.NonNull;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "Quotes")
public class LocalQuote extends Model {

    @Column(name = "AUTHOR_NAME")
    public String authorName;

    @Column(name = "STATEMENT")
    public String statement;

    @Column(name = "BIRTH_PLACE")
    public String birthPlace;

    @Column(name = "PHOTO_URL")
    public String photoUrl;

    @Column(name = "LATITUDE")
    public double latitude;

    @Column(name="LONGITUDE")
    public double longitude;


    public static void saveQuoteRecord(@NonNull String authorName, @NonNull String statement, @NonNull String birthPlace,
                                       @NonNull String photoUrl, double latitude, double longitude) {

        LocalQuote localQuote = new LocalQuote();
        localQuote.authorName = authorName;
        localQuote.statement = statement;
        localQuote.birthPlace = birthPlace;
        localQuote.photoUrl = photoUrl;
        localQuote.latitude = latitude;
        localQuote.longitude = longitude;
        localQuote.save();

    }

    public static void saveQuoteRecords(List<LocalQuote> quoteRecords) {
        ActiveAndroid.beginTransaction();
        try {
            for (LocalQuote quote : quoteRecords) {
                saveQuoteRecord(quote.authorName, quote.statement, quote.birthPlace, quote.photoUrl, quote.latitude, quote.longitude);
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    public static List<LocalQuote> getQuoteRecords() {
        return new Select().from(LocalQuote.class).execute();
    }

    public static int getCount(){
        return new Select().from(LocalQuote.class).count();
    }


    public static LocalQuote getQuoteRecord(long id) {
        return new Select().from(LocalQuote.class).where("Id =?", id).executeSingle();
    }

    public static void deleteQuoteRecords() {
        new Delete().from(LocalQuote.class).execute();
    }

    public static void deleteQuoteRecord(long id) {
        new Delete().from(LocalQuote.class).where("Id = ?", id).execute();
    }

    public static boolean isSave(int initialSize, int finalSize){
        return initialSize < finalSize;
    }
}
