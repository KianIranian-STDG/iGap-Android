package net.iGap.module;

import android.webkit.JavascriptInterface;

import androidx.fragment.app.FragmentActivity;

import net.iGap.helper.HelperFragment;

public class WebAppInterface {

    private FragmentActivity activity;

    /**
     * Instantiate the interface and set the activity
     */
    public WebAppInterface(FragmentActivity activity) {
        this.activity = activity;
    }

    /**
     * Show a payment from the web page
     */
    @JavascriptInterface
    public void IGapPayment(String token, String title) {
        new HelperFragment(activity.getSupportFragmentManager()).loadPayment(title, token, null);
    }
}