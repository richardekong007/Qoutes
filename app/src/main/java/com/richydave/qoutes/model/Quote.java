package com.richydave.qoutes.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Quote {

    private int id;

    private String statement;

    @SerializedName("Author")
    @Expose
    private String author;

    private String birthPlace;

    @SerializedName("photo")
    @Expose
    private String photoUrl;

    private long createdAt;

    private long updatedAt;



    public Integer getId() {
        return id;
    }

    public String getStatement() {
        return statement;
    }

    public String getAuthor() {
        return author;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }
}
