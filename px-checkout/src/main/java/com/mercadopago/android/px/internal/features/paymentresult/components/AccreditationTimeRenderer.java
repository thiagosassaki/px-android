package com.mercadopago.android.px.internal.features.paymentresult.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.util.BitmapUtils;
import com.mercadopago.android.px.internal.util.ScaleUtil;
import com.mercadopago.android.px.internal.view.MPTextView;
import com.mercadopago.android.px.internal.view.Renderer;
import com.mercadopago.android.px.internal.view.RendererFactory;
import java.util.List;

/**
 * Created by vaserber on 11/13/17.
 */

public class AccreditationTimeRenderer extends Renderer<AccreditationTime> {

    @Override
    public View render(final AccreditationTime component, final Context context, final ViewGroup parent) {

        final View accreditationTimeView = inflate(R.layout.px_accreditation_time, parent);
        final MPTextView messageTextView = accreditationTimeView.findViewById(R.id.mpsdkAccreditationTimeMessage);
        final ViewGroup accreditationCommentsContainer =
            accreditationTimeView.findViewById(R.id.mpsdkAccreditationTimeComments);

        renderAccreditationMessage(messageTextView, component, context);

        if (component.hasAccreditationComments()) {
            renderAccreditationComments(accreditationCommentsContainer, component, context);
        }

        return accreditationTimeView;
    }

    private void renderAccreditationMessage(@NonNull final MPTextView messageTextView,
        @NonNull final AccreditationTime component,
        @NonNull final Context context) {

        String accreditationMessage = component.props.accreditationMessage;
        if (accreditationMessage == null || accreditationMessage.isEmpty()) {
            messageTextView.setVisibility(View.GONE);
        } else {
            SpannableStringBuilder textspan = new SpannableStringBuilder("  " + accreditationMessage);

            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.px_time);
            Bitmap resizedBitmap = BitmapUtils.scaleDown(bitmap, ScaleUtil.getPxFromDp(13, context), true);
            Drawable drawable = new BitmapDrawable(context.getResources(), resizedBitmap);
            PorterDuff.Mode mode = PorterDuff.Mode.SRC_ATOP;
            drawable.setColorFilter(ContextCompat.getColor(context, R.color.px_warm_grey_with_alpha), mode);

            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

            textspan.setSpan(new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            messageTextView.setText(textspan);
            messageTextView.setVisibility(View.VISIBLE);
        }
    }

    private void renderAccreditationComments(@NonNull final ViewGroup parent,
        @NonNull final AccreditationTime component,
        @NonNull final Context context) {

        List<AccreditationComment> commentComponents = component.getAccreditationCommentComponents();

        for (AccreditationComment commentComp : commentComponents) {
            final Renderer commentRenderer = RendererFactory.create(context, commentComp);
            final View accreditationComment = commentRenderer.render();
            parent.addView(accreditationComment);
        }
    }
}
