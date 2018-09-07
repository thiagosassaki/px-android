package com.mercadopago.android.testlib.viewactions;

import android.support.annotation.NonNull;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.view.View;
import com.mercadolibre.android.ui.widgets.TextField;
import org.hamcrest.Matcher;

import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.hamcrest.Matchers.allOf;

public class TextFieldViewAction implements ViewAction {

    @NonNull private final String text;

    public TextFieldViewAction(@NonNull final String text) {
        this.text = text;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Matcher getConstraints() {
        return allOf(isDisplayed(), isAssignableFrom(TextField.class));
    }

    @Override
    public void perform(UiController uiController, View view) {
        ((TextField) view).setText(text);
    }

    @Override
    public String getDescription() {
        return "typeText into TextField";
    }
}
