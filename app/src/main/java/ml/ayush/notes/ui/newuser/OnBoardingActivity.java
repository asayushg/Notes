package ml.ayush.notes.ui.newuser;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;
import ml.ayush.notes.R;
import ml.ayush.notes.adapters.OnBoardingAdapter;
import ml.ayush.notes.ui.home.HomeActivity;

public class OnBoardingActivity extends AppCompatActivity {

    @BindView(R.id.viewpager)  ViewPager mSlide;
    @BindView(R.id.dots_layout) LinearLayout dotsLayout;
    private OnBoardingAdapter onBoardingAdapter;
    private TextView[] dots;
    @BindView(R.id.prev_btn) Button Prev;
    @BindView(R.id.next_btn) Button Next;
    private int currentPage = 0;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);
        ButterKnife.bind(this);

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
            dots[i].setTextColor(getBaseContext().getColor(R.color.grey));
            dotsLayout.addView(dots[i]);
        }
        //activeDot(0);
    }

    private void activeDot(int position) {
        dots[0].setTextColor(getBaseContext().getColor(R.color.grey));
        dots[1].setTextColor(getBaseContext().getColor(R.color.grey));
        dots[2].setTextColor(getBaseContext().getColor(R.color.grey));
        dots[position].setTextColor(getBaseContext().getColor(R.color.colorPrimary));
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
        Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage == 2) {
                    nextActivity();
                }
                mSlide.setCurrentItem(currentPage + 1);
            }
        });

        Prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlide.setCurrentItem(currentPage - 1);
            }
        });

    }

    private void nextActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
