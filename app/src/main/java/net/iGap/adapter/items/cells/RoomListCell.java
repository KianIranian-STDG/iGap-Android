package net.iGap.adapter.items.cells;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.vanniktech.emoji.EmojiTextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperImageBackColor;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithInitBitmap;
import net.iGap.module.CircleImageView;
import net.iGap.module.FontIconTextView;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoom;

import static net.iGap.adapter.items.chat.ViewMaker.i_Dp;
import static net.iGap.adapter.items.chat.ViewMaker.setTextSize;
import static net.iGap.adapter.items.chat.ViewMaker.setTypeFace;
import static net.iGap.proto.ProtoGlobal.Room.Type.CHAT;

public class RoomListCell extends FrameLayout {
    private static final String TAG = "abbasi";
    private EmojiTextView roomNameTv;

    private FontIconTextView verifyIconTv;
    private CircleImageView avatarImageView;
    private TextView lastMessageDate;
    private FontIconTextView muteIconTv;
    private FontIconTextView chatIconTv;

    private boolean haveAvatar = false;
    private boolean isRtl = false;
    private boolean isMute = false;
    private boolean isChat = false;
    private boolean haveDate = false;
    private boolean isSelectedMode = false;
    private boolean haveName = false;
    private boolean chatIsVerified = false;
    private boolean roomVerified = false;
    private boolean isChannel = false;
    private boolean haveChatIcon = false;
    private boolean isGroup = false;
    private boolean isBot = false;
    private boolean isDarkTheme = G.isDarkTheme;
    private RealmRoom room;
    private long roomId;
    private boolean multiSelectMode = false;
    private int topMargin = 15;

    public RoomListCell(@NonNull Context context) {
        super(context);
    }

    public void setRoom(RealmRoom room, AvatarHandler avatarHandler) {
        this.room = room;
        isRtl = G.isAppRtl;

        if (!haveAvatar) {
            avatarImageView = new CircleImageView(getContext());
            avatarImageView.setId(R.id.iv_chatCell_userAvatar);
            avatarHandler(room, avatarHandler);
            addView(avatarImageView);
            haveAvatar = true;
        } else {
            avatarHandler(room, avatarHandler);
        }

        if (haveName) {
            roomNameTv.setText(room.getTitle());
        }

        if (room.getType() == ProtoGlobal.Room.Type.CHANNEL) {
            isChannel = true;
        } else if (room.getType() == ProtoGlobal.Room.Type.GROUP) {
            isGroup = true;
        } else if (room.getType() == ProtoGlobal.Room.Type.CHAT) {
            isChat = true;
        }

        if (room.getType() == ProtoGlobal.Room.Type.CHANNEL && room.getChannelRoom().isVerified() || room.getType() == ProtoGlobal.Room.Type.CHAT && room.getChatRoom().isVerified()) {
            if (!roomVerified) {
                verifyIconTv = new FontIconTextView(getContext());
                verifyIconTv.setTextColor(getContext().getResources().getColor(R.color.verify_color));
                verifyIconTv.setText(R.string.verify_icon);
                setTextSize(verifyIconTv, R.dimen.standardTextSize);
                addView(verifyIconTv);
                roomVerified = true;
            }
        } else if (roomVerified) {
            removeView(verifyIconTv);
            roomVerified = false;
        }

        if (room.getLastMessage() != null && room.getLastMessage().getUpdateOrCreateTime() != 0 && !haveDate) {
            lastMessageDate = new AppCompatTextView(G.context);
            lastMessageDate.setId(R.id.tv_chatCell_messageData);
            lastMessageDate.setSingleLine(true);
            lastMessageDate.setTextColor(Color.parseColor(G.textTitleTheme));
            lastMessageDate.setText(HelperCalander.getTimeForMainRoom(room.getLastMessage().getUpdateOrCreateTime()));
            setTextSize(lastMessageDate, R.dimen.dp10);
            setTypeFace(lastMessageDate);
            addView(lastMessageDate);
            haveDate = true;
        } else if (room.getLastMessage() != null && room.getLastMessage().getUpdateOrCreateTime() != 0) {
            lastMessageDate.setText(HelperCalander.getTimeForMainRoom(room.getLastMessage().getUpdateOrCreateTime()));
        }

        if (room.getMute()) {
            if (!isMute) {
                muteIconTv = new FontIconTextView(getContext());
                muteIconTv.setText(R.string.mute_icon);
                muteIconTv.setGravity(Gravity.RIGHT);
                muteIconTv.setTextColor(Color.parseColor(G.textTitleTheme));
                setTextSize(muteIconTv, R.dimen.dp13);
                addView(muteIconTv);
                isMute = true;
            }
        } else if (isMute) {
            removeView(muteIconTv);
            isMute = false;
        }

        if (room.getType() == ProtoGlobal.Room.Type.CHANNEL || room.getType() == ProtoGlobal.Room.Type.GROUP) {
            if (!haveChatIcon) {
                chatIconTv = new FontIconTextView(getContext());
                setTextSize(chatIconTv, R.dimen.standardTextSize);
                addView(chatIconTv);
                haveChatIcon = true;
            }

            if (room.getType() == ProtoGlobal.Room.Type.CHANNEL)
                chatIconTv.setText(R.string.channel_main_icon);
            else if (room.getType() == ProtoGlobal.Room.Type.GROUP)
                chatIconTv.setText(R.string.group_icon);

        } else if (haveChatIcon) {
            removeView(chatIconTv);
            haveChatIcon = false;
        }

        if (room.getTitle() != null && !haveName) {
            roomNameTv = new EmojiTextView(getContext());
            roomNameTv.setId(R.id.tv_chatCell_roomName);
            setTypeFace(roomNameTv);
            setTextSize(roomNameTv, R.dimen.smallTextSize);
            roomNameTv.setGravity(Gravity.CENTER_VERTICAL);
            roomNameTv.setSingleLine(true);
            roomNameTv.setEllipsize(TextUtils.TruncateAt.END);
            roomNameTv.setText(room.getTitle());
            roomNameTv.setEmojiSize(i_Dp(R.dimen.dp16));
            roomNameTv.setTextColor(isDarkTheme ? getResources().getColor(R.color.white) : G.context.getResources().getColor(R.color.black90));
            addView(roomNameTv);
            haveName = true;
        }

    }

    public RealmRoom getRoom() {
        return room;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(dpToPx(72), MeasureSpec.EXACTLY));
    }

    public void setMultiSelectMode(boolean multiSelectMode) {
        this.multiSelectMode = multiSelectMode;
        requestLayout();
    }

    private void avatarHandler(RealmRoom item, AvatarHandler avatarHandler) {
        long idForGetAvatar;
        if (item.getType() == CHAT) {
            idForGetAvatar = item.getChatRoom().getPeerId();
            isChat = true;
        } else {
            idForGetAvatar = item.getId();
        }

        avatarHandler.getAvatar(new ParamWithInitBitmap(avatarImageView, idForGetAvatar)
                .initBitmap(HelperImageBackColor.drawAlphabetOnPicture((int)
                        getContext().getResources().getDimension(R.dimen.dp52), item.getInitials(), item.getColor())));
    }

    private int dpToPx(int dp) {
        return /*LayoutCreator.dpToPx(dp)*/ dp;
    }
}
