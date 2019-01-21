package io.givedirect.givedirectpos.view.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import io.givedirect.givedirectpos.R;
import io.givedirect.givedirectpos.view.home.HomeActivity;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_SCREEN_DURATION = 2000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        scheduleTransition();
    }

    private void scheduleTransition() {
        new Handler().postDelayed(() -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }, SPLASH_SCREEN_DURATION);
    }
}
