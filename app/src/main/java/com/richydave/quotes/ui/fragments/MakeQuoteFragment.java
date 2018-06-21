package com.richydave.quotes.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.richydave.quotes.Constant;
import com.richydave.quotes.R;
import com.richydave.quotes.ui.menu.PopupMenuBuilder;
import com.richydave.quotes.util.MediaUtil;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.disposables.Disposable;

import static android.app.Activity.RESULT_CANCELED;

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

    private Disposable disposable = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        View view = inflater.inflate(R.layout.fragment_make_quote, container, false);
        ButterKnife.bind(this, view);
        setRetainInstance(true);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (disposable != null) {
            disposable.dispose();
        }
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
            MediaUtil.displayImage(getActivity(), disposable, avatar, imageUri);
        }
        if (requestCode == Constant.REQUEST_IMAGE_CAPTURE) {

            MediaUtil.displayImage(getActivity(), imageFilePath, avatar);

        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getActivity(), getString(R.string.camera_cancel), Toast.LENGTH_LONG).show();
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

    private void takePhoto() {
        Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (captureImage.resolveActivity(getActivity().getPackageManager()) != null) {

            File imageFile = null;
            try {
                imageFile = MediaUtil.createImageFile(getActivity());
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (imageFile != null) {
                Uri imageUri = FileProvider.getUriForFile(getActivity(), getString(R.string.authority), imageFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                imageFilePath = imageFile.getAbsolutePath();
                startActivityForResult(captureImage, Constant.REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void postQuoteToFacebook() {
    }
    private void postQuoteToTwitter() {
    }

    private void postQuoteToInsta() {
    }
}
