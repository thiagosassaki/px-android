package com.mercadopago.android.px.internal.features.onetap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mercadolibre.android.ui.widgets.MeliSnackbar;
import com.mercadopago.android.px.BuildConfig;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.di.ConfigurationModule;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.internal.features.CheckoutActivity;
import com.mercadopago.android.px.internal.features.MercadoPagoComponents;
import com.mercadopago.android.px.internal.features.explode.ExplodeDecorator;
import com.mercadopago.android.px.internal.features.explode.ExplodeParams;
import com.mercadopago.android.px.internal.features.explode.ExplodingFragment;
import com.mercadopago.android.px.internal.features.onetap.components.OneTapView;
import com.mercadopago.android.px.internal.features.plugins.PaymentProcessorPluginActivity;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.tracker.FlowHandler;
import com.mercadopago.android.px.internal.tracker.MPTrackingContext;
import com.mercadopago.android.px.internal.tracker.Tracker;
import com.mercadopago.android.px.internal.util.StatusBarDecorator;
import com.mercadopago.android.px.internal.viewmodel.OneTapModel;
import com.mercadopago.android.px.model.BusinessPayment;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.GenericPayment;
import com.mercadopago.android.px.model.IPayment;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.ScreenViewEvent;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.tracking.internal.utils.TrackingUtil;

import static android.app.Activity.RESULT_OK;

public class OneTapFragment extends Fragment implements OneTap.View {

    private static final String ARG_ONE_TAP_MODEL = "ARG_ONETAP_MODEL";
    private static final int REQ_CODE_CARD_VAULT = 0x999;
    private static final int REQ_CODE_PAYMENT_PROCESSOR = 0x123;

    private CallBack callback;

    /* default */ OneTapPresenter presenter;

    private ExplodingFragment explodingFragment;
    private Toolbar toolbar;
    private OneTapView oneTapView;
    private boolean explodingProcess;

    public static OneTapFragment getInstance(@NonNull final OneTapModel oneTapModel) {
        final OneTapFragment oneTapFragment = new OneTapFragment();
        final Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_ONE_TAP_MODEL, oneTapModel);
        oneTapFragment.setArguments(bundle);
        return oneTapFragment;
    }

    public interface CallBack {

        void onOneTapCanceled();

        void onChangePaymentMethod();
    }

    @Override
    public void onResume() {
        super.onResume();
        final OneTapModel model = (OneTapModel) getArguments().getSerializable(ARG_ONE_TAP_MODEL);
        presenter.onViewResumed(model);
        if (explodingFragment != null && explodingFragment.isAdded() && !explodingProcess) {
            cancelLoading();
        }
    }

    @Override
    public void updateViews(final OneTapModel model) {
        oneTapView.update(model);
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        if (context instanceof CallBack) {
            callback = (CallBack) context;
        }
    }

    @Override
    public void onDetach() {
        callback = null;
        super.onDetach();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater,
        @Nullable final ViewGroup container,
        @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.px_onetap_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        final Bundle arguments = getArguments();
        if (arguments != null) {
            final Session session = Session.getSession(view.getContext());

            final OneTapModel model = (OneTapModel) arguments.getSerializable(ARG_ONE_TAP_MODEL);
            presenter = new OneTapPresenter(model, session.getPaymentRepository());
            configureView(view, presenter, model);
            presenter.attachView(this);
            trackScreen(model);
        }
    }

    private void trackScreen(final OneTapModel model) {
        if (getActivity() != null) {
            Tracker.trackOneTapScreen(getActivity().getApplicationContext(), model);
        }
    }

    @Override
    public void cancel() {
        if (callback != null) {
            callback.onOneTapCanceled();
        }
    }

    private void configureView(final View view, final OneTap.Actions actions, final OneTapModel model) {
        toolbar = view.findViewById(R.id.toolbar);
        configureToolbar(toolbar);

        oneTapView = view.findViewById(R.id.one_tap_container);
        oneTapView.setOneTapModel(model, actions);
    }

    private void configureToolbar(final Toolbar toolbar) {
        final AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && toolbar != null) {
            activity.setSupportActionBar(toolbar);
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    presenter.cancel();
                }
            });
        }
        showToolbar();
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_CARD_VAULT && resultCode == RESULT_OK) {
            presenter.onTokenResolved();
        } else if (requestCode == REQ_CODE_PAYMENT_PROCESSOR && getActivity() != null) {
            ((CheckoutActivity) getActivity()).resolvePaymentProcessor(resultCode, data);
        }
    }

    @Override
    public void changePaymentMethod() {
        if (callback != null) {
            callback.onChangePaymentMethod();
        }
    }

    @Override
    public void showDetailModal(@NonNull final OneTapModel model) {
        PaymentDetailInfoDialog.showDialog(getChildFragmentManager());
    }

    @Override
    public void trackConfirm(final OneTapModel model) {
        if (getActivity() != null) {
            Tracker.trackOneTapConfirm(getActivity().getApplicationContext(), model);
        }
    }

    @Override
    public void trackCancel() {
        if (getActivity() != null) {
            Tracker.trackOneTapCancel(getActivity().getApplicationContext());
        }
    }

    @Override
    public void trackModal(final OneTapModel model) {
        if (getActivity() != null) {
            Tracker.trackOneTapSummaryDetail(getActivity().getApplicationContext(), model);
        }
    }

    @Override
    public void showPaymentProcessor() {
        PaymentProcessorPluginActivity.start(this, REQ_CODE_PAYMENT_PROCESSOR);
    }

    @Override
    public void showErrorView(@NonNull final MercadoPagoError error) {
        if (error.isInternalServerError() || error.isNoConnectivityError()) {
            MeliSnackbar.make(getView(), error.getMessage(), Snackbar.LENGTH_LONG,
                MeliSnackbar.SnackbarType.ERROR).show();
            trackError(error);
        } else if (getActivity() != null) {
            ((CheckoutActivity) getActivity()).presenter.onPaymentError(error);
        }
    }

    @Override
    public void showPaymentResult(@NonNull final IPayment paymentResult) {
        explodingProcess = false;
        //TODO refactor
        if (getActivity() != null) {
            //TODO refactor
            if (paymentResult instanceof GenericPayment) {
                ((CheckoutActivity) getActivity()).presenter.onPaymentFinished((GenericPayment) paymentResult);
            } else if (paymentResult instanceof Payment) {
                ((CheckoutActivity) getActivity()).presenter.onPaymentFinished((Payment) paymentResult);
            } else {
                ((CheckoutActivity) getActivity()).presenter.onPaymentFinished((BusinessPayment) paymentResult);
            }
        }
    }

    //TODO refactor
    @Override
    public void onRecoverPaymentEscInvalid() {
        if (getActivity() != null) {
            ((CheckoutActivity) getActivity()).presenter.onRecoverPaymentEscInvalid();
        }
    }

    @Override
    public void showLoadingFor(final ExplodeDecorator params,
        final ExplodingFragment.ExplodingAnimationListener explodingAnimationListener) {
        if (explodingFragment != null && explodingFragment.isAdded()) {
            explodingFragment.finishLoading(params, explodingAnimationListener);
        }
    }

    @Override
    public void cancelLoading() {
        showToolbar();
        oneTapView.showButton();
        explodingProcess = false;
        restoreStatusBar();
        getChildFragmentManager().beginTransaction().remove(explodingFragment).commitNow();
    }

    private void restoreStatusBar() {
        new StatusBarDecorator(getActivity().getWindow())
            .setupStatusBarColor(ContextCompat.getColor(getContext(), R.color.px_colorPrimaryDark));
    }

    private void showToolbar() {
        toolbar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideConfirmButton() {
        oneTapView.hideConfirmButton();
    }

    @Override
    public void hideToolbar() {
        toolbar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void startLoadingButton(final int yButtonPosition, final int buttonHeight, final int paymentTimeout) {
        final ExplodeParams explodeParams = new ExplodeParams(yButtonPosition, buttonHeight,
            (int) getContext().getResources().getDimension(R.dimen.px_m_margin),
            getContext().getResources().getString(R.string.px_processing_payment_button),
            paymentTimeout);

        explodingFragment = ExplodingFragment.newInstance(explodeParams);
        getChildFragmentManager().beginTransaction()
            .replace(R.id.exploding_frame, explodingFragment)
            .commitNow();
        explodingProcess = true;
    }

    @Override
    public void showCardFlow(@NonNull final OneTapModel model, @NonNull final Card card) {
        new MercadoPagoComponents.Activities.CardVaultActivityBuilder()
            .setCard(card)
            .startActivity(this, REQ_CODE_CARD_VAULT);
    }

    private void trackError(@NonNull final MercadoPagoError mercadoPagoError) {
        final Session session = Session.getSession(getContext());
        final ConfigurationModule configurationModule = session.getConfigurationModule();
        final PaymentSettingRepository paymentSettings = configurationModule.getPaymentSettings();

        final MPTrackingContext mpTrackingContext = new MPTrackingContext.Builder(getActivity().getApplicationContext(),
            paymentSettings.getPublicKey())
            .setVersion(BuildConfig.VERSION_NAME)
            .build();

        ScreenViewEvent.Builder builder = new ScreenViewEvent.Builder()
            .setFlowId(FlowHandler.getInstance().getFlowId())
            .setScreenId(TrackingUtil.SCREEN_ID_ERROR)
            .setScreenName(TrackingUtil.SCREEN_NAME_ERROR);

        builder = mercadoPagoError.getErrorEvent(builder);

        final ScreenViewEvent event = builder.build();

        mpTrackingContext.trackEvent(event);
    }
}
