package com.mercadopago.android.px;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import com.mercadopago.android.px.core.CheckoutStore;
import com.mercadopago.android.px.core.MercadoPagoComponents;
import com.mercadopago.android.px.hooks.Hook;
import com.mercadopago.android.px.hooks.HookActivity;
import com.mercadopago.android.px.internal.di.ConfigurationModule;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.Issuer;
import com.mercadopago.android.px.model.Payer;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentRecovery;
import com.mercadopago.android.px.model.PaymentResult;
import com.mercadopago.android.px.model.Token;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.onetap.OneTapFragment;
import com.mercadopago.android.px.plugins.BusinessPaymentResultActivity;
import com.mercadopago.android.px.plugins.PaymentProcessorPluginActivity;
import com.mercadopago.android.px.plugins.model.BusinessPayment;
import com.mercadopago.android.px.plugins.model.BusinessPaymentModel;
import com.mercadopago.android.px.preferences.PaymentPreference;
import com.mercadopago.android.px.presenters.CheckoutPresenter;
import com.mercadopago.android.px.providers.CheckoutProvider;
import com.mercadopago.android.px.providers.CheckoutProviderImpl;
import com.mercadopago.android.px.review_and_confirm.ReviewAndConfirmActivity;
import com.mercadopago.android.px.review_and_confirm.ReviewAndConfirmBuilder;
import com.mercadopago.android.px.tracker.FlowHandler;
import com.mercadopago.android.px.tracker.MPTrackingContext;
import com.mercadopago.android.px.tracking.model.ActionEvent;
import com.mercadopago.android.px.tracking.tracker.MPTracker;
import com.mercadopago.android.px.tracking.utils.TrackingUtil;
import com.mercadopago.android.px.util.ErrorUtil;
import com.mercadopago.android.px.util.JsonUtil;
import com.mercadopago.android.px.util.MercadoPagoESCImpl;
import com.mercadopago.android.px.util.TextUtils;
import com.mercadopago.android.px.util.ViewUtils;
import com.mercadopago.android.px.viewmodel.CardPaymentModel;
import com.mercadopago.android.px.viewmodel.CheckoutStateModel;
import com.mercadopago.android.px.viewmodel.OneTapModel;
import com.mercadopago.android.px.views.CheckoutView;
import com.squareup.picasso.Picasso;
import java.math.BigDecimal;

import static com.mercadopago.android.px.core.MercadoPagoCheckout.EXTRA_ERROR;
import static com.mercadopago.android.px.core.MercadoPagoCheckout.EXTRA_PAYMENT_RESULT;
import static com.mercadopago.android.px.core.MercadoPagoCheckout.PAYMENT_RESULT_CODE;
import static com.mercadopago.android.px.plugins.model.ExitAction.EXTRA_CLIENT_RES_CODE;

public class CheckoutActivity extends MercadoPagoBaseActivity implements CheckoutView, OneTapFragment.CallBack {

    private static final int PLUGIN_PAYMENT_PROCESSOR_REQUEST_CODE = 200;
    private static final int BUSINESS_REQUEST_CODE = 400;

    private static final String EXTRA_ISSUER = "issuer";
    private static final String EXTRA_TOKEN = "token";

    private static final String EXTRA_PAYMENT_METHOD_CHANGED = "paymentMethodChanged";
    private static final String EXTRA_NEXT_ACTION = "nextAction";
    private static final String EXTRA_RESULT_CODE = "resultCode";

    private static final String EXTRA_CARD = "card";
    private static final String EXTRA_PAYER = "payer";

    private static final String EXTRA_PERSISTENT_DATA = "extra_persistent_data";
    private static final String EXTRA_PRIVATE_KEY = "extra_private_key";
    private static final String EXTRA_PUBLIC_KEY = "extra_public_key";

    private CheckoutPresenter presenter;
    private String merchantPublicKey;
    private String privateKey;
    private Intent customDataBundle;

    public static Intent getIntent(@NonNull final Context context) {
        final Intent checkoutIntent = new Intent(context, CheckoutActivity.class);
        return checkoutIntent;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.px_activity_checkout);
        if (savedInstanceState == null) {
            presenter = getActivityParameters();
            configurePresenter();
            presenter.initialize();
        }
    }

    private void configurePresenter() {
        final CheckoutProvider provider = new CheckoutProviderImpl(this,
                merchantPublicKey,
                privateKey,
                new MercadoPagoESCImpl(this, presenter.isESCEnabled()));

        presenter.attachResourcesProvider(provider);
        presenter.attachView(this);
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        // To avoid reload from server the checkout preference.
        outState.putSerializable(EXTRA_PERSISTENT_DATA, presenter.getState());
        outState.putString(EXTRA_PRIVATE_KEY, privateKey);
        outState.putString(EXTRA_PUBLIC_KEY, merchantPublicKey);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            final Session session = Session.getSession(this);
            final ConfigurationModule configurationModule = session.getConfigurationModule();
            presenter =
                    new CheckoutPresenter((CheckoutStateModel) savedInstanceState.getSerializable(EXTRA_PERSISTENT_DATA),
                        configurationModule.getPaymentSettings(), session.getAmountRepository(),
                        configurationModule.getUserSelectionRepository(),
                        session.getDiscountRepository(),
                        session.getGroupsRepository(),
                        session.getPluginRepository());
            privateKey = savedInstanceState.getString(EXTRA_PRIVATE_KEY);
            merchantPublicKey = savedInstanceState.getString(EXTRA_PUBLIC_KEY);
            configurePresenter();
            presenter.initialize();
        }
    }

    protected CheckoutPresenter getActivityParameters() {

        final Session session = Session.getSession(this);
        final ConfigurationModule configurationModule = session.getConfigurationModule();
        final PaymentSettingRepository configuration = configurationModule.getPaymentSettings();

        privateKey = configuration.getPrivateKey();

        final CheckoutStateModel
            persistentData = new CheckoutStateModel();

        merchantPublicKey = configuration.getPublicKey();

        return new CheckoutPresenter(persistentData,
            configuration,
            session.getAmountRepository(),
            configurationModule.getUserSelectionRepository(),
            session.getDiscountRepository(),
            session.getGroupsRepository(),
            session.getPluginRepository());
    }

    @Override
    public void fetchImageFromUrl(final String url) {
        Picasso.with(this)
                .load(url)
                .fetch();
    }

    @Override
    public void showBusinessResult(final BusinessPaymentModel model) {
        overrideTransitionIn();
        BusinessPaymentResultActivity.start(this, model, merchantPublicKey, BUSINESS_REQUEST_CODE);
    }

    @Override
    public void showOneTap(@NonNull final OneTapModel oneTapModel) {
        final OneTapFragment instance = OneTapFragment.getInstance(oneTapModel);
        getSupportFragmentManager()
            .beginTransaction()
            .setCustomAnimations(R.anim.px_slide_right_to_left_in, R.anim.px_slide_right_to_left_out)
            .replace(R.id.one_tap_fragment, instance)
            .commit();
    }

    @Override
    public void hideProgress() {
        ViewUtils.showRegularLayout(this);
    }

    @Override
    public void initializeMPTracker() {
        //Initialize tracker before creating a token
        MPTracker.getInstance()
                .initTracker(merchantPublicKey, presenter.getCheckoutPreference().getSite().getId(),
                        BuildConfig.VERSION_NAME, getApplicationContext());
    }

    @Override
    public void trackScreen() {
        final MPTrackingContext mpTrackingContext = new MPTrackingContext.Builder(this, merchantPublicKey)
                .setVersion(BuildConfig.VERSION_NAME)
                .build();
        final ActionEvent event = new ActionEvent.Builder()
                .setFlowId(FlowHandler.getInstance().getFlowId())
                .setAction(TrackingUtil.SCREEN_ID_CHECKOUT)
                .build();
        mpTrackingContext.clearExpiredTracks();
        mpTrackingContext.trackEvent(event);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
        case ErrorUtil.ERROR_REQUEST_CODE:
            resolveErrorRequest(resultCode, data);
            break;
        case  MercadoPagoComponents.Activities.PAYMENT_VAULT_REQUEST_CODE:
            resolvePaymentVaultRequest(resultCode, data);
            break;
        case MercadoPagoComponents.Activities.PAYMENT_RESULT_REQUEST_CODE:
            resolvePaymentResultRequest(resultCode, data);
            break;
        case MercadoPagoComponents.Activities.CARD_VAULT_REQUEST_CODE:
            resolveCardVaultRequest(resultCode, data);
            break;
        case MercadoPagoComponents.Activities.REVIEW_AND_CONFIRM_REQUEST_CODE:
            resolveReviewAndConfirmRequest(resultCode, data);
            break;
        case MercadoPagoComponents.Activities.HOOK_2:
            resolveHook2(resultCode);
            break;
        case MercadoPagoComponents.Activities.HOOK_3:
            resolveHook3(resultCode);
            break;
        case PLUGIN_PAYMENT_PROCESSOR_REQUEST_CODE:
            resolvePaymentProcessor(resultCode, data);
            break;
        case BUSINESS_REQUEST_CODE:
            resolveBusinessResultActivity(data);
            break;
        default:
            break;
        }
    }

    private void resolveBusinessResultActivity(final Intent data) {
        final int resCode = data != null ? data.getIntExtra(EXTRA_CLIENT_RES_CODE, RESULT_OK) : RESULT_OK;
        presenter.exitWithCode(resCode);
    }

    private void resolveHook3(final int resultCode) {
        if (resultCode == RESULT_OK) {
            presenter.createPayment();
        } else {
            backToReviewAndConfirm();
        }
        overrideTransitionIn();
    }

    private void resolveHook2(final int resultCode) {
        if (resultCode == RESULT_OK) {
            presenter.hook2Continue();
        } else {
            presenter.cancelCheckout();
        }
    }

    private void resolvePaymentProcessor(final int resultCode, final Intent data) {
        showProgress();
        if (resultCode == RESULT_OK) {
            paymentResultOk(data);
        } else {
            cancelCheckout();
        }
    }

    /**
     * Depending on intent data it triggers {@link com.mercadopago.android.px.paymentresult.PaymentResultActivity}
     * flow or {@link BusinessPaymentResultActivity}.
     *
     * @param data intent data that can contains a {@link BusinessPayment}
     */
    private void paymentResultOk(final Intent data) {

        if (PaymentProcessorPluginActivity.isBusiness(data)) {
            final BusinessPayment businessPayment = PaymentProcessorPluginActivity.getBusinessPayment(data);
            presenter.onBusinessResult(businessPayment);
        } else {
            final PaymentResult paymentResult = CheckoutStore.getInstance().getPaymentResult();
            presenter.checkStartPaymentResultActivity(paymentResult);
        }
    }

    private void resolveReviewAndConfirmRequest(final int resultCode, final Intent data) {
        if (resultCode == RESULT_OK) {
            presenter.onPaymentConfirmation();
        } else if (resultCode == ReviewAndConfirmActivity.RESULT_CHANGE_PAYMENT_METHOD) {
            presenter.onChangePaymentMethodFromReviewAndConfirm();
        } else if (resultCode == ReviewAndConfirmActivity.RESULT_CANCEL_PAYMENT) {
            resolveCancelReviewAndConfirm(data);
        } else if (resultCode == RESULT_CANCELED) {
            presenter.onReviewAndConfirmCancel();
        }
    }

    @Override
    public void transitionOut() {
        overrideTransitionOut();
    }

    private void resolveCancelReviewAndConfirm(final Intent data) {
        if (data != null && data.hasExtra(EXTRA_CLIENT_RES_CODE)) {
            final Integer customResultCode = data.getIntExtra(EXTRA_CLIENT_RES_CODE, 0);
            presenter.onCustomReviewAndConfirmResponse(customResultCode);
        } else {
            MercadoPagoError mercadoPagoError = null;
            if (data != null && data.getStringExtra(EXTRA_ERROR) != null) {
                mercadoPagoError = JsonUtil.getInstance()
                    .fromJson(data.getStringExtra(EXTRA_ERROR), MercadoPagoError.class);
            }
            if (mercadoPagoError == null) {
                presenter.cancelCheckout();
            } else {
                presenter.onReviewAndConfirmError(mercadoPagoError);
            }
        }
    }

    protected void resolveCardVaultRequest(final int resultCode, final Intent data) {
        if (resultCode == RESULT_OK) {
            final Issuer issuer = JsonUtil.getInstance().fromJson(data.getStringExtra(EXTRA_ISSUER), Issuer.class);
            final Token token = JsonUtil.getInstance().fromJson(data.getStringExtra(EXTRA_TOKEN), Token.class);
            presenter.onCardFlowResponse(issuer, token);
        } else {
            final MercadoPagoError mercadoPagoError =
                (data == null || data.getStringExtra(EXTRA_ERROR) == null) ? null :
                    JsonUtil.getInstance()
                        .fromJson(data.getStringExtra(EXTRA_ERROR), MercadoPagoError.class);
            if (mercadoPagoError == null) {
                presenter.onCardFlowCancel();
            } else {
                presenter.onCardFlowError(mercadoPagoError);
            }
        }
    }

    private void resolvePaymentVaultRequest(final int resultCode, final Intent data) {
        if (resultCode == RESULT_OK) {
            final Issuer issuer = JsonUtil.getInstance().fromJson(data.getStringExtra(EXTRA_ISSUER), Issuer.class);
            final Token token = JsonUtil.getInstance().fromJson(data.getStringExtra(EXTRA_TOKEN), Token.class);
            final Card card = JsonUtil.getInstance().fromJson(data.getStringExtra(EXTRA_CARD), Card.class);
            final Payer payer = JsonUtil.getInstance().fromJson(data.getStringExtra(EXTRA_PAYER), Payer.class);
            presenter.onPaymentMethodSelectionResponse(issuer, token, card, payer);
        } else if (isErrorResult(data)) {
            final MercadoPagoError mercadoPagoError =
                    JsonUtil.getInstance().fromJson(data.getStringExtra(EXTRA_ERROR), MercadoPagoError.class);
            presenter.onPaymentMethodSelectionError(mercadoPagoError);
        } else {
            presenter.onPaymentMethodSelectionCancel();
        }
    }

    private boolean isErrorResult(final Intent data) {
        return data != null && !TextUtils.isEmpty(data.getStringExtra(EXTRA_ERROR));
    }

    @Override
    public void showReviewAndConfirm(final boolean isUniquePaymentMethod) {
        overrideTransitionOut();
        overrideTransitionIn();
        new ReviewAndConfirmBuilder()
            .setMerchantPublicKey(merchantPublicKey)
            .setPreference(presenter.getCheckoutPreference())
            .setDiscount(presenter.getDiscount(), presenter.getCampaign())
            .setToken(presenter.getCreatedToken())
            .setIssuer(presenter.getIssuer())
            .setHasExtraPaymentMethods(!isUniquePaymentMethod)
            .startForResult(this);
    }

    @Override
    public void backToReviewAndConfirm() {
        showReviewAndConfirm(presenter.isUniquePaymentMethod());
        overrideTransitionOut();
    }

    @Override
    public void showPaymentMethodSelection() {
        if (isActive()) {
            new MercadoPagoComponents.Activities.PaymentVaultActivityBuilder()
                .setActivity(this)
                .startActivity();
            overrideTransitionIn();
        }
    }

    @Override
    public void showPaymentResult(final PaymentResult paymentResult) {
        final BigDecimal amount =
            presenter.getCreatedPayment() == null ? presenter.getCheckoutPreference().getTotalAmount()
                : presenter.getCreatedPayment().getTransactionAmount();

        new MercadoPagoComponents.Activities.PaymentResultActivityBuilder()
            .setActivity(this)
            .setPaymentResult(paymentResult)
            .setDiscount(presenter.getDiscount())
            .setAmount(amount)
            .startActivity();

        overrideTransitionFadeInFadeOut();
    }

    @Override
    public void showSavedCardFlow(final Card card) {
        new MercadoPagoComponents.Activities.CardVaultActivityBuilder()
            .setCard(card)
            .startActivity(this, MercadoPagoComponents.Activities.CARD_VAULT_REQUEST_CODE);
    }

    @Override
    public void showNewCardFlow() {
        new MercadoPagoComponents.Activities.CardVaultActivityBuilder()
            .startActivity(this, MercadoPagoComponents.Activities.CARD_VAULT_REQUEST_CODE);
    }

    private void resolvePaymentResultRequest(final int resultCode, final Intent data) {
        if (resultCode == RESULT_CANCELED && data != null) {
            final String nextAction = data.getStringExtra(EXTRA_NEXT_ACTION);
            presenter.onPaymentResultCancel(nextAction);
        } else {
            if (data != null && data.hasExtra(EXTRA_RESULT_CODE)) {
                final Integer finalResultCode =
                    data.getIntExtra(EXTRA_RESULT_CODE, PAYMENT_RESULT_CODE);
                customDataBundle = data;
                presenter.onCustomPaymentResultResponse(finalResultCode);
            } else {
                presenter.onPaymentResultResponse();
            }
        }
    }

    @Override
    public void startPaymentRecoveryFlow(final PaymentRecovery paymentRecovery) {
        final PaymentPreference paymentPreference = presenter.getCheckoutPreference().getPaymentPreference();
        paymentPreference.setDefaultPaymentTypeId(presenter.getSelectedPaymentMethod().getPaymentTypeId());
        new MercadoPagoComponents.Activities.CardVaultActivityBuilder()
            .setPaymentRecovery(paymentRecovery)
            .setCard(presenter.getSelectedCard())
            .startActivity(this, MercadoPagoComponents.Activities.CARD_VAULT_REQUEST_CODE);
        overrideTransitionIn();
    }

    private void resolveErrorRequest(final int resultCode, final Intent data) {
        if (resultCode == RESULT_OK) {
            presenter.recoverFromFailure();
        } else {
            final MercadoPagoError mercadoPagoError = data.getStringExtra(EXTRA_ERROR) == null ? null :
                JsonUtil.getInstance().fromJson(data.getStringExtra(EXTRA_ERROR), MercadoPagoError.class);
            presenter.onErrorCancel(mercadoPagoError);
        }
    }

    @Override
    public void showError(final MercadoPagoError error) {
        ErrorUtil.startErrorActivity(this, error);
    }

    @Override
    public void showProgress() {
        ViewUtils.showProgressLayout(this);
    }

    @Override
    public void showHook(@NonNull final Hook hook, final int requestCode) {
        startActivityForResult(HookActivity.getIntent(this, hook), requestCode);
        overrideTransitionIn();
    }

    @Override
    public void showPaymentProcessor() {
        startActivityForResult(PaymentProcessorPluginActivity.getIntent(this), PLUGIN_PAYMENT_PROCESSOR_REQUEST_CODE);
        overrideTransitionWithNoAnimation();
    }

    @Override
    protected void onDestroy() {
        presenter.cancelInitialization();
        presenter.detachResourceProvider();
        presenter.detachView();
        super.onDestroy();
    }

    @Override
    public boolean isActive() {
        return !isFinishing();
    }

    @Override
    public void onChangePaymentMethod() {
        presenter.onChangePaymentMethod();
    }

    @Override
    public void onOneTapPay(@NonNull final PaymentMethod paymentMethod) {
        presenter.startPayment(paymentMethod);
    }

    @Override
    public void finishWithPaymentResult() {
        presenter.exitWithCode(RESULT_OK);
    }

    @Override
    public void onOneTapPay(@NonNull final CardPaymentModel cardPaymentModel) {
        presenter.startCardPayment(cardPaymentModel);
    }

    @Override
    public void onOneTapCanceled() {
        cancelCheckout();
    }

    @Override
    public void onOneTapConfirmCardFlow() {
        presenter.confirmCardFlow();
    }

    @Override
    public void onOneTapCardFlowCanceled() {
        presenter.cancelCardFlow();
    }

    //TODO UNIFY
    @Override
    public void cancelCheckout(final Integer resultCode, final Boolean paymentMethodEdited) {
        overrideTransitionOut();
        final Intent intent = new Intent();
        intent.putExtra(EXTRA_PAYMENT_METHOD_CHANGED, paymentMethodEdited);
        setResult(resultCode, intent);
        finish();
    }

    //TODO UNIFY
    @Override
    public void finishWithPaymentResult(final Payment payment) {
        final Intent data = new Intent();
        data.putExtra(EXTRA_PAYMENT_RESULT, payment);
        setResult(PAYMENT_RESULT_CODE, data);
        finish();
    }

    //TODO UNIFY
    @Override
    public void finishWithPaymentResult(final Integer paymentResultCode) {
        final Intent intent = new Intent();
        if (customDataBundle != null) {
            intent.putExtras(customDataBundle);
        }
        setResult(paymentResultCode, intent);
        finish();
    }

    //TODO UNIFY
    @Override
    public void finishWithPaymentResult(final Integer resultCode, final Payment payment) {
        final Intent intent = new Intent();
        if (customDataBundle != null) {
            intent.putExtras(customDataBundle);
        }
        intent.putExtra(EXTRA_PAYMENT_RESULT, payment);
        setResult(resultCode, intent);
        finish();
    }

    //TODO UNIFY
    @Override
    public void cancelCheckout(final MercadoPagoError mercadoPagoError) {
        overrideTransitionOut();
        final Intent intent = new Intent();
        intent.putExtra(EXTRA_ERROR, mercadoPagoError);
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    //TODO UNIFY
    @Override
    public void exitCheckout(final int resCode) {
        overrideTransitionOut();
        setResult(resCode);
        finish();
    }

    //TODO UNIFY
    @Override
    public void cancelCheckout() {
        overrideTransitionOut();
        exitCheckout(RESULT_CANCELED);
    }
}