package com.mercadopago.android.px.model.requests;

import com.google.gson.annotations.SerializedName;

public class GroupsIntent {

    @SerializedName("access_token")
    private String privateKey;

    public GroupsIntent(final String privateKey) {
        this.privateKey = privateKey;
    }
}
