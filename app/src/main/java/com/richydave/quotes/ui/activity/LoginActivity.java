package com.richydave.quotes.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.widget.EditText;
import android.widget.Toast;

import com.richydave.quotes.Constant;
import com.richydave.quotes.R;
import com.richydave.quotes.model.database.UserCredential;
import com.richydave.quotes.ui.AlertDialogs.ErrorAlertDialog;
import com.richydave.quotes.ui.AlertDialogs.InformationDialog;
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
    EditText userName;

    @BindView(R.id.password)
    EditText password;

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
            Toast.makeText(this, getString(R.string.camera_cancel), Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.login)
    public void onLoginClick() {
        String name = "";
        String pwd = "";
        boolean doesPasswordExist, doesUserNameExist;
        try {
            name = userName.getText().toString().trim();
            pwd = password.getText().toString().trim();
            doesPasswordExist = UserCredential.doesPasswordExist(this, name);
            doesUserNameExist = UserCredential.doesUserNameExist(this, name);
            if (doesUserNameExist && doesPasswordExist) {
                viewOnlineQuotes(name);
            } else {
                new ErrorAlertDialog(this, getString(R.string.login_error), getString(R.string.wrong_user))
                        .build().setPositiveButton(getString(R.string.close), (dialog, which) -> dialog.dismiss())
                        .show();
            }
        } catch (NullPointerException e) {
            if (name.equals("") || pwd.equals("")) {
                new ErrorAlertDialog(this, getString(R.string.login_error), getString(R.string.empty_user_credential))
                        .build().setPositiveButton(getString(R.string.close), ((dialog, which) -> dialog.dismiss()))
                        .show();
            } else {
                new ErrorAlertDialog(this, getString(R.string.login_error), getString(R.string.login_error_message))
                        .build().setPositiveButton(getString(R.string.close), ((dialog, which) -> dialog.dismiss()))
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
            UserCredential.saveUserCredential(this, name, pwd);
            if (UserCredential.wasSave()) {
                viewOnlineQuotes(name);
                new InformationDialog(this, getString(R.string.signup_success), getString(R.string.signup_success_message))
                        .build().setPositiveButton(getString(R.string.ok), ((dialog, which) -> dialog.dismiss()))
                        .show();
            }
        }
    }

    @OnClick(R.id.avatar)
    public void onAvatarClick() {
        showPictureMenu();
    }

    private void viewOnlineQuotes(String name) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra(Constant.USERNAME, name);
        if (imageFilePath != null) {
            intent.putExtra(Constant.PHOTO_URI, imageFilePath);
        }
        startActivity(intent);
    }

    private void showPictureMenu() {

        PopupMenuBuilder popupMenu = new PopupMenuBuilder(this, avatar, R.menu.picture_select_menu);
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

    private void selectPhotoFromGallery() {
        if (MediaUtil.isPermissionGranted(this)) {

            Intent selectImage = new Intent();
            selectImage.setType(Constant.IMAGE_CONTENT_TYPE);
            selectImage.setAction(Intent.ACTION_OPEN_DOCUMENT);
            selectImage.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(selectImage, Constant.SELECT_IMAGE_INSTRUCTION),
                    Constant.IMAGE_PICK_CODE);
        }
    }

    private void takePhoto() {
        Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (captureImage.resolveActivity(this.getPackageManager()) != null) {

            File imageFile = null;
            try {
                imageFile = MediaUtil.createImageFile(this);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (imageFile != null) {
                Uri imageUri = FileProvider.getUriForFile(this, getString(R.string.authority), imageFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                imageFilePath = imageFile.getAbsolutePath();
                startActivityForResult(captureImage, Constant.REQUEST_IMAGE_CAPTURE);
            }
        }
    }
}
