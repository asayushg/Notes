package ml.ayush.notes.ui.newuser;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;
import ml.ayush.notes.R;
import ml.ayush.notes.adapters.OnBoardingAdapter;
import ml.ayush.notes.ui.home.HomeActivity;
import ml.ayush.notes.ui.profile.ProfileActivity;

public class OnBoardingActivity extends AppCompatActivity {

    @BindView(R.id.viewpager)
    ViewPager mSlide;
    @BindView(R.id.dots_layout)
    LinearLayout dotsLayout;
    private OnBoardingAdapter onBoardingAdapter;
    private TextView[] dots;
    @BindView(R.id.prev_btn)
    Button Prev;
    @BindView(R.id.next_btn)
    Button Next;
    private int currentPage = 0;
    private SharedPreferences sp;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);
        ButterKnife.bind(this);
        sp = getSharedPreferences("OnBoarding", MODE_PRIVATE);
        onBoardingAdapter = new OnBoardingAdapter(this);

        mSlide.setAdapter(onBoardingAdapter);

        addDotsIndicator();
        mSlide.addOnPageChangeListener(viewListener);

        addClickListnerToBtn();
    }


    private void addDotsIndicator() {
        dotsLayout.removeAllViews();
        dots = new TextView[3];
        for (int i = 0; i < 3; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(50);
            dots[i].setTextColor(getBaseContext().getColor(R.color.colorPrimary));
            dotsLayout.addView(dots[i]);
        }
        //activeDot(0);
    }

    private void activeDot(int position) {
        dots[0].setTextColor(getBaseContext().getColor(R.color.grey));
        dots[1].setTextColor(getBaseContext().getColor(R.color.grey));
        dots[2].setTextColor(getBaseContext().getColor(R.color.grey));
        dots[position].setTextColor(getBaseContext().getColor(R.color.colorPrimaryDark));
    }


    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            activeDot(position);
            currentPage = position;
            if (currentPage == 0) {
                Prev.setEnabled(false);
                Prev.setVisibility(View.INVISIBLE);
            } else if (currentPage == 1) {
                Prev.setEnabled(true);
                Prev.setVisibility(View.VISIBLE);
                Next.setEnabled(true);
                Next.setVisibility(View.VISIBLE);
                Next.setText(getResources().getString(R.string.next));
            } else {
                Next.setEnabled(true);
                Next.setVisibility(View.VISIBLE);
                Next.setText(getResources().getString(R.string.finish));
            }

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


    private void addClickListnerToBtn() {
        Next.setOnClickListener(v -> {
            if (currentPage == 2) {
                showDialog();
            }
            mSlide.setCurrentItem(currentPage + 1);
        });

        Prev.setOnClickListener(v -> mSlide.setCurrentItem(currentPage - 1));

    }

    private void nextActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void SignInActivity() {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void showDialog() {
        sp.edit().putBoolean("firstTime", false).apply();
        new MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.signin))
                .setMessage(getString(R.string.sign_dialog_msg))
                .setCancelable(false)
                .setNeutralButton(getString(R.string.sign_in_later), ((dialog, which) -> nextActivity()))
                .setPositiveButton(getString(R.string.sign_in_now), ((dialog, which) -> SignInActivity()))
                .show();
    }
}
