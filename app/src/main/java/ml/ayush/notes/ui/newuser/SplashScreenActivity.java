package ml.ayush.notes.ui.newuser;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import ml.ayush.notes.R;
import ml.ayush.notes.utils.Constants;

public class SplashScreenActivity extends AppCompatActivity {

    @BindView(R.id.logo)
    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ButterKnife.bind(this);
        setAnimation();
        startTimer();

    }

    private void setAnimation() {
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);
        logo.setAnimation(fadeIn);
    }

    private void startTimer() {
        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(Constants.SPLASH_SCREEN_TIMEOUT);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    nextActivity();
                }
            }
        };
        timer.start();

    }

    private void nextActivity() {
        Intent intent = new Intent(this, OnBoardingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

}
