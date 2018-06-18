package com.richydave.quotes.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.richydave.quotes.R;
import com.richydave.quotes.ui.menu.CustomPopupMenu;

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

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.fragment_make_quote, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.author_avatar)
    public void onAvatarClick() {
        showPictureMenu();
    }

    @OnClick(R.id.post)
    public void onPostClick() {

    }

    @OnClick(R.id.save)
    public void onSaveClick() {

    }

    private void showPictureMenu() {

        CustomPopupMenu popupMenu = new CustomPopupMenu(getContext(), avatar, R.menu.picture_select_menu);
        popupMenu.createMenu()
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
    }

    private void takePhoto() {
    }


}
