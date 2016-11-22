package com.iGap.activities;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.iGap.R;
import com.iGap.module.OnComplete;
import com.iGap.proto.ProtoGlobal;

/**
 * Created by android3 on 8/3/2016.
 */
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
                Log.i("ZZZZZZ", "showDialogMenuItemRooms: " + role);
                txtDeleteChat.setText(context.getString(R.string.delete_item_dialog) + " " + context.getString(R.string.group));
            } else {
                Log.i("ZZZZZZ", "showDialogMenuItemRooms222: " + role);

                txtDeleteChat.setText(context.getString(R.string.left) + " " + context.getString(R.string.group));
            }
        } else if (mType == ProtoGlobal.Room.Type.CHANNEL) {
            txtDeleteChat.setText(context.getString(R.string.delete_item_dialog) + " " + context.getString(R.string.channel));
        }

        txtDeleteChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str0 = "";
                String str = "";
                if (mType == ProtoGlobal.Room.Type.CHAT) {
                    ;
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
                    str0 = context.getString(R.string.do_you_want_delete_this);
                    str = context.getString(R.string.channel);
                }

                showDialogNotification(context, str0 + str + " ?", complete, "txtDeleteChat");

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

        final Dialog dialog = new Dialog(context);
        // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setTitle(context.getString(R.string.igap));
        dialog.setContentView(R.layout.dialog_notification);
        dialog.show();

        TextView txtMessage = (TextView) dialog.findViewById(R.id.md_txt_message);
        txtMessage.setText(Message);

        Button tvYes = (Button) dialog.findViewById(R.id.md_btn_yes);
        tvYes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (complete != null) complete.complete(true, result, "yes");

                dialog.cancel();
            }
        });

        Button tvNo = (Button) dialog.findViewById(R.id.md_btn_no);
        tvNo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                dialog.cancel();
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        // lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);
        dialog.show();
    }
}
