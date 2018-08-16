package com.mercadopago.android.px.internal.features;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.tracker.Tracker;
import com.mercadopago.android.px.internal.util.ErrorUtil;
import com.mercadopago.android.px.internal.util.TextUtil;

public class TermsAndConditionsActivity extends MercadoPagoActivity {

    public static final String EXTRA_URL = "extra_url";
    public static final String EXTRA_PUBLIC_KEY = "extra_public_key";

    protected View mMPTermsAndConditionsView;
    protected WebView mTermsAndConditionsWebView;
    protected ViewGroup mProgressLayout;
    protected Toolbar mToolbar;
    protected TextView mTitle;

    private String url;
    private String publicKey;

    public static void start(final Context context, final String url, final String publicKey) {
        Intent intent = new Intent(context, TermsAndConditionsActivity.class);
        intent.putExtra(EXTRA_URL, url);
        intent.putExtra(EXTRA_PUBLIC_KEY, publicKey);
        context.startActivity(intent);
    }

    @Override
    protected void getActivityParameters() {
        url = getIntent().getStringExtra(EXTRA_URL);
        publicKey = getIntent().getStringExtra(EXTRA_PUBLIC_KEY);
    }

    @Override
    protected void validateActivityParameters() throws IllegalStateException {
        if (TextUtil.isEmpty(url)) {
            throw new IllegalStateException("no site provided");
        }
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.px_activity_terms_and_conditions);
    }

    @Override
    protected void initializeControls() {
        mProgressLayout = findViewById(R.id.mpsdkProgressLayout);
        mMPTermsAndConditionsView = findViewById(R.id.mpsdkMPTermsAndConditions);
        mTermsAndConditionsWebView = findViewById(R.id.mpsdkTermsAndConditionsWebView);
        mTermsAndConditionsWebView.setVerticalScrollBarEnabled(true);
        mTermsAndConditionsWebView.setHorizontalScrollBarEnabled(true);
        initializeToolbar();
    }

    private void initializeToolbar() {
        mToolbar = findViewById(R.id.mpsdkToolbar);
        setSupportActionBar(mToolbar);
        mTitle = findViewById(R.id.mpsdkTitle);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onValidStart() {
        Tracker.trackDiscountTermsAndConditions(getApplicationContext(), publicKey);
        showMPTermsAndConditions();
    }

    @Override
    protected void onInvalidStart(String message) {
        ErrorUtil.startErrorActivity(this, getString(R.string.px_standard_error_message), message, false);
    }

    private void showMPTermsAndConditions() {
        mProgressLayout.setVisibility(View.VISIBLE);
        mTermsAndConditionsWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                mProgressLayout.setVisibility(View.GONE);
                mMPTermsAndConditionsView.setVisibility(View.VISIBLE);
            }
        });

        mTermsAndConditionsWebView.loadUrl(url);
    }
}
