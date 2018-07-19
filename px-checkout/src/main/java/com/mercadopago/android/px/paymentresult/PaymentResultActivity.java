package com.mercadopago.android.px.paymentresult;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.mercadopago.android.px.BuildConfig;
import com.mercadopago.android.px.components.Component;
import com.mercadopago.android.px.components.ComponentManager;
import com.mercadopago.android.px.components.LoadingComponent;
import com.mercadopago.android.px.components.LoadingRenderer;
import com.mercadopago.android.px.components.RendererFactory;
import com.mercadopago.android.px.core.MercadoPagoCheckout;
import com.mercadopago.android.px.core.MercadoPagoComponents;
import com.mercadopago.android.px.exceptions.MercadoPagoError;
import com.mercadopago.android.px.model.PaymentResult;
import com.mercadopago.android.px.model.Site;
import com.mercadopago.android.px.paymentresult.components.AccreditationComment;
import com.mercadopago.android.px.paymentresult.components.AccreditationCommentRenderer;
import com.mercadopago.android.px.paymentresult.components.AccreditationTime;
import com.mercadopago.android.px.paymentresult.components.AccreditationTimeRenderer;
import com.mercadopago.android.px.paymentresult.components.Body;
import com.mercadopago.android.px.paymentresult.components.BodyError;
import com.mercadopago.android.px.paymentresult.components.BodyErrorRenderer;
import com.mercadopago.android.px.paymentresult.components.BodyRenderer;
import com.mercadopago.android.px.paymentresult.components.InstructionReferenceComponent;
import com.mercadopago.android.px.paymentresult.components.InstructionReferenceRenderer;
import com.mercadopago.android.px.paymentresult.components.Instructions;
import com.mercadopago.android.px.paymentresult.components.InstructionsAction;
import com.mercadopago.android.px.paymentresult.components.InstructionsActionRenderer;
import com.mercadopago.android.px.paymentresult.components.InstructionsActions;
import com.mercadopago.android.px.paymentresult.components.InstructionsActionsRenderer;
import com.mercadopago.android.px.paymentresult.components.InstructionsContent;
import com.mercadopago.android.px.paymentresult.components.InstructionsContentRenderer;
import com.mercadopago.android.px.paymentresult.components.InstructionsInfo;
import com.mercadopago.android.px.paymentresult.components.InstructionsInfoRenderer;
import com.mercadopago.android.px.paymentresult.components.InstructionsReferences;
import com.mercadopago.android.px.paymentresult.components.InstructionsReferencesRenderer;
import com.mercadopago.android.px.paymentresult.components.InstructionsRenderer;
import com.mercadopago.android.px.paymentresult.components.InstructionsSecondaryInfo;
import com.mercadopago.android.px.paymentresult.components.InstructionsSecondaryInfoRenderer;
import com.mercadopago.android.px.paymentresult.components.InstructionsSubtitle;
import com.mercadopago.android.px.paymentresult.components.InstructionsSubtitleRenderer;
import com.mercadopago.android.px.paymentresult.components.InstructionsTertiaryInfo;
import com.mercadopago.android.px.paymentresult.components.InstructionsTertiaryInfoRenderer;
import com.mercadopago.android.px.paymentresult.components.PaymentResultContainer;
import com.mercadopago.android.px.preferences.PaymentResultScreenPreference;
import com.mercadopago.android.px.preferences.ServicePreference;
import com.mercadopago.android.px.services.exceptions.ApiException;
import com.mercadopago.android.px.tracker.MPTrackingContext;
import com.mercadopago.android.px.tracking.model.ScreenViewEvent;
import com.mercadopago.android.px.util.ApiUtil;
import com.mercadopago.android.px.util.ErrorUtil;
import com.mercadopago.android.px.util.JsonUtil;
import java.math.BigDecimal;

public class PaymentResultActivity extends AppCompatActivity implements PaymentResultNavigator {

    public static final String PAYER_ACCESS_TOKEN_BUNDLE = "merchantPublicKey";
    public static final String MERCHANT_PUBLIC_KEY_BUNDLE = "payerAccessToken";

    public static final String CONGRATS_DISPLAY_BUNDLE = "congratsDisplay";
    public static final String PAYMENT_RESULT_SCREEN_PREFERENCE_BUNDLE = "paymentResultScreenPreference";
    public static final String SERVICE_PREFERENCE_BUNDLE = "servicePreference";
    public static final String PAYMENT_RESULT_BUNDLE = "paymentResult";
    public static final String AMOUNT_BUNDLE = "amount";
    public static final String SITE_BUNDLE = "site";

    private static final String EXTRA_NEXT_ACTION = "nextAction";

    private PaymentResultPresenter presenter;

    private String merchantPublicKey;
    private String payerAccessToken;
    private Integer congratsDisplay;
    private PaymentResultScreenPreference paymentResultScreenPreference;
    private PaymentResultPropsMutator mutator;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mutator = new PaymentResultPropsMutator();
        presenter = new PaymentResultPresenter(this);

        getActivityParameters();

        final PaymentResultProvider paymentResultProvider =
            new PaymentResultProviderImpl(this, merchantPublicKey, payerAccessToken);

        presenter.attachResourcesProvider(paymentResultProvider);

        final ComponentManager componentManager = new ComponentManager(this);

        RendererFactory.register(Body.class, BodyRenderer.class);
        RendererFactory.register(LoadingComponent.class, LoadingRenderer.class);
        RendererFactory.register(Instructions.class, InstructionsRenderer.class);
        RendererFactory.register(InstructionsSubtitle.class, InstructionsSubtitleRenderer.class);
        RendererFactory.register(InstructionsContent.class, InstructionsContentRenderer.class);
        RendererFactory.register(InstructionsInfo.class, InstructionsInfoRenderer.class);
        RendererFactory.register(InstructionsReferences.class, InstructionsReferencesRenderer.class);
        RendererFactory.register(InstructionReferenceComponent.class, InstructionReferenceRenderer.class);
        RendererFactory.register(AccreditationTime.class, AccreditationTimeRenderer.class);
        RendererFactory.register(AccreditationComment.class, AccreditationCommentRenderer.class);
        RendererFactory.register(InstructionsSecondaryInfo.class, InstructionsSecondaryInfoRenderer.class);
        RendererFactory.register(InstructionsTertiaryInfo.class, InstructionsTertiaryInfoRenderer.class);
        RendererFactory.register(InstructionsActions.class, InstructionsActionsRenderer.class);
        RendererFactory.register(InstructionsAction.class, InstructionsActionRenderer.class);
        RendererFactory.register(BodyError.class, BodyErrorRenderer.class);

        final Component root = new PaymentResultContainer(componentManager, paymentResultProvider);
        componentManager.setActionsListener(presenter);
        componentManager.setComponent(root);

        mutator.setPropsListener(componentManager);
        mutator.renderDefaultProps();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.attachView(mutator);
        presenter.initialize();
    }

    @Override
    public void showApiExceptionError(final ApiException exception, final String requestOrigin) {
        ApiUtil.showApiExceptionError(this, exception, requestOrigin);
    }

    @Override
    public void showError(final MercadoPagoError error, final String requestOrigin) {
        if (error != null && error.isApiException()) {
            showApiExceptionError(error.getApiException(), requestOrigin);
        } else {
            ErrorUtil.startErrorActivity(this, error);
        }
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        if (presenter != null) {
            outState.putString(PAYMENT_RESULT_BUNDLE, JsonUtil.getInstance().toJson(presenter.getPaymentResult()));
            outState.putString(SITE_BUNDLE, JsonUtil.getInstance().toJson(presenter.getSite()));
            outState.putString(AMOUNT_BUNDLE, JsonUtil.getInstance().toJson(presenter.getAmount()));
        }
        outState.putString(MERCHANT_PUBLIC_KEY_BUNDLE, merchantPublicKey);
        outState.putString(PAYER_ACCESS_TOKEN_BUNDLE, payerAccessToken);

        outState.putInt(CONGRATS_DISPLAY_BUNDLE, congratsDisplay);
        outState.putString(PAYMENT_RESULT_SCREEN_PREFERENCE_BUNDLE,
            JsonUtil.getInstance().toJson(paymentResultScreenPreference));
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        final PaymentResult paymentResult =
            JsonUtil.getInstance().fromJson(savedInstanceState.getString(PAYMENT_RESULT_BUNDLE), PaymentResult.class);
        final Site site = JsonUtil.getInstance().fromJson(savedInstanceState.getString(SITE_BUNDLE), Site.class);
        final BigDecimal amount =
            JsonUtil.getInstance().fromJson(savedInstanceState.getString(AMOUNT_BUNDLE), BigDecimal.class);
        final ServicePreference servicePreference = JsonUtil.getInstance()
            .fromJson(savedInstanceState.getString(SERVICE_PREFERENCE_BUNDLE), ServicePreference.class);

        merchantPublicKey = savedInstanceState.getString(MERCHANT_PUBLIC_KEY_BUNDLE);
        payerAccessToken = savedInstanceState.getString(PAYER_ACCESS_TOKEN_BUNDLE);

        congratsDisplay = savedInstanceState.getInt(CONGRATS_DISPLAY_BUNDLE, -1);

        paymentResultScreenPreference = JsonUtil.getInstance()
            .fromJson(savedInstanceState.getString(PAYMENT_RESULT_SCREEN_PREFERENCE_BUNDLE),
                PaymentResultScreenPreference.class);

        presenter = new PaymentResultPresenter(this);
        presenter.setPaymentResult(paymentResult);
        presenter.setSite(site);
        presenter.setAmount(amount);

        final PaymentResultProvider provider = new PaymentResultProviderImpl(this, merchantPublicKey, payerAccessToken);
        presenter.attachResourcesProvider(provider);

        super.onRestoreInstanceState(savedInstanceState);
    }

    protected void getActivityParameters() {

        Intent intent = getIntent();

        Site site = JsonUtil.getInstance().fromJson(intent.getExtras().getString("site"), Site.class);
        BigDecimal amount = null;
        if (intent.getStringExtra("amount") != null) {
            amount = new BigDecimal(intent.getStringExtra("amount"));
        }
        PaymentResult paymentResult =
            JsonUtil.getInstance().fromJson(intent.getExtras().getString("paymentResult"), PaymentResult.class);

        presenter.setSite(site);
        presenter.setAmount(amount);
        presenter.setPaymentResult(paymentResult);

        merchantPublicKey = intent.getStringExtra("merchantPublicKey");
        payerAccessToken = intent.getStringExtra("payerAccessToken");
        congratsDisplay = intent.getIntExtra("congratsDisplay", -1);
        paymentResultScreenPreference = JsonUtil.getInstance()
            .fromJson(intent.getExtras().getString("paymentResultScreenPreference"),
                PaymentResultScreenPreference.class);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {

        if (resultCode == MercadoPagoCheckout.TIMER_FINISHED_RESULT_CODE) {
            resolveTimerObserverResult(resultCode);
        } else if (requestCode == MercadoPagoComponents.Activities.CONGRATS_REQUEST_CODE) {
            finishWithOkResult(resultCode, data);
        } else if (requestCode == MercadoPagoComponents.Activities.PENDING_REQUEST_CODE) {
            resolveRequest(resultCode, data);
        } else if (requestCode == MercadoPagoComponents.Activities.REJECTION_REQUEST_CODE) {
            resolveRequest(resultCode, data);
        } else if (requestCode == MercadoPagoComponents.Activities.CALL_FOR_AUTHORIZE_REQUEST_CODE) {
            resolveRequest(resultCode, data);
        } else if (requestCode == MercadoPagoComponents.Activities.INSTRUCTIONS_REQUEST_CODE) {
            finishWithOkResult(resultCode, data);
        } else {
            finishWithCancelResult(data);
        }
    }

    private void resolveTimerObserverResult(final int resultCode) {
        setResult(resultCode);
        finish();
    }

    private void resolveRequest(final int resultCode, final Intent data) {
        if (resultCode == RESULT_CANCELED && data != null) {
            finishWithCancelResult(data);
        } else {
            finishWithOkResult(resultCode, data);
        }
    }

    private void finishWithCancelResult(final Intent data) {
        setResult(RESULT_CANCELED, data);
        finish();
    }

    private void finishWithOkResult(final int resultCode, final Intent data) {
        setResult(resultCode, data);
        finish();
    }

    @Override
    public void openLink(String url) {
        //TODO agregar try catch
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    @Override
    public void finishWithResult(int resultCode) {
        final Intent intent = new Intent();
        intent.putExtra("resultCode", resultCode);
        setResult(resultCode, intent);
        finish();
    }

    @Override
    public void changePaymentMethod() {
        final Intent returnIntent = new Intent();
        final String action = PaymentResult.SELECT_OTHER_PAYMENT_METHOD;
        returnIntent.putExtra(EXTRA_NEXT_ACTION, action);
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    @Override
    public void recoverPayment() {
        final Intent returnIntent = new Intent();
        final String action = PaymentResult.RECOVER_PAYMENT;
        returnIntent.putExtra(EXTRA_NEXT_ACTION, action);
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    @Override
    public void trackScreen(ScreenViewEvent event) {
        MPTrackingContext mpTrackingContext = new MPTrackingContext.Builder(this, merchantPublicKey)
            .setVersion(BuildConfig.VERSION_NAME)
            .build();

        mpTrackingContext.trackEvent(event);
    }
}
