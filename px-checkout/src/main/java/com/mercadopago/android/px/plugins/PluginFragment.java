package com.mercadopago.android.px.plugins;

import android.content.Context;
import android.support.v4.app.Fragment;

public abstract class PluginFragment extends Fragment {

    public interface Actions {
        void next();

        void back();
    }

    private Actions actions;

    protected Actions getActions() {
        return actions;
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        if (context instanceof Actions) {
            actions = (Actions) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        actions = null;
    }
}
