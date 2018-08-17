package com.mercadopago.android.px.testcheckout.pages;

import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.view.View;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.testcheckout.assertions.CheckoutValidator;
import com.mercadopago.android.testlib.pages.PageObject;
import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class InstallmentsPage extends PageObject<CheckoutValidator> {

    public InstallmentsPage() {
        // This constructor is intentionally empty. Nothing special is needed here.
    }

    protected InstallmentsPage(CheckoutValidator validator) {
        super(validator);
    }

    @Override
    public InstallmentsPage validate(CheckoutValidator validator) {
        validator.validate(this);
        return this;
    }

    public PaymentMethodPage pressBack() {
        onView(isRoot()).perform(ViewActions.pressBack());
        return new PaymentMethodPage(validator);
    }

    public ReviewAndConfirmPage selectInstallments(int installmentsOption) {

        Matcher<View> InstallmentsRecyclerViewMatcher = withId(R.id.mpsdkActivityInstallmentsView);

        onView(InstallmentsRecyclerViewMatcher)
            .perform(RecyclerViewActions.actionOnItemAtPosition(installmentsOption, click()));

        return new ReviewAndConfirmPage(validator);
    }

    public DiscountDetailPage pressOnDiscountDetail() {
        onView(withId(R.id.amount_view)).perform(click());
        return new DiscountDetailPage(validator);
    }
}
