package com.wei.music.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.wei.music.databinding.LayoutPlaybarBinding;

public class PlayBarTransformer extends CustomTarget<Bitmap> {
    
    private final LayoutPlaybarBinding binding;

    public PlayBarTransformer(LayoutPlaybarBinding binding) {
        this.binding = binding;
    }

    @Override
    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
        int[] colors = ColorUtil.getColor(resource);
        GradientDrawable mGroupDrawable = (GradientDrawable) binding.playbarRoot.getBackground();
        mGroupDrawable.setColor(colors[1]);

        binding.playbarTitle.setTextColor(colors[0]);
        binding.playbarPause.setColorFilter(colors[0]);
        binding.playbarList.setColorFilter(colors[0]);
    }

    @Override
    public void onLoadCleared(@Nullable Drawable placeholder) {

    }
}
