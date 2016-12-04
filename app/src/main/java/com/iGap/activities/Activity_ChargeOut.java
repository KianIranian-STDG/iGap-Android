package com.iGap.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.iGap.R;
import com.iGap.libs.rippleeffect.RippleView;


public class Activity_ChargeOut extends ActivityEnhanced {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_charge_out);


        initComponent();

    }

    private void initComponent() {


        RippleView rippleBackButton = (RippleView) findViewById(R.id.aco_ripple_back_Button);
        rippleBackButton.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                finish();
            }
        });


        TextView txtCancel = (TextView) findViewById(R.id.aco_txt_cancel);
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Log.e("dddd", "cancel clicked");
            }
        });


        TextView txtCharge = (TextView) findViewById(R.id.aco_txt_charge);
        txtCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Log.e("dddd", "txtCharge clicked");
            }
        });


    }
}
