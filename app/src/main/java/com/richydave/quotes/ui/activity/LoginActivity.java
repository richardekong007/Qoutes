package com.richydave.quotes.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;

import com.richydave.quotes.Constant;
import com.richydave.quotes.R;
import com.richydave.quotes.model.database.UserCredential;
import com.richydave.quotes.ui.Dialogs.ErrorDialog;
import com.richydave.quotes.ui.Dialogs.InformationDialog;
import com.richydave.quotes.ui.Dialogs.QuestionDialog;
import com.richydave.quotes.ui.menu.PopupMenuBuilder;
import com.richydave.quotes.util.MediaUtil;

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
        }
        if (resultCode == RESULT_CANCELED) {
            requestImageRecapture(requestCode);
        }
    }

    @OnClick(R.id.login)
    public void onLoginClick() {
        login();
    }

    @OnClick(R.id.sign_up)
    public void onSignUpClick() {
        signUp();
    }

    @OnClick(R.id.avatar)
    public void onAvatarClick() {
        showPictureMenu();
    }

    private void recaptureImage(DialogInterface.OnClickListener action) {
        new QuestionDialog(this, getString(R.string.confirmation_tittle), getString(R.string.camera_cancel))
                .build()
                .setPositiveButton(getString(R.string.yes), (dialog, id) -> dialog.dismiss())
                .setNegativeButton(getString(R.string.no), action)
                .show();
    }

    private void showError(Context context, String title, String message) {
        new ErrorDialog(context, title, message)
                .build()
                .setPositiveButton(getString(R.string.close), ((dialog, which) -> dialog.dismiss()))
                .show();
    }

    private void requestImageRecapture(int requestCode) {
        if (requestCode == Constant.REQUEST_IMAGE_CAPTURE) {
            recaptureImage(((dialog, id) -> {
                imageFilePath = MediaUtil.takePhoto(this);
                dialog.dismiss();
            }));
        } else {
            recaptureImage(((dialog, id) -> {
                MediaUtil.selectPhotoFromGallery(this);
                dialog.dismiss();
            }));
        }
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

    private void signUp() {
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

    private void login() {
        String name = "";
        String pwd = "";
        boolean passwordExists, userNameExists;
        try {
            boolean userNameEmpty = userName.getText().toString().equals("");
            boolean passwordEmpty = password.getText().toString().equals("");
            if (!(userNameEmpty || passwordEmpty)) {

                name = userName.getText().toString().trim();
                pwd = password.getText().toString().trim();
                passwordExists = UserCredential.passwordExists(this, name, pwd);
                userNameExists = UserCredential.userNameExist(this, name);
                if ((userNameExists && passwordExists)) {
                    if ((UserCredential.getDbPassword(this, name).equals(pwd))) {
                        imageFilePath = UserCredential.getPhotoUri(this, name);
                        viewOnlineQuotes(name, imageFilePath);
                    } else {
                        showError(this, getString(R.string.login_error), getString(R.string.wrong_password));
                    }
                } else {
                    showError(this, getString(R.string.login_error), getString(R.string.wrong_user));
                }
            } else {
                showError(this, getString(R.string.login_error), getString(R.string.provide_uname_password));
            }
        } catch (NullPointerException e) {
            if (name.equals("") || pwd.equals("")) {
                showError(this, getString(R.string.login_error), getString(R.string.empty_user_credential));
            } else {
                showError(this, getString(R.string.login_error), getString(R.string.login_error_message));
            }
        }
    }
}