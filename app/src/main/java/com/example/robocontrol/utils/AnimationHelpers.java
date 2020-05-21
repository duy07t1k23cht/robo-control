package com.example.robocontrol.utils;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;

import androidx.annotation.Nullable;

/**
 * Created by Duy M. Nguyen on 5/14/2020.
 */
public class AnimationHelpers {

    private final static int DEFAULT_DURATION = 200;

    public static void rotate(View view, float fromDegree, float toDegree) {

        RotateAnimation rotate = new RotateAnimation(fromDegree, toDegree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(DEFAULT_DURATION);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setFillAfter(true);

        view.startAnimation(rotate);

    }

    public static void leftIn(View view, float fromXDelta) {
        TranslateAnimation translateAnimation = new TranslateAnimation(fromXDelta, 0, 0, 0);
        translateAnimation.setDuration(DEFAULT_DURATION);
        view.startAnimation(translateAnimation);
    }

    public static void fadeIn(final View view, final @Nullable Callbacks.VoidCallback callback) {
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(DEFAULT_DURATION);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.VISIBLE);
                if (callback != null) {
                    callback.execute();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        view.startAnimation(fadeIn);
    }

    public static void fadeOut(final View view, final @Nullable Callbacks.VoidCallback callback) {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
        fadeOut.setStartOffset(DEFAULT_DURATION);
        fadeOut.setDuration(DEFAULT_DURATION);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.INVISIBLE);
                if (callback != null) {
                    callback.execute();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        view.startAnimation(fadeOut);
    }
}
