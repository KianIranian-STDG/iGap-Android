/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.module;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.chat.AbstractMessage;
import net.iGap.fragments.FragmentMap;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperLog;
import net.iGap.helper.HelperLogMessage;
import net.iGap.helper.HelperMimeType;
import net.iGap.libs.Tuple;
import net.iGap.messageprogress.CircleProgress.CircularProgressView;
import net.iGap.module.accountManager.DbManager;
import net.iGap.observers.interfaces.IResendMessage;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoUserUpdateStatus;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.structs.MessageObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import me.leolin.shortcutbadger.ShortcutBadger;

import static net.iGap.G.context;
import static net.iGap.module.AndroidUtils.suitablePath;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.AUDIO_TEXT_VALUE;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.AUDIO_VALUE;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.CONTACT_VALUE;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.FILE_TEXT_VALUE;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.FILE_VALUE;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.GIF_TEXT_VALUE;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.GIF_VALUE;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.IMAGE_TEXT_VALUE;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.IMAGE_VALUE;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.LOCATION_VALUE;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.STICKER_VALUE;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.TEXT_VALUE;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.VIDEO_TEXT_VALUE;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.VIDEO_VALUE;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.VOICE_VALUE;

public final class AppUtils {
    private AppUtils() throws InstantiationException {
        throw new InstantiationException("This class is not for instantiation.");
    }


    public static String suitableThumbFileName(String name) {
        if (HelperMimeType.isFileImage(name.toLowerCase())) {
            return name;
        } else {
            return name.replaceFirst("([\\w\\W]+)(\\.(\\w+))$", "$1.jpg");
        }
    }

    /**
     * change enum to string for simple showing in toolbar when get status
     *
     * @param status UserUpdateStatus
     */

    public static String getStatsForUser(String status) {

        String userStatus = "";
        if ((status == null) || (status.equals(ProtoUserUpdateStatus.UserUpdateStatus.Status.OFFLINE.toString()))) {
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

    public static void rightFileThumbnailIcon(final ImageView view, int messageType, @Nullable final MessageObject message) {

        if (message != null && message.attachment != null) {
            switch (messageType) {
                case VOICE_VALUE:
                    setImageDrawable(view, R.drawable.microphone_icon);
                    break;
                case AUDIO_VALUE:
                case AUDIO_TEXT_VALUE:
                    setImageDrawable(view, R.drawable.green_music_note);
                    break;
                case STICKER_VALUE:
                    if (message.attachment.filePath != null) {
                        G.imageLoader.displayImage(suitablePath(message.attachment.filePath), view);
                    }
                    break;
                case FILE_VALUE:
                case FILE_TEXT_VALUE:
                    if (message.attachment != null) {
                        if (message.attachment.name.toLowerCase().endsWith(".pdf")) {
                            setImageDrawable(view, R.drawable.pdf_icon);
                        } else if (message.attachment.name.toLowerCase().endsWith(".txt")) {
                            setImageDrawable(view, R.drawable.txt_icon);
                        } else if (message.attachment.name.toLowerCase().endsWith(".exe")) {
                            setImageDrawable(view, R.drawable.exe_icon);
                        } else if (message.attachment.name.toLowerCase().endsWith(".docs")) {
                            setImageDrawable(view, R.drawable.docx_icon);
                        } else {
                            setImageDrawable(view, R.drawable.file_icon);
                        }
                    } else {
                        setImageDrawable(view, R.drawable.file_icon);
                    }

                    break;
                case LOCATION_VALUE:
                    getAndSetPositionPicture(message, view);

                    break;
                default:
                    if (message.attachment != null) {
                        if (message.attachment.isFileExistsOnLocal()) {
                            G.imageLoader.displayImage(AndroidUtils.suitablePath(message.attachment.filePath), view);
                        } else if (message.attachment.isThumbnailExistsOnLocal()) {
                            G.imageLoader.displayImage(AndroidUtils.suitablePath(message.attachment.filePath), view);
                        } else {
                            view.setVisibility(View.GONE);
                        }
                    } else {
                        view.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    }

    /**
     * convert message type to appropriate text
     */
    public static String conversionMessageType(ProtoGlobal.RoomMessageType type) {
        return conversionMessageType(type, null, 0);
    }

    public static String conversionMessageType(int type) {
        return conversionMessageType(ProtoGlobal.RoomMessageType.forNumber(type), null, 0);
    }

    /**
     * convert message type to appropriate text and setText if textView isn't null
     */
    private static String returnConversionMessageType(ProtoGlobal.RoomMessageType type) {
        String result = "";

        switch (type) {
            case VOICE:
                result = G.fragmentActivity.getResources().getString(R.string.voice_message);
                break;
            case VIDEO:
            case VIDEO_TEXT:
                result = G.fragmentActivity.getResources().getString(R.string.video_message);
                break;
            case FILE:
            case FILE_TEXT:
                result = G.fragmentActivity.getResources().getString(R.string.file_message);
                break;
            case AUDIO:
            case AUDIO_TEXT:
                result = G.fragmentActivity.getResources().getString(R.string.audio_message);
                break;
            case IMAGE:
            case IMAGE_TEXT:
                result = G.fragmentActivity.getResources().getString(R.string.image_message);
                break;
            case CONTACT:
                result = G.fragmentActivity.getResources().getString(R.string.contact_message);
                break;
            case GIF:
            case GIF_TEXT:
                result = G.fragmentActivity.getResources().getString(R.string.gif_message);
                break;
            case LOCATION:
                result = G.fragmentActivity.getResources().getString(R.string.location_message);
                break;
            case WALLET:
                result = G.fragmentActivity.getResources().getString(R.string.wallet_message);
                break;
            case STICKER:
                result = G.fragmentActivity.getResources().getString(R.string.sticker);
                break;
            default:
                break;
        }

        return result;
    }

    public static String conversionMessageType(ProtoGlobal.RoomMessageType type, @Nullable TextView textView, String colorStr) {
        String result = returnConversionMessageType(type);
        if (textView != null && !result.isEmpty()) {
            textView.setTextColor(Color.parseColor(colorStr));
            textView.setText(result);
        }
        return result;
    }

    public static String conversionMessageType(ProtoGlobal.RoomMessageType type, @Nullable TextView textView, int colorId) {
        String result = returnConversionMessageType(type);
        if (textView != null && !result.isEmpty()) {
            textView.setTextColor(ContextCompat.getColor(textView.getContext(), colorId));
            textView.setText(result);
        }
        return result;
    }


    private static void getAndSetPositionPicture(final MessageObject message, final ImageView view) {

        String path = AppUtils.getLocationPath(message.location.lat, message.location.lan);

        if (new File(path).exists()) {
            G.imageLoader.displayImage(AndroidUtils.suitablePath(AppUtils.getLocationPath(message.location.lat, message.location.lan)), view);
        } else {

            FragmentMap.loadImageFromPosition(message.location.lat, message.location.lan, new FragmentMap.OnGetPicture() {
                @Override
                public void getBitmap(Bitmap bitmap) {

                    view.setImageBitmap(bitmap);
                    final String savedPath = AppUtils.saveMapToFile(bitmap, message.location.lat, message.location.lan);
                    DbManager.getInstance().doRealmTask(realm -> {
                        if (message.location != null) {
//                            message.location.setImagePath(savedPath); // TODO: 1/2/21  MESSAGE_REFACTOR
                        }
                    });
                }
            });
        }
    }

    /**
     * make location path with latitude & longitude in temp file
     *
     * @return location path in temp file
     */
    public static String getLocationPath(double locationLat, double locationLong) {
        return G.DIR_TEMP + "/location_" + String.valueOf(locationLat).replace(".", "") + "_" + String.valueOf(locationLong).replace(".", "") + ".png";
    }

    public static String saveMapToFile(Bitmap bitmap, Double latitude, Double longitude) {

        String result = "";

        try {
            if (bitmap == null) return result;

            String fileName = "/location_" + latitude.toString().replace(".", "") + "_" + longitude.toString().replace(".", "") + ".png";
            File file = new File(G.DIR_TEMP, fileName);

            OutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);

            result = file.getPath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * update message status automatically
     *
     * @param iconTextView TextView message status
     */
    public static void rightMessageStatus(FontIconTextView iconTextView, ProtoGlobal.RoomMessageStatus status, boolean isSenderMe) {

        if (iconTextView == null) {
            return;
        }
        if (!isSenderMe) {
            iconTextView.setVisibility(View.GONE);
            return;
        } else {
            iconTextView.setVisibility(View.VISIBLE);
        }
        switch (status) {
            case DELIVERED:
                iconTextView.setText(R.string.delivery_icon);
                iconTextView.setTextColor(Theme.getInstance().getSendMessageOtherTextColor(iconTextView.getContext()));
                break;
            case FAILED:
                iconTextView.setText(R.string.error_icon);
                iconTextView.setTextColor(ContextCompat.getColor(iconTextView.getContext(), R.color.red));
                break;
            case SEEN:
            case LISTENED:
                iconTextView.setText(R.string.delivery_icon);
                iconTextView.setTextColor(Theme.getInstance().getAccentColor(iconTextView.getContext()));
                break;
            case SENDING:
                iconTextView.setText(R.string.history_icon);
                iconTextView.setTextColor(Theme.getInstance().getSendMessageOtherTextColor(iconTextView.getContext()));
                break;
            case SENT:
                iconTextView.setText(R.string.check_icon);
                iconTextView.setTextColor(Theme.getInstance().getSendMessageOtherTextColor(iconTextView.getContext()));
                break;
            default:
                iconTextView.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * update message status automatically
     *
     * @param iconTextView TextView message status
     */
    public static void rightMessageStatus(TextView iconTextView, ProtoGlobal.RoomMessageStatus status, ProtoGlobal.RoomMessageType messageType, boolean isSenderMe) {
        if (iconTextView == null) {
            return;
        }
        if (!isSenderMe) {
            iconTextView.setVisibility(View.GONE);
            return;
        } else {
            iconTextView.setVisibility(View.VISIBLE);
        }

        switch (status) {
            case DELIVERED:
                iconTextView.setText(R.string.delivery_icon);
                iconTextView.setTextColor(new Theme().getSendMessageOtherTextColor(iconTextView.getContext()));
                break;
            case FAILED:
                iconTextView.setText(R.string.error_icon);
                iconTextView.setTextColor(ContextCompat.getColor(iconTextView.getContext(), R.color.red));
                break;
            case LISTENED:
            case SEEN:
                iconTextView.setText(R.string.delivery_icon);
                iconTextView.setTextColor(new Theme().getAccentColor(iconTextView.getContext()));
                break;
            case SENDING:
                iconTextView.setText(R.string.history_icon);
                iconTextView.setTextColor(new Theme().getSendMessageOtherTextColor(iconTextView.getContext()));
                break;
            case SENT:
                iconTextView.setText(R.string.check_icon);
                iconTextView.setTextColor(new Theme().getSendMessageOtherTextColor(iconTextView.getContext()));
                break;
            default:
                iconTextView.setVisibility(View.GONE);
                break;
        }
    }

    public static void setImageDrawable(ImageView view, int res) {
        view.setImageDrawable(net.iGap.messageprogress.AndroidUtils.getDrawable(G.currentActivity, res));
        // view.setImageResource(res);
    }

    /**
     * due to the message type and attachment will be returned appropriate text message
     *
     * @return appropriate text message for showing in view
     */
    public static String rightLastMessage(RealmRoomMessage message) {
        String messageText;
        if (message == null) {
            return null;
        }

        if (message.isDeleted()) {
            return G.fragmentActivity.getString(R.string.deleted_message); //return computeLastMessage(roomId);
        } else if (!TextUtils.isEmpty(message.getMessage())) {
            return message.getMessage();
        } else if (message.getForwardMessage() != null && !TextUtils.isEmpty(message.getForwardMessage().getMessage())) {
            return message.getForwardMessage().getMessage();
        } else if (message.getReplyTo() != null && !TextUtils.isEmpty(message.getReplyTo().getMessage())) {
            return message.getReplyTo().getMessage();
        } else {
            RealmAttachment attachment = message.getAttachment();
            switch (message.getForwardMessage() == null ? message.getMessageType() : message.getForwardMessage().getMessageType()) {
                case AUDIO_TEXT:
                case AUDIO:
                    if (attachment == null) {
                        return null;
                    }
                    messageText = G.fragmentActivity.getString(R.string.last_msg_format_chat, attachment.getName());
                    break;
                case CONTACT:
                    messageText = "contact"; // need to fill messageText with a String because in return check null. this string isn't important.
                    break;
                case FILE_TEXT:
                case FILE:
                    if (attachment == null) {
                        return null;
                    }
                    messageText = G.fragmentActivity.getString(R.string.last_msg_format_chat, attachment.getName());
                    break;
                case GIF_TEXT:
                case GIF:
                    if (attachment == null) {
                        return null;
                    }
                    messageText = G.fragmentActivity.getString(R.string.last_msg_format_chat, attachment.getName());
                    break;
                case IMAGE_TEXT:
                case IMAGE:
                    if (attachment == null) {
                        return null;
                    }
                    messageText = G.fragmentActivity.getString(R.string.last_msg_format_chat, attachment.getName());
                    break;
                case LOCATION:
                    messageText = G.fragmentActivity.getString(R.string.last_msg_format_chat, G.fragmentActivity.getString(R.string.location_message));
                    break;
                case LOG:
                    messageText = G.fragmentActivity.getString(R.string.last_msg_format_chat, HelperLogMessage.deserializeLog(G.fragmentActivity, message.getLogs(), false).toString());
                    break;
                case VIDEO_TEXT:
                case VIDEO:
                    if (attachment == null) {
                        return null;
                    }
                    messageText = G.fragmentActivity.getString(R.string.last_msg_format_chat, attachment.getName());
                    break;
                case VOICE:
                    if (attachment == null) {
                        return null;
                    }
                    messageText = G.fragmentActivity.getString(R.string.last_msg_format_chat, attachment.getName());
                    break;
                case WALLET:
                    messageText = "wallet"; // need to fill messageText with a String because in return check null. this string isn't important.
                    break;
                case STICKER:
                    messageText = G.fragmentActivity.getResources().getString(R.string.sticker);
                    break;
                default:
                    messageText = null;
                    break;
            }
        }

        return messageText;
    }

    /**
     * fetch type of message for show in reply view
     *
     * @param messageObject for detect message type
     * @return final message text
     */
    public static String replyTextMessage(MessageObject messageObject, Resources resources) {
        MessageObject message = RealmRoomMessage.getFinalMessage(messageObject);
        String messageText = "";
        if (message != null) {
            switch (message.messageType) {
                case TEXT_VALUE:
                    if (message.message != null) {
                        messageText = message.message;
                    }
                    break;
                case AUDIO_TEXT_VALUE:
                case AUDIO_VALUE:
                    if (message.getAttachment() == null) {
                        return null;
                    }
                    messageText = resources.getString(R.string.audio_message);
                    break;
                case CONTACT_VALUE:
                    messageText = message.contact.firstName + "\n" + message.contact.phones.get(message.contact.phones.size() - 1);
                    break;
                case FILE_TEXT_VALUE:
                case FILE_VALUE:
                    if (message.getAttachment() == null) {
                        return null;
                    }
                    messageText = resources.getString(R.string.file_message);
                    break;
                case STICKER_VALUE:
                    messageText = resources.getString(R.string.sticker);
                    break;
                case GIF_TEXT_VALUE:
                case GIF_VALUE:
                    if (message.getAttachment() == null) {
                        return null;
                    }
                    messageText = resources.getString(R.string.gif_message);
                    break;
                case IMAGE_TEXT_VALUE:
                case IMAGE_VALUE:
                    if (message.getAttachment() == null) {
                        return null;
                    }
                    messageText = resources.getString(R.string.image_message);
                    break;
                case LOCATION_VALUE:
                    messageText = resources.getString(R.string.location_message);
                    break;
                case VIDEO_TEXT_VALUE:
                case VIDEO_VALUE:
                    if (message.getAttachment() == null) {
                        return null;
                    }
                    messageText = resources.getString(R.string.video_message);
                    break;
                case VOICE_VALUE:
                    if (message.getAttachment() == null) {
                        return null;
                    }
                    messageText = resources.getString(R.string.voice_message);
                    break;
                default:
                    messageText = "";
                    break;
            }
        }

        ArrayList<Tuple<Integer, Integer>> places = AbstractMessage.getBoldPlaces(messageText);
        messageText = AbstractMessage.removeBoldMark(messageText, places);
        return messageText;
    }

    public static String computeLastMessage(long roomId) {
        return DbManager.getInstance().doRealmTask(realm -> {
            String lastMessage = "";
            RealmResults<RealmRoomMessage> realmList = realm.where(RealmRoomMessage.class).equalTo("roomId", roomId).findAll().sort("messageId", Sort.DESCENDING);
            for (RealmRoomMessage realmRoomMessage : realmList) {
                if (realmRoomMessage != null && !realmRoomMessage.isDeleted()) {
                    lastMessage = AppUtils.rightLastMessage(realmRoomMessage);
                    break;
                }
            }
            return lastMessage;
        });
    }

    public static MaterialDialog.Builder buildResendDialog(Context context, int failedMessagesCount, boolean hasTextForCopy, final IResendMessage listener) {
        List<String> items = new ArrayList<>();
        List<Integer> itemsId = new ArrayList<>();
        items.add(context.getString(R.string.resend_chat_message));
        itemsId.add(0);
        if (failedMessagesCount > 1) {
            items.add(String.format(context.getString(R.string.resend_all_messages), failedMessagesCount));
            itemsId.add(1);
        }

        items.add(context.getString(R.string.delete_item_dialog));
        itemsId.add(2);

        if (hasTextForCopy) {
            items.add(context.getString(R.string.copy_item_dialog));
            itemsId.add(3);
        }

        int[] newIds = new int[itemsId.size()];
        for (Integer integer : itemsId) {
            newIds[itemsId.indexOf(integer)] = integer;
        }

        return new MaterialDialog.Builder(context).negativeText(context.getString(R.string.cancel)).items(items).itemsIds(newIds).itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
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
                    case 3:
                        listener.copyMessage();
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

    public static void setProgresColler(ProgressBar progressBar) {

        try {

            progressBar.getIndeterminateDrawable().setColorFilter(new Theme().getAccentColor(progressBar.getContext()), PorterDuff.Mode.SRC_IN);

            //  getResources().getColor(R.color.toolbar_background)

        } catch (Exception e) {

        }
    }

    public static void setProgresColor(CircularProgressView progressBar) {

        try {

            progressBar.setColor(new Theme().getAccentColor(progressBar.getContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Uri createtUri(File file) {

        Uri outputUri;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            outputUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
        } else {
            outputUri = Uri.fromFile(file);
        }

        return outputUri;
    }

    public static void shareItem(Intent intent, MessageObject messageObject) {

        try {
            String message = messageObject.forwardedMessage != null ? messageObject.forwardedMessage.message : messageObject.message;
            if (message != null) {
                intent.putExtra(Intent.EXTRA_TEXT, message);
            }
            String filePath;
            if (messageObject.forwardedMessage != null) {
                filePath = messageObject.forwardedMessage.getAttachment().filePath != null ? messageObject.forwardedMessage.getAttachment().filePath : AndroidUtils.getFilePathWithCashId(messageObject.forwardedMessage.getAttachment().cacheId, messageObject.forwardedMessage.getAttachment().name, messageObject.messageType);
            } else {
                filePath = messageObject.getAttachment().filePath != null ? messageObject.getAttachment().filePath : AndroidUtils.getFilePathWithCashId(messageObject.getAttachment().cacheId, messageObject.getAttachment().cacheId, messageObject.messageType);
            }

            if (filePath != null) {

                Uri uri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", new File(filePath));
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } else {
                    uri = Uri.fromFile(new File(filePath));
                }
                intent.putExtra(Intent.EXTRA_STREAM, uri);
            }
        } catch (Exception e) {
            e.printStackTrace();
            HelperLog.getInstance().setErrorLog(e);
        }
    }


    public static void shareItem(Intent intent, String path) {
        try {
            if (path != null) {
                Uri uri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", new File(path));
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } else {
                    uri = Uri.fromFile(new File(path));
                }
                intent.putExtra(Intent.EXTRA_STREAM, uri);
            }

        } catch (Exception e) {
            e.printStackTrace();
            HelperLog.getInstance().setErrorLog(e);
        }
    }

    public static void setVibrator(long time) {
        Vibrator vShort = (Vibrator) G.context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vShort != null) {
            vShort.vibrate(time);
        }
    }

    public static void closeKeyboard(View v) {
        try {
            InputMethodManager imm = (InputMethodManager) G.fragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        } catch (IllegalStateException e) {
            e.getStackTrace();
        }
    }

    public static void error(String error) {
        try {

            HelperError.showSnackMessage(error, true);

        } catch (IllegalStateException e) {
            e.getStackTrace();
        }
    }

    public static long makeRandomId() {
        return SUID.id().get();
        // return Math.abs(UUID.randomUUID().getLeastSignificantBits());
    }

    public static int[] updateBadgeOnly(Realm realm, long roomId) {
        int unreadMessageCount = 0;
        int chatCount = 0;
        int[] result = new int[2];

        RealmResults<RealmRoom> realmRooms = realm.where(RealmRoom.class).equalTo("keepRoom", false).
                equalTo("mute", false).equalTo("isDeleted", false).notEqualTo("id", roomId).findAll();

        for (RealmRoom realmRoom1 : realmRooms) {
            if (realmRoom1.getUnreadCount() > 0) {
                unreadMessageCount += realmRoom1.getUnreadCount();
                ++chatCount;
            }
        }

        try {
            ShortcutBadger.applyCount(G.context, unreadMessageCount);
        } catch (Exception e) {
            e.printStackTrace();
        }

        result[0] = unreadMessageCount;
        result[1] = chatCount;
        return result;
    }

    public static void cleanBadge() {
        try {
            ShortcutBadger.applyCount(G.context, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getEmojiByUnicode(int unicode) {
        return " " + new String(Character.toChars(unicode)) + " ";
    }

}
