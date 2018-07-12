package com.richydave.quotes.model.database;

import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.richydave.quotes.Constant;
import com.richydave.quotes.R;
import com.richydave.quotes.interfaces.UpdateProfileListener;
import com.richydave.quotes.ui.Dialogs.ErrorDialog;
import com.richydave.quotes.ui.Dialogs.InformationDialog;

import java.util.HashMap;
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

    private static boolean wasUpdated;

    private static UpdateProfileListener updateProfileListener;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static boolean getWasUpdated() {
        return wasUpdated;
    }

    private static void setWasUpdated(boolean wasUpdated) {
        UserCredential.wasUpdated = wasUpdated;
    }

    public static int getCount() {
        return new Select().all()
                .from(UserCredential.class)
                .count();
    }

    public static boolean userNameExist(Context context, String username) {
        try {
            UserCredential userCredential = new Select().from(UserCredential.class)
                    .where(Constant.COLUMN_USERNAME + " = ?", username).executeSingle();
            return userCredential.userName != null;
        } catch (NullPointerException e) {
            return false;
        }
    }

    public static boolean passwordExists(Context context, String username, String inputPassword) {
        try {
            UserCredential userCredential = new Select()
                    .from(UserCredential.class)
                    .where("USERNAME = ?", username)
                    .executeSingle();
            return userCredential.getPassword() != null && userCredential.getPassword().equals(inputPassword);
        } catch (NullPointerException e) {
            return false;
        }
    }

    public static String getDbPassword(Context context, String userName) {
        String password = "";
        try {
            UserCredential userCredential = new Select().from(UserCredential.class)
                    .where(Constant.USERNAME + "=?", userName).executeSingle();
            password = userCredential.password;
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return password;
    }

    public static void updateUserCredentials(Context context, String userName, HashMap<String, String> valueMap) {

        if (userNameExist(context, userName)) {
            UserCredential currentRecord = getUserRecord(context, userName);
            for (int i = 0; i < valueMap.size(); i++) {
                new Update(UserCredential.class)
                        .set(valueMap.keySet().toArray()[i] + "=?", valueMap.values().toArray()[i])
                        .execute();
            }
            UserCredential newRecord = getUserRecord(context, userName);
            if (currentRecord.photoUri.equals(newRecord.photoUri)
                    && currentRecord.password.equals(newRecord.password)) {
                updateProfileListener.onUpdateProfile(getUserRecord(context,userName));
                new InformationDialog(context, context.getString(R.string.update_title), context.getString(R.string.update_success_message))
                        .build()
                        .setPositiveButton(context.getString(R.string.close), (dialog, id) -> dialog.dismiss())
                        .show();
            } else {
                new ErrorDialog(context, context.getString(R.string.update_title), context.getString(R.string.update_failure_message))
                        .build()
                        .setPositiveButton(context.getString(R.string.close), ((dialog, which) -> dialog.dismiss()))
                        .show();
            }
        } else {
            new ErrorDialog(context, context.getString(R.string.error), context.getString(R.string.no_record))
                    .build()
                    .setPositiveButton(context.getString(R.string.close), (dialog, id) -> dialog.dismiss())
                    .show();
        }
    }

    public static UserCredential getUserRecord(Context context, String userName) {
        UserCredential currentRecord = null;
        try {
            currentRecord = new Select().from(UserCredential.class)
                    .where(Constant.COLUMN_USERNAME + "=?", userName)
                    .executeSingle();
        } catch (NullPointerException e) {
            new ErrorDialog(context, context.getString(R.string.error_tittle), context.getString(R.string.no_record))
                    .build()
                    .setPositiveButton(context.getString(R.string.close), (dialog, id) -> dialog.dismiss())
                    .show();
            e.printStackTrace();
        }
        return currentRecord;
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
            UserCredential record = getUserRecord(context, userName);
            photoUri = record.photoUri;
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

    public static void setUpdateProfileListener(UpdateProfileListener listener){
        updateProfileListener = listener;
    }
}