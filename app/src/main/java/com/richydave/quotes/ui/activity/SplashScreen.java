package com.richydave.quotes.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Handler;

import com.richydave.quotes.R;

public class SplashScreen extends AppCompatActivity {

    private static final int DURATION = 3000;

    public void onCreate(Bundle saveInstanceState) {

        super.onCreate(saveInstanceState);
        setContentView(R.layout.splash_screen);
        runSplash();
    }

    private void runSplash() {
        new Handler().postDelayed(() -> {
            Intent splashIntent = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(splashIntent);
            finish();
        }, DURATION);

    }
}
