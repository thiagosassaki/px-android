package com.mercadopago.android.px.internal.util;

import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import com.mercadopago.android.px.R;
import javax.annotation.Nonnull;

public final class FlipModalAnimationUtil {

    private static final int ANIM_DURATION = 800;
    private static final int HALF_ANIM_DURATION = ANIM_DURATION / 2;
    private static final int FLIP_DEGREES = 180;

    private static final float SCALE_DOWN_FACTOR = 0.80f;
    private static final float EXTRA_TENSION = 1.5f;

    private FlipModalAnimationUtil() {
        // utility class
    }

    /**
     * Starts the flip animation of the views
     *
     * @param containerView Used to add the backView to the hierarchy
     * @param frontView Current visible View.
     * @param backView View to show after the animation.
     */
    public static void flipView(@Nonnull final ViewGroup containerView, @Nonnull final View frontView,
        @Nonnull final View backView) {
        //Add the backView to the hiararchy and make it transparent
        backView.setAlpha(0);
        containerView.addView(backView);
        setCameraDistance(containerView, frontView, backView);

        flipFrontView(containerView, frontView);
        flipBackView(backView);
        scaleDownContainer(containerView);
    }

    private static void setCameraDistance(@Nonnull final ViewGroup containerView, @Nonnull final View frontView,
        @Nonnull final View backView) {
        //Set the camera distance
        final float distance = containerView.getResources().getDimension(R.dimen.px_camera_distance);
        containerView.setCameraDistance(distance);
        frontView.setCameraDistance(distance);
        backView.setCameraDistance(distance);
    }

    private static void flipFrontView(@Nonnull final ViewGroup containerView, @Nonnull final View frontView) {
        containerView.animate().rotationY(-FLIP_DEGREES).setDuration(ANIM_DURATION)
            .setInterpolator(new AnticipateOvershootInterpolator(1, EXTRA_TENSION)).start();
        frontView.animate().alpha(0).setDuration(HALF_ANIM_DURATION)
            .start();
    }

    private static void flipBackView(@Nonnull final View backView) {
        backView.animate().alpha(1).setStartDelay(HALF_ANIM_DURATION).setDuration(HALF_ANIM_DURATION).start();
        backView.animate().rotationY(FLIP_DEGREES).setDuration(0).start();
    }

    private static void scaleDownContainer(@Nonnull final ViewGroup containerView) {
        //Scale down the card a little bit to avoid cut borders
        ViewCompat.animate(containerView).scaleX(SCALE_DOWN_FACTOR).scaleY(SCALE_DOWN_FACTOR)
            .setDuration(HALF_ANIM_DURATION).withEndAction(new Runnable() {
            @Override
            public void run() {
                //Reverse scale
                containerView.animate().scaleX(1).scaleY(1).setDuration(HALF_ANIM_DURATION).start();
            }
        }).start();
    }
}