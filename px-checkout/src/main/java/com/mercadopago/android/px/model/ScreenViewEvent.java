package com.mercadopago.android.px.model;

import java.util.HashMap;
import java.util.Map;


public class ScreenViewEvent extends Event {

    private String screenId;
    private String screenName;

    protected ScreenViewEvent() {

    }

    private ScreenViewEvent(Builder builder) {
        super();
        setType(TYPE_SCREEN_VIEW);
        setTimestamp(System.currentTimeMillis());
        setProperties(builder.properties);
        setFlowId(builder.flowId);
        this.screenId = builder.screenId;
        this.screenName = builder.screenName;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getScreenId() {
        return screenId;
    }

    public static class Builder {

        private String flowId;
        private String screenId;
        private String screenName;
        private Map<String, String> properties = new HashMap<>();

        public Builder setFlowId(final String flowId) {
            this.flowId = flowId;
            return this;
        }

        public Builder setScreenId(final String screenId) {
            this.screenId = screenId;
            return this;
        }

        public Builder setScreenName(final String screenName) {
            this.screenName = screenName;
            return this;
        }

        public Builder addProperty(final String key, final String value) {
            properties.put(key, value);
            return this;
        }

        public ScreenViewEvent build() {
            return new ScreenViewEvent(this);
        }
    }
}
