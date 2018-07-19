package com.mercadopago.android.px;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mercadopago.android.px.adapters.CustomerCardItemAdapter;
import com.mercadopago.android.px.callbacks.OnSelectedCallback;
import com.mercadopago.android.px.exceptions.MercadoPagoError;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.presenters.CustomerCardsPresenter;
import com.mercadopago.android.px.providers.CustomerCardsProviderImpl;
import com.mercadopago.android.px.services.exceptions.ApiException;
import com.mercadopago.android.px.uicontrollers.GridSpacingItemDecoration;
import com.mercadopago.android.px.util.ApiUtil;
import com.mercadopago.android.px.util.ErrorUtil;
import com.mercadopago.android.px.util.JsonUtil;
import com.mercadopago.android.px.util.MercadoPagoUtil;
import com.mercadopago.android.px.util.ScaleUtil;
import com.mercadopago.android.px.util.ViewUtils;
import com.mercadopago.android.px.views.CustomerCardsView;
import java.lang.reflect.Type;
import java.util.List;

public class CustomerCardsActivity extends MercadoPagoBaseActivity implements CustomerCardsView {

    public static final int COLUMN_SPACING_DP_VALUE = 20;
    public static final int COLUMNS = 2;

    // Local vars
    protected String mMerchantBaseUrl;
    protected String mMerchantGetCustomerUri;

    protected String mMerchantAccessToken;
    protected boolean mActivityActive;
    protected ViewGroup mSavedCardsContainer;

    //Controls
    protected CustomerCardsPresenter mPresenter;
    protected RecyclerView mItemsRecyclerView;
    protected TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createPresenter();
        getActivityParameters();

        mPresenter.attachView(this);
        mPresenter.attachResourcesProvider(
            new CustomerCardsProviderImpl(this, mMerchantAccessToken, mMerchantBaseUrl, mMerchantGetCustomerUri));

        mActivityActive = true;

        setContentView();
        initializeControls();

        initialize();
    }

    protected void initialize() {
        mPresenter.initialize();
    }

    protected void createPresenter() {
        mPresenter = new CustomerCardsPresenter();
    }

    protected void getActivityParameters() {
        List<Card> cards;

        try {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Card>>() {
            }.getType();

            cards = gson.fromJson(getIntent().getStringExtra("cards"), listType);
        } catch (Exception ex) {
            cards = null;
        }

        mMerchantBaseUrl = getIntent().getStringExtra("merchantBaseUrl");
        mMerchantGetCustomerUri = getIntent().getStringExtra("merchantGetCustomerUri");
        mMerchantAccessToken = getIntent().getStringExtra("merchantAccessToken");

        mPresenter.setCustomTitle(getIntent().getStringExtra("title"));
        mPresenter.setSelectionConfirmPromptText(getIntent().getStringExtra("selectionConfirmPromptText"));
        mPresenter.setCustomActionMessage(getIntent().getStringExtra("customActionMessage"));
        mPresenter.setCards(cards);
    }

    protected void setContentView() {
        setContentView(R.layout.px_activity_customer_cards);
    }

    protected void initializeControls() {
        initializeToolbar();
        mSavedCardsContainer = findViewById(R.id.mpsdkRegularLayout);
    }

    private void initializeToolbar() {
        Toolbar toolbar = findViewById(R.id.mpsdkToolbar);
        setSupportActionBar(toolbar);

        mTitle = findViewById(R.id.mpsdkToolbarTitle);
        if (!TextUtils.isEmpty(mPresenter.getCustomTitle())) {
            mTitle.setText(mPresenter.getCustomTitle());
        }

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void showCards(List<Card> cards, String actionMessage, OnSelectedCallback<Card> onSelectedCallback) {
        initializePaymentOptionsRecyclerView(cards, actionMessage, onSelectedCallback);
    }

    protected void initializePaymentOptionsRecyclerView(List<Card> cards, String actionMessage,
        OnSelectedCallback<Card> onSelectedCallback) {
        int columns = COLUMNS;
        mItemsRecyclerView = findViewById(R.id.mpsdkCardsList);
        mItemsRecyclerView.setLayoutManager(new GridLayoutManager(this, columns));
        mItemsRecyclerView.addItemDecoration(
            new GridSpacingItemDecoration(columns, ScaleUtil.getPxFromDp(COLUMN_SPACING_DP_VALUE, this), true));
        CustomerCardItemAdapter groupsAdapter = new CustomerCardItemAdapter(cards, actionMessage, onSelectedCallback);

        populateCustomerCardList(groupsAdapter);
    }

    protected void populateCustomerCardList(CustomerCardItemAdapter groupsAdapter) {
        mItemsRecyclerView.setAdapter(groupsAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ErrorUtil.ERROR_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                mPresenter.recoverFromFailure();
            } else {
                setResult(RESULT_CANCELED, data);
                finish();
            }
        }
    }

    @Override
    public void showConfirmPrompt(final Card card) {
        String lastDigitsLabel = mPresenter.getResourcesProvider().getLastDigitsLabel();
        String dialogTitle =
            new StringBuilder().append(lastDigitsLabel).append(" ").append(card.getLastFourDigits()).toString();

        new AlertDialog.Builder(this, R.style.ThemeMercadoPagoAlertDialog)
            .setIcon(getResourceId(card))
            .setTitle(dialogTitle)
            .setMessage(mPresenter.getSelectionConfirmPromptText())
            .setPositiveButton(mPresenter.getResourcesProvider().getConfirmPromptYes(),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishWithCardResult(card);
                    }
                })
            .setNegativeButton(mPresenter.getResourcesProvider().getConfirmPromptNo(),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
            .show();
    }

    @Override
    public void hideProgress() {
        ViewUtils.showRegularLayout(this);
    }

    @Override
    public void showProgress() {
        ViewUtils.showProgressLayout(this);
    }

    @Override
    public void showError(final MercadoPagoError error, final String requestOrigin) {
        if (error.isApiException()) {
            showApiException(error.getApiException(), requestOrigin);
        } else {
            ErrorUtil.startErrorActivity(this, error);
        }
    }

    @Override
    public void finishWithCardResult(Card card) {
        final Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        returnIntent.putExtra("card", JsonUtil.getInstance().toJson(card));
        finish();
    }

    @Override
    public void onBackPressed() {
        final Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    @Override
    public void finishWithOkResult() {
        final Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    public void showApiException(ApiException apiException, String requestOrigin) {
        if (mActivityActive) {
            ApiUtil.showApiExceptionError(this, apiException, requestOrigin);
        }
    }

    public void onOtherPaymentMethodClicked(View view) {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    public int getResourceId(Card card) {
        int resourceId = MercadoPagoUtil.getPaymentMethodIcon(this, card.getPaymentMethod().getId());

        if (resourceId == 0) {
            resourceId = mPresenter.getResourcesProvider().getIconDialogAlert();
        }

        return resourceId;
    }
}
