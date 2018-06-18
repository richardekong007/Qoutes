package com.richydave.qoutes.activity;

import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.richydave.qoutes.R;
import com.richydave.qoutes.fragments.LocalQuotesFragment;
import com.richydave.qoutes.fragments.MakeQuoteFragment;
import com.richydave.qoutes.fragments.OnlineQuotesFragment;
import com.richydave.qoutes.util.FragmentUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

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
    public void onBackPressed(){
        super.onBackPressed();
    }

    private void placeFirstFragment() {
        FragmentUtil.replaceFragment(getSupportFragmentManager(), new OnlineQuotesFragment(), null, false);
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
                        System.exit(0);
                        return true;
                }

                return true;
            }
        });
    }
}
