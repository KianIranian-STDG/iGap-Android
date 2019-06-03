package net.iGap.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;

public class SubmitScoreDialog extends Dialog {

    private String message;
    private boolean isOk;

    public SubmitScoreDialog(Activity a, String message, boolean isOk) {
        super(a);
        this.isOk = isOk;
        this.message = message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setCancelable(false);
        setContentView(R.layout.submit_score_dialog);
        Button ok = findViewById(R.id.ok);
        TextView image = findViewById(R.id.image);
        TextView txt_message = findViewById(R.id.txt_message);
        txt_message.setText(message);
        if (isOk) {
            image.setText(G.context.getString(R.string.md_get_score_ok));
            image.setTextColor(G.context.getResources().getColor(R.color.green));
        }  else {
            image.setText(G.context.getString(R.string.md_get_score_failed));
            image.setTextColor(G.context.getResources().getColor(R.color.red));
        }

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubmitScoreDialog.this.dismiss();
            }
        });

    }
}
