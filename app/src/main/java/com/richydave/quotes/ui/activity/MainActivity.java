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
import com.richydave.quotes.ui.fragments.LocalQuotesFragment;
import com.richydave.quotes.ui.fragments.MakeQuoteFragment;
import com.richydave.quotes.ui.fragments.OnlineQuotesFragment;
import com.richydave.quotes.util.FragmentUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer)
    DrawerLayout drawer;

    @BindView(R.id.navigation_view)
    NavigationView navigationView;

    @BindView(R.id.content_frame)
    LinearLayout contentFrame;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
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


    private void placeFirstFragment() {
        FragmentUtil.replaceFragment(getSupportFragmentManager(), new OnlineQuotesFragment(), null, false);
    }

    private void setNavigationHeader() {

        View navigationHeader = navigationView.inflateHeaderView(R.layout.nav_header);
        CircleImageView avatar = navigationHeader.findViewById(R.id.avatar);
        TextView authorName = navigationHeader.findViewById(R.id.author_name);
        try {
            Intent receivedIntent = getIntent();
            String name = receivedIntent.getStringExtra(Constant.USERNAME);
            String photoUri = receivedIntent.getStringExtra(Constant.PHOTO_URI);
            authorName.setText(name);
            Glide.with(this)
                    .load(photoUri)
                    .into(avatar);
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
                        FragmentUtil.replaceFragment(getSupportFragmentManager(), new MakeQuoteFragment(), null, true);
                        drawer.closeDrawers();
                        return true;
                    case R.id.view_online_quotes:
                        FragmentUtil.replaceFragment(getSupportFragmentManager(), new OnlineQuotesFragment(), null, true);
                        drawer.closeDrawers();
                        return true;
                    case R.id.view_saved_quotes:
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