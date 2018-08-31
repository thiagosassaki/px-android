package com.mercadopago.android.px.paymentmethods;

import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentTypes;
import com.mercadopago.android.px.internal.callbacks.TaggedCallback;
import com.mercadopago.android.px.preferences.PaymentPreference;
import com.mercadopago.android.px.internal.features.PaymentMethodsPresenter;
import com.mercadopago.android.px.internal.features.providers.PaymentMethodsProvider;
import com.mercadopago.android.px.internal.features.PaymentMethodsView;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Created by mreverter on 1/5/17.
 */

public class PaymentMethodsPresenterTest {

    @Test
    public void whenPaymentMethodsPresenterStartsShowPaymentMethods() {

        MockedView mockedView = new MockedView();
        MockedResourcesProvider resourcesProvider = new MockedResourcesProvider();

        PaymentMethodsPresenter presenter = new PaymentMethodsPresenter();

        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(resourcesProvider);

        presenter.start();

        Assert.assertTrue(mockedView.paymentMethods.size() == 2);
        Assert.assertTrue(!mockedView.progressVisible);
        Assert.assertTrue(!mockedView.bankDealsShown);
        Assert.assertTrue(mockedView.error == null);
    }

    @Test
    public void whenPaymentTypeExcludedDoNotShowIt() {

        MockedView mockedView = new MockedView();
        MockedResourcesProvider resourcesProvider = new MockedResourcesProvider();

        PaymentMethodsPresenter presenter = new PaymentMethodsPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(resourcesProvider);

        PaymentPreference paymentPreference = new PaymentPreference();

        List<String> paymentTypes = new ArrayList<>();
        paymentTypes.add(PaymentTypes.CREDIT_CARD);

        paymentPreference.setExcludedPaymentTypeIds(paymentTypes);

        presenter.setPaymentPreference(paymentPreference);
        presenter.start();

        Assert.assertTrue(mockedView.paymentMethods.size() == 1);
        Assert.assertTrue(!mockedView.progressVisible);
        Assert.assertTrue(!mockedView.bankDealsShown);
        Assert.assertTrue(mockedView.error == null);
    }

    private class MockedView implements PaymentMethodsView {

        public List<PaymentMethod> paymentMethods;
        private boolean progressVisible;
        private MercadoPagoError error;
        private boolean bankDealsShown;

        @Override
        public void showPaymentMethods(List<PaymentMethod> paymentMethods) {
            this.paymentMethods = paymentMethods;
        }

        @Override
        public void showProgress() {
            this.progressVisible = true;
        }

        @Override
        public void hideProgress() {
            this.progressVisible = false;
        }

        @Override
        public void showError(MercadoPagoError exception) {
            this.error = exception;
        }

        @Override
        public void showBankDeals() {
            this.bankDealsShown = true;
        }
    }

    private class MockedResourcesProvider implements PaymentMethodsProvider {

        @Override
        public void getPaymentMethods(TaggedCallback<List<PaymentMethod>> resourcesRetrievedCallback) {
            List<PaymentMethod> paymentMethods = new ArrayList<>();

            PaymentMethod paymentMethod1 = new PaymentMethod();
            paymentMethod1.setPaymentTypeId(PaymentTypes.CREDIT_CARD);

            PaymentMethod paymentMethod2 = new PaymentMethod();
            paymentMethod2.setPaymentTypeId(PaymentTypes.DEBIT_CARD);

            paymentMethods.add(paymentMethod1);
            paymentMethods.add(paymentMethod2);
            resourcesRetrievedCallback.onSuccess(paymentMethods);
        }
    }
}
