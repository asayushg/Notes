package ml.ayush.notes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import ml.ayush.notes.R;

public class OnBoardingAdapter extends PagerAdapter {

    private Context context;

    public OnBoardingAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.onboarding_layout, container, false);
        ImageView sliderImage = view.findViewById(R.id.on_board_img);
        TextView sliderTitle = view.findViewById(R.id.on_board_title);
        TextView sliderDes = view.findViewById(R.id.on_board_des);

        sliderImage.setImageResource(images[position]);
        sliderTitle.setText(titles[position]);
        sliderDes.setText(des[position]);
        container.addView(view);

        return view;
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }

    public int[] images = {
            R.mipmap.boarding_1,
            R.mipmap.boarding_2,
            R.mipmap.boarding_3
    };

    public String[] titles = {
            "Take Notes",
            "Not Just Texts",
            "Stay Organised"
    };

    public String[] des = {
            "Take notes and access them from\nAnywhere. Anytime",
            "Add images, audio recording or videos\nto your notes",
            "Group your notes.\nAdd due date and reminder for Task"
    };

}
