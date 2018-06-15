package com.richydave.qoutes.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Quote {

    private int id;

    private String statement;

    @SerializedName("Author")
    @Expose
    private String author;

    private String birthplace;

    @SerializedName("photo")
    @Expose
    private String photoUrl;

    private Integer createdAt;

    private Integer updatedAt;



    public Integer getId() {
        return id;
    }

    public String getStatement() {
        return statement;
    }

    public String getAuthor() {
        return author;
    }

    public String getBirthplace() {
        return birthplace;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public Integer getCreatedAt() {
        return createdAt;
    }

    public Integer getUpdatedAt() {
        return updatedAt;
    }
}
