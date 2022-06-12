package net.iGap.messenger.ui.cell;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.theme.Theme;

public class ThemePreviewMessagesCell extends LinearLayout {

    private LinearLayout frameReceived;
    private LinearLayout frameSend;
    private FrameLayout frameReceivedReply;
    private View view;
    private TextView userNameText;
    private TextView replyMessageText;
    private TextView message;
    private TextView senderMessage;
    private TextView seenIcon;
    private TextView senderTime;
    private FrameLayout frameReceivedBottom;
    private TextView edited;
    private TextView receivedTime;

    private Context context;

    public ThemePreviewMessagesCell(Context context) {
        super(context);
        this.context = context;
        setOrientation(LinearLayout.VERTICAL);

        frameReceived = new LinearLayout(context);
        frameReceived.setOrientation(LinearLayout.VERTICAL);
        frameReceived.setPadding(8, 8, 8, 8);
        frameReceived.setBackground(Theme.tintDrawable(context.getResources().getDrawable(R.drawable.chat_item_receive_bg_light), context, Theme.getColor(Theme.key_received_item_background)));
        addView(frameReceived, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, G.isAppRtl ? Gravity.LEFT : Gravity.RIGHT, 16, 8, 16, 8));

        frameReceivedReply = new FrameLayout(context);
        frameReceivedReply.setPadding(8, 8, 8, 8);
        frameReceivedReply.setBackground(Theme.tintDrawable(context.getResources().getDrawable(R.drawable.chat_item_sent_bg_light), context, Theme.getColor(Theme.key_send_item_background)));
        frameReceived.addView(frameReceivedReply, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT));

        view = new View(context);
        frameReceivedReply.addView(view, LayoutCreator.createFrame(3, LayoutCreator.MATCH_PARENT, G.isAppRtl ? Gravity.LEFT : Gravity.RIGHT, G.isAppRtl ? 8 : 0, 8, G.isAppRtl ? 0 : 8, 8));

        userNameText = new TextView(context);
        userNameText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        userNameText.setSingleLine();
        userNameText.setText("Shokoofe Adeli");
        userNameText.setTextColor(Theme.getColor(Theme.key_chat_item_holder));
        userNameText.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        userNameText.setGravity(G.isAppRtl ? Gravity.LEFT : Gravity.RIGHT);
        frameReceivedReply.addView(userNameText, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, G.isAppRtl ? Gravity.LEFT : Gravity.RIGHT, G.isAppRtl ? 16 : 0, 16 / 2, G.isAppRtl ? 0 : 16, 4));

        replyMessageText = new TextView(context);
        replyMessageText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        replyMessageText.setText("What do you think about iGap Messenger?");
        replyMessageText.setSingleLine();
        replyMessageText.setTextColor(Theme.getColor(Theme.key_default_text));
        replyMessageText.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        replyMessageText.setGravity(G.isAppRtl ? Gravity.LEFT : Gravity.RIGHT);
        frameReceivedReply.addView(replyMessageText, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, G.isAppRtl ? Gravity.LEFT : Gravity.RIGHT, G.isAppRtl ? 16 : 0, 16 * 2, G.isAppRtl ? 0 : 16, 4));

        message = new TextView(context);
        message.setSingleLine();
        message.setText("I think it is very user-friendly and safe.");
        message.setTextColor(Theme.getColor(Theme.key_default_text));
        message.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        message.setGravity(G.isAppRtl ? Gravity.LEFT : Gravity.RIGHT);
        frameReceived.addView(message, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, G.isAppRtl ? Gravity.LEFT : Gravity.RIGHT, 8, 8, 8, 8));

        frameReceivedBottom = new FrameLayout(context);
        frameReceivedBottom.setBackground(Theme.tintDrawable(context.getResources().getDrawable(R.drawable.chat_item_receive_bg_light), context, Theme.getColor(Theme.key_received_item_background)));
        frameReceived.addView(frameReceivedBottom, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 35, G.isAppRtl ? Gravity.LEFT : Gravity.RIGHT));

        edited = new TextView(context);
        edited.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        edited.setSingleLine();
        edited.setText("Edited");
        edited.setTextColor(Theme.getColor(Theme.key_chat_item_holder));
        edited.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        edited.setGravity((G.isAppRtl ? Gravity.LEFT : Gravity.RIGHT) | Gravity.CENTER_VERTICAL);
        frameReceivedBottom.addView(edited, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.MATCH_PARENT, G.isAppRtl ? Gravity.LEFT : Gravity.RIGHT));

        receivedTime = new TextView(context);
        receivedTime.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        receivedTime.setSingleLine();
        receivedTime.setText("05:02 PM");
        receivedTime.setTextColor(Theme.getColor(Theme.key_chat_item_holder));
        receivedTime.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        receivedTime.setGravity((G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL);
        frameReceivedBottom.addView(receivedTime, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.MATCH_PARENT, G.isAppRtl ? Gravity.RIGHT : Gravity.END));

        frameSend = new LinearLayout(context);
        frameSend.setOrientation(LinearLayout.VERTICAL);
        frameSend.setPadding(8, 8, 8, 8);
        frameSend.setBackground(Theme.tintDrawable(context.getResources().getDrawable(R.drawable.chat_item_sent_bg_light), context, Theme.getColor(Theme.key_light_theme_color)));
        frameSend.setGravity(Gravity.END);
        addView(frameSend, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT, 16, 8, 16, 8));

        senderMessage = new TextView(context);
        senderMessage.setText(getContext().getResources().getString(R.string.FontSizePreviewReply));
        senderMessage.setTextColor(Theme.getColor(Theme.key_default_text));
        senderMessage.setTypeface(ResourcesCompat.getFont(context, R.font.font_icon_new));
        frameSend.addView(senderMessage, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT, 4, 16, 4, 4));

        FrameLayout frameSenderBottom = new FrameLayout(context);
        frameSend.addView(frameSenderBottom, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 30, G.isAppRtl ? Gravity.LEFT : Gravity.RIGHT, 0, 16, 0, 0));

        senderTime = new TextView(context);
        senderTime.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        senderTime.setSingleLine();
        senderTime.setText("05:03 PM");
        senderTime.setTextColor(Theme.getColor(Theme.key_chat_item_holder));
        senderTime.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        frameSenderBottom.addView(senderTime, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, G.isAppRtl ? Gravity.LEFT : Gravity.RIGHT));

        seenIcon = new TextView(context);
        seenIcon.setTextSize(22);
        seenIcon.setText(getResources().getString(R.string.ic_seen));
        seenIcon.setTypeface(ResourcesCompat.getFont(context, R.font.font_icon_new));
        seenIcon.setTextColor(Theme.getColor(Theme.key_theme_color));
        frameSenderBottom.addView(seenIcon, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT));

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        FrameLayout.LayoutParams layoutParams;
        int seenIconWeight = seenIcon.getWidth();
        int sendTimeWeight = senderTime.getWidth();

        layoutParams = (FrameLayout.LayoutParams) seenIcon.getLayoutParams();
        layoutParams.setMargins(G.isAppRtl ? sendTimeWeight : 0, 0, G.isAppRtl ? 0 : seenIconWeight, 0);
        layoutParams = (FrameLayout.LayoutParams) senderTime.getLayoutParams();
        layoutParams.setMargins(G.isAppRtl ? 0 : seenIconWeight, 0, G.isAppRtl ? seenIconWeight : 0, 0);

        super.onLayout(changed, left, top, right, bottom);
    }

    public void setPreviewValue(int textSize) {
        message.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
        senderMessage.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
    }

    @Override
    public void invalidate() {
        super.invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawLine(G.isAppRtl ? 0 : LayoutCreator.dp(20), getMeasuredHeight() - 1, getMeasuredWidth() - (G.isAppRtl ? LayoutCreator.dp(20) : 0), getMeasuredHeight() - 1,Theme.dividerPaint);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    protected void dispatchSetPressed(boolean pressed) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    public void setColors(int color) {
        frameReceived.setBackground(Theme.tintDrawable(context.getResources().getDrawable(R.drawable.chat_item_receive_bg_light), context, Theme.getColor(Theme.key_light_gray)));
        frameReceivedReply.setBackground(Theme.tintDrawable(context.getResources().getDrawable(R.drawable.received_replay_shape), context, Theme.getColor(Theme.key_light_theme_color)));
        userNameText.setTextColor(Theme.getColor(Theme.key_title_text));
        replyMessageText.setTextColor(Theme.getColor(Theme.key_default_text));
        message.setTextColor(Theme.getColor(Theme.key_default_text));
        frameReceivedBottom.setBackground(Theme.tintDrawable(context.getResources().getDrawable(R.drawable.chat_item_receive_bg_light), context, Theme.getColor(Theme.key_light_gray)));
        edited.setTextColor(Theme.getColor(Theme.key_light_theme_color));
        receivedTime.setTextColor(Theme.getColor(Theme.key_theme_color));
        frameSend.setBackground(Theme.tintDrawable(context.getResources().getDrawable(R.drawable.chat_item_sent_bg_light), context, Theme.getColor(Theme.key_light_theme_color)));
        senderMessage.setTextColor(Theme.getColor(Theme.key_default_text));
        senderTime.setTextColor(Theme.getColor(Theme.key_theme_color));
        seenIcon.setTextColor(Theme.getColor(Theme.key_theme_color));
    }

}
