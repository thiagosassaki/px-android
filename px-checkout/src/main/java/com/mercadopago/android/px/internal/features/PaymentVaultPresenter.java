package com.mercadopago.android.px.internal.features;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.core.PaymentMethodPlugin;
import com.mercadopago.android.px.internal.base.MvpPresenter;
import com.mercadopago.android.px.internal.callbacks.FailureRecovery;
import com.mercadopago.android.px.internal.callbacks.OnSelectedCallback;
import com.mercadopago.android.px.internal.datasource.CheckoutStore;
import com.mercadopago.android.px.internal.features.hooks.Hook;
import com.mercadopago.android.px.internal.features.hooks.HookHelper;
import com.mercadopago.android.px.internal.features.providers.PaymentVaultProvider;
import com.mercadopago.android.px.internal.repository.DiscountRepository;
import com.mercadopago.android.px.internal.repository.GroupsRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.repository.PluginRepository;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.internal.view.AmountView;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.CustomSearchItem;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentMethodSearch;
import com.mercadopago.android.px.model.PaymentMethodSearchItem;
import com.mercadopago.android.px.model.PaymentMethods;
import com.mercadopago.android.px.model.PaymentTypes;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.preferences.PaymentPreference;
import com.mercadopago.android.px.services.Callback;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class PaymentVaultPresenter extends MvpPresenter<PaymentVaultView, PaymentVaultProvider>
    implements AmountView.OnClick {

    private static final String MISMATCHING_PAYMENT_METHOD_ERROR = "Payment method in search not found";

    @NonNull
    private final PaymentSettingRepository configuration;
    @NonNull
    private final UserSelectionRepository userSelectionRepository;
    @NonNull
    private final PluginRepository pluginRepository;

    private final DiscountRepository discountRepository;
    @NonNull
    private final GroupsRepository groupsRepository;

    private PaymentMethodSearchItem selectedSearchItem;
    private PaymentMethodSearchItem resumeItem;
    private boolean skipHook = false;
    private boolean hook1Displayed = false;
    /* default */ PaymentMethodSearch paymentMethodSearch;
    private FailureRecovery failureRecovery;

    public PaymentVaultPresenter(@NonNull final PaymentSettingRepository configuration,
        @NonNull final UserSelectionRepository userSelectionRepository,
        @NonNull final PluginRepository pluginService,
        @NonNull final DiscountRepository discountRepository,
        @NonNull final GroupsRepository groupsRepository) {
        this.configuration = configuration;
        this.userSelectionRepository = userSelectionRepository;
        pluginRepository = pluginService;
        this.discountRepository = discountRepository;
        this.groupsRepository = groupsRepository;
    }

    public void initialize() {
        try {
            validateParameters();
            initPaymentVaultFlow();
        } catch (final IllegalStateException exception) {
            getView().showError(new MercadoPagoError(exception.getMessage(), false), "");
        }
    }

    public void initPaymentVaultFlow() {
        initializeAmountRow();

        groupsRepository.getGroups().enqueue(new Callback<PaymentMethodSearch>() {
            @Override
            public void success(final PaymentMethodSearch paymentMethodSearch) {
                if (isViewAttached()) {
                    PaymentVaultPresenter.this.paymentMethodSearch = paymentMethodSearch;
                    getView().onSuccessCodeDiscountCallback(discountRepository.getDiscount());
                    initPaymentMethodSearch();
                }
            }

            @Override
            public void failure(final ApiException apiException) {
                getView()
                    .showError(new MercadoPagoError(apiException, ApiException.ErrorCodes.PAYMENT_METHOD_NOT_FOUND),
                        ApiException.ErrorCodes.PAYMENT_METHOD_NOT_FOUND);
                setFailureRecovery(new FailureRecovery() {
                    @Override
                    public void recover() {
                        initPaymentVaultFlow();
                    }
                });
                getView().onFailureCodeDiscountCallback();
            }
        });
    }

    /* default */ void setFailureRecovery(final FailureRecovery failureRecovery) {
        this.failureRecovery = failureRecovery;
    }

    public void recoverFromFailure() {
        if (failureRecovery != null) {
            failureRecovery.recover();
        }
    }

    public void initializeAmountRow() {
        if (isViewAttached()) {
            getView().showAmount(discountRepository,
                configuration.getCheckoutPreference().getTotalAmount(),
                configuration.getCheckoutPreference().getSite());
        }
    }

    public void onPayerInformationReceived() {
        getView().finishPaymentMethodSelection(userSelectionRepository.getPaymentMethod());
    }

    private void validateParameters() throws IllegalStateException {
        final PaymentPreference paymentPreference = configuration.getCheckoutPreference().getPaymentPreference();
        if (!paymentPreference.validMaxInstallments()) {
            throw new IllegalStateException(getResourcesProvider().getInvalidMaxInstallmentsErrorMessage());
        }
        if (!paymentPreference.validDefaultInstallments()) {
            throw new IllegalStateException(getResourcesProvider().getInvalidDefaultInstallmentsErrorMessage());
        }
        if (!paymentPreference.excludedPaymentTypesValid()) {
            throw new IllegalStateException(getResourcesProvider().getAllPaymentTypesExcludedErrorMessage());
        }
    }

    public boolean isItemSelected() {
        return selectedSearchItem != null;
    }

    /* default */ void initPaymentMethodSearch() {
        trackInitialScreen();
        getView().setTitle(getResourcesProvider().getTitle());
        showPaymentMethodGroup();
    }

    private void showPaymentMethodGroup() {
        if (isItemSelected()) {
            showSelectedItemChildren();
        } else {
            resolveAvailablePaymentMethods();
        }
    }

    private void showSelectedItemChildren() {
        trackChildrenScreen();
        getView().setTitle(selectedSearchItem.getChildrenHeader());
        getView().showSearchItems(selectedSearchItem.getChildren(), getPaymentMethodSearchItemSelectionCallback());
        getView().hideProgress();
    }

    private void resolveAvailablePaymentMethods() {
        if (noPaymentMethodsAvailable()) {
            showEmptyPaymentMethodsError();
        } else if (isOnlyOneItemAvailable() && !isDiscountAvailable()) {
            if (pluginRepository.hasEnabledPaymentMethodPlugin()) {
                selectPluginPaymentMethod(pluginRepository.getFirstEnabledPlugin());
            } else if (paymentMethodSearch.getGroups() != null && !paymentMethodSearch.getGroups().isEmpty()) {
                selectItem(paymentMethodSearch.getGroups().get(0), true);
            } else if (paymentMethodSearch.getCustomSearchItems() != null
                && !paymentMethodSearch.getCustomSearchItems().isEmpty()) {
                if (PaymentTypes.CREDIT_CARD.equals(paymentMethodSearch.getCustomSearchItems().get(0).getType())) {
                    selectCard(paymentMethodSearch.getCustomSearchItems().get(0));
                }
            }
        } else {
            showAvailableOptions();
            getView().hideProgress();
        }
    }

    private void selectItem(final PaymentMethodSearchItem item) {
        selectItem(item, false);
    }

    private void selectItem(final PaymentMethodSearchItem item, final Boolean automaticSelection) {
        if (item.hasChildren()) {
            getView().showSelectedItem(item);
        } else if (item.isPaymentType()) {
            startNextStepForPaymentType(item, automaticSelection);
        } else if (item.isPaymentMethod()) {
            resolvePaymentMethodSelection(item);
        }
    }

    private void showAvailableOptions() {
        final Collection<PaymentMethodPlugin> paymentMethodPluginList =
            pluginRepository.getEnabledPlugins();

        getView().showPluginOptions(paymentMethodPluginList, PaymentMethodPlugin.PluginPosition.TOP);

        if (paymentMethodSearch.hasCustomSearchItems()) {
            final List<CustomSearchItem> shownCustomItems;
            shownCustomItems = paymentMethodSearch.getCustomSearchItems();
            getView().showCustomOptions(shownCustomItems, getCustomOptionCallback());
        }

        if (searchItemsAvailable()) {
            getView().showSearchItems(paymentMethodSearch.getGroups(), getPaymentMethodSearchItemSelectionCallback());
        }

        getView().showPluginOptions(paymentMethodPluginList, PaymentMethodPlugin.PluginPosition.BOTTOM);
    }

    private OnSelectedCallback<PaymentMethodSearchItem> getPaymentMethodSearchItemSelectionCallback() {
        return new OnSelectedCallback<PaymentMethodSearchItem>() {
            @Override
            public void onSelected(PaymentMethodSearchItem item) {
                selectItem(item);
            }
        };
    }

    private OnSelectedCallback<CustomSearchItem> getCustomOptionCallback() {
        return new OnSelectedCallback<CustomSearchItem>() {
            @Override
            public void onSelected(final CustomSearchItem searchItem) {
                selectCard(searchItem);
            }
        };
    }

    private void selectCard(final CustomSearchItem item) {
        if (PaymentTypes.isCardPaymentType(item.getType())) {
            final Card card = getCardWithPaymentMethod(item);
            userSelectionRepository.select(card);
            //TODO ver que pasa si selectedCard es null
            getView().startSavedCardFlow(card);
        }
    }

    private Card getCardWithPaymentMethod(final CustomSearchItem searchItem) {
        final PaymentMethod paymentMethod = paymentMethodSearch.getPaymentMethodById(searchItem.getPaymentMethodId());
        final Card selectedCard = getCardById(paymentMethodSearch.getCards(), searchItem.getId());
        if (paymentMethod != null) {
            selectedCard.setPaymentMethod(paymentMethod);
            if (selectedCard.getSecurityCode() == null && paymentMethod.getSettings() != null &&
                paymentMethod.getSettings().get(0) != null) {
                selectedCard.setSecurityCode(paymentMethod.getSettings().get(0).getSecurityCode());
            }
        }
        return selectedCard;
    }

    private Card getCardById(final Iterable<Card> savedCards, final String cardId) {
        Card foundCard = null;
        for (final Card card : savedCards) {
            if (card.getId().equals(cardId)) {
                foundCard = card;
                break;
            }
        }
        return foundCard;
    }

    private void startNextStepForPaymentType(final PaymentMethodSearchItem item, final boolean automaticSelection) {

        final String itemId = item.getId();
        if (skipHook || (!hook1Displayed && !showHook1(itemId))) {
            skipHook = false;
            if (PaymentTypes.isCardPaymentType(itemId)) {
                // TODO refactor renew configuration for screen recursion
                configuration.getCheckoutPreference().getPaymentPreference().setDefaultPaymentTypeId(itemId);
                configuration.configure(configuration.getCheckoutPreference());
                getView().startCardFlow(automaticSelection);
            } else {
                getView().startPaymentMethodsSelection(configuration.getCheckoutPreference().getPaymentPreference());
            }
        } else {
            resumeItem = item;
        }
    }

    private void resolvePaymentMethodSelection(final PaymentMethodSearchItem item) {

        final PaymentMethod selectedPaymentMethod = paymentMethodSearch.getPaymentMethodBySearchItem(item);
        userSelectionRepository.select(selectedPaymentMethod);

        if (skipHook || (!hook1Displayed && !showHook1(selectedPaymentMethod.getPaymentTypeId()))) {
            skipHook = false;
            if (selectedPaymentMethod == null) {
                showMismatchingPaymentMethodError();
            } else if (selectedPaymentMethod.getId().equals(PaymentMethods.BRASIL.BOLBRADESCO)) {
                getView().collectPayerInformation();
            } else {
                getView().finishPaymentMethodSelection(selectedPaymentMethod);
            }
        } else {
            resumeItem = item;
        }
    }

    public boolean isOnlyOneItemAvailable() {
        int groupCount = 0;
        int customCount = 0;

        if (paymentMethodSearch != null && paymentMethodSearch.hasSearchItems()) {
            groupCount = paymentMethodSearch.getGroups().size();
        }

        if (paymentMethodSearch != null && paymentMethodSearch.hasCustomSearchItems()) {
            customCount = paymentMethodSearch.getCustomSearchItems().size();
        }

        int pluginCount = pluginRepository.getPaymentMethodPluginCount();

        return groupCount + customCount + pluginCount == 1;
    }

    private boolean searchItemsAvailable() {
        return paymentMethodSearch != null && paymentMethodSearch.getGroups() != null
            && (!paymentMethodSearch.getGroups().isEmpty() || pluginRepository.hasEnabledPaymentMethodPlugin());
    }

    private boolean noPaymentMethodsAvailable() {
        return (paymentMethodSearch.getGroups() == null || paymentMethodSearch.getGroups().isEmpty())
            &&
            (paymentMethodSearch.getCustomSearchItems() == null || paymentMethodSearch.getCustomSearchItems().isEmpty())
            && !pluginRepository.hasEnabledPaymentMethodPlugin();
    }

    private void showEmptyPaymentMethodsError() {
        final String errorMessage = getResourcesProvider().getEmptyPaymentMethodsErrorMessage();
        getView().showError(new MercadoPagoError(errorMessage, false), "");
    }

    private void showMismatchingPaymentMethodError() {
        final String errorMessage = getResourcesProvider().getStandardErrorMessage();
        getView().showError(new MercadoPagoError(errorMessage, MISMATCHING_PAYMENT_METHOD_ERROR, false), "");
    }

    public PaymentMethodSearchItem getSelectedSearchItem() {
        return selectedSearchItem;
    }

    public void setSelectedSearchItem(PaymentMethodSearchItem mSelectedSearchItem) {
        selectedSearchItem = mSelectedSearchItem;
    }

    //###Hooks HACKS #######################################################

    public void onHookContinue() {
        if (resumeItem != null) {
            skipHook = true;
            selectItem(resumeItem, true);
        }
    }

    public void onHookReset() {
        hook1Displayed = false;
        resumeItem = null;
    }

    public boolean showHook1(final String typeId) {
        return showHook1(typeId, MercadoPagoComponents.Activities.HOOK_1);
    }

    public boolean showHook1(final String typeId, final int requestCode) {

        final Map<String, Object> data = CheckoutStore.getInstance().getData();
        final Hook hook = HookHelper.activateBeforePaymentMethodConfig(
            CheckoutStore.getInstance().getCheckoutHooks(), typeId, data);

        if (resumeItem == null && hook != null && getView() != null) {
            hook1Displayed = true;
            getView().showHook(hook, requestCode);
            return true;
        }
        return false;
    }

    public void trackInitialScreen() {
        getResourcesProvider()
            .trackInitialScreen(paymentMethodSearch, configuration.getCheckoutPreference().getSite().getId());
    }

    /**
     * When users selects option then track payment selected method.
     * If there is no option selected by the user then track the first available.
     */
    public void trackChildrenScreen() {
        if (selectedSearchItem != null) {
            getResourcesProvider()
                .trackChildrenScreen(selectedSearchItem, configuration.getCheckoutPreference().getSite().getId());
        } else if (paymentMethodSearch.hasSearchItems()) {
            getResourcesProvider().trackChildrenScreen(paymentMethodSearch.getGroups().get(0),
                configuration.getCheckoutPreference().getSite().getId());
        } else {
            throw new IllegalStateException("No payment method available to track");
        }
    }

    @Override
    public void onDetailClicked() {
        getView().showDetailDialog();
    }

    @Override
    public void onInputRequestClicked() {
        getView().showDiscountInputDialog();
    }

    public void selectPluginPaymentMethod(final PaymentMethodPlugin plugin) {
        userSelectionRepository.select(pluginRepository.getPluginAsPaymentMethod(plugin.getId(), PaymentTypes.PLUGIN));
        if (!showHook1(PaymentTypes.PLUGIN, MercadoPagoComponents.Activities.HOOK_1_PLUGIN)) {

            if (plugin.isEnabled() && plugin.shouldShowFragmentOnSelection()) {
                getView().showPaymentMethodPluginActivity();
            } else {
                onPluginAfterHookOne();
            }
        }
    }

    public void onPluginHookOneResult() {
        // we assume that the last selected payment method was this.
        final String paymentMethodId = userSelectionRepository
            .getPaymentMethod()
            .getId();

        final PaymentMethodPlugin plugin = pluginRepository
            .getPlugin(paymentMethodId);

        selectPluginPaymentMethod(plugin);
    }

    public void onPluginAfterHookOne() {
        getView().finishPaymentMethodSelection(userSelectionRepository.getPaymentMethod());
    }

    public void onPaymentMethodReturned() {
        getView().finishPaymentMethodSelection(userSelectionRepository.getPaymentMethod());
    }

    private boolean isDiscountAvailable() {
        return discountRepository.getDiscount() != null || discountRepository.hasCodeCampaign();
    }
}