package com.iGap.activities;

import android.graphics.Color;
import android.os.Bundle;
import com.iGap.G;
import com.iGap.R;
import com.iGap.libs.rippleeffect.RippleView;

/**
 * Created by android3 on 12/4/2016.
 */

public class Activity_transactionHistory extends ActivityEnhanced {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_transaction_history);

        initComponent();
    }

    private void initComponent() {

        findViewById(R.id.apth_ll_toolbar).setBackgroundColor(Color.parseColor(G.appBarColor));

        RippleView rippleBackButton = (RippleView) findViewById(R.id.ath_ripple_back_Button);
        rippleBackButton.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                finish();
            }
        });

    }
}
