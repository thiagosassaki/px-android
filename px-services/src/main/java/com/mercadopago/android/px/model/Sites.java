package com.mercadopago.android.px.model;

import android.support.annotation.NonNull;
import java.util.Arrays;

/**
 * Utility class for sites.
 */
public final class Sites {

    public static final Site ARGENTINA =
        new Site("MLA", "ARS", "https://www.mercadopago.com.ar/ayuda/terminos-y-condiciones_299");
    public static final Site BRASIL =
        new Site("MLB", "BRL", "https://www.mercadopago.com.br/ajuda/termos-e-condicoes_300");
    public static final Site CHILE =
        new Site("MLC", "CLP", "https://www.mercadopago.cl/ayuda/terminos-y-condiciones_299");
    public static final Site MEXICO =
        new Site("MLM", "MXN", "https://www.mercadopago.com.mx/ayuda/terminos-y-condiciones_715");
    public static final Site COLOMBIA =
        new Site("MCO", "COP", "https://www.mercadopago.com.co/ayuda/terminos-y-condiciones_299");
    public static final Site VENEZUELA =
        new Site("MLV", "VES", "https://www.mercadopago.com.ve/ayuda/terminos-y-condiciones_299");
    public static final Site USA = new Site("USA", "USD", "");
    public static final Site PERU =
        new Site("MPE", "PEN", "https://www.mercadopago.com.pe/ayuda/terminos-condiciones-uso_2483");

    private static final Iterable<Site> SITES = Arrays.asList(
        ARGENTINA,
        BRASIL,
        CHILE,
        MEXICO,
        COLOMBIA,
        VENEZUELA,
        USA,
        PERU
    );

    private Sites() {
    }

    /**
     * Given a site id returns a {@link Site} or throws.
     *
     * @param siteId searched site id.
     * @return site
     * @throws IllegalArgumentException if it does not exists.
     */
    @NonNull
    public static Site getById(@NonNull final String siteId) throws IllegalArgumentException {
        for (final Site site : SITES) {
            if (site.getId().equals(siteId)) {
                return site;
            }
        }
        throw new IllegalArgumentException("There is no site for that id");
    }
}
