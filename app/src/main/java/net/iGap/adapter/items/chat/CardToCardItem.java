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

import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

import net.iGap.R;
import net.iGap.adapter.MessagesAdapter;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.LayoutCreator;
import net.iGap.model.CardToCardValue;
import net.iGap.module.FontIconTextView;
import net.iGap.observers.interfaces.IMessageItem;
import net.iGap.proto.ProtoGlobal;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CardToCardItem extends AbstractMessage<CardToCardItem, CardToCardItem.ViewHolder> {

    public CardToCardItem(MessagesAdapter<AbstractMessage> mAdapter, ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(mAdapter, true, type, messageClickListener);
    }

    @Override
    public int getType() {
        return R.id.cardToCard;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_message;
    }

    @Override
    public void bindView(final ViewHolder holder, List payloads) {

        super.bindView(holder, payloads);
        holder.getChatBloke().setBackgroundResource(0);

        holder.payButton.setVisibility(structMessage.isSenderMe() ? View.GONE : View.VISIBLE);

        setTextIfNeeded(holder.messageTv);

    }

    @NotNull
    @Override
    public ViewHolder getViewHolder(@NotNull View v) {
        return new ViewHolder(v);
    }

    protected class ViewHolder extends NewChatItemHolder {
        private TextView cardToCardAmountTv;
        private TextView messageTv;
        private Button payButton;
        private FontIconTextView cardIcon;
        private ConstraintLayout rootView;
        private ConstraintSet set;
        private LinearLayout innerLayout;
        private CardToCardValue value;
        private OnCardToCard onCardToCard;
        private View lineView;

        public ViewHolder(View view) {
            super(view);
            rootView = new ConstraintLayout(view.getContext());
            set = new ConstraintSet();
            innerLayout = new LinearLayout(view.getContext());
            messageTv = new AppCompatTextView(view.getContext());
            payButton = new Button(view.getContext());
            cardToCardAmountTv = new TextView(view.getContext());
            cardIcon = new FontIconTextView(view.getContext());

            messageTv.setTextColor(theme.getReceivedMessageColor(messageTv.getContext()));
            setTextSize(messageTv, R.dimen.smallTextSize);
            setTypeFace(messageTv);

            payButton.setId(R.id.cardToCard_button);
            payButton.setText(getResources().getString(R.string.pay));
            setTextSize(payButton, R.dimen.standardTextSize);
            setTypeFace(payButton);
            payButton.setTextColor(getColor(R.color.white));


            cardToCardAmountTv.setTextColor(theme.getReceivedMessageColor(cardToCardAmountTv.getContext()));
            cardToCardAmountTv.setGravity(Gravity.CENTER);
            setTextSize(cardToCardAmountTv, R.dimen.standardTextSize);
            setTypeFace(cardToCardAmountTv, Typeface.BOLD);

            cardIcon.setId(R.id.cardToCard_icon);
            cardIcon.setText(getResources().getString(R.string.icon_card_to_card));
            setTextSize(cardIcon, R.dimen.dp32);
            cardIcon.setTextColor(ContextCompat.getColor(cardIcon.getContext(), R.color.white));

            lineView = new View(getContext());
            lineView.setBackgroundColor(getColor(R.color.gray_9d));

            innerLayout.setOrientation(LinearLayout.VERTICAL);
            innerLayout.setId(R.id.cardToCard_innerLayout);

//            if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.LOLLIPOP_MR1){
            innerLayout.setPadding(LayoutCreator.dp(4), LayoutCreator.dp(25), LayoutCreator.dp(4), LayoutCreator.dp(4));
//            } else{
//                innerLayout.setPadding(LayoutCreator.dp(4), LayoutCreator.dp(4), LayoutCreator.dp(4), LayoutCreator.dp(4));
//            }

            innerLayout.setBackgroundResource(theme.getCardToCardBackground(innerLayout.getContext()));
            payButton.setBackgroundResource(theme.getCardToCardButton(payButton.getContext()));
            cardIcon.setBackgroundResource(theme.getCardToCardIconBackground(cardIcon.getContext()));

            set.constrainHeight(payButton.getId(), ConstraintSet.WRAP_CONTENT);
            set.constrainWidth(payButton.getId(), ConstraintSet.MATCH_CONSTRAINT);

            set.constrainHeight(innerLayout.getId(), ConstraintSet.WRAP_CONTENT);
            set.constrainWidth(innerLayout.getId(), ConstraintSet.MATCH_CONSTRAINT);

            set.connect(payButton.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
            set.connect(payButton.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
            set.connect(payButton.getId(), ConstraintSet.TOP, innerLayout.getId(), ConstraintSet.BOTTOM, LayoutCreator.dp(8));
            set.connect(payButton.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, LayoutCreator.dp(8));

            innerLayout.addView(cardToCardAmountTv, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT,
                    Gravity.CENTER, 8, 24, 8, 4));

            innerLayout.addView(lineView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 1, Gravity.CENTER,
                    16, 0, 16, 0));

            innerLayout.addView(messageTv, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT,
                    Gravity.CENTER, 8, 4, 8, 8));

            set.constrainHeight(cardIcon.getId(), LayoutCreator.dp(56));
            set.constrainWidth(cardIcon.getId(), LayoutCreator.dp(56));

            set.connect(innerLayout.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, LayoutCreator.dp(25));

            set.connect(cardIcon.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
            set.connect(cardIcon.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
            set.connect(cardIcon.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);

            rootView.addView(innerLayout);
            rootView.addView(payButton);
            rootView.addView(cardIcon);
            set.applyTo(rootView);

            payButton.setOnClickListener(v -> {
                if (onCardToCard != null && value != null) {
                    onCardToCard.onClick(value);
                }
            });

            rootView.setLayoutParams(LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT));
            getContentBloke().addView(rootView, 0);
        }

        public ConstraintLayout getRootView() {
            return rootView;
        }

        public LinearLayout getInnerLayout() {
            return innerLayout;
        }

        public CardToCardValue getValue() {
            return value;
        }

        public void setValue(CardToCardValue value) {
            cardToCardAmountTv.setText(HelperCalander.isPersianUnicode ?
                    HelperCalander.convertToUnicodeFarsiNumber(value.getAmount() + " " + getResources().getString(R.string.rial))
                    : value.getAmount() + " " + getResources().getString(R.string.rial));
            this.value = value;
        }

        public void setOnCardToCard(OnCardToCard onCardToCard) {
            this.onCardToCard = onCardToCard;
        }

    }

    @FunctionalInterface
    public interface OnCardToCard {
        void onClick(CardToCardValue cardToCard);
    }
}
