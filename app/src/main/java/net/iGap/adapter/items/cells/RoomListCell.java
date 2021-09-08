package net.iGap.adapter.items.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.chat.AbstractMessage;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperImageBackColor;
import net.iGap.helper.HelperLogMessage;
import net.iGap.helper.LayoutCreator;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithInitBitmap;
import net.iGap.libs.Tuple;
import net.iGap.libs.emojiKeyboard.emoji.EmojiManager;
import net.iGap.messenger.ui.components.IconView;
import net.iGap.module.AppUtils;
import net.iGap.module.CircleImageView;
import net.iGap.module.FontIconTextView;
import net.iGap.module.Theme;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.customView.CheckBox;
import net.iGap.module.customView.TextBadge;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomMessage;

import org.w3c.dom.Text;

import java.util.ArrayList;

import static android.view.View.MeasureSpec.AT_MOST;
import static android.view.View.MeasureSpec.EXACTLY;
import static net.iGap.adapter.items.chat.ViewMaker.setTextSize;
import static net.iGap.adapter.items.chat.ViewMaker.setTypeFace;
import static net.iGap.proto.ProtoGlobal.Room.Type.CHAT;
import static net.iGap.proto.ProtoGlobal.Room.Type.GROUP;
import static net.iGap.proto.ProtoGlobal.RoomMessageWallet.Type.BILL;
import static net.iGap.proto.ProtoGlobal.RoomMessageWallet.Type.CARD_TO_CARD;
import static net.iGap.proto.ProtoGlobal.RoomMessageWallet.Type.MONEY_TRANSFER;
import static net.iGap.proto.ProtoGlobal.RoomMessageWallet.Type.PAYMENT;
import static net.iGap.proto.ProtoGlobal.RoomMessageWallet.Type.TOPUP;

public class RoomListCell extends FrameLayout {

    public final int FILE = 0x1F4CE;
    public final int VIDEO = 0x1F4F9;
    public final int MUSIC = 0x1F3A7;
    public final int IMAGE = 0x1F5BC;
    public final int GIF = 0x1F308;
    public final int WALLET = 0x1F4B3;

    private TextView roomNameTv;
    private FontIconTextView verifyIconTv;
    private CircleImageView avatarImageView;
    private TextView messageDateTv;
    private FontIconTextView muteIconTv;
    private FontIconTextView chatIconTv;
    private AppCompatTextView lastMessageTv;
    private TextBadge badgeView;
    private FontIconTextView statusTv;
    private IconView pinIcon;
    private AppCompatImageView pinView;
    private CheckBox checkBox;

    private boolean haveAvatar = false;
    private boolean isMute = false;
    private boolean haveDate = false;
    private boolean haveName = false;
    private boolean roomVerified = false;
    private boolean haveChatIcon = false;
    private boolean isRtl = G.isAppRtl;
    private boolean haveLastMessage = false;
    private boolean haveBadge = false;
    private boolean haveStatus = false;
    private boolean havePin = false;
    private boolean haveCheckBox = false;

    public RoomListCell(@NonNull Context context) {
        super(context);
        setBackground(Theme.getSelectorDrawable(Theme.getInstance().getDividerColor(context)));
    }

    public void setData(RealmRoom room, AvatarHandler avatarHandler, boolean isSelectedMode) {
        if (room.isPinned()) {
            if (!havePin) {
                pinIcon = new IconView(getContext());
                pinIcon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                pinIcon.setIconColor(Theme.getInstance().getSubTitleColor(getContext()));
                pinIcon.setIcon(R.string.icon_pin_to_top2);
                pinView = new AppCompatImageView(getContext());
                pinView.setBackgroundResource(R.drawable.pin);
                pinView.setAlpha(G.themeColor == Theme.DARK ? 0.2f : 0.6f);
                addView(pinView, 0);
                addView(pinIcon, 1);
                havePin = true;
            }
        } else if (havePin) {
            removeView(pinIcon);
            removeView(pinView);
            havePin = false;
        }

        if (!haveAvatar) {
            avatarImageView = new CircleImageView(getContext());
            addView(avatarImageView);
            haveAvatar = true;
            final boolean isMyCloud;

            isMyCloud = room.getChatRoom() != null && room.getChatRoom().getPeerId() > 0 && room.getChatRoom().getPeerId() == AccountManager.getInstance().getCurrentUser().getId();

            if (isMyCloud) {
                avatarHandler.removeImageViewFromHandler(avatarImageView);
                avatarImageView.setImageResource(R.drawable.ic_cloud_space_blue);

            } else {
                avatarHandler(room, avatarHandler);
            }
        } else {
            final boolean isMyCloud;

            isMyCloud = room.getChatRoom() != null && room.getChatRoom().getPeerId() > 0 && room.getChatRoom().getPeerId() == AccountManager.getInstance().getCurrentUser().getId();

            if (isMyCloud) {
                avatarHandler.removeImageViewFromHandler(avatarImageView);
                avatarImageView.setImageResource(R.drawable.ic_cloud_space_blue);

            } else {
                avatarHandler(room, avatarHandler);
            }
        }

        if (haveName) {
            roomNameTv.setText(EmojiManager.getInstance().replaceEmoji(room.getTitle(), roomNameTv.getPaint().getFontMetricsInt(), -1, false));
        }

        if (room.getType() == ProtoGlobal.Room.Type.CHANNEL && room.getChannelRoom().isVerified() || room.getType() == CHAT && room.getChatRoom().isVerified()) {
            if (!roomVerified) {
                verifyIconTv = new FontIconTextView(getContext());
                verifyIconTv.setTextColor(getContext().getResources().getColor(R.color.verify_color));
                verifyIconTv.setText(R.string.icon_blue_badge);
                setTextSize(verifyIconTv, R.dimen.standardTextSize);
                addView(verifyIconTv);
                roomVerified = true;
            }
        } else if (roomVerified) {
            removeView(verifyIconTv);
            roomVerified = false;
        }

        if (room.getLastMessage() != null && room.getLastMessage().getUpdateOrCreateTime() != 0 && !haveDate) {
            messageDateTv = new AppCompatTextView(getContext());
            messageDateTv.setSingleLine(true);
            messageDateTv.setTextColor(Theme.getInstance().getSendMessageTextColor(messageDateTv.getContext()));
            messageDateTv.setText(HelperCalander.getTimeForMainRoom(room.getLastMessage().getUpdateOrCreateTime()));
            setTextSize(messageDateTv, R.dimen.smallTextSize);
            setTypeFace(messageDateTv);
            addView(messageDateTv);
            haveDate = true;

        } else if (room.getLastMessage() != null && room.getLastMessage().getUpdateOrCreateTime() != 0) {
            messageDateTv.setText(HelperCalander.getTimeForMainRoom(room.getLastMessage().getUpdateOrCreateTime()));
        }

        if (room.getMute()) {
            if (!isMute) {
                muteIconTv = new FontIconTextView(getContext());
                muteIconTv.setText(R.string.icon_mute);
                muteIconTv.setGravity(Gravity.RIGHT);
                muteIconTv.setTextColor(Theme.getInstance().getSendMessageTextColor(getContext()));
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
                setTextSize(chatIconTv, R.dimen.dp14);
                addView(chatIconTv);
                chatIconTv.setTextColor(Theme.getInstance().getSendMessageTextColor(chatIconTv.getContext()));
                haveChatIcon = true;

            }

            if (room.getType() == ProtoGlobal.Room.Type.CHANNEL)
                chatIconTv.setText(R.string.icon_channel);
            else if (room.getType() == ProtoGlobal.Room.Type.GROUP)
                chatIconTv.setText(R.string.icon_contacts);

        } else if (haveChatIcon) {
            removeView(chatIconTv);
            haveChatIcon = false;
        }

        if (room.getTitle() != null && !haveName) {
            roomNameTv = new TextView(getContext());
            roomNameTv.setTypeface(ResourcesCompat.getFont(getContext(), R.font.main_font_bold));
            setTextSize(roomNameTv, R.dimen.dp15);
            roomNameTv.setSingleLine(true);
            roomNameTv.setEllipsize(TextUtils.TruncateAt.END);
            roomNameTv.setText(EmojiManager.getInstance().replaceEmoji(room.getTitle(), roomNameTv.getPaint().getFontMetricsInt(), -1, false));
            roomNameTv.setTextColor(Theme.getInstance().getSendMessageTextColor(roomNameTv.getContext()));
            roomNameTv.setGravity(isRtl ? Gravity.RIGHT : Gravity.LEFT | Gravity.CENTER_VERTICAL);
            addView(roomNameTv);
            haveName = true;
        }

        if (room.getLastMessage() != null) {
            if (!haveLastMessage) {
                lastMessageTv = new AppCompatTextView(getContext());
                lastMessageTv.setEllipsize(TextUtils.TruncateAt.END);
                lastMessageTv.setGravity(isRtl ? Gravity.RIGHT : Gravity.LEFT | Gravity.CENTER_VERTICAL);
                lastMessageTv.setSingleLine(true);
                setTypeFace(lastMessageTv);
                setTextSize(lastMessageTv, R.dimen.dp13);
                addView(lastMessageTv);
                haveLastMessage = true;
            }
            getLastMessage(room, lastMessageTv);
        } else if (haveLastMessage) {
            removeView(lastMessageTv);
            haveLastMessage = false;
        }

        if (!(room.getUnreadCount() < 1)) {
            if (!haveBadge) {
                badgeView = new TextBadge(getContext());
                setTypeFace(badgeView.getTextView());
                addView(badgeView);
                haveBadge = true;
            }
            if (room.getMute()) {
                badgeView.setBadgeColor(getResources().getColor(R.color.gray_9d));
            } else {
                badgeView.setBadgeColor(Theme.getInstance().getAccentColor(badgeView.getContext()));
            }
            badgeView.setText(getUnreadCount(room.getUnreadCount()));
            if (havePin) {
                removeView(pinIcon);
            }
        } else if (haveBadge) {
            removeView(badgeView);
            if (havePin) {
                removeView(pinIcon);
                addView(pinIcon);
            }
            haveBadge = false;
        }

        if (haveLastMessage && room.getLastMessage().isAuthorMe()) {
            if (!haveStatus) {
                statusTv = new FontIconTextView(getContext());
                setTextSize(statusTv, R.dimen.xlargeTextSize);
                addView(statusTv);
                haveStatus = true;
            }
            ProtoGlobal.RoomMessageStatus status = ProtoGlobal.RoomMessageStatus.UNRECOGNIZED;
            if (room.getLastMessage().getStatus() != null) {
                try {
                    status = ProtoGlobal.RoomMessageStatus.valueOf(room.getLastMessage().getStatus());
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
            AppUtils.rightMessageStatus(statusTv, status, room.getLastMessage().isAuthorMe());
        } else if (haveStatus) {
            removeView(statusTv);
            haveStatus = false;
        }

        if (isSelectedMode) {
            if (!haveCheckBox) {
                checkBox = new CheckBox(getContext(), R.drawable.round_check);
                checkBox.setVisibility(VISIBLE);
                addView(checkBox);
                haveCheckBox = true;
            }
        } else if (haveCheckBox) {
            removeView(checkBox);
            haveCheckBox = false;
        }
        requestLayout();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        int h2 = getMeasuredHeight() / 2;
        int standardMargin = dpToPx(8);
        int smallMargin = dpToPx(4);
        int paddingEnd = dpToPx(8);

        if (haveAvatar) {
            int avatarHeight = dpToPx(60);
            avatarImageView.measure(makeMeasureSpec(avatarHeight, MeasureSpec.EXACTLY), makeMeasureSpec(avatarHeight, MeasureSpec.EXACTLY));

            int avatarTop = (getMeasuredHeight() - avatarImageView.getMeasuredHeight()) / 2;
            int avatarBottom = avatarTop + avatarImageView.getMeasuredHeight();

            int avatarLeft = isRtl ? getWidth() - avatarHeight : standardMargin;
            int avatarRight = isRtl ? getWidth() - standardMargin : avatarImageView.getMeasuredHeight();

            avatarImageView.layout(avatarLeft, avatarTop, avatarRight, avatarBottom);

            if (haveName) {
                int roomNameHeight = LayoutCreator.getTextHeight(roomNameTv);
                int roomNameWidth = LayoutCreator.getTextWidth(roomNameTv);
                int emojiCount = EmojiManager.getEmojiCount(roomNameTv.getText().toString());
                if (emojiCount > 0) {
                    roomNameWidth = roomNameWidth + LayoutCreator.dp(emojiCount * 2);
                }
                int roomTop = h2 - roomNameHeight;
                int nameLeft = 0;
                int nameRight = 0;

                int finalNameLeft = 0;
                int finalNameRight = 0;

                if (haveChatIcon) {
                    int chatIconWidth = LayoutCreator.getTextWidth(chatIconTv);
                    int chatIconHeight = LayoutCreator.getTextHeight(chatIconTv);

                    int t = (roomNameHeight - chatIconHeight) / 2;
                    int chatIconTop = roomTop + t;

                    int chatIconLeft = isRtl ? avatarLeft - chatIconWidth - standardMargin : avatarRight + standardMargin;
                    int chatIconRight = isRtl ? avatarLeft - standardMargin : avatarRight + chatIconWidth + standardMargin;

                    chatIconTv.measure(makeMeasureSpec(chatIconWidth, AT_MOST), makeMeasureSpec(chatIconWidth, AT_MOST));
                    chatIconTv.layout(chatIconLeft, chatIconTop, chatIconRight, h2 - t);

                    if (isRtl) {
                        nameRight = chatIconLeft - smallMargin;
                        nameLeft = chatIconLeft - roomNameWidth - smallMargin;
                    } else {
                        nameLeft = chatIconRight + smallMargin;
                        nameRight = chatIconRight + roomNameWidth + smallMargin;
                    }
                } else {
                    if (isRtl) {
                        nameRight = avatarLeft - standardMargin;
                        nameLeft = avatarLeft - roomNameWidth - standardMargin;
                    } else {
                        nameLeft = avatarRight + standardMargin;
                        nameRight = avatarRight + roomNameWidth + standardMargin;
                    }
                }
                int dateWidth = 0;
                int dateHeight = 0;
                int dateTop;
                int dateLeft;
                int dateRight;

                if (haveDate) {
                    dateWidth = LayoutCreator.getTextWidth(messageDateTv);
                    dateHeight = LayoutCreator.getTextHeight(messageDateTv);
                    dateTop = h2 - dateHeight;

                    dateRight = isRtl ? dateWidth + paddingEnd : getWidth() - paddingEnd;
                    dateLeft = isRtl ? paddingEnd : getWidth() - dateWidth - paddingEnd;

                    messageDateTv.measure(makeMeasureSpec(dateWidth, AT_MOST), makeMeasureSpec(dateHeight, AT_MOST));
                    messageDateTv.layout(dateLeft, dateTop, dateRight, h2);
                } else {
                    dateTop = h2;
                    dateRight = isRtl ? paddingEnd : getWidth() - paddingEnd;
                    dateLeft = isRtl ? paddingEnd : getWidth() - paddingEnd;
                }

                int muteRight;
                int muteLeft;
                int muteWidth = 0;

                int verifyRight;
                int verifyLeft;
                int verifyWidth = 0;

                if (roomVerified) {

                    verifyWidth = LayoutCreator.getTextWidth(verifyIconTv);
                    int t = (dateHeight - verifyWidth) / 2;
                    if (isMute) {
                        muteWidth = LayoutCreator.getTextWidth(muteIconTv);
                        int muteTop = dateTop + t;

                        finalNameLeft = isRtl ? nameLeft : nameLeft;
                        finalNameRight = isRtl ? nameRight : nameRight;

                        muteRight = isRtl ? finalNameLeft - smallMargin : finalNameRight + muteWidth + smallMargin;
                        muteLeft = isRtl ? finalNameLeft - muteWidth - smallMargin : finalNameRight + smallMargin;

                        muteIconTv.measure(makeMeasureSpec(muteWidth, AT_MOST), makeMeasureSpec(muteWidth, AT_MOST));
                        muteIconTv.layout(muteLeft, muteTop, muteRight, h2 - t);

                        verifyRight = isRtl ? muteLeft - smallMargin : muteRight + verifyWidth + smallMargin;
                        verifyLeft = isRtl ? muteLeft - verifyWidth - smallMargin : muteRight + smallMargin;

                    } else {
                        verifyRight = isRtl ? nameLeft : nameRight + verifyWidth + smallMargin;
                        verifyLeft = isRtl ? nameLeft - verifyWidth - smallMargin : nameRight + smallMargin;

                        finalNameLeft = isRtl ? nameLeft : nameLeft;
                        finalNameRight = isRtl ? nameRight : nameRight;
                    }

                    int verifyTop = dateTop + t;

                    verifyIconTv.measure(makeMeasureSpec(verifyWidth, AT_MOST), makeMeasureSpec(verifyWidth, AT_MOST));
                    verifyIconTv.layout(verifyLeft, verifyTop, verifyRight, h2 - t);
                } else {
                    if (isMute) {
                        muteWidth = LayoutCreator.getTextWidth(muteIconTv);
                        int t = (dateHeight - muteWidth) / 2;
                        int muteTop = dateTop + t;

                        if (isRtl) {
                            if (nameLeft < dateRight + muteWidth) {
                                finalNameLeft = dateRight + muteWidth + standardMargin;
                            } else {
                                finalNameLeft = nameLeft;
                            }
                            finalNameRight = nameRight;

                        } else {
                            finalNameLeft = nameLeft;
                            if (nameRight > dateLeft - muteWidth) {
                                finalNameRight = dateLeft - muteWidth - standardMargin;
                            } else {
                                finalNameRight = nameRight;
                            }
                        }

                        muteRight = isRtl ? finalNameLeft - smallMargin : finalNameRight + muteWidth + smallMargin;
                        muteLeft = isRtl ? finalNameLeft - muteWidth - smallMargin : finalNameRight + smallMargin;

                        muteIconTv.measure(makeMeasureSpec(muteWidth, AT_MOST), makeMeasureSpec(muteWidth, AT_MOST));
                        muteIconTv.layout(muteLeft, muteTop, muteRight, h2 - t);
                    } else {
                        finalNameLeft = isRtl ? dateRight + standardMargin : nameLeft;
                        finalNameRight = isRtl ? nameRight : dateLeft - standardMargin;
                    }
                }


                if (haveStatus) {
                    int statusHeight = LayoutCreator.getTextHeight(statusTv);
                    int statusWidth = LayoutCreator.getTextWidth(statusTv);


                    int statusRight = isRtl ? dateRight + statusWidth + standardMargin : dateLeft - standardMargin;
                    int statusLeft = isRtl ? dateRight + standardMargin : dateLeft - statusWidth - standardMargin;

                    int t = (dateHeight - statusHeight) / 2;
                    statusTv.measure(makeMeasureSpec(statusWidth, AT_MOST), makeMeasureSpec(statusHeight, AT_MOST));
                    statusTv.layout(statusLeft, dateTop + t, statusRight, h2 - t);

                    finalNameLeft = isRtl ? nameLeft : nameLeft;
                    finalNameRight = isRtl ? nameRight : nameRight;

                    int newMuteLeft = 0;
                    int newMuteRight;
                    int newVerifyLeft;
                    int newVerifyRight;
                    if (!isRtl) {
                        if (nameRight > statusLeft) {
                            finalNameRight = statusLeft - statusWidth - muteWidth - verifyWidth;
                            newMuteLeft = finalNameRight;
                            newMuteRight = finalNameRight + muteWidth;
                            if (isMute) {
                                muteIconTv.layout(newMuteLeft, dateTop + t, newMuteRight, h2 - t);
                            }
                            newVerifyLeft = newMuteRight + smallMargin;
                            newVerifyRight = newMuteRight + verifyWidth;
                            if (roomVerified) {
                                verifyIconTv.layout(newVerifyLeft, dateTop + t, newVerifyRight, h2 - t);
                            }
                        }
                    } else {
                        if (nameLeft < statusRight) {
                            finalNameLeft = statusRight + statusWidth + muteWidth + verifyWidth;
                            newMuteRight = finalNameLeft - smallMargin;
                            newMuteLeft = finalNameLeft - muteWidth - smallMargin;
                            if (isMute) {
                                muteIconTv.layout(newMuteLeft, dateTop + t, newMuteRight, h2 - t);
                            }

                            newVerifyRight = newMuteLeft - smallMargin;
                            newVerifyLeft = newMuteLeft - verifyWidth - smallMargin;
                            if (roomVerified) {
                                verifyIconTv.layout(newVerifyLeft, dateTop + t, newVerifyRight, h2 - t);
                            }
                        }
                    }
                }

                roomNameTv.measure(makeMeasureSpec(finalNameRight - finalNameLeft, EXACTLY), makeMeasureSpec(roomNameHeight, AT_MOST));
                roomNameTv.layout(finalNameLeft, roomTop, finalNameRight, h2);
            }

            if (haveLastMessage) {
                int lastMessageHeight = LayoutCreator.getTextHeight(lastMessageTv);

                int messageLeft = isRtl ? paddingEnd : avatarRight + standardMargin;
                int messageRight = isRtl ? avatarLeft - standardMargin : getWidth() - paddingEnd;
                int messageBottom = h2 + lastMessageHeight;

                if (haveBadge) {
                    int badgeHeight = LayoutCreator.getTextHeight(badgeView.getTextView());
                    int badgeWidth = LayoutCreator.getTextWidth(badgeView.getTextView());

                    int badgeRight = isRtl ? paddingEnd + badgeWidth + standardMargin : getWidth() - paddingEnd;
                    int badgeLeft = isRtl ? paddingEnd : getWidth() - badgeWidth - paddingEnd - standardMargin;

                    badgeView.measure(makeMeasureSpec(badgeWidth, AT_MOST), makeMeasureSpec(badgeHeight, AT_MOST));
                    badgeView.layout(badgeLeft, h2 + dpToPx(2), badgeRight, messageBottom - dpToPx(2));
                    if (isRtl) {
                        messageLeft = badgeRight + standardMargin;
                    } else {
                        messageRight = badgeLeft - standardMargin;
                    }

                    lastMessageTv.measure(makeMeasureSpec(messageRight - messageLeft, MeasureSpec.EXACTLY), makeMeasureSpec(lastMessageHeight, AT_MOST));
                    lastMessageTv.layout(messageLeft, h2, messageRight, messageBottom);
                } else if (havePin) {
                    int pinRight = isRtl ? paddingEnd + LayoutCreator.getTextWidth(pinIcon) + standardMargin : getWidth() - paddingEnd;
                    int pinLeft = isRtl ? paddingEnd : getWidth() - LayoutCreator.getTextWidth(pinIcon) - paddingEnd - standardMargin;

                    pinIcon.measure(makeMeasureSpec(getWidth(), AT_MOST), makeMeasureSpec(35, EXACTLY));
                    pinIcon.layout(pinLeft, h2 + dpToPx(4), pinRight, messageBottom);
                    if (isRtl) {
                        messageLeft = pinRight + standardMargin;
                    } else {
                        messageRight = pinLeft - standardMargin;
                    }
                    lastMessageTv.measure(makeMeasureSpec(messageRight - messageLeft, MeasureSpec.EXACTLY), makeMeasureSpec(lastMessageHeight, AT_MOST));
                    lastMessageTv.layout(messageLeft, h2, messageRight, messageBottom);
                } else {
                    lastMessageTv.measure(makeMeasureSpec(messageRight - messageLeft, MeasureSpec.EXACTLY), makeMeasureSpec(lastMessageHeight, AT_MOST));
                    lastMessageTv.layout(messageLeft, h2, messageRight, messageBottom);
                }
            } else {
                if (havePin) {
                    int pinRight = isRtl ? paddingEnd + LayoutCreator.getTextWidth(pinIcon) + standardMargin : getWidth() - paddingEnd;
                    int pinLeft = isRtl ? paddingEnd : getWidth() - LayoutCreator.getTextWidth(pinIcon) - paddingEnd - standardMargin;

                    pinIcon.measure(makeMeasureSpec(getWidth(), AT_MOST), makeMeasureSpec(35, EXACTLY));
                    pinIcon.layout(pinLeft, h2 + dpToPx(4), pinRight, h2 + dpToPx(30));
                }
            }

            if (havePin) {
                pinView.measure(makeMeasureSpec(getWidth(), AT_MOST), makeMeasureSpec(getHeight(), EXACTLY));
                pinView.layout(0, 0, getWidth(), getHeight());
            }

            if (haveCheckBox) {
                checkBox.measure(makeMeasureSpec(dpToPx(20), EXACTLY), makeMeasureSpec(dpToPx(20), EXACTLY));
                checkBox.layout(isRtl ? avatarLeft : avatarRight - dpToPx(20), avatarBottom - dpToPx(20), isRtl ? avatarLeft + dpToPx(20) : avatarRight, avatarBottom);
            }
        }
    }

    public void setCheck(boolean check) {
        if (haveCheckBox)
            checkBox.setChecked(check, true);
    }

    public boolean isChecked() {
        return checkBox.isChecked();
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Theme.getInstance().getDividerColor(getContext()));
        canvas.drawLine(isRtl ? 4 : avatarImageView.getRight(), getMeasuredHeight() - 1, isRtl ? avatarImageView.getLeft() : getWidth(), getMeasuredHeight(), paint);
        super.dispatchDraw(canvas);
    }

    private void avatarHandler(RealmRoom item, AvatarHandler avatarHandler) {
        long idForGetAvatar;
        if (item.getType() == CHAT) {
            idForGetAvatar = item.getChatRoom().getPeerId();
        } else {
            idForGetAvatar = item.getId();
        }

        avatarHandler.getAvatar(new ParamWithInitBitmap(avatarImageView, idForGetAvatar)
                .initBitmap(HelperImageBackColor.drawAlphabetOnPicture((int)
                        getContext().getResources().getDimension(R.dimen.dp52), item.getInitials(), item.getColor())));
    }

    private void getLastMessage(RealmRoom room, AppCompatTextView lastMessageTv) {
        SpannableStringBuilder builder = new SpannableStringBuilder();

        if (room.getActionState() != null && room.getActionStateUserId() != AccountManager.getInstance().getCurrentUser().getId()) {

            SpannableString typingSpannableString = new SpannableString(room.getActionState());
            typingSpannableString.setSpan(new ForegroundColorSpan(Theme.getInstance().getAccentColor(lastMessageTv.getContext())), 0, room.getActionState().length(), 0);

            builder.append(typingSpannableString);

        } else if (room.getDraft() != null && !TextUtils.isEmpty(room.getDraft().getMessage())) {
            String draft = getResources().getString(R.string.txt_draft) + " ";

            SpannableString redSpannable = new SpannableString(draft);
            redSpannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(lastMessageTv.getContext(), R.color.red)), 0, draft.length(), 0);

            String draftMessage = room.getDraft().getMessage();
            SpannableString message = new SpannableString(draftMessage);
            message.setSpan(new ForegroundColorSpan(Theme.getInstance().getSendMessageTextColor(lastMessageTv.getContext())), 0, message.length(), 0);

            builder.append(redSpannable);
            builder.append(message);

        } else {
            boolean haveAttachment = false;
            boolean haveSenderName = false;
            boolean nameIsPersian = false;


            RealmRoomMessage lastMessage;
            if (room.getLastMessage() != null) {
                if (room.getLastMessage().getForwardMessage() != null) {
                    lastMessage = room.getLastMessage().getForwardMessage();
                } else {
                    lastMessage = room.getLastMessage();
                }

                if (lastMessage.isDeleted()) {
                    String deletedMessage = getResources().getString(R.string.deleted_message);
                    SpannableString deletedSpannable = new SpannableString(deletedMessage);
                    deletedSpannable.setSpan(new ForegroundColorSpan(Theme.getInstance().getSendMessageTextColor(lastMessageTv.getContext())), 0, deletedMessage.length(), 0);
                    builder.append(deletedSpannable);
                    lastMessageTv.setText(builder, TextView.BufferType.SPANNABLE);
                    return;
                }

                if (lastMessage.getMessage() != null) {
                    String attachmentTag = null;
                    String senderNameTag = null;
                    SpannableString attachmentSpannable = null;
                    SpannableString senderNameSpannable = null;
                    SpannableString lastMessageSpannable;
                    SpannableString senderNameQuoteSpannable = null;


                    if (room.getType() == GROUP && lastMessage.getMessageType() != ProtoGlobal.RoomMessageType.LOG) {
                        if (lastMessage.isAuthorMe() && room.getLastMessage().getForwardMessage() == null) {
                            senderNameTag = getResources().getString(R.string.txt_you);
                            senderNameSpannable = new SpannableString(senderNameTag);
                        } else {
                            if (room.getLastMessage().isAuthorMe()) {
                                senderNameTag = getResources().getString(R.string.txt_you);
                                senderNameSpannable = new SpannableString(senderNameTag);
                            } else {
                                try {
                                    RealmRegisteredInfo realmRegisteredInfo;
                                    if (room.getLastMessage().getForwardMessage() != null)
                                        realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(room.getRealm(), room.getLastMessage().getUserId());
                                    else
                                        realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(room.getRealm(), lastMessage.getUserId());

                                    if (realmRegisteredInfo != null && realmRegisteredInfo.getDisplayName() != null) {
                                        senderNameTag = realmRegisteredInfo.getDisplayName();
                                        senderNameSpannable = new SpannableString(senderNameTag);
                                        nameIsPersian = Character.getDirectionality(realmRegisteredInfo.getDisplayName().charAt(0)) == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
                                    }
                                } catch (Exception e) {
                                    Log.e(getClass().getName(), "room list last message: ", e);
                                }
                            }
                        }

                        if (senderNameSpannable != null) {
                            haveSenderName = true;
                            senderNameSpannable.setSpan(new ForegroundColorSpan(Theme.getInstance().getAccentColor(lastMessageTv.getContext())), 0, senderNameTag.length(), 0);
                        }
                    }

                    switch (lastMessage.getMessageType()) {
                        case IMAGE_TEXT:
                            attachmentTag = AppUtils.getEmojiByUnicode(IMAGE);
                            attachmentSpannable = new SpannableString(attachmentTag);
                            break;
                        case GIF_TEXT:
                            attachmentTag = AppUtils.getEmojiByUnicode(GIF);
                            attachmentSpannable = new SpannableString(attachmentTag);
                            break;
                        case FILE_TEXT:
                            attachmentTag = AppUtils.getEmojiByUnicode(FILE);
                            attachmentSpannable = new SpannableString(attachmentTag);
                            break;
                        case VIDEO_TEXT:
                            attachmentTag = AppUtils.getEmojiByUnicode(VIDEO);
                            attachmentSpannable = new SpannableString(attachmentTag);
                            break;
                        case AUDIO_TEXT:
                            attachmentTag = AppUtils.getEmojiByUnicode(MUSIC);
                            attachmentSpannable = new SpannableString(attachmentTag);
                            break;
                        case GIF:
                            attachmentTag = getResources().getString(R.string.gif_message);
                            attachmentSpannable = new SpannableString(attachmentTag);
                            break;
                        case VOICE:
                            attachmentTag = getResources().getString(R.string.voice_message);
                            attachmentSpannable = new SpannableString(attachmentTag);
                            break;
                        case LOG:
                            attachmentTag = HelperLogMessage.deserializeLog(lastMessageTv.getContext(), lastMessage.getLogs(), false).toString();
                            attachmentSpannable = new SpannableString(attachmentTag);
                            break;
                        case AUDIO:
                            attachmentTag = AppUtils.getEmojiByUnicode(MUSIC) + lastMessage.getAttachment().getName();
                            attachmentSpannable = new SpannableString(attachmentTag);
                            break;
                        case FILE:
                            attachmentTag = getResources().getString(R.string.file_message);
                            attachmentSpannable = new SpannableString(attachmentTag);
                            break;
                        case IMAGE:
                            attachmentTag = getResources().getString(R.string.image_message);
                            attachmentSpannable = new SpannableString(attachmentTag);
                            break;
                        case VIDEO:
                            attachmentTag = getResources().getString(R.string.video_message);
                            attachmentSpannable = new SpannableString(attachmentTag);
                            break;
                        case WALLET:
                            builder.append(AppUtils.getEmojiByUnicode(WALLET));
                            if (lastMessage.getRoomMessageWallet() != null) {
                                String type = lastMessage.getRoomMessageWallet().getType();
                                if (type.equals(CARD_TO_CARD.toString())) {
                                    attachmentTag = getResources().getString(R.string.card_to_card_message);
                                } else if (type.equals(TOPUP.toString())) {
                                    attachmentTag = getResources().getString(R.string.topUp_message);
                                } else if (type.equals(BILL.toString())) {
                                    attachmentTag = getResources().getString(R.string.bill_message);
                                } else if (type.equals(PAYMENT.toString())) {
                                    attachmentTag = getResources().getString(R.string.payment_message);
                                } else if (type.equals(MONEY_TRANSFER.toString())) {
                                    attachmentTag = getResources().getString(R.string.wallet_message);
                                } else {
                                    attachmentTag = getResources().getString(R.string.unknown_message);
                                }
                            } else
                                attachmentTag = getResources().getString(R.string.wallet_message);

                            attachmentSpannable = new SpannableString(attachmentTag);
                            break;
                        case CONTACT:
                            attachmentTag = getResources().getString(R.string.contact_message);
                            attachmentSpannable = new SpannableString(attachmentTag);
                            break;
                        case STICKER:
                            attachmentTag = getResources().getString(R.string.sticker_message);
                            attachmentSpannable = new SpannableString(attachmentTag);
                            break;
                        case LOCATION:
                            attachmentTag = getResources().getString(R.string.location_message);
                            attachmentSpannable = new SpannableString(attachmentTag);
                            break;
                    }

                    if (attachmentSpannable != null) {
                        haveAttachment = true;
                        attachmentSpannable.setSpan(new ForegroundColorSpan(Theme.getInstance().getAccentColor(lastMessageTv.getContext())), 0, attachmentTag.length(), 0);
                    }

                    if (haveSenderName) {
                        senderNameQuoteSpannable = new SpannableString(haveAttachment ? ":" : nameIsPersian ? ": " : ": ");
                        senderNameQuoteSpannable.setSpan(new ForegroundColorSpan(Theme.getInstance().getAccentColor(lastMessageTv.getContext())), 0, senderNameQuoteSpannable.length(), 0);
                    }
                    String message;
                    if (lastMessage.getMessage().length() > 70) {
                        message = lastMessage.getMessage().substring(0, 70) + "...";
                    } else
                        message = lastMessage.getMessage();

                    ArrayList<Tuple<Integer, Integer>> boldPlaces = AbstractMessage.getBoldPlaces(message);
                    message = AbstractMessage.removeBoldMark(message, boldPlaces);

//                    if (HelperCalander.isPersianUnicode)
//                        message = HelperCalander.convertToUnicodeFarsiNumber(message);

                    lastMessageSpannable = new SpannableString(/*subStringInternal(*/message/*)*/);
                    lastMessageSpannable.setSpan(new ForegroundColorSpan(Theme.getInstance().getSendMessageTextColor(lastMessageTv.getContext())), 0, lastMessageSpannable.length(), 0);

                    if (haveSenderName) {
                        if (haveAttachment) {
                            builder.append(senderNameSpannable).append(senderNameQuoteSpannable).append(attachmentSpannable).append(lastMessageSpannable);
                        } else
                            builder.append(senderNameSpannable).append(senderNameQuoteSpannable).append(lastMessageSpannable);
                    } else {
                        if (haveAttachment) {
                            builder.append(attachmentSpannable).append(lastMessageSpannable);
                        } else
                            builder.append(lastMessageSpannable);
                    }

                }
            }
        }
        lastMessageTv.setText(EmojiManager.getInstance().replaceEmoji(builder, lastMessageTv.getPaint().getFontMetricsInt(), -1, false), TextView.BufferType.SPANNABLE);
    }

    private int dpToPx(int dp) {
        return LayoutCreator.dpToPx(dp);
    }

    private int makeMeasureSpec(int size, int mode) {
        return LayoutCreator.manageSpec(size, mode);
    }

    private String getUnreadCount(int unreadCount) {
        if (unreadCount > 999) {
            if (isRtl)
                return HelperCalander.convertToUnicodeFarsiNumber("+999");
            else
                return "+999";
        } else {
            String s = String.valueOf(unreadCount);
            if (isRtl)
                return HelperCalander.convertToUnicodeFarsiNumber(s);
            else
                return s;

        }
    }

}
