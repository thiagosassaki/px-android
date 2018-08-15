package com.mercadopago.android.px.internal.base;

import com.mercadopago.android.px.internal.callbacks.TaggedCallback;

/**
 * All <code>MvpPresenter</code>'s implementations MUST contain a ResourcesProvider.
 * <p>
 * <code>ResourcesProvider</code> implementations SHOULD have resources getters
 * which receive as param an <code>TaggedCallback</code>.
 * <p>
 * <code>ResourcesProvider</code> implementations SHOULD contain an Android <code>Context</code>.
 * <p>
 * See also {@link TaggedCallback}
 * See also {@link MvpPresenter}
 */

public interface ResourcesProvider {
}






