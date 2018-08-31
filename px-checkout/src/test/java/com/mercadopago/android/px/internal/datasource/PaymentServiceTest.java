package com.mercadopago.android.px.internal.datasource;

import android.content.Context;
import android.support.annotation.NonNull;
import com.mercadopago.android.px.core.PaymentProcessor;
import com.mercadopago.android.px.internal.callbacks.MPCall;
import com.mercadopago.android.px.internal.callbacks.PaymentServiceHandler;
import com.mercadopago.android.px.internal.repository.AmountRepository;
import com.mercadopago.android.px.internal.repository.DiscountRepository;
import com.mercadopago.android.px.internal.repository.EscManager;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.repository.PluginRepository;
import com.mercadopago.android.px.internal.repository.TokenRepository;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.internal.viewmodel.OneTapModel;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.CardPaymentMetadata;
import com.mercadopago.android.px.model.OneTapMetadata;
import com.mercadopago.android.px.model.PayerCost;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentMethodSearch;
import com.mercadopago.android.px.model.PaymentTypes;
import com.mercadopago.android.px.model.Token;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import com.mercadopago.android.px.utils.StubFailMpCall;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PaymentServiceTest {

    @Mock private OneTapModel oneTapModel;

    @Mock private PaymentServiceHandler handler;

    @Mock private UserSelectionRepository userSelectionRepository;
    @Mock private PaymentSettingRepository paymentSettingRepository;
    @Mock private PluginRepository pluginRepository;
    @Mock private DiscountRepository discountRepository;
    @Mock private AmountRepository amountRepository;
    @Mock private PaymentProcessor paymentProcessor;
    @Mock private Context context;
    @Mock private EscManager escManager;
    @Mock private TokenRepository tokenRepository;

    private PaymentService paymentService;

    @Mock OneTapMetadata oneTapMetadata;
    @Mock CardPaymentMetadata cardPaymentMetadata;

    @Before
    public void setUp() {
        paymentService = new PaymentService(userSelectionRepository,
            paymentSettingRepository,
            pluginRepository,
            discountRepository,
            amountRepository,
            paymentProcessor,
            context,
            escManager,
            tokenRepository);

        when(paymentSettingRepository.getCheckoutPreference()).thenReturn(mock(CheckoutPreference.class));
    }

    @Test
    public void whenOneTapPaymentIsCardSelectCard() {
        final Card card = creditCardPresetMock();
        paymentService.startOneTapPayment(oneTapModel, handler);
        verify(userSelectionRepository).select(card);
    }

    @Test
    public void whenOneTapPaymentIsCardSelectPayerCost() {
        creditCardPresetMock();
        paymentService.startOneTapPayment(oneTapModel, handler);
        verify(userSelectionRepository).select(cardPaymentMetadata.getAutoSelectedInstallment());
    }

    @Test
    public void whenOneTapPaymentIsCardSelectPayerCostAndCard() {
        final Card card = creditCardPresetMock();
        paymentService.startOneTapPayment(oneTapModel, handler);
        verify(userSelectionRepository).select(card);
        verify(userSelectionRepository).select(cardPaymentMetadata.getAutoSelectedInstallment());
    }

    @Test
    public void whenOneTapPaymentWhenSavedCardAndESCSavedThenAskTokenButFailApiCallThenCVVIsRequiered() {
        final Card card = savedCreditCardOneTapPresent();
        when(escManager.hasEsc(card)).thenReturn(true);
        when(tokenRepository.createToken(card)).thenReturn(new StubFailMpCall(mock(ApiException.class)));

        paymentService.startOneTapPayment(oneTapModel, handler);

        verify(escManager).hasEsc(card);
        verifyNoMoreInteractions(escManager);

        verify(tokenRepository).createToken(card);
        verifyNoMoreInteractions(tokenRepository);

        // if api call to tokenize fails, then ask for CVV.
        verify(handler).onCvvRequired(card);
        verifyNoMoreInteractions(handler);
    }

    @Test
    public void whenOneTapPaymentWhenSavedCardAndESCSavedThenAskTokenSuccess() {
        final Card card = savedCreditCardOneTapPresent();
        when(escManager.hasEsc(card)).thenReturn(true);
        final MPCall<Token> tokenMPCall = mock(MPCall.class);

        when(tokenRepository.createToken(card)).thenReturn(tokenMPCall);

        paymentService.startOneTapPayment(oneTapModel, handler);

        verify(escManager).hasEsc(card);
        verifyNoMoreInteractions(escManager);
        verifyNoMoreInteractions(handler);
        verify(tokenRepository).createToken(card);
        verifyNoMoreInteractions(tokenRepository);
    }

    @Test
    public void whenOneTapPaymentWhenNotSavedCardAndESCSavedThenAskCVV() {
        final Card card = savedCreditCardOneTapPresent();
        when(escManager.hasEsc(card)).thenReturn(false);

        paymentService.startOneTapPayment(oneTapModel, handler);

        verify(escManager).hasEsc(card);
        verifyNoMoreInteractions(escManager);
        verifyNoMoreInteractions(tokenRepository);
    }

    @NonNull
    private Card savedCreditCardOneTapPresent() {
        final Card card = creditCardPresetMock();
        final PaymentMethod paymentMethod = mock(PaymentMethod.class);
        when(paymentMethod.getPaymentTypeId()).thenReturn(PaymentTypes.CREDIT_CARD);
        when(userSelectionRepository.getPaymentMethod()).thenReturn(paymentMethod);
        when(userSelectionRepository.hasCardSelected()).thenReturn(true);
        when(userSelectionRepository.getPayerCost()).thenReturn(mock(PayerCost.class));
        when(userSelectionRepository.getCard()).thenReturn(card);
        return card;
    }

    private Card creditCardPresetMock() {
        final Card card = mock(Card.class);
        final PaymentMethodSearch paymentMethodSearch = mock(PaymentMethodSearch.class);
        when(oneTapMetadata.getCard()).thenReturn(cardPaymentMetadata);
        when(oneTapMetadata.getPaymentTypeId()).thenReturn(PaymentTypes.CREDIT_CARD);
        when(paymentMethodSearch.getOneTapMetadata()).thenReturn(oneTapMetadata);
        when(paymentMethodSearch.getCardById(cardPaymentMetadata.getId())).thenReturn(card);
        when(oneTapModel.getPaymentMethods()).thenReturn(paymentMethodSearch);
        when(paymentMethodSearch.getPaymentMethodById(oneTapMetadata.getPaymentMethodId()))
            .thenReturn(mock(PaymentMethod.class));
        return card;
    }
}