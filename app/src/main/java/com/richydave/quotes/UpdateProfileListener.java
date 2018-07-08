package com.richydave.quotes;

import com.richydave.quotes.model.database.UserCredential;

public interface UpdateProfileListener {

    void onUpdateProfile(UserCredential userRecord);
}
