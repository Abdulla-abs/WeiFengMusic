package com.wei.music.view;

import android.widget.TextView;

import androidx.viewpager2.widget.ViewPager2;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

public class MainPagerCallback extends ViewPager2.OnPageChangeCallback{
    private final List<String> titles = Arrays.asList("Home", "More", "About");
    private final WeakReference<TextView> animationView;

    public MainPagerCallback(TextView animationView) {
        this.animationView = new WeakReference<>(animationView);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        super.onPageScrolled(position, positionOffset, positionOffsetPixels);
        TextView view = animationView.get();
        
        if (positionOffset <= 0.5) {
            view.setText(titles.get(position));
            view.setAlpha(1 - positionOffset * 2);
        } else {
            if (position < 2) {
                view.setText(titles.get(position + 1));
            } else {
                view.setText(titles.get(position));
            }
            view.setAlpha(positionOffset * 2 - 1);
        }
    }
}
