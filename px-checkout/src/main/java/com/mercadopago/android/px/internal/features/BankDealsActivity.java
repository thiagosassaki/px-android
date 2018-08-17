package com.mercadopago.android.px.internal.features;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.google.gson.reflect.TypeToken;
import com.mercadopago.android.px.BuildConfig;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.datasource.MercadoPagoServicesAdapter;
import com.mercadopago.android.px.internal.adapters.BankDealsAdapter;
import com.mercadopago.android.px.internal.callbacks.FailureRecovery;
import com.mercadopago.android.px.internal.callbacks.OnSelectedCallback;
import com.mercadopago.android.px.internal.callbacks.TaggedCallback;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.tracker.FlowHandler;
import com.mercadopago.android.px.internal.tracker.MPTrackingContext;
import com.mercadopago.android.px.internal.util.ApiUtil;
import com.mercadopago.android.px.internal.util.ErrorUtil;
import com.mercadopago.android.px.internal.util.JsonUtil;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.model.BankDeal;
import com.mercadopago.android.px.model.ScreenViewEvent;
import com.mercadopago.android.px.tracking.internal.utils.TrackingUtil;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import java.lang.reflect.Type;
import java.util.List;

public class BankDealsActivity extends MercadoPagoActivity implements OnSelectedCallback<BankDeal> {

    // Local vars
    protected MercadoPagoServicesAdapter mMercadoPago;
    protected RecyclerView mRecyclerView;
    protected Toolbar mToolbar;

    protected List<BankDeal> mBankDeals;

    @Override
    protected void onValidStart() {
        final Session session = Session.getSession(this);
        final PaymentSettingRepository paymentSettings = session.getConfigurationModule().getPaymentSettings();

        mMercadoPago = new MercadoPagoServicesAdapter(getActivity(), paymentSettings.getPublicKey(),
            paymentSettings.getPrivateKey());
        trackInitialScreen(paymentSettings.getPublicKey());
        getBankDeals();
    }

    protected void trackInitialScreen(final String publicKey) {
        MPTrackingContext mpTrackingContext = new MPTrackingContext.Builder(this, publicKey)
            .setVersion(BuildConfig.VERSION_NAME)
            .build();
        ScreenViewEvent event = new ScreenViewEvent.Builder()
            .setFlowId(FlowHandler.getInstance().getFlowId())
            .setScreenId(TrackingUtil.SCREEN_ID_BANK_DEALS)
            .setScreenName(TrackingUtil.SCREEN_NAME_BANK_DEALS)
            .build();

        mpTrackingContext.trackEvent(event);
    }

    @Override
    protected void onInvalidStart(String message) {
        ErrorUtil.startErrorActivity(this, message, false);
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.px_activity_bank_deals);
    }

    @Override
    protected void initializeControls() {
        initializeToolbar();
        mRecyclerView = findViewById(R.id.mpsdkBankDealsList);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    }

    @Override
    protected void getActivityParameters() {
        try {
            Type listType = new TypeToken<List<BankDeal>>() {
            }.getType();
            mBankDeals = JsonUtil.getInstance().getGson().fromJson(getIntent().getStringExtra("bankDeals"), listType);
        } catch (Exception ex) {
            mBankDeals = null;
        }
    }

    @Override
    protected void validateActivityParameters() throws IllegalStateException {

    }

    private void initializeToolbar() {
        mToolbar = findViewById(R.id.mpsdkToolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
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

    private void getBankDeals() {
        ViewUtils.showProgressLayout(this);
        mMercadoPago.getBankDeals(new TaggedCallback<List<BankDeal>>(ApiUtil.RequestOrigin.GET_BANK_DEALS) {

            @Override
            public void onSuccess(final List<BankDeal> bankDeals) {
                solveBankDeals(bankDeals);
            }

            @Override
            public void onFailure(final MercadoPagoError error) {
                if (isActivityActive()) {
                    setFailureRecovery(new FailureRecovery() {
                        @Override
                        public void recover() {
                            getBankDeals();
                        }
                    });

                    ErrorUtil.showApiExceptionError(getActivity(),
                        error.getApiException(),
                        ApiUtil.RequestOrigin.GET_BANK_DEALS);
                } else {
                    finishWithCancelResult();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ErrorUtil.ERROR_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                recoverFromFailure();
            } else {
                finishWithCancelResult();
            }
        }
    }

    private void finishWithCancelResult() {
        setResult(RESULT_CANCELED);
        finish();
    }

    protected void solveBankDeals(List<BankDeal> bankDeals) {
        mRecyclerView.setAdapter(new BankDealsAdapter(bankDeals, this));
        ViewUtils.showRegularLayout(getActivity());
    }

    @Override
    public void onSelected(final BankDeal selectedBankDeal) {
        BankDealDetailActivity.startWithBankDealLegals(this, selectedBankDeal);
    }
}
