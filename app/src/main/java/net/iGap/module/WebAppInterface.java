package net.iGap.module;

import androidx.fragment.app.FragmentActivity;
import android.webkit.JavascriptInterface;

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
        new HelperFragment(activity.getSupportFragmentManager()).loadPayment(title, token, result -> {

        });
    }
}