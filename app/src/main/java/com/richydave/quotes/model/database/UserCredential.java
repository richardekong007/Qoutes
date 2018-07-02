package com.richydave.quotes.model.database;

import android.content.Context;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.richydave.quotes.Constant;
import com.richydave.quotes.R;
import com.richydave.quotes.ui.Dialogs.ErrorDialog;

import java.sql.SQLException;
import java.util.regex.Pattern;

@Table(name = Constant.TABLE_USER_CREDENTIALS)

public class UserCredential extends Model {

    @Column(name = Constant.COLUMN_USERNAME, unique = true)
    private String userName;

    @Column(name = Constant.COLUMN_PHOTO_URI)
    private String photoUri;

    //no encryption
    @Column(name = Constant.COLUMN_PASSWORD)
    private String password;

    private static int initialSize;

    public static int getCount() {
        return new Select().all()
                .from(UserCredential.class)
                .count();
    }

    public static boolean userNameExist(Context context, String username) {
        try {
            UserCredential userCredential = new Select().from(UserCredential.class)
                    .where("USERNAME = ?", username).executeSingle();
            return userCredential.userName != null;
        } catch (NullPointerException e) {
            return false;
        }
    }

    public static boolean passwordExist(Context context, String username) {
        try {
            UserCredential userCredential = new Select()
                    .from(UserCredential.class)
                    .where("USERNAME = ?", username)
                    .executeSingle();
            return userCredential.password != null;
        } catch (NullPointerException e) {
            return false;
        }
    }

    public static void updateUserCredential(String userName, String... columns) {
        Update updateStatement = new Update(UserCredential.class);
        try {
            for (String column : columns) {
                updateStatement.set(column)
                        .where(Constant.COLUMN_USERNAME + "=?", userName)
                        .execute();
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public static void saveUserCredential(Context context, String username, String photoUrl, String password) {

        UserCredential userCredential = new UserCredential();
        if (username != null && password != null) {
            userCredential.userName = username;
            userCredential.photoUri = photoUrl;
            userCredential.password = password;
            initialSize = getCount();
            //do not save user credential if  username exists in the database
            if (userNameExist(context, username)) {
                String title = context.getString(R.string.signup_conflict_error);
                String message = context.getString(R.string.signup_conflict_error_message);
                new ErrorDialog(context, title, message).build()
                        .setPositiveButton(context.getString(R.string.close),
                                ((dialog, which) -> dialog.dismiss()))
                        .show();
            } else {
                userCredential.save();
            }
        }
    }

    public static String getPhotoUri(Context context, String userName) {
        String photoUri = null;
        try {
            if (userNameExist(context, userName)) {
                UserCredential userCredential = new Select().from(UserCredential.class)
                        .where(Constant.USERNAME + "=?", userName).executeSingle();
                photoUri = userCredential.photoUri;
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return photoUri;
    }

    public static boolean wasSave() {
        return initialSize < getCount();
    }


    public static boolean isCredentialCorrect(Context context, String userName, String password) {

        return validateCredential(context, userName, password);
    }

    private static boolean validateCredential(Context context, String userName, String password) {
        String validationTitle;
        String validationMessage;
        if (userName != null && password != null) {

            if (!(userName.equals("") || password.equals(""))) {

                if (userName.length() > 1 && password.length() > 6) {
                    Pattern pattern = Pattern.compile(context.getString(R.string.number_pattern));
                    boolean containsDigit = pattern.matcher(password).find();
                    if (!containsDigit) {
                        validationTitle = context.getString(R.string.password_error);
                        validationMessage = context.getString(R.string.weak_password_message);
                        new ErrorDialog(context, validationTitle, validationMessage)
                                .build().setPositiveButton(context.getString(R.string.close), ((dialog, which) -> dialog.dismiss()))
                                .show();
                        return false;
                    } else {
                        return true;
                    }
                } else {
                    validationTitle = context.getString(R.string.password_error);
                    validationMessage = context.getString(R.string.weak_username_password);
                    new ErrorDialog(context, validationTitle, validationMessage)
                            .build().setPositiveButton(context.getString(R.string.close), ((dialog, which) -> dialog.dismiss()))
                            .show();
                }
            } else {
                validationTitle = context.getString(R.string.signup_error);
                validationMessage = context.getString(R.string.empty_user_credential);
                new ErrorDialog(context, validationTitle, validationMessage)
                        .build().setPositiveButton(context.getString(R.string.close), ((dialog, which) -> dialog.dismiss()))
                        .show();
            }
        }
        return false;
    }
}