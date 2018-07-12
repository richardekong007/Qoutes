package com.richydave.quotes.ui.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.richydave.quotes.Constant;
import com.richydave.quotes.R;
import com.richydave.quotes.interfaces.UpdateProfileListener;
import com.richydave.quotes.model.database.UserCredential;
import com.richydave.quotes.ui.fragments.LocalQuotesFragment;
import com.richydave.quotes.ui.fragments.MakeQuoteFragment;
import com.richydave.quotes.ui.fragments.OnlineQuotesFragment;
import com.richydave.quotes.ui.fragments.UpdateProfileFragment;
import com.richydave.quotes.util.FragmentUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements UpdateProfileListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer)
    DrawerLayout drawer;

    @BindView(R.id.navigation_view)
    NavigationView navigationView;

    @BindView(R.id.content_frame)
    LinearLayout contentFrame;

    private CircleImageView avatar;

    private TextView authorName;

    private Intent receivedIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        UserCredential.setUpdateProfileListener(this);

        setNavigationHeader();

        setNavigationItemSelectedListener();

        placeFirstFragment();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onUpdateProfile(UserCredential userRecord) {
        try {
            Glide.with(this)
                    .load(userRecord.getPhotoUri())
                    .into(avatar);
            authorName.setText(userRecord.getUserName());

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void placeFirstFragment() {
        FragmentUtil.replaceFragment(getSupportFragmentManager(), new OnlineQuotesFragment(), null, false);
    }

    private void setNavigationHeader() {

        View navigationHeader = navigationView.inflateHeaderView(R.layout.nav_header);
        avatar = navigationHeader.findViewById(R.id.avatar);
        authorName = navigationHeader.findViewById(R.id.author_name);
        try {
            receivedIntent = getIntent();
            String name = receivedIntent.getStringExtra(Constant.COLUMN_USERNAME);
            String photoUri = receivedIntent.getStringExtra(Constant.PHOTO_URI);
            authorName.setText(name);
            Glide.with(this)
                    .load(photoUri)
                    .into(avatar);
            navigationHeader.setOnClickListener(listener -> {
                Bundle args = new Bundle();
                args.putString(Constant.USERNAME, name);
                args.putString(Constant.PHOTO_URI, photoUri);
                FragmentUtil.replaceFragment(getSupportFragmentManager(), new UpdateProfileFragment(), args, true);
                drawer.closeDrawers();
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void setNavigationItemSelectedListener() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.make_quote:
                        Bundle args = new Bundle();
                        String photoUri = receivedIntent.getStringExtra(Constant.PHOTO_URI);
                        String userName = receivedIntent.getStringExtra(Constant.USERNAME);
                        args.putString(Constant.PHOTO_URI, photoUri);
                        args.putString(Constant.USERNAME, userName);

                        FragmentUtil.replaceFragment(getSupportFragmentManager(), new MakeQuoteFragment(), args, true);
                        drawer.closeDrawers();
                        return true;
                    case R.id.view_online_quotes:
                        FragmentUtil.replaceFragment(getSupportFragmentManager(), new OnlineQuotesFragment(), null, true);
                        drawer.closeDrawers();
                        return true;
                    case R.id.view_local_quotes:

                        FragmentUtil.replaceFragment(getSupportFragmentManager(), new LocalQuotesFragment(), null, true);
                        drawer.closeDrawers();
                        return true;
                    case R.id.exit:
                        drawer.closeDrawers();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        return true;
                }

                return true;
            }
        });
    }
}