package com.mercadopago.android.px.internal.datasource;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.reflect.TypeToken;
import com.mercadopago.android.px.model.Campaign;
import com.mercadopago.android.px.model.Discount;
import com.mercadopago.android.px.util.JsonUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.mercadopago.android.px.util.TextUtils.isEmpty;

public class DiscountStorageService {

    private static final String PREF_CAMPAIGN = "pref_campaign";
    private static final String PREF_DISCOUNT = "pref_discount";
    private static final String PREF_DISCOUNT_CODE = "pref_discount_code";
    private static final String PREF_CAMPAIGNS = "pref_campaigns";

    @NonNull
    private final SharedPreferences sharedPreferences;
    @NonNull
    private final JsonUtil jsonUtil;

    public DiscountStorageService(@NonNull final SharedPreferences sharedPreferences,
                                  @NonNull final JsonUtil jsonUtil) {
        this.sharedPreferences = sharedPreferences;
        this.jsonUtil = jsonUtil;
    }

    public void configureDiscountManually(@Nullable final Discount discount, @Nullable final Campaign campaign) {
        configure(campaign);
        configure(discount);
    }

    public void reset() {
        sharedPreferences.edit().remove(PREF_CAMPAIGNS).apply();
        sharedPreferences.edit().remove(PREF_CAMPAIGN).apply();
        sharedPreferences.edit().remove(PREF_DISCOUNT).apply();
        sharedPreferences.edit().remove(PREF_DISCOUNT_CODE).apply();
    }

    @Nullable
    public Discount getDiscount() {
        return jsonUtil.fromJson(sharedPreferences.getString(PREF_DISCOUNT, ""), Discount.class);
    }

    @Nullable
    public String getDiscountCode() {
        return sharedPreferences.getString(PREF_DISCOUNT_CODE, "");
    }

    @Nullable
    public Campaign getCampaign() {
        return jsonUtil.fromJson(sharedPreferences.getString(PREF_CAMPAIGN, ""), Campaign.class);
    }

    private void configure(@Nullable final Discount discount) {
        if (discount == null) {
            sharedPreferences.edit().remove(PREF_DISCOUNT).apply();
        } else {
            final SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putString(PREF_DISCOUNT, jsonUtil.toJson(discount));
            edit.apply();
        }
    }

    private void configure(@Nullable final Campaign campaign) {
        if (campaign == null) {
            sharedPreferences.edit().remove(PREF_CAMPAIGN).apply();
        } else {
            final SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putString(PREF_CAMPAIGN, jsonUtil.toJson(campaign));
            edit.apply();
        }
    }

    public void saveDiscountCode(@Nullable final String code) {
        sharedPreferences.edit().putString(PREF_DISCOUNT_CODE, code).apply();
    }

    public boolean hasCodeCampaign() {
        for (final Campaign campaign : getCampaigns()) {
            if (campaign.isMultipleCodeDiscountCampaign() || campaign.isSingleCodeDiscountCampaign()) {
                return true;
            }
        }
        return false;
    }

    @NonNull
    public List<Campaign> getCampaigns() {
        final String stringCampaigns = sharedPreferences.getString(PREF_CAMPAIGNS, "");
        final Type listType = new TypeToken<List<Campaign>>() {
        }.getType();
        return isEmpty(stringCampaigns) ? new ArrayList<Campaign>()
                : (List<Campaign>) jsonUtil.fromJson(stringCampaigns, listType);
    }

    public void saveCampaigns(@NonNull final List<Campaign> campaigns) {
        sharedPreferences.edit().putString(PREF_CAMPAIGNS, jsonUtil.toJson(campaigns)).apply();
    }
}
