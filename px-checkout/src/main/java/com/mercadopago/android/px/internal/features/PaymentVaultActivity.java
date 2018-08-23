package com.mercadopago.android.px.internal.features;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.core.PaymentMethodPlugin;
import com.mercadopago.android.px.internal.adapters.PaymentMethodSearchItemAdapter;
import com.mercadopago.android.px.internal.callbacks.OnCodeDiscountCallback;
import com.mercadopago.android.px.internal.callbacks.OnSelectedCallback;
import com.mercadopago.android.px.internal.controllers.CheckoutTimer;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.internal.features.codediscount.CodeDiscountDialog;
import com.mercadopago.android.px.internal.features.codediscount.CodeDiscountDialog.DiscountListener;
import com.mercadopago.android.px.internal.features.hooks.Hook;
import com.mercadopago.android.px.internal.features.hooks.HookActivity;
import com.mercadopago.android.px.internal.features.plugins.PaymentMethodPluginActivity;
import com.mercadopago.android.px.internal.features.providers.PaymentVaultProviderImpl;
import com.mercadopago.android.px.internal.features.uicontrollers.FontCache;
import com.mercadopago.android.px.internal.features.uicontrollers.paymentmethodsearch.PaymentMethodInfoController;
import com.mercadopago.android.px.internal.features.uicontrollers.paymentmethodsearch.PaymentMethodSearchCustomOption;
import com.mercadopago.android.px.internal.features.uicontrollers.paymentmethodsearch.PaymentMethodSearchOption;
import com.mercadopago.android.px.internal.features.uicontrollers.paymentmethodsearch.PaymentMethodSearchViewController;
import com.mercadopago.android.px.internal.features.uicontrollers.paymentmethodsearch.PluginPaymentMethodInfo;
import com.mercadopago.android.px.internal.repository.DiscountRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.repository.PluginRepository;
import com.mercadopago.android.px.internal.util.ErrorUtil;
import com.mercadopago.android.px.internal.util.JsonUtil;
import com.mercadopago.android.px.internal.util.ScaleUtil;
import com.mercadopago.android.px.internal.view.AmountView;
import com.mercadopago.android.px.internal.view.DiscountDetailDialog;
import com.mercadopago.android.px.internal.view.GridSpacingItemDecoration;
import com.mercadopago.android.px.internal.view.MPTextView;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.CustomSearchItem;
import com.mercadopago.android.px.model.Discount;
import com.mercadopago.android.px.model.Issuer;
import com.mercadopago.android.px.model.PayerCost;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentMethodInfo;
import com.mercadopago.android.px.model.PaymentMethodSearchItem;
import com.mercadopago.android.px.model.Site;
import com.mercadopago.android.px.model.Token;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.preferences.PaymentPreference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.mercadopago.android.px.core.MercadoPagoCheckout.EXTRA_ERROR;

public class PaymentVaultActivity extends MercadoPagoBaseActivity
        implements PaymentVaultView, DiscountListener {

    public static final int COLUMN_SPACING_DP_VALUE = 20;
    public static final int COLUMNS = 2;
    private static final int PAYER_INFORMATION_REQUEST_CODE = 22;

    // Local vars
    protected boolean mActivityActive;
    protected Token mToken;
    protected Issuer mSelectedIssuer;
    protected PayerCost mSelectedPayerCost;
    protected Card mSelectedCard;
    protected Context mContext;

    // Controls
    protected RecyclerView mSearchItemsRecyclerView;
    protected AppBarLayout mAppBar;

    protected PaymentVaultPresenter presenter;
    protected CollapsingToolbarLayout mAppBarLayout;
    protected MPTextView mTimerTextView;

    protected View mProgressLayout;

    private AmountView amountView;
    private OnCodeDiscountCallback onCodeDiscountCallback;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Session session = Session.getSession(this);
        final PaymentSettingRepository configuration = session.getConfigurationModule().getPaymentSettings();
        presenter = new PaymentVaultPresenter(configuration,
            session.getConfigurationModule().getUserSelectionRepository(),
            session.getPluginRepository(),
            session.getDiscountRepository(),
            session.getGroupsRepository());

        getActivityParameters();
        configurePresenter();
        setContentView();
        initializeControls();
        cleanPaymentMethodOptions();

        //Avoid automatic selection if activity restored on back pressed from next step
        initialize();
    }

    private void configurePresenter() {
        presenter.attachView(this);
        presenter.attachResourcesProvider(new PaymentVaultProviderImpl(getApplicationContext()));
    }

    protected void setContentView() {
        setContentView(R.layout.px_activity_payment_vault);
    }

    protected void getActivityParameters() {
        final Intent intent = getIntent();

        final JsonUtil instance = JsonUtil.getInstance();

        if (intent.getStringExtra("selectedSearchItem") != null) {
            presenter.setSelectedSearchItem(instance
                .fromJson(intent.getStringExtra("selectedSearchItem"), PaymentMethodSearchItem.class));
        }
    }

    protected void initializeControls() {
        mTimerTextView = findViewById(R.id.mpsdkTimerTextView);

        amountView = findViewById(R.id.amount_view);
        mProgressLayout = findViewById(R.id.mpsdkProgressLayout);

        initializePaymentOptionsRecyclerView();
        mAppBar = findViewById(R.id.mpsdkAppBar);
        mAppBarLayout = findViewById(R.id.mpsdkCollapsingToolbar);
        initializeToolbar();
    }

    protected void initialize() {
        showTimer();
        presenter.initialize();
    }

    private void showTimer() {
        if (CheckoutTimer.getInstance().isTimerEnabled()) {
            mTimerTextView.setVisibility(View.VISIBLE);
            mTimerTextView.setText(CheckoutTimer.getInstance().getCurrentTime());
        }
    }

    private void initializeToolbar() {
        final Toolbar toolbar = findViewById(R.id.mpsdkToolbar);
        setSupportActionBar(toolbar);
        final ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayShowTitleEnabled(false);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (FontCache.hasTypeface(FontCache.CUSTOM_REGULAR_FONT)) {
            mAppBarLayout.setCollapsedTitleTypeface(FontCache.getTypeface(FontCache.CUSTOM_REGULAR_FONT));
            mAppBarLayout.setExpandedTitleTypeface(FontCache.getTypeface(FontCache.CUSTOM_REGULAR_FONT));
        }
    }

    protected void initializePaymentOptionsRecyclerView() {
        int columns = COLUMNS;
        mSearchItemsRecyclerView = findViewById(R.id.mpsdkGroupsList);
        mSearchItemsRecyclerView.setLayoutManager(new GridLayoutManager(this, columns));
        mSearchItemsRecyclerView.addItemDecoration(
            new GridSpacingItemDecoration(columns, ScaleUtil.getPxFromDp(COLUMN_SPACING_DP_VALUE, this), true));
        PaymentMethodSearchItemAdapter groupsAdapter = new PaymentMethodSearchItemAdapter();
        mSearchItemsRecyclerView.setAdapter(groupsAdapter);
    }

    protected void populateSearchList(List<PaymentMethodSearchItem> items,
        OnSelectedCallback<PaymentMethodSearchItem> onSelectedCallback) {
        PaymentMethodSearchItemAdapter adapter = (PaymentMethodSearchItemAdapter) mSearchItemsRecyclerView.getAdapter();
        List<PaymentMethodSearchViewController> customViewControllers =
            createSearchItemsViewControllers(items, onSelectedCallback);
        adapter.addItems(customViewControllers);
        adapter.notifyItemInserted();
    }

    @Deprecated
    private void populateCustomOptionsList(List<CustomSearchItem> customSearchItems,
        OnSelectedCallback<CustomSearchItem> onSelectedCallback) {
        PaymentMethodSearchItemAdapter adapter = (PaymentMethodSearchItemAdapter) mSearchItemsRecyclerView.getAdapter();
        List<PaymentMethodSearchViewController> customViewControllers =
            createCustomSearchItemsViewControllers(customSearchItems, onSelectedCallback);
        adapter.addItems(customViewControllers);
        adapter.notifyItemInserted();
    }

    private List<PaymentMethodSearchViewController> createSearchItemsViewControllers(
        List<PaymentMethodSearchItem> items, final OnSelectedCallback<PaymentMethodSearchItem> onSelectedCallback) {
        final List<PaymentMethodSearchViewController> customViewControllers = new ArrayList<>();
        for (final PaymentMethodSearchItem item : items) {
            PaymentMethodSearchViewController viewController = new PaymentMethodSearchOption(this, item);
            viewController.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSelectedCallback.onSelected(item);
                }
            });
            customViewControllers.add(viewController);
        }
        return customViewControllers;
    }

    @Deprecated
    private List<PaymentMethodSearchViewController> createCustomSearchItemsViewControllers(
        final List<CustomSearchItem> customSearchItems, final OnSelectedCallback<CustomSearchItem> onSelectedCallback) {
        final List<PaymentMethodSearchViewController> customViewControllers = new ArrayList<>();
        for (final CustomSearchItem item : customSearchItems) {
            final PaymentMethodSearchCustomOption viewController = new PaymentMethodSearchCustomOption(this, item);
            viewController.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    onSelectedCallback.onSelected(item);
                }
            });

            customViewControllers.add(viewController);
        }
        return customViewControllers;
    }

    private List<PaymentMethodSearchViewController> createPluginItemsViewControllers(
            final List<PaymentMethodInfo> infoItems) {
        final PluginRepository pluginRepository = Session.getSession(this).getPluginRepository();
        final List<PaymentMethodSearchViewController> controllers = new ArrayList<>();
        for (final PaymentMethodInfo infoItem : infoItems) {
            final PaymentMethodPlugin plugin = pluginRepository.getPlugin(infoItem.getId());
            if (plugin.isEnabled()) {
                final PluginPaymentMethodInfo pluginPaymentMethodInfo = new PluginPaymentMethodInfo(infoItem);
                final PaymentMethodSearchViewController viewController =
                    new PaymentMethodInfoController(this, pluginPaymentMethodInfo);
                viewController.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        final String id = String.valueOf(v.getTag());
                        presenter.selectPluginPaymentMethod(pluginRepository.getPlugin(id));
                    }
                });
                controllers.add(viewController);
            }
        }
        return controllers;
    }

    @Override
    public void showPaymentMethodPluginActivity() {
        startActivityForResult(PaymentMethodPluginActivity.getIntent(this),
            MercadoPagoComponents.Activities.PLUGIN_PAYMENT_METHOD_REQUEST_CODE);
        overrideTransitionIn();
    }

    @Override
    public void showSelectedItem(PaymentMethodSearchItem item) {
        final Intent intent = new Intent(this, PaymentVaultActivity.class);
        intent.putExtras(getIntent());
        intent.putExtra("selectedSearchItem", JsonUtil.getInstance().toJson(item));
        startActivityForResult(intent, MercadoPagoComponents.Activities.PAYMENT_VAULT_REQUEST_CODE);
        overridePendingTransition(R.anim.px_slide_right_to_left_in, R.anim.px_slide_right_to_left_out);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        presenter.initializeAmountRow();
        if (requestCode == MercadoPagoComponents.Activities.CARD_VAULT_REQUEST_CODE) {
            resolveCardRequest(resultCode, data);
        } else if (requestCode == MercadoPagoComponents.Activities.PAYMENT_METHODS_REQUEST_CODE) {
            presenter.onPaymentMethodReturned();
        } else if (requestCode == MercadoPagoComponents.Activities.PAYMENT_VAULT_REQUEST_CODE) {
            resolvePaymentVaultRequest(resultCode, data);
        } else if (requestCode == PAYER_INFORMATION_REQUEST_CODE) {
            resolvePayerInformationRequest(resultCode);
        } else if (requestCode == MercadoPagoComponents.Activities.PLUGIN_PAYMENT_METHOD_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                presenter.onPluginAfterHookOne();
            } else {
                overrideTransitionOut();
            }
        } else if (requestCode == MercadoPagoComponents.Activities.HOOK_1) {
            resolveHook1Request(resultCode);
        } else if (requestCode == MercadoPagoComponents.Activities.HOOK_1_PLUGIN) {
            presenter.onPluginHookOneResult();
        } else if (requestCode == ErrorUtil.ERROR_REQUEST_CODE) {
            resolveErrorRequest(resultCode, data);
            overrideTransitionOut();
        }
    }

    private void resolveErrorRequest(final int resultCode, final Intent data) {
        presenter.onHookReset();
        if (resultCode == RESULT_OK) {
            recoverFromFailure();
        } else if (presenter.isItemSelected()) {
            hideProgress();
        } else {
            setResult(resultCode, data);
            finish();
        }
    }

    private void recoverFromFailure() {
        presenter.recoverFromFailure();
    }

    private void resolvePaymentVaultRequest(final int resultCode, final Intent data) {
        presenter.onHookReset();

        if (resultCode == RESULT_OK) {
            setResult(RESULT_OK, data);
            finish();
        } else if (resultCode == RESULT_CANCELED && data != null && data.hasExtra(EXTRA_ERROR)) {
            setResult(Activity.RESULT_CANCELED, data);
            finish();
        } else {
            //When it comes back from payment vault "children" view
            presenter.trackInitialScreen();

            if (shouldFinishOnBack(data)) {
                setResult(Activity.RESULT_CANCELED, data);
                finish();
            }
        }
    }

    protected void resolveCardRequest(final int resultCode, final Intent data) {
        presenter.onHookReset();

        if (resultCode == RESULT_OK) {
            showProgress();

            mToken = JsonUtil.getInstance().fromJson(data.getStringExtra("token"), Token.class);
            mSelectedIssuer = JsonUtil.getInstance().fromJson(data.getStringExtra("issuer"), Issuer.class);
            mSelectedPayerCost = JsonUtil.getInstance().fromJson(data.getStringExtra("payerCost"), PayerCost.class);
            mSelectedCard = JsonUtil.getInstance().fromJson(data.getStringExtra("card"), Card.class);
            finishWithCardResult();
        } else {
            presenter.trackChildrenScreen();

            if (shouldFinishOnBack(data)) {
                setResult(Activity.RESULT_CANCELED, data);
                finish();
            } else {
                overridePendingTransition(R.anim.px_slide_left_to_right_in, R.anim.px_slide_left_to_right_out);
            }
        }
    }

    private void resolvePayerInformationRequest(final int resultCode) {
        presenter.onHookReset();
        if (resultCode == RESULT_OK) {
            presenter.onPayerInformationReceived();
        } else {
            overridePendingTransition(R.anim.px_slide_left_to_right_in, R.anim.px_slide_left_to_right_out);
        }
    }

    private boolean shouldFinishOnBack(final Intent data) {
        return !Session.getSession(this).getPluginRepository().hasEnabledPaymentMethodPlugin() &&
                (presenter.getSelectedSearchItem() != null &&
                        (!presenter.getSelectedSearchItem().hasChildren()
                                || (presenter.getSelectedSearchItem().getChildren().size() == 1))
                        || (presenter.getSelectedSearchItem() == null &&
                        presenter.isOnlyOneItemAvailable()) ||
                        (data != null) && (data.getStringExtra(EXTRA_ERROR) != null));
    }

    @Override
    public void cleanPaymentMethodOptions() {
        final PaymentMethodSearchItemAdapter adapter =
            (PaymentMethodSearchItemAdapter) mSearchItemsRecyclerView.getAdapter();
        adapter.clear();
    }

    @Override
    public void finishPaymentMethodSelection(final PaymentMethod paymentMethod) {
        finishWith(paymentMethod);
    }

    private void finishWith(final PaymentMethod paymentMethod) {
        final Intent returnIntent = new Intent();
        returnIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        finishWithResult(returnIntent);
    }

    protected void finishWithCardResult() {
        final Intent returnIntent = new Intent();
        returnIntent.putExtra("token", JsonUtil.getInstance().toJson(mToken));
        if (mSelectedIssuer != null) {
            returnIntent.putExtra("issuer", JsonUtil.getInstance().toJson(mSelectedIssuer));
        }
        returnIntent.putExtra("payerCost", JsonUtil.getInstance().toJson(mSelectedPayerCost));
        returnIntent.putExtra("card", JsonUtil.getInstance().toJson(mSelectedCard));
        finishWithResult(returnIntent);
    }

    private void finishWithResult(final Intent returnIntent) {
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
        overrideTransitionIn();
    }

    @Override
    public void showProgress() {
        mProgressLayout.setVisibility(View.VISIBLE);
        mAppBar.setVisibility(View.GONE);
    }

    @Override
    public void hideProgress() {
        mProgressLayout.setVisibility(View.GONE);
        mAppBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void setTitle(final String title) {
        if (mAppBarLayout != null) {
            mAppBarLayout.setTitle(title);
        }
    }

    @Override
    public void startSavedCardFlow(final Card card) {
        new MercadoPagoComponents.Activities.CardVaultActivityBuilder()
            .setCard(card)
            .startActivity(this, MercadoPagoComponents.Activities.CARD_VAULT_REQUEST_CODE);
        overrideTransitionIn();
    }

    @Override
    public void startCardFlow(final Boolean automaticSelection) {
        new MercadoPagoComponents.Activities.CardVaultActivityBuilder()
            .setAutomaticSelection(automaticSelection)
            .startActivity(this, MercadoPagoComponents.Activities.CARD_VAULT_REQUEST_CODE);
        overrideTransitionIn();
    }

    @Override
    public void startPaymentMethodsSelection(final PaymentPreference paymentPreference) {
        new MercadoPagoComponents.Activities.PaymentMethodsActivityBuilder()
            .setActivity(this)
            .setPaymentPreference(paymentPreference)
            .startActivity();
    }

    public void showApiException(final ApiException apiException, final String requestOrigin) {
        if (mActivityActive) {
            ErrorUtil.showApiExceptionError(this, apiException, requestOrigin);
        }
    }

    @Deprecated
    @Override
    public void showCustomOptions(List<CustomSearchItem> customSearchItems,
        OnSelectedCallback<CustomSearchItem> customSearchItemOnSelectedCallback) {
        populateCustomOptionsList(customSearchItems, customSearchItemOnSelectedCallback);
    }

    @Override
    public void showSearchItems(List<PaymentMethodSearchItem> searchItems,
        OnSelectedCallback<PaymentMethodSearchItem> paymentMethodSearchItemSelectionCallback) {
        populateSearchList(searchItems, paymentMethodSearchItemSelectionCallback);
    }

    @Override
    public void showPluginOptions(@NonNull final Collection<PaymentMethodPlugin> items,
        final PaymentMethodPlugin.PluginPosition position) {

        final List<PaymentMethodInfo> toInsert = new ArrayList<>();

        for (final PaymentMethodPlugin plugin : items) {
            if (position == plugin.getPluginPosition()) {
                toInsert.add(plugin.getPaymentMethodInfo(this));
            }
        }

        final PaymentMethodSearchItemAdapter adapter =
            (PaymentMethodSearchItemAdapter) mSearchItemsRecyclerView.getAdapter();
        final List<PaymentMethodSearchViewController> customViewControllers =
            createPluginItemsViewControllers(toInsert);
        adapter.addItems(customViewControllers);
        adapter.notifyItemInserted();
    }

    @Override
    public void showError(MercadoPagoError error, String requestOrigin) {
        if (error.isApiException()) {
            showApiException(error.getApiException(), requestOrigin);
        } else {
            ErrorUtil.startErrorActivity(this, error);
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
        overrideTransitionOut();
    }

    @Override
    protected void onResume() {
        mActivityActive = true;
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mActivityActive = false;
        presenter.detachView();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        mActivityActive = false;
        super.onPause();
    }

    @Override
    protected void onStop() {
        mActivityActive = false;
        super.onStop();
    }

    @Override
    public void collectPayerInformation() {
        overrideTransitionIn();
        PayerInformationActivity.start(this, PAYER_INFORMATION_REQUEST_CODE);
    }

    //### HOOKS ######################

    public void resolveHook1Request(final int resultCode) {
        if (resultCode == RESULT_OK) {
            presenter.onHookContinue();
        } else {
            overrideTransitionOut();
            presenter.onHookReset();
        }
    }

    @Override
    public void showHook(final Hook hook, final int code) {
        startActivityForResult(HookActivity.getIntent(this, hook), code);
        overrideTransitionIn();
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
    public void showAmount(@NonNull final DiscountRepository discountRepository,
        @NonNull final BigDecimal totalAmount,
        @NonNull final Site site) {
        amountView.setOnClickListener(presenter);
        amountView.show(discountRepository, totalAmount, site);
    }

    @Override
    public void onDiscountRetrieved(final OnCodeDiscountCallback onCodeDiscountCallback) {
        this.onCodeDiscountCallback = onCodeDiscountCallback;
        cleanPaymentMethodOptions();
        presenter.initPaymentVaultFlow();
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
        return isCodeDiscountDialogAvailable() && onCodeDiscountCallback != null;
    }

    private boolean isCodeDiscountDialogAvailable() {
        return getCodeDiscountDialogInstance() != null && getCodeDiscountDialogInstance().isVisible();
    }

    private Fragment getCodeDiscountDialogInstance() {
        return getSupportFragmentManager().findFragmentByTag(CodeDiscountDialog.class.getName());
    }
}
