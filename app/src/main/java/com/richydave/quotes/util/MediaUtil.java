package com.richydave.quotes.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.richydave.quotes.Constant;
import com.richydave.quotes.R;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.richydave.quotes.Constant.BUILD_VERSION;
import static com.richydave.quotes.Constant.HONEYCOMB;
import static com.richydave.quotes.Constant.KITKAT;
import static com.richydave.quotes.Constant.MASHMALLOW;

public class MediaUtil {

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

    private static File createImageFile(Context context) throws IOException {
        String timeStamp = new SimpleDateFormat(Constant.FILE_NAME_FORMAT, Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File fileSystem = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        return File.createTempFile(imageFileName,
                Constant.IMAGE_FILE_TYPE,
                fileSystem);
    }

    private static boolean isPermissionGranted(Context context) {
        if (BUILD_VERSION >= MASHMALLOW) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog(context,
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

    private static void showDialog(Context context, String readExternalStoragePermission) {

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage("External storage " + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                (dialog, which) ->
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{readExternalStoragePermission},
                                Constant.PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE));
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    private static Bitmap getImageFromUri(Context context, Uri imageUri) {
        //must be executed in background thread
        Bitmap image = null;
        ParcelFileDescriptor parcelFileDescriptor;
        try {
            parcelFileDescriptor = context.getContentResolver().openFileDescriptor(imageUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public static void displayImage(Context context, Disposable disposable, ImageView view, Uri imageUri) {
        disposable = getBitmapObservable(context, imageUri)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bitmap -> Glide.with(context).load(bitmap).into(view)
                        , Throwable::printStackTrace);
    }

    public static void displayImage(Context context, String imageFilePath, ImageView imageView) {
        File imageFile = new File(imageFilePath);
        if (imageFile.exists()) {
            Glide.with(context).load(imageFile).into(imageView);
        }
    }

    public static String takePhoto(Context context) {
        Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String imageFilePath = null;
        if (captureImage.resolveActivity(context.getPackageManager()) != null) {

            File imageFile = null;
            try {
                imageFile = createImageFile(context);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (imageFile != null) {
                Uri imageUri = FileProvider.getUriForFile(context, context.getString(R.string.authority), imageFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                imageFilePath = imageFile.getAbsolutePath();
                ((Activity) context).startActivityForResult(captureImage, Constant.REQUEST_IMAGE_CAPTURE);

            }
        }
        return imageFilePath;
    }

    public static String takePhoto(Fragment fragment) {
        Context context = fragment.getContext();
        Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String imageFilePath = null;
        if (captureImage.resolveActivity(context.getPackageManager()) != null) {

            File imageFile = null;
            try {
                imageFile = createImageFile(context);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (imageFile != null) {
                Uri imageUri = FileProvider.getUriForFile(context, context.getString(R.string.authority), imageFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                imageFilePath = imageFile.getAbsolutePath();
                fragment.startActivityForResult(captureImage, Constant.REQUEST_IMAGE_CAPTURE);

            }
        }
        return imageFilePath;
    }

    public static void selectPhotoFromGallery(Context context) {
        if (MediaUtil.isPermissionGranted(context)) {

            Intent selectImage = getGalleryIntent();
            ((Activity) context).startActivityForResult(Intent.createChooser(selectImage,
                    Constant.SELECT_IMAGE_INSTRUCTION), Constant.IMAGE_PICK_CODE);

        }
    }

    public static void selectPhotoFromGallery(Fragment fragment) {
        if (MediaUtil.isPermissionGranted(fragment.getContext())) {

            Intent selectImage = getGalleryIntent();
            fragment.startActivityForResult(Intent.createChooser(selectImage,
                    Constant.SELECT_IMAGE_INSTRUCTION), Constant.IMAGE_PICK_CODE);
        }
    }

    @NonNull
    private static Intent getGalleryIntent() {
        Intent selectImage = new Intent();
        selectImage.setType(Constant.IMAGE_CONTENT_TYPE);
        selectImage.setAction(Intent.ACTION_OPEN_DOCUMENT);
        selectImage.addCategory(Intent.CATEGORY_OPENABLE);
        return selectImage;
    }

    public static Observable<Bitmap> getBitmapObservable(Context context, Uri imageUri) {
        return Observable.just(getImageFromUri(context, imageUri));
    }
}