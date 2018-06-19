package com.richydave.quotes.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

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
            Glide.with(getContext())
                    .load(imageUri)
                    .into(avatar);
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
                            makeFacebookPost();
                            return true;
                        case R.id.twitter:
                            makeTwitterPost();
                            return true;
                        case R.id.instagram:
                            makeInstaPost();
                            return true;
                        default:
                            return false;
                    }
                });
    }

    private void selectPhotoFromGallery() {
        Intent selectImage = new Intent();
        selectImage.setType(Constant.IMAGE_CONTENT_TYPE);
        selectImage.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(selectImage, Constant.SELECT_IMAGE_INSTRUCTION), Constant.IMAGE_PICK_CODE);
    }

    private void takePhoto() {
    }

    private void makeFacebookPost() {
    }

    private void makeTwitterPost() {
    }

    private void makeInstaPost() {
    }

}
