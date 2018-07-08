package com.richydave.quotes.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.richydave.quotes.Constant;
import com.richydave.quotes.R;
import com.richydave.quotes.UpdateProfileListener;
import com.richydave.quotes.model.database.UserCredential;
import com.richydave.quotes.ui.Dialogs.ErrorDialog;
import com.richydave.quotes.ui.Dialogs.InformationDialog;
import com.richydave.quotes.ui.menu.PopupMenuBuilder;
import com.richydave.quotes.util.MediaUtil;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.disposables.Disposable;

import static android.app.Activity.RESULT_CANCELED;

public class UpdateProfileFragment extends Fragment {

    @BindView(R.id.avatar)
    CircleImageView avatar;

    @BindView(R.id.username)
    AppCompatEditText userName;

    @BindView(R.id.password)
    AppCompatEditText password;

    @BindView(R.id.new_password)
    AppCompatEditText newPassword;

    @BindView(R.id.update)
    AppCompatButton update;

    private Disposable disposable;

    private String imageFilePath;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        View view = inflater.inflate(R.layout.fragment_update_profile, container, false);
        ButterKnife.bind(this, view);
        init();
        setRetainInstance(true);
        return view;
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (disposable != null) {
            disposable.dispose();
        }
    }

    @OnClick(R.id.avatar)
    public void onAvatarClick() {
        showPictureMenu();
    }

    @OnClick(R.id.update)
    public void onUpdateClick() {
        updateUser();
    }

    private void init() {
        Bundle receivedBundle;
        try {
            receivedBundle = getArguments();
            imageFilePath = receivedBundle.getString(Constant.PHOTO_URI);
            String name = receivedBundle.getString(Constant.USERNAME);
            Glide.with(getActivity())
                    .load(imageFilePath)
                    .into(avatar);
            userName.setText(name);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void showPictureMenu() {

        PopupMenuBuilder popupMenu = new PopupMenuBuilder(getContext(), avatar, R.menu.picture_select_menu);
        popupMenu.getInstance()
                .setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.camera:
                            imageFilePath = MediaUtil.takePhoto(this);
                            return true;
                        case R.id.gallery:
                            MediaUtil.selectPhotoFromGallery(this);
                            return true;
                        default:
                            return false;
                    }
                });
    }

    private void updateUser() {
        String name, pwd, newPwd;
        name = userName.getText().toString().trim();
        pwd = password.getText().toString().trim();
        newPwd = newPassword.getText().toString().trim();
        if (UserCredential.passwordExists(getActivity(), name, pwd)) {
            HashMap<String, String> values = new HashMap<>();
            values.put(Constant.COLUMN_PHOTO_URI, imageFilePath);
            values.put(Constant.COLUMN_PASSWORD, newPwd.equals("") ? pwd : newPwd);
            UserCredential.updateUserCredentials(getActivity(), name, values);
        } else {
            showErrorDialog(getString(R.string.update_title), getString(R.string.wrong_credentials));
        }
    }

    private void showInformationDialog(String tittle, String message) {
        new InformationDialog(getActivity(), tittle, message)
                .build()
                .setPositiveButton(getString(R.string.ok), (dialog, id) -> dialog.dismiss())
                .show();
    }

    private void showErrorDialog(String tittle, String message) {
        new ErrorDialog(getActivity(), tittle, message)
                .build()
                .setPositiveButton(getString(R.string.close), ((dialog, id) -> dialog.dismiss()))
                .show();
    }


}