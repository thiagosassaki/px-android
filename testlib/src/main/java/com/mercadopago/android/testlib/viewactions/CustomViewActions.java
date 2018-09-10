package com.mercadopago.android.testlib.viewactions;

import android.support.test.espresso.ViewAction;
import javax.annotation.Nonnull;

import static android.support.test.espresso.action.ViewActions.actionWithAssertions;

public final class CustomViewActions {

    private CustomViewActions() {
    }

    /**
     * Custom ViewAction to type text into MeliUI's TextField.
     *
     * @param text to be typed in.
     * @return Custom ViewAction to type text into TextField MeliUi Component.
     */
    public static ViewAction typeTextIntoTextField(@Nonnull String text) {
        return actionWithAssertions(new TextFieldViewAction(text));
    }
}

