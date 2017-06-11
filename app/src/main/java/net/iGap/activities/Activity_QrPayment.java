/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;
import java.io.IOException;
import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperPermision;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.libs.rippleeffect.RippleView;

public class Activity_QrPayment extends ActivityEnhanced {

    private enum Action {
        chargeBalance, transactionHistory, transferMony, crateQRCode, payViaQRCode, chargeOut;
    }

    Action action = Activity_QrPayment.Action.chargeBalance;

    TextView txtChargeBalance;
    TextView txtChargeBalanceIcon;
    CardView cardViewChargeBAlance;

    //*********************************************************

    TextView txtTransactionHistory;
    TextView txtTransactionHistoryIcon;
    CardView cardViewTransactionHistory;

    //*********************************************************

    TextView txtTransferMony;
    TextView txtTransferMonyIcon;
    CardView cardViewTransferMony;

    //*********************************************************

    TextView txtCreateQRCode;
    TextView txtCreateQRCodeIcon;
    CardView cardViewCreateQRCode;

    //*********************************************************

    TextView txtPayViaQRCode;
    TextView txtPayViaQRCodeIcon;
    CardView cardViewPayViaQRCode;

    //*********************************************************

    TextView txtChargeOut;
    TextView txtChargeOutIcon;
    CardView cardViewChargeOut;

    initComponent initComponent;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_qr_payment);

        initComponent = new initComponent();

        initComponent.init();
    }

    @Override protected void onResume() {
        super.onResume();

    }

    private class initComponent {

        private void init() {

            RippleView rippleBackButton = (RippleView) findViewById(R.id.aqp_ripple_back_Button);
            rippleBackButton.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override public void onComplete(RippleView rippleView) {
                    finish();
                }
            });

            findViewById(R.id.apqp_ll_toolbar).setBackgroundColor(Color.parseColor(G.appBarColor));

            CardView cardViewShowTotal = (CardView) findViewById(R.id.apqp_card_view_show_total);
            cardViewShowTotal.setCardBackgroundColor(Color.parseColor(G.appBarColor));



            initChargeBalance();
            initTransactiomHistory();
            initTransferMony();
            initCreateQRCode();
            initPayViaQRCode();
            initChargeOut();
        }

        private void initChargeBalance() {

            txtChargeBalance = (TextView) findViewById(R.id.aqp_txt_charge_balance);
            txtChargeBalanceIcon = (TextView) findViewById(R.id.aqp_txt_icon_charge_balance);
            setBackGround(txtChargeBalanceIcon, R.drawable.grey_charge_balance);
            cardViewChargeBAlance = (CardView) findViewById(R.id.aqp_card_view_charge_balance);

            cardViewChargeBAlance.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {

                    startActivity(new Intent(Activity_QrPayment.this, Activity_charge_balance.class));
                }
            });
        }

        private void initTransactiomHistory() {

            txtTransactionHistory = (TextView) findViewById(R.id.aqp_txt_transaction_history);
            txtTransactionHistoryIcon = (TextView) findViewById(R.id.aqp_txt_icon_transaction_history);
            setBackGround(txtTransactionHistoryIcon, R.drawable.grey_transaction_history);
            cardViewTransactionHistory = (CardView) findViewById(R.id.aqp_card_view_transaction_history);

            cardViewTransactionHistory.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {

                    startActivity(new Intent(Activity_QrPayment.this, Activity_transactionHistory.class));
                }
            });
        }

        private void initTransferMony() {

            txtTransferMony = (TextView) findViewById(R.id.aqp_txt_transfer_mony);
            txtTransferMonyIcon = (TextView) findViewById(R.id.aqp_txt_icon_transfer_mony);
            setBackGround(txtTransferMonyIcon, R.drawable.grey_transfer);
            cardViewTransferMony = (CardView) findViewById(R.id.aqp_card_view_transfer_mony);

            cardViewTransferMony.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {


                    startActivity(new Intent(Activity_QrPayment.this, Activity_Transfer_mony.class));
                }
            });
        }

        private void initCreateQRCode() {

            txtCreateQRCode = (TextView) findViewById(R.id.aqp_txt_create_qr_code);
            txtCreateQRCodeIcon = (TextView) findViewById(R.id.aqp_txt_icon_create_qr_code);
            setBackGround(txtCreateQRCodeIcon, R.drawable.qr_code);
            cardViewCreateQRCode = (CardView) findViewById(R.id.aqp_card_view_crate_qr_code);

            cardViewCreateQRCode.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {


                    startActivity(new Intent(Activity_QrPayment.this, Activity_CreateQRCode.class));
                }
            });
        }

        private void initPayViaQRCode() {

            txtPayViaQRCode = (TextView) findViewById(R.id.aqp_txt_pay_via_qr_code);
            txtPayViaQRCodeIcon = (TextView) findViewById(R.id.aqp_txt_icon_pay_via_qr_code);
            setBackGround(txtPayViaQRCodeIcon, R.drawable.grey_pay_qr_code);
            cardViewPayViaQRCode = (CardView) findViewById(R.id.aqp_card_view_pay_via_qr_code);

            cardViewPayViaQRCode.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {

                    try {
                        HelperPermision.getCameraPermission(Activity_QrPayment.this, new OnGetPermission() {
                            @Override public void Allow() {

                                startActivity(new Intent(Activity_QrPayment.this, Activity_payViaQRCode.class));
                            }

                            @Override public void deny() {

                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        private void initChargeOut() {

            txtChargeOut = (TextView) findViewById(R.id.aqp_txt_charge_out);
            txtChargeOutIcon = (TextView) findViewById(R.id.aqp_txt_icon_charge_out);
            setBackGround(txtChargeOutIcon, R.drawable.grey_charge_out);
            cardViewChargeOut = (CardView) findViewById(R.id.aqp_card_view_charge_out);

            cardViewChargeOut.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {


                    startActivity(new Intent(Activity_QrPayment.this, Activity_ChargeOut.class));
                }
            });
        }

        //*********************************************************   deSelect view       ***********




    }

    private void setBackGround(View view, int redDrawable) {

        Drawable drawable = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable = Activity_QrPayment.this.getResources().getDrawable(redDrawable, Activity_QrPayment.this.getTheme());
        } else {
            drawable = Activity_QrPayment.this.getResources().getDrawable(redDrawable);
        }

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(drawable);
        } else {
            view.setBackground(drawable);
        }
    }

    public static final int getColor(Context context, int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            return ContextCompat.getColor(context, id);
        } else {
            return context.getResources().getColor(id);
        }
    }
}
