package com.mercadopago.android.px.mocks;

import com.google.gson.reflect.TypeToken;
import com.mercadopago.android.px.internal.util.JsonUtil;
import com.mercadopago.android.px.model.Campaign;
import com.mercadopago.android.px.utils.ResourcesUtil;
import java.lang.reflect.Type;
import java.util.List;

public class Discounts {
    private Discounts() {
    }

    public static List<Campaign> getCampaigns() {
        String json = ResourcesUtil.getStringResource("complete_campaigns.json");
        Type listType = new TypeToken<List<Campaign>>() {
        }.getType();
        return JsonUtil.getInstance().getGson().fromJson(json, listType);
    }
}
