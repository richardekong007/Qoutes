package com.richydave.quotes.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.richydave.quotes.Constant;
import com.richydave.quotes.R;
import com.richydave.quotes.model.database.LocalQuote;
import com.richydave.quotes.ui.Dialogs.ErrorDialog;
import com.richydave.quotes.ui.Dialogs.InformationDialog;
import com.richydave.quotes.ui.menu.PopupMenuBuilder;
import com.richydave.quotes.util.LocationUtil;
import com.richydave.quotes.util.MediaUtil;

import java.util.regex.Pattern;

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

    private LocationManager locationManager;

    protected double latitude;

    protected double longitude;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        View view = inflater.inflate(R.layout.fragment_make_quote, container, false);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        LocationUtil.requestLocationUpdate(getActivity(), locationManager);
        ButterKnife.bind(this, view);
        loadQuoterInfo();
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

    @OnClick(R.id.location_switch)
    public void onLocationSwitch() {

        if (locationSwitch.isChecked()) {
            latitude = LocationUtil.getLatitude();
            longitude = LocationUtil.getLongitude();

        } else {
            LocationUtil.stopLocationUpdate(locationManager);
        }
    }

    @OnClick(R.id.post)
    public void onPostClick() {
        showSocialMediaMenu();
    }

    @OnClick(R.id.save)
    public void onSaveClick() {
        if (locationSwitch.isChecked() && editTextNotEmpty(firstName, lastName, quoteInput)) {

            String authorName = String.format(getString(R.string.author_name_format), firstName.getText().toString(), lastName.getText().toString());
            String statement = quoteInput.getText().toString();
            double latitude = LocationUtil.getLatitude();
            double longitude = LocationUtil.getLongitude();

            LocalQuote.saveQuoteRecord(authorName, statement, imageFilePath, latitude, longitude);

            if (LocalQuote.isSave()) {
                new InformationDialog(getActivity(), getString(R.string.quote_creation_title), getString(R.string.quote_success_message))
                        .build()
                        .setPositiveButton(getString(R.string.close), (dialog, id) -> dialog.dismiss())
                        .show();

                clear(firstName, lastName, quoteInput, locationSwitch, avatar);
            } else {
                String errorMessage = getString(R.string.not_saved);
                String errorTitle = getString(R.string.error);
                new ErrorDialog(getActivity(), errorTitle, errorMessage)
                        .build()
                        .setPositiveButton(getString(R.string.close), ((dialog, which) -> dialog.dismiss()))
                        .show();
            }

        }
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
                            imageFilePath = MediaUtil.takePhoto( this);
                            return true;
                        case R.id.gallery:
                            MediaUtil.selectPhotoFromGallery(this);
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

    private void loadQuoterInfo() {

        Bundle receivedBundle;
        try {
            receivedBundle = getArguments();
            String name = receivedBundle.getString(Constant.USERNAME).trim();
            imageFilePath = receivedBundle.getString(Constant.PHOTO_URI);
            Glide.with(getActivity())
                    .load(imageFilePath)
                    .into(avatar);
            Pattern pattern = Pattern.compile(getString(R.string.space_pattern));
            boolean spaceFound = pattern.matcher(name).find();
            if (spaceFound) {
                String[] names = name.split(getString(R.string.space_pattern));
                firstName.setText(names[0]);
                lastName.setText(names[1]);
            } else {
                firstName.setText(name);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private boolean editTextNotEmpty(EditText... editTexts) {
        for (EditText editText : editTexts) {
            if (editText == null) {
                return false;
            } else if (editText.getText().length() < 2) {
                return false;
            }
        }
        return true;
    }

    private void clear(View... views) {

        for (View view : views) {
            if (view instanceof SwitchCompat) {
                ((SwitchCompat) view).setChecked(false);
            } else if (view instanceof EditText) {
                ((EditText) view).setText("");
            } else if (view instanceof ImageView) {
                Glide.with(this)
                        .load(R.drawable.ic_account)
                        .into((ImageView) view);
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