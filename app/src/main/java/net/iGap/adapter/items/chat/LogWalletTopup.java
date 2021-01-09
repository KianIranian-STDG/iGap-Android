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

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.MessagesAdapter;
import net.iGap.helper.HelperCalander;
import net.iGap.module.ReserveSpaceRoundedImageView;
import net.iGap.module.TimeUtils;
import net.iGap.observers.interfaces.IMessageItem;
import net.iGap.proto.ProtoGlobal;

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
    public void bindView(final ViewHolder holder, List payloads) {// TODO: 12/29/20 MESSAGE_REFACTOR
        super.bindView(holder, payloads);


        String amount = String.valueOf(messageObject.wallet.topupObject.amount);
        String requesterNumber = (messageObject.wallet.topupObject.requestMobileNumber);
        String chargerNumber = (messageObject.wallet.topupObject.chargeMobileNumber);
        String cardNumber = messageObject.wallet.topupObject.cardNumber;
        String orderId = String.valueOf(messageObject.wallet.topupObject.orderId);
        String terminalNo = String.valueOf(messageObject.wallet.topupObject.terminalNo);
        String rrn = String.valueOf(messageObject.wallet.topupObject.rrn);
        String traceNumber = String.valueOf(messageObject.wallet.topupObject.traceNumber);
        String persianCalender = HelperCalander.checkHijriAndReturnTime(messageObject.wallet.topupObject.requestTime) + " " + "-" + " " + TimeUtils.toLocal(messageObject.wallet.topupObject.requestTime * DateUtils.SECOND_IN_MILLIS, G.CHAT_MESSAGE_TIME);

        if (HelperCalander.isPersianUnicode) {
            amount = HelperCalander.convertToUnicodeFarsiNumber(amount);
            requesterNumber = HelperCalander.convertToUnicodeFarsiNumber(requesterNumber);
            chargerNumber = HelperCalander.convertToUnicodeFarsiNumber(chargerNumber);
            cardNumber = HelperCalander.convertToUnicodeFarsiNumber(cardNumber);
            orderId = HelperCalander.convertToUnicodeFarsiNumber(orderId);
            terminalNo = HelperCalander.convertToUnicodeFarsiNumber(terminalNo);
            rrn = HelperCalander.convertToUnicodeFarsiNumber(rrn);
            traceNumber = HelperCalander.convertToUnicodeFarsiNumber(traceNumber);
            persianCalender = HelperCalander.convertToUnicodeFarsiNumber(persianCalender);
        }

        holder.amount.setText(amount);
        holder.requesterNumber.setText(requesterNumber);
        holder.chargerNumber.setText(chargerNumber);
        holder.cardNumber.setText(cardNumber);
        holder.orderId.setText(orderId);
        holder.terminalNo.setText(terminalNo);
        holder.rrn.setText(rrn);
        holder.traceNumber.setText(traceNumber);
        holder.requestTime.setText(persianCalender);

        switch (messageObject.wallet.topupObject.topupType) {
            case ProtoGlobal.RoomMessageWallet.Topup.Type.IRANCELL_PREPAID_VALUE:
            case ProtoGlobal.RoomMessageWallet.Topup.Type.IRANCELL_WOW_VALUE:
            case ProtoGlobal.RoomMessageWallet.Topup.Type.IRANCELL_WIMAX_VALUE:
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

    }

    @NotNull
    @Override
    public ViewHolder getViewHolder(@NotNull View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
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
