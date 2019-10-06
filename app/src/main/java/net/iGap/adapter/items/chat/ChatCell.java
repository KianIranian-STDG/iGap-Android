package net.iGap.adapter.items.chat;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.res.ResourcesCompat;

import com.vanniktech.emoji.EmojiTextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.LayoutCreator;
import net.iGap.libs.bottomNavigation.Util.Utils;
import net.iGap.module.CircleImageView;
import net.iGap.module.FontIconTextView;

import static net.iGap.adapter.items.chat.ViewMaker.i_Dp;

public class ChatCell extends ConstraintLayout {

    public static final int DRAFT_COLOR = Color.RED;
    public static final int TYPING_COLOR = Color.parseColor("#1DA1F2");
    public static final int SENDER_COLOR = attachmentColor();
    public static final int DELETED_COLOR = Color.GRAY;

    public static final int FILE = 0x1F4CE;
    public static final int VIDEO = 0x1F4F9;
    public static final int MUSIC = 0x1F3A7;
    public static final int IMAGE = 0x1F5BC;
    public static final int GIF = 0x1F308;
    public static final int WALLET = 0x1F4B3;

    private EmojiTextView lastMessage;
    private AppCompatImageView pinView;
    private CircleImageView avatarImageView;
    private EmojiTextView roomNameTv;
    private FontIconTextView muteIconTv;
    private FontIconTextView chatIconTv;
    private AppCompatTextView lastMessageDate;
    private FontIconTextView verifyIconTv;
    private BadgeView badgeView;
    private FontIconTextView lastMessageStatusTv;
    private CheckBox cellCb;

    public static int messageColor() {
        return Color.parseColor(G.isDarkTheme ? "#E0E0E0" : "#616161");
    }

    public static int attachmentColor() {
        return Color.parseColor(G.isDarkTheme ? "#667B42" : "#9DC756");
    }

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

        pinView = new AppCompatImageView(getContext());
        pinView.setId(R.id.iv_iv_chatCell_pin);
        pinView.setBackgroundResource(R.drawable.pin);
        addView(pinView);

        /**
         * add check box
         */
        cellCb = new CheckBox(getContext());
        cellCb.setId(R.id.iv_itemContactChat_checkBox);
        cellCb.setButtonDrawable(R.drawable.check_box_background);
        cellCb.setPadding(10, 0, 0, 0);
        cellCb.setClickable(false);
        addView(cellCb);

        /**
         * init avatar userAvatarIv
         * */
        avatarImageView = new CircleImageView(getContext());
        avatarImageView.setId(R.id.iv_chatCell_userAvatar);
        avatarImageView.setPadding(isRtl ? 16 : 10, 0, isRtl ? 10 : 16, 0);
        addView(avatarImageView);


        /**
         * init chat icon(channel,group,pv,muteRoomTv and unMute)
         * */
        chatIconTv = new FontIconTextView(getContext());
        chatIconTv.setId(R.id.tv_chatCell_chatIcon);
        chatIconTv.setTextColor(isDarkTheme ? Color.parseColor(G.textTitleTheme) : Color.parseColor("#333333"));
        setTextSize(chatIconTv, R.dimen.standardTextSize);
        addView(chatIconTv);


        /**
         * init room roomNameTv
         * */
        roomNameTv = new EmojiTextView(getContext());
        roomNameTv.setId(R.id.tv_chatCell_roomName);
        setTypeFace(roomNameTv);
        setTextSize(roomNameTv, R.dimen.standardTextSize);
        roomNameTv.setSingleLine(true);
        roomNameTv.setEllipsize(TextUtils.TruncateAt.END);
        roomNameTv.setEmojiSize(i_Dp(R.dimen.dp16));
        roomNameTv.setTextColor(isDarkTheme ? getResources().getColor(R.color.white) : G.context.getResources().getColor(R.color.black90));
        addView(roomNameTv);


        /**
         * init verify room
         * */
        verifyIconTv = new FontIconTextView(getContext());
        verifyIconTv.setId(R.id.tv_chatCell_verify);
        verifyIconTv.setTextColor(getContext().getResources().getColor(R.color.verify_color));
        verifyIconTv.setText(R.string.verify_icon);
        setTextSize(verifyIconTv, R.dimen.standardTextSize);
        addView(verifyIconTv);


        /**
         * init room notification
         * */
        muteIconTv = new FontIconTextView(getContext());
        muteIconTv.setId(R.id.iv_chatCell_mute);
        muteIconTv.setText(R.string.mute_icon);
        muteIconTv.setTextColor(Color.parseColor(G.textTitleTheme));
        setTextSize(muteIconTv, R.dimen.dp13);
        addView(muteIconTv);


        /**
         * init last message status(read ,send , failed)
         * */
        lastMessageStatusTv = new FontIconTextView(getContext());
        lastMessageStatusTv.setId(R.id.iv_chatCell_messageStatus);
        lastMessageStatusTv.setTextSize(20);
        setTextSize(lastMessageStatusTv, R.dimen.standardTextSize);
        addView(lastMessageStatusTv);


        /**
         * init last message send data and time
         * */
        lastMessageDate = new AppCompatTextView(G.context);
        lastMessageDate.setId(R.id.tv_chatCell_messageData);
        lastMessageDate.setSingleLine(true);
        lastMessageDate.setTextColor(Color.parseColor(G.textTitleTheme));
        setTextSize(lastMessageDate, R.dimen.dp10);
        setTypeFace(lastMessageDate);
        addView(lastMessageDate);


        /**
         * init room unRead message count
         * */
        badgeView = new BadgeView(G.context);
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

        lastMessage = new EmojiTextView(getContext());
        lastMessage.setId(R.id.tv_chatCell_lastMessage);
        lastMessage.setEllipsize(TextUtils.TruncateAt.END);
        lastMessage.setGravity(Gravity.CENTER_VERTICAL);
        lastMessage.setSingleLine(true);
        setTypeFace(lastMessage);
        setTextSize(lastMessage, R.dimen.dp12);
        lastMessage.setEmojiSize(i_Dp(R.dimen.dp15));


        /**
         * force gravity in message preview because we use constraint layout chain
         * */
        if (isRtl) {
            roomNameTv.setGravity(Gravity.RIGHT);
            lastMessage.setGravity(Gravity.RIGHT);
        } else {
            roomNameTv.setGravity(Gravity.LEFT);
            lastMessage.setGravity(Gravity.LEFT);
        }

        /**
         * set views dependency
         * */

        set.constrainHeight(cellCb.getId(), ConstraintSet.WRAP_CONTENT);
        set.constrainWidth(cellCb.getId(), ConstraintSet.WRAP_CONTENT);

        set.connect(cellCb.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        set.connect(cellCb.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        set.connect(cellCb.getId(), isRtl ? ConstraintSet.RIGHT : ConstraintSet.LEFT,
                ConstraintSet.PARENT_ID, isRtl ? ConstraintSet.RIGHT : ConstraintSet.LEFT, 16);

        set.constrainHeight(avatarImageView.getId(), i_Dp(R.dimen.dp60));
        set.constrainWidth(avatarImageView.getId(), i_Dp(R.dimen.dp60));

        set.connect(avatarImageView.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        set.connect(avatarImageView.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        set.connect(avatarImageView.getId(), ConstraintSet.START, cellCb.getId(), ConstraintSet.END);

        set.constrainHeight(chatIconTv.getId(), i_Dp(R.dimen.dp18));
        set.constrainWidth(chatIconTv.getId(), i_Dp(R.dimen.dp18));

        set.constrainHeight(roomNameTv.getId(), ConstraintSet.WRAP_CONTENT);
        set.constrainWidth(roomNameTv.getId(), ConstraintSet.MATCH_CONSTRAINT);

        set.constrainHeight(verifyIconTv.getId(), i_Dp(R.dimen.dp18));
        set.constrainWidth(verifyIconTv.getId(), i_Dp(R.dimen.dp18));

        set.constrainHeight(badgeView.getId(), ConstraintSet.WRAP_CONTENT);
        set.constrainWidth(badgeView.getId(), ConstraintSet.WRAP_CONTENT);

        set.constrainHeight(muteIconTv.getId(), ConstraintSet.WRAP_CONTENT);
        set.constrainWidth(muteIconTv.getId(), ConstraintSet.WRAP_CONTENT);

        set.constrainHeight(lastMessageDate.getId(), ConstraintSet.WRAP_CONTENT);
        set.constrainWidth(lastMessageDate.getId(), ConstraintSet.WRAP_CONTENT);

        set.connect(chatIconTv.getId(), ConstraintSet.TOP, roomNameTv.getId(), ConstraintSet.TOP);
        set.connect(chatIconTv.getId(), ConstraintSet.BOTTOM, roomNameTv.getId(), ConstraintSet.BOTTOM);

        set.connect(verifyIconTv.getId(), ConstraintSet.TOP, roomNameTv.getId(), ConstraintSet.TOP);
        set.connect(verifyIconTv.getId(), ConstraintSet.BOTTOM, roomNameTv.getId(), ConstraintSet.BOTTOM);

        set.connect(lastMessageDate.getId(), ConstraintSet.TOP, roomNameTv.getId(), ConstraintSet.TOP);
        set.connect(lastMessageDate.getId(), ConstraintSet.BOTTOM, roomNameTv.getId(), ConstraintSet.BOTTOM);

        set.constrainHeight(lastMessageStatusTv.getId(), i_Dp(R.dimen.dp24));
        set.constrainWidth(lastMessageStatusTv.getId(), i_Dp(R.dimen.dp24));

        set.connect(lastMessage.getId(), ConstraintSet.TOP, lastMessageStatusTv.getId(), ConstraintSet.TOP);
        set.connect(lastMessage.getId(), ConstraintSet.BOTTOM, lastMessageStatusTv.getId(), ConstraintSet.BOTTOM);

        set.constrainHeight(bottomView.getId(), i_Dp(R.dimen.dp1));
        set.constrainWidth(bottomView.getId(), ConstraintSet.MATCH_CONSTRAINT);

        if (isRtl) {

            int[] topViews = {chatIconTv.getId(), roomNameTv.getId(), verifyIconTv.getId()};
            float[] tioChainWeights = {0, 0, 0};
            set.createHorizontalChainRtl(avatarImageView.getId(), ConstraintSet.END, muteIconTv.getId(), ConstraintSet.START,
                    topViews, tioChainWeights, ConstraintSet.CHAIN_PACKED);

            set.connect(lastMessageDate.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, i_Dp(R.dimen.dp24));

            set.connect(badgeView.getId(), ConstraintSet.LEFT, lastMessageStatusTv.getId(), ConstraintSet.LEFT);
            set.connect(badgeView.getId(), ConstraintSet.RIGHT, lastMessageStatusTv.getId(), ConstraintSet.RIGHT);
            set.connect(badgeView.getId(), ConstraintSet.BOTTOM, lastMessageStatusTv.getId(), ConstraintSet.BOTTOM);
            set.connect(badgeView.getId(), ConstraintSet.TOP, lastMessageStatusTv.getId(), ConstraintSet.TOP);

            set.connect(lastMessageStatusTv.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, i_Dp(R.dimen.dp8));

            set.connect(pinView.getId(), ConstraintSet.RIGHT, avatarImageView.getId(), ConstraintSet.RIGHT, i_Dp(R.dimen.dp32));
            set.connect(pinView.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, i_Dp(R.dimen.dp4));
            set.connect(pinView.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, i_Dp(R.dimen.dp2));
            set.connect(pinView.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, i_Dp(R.dimen.dp4));

            set.connect(bottomView.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, i_Dp(R.dimen.dp8));
            set.connect(bottomView.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
            set.connect(bottomView.getId(), ConstraintSet.RIGHT, avatarImageView.getId(), ConstraintSet.LEFT);

            set.connect(muteIconTv.getId(), ConstraintSet.TOP, roomNameTv.getId(), ConstraintSet.TOP);
            set.connect(muteIconTv.getId(), ConstraintSet.BOTTOM, roomNameTv.getId(), ConstraintSet.BOTTOM);
            set.connect(muteIconTv.getId(), ConstraintSet.LEFT, lastMessageDate.getId(), ConstraintSet.RIGHT, LayoutCreator.dp(4));

            set.connect(lastMessage.getId(), ConstraintSet.END, lastMessageDate.getId(), ConstraintSet.START);
            set.connect(lastMessage.getId(), ConstraintSet.START, avatarImageView.getId(), ConstraintSet.END);
            addView(lastMessage);


        } else {

            int[] topViews = {chatIconTv.getId(), roomNameTv.getId(), verifyIconTv.getId()};
            float[] tioChainWeights = {0, 0, 0};
            set.createHorizontalChain(avatarImageView.getId(), ConstraintSet.RIGHT, muteIconTv.getId(), ConstraintSet.LEFT,
                    topViews, tioChainWeights, ConstraintSet.CHAIN_PACKED);

            set.connect(lastMessageDate.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, i_Dp(R.dimen.dp24));

            set.connect(badgeView.getId(), ConstraintSet.LEFT, lastMessageStatusTv.getId(), ConstraintSet.LEFT);
            set.connect(badgeView.getId(), ConstraintSet.RIGHT, lastMessageStatusTv.getId(), ConstraintSet.RIGHT);
            set.connect(badgeView.getId(), ConstraintSet.BOTTOM, lastMessageStatusTv.getId(), ConstraintSet.BOTTOM);
            set.connect(badgeView.getId(), ConstraintSet.TOP, lastMessageStatusTv.getId(), ConstraintSet.TOP);

            set.connect(lastMessageStatusTv.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, i_Dp(R.dimen.dp8));

            set.connect(bottomView.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, i_Dp(R.dimen.dp8));
            set.connect(bottomView.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
            set.connect(bottomView.getId(), ConstraintSet.LEFT, avatarImageView.getId(), ConstraintSet.RIGHT);

            set.connect(pinView.getId(), ConstraintSet.LEFT, avatarImageView.getId(), ConstraintSet.LEFT, i_Dp(R.dimen.dp32));
            set.connect(pinView.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, i_Dp(R.dimen.dp8));
            set.connect(pinView.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, i_Dp(R.dimen.dp2));
            set.connect(pinView.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, i_Dp(R.dimen.dp4));

            set.connect(muteIconTv.getId(), ConstraintSet.TOP, roomNameTv.getId(), ConstraintSet.TOP);
            set.connect(muteIconTv.getId(), ConstraintSet.BOTTOM, roomNameTv.getId(), ConstraintSet.BOTTOM);
            set.connect(muteIconTv.getId(), ConstraintSet.RIGHT, lastMessageDate.getId(), ConstraintSet.LEFT, LayoutCreator.dp(4));

            set.connect(lastMessage.getId(), ConstraintSet.RIGHT, lastMessageDate.getId(), ConstraintSet.LEFT);
            set.connect(lastMessage.getId(), ConstraintSet.LEFT, avatarImageView.getId(), ConstraintSet.RIGHT);
            addView(lastMessage);
        }


        int[] chainViews = {roomNameTv.getId(), lastMessageStatusTv.getId()};
        float[] chainWeights = {1, 1};
        set.createVerticalChain(ConstraintSet.PARENT_ID, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM,
                chainViews, chainWeights, ConstraintSet.CHAIN_PACKED);

        set.applyTo(this);
    }

    private void setTextSize(TextView textView, int pxSize) {
        Utils.setTextSize(textView, pxSize);
    }

    private void setTypeFace(TextView v) {
        v.setTypeface(ResourcesCompat.getFont(v.getContext() , R.font.main_font));
    }

    public EmojiTextView getLastMessage() {
        return lastMessage;
    }

    public AppCompatImageView getPinView() {
        return pinView;
    }

    public CircleImageView getAvatarImageView() {
        return avatarImageView;
    }

    public EmojiTextView getRoomNameTv() {
        return roomNameTv;
    }

    public FontIconTextView getMuteIconTv() {
        return muteIconTv;
    }

    public FontIconTextView getChatIconTv() {
        return chatIconTv;
    }

    public AppCompatTextView getLastMessageDate() {
        return lastMessageDate;
    }

    public FontIconTextView getVerifyIconTv() {
        return verifyIconTv;
    }

    public BadgeView getBadgeView() {
        return badgeView;
    }

    public FontIconTextView getLastMessageStatusTv() {
        return lastMessageStatusTv;
    }

    public CheckBox getCellCb() {
        return cellCb;
    }
}
