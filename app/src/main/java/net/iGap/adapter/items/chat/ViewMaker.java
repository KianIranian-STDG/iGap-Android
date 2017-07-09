package net.iGap.adapter.items.chat;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import net.iGap.G;
import net.iGap.R;
import net.iGap.module.AppUtils;
import net.iGap.module.CircleImageView;
import net.iGap.module.EmojiTextViewE;
import net.iGap.module.MaterialDesignTextView;

import static android.R.attr.left;
import static android.graphics.Typeface.BOLD;
import static android.support.design.R.id.center;
import static android.support.design.R.id.center_horizontal;
import static android.view.Gravity.BOTTOM;
import static android.view.Gravity.CENTER;
import static android.view.Gravity.CENTER_HORIZONTAL;
import static android.view.Gravity.LEFT;
import static android.view.Gravity.RIGHT;
import static android.widget.LinearLayout.HORIZONTAL;
import static android.widget.LinearLayout.VERTICAL;
import static java.lang.Boolean.TRUE;
import static net.iGap.G.context;
import static net.iGap.R.dimen.dp32;
import static net.iGap.R.dimen.dp4;
import static net.iGap.R.dimen.dp52;
import static net.iGap.R.dimen.dp8;


public class ViewMaker {

    public static View getTextItem() {

        LinearLayout mainContainer = new LinearLayout(context);
        mainContainer.setId(R.id.mainContainer);
        mainContainer.setOrientation(HORIZONTAL);
        LinearLayout.LayoutParams layout_216 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mainContainer.setLayoutParams(layout_216);

        LinearLayout linearLayout_683 = new LinearLayout(context);
        linearLayout_683.setOrientation(VERTICAL);
        LinearLayout.LayoutParams layout_584 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout_683.setLayoutParams(layout_584);

        LinearLayout contentContainer = new LinearLayout(context);
        contentContainer.setId(R.id.contentContainer);
        LinearLayout.LayoutParams layout_617 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        contentContainer.setLayoutParams(layout_617);
        contentContainer.setPadding(5, 5, 5, 5);

        LinearLayout m_container = new LinearLayout(context);
        m_container.setId(R.id.m_container);
        m_container.setOrientation(VERTICAL);
        LinearLayout.LayoutParams layout_842 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        m_container.setLayoutParams(layout_842);

        LinearLayout csliwt_layout_container_message = new LinearLayout(context);
        csliwt_layout_container_message.setId(R.id.csliwt_layout_container_message);
        csliwt_layout_container_message.setMinimumHeight((int) context.getResources().getDimension(dp32));
        csliwt_layout_container_message.setOrientation(HORIZONTAL);
        LinearLayout.LayoutParams layout_577 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        csliwt_layout_container_message.setLayoutParams(layout_577);

        m_container.addView(csliwt_layout_container_message);
        contentContainer.addView(m_container);
        linearLayout_683.addView(contentContainer);

        mainContainer.addView(linearLayout_683);

        return mainContainer;
    }

    //********************************************************************************************

    public static View getViewTime(Boolean addHearing) {

        LinearLayout csl_ll_time = new LinearLayout(context);
        csl_ll_time.setId(R.id.csl_ll_time);
        csl_ll_time.setOrientation(HORIZONTAL);
        LinearLayout.LayoutParams layout_189 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_189.bottomMargin = dpToPixel(4);
        layout_189.topMargin = dpToPixel(-1);
        csl_ll_time.setLayoutParams(layout_189);

        //******************************************************************************************************

        TextView txtEditedIndicator = new TextView(context);
        txtEditedIndicator.setId(R.id.txtEditedIndicator);
        txtEditedIndicator.setPadding(i_Dp(dp4), 0, 0, 0);
        txtEditedIndicator.setGravity(CENTER);
        txtEditedIndicator.setSingleLine(true);
        txtEditedIndicator.setText(context.getResources().getString(R.string.edited));
        txtEditedIndicator.setTextAppearance(context, R.style.ChatMessages_Time);
        LinearLayout.LayoutParams layout_927 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_927.rightMargin = i_Dp(dp4);
        layout_927.topMargin = dpToPixel(4);
        txtEditedIndicator.setLayoutParams(layout_927);

        TextView cslr_txt_time = new TextView(context);
        cslr_txt_time.setId(R.id.cslr_txt_time);
        cslr_txt_time.setGravity(CENTER);
        cslr_txt_time.setPadding(dpToPixel(2), 0, dpToPixel(2), 0);
        cslr_txt_time.setText("10:21");
        cslr_txt_time.setSingleLine(true);
        cslr_txt_time.setTextAppearance(context, R.style.ChatMessages_Time);
        LinearLayout.LayoutParams layout_638 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_638.topMargin = i_Dp(dp4);
        cslr_txt_time.setLayoutParams(layout_638);

        // ContextThemeWrapper newContext = new ContextThemeWrapper(G.context, R.style.ChatMessages_MaterialDesignTextView_Tick);
        ImageView cslr_txt_tic = new ImageView(context);
        cslr_txt_tic.setId(R.id.cslr_txt_tic);
        cslr_txt_tic.setColorFilter(context.getResources().getColor(R.color.colorOldBlack));
        AppUtils.setImageDrawable(cslr_txt_tic, R.drawable.ic_double_check);
        LinearLayout.LayoutParams layout_311 = new LinearLayout.LayoutParams(i_Dp(R.dimen.dp16), ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_311.leftMargin = i_Dp(dp4);
        layout_311.topMargin = dpToPixel(-5);
        cslr_txt_tic.setLayoutParams(layout_311);

        csl_ll_time.addView(txtEditedIndicator);
        csl_ll_time.addView(cslr_txt_time);
        csl_ll_time.addView(cslr_txt_tic);

        if (addHearing) {
            MaterialDesignTextView cslr_txt_hearing = new MaterialDesignTextView(context);
            cslr_txt_hearing.setId(R.id.cslr_txt_hearing);
            cslr_txt_hearing.setGravity(CENTER);
            cslr_txt_hearing.setText(context.getResources().getString(R.string.md_hearing));
            cslr_txt_hearing.setTextColor(context.getResources().getColor(R.color.room_message_gray));
            cslr_txt_hearing.setSingleLine(true);
            cslr_txt_hearing.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 8);
            cslr_txt_hearing.setTypeface(G.typeface_Fontico);
            LinearLayout.LayoutParams layout_899 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layout_899.gravity = LEFT | Gravity.CENTER_VERTICAL;
            layout_899.leftMargin = 14;
            cslr_txt_hearing.setLayoutParams(layout_899);
            csl_ll_time.addView(cslr_txt_hearing);
        }

        return csl_ll_time;
    }

    public static View getViewSeen() {

        LinearLayout lyt_see = new LinearLayout(context);
        lyt_see.setId(R.id.lyt_see);
        lyt_see.setGravity(Gravity.CENTER_VERTICAL);
        lyt_see.setOrientation(HORIZONTAL);
        lyt_see.setPadding(0, 0, i_Dp(dp4), 0);
        LinearLayout.LayoutParams layout_865 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        lyt_see.setLayoutParams(layout_865);

        View cslm_view_left_dis = new View(context);
        cslm_view_left_dis.setId(R.id.cslm_view_left_dis);
        cslm_view_left_dis.setVisibility(View.GONE);
        LinearLayout.LayoutParams layout_901 = new LinearLayout.LayoutParams(i_Dp(dp52), dpToPixel(1));
        cslm_view_left_dis.setLayoutParams(layout_901);
        lyt_see.addView(cslm_view_left_dis);

        LinearLayout lyt_signature = new LinearLayout(context);
        lyt_signature.setId(R.id.lyt_signature);
        lyt_signature.setGravity(CENTER | RIGHT);
        lyt_signature.setOrientation(HORIZONTAL);
        lyt_signature.setPadding(0, 0, i_Dp(dp4), 0);
        lyt_signature.setVisibility(View.GONE);
        LinearLayout.LayoutParams layout_483 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        lyt_signature.setLayoutParams(layout_483);

        TextView txt_signature = new TextView(context);
        txt_signature.setId(R.id.txt_signature);
        txt_signature.setGravity(CENTER);
        txt_signature.setText("");
        txt_signature.setSingleLine(true);
        //  txt_signature.setFilters();
        txt_signature.setTextColor(context.getResources().getColor(R.color.room_message_gray));
        txt_signature.setTextAppearance(context, R.style.ChatMessages_Time);
        LinearLayout.LayoutParams layout_266 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, i_Dp(R.dimen.dp18));
        txt_signature.setLayoutParams(layout_266);
        lyt_signature.addView(txt_signature);
        lyt_see.addView(lyt_signature);

        TextView txt_views_label = new TextView(context);
        txt_views_label.setId(R.id.txt_views_label);
        txt_views_label.setGravity(CENTER);
        txt_views_label.setText("0");
        txt_views_label.setTextAppearance(context, R.style.ChatMessages_Time);
        txt_views_label.setPadding(0, dpToPixel(2), 0, 0);
        txt_views_label.setTextColor(context.getResources().getColor(R.color.room_message_gray));
        LinearLayout.LayoutParams layout_959 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, i_Dp(R.dimen.dp16));
        txt_views_label.setLayoutParams(layout_959);
        lyt_see.addView(txt_views_label);

        MaterialDesignTextView img_eye = new MaterialDesignTextView(context);
        img_eye.setId(R.id.img_eye);
        img_eye.setText(context.getResources().getString(R.string.md_visibility));
        img_eye.setTextColor(context.getResources().getColor(R.color.gray_6c));
        img_eye.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        // img_eye.setPadding(0, dpToPixel(2), 0, 0);
        img_eye.setSingleLine(true);
        // img_eye.setTextAppearance(G.context, R.style.TextIconAppearance_toolbar);
        LinearLayout.LayoutParams layout_586 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_586.leftMargin = i_Dp(dp4);
        img_eye.setLayoutParams(layout_586);
        lyt_see.addView(img_eye);

        return lyt_see;
    }

    public static View getViewReplay() {

        LinearLayout cslr_replay_layout = new LinearLayout(context);
        cslr_replay_layout.setId(R.id.cslr_replay_layout);
        cslr_replay_layout.setBackgroundColor(context.getResources().getColor(R.color.messageBox_replyBoxBackgroundSend));
        cslr_replay_layout.setClickable(true);
        cslr_replay_layout.setOrientation(HORIZONTAL);
        cslr_replay_layout.setPadding(i_Dp(R.dimen.messageContainerPaddingLeftRight), i_Dp(R.dimen.messageContainerPaddingLeftRight), i_Dp(R.dimen.messageContainerPaddingLeftRight), i_Dp(R.dimen.messageContainerPaddingLeftRight));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            cslr_replay_layout.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG);
        }
        LinearLayout.LayoutParams layout_468 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cslr_replay_layout.setLayoutParams(layout_468);

        View verticalLine = new View(context);
        verticalLine.setId(R.id.verticalLine);
        verticalLine.setBackgroundColor(Color.parseColor("#f7ab07"));
        LinearLayout.LayoutParams layout_81 = new LinearLayout.LayoutParams(dpToPixel(3), ViewGroup.LayoutParams.MATCH_PARENT);
        layout_81.rightMargin = i_Dp(dp8);
        verticalLine.setLayoutParams(layout_81);
        cslr_replay_layout.addView(verticalLine);

        ImageView chslr_imv_replay_pic = new ImageView(context);
        chslr_imv_replay_pic.setId(R.id.chslr_imv_replay_pic);
        chslr_imv_replay_pic.setAdjustViewBounds(true);
        chslr_imv_replay_pic.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams layout_760 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, i_Dp(R.dimen.dp40));
        layout_760.rightMargin = i_Dp(dp8);
        chslr_imv_replay_pic.setLayoutParams(layout_760);
        cslr_replay_layout.addView(chslr_imv_replay_pic);

        LinearLayout linearLayout_376 = new LinearLayout(context);
        linearLayout_376.setGravity(LEFT);
        linearLayout_376.setOrientation(VERTICAL);
        LinearLayout.LayoutParams layout_847 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout_376.setLayoutParams(layout_847);

        EmojiTextViewE chslr_txt_replay_from = new EmojiTextViewE(context);
        chslr_txt_replay_from.setId(R.id.chslr_txt_replay_from);
        chslr_txt_replay_from.setSingleLine(true);
        chslr_txt_replay_from.setPadding(0, 0, 0, 0);
        chslr_txt_replay_from.setText("");
        chslr_txt_replay_from.setTextColor(context.getResources().getColor(R.color.colorOldBlack));
        chslr_txt_replay_from.setTextAppearance(context, R.style.ChatMessages_EmojiTextView);
        chslr_txt_replay_from.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);

        chslr_txt_replay_from.setTypeface(G.typeface_IRANSansMobile_Bold);
        LinearLayout.LayoutParams layout_55 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        chslr_txt_replay_from.setLayoutParams(layout_55);
        linearLayout_376.addView(chslr_txt_replay_from);

        TextView chslr_txt_replay_message = new TextView(context);
        chslr_txt_replay_message.setId(R.id.chslr_txt_replay_message);
        chslr_txt_replay_message.setEllipsize(TextUtils.TruncateAt.END);
        chslr_txt_replay_message.setSingleLine(true);
        chslr_txt_replay_message.setPadding(0, 0, 0, 0);
        chslr_txt_replay_message.setText("");
        chslr_txt_replay_message.setTextColor(Color.WHITE);
        chslr_txt_replay_message.setTypeface(G.typeface_IRANSansMobile);
        chslr_txt_replay_message.setTextAppearance(context, R.style.ChatMessages_EmojiTextView);
        chslr_txt_replay_message.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        LinearLayout.LayoutParams layout_641 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        chslr_txt_replay_message.setLayoutParams(layout_641);
        linearLayout_376.addView(chslr_txt_replay_message);
        cslr_replay_layout.addView(linearLayout_376);

        return cslr_replay_layout;
    }

    public static View getViewForward() {

        LinearLayout cslr_ll_forward = new LinearLayout(context);
        cslr_ll_forward.setId(R.id.cslr_ll_forward);
        cslr_ll_forward.setClickable(true);
        cslr_ll_forward.setOrientation(HORIZONTAL);
        cslr_ll_forward.setPadding(i_Dp(R.dimen.messageContainerPaddingLeftRight), i_Dp(R.dimen.messageContainerPadding), i_Dp(R.dimen.messageContainerPaddingLeftRight), i_Dp(R.dimen.messageContainerPadding));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            cslr_ll_forward.setTextDirection(View.TEXT_DIRECTION_LOCALE);
            cslr_ll_forward.setLayoutDirection(View.LAYOUT_DIRECTION_LOCALE);
        }
        LinearLayout.LayoutParams layout_687 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cslr_ll_forward.setLayoutParams(layout_687);

        View View_997 = new View(context);
        View_997.setBackgroundColor(context.getResources().getColor(R.color.newBlack));
        LinearLayout.LayoutParams layout_547 = new LinearLayout.LayoutParams(dpToPixel(2), ViewGroup.LayoutParams.MATCH_PARENT);
        layout_547.rightMargin = dpToPixel(3);
        View_997.setLayoutParams(layout_547);
        cslr_ll_forward.addView(View_997);

        LinearLayout linearLayout_515 = new LinearLayout(context);
        linearLayout_515.setOrientation(HORIZONTAL);
        LinearLayout.LayoutParams layout_762 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout_515.setLayoutParams(layout_762);

        TextView cslr_txt_prefix_forward = new TextView(context);
        cslr_txt_prefix_forward.setId(R.id.cslr_txt_prefix_forward);
        cslr_txt_prefix_forward.setText(context.getResources().getString(R.string.forwarded_from));
        cslr_txt_prefix_forward.setTextColor(context.getResources().getColor(R.color.newBlack));
        cslr_txt_prefix_forward.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        cslr_txt_prefix_forward.setSingleLine(true);
        cslr_txt_prefix_forward.setTypeface(G.typeface_IRANSansMobile_Bold);
        LinearLayout.LayoutParams layout_992 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_992.rightMargin = i_Dp(dp4);
        layout_992.leftMargin = i_Dp(R.dimen.dp6);
        cslr_txt_prefix_forward.setLayoutParams(layout_992);
        linearLayout_515.addView(cslr_txt_prefix_forward);

        TextView cslr_txt_forward_from = new TextView(context);
        cslr_txt_forward_from.setId(R.id.cslr_txt_forward_from);
        cslr_txt_forward_from.setMinimumWidth(i_Dp(R.dimen.dp100));
        cslr_txt_forward_from.setMaxWidth(i_Dp(R.dimen.dp140));
        cslr_txt_forward_from.setTextColor(context.getResources().getColor(R.color.newBlack));
        cslr_txt_forward_from.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        cslr_txt_forward_from.setSingleLine(true);
        cslr_txt_forward_from.setTypeface(G.typeface_IRANSansMobile_Bold);
        LinearLayout.LayoutParams layout_119 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cslr_txt_forward_from.setLayoutParams(layout_119);
        linearLayout_515.addView(cslr_txt_forward_from);
        cslr_ll_forward.addView(linearLayout_515);

        return cslr_ll_forward;
    }

    public static View getViewVote() {

        LinearLayout lyt_vote = new LinearLayout(context);
        lyt_vote.setId(R.id.lyt_vote);
        lyt_vote.setGravity(BOTTOM);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            lyt_vote.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        lyt_vote.setOrientation(VERTICAL);
        LinearLayout.LayoutParams layout_356 = new LinearLayout.LayoutParams(i_Dp(R.dimen.dp40), ViewGroup.LayoutParams.MATCH_PARENT);
        layout_356.gravity = BOTTOM;
        layout_356.setMargins(0, 0, 0, i_Dp(R.dimen.dp40));
        lyt_vote.setLayoutParams(layout_356);

        LinearLayout lyt_vote_up = new LinearLayout(context);
        lyt_vote_up.setId(R.id.lyt_vote_up);
        lyt_vote_up.setGravity(CENTER);
        lyt_vote_up.setOrientation(VERTICAL);
        LinearLayout.LayoutParams layout_799 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_799.bottomMargin = i_Dp(R.dimen.dp12);
        lyt_vote_up.setLayoutParams(layout_799);

        TextView txt_vote_up = new TextView(context);
        txt_vote_up.setId(R.id.txt_vote_up);
        txt_vote_up.setGravity(CENTER);
        txt_vote_up.setText("0");
        txt_vote_up.setSingleLine(true);
        txt_vote_up.setTextAppearance(context, R.style.ChatMessages_Time);
        txt_vote_up.setTextColor(context.getResources().getColor(R.color.room_message_gray));
        LinearLayout.LayoutParams layout_713 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dpToPixel(16));
        txt_vote_up.setLayoutParams(layout_713);
        lyt_vote_up.addView(txt_vote_up);

        MaterialDesignTextView img_vote_up = new MaterialDesignTextView(context);
        img_vote_up.setId(R.id.img_vote_up);
        img_vote_up.setText(context.getResources().getString(R.string.md_thumb_up));
        img_vote_up.setTextColor(context.getResources().getColor(R.color.gray_6c));
        img_vote_up.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        LinearLayout.LayoutParams layout_216 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_216.leftMargin = i_Dp(dp4);
        img_vote_up.setLayoutParams(layout_216);
        lyt_vote_up.addView(img_vote_up);
        lyt_vote.addView(lyt_vote_up);

        LinearLayout lyt_vote_down = new LinearLayout(context);
        lyt_vote_down.setId(R.id.lyt_vote_down);
        lyt_vote_down.setGravity(CENTER);
        lyt_vote_down.setOrientation(VERTICAL);
        LinearLayout.LayoutParams layout_221 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lyt_vote_down.setLayoutParams(layout_221);

        MaterialDesignTextView img_vote_down = new MaterialDesignTextView(context);
        img_vote_down.setId(R.id.img_vote_down);
        img_vote_down.setText(context.getResources().getString(R.string.md_thumb_down));
        img_vote_down.setTextColor(context.getResources().getColor(R.color.gray_6c));
        img_vote_down.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        LinearLayout.LayoutParams layout_877 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_877.leftMargin = i_Dp(dp4);
        img_vote_down.setLayoutParams(layout_877);
        lyt_vote_down.addView(img_vote_down);

        TextView txt_vote_down = new TextView(context);
        txt_vote_down.setId(R.id.txt_vote_down);
        txt_vote_down.setGravity(CENTER);
        txt_vote_down.setText("0");
        txt_vote_down.setSingleLine(true);
        txt_vote_down.setTextColor(context.getResources().getColor(R.color.room_message_gray));
        LinearLayout.LayoutParams layout_856 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dpToPixel(16));
        txt_vote_down.setLayoutParams(layout_856);
        txt_vote_down.setTextAppearance(context, R.style.ChatMessages_Time);
        lyt_vote_down.addView(txt_vote_down);
        lyt_vote.addView(lyt_vote_down);

        View textView_564 = new View(context);
        LinearLayout.LayoutParams layout_437 = new LinearLayout.LayoutParams(i_Dp(R.dimen.dp40), i_Dp(dp32));
        textView_564.setLayoutParams(layout_437);
        lyt_vote.addView(textView_564);

        return lyt_vote;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    static View getAudioItem() {

        LinearLayout mainContainer = new LinearLayout(context);
        mainContainer.setId(R.id.mainContainer);
        mainContainer.setOrientation(HORIZONTAL);
        LinearLayout.LayoutParams layout_859 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mainContainer.setLayoutParams(layout_859);

        LinearLayout linearLayout_880 = new LinearLayout(G.context);
        linearLayout_880.setOrientation(VERTICAL);
        LinearLayout.LayoutParams layout_525 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout_880.setLayoutParams(layout_525);

        LinearLayout contentContainer = new LinearLayout(G.context, null, R.style.ChatMessages_CardView);
        contentContainer.setId(R.id.contentContainer);
        LinearLayout.LayoutParams layout_256 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        contentContainer.setLayoutParams(layout_256);

        LinearLayout m_container = new LinearLayout(G.context);
        m_container.setId(R.id.m_container);
        m_container.setOrientation(VERTICAL);
        LinearLayout.LayoutParams layout_520 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        m_container.setLayoutParams(layout_520);

        LinearLayout audioBox = new LinearLayout(G.context);
        audioBox.setId(R.id.audioBox);
        audioBox.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        audioBox.setMinimumHeight((int) context.getResources().getDimension(R.dimen.dp130));
        audioBox.setOrientation(HORIZONTAL);
        audioBox.setPaddingRelative(0, (int) G.context.getResources().getDimension(R.dimen.messageContainerPadding), 0, (int) G.context.getResources().getDimension(R.dimen.messageContainerPaddingBottom));
        LinearLayout.LayoutParams layout_262 = new LinearLayout.LayoutParams((int) G.context.getResources().getDimension(R.dimen.dp200), LinearLayout.LayoutParams.WRAP_CONTENT);
        audioBox.setLayoutParams(layout_262);

        LinearLayout linearLayout_39 = new LinearLayout(G.context);
        linearLayout_39.setOrientation(VERTICAL);
        LinearLayout.LayoutParams layout_803 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layout_803.leftMargin = (int) G.context.getResources().getDimension(R.dimen.dp8);
        linearLayout_39.setLayoutParams(layout_803);

        LinearLayout linearLayout_632 = new LinearLayout(G.context);
        LinearLayout.LayoutParams layout_842 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout_632.setLayoutParams(layout_842);

        LinearLayout linearLayout_916 = new LinearLayout(G.context);
        linearLayout_916.setGravity(center_horizontal);
        linearLayout_916.setOrientation(VERTICAL);
        LinearLayout.LayoutParams layout_6 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout_916.setLayoutParams(layout_6);

        FrameLayout frameLayout = new FrameLayout(G.context);
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));

        ImageView imgThumbnail = new ImageView(G.context);
        imgThumbnail.setId(R.id.audio_thumbnail);
        LinearLayout.LayoutParams thumbnailParams = new LinearLayout.LayoutParams((int) G.context.getResources().getDimension(R.dimen.dp48), (int) G.context.getResources().getDimension(R.dimen.dp48));
        imgThumbnail.setAdjustViewBounds(true);
        imgThumbnail.setScaleType(ImageView.ScaleType.FIT_XY);
        imgThumbnail.setImageResource(R.drawable.green_music_note);
        imgThumbnail.setLayoutParams(thumbnailParams);

        TextView fileSize = new TextView(G.context);
        fileSize.setId(R.id.fileSize);
        fileSize.setTextAppearance(context, android.R.style.TextAppearance_Small);
        fileSize.setGravity(BOTTOM | CENTER_HORIZONTAL);
        fileSize.setSingleLine();
        fileSize.setMaxLines(1);
        fileSize.setText("3.2 mb");
        fileSize.setAllCaps(TRUE);
        fileSize.setTextColor(G.context.getResources().getColor(R.color.black90));
        fileSize.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        LinearLayout.LayoutParams layout_996 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        fileSize.setLayoutParams(layout_996);
        linearLayout_632.addView(linearLayout_916);

        LinearLayout linearLayout_222 = new LinearLayout(G.context);
        linearLayout_222.setOrientation(VERTICAL);
        linearLayout_222.setPadding((int) G.context.getResources().getDimension(R.dimen.dp8), 0, 0, 0);
        LinearLayout.LayoutParams layout_114 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        linearLayout_222.setLayoutParams(layout_114);

        TextView fileName = new TextView(G.context);
        fileName.setId(R.id.fileName);
        fileName.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        fileName.setGravity(LEFT);
        fileName.setMaxLines(1);
        fileName.setTextAppearance(context, android.R.style.TextAppearance_Medium);
        fileName.setMaxWidth((int) G.context.getResources().getDimension(R.dimen.dp160));
        fileName.setText("file_name.ext");
        fileName.setTextColor(Color.BLACK);
        fileName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        fileName.setTypeface(G.typeface_IRANSansMobile_Bold, BOLD);
        LinearLayout.LayoutParams layout_298 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        fileName.setLayoutParams(layout_298);
        linearLayout_222.addView(fileName);

        TextView songArtist = new TextView(G.context);
        songArtist.setId(R.id.songArtist);
        songArtist.setTextAppearance(context, android.R.style.TextAppearance_Small);
        songArtist.setMaxLines(1);
        songArtist.setText("Artist");
        songArtist.setTextColor(G.context.getResources().getColor(R.color.black90));
        LinearLayout.LayoutParams layout_757 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        songArtist.setLayoutParams(layout_757);
        linearLayout_222.addView(songArtist);
        linearLayout_632.addView(linearLayout_222);
        linearLayout_39.addView(linearLayout_632);

        LinearLayout audioPlayerViewContainer = new LinearLayout(G.context);
        audioPlayerViewContainer.setId(R.id.audioPlayerViewContainer);
        audioPlayerViewContainer.setOrientation(VERTICAL);
        LinearLayout.LayoutParams layout_435 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        audioPlayerViewContainer.setLayoutParams(layout_435);

        LinearLayout linearLayout_511 = new LinearLayout(G.context);
        linearLayout_511.setGravity(left | center);
        LinearLayout.LayoutParams layout_353 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) G.context.getResources().getDimension(R.dimen.dp36));
        linearLayout_511.setLayoutParams(layout_353);

        TextView csla_btn_play_music = new TextView(G.context);
        csla_btn_play_music.setId(R.id.txt_play_music);
        csla_btn_play_music.setBackgroundResource(0); //csla_btn_play_music.setBackgroundResource(@null);
        csla_btn_play_music.setTypeface(G.typeface_Fontico);
        csla_btn_play_music.setGravity(CENTER);
        csla_btn_play_music.setText(G.context.getResources().getString(R.string.md_play_arrow));
        csla_btn_play_music.setTextColor(G.context.getResources().getColor(R.color.toolbar_background));
        csla_btn_play_music.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        LinearLayout.LayoutParams layout_326 = new LinearLayout.LayoutParams((int) G.context.getResources().getDimension(R.dimen.dp32), LinearLayout.LayoutParams.MATCH_PARENT);
        csla_btn_play_music.setLayoutParams(layout_326);
        linearLayout_511.addView(csla_btn_play_music);

        SeekBar csla_seekBar1 = new SeekBar(G.context);
        csla_seekBar1.setId(R.id.csla_seekBar1);
        LinearLayout.LayoutParams layout_990 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout_990.weight = 1;
        layout_990.gravity = CENTER;
        csla_seekBar1.setLayoutParams(layout_990);
        linearLayout_511.addView(csla_seekBar1);
        audioPlayerViewContainer.addView(linearLayout_511);

        TextView csla_txt_timer = new TextView(G.context);
        csla_txt_timer.setId(R.id.csla_txt_timer);
        csla_txt_timer.setPadding(0, 0, (int) G.context.getResources().getDimension(R.dimen.dp8), 0);
        csla_txt_timer.setText("00:00");
        csla_txt_timer.setTextColor(G.context.getResources().getColor(R.color.toolbar_background));
        csla_txt_timer.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        LinearLayout.LayoutParams layout_637 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout_637.gravity = RIGHT;
        layout_637.leftMargin = (int) G.context.getResources().getDimension(R.dimen.dp52);
        csla_txt_timer.setLayoutParams(layout_637);
        audioPlayerViewContainer.addView(csla_txt_timer);
        linearLayout_39.addView(audioPlayerViewContainer);
        audioBox.addView(linearLayout_39);
        m_container.addView(audioBox);

        LinearLayout csliwt_layout_container_message = new LinearLayout(G.context);
        csliwt_layout_container_message.setId(R.id.csliwt_layout_container_message);
        csliwt_layout_container_message.setOrientation(HORIZONTAL);
        LinearLayout.LayoutParams layout_992 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        csliwt_layout_container_message.setLayoutParams(layout_992);
        m_container.addView(csliwt_layout_container_message);
        contentContainer.addView(m_container);
        linearLayout_880.addView(contentContainer);

        linearLayout_916.addView(frameLayout);
        linearLayout_916.addView(fileSize);
        frameLayout.addView(imgThumbnail);
        mainContainer.addView(linearLayout_880);

        return mainContainer;
    }

    public static View makeTextViewMessage(int maxsize, boolean hasEmoji) {

        if (hasEmoji) {
            EmojiTextViewE emojiTextViewE = new EmojiTextViewE(context);
            emojiTextViewE.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            emojiTextViewE.setTextColor(Color.parseColor("#333333"));
            emojiTextViewE.setId(R.id.messageSenderTextMessage);
            emojiTextViewE.setPadding(10, 0, 10, 0);
            emojiTextViewE.setTypeface(G.typeface_IRANSansMobile);
            emojiTextViewE.setTextSize(TypedValue.COMPLEX_UNIT_DIP, G.userTextSize);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                emojiTextViewE.setLayoutDirection(View.LAYOUT_DIRECTION_LOCALE);
                emojiTextViewE.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG);
            }
            emojiTextViewE.setMovementMethod(LinkMovementMethod.getInstance());

            if (maxsize > 0) {
                emojiTextViewE.setMaxWidth(maxsize);
            }

            return emojiTextViewE;
        } else {
            TextView textView = new TextView(context);
            textView.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            textView.setTextColor(Color.parseColor("#333333"));
            textView.setId(R.id.messageSenderTextMessage);
            textView.setPadding(10, 0, 10, 0);
            textView.setTypeface(G.typeface_IRANSansMobile);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, G.userTextSize);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                textView.setLayoutDirection(View.LAYOUT_DIRECTION_LOCALE);
                textView.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG);
            }
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            if (maxsize > 0) {
                textView.setMaxWidth(maxsize);
            }

            return textView;
        }
    }

    public static View makeHeaderTextView(String text) {

        EmojiTextViewE textView = new EmojiTextViewE(context);
        textView.setTextColor(Color.BLACK);
        textView.setBackgroundResource(R.drawable.rect_radios_top_gray);
        textView.setId(R.id.messageSenderName);
        textView.setGravity(LEFT);
        textView.setPadding(20, 0, 20, 5);
        //textView.setMinimumWidth((int) G.context.getResources().getDimension(R.dimen.dp220));
        textView.setSingleLine();
        textView.setTypeface(G.typeface_IRANSansMobile);
        textView.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);

        return textView;
    }

    public static View makeCircleImageView() {

        CircleImageView circleImageView = new CircleImageView(context);
        circleImageView.setId(R.id.messageSenderAvatar);

        int size = (int) context.getResources().getDimension(R.dimen.dp48);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
        params.setMargins(0, 0, (int) context.getResources().getDimension(dp8), 0);

        circleImageView.setLayoutParams(params);

        return circleImageView;
    }

    //*********************************************************************************************
    private static int dpToPixel(int dp) {
        Resources r = context.getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return px;

        //  (2/getApplicationContext().getResources().getDisplayMetrics().density)
    }

    private static int i_Dp(int dpSrc) {

        return (int) context.getResources().getDimension(dpSrc);
    }

    //****************************************************************************************************
}
