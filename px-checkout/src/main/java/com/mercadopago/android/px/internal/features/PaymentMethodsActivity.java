package com.mercadopago.android.px.internal.features;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.adapters.PaymentMethodsAdapter;
import com.mercadopago.android.px.internal.features.providers.PaymentMethodsProvider;
import com.mercadopago.android.px.internal.features.providers.PaymentMethodsProviderImpl;
import com.mercadopago.android.px.internal.util.ErrorUtil;
import com.mercadopago.android.px.internal.util.JsonUtil;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.preferences.PaymentPreference;
import java.lang.reflect.Type;
import java.util.List;

public class PaymentMethodsActivity extends MercadoPagoBaseActivity implements PaymentMethodsView {

    protected RecyclerView mRecyclerView;
    protected Toolbar mToolbar;
    protected TextView mBankDealsTextView;
    protected TextView mTitle;

    private PaymentMethodsPresenter mPresenter;
    private PaymentMethodsProvider mResourcesProvider;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPresenter = new PaymentMethodsPresenter();

        try {
            getActivityParameters();
            mResourcesProvider = new PaymentMethodsProviderImpl(this);
            onValidStart();
        } catch (final IllegalStateException exception) {
            onInvalidStart(exception.getMessage());
        }
    }

    protected void getActivityParameters() {

        PaymentPreference paymentPreference =
            JsonUtil.getInstance().fromJson(getIntent().getStringExtra("paymentPreference"), PaymentPreference.class);
        mPresenter.setPaymentPreference(paymentPreference);

        Boolean showBankDeals = getIntent().getBooleanExtra("showBankDeals", true);
        mPresenter.setShowBankDeals(showBankDeals);

        if (getIntent().getStringExtra("supportedPaymentTypes") != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<String>>() {
            }.getType();

            List<String> supportedPaymentTypes =
                gson.fromJson(getIntent().getStringExtra("supportedPaymentTypes"), listType);
            mPresenter.setSupportedPaymentTypes(supportedPaymentTypes);
        }
    }

    protected void setContentView() {
        setContentView(R.layout.px_activity_payment_methods);
    }

    protected void initializeControls() {
        mRecyclerView = findViewById(R.id.mpsdkPaymentMethodsList);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        // Set a linear layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        initializeToolbar();
    }

    protected void onValidStart() {
        mPresenter.attachView(this);
        mPresenter.attachResourcesProvider(mResourcesProvider);

        setContentView();
        initializeControls();
        mPresenter.start();
    }

    protected void onInvalidStart(String message) {
        ErrorUtil.startErrorActivity(this, message, false);
    }

    private void initializeToolbar() {

        mToolbar = findViewById(R.id.mpsdkToolbar);
        mBankDealsTextView = findViewById(R.id.mpsdkBankDeals);
        mTitle = findViewById(R.id.mpsdkToolbarTitle);

        setSupportActionBar(mToolbar);
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayShowTitleEnabled(false);
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("backButtonPressed", true);
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ErrorUtil.ERROR_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                recoverFromFailure();
            } else {
                setResult(resultCode, data);
                finish();
            }
        }
    }

    protected void recoverFromFailure() {
        mPresenter.recoverFromFailure();
    }

    @Override
    public void showPaymentMethods(List<PaymentMethod> paymentMethods) {
        mRecyclerView.setAdapter(new PaymentMethodsAdapter(this, paymentMethods, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Return to parent
                Intent returnIntent = new Intent();
                PaymentMethod selectedPaymentMethod = (PaymentMethod) view.getTag();
                returnIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(selectedPaymentMethod));
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        }));
    }

    @Override
    public void showProgress() {
        ViewUtils.showProgressLayout(this);
    }

    @Override
    public void hideProgress() {
        ViewUtils.showRegularLayout(this);
    }

    @Override
    public void showError(MercadoPagoError exception) {
        ErrorUtil.startErrorActivity(this, exception);
    }

    @Override
    public void showBankDeals() {
        mBankDealsTextView.setVisibility(View.VISIBLE);
        mBankDealsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MercadoPagoComponents.Activities.BankDealsActivityBuilder()
                    .setActivity(PaymentMethodsActivity.this)
                    .startActivity();
            }
        });
    }
}

