package net.iGap.module.dialog;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;

import net.iGap.R;
import net.iGap.messenger.theme.Theme;
import net.iGap.viewmodel.WaitTimeModel;

import java.util.Locale;

public class WaitingDialog extends AlertDialog {

    private AppCompatTextView timerTextView;
    private CountDownTimer countDownTimer;

    public WaitingDialog(@NonNull Context context, WaitTimeModel timeModel) {
        super(context);
        countDownTimer = new CountDownTimer(timeModel.getTime() * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000 % 60;
                long minutes = millisUntilFinished / (60 * 1000) % 60;
                long hour = millisUntilFinished / (3600 * 1000);

                if (timerTextView != null) {
                    timerTextView.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", hour, minutes, seconds));
                }
            }

            @Override
            public void onFinish() {
                try {
                    if (isShowing() && timerTextView != null) {
                        timerTextView.setText("00:00");
                        dismiss();
                    }
                } catch (final IllegalArgumentException e) {
                    // Handle or log or ignore
                } catch (final Exception e) {
                    // Handle or log or ignore
                }
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_remind_time);

        timerTextView = findViewById(R.id.remindTime);
        timerTextView.setTextColor(Theme.getColor(Theme.key_title_text));

        final TextView textReason = findViewById(R.id.textReason);
        textReason.setTextColor(Theme.getColor(Theme.key_title_text));
        final TextView textRemindTime = findViewById(R.id.textRemindTime);
        textRemindTime.setTextColor(Theme.getColor(Theme.key_title_text));

        View positiveButtonView = findViewById(R.id.positiveButton);
        if (positiveButtonView != null) {
            positiveButtonView.setOnClickListener(v -> dismiss());
        }
        countDownTimer.start();

    }

    @Override
    public void setOnDismissListener(@Nullable OnDismissListener listener) {
        super.setOnDismissListener(listener);
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }
}
