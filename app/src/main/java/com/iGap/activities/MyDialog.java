package com.iGap.activities;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.R;
import com.iGap.module.OnComplete;
import com.iGap.proto.ProtoGlobal;

public class MyDialog {

    public static void showDialogMenuItemRooms(final Context context, final ProtoGlobal.Room.Type mType, boolean isMute, final String role, final OnComplete complete) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.chat_popup_dialog);
        dialog.setCancelable(true);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        // layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.gravity = Gravity.CENTER;

        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);

        TextView txtMuteNotification =
                (TextView) dialog.findViewById(R.id.cm_txt_mute_notification);
        TextView txtClearHistory = (TextView) dialog.findViewById(R.id.cm_txt_clear_history);
        TextView txtDeleteChat = (TextView) dialog.findViewById(R.id.cm_txt_delete_chat);
        TextView txtCancle = (TextView) dialog.findViewById(R.id.cm_txt_cancle);

        txtMuteNotification.setText(isMute ? context.getString(R.string.unmute_notification)
                : context.getString(R.string.mute_notification));

        txtMuteNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (complete != null) complete.complete(true, "txtMuteNotification", "");
                dialog.cancel();
            }
        });

        txtClearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (complete != null) complete.complete(true, "txtClearHistory", "");
                dialog.cancel();
            }
        });

        if (mType == ProtoGlobal.Room.Type.CHAT) {
            txtDeleteChat.setText(context.getString(R.string.delete_item_dialog) + " " + context.getString(R.string.chat));
        } else if (mType == ProtoGlobal.Room.Type.GROUP) {
            if (role.equals("OWNER")) {

                txtDeleteChat.setText(context.getString(R.string.delete_item_dialog) + " " + context.getString(R.string.group));
            } else {

                txtDeleteChat.setText(context.getString(R.string.left) + " " + context.getString(R.string.group));
            }
        } else if (mType == ProtoGlobal.Room.Type.CHANNEL) {


            txtDeleteChat.setText(context.getString(R.string.delete_item_dialog) + " " + context.getString(R.string.channel));

            if (role.equals("OWNER")) {

                txtDeleteChat.setText(context.getString(R.string.delete_item_dialog) + " " + context.getString(R.string.channel));
            } else {

                txtDeleteChat.setText(context.getString(R.string.left) + " " + context.getString(R.string.channel));
            }
        }

        txtDeleteChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str0 = "";
                String str = "";
                if (mType == ProtoGlobal.Room.Type.CHAT) {
                    str0 = context.getString(R.string.do_you_want_delete_this);
                    str = context.getString(R.string.chat);
                } else if (mType == ProtoGlobal.Room.Type.GROUP) {
                    str = context.getString(R.string.group);
                    if (role.equals("OWNER")) {
                        str0 = context.getString(R.string.do_you_want_delete_this);
                    } else {
                        str0 = context.getString(R.string.do_you_want_left_this);
                    }

                } else if (mType == ProtoGlobal.Room.Type.CHANNEL) {


                    str = context.getString(R.string.channel);
                    if (role.equals("OWNER")) {
                        str0 = context.getString(R.string.do_you_want_delete_this);
                    } else {
                        str0 = context.getString(R.string.do_you_want_left_this);
                    }
                }

                showDialogNotification(context, str0, complete, "txtDeleteChat");

                dialog.cancel();
            }
        });

        txtCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
    }

    public static void showDialogNotification(Context context, String Message, final OnComplete complete, final String result) {


        new MaterialDialog.Builder(context)
                .title(G.context.getResources().getString(R.string.igap))
                .titleColor(G.context.getResources().getColor(R.color.toolbar_background))
                .content(Message)
                .positiveText(G.context.getResources().getString(R.string.B_ok))
                .negativeText(G.context.getResources().getString(R.string.B_cancel))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (complete != null) complete.complete(true, result, "yes");

                        dialog.cancel();

                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.cancel();
                    }
                })
                .show();
    }
}
