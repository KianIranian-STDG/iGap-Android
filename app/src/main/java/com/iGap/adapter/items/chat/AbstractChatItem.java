package com.iGap.adapter.items.chat;

import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.CallSuper;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.module.MyType;
import com.iGap.module.StructMessageInfo;
import com.iGap.module.TimeUtils;
import com.iGap.proto.ProtoGlobal;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.List;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/6/2016.
 */
public abstract class AbstractChatItem<Item extends AbstractChatItem<?, ?>, VH extends RecyclerView.ViewHolder> extends AbstractItem<Item, VH> {
    public StructMessageInfo mMessage;
    public boolean directionalBased = true;
    public ProtoGlobal.Room.Type type;

    public AbstractChatItem(boolean directionalBased, ProtoGlobal.Room.Type type) {
        this.directionalBased = directionalBased;
        this.type = type;
    }

    public AbstractChatItem setMessage(StructMessageInfo message) {
        this.mMessage = message;
        return this;
    }

    /**
     * return suitable path for using with UIL
     *
     * @param path String path
     * @return correct local path/passed path
     */
    protected String suitablePath(String path) {
        if (path.matches("\\w+?://")) {
            return path;
        } else {
            return Uri.fromFile(new File(path)).toString();
        }
    }

    /**
     * format long time as string
     *
     * @return String
     */
    protected String formatTime() {
        return TimeUtils.toLocal(mMessage.time, G.CHAT_MESSAGE_TIME);
    }

    @Override
    @CallSuper
    public void bindView(VH holder, List payloads) {
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
            updateMessageStatus((TextView) holder.itemView.findViewById(R.id.cslr_txt_tic),mMessage.status);
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
            if (!mMessage.senderAvatar.isEmpty()) {
                ImageLoader.getInstance().displayImage(suitablePath(mMessage.senderAvatar), (ImageView) holder.itemView.findViewById(R.id.messageSenderAvatar));
            } else {
                ((ImageView) holder.itemView.findViewById(R.id.messageSenderAvatar)).setImageBitmap(com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp60), mMessage.initials, mMessage.senderColor));
            }
            holder.itemView.findViewById(R.id.messageSenderAvatar).setVisibility(View.VISIBLE);
        } else {
            holder.itemView.findViewById(R.id.messageSenderAvatar).setVisibility(View.GONE);
        }

        // set message time
        ((TextView) holder.itemView.findViewById(R.id.cslr_txt_time)).setText(formatTime());

        setReplayMessage(holder);
        setForwardMessage(holder);
    }

    @CallSuper
    protected void setReplayMessage(VH holder) {
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
    protected void setForwardMessage(VH holder) {
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

    @Override
    public Item withIdentifier(long identifier) {
        return super.withIdentifier(identifier);
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
     * update message status automatically
     *
     * @param view TextView message status
     */
    public static void updateMessageStatus(TextView view, String status) {
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
                view.setTextColor(view.getContext().getResources().getColor(R.color.green));
                view.setText(G.context.getResources().getString(R.string.md_check_symbol));
                view.setTextSize(12F);
                break;
        }
    }
}
