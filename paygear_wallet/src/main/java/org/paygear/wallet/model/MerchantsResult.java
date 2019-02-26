package org.paygear.wallet.model;

import java.util.ArrayList;

public class MerchantsResult {

    /**
     * merchants : [{"_id":"5b5043fd7c2f88000bf2fdf4","account_type":0,"is_vip":false,"location":[35.740286064023756,51.50158345699311],"name":"amin","sub_title":"a","username":""}]
     * next_page : false
     */

    private boolean next_page;
    private ArrayList<SearchedAccount> merchants;

    public boolean isNext_page() {
        return next_page;
    }

    public void setNext_page(boolean next_page) {
        this.next_page = next_page;
    }

    public ArrayList<SearchedAccount> getMerchants() {
        return merchants;
    }

    public void setMerchants(ArrayList<SearchedAccount> merchants) {
        this.merchants = merchants;
    }

}
