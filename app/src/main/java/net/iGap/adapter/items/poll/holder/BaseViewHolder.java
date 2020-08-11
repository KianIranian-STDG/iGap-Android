package net.iGap.adapter.items.poll.holder;

import android.app.Activity;
import android.os.SystemClock;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.poll.PollAdapter;
import net.iGap.adapter.items.poll.PollItem;
import net.iGap.adapter.items.poll.PollItemField;
import net.iGap.request.RequestClientSetPollItemClick;

public abstract class BaseViewHolder extends RecyclerView.ViewHolder {
    private long mLastClickTime = 0;
    private PollAdapter pollAdapter;

    BaseViewHolder(PollAdapter pollAdapter, @NonNull View itemView) {
        super(itemView);
        this.pollAdapter = pollAdapter;
    }

    public abstract void bindView(PollItem item);

    protected void checkTickVisibility(PollItemField pollItemField, View tick) {
        if (pollItemField.clicked) {
            tick.setVisibility(View.VISIBLE);
        } else {
            tick.setVisibility(View.GONE);
        }
    }

    void loadImage(ImageView imageView, String url) {
        if (url.endsWith(".gif")) {
            Glide.with(G.context)
                    .asGif()
                    .load(url)
                    .into(imageView);
        } else {
            Glide.with(G.context).load(url).into(imageView);
        }
    }

    void handlePollFieldsClick(PollItemField pollField) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        Activity activity = G.currentActivity;
        mLastClickTime = SystemClock.elapsedRealtime();
        if (pollField.clickable) {
            if (!pollField.clicked) {
                new MaterialDialog.Builder(activity).content(activity.getString(R.string.poll_dialog_question)).negativeText(R.string.st_dialog_reset_all_notification_no).onNegative((dialog, which) -> {
                    dialog.dismiss();
                }).positiveText(R.string.kuknos_tradeDialogDelete_btn).onPositive(((dialog1, which) -> {
                    new RequestClientSetPollItemClick().setPollClicked(pollField.id, new RequestClientSetPollItemClick.OnSetPollItemClick() {
                        @Override
                        public void onSet() {
                            pollField.clicked = true;
                            pollField.sum += 1;
                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    pollAdapter.notifyChangeData();
                                    showDialog(activity, activity.getString(R.string.poll_ok));
                                }
                            });
                        }

                        @Override

                        public void onError(int major, int minor) {
                            G.handler.post(() -> {
                                if (major == 677) {
                                    if (minor == 6) {
                                        showDialog(activity, activity.getString(R.string.maximum_poll));
                                    } else if (minor == 7) {
                                        showDialog(activity, activity.getString(R.string.before_try));
                                    }
                                }
                            });
                        }
                    });
                })).show();

            } else {
                showDialog(activity, activity.getString(R.string.before_try));
            }
        }
    }

    private static void showDialog(Activity activity, String message) {
        new MaterialDialog.Builder(activity).content(message).positiveText(R.string.dialog_ok)
                .onPositive((dialog, which) -> {
                })
                .show();
    }

}
