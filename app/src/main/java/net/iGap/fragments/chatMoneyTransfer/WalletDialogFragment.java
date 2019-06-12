package net.iGap.fragments.chatMoneyTransfer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;

public class WalletDialogFragment extends DialogFragment {

    private View rootView;
    private TextView titleTv;
    private TextView messageTv;
    private String message;
    private String title;
    private TextView confirm;
    private boolean showStatus;

//    public WalletDialogFragment getInstance(String title, String message) {
//        WalletDialogFragment dialogFragment = new WalletDialogFragment();
//        dialogFragment.message = message;
//        dialogFragment.title = title;
//        return dialogFragment;
//    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_wallet_dialog, null);
        builder.setView(rootView);
        builder.setCancelable(true);

        titleTv = rootView.findViewById(R.id.tv_walletDialog_title);
        messageTv = rootView.findViewById(R.id.tv_walletDialog_message);
        confirm = rootView.findViewById(R.id.tv_walletDialog_confirm);

        darkModeHandler(rootView);
        titleTv.setTextColor(darkModeHandler());
        messageTv.setTextColor(darkModeHandler());

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        confirm.setOnClickListener(v -> dismiss());
        messageTv.setText(message);
        titleTv.setText(title);

    }

    private void darkModeHandler(View view) {
        if (G.isDarkTheme) {
            view.setBackgroundColor(getContext().getResources().getColor(R.color.background_setting_dark));
        } else {
            view.setBackgroundColor(getContext().getResources().getColor(R.color.white));
        }
    }

    private int darkModeHandler() {
        if (G.isDarkTheme) {
            return getContext().getResources().getColor(R.color.white);
        } else {
            return getContext().getResources().getColor(R.color.black);
        }
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public boolean isShowStatus() {
        return showStatus;
    }

    public void setShowStatus(boolean showStatus) {
        this.showStatus = showStatus;
    }
}
