package net.iGap.adapter.items.chat;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.Theme;
import net.iGap.helper.HelperCalander;
import net.iGap.messageprogress.MessageProgress;
import net.iGap.module.CircleImageView;
import net.iGap.module.EmojiTextViewE;
import net.iGap.module.MaterialDesignTextView;

import static android.view.Gravity.CENTER;
import static android.view.Gravity.LEFT;
import static android.widget.LinearLayout.HORIZONTAL;
import static android.widget.LinearLayout.VERTICAL;
import static net.iGap.G.context;
import static net.iGap.G.isDarkTheme;
import static net.iGap.R.dimen.dp4;
import static net.iGap.R.dimen.dp8;
import static net.iGap.R.dimen.messageContainerPadding;

public class ViewMaker {

    public static View getUnreadMessageItemView(Context context) {
        TextView cslum_txt_unread_message = new AppCompatTextView(context);
        cslum_txt_unread_message.setId(R.id.cslum_txt_unread_message);
        cslum_txt_unread_message.setPadding(0, dpToPixel(2), 0, dpToPixel(2));
        /*cslum_txt_unread_message.setBackgroundColor(G.context.getResources().getColor(R.color.green));*/
        setTextSize(cslum_txt_unread_message, R.dimen.dp12);
        /*setTypeFace(cslum_txt_unread_message);*/
        cslum_txt_unread_message.setGravity(CENTER);
        cslum_txt_unread_message.setText(G.fragmentActivity.getResources().getString(R.string.unread_message));
        cslum_txt_unread_message.setTextColor(ContextCompat.getColor(context, R.color.white));
        LinearLayout.LayoutParams layout_692 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_692.setMargins(i_Dp(R.dimen.dp8), i_Dp(R.dimen.dp8), i_Dp(R.dimen.dp8), i_Dp(R.dimen.dp8));
        cslum_txt_unread_message.setLayoutParams(layout_692);

        return cslum_txt_unread_message;
    }

    public static View getProgressWaitingItemView() {
        ProgressBar cslp_progress_bar_waiting = new ProgressBar(G.context);
        cslp_progress_bar_waiting.setId(R.id.cslp_progress_bar_waiting);
        cslp_progress_bar_waiting.setPadding(i_Dp(R.dimen.dp4), i_Dp(R.dimen.dp4), i_Dp(R.dimen.dp4), i_Dp(R.dimen.dp4));
        cslp_progress_bar_waiting.setVisibility(View.VISIBLE);
        FrameLayout.LayoutParams layout_842 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_842.gravity = Gravity.CENTER;
        cslp_progress_bar_waiting.setIndeterminate(true);
        cslp_progress_bar_waiting.setLayoutParams(layout_842);

        return cslp_progress_bar_waiting;
    }

    public static View getLogItemView() {
        TextView text = new AppCompatTextView(G.context);
        text.setId(R.id.csll_txt_log_text);

        text.setPadding(i_Dp(R.dimen.dp24), i_Dp(R.dimen.dp4), i_Dp(R.dimen.dp24), i_Dp(R.dimen.dp4));
        text.setGravity(CENTER);
        text.setText("Log");
        if (isDarkTheme) {
            text.setTextColor(Color.parseColor(G.textBubble));
            text.setBackgroundResource(R.drawable.recangle_gray_tranceparent_dark);
        } else {
            text.setTextColor(Color.parseColor("#4a5d5c"));
            text.setBackgroundResource(R.drawable.recangle_gray_tranceparent);
        }

        setTextSize(text, R.dimen.dp12);
        setTypeFace(text);
        text.setAllCaps(false);
        FrameLayout.LayoutParams layout_138 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_138.gravity = Gravity.CENTER_HORIZONTAL;
        text.setLayoutParams(layout_138);

        return text;
    }

    static View getViewReplay(Context context) {
        LinearLayout cslr_replay_layout = new LinearLayout(context);
        cslr_replay_layout.setId(R.id.cslr_replay_layout);
        /*cslr_replay_layout.setBackgroundColor(context.getResources().getColor(R.color.messageBox_replyBoxBackgroundSend));*/
        cslr_replay_layout.setClickable(true);
        cslr_replay_layout.setOrientation(HORIZONTAL);
        cslr_replay_layout.setPadding(i_Dp(R.dimen.messageContainerPaddingLeftRight), i_Dp(R.dimen.messageContainerPaddingLeftRight), i_Dp(R.dimen.messageContainerPaddingLeftRight), i_Dp(R.dimen.messageContainerPaddingLeftRight));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            cslr_replay_layout.setTextDirection(View.TEXT_DIRECTION_LOCALE);
        }

        setLayoutDirection(cslr_replay_layout, View.LAYOUT_DIRECTION_LOCALE);

        LinearLayout.LayoutParams layout_468 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cslr_replay_layout.setLayoutParams(layout_468);

        View verticalLine = new View(context);
        verticalLine.setId(R.id.verticalLine);
        verticalLine.setBackgroundColor(new Theme().getMessageVerticalLineColor(verticalLine.getContext()));
        LinearLayout.LayoutParams layout_81 = new LinearLayout.LayoutParams(dpToPixel(3), ViewGroup.LayoutParams.MATCH_PARENT);

        if (HelperCalander.isPersianUnicode) {
            layout_81.leftMargin = i_Dp(dp8);
        } else {
            layout_81.rightMargin = i_Dp(dp8);
        }

        verticalLine.setLayoutParams(layout_81);
        cslr_replay_layout.addView(verticalLine);

        AppCompatImageView chslr_imv_replay_pic = new AppCompatImageView(context);
        chslr_imv_replay_pic.setId(R.id.chslr_imv_replay_pic);
        chslr_imv_replay_pic.setAdjustViewBounds(true);

        LinearLayout.LayoutParams layout_760 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, i_Dp(R.dimen.dp40));

        if (HelperCalander.isPersianUnicode) {
            layout_760.leftMargin = i_Dp(dp8);
        } else {
            layout_760.rightMargin = i_Dp(dp8);
        }

        chslr_imv_replay_pic.setLayoutParams(layout_760);
        cslr_replay_layout.addView(chslr_imv_replay_pic);

        LinearLayout linearLayout_376 = new LinearLayout(context);
        linearLayout_376.setGravity(LEFT);

        linearLayout_376.setOrientation(VERTICAL);
        LinearLayout.LayoutParams layout_847 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        linearLayout_376.setLayoutParams(layout_847);

        EmojiTextViewE chslr_txt_replay_from = new EmojiTextViewE(context);
        chslr_txt_replay_from.setId(R.id.chslr_txt_replay_from);
        chslr_txt_replay_from.setSingleLine(true);
        chslr_txt_replay_from.setPadding(0, 0, 0, 0);

        chslr_txt_replay_from.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG);

        chslr_txt_replay_from.setText("");

        chslr_txt_replay_from.setTextColor(context.getResources().getColor(R.color.colorOldBlack));
        chslr_txt_replay_from.setTextAppearance(context, R.style.ChatMessages_EmojiTextView);
        setTextSize(chslr_txt_replay_from, R.dimen.dp12);

        chslr_txt_replay_from.setTypeface(G.typeface_IRANSansMobile_Bold);
        LinearLayout.LayoutParams layout_55 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        chslr_txt_replay_from.setLayoutParams(layout_55);
        linearLayout_376.addView(chslr_txt_replay_from);

        EmojiTextViewE chslr_txt_replay_message = new EmojiTextViewE(context);
        chslr_txt_replay_message.setId(R.id.chslr_txt_replay_message);
        chslr_txt_replay_message.setEllipsize(TextUtils.TruncateAt.END);
        chslr_txt_replay_message.setSingleLine(true);
        chslr_txt_replay_message.setPadding(0, 0, 0, 0);
        chslr_txt_replay_message.setText("");

        chslr_txt_replay_message.setTextColor(new Theme().getPrimaryTextColor(chslr_txt_replay_message.getContext()));
        chslr_txt_replay_message.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG);

        chslr_txt_replay_message.setTextAppearance(context, R.style.ChatMessages_EmojiTextView);
        setTextSize(chslr_txt_replay_message, R.dimen.dp12);
        LinearLayout.LayoutParams layout_641 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        chslr_txt_replay_message.setLayoutParams(layout_641);
        chslr_txt_replay_message.setTypeface(G.typeface_IRANSansMobile);
        linearLayout_376.addView(chslr_txt_replay_message);
        cslr_replay_layout.addView(linearLayout_376);

        return cslr_replay_layout;
    }

    static View getViewForward(Context context) {
        LinearLayout cslr_ll_forward = new LinearLayout(context);
        cslr_ll_forward.setId(R.id.cslr_ll_forward);
        cslr_ll_forward.setClickable(true);
        cslr_ll_forward.setOrientation(HORIZONTAL);
        cslr_ll_forward.setPadding(i_Dp(R.dimen.messageContainerPaddingLeftRight), i_Dp(messageContainerPadding), i_Dp(R.dimen.messageContainerPaddingLeftRight), i_Dp(messageContainerPadding));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            cslr_ll_forward.setTextDirection(View.TEXT_DIRECTION_LOCALE);
        }
        setLayoutDirection(cslr_ll_forward, View.LAYOUT_DIRECTION_LOCALE);

        LinearLayout.LayoutParams layout_687 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cslr_ll_forward.setLayoutParams(layout_687);

        View View_997 = new View(context);
        View_997.setBackgroundColor(new Theme().getMessageVerticalLineColor(View_997.getContext()));
        LinearLayout.LayoutParams layout_547 = new LinearLayout.LayoutParams(dpToPixel(2), ViewGroup.LayoutParams.MATCH_PARENT);
        layout_547.rightMargin = dpToPixel(3);
        View_997.setLayoutParams(layout_547);
        cslr_ll_forward.addView(View_997);


        TextView cslr_txt_prefix_forward = new AppCompatTextView(context);
        cslr_txt_prefix_forward.setId(R.id.cslr_txt_prefix_forward);
        cslr_txt_prefix_forward.setText(context.getResources().getString(R.string.forwarded_from));
        cslr_txt_prefix_forward.setTextColor(new Theme().getReceivedMessageOtherTextColor(cslr_txt_prefix_forward.getContext()));
        setTextSize(cslr_txt_prefix_forward, R.dimen.dp12);
        cslr_txt_prefix_forward.setSingleLine(true);
        cslr_txt_prefix_forward.setTypeface(G.typeface_IRANSansMobile_Bold);
        LinearLayout.LayoutParams layout_992 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_992.rightMargin = i_Dp(dp4);
        layout_992.leftMargin = i_Dp(R.dimen.dp6);
        cslr_txt_prefix_forward.setLayoutParams(layout_992);
        cslr_ll_forward.addView(cslr_txt_prefix_forward);

        TextView cslr_txt_forward_from = new AppCompatTextView(context);
        cslr_txt_forward_from.setId(R.id.cslr_txt_forward_from);
        cslr_txt_forward_from.setMinimumWidth(i_Dp(R.dimen.dp100));
        cslr_txt_forward_from.setMaxWidth(i_Dp(R.dimen.dp140));
        cslr_txt_forward_from.setTextColor(new Theme().getForwardFromTextColor(cslr_txt_forward_from.getContext()));
        setTextSize(cslr_txt_forward_from, R.dimen.dp12);
        cslr_txt_forward_from.setSingleLine(true);
        cslr_txt_forward_from.setTypeface(G.typeface_IRANSansMobile_Bold);
        LinearLayout.LayoutParams layout_119 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cslr_txt_forward_from.setLayoutParams(layout_119);
        cslr_ll_forward.addView(cslr_txt_forward_from);

        return cslr_ll_forward;
    }

    public static View getHorizontalSpace(int size) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, ViewGroup.LayoutParams.MATCH_PARENT);
        View view = new View(context);
        view.setLayoutParams(params);
        return view;
    }

    public static View getVerticalSpace(int size) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, size);
        View view = new View(context);
        view.setLayoutParams(params);
        return view;
    }

    static EmojiTextViewE makeTextViewMessage() {
        EmojiTextViewE textView = new EmojiTextViewE(context);
        textView.setPadding(10, 4, 10, 4);
        textView.setEmojiSize(i_Dp(R.dimen.dp18));

        textView.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setTextColor(Color.parseColor(G.textBubble));

        textView.setId(R.id.messageSenderTextMessage);
        textView.setTypeface(G.typeface_IRANSansMobile);
        setTextSizeDirect(textView, G.userTextSize);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            textView.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG);

        setLayoutDirection(textView, View.LAYOUT_DIRECTION_LOCALE);


        return textView;
    }

    static View makeHeaderTextView(String text) {
        EmojiTextViewE textView = new EmojiTextViewE(context);
        textView.setTextColor(Color.parseColor(G.textBubble));
        textView.setId(R.id.messageSenderName);
        textView.setGravity(LEFT);
        textView.setPadding(20, 0, 20, 5);
        textView.setSingleLine(true);
        textView.setTypeface(G.typeface_IRANSansMobile);
        textView.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setText(text);
        setTextSize(textView, R.dimen.dp12);
        return textView;
    }

    static View makeCircleImageView() {

        CircleImageView circleImageView = new CircleImageView(context);
        circleImageView.setId(R.id.messageSenderAvatar);

        int size = (int) context.getResources().getDimension(R.dimen.dp48);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
        params.setMargins(0, 0, (int) context.getResources().getDimension(dp8), 0);

        circleImageView.setLayoutParams(params);

        return circleImageView;
    }

    /**
     * return text view for items that have text (for example : image_text, video_text , ...)
     */
    public static LinearLayout getTextView() {
        LinearLayout csliwt_layout_container_message = new LinearLayout(G.context);
        csliwt_layout_container_message.setId(R.id.csliwt_layout_container_message);
//        csliwt_layout_container_message.setBackgroundColor(Color.parseColor(G.backgroundTheme));
        csliwt_layout_container_message.setOrientation(VERTICAL);
        LinearLayout.LayoutParams layout_327 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        csliwt_layout_container_message.setLayoutParams(layout_327);
        return csliwt_layout_container_message;
    }

    public static MessageProgress getProgressBar(int sizeSrc) {
        MessageProgress messageProgress = new MessageProgress(G.context);
        messageProgress.setId(R.id.progress);
        LinearLayout.LayoutParams params;
        if (sizeSrc > 0) {
            params = new LinearLayout.LayoutParams(i_Dp(sizeSrc), i_Dp(sizeSrc));
        } else {
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        messageProgress.setLayoutParams(params);

        return messageProgress;
    }

    /**
     * ***************** Common Methods *****************
     */
    public static int dpToPixel(int dp) {
        Resources r = context.getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return px;

        //  (2/getApplicationContext().getResources().getDisplayMetrics().density)
    }


    public static float pixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static int i_Dp(int dpSrc) {

        return (int) context.getResources().getDimension(dpSrc);
    }

    public static void setTextSize(TextView v, int sizeSrc) {

        int mSize = i_Dp(sizeSrc);
        v.setTextSize(TypedValue.COMPLEX_UNIT_PX, mSize);
    }

    private static void setTextSizeDirect(TextView v, int size) {
        v.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
    }

    public static void setTypeFace(TextView v) {
        v.setTypeface(ResourcesCompat.getFont(v.getContext(), R.font.main_font));
    }

    public static void setLayoutDirection(View view, int direction) {
        ViewCompat.setLayoutDirection(view, direction);
    }

    //*******************************************************************************************

    public static View getViewItemCall() {

        LinearLayout linearLayout_205 = new LinearLayout(G.context);
        linearLayout_205.setId(R.id.mainContainer);
        LinearLayout.LayoutParams layout_218 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout_205.setLayoutParams(layout_218);

        TypedValue rippleView = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, rippleView, true);
        linearLayout_205.setBackgroundResource(rippleView.resourceId);

        //linearLayout_205.setBackgroundColor(Color.parseColor(G.backgroundTheme));

        CheckBox checkBox = new CheckBox(G.context);
        checkBox.setId(R.id.fcsl_check_box);
        checkBox.setVisibility(View.GONE);
        checkBox.setClickable(false);
        checkBox.setButtonDrawable(R.drawable.check_box_background);
        LinearLayout.LayoutParams lp_checkBox = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp_checkBox.setMargins(i_Dp(R.dimen.dp10), 0, i_Dp(R.dimen.dp4), 0);
        lp_checkBox.gravity = Gravity.CENTER;
        checkBox.setLayoutParams(lp_checkBox);
        linearLayout_205.addView(checkBox);

        CircleImageView fcsl_imv_picture = new CircleImageView(G.context);
        fcsl_imv_picture.setId(R.id.fcsl_imv_picture);
        LinearLayout.LayoutParams layout_856 = new LinearLayout.LayoutParams(i_Dp(R.dimen.dp48), i_Dp(R.dimen.dp48));
        layout_856.setMargins(i_Dp(R.dimen.dp6), i_Dp(R.dimen.dp6), i_Dp(R.dimen.dp6), i_Dp(R.dimen.dp6));
        layout_856.gravity = Gravity.CENTER;

        fcsl_imv_picture.setLayoutParams(layout_856);
        linearLayout_205.addView(fcsl_imv_picture);

        LinearLayout linearLayout_71 = new LinearLayout(G.context);
        linearLayout_71.setOrientation(VERTICAL);
        LinearLayout.LayoutParams layout_794 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, i_Dp(R.dimen.dp68));
        linearLayout_71.setLayoutParams(layout_794);

        LinearLayout linearLayout_470 = new LinearLayout(G.context);
        linearLayout_470.setOrientation(HORIZONTAL);
        LinearLayout.LayoutParams layout_822 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1);
        linearLayout_470.setPadding(0, i_Dp(R.dimen.dp12), 0, 0);
        linearLayout_470.setLayoutParams(layout_822);

        LinearLayout linearLayout_983 = new LinearLayout(G.context);
        linearLayout_983.setOrientation(VERTICAL);
        LinearLayout.LayoutParams layout_313 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        layout_313.leftMargin = i_Dp(R.dimen.dp6);
        linearLayout_983.setLayoutParams(layout_313);

        EmojiTextViewE fcsl_txt_name = new EmojiTextViewE(G.context);
        fcsl_txt_name.setId(R.id.fcsl_txt_name);
        fcsl_txt_name.setPadding(0, 0, 0, dpToPixel(1));
        fcsl_txt_name.setText("Name");
        fcsl_txt_name.setSingleLine(true);
        if (G.isDarkTheme) {
            fcsl_txt_name.setTextColor(Color.parseColor(G.textTitleTheme));
        } else {
            fcsl_txt_name.setTextColor(G.context.getResources().getColor(R.color.black90));
        }

        setTextSize(fcsl_txt_name, R.dimen.dp15);
        fcsl_txt_name.setTypeface(G.typeface_IRANSansMobile_Bold);
        LinearLayout.LayoutParams layout_415 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_415.gravity = Gravity.START;
        fcsl_txt_name.setLayoutParams(layout_415);
        linearLayout_983.addView(fcsl_txt_name);

        LinearLayout linearLayout_976 = new LinearLayout(G.context);
        linearLayout_976.setOrientation(HORIZONTAL);
        LinearLayout.LayoutParams layout_106 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout_976.setLayoutParams(layout_106);

        AppCompatTextView fcsl_txt_time_info = new AppCompatTextView(G.context);
        fcsl_txt_time_info.setId(R.id.fcsl_txt_time_info);
        fcsl_txt_time_info.setGravity(Gravity.START);
        fcsl_txt_time_info.setSingleLine(true);
        fcsl_txt_time_info.setText("(4) 9:24 am");
        if (G.isDarkTheme) {
            fcsl_txt_time_info.setTextColor(Color.parseColor(G.textSubTheme));
        } else {
            fcsl_txt_time_info.setTextColor(G.context.getResources().getColor(R.color.gray_5c));
        }
        setTextSize(fcsl_txt_time_info, R.dimen.dp12);
        fcsl_txt_time_info.setTypeface(G.typeface_IRANSansMobile);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            fcsl_txt_time_info.setTextDirection(View.TEXT_DIRECTION_LOCALE);
        }
        LinearLayout.LayoutParams layout_959 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);

        fcsl_txt_time_info.setLayoutParams(layout_959);
        linearLayout_976.addView(fcsl_txt_time_info);
        linearLayout_983.addView(linearLayout_976);
        linearLayout_470.addView(linearLayout_983);

        LinearLayout linearLayout_202 = new LinearLayout(G.context);
        linearLayout_202.setOrientation(VERTICAL);
        LinearLayout.LayoutParams layout_803 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layout_803.rightMargin = i_Dp(R.dimen.dp8);
        layout_803.leftMargin = i_Dp(R.dimen.dp8);
        linearLayout_202.setLayoutParams(layout_803);

        MaterialDesignTextView fcsl_txt_icon = new MaterialDesignTextView(G.context);
        fcsl_txt_icon.setId(R.id.fcsl_txt_icon);
        fcsl_txt_icon.setText(G.fragmentActivity.getResources().getString(R.string.md_call_made));
        fcsl_txt_icon.setTextColor(G.context.getResources().getColor(R.color.green));
        setTextSize(fcsl_txt_icon, R.dimen.dp18);
        LinearLayout.LayoutParams layout_178 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_178.gravity = Gravity.END;
        fcsl_txt_icon.setLayoutParams(layout_178);
        linearLayout_202.addView(fcsl_txt_icon);

        AppCompatTextView fcsl_txt_dureation_time = new AppCompatTextView(G.context);
        fcsl_txt_dureation_time.setId(R.id.fcsl_txt_dureation_time);
        fcsl_txt_dureation_time.setText("2:24");
        fcsl_txt_dureation_time.setTextColor(G.context.getResources().getColor(R.color.btn_start_page5));
        setTextSize(fcsl_txt_dureation_time, R.dimen.dp12);
        fcsl_txt_dureation_time.setTypeface(G.typeface_IRANSansMobile);
        LinearLayout.LayoutParams layout_483 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        fcsl_txt_dureation_time.setLayoutParams(layout_483);
        linearLayout_202.addView(fcsl_txt_dureation_time);
        linearLayout_470.addView(linearLayout_202);
        linearLayout_71.addView(linearLayout_470);

        View textView_316 = new View(G.context);
        textView_316.setBackgroundColor(G.context.getResources().getColor(R.color.gray_3c));
        LinearLayout.LayoutParams layout_241 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        textView_316.setLayoutParams(layout_241);
        linearLayout_71.addView(textView_316);
        linearLayout_205.addView(linearLayout_71);

        return linearLayout_205;
    }

    /*public static View getViewRegisteredContacts() {

        LinearLayout linearLayout_main = new LinearLayout(G.context);
        linearLayout_main.setBackgroundColor(Color.parseColor(G.backgroundTheme));
        linearLayout_main.setOrientation(VERTICAL);

        LinearLayout.LayoutParams layout_main = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout_main.setLayoutParams(layout_main);

        SwipeLayout swipeRevealLayout = new SwipeLayout(G.context);
        swipeRevealLayout.setId(R.id.swipeRevealLayout);

        ViewGroup.LayoutParams layoutParams = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        swipeRevealLayout.setLayoutParams(layoutParams);

        LinearLayout swipeLayout = new LinearLayout(G.context);
        swipeLayout.setOrientation(HORIZONTAL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            swipeLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        swipeLayout.setLayoutParams(layoutParams1);


        LinearLayout layoutDelete = new LinearLayout(G.context);
        layoutDelete.setId(R.id.swipeDelete);
        layoutDelete.setBackgroundColor(G.context.getResources().getColor(R.color.red_swipe));
        layoutDelete.setOrientation(HORIZONTAL);
        LinearLayout.LayoutParams layoutDeleteParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutDelete.setLayoutParams(layoutDeleteParams);

        AppCompatTextView txtDelete = new AppCompatTextView(G.context);
        txtDelete.setText(G.context.getResources().getString(R.string.delete_item_dialog));
        txtDelete.setGravity(Gravity.CENTER);
        setTypeFace(txtDelete);

        txtDelete.setPadding(20, 0, 20, 0);
        txtDelete.setTextColor(G.context.getResources().getColor(R.color.white));
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        txtDelete.setLayoutParams(layoutParams2);

        MaterialDesignTextView iconDelete = new MaterialDesignTextView(G.context);
        iconDelete.setGravity(CENTER_VERTICAL);
        iconDelete.setText(G.fragmentActivity.getResources().getString(R.string.md_rubbish_delete_file));
        iconDelete.setTextColor(G.context.getResources().getColor(R.color.white));
        iconDelete.setPadding(10, 0, 10, 0);
        setTextSize(iconDelete, R.dimen.dp22);
        LinearLayout.LayoutParams layout_178 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        layout_178.gravity = Gravity.LEFT;
//        layout_178.leftMargin = i_Dp(R.dimen.dp32);
        iconDelete.setLayoutParams(layout_178);

        layoutDelete.addView(iconDelete);
        layoutDelete.addView(txtDelete);

        LinearLayout layoutEdit = new LinearLayout(G.context);
        layoutEdit.setId(R.id.swipeEdit);
        layoutEdit.setBackgroundColor(G.context.getResources().getColor(R.color.green));
        layoutEdit.setOrientation(HORIZONTAL);
        LinearLayout.LayoutParams layoutEditParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutEdit.setLayoutParams(layoutEditParams);


        AppCompatTextView txtEdit = new AppCompatTextView(G.context);
        txtEdit.setText(G.context.getResources().getString(R.string.edit));
        txtEdit.setGravity(Gravity.CENTER);
        setTypeFace(txtEdit);
        txtEdit.setPadding(20, 0, 20, 0);

        txtEdit.setTextColor(G.context.getResources().getColor(R.color.white));
        ViewGroup.LayoutParams layoutParamsEdit = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        txtEdit.setLayoutParams(layoutParamsEdit);

        MaterialDesignTextView iconEdit = new MaterialDesignTextView(G.context);
        iconEdit.setGravity(CENTER_VERTICAL);
        iconEdit.setText(G.fragmentActivity.getResources().getString(R.string.md_edit));
        iconEdit.setTextColor(G.context.getResources().getColor(R.color.white));
        iconEdit.setPadding(10, 0, 10, 0);
        setTextSize(iconEdit, R.dimen.dp22);
        LinearLayout.LayoutParams iconEditParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        iconEditParams.gravity = Gravity.LEFT;
        iconEdit.setLayoutParams(iconEditParams);

        layoutEdit.addView(iconEdit);
        layoutEdit.addView(txtEdit);


        swipeLayout.addView(layoutDelete);
        swipeLayout.addView(layoutEdit);
        swipeRevealLayout.addView(swipeLayout);

        LinearLayout linearLayout_578 = new LinearLayout(G.context);
//        linearLayout_578.setId(R.id.rootRegisterContact);
        linearLayout_578.setId(R.id.mainContainer);
        linearLayout_578.setOrientation(VERTICAL);
        linearLayout_578.setBackgroundColor(Color.parseColor(G.backgroundTheme));
        if (HelperCalander.isPersianUnicode) {
            linearLayout_578.setPadding(i_Dp(R.dimen.dp20), 0, i_Dp(R.dimen.dp20), 0);
        } else {
            linearLayout_578.setPadding(i_Dp(R.dimen.dp16), 0, i_Dp(R.dimen.dp20), 0);
        }

        LinearLayout.LayoutParams layout_842 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout_578.setLayoutParams(layout_842);

        AppCompatTextView topLine = new AppCompatTextView(G.context);
        topLine.setId(R.id.topLine);
        topLine.setBackgroundColor(G.context.getResources().getColor(R.color.gray));
        LinearLayout.LayoutParams layout_323 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        layout_323.bottomMargin = i_Dp(R.dimen.dp6);

        if (HelperCalander.isPersianUnicode) {
            layout_323.leftMargin = i_Dp(R.dimen.dp20);
            layout_323.rightMargin = i_Dp(R.dimen.dp8);
        } else {
            layout_323.leftMargin = i_Dp(R.dimen.dp8);
            layout_323.rightMargin = i_Dp(R.dimen.dp20);
        }

        topLine.setLayoutParams(layout_323);


        LinearLayout linearLayout_823 = new LinearLayout(G.context);
        linearLayout_823.setOrientation(HORIZONTAL);
        LinearLayout.LayoutParams layout_692 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_692.topMargin = i_Dp(R.dimen.dp16);
        linearLayout_823.setLayoutParams(layout_692);

        RelativeLayout layoutCheckBoxAndImage = new RelativeLayout(G.context);
        RelativeLayout.LayoutParams layoutCheckBoxAndImageParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutCheckBoxAndImage.setLayoutParams(layoutCheckBoxAndImageParams);


        CircleImageView imageView = new CircleImageView(G.context);
        imageView.setId(R.id.imageView);
        RelativeLayout.LayoutParams layout_54 = new RelativeLayout.LayoutParams(i_Dp(R.dimen.dp60), i_Dp(R.dimen.dp48));

        if (HelperCalander.isPersianUnicode) {
            layout_54.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            layout_54.setMargins(0, 0, i_Dp(R.dimen.dp36), 0);
        } else {
            layout_54.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            layout_54.setMargins(i_Dp(R.dimen.dp36), 0, 0, 0);
        }
        //  layout_54.leftMargin=i_Dp(R.dimen.dp36);
        imageView.setLayoutParams(layout_54);


        CheckBox animateCheckBox = new CheckBox(G.context);
        animateCheckBox.setId(R.id.animateCheckBoxContact);
        animateCheckBox.setVisibility(View.INVISIBLE);
        animateCheckBox.setClickable(false);

        // animateCheckBox.on(R.color.white);
        RelativeLayout.LayoutParams animateCheckBoxParams = new RelativeLayout.LayoutParams(i_Dp(R.dimen.dp48), i_Dp(R.dimen.dp48));

        // animateCheckBoxParams.rightMargin=i_Dp(R.dimen.dp24);
        // animateCheckBoxParams.leftMargin=i_Dp(R.dimen.dp24);
        animateCheckBox.setLayoutParams(animateCheckBoxParams);
     *//*   animateCheckBoxParams.gravity = Gravity.BOTTOM;
        animateCheckBoxParams.gravity = Gravity.LEFT;

        animateCheckBoxParams.gravity = Gravity.START;*//*

        // animateCheckBoxParams.leftMargin=90;


        layoutCheckBoxAndImage.addView(animateCheckBox);
        layoutCheckBoxAndImage.addView(imageView);


        linearLayout_823.addView(layoutCheckBoxAndImage);

        LinearLayout linearLayout_673 = new LinearLayout(G.context);
        linearLayout_673.setOrientation(VERTICAL);
        LinearLayout.LayoutParams layout_445 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


        if (HelperCalander.isPersianUnicode) {
            layout_445.rightMargin = i_Dp(R.dimen.dp8);
         *//*   title.setPadding(i_Dp(R.dimen.dp48), 0, 0, 0);
            subtitle.setPadding(i_Dp(R.dimen.dp48), 0, 0, 0);*//*
        } else {
            layout_445.leftMargin = i_Dp(R.dimen.dp8);
        }
        linearLayout_673.setLayoutParams(layout_445);

        AppCompatTextView title = new AppCompatTextView(G.context);
        title.setId(R.id.title);


        title.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        title.setSingleLine(true);
        title.setTextColor(Color.parseColor(G.textTitleTheme));
        setTextSize(title, R.dimen.dp16);
        setTypeFace(title);
        LinearLayout.LayoutParams layout_949 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 0, 1);
        title.setLayoutParams(layout_949);
        linearLayout_673.addView(title);

        AppCompatTextView subtitle = new AppCompatTextView(G.context);
        subtitle.setId(R.id.subtitle);
        subtitle.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        subtitle.setText(G.fragmentActivity.getResources().getString(R.string.last_seen_recently));
        setTextSize(subtitle, R.dimen.dp14);
        subtitle.setSingleLine(true);
        setTypeFace(subtitle);
        subtitle.setTextColor(Color.parseColor(G.textSubTheme));
        LinearLayout.LayoutParams layout_488 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 0, 1);
        subtitle.setLayoutParams(layout_488);
        linearLayout_673.addView(subtitle);
        linearLayout_823.addView(linearLayout_673);
        linearLayout_578.addView(linearLayout_823);

        swipeRevealLayout.addView(linearLayout_578);
        linearLayout_main.addView(topLine);
        linearLayout_main.addView(swipeRevealLayout);


        return linearLayout_main;
    }*/
}
