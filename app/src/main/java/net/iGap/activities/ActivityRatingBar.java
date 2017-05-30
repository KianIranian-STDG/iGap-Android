package net.iGap.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import net.iGap.R;
import net.iGap.request.RequestSignalingRate;

/**
 * Created by android3 on 5/30/2017.
 */

public class ActivityRatingBar extends ActivityEnhanced {

    public static final String ID_EXTRA = "ID_EXTRA";

    RatingBar ratingBar;
    EditText edtResone;
    long id = -1;

    @Override public void onCreate(Bundle savedInstanceState) {

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
            | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
            | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_bar);

        id = getIntent().getExtras().getLong(ID_EXTRA);

        initComponent();
    }

    private void initComponent() {

        ratingBar = (RatingBar) findViewById(R.id.arb_ratingBar_call);
        edtResone = (EditText) findViewById(R.id.arb_edt_resone);
        LinearLayout layotRoot = (LinearLayout) findViewById(R.id.arb_layout_root);
        Button btnCancel = (Button) findViewById(R.id.arb_btn_cancel);
        final Button btnOk = (Button) findViewById(R.id.arb_btn_ok);

        layotRoot.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                closeDialog();
            }
        });

        findViewById(R.id.arb_layout_rate).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                // no action
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                closeDialog();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

                sendRateToServer();
                closeDialog();
            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                Log.e("dddddd", ratingBar.getRating() + "");

                if (rating < 3) {

                    edtResone.setVisibility(View.VISIBLE);

                    if (edtResone.getText().length() > 0) {
                        btnOk.setEnabled(true);
                    } else {
                        btnOk.setEnabled(false);
                    }
                } else {

                    edtResone.setVisibility(View.INVISIBLE);
                    btnOk.setEnabled(true);
                }
            }
        });

        edtResone.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (edtResone.getText().length() > 0) {
                    btnOk.setEnabled(true);
                } else {
                    btnOk.setEnabled(false);
                }
            }

            @Override public void afterTextChanged(Editable s) {

            }
        });
    }

    private void sendRateToServer() {

        String resone = edtResone.getText().toString();
        int rate = (int) ratingBar.getRating();

        new RequestSignalingRate().signalingRate(id, rate, resone);
    }

    private void closeDialog() {

        ratingBar.setIsIndicator(true);
        finish();
    }
}
