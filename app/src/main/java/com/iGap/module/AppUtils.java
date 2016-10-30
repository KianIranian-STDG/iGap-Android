package com.iGap.module;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;
import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.R;
import com.iGap.interfaces.IResendMessage;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;
import com.iGap.realm.enums.RoomType;
import io.realm.Realm;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 10/22/2016.
 */

public final class AppUtils {
    private AppUtils() throws InstantiationException {
        throw new InstantiationException("This class is not for Instantiation.");
    }

    /**
     * update message status automatically
     *
     * @param view TextView message status
     */
    public static void rightMessageStatus(TextView view, String status) {
        // icons font MaterialDesign yeksan design nashodan vase hamin man dasti size ro barabar kardam
        switch (status) {
            case "DELIVERED":
                view.setTextColor(view.getContext().getResources().getColor(R.color.green));
                view.setText(G.context.getResources().getString(R.string.md_check_symbol));
                view.setTextSize(12F);
                break;
            case "FAILED":
                view.setTextColor(Color.RED);
                view.setText(G.context.getResources().getString(R.string.md_cancel_button));
                view.setTextSize(15F);
                break;
            case "SEEN":
                view.setTextColor(view.getContext().getResources().getColor(R.color.green));
                view.setText(G.context.getResources().getString(R.string.md_double_tick_indicator));
                view.setTextSize(15F);
                break;
            case "SENDING":
                view.setTextColor(view.getContext().getResources().getColor(R.color.green));
                view.setText(G.context.getResources().getString(R.string.md_clock_with_white_face));
                view.setTextSize(12F);
                break;
            case "SENT":
                view.setTextColor(view.getContext().getResources().getColor(R.color.gray));
                view.setText(G.context.getResources().getString(R.string.md_check_symbol));
                view.setTextSize(12F);
                break;
        }
    }

    public static String rightLastMessage(Resources resources, RoomType roomType, long messageId) {
        Realm realm = Realm.getDefaultInstance();
        String messageText;
        RealmRoomMessage message = realm.where(RealmRoomMessage.class)
            .equalTo(RealmRoomMessageFields.MESSAGE_ID, messageId)
            .findFirst();
        if (message == null) {
            return null;
        }
        if (message.getMessage() != null && !message.getMessage().isEmpty()) {
            return message.getMessage();
        } else {
            switch (ProtoGlobal.RoomMessageType.valueOf(message.getMessageType())) {
                case AUDIO:
                    if (message.getAttachment() == null) {
                        return null;
                    }
                    messageText = resources.getString(R.string.last_msg_format_chat,
                        message.getAttachment().getName());
                    break;
                case CONTACT:
                    if (message.getAttachment() == null) {
                        return null;
                    }
                    messageText = resources.getString(R.string.last_msg_format_chat,
                        message.getRoomMessageContact().getFirstName());
                    break;
                case FILE:
                    if (message.getAttachment() == null) {
                        return null;
                    }
                    messageText = resources.getString(R.string.last_msg_format_chat,
                        message.getAttachment().getName());
                    break;
                case GIF:
                    messageText = null;
                    break;
                case IMAGE:
                    if (message.getAttachment() == null) {
                        return null;
                    }
                    messageText = resources.getString(R.string.last_msg_format_chat,
                        message.getAttachment().getName());
                    break;
                case LOCATION:
                    messageText = null;
                    break;
                case LOG:
                    messageText = null;
                    break;
                case VIDEO:
                    if (message.getAttachment() == null) {
                        return null;
                    }
                    messageText = resources.getString(R.string.last_msg_format_chat,
                        message.getAttachment().getName());
                    break;
                case VOICE:
                    if (message.getAttachment() == null) {
                        return null;
                    }
                    messageText = resources.getString(R.string.last_msg_format_chat,
                        message.getAttachment().getName());
                    break;
                default:
                    messageText = null;
                    break;
            }
        }

        realm.close();
        return messageText;
    }

    public static MaterialDialog.Builder buildResendDialog(Context context, int failedMessagesCount,
        final IResendMessage listener) {
        List<String> items = new ArrayList<>();
        List<Integer> itemsId = new ArrayList<>();
        items.add(context.getString(R.string.resend_message));
        itemsId.add(0);
        if (failedMessagesCount > 1) {
            items.add(String.format(context.getString(R.string.resend_all_messages),
                failedMessagesCount));
            itemsId.add(1);
        }
        items.add(context.getString(R.string.delete_item_dialog));
        itemsId.add(2);

        int[] newIds = new int[itemsId.size()];
        for (Integer integer : itemsId) {
            newIds[itemsId.indexOf(integer)] = integer;
        }

        return new MaterialDialog.Builder(context).title("Resend Messages")
            .negativeText("CANCEL").items(items).itemsIds(newIds)
            .itemsCallback(new MaterialDialog.ListCallback() {
                @Override
                public void onSelection(MaterialDialog dialog, View itemView, int position,
                    CharSequence text) {
                    switch (itemView.getId()) {
                        case 0:
                            listener.resendMessage();
                            break;
                        case 1:
                            listener.resendAllMessages();
                            break;
                        case 2:
                            listener.deleteMessage();
                            break;
                    }
                }
            });
    }
}
