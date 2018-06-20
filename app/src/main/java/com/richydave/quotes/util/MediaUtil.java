package com.richydave.quotes.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;

import com.richydave.quotes.Constant;

public class MediaUtil {

    private static final int BUILD_VERSION = Build.VERSION.SDK_INT;

    private static final int MASHMALLOW = Build.VERSION_CODES.M;

    private static int KITKAT = Build.VERSION_CODES.KITKAT;

    private static int HONEYCOMB = Build.VERSION_CODES.HONEYCOMB;

    public static String resolveMediaPath(Context context, Uri uri) {

        String filePath = null;

        if (isPermissionGranted(context)) {

            if (BUILD_VERSION >= Build.VERSION_CODES.KITKAT) {

                String fullDocumentId = DocumentsContract.getDocumentId(uri);
                String partDocId = fullDocumentId.split(":")[1];
                String[] column = {MediaStore.Images.Media.DATA};
                String selection = MediaStore.Images.Media._ID + "=?";
                Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, selection, new String[]{partDocId}, null);
                int columnIndex = cursor.getColumnIndex(column[0]);
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex);
                }
            } else if (BUILD_VERSION >= HONEYCOMB && BUILD_VERSION < KITKAT) {

                String[] projection = {MediaStore.Images.Media.DATA};
                CursorLoader cursorLoader = new CursorLoader(context, uri, projection, null, null, null);
                Cursor cursor = cursorLoader.loadInBackground();
                if (cursor != null) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    filePath = cursor.getString(columnIndex);
                }

            } else if (BUILD_VERSION < HONEYCOMB) {

                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cursor = context.getContentResolver().query(uri, projection,
                        null, null, null);
                int columnIndex = cursor.getColumnIndexOrThrow(projection[0]);
                cursor.moveToFirst();
                filePath = cursor.getString(columnIndex);
            }
        }

        return filePath;
    }

    public static boolean isPermissionGranted(Context context) {
        if (BUILD_VERSION >= MASHMALLOW) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage ", context,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    Constant.PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    private static void showDialog(String message, Context context, String readExternalStoragePermission) {

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(message + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                (dialog, which) ->
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{readExternalStoragePermission},
                                Constant.PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE));
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }
}

