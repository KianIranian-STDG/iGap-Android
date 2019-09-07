package net.iGap.dialog;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import net.iGap.G;
import net.iGap.R;

public class DefaultRoundDialog extends AlertDialog.Builder {

    public DefaultRoundDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    public AlertDialog show() {
        AlertDialog dialog = this.create();
        dialog.getWindow().setBackgroundDrawable(getContext().getResources().getDrawable(G.isDarkTheme ? R.drawable.dialog_background_dark : R.drawable.dialog_background));
        dialog.show();
        return dialog;
    }
}
