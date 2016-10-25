package com.iGap.adapter.items.chat;

import android.os.Environment;
import android.support.annotation.CallSuper;
import android.support.v7.widget.RecyclerView;
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
import com.iGap.interface_package.IChatItemAttachment;
import com.iGap.interface_package.IChatItemAvatar;
import com.iGap.interface_package.OnMessageViewClick;
import com.iGap.module.AndroidUtils;
import com.iGap.module.AppUtils;
import com.iGap.module.MyType;
import com.iGap.module.StructDownloadAttachment;
import com.iGap.module.StructMessageInfo;
import com.iGap.module.TimeUtils;
import com.iGap.module.enums.LocalFileType;
import com.iGap.proto.ProtoFileDownload;
import com.iGap.proto.ProtoGlobal;
import com.iGap.request.RequestFileDownload;
import com.iGap.request.RequestUserInfo;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.util.List;

import io.meness.github.messageprogress.MessageProgress;
import io.meness.github.messageprogress.OnMessageProgressClick;
import io.meness.github.messageprogress.OnProgress;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/6/2016.
 */
public abstract class AbstractMessage<Item extends AbstractMessage<?, ?>, VH extends RecyclerView.ViewHolder> extends AbstractItem<Item, VH> implements IChatItemAttachment<VH>, IChatItemAvatar {
    public OnMessageViewClick messageClickListener;
    public StructMessageInfo mMessage;
    public boolean directionalBased = true;
    public ProtoGlobal.Room.Type type;

    public AbstractMessage(boolean directionalBased, ProtoGlobal.Room.Type type, OnMessageViewClick messageClickListener) {
        this.directionalBased = directionalBased;
        this.type = type;
        this.messageClickListener = messageClickListener;
    }

    protected void setOnClick(final VH holder, View view, final ProtoGlobal.RoomMessageType type) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (messageClickListener != null) {
                    messageClickListener.onMessageFileClick(v, mMessage, holder.getAdapterPosition(), type);
                }
            }
        });
    }

    protected void setTextIfNeeded(TextView view) {
        if (mMessage.messageText != null && !mMessage.messageText.isEmpty()) {
            view.setText(mMessage.messageText);
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
    public void onRequestDownloadAvatar(int offset, int progress) {
        ProtoFileDownload.FileDownload.Selector selector = ProtoFileDownload.FileDownload.Selector.LARGE_THUMBNAIL;
        String fileName = mMessage.downloadAttachment.token + "_" + mMessage.senderAvatar.name;
        if (progress == 100) {
            mMessage.senderAvatar.setLocalThumbnailPath(Long.parseLong(mMessage.senderID), G.DIR_IMAGE_USER + "/" + fileName);

            try {
                AndroidUtils.cutFromTemp(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // I don't use offset in getting thumbnail
        String identity = mMessage.downloadAttachment.token + '*' + selector.toString() + '*' + mMessage.senderAvatar.largeThumbnail.size + '*' + fileName + '*' + mMessage.downloadAttachment.offset + "*" + Boolean.toString(true) + "*" + mMessage.senderID + "*" + type.toString();

        new RequestFileDownload().download(mMessage.downloadAttachment.token, offset, (int) mMessage.senderAvatar.largeThumbnail.size, selector, identity);
    }

    @Override
    public Item withIdentifier(long identifier) {
        return super.withIdentifier(identifier);
    }

    @Override
    @CallSuper
    public void bindView(final VH holder, List payloads) {
        super.bindView(holder, payloads);

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
                        ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(mMessage.senderAvatar.getLocalThumbnailPath()), (ImageView) holder.itemView.findViewById(R.id.messageSenderAvatar));
                    } else {
                        ((ImageView) holder.itemView.findViewById(R.id.messageSenderAvatar)).setImageBitmap(com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp60), mMessage.initials, mMessage.senderColor));

                        if (mMessage.senderAvatar.token != null && !mMessage.senderAvatar.token.isEmpty()) {
                            requestForAvatar();
                        } else {
                            requestForUserInfo();
                        }
                    }
                } else {
                    ((ImageView) holder.itemView.findViewById(R.id.messageSenderAvatar)).setImageBitmap(com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp60), mMessage.initials, mMessage.senderColor));

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

        prepareAttachmentIfNeeded(holder);
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
            if (!mMessage.replayFrom.isEmpty()) {
                if (!mMessage.replayPicturePath.isEmpty()) {
                    holder.itemView.findViewById(R.id.chslr_imv_replay_pic).setVisibility(View.VISIBLE);
                    ((ImageView) holder.itemView.findViewById(R.id.chslr_imv_replay_pic)).setImageResource(Integer.parseInt(mMessage.replayPicturePath));
                } else {
                    holder.itemView.findViewById(R.id.chslr_imv_replay_pic).setVisibility(View.GONE);
                }

                ((TextView) holder.itemView.findViewById(R.id.chslr_txt_replay_from)).setText(mMessage.replayFrom);
                ((TextView) holder.itemView.findViewById(R.id.chslr_txt_replay_message)).setText(mMessage.replayMessage);
                replayContainer.setVisibility(View.VISIBLE);
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
            if (!mMessage.forwardMessageFrom.isEmpty()) {
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

    private void prepareAttachmentIfNeeded(final VH holder) {
        // runs if message has attachment
        if (mMessage.hasAttachment()) {
            // if file already exists, simply show the local one
            if (mMessage.attachment.isFileExistsOnLocal()) {
                // load file from local
                onLoadFromLocal(holder, mMessage.attachment.getLocalFilePath(), LocalFileType.FILE);
            } else {
                // file doesn't exist on local, I check for a thumbnail
                // if thumbnail exists, I load it into the view
                if (mMessage.attachment.isThumbnailExistsOnLocal()) {
                    if (mMessage.messageType == ProtoGlobal.RoomMessageType.IMAGE || mMessage.messageType == ProtoGlobal.RoomMessageType.IMAGE_TEXT) {
                        ViewGroup view = (ViewGroup) holder.itemView.findViewById(R.id.thumbnail).getParent();
                        if (view != null) {
                            int[] dimens = AndroidUtils.scaleDimenWithSavedRatio(holder.itemView.getContext(), mMessage.attachment.width, mMessage.attachment.height);
                            view.setLayoutParams(new LinearLayout.LayoutParams(dimens[0], dimens[1]));
                            view.requestLayout();
                        }
                    }

                    // load thumbnail from local
                    onLoadFromLocal(holder, mMessage.attachment.getLocalThumbnailPath(), LocalFileType.THUMBNAIL);
                } else {
                    requestForThumbnail();
                }

                // TODO: 10/15/2016 [Alireza] vase halate auto download inja bayad taghir kone (vase ba'dan goftam)
                if (hasProgress(holder.itemView)) {
                    ((MessageProgress) holder.itemView.findViewById(R.id.progress)).withOnMessageProgress(new OnMessageProgressClick() {
                        @Override
                        public void onMessageProgressClick(MessageProgress progress) {
                            ((MessageProgress) holder.itemView.findViewById(R.id.progress)).withDrawable(R.drawable.ic_cancel);
                            ((MessageProgress) holder.itemView.findViewById(R.id.progress)).withIndeterminate(true);
                            // create new download attachment once with attachment token
                            if (mMessage.downloadAttachment == null) {
                                mMessage.downloadAttachment = new StructDownloadAttachment(mMessage.attachment.token);
                            }

                            // make sure to not request multiple times by checking last offset with the new one
                            if (mMessage.downloadAttachment.lastOffset < mMessage.downloadAttachment.offset) {
                                onRequestDownloadFile(mMessage.downloadAttachment.offset, mMessage.downloadAttachment.progress);
                                mMessage.downloadAttachment.lastOffset = mMessage.downloadAttachment.offset;
                            }
                        }
                    });
                }
            }

            if (hasProgress(holder.itemView)) {
                ((MessageProgress) holder.itemView.findViewById(R.id.progress)).withOnProgress(new OnProgress() {
                    @Override
                    public void onProgressFinished() {
                        // TODO: 10/15/2016 [Alireza] onClick babate har kodom age niaz bood, masalan vase play, bayad video ro play kone
                        switch (mMessage.messageType) {
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
    public void onRequestDownloadFile(int offset, int progress) {
        String fileName = mMessage.attachment.token + "_" + mMessage.attachment.name;
        if (progress == 100) {
            mMessage.attachment.setLocalFilePath(Long.parseLong(mMessage.messageID), AndroidUtils.suitableAppFilePath(mMessage.messageType) + "/" + fileName);

            try {
                AndroidUtils.cutFromTemp(mMessage.messageType, fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return; // necessary
        }
        ProtoFileDownload.FileDownload.Selector selector = ProtoFileDownload.FileDownload.Selector.FILE;
        String identity = mMessage.attachment.token + '*' + selector.toString() + '*' + mMessage.attachment.size + '*' + fileName + '*' + mMessage.downloadAttachment.offset;

        new RequestFileDownload().download(mMessage.downloadAttachment.token, offset, (int) mMessage.attachment.size, selector, identity);
    }

    @Override
    public void onRequestDownloadThumbnail() {
        if (mMessage.attachment.smallThumbnail.size != 0) {
            ProtoFileDownload.FileDownload.Selector selector = ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL;
            if (mMessage.attachment.getLocalThumbnailPath() == null || mMessage.attachment.getLocalThumbnailPath().isEmpty()) {
                mMessage.attachment.setLocalThumbnailPath(Long.parseLong(mMessage.messageID), Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + mMessage.downloadAttachment.token + System.nanoTime() + ".jpg");
            }

            // I don't use offset in getting thumbnail
            String identity = mMessage.downloadAttachment.token + '*' + selector.toString() + '*' + mMessage.attachment.smallThumbnail.size + '*' + mMessage.attachment.getLocalThumbnailPath() + '*' + mMessage.downloadAttachment.offset;

            new RequestFileDownload().download(mMessage.downloadAttachment.token, 0, (int) mMessage.attachment.smallThumbnail.size, selector, identity);
        }
    }

    private void requestForThumbnail() {
        // create new download attachment once with attachment token
        if (mMessage.downloadAttachment == null) {
            mMessage.downloadAttachment = new StructDownloadAttachment(mMessage.attachment.token);
        }

        // request thumbnail
        if (!mMessage.downloadAttachment.thumbnailRequested) {
            onRequestDownloadThumbnail();
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
            if (MessagesAdapter.downloading.containsKey(mMessage.attachment.token)) {
                ((MessageProgress) holder.itemView.findViewById(R.id.progress)).withDrawable(R.drawable.ic_cancel);
                holder.itemView.findViewById(R.id.progress).setVisibility(View.VISIBLE);
                ((MessageProgress) holder.itemView.findViewById(R.id.progress)).withProgress(MessagesAdapter.downloading.get(mMessage.attachment.token));

                if (MessagesAdapter.downloading.get(mMessage.attachment.token) == 100) {
                    MessagesAdapter.downloading.remove(mMessage.attachment.token);
                    ((MessageProgress) holder.itemView.findViewById(R.id.progress)).performProgress();
                }
            } else {
                if (mMessage.attachment.isFileExistsOnLocal()) {
                    ((MessageProgress) holder.itemView.findViewById(R.id.progress)).performProgress();
                } else {
                    ((MessageProgress) holder.itemView.findViewById(R.id.progress)).withDrawable(R.drawable.ic_download);
                    holder.itemView.findViewById(R.id.progress).setVisibility(View.VISIBLE);
                }
            }
        } else {
            if (mMessage.attachment.isFileExistsOnLocal()) {
                ((MessageProgress) holder.itemView.findViewById(R.id.progress)).performProgress();
            } else {
                ((MessageProgress) holder.itemView.findViewById(R.id.progress)).withDrawable(R.drawable.ic_download);
            }
        }
    }
}
