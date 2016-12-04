package com.iGap.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.iGap.R;
import com.iGap.libs.rippleeffect.RippleView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android3 on 12/1/2016.
 */

public class Activity_charge_balance extends ActivityEnhanced {


    EditText edtPrice;
    EditText edtEmail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_charge_balance);


        initComponent();

    }

    private void initComponent() {


        RippleView rippleBackButton = (RippleView) findViewById(R.id.acb_ripple_back_Button);
        rippleBackButton.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                finish();
            }
        });


        TextView txtCancel = (TextView) findViewById(R.id.acb_txt_cancel);
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Log.e("dddd", "cancel clicked");
            }
        });


        TextView txtCharge = (TextView) findViewById(R.id.acb_txt_charge);
        txtCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Log.e("dddd", "txtCharge clicked");
            }
        });


        edtPrice = (EditText) findViewById(R.id.acb_edt_price);
        final View edtBottomLinePrice = findViewById(R.id.acb_view_bottom_line_price);
        edtPrice.setPadding(0, 8, 0, 8);
        edtPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (b) {
                    edtBottomLinePrice.setBackgroundColor(getResources().getColor(R.color.toolbar_background));
                } else {
                    edtBottomLinePrice.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
                }
            }
        });


        edtEmail = (EditText) findViewById(R.id.acb_edt_email);
        final View edtBottomLineEmail = findViewById(R.id.acb_view_bottom_line_email);
        edtEmail.setPadding(0, 8, 0, 8);
        edtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (b) {
                    edtBottomLineEmail.setBackgroundColor(getResources().getColor(R.color.toolbar_background));
                } else {
                    edtBottomLineEmail.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
                }
            }
        });


        List<String> spinnerArray = new ArrayList<String>();
        spinnerArray.add("Iranian Rials(IRR)");


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Activity_charge_balance.this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner) findViewById(R.id.acb_spinner_price_type);
        spinner.setAdapter(adapter);


    }
}
