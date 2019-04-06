package net.iGap.adapter.items.chat;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentChat;
import net.iGap.messageprogress.MessageProgress;
import net.iGap.module.MaterialDesignTextView;

import static android.view.Gravity.BOTTOM;
import static android.view.Gravity.CENTER;
import static android.view.Gravity.RIGHT;
import static android.widget.LinearLayout.HORIZONTAL;
import static android.widget.LinearLayout.VERTICAL;
import static net.iGap.G.context;
import static net.iGap.R.dimen.dp1_minus;
import static net.iGap.R.dimen.dp4;
import static net.iGap.R.dimen.dp52;

public class ChatItemHolder extends RecyclerView.ViewHolder {
    LinearLayout contentContainer;
    LinearLayout mainContainer;
    LinearLayout m_container;
    LinearLayout csl_ll_time;
    LinearLayout lyt_see;
    LinearLayout lyt_vote;
    LinearLayout lyt_vote_up;
    LinearLayout lyt_vote_down;
    LinearLayout lyt_signature;
    AppCompatTextView txt_vote_down;
    AppCompatTextView txt_vote_up;
    AppCompatTextView txt_signature;
    AppCompatTextView txt_views_label;
    AppCompatTextView txtEditedIndicator;
    AppCompatTextView cslr_txt_time;
    AppCompatImageView cslr_txt_tic;
    View cslm_view_left_dis;


    public ChatItemHolder(View view) {
        super(view);

        LinearLayout.LayoutParams layout_216 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        mainContainer = new LinearLayout(context);
        mainContainer.setId(R.id.mainContainer);
        mainContainer.setOrientation(HORIZONTAL);
        mainContainer.setLayoutParams(layout_216);

        LinearLayout.LayoutParams layout_584 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        LinearLayout linearLayout_683 = new LinearLayout(context);
        linearLayout_683.setOrientation(VERTICAL);
        linearLayout_683.setLayoutParams(layout_584);

        mainContainer.addView(linearLayout_683);

        LinearLayout.LayoutParams layout_617 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        contentContainer = new LinearLayout(context);
        contentContainer.setId(R.id.contentContainer);
        contentContainer.setOrientation(VERTICAL);
        contentContainer.setLayoutParams(layout_617);
        contentContainer.setPadding(ViewMaker.i_Dp(R.dimen.dp4), ViewMaker.i_Dp(R.dimen.dp4), ViewMaker.i_Dp(R.dimen.dp4), ViewMaker.i_Dp(R.dimen.dp4));

        linearLayout_683.addView(contentContainer);


        ////Time////
        csl_ll_time = new LinearLayout(context);
        csl_ll_time.setId(R.id.csl_ll_time);
        csl_ll_time.setOrientation(HORIZONTAL);
        LinearLayout.LayoutParams layout_189 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_189.bottomMargin = dpToPixel(4);
        csl_ll_time.setPadding(dpToPixel(5), 0, dpToPixel(5), 0);
        csl_ll_time.setLayoutParams(layout_189);

        txtEditedIndicator = new MaterialDesignTextView(context);
        txtEditedIndicator.setId(R.id.txtEditedIndicator);
        txtEditedIndicator.setPadding(i_Dp(dp4), 0, 0, 0);
        txtEditedIndicator.setGravity(CENTER);
        txtEditedIndicator.setSingleLine(true);
        txtEditedIndicator.setText(context.getResources().getString(R.string.edited));
        setTextSize(txtEditedIndicator, R.dimen.dp8);
//        if (G.isDarkTheme) {
//            txtEditedIndicator.setTextAppearance(context, R.style.text_sub_style_setting_dark);
//        } else {
//            txtEditedIndicator.setTextAppearance(context, R.style.ChatMessages_Time);
//        }
        txtEditedIndicator.setTextColor(Color.parseColor(G.textBubble));

        setTypeFace(txtEditedIndicator);
        LinearLayout.LayoutParams layout_927 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_927.rightMargin = i_Dp(dp4);
        layout_927.topMargin = dpToPixel(4);
        txtEditedIndicator.setLayoutParams(layout_927);

        cslr_txt_time = new AppCompatTextView(context);
        cslr_txt_time.setId(R.id.cslr_txt_time);
        cslr_txt_time.setGravity(CENTER);
        cslr_txt_time.setPadding(dpToPixel(2), 0, dpToPixel(2), 0);
        cslr_txt_time.setTextColor(Color.parseColor(G.textBubble));
        cslr_txt_time.setText("10:21");
        cslr_txt_time.setSingleLine(true);
        cslr_txt_time.setTextAppearance(context, R.style.ChatMessages_Time);
        setTypeFace(cslr_txt_time);
        LinearLayout.LayoutParams layout_638 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_638.topMargin = i_Dp(dp4);
        cslr_txt_time.setLayoutParams(layout_638);

        // ContextThemeWrapper newContext = new ContextThemeWrapper(G.context, R.style.ChatMessages_MaterialDesignTextView_Tick);
        cslr_txt_tic = new AppCompatImageView(context);
        cslr_txt_tic.setId(R.id.cslr_txt_tic);

//        cslr_txt_tic.setColorFilter(context.getResources().getColor(R.color.colorOldBlack));
        cslr_txt_tic.setColorFilter(Color.parseColor(G.textBubble), PorterDuff.Mode.SRC_IN);
        //AppUtils.setImageDrawable(cslr_txt_tic, R.drawable.ic_double_check);
        LinearLayout.LayoutParams layout_311 = new LinearLayout.LayoutParams(i_Dp(R.dimen.dp16), ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_311.leftMargin = i_Dp(dp4);
        layout_311.topMargin = i_Dp(dp1_minus);
        cslr_txt_tic.setLayoutParams(layout_311);

        csl_ll_time.addView(txtEditedIndicator);
        csl_ll_time.addView(cslr_txt_time);
        csl_ll_time.addView(cslr_txt_tic);

        ////end////

        /////////////Vote////
        LinearLayout.LayoutParams layout_356 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layout_356.bottomMargin = i_Dp(R.dimen.dp16);
        layout_356.rightMargin = i_Dp(R.dimen.dp6);
        layout_356.leftMargin = i_Dp(R.dimen.dp6);

        lyt_vote = new LinearLayout(context);
        lyt_vote.setId(R.id.lyt_vote);
        lyt_vote.setOrientation(HORIZONTAL);
        lyt_vote.setLayoutParams(layout_356);
        setLayoutDirection(lyt_vote, View.LAYOUT_DIRECTION_LTR);

        LinearLayout.LayoutParams layout_799 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lyt_vote_up = new LinearLayout(context);
        lyt_vote_up.setId(R.id.lyt_vote_up);
        lyt_vote_up.setOrientation(HORIZONTAL);
        lyt_vote_up.setPadding(i_Dp(R.dimen.dp4), 0, i_Dp(R.dimen.dp4), i_Dp(R.dimen.dp2));
        lyt_vote_up.setLayoutParams(layout_799);

        LinearLayout.LayoutParams layout_713 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        txt_vote_up = new AppCompatTextView(context);
        txt_vote_up.setId(R.id.txt_vote_up);
        txt_vote_up.setText("0");
        txt_vote_up.setGravity(BOTTOM);
        txt_vote_up.setTextAppearance(context, R.style.ChatMessages_Time);
        txt_vote_up.setSingleLine(true);
        txt_vote_up.setTextColor(Color.parseColor(G.voteIconTheme));
        txt_vote_up.setLayoutParams(layout_713);
        setTypeFace(txt_vote_up);

        LinearLayout.LayoutParams layout_21600 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        MaterialDesignTextView img_vote_up = new MaterialDesignTextView(context);
        img_vote_up.setId(R.id.img_vote_up);
        img_vote_up.setText(context.getResources().getString(R.string.md_thumb_up));
        img_vote_up.setGravity(BOTTOM);
        img_vote_up.setTextColor(Color.parseColor(G.voteIconTheme));
        img_vote_up.setLayoutParams(layout_21600);
        setTextSize(img_vote_up, R.dimen.dp12);

        LinearLayout.LayoutParams layout_221 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        lyt_vote_down = new LinearLayout(context);
        lyt_vote_down.setId(R.id.lyt_vote_down);
        lyt_vote_down.setPadding(i_Dp(R.dimen.dp4), 0, i_Dp(R.dimen.dp4), i_Dp(R.dimen.dp2));
        lyt_vote_down.setOrientation(HORIZONTAL);
        lyt_vote_down.setLayoutParams(layout_221);

        LinearLayout.LayoutParams layout_877 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        MaterialDesignTextView img_vote_down = new MaterialDesignTextView(context);
        img_vote_down.setId(R.id.img_vote_down);
        img_vote_down.setText(context.getResources().getString(R.string.md_thumb_down));
        img_vote_down.setGravity(BOTTOM);
        img_vote_down.setTextColor(Color.parseColor(G.voteIconTheme));
        img_vote_down.setLayoutParams(layout_877);
        setTextSize(img_vote_down, R.dimen.dp12);

        LinearLayout.LayoutParams layout_856 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);

        txt_vote_down = new AppCompatTextView(context);
        txt_vote_down.setId(R.id.txt_vote_down);
        txt_vote_down.setText("0");
        txt_vote_down.setGravity(BOTTOM);
        txt_vote_down.setTextAppearance(context, R.style.ChatMessages_Time);
        txt_vote_down.setSingleLine(true);
        txt_vote_down.setTextColor(Color.parseColor(G.voteIconTheme));
        txt_vote_down.setLayoutParams(layout_856);
        setTypeFace(txt_vote_down);

        lyt_vote_up.addView(txt_vote_up);
        lyt_vote_up.addView(getHorizontalSpace(i_Dp(R.dimen.dp2)));
        lyt_vote_up.addView(img_vote_up);

        lyt_vote_down.addView(txt_vote_down);
        lyt_vote_down.addView(getHorizontalSpace(i_Dp(R.dimen.dp2)));
        lyt_vote_down.addView(img_vote_down);

        lyt_vote.addView(lyt_vote_down);
        lyt_vote.addView(getHorizontalSpace(i_Dp(R.dimen.dp4)));
        lyt_vote.addView(lyt_vote_up);

        //////////////end////
        lyt_vote.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;

        ///////see/////
        lyt_see = new LinearLayout(context);
        lyt_see.setId(R.id.lyt_see);
        lyt_see.setGravity(Gravity.CENTER_VERTICAL);
        lyt_see.setOrientation(HORIZONTAL);
        lyt_see.setPadding(0, 0, i_Dp(dp4), 0);
        LinearLayout.LayoutParams layout_865 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        lyt_see.setLayoutParams(layout_865);

        cslm_view_left_dis = new View(context);
        cslm_view_left_dis.setId(R.id.cslm_view_left_dis);
        cslm_view_left_dis.setVisibility(View.GONE);
        LinearLayout.LayoutParams layout_901 = new LinearLayout.LayoutParams(i_Dp(dp52), dpToPixel(1));
        cslm_view_left_dis.setLayoutParams(layout_901);
        lyt_see.addView(cslm_view_left_dis);

        lyt_signature = new LinearLayout(context);
        lyt_signature.setId(R.id.lyt_signature);
        lyt_signature.setGravity(CENTER | RIGHT);
        lyt_signature.setOrientation(HORIZONTAL);
        lyt_signature.setPadding(0, 0, i_Dp(dp4), 0);
        lyt_signature.setVisibility(View.GONE);
        LinearLayout.LayoutParams layout_483 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        lyt_signature.setLayoutParams(layout_483);

        txt_signature = new AppCompatTextView(context);
        txt_signature.setId(R.id.txt_signature);
        txt_signature.setGravity(CENTER);
        txt_signature.setText("");
        txt_signature.setSingleLine(true);
        //  txt_signature.setFilters();
        txt_signature.setTextColor(Color.parseColor(G.textBubble));
        txt_signature.setTextAppearance(context, R.style.ChatMessages_Time);
        LinearLayout.LayoutParams layout_266 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, i_Dp(R.dimen.dp18));
        txt_signature.setLayoutParams(layout_266);
        setTypeFace(txt_signature);
        lyt_signature.addView(txt_signature);
        lyt_see.addView(lyt_signature);

        txt_views_label = new AppCompatTextView(context);
        txt_views_label.setId(R.id.txt_views_label);
        txt_views_label.setGravity(CENTER);
        txt_views_label.setText("0");
        txt_views_label.setTextAppearance(context, R.style.ChatMessages_Time);
        setTypeFace(txt_views_label);

        txt_views_label.setPadding(0, dpToPixel(2), 0, 0);
        txt_views_label.setTextColor(Color.parseColor(G.textBubble));
        LinearLayout.LayoutParams layout_959 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, i_Dp(R.dimen.dp16));
        txt_views_label.setLayoutParams(layout_959);
        lyt_see.addView(txt_views_label);

        MaterialDesignTextView img_eye = new MaterialDesignTextView(context);
        img_eye.setId(R.id.img_eye);
        img_eye.setText(context.getResources().getString(R.string.md_visibility));
        img_eye.setTextColor(Color.parseColor(G.textBubble));
        setTextSize(img_eye, R.dimen.dp12);
        // img_eye.setPadding(0, dpToPixel(2), 0, 0);
        img_eye.setSingleLine(true);
        // img_eye.setTextAppearance(G.context, R.style.TextIconAppearance_toolbar);
        LinearLayout.LayoutParams layout_586 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_586.leftMargin = i_Dp(dp4);
        img_eye.setLayoutParams(layout_586);
        lyt_see.addView(img_eye);

        ////////end/////
        lyt_see.setVisibility(View.GONE);

        linearLayout_683.addView(csl_ll_time, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        csl_ll_time.addView(lyt_see, 0);
        csl_ll_time.addView(lyt_vote, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));


        LinearLayout.LayoutParams layout_842 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        m_container = new LinearLayout(context);
        m_container.setId(R.id.m_container);
        m_container.setOrientation(VERTICAL);
        m_container.setLayoutParams(layout_842);

        contentContainer.addView(m_container);

        ((ViewGroup) itemView).addView(mainContainer);

        lyt_vote_up.setOnLongClickListener(getLongClickPerform());
        lyt_vote_down.setOnLongClickListener(getLongClickPerform());

        mainContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!FragmentChat.isInSelectionMode && (
                        ChatItemHolder.this instanceof VoiceItem.ViewHolder)
                ) {
                    return true;
                }

                itemView.performLongClick();
                return true;
            }
        });
    }

    private View.OnLongClickListener getLongClickPerform(){
        return view -> {
            itemView.performLongClick();
            return true;
        };
    }

    protected void setLayoutDirection(View view, int direction){
        ViewMaker.setLayoutDirection(view, direction);
    }

    protected int i_Dp(int dpSrc) {
        return ViewMaker.i_Dp(dpSrc);
    }

    protected void setTextSize(TextView v, int sizeSrc){
        ViewMaker.setTextSize(v, sizeSrc);
    }

    protected void setTypeFace(TextView v) {
        ViewMaker.setTypeFace(v);
    }

    protected int dpToPixel(int dp) {
        return ViewMaker.dpToPixel(dp);
    }

    protected MessageProgress getProgressBar(int sizeSrc){
        return ViewMaker.getProgressBar(sizeSrc);
    }

    protected View getHorizontalSpace(int size) {
        return ViewMaker.getHorizontalSpace(size);
    }

    protected View getVerticalSpace(int size) {
        return ViewMaker.getVerticalSpace(size);
    }

}

