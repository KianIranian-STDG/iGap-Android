package net.iGap.helper;

import android.app.ProgressDialog;
import android.content.Context;

class iGapLoading extends ProgressDialog {

    public iGapLoading(Context context) {
        super(context);
    }

    public static iGapLoading createGlobalLoading(Context context) {
        iGapLoading loading = new iGapLoading(context);
        loading.setTitle("");
        loading.setMessage("Loading. Please wait...");
        loading.setIndeterminate(true);
        loading.setCancelable(false);

        return loading;
    }
}
