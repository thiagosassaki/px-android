package com.mercadopago.android.px.internal.features.paymentresult;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.mercadopago.android.px.BuildConfig;
import com.mercadopago.android.px.configuration.PaymentResultScreenConfiguration;
import com.mercadopago.android.px.internal.features.MercadoPagoComponents;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.internal.features.paymentresult.components.AccreditationComment;
import com.mercadopago.android.px.internal.features.paymentresult.components.AccreditationCommentRenderer;
import com.mercadopago.android.px.internal.features.paymentresult.components.AccreditationTime;
import com.mercadopago.android.px.internal.features.paymentresult.components.AccreditationTimeRenderer;
import com.mercadopago.android.px.internal.features.paymentresult.components.Body;
import com.mercadopago.android.px.internal.features.paymentresult.components.BodyError;
import com.mercadopago.android.px.internal.features.paymentresult.components.BodyErrorRenderer;
import com.mercadopago.android.px.internal.features.paymentresult.components.BodyRenderer;
import com.mercadopago.android.px.internal.features.paymentresult.components.InstructionReferenceComponent;
import com.mercadopago.android.px.internal.features.paymentresult.components.InstructionReferenceRenderer;
import com.mercadopago.android.px.internal.features.paymentresult.components.Instructions;
import com.mercadopago.android.px.internal.features.paymentresult.components.InstructionsAction;
import com.mercadopago.android.px.internal.features.paymentresult.components.InstructionsActionRenderer;
import com.mercadopago.android.px.internal.features.paymentresult.components.InstructionsActions;
import com.mercadopago.android.px.internal.features.paymentresult.components.InstructionsActionsRenderer;
import com.mercadopago.android.px.internal.features.paymentresult.components.InstructionsContent;
import com.mercadopago.android.px.internal.features.paymentresult.components.InstructionsContentRenderer;
import com.mercadopago.android.px.internal.features.paymentresult.components.InstructionsInfo;
import com.mercadopago.android.px.internal.features.paymentresult.components.InstructionsInfoRenderer;
import com.mercadopago.android.px.internal.features.paymentresult.components.InstructionsReferences;
import com.mercadopago.android.px.internal.features.paymentresult.components.InstructionsReferencesRenderer;
import com.mercadopago.android.px.internal.features.paymentresult.components.InstructionsRenderer;
import com.mercadopago.android.px.internal.features.paymentresult.components.InstructionsSecondaryInfo;
import com.mercadopago.android.px.internal.features.paymentresult.components.InstructionsSecondaryInfoRenderer;
import com.mercadopago.android.px.internal.features.paymentresult.components.InstructionsSubtitle;
import com.mercadopago.android.px.internal.features.paymentresult.components.InstructionsSubtitleRenderer;
import com.mercadopago.android.px.internal.features.paymentresult.components.InstructionsTertiaryInfo;
import com.mercadopago.android.px.internal.features.paymentresult.components.InstructionsTertiaryInfoRenderer;
import com.mercadopago.android.px.internal.features.paymentresult.components.PaymentResultContainer;
import com.mercadopago.android.px.internal.features.paymentresult.props.PaymentResultProps;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.tracker.MPTrackingContext;
import com.mercadopago.android.px.internal.util.ErrorUtil;
import com.mercadopago.android.px.internal.util.JsonUtil;
import com.mercadopago.android.px.internal.view.Component;
import com.mercadopago.android.px.internal.view.ComponentManager;
import com.mercadopago.android.px.internal.view.LoadingComponent;
import com.mercadopago.android.px.internal.view.LoadingRenderer;
import com.mercadopago.android.px.internal.view.RendererFactory;
import com.mercadopago.android.px.model.PaymentResult;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.model.ScreenViewEvent;
import com.mercadopago.android.px.model.exceptions.ApiException;
import java.math.BigDecimal;

public class PaymentResultActivity extends AppCompatActivity implements PaymentResultNavigator {


    public static final String CONGRATS_DISPLAY_BUNDLE = "congratsDisplay";

    public static final String PAYMENT_RESULT_BUNDLE = "paymentResult";
    public static final String AMOUNT_BUNDLE = "amount";

    private static final String EXTRA_NEXT_ACTION = "nextAction";

    private PaymentResultPresenter presenter;
    private Integer congratsDisplay;

    private PaymentResultPropsMutator mutator;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final PaymentSettingRepository paymentSettings =
            Session.getSession(this).getConfigurationModule().getPaymentSettings();
        final PaymentResultScreenConfiguration paymentResultScreenConfiguration =
            paymentSettings.getAdvancedConfiguration().getPaymentResultScreenConfiguration();
        presenter = new PaymentResultPresenter(this,
            paymentSettings);

        mutator = new PaymentResultPropsMutator(new PaymentResultProps.Builder(
            paymentResultScreenConfiguration).build());

        getActivityParameters();

        final PaymentResultProvider paymentResultProvider = new PaymentResultProviderImpl(this);

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

        final Component root = new PaymentResultContainer(componentManager,
            new PaymentResultProps.Builder(
                paymentResultScreenConfiguration).build(),
            paymentResultProvider);
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
        ErrorUtil.showApiExceptionError(this, exception, requestOrigin);
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
            outState.putString(AMOUNT_BUNDLE, JsonUtil.getInstance().toJson(presenter.getAmount()));
        }

        outState.putInt(CONGRATS_DISPLAY_BUNDLE, congratsDisplay);
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        final PaymentResult paymentResult =
            JsonUtil.getInstance().fromJson(savedInstanceState.getString(PAYMENT_RESULT_BUNDLE), PaymentResult.class);

        final BigDecimal amount =
            JsonUtil.getInstance().fromJson(savedInstanceState.getString(AMOUNT_BUNDLE), BigDecimal.class);

        congratsDisplay = savedInstanceState.getInt(CONGRATS_DISPLAY_BUNDLE, -1);

        presenter = new PaymentResultPresenter(this,
            Session.getSession(this).getConfigurationModule().getPaymentSettings());
        presenter.setPaymentResult(paymentResult);
        presenter.setAmount(amount);

        final PaymentResultProvider provider = new PaymentResultProviderImpl(this);
        presenter.attachResourcesProvider(provider);

        super.onRestoreInstanceState(savedInstanceState);
    }

    protected void getActivityParameters() {

        Intent intent = getIntent();
        BigDecimal amount = null;
        if (intent.getStringExtra("amount") != null) {
            amount = new BigDecimal(intent.getStringExtra("amount"));
        }
        PaymentResult paymentResult =
            JsonUtil.getInstance().fromJson(intent.getExtras().getString("paymentResult"), PaymentResult.class);

        presenter.setAmount(amount);
        presenter.setPaymentResult(paymentResult);

        congratsDisplay = intent.getIntExtra("congratsDisplay", -1);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == MercadoPagoComponents.Activities.CONGRATS_REQUEST_CODE) {
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
        final String publicKey = Session.getSession(this).getConfigurationModule().getPaymentSettings().getPublicKey();
        MPTrackingContext mpTrackingContext = new MPTrackingContext.Builder(this, publicKey)
            .setVersion(BuildConfig.VERSION_NAME)
            .build();

        mpTrackingContext.trackEvent(event);
    }
}
