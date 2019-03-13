/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.adapter.items.chat;

import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.MessagesAdapter;
import net.iGap.interfaces.IMessageItem;
import net.iGap.messageprogress.MessageProgress;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.enums.LocalFileType;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmRoomMessageFields;

import java.util.List;

import io.realm.Realm;

import static android.graphics.Typeface.BOLD;
import static android.view.Gravity.CENTER;
import static android.widget.LinearLayout.HORIZONTAL;
import static android.widget.LinearLayout.VERTICAL;
import static java.lang.Boolean.TRUE;
import static net.iGap.fragments.FragmentChat.getRealmChat;

public class FileItem extends AbstractMessage<FileItem, FileItem.ViewHolder> {

    public FileItem(MessagesAdapter<AbstractMessage> mAdapter, ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(mAdapter, true, type, messageClickListener);
    }

    @Override
    public int getType() {
        return R.id.chatSubLayoutFile;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_message;
    }

    @Override
    public void onLoadThumbnailFromLocal(final ViewHolder holder, final String tag, final String localPath, LocalFileType fileType) {
        super.onLoadThumbnailFromLocal(holder, tag, localPath, fileType);
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        if (mMessage.forwardedFrom != null) {
            if (mMessage.forwardedFrom.getAttachment() != null) {
                holder.cslf_txt_file_name.setText(mMessage.forwardedFrom.getAttachment().getName());
                holder.cslf_txt_file_size.setText(AndroidUtils.humanReadableByteCount(mMessage.forwardedFrom.getAttachment().getSize(), true));
            }
        } else {
            if (mMessage.attachment != null) {
                holder.cslf_txt_file_name.setText(mMessage.attachment.name);
                holder.cslf_txt_file_size.setText(AndroidUtils.humanReadableByteCount(mMessage.attachment.size, true));
            }
        }

        setTextIfNeeded(holder.messageView);

        RealmRoomMessage roomMessage = RealmRoomMessage.getFinalMessage(getRealmChat().where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, Long.valueOf(mMessage.messageID)).findFirst());
        if (roomMessage != null) {
            holder.thumbnail.setVisibility(View.VISIBLE);
            if (roomMessage.getAttachment().getName().toLowerCase().endsWith(".pdf")) {
                holder.thumbnail.setImageDrawable(net.iGap.messageprogress.AndroidUtils.getDrawable(G.currentActivity, R.drawable.pdf_icon));
            } else if (roomMessage.getAttachment().getName().toLowerCase().endsWith(".txt")) {
                holder.thumbnail.setImageDrawable(net.iGap.messageprogress.AndroidUtils.getDrawable(G.currentActivity, R.drawable.txt_icon));
            } else if (roomMessage.getAttachment().getName().toLowerCase().endsWith(".exe")) {
                holder.thumbnail.setImageDrawable(net.iGap.messageprogress.AndroidUtils.getDrawable(G.currentActivity, R.drawable.exe_icon));
            } else if (roomMessage.getAttachment().getName().toLowerCase().endsWith(".doc") || roomMessage.getAttachment().getName().toLowerCase().endsWith(".docs") || roomMessage.getAttachment().getName().toLowerCase().endsWith(".docx")) {
                holder.thumbnail.setImageDrawable(net.iGap.messageprogress.AndroidUtils.getDrawable(G.currentActivity, R.drawable.docx_icon));
            } else {
                holder.thumbnail.setImageDrawable(net.iGap.messageprogress.AndroidUtils.getDrawable(G.currentActivity, R.drawable.file_icon));
            }
        }
    }

    @Override
    protected void updateLayoutForSend(ViewHolder holder) {
        super.updateLayoutForSend(holder);
    }

    @Override
    protected void updateLayoutForReceive(ViewHolder holder) {
        super.updateLayoutForReceive(holder);
//        if (G.isDarkTheme) {
//            holder.cslf_txt_file_name.setTextColor(Color.parseColor(G.textTitleTheme));
//            holder.cslf_txt_file_size.setTextColor(Color.parseColor(G.textSubTheme));
//        } else {
//            holder.cslf_txt_file_name.setTextColor(holder.itemView.getResources().getColor(R.color.colorOldBlack));
//            holder.cslf_txt_file_size.setTextColor(holder.itemView.getResources().getColor(R.color.colorOldBlack));
//        }
//
        holder.cslf_txt_file_name.setTextColor(Color.parseColor(G.textBubble));
        holder.cslf_txt_file_size.setTextColor(Color.parseColor(G.textBubble));

    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends ChatItemWithTextHolder implements IThumbNailItem, IProgress {
        protected TextView cslf_txt_file_name;
        protected TextView cslf_txt_file_size;
        protected ImageView thumbnail;
        protected MessageProgress progress;

        public ViewHolder(View view) {
            super(view);

            LinearLayout linearLayout_784 = new LinearLayout(G.context);
            linearLayout_784.setGravity(Gravity.CENTER_VERTICAL);
            setLayoutDirection(linearLayout_784, View.LAYOUT_DIRECTION_LTR);
            linearLayout_784.setOrientation(HORIZONTAL);
            linearLayout_784.setPadding(0, 0, (int) G.context.getResources().getDimension(R.dimen.messageContainerPadding), 0);
            LinearLayout.LayoutParams layout_419 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layout_419.gravity = CENTER;
            linearLayout_784.setLayoutParams(layout_419);

            FrameLayout frameLayout = new FrameLayout(G.context);
            FrameLayout.LayoutParams layoutParamsFrameLayout = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            layoutParamsFrameLayout.gravity = CENTER;
            frameLayout.setPadding(10, 10, 10, 10);
            frameLayout.setLayoutParams(layoutParamsFrameLayout);

            thumbnail = new ImageView(G.context);
            thumbnail.setId(R.id.thumbnail);
            LinearLayout.LayoutParams thumbnailParams = new LinearLayout.LayoutParams((int) G.context.getResources().getDimension(R.dimen.dp48), (int) G.context.getResources().getDimension(R.dimen.dp48));
            thumbnailParams.gravity = CENTER;
            thumbnail.setBackgroundColor(Color.TRANSPARENT);
            thumbnail.setScaleType(ImageView.ScaleType.FIT_CENTER);
            AppUtils.setImageDrawable(thumbnail, R.drawable.file_icon);
            thumbnail.setLayoutParams(thumbnailParams);

            LinearLayout linearLayout_780 = new LinearLayout(G.context);
            linearLayout_780.setOrientation(VERTICAL);
            LinearLayout.LayoutParams layout_752 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layout_752.gravity = CENTER;
            linearLayout_780.setLayoutParams(layout_752);

            cslf_txt_file_name = new TextView(G.context);
            cslf_txt_file_name.setId(R.id.songArtist);
            cslf_txt_file_name.setEllipsize(TextUtils.TruncateAt.MIDDLE);
            cslf_txt_file_name.setSingleLine(true);

            cslf_txt_file_name.setMaxWidth((int) G.context.getResources().getDimension(R.dimen.dp180));
            cslf_txt_file_name.setText("file_name.ext");
            cslf_txt_file_name.setTextColor(Color.parseColor(G.textBubble));
            setTextSize(cslf_txt_file_name, R.dimen.dp14);
            cslf_txt_file_name.setTypeface(G.typeface_IRANSansMobile_Bold, BOLD);
            LinearLayout.LayoutParams layout_1000 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            cslf_txt_file_name.setLayoutParams(layout_1000);
            linearLayout_780.addView(cslf_txt_file_name);

            cslf_txt_file_size = new TextView(G.context);
            cslf_txt_file_size.setId(R.id.fileSize);
            cslf_txt_file_size.setSingleLine(true);
            cslf_txt_file_size.setText("3.2 mb");
            cslf_txt_file_size.setAllCaps(TRUE);
            cslf_txt_file_size.setTextColor(Color.parseColor(G.textBubble));
            setTextSize(cslf_txt_file_size, R.dimen.dp10);
            setTypeFace(cslf_txt_file_size);
            LinearLayout.LayoutParams layout_958 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layout_958.topMargin = 3;
            cslf_txt_file_size.setLayoutParams(layout_958);
            linearLayout_780.addView(cslf_txt_file_size);
            linearLayout_784.addView(frameLayout);
            linearLayout_784.addView(linearLayout_780);
            m_container.addView(linearLayout_784);

            setLayoutMessageContainer();

            progress = getProgressBar(R.dimen.dp52);
            frameLayout.addView(thumbnail);
            frameLayout.addView(progress);
        }

        @Override
        public ImageView getThumbNailImageView() {
            return thumbnail;
        }

        @Override
        public MessageProgress getProgress() {
            return progress;
        }
    }
}
