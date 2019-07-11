package net.iGap.adapter.items.chat;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.NinePatchDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.LayoutCreator;
import net.iGap.helper.NinePatchBitmapFactory;
import net.iGap.libs.bottomNavigation.Util.Utils;
import net.iGap.module.CircleImageView;
import net.iGap.module.EmojiTextViewE;
import net.iGap.module.FontIconTextView;

import java.io.BufferedInputStream;
import java.io.IOException;

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

        /**
         * init pinned room on top
         * */

        AppCompatImageView pinView = new AppCompatImageView(getContext());
        pinView.setId(R.id.iv_iv_chatCell_pin);
        if (isRtl) {
            Bitmap bitmap = loadBitmapAsset("pinrtl.9.png", getContext());
            NinePatchDrawable drawable = NinePatchBitmapFactory.createNinePatchDrawable(getResources(), bitmap);
            pinView.setBackground(drawable);
        } else {
            Bitmap bitmap = loadBitmapAsset("pin.9.png", getContext());
            NinePatchDrawable drawable = NinePatchBitmapFactory.createNinePatchDrawable(getResources(), bitmap);
            pinView.setBackground(drawable);
        }
        addView(pinView);

        /**
         * add check box
         */
        CheckBox cellCheckbox = new CheckBox(getContext());
        cellCheckbox.setId(R.id.iv_itemContactChat_checkBox);
        cellCheckbox.setButtonDrawable(R.drawable.check_box_background);
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
        roomName.setEllipsize(TextUtils.TruncateAt.END);
        setTextSize(roomName, R.dimen.standardTextSize);
        roomName.setSingleLine(true);
        roomName.setEmojiSize(i_Dp(R.dimen.dp16));
        roomName.setTextColor(isDarkTheme ? getResources().getColor(R.color.white) : G.context.getResources().getColor(R.color.black90));
        addView(roomName);


        /**
         * init verify room
         * */
        FontIconTextView verify = new FontIconTextView(G.context);
        verify.setId(R.id.tv_chatCell_verify);
        verify.setTextColor(getContext().getResources().getColor(R.color.verify_color));
        verify.setText("a");
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
        setTextSize(firstTextView, R.dimen.dp14);
        firstTextView.setEmojiSize(i_Dp(R.dimen.dp14));
        addView(firstTextView);


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
        addView(secondTextView);


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
        setTextSize(thirdTextView, R.dimen.dp14);
        thirdTextView.setEmojiSize(i_Dp(R.dimen.dp14));
        addView(thirdTextView);


        /**
         * init room notification
         * */
        FontIconTextView mute = new FontIconTextView(G.context);
        mute.setId(R.id.iv_chatCell_mute);
        mute.setText("K");
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


        /**
         * force gravity in message preview
         * */
        if (isRtl) {
            firstTextView.setGravity(Gravity.RIGHT);
            secondTextView.setGravity(Gravity.RIGHT);
            thirdTextView.setGravity(Gravity.RIGHT);
        } else {
            firstTextView.setGravity(Gravity.LEFT);
            secondTextView.setGravity(Gravity.LEFT);
            thirdTextView.setGravity(Gravity.LEFT);
        }

        /**
         * set views dependency
         * */

        set.constrainHeight(cellCheckbox.getId(), ConstraintSet.WRAP_CONTENT);
        set.constrainWidth(cellCheckbox.getId(), ConstraintSet.WRAP_CONTENT);

        set.connect(cellCheckbox.getId(), ConstraintSet.TOP, avatarImageView.getId(), ConstraintSet.TOP);
        set.connect(cellCheckbox.getId(), ConstraintSet.BOTTOM, avatarImageView.getId(), ConstraintSet.BOTTOM);

        set.constrainHeight(avatarImageView.getId(), i_Dp(R.dimen.dp60));
        set.constrainWidth(avatarImageView.getId(), i_Dp(R.dimen.dp60));

        set.connect(avatarImageView.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        set.connect(avatarImageView.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        set.connect(avatarImageView.getId(), ConstraintSet.START, cellCheckbox.getId(), ConstraintSet.END);

        set.constrainHeight(chatIcon.getId(), i_Dp(R.dimen.dp18));
        set.constrainWidth(chatIcon.getId(), i_Dp(R.dimen.dp18));

        set.constrainHeight(roomName.getId(), ConstraintSet.WRAP_CONTENT);
        set.constrainWidth(roomName.getId(), ConstraintSet.WRAP_CONTENT);

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


        set.constrainHeight(firstTextView.getId(), ConstraintSet.WRAP_CONTENT);
        set.constrainWidth(firstTextView.getId(), ConstraintSet.WRAP_CONTENT);

        set.constrainHeight(secondTextView.getId(), ConstraintSet.WRAP_CONTENT);
        set.constrainWidth(secondTextView.getId(), ConstraintSet.MATCH_CONSTRAINT);

        set.constrainHeight(thirdTextView.getId(), ConstraintSet.WRAP_CONTENT);
        set.constrainWidth(thirdTextView.getId(), ConstraintSet.MATCH_CONSTRAINT);

        set.constrainHeight(messageStatus.getId(), i_Dp(R.dimen.dp24));
        set.constrainWidth(messageStatus.getId(), i_Dp(R.dimen.dp24));

        set.connect(firstTextView.getId(), ConstraintSet.TOP, messageStatus.getId(), ConstraintSet.TOP);
        set.connect(firstTextView.getId(), ConstraintSet.BOTTOM, messageStatus.getId(), ConstraintSet.BOTTOM);

        set.connect(thirdTextView.getId(), ConstraintSet.TOP, messageStatus.getId(), ConstraintSet.TOP);
        set.connect(thirdTextView.getId(), ConstraintSet.BOTTOM, messageStatus.getId(), ConstraintSet.BOTTOM);

        set.connect(secondTextView.getId(), ConstraintSet.TOP, messageStatus.getId(), ConstraintSet.TOP);
        set.connect(secondTextView.getId(), ConstraintSet.BOTTOM, messageStatus.getId(), ConstraintSet.BOTTOM);

        set.constrainHeight(bottomView.getId(), i_Dp(R.dimen.dp1));
        set.constrainWidth(bottomView.getId(), ConstraintSet.MATCH_CONSTRAINT);


        if (isRtl) {
            set.connect(chatIcon.getId(), ConstraintSet.RIGHT, avatarImageView.getId(), ConstraintSet.LEFT);
            set.connect(roomName.getId(), ConstraintSet.RIGHT, chatIcon.getId(), ConstraintSet.LEFT);
            set.connect(verify.getId(), ConstraintSet.RIGHT, roomName.getId(), ConstraintSet.LEFT, i_Dp(R.dimen.dp4));
            set.connect(messageData.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, i_Dp(R.dimen.dp16));

            set.connect(badgeView.getId(), ConstraintSet.LEFT, messageStatus.getId(), ConstraintSet.LEFT, i_Dp(R.dimen.dp4));
            set.connect(badgeView.getId(), ConstraintSet.RIGHT, thirdTextView.getId(), ConstraintSet.LEFT, i_Dp(R.dimen.dp4));
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

            int[] checkBoxChain = {cellCheckbox.getId(), avatarImageView.getId()};
            float[] checkBoxWeight = {0, 0};
            set.createHorizontalChainRtl(ConstraintSet.PARENT_ID, ConstraintSet.START, firstTextView.getId(), ConstraintSet.END,
                    checkBoxChain, checkBoxWeight, ConstraintSet.CHAIN_PACKED);


            int[] chainViews = {firstTextView.getId(), secondTextView.getId(), thirdTextView.getId()};
            float[] chainWeights = {0, 0, 1};
            set.createHorizontalChainRtl(avatarImageView.getId(), ConstraintSet.END, messageStatus.getId(), ConstraintSet.START,
                    chainViews, chainWeights, ConstraintSet.CHAIN_PACKED);

        } else {
            set.connect(chatIcon.getId(), ConstraintSet.LEFT, avatarImageView.getId(), ConstraintSet.RIGHT);
            set.connect(roomName.getId(), ConstraintSet.LEFT, chatIcon.getId(), ConstraintSet.RIGHT);
            set.connect(verify.getId(), ConstraintSet.LEFT, roomName.getId(), ConstraintSet.RIGHT, i_Dp(R.dimen.dp4));
            set.connect(messageData.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, i_Dp(R.dimen.dp16));

            set.connect(badgeView.getId(), ConstraintSet.LEFT, thirdTextView.getId(), ConstraintSet.RIGHT, i_Dp(R.dimen.dp4));
            set.connect(badgeView.getId(), ConstraintSet.RIGHT, messageStatus.getId(), ConstraintSet.RIGHT, i_Dp(R.dimen.dp4));
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


            int[] checkBoxChain = {cellCheckbox.getId(), avatarImageView.getId()};
            float[] checkBoxWeight = {0, 0};
            set.createHorizontalChain(ConstraintSet.PARENT_ID, ConstraintSet.LEFT, firstTextView.getId(), ConstraintSet.LEFT,
                    checkBoxChain, checkBoxWeight, ConstraintSet.CHAIN_PACKED);

            int[] chainViews = {firstTextView.getId(), secondTextView.getId(), thirdTextView.getId()};
            float[] chainWeights = {0, 0, 1};
            set.createHorizontalChain(avatarImageView.getId(), ConstraintSet.RIGHT, messageStatus.getId(), ConstraintSet.LEFT,
                    chainViews, chainWeights, ConstraintSet.CHAIN_PACKED);

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

    private Bitmap loadBitmapAsset(String fileName, Context context) {
        final AssetManager assetManager = context.getAssets();
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(assetManager.open(fileName));
            return BitmapFactory.decodeStream(bis);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bis.close();
            } catch (Exception e) {

            }
        }
        return null;
    }

}
