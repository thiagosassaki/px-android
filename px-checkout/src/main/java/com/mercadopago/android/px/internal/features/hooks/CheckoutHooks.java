package com.mercadopago.android.px.internal.features.hooks;

import android.support.annotation.NonNull;

/**
 * @deprecated this functionality doesn't accomplish any real use case.
 * For that reason we've decided to deprecate this mechanism to the public.
 */
@Deprecated
public interface CheckoutHooks {

    Hook beforePaymentMethodConfig(@NonNull final HookComponent.Props props);

    Hook afterPaymentMethodConfig(@NonNull final HookComponent.Props props);

    Hook beforePayment(@NonNull final HookComponent.Props props);
}