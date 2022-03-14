package net.iGap.messenger.ui.components;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import net.iGap.R;

public class ProgressDialog extends Dialog {

    /**For setBackgroundDrawable consider two way to pass drawable to it
     * Because of color as attribute do not set in drawable in api under 21 correctly, for
     * api under 21 pass drawable without attribute color*/

    public ProgressDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.progress_dialog);
        Window window = getWindow();
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            window.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.drawable_rounded_corners));
        } else {
            window.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.drawable_rounded));
        }
    }
}
