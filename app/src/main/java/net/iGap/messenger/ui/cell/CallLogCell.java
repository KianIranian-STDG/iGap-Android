package net.iGap.messenger.ui.cell;

import android.content.Context;
import android.graphics.Canvas;
import android.text.format.DateUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.LayoutCreator;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.libs.emojiKeyboard.emoji.EmojiManager;
import net.iGap.messenger.theme.Theme;
import net.iGap.messenger.ui.components.IconView;
import net.iGap.module.CircleImageView;
import net.iGap.proto.ProtoSignalingGetLog;
import net.iGap.proto.ProtoSignalingOffer;
import net.iGap.realm.RealmCallLog;
import net.iGap.realm.RealmRegisteredInfo;

import static net.iGap.helper.HelperCalander.checkHijriAndReturnTime;
import static net.iGap.module.TimeUtils.toLocal;
import static net.iGap.proto.ProtoSignalingOffer.SignalingOffer.Type.VIDEO_CALLING;


public class CallLogCell extends FrameLayout {

    private final CheckBox checkBox;
    private final CircleImageView imageView;
    private final TextView displayNameCall;
    private final TextView offerTimeCall;
    private final IconView iconView;
    private final TextView durationCall;

    private boolean haveCheckBox = false;
    private final boolean isRtl = G.isAppRtl;

    public CallLogCell(Context context) {
        super(context);

        setWillNotDraw(false);

        checkBox = new CheckBox(context);
        addView(checkBox);

        imageView = new CircleImageView(context);
        addView(imageView);

        displayNameCall = new TextView(context);
        displayNameCall.setGravity(isRtl ? Gravity.RIGHT : Gravity.LEFT);
        displayNameCall.setPadding(0, 0, 0, LayoutCreator.dp(1));
        displayNameCall.setText("Name");
        displayNameCall.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        displayNameCall.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        displayNameCall.setSingleLine();
        addView(displayNameCall);

        offerTimeCall = new TextView(context);
        offerTimeCall.setGravity(isRtl ? Gravity.RIGHT : Gravity.LEFT);
        offerTimeCall.setSingleLine();
        offerTimeCall.setText("(4) 9:24 am");
        offerTimeCall.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        offerTimeCall.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        offerTimeCall.setTextColor(Theme.getColor(Theme.key_subtitle_text));
        addView(offerTimeCall);

        iconView = new IconView(context);
        iconView.setText(getResources().getString(R.string.ic_call));
        iconView.setTextColor(getResources().getColor(R.color.dayGreenTheme));
        iconView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        iconView.setGravity(isRtl ? Gravity.LEFT : Gravity.RIGHT);
        iconView.setSingleLine();
        iconView.setTypeface(ResourcesCompat.getFont(context, R.font.font_icon_new));
        addView(iconView);

        durationCall = new TextView(context);
        durationCall.setText("2:24");
        durationCall.setTextColor(Theme.getColor(Theme.key_light_theme_color));
        durationCall.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        durationCall.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        durationCall.setGravity(isRtl ? Gravity.LEFT : Gravity.RIGHT);
        durationCall.setSingleLine();
        addView(durationCall);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        checkBox.measure(MeasureSpec.makeMeasureSpec(LayoutCreator.dp(25), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(25), MeasureSpec.EXACTLY));
        imageView.measure(MeasureSpec.makeMeasureSpec(LayoutCreator.dp(48), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(48), MeasureSpec.EXACTLY));
        displayNameCall.measure(MeasureSpec.makeMeasureSpec((MeasureSpec.getSize(widthMeasureSpec) - checkBox.getMeasuredWidth() - imageView.getMeasuredWidth() - LayoutCreator.dp(16)) / 2, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(52) / 2 - LayoutCreator.dp(8), MeasureSpec.EXACTLY));
        offerTimeCall.measure(MeasureSpec.makeMeasureSpec((MeasureSpec.getSize(widthMeasureSpec) - checkBox.getMeasuredWidth() - imageView.getMeasuredWidth() - LayoutCreator.dp(16)) / 2, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(52) / 2 - LayoutCreator.dp(8), MeasureSpec.EXACTLY));
        iconView.measure(MeasureSpec.makeMeasureSpec((MeasureSpec.getSize(widthMeasureSpec) - checkBox.getMeasuredWidth() - imageView.getMeasuredWidth() - LayoutCreator.dp(16)) / 2, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(52) / 2 - LayoutCreator.dp(8), MeasureSpec.EXACTLY));
        durationCall.measure(MeasureSpec.makeMeasureSpec((MeasureSpec.getSize(widthMeasureSpec) - checkBox.getMeasuredWidth() - imageView.getMeasuredWidth() - LayoutCreator.dp(16)) / 2, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(52) / 2 - LayoutCreator.dp(8), MeasureSpec.EXACTLY));
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), LayoutCreator.dp(52));
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
        int displayNameCallBottom = displayNameCallTop + displayNameCall.getMeasuredHeight();
        int displayNameCallLeft = isRtl ? imageViewLeft - displayNameCall.getMeasuredWidth() - LayoutCreator.dp(8) : imageViewRight + LayoutCreator.dp(8);
        int displayNameCallRight = isRtl ? imageViewLeft - LayoutCreator.dp(8) : displayNameCallLeft + displayNameCall.getMeasuredWidth();
        displayNameCall.layout(displayNameCallLeft, displayNameCallTop, displayNameCallRight, displayNameCallBottom);

        int offerTimeCallBottom = getMeasuredHeight() - LayoutCreator.dp(8);
        int offerTimeCallTop = offerTimeCallBottom - offerTimeCall.getMeasuredHeight();
        int offerTimeCallLeft = isRtl ? imageViewLeft - offerTimeCall.getMeasuredWidth() - LayoutCreator.dp(8) : imageViewRight + LayoutCreator.dp(8);
        int offerTimeCallRight = isRtl ? imageViewLeft - LayoutCreator.dp(8) : offerTimeCallLeft + offerTimeCall.getMeasuredWidth();
        offerTimeCall.layout(offerTimeCallLeft, offerTimeCallTop, offerTimeCallRight, offerTimeCallBottom);

        int iconViewTop = LayoutCreator.dp(8);
        int iconViewBottom = iconViewTop + iconView.getMeasuredHeight();
        int iconViewLeft = isRtl ? LayoutCreator.dp(8) : getMeasuredWidth() - iconView.getMeasuredWidth() - LayoutCreator.dp(8);
        int iconViewRight = isRtl ? LayoutCreator.dp(8) + iconView.getMeasuredWidth() : getMeasuredWidth() - LayoutCreator.dp(8);
        iconView.layout(iconViewLeft, iconViewTop, iconViewRight, iconViewBottom);

        int durationCallBottom = getMeasuredHeight() - LayoutCreator.dp(8);
        int durationCallTop = durationCallBottom - durationCall.getMeasuredHeight();
        int durationCallLeft = isRtl ? LayoutCreator.dp(8) : getMeasuredWidth() - durationCall.getMeasuredWidth() - LayoutCreator.dp(8);
        int durationCallRight = isRtl ? LayoutCreator.dp(8) + durationCall.getMeasuredWidth() : getMeasuredWidth() - LayoutCreator.dp(8);
        durationCall.layout(durationCallLeft, durationCallTop, durationCallRight, durationCallBottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawLine(isRtl ? 4 : imageView.getRight(), getMeasuredHeight() - 1, isRtl ? imageView.getLeft() : getWidth(), getMeasuredHeight(),Theme.dividerPaint);
        super.onDraw(canvas);
    }

    public void setValue(RealmCallLog callLog, boolean haveCheckBox) {
        this.haveCheckBox = haveCheckBox;

        int duration = callLog.getDuration();
        int offerTime = callLog.getOfferTime();
        String status = callLog.getStatus();
        String type = callLog.getType();
        RealmRegisteredInfo user = callLog.getUser();

        AvatarHandler avatarHandler = new AvatarHandler();
        avatarHandler.getAvatar(new ParamWithAvatarType(imageView, user.getId()).avatarType(AvatarHandler.AvatarType.USER));

        switch (ProtoSignalingGetLog.SignalingGetLogResponse.SignalingLog.Status.forNumber(Integer.parseInt(status))) {
            case OUTGOING:
                iconView.setText(getResources().getString(R.string.ic_outgoing_call));
                iconView.setTextColor(getResources().getColor(R.color.dayGreenTheme));
                durationCall.setTextColor(getResources().getColor(R.color.dayGreenTheme));
                break;
            case MISSED:
                iconView.setText(getResources().getString(R.string.ic_missed_call));
                iconView.setTextColor(getResources().getColor(R.color.dayRedTheme));
                durationCall.setTextColor(getResources().getColor(R.color.dayRedTheme));
                durationCall.setText(getResources().getString(R.string.miss));
                break;
            case CANCELED:
                iconView.setText(getResources().getString(R.string.ic_call));
                iconView.setTextColor(getResources().getColor(R.color.dayGreenTheme));
                durationCall.setTextColor(getResources().getColor(R.color.dayGreenTheme));
                durationCall.setText(getResources().getString(R.string.not_answer));
                break;
            case INCOMING:
                iconView.setText(getResources().getString(R.string.ic_incoming_call));
                iconView.setTextColor(getResources().getColor(R.color.dayBlueTheme));
                durationCall.setTextColor(getResources().getColor(R.color.dayBlueTheme));
                break;
        }

        if (ProtoSignalingOffer.SignalingOffer.Type.valueOf(type) == VIDEO_CALLING) {
            iconView.setText(getResources().getString(R.string.video_call_icon));
        }

        if (duration > 0) {
            durationCall.setText(DateUtils.formatElapsedTime(duration));
        }

        offerTimeCall.setText(String.format("%s %s", checkHijriAndReturnTime(offerTime), toLocal(offerTime * DateUtils.SECOND_IN_MILLIS, G.CHAT_MESSAGE_TIME)));

        if (HelperCalander.isPersianUnicode) {
            offerTimeCall.setText(HelperCalander.convertToUnicodeFarsiNumber(offerTimeCall.getText().toString()));
            durationCall.setText(HelperCalander.convertToUnicodeFarsiNumber(durationCall.getText().toString()));
        }

        displayNameCall.setText(EmojiManager.getInstance().replaceEmoji(user.getDisplayName(), displayNameCall.getPaint().getFontMetricsInt()).toString());
    }

    public void setTextColor(int color) {
        displayNameCall.setTextColor(color);
        invalidate();
    }
}
