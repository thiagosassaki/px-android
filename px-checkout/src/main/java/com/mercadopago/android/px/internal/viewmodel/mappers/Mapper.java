package com.mercadopago.android.px.internal.viewmodel.mappers;

import android.support.annotation.NonNull;

public abstract class Mapper<T, V> {

    public abstract V map(@NonNull T val);
}
