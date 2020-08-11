package net.iGap.module.dialog;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import net.iGap.R;
import net.iGap.module.Theme;

public class DefaultRoundDialog extends AlertDialog.Builder {

    public DefaultRoundDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    public AlertDialog show() {
        AlertDialog dialog = this.create();
        dialog.getWindow().setBackgroundDrawable(new Theme().tintDrawable(ContextCompat.getDrawable(getContext(), R.drawable.dialog_background_dark), getContext(), R.attr.rootBackgroundColor));
        dialog.show();
        return dialog;
    }
}
