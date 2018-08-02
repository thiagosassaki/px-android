package com.mercadopago.android.px.model;

import java.io.Serializable;

/**
 * Created by mromar on 10/20/17.
 */

public class Cause implements Serializable {

    private String code;
    private String description;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Cause{" +
            "code='" + code + '\'' +
            ", description='" + description + '\'' +
            '}';
    }
}
