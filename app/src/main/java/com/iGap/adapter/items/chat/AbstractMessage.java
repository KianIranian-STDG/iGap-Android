package com.iGap.adapter.items.chat;

import android.support.annotation.CallSuper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.adapter.MessagesAdapter;
import com.iGap.helper.HelperStringAnalayser;
import com.iGap.interfaces.IChatItemAttachment;
import com.iGap.interfaces.IChatItemAvatar;
import com.iGap.interfaces.IMessageItem;
import com.iGap.module.AndroidUtils;
import com.iGap.module.AppUtils;
import com.iGap.module.MyType;
import com.iGap.module.StructDownloadAttachment;
import com.iGap.module.StructMessageInfo;
import com.iGap.module.TimeUtils;
import com.iGap.module.enums.LocalFileType;
import com.iGap.proto.ProtoFileDownload;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmAttachment;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmRegisteredInfoFields;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;
import com.iGap.request.RequestFileDownload;
import com.iGap.request.RequestUserInfo;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.util.List;

import io.meness.github.messageprogress.MessageProgress;
import io.meness.github.messageprogress.OnMessageProgressClick;
import io.meness.github.messageprogress.OnProgress;
import io.meness.github.messageprogress.ProgressProcess;
import io.realm.Realm;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/6/2016.
 */
public abstract class AbstractMessage<Item extends AbstractMessage<?, ?>, VH extends RecyclerView.ViewHolder> extends AbstractItem<Item, VH> implements IChatItemAttachment<VH>, IChatItemAvatar {
    public IMessageItem messageClickListener;
    public StructMessageInfo mMessage;
    public boolean directionalBased = true;
    public ProtoGlobal.Room.Type type;

    public AbstractMessage(boolean directionalBased, ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        this.directionalBased = directionalBased;
        this.type = type;
        this.messageClickListener = messageClickListener;
    }

    protected void setTextIfNeeded(TextView view, String msg) {
        if (!TextUtils.isEmpty(msg)) {
            view.setText(HelperStringAnalayser.analaysHash(msg, mMessage.messageID));
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    public AbstractMessage setMessage(StructMessageInfo message) {
        this.mMessage = message;
        return this;
    }

    private void requestForAvatar() {
        // create new download attachment once with attachment token
        if (mMessage.downloadAttachment == null) {
            mMessage.downloadAttachment = new StructDownloadAttachment(mMessage.senderAvatar.token);
        }

        // request thumbnail
        if (!MessagesAdapter.avatarsRequested.contains(mMessage.senderID)) {
            onRequestDownloadAvatar(mMessage.downloadAttachment.offset, mMessage.downloadAttachment.progress);
            // prevent from multiple requesting thumbnail
            MessagesAdapter.avatarsRequested.add(mMessage.senderID);
        }
    }

    private void requestForUserInfo() {
        if (!MessagesAdapter.usersInfoRequested.contains(mMessage.senderID)) {
            RequestUserInfo requestUserInfo = new RequestUserInfo();
            requestUserInfo.userInfo(Long.parseLong(mMessage.senderID));

            MessagesAdapter.usersInfoRequested.add(mMessage.senderID);
        }
    }

    @Override
    public void onRequestDownloadAvatar(long offset, int progress) {
        ProtoFileDownload.FileDownload.Selector selector = ProtoFileDownload.FileDownload.Selector.LARGE_THUMBNAIL;
        String fileName = mMessage.downloadAttachment.token + "_" + mMessage.senderAvatar.name;
        if (progress == 100) {
            mMessage.senderAvatar.setLocalThumbnailPath(Long.parseLong(mMessage.senderID), G.DIR_IMAGE_USER + "/" + fileName);

            try {
                AndroidUtils.cutFromTemp(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // I don't use offset in getting thumbnail
            String identity = mMessage.downloadAttachment.token
                    + '*'
                    + selector.toString()
                    + '*'
                    + mMessage.senderAvatar.largeThumbnail.size
                    + '*'
                    + fileName
                    + '*'
                    + mMessage.downloadAttachment.offset
                    + "*"
                    + Boolean.toString(true)
                    + "*"
                    + mMessage.senderID
                    + "*"
                    + type.toString();

            new RequestFileDownload().download(mMessage.downloadAttachment.token, offset, (int) mMessage.senderAvatar.largeThumbnail.size, selector, identity);
        }
    }

    @Override
    public Item withIdentifier(long identifier) {
        return super.withIdentifier(identifier);
    }

    @Override
    @CallSuper
    public void bindView(final VH holder, List payloads) {
        super.bindView(holder, payloads);

        mMessage.view = holder.itemView;  // this use for select foregroung in activity chat  for search item and hash item

        //noinspection RedundantCast
        if (!isSelected() && ((FrameLayout) holder.itemView).getForeground() != null) {
            //noinspection RedundantCast
            ((FrameLayout) holder.itemView).setForeground(null);
        }

        // only will be called when message layout is directional-base (e.g. single chat)
        if (directionalBased) {
            if (mMessage.sendType == MyType.SendType.recvive) {
                updateLayoutForReceive(holder);
            } else if (mMessage.sendType == MyType.SendType.send) {
                updateLayoutForSend(holder);
            }
        }

        if (mMessage.sendType == MyType.SendType.send) {
            AppUtils.rightMessageStatus((TextView) holder.itemView.findViewById(R.id.cslr_txt_tic), mMessage.status);
        }

        // display 'edited' indicator beside message time if message was edited
        if (holder.itemView.findViewById(R.id.txtEditedIndicator) != null) {
            if (mMessage.isEdited) {
                holder.itemView.findViewById(R.id.txtEditedIndicator).setVisibility(View.VISIBLE);
            } else {
                holder.itemView.findViewById(R.id.txtEditedIndicator).setVisibility(View.GONE);
            }
        }

        // display user avatar only if chat type is GROUP
        if (type == ProtoGlobal.Room.Type.GROUP) {
            if (!mMessage.isSenderMe()) {
                holder.itemView.findViewById(R.id.messageSenderAvatar).setVisibility(View.VISIBLE);

                holder.itemView.findViewById(R.id.messageSenderAvatar).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        messageClickListener.onSenderAvatarClick(v, mMessage, holder.getAdapterPosition());
                    }
                });
                if (mMessage.senderAvatar != null) {
                    if (mMessage.senderAvatar.isFileExistsOnLocal()) {
                        ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(mMessage.senderAvatar.getLocalFilePath()), (ImageView) holder.itemView.findViewById(R.id.messageSenderAvatar));
                    } else if (mMessage.senderAvatar.isThumbnailExistsOnLocal()) {
                        ImageLoader.getInstance()
                                .displayImage(AndroidUtils.suitablePath(mMessage.senderAvatar.getLocalThumbnailPath()), (ImageView) holder.itemView.findViewById(R.id.messageSenderAvatar));
                    } else {
                        ((ImageView) holder.itemView.findViewById(R.id.messageSenderAvatar)).setImageBitmap(
                                com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp60), mMessage.initials,
                                        mMessage.senderColor));

                        if (mMessage.senderAvatar.token != null && !mMessage.senderAvatar.token.isEmpty()) {
                            requestForAvatar();
                        } else {
                            requestForUserInfo();
                        }
                    }
                } else {
                    ((ImageView) holder.itemView.findViewById(R.id.messageSenderAvatar)).setImageBitmap(
                            com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp60), mMessage.initials,
                                    mMessage.senderColor));

                    requestForUserInfo();
                }
            } else {
                holder.itemView.findViewById(R.id.messageSenderAvatar).setVisibility(View.GONE);
            }
        } else {
            if (!mMessage.isTimeMessage()) {
                holder.itemView.findViewById(R.id.messageSenderAvatar).setVisibility(View.GONE);
            }
        }

        // set message time
        if (holder.itemView.findViewById(R.id.cslr_txt_time) != null) {
            ((TextView) holder.itemView.findViewById(R.id.cslr_txt_time)).setText(formatTime());
        }

        replayMessageIfNeeded(holder);
        forwardMessageIfNeeded(holder);

        Realm realm = Realm.getDefaultInstance();
        RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, Long.parseLong(mMessage.messageID)).findFirst();
        if (roomMessage != null) {
            prepareAttachmentIfNeeded(holder, roomMessage.getForwardMessage() != null ? roomMessage.getForwardMessage().getAttachment() : roomMessage.getAttachment());
        }
        realm.close();
    }

    @CallSuper
    protected void updateLayoutForReceive(VH holder) {
        LinearLayout frameLayout = (LinearLayout) holder.itemView.findViewById(R.id.mainContainer);
        ((FrameLayout.LayoutParams) frameLayout.getLayoutParams()).gravity = Gravity.START;

        holder.itemView.findViewById(R.id.contentContainer).setBackgroundResource(R.drawable.rectangle_round_gray);
        // add main layout margin to prevent getting match parent completely
        // set to mainContainer not itemView because of selecting item foreground
        ((FrameLayout.LayoutParams) holder.itemView.findViewById(R.id.mainContainer).getLayoutParams()).leftMargin = (int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp8);
        ((FrameLayout.LayoutParams) holder.itemView.findViewById(R.id.mainContainer).getLayoutParams()).rightMargin = (int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp28);

        // gone message status
        holder.itemView.findViewById(R.id.cslr_txt_tic).setVisibility(View.GONE);
    }

    @CallSuper
    protected void updateLayoutForSend(VH holder) {
        LinearLayout frameLayout = (LinearLayout) holder.itemView.findViewById(R.id.mainContainer);
        ((FrameLayout.LayoutParams) frameLayout.getLayoutParams()).gravity = Gravity.END;

        holder.itemView.findViewById(R.id.contentContainer).setBackgroundResource(R.drawable.rectangle_round_white);
        // add main layout margin to prevent getting match parent completely
        // set to mainContainer not itemView because of selecting item foreground
        ((FrameLayout.LayoutParams) holder.itemView.findViewById(R.id.mainContainer).getLayoutParams()).leftMargin = (int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp28);
        ((FrameLayout.LayoutParams) holder.itemView.findViewById(R.id.mainContainer).getLayoutParams()).rightMargin = (int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp8);

        // visible message status
        holder.itemView.findViewById(R.id.cslr_txt_tic).setVisibility(View.VISIBLE);
    }

    /**
     * format long time as string
     *
     * @return String
     */
    protected String formatTime() {
        return TimeUtils.toLocal(mMessage.time, G.CHAT_MESSAGE_TIME);
    }

    @CallSuper
    protected void replayMessageIfNeeded(VH holder) {
        // set replay container visible if message was replayed, otherwise, gone it
        LinearLayout replayContainer = (LinearLayout) holder.itemView.findViewById(R.id.replayLayout);
        if (replayContainer != null) {
            if (mMessage.replayTo != null) {
                holder.itemView.findViewById(R.id.chslr_imv_replay_pic).setVisibility(View.VISIBLE);

                switch (ProtoGlobal.RoomMessageType.valueOf(mMessage.replayTo.getMessageType())) {
                    case VOICE:
                        ((ImageView) holder.itemView.findViewById(R.id.chslr_imv_replay_pic)).setImageResource(R.drawable.microphone);
                        break;
                    case FILE:
                    case FILE_TEXT:
                        if (mMessage.replayTo.getAttachment().getName().toLowerCase().endsWith(".pdf")) {
                            ((ImageView) holder.itemView.findViewById(R.id.chslr_imv_replay_pic)).setImageResource(R.drawable.pdf);
                        } else if (mMessage.replayTo.getAttachment().getName().toLowerCase().endsWith(".docx")) {
                            ((ImageView) holder.itemView.findViewById(R.id.chslr_imv_replay_pic)).setImageResource(R.drawable.docx);
                        } else if (mMessage.replayTo.getAttachment().getName().toLowerCase().endsWith(".rar")) {
                            ((ImageView) holder.itemView.findViewById(R.id.chslr_imv_replay_pic)).setImageResource(R.drawable.rar);
                        } else if (mMessage.replayTo.getAttachment().getName().toLowerCase().endsWith(".txt")) {
                            ((ImageView) holder.itemView.findViewById(R.id.chslr_imv_replay_pic)).setImageResource(R.drawable.txt);
                        } else {
                            ((ImageView) holder.itemView.findViewById(R.id.chslr_imv_replay_pic)).setImageResource(R.drawable.file);
                        }
                        break;
                    default:
                        if (mMessage.replayTo.getAttachment() != null) {
                            if (mMessage.replayTo.getAttachment().isFileExistsOnLocal()) {
                                ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(mMessage.replayTo.getAttachment().getLocalFilePath()), (ImageView) holder.itemView.findViewById(R.id.chslr_imv_replay_pic));
                            } else if (mMessage.replayTo.getAttachment().isThumbnailExistsOnLocal()) {
                                ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(mMessage.replayTo.getAttachment().getLocalThumbnailPath()), (ImageView) holder.itemView.findViewById(R.id.chslr_imv_replay_pic));
                            } else {
                                holder.itemView.findViewById(R.id.chslr_imv_replay_pic).setVisibility(View.GONE);
                                // TODO: 11/15/2016 [Alireza] request download bede
                            }
                        } else {
                            holder.itemView.findViewById(R.id.chslr_imv_replay_pic).setVisibility(View.GONE);
                        }
                        break;
                }

                Realm realm = Realm.getDefaultInstance();
                RealmRegisteredInfo replayToInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, mMessage.replayTo.getUserId()).findFirst();
                if (replayToInfo != null) {
                    ((TextView) holder.itemView.findViewById(R.id.chslr_txt_replay_from)).setText(replayToInfo.getDisplayName());
                }
                ((TextView) holder.itemView.findViewById(R.id.chslr_txt_replay_message)).setText(mMessage.replayTo.getMessage());

                if (mMessage.isSenderMe()) {
                    holder.itemView.findViewById(R.id.verticalLine).setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.iGapColor));
                } else {
                    holder.itemView.findViewById(R.id.verticalLine).setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.newBlack));
                }

                replayContainer.setVisibility(View.VISIBLE);
                realm.close();
            } else {
                replayContainer.setVisibility(View.GONE);
            }
        }
    }

    @CallSuper
    protected void forwardMessageIfNeeded(VH holder) {
        // set forward container visible if message was forwarded, otherwise, gone it
        LinearLayout forwardContainer = (LinearLayout) holder.itemView.findViewById(R.id.cslr_ll_forward);
        if (forwardContainer != null) {
            if (mMessage.forwardMessageFrom != null) {
                forwardContainer.setVisibility(View.VISIBLE);
                ((TextView) forwardContainer.findViewById(R.id.cslr_txt_forward_from)).setText(mMessage.forwardMessageFrom);
            } else {
                forwardContainer.setVisibility(View.GONE);
            }
        }
    }

    /**
     * does item have progress view
     *
     * @param itemView View
     * @return true if item has a progress
     */
    private boolean hasProgress(View itemView) {
        return itemView.findViewById(R.id.progress) != null;
    }

    private void prepareAttachmentIfNeeded(final VH holder, final RealmAttachment attachment) {
        // runs if message has attachment
        if (attachment != null) {
            // if file already exists, simply show the local one
            if (attachment.isFileExistsOnLocal()) {
                // load file from local
                onLoadFromLocal(holder, attachment.getLocalFilePath(), LocalFileType.FILE);
            } else {
                // file doesn't exist on local, I check for a thumbnail
                // if thumbnail exists, I load it into the view
                if (attachment.isThumbnailExistsOnLocal()) {
                    if ((mMessage.forwardedFrom != null && (mMessage.forwardedFrom.getMessageType().equalsIgnoreCase(ProtoGlobal.RoomMessageType.IMAGE.toString()) || mMessage.forwardedFrom.getMessageType().equalsIgnoreCase(ProtoGlobal.RoomMessageType.IMAGE_TEXT.toString()))) || mMessage.messageType == ProtoGlobal.RoomMessageType.IMAGE || mMessage.messageType == ProtoGlobal.RoomMessageType.IMAGE_TEXT) {
                        ViewGroup view = (ViewGroup) holder.itemView.findViewById(R.id.thumbnail).getParent();
                        if (view != null) {
                            int[] dimens = AndroidUtils.scaleDimenWithSavedRatio(holder.itemView.getContext(), attachment.getWidth(), attachment.getHeight());
                            view.setLayoutParams(new LinearLayout.LayoutParams(dimens[0], dimens[1]));
                            view.requestLayout();
                        }
                    }

                    // load thumbnail from local
                    onLoadFromLocal(holder, attachment.getLocalThumbnailPath(), LocalFileType.THUMBNAIL);
                } else {
                    requestForThumbnail();
                }
            }

            if (hasProgress(holder.itemView)) {
                ((MessageProgress) holder.itemView.findViewById(R.id.progress)).withOnMessageProgress(new OnMessageProgressClick() {
                    @Override
                    public void onMessageProgressClick(MessageProgress progress) {
                        if (progress.getProcessType() == ProgressProcess.PROCESSING) {
                            if (MessagesAdapter.hasUploadRequested(Long.parseLong(mMessage.messageID))) {
                                messageClickListener.onUploadCancel(progress, mMessage, holder.getAdapterPosition());
                            } else {
                                ((MessageProgress) holder.itemView.findViewById(R.id.progress)).withDrawable(R.drawable.ic_download);
                                progress.withHideProgress();

                                messageClickListener.onDownloadCancel(progress, mMessage, holder.getAdapterPosition());
                            }
                        } else {
                            if (attachment.isFileExistsOnLocal()) {
                                if (mMessage.status.equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.SENDING.toString())) {
                                    return;
                                }
                                if (mMessage.status.equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.FAILED.toString())) {
                                    messageClickListener.onFailedMessageClick(progress, mMessage, holder.getAdapterPosition());
                                } else {
                                    messageClickListener.onOpenClick(progress, mMessage, holder.getAdapterPosition());
                                }
                            } else {
                                ((MessageProgress) holder.itemView.findViewById(R.id.progress)).withDrawable(R.drawable.ic_cancel);
                                // create new download attachment once with attachment token
                                if (mMessage.downloadAttachment == null) {
                                    mMessage.downloadAttachment = new StructDownloadAttachment(attachment.getToken());
                                }

                                // make sure to not request multiple times by checking last offset with the new one
                                if (mMessage.downloadAttachment.lastOffset < mMessage.downloadAttachment.offset) {
                                    onRequestDownloadFile(mMessage.downloadAttachment.offset, mMessage.downloadAttachment.progress);
                                    mMessage.downloadAttachment.lastOffset = mMessage.downloadAttachment.offset;
                                }

                                messageClickListener.onDownloadStart(progress, mMessage, holder.getAdapterPosition());
                            }
                        }
                    }
                });

                ((MessageProgress) holder.itemView.findViewById(R.id.progress)).withOnProgress(new OnProgress() {
                    @Override
                    public void onProgressFinished() {
                        // TODO: 10/15/2016 [Alireza] onClick babate har kodom age niaz bood, masalan vase play, bayad video ro play kone
                        switch (mMessage.forwardedFrom != null ? ProtoGlobal.RoomMessageType.valueOf(mMessage.forwardedFrom.getMessageType()) : mMessage.messageType) {
                            case IMAGE:
                            case IMAGE_TEXT:
                                holder.itemView.findViewById(R.id.progress).setVisibility(View.INVISIBLE);
                                break;
                            case VIDEO:
                            case VIDEO_TEXT:
                                ((MessageProgress) holder.itemView.findViewById(R.id.progress)).withDrawable(R.drawable.ic_play);
                                break;
                            case AUDIO:
                            case AUDIO_TEXT:
                                ((MessageProgress) holder.itemView.findViewById(R.id.progress)).withDrawable(R.drawable.ic_play);
                                break;
                            case FILE:
                            case FILE_TEXT:
                                ((MessageProgress) holder.itemView.findViewById(R.id.progress)).withDrawable(R.drawable.ic_open);
                                break;
                            case VOICE:
                                ((MessageProgress) holder.itemView.findViewById(R.id.progress)).withDrawable(R.drawable.ic_play);
                                break;
                        }
                    }
                });
            }

            prepareProgress(holder);
        }
    }

    @Override
    @CallSuper
    public void onLoadFromLocal(VH holder, String localPath, LocalFileType fileType) {

    }

    @Override
    @CallSuper
    public void onRequestDownloadFile(long offset, int progress) {
        if (mMessage.forwardedFrom != null) {
            final String fileName = mMessage.forwardedFrom.getAttachment().getToken() + "_" + mMessage.forwardedFrom.getAttachment().getName();
            if (progress == 100) {
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, mMessage.forwardedFrom.getMessageId()).findFirst().getAttachment().setLocalFilePath(AndroidUtils.suitableAppFilePath(ProtoGlobal.RoomMessageType.valueOf(mMessage.forwardedFrom.getMessageType())) + "/" + fileName);
                    }
                });
                realm.close();
                try {
                    AndroidUtils.cutFromTemp(ProtoGlobal.RoomMessageType.valueOf(mMessage.forwardedFrom.getMessageType()), fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return; // necessary
            }

            if (!MessagesAdapter.hasDownloadRequested(mMessage.forwardedFrom.getAttachment().getToken())) {
                MessagesAdapter.requestDownload(mMessage.forwardedFrom.getAttachment().getToken(), progress, offset);
            }
        } else {
            String fileName = mMessage.attachment.token + "_" + mMessage.attachment.name;
            if (progress == 100) {
                mMessage.attachment.setLocalFilePath(Long.parseLong(mMessage.messageID), AndroidUtils.suitableAppFilePath(mMessage.forwardedFrom != null ? ProtoGlobal.RoomMessageType.valueOf(mMessage.forwardedFrom.getMessageType()) : mMessage.messageType) + "/" + fileName);

                try {
                    AndroidUtils.cutFromTemp(mMessage.forwardedFrom != null ? ProtoGlobal.RoomMessageType.valueOf(mMessage.forwardedFrom.getMessageType()) : mMessage.messageType, fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return; // necessary
            }

            if (!MessagesAdapter.hasDownloadRequested(mMessage.attachment.token)) {
                MessagesAdapter.requestDownload(mMessage.attachment.token, progress, offset);
            }
        }

    }

    @Override
    public void onRequestDownloadThumbnail(String token, boolean done) {
        if (mMessage.forwardedFrom != null) {
            if (mMessage.forwardedFrom.getAttachment().getSmallThumbnail().getSize() != 0) {

                final String fileName = "thumb_" + token + "_" + mMessage.forwardedFrom.getAttachment().getName();
                if (done) {
                    mMessage.attachment.setLocalThumbnailPath(Long.parseLong(mMessage.messageID), G.DIR_TEMP + "/" + fileName);

                    return; // necessary
                }

                ProtoFileDownload.FileDownload.Selector selector = ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL;
                String identity = mMessage.attachment.token + '*' + selector.toString() + '*' + mMessage.forwardedFrom.getAttachment().getSmallThumbnail().getSize() + '*' + fileName + '*' + 0;

                new RequestFileDownload().download(token, 0, (int) mMessage.forwardedFrom.getAttachment().getSmallThumbnail().getSize(), selector, identity);
            }
        } else {
            if (mMessage.attachment.smallThumbnail.size != 0) {

                final String fileName = "thumb_" + token + "_" + mMessage.attachment.name;
                if (done) {
                    mMessage.attachment.setLocalThumbnailPath(Long.parseLong(mMessage.messageID), G.DIR_TEMP + "/" + fileName);

                    return; // necessary
                }

                ProtoFileDownload.FileDownload.Selector selector = ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL;
                String identity = mMessage.attachment.token + '*' + selector.toString() + '*' + mMessage.attachment.smallThumbnail.size + '*' + fileName + '*' + 0;

                new RequestFileDownload().download(token, 0, (int) mMessage.attachment.smallThumbnail.size, selector, identity);
            }
        }

    }

    private void requestForThumbnail() {
        // create new download attachment once with attachment token
        if (mMessage.downloadAttachment == null) {
            mMessage.downloadAttachment = new StructDownloadAttachment(mMessage.forwardedFrom != null ? mMessage.forwardedFrom.getAttachment().getToken() : mMessage.attachment.token);
        }

        // request thumbnail
        if (!mMessage.downloadAttachment.thumbnailRequested) {
            onRequestDownloadThumbnail(mMessage.forwardedFrom != null ? mMessage.forwardedFrom.getAttachment().getToken() : mMessage.attachment.token, false);
            // prevent from multiple requesting thumbnail
            mMessage.downloadAttachment.thumbnailRequested = true;
        }
    }

    /**
     * automatically update progress if layout has one
     *
     * @param holder VH
     */
    private void prepareProgress(VH holder) {
        if (!hasProgress(holder.itemView)) {
            return;
        }

        if (mMessage.sendType == MyType.SendType.send) {
            // update progress when user trying to upload or download
            if (MessagesAdapter.uploading.containsKey(Long.parseLong(mMessage.messageID))) {
                ((MessageProgress) holder.itemView.findViewById(R.id.progress)).withDrawable(R.drawable.ic_cancel);
                holder.itemView.findViewById(R.id.progress).setVisibility(View.VISIBLE);
                ((MessageProgress) holder.itemView.findViewById(R.id.progress)).withProgress(MessagesAdapter.uploading.get(Long.parseLong(mMessage.messageID)));
                if (MessagesAdapter.uploading.get(Long.parseLong(mMessage.messageID)) == 100) {
                    ((MessageProgress) holder.itemView.findViewById(R.id.progress)).performProgress();
                }
            } else {
                checkForDownloading(holder);
            }
        } else {
            checkForDownloading(holder);
        }
    }

    private void checkForDownloading(VH holder) {
        if (mMessage.downloadAttachment != null) {
            if (MessagesAdapter.downloading.containsKey(mMessage.downloadAttachment.token)) {
                ((MessageProgress) holder.itemView.findViewById(R.id.progress)).withDrawable(R.drawable.ic_cancel);
                holder.itemView.findViewById(R.id.progress).setVisibility(View.VISIBLE);
                ((MessageProgress) holder.itemView.findViewById(R.id.progress)).withProgress(MessagesAdapter.downloading.get(mMessage.downloadAttachment.token));

                if (MessagesAdapter.downloading.get(mMessage.downloadAttachment.token) == 100) {
                    MessagesAdapter.downloading.remove(mMessage.downloadAttachment.token);
                    ((MessageProgress) holder.itemView.findViewById(R.id.progress)).performProgress();
                }
            } else {
                if (mMessage.forwardedFrom != null ? mMessage.forwardedFrom.getAttachment().isFileExistsOnLocal() : mMessage.attachment.isFileExistsOnLocal()) {
                    ((MessageProgress) holder.itemView.findViewById(R.id.progress)).performProgress();
                } else {
                    ((MessageProgress) holder.itemView.findViewById(R.id.progress)).withDrawable(R.drawable.ic_download);
                    holder.itemView.findViewById(R.id.progress).setVisibility(View.VISIBLE);
                }
            }
        } else {
            if (mMessage.forwardedFrom != null ? mMessage.forwardedFrom.getAttachment().isFileExistsOnLocal() : mMessage.attachment.isFileExistsOnLocal()) {
                ((MessageProgress) holder.itemView.findViewById(R.id.progress)).performProgress();
            } else {
                ((MessageProgress) holder.itemView.findViewById(R.id.progress)).withDrawable(R.drawable.ic_download);
            }
        }
    }
}
