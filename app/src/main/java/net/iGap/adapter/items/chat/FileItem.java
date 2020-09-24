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
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.MessagesAdapter;
import net.iGap.helper.LayoutCreator;
import net.iGap.messageprogress.MessageProgress;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.enums.LocalFileType;
import net.iGap.observers.interfaces.IMessageItem;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoomMessage;

import java.util.List;

import static android.graphics.Typeface.BOLD;
import static android.view.Gravity.CENTER;
import static android.widget.LinearLayout.HORIZONTAL;
import static android.widget.LinearLayout.VERTICAL;
import static java.lang.Boolean.TRUE;

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

        if (mMessage.getForwardMessage() != null) {
            if (mMessage.getForwardMessage().getAttachment() != null) {
                holder.cslf_txt_file_name.setText(mMessage.getForwardMessage().getAttachment().getName());
                holder.cslf_txt_file_size.setText(AndroidUtils.humanReadableByteCount(mMessage.getForwardMessage().getAttachment().getSize(), true));
            }
        } else {
            if (structMessage.getAttachment() != null) {
                holder.cslf_txt_file_name.setText(structMessage.getAttachment().getName());
                holder.cslf_txt_file_size.setText(AndroidUtils.humanReadableByteCount(structMessage.getAttachment().getSize(), true));
            }
        }

        setTextIfNeeded(holder.messageView);
        RealmRoomMessage roomMessage = DbManager.getInstance().doRealmTask(realm -> {
            return RealmRoomMessage.getFinalMessage(realm.where(RealmRoomMessage.class).equalTo("messageId", mMessage.getMessageId()).findFirst());
        });

        if (roomMessage != null) {
            holder.thumbnail.setVisibility(View.VISIBLE);
            if (roomMessage.getAttachment().getName().toLowerCase().endsWith(".pdf")) {
                holder.thumbnail.setImageDrawable(net.iGap.messageprogress.AndroidUtils.getDrawable(G.currentActivity, R.drawable.pdf_icon));
                holder.fileType.setText("PDF");
            } else if (roomMessage.getAttachment().getName().toLowerCase().endsWith(".txt")) {
                holder.thumbnail.setImageDrawable(net.iGap.messageprogress.AndroidUtils.getDrawable(G.currentActivity, R.drawable.txt_icon));
                holder.fileType.setText("TXT");
            } else if (roomMessage.getAttachment().getName().toLowerCase().endsWith(".exe")) {
                holder.thumbnail.setImageDrawable(net.iGap.messageprogress.AndroidUtils.getDrawable(G.currentActivity, R.drawable.exe_icon));
                holder.fileType.setText("EXE");
            } else if (roomMessage.getAttachment().getName().toLowerCase().endsWith(".doc") || roomMessage.getAttachment().getName().toLowerCase().endsWith(".docs") || roomMessage.getAttachment().getName().toLowerCase().endsWith(".docx")) {
                holder.thumbnail.setImageDrawable(net.iGap.messageprogress.AndroidUtils.getDrawable(G.currentActivity, R.drawable.docx_icon));
                holder.fileType.setText("DOC");
            } else {
                holder.thumbnail.setImageDrawable(net.iGap.messageprogress.AndroidUtils.getDrawable(G.currentActivity, R.drawable.file_icon));
                holder.fileType.setText("FILE");
            }
        }
    }

    @Override
    protected void updateLayoutForSend(ViewHolder holder) {
        super.updateLayoutForSend(holder);
        holder.cslf_txt_file_name.setTextColor(theme.getSendMessageOtherTextColor(holder.getContext()));
        holder.cslf_txt_file_size.setTextColor(theme.getSendMessageOtherTextColor(holder.getContext()));
        holder.spaceView.setBackgroundColor(theme.getSendMessageOtherTextColor(holder.getContext()));
        holder.fileType.setTextColor(theme.getSendMessageOtherTextColor(holder.getContext()));


    }

    @Override
    protected void updateLayoutForReceive(ViewHolder holder) {
        super.updateLayoutForReceive(holder);
        holder.cslf_txt_file_name.setTextColor(theme.getReceivedMessageOtherTextColor(holder.getContext()));
        holder.cslf_txt_file_size.setTextColor(theme.getReceivedMessageOtherTextColor(holder.getContext()));
        holder.spaceView.setBackgroundColor(theme.getReceivedMessageOtherTextColor(holder.getContext()));
        holder.fileType.setTextColor(theme.getReceivedMessageOtherTextColor(holder.getContext()));

    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends ChatItemWithTextHolder implements IThumbNailItem, IProgress {
        protected AppCompatTextView cslf_txt_file_name;
        protected AppCompatTextView cslf_txt_file_size;
        protected AppCompatImageView thumbnail;
        protected MessageProgress progress;
        private TextView fileType;
        private View spaceView;

        public ViewHolder(View view) {
            super(view);


            LinearLayout linearLayout_784 = new LinearLayout(view.getContext());
            linearLayout_784.setGravity(Gravity.CENTER_VERTICAL);
            setLayoutDirection(linearLayout_784, View.LAYOUT_DIRECTION_LTR);
            linearLayout_784.setOrientation(HORIZONTAL);
            linearLayout_784.setPadding(0, 0, (int) G.context.getResources().getDimension(R.dimen.messageContainerPadding), 0);
            LinearLayout.LayoutParams layout_419 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layout_419.gravity = CENTER;
            linearLayout_784.setLayoutParams(layout_419);

            FrameLayout frameLayout = new FrameLayout(view.getContext());
            FrameLayout.LayoutParams layoutParamsFrameLayout = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            layoutParamsFrameLayout.gravity = CENTER;
            frameLayout.setPadding(10, 10, 10, 10);
            frameLayout.setLayoutParams(layoutParamsFrameLayout);

            thumbnail = new AppCompatImageView(view.getContext());
            thumbnail.setId(R.id.thumbnail);
            LinearLayout.LayoutParams thumbnailParams = new LinearLayout.LayoutParams((int) G.context.getResources().getDimension(R.dimen.dp48), (int) G.context.getResources().getDimension(R.dimen.dp48));
            thumbnailParams.gravity = CENTER;
            thumbnail.setBackgroundColor(Color.TRANSPARENT);
            thumbnail.setScaleType(ImageView.ScaleType.FIT_CENTER);
            AppUtils.setImageDrawable(thumbnail, R.drawable.file_icon);
            thumbnail.setLayoutParams(thumbnailParams);

            LinearLayout linearLayout_780 = new LinearLayout(view.getContext());
            linearLayout_780.setOrientation(VERTICAL);
            LinearLayout.LayoutParams layout_752 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layout_752.gravity = CENTER;
            linearLayout_780.setLayoutParams(layout_752);

            cslf_txt_file_name = new AppCompatTextView(view.getContext());
            cslf_txt_file_name.setId(R.id.songArtist);
            cslf_txt_file_name.setEllipsize(TextUtils.TruncateAt.MIDDLE);
            cslf_txt_file_name.setSingleLine(true);

            cslf_txt_file_name.setMaxWidth((int) G.context.getResources().getDimension(R.dimen.dp180));
            cslf_txt_file_name.setText("file_name.ext");
            setTextSize(cslf_txt_file_name, R.dimen.standardTextSize);
            cslf_txt_file_name.setTypeface(ResourcesCompat.getFont(cslf_txt_file_name.getContext(), R.font.main_font_bold), BOLD);
            LinearLayout.LayoutParams layout_1000 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            cslf_txt_file_name.setLayoutParams(layout_1000);
            linearLayout_780.addView(cslf_txt_file_name);

            LinearLayout bottomView = new LinearLayout(getContext());
            bottomView.setGravity(Gravity.CENTER);
            bottomView.setOrientation(HORIZONTAL);

            cslf_txt_file_size = new AppCompatTextView(view.getContext());
            cslf_txt_file_size.setId(R.id.fileSize);
            cslf_txt_file_size.setSingleLine(true);
            cslf_txt_file_size.setText("3.2 mb");
            cslf_txt_file_size.setAllCaps(TRUE);
            setTextSize(cslf_txt_file_size, R.dimen.verySmallTextSize);
            setTypeFace(cslf_txt_file_size);
            bottomView.addView(cslf_txt_file_size);
//            LinearLayout.LayoutParams layout_958 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            layout_958.topMargin = 3;
//            cslf_txt_file_size.setLayoutParams(layout_958);

            spaceView = new View(getContext());
            fileType = new TextView(getContext());


            fileType.setSingleLine(true);
            fileType.setText("3.2 mb");
            fileType.setAllCaps(TRUE);
            setTextSize(fileType, R.dimen.verySmallTextSize);
            setTypeFace(fileType);


            bottomView.addView(spaceView, LayoutCreator.createFrame(1, LayoutCreator.MATCH_PARENT, CENTER,
                    dpToPx(1), 0, dpToPx(1), 0));

            linearLayout_780.addView(bottomView, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, CENTER,
                    0, dpToPx(2), 0, 0));

            bottomView.addView(fileType);
            linearLayout_784.addView(frameLayout);
            linearLayout_784.addView(linearLayout_780);
            getContentBloke().addView(linearLayout_784);

            setLayoutMessageContainer();

            progress = getProgressBar(view.getContext(), R.dimen.dp52);
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
