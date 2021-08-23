package net.iGap.adapter.items.chat;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.fragments.FragmentChat;
import net.iGap.helper.LayoutCreator;
import net.iGap.libs.bottomNavigation.Util.Utils;
import net.iGap.messageprogress.MessageProgress;
import net.iGap.module.FontIconTextView;
import net.iGap.module.Theme;

import static android.view.Gravity.BOTTOM;
import static android.view.Gravity.CENTER;
import static android.view.Gravity.CENTER_VERTICAL;
import static android.view.Gravity.END;

public class NewChatItemHolder extends RecyclerView.ViewHolder {

    private Context context;

    private LinearLayout itemContainer;
    private ConstraintLayout chatBloke;
    private LinearLayout contentBloke;

    private AppCompatTextView voteDownTv;
    private AppCompatTextView voteUpTv;
    private AppCompatTextView signatureTv;
    private TextView viewsLabelTv;
    private AppCompatTextView messageTimeTv;
    private FontIconTextView messageStatusTv;
    private View cslm_view_left_dis;
    private FontIconTextView eyeIconTv;
    private FontIconTextView voteDownIv;
    private FontIconTextView voteUpIv;
    private View emptyView;

    private LinearLayout voteContainer;
    private LinearLayout viewContainer;
    private LinearLayout voteUpContainer;
    private LinearLayout voteDownContainer;

    private AppCompatImageView channelForwardIv;

    public NewChatItemHolder(@NonNull View itemView) {
        super(itemView);
        context = itemView.getContext();

        int otherColor = new Theme().getReceivedMessageOtherTextColor(itemView.getContext());
        ConstraintSet set = new ConstraintSet();
        itemContainer = new LinearLayout(itemView.getContext());

        channelForwardIv = new AppCompatImageView(itemView.getContext());
        /*channelForwardIv.setImageResource(R.drawable.ic_channel_forward_light);*/

        voteContainer = new LinearLayout(itemView.getContext());
        voteContainer.setId(R.id.ll_chatItem_vote);
        voteContainer.setOrientation(LinearLayout.HORIZONTAL);
        voteContainer.setGravity(Gravity.CENTER);

        viewContainer = new LinearLayout(itemView.getContext());
        viewContainer.setId(R.id.ll_chatItem_view);
        viewContainer.setGravity(Gravity.CENTER);

        voteUpContainer = new LinearLayout(itemView.getContext());
        voteUpContainer.setOrientation(LinearLayout.HORIZONTAL);
        voteUpContainer.setGravity(Gravity.CENTER);

        voteDownContainer = new LinearLayout(itemView.getContext());
        voteDownContainer.setOrientation(LinearLayout.HORIZONTAL);
        voteDownContainer.setGravity(Gravity.CENTER);

        chatBloke = new ConstraintLayout(itemView.getContext());
        chatBloke.setId(R.id.ll_chatItem_chatBloke);

        contentBloke = new LinearLayout(itemView.getContext());
        contentBloke.setId(R.id.ll_chatItem_contentBloke);
        contentBloke.setOrientation(LinearLayout.VERTICAL);

        voteDownTv = new AppCompatTextView(itemView.getContext());
        voteUpTv = new AppCompatTextView(itemView.getContext());

        emptyView = new View(itemView.getContext());
        emptyView.setId(R.id.tv_chatItem_empty);

        //set text color not here because text color depends on send message type or Received message type
        signatureTv = new AppCompatTextView(itemView.getContext());
        signatureTv.setId(R.id.tv_chatItem_signature);
        setTextSize(signatureTv, R.dimen.verySmallTextSize);
        signatureTv.setEllipsize(TextUtils.TruncateAt.END);
        signatureTv.setPadding(0, 0, LayoutCreator.dp(4), 0);
        setTypeFace(signatureTv);
        signatureTv.setSingleLine(true);
        signatureTv.setGravity(CENTER_VERTICAL);
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(11);
        signatureTv.setFilters(FilterArray);

        viewsLabelTv = new TextView(itemView.getContext());
        viewsLabelTv.setId(R.id.tv_chatItem_viewLabel);
        viewsLabelTv.setSingleLine(true);
        viewsLabelTv.setTextColor(otherColor);
        setTypeFace(viewsLabelTv);
        setTextSize(viewsLabelTv, R.dimen.verySmallTextSize);

        eyeIconTv = new FontIconTextView(itemView.getContext());
        eyeIconTv.setId(R.id.ll_chatItem_viewIcon);
        eyeIconTv.setText(R.string.icon_eye);
        eyeIconTv.setTextColor(otherColor);
        setTextSize(eyeIconTv, R.dimen.standardTextSize);

        messageStatusTv = new FontIconTextView(itemView.getContext());
        messageStatusTv.setId(R.id.tv_chatItem_status);
        setTextSize(messageStatusTv, R.dimen.largeTextSize);
        cslm_view_left_dis = new View(itemView.getContext());

        //set text color not here because text color depends on send message type or Received message type
        messageTimeTv = new AppCompatTextView(itemView.getContext());
        messageTimeTv.setId(R.id.tv_chatItem_time);
        setTextSize(messageTimeTv, R.dimen.verySmallTextSize);
        messageTimeTv.setPadding(dpToPx(2), 0, dpToPx(2), 0);
        messageTimeTv.setSingleLine(true);
        setTypeFace(messageTimeTv);

        voteUpTv.setSingleLine(true);
        voteUpTv.setTextColor(otherColor);
        setTextSize(voteUpTv, R.dimen.verySmallTextSize);
        setTypeFace(voteUpTv);

        voteDownTv.setSingleLine(true);
        voteDownTv.setTextColor(otherColor);
        setTypeFace(voteDownTv);
        setTextSize(voteDownTv, R.dimen.verySmallTextSize);


        itemContainer.setOrientation(LinearLayout.HORIZONTAL);
        itemContainer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        itemContainer.setPadding(ViewMaker.i_Dp(R.dimen.dp4), ViewMaker.i_Dp(R.dimen.dp4), ViewMaker.i_Dp(R.dimen.dp4), ViewMaker.i_Dp(R.dimen.dp4));

        chatBloke.setPadding(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(4));

        voteUpIv = new FontIconTextView(itemView.getContext());
        voteUpIv.setText(R.string.icon_heart);
        voteUpIv.setGravity(BOTTOM);
        voteUpIv.setTextColor(otherColor);
        setTextSize(voteUpIv, R.dimen.standardTextSize);

        voteDownIv = new FontIconTextView(itemView.getContext());
        voteDownIv.setText(R.string.icon_dislike);
        voteDownIv.setGravity(BOTTOM);
        voteDownIv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        voteDownIv.setTextColor(otherColor);
        setTextSize(voteDownIv, R.dimen.standardTextSize);

        viewContainer.addView(viewsLabelTv, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER, dpToPx(2), 0, dpToPx(1), 0));
        viewContainer.addView(eyeIconTv, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        voteUpContainer.addView(voteUpIv, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER, 0, 0, dpToPx(1), 0));
        voteUpContainer.addView(voteUpTv);

        voteDownContainer.addView(voteDownIv, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER, 0, 0, 2, 0));
        voteDownContainer.addView(voteDownTv);

        voteContainer.addView(voteUpContainer, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER, 8, 0, 2, 0));
        voteContainer.addView(voteDownContainer, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER, 0, 0, 8, 0));

        set.constrainWidth(emptyView.getId(), ConstraintSet.MATCH_CONSTRAINT);
        set.constrainHeight(emptyView.getId(), LayoutCreator.dp(20));

        set.constrainWidth(signatureTv.getId(), ConstraintSet.WRAP_CONTENT);
        set.constrainHeight(signatureTv.getId(), LayoutCreator.dp(20));

        set.constrainHeight(contentBloke.getId(), ConstraintSet.WRAP_CONTENT);
        set.constrainWidth(contentBloke.getId(), ConstraintSet.WRAP_CONTENT);

        set.constrainHeight(messageStatusTv.getId(), ConstraintSet.WRAP_CONTENT);
        set.constrainWidth(messageStatusTv.getId(), ConstraintSet.WRAP_CONTENT);

        set.constrainHeight(messageTimeTv.getId(), ConstraintSet.WRAP_CONTENT);
        set.constrainWidth(messageTimeTv.getId(), ConstraintSet.WRAP_CONTENT);

        set.constrainHeight(voteDownIv.getId(), ConstraintSet.WRAP_CONTENT);
        set.constrainWidth(voteDownTv.getId(), ConstraintSet.WRAP_CONTENT);

        set.constrainHeight(voteContainer.getId(), ConstraintSet.WRAP_CONTENT);
        set.constrainWidth(voteContainer.getId(), ConstraintSet.WRAP_CONTENT);

        set.constrainHeight(viewContainer.getId(), ConstraintSet.WRAP_CONTENT);
        set.constrainWidth(viewContainer.getId(), ConstraintSet.WRAP_CONTENT);

        set.connect(contentBloke.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT);
        set.connect(contentBloke.getId(), ConstraintSet.BOTTOM, messageTimeTv.getId(), ConstraintSet.TOP, dpToPx(2));

        set.connect(messageStatusTv.getId(), ConstraintSet.RIGHT, chatBloke.getId(), ConstraintSet.RIGHT);
        set.connect(messageStatusTv.getId(), ConstraintSet.LEFT, messageTimeTv.getId(), ConstraintSet.RIGHT);
        set.connect(messageStatusTv.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);

        set.connect(messageTimeTv.getId(), ConstraintSet.RIGHT, messageStatusTv.getId(), ConstraintSet.LEFT, dpToPx(4));
        set.connect(messageTimeTv.getId(), ConstraintSet.LEFT, signatureTv.getId(), ConstraintSet.RIGHT);
        set.connect(messageTimeTv.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);

        set.connect(signatureTv.getId(), ConstraintSet.RIGHT, messageTimeTv.getId(), ConstraintSet.LEFT,dpToPx(4));
        set.connect(signatureTv.getId(), ConstraintSet.LEFT, emptyView.getId(), ConstraintSet.RIGHT);
        set.connect(signatureTv.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);

        set.connect(emptyView.getId(), ConstraintSet.RIGHT, signatureTv.getId(), ConstraintSet.LEFT,dpToPx(4));
        set.connect(emptyView.getId(), ConstraintSet.LEFT, voteContainer.getId(), ConstraintSet.RIGHT);
        set.connect(emptyView.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);

        set.connect(voteContainer.getId(), ConstraintSet.RIGHT, emptyView.getId(), ConstraintSet.LEFT,dpToPx(4));
        set.connect(voteContainer.getId(), ConstraintSet.LEFT, viewContainer.getId(), ConstraintSet.RIGHT);
        set.connect(voteContainer.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);

        set.connect(viewContainer.getId(), ConstraintSet.RIGHT, voteContainer.getId(), ConstraintSet.LEFT,dpToPx(4));
        set.connect(viewContainer.getId(), ConstraintSet.LEFT, chatBloke.getId(), ConstraintSet.LEFT);
        set.connect(viewContainer.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);

     /* int[] views = {viewContainer.getId(), voteContainer.getId(),emptyView.getId(),signatureTv.getId(), messageTimeTv.getId(), messageStatusTv.getId()};
        set.createHorizontalChain(chatBloke.getId(), ConstraintSet.LEFT, chatBloke.getId(), ConstraintSet.RIGHT, views, null, ConstraintSet.CHAIN_SPREAD);*/

        chatBloke.addView(contentBloke);
        chatBloke.addView(messageStatusTv);
        chatBloke.addView(messageTimeTv);
        chatBloke.addView(signatureTv);
        chatBloke.addView(emptyView);
        chatBloke.addView(voteContainer);
        chatBloke.addView(viewContainer);

        set.applyTo(chatBloke);
        itemContainer.addView(chatBloke, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT));

        ((ViewGroup) itemView).addView(itemContainer);

        voteUpContainer.setOnLongClickListener(getLongClickPerform());
        voteDownContainer.setOnLongClickListener(getLongClickPerform());

        itemContainer.setOnLongClickListener(view -> {
            if (!FragmentChat.isInSelectionMode && (NewChatItemHolder.this instanceof VoiceItem.ViewHolder)) {
                return true;
            }
            itemView.performLongClick();
            return true;
        });
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    private View.OnLongClickListener getLongClickPerform() {
        return view -> {
            itemView.performLongClick();
            return true;
        };
    }

    protected void setTextSize(TextView v, int sizeSrc) {
        ViewMaker.setTextSize(v, sizeSrc);
    }

    protected void setLayoutDirection(View view, int direction) {
        ViewMaker.setLayoutDirection(view, direction);
    }


    protected int i_Dp(int dpSrc) {
        return ViewMaker.i_Dp(dpSrc);
    }

    protected void setTypeFace(TextView textView) {
        textView.setTypeface(ResourcesCompat.getFont(textView.getContext(), R.font.main_font));
    }

    protected void setTypeFace(TextView textView, int style) {
        textView.setTypeface(ResourcesCompat.getFont(textView.getContext(), R.font.main_font), style);
    }

    protected MessageProgress getProgressBar(Context context, int sizeSrc) {
        return ViewMaker.getProgressBar(context, sizeSrc);
    }

    public Context getContext() {
        return context;
    }

    private void SetTextSize(TextView textView, int pxSize) {
        Utils.setTextSize(textView, pxSize);
    }

    public LinearLayout getItemContainer() {
        return itemContainer;
    }

    public ConstraintLayout getChatBloke() {
        return chatBloke;
    }

    public LinearLayout getContentBloke() {
        return contentBloke;
    }

    public AppCompatTextView getVoteDownTv() {
        return voteDownTv;
    }

    public AppCompatTextView getVoteUpTv() {
        return voteUpTv;
    }

    public AppCompatTextView getSignatureTv() {
        return signatureTv;
    }

    public TextView getViewsLabelTv() {
        return viewsLabelTv;
    }

    public AppCompatTextView getMessageTimeTv() {
        return messageTimeTv;
    }

    public FontIconTextView getMessageStatusTv() {
        return messageStatusTv;
    }

    public View getCslm_view_left_dis() {
        return cslm_view_left_dis;
    }

    public LinearLayout getVoteContainer() {
        return voteContainer;
    }

    public LinearLayout getViewContainer() {
        return viewContainer;
    }

    public LinearLayout getVoteUpContainer() {
        return voteUpContainer;
    }

    public LinearLayout getVoteDownContainer() {
        return voteDownContainer;
    }

    public Resources getResources() {
        return context.getResources();
    }

    public int getColor(int color) {
        return getResources().getColor(color);
    }

    public Drawable getDrawable(int drawableId) {
        return getResources().getDrawable(drawableId);
    }

    public FontIconTextView getVoteDownIv() {
        return voteDownIv;
    }

    public FontIconTextView getVoteUpIv() {
        return voteUpIv;
    }

    public FontIconTextView getEyeIconTv() {
        return eyeIconTv;
    }

    public ImageView getChannelForwardIv() {
        return channelForwardIv;
    }
}
