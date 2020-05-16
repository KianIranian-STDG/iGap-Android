package net.iGap.helper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;

import com.top.lib.mpl.view.CardToCardInitiator;

import net.iGap.G;
import net.iGap.R;
import net.iGap.request.RequestMplGetCardToCardToken;
import net.iGap.request.RequestMplSetCardToCardResult;

public class CardToCardHelper {

    public static final int requestCodeCardToCard = 19800;
    private static long toUserId = 0;

    public static void CallCardToCard(Activity activity) {
        CallCardToCard(activity, 0);
    }

    public static void CallCardToCard(Activity activity, long to_UserId) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        toUserId = to_UserId;

        final ProgressDialog dialog = ProgressDialog.show(activity, G.context.getResources().getString(R.string.app_name), G.context.getString(R.string.please_wait), true);
        boolean isSend = new RequestMplGetCardToCardToken().mplGetToken(new RequestMplGetCardToCardToken.OnMplCardToCardToken() {
            @Override
            public void onToken(String token) {
                G.runOnUiThread(() -> {
                    if (!activity.isFinishing()) {
                        Intent intent = new Intent(G.context, CardToCardInitiator.class);
                        intent.putExtra("Token", token);

                        activity.startActivityForResult(intent, requestCodeCardToCard);
                        dialog.dismiss();
                    }
                });
            }

            @Override
            public void onError(int major, int minor) {
                G.runOnUiThread(() -> {
                    dialog.dismiss();
                    if (major == 5) {
                        HelperError.showSnackMessage(G.context.getString(R.string.wallet_error_server), false);
                    } else {
                        HelperError.showSnackMessage(G.context.getString(R.string.server_error), false);
                    }
                });
            }
        }, to_UserId);

        if (!isSend) {
            HelperError.showSnackMessage(G.context.getString(R.string.wallet_error_server), false);
            dialog.dismiss();
        }
    }

    public static void NewCallCardToCard(Activity activity, long to_UserId, int amount, String cardNumber) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        toUserId = to_UserId;

        final ProgressDialog dialog = ProgressDialog.show(activity, G.context.getResources().getString(R.string.app_name), G.context.getString(R.string.please_wait), true);
        boolean isSend = new RequestMplGetCardToCardToken().mplGetToken(new RequestMplGetCardToCardToken.OnMplCardToCardToken() {
            @Override
            public void onToken(String token) {
                G.runOnUiThread(() -> {
                    if (!activity.isFinishing()) {
                        Intent intent = new Intent(G.context, CardToCardInitiator.class);
                        intent.putExtra("Token", token);
                        intent.putExtra("destinationCard", cardNumber);
                        intent.putExtra("amount", amount);
                        activity.startActivityForResult(intent, requestCodeCardToCard);
                        dialog.dismiss();
                    }
                });
            }

            @Override
            public void onError(int major, int minor) {
                G.runOnUiThread(() -> {
                    dialog.dismiss();
                    if (major == 5) {
                        HelperError.showSnackMessage(G.context.getString(R.string.wallet_error_server), false);
                    } else {
                        HelperError.showSnackMessage(G.context.getString(R.string.server_error), false);
                    }
                });
            }
        }, to_UserId);

        if (!isSend) {
            HelperError.showSnackMessage(G.context.getString(R.string.wallet_error_server), false);
            dialog.dismiss();
        }
    }


    public static void setResultOfCardToCard(String data, int retryCount, ProgressDialog dialogg, String messageToShow) {
        if (dialogg == null) {
            dialogg = ProgressDialog.show(G.currentActivity, G.context.getResources().getString(R.string.app_name), G.context.getString(R.string.please_wait), true);
        }
        final ProgressDialog dialog = dialogg;
        boolean isSend = new RequestMplSetCardToCardResult().onSetCardToCardResult(data, toUserId, new RequestMplSetCardToCardResult.OnSetCardToCardResult() {
            @Override
            public void onSetResultCardToCardReady() {
                G.runOnUiThread(() -> {
                    dialog.dismiss();
                    if (messageToShow.length() > 0)
                        HelperError.showSnackMessage(messageToShow, false);
                });
            }

            @Override
            public void onError(int major, int minor) {
                G.runOnUiThread(() -> {
                    if (major == 5) {
                        retryCardToCardSetResult(data, retryCount + 1, dialog, messageToShow);
                    } else {
                        dialog.dismiss();
                        if (messageToShow.length() > 0)
                            HelperError.showSnackMessage(messageToShow, false);
                    }
                });
            }
        });

        if (!isSend) {
            retryCardToCardSetResult(data, retryCount + 1, dialog, messageToShow);
        }

    }

    private static void retryCardToCardSetResult(String data, int retryCount, ProgressDialog dialog, String messageToShow) {
        if (retryCount < 6) {
            G.handler.postDelayed(() -> setResultOfCardToCard(data, retryCount + 1, dialog, messageToShow), 700 * retryCount);
        } else {
            dialog.dismiss();
            if (messageToShow.length() > 0)
                HelperError.showSnackMessage(messageToShow, false);
        }
    }
}
