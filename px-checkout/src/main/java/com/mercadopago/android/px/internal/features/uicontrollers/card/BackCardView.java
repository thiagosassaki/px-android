package com.mercadopago.android.px.internal.features.uicontrollers.card;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.util.MPCardMaskUtil;
import com.mercadopago.android.px.internal.util.ScaleUtil;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.internal.view.MPTextView;
import com.mercadopago.android.px.model.PaymentMethod;

/**
 * Created by vaserber on 10/19/16.
 */

public class BackCardView {

    public static final int CARD_SECURITY_CODE_DEFAULT_LENGTH = 3;

    public static final String BASE_BACK_SECURITY_CODE = "•••";

    public static final int NEUTRAL_CARD_COLOR = R.color.px_white;

    private final Context mContext;
    private View mView;
    private String mSize;

    //Card info
    private PaymentMethod mPaymentMethod;
    private int mSecurityCodeLength;

    //View controls
    private FrameLayout mCardContainer;
    private ImageView mCardBorder;
    private MPTextView mCardSecurityCodeTextView;
    private ImageView mCardImageView;

    public BackCardView(Context context) {
        mContext = context;
        mSecurityCodeLength = CARD_SECURITY_CODE_DEFAULT_LENGTH;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        mPaymentMethod = paymentMethod;
    }

    public void setSize(String size) {
        mSize = size;
    }

    public void setSecurityCodeLength(int securityCodeLength) {
        mSecurityCodeLength = securityCodeLength;
    }

    public void initializeControls() {
        mCardContainer = mView.findViewById(R.id.mpsdkCardBackContainer);
        mCardBorder = mView.findViewById(R.id.mpsdkCardShadowBorder);
        mCardSecurityCodeTextView = mView.findViewById(R.id.mpsdkCardSecurityCodeViewBack);
        mCardImageView = mView.findViewById(R.id.mpsdkCardImageView);
        if (mSize != null) {
            resize();
        }
    }

    private void resize() {
        if (mSize == null) {
            return;
        }
        if (mSize.equals(CardRepresentationModes.MEDIUM_SIZE)) {
            resizeCard(mCardContainer, R.dimen.px_card_size_medium_height, R.dimen.px_card_size_medium_width,
                CardRepresentationModes.CARD_SECURITY_CODE_BACK_SIZE_MEDIUM);
        } else if (mSize.equals(CardRepresentationModes.BIG_SIZE)) {
            resizeCard(mCardContainer, R.dimen.px_card_size_big_height, R.dimen.px_card_size_big_width,
                CardRepresentationModes.CARD_SECURITY_CODE_BACK_SIZE_BIG);
        } else if (mSize.equals(CardRepresentationModes.EXTRA_BIG_SIZE)) {
            resizeCard(mCardContainer, R.dimen.px_card_size_extra_big_height,
                R.dimen.px_card_size_extra_big_width,
                CardRepresentationModes.CARD_SECURITY_CODE_BACK_SIZE_EXTRA_BIG);
        }
    }

    private void resizeCard(ViewGroup cardViewContainer, int cardHeight, int cardWidth, int cardSecurityCodeFontSize) {
        ViewUtils.resizeViewGroupLayoutParams(cardViewContainer, cardHeight, cardWidth, mContext);
        mCardSecurityCodeTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, cardSecurityCodeFontSize);
    }

    public View inflateInParent(ViewGroup parent, boolean attachToRoot) {
        mView = LayoutInflater.from(mContext)
            .inflate(R.layout.px_card_back, parent, attachToRoot);
        return mView;
    }

    public View getView() {
        return mView;
    }

    public void decorateCardBorder(int borderColor) {
        GradientDrawable cardShadowRounded =
            (GradientDrawable) ContextCompat.getDrawable(mContext, R.drawable.px_card_shadow_rounded);
        cardShadowRounded.setStroke(ScaleUtil.getPxFromDp(6, mContext), borderColor);
        mCardBorder.setImageDrawable(cardShadowRounded);
    }

    public void draw() {
        showEmptySecurityCode();
        if (mPaymentMethod == null) {
            return;
        }
        onPaymentMethodSet();
    }

    public void hide() {
        mCardContainer.setVisibility(View.GONE);
    }

    public void show() {
        mCardContainer.setVisibility(View.VISIBLE);
    }

    public void clearPaymentMethod() {
        mCardImageView.setBackgroundColor(ContextCompat.getColor(mContext, NEUTRAL_CARD_COLOR));
        drawEditingSecurityCode(null);
    }

    private void onPaymentMethodSet() {
        mCardImageView.setBackgroundColor(ContextCompat.getColor(mContext, getCardColor(mPaymentMethod)));
    }

    private int getCardColor(PaymentMethod paymentMethod) {
        String colorName = "px_" + paymentMethod.getId().toLowerCase();
        int color = mContext.getResources().getIdentifier(colorName, "color", mContext.getPackageName());
        if (color == 0) {
            color = NEUTRAL_CARD_COLOR;
        }
        return color;
    }

    public void drawEditingSecurityCode(String securityCode) {
        if (securityCode == null || securityCode.length() == 0) {
            mCardSecurityCodeTextView.setText(BASE_BACK_SECURITY_CODE);
        } else {
            mCardSecurityCodeTextView.setText(MPCardMaskUtil.buildSecurityCode(mSecurityCodeLength, securityCode));
        }
    }

    private void showEmptySecurityCode() {
        mCardSecurityCodeTextView.setText(BASE_BACK_SECURITY_CODE);
    }
}
