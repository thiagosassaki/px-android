package com.mercadopago.android.px.internal.features.onetap;

import com.mercadopago.android.px.internal.repository.PaymentRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.viewmodel.OneTapModel;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.CardPaymentMetadata;
import com.mercadopago.android.px.model.Issuer;
import com.mercadopago.android.px.model.OneTapMetadata;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentMethodSearch;
import com.mercadopago.android.px.model.PaymentTypes;
import com.mercadopago.android.px.model.Token;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@Ignore
@RunWith(MockitoJUnitRunner.class)
public class OneTapPresenterTest {

    private static final String CARD_ID = "visa";
    private static final String PLUGIN_ID = "account_money";
    private static final String OTHER_ID = "other";

    @Mock
    private OneTapModel model;

    @Mock
    private Card card;

    @Mock
    private PaymentMethodSearch paymentMethodSearch;

    @Mock
    private OneTapMetadata metadata;

    @Mock
    private OneTap.View view;

    @Mock
    private PaymentMethod paymentMethod;

    @Mock
    private CardPaymentMetadata cardMetadata;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentSettingRepository configuration;

    private OneTapPresenter oneTapPresenter;

    @Before
    public void setUp() {
        oneTapPresenter = new OneTapPresenter(model, paymentRepository);
        oneTapPresenter.attachView(view);
    }

    //TODO fix

    @Test
    public void whenConfirmStartPayment() {
//        configPlugin();
//        oneTapPresenter.confirmPayment();
        verify(view).trackConfirm(model);
        //TODO fix
//        verify(paymentRepository).doPayment(model, oneTapPresenter);
        verifyNoMoreInteractions(view);
    }

    //TODO fix
    @Test
    public void whenAnyTokenReceivedThenShowCardPaymentFlow() {
//        cardConfig();
        configuration.configure(mock(Token.class));
        oneTapPresenter.onTokenResolved();
        //TODO fix
//        verify(view).showPaymentFlow(any(CardPaymentModel.class));
    }

    private void configOther() {
        when(metadata.getPaymentTypeId()).thenReturn(PaymentTypes.ATM);
        when(metadata.getPaymentMethodId()).thenReturn(OTHER_ID);
        when(paymentMethodSearch.getPaymentMethodById(OTHER_ID)).thenReturn(paymentMethod);
    }

    private void configPlugin() {
        when(metadata.getPaymentTypeId()).thenReturn(PaymentTypes.PLUGIN);
        when(metadata.getPaymentMethodId()).thenReturn(PLUGIN_ID);
    }

    private void cardConfig() {
        when(metadata.getPaymentTypeId()).thenReturn(PaymentTypes.CREDIT_CARD);
        when(metadata.getPaymentMethodId()).thenReturn(CARD_ID);

        when(paymentMethodSearch.getCardById(CARD_ID)).thenReturn(card);
        when(cardMetadata.getId()).thenReturn(CARD_ID);

        when(paymentMethodSearch.getIssuer(CARD_ID)).thenReturn(mock(Issuer.class));
    }

    @Test
    public void changePaymentMethod() {
        oneTapPresenter.changePaymentMethod();
        verify(view).changePaymentMethod();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void onAmountShowMore() {
        oneTapPresenter.onAmountShowMore();
        verify(view).showDetailModal(model);
        verify(view).trackModal(model);
        verifyNoMoreInteractions(view);
    }

    @Test
    public void whenCanceledThenCancelAndTrack() {
        oneTapPresenter.cancel();
        verify(view).cancel();
        verify(view).trackCancel();
    }
}