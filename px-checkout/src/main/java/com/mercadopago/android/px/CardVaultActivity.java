package com.mercadopago.android.px;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.google.gson.reflect.TypeToken;
import com.mercadopago.android.px.core.MercadoPagoCheckout;
import com.mercadopago.android.px.core.MercadoPagoComponents;
import com.mercadopago.android.px.exceptions.MercadoPagoError;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.CardInfo;
import com.mercadopago.android.px.model.Issuer;
import com.mercadopago.android.px.model.PayerCost;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentRecovery;
import com.mercadopago.android.px.model.Token;
import com.mercadopago.android.px.presenters.CardVaultPresenter;
import com.mercadopago.android.px.providers.CardVaultProviderImpl;
import com.mercadopago.android.px.services.exceptions.ApiException;
import com.mercadopago.android.px.tracking.utils.TrackingUtil;
import com.mercadopago.android.px.views.CardVaultView;
import com.mercadopago.android.px.util.ApiUtil;
import com.mercadopago.android.px.util.ErrorUtil;
import com.mercadopago.android.px.util.JsonUtil;
import com.mercadopago.android.px.util.ViewUtils;
import java.lang.reflect.Type;
import java.util.List;

public class CardVaultActivity extends AppCompatActivity implements CardVaultView {

    private static final String EXTRA_MERCHANT_PUBLIC_KEY = "merchantPublicKey";
    private static final String EXTRA_SHOW_BANK_DEALS = "showBankDeals";
    private static final String EXTRA_ESC_ENABLED = "escEnabled";

    private static final String EXTRA_CARD = "card";

    //Parameters
    private String publicKey;

    private Boolean showBankDeals;
    private Boolean escEnabled;

    private CardVaultPresenter presenter;
    private String privateKey;

    private PaymentSettingRepository configuration;

    private void configure() {
        final Intent intent = getIntent();

        publicKey = intent.getStringExtra(EXTRA_MERCHANT_PUBLIC_KEY);
        escEnabled = intent.getBooleanExtra(EXTRA_ESC_ENABLED, false);
        showBankDeals = intent.getBooleanExtra(EXTRA_SHOW_BANK_DEALS, true);
        final Card card = JsonUtil.getInstance().fromJson(intent.getStringExtra(EXTRA_CARD), Card.class);
        final Session session = Session.getSession(this);
        configuration = session.getConfigurationModule().getPaymentSettings();
        privateKey = configuration.getCheckoutPreference().getPayer().getAccessToken();
        presenter = new CardVaultPresenter(session.getAmountRepository(), configuration, session.getConfigurationModule().getUserSelectionRepository());
        presenter.attachResourcesProvider(new CardVaultProviderImpl(this, publicKey, privateKey, escEnabled));
        presenter.attachView(this);
        presenter.setCard(card);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScreenOrientation();
        setContentView();
        configure();

        if (savedInstanceState == null) {
            getActivityParameters();
            presenter.initialize();
        } else {
            restoreInstanceState(savedInstanceState);
        }
    }

    @Override
    protected void onDestroy() {
        presenter.detachView();
        presenter.detachResourceProvider();
        super.onDestroy();
    }

    private void setScreenOrientation() {
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }
    }

    public void restoreInstanceState(Bundle savedInstanceState) {
        List<PayerCost> payerCosts;
        try {
            Type listType = new TypeToken<List<Card>>() {
            }.getType();
            payerCosts =
                JsonUtil.getInstance().getGson().fromJson(savedInstanceState.getString("payerCostsList"), listType);
        } catch (Exception ex) {
            payerCosts = null;
        }

        presenter.setPayerCostsList(payerCosts);
        presenter.setPaymentRecovery(
            JsonUtil.getInstance().fromJson(savedInstanceState.getString("paymentRecovery"), PaymentRecovery.class));
        presenter.setCard(JsonUtil.getInstance().fromJson(savedInstanceState.getString(EXTRA_CARD), Card.class));

        presenter.setPaymentMethod(
            JsonUtil.getInstance().fromJson(savedInstanceState.getString("paymentMethod"), PaymentMethod.class));
        presenter
            .setIssuer(JsonUtil.getInstance().fromJson(savedInstanceState.getString("issuer"), Issuer.class));
        presenter
            .setPayerCost(JsonUtil.getInstance().fromJson(savedInstanceState.getString("payerCost"), PayerCost.class));
        presenter
            .setToken(JsonUtil.getInstance().fromJson(savedInstanceState.getString("token"), Token.class));
        presenter
            .setCardInfo(JsonUtil.getInstance().fromJson(savedInstanceState.getString("cardInfo"), CardInfo.class));
        presenter.setInstallmentsEnabled(savedInstanceState.getBoolean("installmentsEnabled", false));
        presenter
            .setInstallmentsReviewEnabled(savedInstanceState.getBoolean("installmentsReviewEnabled", false));
        presenter.setInstallmentsListShown(savedInstanceState.getBoolean("installmentsListShown", false));
        presenter.setIssuersListShown(savedInstanceState.getBoolean("issuersListShown", false));
    }

    private void getActivityParameters() {
        Intent intent = getIntent();

        Boolean installmentsEnabled = intent.getBooleanExtra("installmentsEnabled", true);
        Boolean installmentsReviewEnabled = intent.getBooleanExtra("installmentsReviewEnabled", true);
        PaymentRecovery paymentRecovery =
            JsonUtil.getInstance().fromJson(intent.getStringExtra("paymentRecovery"), PaymentRecovery.class);
        Boolean automaticSelection = intent.getBooleanExtra("automaticSelection", false);

        List<PaymentMethod> paymentMethods;
        try {
            Type listType = new TypeToken<List<PaymentMethod>>() {
            }.getType();
            paymentMethods =
                JsonUtil.getInstance().getGson().fromJson(intent.getStringExtra("paymentMethodList"), listType);
        } catch (Exception ex) {
            paymentMethods = null;
        }

        presenter.setInstallmentsEnabled(installmentsEnabled);
        presenter.setPaymentRecovery(paymentRecovery);
        presenter.setInstallmentsReviewEnabled(installmentsReviewEnabled);
        presenter.setAutomaticSelection(automaticSelection);
    }

    private void setContentView() {
        setContentView(R.layout.px_activity_card_vault);
    }

    @Override
    public void showProgressLayout() {
        ViewUtils.showProgressLayout(this);
    }

    @Override
    public void askForSecurityCodeFromTokenRecovery() {
        PaymentRecovery paymentRecovery = presenter.getPaymentRecovery();
        String reason = "";
        if (paymentRecovery != null) {
            if (paymentRecovery.isStatusDetailInvalidESC()) {
                reason = TrackingUtil.SECURITY_CODE_REASON_ESC;
            } else if (paymentRecovery.isStatusDetailCallForAuthorize()) {
                reason = TrackingUtil.SECURITY_CODE_REASON_CALL;
            }
        }
        startSecurityCodeActivity(reason);
        animateTransitionSlideInSlideOut();
    }

    @Override
    public void startSecurityCodeActivity(String reason) {
        new MercadoPagoComponents.Activities.SecurityCodeActivityBuilder()
            .setActivity(this)
            .setMerchantPublicKey(publicKey)
            .setSiteId(configuration.getCheckoutPreference().getSiteId())
            .setPaymentMethod(presenter.getPaymentMethod())
            .setCardInfo(presenter.getCardInfo())
            .setToken(presenter.getToken())
            .setCard(presenter.getCard())
            .setPayerAccessToken(privateKey)
            .setESCEnabled(escEnabled)
            .setPaymentRecovery(presenter.getPaymentRecovery())
            .setTrackingReason(reason)
            .startActivity();
    }

    @Override
    public void askForCardInformation() {
        startGuessingCardActivity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == MercadoPagoCheckout.TIMER_FINISHED_RESULT_CODE) {
            resolveTimerObserverResult(resultCode);
        } else if (requestCode == MercadoPagoComponents.Activities.GUESSING_CARD_REQUEST_CODE) {
            resolveGuessingCardRequest(resultCode, data);
        } else if (requestCode == MercadoPagoComponents.Activities.ISSUERS_REQUEST_CODE) {
            resolveIssuersRequest(resultCode, data);
        } else if (requestCode == MercadoPagoComponents.Activities.INSTALLMENTS_REQUEST_CODE) {
            resolveInstallmentsRequest(resultCode, data);
        } else if (requestCode == MercadoPagoComponents.Activities.SECURITY_CODE_REQUEST_CODE) {
            resolveSecurityCodeRequest(resultCode, data);
        } else if (requestCode == ErrorUtil.ERROR_REQUEST_CODE) {
            resolveErrorRequest(resultCode, data);
        }
    }

    private void resolveTimerObserverResult(int resultCode) {
        setResult(resultCode);
        finish();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (presenter != null) {
            outState.putBoolean("installmentsEnabled", presenter.isInstallmentsEnabled());
            outState.putBoolean("installmentsReviewEnabled", presenter.getInstallmentsReviewEnabled());
            outState.putString(EXTRA_MERCHANT_PUBLIC_KEY, publicKey);
            outState.putString(EXTRA_CARD, JsonUtil.getInstance().toJson(presenter.getCard()));
            outState
                .putString("paymentRecovery", JsonUtil.getInstance().toJson(presenter.getPaymentRecovery()));
            outState.putBoolean(EXTRA_SHOW_BANK_DEALS, showBankDeals);
            outState.putBoolean("installmentsListShown", presenter.isInstallmentsListShown());
            outState.putBoolean("issuersListShown", presenter.isIssuersListShown());
            outState.putBoolean(EXTRA_ESC_ENABLED, escEnabled);

            if (presenter.getPayerCostList() != null) {
                outState
                    .putString("payerCostsList", JsonUtil.getInstance().toJson(presenter.getPayerCostList()));
            }

            if (presenter.getPaymentMethod() != null) {
                outState
                    .putString("paymentMethod", JsonUtil.getInstance().toJson(presenter.getPaymentMethod()));
            }

            if (presenter.getIssuer() != null) {
                outState.putString("issuer", JsonUtil.getInstance().toJson(presenter.getIssuer()));
            }

            if (presenter.getPayerCost() != null) {
                outState.putString("payerCost", JsonUtil.getInstance().toJson(presenter.getPayerCost()));
            }

            if (presenter.getToken() != null) {
                outState.putString("token", JsonUtil.getInstance().toJson(presenter.getToken()));
            }

            if (presenter.getCardInfo() != null) {
                outState.putString("cardInfo", JsonUtil.getInstance().toJson(presenter.getCardInfo()));
            }
        }
    }

    private void resolveErrorRequest(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            presenter.recoverFromFailure();
        } else {
            setResult(resultCode, data);
            finish();
        }
    }

    protected void resolveIssuersRequest(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            Issuer issuer = JsonUtil.getInstance().fromJson(bundle.getString("issuer"), Issuer.class);

            presenter.resolveIssuersRequest(issuer);
        } else if (resultCode == RESULT_CANCELED) {
            presenter.onResultCancel();
        }
    }

    protected void resolveInstallmentsRequest(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            PayerCost payerCost = JsonUtil.getInstance().fromJson(bundle.getString("payerCost"), PayerCost.class);
            presenter.resolveInstallmentsRequest(payerCost);
        } else if (resultCode == RESULT_CANCELED) {
            presenter.onResultCancel();
        }
    }

    protected void resolveGuessingCardRequest(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            PaymentMethod paymentMethod =
                JsonUtil.getInstance().fromJson(data.getStringExtra("paymentMethod"), PaymentMethod.class);
            Token token = JsonUtil.getInstance().fromJson(data.getStringExtra("token"), Token.class);
            Issuer issuer = JsonUtil.getInstance().fromJson(data.getStringExtra("issuer"), Issuer.class);
            PayerCost payerCost = JsonUtil.getInstance().fromJson(data.getStringExtra("payerCost"), PayerCost.class);

            List<PayerCost> payerCosts;
            try {
                Type listType = new TypeToken<List<PayerCost>>() {
                }.getType();
                payerCosts = JsonUtil.getInstance().getGson().fromJson(data.getStringExtra("payerCosts"), listType);
            } catch (Exception ex) {
                payerCosts = null;
            }

            List<Issuer> issuers;
            try {
                Type listType = new TypeToken<List<Issuer>>() {
                }.getType();
                issuers = JsonUtil.getInstance().getGson().fromJson(data.getStringExtra("issuers"), listType);
            } catch (Exception ex) {
                issuers = null;
            }

            presenter.resolveNewCardRequest(paymentMethod, token, payerCost, issuer, payerCosts, issuers);
        } else if (resultCode == RESULT_CANCELED) {
            presenter.onResultCancel();
        }
    }

    protected void resolveSecurityCodeRequest(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Token token = JsonUtil.getInstance().fromJson(data.getStringExtra("token"), Token.class);

            presenter.resolveSecurityCodeRequest(token);
        } else if (resultCode == RESULT_CANCELED) {
            presenter.onResultCancel();
        }
    }

    @Override
    public void cancelCardVault() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void startIssuersActivity() {
        new MercadoPagoComponents.Activities.IssuersActivityBuilder()
            .setActivity(this)
            .setMerchantPublicKey(publicKey)
            .setPayerAccessToken(privateKey)
            .setPaymentMethod(presenter.getPaymentMethod())
            .setCardInfo(presenter.getCardInfo())
            .setIssuers(presenter.getIssuersList())
            .startActivity();
        animateTransitionSlideInSlideOut();
    }

    @Override
    public void askForInstallmentsFromIssuers() {
        startInstallmentsActivity();
        animateTransitionSlideInSlideOut();
    }

    @Override
    public void askForInstallmentsFromNewCard() {
        startInstallmentsActivity();
        animateTransitionSlideInSlideOut();
    }

    @Override
    public void askForInstallments() {
        startInstallmentsActivity();
        animateTransitionSlideInSlideOut();
    }

    private void startGuessingCardActivity() {
        final Activity context = this;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new MercadoPagoComponents.Activities.GuessingCardActivityBuilder()
                    .setActivity(context)
                    .setMerchantPublicKey(publicKey)
                    .setPayerEmail(configuration.getCheckoutPreference().getPayer().getEmail())
                    .setPayerAccessToken(privateKey)
                    .setShowBankDeals(showBankDeals)
                    .setPaymentPreference(configuration.getCheckoutPreference().getPaymentPreference())
                    .setPaymentRecovery(presenter.getPaymentRecovery())
                    .startActivity();
                overridePendingTransition(R.anim.px_slide_right_to_left_in, R.anim.px_slide_right_to_left_out);
            }
        });
    }

    private void startInstallmentsActivity() {
        new MercadoPagoComponents.Activities.InstallmentsActivityBuilder()
            .setActivity(this)
            .setMerchantPublicKey(publicKey)
            .setPayerAccessToken(privateKey)
            .setPaymentMethod(presenter.getPaymentMethod())
            .setPayerEmail(configuration.getCheckoutPreference().getPayer().getEmail())
            .setIssuer(presenter.getIssuer())
            .setPaymentPreference(configuration.getCheckoutPreference().getPaymentPreference())
            .setSite(configuration.getCheckoutPreference().getSite())
            .setInstallmentsEnabled(presenter.isInstallmentsEnabled())
            .setInstallmentsReviewEnabled(presenter.getInstallmentsReviewEnabled())
            .setCardInfo(presenter.getCardInfo())
            .setPayerCosts(presenter.getPayerCostList())
            .startActivity();
    }

    @Override
    public void animateTransitionSlideInSlideOut() {
        overridePendingTransition(R.anim.px_slide_right_to_left_in, R.anim.px_slide_right_to_left_out);
    }

    @Override
    public void transitionWithNoAnimation() {
        overridePendingTransition(R.anim.px_no_change_animation, R.anim.px_no_change_animation);
    }

    @Override
    public void finishWithResult() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("payerCost", JsonUtil.getInstance().toJson(presenter.getPayerCost()));
        returnIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(presenter.getPaymentMethod()));
        returnIntent.putExtra("token", JsonUtil.getInstance().toJson(presenter.getToken()));
        returnIntent.putExtra("issuer", JsonUtil.getInstance().toJson(presenter.getIssuer()));
        returnIntent.putExtra(EXTRA_CARD, JsonUtil.getInstance().toJson(presenter.getCard()));
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void showApiExceptionError(final ApiException exception, final String requestOrigin) {
        ApiUtil.showApiExceptionError(this, exception, publicKey, requestOrigin);
    }

    @Override
    public void showError(final MercadoPagoError error, final String requestOrigin) {
        if (error != null && error.isApiException()) {
            showApiExceptionError(error.getApiException(), requestOrigin);
        } else {
            ErrorUtil.startErrorActivity(this, error, publicKey);
        }
    }
}
