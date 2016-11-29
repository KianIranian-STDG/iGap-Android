package com.iGap.module;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.R;
import com.iGap.interfaces.IResendMessage;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoUserUpdateStatus;
import com.iGap.realm.RealmAttachment;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

import static com.iGap.G.context;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 10/22/2016.
 */

public final class AppUtils {
    private AppUtils() throws InstantiationException {
        throw new InstantiationException("This class is not for instantiation.");
    }

    public static String[] exts = {".jpg", ".jpeg", ".gif", ".png", ".tif"};

    public static String suitableThumbFileName(String name) {
        boolean isImage = false;
        for (String ext : exts) {
            if (name.endsWith(ext)) {
                isImage = true;
                break;
            }
        }

        if (isImage) {
            return name;
        } else {
            return name.replaceFirst("([\\w\\W]+)(\\.(\\w+))$", "$1.jpg");
        }
    }

    /**
     * change enum to string for simple showing in toolbar when get status
     *
     * @param status UserUpdateStatus
     * @return
     */

    public static String setStatsForUser(String status) {

        String userStatus = "Online";
        if (status.equals(ProtoUserUpdateStatus.UserUpdateStatus.Status.OFFLINE.toString())) {
            userStatus = context.getResources().getString(R.string.last_seen_recently);
        } else if (status.equals(ProtoGlobal.RegisteredUser.Status.LONG_TIME_AGO.toString())) {
            userStatus = context.getResources().getString(R.string.long_time_ago);
        } else if (status.equals(ProtoGlobal.RegisteredUser.Status.LAST_MONTH.toString())) {
            userStatus = context.getResources().getString(R.string.last_month);
        } else if (status.equals(ProtoGlobal.RegisteredUser.Status.LAST_WEEK.toString())) {
            userStatus = context.getResources().getString(R.string.last_week);
        } else if (status.equals(ProtoGlobal.RegisteredUser.Status.RECENTLY.toString())) {
            userStatus = context.getResources().getString(R.string.recently);
        } else if (status.equals(ProtoGlobal.RegisteredUser.Status.SUPPORT.toString())) {
            userStatus = context.getResources().getString(R.string.support);
        } else if (status.equals(ProtoGlobal.RegisteredUser.Status.SERVICE_NOTIFICATIONS.toString())) {
            userStatus = context.getResources().getString(R.string.service_notification);
        } else if (status.equals(ProtoGlobal.RegisteredUser.Status.ONLINE.toString())) {
            userStatus = context.getResources().getString(R.string.online);
        } else if (status.equals(ProtoGlobal.RegisteredUser.Status.EXACTLY.toString())) {
            userStatus = ProtoGlobal.RegisteredUser.Status.EXACTLY.toString();
        }
        return userStatus;
    }

    public static void rightFileThumbnailIcon(ImageView view, ProtoGlobal.RoomMessageType messageType, @Nullable RealmAttachment attachment) {
        switch (messageType) {
            case VOICE:
                view.setImageResource(R.drawable.microphone_icon);
                break;
            case AUDIO:
            case AUDIO_TEXT:
                view.setImageResource(R.drawable.green_music_note);
            case FILE:
            case FILE_TEXT:
                if (attachment.getName().toLowerCase().endsWith(".pdf")) {
                    view.setImageResource(R.drawable.pdf_icon);
                } else if (attachment.getName().toLowerCase().endsWith(".txt")) {
                    view.setImageResource(R.drawable.txt_icon);
                } else if (attachment.getName().toLowerCase().endsWith(".exe")) {
                    view.setImageResource(R.drawable.exe_icon);
                } else if (attachment.getName().toLowerCase().endsWith(".docs")) {
                    view.setImageResource(R.drawable.docx_icon);
                } else {
                    view.setImageResource(R.drawable.file_icon);
                }
                break;
            default:
                if (attachment != null) {
                    if (attachment.isFileExistsOnLocal()) {
                        ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(attachment.getLocalFilePath()), view);
                    } else if (attachment.isThumbnailExistsOnLocal()) {
                        ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(attachment.getLocalThumbnailPath()), view);
                    } else {
                        view.setVisibility(View.GONE);
                        // TODO: 11/15/2016 [Alireza] request download bede
                    }
                } else {
                    view.setVisibility(View.GONE);
                }
                break;
        }
    }

    /**
     * update message status automatically
     *
     * @param view TextView message status
     */
    public static void rightMessageStatus(ImageView view, String status) {
        // icons font MaterialDesign yeksan design nashodan vase hamin man dasti size ro barabar kardam
        switch (ProtoGlobal.RoomMessageStatus.valueOf(status)) {
            case DELIVERED:
                DrawableCompat.setTint(view.getDrawable(), Color.WHITE);
                view.setImageResource(R.drawable.ic_check);
                break;
            case FAILED:
                DrawableCompat.setTint(view.getDrawable(), Color.RED);
                view.setImageResource(R.drawable.ic_error);
                break;
            case SEEN:
                DrawableCompat.setTint(view.getDrawable(), Color.WHITE);
                view.setImageResource(R.drawable.ic_double_check);
                break;
            case SENDING:
                DrawableCompat.setTint(view.getDrawable(), view.getContext().getResources().getColor(R.color.statusSendingColor));
                view.setImageResource(R.drawable.ic_clock);
                break;
            case SENT:
                DrawableCompat.setTint(view.getDrawable(), view.getContext().getResources().getColor(R.color.statusSendingColor));
                view.setImageResource(R.drawable.ic_check);
                break;
        }
    }

    public static long findLastMessageId(long roomId) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmRoomMessage> roomMessages = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).findAllSorted(RealmRoomMessageFields.MESSAGE_ID, Sort.ASCENDING);
        realm.close();
        if (!roomMessages.isEmpty()) {
            return roomMessages.first().getMessageId();
        }
        return 0;
    }

    public static String rightLastMessage(Resources resources, ProtoGlobal.Room.Type roomType, RealmRoomMessage message) {
        String messageText;
        if (message == null) {
            return null;
        }
        if (!TextUtils.isEmpty(message.getMessage())) {
            return message.getMessage();
        } else if (message.getForwardMessage() != null && !TextUtils.isEmpty(message.getForwardMessage().getMessage())) {
            return message.getForwardMessage().getMessage();
        } else if (message.getReplyTo() != null && !TextUtils.isEmpty(message.getReplyTo().getMessage())) {
            return message.getReplyTo().getMessage();
        } else {
            switch (message.getForwardMessage() == null ? message.getMessageType() : message.getForwardMessage().getMessageType()) {
                case AUDIO:
                    if (message.getAttachment() == null) {
                        return null;
                    }
                    messageText = resources.getString(R.string.last_msg_format_chat,
                            message.getAttachment().getName());
                    break;
                case CONTACT:
                    messageText = "contact"; // need to fill messageText with a String because in return check null
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
                .negativeText(context.getString(R.string.cancel)).items(items).itemsIds(newIds)
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

    public static String humanReadableDuration(double d) {
        String output = Double.toString(d);

        if (output.contains(".")) {
            String[] split = output.split("\\.");
            if (split[1].length() > 2) {
                output = split[0] + "." + split[1].charAt(0) + split[1].charAt(1);
            }
        }

        return output;
    }
}
