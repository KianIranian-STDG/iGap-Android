package com.iGap.adapter;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.util.ArrayMap;
import android.view.View;
import android.widget.FrameLayout;

import com.iGap.R;
import com.iGap.adapter.items.chat.AbstractMessage;
import com.iGap.adapter.items.chat.TimeItem;
import com.iGap.interfaces.IMessageItem;
import com.iGap.interfaces.OnChatMessageRemove;
import com.iGap.interfaces.OnChatMessageSelectionChanged;
import com.iGap.interfaces.OnFileDownload;
import com.iGap.interfaces.OnProgressUpdate;
import com.iGap.module.StructMessageAttachment;
import com.iGap.module.StructMessageInfo;
import com.iGap.proto.ProtoFileDownload;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmAttachment;
import com.iGap.realm.RealmAttachmentFields;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.request.RequestFileDownload;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/6/2016.
 */
public class MessagesAdapter<Item extends AbstractMessage> extends FastItemAdapter<Item> implements FastAdapter.OnLongClickListener<Item> {
    // contain sender id
    public static List<String> avatarsRequested = new ArrayList<>();
    public static List<String> usersInfoRequested = new ArrayList<>();
    public static ArrayMap<Long, Integer> uploading = new ArrayMap<>();
    public static ArrayMap<String, Integer> downloading = new ArrayMap<>();
    private OnChatMessageSelectionChanged<Item> onChatMessageSelectionChanged;
    private IMessageItem iMessageItem;
    private OnChatMessageRemove onChatMessageRemove;
    private OnLongClickListener longClickListener = new OnLongClickListener<Item>() {
        @Override
        public boolean onLongClick(View v, IAdapter<Item> adapter, Item item, int position) {

            if (item instanceof TimeItem)
                if (item.isSelected())
                    v.performLongClick();

            if (onChatMessageSelectionChanged != null) {
                onChatMessageSelectionChanged.onChatMessageSelectionChanged(getSelectedItems().size(), getSelectedItems());
            }
            return true;
        }
    };

    public MessagesAdapter(OnChatMessageSelectionChanged<Item> OnChatMessageSelectionChangedListener, final IMessageItem iMessageItemListener, final OnChatMessageRemove chatMessageRemoveListener) {
        onChatMessageSelectionChanged = OnChatMessageSelectionChangedListener;
        iMessageItem = iMessageItemListener;
        onChatMessageRemove = chatMessageRemoveListener;

        // as we provide id's for the items we want the hasStableIds enabled to speed up things
        setHasStableIds(true);

        withSelectable(true);
        withMultiSelect(true);
        withSelectOnLongClick(true);
        withOnPreLongClickListener(this);
        withOnLongClickListener(longClickListener);
        withOnClickListener(new OnClickListener<Item>() {
            @Override
            public boolean onClick(View v, IAdapter<Item> adapter, Item item, int position) {
                if (getSelectedItems().size() == 0) {
                    if (iMessageItem != null && !item.mMessage.senderID.equalsIgnoreCase("-1")) {
                        if (item.mMessage.status.equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.SENDING.toString())) {
                            return true;
                        }
                        if (item.mMessage.status.equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.FAILED.toString())) {
                            iMessageItem.onFailedMessageClick(v, item.mMessage, position);
                        } else {
                            iMessageItem.onContainerClick(v, item.mMessage, position);
                        }
                    }
                } else {
                    if (!(item instanceof TimeItem))
                        v.performLongClick();
                }
                return false;
            }
        });
    }

    public static boolean hasDownloadRequested(String token) {
        return downloading.containsKey(token);
    }

    public static boolean hasUploadRequested(long messageId) {
        return uploading.containsKey(messageId);
    }

    public List<StructMessageInfo> getFailedMessages() {
        List<StructMessageInfo> failedMessages = new ArrayList<>();
        for (Item item : getAdapterItems()) {
            if (!item.mMessage.senderID.equalsIgnoreCase("-1") && item.mMessage.status.equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.FAILED.toString())) {
                failedMessages.add(item.mMessage);
            }
        }
        return failedMessages;
    }

    public void downloadingAvatar(long peerId, int progress, long offset, StructMessageAttachment avatar) {
        for (Item item : getAdapterItems()) {
            if (item.mMessage.downloadAttachment != null && Long.parseLong(item.mMessage.senderID) == peerId) {
                int pos = getAdapterItems().indexOf(item);
                item.mMessage.senderAvatar = avatar;
                item.mMessage.downloadAttachment.progress = progress;
                item.mMessage.downloadAttachment.offset = offset;
                item.onRequestDownloadAvatar(offset, progress);
                notifyItemChanged(pos);
            }
        }
    }

    /**
     * update progress while file uploading
     * NOTE: it needs rewriting, because currently updates whole item view not just the progress
     * (almost)
     */
    public void updateProgress(long messageId, int progress) {
        if (!uploading.containsKey(messageId)) {
            uploading.put(messageId, progress);
        } else {
            int pos2 = uploading.indexOfKey(messageId);
            uploading.setValueAt(pos2, progress);
        }

        final Item item = getItemByFileIdentity(messageId);
        if (item != null) {
            final int pos = getAdapterItems().indexOf(item);

            item.updateProgress(new OnProgressUpdate() {
                @Override
                public void onProgressUpdate() {
                    notifyAdapterItemChanged(pos);
                    //set(pos, item);
                }
            });
        }
    }

    public static void requestDownload(String token, int progress, long offset) {
        if (!downloading.containsKey(token)) {
            downloading.put(token, progress);
        } else {
            int pos2 = downloading.indexOfKey(token);
            downloading.setValueAt(pos2, progress);
        }

        if (progress != 100) {
            Realm realm = Realm.getDefaultInstance();
            RealmAttachment attachment = realm.where(RealmAttachment.class).equalTo(RealmAttachmentFields.TOKEN, token).findFirst();
            if (attachment != null) {
                ProtoFileDownload.FileDownload.Selector selector = ProtoFileDownload.FileDownload.Selector.FILE;
                String identity = attachment.getToken() + '*' + selector.toString() + '*' + attachment.getSize() + '*' + attachment.getToken() + "_" + attachment.getName() + '*' + offset;
                new RequestFileDownload().download(token, offset, (int) attachment.getSize(), selector, identity);
            }

            realm.close();
        }
    }

    public void updateDownloadFields(String token, int progress, long offset) {
        requestDownload(token, progress, offset);

        for (Item item : getAdapterItems()) {
            if (item.mMessage.downloadAttachment != null && item.mMessage.downloadAttachment.token.equalsIgnoreCase(token)) {
                final int pos = getAdapterItems().indexOf(item);
                item.mMessage.downloadAttachment.offset = offset;
                item.mMessage.downloadAttachment.progress = progress;

                item.onRequestDownloadFile(offset, progress, new OnFileDownload() {
                    @Override
                    public void onFileDownloaded() {
                        notifyItemChanged(pos);
                    }
                });
            }
        }
    }

    public void updateThumbnail(String token) {
        for (Item item : getAdapterItems()) {
            if (item.mMessage.downloadAttachment != null && item.mMessage.downloadAttachment.token != null && item.mMessage.downloadAttachment.token.equalsIgnoreCase(token)) {
                final int pos = getAdapterItems().indexOf(item);
                item.onRequestDownloadThumbnail(token, true, new OnFileDownload() {
                    @Override
                    public void onFileDownloaded() {
                        notifyItemChanged(pos);
                    }
                });
            }
        }
    }

    public void updateChatAvatar(long userId, RealmRegisteredInfo registeredInfo) {
        for (Item item : getAdapterItems()) {
            if (!item.mMessage.isSenderMe() && item.mMessage.senderID.equalsIgnoreCase(Long.toString(userId))) {
                int pos = getAdapterItems().indexOf(item);
                item.mMessage.senderAvatar = StructMessageAttachment.convert(registeredInfo.getLastAvatar());
                item.mMessage.initials = registeredInfo.getInitials();
                item.mMessage.senderColor = registeredInfo.getColor();
                notifyItemChanged(pos);
            }
        }
    }

    /**
     * update message text
     *
     * @param messageId   message id
     * @param updatedText new message text
     */
    public void updateMessageText(long messageId, String updatedText) {
        List<Item> items = getAdapterItems();
        for (Item messageInfo : items) {
            if (messageInfo.mMessage.messageID.equals(Long.toString(messageId))) {
                int pos = items.indexOf(messageInfo);
                messageInfo.mMessage.messageText = updatedText;
                messageInfo.mMessage.isEdited = true;
                set(pos, messageInfo);
                break;
            }
        }
    }

    public void updateToken(long messageId, String token) {
        Item item = getItemByFileIdentity(messageId);
        if (item != null) {
            int pos = getAdapterItems().indexOf(item);
            item.mMessage.attachment.token = token;

            set(pos, item);
        }
    }

    /**
     * get item by its file hash
     * useful for finding item which tries to upload something
     *
     * @param messageId String
     * @return Item
     */
    public Item getItemByFileIdentity(long messageId) {
        for (Item item : getAdapterItems()) {
            if (item != null && item.mMessage.messageID.equalsIgnoreCase(Long.toString(messageId))) {
                return item;
            }
        }
        return null;
    }

    public void removeMessage(long messageId) {
        List<Item> items = getAdapterItems();
        for (Item messageInfo : items) {
            if (messageInfo.mMessage.messageID.equals(Long.toString(messageId))) {
                int pos = items.indexOf(messageInfo);
                if (onChatMessageRemove != null) {
                    onChatMessageRemove.onPreChatMessageRemove(messageInfo.mMessage, pos);
                }
                remove(pos);
                break;
            }
        }
    }

    /**
     * update message status
     *
     * @param messageId message id
     * @param status    ProtoGlobal.RoomMessageStatus
     */
    public void updateMessageStatus(long messageId, ProtoGlobal.RoomMessageStatus status) {
        List<Item> items = getAdapterItems();
        for (Item messageInfo : items) {
            if (messageInfo.mMessage.messageID.equals(Long.toString(messageId))) {
                int pos = items.indexOf(messageInfo);
                messageInfo.mMessage.status = status.toString();
                set(pos, messageInfo);
                break;
            }
        }
    }

    /**
     * update message id and status
     *
     * @param messageId new message id
     * @param identity  old manually defined as identity id
     * @param status    ProtoGlobal.RoomMessageStatus
     */
    public void updateMessageIdAndStatus(long messageId, String identity, ProtoGlobal.RoomMessageStatus status) {
        List<Item> items = getAdapterItems();
        for (Item messageInfo : items) {
            if (messageInfo.mMessage.messageID.equals(identity)) {
                int pos = items.indexOf(messageInfo);
                messageInfo.mMessage.status = status.toString();
                messageInfo.mMessage.messageID = Long.toString(messageId);
                set(pos, messageInfo);
                break;
            }
        }
    }

    @Override
    public void notifyAdapterItemRemoved(int position) {
        super.notifyAdapterItemRemoved(position);

        if (onChatMessageSelectionChanged != null) {
            onChatMessageSelectionChanged.onChatMessageSelectionChanged(getSelectedItems().size(), getSelectedItems());
        }
    }

    @Override
    public void deselect() {
        super.deselect();

        if (onChatMessageSelectionChanged != null) {
            onChatMessageSelectionChanged.onChatMessageSelectionChanged(getSelectedItems().size(), getSelectedItems());
        }
    }

    private void makeSelected(View v) {
        //noinspection RedundantCast
        ((FrameLayout) v).setForeground(new ColorDrawable(v.getResources().getColor(R.color.colorChatMessageSelectableItemBg)));
    }

    private void makeDeselected(View v) {
        //noinspection RedundantCast
        ((FrameLayout) v).setForeground(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public boolean onLongClick(View v, IAdapter<Item> adapter, Item item, int position) {
        if (!item.isSelected()) {
            makeSelected(v);
        } else {
            makeDeselected(v);
        }
        return false;
    }
}
