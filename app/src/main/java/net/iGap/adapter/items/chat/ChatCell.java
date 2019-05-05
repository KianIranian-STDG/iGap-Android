package net.iGap.adapter.items.chat;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.module.CircleImageView;
import net.iGap.module.EmojiTextViewE;
import net.iGap.module.MaterialDesignTextView;

import static android.view.Gravity.CENTER;
import static net.iGap.adapter.items.chat.ViewMaker.i_Dp;

public class ChatCell extends ConstraintLayout {

    boolean isRtl;
    boolean isDarkTheme;

    public ChatCell(Context context) {
        super(context);
        init();
    }

    public static void setTextSize(TextView v, int sizeSrc) {
        int mSize = i_Dp(sizeSrc);
        v.setTextSize(TypedValue.COMPLEX_UNIT_PX, mSize);
    }

    public static void setTypeFace(TextView v) {
        v.setTypeface(G.typeface_IRANSansMobile);
    }

    private void init() {
        setId(R.id.cl_chatCell_root);

        ConstraintSet set = new ConstraintSet();
        isRtl = HelperCalander.isPersianUnicode;
        isDarkTheme = G.isDarkTheme;

        /**
         * init avatar image
         * */
        CircleImageView avatarImageView = new CircleImageView(G.context);
        avatarImageView.setId(R.id.iv_chatCell_userAvatar);
        addView(avatarImageView);


        /**
         * init chat icon(channel,group,pv,mute and unMute)
         * */
        MaterialDesignTextView chatIcon = new MaterialDesignTextView(G.context);
        chatIcon.setId(R.id.tv_chatCell_chatIcon);
        chatIcon.setTextColor(isDarkTheme ? Color.parseColor(G.textTitleTheme) : Color.parseColor("#333333"));
        chatIcon.setTextSize(R.dimen.dp14);
        addView(chatIcon);


        /**
         * init room name
         * */
        EmojiTextViewE roomName = new EmojiTextViewE(G.context);
        roomName.setId(R.id.tv_chatCell_roomName);
        setTypeFace(roomName);
        roomName.setEllipsize(TextUtils.TruncateAt.END);
        roomName.setSingleLine(true);
        roomName.setEmojiSize(i_Dp(R.dimen.dp16));
        roomName.setTextColor(isDarkTheme ? Color.parseColor(G.textTitleTheme) : G.context.getResources().getColor(R.color.black90));
        addView(roomName);


        /**
         * init verify room
         * */
        AppCompatImageView verify = new AppCompatImageView(G.context);
        verify.setId(R.id.tv_chatCell_verify);
        verify.setImageResource(R.drawable.ic_verify);
        addView(verify);


        /**
         *
         * init last message sender name
         * drafts
         *
         * */
        EmojiTextViewE lastMessageSender = new EmojiTextViewE(G.context);
        lastMessageSender.setId(R.id.tv_chatCell_firstTextView);
        lastMessageSender.setSingleLine(true);
        setTypeFace(lastMessageSender);
        lastMessageSender.setTextColor(G.context.getResources().getColor(R.color.green));
        setTextSize(lastMessageSender, R.dimen.dp13);
        lastMessageSender.setEmojiSize(i_Dp(R.dimen.dp14));
        addView(lastMessageSender);

        /**
         * init last message content
         * is typing
         * */
        EmojiTextViewE isTyping = new EmojiTextViewE(G.context);
        isTyping.setId(R.id.tv_chatCell_secondTextView);
        isTyping.setEllipsize(TextUtils.TruncateAt.END);
        isTyping.setSingleLine(true);
        setTypeFace(isTyping);
        isTyping.setTextColor(Color.parseColor("#FF616161"));
        setTextSize(isTyping, G.twoPaneMode ? R.dimen.dp16 : R.dimen.dp12);
        isTyping.setEmojiSize(i_Dp(R.dimen.dp13));
        isTyping.setTextColor(getResources().getColor(R.color.md_blue_900));
        addView(isTyping);


        /**
         * init last message content type (image,file,voice)
         * */
        EmojiTextViewE lastMessageType = new EmojiTextViewE(G.context);
        lastMessageType.setId(R.id.tv_chatCell_thirtedTextView);
        lastMessageType.setEllipsize(TextUtils.TruncateAt.END);
        lastMessageType.setSingleLine(true);
        setTypeFace(lastMessageType);
        lastMessageType.setTextColor(isDarkTheme ? Color.parseColor(G.textSubTheme) : Color.parseColor("#FF616161"));
        setTextSize(lastMessageType, R.dimen.dp12);
        lastMessageType.setEmojiSize(i_Dp(R.dimen.dp13));
        addView(lastMessageType);


        /**
         * init room notification
         * */
        MaterialDesignTextView mute = new MaterialDesignTextView(G.context);
        mute.setId(R.id.iv_chatCell_mute);
        mute.setText(G.fragmentActivity.getResources().getString(R.string.md_muted));
        mute.setTextColor(Color.parseColor(G.textTitleTheme));
        setTextSize(mute, R.dimen.dp13);
        addView(mute);


        /**
         * init last message status(read ,send , failed)
         * */
        AppCompatImageView messageStatus = new AppCompatImageView(G.context);
        messageStatus.setId(R.id.iv_chatCell_messageStatus);
        messageStatus.setColorFilter(Color.parseColor(G.tintImage), PorterDuff.Mode.SRC_IN);
        addView(messageStatus);


        /**
         * init last message send data and time
         * */
        AppCompatTextView messageData = new AppCompatTextView(G.context);
        messageData.setId(R.id.tv_chatCell_messageData);
        messageData.setSingleLine(true);
        messageData.setTextColor(Color.parseColor(G.textTitleTheme));
        setTextSize(messageData, R.dimen.dp12);
        setTypeFace(messageData);
        addView(messageData);


        /**
         * init room unRead message count
         * */
        BadgeView badgeView = new BadgeView(G.context);
        badgeView.setId(R.id.cv_chatCell_badge);
        badgeView.getTextView().setId(R.id.iv_chatCell_messageCount);
        badgeView.setBadgeColor(R.drawable.rect_oval_red);
        setTypeFace(badgeView.getTextView());
        addView(badgeView);


        /**
         * init pinned room on top
         * */
        MaterialDesignTextView pinnedMessage = new MaterialDesignTextView(G.context);
        pinnedMessage.setId(R.id.iv_chatCell_pinnedMessage);
        pinnedMessage.setGravity(CENTER);
        pinnedMessage.setText(G.fragmentActivity.getResources().getString(R.string.md_circlePin));
        pinnedMessage.setTextColor(Color.parseColor(G.textTitleTheme));
        pinnedMessage.setTextSize(i_Dp(R.dimen.dp20));
        setTextSize(pinnedMessage, R.dimen.dp20);
        addView(pinnedMessage);


        /**
         * set views dependency
         * */

        set.constrainHeight(avatarImageView.getId(), i_Dp(R.dimen.dp52));
        set.constrainWidth(avatarImageView.getId(), i_Dp(R.dimen.dp52));
        set.connect(avatarImageView.getId(), ConstraintSet.TOP,
                ConstraintSet.PARENT_ID, ConstraintSet.TOP, i_Dp(R.dimen.dp6));
        set.connect(avatarImageView.getId(), ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, i_Dp(R.dimen.dp6));
        set.connect(avatarImageView.getId(), isRtl ? ConstraintSet.RIGHT : ConstraintSet.LEFT,
                ConstraintSet.PARENT_ID, isRtl ? ConstraintSet.RIGHT : ConstraintSet.LEFT, i_Dp(R.dimen.dp6));

        set.constrainHeight(chatIcon.getId(), 0);
        set.constrainWidth(chatIcon.getId(), ConstraintSet.WRAP_CONTENT);
        set.connect(chatIcon.getId(), isRtl ? ConstraintSet.RIGHT : ConstraintSet.LEFT,
                avatarImageView.getId(), isRtl ? ConstraintSet.LEFT : ConstraintSet.RIGHT, i_Dp(R.dimen.dp8));

        set.constrainHeight(roomName.getId(), ConstraintSet.WRAP_CONTENT);
        set.constrainWidth(roomName.getId(), ConstraintSet.WRAP_CONTENT);
        set.connect(roomName.getId(), isRtl ? ConstraintSet.RIGHT : ConstraintSet.LEFT,
                chatIcon.getId(), isRtl ? ConstraintSet.LEFT : ConstraintSet.RIGHT, i_Dp(R.dimen.dp8));

        set.constrainHeight(verify.getId(), i_Dp(R.dimen.dp18));
        set.constrainWidth(verify.getId(), i_Dp(R.dimen.dp18));
        set.connect(verify.getId(), isRtl ? ConstraintSet.RIGHT : ConstraintSet.LEFT,
                roomName.getId(), isRtl ? ConstraintSet.LEFT : ConstraintSet.RIGHT, i_Dp(R.dimen.dp8));
        set.connect(verify.getId(), ConstraintSet.TOP, roomName.getId(), ConstraintSet.TOP);
        set.connect(verify.getId(), ConstraintSet.BOTTOM, roomName.getId(), ConstraintSet.BOTTOM);

        set.constrainHeight(messageData.getId(), ConstraintSet.WRAP_CONTENT);
        set.constrainWidth(messageData.getId(), ConstraintSet.WRAP_CONTENT);
        set.connect(messageData.getId(), isRtl ? ConstraintSet.LEFT : ConstraintSet.RIGHT,
                ConstraintSet.PARENT_ID, isRtl ? ConstraintSet.LEFT : ConstraintSet.RIGHT);


//        bottom

        set.constrainHeight(lastMessageType.getId(), 0);
        set.constrainWidth(lastMessageType.getId(), ConstraintSet.WRAP_CONTENT);
        set.connect(lastMessageType.getId(), isRtl ? ConstraintSet.RIGHT : ConstraintSet.LEFT,
                chatIcon.getId(), isRtl ? ConstraintSet.RIGHT : ConstraintSet.LEFT);

        set.constrainHeight(isTyping.getId(), i_Dp(R.dimen.dp100));
        set.constrainWidth(isTyping.getId(), i_Dp(R.dimen.dp100));
        set.connect(isTyping.getId(), isRtl ? ConstraintSet.RIGHT : ConstraintSet.LEFT, lastMessageType.getId(),
                isRtl ? ConstraintSet.LEFT : ConstraintSet.RIGHT);

        set.constrainHeight(messageStatus.getId(), ConstraintSet.WRAP_CONTENT);
        set.constrainWidth(messageStatus.getId(), ConstraintSet.WRAP_CONTENT);
        set.connect(messageStatus.getId(), isRtl ? ConstraintSet.LEFT : ConstraintSet.RIGHT,
                ConstraintSet.PARENT_ID, isRtl ? ConstraintSet.LEFT : ConstraintSet.RIGHT);

        set.setVerticalChainStyle(roomName.getId(), ConstraintSet.CHAIN_PACKED);
        set.setVerticalChainStyle(lastMessageType.getId(), ConstraintSet.CHAIN_PACKED);

        set.applyTo(this);
    }

    public int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

}
