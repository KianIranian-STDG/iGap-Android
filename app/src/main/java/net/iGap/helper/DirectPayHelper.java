package net.iGap.helper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.module.DialogAnimation;
import net.iGap.request.RequestMplGetSalesToken;
import net.iGap.request.RequestMplSetSalesResult;

import org.json.JSONException;
import org.json.JSONObject;

import ir.pec.mpl.pecpayment.view.PaymentInitiator;

import static net.iGap.G.context;

public class DirectPayHelper {


    public static final int requestCodeDirectPay = 4258;

    public static void directPayBot(JSONObject jsonObject, long botId) throws JSONException {
        boolean inquiry = jsonObject.getBoolean("inquiry");
        long invoiceNumber = jsonObject.getLong("invoiceNumber");
        long price = jsonObject.getLong("price");
        String description = jsonObject.getString("description");
        String title = jsonObject.getString("title");
        long toId;
        if (botId == -1L) {
            toId = jsonObject.getLong("toId");
        } else {
            toId = botId;
        }

        String pp = String.valueOf(price);
        int k = 0;
        StringBuilder newPP = new StringBuilder();
        for (int i = pp.length() - 1; i > -1; i--) {
            k ++;
            newPP.insert(0, pp.charAt(i));
            if (k % 3 == 0) {
                newPP.insert(0, ",");
            }
        }

        if (HelperCalander.isPersianUnicode) {
            newPP = new StringBuilder(HelperCalander.convertToUnicodeFarsiNumber(newPP.toString()));
        }

        String content = newPP +  " " + G.currentActivity.getString(R.string.rial);


        final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).customView(R.layout.pay_direct_dialog, true).build();

        View v = dialog.getCustomView();
        if (v == null) {
            return;
        }
        DialogAnimation.animationDown(dialog);
        dialog.show();
        TextView titleTxt = v.findViewById(R.id.title);
        TextView descriptionTxt = v.findViewById(R.id.description);
        TextView priceTxt = v.findViewById(R.id.price);
        TextView priceTitle = v.findViewById(R.id.priceTitle);
        priceTxt.setTypeface(G.typeface_IRANSansMobile_Bold);
        priceTitle.setText(G.currentActivity.getString(R.string.price) + ":");
        titleTxt.setText(title);
        descriptionTxt.setText(description);
        priceTxt.setText(content);
        if (G.isDarkTheme) {
            priceTxt.setTextColor(G.currentActivity.getResources().getColor(R.color.white));
        } else {
            priceTxt.setTextColor(Color.parseColor(G.appBarColor));
        }
        Button pay = v.findViewById(R.id.pay);
        pay.getBackground().setColorFilter(new PorterDuffColorFilter(Color.parseColor(G.appBarColor), PorterDuff.Mode.SRC_IN));

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                directPay(G.currentActivity, price, description, inquiry, toId, invoiceNumber);
            }
        });

    }

    public static void directPay(JSONObject jsonObject) throws JSONException {
        directPayBot(jsonObject, -1L);
    }

    private static void directPay(Activity activity, long amount, String description,
                                  boolean inquiry, long toUserId,
                                  long invoiceNumber) {
        if (G.currentActivity == null || G.currentActivity.isFinishing()) {
            return;
        }

        final ProgressDialog dialog = ProgressDialog.show(G.currentActivity, "",
                G.context.getString(R.string.please_wait), true);

        boolean isSend = new RequestMplGetSalesToken().mplGetSalesToken(amount, description, inquiry, toUserId, invoiceNumber, new RequestMplGetSalesToken.GetSalesToken() {
            @Override
            public void onSalesToken(String token) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        if (activity != null && !activity.isFinishing()) {
                            Intent intent = new Intent(activity, PaymentInitiator.class);
                            intent.putExtra("Type" , "1");
                            intent.putExtra("Token" , token);
//                        intent.putExtra("OrderID" , int_something);
                            activity.startActivityForResult(intent, requestCodeDirectPay);
                        }
                    }
                });
            }

            @Override
            public void onError(int major, int minor) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        if (major == 5) {
                            HelperError.showSnackMessage(G.context.getString(R.string.wallet_error_server), false);
                        } else {
                            HelperError.showSnackMessage(G.context.getString(R.string.server_error), false);
                        }
                    }
                });
            }
        });

        if (!isSend) {
            dialog.dismiss();
            HelperError.showSnackMessage(G.context.getString(R.string.wallet_error_server), false);
        }
    }

    public static void setResultOfDirectPay(String data, int retryCount, ProgressDialog dialogg, String messageToShow) {

        Log.d("bagi", "setResultOfDirectPay" + retryCount);
        if (dialogg == null) {
            dialogg = ProgressDialog.show(G.currentActivity, "",
                    G.context.getString(R.string.please_wait), true);
        }
        final ProgressDialog dialog = dialogg;
        boolean isSend = new RequestMplSetSalesResult().setSalesResult(data, new RequestMplSetSalesResult.OnSetSalesResult() {
            @Override
            public void onSetResultReady() {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        HelperError.showSnackMessage(messageToShow, false);
                    }
                });

            }

            @Override
            public void onError(int major, int minor) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (major == 5) {
                            retryPayDirectSetResult(data, retryCount + 1, dialog, messageToShow);
                        } else if (major == 9115 && minor == 3) {
                            dialog.dismiss();
                            HelperError.showSnackMessage(messageToShow, false);
                        } else {
                            dialog.dismiss();
                            HelperError.showSnackMessage(context.getString(R.string.server_error), false);
                        }
                    }
                });
            }
        });

        if (!isSend) {
            retryPayDirectSetResult(data, retryCount + 1, dialog, messageToShow);
        }

    }

    private static void retryPayDirectSetResult(String data, int retryCount, ProgressDialog dialog, String messageToShow) {
        if (retryCount < 20) {
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setResultOfDirectPay(data, retryCount + 1, dialog, messageToShow);
                }
            }, 500 * retryCount);
        } else {
            dialog.dismiss();
            HelperError.showSnackMessage(context.getString(R.string.server_error), false);
        }
    }
}
