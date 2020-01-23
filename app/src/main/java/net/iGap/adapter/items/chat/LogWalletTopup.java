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

import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import net.iGap.DbManager;
import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.MessagesAdapter;
import net.iGap.helper.HelperCalander;
import net.iGap.interfaces.IMessageItem;
import net.iGap.module.ReserveSpaceRoundedImageView;
import net.iGap.module.TimeUtils;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoomMessageWalletTopup;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LogWalletTopup extends AbstractMessage<LogWalletTopup, LogWalletTopup.ViewHolder> {

    public LogWalletTopup(MessagesAdapter<AbstractMessage> mAdapter, ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(mAdapter, true, type, messageClickListener);
    }

    @Override
    public int getType() {
        return R.id.ll_topUp;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_log_topup;
    }


    @Override
    public void bindView(final ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
        RealmRoomMessageWalletTopup realmRoomMessageWalletTopup = mMessage.getRoomMessageWallet().getRealmRoomMessageWalletTopup();

        holder.title.setText(R.string.topUp_title);

        DbManager.getInstance().doRealmTask(realm -> {
            String persianCalender = HelperCalander.checkHijriAndReturnTime(realmRoomMessageWalletTopup.getRequestTime()) + " " + "-" + " " +
                    TimeUtils.toLocal(realmRoomMessageWalletTopup.getRequestTime() * DateUtils.SECOND_IN_MILLIS, G.CHAT_MESSAGE_TIME);

            holder.orderId.setText(String.valueOf(realmRoomMessageWalletTopup.getOrderId()));
            holder.amount.setText(String.valueOf(realmRoomMessageWalletTopup.getAmount()));
            holder.requesterNumber.setText(realmRoomMessageWalletTopup.getRequestMobileNumber());
            holder.chargerNumber.setText(realmRoomMessageWalletTopup.getChargeMobileNumber());
            switch (realmRoomMessageWalletTopup.getTopupType()) {
                case ProtoGlobal.RoomMessageWallet.Topup.Type.IRANCELL_PREPAID_VALUE:
                    holder.topUpType.setText(R.string.irancell);
                    break;
                case ProtoGlobal.RoomMessageWallet.Topup.Type.IRANCELL_WOW_VALUE:
                    holder.topUpType.setText(R.string.irancell);
                    break;
                case ProtoGlobal.RoomMessageWallet.Topup.Type.IRANCELL_WIMAX_VALUE:
                    holder.topUpType.setText(R.string.irancell);
                    break;
                case ProtoGlobal.RoomMessageWallet.Topup.Type.IRANCELL_POSTPAID_VALUE:
                    holder.topUpType.setText(R.string.irancell);
                    break;
                case ProtoGlobal.RoomMessageWallet.Topup.Type.MCI_VALUE:
                    holder.topUpType.setText(R.string.hamrahe_aval);
                    break;
                case ProtoGlobal.RoomMessageWallet.Topup.Type.RIGHTEL_VALUE:
                    holder.topUpType.setText(R.string.ritel);
                    break;
            }
            holder.cardNumber.setText(realmRoomMessageWalletTopup.getCardNumber());
            holder.terminalNo.setText(String.valueOf(realmRoomMessageWalletTopup.getTerminalNo()));
            holder.rrn.setText(String.valueOf(realmRoomMessageWalletTopup.getRrn()));
            holder.traceNumber.setText(String.valueOf(realmRoomMessageWalletTopup.getTraceNumber()));
            holder.requestTime.setText(persianCalender);
        });
    }

    @NotNull
    @Override
    public ViewHolder getViewHolder(@NotNull View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView orderId;
        private TextView amount;
        private TextView requesterNumber;
        private TextView chargerNumber;
        private TextView topUpType;
        private TextView cardNumber;
        private TextView terminalNo;
        private TextView rrn;
        private TextView traceNumber;
        private TextView requestTime;

        protected ReserveSpaceRoundedImageView image;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title_topUp);
            orderId = view.findViewById(R.id.tv_topUp_orderId);
            amount = view.findViewById(R.id.tv_topUp_amount);
            requesterNumber = view.findViewById(R.id.tv_topUp_requesterNumber);
            chargerNumber = view.findViewById(R.id.tv_topUp_chargerNumber);
            topUpType = view.findViewById(R.id.tv_topUp_type);
            cardNumber = view.findViewById(R.id.tv_topUp_cardNumber);
            terminalNo = view.findViewById(R.id.tv_topUp_terminalNo);
            rrn = view.findViewById(R.id.tv_topUp_rrn);
            traceNumber = view.findViewById(R.id.tv_topUp_traceNumber);
            requestTime = view.findViewById(R.id.tv_topup_requestDate);

        }
    }
}
