package net.iGap.adapter.items.chat;

import android.content.Context;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.LayoutCreator;
import net.iGap.libs.bottomNavigation.Util.Utils;
import net.iGap.module.CircleImageView;
import net.iGap.module.EmojiTextViewE;
import net.iGap.module.FontIconTextView;

import static net.iGap.adapter.items.chat.ViewMaker.i_Dp;

public class ChatCell extends ConstraintLayout {

    public ChatCell(Context context) {
        super(context);
        init();
    }


    private void init() {

        boolean isRtl = HelperCalander.isPersianUnicode;
        boolean isDarkTheme = G.isDarkTheme;
        ConstraintSet set = new ConstraintSet();


        TypedValue rippleView = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, rippleView, true);
        this.setBackgroundResource(rippleView.resourceId);


        /**
         * init pinned room on top
         * */

        AppCompatImageView pinView = new AppCompatImageView(getContext());
        pinView.setId(R.id.iv_iv_chatCell_pin);
        if (isRtl) {
            pinView.setBackgroundResource(R.drawable.pin_rtl);
        } else {
            pinView.setBackgroundResource(R.drawable.pin);
        }
        addView(pinView);

        /**
         * add check box
         */
        CheckBox cellCheckbox = new CheckBox(getContext());
        cellCheckbox.setId(R.id.iv_itemContactChat_checkBox);
        cellCheckbox.setButtonDrawable(R.drawable.check_box_background);
        cellCheckbox.setPadding(10, 0, 0, 0);
        cellCheckbox.setClickable(false);
        addView(cellCheckbox);

        /**
         * init avatar userAvatarIv
         * */
        CircleImageView avatarImageView = new CircleImageView(G.context);
        avatarImageView.setId(R.id.iv_chatCell_userAvatar);
        avatarImageView.setPadding(isRtl ? 16 : 10, 0, isRtl ? 10 : 16, 0);
        addView(avatarImageView);


        /**
         * init chat icon(channel,group,pv,muteRoomTv and unMute)
         * */
        FontIconTextView chatIcon = new FontIconTextView(G.context);
        chatIcon.setId(R.id.tv_chatCell_chatIcon);
        chatIcon.setTextColor(isDarkTheme ? Color.parseColor(G.textTitleTheme) : Color.parseColor("#333333"));
        setTextSize(chatIcon, R.dimen.standardTextSize);
        addView(chatIcon);


        /**
         * init room roomNameTv
         * */
        EmojiTextViewE roomName = new EmojiTextViewE(G.context);
        roomName.setId(R.id.tv_chatCell_roomName);
        setTypeFace(roomName);
        setTextSize(roomName, R.dimen.standardTextSize);
        roomName.setSingleLine(true);
        roomName.setEllipsize(TextUtils.TruncateAt.END);
        roomName.setEmojiSize(i_Dp(R.dimen.dp16));
        roomName.setTextColor(isDarkTheme ? getResources().getColor(R.color.white) : G.context.getResources().getColor(R.color.black90));
        addView(roomName);


        /**
         * init verify room
         * */
        FontIconTextView verify = new FontIconTextView(G.context);
        verify.setId(R.id.tv_chatCell_verify);
        verify.setTextColor(getContext().getResources().getColor(R.color.verify_color));
        verify.setText(R.string.verify_icon);
        setTextSize(verify, R.dimen.standardTextSize);
        addView(verify);


        /**
         * init last message sender roomNameTv
         * drafts
         * you
         *
         * */
        EmojiTextViewE firstTextView = new EmojiTextViewE(G.context);
        firstTextView.setId(R.id.tv_chatCell_firstTextView);
        firstTextView.setSingleLine(true);
        setTypeFace(firstTextView);
        setTextSize(firstTextView, R.dimen.dp12);
        firstTextView.setEmojiSize(i_Dp(R.dimen.dp14));
//        addView(firstTextView);


        /**
         * init last message content
         * is typing
         * message content
         * Voice Call Cancelled
         *
         * */
        EmojiTextViewE secondTextView = new EmojiTextViewE(G.context);
        secondTextView.setId(R.id.tv_chatCell_secondTextView);
        secondTextView.setEllipsize(TextUtils.TruncateAt.END);
        secondTextView.setSingleLine(true);
        setTypeFace(secondTextView);
        secondTextView.setTextColor(isDarkTheme ? getContext().getResources().getColor(R.color.gray_f2) : Color.parseColor("#FF616161"));
        setTextSize(secondTextView, G.twoPaneMode ? R.dimen.dp16 : R.dimen.dp12);
        secondTextView.setEmojiSize(i_Dp(R.dimen.dp14));
//        addView(secondTextView);


        /**
         * init last message content type (userAvatarIv,file,voice)
         * sticker
         * photo caption
         * gif caption
         * video caption
         * */
        EmojiTextViewE thirdTextView = new EmojiTextViewE(G.context);
        thirdTextView.setId(R.id.tv_chatCell_thirdTextView);
        thirdTextView.setEllipsize(TextUtils.TruncateAt.END);
        thirdTextView.setSingleLine(true);
        setTypeFace(thirdTextView);
        thirdTextView.setTextColor(isDarkTheme ? getContext().getResources().getColor(R.color.gray_f2) : Color.parseColor("#FF616161"));
        setTextSize(thirdTextView, R.dimen.dp12);
        thirdTextView.setEmojiSize(i_Dp(R.dimen.dp14));
//        addView(thirdTextView);


        /**
         * init room notification
         * */
        FontIconTextView mute = new FontIconTextView(G.context);
        mute.setId(R.id.iv_chatCell_mute);
        mute.setText(R.string.mute_icon);
        mute.setTextColor(Color.parseColor(G.textTitleTheme));
        setTextSize(mute, R.dimen.dp13);
        addView(mute);


        /**
         * init last message status(read ,send , failed)
         * */
        FontIconTextView messageStatus = new FontIconTextView(G.context);
        messageStatus.setId(R.id.iv_chatCell_messageStatus);
        messageStatus.setTextSize(20);
        setTextSize(messageStatus, R.dimen.standardTextSize);
        addView(messageStatus);


        /**
         * init last message send data and time
         * */
        AppCompatTextView messageData = new AppCompatTextView(G.context);
        messageData.setId(R.id.tv_chatCell_messageData);
        messageData.setSingleLine(true);
        messageData.setTextColor(Color.parseColor(G.textTitleTheme));
        setTextSize(messageData, R.dimen.dp10);
        setTypeFace(messageData);
        addView(messageData);


        /**
         * init room unRead message count
         * */
        BadgeView badgeView = new BadgeView(G.context);
        badgeView.setId(R.id.iv_chatCell_messageCount);
        setTypeFace(badgeView.getTextView());
        addView(badgeView);

        /**
         * bottom line
         * */

        View bottomView = new View(getContext());
        bottomView.setId(R.id.v_chatCell_bottomView);
        bottomView.setBackgroundColor(isDarkTheme ? getResources().getColor(R.color.gray_6c) : getResources().getColor(R.color.gray_300));
        addView(bottomView);

        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setId(R.id.ll_chatCell_messageBox);


        /**
         * force gravity in message preview because we use constraint layout chain
         * */
        if (isRtl) {
            firstTextView.setGravity(Gravity.RIGHT);
            secondTextView.setGravity(Gravity.RIGHT);
            thirdTextView.setGravity(Gravity.RIGHT);
            roomName.setGravity(Gravity.RIGHT);
        } else {
            firstTextView.setGravity(Gravity.LEFT);
            secondTextView.setGravity(Gravity.LEFT);
            thirdTextView.setGravity(Gravity.LEFT);
            roomName.setGravity(Gravity.LEFT);
        }

        /**
         * set views dependency
         * */

        set.constrainHeight(cellCheckbox.getId(), ConstraintSet.WRAP_CONTENT);
        set.constrainWidth(cellCheckbox.getId(), ConstraintSet.WRAP_CONTENT);

        set.connect(cellCheckbox.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        set.connect(cellCheckbox.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        set.connect(cellCheckbox.getId(), isRtl ? ConstraintSet.RIGHT : ConstraintSet.LEFT,
                ConstraintSet.PARENT_ID, isRtl ? ConstraintSet.RIGHT : ConstraintSet.LEFT, 16);

        set.constrainHeight(avatarImageView.getId(), i_Dp(R.dimen.dp60));
        set.constrainWidth(avatarImageView.getId(), i_Dp(R.dimen.dp60));

        set.connect(avatarImageView.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        set.connect(avatarImageView.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        set.connect(avatarImageView.getId(), ConstraintSet.START, cellCheckbox.getId(), ConstraintSet.END);

        set.constrainHeight(chatIcon.getId(), i_Dp(R.dimen.dp18));
        set.constrainWidth(chatIcon.getId(), i_Dp(R.dimen.dp18));

        set.constrainHeight(roomName.getId(), ConstraintSet.WRAP_CONTENT);
        set.constrainWidth(roomName.getId(), ConstraintSet.MATCH_CONSTRAINT);

        set.constrainHeight(verify.getId(), i_Dp(R.dimen.dp18));
        set.constrainWidth(verify.getId(), i_Dp(R.dimen.dp18));

        set.constrainHeight(badgeView.getId(), ConstraintSet.WRAP_CONTENT);
        set.constrainWidth(badgeView.getId(), ConstraintSet.WRAP_CONTENT);

        set.constrainHeight(mute.getId(), ConstraintSet.WRAP_CONTENT);
        set.constrainWidth(mute.getId(), ConstraintSet.WRAP_CONTENT);

        set.constrainHeight(messageData.getId(), ConstraintSet.WRAP_CONTENT);
        set.constrainWidth(messageData.getId(), ConstraintSet.WRAP_CONTENT);

        set.connect(chatIcon.getId(), ConstraintSet.TOP, roomName.getId(), ConstraintSet.TOP);
        set.connect(chatIcon.getId(), ConstraintSet.BOTTOM, roomName.getId(), ConstraintSet.BOTTOM);

        set.connect(verify.getId(), ConstraintSet.TOP, roomName.getId(), ConstraintSet.TOP);
        set.connect(verify.getId(), ConstraintSet.BOTTOM, roomName.getId(), ConstraintSet.BOTTOM);

        set.connect(messageData.getId(), ConstraintSet.TOP, roomName.getId(), ConstraintSet.TOP);
        set.connect(messageData.getId(), ConstraintSet.BOTTOM, roomName.getId(), ConstraintSet.BOTTOM);


//        set.constrainHeight(firstTextView.getId(), ConstraintSet.WRAP_CONTENT);
//        set.constrainWidth(firstTextView.getId(), ConstraintSet.WRAP_CONTENT);
//
//        set.constrainHeight(secondTextView.getId(), ConstraintSet.WRAP_CONTENT);
//        set.constrainWidth(secondTextView.getId(), ConstraintSet.MATCH_CONSTRAINT);
//
//        set.constrainHeight(thirdTextView.getId(), ConstraintSet.WRAP_CONTENT);
//        set.constrainWidth(thirdTextView.getId(), ConstraintSet.MATCH_CONSTRAINT);

        set.constrainHeight(messageStatus.getId(), i_Dp(R.dimen.dp24));
        set.constrainWidth(messageStatus.getId(), i_Dp(R.dimen.dp24));

//        set.connect(firstTextView.getId(), ConstraintSet.TOP, messageStatus.getId(), ConstraintSet.TOP);
//        set.connect(firstTextView.getId(), ConstraintSet.BOTTOM, messageStatus.getId(), ConstraintSet.BOTTOM);
//
//        set.connect(thirdTextView.getId(), ConstraintSet.TOP, messageStatus.getId(), ConstraintSet.TOP);
//        set.connect(thirdTextView.getId(), ConstraintSet.BOTTOM, messageStatus.getId(), ConstraintSet.BOTTOM);
//
//        set.connect(secondTextView.getId(), ConstraintSet.TOP, messageStatus.getId(), ConstraintSet.TOP);
//        set.connect(secondTextView.getId(), ConstraintSet.BOTTOM, messageStatus.getId(), ConstraintSet.BOTTOM);

        set.connect(linearLayout.getId(), ConstraintSet.TOP, messageStatus.getId(), ConstraintSet.TOP);
        set.connect(linearLayout.getId(), ConstraintSet.BOTTOM, messageStatus.getId(), ConstraintSet.BOTTOM);

        set.constrainHeight(bottomView.getId(), i_Dp(R.dimen.dp1));
        set.constrainWidth(bottomView.getId(), ConstraintSet.MATCH_CONSTRAINT);


        if (isRtl) {

            int[] topViews = {chatIcon.getId(), roomName.getId(), verify.getId()};
            float[] tioChainWeights = {0, 0, 0};
            set.createHorizontalChainRtl(avatarImageView.getId(), ConstraintSet.END, mute.getId(), ConstraintSet.START,
                    topViews, tioChainWeights, ConstraintSet.CHAIN_PACKED);

            set.connect(messageData.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, i_Dp(R.dimen.dp24));

            set.connect(badgeView.getId(), ConstraintSet.LEFT, messageStatus.getId(), ConstraintSet.LEFT);
            set.connect(badgeView.getId(), ConstraintSet.RIGHT, messageStatus.getId(), ConstraintSet.RIGHT);
            set.connect(badgeView.getId(), ConstraintSet.BOTTOM, messageStatus.getId(), ConstraintSet.BOTTOM);
            set.connect(badgeView.getId(), ConstraintSet.TOP, messageStatus.getId(), ConstraintSet.TOP);

            set.connect(messageStatus.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, i_Dp(R.dimen.dp8));

            set.connect(pinView.getId(), ConstraintSet.RIGHT, avatarImageView.getId(), ConstraintSet.RIGHT, i_Dp(R.dimen.dp32));
            set.connect(pinView.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, i_Dp(R.dimen.dp4));
            set.connect(pinView.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, i_Dp(R.dimen.dp2));
            set.connect(pinView.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, i_Dp(R.dimen.dp4));

            set.connect(bottomView.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, i_Dp(R.dimen.dp8));
            set.connect(bottomView.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
            set.connect(bottomView.getId(), ConstraintSet.RIGHT, avatarImageView.getId(), ConstraintSet.LEFT);

            set.connect(mute.getId(), ConstraintSet.TOP, roomName.getId(), ConstraintSet.TOP);
            set.connect(mute.getId(), ConstraintSet.BOTTOM, roomName.getId(), ConstraintSet.BOTTOM);
            set.connect(mute.getId(), ConstraintSet.LEFT, messageData.getId(), ConstraintSet.RIGHT, LayoutCreator.dp(4));

            set.connect(linearLayout.getId(), ConstraintSet.END, mute.getId(), ConstraintSet.END);
            set.connect(linearLayout.getId(), ConstraintSet.START, avatarImageView.getId(), ConstraintSet.END);

            linearLayout.addView(firstTextView, 0, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT));
            linearLayout.addView(secondTextView, 1, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT));
            linearLayout.addView(thirdTextView, 2, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT));
            addView(linearLayout);

        } else {

            int[] topViews = {chatIcon.getId(), roomName.getId(), verify.getId()};
            float[] tioChainWeights = {0, 0, 0};
            set.createHorizontalChain(avatarImageView.getId(), ConstraintSet.RIGHT, mute.getId(), ConstraintSet.LEFT,
                    topViews, tioChainWeights, ConstraintSet.CHAIN_PACKED);

            set.connect(messageData.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, i_Dp(R.dimen.dp24));

            set.connect(badgeView.getId(), ConstraintSet.LEFT, messageStatus.getId(), ConstraintSet.LEFT);
            set.connect(badgeView.getId(), ConstraintSet.RIGHT, messageStatus.getId(), ConstraintSet.RIGHT);
            set.connect(badgeView.getId(), ConstraintSet.BOTTOM, messageStatus.getId(), ConstraintSet.BOTTOM);
            set.connect(badgeView.getId(), ConstraintSet.TOP, messageStatus.getId(), ConstraintSet.TOP);

            set.connect(messageStatus.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, i_Dp(R.dimen.dp8));

            set.connect(bottomView.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, i_Dp(R.dimen.dp8));
            set.connect(bottomView.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
            set.connect(bottomView.getId(), ConstraintSet.LEFT, avatarImageView.getId(), ConstraintSet.RIGHT);

            set.connect(pinView.getId(), ConstraintSet.LEFT, avatarImageView.getId(), ConstraintSet.LEFT, i_Dp(R.dimen.dp32));
            set.connect(pinView.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, i_Dp(R.dimen.dp8));
            set.connect(pinView.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, i_Dp(R.dimen.dp2));
            set.connect(pinView.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, i_Dp(R.dimen.dp4));

            set.connect(mute.getId(), ConstraintSet.TOP, roomName.getId(), ConstraintSet.TOP);
            set.connect(mute.getId(), ConstraintSet.BOTTOM, roomName.getId(), ConstraintSet.BOTTOM);
            set.connect(mute.getId(), ConstraintSet.RIGHT, messageData.getId(), ConstraintSet.LEFT, LayoutCreator.dp(4));

            set.connect(linearLayout.getId(), ConstraintSet.RIGHT, mute.getId(), ConstraintSet.RIGHT);
            set.connect(linearLayout.getId(), ConstraintSet.LEFT, avatarImageView.getId(), ConstraintSet.RIGHT);

            thirdTextView.setGravity(Gravity.RIGHT);
            firstTextView.setGravity(Gravity.RIGHT);
            secondTextView.setGravity(Gravity.RIGHT);

            linearLayout.addView(firstTextView, 0, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT));
            linearLayout.addView(secondTextView, 1, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT));
            linearLayout.addView(thirdTextView, 2, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT));
            addView(linearLayout);
        }


        int[] chainViews = {roomName.getId(), messageStatus.getId()};
        float[] chainWeights = {1, 1};
        set.createVerticalChain(ConstraintSet.PARENT_ID, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM,
                chainViews, chainWeights, ConstraintSet.CHAIN_PACKED);

        set.applyTo(this);
    }

    private void setTextSize(TextView textView, int pxSize) {
        Utils.setTextSize(textView, pxSize);
    }

    private void setTypeFace(TextView v) {
        v.setTypeface(G.typeface_IRANSansMobile);
    }

}
