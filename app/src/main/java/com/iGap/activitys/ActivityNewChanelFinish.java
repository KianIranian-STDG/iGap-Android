package com.iGap.activitys;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.iGap.R;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.MaterialDesignTextView;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ActivityNewChanelFinish extends ActivityEnhanced {

    private MaterialDesignTextView txtBack;
    private RadioButton radioButtonPublic, radioButtonPrivate;
    private EditText edtLink;
    private TextView txtFinish, txtCancel;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_chanel_finish);

        txtBack = (MaterialDesignTextView) findViewById(R.id.nclf_txt_back);
        RippleView rippleBack = (RippleView) findViewById(R.id.nclf_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                finish();
            }
        });

        radioButtonPublic = (RadioButton) findViewById(R.id.nclf_radioButton_Public);
        radioButtonPublic.setChecked(true);
        radioButtonPrivate = (RadioButton) findViewById(R.id.nclf_radioButton_private);

        edtLink = (EditText) findViewById(R.id.nclf_edt_link);

        txtFinish = (TextView) findViewById(R.id.nclf_txt_nextStep);
        txtCancel = (TextView) findViewById(R.id.nclf_txt_cancel);

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        txtFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
