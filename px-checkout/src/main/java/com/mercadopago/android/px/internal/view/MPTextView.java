package com.mercadopago.android.px.internal.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.features.uicontrollers.FontCache;

public class MPTextView extends AppCompatTextView {

    public static final String LIGHT = "light";
    public static final String REGULAR = "regular";
    public static final String MONO_REGULAR = "mono_regular";
    public static final String BOLD = "bold";

    private String mFontStyle;

    public MPTextView(Context context) {
        this(context, null);
    }

    public MPTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MPTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        readAttr(context, attrs);

        if (!isInEditMode()) {
            configureStyle();
        }
    }

    public void setFontStyle(final String fontStyle) {
        mFontStyle = fontStyle;
        configureStyle();
    }

    private void configureStyle() {
        final Typeface tf = getCustomTypeface();
        final int style = getStyle();

        if (tf != null) {
            setTypeface(tf, style);
        }
    }

    private int getStyle() {
        return isBoldFontStyle() ? Typeface.BOLD : Typeface.NORMAL;
    }

    private Typeface getCustomTypeface() {
        Typeface customFont = null;

        if (isLightFontStyle() && FontCache.hasTypeface(FontCache.CUSTOM_LIGHT_FONT)) {
            customFont = FontCache.getTypeface(FontCache.CUSTOM_LIGHT_FONT);
        } else if (isMonoRegularFontStyle() && FontCache.hasTypeface(FontCache.CUSTOM_MONO_FONT)) {
            customFont = FontCache.getTypeface(FontCache.CUSTOM_MONO_FONT);
        } else if ((isRegularFontStyle() || isBoldFontStyle()) &&
            FontCache.hasTypeface(FontCache.CUSTOM_REGULAR_FONT)) {
            customFont = FontCache.getTypeface(FontCache.CUSTOM_REGULAR_FONT);
        }

        return customFont;
    }

    private boolean isBoldFontStyle() {
        return BOLD.equals(mFontStyle);
    }

    private boolean isLightFontStyle() {
        return LIGHT.equals(mFontStyle);
    }

    private boolean isRegularFontStyle() {
        return REGULAR.equals(mFontStyle);
    }

    private boolean isMonoRegularFontStyle() {
        return MONO_REGULAR.equals(mFontStyle);
    }

    private void readAttr(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MPTextView);
        mFontStyle = a.getString(R.styleable.MPTextView_font_style);
        if (mFontStyle == null) {
            mFontStyle = REGULAR;
        }
        a.recycle();
    }
}
