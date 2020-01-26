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
import net.iGap.realm.RealmRoomMessageWalletBill;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LogWalletBill extends AbstractMessage<LogWalletBill, LogWalletBill.ViewHolder> {

    public LogWalletBill(MessagesAdapter<AbstractMessage> mAdapter, ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(mAdapter, true, type, messageClickListener);
    }

    @Override
    public int getType() {
        return R.id.ll_bill;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_log_bill;
    }


    @Override
    public void bindView(final ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
        RealmRoomMessageWalletBill realmRoomMessageWalletBill = mMessage.getRoomMessageWallet().getRealmRoomMessageWalletBill();
        DbManager.getInstance().doRealmTask(realm -> {
            String rrn = String.valueOf(realmRoomMessageWalletBill.getRrn());
            String cardNumber = realmRoomMessageWalletBill.getCardNumber();
            String traceNumber = String.valueOf(realmRoomMessageWalletBill.getTraceNumber());
            String persianCalender = HelperCalander.checkHijriAndReturnTime(realmRoomMessageWalletBill.getRequestTime()) + " " + "-" + " " + TimeUtils.toLocal(realmRoomMessageWalletBill.getRequestTime() * DateUtils.SECOND_IN_MILLIS, G.CHAT_MESSAGE_TIME);

            if (HelperCalander.isPersianUnicode) {
                rrn = HelperCalander.convertToUnicodeFarsiNumber(rrn);
                cardNumber = HelperCalander.convertToUnicodeFarsiNumber(cardNumber);
                traceNumber = HelperCalander.convertToUnicodeFarsiNumber(traceNumber);
                persianCalender = HelperCalander.convertToUnicodeFarsiNumber(persianCalender);
            }

            holder.rrn.setText(rrn);
            holder.cardNumber.setText(cardNumber);
            holder.traceNumber.setText(traceNumber);
            holder.requestTime.setText(persianCalender);
            holder.payId.setText(realmRoomMessageWalletBill.getPayId());
            holder.billId.setText(realmRoomMessageWalletBill.getBillId());
            holder.billType.setText(realmRoomMessageWalletBill.getBillType());
            holder.amount.setText(String.valueOf(realmRoomMessageWalletBill.getAmount()));
            holder.orderId.setText(String.valueOf(realmRoomMessageWalletBill.getOrderId()));
            holder.terminalNo.setText(String.valueOf(realmRoomMessageWalletBill.getTerminalNo()));
        });
    }

    @NotNull
    @Override
    public ViewHolder getViewHolder(@NotNull View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView amount;
        private TextView payId;
        private TextView orderId;
        private TextView billId;
        private TextView billType;
        private TextView cardNumber;
        private TextView terminalNo;
        private TextView rrn;
        private TextView traceNumber;
        private TextView requestTime;

        protected ReserveSpaceRoundedImageView image;

        public ViewHolder(View view) {
            super(view);
            amount = view.findViewById(R.id.tv_bill_amount);
            payId = view.findViewById(R.id.tv_bill_payId);
            orderId = view.findViewById(R.id.tv_bill_orderId);
            billId = view.findViewById(R.id.tv_bill_billId);
            billType = view.findViewById(R.id.tv_bill_billType);
            cardNumber = view.findViewById(R.id.tv_bill_cardNumber);
            terminalNo = view.findViewById(R.id.tv_bill_terminalNo);
            rrn = view.findViewById(R.id.tv_bill_referenceNo);
            traceNumber = view.findViewById(R.id.tv_bill_traceNumber);
            requestTime = view.findViewById(R.id.tv_bill_requestDate);
        }
    }
}