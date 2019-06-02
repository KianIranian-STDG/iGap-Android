package net.iGap.adapter.items.chat;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentChat;
import net.iGap.helper.LayoutCreator;
import net.iGap.messageprogress.MessageProgress;
import net.iGap.module.FontIconTextView;

import static android.view.Gravity.BOTTOM;

public class NewChatItemHolder extends RecyclerView.ViewHolder {

    private Context context;

    private LinearLayout itemContainer;
    private ConstraintLayout chatBloke;
    private LinearLayout contentBloke;

    private AppCompatTextView voteDownTv;
    private AppCompatTextView voteUpTv;
    private AppCompatTextView signatureTv;
    private TextView viewsLabelTv;
    private AppCompatTextView editedIndicatorTv;
    private AppCompatTextView timeTv;
    private FontIconTextView ticTv;
    private View cslm_view_left_dis;
    private FontIconTextView eyeIconTv;
    private FontIconTextView voteDownIcon;
    private FontIconTextView voteUpIcon;

    private LinearLayout voteContainer;
    private LinearLayout viewContainer;
    private LinearLayout voteUpContainer;
    private LinearLayout voteDownContainer;

    private boolean inChannel = false;
    private boolean inChat = false;


    public NewChatItemHolder(@NonNull View itemView) {
        super(itemView);

        ConstraintSet set = new ConstraintSet();
        itemContainer = new LinearLayout(getContext());

        voteContainer = new LinearLayout(getContext());
        voteContainer.setId(R.id.ll_chatItem_vote);
        voteContainer.setOrientation(LinearLayout.HORIZONTAL);
        voteContainer.setGravity(Gravity.CENTER);

        viewContainer = new LinearLayout(getContext());
        viewContainer.setId(R.id.ll_chatItem_view);
        viewContainer.setGravity(Gravity.CENTER);

        voteUpContainer = new LinearLayout(getContext());
        voteUpContainer.setOrientation(LinearLayout.HORIZONTAL);
        voteUpContainer.setGravity(Gravity.CENTER);

        voteDownContainer = new LinearLayout(getContext());
        voteDownContainer.setOrientation(LinearLayout.HORIZONTAL);
        voteDownContainer.setGravity(Gravity.CENTER);

        chatBloke = new ConstraintLayout(getContext());
        chatBloke.setId(R.id.ll_chatItem_chatBloke);

        contentBloke = new LinearLayout(getContext());
        contentBloke.setId(R.id.ll_chatItem_contentBloke);

        voteDownTv = new AppCompatTextView(getContext());
        voteUpTv = new AppCompatTextView(getContext());
        signatureTv = new AppCompatTextView(getContext());
        signatureTv.setId(R.id.tv_chatItem_signature);

        viewsLabelTv = new TextView(getContext());
        viewsLabelTv.setId(R.id.tv_chatItem_viewLabel);
        viewsLabelTv.setTextAppearance(context, R.style.ChatMessages_Time);
        viewsLabelTv.setSingleLine(true);
        setTypeFace(viewsLabelTv);
        viewsLabelTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);

        eyeIconTv = new FontIconTextView(getContext());
        eyeIconTv.setId(R.id.ll_chatItem_viewIcon);
        eyeIconTv.setText("Ë");
        eyeIconTv.setTextColor(Color.parseColor(G.textBubble));
        eyeIconTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        editedIndicatorTv = new AppCompatTextView(getContext());
        editedIndicatorTv.setId(R.id.tv_chatItem_edited);

        ticTv = new FontIconTextView(getContext());
        ticTv.setId(R.id.tv_chatItem_status);
        ticTv.setTextSize(16);
        cslm_view_left_dis = new View(getContext());

        timeTv = new AppCompatTextView(getContext());
        timeTv.setId(R.id.tv_chatItem_time);

        voteUpTv.setTextAppearance(context, R.style.ChatMessages_Time);
        voteUpTv.setSingleLine(true);
        voteUpTv.setTextColor(Color.parseColor(G.voteIconTheme));
        setTypeFace(voteUpTv);
        voteUpTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);

        voteDownTv.setTextAppearance(context, R.style.ChatMessages_Time);
        voteDownTv.setSingleLine(true);
        voteDownTv.setTextColor(Color.parseColor(G.voteIconTheme));
        setTypeFace(voteUpTv);
        voteDownTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);

        itemContainer.setOrientation(LinearLayout.HORIZONTAL);
        itemContainer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        itemContainer.setPadding(ViewMaker.i_Dp(R.dimen.dp4), ViewMaker.i_Dp(R.dimen.dp4), ViewMaker.i_Dp(R.dimen.dp4), ViewMaker.i_Dp(R.dimen.dp4));

        contentBloke.setOrientation(LinearLayout.VERTICAL);

        chatBloke.setPadding(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(4));


        timeTv.setTextColor(Color.parseColor(G.textBubble));
        timeTv.setPadding(dpToPx(2), 0, dpToPx(2), 0);
        timeTv.setSingleLine(true);
        timeTv.setTextAppearance(getContext(), R.style.ChatMessages_Time);
        setTypeFace(timeTv);


        voteUpIcon = new FontIconTextView(context);
        voteUpIcon.setText("Ö");
        voteUpIcon.setGravity(BOTTOM);
        voteUpIcon.setTextColor(Color.parseColor(G.voteIconTheme));
        voteUpIcon.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

        voteDownIcon = new FontIconTextView(context);
        voteDownIcon.setText("Ü");
        voteDownIcon.setGravity(BOTTOM);
        voteDownIcon.setTextColor(Color.parseColor(G.voteIconTheme));
        voteDownIcon.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

        set.constrainHeight(contentBloke.getId(), ConstraintSet.WRAP_CONTENT);
        set.constrainWidth(contentBloke.getId(), ConstraintSet.WRAP_CONTENT);

        set.connect(contentBloke.getId(), ConstraintSet.BOTTOM, timeTv.getId(), ConstraintSet.TOP, dpToPx(6));

        set.constrainHeight(ticTv.getId(), ConstraintSet.WRAP_CONTENT);
        set.constrainWidth(ticTv.getId(), ConstraintSet.WRAP_CONTENT);

        set.connect(ticTv.getId(), ConstraintSet.RIGHT, chatBloke.getId(), ConstraintSet.RIGHT,dpToPx(4));
        set.connect(ticTv.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);

        set.constrainHeight(timeTv.getId(), ConstraintSet.WRAP_CONTENT);
        set.constrainWidth(timeTv.getId(), ConstraintSet.WRAP_CONTENT);

        set.connect(timeTv.getId(), ConstraintSet.RIGHT, ticTv.getId(), ConstraintSet.LEFT, dpToPx(4));
        set.connect(timeTv.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);


        set.constrainHeight(editedIndicatorTv.getId(), ConstraintSet.WRAP_CONTENT);
        set.constrainWidth(editedIndicatorTv.getId(), ConstraintSet.WRAP_CONTENT);

        set.constrainHeight(voteDownIcon.getId(), ConstraintSet.WRAP_CONTENT);
        set.constrainWidth(voteDownTv.getId(), ConstraintSet.WRAP_CONTENT);

        set.constrainHeight(signatureTv.getId(), ConstraintSet.WRAP_CONTENT);
        set.constrainWidth(signatureTv.getId(), ConstraintSet.WRAP_CONTENT);

        set.constrainHeight(voteContainer.getId(), ConstraintSet.WRAP_CONTENT);
        set.constrainWidth(voteContainer.getId(), ConstraintSet.MATCH_CONSTRAINT);

        set.constrainHeight(viewContainer.getId(), ConstraintSet.WRAP_CONTENT);
        set.constrainWidth(viewContainer.getId(), ConstraintSet.WRAP_CONTENT);

        viewContainer.addView(viewsLabelTv, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT,
                Gravity.CENTER, dpToPx(2), 0, dpToPx(1), 0));
        viewContainer.addView(eyeIconTv, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        set.connect(viewContainer.getId(), ConstraintSet.LEFT, chatBloke.getId(), ConstraintSet.LEFT);
        set.connect(viewContainer.getId(), ConstraintSet.TOP, timeTv.getId(), ConstraintSet.TOP);
        set.connect(viewContainer.getId(), ConstraintSet.BOTTOM, timeTv.getId(), ConstraintSet.BOTTOM);

        set.connect(voteContainer.getId(), ConstraintSet.LEFT, viewContainer.getId(), ConstraintSet.RIGHT, dpToPx(8));
        set.connect(voteContainer.getId(), ConstraintSet.TOP, timeTv.getId(), ConstraintSet.TOP);
        set.connect(voteContainer.getId(), ConstraintSet.BOTTOM, timeTv.getId(), ConstraintSet.BOTTOM);

        voteUpContainer.addView(voteUpIcon, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT,
                Gravity.CENTER, 0, 0, dpToPx(1), 0));
        voteUpContainer.addView(voteUpTv);

        voteDownContainer.addView(voteDownIcon, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT,
                Gravity.CENTER, 0, 0, dpToPx(1), 0));
        voteDownContainer.addView(voteDownTv);

        voteContainer.addView(voteUpContainer, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT,
                Gravity.CENTER, 0, 0, dpToPx(2), 0));
        voteContainer.addView(voteDownContainer);

        chatBloke.addView(contentBloke);
        chatBloke.addView(timeTv);
        chatBloke.addView(ticTv);
        chatBloke.addView(voteContainer);
        chatBloke.addView(viewContainer);

        set.applyTo(chatBloke);
        itemContainer.addView(chatBloke);

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


    protected void setTypeFace(TextView v) {
        ViewMaker.setTypeFace(v);
    }

    protected MessageProgress getProgressBar(int sizeSrc) {
        return ViewMaker.getProgressBar(sizeSrc);
    }

    public Context getContext() {
        context = G.context;
        return context;
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

    public AppCompatTextView getEditedIndicatorTv() {
        return editedIndicatorTv;
    }

    public AppCompatTextView getTimeTv() {
        return timeTv;
    }

    public FontIconTextView getTicTv() {
        return ticTv;
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
        return getContext().getResources();
    }

    public int getColor(int color) {
        return getResources().getColor(color);
    }

    public Drawable getDrawable(int drawableId) {
        return getResources().getDrawable(drawableId);
    }

    public FontIconTextView getVoteDownIcon() {
        return voteDownIcon;
    }

    public void setVoteDownIcon(FontIconTextView voteDownIcon) {
        this.voteDownIcon = voteDownIcon;
    }

    public FontIconTextView getVoteUpIcon() {
        return voteUpIcon;
    }

    public void setVoteUpIcon(FontIconTextView voteUpIcon) {
        this.voteUpIcon = voteUpIcon;
    }
}
