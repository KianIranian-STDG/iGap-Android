package com.iGap.module;

import android.graphics.Color;
import android.widget.TextView;

import com.iGap.G;
import com.iGap.R;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 10/22/2016.
 */

public final class AppUtils {
    private AppUtils() throws InstantiationException {
        throw new InstantiationException("This class is not for Instantiation.");
    }

    /**
     * update message status automatically
     *
     * @param view TextView message status
     */
    public static void updateMessageStatus(TextView view, String status) {
        // icons font MaterialDesign yeksan design nashodan vase hamin man dasti size ro barabar kardam
        switch (status) {
            case "DELIVERED":
                view.setTextColor(view.getContext().getResources().getColor(R.color.green));
                view.setText(G.context.getResources().getString(R.string.md_check_symbol));
                view.setTextSize(12F);
                break;
            case "FAILED":
                view.setTextColor(Color.RED);
                view.setText(G.context.getResources().getString(R.string.md_cancel_button));
                view.setTextSize(15F);
                break;
            case "SEEN":
                view.setTextColor(view.getContext().getResources().getColor(R.color.green));
                view.setText(G.context.getResources().getString(R.string.md_double_tick_indicator));
                view.setTextSize(15F);
                break;
            case "SENDING":
                view.setTextColor(view.getContext().getResources().getColor(R.color.green));
                view.setText(G.context.getResources().getString(R.string.md_clock_with_white_face));
                view.setTextSize(12F);
                break;
            case "SENT":
                view.setTextColor(view.getContext().getResources().getColor(R.color.gray));
                view.setText(G.context.getResources().getString(R.string.md_check_symbol));
                view.setTextSize(12F);
                break;
        }
    }
}
