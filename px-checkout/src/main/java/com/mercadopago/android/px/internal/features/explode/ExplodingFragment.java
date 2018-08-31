package com.mercadopago.android.px.internal.features.explode;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.util.StatusBarDecorator;
import com.mercadopago.android.px.internal.util.TextUtil;

public class ExplodingFragment extends Fragment {

    public interface ExplodingAnimationListener {
        void onAnimationFinished();
    }

    private static final String ARG_EXPLODING_PARAMS = "ARG_EXPLODING_PARAMS";

    private static final int MAX_LOADING_TIME = 20000; // the max loading time in milliseconds
    public static final float ICON_SCALE = 3.0f;

    private ProgressBar progressBar;
    private ObjectAnimator animator;
    private ImageView icon;
    private ImageView circle;
    private View reveal;
    private TextView text;
    private ViewGroup rootView;

    private ExplodeDecorator explodeDecorator;
    private int buttonHeight;
    private int buttonLeftRightMargin;
    private int yButtonPosition;
    private String buttonText;
    //TODO add loading time payment processor
    private int maxLoadingTime;

    public static ExplodingFragment newInstance(final ExplodeParams explodeParams) {
        final ExplodingFragment explodingFragment = new ExplodingFragment();
        final Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_EXPLODING_PARAMS, explodeParams);
        explodingFragment.setArguments(bundle);
        return explodingFragment;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();
        if (args != null) {
            final ExplodeParams explodeParams = (ExplodeParams) args.getSerializable(ARG_EXPLODING_PARAMS);
            if (explodeParams == null) {
                yButtonPosition = 0;
                buttonHeight = (int) getContext().getResources().getDimension(R.dimen.px_m_height);
                buttonLeftRightMargin = (int) getContext().getResources().getDimension(R.dimen.px_s_margin);
                maxLoadingTime = MAX_LOADING_TIME;
            } else {
                yButtonPosition = explodeParams.getyButtonPositionInPixels();
                buttonHeight = explodeParams.getButtonHeightInPixels();
                buttonLeftRightMargin = explodeParams.getButtonLeftRightMarginInPixels();
                buttonText = explodeParams.getButtonText();
                maxLoadingTime = explodeParams.getPaymentTimeout();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container,
        final Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.px_fragment_exploding, container, false);
        circle = rootView.findViewById(R.id.cho_loading_buy_circular);
        icon = rootView.findViewById(R.id.cho_loading_buy_icon);
        reveal = rootView.findViewById(R.id.cho_loading_buy_reveal);
        text = rootView.findViewById(R.id.cho_loading_buy_progress_text);
        if (!TextUtil.isEmpty(buttonText)) {
            text.setText(buttonText);
        }

        // set the initial Y to match the button clicked
        final View loadingContainer = rootView.findViewById(R.id.cho_loading_buy_container);

        progressBar = rootView.findViewById(R.id.cho_loading_buy_progress);

        final RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) progressBar.getLayoutParams();
        layoutParams.height = buttonHeight;
        layoutParams.leftMargin = buttonLeftRightMargin;
        layoutParams.rightMargin = buttonLeftRightMargin;
        progressBar.setLayoutParams(layoutParams);
        adjustHeight(circle);
        adjustHeight(icon);
        loadingContainer.setY(yButtonPosition);

        progressBar.setMax(maxLoadingTime);

        // start loading assuming the worst time possible
        animator = ObjectAnimator.ofInt(progressBar, "progress", 0, maxLoadingTime);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(maxLoadingTime).start();

        return rootView;
    }

    private void adjustHeight(final ImageView view) {
        final ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = buttonHeight;
        params.width = buttonHeight;
        view.setLayoutParams(params);
    }

    /**
     * Notify this view that the loading has finish so as to start the finish anim.
     *
     * @param explodeDecorator information about the order result,
     * useful for styling the view.
     */
    public void finishLoading(@NonNull final ExplodeDecorator explodeDecorator,
        @NonNull final ExplodingAnimationListener listener) {

        this.explodeDecorator = explodeDecorator;
        // now finish the remaining loading progress
        final int progress = progressBar.getProgress();
        animator.cancel();
        animator = ObjectAnimator.ofInt(progressBar, "progress", progress, maxLoadingTime);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(getResources().getInteger(R.integer.px_long_animation_time));

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(final Animator animation) {
                animator.removeListener(this);
                if (isAdded()) {
                    createResultAnim(listener);
                }
            }
        });
        animator.start();
    }

    /**
     * Transform the progress bar into the result icon background.
     * The color and the shape are animated.
     *
     * @param listener
     */
    /* default */ void createResultAnim(final ExplodingAnimationListener listener) {
        @ColorInt
        final int color = ContextCompat.getColor(getContext(), explodeDecorator.getDarkPrimaryColor());
        circle.setColorFilter(color);
        icon.setImageResource(explodeDecorator.getStatusIcon());
        final int duration = getResources().getInteger(R.integer.px_long_animation_time);
        final int initialWidth = progressBar.getWidth();
        final int finalSize = progressBar.getHeight();
        final int initialRadius = getResources().getDimensionPixelOffset(R.dimen.px_xxxs_margin);
        final int finalRadius = finalSize / 2;

        final GradientDrawable initialBg =
            getProgressBarShape(ContextCompat.getColor(getContext(), R.color.ui_action_button_pressed), initialRadius);
        final GradientDrawable finalBg = getProgressBarShape(color, initialRadius);
        final TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[] { initialBg, finalBg });
        progressBar.setProgressDrawable(transitionDrawable);
        transitionDrawable.startTransition(duration);

        final ValueAnimator a = ValueAnimator.ofFloat(0, 1);
        a.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                final float interpolatedTime = animation.getAnimatedFraction();
                final int radius = getNewRadius(interpolatedTime);
                setRadius(initialBg, radius);
                setRadius(finalBg, radius);
                progressBar.getLayoutParams().width = getNewWidth(interpolatedTime);
                progressBar.requestLayout();
            }

            private int getNewRadius(final float t) {
                return initialRadius + (int) ((finalRadius - initialRadius) * t);
            }

            private int getNewWidth(final float t) {
                return initialWidth + (int) ((finalSize - initialWidth) * t);
            }

            private void setRadius(final Drawable bg, final int value) {
                final GradientDrawable layerBg = (GradientDrawable) bg;
                layerBg.setCornerRadius(value);
            }
        });
        a.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(final Animator animation) {
                animation.removeAllListeners();
                ((ValueAnimator) animation).removeAllUpdateListeners();
                if (isAdded()) {
                    createResultIconAnim(listener);
                }
            }
        });
        a.setInterpolator(new DecelerateInterpolator(2f));
        a.setDuration(duration);
        a.start();
        text.setVisibility(View.GONE);
}

    /**
     * @return the shape of the progress bar to transform
     */
    private GradientDrawable getProgressBarShape(int color, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(radius);
        return drawable;
    }

    /**
     * Now that the icon background is visible, animate the icon.
     * The icon will start big and transparent and become small and opaque
     *
     * @param listener
     */
    private void createResultIconAnim(final ExplodingAnimationListener listener) {
        progressBar.setVisibility(View.INVISIBLE);
        icon.setVisibility(View.VISIBLE);
        circle.setVisibility(View.VISIBLE);

        icon.setScaleY(ICON_SCALE);
        icon.setScaleX(ICON_SCALE);
        icon.setAlpha(0f);
        icon.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f)
            .setInterpolator(new DecelerateInterpolator(2f))
            .setDuration(getResources().getInteger(R.integer.px_default_animation_time))
            .setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    animation.removeAllListeners();
                    if (isAdded()) {
                        createCircularReveal(listener);
                    }
                }
            }).start();
    }

    /**
     * Wait so that the icon is visible for a while.. then fill the whole screen with the appropriate color.
     *
     * @param listener
     */
    private void createCircularReveal(final ExplodingAnimationListener listener) {
        // when the icon anim has finished, paint the whole screen with the result color
        final float finalRadius = (float) Math.hypot(rootView.getWidth(), rootView.getHeight());
        final int startRadius = buttonHeight / 2;
        final int cx = (progressBar.getLeft() + progressBar.getRight()) / 2;
        final int cy = (progressBar.getTop() + progressBar.getBottom()) / 2 + yButtonPosition;

        final Animator anim;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            anim = ViewAnimationUtils.createCircularReveal(reveal, cx, cy, startRadius, finalRadius);
        } else {
            anim = ObjectAnimator.ofFloat(reveal, "alpha", 0, 1);
        }
        anim.setDuration(getResources().getInteger(R.integer.px_long_animation_time));
        anim.setStartDelay(getResources().getInteger(R.integer.px_long_animation_time));
        anim.setInterpolator(new AccelerateInterpolator());
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(final Animator animation) {
                if (isAdded()) {
                    circle.setVisibility(View.GONE);
                    icon.setVisibility(View.GONE);
                    reveal.setVisibility(View.VISIBLE);

                    final int startColor = ContextCompat.getColor(getContext(), explodeDecorator.getDarkPrimaryColor());
                    final int endColor = ContextCompat.getColor(getContext(), explodeDecorator.getPrimaryColor());
                    final Drawable[] switchColors =
                        new Drawable[] { new ColorDrawable(startColor), new ColorDrawable(endColor) };
                    TransitionDrawable colorSwitch = new TransitionDrawable(switchColors);
                    reveal.setBackgroundDrawable(colorSwitch);
                    colorSwitch.startTransition((int) animation.getDuration());
                    tintStatusBar(endColor);
                }
            }

            @Override
            public void onAnimationEnd(final Animator animation) {
                animation.removeAllListeners();
                listener.onAnimationFinished();
            }
        });
        anim.start();
    }

    private void tintStatusBar(final int color) {
        new StatusBarDecorator(getActivity().getWindow()).setupStatusBarColor(color);
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        // lock the orientation during the loading
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        } else {
            getActivity().setRequestedOrientation(getResources().getConfiguration().orientation);
        }
    }

    @Override
    public void onDetach() {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        super.onDetach();
    }
}
