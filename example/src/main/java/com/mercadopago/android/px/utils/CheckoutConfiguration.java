package com.mercadopago.android.px.utils;

import com.google.gson.annotations.SerializedName;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.model.Item;
import com.mercadopago.android.px.model.Site;
import com.mercadopago.android.px.model.Sites;
import com.mercadopago.android.px.configuration.AdvancedConfiguration;
import java.util.List;

public class CheckoutConfiguration {
    private String startFor;
    private String prefId;
    private String publicKey;
    private List<Item> items;
    private String payerEmail;
    private String siteId;
    private AdvancedConfiguration advancedConfiguration;

    @SerializedName(value = "timer")
    private Integer time;

    public String getStartFor() {
        return startFor;
    }

    public String getPrefId() {
        return prefId;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public boolean paymentDataRequired() {
        return "payment_data".equals(startFor);
    }

    public List<Item> getItems() {
        return items;
    }

    public String getPayerEmail() {
        return payerEmail;
    }

    public Site getSite() {
        return TextUtil.isEmpty(siteId) ? null : Sites.getById(siteId);
    }

    public Integer getTime() {
        return time;
    }

    public AdvancedConfiguration getAdvancedConfiguration() {
        return advancedConfiguration;
    }
}
