package net.iGap.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;


import net.iGap.G;
import net.iGap.R;
import net.iGap.messenger.theme.Theme;
import net.iGap.request.RequestSignalingRate;

public class ActivityRatingBar extends ActivityEnhanced {

    public static final String ID_EXTRA = "ID_EXTRA";

    RatingBar ratingBar;
    EditText edtResone;
    long id = -1;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        G.isShowRatingDialog = false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        G.isShowRatingDialog = true;

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
        openDialogForRating();

        LinearLayout layotRoot = (LinearLayout) findViewById(R.id.arb_layout_root);
        layotRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void openDialogForRating() {

        MaterialDialog dialog = new MaterialDialog.Builder(ActivityRatingBar.this)
                .backgroundColor(net.iGap.messenger.theme.Theme.getColor(net.iGap.messenger.theme.Theme.key_popup_background))
                .title(R.string.Call_Quality).customView(R.layout.dialog_rating_call, true)
                .positiveText(R.string.ok)
                .negativeColor(Theme.getColor(Theme.key_button_background))
                .positiveColor(Theme.getColor(Theme.key_button_background))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                sendRateToServer();
                closeDialog();

            }
        }).negativeText(R.string.cancel).onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                closeDialog();
            }
        }).build();
        View view = dialog.getView();

        dialog.getTitleView().setTypeface(ResourcesCompat.getFont(dialog.getContext() , R.font.main_font));
        dialog.getActionButton(DialogAction.NEGATIVE).setTypeface(ResourcesCompat.getFont(dialog.getContext() , R.font.main_font));
        dialog.getActionButton(DialogAction.POSITIVE).setTypeface(ResourcesCompat.getFont(dialog.getContext() , R.font.main_font));


        final View positive = dialog.getActionButton(DialogAction.POSITIVE);
        positive.setEnabled(false);

        ratingBar = view.findViewById(R.id.arb_ratingBar_call);
        edtResone = view.findViewById(R.id.arb_edt_resone);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                if (rating < 3) {

                    edtResone.setVisibility(View.VISIBLE);

                    if (edtResone.getText().length() > 0) {
                        positive.setEnabled(true);
                    } else {
                        positive.setEnabled(false);
                    }
                } else {

                    edtResone.setVisibility(View.GONE);
                    positive.setEnabled(true);
                }
            }
        });

        edtResone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (edtResone.getText().length() > 0) {
                    positive.setEnabled(true);
                } else {
                    positive.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        view.findViewById(R.id.arb_layout_rate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // no action
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                closeDialog();
            }
        });

        dialog.show();


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
