package com.mercadopago.android.px.guessing;

import com.mercadopago.android.px.internal.controllers.PaymentMethodGuessingController;
import com.mercadopago.android.px.internal.repository.AmountRepository;
import com.mercadopago.android.px.internal.repository.GroupsRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.mocks.BankDeals;
import com.mercadopago.android.px.mocks.Cards;
import com.mercadopago.android.px.mocks.DummyCard;
import com.mercadopago.android.px.mocks.IdentificationTypes;
import com.mercadopago.android.px.mocks.Issuers;
import com.mercadopago.android.px.mocks.PayerCosts;
import com.mercadopago.android.px.mocks.PaymentMethods;
import com.mercadopago.android.px.mocks.Tokens;
import com.mercadopago.android.px.model.BankDeal;
import com.mercadopago.android.px.model.CardToken;
import com.mercadopago.android.px.model.Discount;
import com.mercadopago.android.px.model.Identification;
import com.mercadopago.android.px.model.IdentificationType;
import com.mercadopago.android.px.model.Installment;
import com.mercadopago.android.px.model.Issuer;
import com.mercadopago.android.px.model.PayerCost;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentMethodSearch;
import com.mercadopago.android.px.model.PaymentRecovery;
import com.mercadopago.android.px.model.PaymentTypes;
import com.mercadopago.android.px.model.Token;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.internal.callbacks.TaggedCallback;
import com.mercadopago.android.px.configuration.AdvancedConfiguration;
import com.mercadopago.android.px.preferences.PaymentPreference;
import com.mercadopago.android.px.internal.features.guessing_card.GuessingCardPresenter;
import com.mercadopago.android.px.internal.features.providers.GuessingCardProvider;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.model.exceptions.CardTokenException;
import com.mercadopago.android.px.internal.tracker.MPTrackingContext;
import com.mercadopago.android.px.internal.features.uicontrollers.card.CardView;
import com.mercadopago.android.px.utils.CardTestUtils;
import com.mercadopago.android.px.utils.StubSuccessMpCall;
import com.mercadopago.android.px.internal.features.guessing_card.GuessingCardActivityView;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.mercadopago.android.px.internal.util.ApiUtil.RequestOrigin.GET_IDENTIFICATION_TYPES;
import static com.mercadopago.android.px.internal.util.ApiUtil.RequestOrigin.GET_PAYMENT_METHODS;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GuessingCardPresenterTest {

    private final MockedView mockedView = new MockedView();
    private final MockedProvider provider = new MockedProvider();
    private GuessingCardPresenter presenter;

    @Mock private AmountRepository amountRepository;
    @Mock private UserSelectionRepository userSelectionRepository;
    @Mock private GroupsRepository groupsRepository;
    @Mock private PaymentMethodSearch paymentMethodSearch;
    @Mock private AdvancedConfiguration advancedConfiguration;
    @Mock private PaymentSettingRepository paymentSettingRepository;

    @Before
    public void setUp() {
        // No charge initialization.
        final List<PaymentMethod> pm = PaymentMethods.getPaymentMethodListMLA();
        when(groupsRepository.getGroups()).thenReturn(new StubSuccessMpCall<>(paymentMethodSearch));
        when(paymentMethodSearch.getPaymentMethods()).thenReturn(pm);
        when(advancedConfiguration.isBankDealsEnabled()).thenReturn(true);
        presenter = new GuessingCardPresenter(amountRepository, userSelectionRepository, paymentSettingRepository,
            groupsRepository,
            advancedConfiguration);
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);
    }

    @Test
    public void ifPublicKeySetThenCheckValidStart() {

        PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);

        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.initialize();

        assertTrue(mockedView.validStart);
    }

    @Test
    public void ifPaymentRecoverySetThenSaveCardholderNameAndIdentification() {

        PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        Token mockedToken = Tokens.getToken();
        PaymentMethod mockedPaymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        PayerCost mockedPayerCost = PayerCosts.getPayerCost();
        Issuer mockedIssuer = Issuers.getIssuerMLA();
        String paymentStatus = Payment.StatusCodes.STATUS_REJECTED;
        String paymentStatusDetail = Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE;
        PaymentRecovery mockedPaymentRecovery =
            new PaymentRecovery(mockedToken, mockedPaymentMethod, mockedPayerCost, mockedIssuer, paymentStatus,
                paymentStatusDetail);

        presenter.setPaymentRecovery(mockedPaymentRecovery);

        presenter.initialize();

        assertTrue(mockedView.validStart);
        assertEquals(presenter.getCardholderName(), mockedPaymentRecovery.getToken().getCardHolder().getName());
        assertEquals(presenter.getIdentificationNumber(),
            mockedPaymentRecovery.getToken().getCardHolder().getIdentification().getNumber());
        assertEquals(mockedView.savedCardholderName, mockedPaymentRecovery.getToken().getCardHolder().getName());
        assertEquals(mockedView.savedIdentificationNumber,
            mockedPaymentRecovery.getToken().getCardHolder().getIdentification().getNumber());
    }

    @Test
    public void ifPaymentMethodListSetWithOnePaymentMethodThenSelectIt() {

        List<IdentificationType> identificationTypesList = IdentificationTypes.getIdentificationTypes();
        provider.setIdentificationTypesResponse(identificationTypesList);

        PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.initialize();

        List<PaymentMethod> mockedGuessedPaymentMethods = new ArrayList<>();
        mockedGuessedPaymentMethods.add(PaymentMethods.getPaymentMethodOnVisa());

        presenter.resolvePaymentMethodListSet(mockedGuessedPaymentMethods, Cards.MOCKED_BIN_VISA);

        assertTrue(mockedView.paymentMethodSet);
    }

    @Test
    public void ifPaymentMethodListSetisEmptyhenShowError() {

        List<IdentificationType> identificationTypesList = IdentificationTypes.getIdentificationTypes();
        provider.setIdentificationTypesResponse(identificationTypesList);
        PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.initialize();

        List<PaymentMethod> mockedGuessedPaymentMethods = new ArrayList<>();

        presenter.resolvePaymentMethodListSet(mockedGuessedPaymentMethods, Cards.MOCKED_BIN_VISA);

        assertFalse(mockedView.paymentMethodSet);
        assertTrue(mockedView.invalidPaymentMethod);
        assertTrue(mockedView.multipleErrorViewShown);
    }

    @Test
    public void ifPaymentMethodListSetWithTwoOptionsThenAskForPaymentType() {

        List<IdentificationType> identificationTypesList = IdentificationTypes.getIdentificationTypes();
        provider.setIdentificationTypesResponse(identificationTypesList);

        PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.initialize();

        List<PaymentMethod> mockedGuessedPaymentMethods = new ArrayList<>();
        mockedGuessedPaymentMethods.add(PaymentMethods.getPaymentMethodOnVisa());
        mockedGuessedPaymentMethods.add(PaymentMethods.getPaymentMethodOnDebit());

        presenter.resolvePaymentMethodListSet(mockedGuessedPaymentMethods, Cards.MOCKED_BIN_VISA);

        assertTrue(presenter.hasToShowPaymentTypes());
    }

    @Test
    public void ifPaymentMethodListSetWithTwoOptionsThenChooseFirstOne() {

        List<IdentificationType> identificationTypesList = IdentificationTypes.getIdentificationTypes();
        provider.setIdentificationTypesResponse(identificationTypesList);

        PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.initialize();

        List<PaymentMethod> mockedGuessedPaymentMethods = new ArrayList<>();
        final PaymentMethod paymentMethodOnVisa = PaymentMethods.getPaymentMethodOnVisa();
        mockedGuessedPaymentMethods.add(paymentMethodOnVisa);
        mockedGuessedPaymentMethods.add(PaymentMethods.getPaymentMethodOnDebit());

        presenter.resolvePaymentMethodListSet(mockedGuessedPaymentMethods, Cards.MOCKED_BIN_VISA);

        when(userSelectionRepository.getPaymentMethod()).thenReturn(paymentMethodOnVisa);

        assertTrue(mockedView.paymentMethodSet);
        assertEquals(presenter.getPaymentMethod().getId(), mockedGuessedPaymentMethods.get(0).getId());
    }

    @Test
    public void ifPaymentMethodSetAndDeletedThenClearConfiguration() {

        List<IdentificationType> identificationTypesList = IdentificationTypes.getIdentificationTypes();
        provider.setIdentificationTypesResponse(identificationTypesList);

        PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.initialize();

        List<PaymentMethod> mockedGuessedPaymentMethods = new ArrayList<>();
        mockedGuessedPaymentMethods.add(PaymentMethods.getPaymentMethodOnVisa());

        presenter.resolvePaymentMethodListSet(mockedGuessedPaymentMethods, Cards.MOCKED_BIN_VISA);

        assertTrue(mockedView.paymentMethodSet);

        presenter.setPaymentMethod(null);

        assertEquals(presenter.getSecurityCodeLength(), GuessingCardPresenter.CARD_DEFAULT_SECURITY_CODE_LENGTH);
        assertEquals(presenter.getSecurityCodeLocation(), CardView.CARD_SIDE_BACK);
        assertTrue(presenter.isSecurityCodeRequired());
        assertEquals(presenter.getSavedBin().length(), 0);
    }

    @Test
    public void ifPaymentMethodSetAndDeletedThenClearViews() {

        List<IdentificationType> identificationTypesList = IdentificationTypes.getIdentificationTypes();
        provider.setIdentificationTypesResponse(identificationTypesList);

        PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.initialize();

        List<PaymentMethod> mockedGuessedPaymentMethods = new ArrayList<>();
        mockedGuessedPaymentMethods.add(PaymentMethods.getPaymentMethodOnVisa());

        presenter.resolvePaymentMethodListSet(mockedGuessedPaymentMethods, Cards.MOCKED_BIN_VISA);
        when(userSelectionRepository.hasSelectedPaymentMethod()).thenReturn(true);
        assertTrue(mockedView.paymentMethodSet);

        presenter.resolvePaymentMethodCleared();

        assertFalse(mockedView.errorState);
        assertTrue(mockedView.cardNumberLengthDefault);
        assertTrue(mockedView.cardNumberMaskDefault);
        assertTrue(mockedView.securityCodeInputErased);
        assertTrue(mockedView.clearCardView);
    }

    @Test
    public void ifPaymentMethodSetHasIdentificationTypeRequiredThenShowIdentificationView() {

        List<IdentificationType> identificationTypesList = IdentificationTypes.getIdentificationTypes();
        provider.setIdentificationTypesResponse(identificationTypesList);

        PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.initialize();

        List<PaymentMethod> mockedGuessedPaymentMethods = new ArrayList<>();
        mockedGuessedPaymentMethods.add(PaymentMethods.getPaymentMethodOnVisa());

        presenter.resolvePaymentMethodListSet(mockedGuessedPaymentMethods, Cards.MOCKED_BIN_VISA);

        assertTrue(mockedView.paymentMethodSet);
        assertTrue(presenter.isIdentificationNumberRequired());
        assertTrue(mockedView.identificationTypesInitialized);
    }

    @Test
    public void ifPaymentMethodSetDoesntHaveIdentificationTypeRequiredThenHideIdentificationView() {

        List<IdentificationType> identificationTypesList = IdentificationTypes.getIdentificationTypes();
        provider.setIdentificationTypesResponse(identificationTypesList);

        PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.initialize();

        List<PaymentMethod> mockedGuessedPaymentMethods = new ArrayList<>();
        mockedGuessedPaymentMethods.add(PaymentMethods.getPaymentMethodWithIdNotRequired());

        presenter.resolvePaymentMethodListSet(mockedGuessedPaymentMethods, Cards.MOCKED_BIN_CORDIAL);

        assertTrue(mockedView.paymentMethodSet);
        assertFalse(presenter.isIdentificationNumberRequired());
        assertFalse(mockedView.identificationTypesInitialized);
        assertTrue(mockedView.hideIdentificationInput);
    }

    @Test
    public void initializeGuessingFormWithPaymentMethodListFromCardVault() {

        List<PaymentMethod> paymentMethodList = PaymentMethods.getPaymentMethodListMLA();

        List<IdentificationType> identificationTypesList = IdentificationTypes.getIdentificationTypes();
        provider.setIdentificationTypesResponse(identificationTypesList);

        PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.initialize();

        assertTrue(mockedView.showInputContainer);
        assertTrue(mockedView.initializeGuessingForm);
        assertTrue(mockedView.initializeGuessingListeners);
    }

    @Test
    public void ifBankDealsNotEnabledThenHideBankDeals() {

        List<PaymentMethod> paymentMethodList = PaymentMethods.getPaymentMethodListMLA();

        List<IdentificationType> identificationTypesList = IdentificationTypes.getIdentificationTypes();
        provider.setIdentificationTypesResponse(identificationTypesList);

        PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.initialize();

        assertTrue(mockedView.hideBankDeals);
    }

    @Test
    public void ifGetPaymentMethodFailsThenShowErrorMessage() {

        ApiException apiException = PaymentMethods.getDoNotFindPaymentMethodsException();
        MercadoPagoError mpException = new MercadoPagoError(apiException, GET_PAYMENT_METHODS);
        provider.setPaymentMethodsResponse(mpException);

        PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.initialize();

        assertTrue(
            provider.failedResponse.getApiException().getError().equals(MockedProvider.PAYMENT_METHODS_NOT_FOUND));
    }

    @Test
    public void ifPaymentTypeSetAndTwoPaymentMethodssThenChooseByPaymentType() {

        List<PaymentMethod> paymentMethodList = PaymentMethods.getPaymentMethodListMLM();
        List<IdentificationType> identificationTypesList = IdentificationTypes.getIdentificationTypes();
        provider.setIdentificationTypesResponse(identificationTypesList);

        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setDefaultPaymentTypeId(PaymentTypes.DEBIT_CARD);

        presenter.setPaymentPreference(paymentPreference);

        presenter.initialize();

        PaymentMethodGuessingController controller = new PaymentMethodGuessingController(
            paymentMethodList, PaymentTypes.DEBIT_CARD, null);

        List<PaymentMethod> paymentMethodsWithExclusionsList =
            controller.guessPaymentMethodsByBin(Cards.MOCKED_BIN_MASTER);

        presenter.resolvePaymentMethodListSet(paymentMethodsWithExclusionsList, Cards.MOCKED_BIN_MASTER);

        when(userSelectionRepository.getPaymentMethod()).thenReturn(controller.getGuessedPaymentMethods().get(0));
        assertEquals(paymentMethodsWithExclusionsList.size(), 1);
        assertEquals(presenter.getPaymentMethod().getId(), "debmaster");
        assertFalse(presenter.hasToShowPaymentTypes());
    }

    @Test
    public void ifSecurityCodeSettingsAreWrongThenHideSecurityCodeView() {

        List<IdentificationType> identificationTypesList = IdentificationTypes.getIdentificationTypes();
        provider.setIdentificationTypesResponse(identificationTypesList);

        PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.initialize();

        List<PaymentMethod> mockedGuessedPaymentMethods = new ArrayList<>();
        mockedGuessedPaymentMethods.add(PaymentMethods.getPaymentMethodWithWrongSecurityCodeSettings());
        when(userSelectionRepository.hasSelectedPaymentMethod()).thenReturn(false);
        when(userSelectionRepository.getPaymentMethod()).thenReturn(null);
        presenter.resolvePaymentMethodListSet(mockedGuessedPaymentMethods, Cards.MOCKED_BIN_VISA);
        assertTrue(mockedView.paymentMethodSet);
        assertTrue(mockedView.hideSecurityCodeInput);
    }

    @Test
    public void ifPaymentMethodSettingsAreEmptyThenShowErrorMessage() {

        List<IdentificationType> identificationTypesList = IdentificationTypes.getIdentificationTypes();
        provider.setIdentificationTypesResponse(identificationTypesList);

        PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.initialize();

        List<PaymentMethod> mockedGuessedPaymentMethods = new ArrayList<>();
        PaymentMethod mockedPaymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        mockedPaymentMethod.setSettings(null);
        mockedGuessedPaymentMethods.add(mockedPaymentMethod);

        presenter.resolvePaymentMethodListSet(mockedGuessedPaymentMethods, Cards.MOCKED_BIN_VISA);

        assertEquals(MockedProvider.SETTING_NOT_FOUND_FOR_BIN, mockedView.errorShown.getMessage());
    }

    @Test
    public void ifGetIdentificationTypesFailsThenShowErrorMessage() {

        final ApiException apiException = IdentificationTypes.getDoNotFindIdentificationTypesException();
        final MercadoPagoError mpException =
            new MercadoPagoError(apiException, GET_IDENTIFICATION_TYPES);
        PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);
        provider.setIdentificationTypesResponse(mpException);

        presenter.initialize();
        assertEquals(provider.failedResponse.getApiException().getError(),
            MockedProvider.IDENTIFICATION_TYPES_NOT_FOUND);
    }

    @Test
    public void ifGetIdentificationTypesisEmptyhenShowErrorMessage() {

        List<IdentificationType> identificationTypes = new ArrayList<>();
        provider.setIdentificationTypesResponse(identificationTypes);

        PaymentPreference paymentPreference = new PaymentPreference();

        when(userSelectionRepository.hasSelectedPaymentMethod()).thenReturn(false);
        when(userSelectionRepository.getPaymentMethod()).thenReturn(null);

        List<PaymentMethod> paymentMethodList = PaymentMethods.getPaymentMethodListMLA();
        presenter.setPaymentPreference(paymentPreference);
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);
        presenter.initialize();
        presenter.resolvePaymentMethodListSet(paymentMethodList, Cards.MOCKED_BIN_VISA);

        assertEquals(MockedProvider.MISSING_IDENTIFICATION_TYPES, mockedView.errorShown.getMessage());
    }

    @Test
    public void ifBankDealsNotEmptyThenShowThem() {

        List<IdentificationType> identificationTypes = IdentificationTypes.getIdentificationTypes();
        provider.setIdentificationTypesResponse(identificationTypes);

        List<BankDeal> bankDeals = BankDeals.getBankDealsListMLA();
        provider.setBankDealsResponse(bankDeals);

        PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.initialize();

        final List<PaymentMethod> pm = PaymentMethods.getPaymentMethodListMLA();
        presenter.resolvePaymentMethodListSet(pm, Cards.MOCKED_BIN_VISA);

        assertTrue(mockedView.bankDealsShown);
    }

    @Test
    public void ifCardNumberSetThenValidateItAndSaveItInCardToken() {

        List<IdentificationType> identificationTypesList = IdentificationTypes.getIdentificationTypes();
        provider.setIdentificationTypesResponse(identificationTypesList);

        PaymentMethod mockedPaymentMethod = PaymentMethods.getPaymentMethodOnMaster();
        when(userSelectionRepository.getPaymentMethod()).thenReturn(mockedPaymentMethod);
        PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.initialize();

        DummyCard card = CardTestUtils.getDummyCard("master");
        presenter.saveCardNumber(card.getCardNumber());
        presenter.setPaymentMethod(mockedPaymentMethod);

        boolean valid = presenter.validateCardNumber();

        assertTrue(valid);
        assertEquals(presenter.getCardToken().getCardNumber(), card.getCardNumber());
    }

    @Test
    public void ifCardholderNameSetThenValidateItAndSaveItInCardToken() {

        List<IdentificationType> identificationTypesList = IdentificationTypes.getIdentificationTypes();
        provider.setIdentificationTypesResponse(identificationTypesList);

        PaymentMethod mockedPaymentMethod = PaymentMethods.getPaymentMethodOnMaster();

        PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.initialize();

        DummyCard card = CardTestUtils.getDummyCard("master");
        presenter.saveCardNumber(card.getCardNumber());
        presenter.setPaymentMethod(mockedPaymentMethod);
        presenter.saveCardholderName(CardTestUtils.DUMMY_CARDHOLDER_NAME);

        boolean valid = presenter.validateCardName();

        assertTrue(valid);
        assertEquals(presenter.getCardToken().getCardholder().getName(), CardTestUtils.DUMMY_CARDHOLDER_NAME);
    }

    @Test
    public void ifCardExpiryDateSetThenValidateItAndSaveItInCardToken() {

        List<IdentificationType> identificationTypesList = IdentificationTypes.getIdentificationTypes();
        provider.setIdentificationTypesResponse(identificationTypesList);

        PaymentMethod mockedPaymentMethod = PaymentMethods.getPaymentMethodOnMaster();

        PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.initialize();

        DummyCard card = CardTestUtils.getDummyCard("master");
        presenter.saveCardNumber(card.getCardNumber());
        presenter.setPaymentMethod(mockedPaymentMethod);
        presenter.saveCardholderName(CardTestUtils.DUMMY_CARDHOLDER_NAME);
        presenter.saveExpiryMonth(CardTestUtils.DUMMY_EXPIRY_MONTH);
        presenter.saveExpiryYear(CardTestUtils.DUMMY_EXPIRY_YEAR_SHORT);

        boolean valid = presenter.validateExpiryDate();

        assertTrue(valid);
        assertEquals(presenter.getCardToken().getExpirationMonth(), Integer.valueOf(CardTestUtils.DUMMY_EXPIRY_MONTH));
        assertEquals(presenter.getCardToken().getExpirationYear(),
            Integer.valueOf(CardTestUtils.DUMMY_EXPIRY_YEAR_LONG));
    }

    @Test
    public void ifCardSecurityCodeSetThenValidateItAndSaveItInCardToken() {

        List<IdentificationType> identificationTypesList = IdentificationTypes.getIdentificationTypes();
        provider.setIdentificationTypesResponse(identificationTypesList);

        PaymentMethod mockedPaymentMethod = PaymentMethods.getPaymentMethodOnMaster();

        PaymentPreference paymentPreference = new PaymentPreference();
        when(userSelectionRepository.getPaymentMethod()).thenReturn(mockedPaymentMethod);
        presenter.setPaymentPreference(paymentPreference);
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.initialize();

        DummyCard card = CardTestUtils.getDummyCard("master");
        presenter.saveCardNumber(card.getCardNumber());
        presenter.setPaymentMethod(mockedPaymentMethod);
        presenter.saveCardholderName(CardTestUtils.DUMMY_CARDHOLDER_NAME);
        presenter.saveExpiryMonth(CardTestUtils.DUMMY_EXPIRY_MONTH);
        presenter.saveExpiryYear(CardTestUtils.DUMMY_EXPIRY_YEAR_SHORT);
        presenter.saveSecurityCode(card.getSecurityCode());
        boolean validCardNumber = presenter.validateCardNumber();
        boolean validSecurityCode = presenter.validateSecurityCode();

        assertTrue(validCardNumber && validSecurityCode);
        assertEquals(presenter.getCardToken().getSecurityCode(), card.getSecurityCode());
    }

    @Test
    public void ifIdentificationNumberSetThenValidateItAndSaveItInCardToken() {

        List<IdentificationType> identificationTypesList = IdentificationTypes.getIdentificationTypes();
        provider.setIdentificationTypesResponse(identificationTypesList);

        PaymentMethod mockedPaymentMethod = PaymentMethods.getPaymentMethodOnMaster();

        Identification identification = new Identification();
        presenter.setIdentification(identification);
        PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.initialize();

        DummyCard card = CardTestUtils.getDummyCard("master");
        presenter.saveCardNumber(card.getCardNumber());
        presenter.setPaymentMethod(mockedPaymentMethod);
        presenter.saveCardholderName(CardTestUtils.DUMMY_CARDHOLDER_NAME);
        presenter.saveExpiryMonth(CardTestUtils.DUMMY_EXPIRY_MONTH);
        presenter.saveExpiryYear(CardTestUtils.DUMMY_EXPIRY_YEAR_SHORT);
        presenter.saveSecurityCode(card.getSecurityCode());
        presenter.saveIdentificationNumber(CardTestUtils.DUMMY_IDENTIFICATION_NUMBER_DNI);
        presenter.saveIdentificationType(IdentificationTypes.getIdentificationType());

        boolean valid = presenter.validateIdentificationNumber();

        assertTrue(valid);
        assertEquals(presenter.getCardToken().getCardholder().getIdentification().getNumber(),
            CardTestUtils.DUMMY_IDENTIFICATION_NUMBER_DNI);
    }

    @Test
    public void ifCardDataSetAndValidThenCreateToken() {

        List<IdentificationType> identificationTypesList = IdentificationTypes.getIdentificationTypes();
        provider.setIdentificationTypesResponse(identificationTypesList);

        List<Issuer> issuerList = Issuers.getIssuersListMLA();
        provider.setIssuersResponse(issuerList);

        PaymentMethod mockedPaymentMethod = PaymentMethods.getPaymentMethodOnMaster();

        Token mockedtoken = Tokens.getToken();
        provider.setTokenResponse(mockedtoken);

        Identification identification = new Identification();
        presenter.setIdentification(identification);
        PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);
        when(userSelectionRepository.getPaymentMethod()).thenReturn(mockedPaymentMethod);

        presenter.initialize();

        DummyCard card = CardTestUtils.getDummyCard("master");
        presenter.saveCardNumber(card.getCardNumber());
        presenter.setPaymentMethod(mockedPaymentMethod);
        presenter.saveCardholderName(CardTestUtils.DUMMY_CARDHOLDER_NAME);
        presenter.saveExpiryMonth(CardTestUtils.DUMMY_EXPIRY_MONTH);
        presenter.saveExpiryYear(CardTestUtils.DUMMY_EXPIRY_YEAR_SHORT);
        presenter.saveSecurityCode(card.getSecurityCode());
        presenter.saveIdentificationNumber(CardTestUtils.DUMMY_IDENTIFICATION_NUMBER_DNI);
        presenter.saveIdentificationType(IdentificationTypes.getIdentificationType());

        boolean valid = presenter.validateCardNumber();
        valid = valid & presenter.validateCardName();
        valid = valid & presenter.validateExpiryDate();
        valid = valid & presenter.validateSecurityCode();
        valid = valid & presenter.validateIdentificationNumber();

        assertTrue(valid);

        presenter.checkFinishWithCardToken();

        presenter.resolveTokenRequest(mockedtoken);

        assertEquals(presenter.getToken(), mockedtoken);
    }

    @Test
    public void ifPaymentMethodExclusionSetAndUserSelectsItThenShowErrorMessage() {

        List<IdentificationType> identificationTypesList = IdentificationTypes.getIdentificationTypes();
        provider.setIdentificationTypesResponse(identificationTypesList);

        //We exclude master
        List<String> excludedPaymentMethodIds = new ArrayList<>();
        excludedPaymentMethodIds.add("master");
        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setExcludedPaymentMethodIds(excludedPaymentMethodIds);

        presenter.setPaymentPreference(paymentPreference);

        presenter.initialize();

        //The user enters a master bin
        PaymentMethodGuessingController controller = presenter.getGuessingController();
        List<PaymentMethod> guessedPaymentMethods = controller.guessPaymentMethodsByBin(Cards.MOCKED_BIN_MASTER);

        presenter.resolvePaymentMethodListSet(guessedPaymentMethods, Cards.MOCKED_BIN_MASTER);

        //We show a red container showing the multiple available payment methods
        assertFalse(mockedView.paymentMethodSet);
        assertTrue(mockedView.invalidPaymentMethod);
        assertTrue(mockedView.multipleErrorViewShown);

        //The users deletes the bin master
        presenter.setPaymentMethod(null);
        presenter.resolvePaymentMethodCleared();

        //The red container disappears
        assertFalse(mockedView.multipleErrorViewShown);
        assertFalse(mockedView.invalidPaymentMethod);
    }

    @Test
    public void ifPaymentMethodExclusionSetAndUserSelectsItWithOnlyOnePMAvailableThenShowInfoMessage() {

        //We only have visa and master
        final List<PaymentMethod> paymentMethodList = PaymentMethods.getPaymentMethodListWithTwoOptions();
        final PaymentMethodSearch paymentMethodSearch = mock(PaymentMethodSearch.class);
        when(groupsRepository.getGroups()).thenReturn(new StubSuccessMpCall<>(paymentMethodSearch));
        when(paymentMethodSearch.getPaymentMethods()).thenReturn(paymentMethodList);

        final List<IdentificationType> identificationTypesList = IdentificationTypes.getIdentificationTypes();
        provider.setIdentificationTypesResponse(identificationTypesList);

        //We exclude master
        final List<String> excludedPaymentMethodIds = new ArrayList<>();
        excludedPaymentMethodIds.add("master");

        final PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setExcludedPaymentMethodIds(excludedPaymentMethodIds);

        presenter = new GuessingCardPresenter(amountRepository, userSelectionRepository, paymentSettingRepository,
            groupsRepository,
            advancedConfiguration);
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setPaymentPreference(paymentPreference);

        presenter.initialize();

        //Black info container shows the only available payment method
        assertTrue(mockedView.onlyOnePMErrorViewShown);

        assertEquals(mockedView.supportedPaymentMethodId, "visa");

        final PaymentMethodGuessingController controller = presenter.getGuessingController();
        final List<PaymentMethod> guessedPaymentMethods = controller.guessPaymentMethodsByBin(Cards.MOCKED_BIN_MASTER);
        presenter.resolvePaymentMethodListSet(guessedPaymentMethods, Cards.MOCKED_BIN_MASTER);

        //When the user enters a master bin the container turns red
        assertFalse(mockedView.paymentMethodSet);
        assertTrue(mockedView.infoContainerTurnedRed);
        assertTrue(mockedView.invalidPaymentMethod);

        presenter.setPaymentMethod(null);
        presenter.resolvePaymentMethodCleared();

        //When the user deletes the input the container turns black again
        assertFalse(mockedView.infoContainerTurnedRed);
        assertTrue(mockedView.onlyOnePMErrorViewShown);
    }

    @Test
    public void whenAllGuessedPaymentMethodsShareTypeThenDoNotAskForPaymentType() {

        PaymentMethod creditCard1 = new PaymentMethod();
        creditCard1.setPaymentTypeId(PaymentTypes.CREDIT_CARD);

        PaymentMethod creditCard2 = new PaymentMethod();
        creditCard2.setPaymentTypeId(PaymentTypes.CREDIT_CARD);

        List<PaymentMethod> paymentMethodList = new ArrayList<>();
        paymentMethodList.add(creditCard1);
        paymentMethodList.add(creditCard2);

        boolean shouldAskPaymentType = presenter.shouldAskPaymentType(paymentMethodList);
        assertFalse(shouldAskPaymentType);
    }

    @Test
    public void whenNotAllGuessedPaymentMethodsShareTypeThenDoAskForPaymentType() {

        PaymentMethod creditCard = new PaymentMethod();
        creditCard.setPaymentTypeId(PaymentTypes.CREDIT_CARD);

        PaymentMethod debitCard = new PaymentMethod();
        debitCard.setPaymentTypeId(PaymentTypes.DEBIT_CARD);

        List<PaymentMethod> paymentMethodList = new ArrayList<>();
        paymentMethodList.add(creditCard);
        paymentMethodList.add(debitCard);

        boolean shouldAskPaymentType = presenter.shouldAskPaymentType(paymentMethodList);
        assertTrue(shouldAskPaymentType);
    }

    @Test
    public void whenGuessedPaymentMethodsListIsNullThenPaymentMethodShouldBeUndefined() {

        List<PaymentMethod> paymentMethodList = null;

        boolean shouldAskPaymentType = presenter.shouldAskPaymentType(paymentMethodList);
        assertTrue(shouldAskPaymentType);
    }

    @Test
    public void whenGuessedPaymentMethodsListisEmptyhenPaymentMethodShouldBeUndefined() {

        List<PaymentMethod> paymentMethodList = new ArrayList<>();

        boolean shouldAskPaymentType = presenter.shouldAskPaymentType(paymentMethodList);
        assertTrue(shouldAskPaymentType);
    }

    @Test
    public void whenUniquePaymentMethodGuessedThenPaymentMethodShouldDefined() {

        PaymentMethod creditCard = new PaymentMethod();
        creditCard.setPaymentTypeId(PaymentTypes.CREDIT_CARD);

        List<PaymentMethod> paymentMethodList = new ArrayList<>();
        paymentMethodList.add(creditCard);

        boolean shouldAskPaymentType = presenter.shouldAskPaymentType(paymentMethodList);
        assertFalse(shouldAskPaymentType);
    }

    private class MockedView implements GuessingCardActivityView {

        private MercadoPagoError errorShown;
        private CardTokenException cardTokenError;
        private boolean formDataErrorState;
        private boolean errorState;
        private boolean validStart;
        private boolean cardNumberLengthDefault;
        private boolean cardNumberMaskDefault;
        private boolean securityCodeInputErased;
        private boolean clearCardView;
        private boolean identificationTypesInitialized;
        private boolean hideIdentificationInput;
        private boolean showInputContainer;
        private boolean initializeGuessingForm;
        private boolean initializeGuessingListeners;
        private boolean hideBankDeals;
        private boolean hideSecurityCodeInput;
        private boolean bankDealsShown;
        private boolean paymentMethodSet;
        private boolean invalidPaymentMethod;
        private boolean multipleErrorViewShown;
        private boolean onlyOnePMErrorViewShown;
        private boolean infoContainerTurnedRed;
        private String supportedPaymentMethodId;
        private String savedCardholderName;
        private String savedIdentificationNumber;

        @Override
        public void setPaymentMethod(PaymentMethod paymentMethod) {

        }

        @Override
        public void clearSecurityCodeEditText() {
            securityCodeInputErased = true;
        }

        @Override
        public void clearCardNumberEditTextMask() {
            cardNumberMaskDefault = true;
        }

        @Override
        public void restoreBlackInfoContainerView() {
            onlyOnePMErrorViewShown = true;
            infoContainerTurnedRed = false;
        }

        @Override
        public void hideRedErrorContainerView(boolean withAnimation) {
            multipleErrorViewShown = false;
            invalidPaymentMethod = false;
        }

        @Override
        public void resolvePaymentMethodSet(PaymentMethod paymentMethod) {
            paymentMethodSet = true;
        }

        @Override
        public void clearErrorIdentificationNumber() {

        }

        @Override
        public void setSoftInputMode() {

        }

        @Override
        public void setErrorContainerListener() {

        }

        @Override
        public void setInvalidCardOnePaymentMethodErrorView() {
            invalidPaymentMethod = true;
            onlyOnePMErrorViewShown = true;
            infoContainerTurnedRed = true;
        }

        @Override
        public void setInvalidCardMultipleErrorView() {
            invalidPaymentMethod = true;
            multipleErrorViewShown = true;
        }

        @Override
        public void hideProgress() {

        }

        @Override
        public void finishCardFlow(PaymentMethod paymentMethod, Token token, Issuer issuer,
            PayerCost payerCost) {

        }

        @Override
        public void finishCardFlow(PaymentMethod paymentMethod, Token token, Issuer issuer,
            List<PayerCost> payerCosts) {

        }

        @Override
        public void showApiExceptionError(ApiException exception, String requestOrigin) {
        }

        @Override
        public void onValidStart() {
            validStart = true;
        }

        @Override
        public void hideExclusionWithOneElementInfoView() {

        }

        @Override
        public void showError(MercadoPagoError error, String requestOrigin) {
            errorShown = error;
        }

        @Override
        public void setContainerAnimationListeners() {

        }

        @Override
        public void setExclusionWithOneElementInfoView(PaymentMethod supportedPaymentMethod, boolean withAnimation) {
            onlyOnePMErrorViewShown = true;
            supportedPaymentMethodId = supportedPaymentMethod.getId();
        }

        @Override
        public void clearCardNumberInputLength() {
            cardNumberLengthDefault = true;
        }

        @Override
        public void clearErrorView() {
            errorState = false;
        }

        @Override
        public void checkClearCardView() {
            clearCardView = true;
        }

        @Override
        public void setBackButtonListeners() {
            initializeGuessingListeners = true;
        }

        @Override
        public void setCardNumberListeners(PaymentMethodGuessingController controller) {
            initializeGuessingListeners = true;
        }

        @Override
        public void setErrorSecurityCode() {

        }

        @Override
        public void setErrorCardNumber() {

        }

        @Override
        public void setErrorView(CardTokenException exception) {
            formDataErrorState = true;
            cardTokenError = exception;
        }

        @Override
        public void setErrorView(String mErrorState) {
            formDataErrorState = true;
        }

        @Override
        public void setSecurityCodeListeners() {
            initializeGuessingListeners = true;
        }

        @Override
        public void setIdentificationTypeListeners() {
            initializeGuessingListeners = true;
        }

        @Override
        public void setNextButtonListeners() {
            initializeGuessingListeners = true;
        }

        @Override
        public void setIdentificationNumberListeners() {
            initializeGuessingListeners = true;
        }

        @Override
        public void setSecurityCodeInputMaxLength(int length) {

        }

        @Override
        public void setSecurityCodeViewLocation(String location) {

        }

        @Override
        public void setIdentificationNumberRestrictions(String type) {

        }

        @Override
        public void setCardholderNameListeners() {
            initializeGuessingListeners = true;
        }

        @Override
        public void setExpiryDateListeners() {
            initializeGuessingListeners = true;
        }

        @Override
        public void setCardholderName(String cardholderName) {
            this.savedCardholderName = cardholderName;
        }

        @Override
        public void setCardNumberInputMaxLength(int length) {

        }

        @Override
        public void setErrorCardholderName() {

        }

        @Override
        public void setErrorExpiryDate() {

        }

        @Override
        public void setErrorIdentificationNumber() {

        }

        @Override
        public void setIdentificationNumber(String identificationNumber) {
            this.savedIdentificationNumber = identificationNumber;
        }

        @Override
        public void showIdentificationInput() {

        }

        @Override
        public void showSecurityCodeInput() {

        }

        @Override
        public void showInputContainer() {
            showInputContainer = true;
        }

        @Override
        public void showBankDeals() {
            bankDealsShown = true;
        }

        @Override
        public void hideBankDeals() {
            hideBankDeals = true;
        }

        @Override
        public void hideIdentificationInput() {
            hideIdentificationInput = true;
        }

        @Override
        public void hideSecurityCodeInput() {
            hideSecurityCodeInput = true;
        }

        @Override
        public void initializeTimer() {

        }

        @Override
        public void initializeIdentificationTypes(List<IdentificationType> identificationTypes) {
            identificationTypesInitialized = true;
        }

        @Override
        public void initializeTitle() {
            initializeGuessingForm = true;
        }

        @Override
        public void finishCardFlow(PaymentMethod paymentMethod, Token token, List<Issuer> issuers) {
        }

        @Override
        public void askForPaymentType() {
        }

        @Override
        public void showFinishCardFlow() {

        }
    }

    private class MockedProvider implements GuessingCardProvider {

        private static final String MULTIPLE_INSTALLMENTS = "multiple installments";
        private static final String MISSING_INSTALLMENTS = "missing installments";
        private static final String MISSING_PAYER_COSTS = "missing payer costs";
        private static final String MISSING_PUBLIC_KEY = "missing public key";
        private static final String MISSING_IDENTIFICATION_TYPES = "missing identification types";
        private static final String INVALID_IDENTIFICATION_NUMBER = "invalid identification number";
        private static final String INVALID_EMPTY_NAME = "invalid empty name";
        private static final String INVALID_EXPIRY_DATE = "invalid expiry date";
        private static final String SETTING_NOT_FOUND_FOR_BIN = "setting not found for bin";
        private static final String PAYMENT_METHODS_NOT_FOUND = "payment methods not found error";
        private static final String IDENTIFICATION_TYPES_NOT_FOUND = "identification types not found error";
        private static final String INVALID_FIELD = "invalid field";

        private boolean shouldFail;
        private MercadoPagoError failedResponse;
        private List<Installment> successfulInstallmentsResponse;
        private List<IdentificationType> successfulIdentificationTypesResponse;
        private List<BankDeal> successfulBankDealsResponse;
        private Token successfulTokenResponse;
        private List<Issuer> successfulIssuersResponse;
        private Discount successfulDiscountResponse;
        private List<PaymentMethod> successfulPaymentMethodsResponse;

        public void setResponse(MercadoPagoError exception) {
            shouldFail = true;
            failedResponse = exception;
        }

        public void setInstallmentsResponse(List<Installment> installmentList) {
            shouldFail = false;
            successfulInstallmentsResponse = installmentList;
        }

        public void setIdentificationTypesResponse(List<IdentificationType> identificationTypes) {
            shouldFail = false;
            successfulIdentificationTypesResponse = identificationTypes;
        }

        public void setIdentificationTypesResponse(MercadoPagoError exception) {
            shouldFail = true;
            failedResponse = exception;
        }

        public void setBankDealsResponse(List<BankDeal> bankDeals) {
            shouldFail = false;
            successfulBankDealsResponse = bankDeals;
        }

        public void setTokenResponse(Token token) {
            shouldFail = false;
            successfulTokenResponse = token;
        }

        public void setIssuersResponse(List<Issuer> issuers) {
            shouldFail = false;
            successfulIssuersResponse = issuers;
        }

        public void setDiscountResponse(Discount discount) {
            shouldFail = false;
            successfulDiscountResponse = discount;
        }

        public void setPaymentMethodsResponse(MercadoPagoError exception) {
            shouldFail = true;
            failedResponse = exception;
        }

        @Override
        public MPTrackingContext getTrackingContext() {
            return null;
        }

        @Override
        public String getMissingIdentificationTypesErrorMessage() {
            return MISSING_IDENTIFICATION_TYPES;
        }

        @Override
        public String getInvalidIdentificationNumberErrorMessage() {
            return INVALID_IDENTIFICATION_NUMBER;
        }

        @Override
        public String getInvalidEmptyNameErrorMessage() {
            return INVALID_EMPTY_NAME;
        }

        @Override
        public String getMissingPayerCostsErrorMessage() {
            return MISSING_PAYER_COSTS;
        }

        @Override
        public String getMissingInstallmentsForIssuerErrorMessage() {
            return MISSING_INSTALLMENTS;
        }

        @Override
        public String getInvalidExpiryDateErrorMessage() {
            return INVALID_EXPIRY_DATE;
        }

        @Override
        public String getMultipleInstallmentsForIssuerErrorMessage() {
            return MULTIPLE_INSTALLMENTS;
        }

        @Override
        public String getSettingNotFoundForBinErrorMessage() {
            return SETTING_NOT_FOUND_FOR_BIN;
        }

        @Override
        public String getMissingPublicKeyErrorMessage() {
            return MISSING_PUBLIC_KEY;
        }

        @Override
        public String getInvalidFieldErrorMessage() {
            return INVALID_FIELD;
        }

        @Override
        public void getInstallmentsAsync(String bin, BigDecimal amount, Long issuerId, String paymentMethodId,
            Integer diff,
            TaggedCallback<List<Installment>> taggedCallback) {
            if (shouldFail) {
                taggedCallback.onFailure(failedResponse);
            } else {
                taggedCallback.onSuccess(successfulInstallmentsResponse);
            }
        }

        @Override
        public void getIdentificationTypesAsync(TaggedCallback<List<IdentificationType>> taggedCallback) {
            if (shouldFail) {
                taggedCallback.onFailure(failedResponse);
            } else {
                taggedCallback.onSuccess(successfulIdentificationTypesResponse);
            }
        }

        @Override
        public void getBankDealsAsync(TaggedCallback<List<BankDeal>> taggedCallback) {
            if (shouldFail) {
                taggedCallback.onFailure(failedResponse);
            } else {
                taggedCallback.onSuccess(successfulBankDealsResponse);
            }
        }

        @Override
        public void createTokenAsync(CardToken cardToken, TaggedCallback<Token> taggedCallback) {
            if (shouldFail) {
                taggedCallback.onFailure(failedResponse);
            } else {
                taggedCallback.onSuccess(successfulTokenResponse);
            }
        }

        @Override
        public void getIssuersAsync(String paymentMethodId, String bin, TaggedCallback<List<Issuer>> taggedCallback) {
            if (shouldFail) {
                taggedCallback.onFailure(failedResponse);
            } else {
                taggedCallback.onSuccess(successfulIssuersResponse);
            }
        }

        @Override
        public void getPaymentMethodsAsync(TaggedCallback<List<PaymentMethod>> taggedCallback) {
            if (shouldFail) {
                taggedCallback.onFailure(failedResponse);
            } else {
                taggedCallback.onSuccess(successfulPaymentMethodsResponse);
            }
        }
    }
}
