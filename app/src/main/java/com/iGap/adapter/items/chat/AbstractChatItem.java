package com.iGap.adapter.items.chat;

import android.graphics.Color;
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

        // display user avatar only if chat type is GROUP
        if (type == ProtoGlobal.Room.Type.GROUP) {
            holder.itemView.findViewById(R.id.cslr_imv_sender_picture).setVisibility(View.VISIBLE);
        } else {
            holder.itemView.findViewById(R.id.cslr_imv_sender_picture).setVisibility(View.GONE);
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
        FrameLayout frameLayout = (FrameLayout) holder.itemView.findViewById(R.id.mainContainer).getParent();
        ((FrameLayout.LayoutParams) frameLayout.getLayoutParams()).gravity = Gravity.START;

        holder.itemView.findViewById(R.id.cslr_imv_sender_picture).setVisibility(View.VISIBLE);
        holder.itemView.findViewById(R.id.mainContainer).setBackgroundResource(R.drawable.rectangle_round_gray);
    }

    @CallSuper
    protected void updateLayoutForSend(VH holder) {
        FrameLayout frameLayout = (FrameLayout) holder.itemView.findViewById(R.id.mainContainer).getParent();
        ((FrameLayout.LayoutParams) frameLayout.getLayoutParams()).gravity = Gravity.END;

        holder.itemView.findViewById(R.id.mainContainer).setPadding(4, 4, 4, 4);
        holder.itemView.findViewById(R.id.cslr_imv_sender_picture).setVisibility(View.GONE);
        holder.itemView.findViewById(R.id.mainContainer).setBackgroundResource(R.drawable.rectangle_round_white);
    }

    protected void updateMessageStatus(TextView view) {
        switch (mMessage.status) {
            case "DELIVERED":
                view.setTextColor(view.getContext().getResources().getColor(R.color.green));
                view.setText(G.context.getResources().getString(R.string.md_check_symbol));
                view.setTextSize(13F);
                break;
            case "FAILED":
                view.setTextColor(Color.RED);
                view.setText(G.context.getResources().getString(R.string.md_cancel_button));
                view.setTextSize(16F);
                break;
            case "SEEN":
                view.setTextColor(view.getContext().getResources().getColor(R.color.green));
                view.setText(G.context.getResources().getString(R.string.md_double_tick_indicator));
                view.setTextSize(16F);
                break;
            case "SENDING":
                view.setTextColor(view.getContext().getResources().getColor(R.color.green));
                view.setText(G.context.getResources().getString(R.string.md_clock_with_white_face));
                view.setTextSize(16F);
                break;
            case "SENT":
                view.setTextColor(view.getContext().getResources().getColor(R.color.green));
                view.setText(G.context.getResources().getString(R.string.md_check_symbol));
                view.setTextSize(13F);
                break;
        }
    }
}
