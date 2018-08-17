package com.mercadopago.android.px.internal.features;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.google.gson.reflect.TypeToken;
import com.mercadopago.android.px.BuildConfig;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.adapters.PayerCostsAdapter;
import com.mercadopago.android.px.internal.callbacks.OnCodeDiscountCallback;
import com.mercadopago.android.px.internal.callbacks.OnSelectedCallback;
import com.mercadopago.android.px.internal.callbacks.RecyclerItemClickListener;
import com.mercadopago.android.px.internal.controllers.CheckoutTimer;
import com.mercadopago.android.px.internal.di.ConfigurationModule;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.internal.features.codediscount.CodeDiscountDialog;
import com.mercadopago.android.px.internal.features.providers.InstallmentsProviderImpl;
import com.mercadopago.android.px.internal.features.uicontrollers.FontCache;
import com.mercadopago.android.px.internal.features.uicontrollers.card.CardRepresentationModes;
import com.mercadopago.android.px.internal.features.uicontrollers.card.FrontCardView;
import com.mercadopago.android.px.internal.repository.DiscountRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.tracker.FlowHandler;
import com.mercadopago.android.px.internal.tracker.MPTrackingContext;
import com.mercadopago.android.px.internal.util.ErrorUtil;
import com.mercadopago.android.px.internal.util.JsonUtil;
import com.mercadopago.android.px.internal.util.ScaleUtil;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.internal.view.AmountView;
import com.mercadopago.android.px.internal.view.DiscountDetailDialog;
import com.mercadopago.android.px.internal.view.MPTextView;
import com.mercadopago.android.px.model.CardInfo;
import com.mercadopago.android.px.model.Discount;
import com.mercadopago.android.px.model.PayerCost;
import com.mercadopago.android.px.model.ScreenViewEvent;
import com.mercadopago.android.px.model.Site;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.preferences.PaymentPreference;
import com.mercadopago.android.px.tracking.internal.utils.TrackingUtil;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

public class InstallmentsActivity extends MercadoPagoBaseActivity
        implements InstallmentsActivityView, CodeDiscountDialog.DiscountListener {

    protected InstallmentsPresenter presenter;

    //Local vars
    protected boolean mActivityActive;

    //View controls
    protected PayerCostsAdapter mPayerCostsAdapter;
    protected RecyclerView mInstallmentsRecyclerView;

    //ViewMode
    protected boolean mLowResActive;

    //Low Res View
    protected Toolbar mLowResToolbar;
    protected MPTextView mLowResTitleToolbar;

    //Normal View
    protected CollapsingToolbarLayout mCollapsingToolbar;
    protected AppBarLayout mAppBar;
    protected FrameLayout mCardContainer;
    protected Toolbar mNormalToolbar;
    protected FrontCardView mFrontCardView;
    protected MPTextView mTimerTextView;

    private MPTextView mNoInstallmentsRateTextView;
    private LinearLayout mNoInstallmentsRate;

    private AmountView amountView;
    private PaymentSettingRepository configuration;
    private OnCodeDiscountCallback onCodeDiscountCallback;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Session session = Session.getSession(this);
        final ConfigurationModule configurationModule = session.getConfigurationModule();
        configuration = configurationModule.getPaymentSettings();
        presenter = new InstallmentsPresenter(session.getAmountRepository(), configuration,
            configurationModule.getUserSelectionRepository(),
            session.getDiscountRepository());

        getActivityParameters();
        presenter.attachView(this);
        presenter.attachResourcesProvider(new InstallmentsProviderImpl(this));

        mActivityActive = true;
        analyzeLowRes();
        setContentView();
        initializeControls();
        initializeView();

        presenter.initialize();
    }

    private void getActivityParameters() {
        final Intent intent = getIntent();

        presenter.setCardInfo(JsonUtil.getInstance().fromJson(intent.getStringExtra("cardInfo"), CardInfo.class));

        List<PayerCost> payerCosts;
        try {
            final Type listType = new TypeToken<List<PayerCost>>() {
            }.getType();
            payerCosts = JsonUtil.getInstance().getGson().fromJson(intent.getStringExtra("payerCosts"), listType);
        } catch (final Exception ex) {
            payerCosts = null;
        }

        presenter.setPayerCosts(payerCosts);
        presenter.setPaymentPreference(
            JsonUtil.getInstance().fromJson(intent.getStringExtra("paymentPreference"), PaymentPreference.class));
    }

    public void analyzeLowRes() {
        if (presenter.isRequiredCardDrawn()) {
            mLowResActive = ScaleUtil.isLowRes(this);
        } else {
            mLowResActive = true;
        }
    }

    public void setContentView() {
        if (mLowResActive) {
            setContentViewLowRes();
        } else {
            setContentViewNormal();
        }
    }

    public void setContentViewLowRes() {
        setContentView(R.layout.px_activity_installments_lowres);
    }

    public void setContentViewNormal() {
        setContentView(R.layout.px_activity_installments_normal);
    }

    private void initializeControls() {
        mInstallmentsRecyclerView = findViewById(R.id.mpsdkActivityInstallmentsView);
        mTimerTextView = findViewById(R.id.mpsdkTimerTextView);

        amountView = findViewById(R.id.amount_view);

        if (mLowResActive) {
            initializeLowResControls();
        } else {
            initializeNormalControls();
        }
    }

    private void initializeLowResControls() {
        mLowResToolbar = findViewById(R.id.mpsdkRegularToolbar);
        mLowResTitleToolbar = findViewById(R.id.mpsdkTitle);

        if (CheckoutTimer.getInstance().isTimerEnabled()) {
            Toolbar.LayoutParams marginParams =
                new Toolbar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            marginParams.setMargins(0, 0, 0, 6);
            mLowResTitleToolbar.setLayoutParams(marginParams);
            mLowResTitleToolbar.setTextSize(19);
            mTimerTextView.setTextSize(17);
        }

        mLowResToolbar.setVisibility(View.VISIBLE);
    }

    private void initializeNormalControls() {
        mCollapsingToolbar = findViewById(R.id.mpsdkCollapsingToolbar);
        mAppBar = findViewById(R.id.mpsdkInstallmentesAppBar);
        mCardContainer = findViewById(R.id.mpsdkActivityCardContainer);
        mNormalToolbar = findViewById(R.id.mpsdkRegularToolbar);
        mNormalToolbar.setVisibility(View.VISIBLE);
    }

    private void initializeView() {
        loadViews();
        hideHeader();
        showTimer();
    }

    protected void trackScreen() {
        final String publicKey = Session.getSession(this).getConfigurationModule().getPaymentSettings().getPublicKey();
        final MPTrackingContext mTrackingContext = new MPTrackingContext.Builder(this, publicKey)
            .setVersion(BuildConfig.VERSION_NAME)
            .build();

        ScreenViewEvent event = new ScreenViewEvent.Builder()
            .setFlowId(FlowHandler.getInstance().getFlowId())
            .setScreenId(TrackingUtil.SCREEN_ID_INSTALLMENTS)
            .setScreenName(TrackingUtil.SCREEN_NAME_CARD_FORM_INSTALLMENTS)
            .addProperty(TrackingUtil.PROPERTY_PAYMENT_METHOD_ID, presenter.getPaymentMethod().getId())
            .build();

        mTrackingContext.trackEvent(event);
    }

    public void loadViews() {
        if (mLowResActive) {
            loadLowResViews();
        } else {
            loadNormalViews();
        }
    }

    @Override
    public void warnAboutBankInterests() {
        mNoInstallmentsRate = findViewById(R.id.mpsdkNoInstallmentsRate);
        mNoInstallmentsRate.setVisibility(View.VISIBLE);
        mNoInstallmentsRateTextView = findViewById(R.id.mpsdkNoInstallmentsRateTextView);
        mNoInstallmentsRateTextView.setVisibility(View.VISIBLE);
        mNoInstallmentsRateTextView.setText(R.string.px_interest_label);
    }

    public void loadLowResViews() {
        loadToolbarArrow(mLowResToolbar);
        mLowResTitleToolbar.setText(getString(R.string.px_card_installments_title));

        if (FontCache.hasTypeface(FontCache.CUSTOM_REGULAR_FONT)) {
            mLowResTitleToolbar.setTypeface(FontCache.getTypeface(FontCache.CUSTOM_REGULAR_FONT));
        }
    }

    public void loadNormalViews() {
        loadToolbarArrow(mNormalToolbar);
        mNormalToolbar.setTitle(getString(R.string.px_card_installments_title));
        setCustomFontNormal();

        mFrontCardView = new FrontCardView(this, CardRepresentationModes.SHOW_FULL_FRONT_ONLY);
        mFrontCardView.setSize(CardRepresentationModes.MEDIUM_SIZE);
        mFrontCardView.setPaymentMethod(presenter.getPaymentMethod());
        if (presenter.getCardInfo() != null) {
            mFrontCardView.setCardNumberLength(presenter.getCardNumberLength());
            mFrontCardView.setLastFourDigits(presenter.getCardInfo().getLastFourDigits());
        }
        mFrontCardView.inflateInParent(mCardContainer, true);
        mFrontCardView.initializeControls();
        mFrontCardView.draw();
        mFrontCardView.enableEditingCardNumber();
    }

    private void loadToolbarArrow(final Toolbar toolbar) {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    onBackPressed();
                }
            });
        }
    }

    private void setCustomFontNormal() {
        if (FontCache.hasTypeface(FontCache.CUSTOM_REGULAR_FONT)) {
            mCollapsingToolbar.setCollapsedTitleTypeface(FontCache.getTypeface(FontCache.CUSTOM_REGULAR_FONT));
            mCollapsingToolbar.setExpandedTitleTypeface(FontCache.getTypeface(FontCache.CUSTOM_REGULAR_FONT));
        }
    }

    private void hideHeader() {
        if (mLowResActive) {
            mLowResToolbar.setVisibility(View.GONE);
        } else {
            mNormalToolbar.setTitle("");
        }
    }

    private void showTimer() {
        if (CheckoutTimer.getInstance().isTimerEnabled()) {
            mTimerTextView.setVisibility(View.VISIBLE);
            mTimerTextView.setText(CheckoutTimer.getInstance().getCurrentTime());
        }
    }

    @Override
    public void showHeader() {
        if (mLowResActive) {
            mLowResToolbar.setVisibility(View.VISIBLE);
        } else {
            mNormalToolbar.setTitle(getString(R.string.px_card_installments_title));
            setCustomFontNormal();
        }
    }

    private void initializeAdapter(OnSelectedCallback<Integer> onSelectedCallback) {
        mPayerCostsAdapter = new PayerCostsAdapter(configuration.getCheckoutPreference().getSite(), onSelectedCallback);
        initializeAdapterListener(mPayerCostsAdapter, mInstallmentsRecyclerView);
    }

    private void initializeAdapterListener(RecyclerView.Adapter adapter, RecyclerView view) {
        view.setAdapter(adapter);
        view.setLayoutManager(new LinearLayoutManager(this));
        view.addOnItemTouchListener(new RecyclerItemClickListener(this,
            new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    presenter.onItemSelected(position);
                }
            }));
    }

    @Override
    public void showError(MercadoPagoError error, String requestOrigin) {
        if (error.isApiException()) {
            showApiException(error.getApiException(), requestOrigin);
        } else {
            ErrorUtil.startErrorActivity(this, error);
        }
    }

    public void showApiException(ApiException apiException, String requestOrigin) {
        if (mActivityActive) {
            ErrorUtil.showApiExceptionError(this, apiException, requestOrigin);
        }
    }

    @Override
    public void showInstallments(List<PayerCost> payerCostList, OnSelectedCallback<Integer> onSelectedCallback) {
        //We track after evaluating default installments or autoselected installments
        trackScreen();
        initializeAdapter(onSelectedCallback);
        mPayerCostsAdapter.addResults(payerCostList);
    }

    @Override
    public void showLoadingView() {
        mInstallmentsRecyclerView.setVisibility(View.GONE);
        ViewUtils.showProgressLayout(this);
    }

    @Override
    public void hideLoadingView() {
        mInstallmentsRecyclerView.setVisibility(View.VISIBLE);
        ViewUtils.showRegularLayout(this);
    }

    @Override
    public void finishWithResult(PayerCost payerCost) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("payerCost", JsonUtil.getInstance().toJson(payerCost));
        setResult(RESULT_OK, returnIntent);
        finish();
        animateTransitionSlideInSlideOut();
    }

    public void animateTransitionSlideInSlideOut() {
        overridePendingTransition(R.anim.px_slide_right_to_left_in, R.anim.px_slide_right_to_left_out);
    }

    @Override
    public void onBackPressed() {
        showInstallmentsRecyclerView();
        final Intent returnIntent = new Intent();
            returnIntent.putExtra("backButtonPressed", true);
            setResult(RESULT_CANCELED, returnIntent);
            finish();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ErrorUtil.ERROR_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                presenter.recoverFromFailure();
            } else {
                setResult(resultCode, data);
                finish();
            }
        } else {
            setResult(resultCode, data);
            finish();
        }
    }

    @Override
    public void showInstallmentsRecyclerView() {
        mInstallmentsRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showAmount(@NonNull final DiscountRepository discountRepository,
        @NonNull final BigDecimal itemsPlusCharges, @NonNull final Site site) {
        amountView.setOnClickListener(presenter);
        amountView.show(discountRepository, itemsPlusCharges, site);
    }

    @Override
    public void showDetailDialog() {
        DiscountDetailDialog.showDialog(getSupportFragmentManager());
    }

    @Override
    public void showDiscountInputDialog() {
        CodeDiscountDialog.showDialog(getSupportFragmentManager());
    }

    @Override
    public void onDiscountRetrieved(final OnCodeDiscountCallback onCodeDiscountCallback) {
        this.onCodeDiscountCallback = onCodeDiscountCallback;
        presenter.onDiscountRetrieved();
    }

    @Override
    public void onSuccessCodeDiscountCallback(final Discount discount) {
        if (isCodeDiscountDialogActive()) {
            onCodeDiscountCallback.onSuccess(discount);
        }
    }

    @Override
    public void onFailureCodeDiscountCallback() {
        if (isCodeDiscountDialogActive()) {
            onCodeDiscountCallback.onFailure();
            presenter.initializeAmountRow();
        }
    }

    private boolean isCodeDiscountDialogActive() {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(CodeDiscountDialog.class.getName());
        return fragment != null && fragment.isVisible() && onCodeDiscountCallback != null;
    }
}
