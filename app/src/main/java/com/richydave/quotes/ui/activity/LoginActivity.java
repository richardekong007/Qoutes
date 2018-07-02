package com.richydave.quotes.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.widget.Toast;

import com.richydave.quotes.Constant;
import com.richydave.quotes.R;
import com.richydave.quotes.model.database.UserCredential;
import com.richydave.quotes.ui.Dialogs.ErrorDialog;
import com.richydave.quotes.ui.Dialogs.InformationDialog;
import com.richydave.quotes.ui.Dialogs.QuestionDialog;
import com.richydave.quotes.ui.menu.PopupMenuBuilder;
import com.richydave.quotes.util.MediaUtil;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.disposables.Disposable;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.avatar)
    CircleImageView avatar;

    @BindView(R.id.username)
    AppCompatEditText userName;

    @BindView(R.id.password)
    AppCompatEditText password;

    @BindView(R.id.login)
    AppCompatButton login;

    @BindView(R.id.sign_up)
    AppCompatButton signUp;

    private Disposable disposable;

    private String imageFilePath;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.IMAGE_PICK_CODE && data != null) {
            Uri imageUri = data.getData();
            imageFilePath = MediaUtil.resolveMediaPath(this, imageUri);
            MediaUtil.displayImage(this, disposable, avatar, imageUri);
        }
        if (requestCode == Constant.REQUEST_IMAGE_CAPTURE) {

            MediaUtil.displayImage(this, imageFilePath, avatar);

        } else if (resultCode == RESULT_CANCELED) {
            new QuestionDialog(this, getString(R.string.confirmation_tittle), getString(R.string.camera_cancel))
                    .build()
                    .setPositiveButton(getString(R.string.yes), (dialog, id) -> dialog.dismiss())
                    .setNegativeButton(getString(R.string.no), ((dialog, id) -> {
                        imageFilePath = MediaUtil.takePhoto(this);
                        dialog.dismiss();
                    }))
                    .show();
            Toast.makeText(this, getString(R.string.camera_cancel), Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.login)
    public void onLoginClick() {
        String name = "";
        String pwd = "";
        boolean passwordExists, userNameExists;
        try {
            name = userName.getText().toString().trim();
            pwd = password.getText().toString().trim();
            passwordExists = UserCredential.passwordExist(this, name);
            userNameExists = UserCredential.userNameExist(this, name);
            if (userNameExists && passwordExists) {
                imageFilePath = UserCredential.getPhotoUri(this, name);
                viewOnlineQuotes(name, imageFilePath);
            } else {
                new ErrorDialog(this, getString(R.string.login_error), getString(R.string.wrong_user))
                        .build()
                        .setPositiveButton(getString(R.string.close), (dialog, which) -> dialog.dismiss())
                        .show();
            }
        } catch (NullPointerException e) {
            if (name.equals("") || pwd.equals("")) {
                new ErrorDialog(this, getString(R.string.login_error), getString(R.string.empty_user_credential))
                        .build()
                        .setPositiveButton(getString(R.string.close), ((dialog, which) -> dialog.dismiss()))
                        .show();
            } else {
                new ErrorDialog(this, getString(R.string.login_error), getString(R.string.login_error_message))
                        .build()
                        .setPositiveButton(getString(R.string.close), ((dialog, which) -> dialog.dismiss()))
                        .show();
            }
        }
    }

    @OnClick(R.id.sign_up)
    public void onSignUpClick() {
        String name;
        String pwd;
        name = userName.getText().toString().trim();
        pwd = password.getText().toString().trim();
        //for practical purpose, this is not secure
        if (UserCredential.isCredentialCorrect(this, name, pwd)) {
            UserCredential.saveUserCredential(this, name, imageFilePath, pwd);
            if (UserCredential.wasSave()) {
                viewOnlineQuotes(name, imageFilePath);
                new InformationDialog(this, getString(R.string.signup_success), getString(R.string.signup_success_message))
                        .build()
                        .setPositiveButton(getString(R.string.ok), ((dialog, which) -> dialog.dismiss()))
                        .show();
            }
        }
    }

    @OnClick(R.id.avatar)
    public void onAvatarClick() {
        showPictureMenu();
    }

    private void viewOnlineQuotes(String name, String imageFilePath) {
        try {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra(Constant.USERNAME, name);
            intent.putExtra(Constant.PHOTO_URI, imageFilePath);
            startActivity(intent);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    private void showPictureMenu() {

        PopupMenuBuilder popupMenu = new PopupMenuBuilder(this, avatar, R.menu.picture_select_menu);
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

}
