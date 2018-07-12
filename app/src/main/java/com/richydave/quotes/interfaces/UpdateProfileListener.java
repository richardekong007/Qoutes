package com.richydave.quotes.interfaces;

import com.richydave.quotes.model.database.UserCredential;

public interface UpdateProfileListener {

    void onUpdateProfile(UserCredential userRecord);
}
