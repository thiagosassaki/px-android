package com.mercadopago.android.px.paymentvault;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.core.PaymentMethodPlugin;
import com.mercadopago.android.px.internal.callbacks.OnSelectedCallback;
import com.mercadopago.android.px.internal.features.PaymentVaultPresenter;
import com.mercadopago.android.px.internal.features.PaymentVaultView;
import com.mercadopago.android.px.internal.features.hooks.Hook;
import com.mercadopago.android.px.internal.features.providers.PaymentVaultProvider;
import com.mercadopago.android.px.internal.repository.DiscountRepository;
import com.mercadopago.android.px.internal.repository.GroupsRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.repository.PluginRepository;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.mocks.PaymentMethodSearchs;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.CustomSearchItem;
import com.mercadopago.android.px.model.Discount;
import com.mercadopago.android.px.model.Payer;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentMethodSearch;
import com.mercadopago.android.px.model.PaymentMethodSearchItem;
import com.mercadopago.android.px.model.PaymentMethods;
import com.mercadopago.android.px.model.PaymentTypes;
import com.mercadopago.android.px.model.Site;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import com.mercadopago.android.px.preferences.PaymentPreference;
import com.mercadopago.android.px.utils.Discounts;
import com.mercadopago.android.px.utils.StubFailMpCall;
import com.mercadopago.android.px.utils.StubSuccessMpCall;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PaymentVaultPresenterTest {

    private static final String ANY_SITE_ID = "ANY_ID";

    private MockedView stubView = new MockedView();
    private MockedProvider stubProvider = new MockedProvider();
    private PaymentVaultPresenter presenter;

    @Mock private PaymentSettingRepository paymentSettingRepository;
    @Mock private CheckoutPreference checkoutPreference;
    @Mock private UserSelectionRepository userSelectionRepository;
    @Mock private PluginRepository pluginRepository;
    @Mock private DiscountRepository discountRepository;
    @Mock private GroupsRepository groupsRepository;
    @Mock private PaymentVaultView paymentVaultView;
    @Mock private PaymentVaultProvider paymentVaultProvider;

    @Mock private Site mockSite;

    @Before
    public void setUp() {
        when(checkoutPreference.getTotalAmount()).thenReturn(new BigDecimal(100));
        when(paymentSettingRepository.getCheckoutPreference()).thenReturn(checkoutPreference);
        when(checkoutPreference.getPaymentPreference()).thenReturn(new PaymentPreference());
        when(checkoutPreference.getSite()).thenReturn(mockSite);
        when(mockSite.getId()).thenReturn(ANY_SITE_ID);
        presenter = getBasePresenter(stubView, stubProvider);
    }

    @NonNull
    private PaymentVaultPresenter getBasePresenter(
        final PaymentVaultView view,
        final PaymentVaultProvider provider) {

        presenter = new PaymentVaultPresenter(paymentSettingRepository, userSelectionRepository,
            pluginRepository, discountRepository, groupsRepository);
        presenter.attachView(view);
        presenter.attachResourcesProvider(provider);

        return presenter;
    }

    @NonNull
    private PaymentVaultPresenter getPresenter() {
        return getBasePresenter(paymentVaultView, paymentVaultProvider);
    }

    @Test
    public void whenItemSelectedAvailableTrackIt() {
        final PaymentVaultView mockView = mock(PaymentVaultView.class);
        final PaymentVaultProvider mockProvider = mock(PaymentVaultProvider.class);
        final PaymentMethodSearchItem mockPaymentOptions = mock(PaymentMethodSearchItem.class);
        presenter.attachView(mockView);
        presenter.attachResourcesProvider(mockProvider);
        presenter.setSelectedSearchItem(mockPaymentOptions);
        presenter.trackChildrenScreen();
        verify(mockProvider).trackChildrenScreen(mockPaymentOptions, mockSite.getId());
        verifyNoMoreInteractions(mockProvider);
        verifyNoMoreInteractions(mockView);
    }

    @Test
    public void whenItemSelectedNotAvailableTrackFirstOfGroup() {
        final PaymentVaultView mockView = mock(PaymentVaultView.class);
        final PaymentVaultProvider mockProvider = mock(PaymentVaultProvider.class);

        final PaymentMethodSearch mockPaymentOptions = mock(PaymentMethodSearch.class);
        final PaymentMethodSearchItem mockPaymentOptionsItem = mock(PaymentMethodSearchItem.class);

        final List<PaymentMethodSearchItem> paymentMethodSearchItems = Arrays.asList(mockPaymentOptionsItem);
        when(mockPaymentOptions.getGroups()).thenReturn(paymentMethodSearchItems);
        when(mockPaymentOptions.hasSearchItems()).thenReturn(true);

        when(groupsRepository.getGroups()).thenReturn(new StubSuccessMpCall<>(mockPaymentOptions));

        presenter = new PaymentVaultPresenter(paymentSettingRepository, userSelectionRepository,
            pluginRepository, discountRepository, groupsRepository);

        presenter.attachView(mockView);
        presenter.attachResourcesProvider(mockProvider);
        presenter.initialize();

        presenter.trackChildrenScreen();

        verify(mockProvider).trackChildrenScreen(paymentMethodSearchItems.get(0), mockSite.getId());
    }

    @Test
    public void ifNoPaymentMethodsAvailableThenShowError() {
        PaymentMethodSearch paymentMethodSearch = new PaymentMethodSearch();
        when(groupsRepository.getGroups()).thenReturn(new StubSuccessMpCall<>(paymentMethodSearch));
        presenter.initialize();
        assertTrue(stubView.errorShown.getMessage().equals(MockedProvider.EMPTY_PAYMENT_METHODS));
    }

    @Test
    public void ifPaymentMethodSearchHasItemsShowThem() {
        final PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getPaymentMethodWithoutCustomOptionsMLA();
        when(groupsRepository.getGroups()).thenReturn(new StubSuccessMpCall<>(paymentMethodSearch));
        presenter.initialize();
        assertEquals(paymentMethodSearch.getGroups(), stubView.searchItemsShown);
    }

    @Test
    public void ifPaymentMethodSearchHasPayerCustomOptionsShowThem() {
        PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getCompletePaymentMethodSearchMLA();
        when(groupsRepository.getGroups()).thenReturn(new StubSuccessMpCall<>(paymentMethodSearch));
        presenter.initialize();
        assertEquals(paymentMethodSearch.getCustomSearchItems(), stubView.customOptionsShown);
    }

    @Test
    public void whenItemWithChildrenSelectedThenShowChildren() {

        PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getCompletePaymentMethodSearchMLA();
        when(groupsRepository.getGroups()).thenReturn(new StubSuccessMpCall<>(paymentMethodSearch));

        presenter.initialize();
        stubView.simulateItemSelection(1);

        assertEquals(paymentMethodSearch.getGroups().get(1).getChildren(), stubView.searchItemsShown);
    }

    //Automatic selections

    @Test
    public void ifOnlyUniqueSearchItemAvailableRestartWithItSelected() {

        PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getPaymentMethodSearchWithOnlyTicketMLA();
        when(groupsRepository.getGroups()).thenReturn(new StubSuccessMpCall<>(paymentMethodSearch));

        presenter.initialize();

        assertEquals(paymentMethodSearch.getGroups().get(0), stubView.itemShown);
    }

    @Test
    public void ifOnlyCardPaymentTypeAvailableStartCardFlow() {
        final PaymentMethodSearch paymentMethodSearch =
            PaymentMethodSearchs.getPaymentMethodSearchWithOnlyCreditCardMLA();
        final PaymentVaultPresenter presenter = getPresenter();

        when(groupsRepository.getGroups()).thenReturn(new StubSuccessMpCall<>(paymentMethodSearch));

        presenter.initialize();

        verify(paymentVaultView).showAmount(discountRepository, checkoutPreference.getTotalAmount(), mockSite);
        verify(paymentVaultView).onSuccessCodeDiscountCallback(discountRepository.getDiscount());
        verify(paymentVaultProvider).trackInitialScreen(paymentMethodSearch,mockSite.getId());
        verify(paymentVaultView).setTitle(paymentVaultProvider.getTitle());
        verify(paymentVaultView).startCardFlow(true);
        verify(paymentSettingRepository, atLeastOnce()).getCheckoutPreference();
        verify(paymentSettingRepository).configure(checkoutPreference);

        verifyNoMoreInteractions(paymentVaultView);
        verifyNoMoreInteractions(paymentSettingRepository);
    }

    @Test
    public void ifOnlyCardPaymentTypeAvailableAndCardAvailableDoNotSelectAutomatically() {

        PaymentMethodSearch paymentMethodSearch =
            PaymentMethodSearchs.getPaymentMethodSearchWithOnlyCreditCardAndOneCardMLA();
        when(groupsRepository.getGroups()).thenReturn(new StubSuccessMpCall<>(paymentMethodSearch));

        presenter.initialize();

        assertTrue(stubView.customOptionsShown != null);
        assertFalse(stubView.cardFlowStarted);
        assertFalse(stubView.isItemShown);
    }

    @Test
    public void ifOnlyCardPaymentTypeAvailableAndAccountMoneyAvailableDoNotSelectAutomatically() {

        final PaymentMethodSearch paymentMethodSearch =
            PaymentMethodSearchs.getPaymentMethodSearchWithOnlyCreditCardAndAccountMoneyMLA();
        when(groupsRepository.getGroups()).thenReturn(new StubSuccessMpCall<>(paymentMethodSearch));

        presenter.initialize();

        assertTrue(stubView.customOptionsShown != null);
        assertFalse(stubView.cardFlowStarted);
        assertFalse(stubView.isItemShown);
    }

    @Test
    public void ifOnlyOffPaymentTypeAvailableAndAccountMoneyAvailableDoNotSelectAutomatically() {

        PaymentMethodSearch paymentMethodSearch =
            PaymentMethodSearchs.getPaymentMethodSearchWithOnlyOneOffTypeAndAccountMoneyMLA();
        when(groupsRepository.getGroups()).thenReturn(new StubSuccessMpCall<>(paymentMethodSearch));

        presenter.initialize();

        assertTrue(stubView.customOptionsShown != null);
        assertFalse(stubView.cardFlowStarted);
        assertFalse(stubView.isItemShown);
    }

    //User selections

    @Test
    public void ifItemSelectedShowItsChildren() {

        PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getCompletePaymentMethodSearchMLA();
        when(groupsRepository.getGroups()).thenReturn(new StubSuccessMpCall<>(paymentMethodSearch));

        presenter.initialize();

        stubView.simulateItemSelection(1);

        assertEquals(paymentMethodSearch.getGroups().get(1).getChildren(), stubView.searchItemsShown);
        assertEquals(paymentMethodSearch.getGroups().get(1), stubView.itemShown);
    }

    @Test
    public void ifCardPaymentTypeSelectedStartCardFlow() {

        PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getCompletePaymentMethodSearchMLA();
        when(groupsRepository.getGroups()).thenReturn(new StubSuccessMpCall<>(paymentMethodSearch));

        presenter.initialize();

        stubView.simulateItemSelection(0);

        assertTrue(stubView.cardFlowStarted);
    }

    @Test
    public void ifSavedCardSelectedStartSavedCardFlow() {

        PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getCompletePaymentMethodSearchMLA();
        when(groupsRepository.getGroups()).thenReturn(new StubSuccessMpCall<>(paymentMethodSearch));

        presenter.initialize();

        stubView.simulateCustomItemSelection(1);

        assertTrue(stubView.savedCardFlowStarted);
        assertTrue(stubView.savedCardSelected.equals(paymentMethodSearch.getCards().get(0)));
    }

    //Payment Preference tests
    @Test
    public void whenAllPaymentMethodsExcludedShowError() {
        final PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setExcludedPaymentTypeIds(PaymentTypes.getAllPaymentTypes());

        when(checkoutPreference.getPaymentPreference()).thenReturn(paymentPreference);
        presenter.initialize();

        assertEquals(MockedProvider.ALL_TYPES_EXCLUDED, stubView.errorShown.getMessage());
    }

    @Test
    public void ifInvalidDefaultInstallmentsShowError() {
        final PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setDefaultInstallments(BigDecimal.ONE.negate().intValue());
        when(checkoutPreference.getPaymentPreference()).thenReturn(paymentPreference);
        presenter.initialize();
        assertEquals(MockedProvider.INVALID_DEFAULT_INSTALLMENTS, stubView.errorShown.getMessage());
    }

    @Test
    public void ifInvalidMaxInstallmentsShowError() {
        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setMaxAcceptedInstallments(BigDecimal.ONE.negate().intValue());
        when(checkoutPreference.getPaymentPreference()).thenReturn(paymentPreference);
        presenter.initialize();

        assertEquals(MockedProvider.INVALID_MAX_INSTALLMENTS, stubView.errorShown.getMessage());
    }

    @Test
    public void ifMaxSavedCardNotSetDoNotLimitCardsShown() {

        PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getCompletePaymentMethodSearchMLA();
        when(groupsRepository.getGroups()).thenReturn(new StubSuccessMpCall<>(paymentMethodSearch));

        presenter.initialize();

        assertEquals(paymentMethodSearch.getCustomSearchItems().size(), stubView.customOptionsShown.size());
    }

    //Discounts
    @Test
    public void ifDiscountsAreNotEnabledNotShowDiscountRow() {

        PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getCompletePaymentMethodSearchMLA();

        when(groupsRepository.getGroups()).thenReturn(new StubSuccessMpCall<>(paymentMethodSearch));

        presenter.initialize();

        stubView.simulateItemSelection(0);

        assertTrue(stubView.showedDiscountRow);
    }

    @Test
    public void ifDiscountsAreEnabledGetDirectDiscount() {

        PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getCompletePaymentMethodSearchMLA();
        when(groupsRepository.getGroups()).thenReturn(new StubSuccessMpCall<>(paymentMethodSearch));

        Discount discount = Discounts.getDiscountWithAmountOffMLA();
        stubProvider.setDiscountResponse(discount);

        presenter.initialize();
    }

    @Test
    public void ifHasNotDirectDiscountsShowDiscountRow() {

        PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getCompletePaymentMethodSearchMLA();
        when(groupsRepository.getGroups()).thenReturn(new StubSuccessMpCall<>(paymentMethodSearch));

        ApiException apiException = Discounts.getDoNotFindCampaignApiException();
        MercadoPagoError mpException = new MercadoPagoError(apiException, "");
        stubProvider.setDiscountResponse(mpException);

        presenter.initialize();

        stubView.simulateItemSelection(0);

        assertTrue(stubProvider.failedResponse.getApiException().getError()
            .equals(stubProvider.CAMPAIGN_DOES_NOT_MATCH_ERROR));
        assertTrue(stubView.showedDiscountRow);
    }

    @Test
    public void ifIsDirectDiscountNotEnabledNotGetDirectDiscount() {

        PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getCompletePaymentMethodSearchMLA();
        when(groupsRepository.getGroups()).thenReturn(new StubSuccessMpCall<>(paymentMethodSearch));

        presenter.initialize();

        stubView.simulateItemSelection(0);

        assertTrue(stubView.showedDiscountRow);
    }

    @Test
    public void whenGroupsRetrievalReturnsAliExceptionThenShowError() {
        final ApiException apiException = new ApiException();
        apiException.setMessage("Mocked failure");
        when(groupsRepository.getGroups()).thenReturn(new StubFailMpCall<PaymentMethodSearch>(apiException));
        final PaymentVaultView mock = mock(PaymentVaultView.class);
        presenter.attachView(mock);
        presenter.initialize();
        verify(mock).showError(any(MercadoPagoError.class), anyString());
    }

    @Test
    public void whenResourcesRetrievalFailedAndRecoverRequestedThenRepeatRetrieval() {
        //Set Up
        ApiException apiException = new ApiException();
        apiException.setMessage("Mocked failure");
        MercadoPagoError mercadoPagoError = new MercadoPagoError(apiException, "");
        stubProvider.setResponse(mercadoPagoError);

        when(groupsRepository.getGroups())
            .thenReturn(new StubSuccessMpCall<>(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA()));
        presenter.initialize();
        //Presenter gets resources, fails

        presenter.recoverFromFailure();

        assertFalse(stubView.searchItemsShown.isEmpty());
    }

    @Test
    public void ifPaymentMethodSearchSetAndHasItemsThenShowThem() {

        final PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getPaymentMethodWithoutCustomOptionsMLA();
        when(groupsRepository.getGroups()).thenReturn(new StubSuccessMpCall<>(paymentMethodSearch));
        presenter.initialize();

        assertEquals(paymentMethodSearch.getGroups(), stubView.searchItemsShown);
    }

    @Test
    public void ifPaymentMethodSearchItemIsNotCardAndDoesNotHaveChildrenThenStartPaymentMethodsSelection() {

        PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getCompletePaymentMethodSearchMLA();
        paymentMethodSearch.getGroups().get(1).getChildren()
            .removeAll(paymentMethodSearch.getGroups().get(1).getChildren());

        when(groupsRepository.getGroups()).thenReturn(new StubSuccessMpCall<>(paymentMethodSearch));

        presenter.initialize();

        stubView.simulateItemSelection(1);
        assertTrue(stubView.paymentMethodSelectionStarted);
    }

    @Test
    public void ifPaymentMethodTypeSelectedThenSelectPaymentMethod() {
        PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getPaymentMethodSearchWithPaymentMethodOnTop();
        when(groupsRepository.getGroups()).thenReturn(new StubSuccessMpCall<>(paymentMethodSearch));
        presenter.initialize();

        stubView.simulateItemSelection(1);
        assertTrue(paymentMethodSearch.getGroups().get(1).getId().equals(stubView.selectedPaymentMethod.getId()));
    }

    @Test
    public void whenHasCustomItemsThenShowThemAll() {
        // 6 Saved Cards + Account Money
        final PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getPaymentMethodSearchWithSavedCardsMLA();

        when(groupsRepository.getGroups()).thenReturn(new StubSuccessMpCall<>(paymentMethodSearch));

        presenter.attachView(stubView);
        presenter.attachResourcesProvider(stubProvider);

        // Set show all saved cards

        presenter.initialize();

        assertEquals(stubView.customOptionsShown.size(), paymentMethodSearch.getCustomSearchItems().size());
    }

    @Test
    public void whenBoletoSelectedThenCollectPayerInformation() {
        final PaymentVaultPresenter presenter = getPresenter();
        when(groupsRepository.getGroups()).thenReturn(new StubSuccessMpCall<>(PaymentMethodSearchs.getPaymentMethodSearchWithOnlyBolbradescoMLB()));

        presenter.initialize();

        verify(paymentVaultView).collectPayerInformation();
    }

    @Test
    public void whenPayerInformationReceivedThenFinishWithPaymentMethodSelection() {
        final PaymentVaultPresenter presenter = getPresenter();

        presenter.onPayerInformationReceived();

        verify(paymentVaultView).finishPaymentMethodSelection(userSelectionRepository.getPaymentMethod());
    }

    private static class MockedProvider implements PaymentVaultProvider {

        private static final String INVALID_SITE = "invalid site";
        private static final String INVALID_AMOUNT = "invalid amount";
        private static final String ALL_TYPES_EXCLUDED = "all types excluded";
        private static final String INVALID_DEFAULT_INSTALLMENTS = "invalid default installments";
        private static final String INVALID_MAX_INSTALLMENTS = "invalid max installments";
        private static final String STANDARD_ERROR_MESSAGE = "standard error";
        private static final String EMPTY_PAYMENT_METHODS = "empty payment methods";
        private static final String CAMPAIGN_DOES_NOT_MATCH_ERROR = "campaign-doesnt-match";

        private boolean shouldFail;
        private boolean shouldDiscountFail;
        private PaymentMethodSearch successfulResponse;
        private Discount successfulDiscountResponse;
        private MercadoPagoError failedResponse;

        public void setResponse(MercadoPagoError exception) {
            shouldFail = true;
            failedResponse = exception;
        }

        public void setDiscountResponse(Discount discount) {
            shouldDiscountFail = false;
            successfulDiscountResponse = discount;
        }

        public void setDiscountResponse(MercadoPagoError exception) {
            shouldDiscountFail = true;
            failedResponse = exception;
        }

        @Override
        public String getTitle() {
            return "¿Cómo quieres pagar?";
        }

        @Override
        public String getAllPaymentTypesExcludedErrorMessage() {
            return ALL_TYPES_EXCLUDED;
        }

        @Override
        public String getInvalidDefaultInstallmentsErrorMessage() {
            return INVALID_DEFAULT_INSTALLMENTS;
        }

        @Override
        public String getInvalidMaxInstallmentsErrorMessage() {
            return INVALID_MAX_INSTALLMENTS;
        }

        @Override
        public String getStandardErrorMessage() {
            return STANDARD_ERROR_MESSAGE;
        }

        @Override
        public String getEmptyPaymentMethodsErrorMessage() {
            return EMPTY_PAYMENT_METHODS;
        }

        @Override
        public void trackInitialScreen(PaymentMethodSearch paymentMethodSearch, String siteId) {

        }

        @Override
        public void trackChildrenScreen(@NonNull PaymentMethodSearchItem paymentMethodSearchItem,
            @NonNull String siteId) {

        }
    }

    private static class MockedView implements PaymentVaultView {

        private List<PaymentMethodSearchItem> searchItemsShown;
        private MercadoPagoError errorShown;
        private List<CustomSearchItem> customOptionsShown;
        private PaymentMethodSearchItem itemShown;
        private boolean cardFlowStarted = false;
        private boolean isItemShown;
        private PaymentMethod selectedPaymentMethod;
        private OnSelectedCallback<PaymentMethodSearchItem> itemSelectionCallback;
        private OnSelectedCallback<CustomSearchItem> customItemSelectionCallback;
        private String title;
        private boolean savedCardFlowStarted;
        private boolean payerInformationStarted;
        private Card savedCardSelected;
        private Boolean showedDiscountRow;

        private boolean paymentMethodSelectionStarted = false;
        private Payer selectedPayer;

        @Override
        public void startSavedCardFlow(final Card card) {
            this.savedCardFlowStarted = true;
            this.savedCardSelected = card;
        }

        @Override
        public void showPaymentMethodPluginActivity() {
            //Not yet tested
        }

        @Override
        public void showSelectedItem(PaymentMethodSearchItem item) {
            this.itemShown = item;
            this.isItemShown = true;
            this.searchItemsShown = item.getChildren();
        }

        @Override
        public void showProgress() {
            //Not yet tested
        }

        @Override
        public void hideProgress() {
            //Not yet tested
        }

        @Override
        public void showCustomOptions(List<CustomSearchItem> customSearchItems,
            OnSelectedCallback<CustomSearchItem> customSearchItemOnSelectedCallback) {
            this.customOptionsShown = customSearchItems;
            this.customItemSelectionCallback = customSearchItemOnSelectedCallback;
        }

        @Override
        public void showPluginOptions(final Collection<PaymentMethodPlugin> items,
            final PaymentMethodPlugin.PluginPosition position) {

        }

        @Override
        public void showSearchItems(List<PaymentMethodSearchItem> searchItems,
            OnSelectedCallback<PaymentMethodSearchItem> paymentMethodSearchItemSelectionCallback) {
            this.searchItemsShown = searchItems;
            this.itemSelectionCallback = paymentMethodSearchItemSelectionCallback;
        }

        @Override
        public void showError(MercadoPagoError mpException, String requestOrigin) {
            errorShown = mpException;
        }

        @Override
        public void setTitle(String title) {
            this.title = title;
        }

        @Override
        public void startCardFlow(final Boolean automaticallySelection) {
            cardFlowStarted = true;
        }

        @Override
        public void startPaymentMethodsSelection(final PaymentPreference paymentPreference) {
            paymentMethodSelectionStarted = true;
        }

        @Override
        public void finishPaymentMethodSelection(PaymentMethod selectedPaymentMethod) {
            this.selectedPaymentMethod = selectedPaymentMethod;
        }

        @Override
        public void showAmount(@NonNull final DiscountRepository discountRepository,
            @NonNull final BigDecimal totalAmount,
            @NonNull final Site site) {
            this.showedDiscountRow = true;
        }

        @Override
        public void collectPayerInformation() {
            this.payerInformationStarted = true;
        }

        @Override
        public void cleanPaymentMethodOptions() {
            //Not yet tested
        }

        @Override
        public void showHook(Hook hook, int code) {
            //Not yet tested
        }

        @Override
        public void showDetailDialog() {
            //Do nothing
        }

        @Override
        public void showDiscountInputDialog() {
            //Do nothing
        }

        @Override
        public void onSuccessCodeDiscountCallback(Discount discount) {
            //Do nothing
        }

        @Override
        public void onFailureCodeDiscountCallback() {
            //Do nothing
        }

        private void simulateItemSelection(int index) {
            itemSelectionCallback.onSelected(searchItemsShown.get(index));
        }

        private void simulateCustomItemSelection(int index) {
            customItemSelectionCallback.onSelected(customOptionsShown.get(index));
        }
    }
}
