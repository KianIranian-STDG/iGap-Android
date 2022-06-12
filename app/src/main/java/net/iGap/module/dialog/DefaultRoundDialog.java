package net.iGap.module.dialog;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import net.iGap.R;
import net.iGap.messenger.theme.Theme;

public class DefaultRoundDialog extends AlertDialog.Builder {

    public DefaultRoundDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    public AlertDialog show() {
        AlertDialog dialog = this.create();
        dialog.getWindow().setBackgroundDrawable(Theme.tintDrawable(ContextCompat.getDrawable(getContext(), R.drawable.dialog_background_dark), getContext(), Theme.getColor(Theme.key_window_background)));
        dialog.show();
        return dialog;
    }
}
