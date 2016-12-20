package com.iGap.helper;

import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import com.iGap.G;
import com.iGap.R;

/**
 * Created by android3 on 12/20/2016.
 */

public class HelperError {

    public static String getErrorFromCode(int majorCode, int minorCode) {
        Log.e("dddddd", "  error    " + majorCode + "  " + minorCode);

        String error = "";

        switch (majorCode) {

            case 2:
                if (minorCode == 1) error = "Login is required to perform this action";
                break;
            case 5:
                if (minorCode == 1) error = "time out  server not response";
                break;
            case 623:
                if (minorCode == 1) error = G.context.getResources().getString(R.string.E_713_1);
                break;
            case 629:
                if (minorCode == 1) {
                    error = G.context.getResources().getString(R.string.E_713_1);
                } else if (minorCode == 2) {
                    error = G.context.getResources().getString(R.string.E_713_1);
                } else if (minorCode == 3) error = G.context.getResources().getString(R.string.E_713_1);
                break;
        }

        return error;
    }

    public static void showSnackMessage(final String message) {

        if (message.length() > 0) {

            G.currentActivity.runOnUiThread(new Runnable() {
                @Override public void run() {
                    final Snackbar snack = Snackbar.make(G.currentActivity.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);

                    snack.setAction(R.string.cancel, new View.OnClickListener() {
                        @Override public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        }
    }
}