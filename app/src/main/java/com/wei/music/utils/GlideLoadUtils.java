package com.wei.music.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.wei.music.App;
import com.wei.music.R;

import org.jetbrains.annotations.NotNull;

import jp.wasabeef.glide.transformations.BlurTransformation;

public final class GlideLoadUtils {

    private GlideLoadUtils() {}

    // ==================== 圆形头像 ====================

    public static void setCircle(@NotNull Context context, @Nullable String url, @NonNull ImageView view) {
        if (!isViewValid(view)) return;

        Glide.with(view)
                .load(url)
                .placeholder(R.drawable.ic_music)
                .error(R.drawable.ic_music)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(RequestOptions.circleCropTransform())  // 官方推荐圆形
                .into(view);
    }

    public static void setCircle(@NotNull Context context, int resId, @NonNull ImageView view) {
        if (!isViewValid(view)) return;

        Glide.with(view)
                .load(resId)
                .placeholder(R.drawable.ic_music_load)
                .error(R.drawable.ic_music_load)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(RequestOptions.circleCropTransform())
                .into(view);
    }

    // ==================== 圆角图 ====================

    public static void setRound(@Nullable Object source, int radiusDp, @NonNull ImageView view) {
        setRound(source, radiusDp, 0, view);
    }

    public static void setRound(@Nullable Object source, int radiusDp, int blurRadius, @NonNull ImageView view) {
        if (!isViewValid(view)) return;

        int radiusPx = dpToPx(view.getContext(), radiusDp);
        MultiTransformation<Bitmap> transformation;

        if (blurRadius > 0) {
            transformation = new MultiTransformation<>(
                    new CenterCrop(),
                    new RoundedCorners(radiusPx),
                    new BlurTransformation(blurRadius)  // 新版只传 radius
            );
        } else {
            transformation = new MultiTransformation<>(
                    new CenterCrop(),
                    new RoundedCorners(radiusPx)
            );
        }

        Glide.with(view)
                .load(source)
                .placeholder(R.drawable.ic_music_load)
                .error(R.drawable.ic_music_load)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(RequestOptions.bitmapTransform(transformation))
                .into(view);
    }

    // ==================== 获取 Bitmap（关键！用 ApplicationContext） ====================

//    private static Context getAppContext() {
//        // 安全获取 Application Context（避免内存泄漏）
//        return App.instance;  // 假设你的 Application 类有静态 getInstance()
//        // 或者直接用你自己的 Application 单例方式
//    }

    public static void loadBitmap(@NotNull Context context, @Nullable String url, @NonNull CustomTarget<Bitmap> target) {
        Glide.with(context)
                .asBitmap()
                .load(url)
                .into(target);
    }

    public static void loadBitmap(@NotNull Context context, @Nullable String url, int blurRadius, @NonNull CustomTarget<Bitmap> target) {
        RequestOptions options = blurRadius > 0
                ? RequestOptions.bitmapTransform(new BlurTransformation(blurRadius))
                : new RequestOptions();

        Glide.with(context)
                .asBitmap()
                .load(url)
                .apply(options)
                .into(target);
    }

    public static void loadBitmap(@NotNull Context context, @Nullable String url, int radiusDp, int blurRadius, @NonNull CustomTarget<Bitmap> target) {
        int radiusPx = dpToPx(context, radiusDp);
        MultiTransformation<Bitmap> trans = blurRadius > 0
                ? new MultiTransformation<>(
                new RoundedCorners(radiusPx),
                new BlurTransformation(blurRadius)
        )
                : new MultiTransformation<>(new RoundedCorners(radiusPx));

        Glide.with(context)
                .asBitmap()
                .load(url)
                .apply(RequestOptions.bitmapTransform(trans))
                .into(target);
    }

    public static void loadBitmap(@NotNull Context context, int resId, int radiusDp, int blurRadius, @NonNull CustomTarget<Bitmap> target) {
        loadBitmap(context,String.valueOf(resId), radiusDp, blurRadius, target);  // resId 会自动处理
    }

    // ==================== 工具方法 ====================

    private static boolean isViewValid(@NonNull ImageView view) {
        //return view.isAttachedToWindow();
        return true;
    }

    private static int dpToPx(Context context, float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    public static Drawable setDrawableColor(Drawable drawable, int colorResId) {
        Drawable wrapped = DrawableCompat.wrap(drawable.mutate());
        DrawableCompat.setTint(wrapped, colorResId);
        return wrapped;
    }

}