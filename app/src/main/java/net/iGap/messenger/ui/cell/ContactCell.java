package net.iGap.messenger.ui.cell;

import android.content.Context;
import android.graphics.Canvas;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.ContactFragment;
import net.iGap.helper.LayoutCreator;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.libs.emojiKeyboard.emoji.EmojiManager;
import net.iGap.messenger.theme.Theme;
import net.iGap.messenger.ui.components.IconView;
import net.iGap.module.AppUtils;
import net.iGap.module.CircleImageView;
import net.iGap.module.LastSeenTimeUtil;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmContacts;

public class ContactCell extends FrameLayout {

    private final CheckBox checkBox;
    private final CircleImageView imageView;
    private final TextView contactName;
    private final TextView textView;
    private final IconView iconView;
    private final boolean isRtl = G.isAppRtl;
    private final String contactType;
    private boolean haveCheckBox;

    public ContactCell(Context context, String contactType) {
        super(context);
        this.contactType = contactType;
        setWillNotDraw(false);

        checkBox = new CheckBox(context);
        checkBox.setButtonDrawable(Theme.tintDrawable(ContextCompat.getDrawable(getContext(), R.drawable.check_box_background), getContext(), Theme.getColor(Theme.key_theme_color)));
        addView(checkBox);

        imageView = new CircleImageView(context);
        addView(imageView);

        contactName = new TextView(context);
        contactName.setGravity(isRtl ? Gravity.RIGHT : Gravity.LEFT);
        contactName.setPadding(0, 0, 0, LayoutCreator.dp(1));
        contactName.setText("Name");
        contactName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        contactName.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        contactName.setTextColor(Theme.getColor(Theme.key_default_text));
        contactName.setSingleLine();
        addView(contactName);

        textView = new TextView(context);
        textView.setGravity(isRtl ? Gravity.RIGHT : Gravity.LEFT);
        textView.setSingleLine();
        textView.setText("(4) 9:24 am");
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        textView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        textView.setTextColor(Theme.getColor(Theme.key_subtitle_text));
        addView(textView);

        iconView = new IconView(context);
        iconView.setText(getResources().getString(R.string.ic_call));
        iconView.setTextColor(Theme.getColor(Theme.key_subtitle_text));
        iconView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
        iconView.setGravity(isRtl ? Gravity.LEFT : Gravity.RIGHT);
        iconView.setSingleLine();
        iconView.setTypeface(ResourcesCompat.getFont(context, R.font.font_icon_new));
        addView(iconView);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        checkBox.measure(MeasureSpec.makeMeasureSpec(LayoutCreator.dp(25), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(25), MeasureSpec.EXACTLY));
        imageView.measure(MeasureSpec.makeMeasureSpec(LayoutCreator.dp(48), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(48), MeasureSpec.EXACTLY));
        contactName.measure(MeasureSpec.makeMeasureSpec((MeasureSpec.getSize(widthMeasureSpec) - checkBox.getMeasuredWidth() - imageView.getMeasuredWidth() - iconView.getMeasuredWidth() - LayoutCreator.dp(56)), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(58) / 2 - LayoutCreator.dp(8), MeasureSpec.EXACTLY));
        textView.measure(MeasureSpec.makeMeasureSpec((MeasureSpec.getSize(widthMeasureSpec) - checkBox.getMeasuredWidth() - imageView.getMeasuredWidth() - iconView.getMeasuredWidth() - LayoutCreator.dp(56)), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(58) / 2 - LayoutCreator.dp(8), MeasureSpec.EXACTLY));
        iconView.measure(MeasureSpec.makeMeasureSpec(LayoutCreator.dp(25), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(25), MeasureSpec.EXACTLY));
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), LayoutCreator.dp(58));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        int imageViewTop;
        int imageViewBottom;
        int imageViewLeft;
        int imageViewRight;

        if (haveCheckBox) {
            checkBox.setAlpha(1f);
            int checkBoxTop = (getMeasuredHeight() - checkBox.getMeasuredHeight()) / 2;
            int checkBoxBottom = checkBoxTop + checkBox.getMeasuredHeight();
            int checkBoxLeft = isRtl ? getMeasuredWidth() - checkBox.getMeasuredWidth() - LayoutCreator.dp(8) : LayoutCreator.dp(8);
            int checkBoxRight = isRtl ? getMeasuredWidth() - LayoutCreator.dp(8) : LayoutCreator.dp(8) + checkBox.getMeasuredWidth();
            checkBox.layout(checkBoxLeft, checkBoxTop, checkBoxRight, checkBoxBottom);

            imageViewTop = (getMeasuredHeight() - imageView.getMeasuredHeight()) / 2;
            imageViewBottom = imageViewTop + imageView.getMeasuredHeight();
            imageViewLeft = isRtl ? checkBoxLeft - imageView.getMeasuredWidth() - LayoutCreator.dp(8) : checkBoxRight + LayoutCreator.dp(8);
            imageViewRight = isRtl ? checkBoxLeft - LayoutCreator.dp(8) : imageViewLeft + imageView.getMeasuredWidth();
        } else {
            checkBox.setAlpha(0f);
            imageViewTop = (getMeasuredHeight() - imageView.getMeasuredHeight()) / 2;
            imageViewBottom = imageViewTop + imageView.getMeasuredHeight();
            imageViewLeft = isRtl ? getMeasuredWidth() - imageView.getMeasuredWidth() - LayoutCreator.dp(8) : LayoutCreator.dp(8);
            imageViewRight = isRtl ? getMeasuredWidth() - LayoutCreator.dp(8) : LayoutCreator.dp(8) + imageView.getMeasuredWidth();
        }

        imageView.layout(imageViewLeft, imageViewTop, imageViewRight, imageViewBottom);

        int displayNameCallTop = LayoutCreator.dp(8);
        int displayNameCallBottom = displayNameCallTop + contactName.getMeasuredHeight();
        int displayNameCallLeft = isRtl ? imageViewLeft - contactName.getMeasuredWidth() - LayoutCreator.dp(8) : imageViewRight + LayoutCreator.dp(8);
        int displayNameCallRight = isRtl ? imageViewLeft - LayoutCreator.dp(8) : displayNameCallLeft + contactName.getMeasuredWidth();
        contactName.layout(displayNameCallLeft, displayNameCallTop, displayNameCallRight, displayNameCallBottom);

        int offerTimeCallBottom = getMeasuredHeight() - LayoutCreator.dp(8);
        int offerTimeCallTop = offerTimeCallBottom - textView.getMeasuredHeight();
        int offerTimeCallLeft = isRtl ? imageViewLeft - textView.getMeasuredWidth() - LayoutCreator.dp(8) : imageViewRight + LayoutCreator.dp(8);
        int offerTimeCallRight = isRtl ? imageViewLeft - LayoutCreator.dp(8) : offerTimeCallLeft + textView.getMeasuredWidth();
        textView.layout(offerTimeCallLeft, offerTimeCallTop, offerTimeCallRight, offerTimeCallBottom);

        int iconViewTop = (getMeasuredHeight() - iconView.getMeasuredHeight()) / 2;
        int iconViewBottom = iconViewTop + iconView.getMeasuredHeight();
        int iconViewLeft = isRtl ? LayoutCreator.dp(24) : getMeasuredWidth() - iconView.getMeasuredWidth() - LayoutCreator.dp(24);
        int iconViewRight = isRtl ? LayoutCreator.dp(24) + iconView.getMeasuredWidth() : getMeasuredWidth() - LayoutCreator.dp(24);
        iconView.layout(iconViewLeft, iconViewTop, iconViewRight, iconViewBottom);

        if (contactType.equals(ContactFragment.CALL))
            iconView.setAlpha(1f);
        else
            iconView.setAlpha(0f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawLine(isRtl ? 4 : imageView.getRight(), getMeasuredHeight() - 1, isRtl ? imageView.getLeft() : getWidth(), getMeasuredHeight(), Theme.dividerPaint);
        super.onDraw(canvas);
    }

    public void setCheck() {
        checkBox.setChecked(!checkBox.isChecked());
    }

    public void setValue(RealmContacts contacts) {
        contactName.setText(EmojiManager.getInstance().replaceEmoji(contacts.getDisplay_name(), contactName.getPaint().getFontMetricsInt()));
        if (contactType.equals(ContactFragment.CALL))
            textView.setText(String.format(G.isAppRtl ? "%d+" : "+%d", contacts.getPhone()));
        else
            textView.setText(setUserStatus(getContext(), contacts.getStatus() == null ? null : AppUtils.getStatsForUser(contacts.getStatus()), contacts.getId(), contacts.getLast_seen()));
        AvatarHandler avatarHandler = new AvatarHandler();
        avatarHandler.getAvatar(new ParamWithAvatarType(imageView, contacts.getId()).avatarType(AvatarHandler.AvatarType.USER));
    }

    private String setUserStatus(Context context, String userStatus, long userId, long time) {
        if (userStatus != null) {
            if (userStatus.equals(ProtoGlobal.RegisteredUser.Status.EXACTLY.toString())) {
                return LastSeenTimeUtil.computeTime(context, userId, time, false);
            } else {
                return userStatus;
            }
        } else {
            return LastSeenTimeUtil.computeTime(context, userId, time, false);
        }
    }

    public void setMultiSelect(boolean isMultiSelect) {
        haveCheckBox = isMultiSelect;
    }
}

