package com.iGap.adapter.items.chat;

import android.support.annotation.CallSuper;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.widget.FrameLayout;

import com.iGap.G;
import com.iGap.R;
import com.iGap.module.MyType;
import com.iGap.module.StructMessageInfo;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/6/2016.
 */
public abstract class AbstractChatItem<Item extends AbstractChatItem<?, ?>, VH extends RecyclerView.ViewHolder> extends AbstractItem<Item, VH> {
    public StructMessageInfo mMessage;
    public boolean mDirectionalBased = true;
    private Calendar mCalendar = Calendar.getInstance();

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
        mCalendar.setTimeInMillis(mMessage.time);
        return new SimpleDateFormat(G.CHAT_MESSAGE_TIME, Locale.US).format(mCalendar.getTime());
    }

    @Override
    @CallSuper
    public void bindView(VH holder, List payloads) {
        super.bindView(holder, payloads);

        // only will be called when message layout is directional-base (e.g. single chat)
        if (mDirectionalBased) {
            if (mMessage.sendType == MyType.SendType.recvive) {
                updateLayoutForReceive(holder);
            } else if (mMessage.sendType == MyType.SendType.send) {
                updateLayoutForSend(holder);
            }
        }
    }

    @Override
    public Item withIdentifier(long identifier) {
        return super.withIdentifier(identifier);
    }

    @CallSuper
    protected void updateLayoutForReceive(VH holder) {
        ((FrameLayout.LayoutParams) holder.itemView.findViewById(R.id.cslr_ll_frame).getLayoutParams()).gravity = Gravity.START;
    }

    @CallSuper
    protected void updateLayoutForSend(VH holder) {
        ((FrameLayout.LayoutParams) holder.itemView.findViewById(R.id.cslr_ll_frame).getLayoutParams()).gravity = Gravity.END;
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
