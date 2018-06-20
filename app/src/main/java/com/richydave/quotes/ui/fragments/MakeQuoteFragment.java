package com.richydave.quotes.ui.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.richydave.quotes.Constant;
import com.richydave.quotes.R;
import com.richydave.quotes.ui.menu.PopupMenuBuilder;
import com.richydave.quotes.util.MediaUtil;

import java.io.FileDescriptor;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MakeQuoteFragment extends Fragment {


    @BindView(R.id.author_avatar)
    CircleImageView avatar;

    @BindView(R.id.first_name)
    EditText firstName;

    @BindView(R.id.last_name)
    EditText lastName;

    @BindView(R.id.location_switch)
    SwitchCompat locationSwitch;

    @BindView(R.id.quote)
    EditText quoteInput;


    @BindView(R.id.post)
    AppCompatButton post;

    @BindView(R.id.save)
    AppCompatButton saveQuote;

    private String imageFilePath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        View view = inflater.inflate(R.layout.fragment_make_quote, container, false);
        ButterKnife.bind(this, view);
        setRetainInstance(true);
        return view;
    }

    @OnClick(R.id.author_avatar)
    public void onAvatarClick() {
        showPictureMenu();
    }

    @OnClick(R.id.post)
    public void onPostClick() {
        showSocialMediaMenu();
    }

    @OnClick(R.id.save)
    public void onSaveClick() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.IMAGE_PICK_CODE && data != null) {
            Uri imageUri = data.getData();
            imageFilePath = MediaUtil.resolveMediaPath(getActivity(), imageUri);
            displayImageFromUri(imageUri);
        }
    }

    private void showPictureMenu() {

        PopupMenuBuilder popupMenu = new PopupMenuBuilder(getContext(), avatar, R.menu.picture_select_menu);
        popupMenu.getInstance()
                .setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.camera:
                            takePhoto();
                            return true;
                        case R.id.gallery:
                            selectPhotoFromGallery();
                            return true;
                        default:
                            return false;
                    }
                });

    }

    private void showSocialMediaMenu() {
        PopupMenuBuilder socialMediaMenu = new PopupMenuBuilder(getContext(), post, R.menu.social_media_select_menu);
        socialMediaMenu.getInstance()
                .setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.facebook:
                            postQuoteToFacebook();
                            return true;
                        case R.id.twitter:
                            postQuoteToTwitter();
                            return true;
                        case R.id.instagram:
                            postQuoteToInsta();
                            return true;
                        default:
                            return false;
                    }
                });
    }

    private void selectPhotoFromGallery() {
        if (MediaUtil.isPermissionGranted(getActivity())) {

            Intent selectImage = new Intent();
            selectImage.setType(Constant.IMAGE_CONTENT_TYPE);
            selectImage.setAction(Intent.ACTION_OPEN_DOCUMENT);
            selectImage.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(selectImage, Constant.SELECT_IMAGE_INSTRUCTION), Constant.IMAGE_PICK_CODE);
        }
    }

    private Bitmap getImageFromUri(Uri imageUri) {
        //must be executed in background thread
        Bitmap image = null;
        ParcelFileDescriptor parcelFileDescriptor;
        try {
            parcelFileDescriptor = getContext().getContentResolver().openFileDescriptor(imageUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    private Observable<Bitmap> getImageObservable(Uri imageUri) {
        return Observable.just(getImageFromUri(imageUri));
    }

    private void displayImageFromUri(Uri imageUri) {
        getImageObservable(imageUri)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(image -> Glide.with(this)
                                .load(image)
                                .into(avatar),
                        Throwable::printStackTrace);
    }


    private void takePhoto() {
    }

    private void postQuoteToFacebook() {
    }

    private void postQuoteToTwitter() {
    }

    private void postQuoteToInsta() {
    }

}
