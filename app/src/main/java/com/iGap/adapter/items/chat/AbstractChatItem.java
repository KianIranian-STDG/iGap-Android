package com.iGap.adapter.items.chat;

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
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/6/2016.
 */
public abstract class AbstractChatItem<Item extends AbstractChatItem<?, ?>, VH extends RecyclerView.ViewHolder> extends AbstractItem<Item, VH> {
    public StructMessageInfo mMessage;
    public boolean mDirectionalBased = true;

    public AbstractChatItem(boolean directionalBased) {
        this.mDirectionalBased = directionalBased;
    }

    public AbstractChatItem setMessage(StructMessageInfo message) {
        this.mMessage = message;
        return this;
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
        if (mDirectionalBased) {
            if (mMessage.sendType == MyType.SendType.recvive) {
                updateLayoutForReceive(holder);
            } else if (mMessage.sendType == MyType.SendType.send) {
                updateLayoutForSend(holder);
            }
        }

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
        ((FrameLayout.LayoutParams) holder.itemView.findViewById(R.id.mainContainer).getLayoutParams()).gravity = Gravity.START;
    }

    @CallSuper
    protected void updateLayoutForSend(VH holder) {
        ((FrameLayout.LayoutParams) holder.itemView.findViewById(R.id.mainContainer).getLayoutParams()).gravity = Gravity.END;
    }

    protected CharSequence defineMessageStatus() {
        CharSequence status = null;
        switch (mMessage.status) {
            case "DELIVERED":
                status = G.context.getResources().getString(R.string.fa_check);
                break;
            case "FAILED":
                status = G.context.getResources().getString(R.string.fa_exclamation_triangle);
                break;
            case "SEEN":
                status = G.context.getResources().getString(R.string.fa_check) + G.context.getResources().getString(R.string.fa_check);
                break;
            case "SENDING":
                status = G.context.getResources().getString(R.string.fa_clock_o);
                break;
            case "SENT":
                status = G.context.getResources().getString(R.string.fa_check);
                break;
            case "UNRECOGNIZED":
                status = null; // TODO: 9/8/2016 [Alireza Eskandarpour Shoferi] fill appreciate icon
                break;
        }

        return status;
    }
}
