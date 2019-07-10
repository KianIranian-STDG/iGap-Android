package net.iGap.adapter.items.chat;

import android.content.Context;
import android.content.res.Resources;
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
import net.iGap.libs.bottomNavigation.Util.Utils;
import net.iGap.messageprogress.MessageProgress;
import net.iGap.module.FontIconTextView;

import static android.view.Gravity.BOTTOM;
import static android.view.Gravity.CENTER;
import static android.view.Gravity.CENTER_VERTICAL;
import static android.view.Gravity.LEFT;

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
    private AppCompatTextView messageTimeTv;
    private FontIconTextView messageStatusTv;
    private View cslm_view_left_dis;
    private FontIconTextView eyeIconTv;
    private FontIconTextView voteDownIv;
    private FontIconTextView voteUpIv;

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
        contentBloke.setOrientation(LinearLayout.VERTICAL);

        voteDownTv = new AppCompatTextView(getContext());
        voteUpTv = new AppCompatTextView(getContext());
        signatureTv = new AppCompatTextView(getContext());
        signatureTv.setId(R.id.tv_chatItem_signature);
        setTextSize(signatureTv, R.dimen.verySmallTextSize);

        viewsLabelTv = new TextView(getContext());
        viewsLabelTv.setId(R.id.tv_chatItem_viewLabel);
        viewsLabelTv.setSingleLine(true);
        setTypeFace(viewsLabelTv);
        setTextSize(viewsLabelTv,R.dimen.verySmallTextSize);

        eyeIconTv = new FontIconTextView(getContext());
        eyeIconTv.setId(R.id.ll_chatItem_viewIcon);
        eyeIconTv.setText("Ë");
        eyeIconTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        editedIndicatorTv = new AppCompatTextView(getContext());
        editedIndicatorTv.setId(R.id.tv_chatItem_edited);
        setTextSize(editedIndicatorTv,R.dimen.smallTextSize);
        setTypeFace(editedIndicatorTv);
        editedIndicatorTv.setText(getResources().getString(R.string.edited));
        editedIndicatorTv.setGravity(LEFT);

        messageStatusTv = new FontIconTextView(getContext());
        messageStatusTv.setId(R.id.tv_chatItem_status);
        setTextSize(messageStatusTv,R.dimen.largeTextSize);
        cslm_view_left_dis = new View(getContext());

        messageTimeTv = new AppCompatTextView(getContext());
        messageTimeTv.setId(R.id.tv_chatItem_time);
        setTextSize(messageTimeTv,R.dimen.verySmallTextSize);

        voteUpTv.setSingleLine(true);
        setTextSize(voteUpTv, R.dimen.verySmallTextSize);
        setTypeFace(voteUpTv);

        voteDownTv.setSingleLine(true);
        setTypeFace(voteDownTv);
        setTextSize(voteDownTv, R.dimen.verySmallTextSize);


        itemContainer.setOrientation(LinearLayout.HORIZONTAL);
        itemContainer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        itemContainer.setPadding(ViewMaker.i_Dp(R.dimen.dp4), ViewMaker.i_Dp(R.dimen.dp4), ViewMaker.i_Dp(R.dimen.dp4), ViewMaker.i_Dp(R.dimen.dp4));

        chatBloke.setPadding(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(4));

        messageTimeTv.setPadding(dpToPx(2), 0, dpToPx(2), 0);
        messageTimeTv.setSingleLine(true);
        setTypeFace(messageTimeTv);


        voteUpIv = new FontIconTextView(context);
        voteUpIv.setText("Ö");
        voteUpIv.setGravity(BOTTOM);
        voteUpIv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

        voteDownIv = new FontIconTextView(context);
        voteDownIv.setText("Ü");
        voteDownIv.setGravity(BOTTOM);
        voteDownIv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);


        LinearLayout messageDetailContainer = new LinearLayout(getContext());
        messageDetailContainer.setOrientation(LinearLayout.HORIZONTAL);
        messageDetailContainer.setGravity(CENTER_VERTICAL);
        messageDetailContainer.setId(R.id.ll_chatItem_detailContainer);

        messageDetailContainer.addView(editedIndicatorTv,
                LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, CENTER,
                        4, 0, 4, 0));
        messageDetailContainer.addView(signatureTv,
                LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, CENTER,
                        4, 0, 4, 0));


        set.constrainWidth(messageDetailContainer.getId(), ConstraintSet.MATCH_CONSTRAINT);
        set.constrainHeight(messageDetailContainer.getId(), ConstraintSet.MATCH_CONSTRAINT);

        set.constrainHeight(contentBloke.getId(), ConstraintSet.WRAP_CONTENT);
        set.constrainWidth(contentBloke.getId(), ConstraintSet.WRAP_CONTENT);

        set.connect(contentBloke.getId(), ConstraintSet.BOTTOM, messageTimeTv.getId(), ConstraintSet.TOP, dpToPx(6));

        set.constrainHeight(messageStatusTv.getId(), ConstraintSet.WRAP_CONTENT);
        set.constrainWidth(messageStatusTv.getId(), ConstraintSet.WRAP_CONTENT);

        set.connect(messageStatusTv.getId(), ConstraintSet.RIGHT, chatBloke.getId(), ConstraintSet.RIGHT, dpToPx(4));
        set.connect(messageStatusTv.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);

        set.constrainHeight(messageTimeTv.getId(), ConstraintSet.WRAP_CONTENT);
        set.constrainWidth(messageTimeTv.getId(), ConstraintSet.WRAP_CONTENT);

        set.connect(messageTimeTv.getId(), ConstraintSet.RIGHT, messageStatusTv.getId(), ConstraintSet.LEFT, dpToPx(4));
        set.connect(messageTimeTv.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);

        set.constrainHeight(voteDownIv.getId(), ConstraintSet.WRAP_CONTENT);
        set.constrainWidth(voteDownTv.getId(), ConstraintSet.WRAP_CONTENT);

        set.constrainHeight(voteContainer.getId(), ConstraintSet.WRAP_CONTENT);
        set.constrainWidth(voteContainer.getId(), ConstraintSet.MATCH_CONSTRAINT);

        set.constrainHeight(viewContainer.getId(), ConstraintSet.WRAP_CONTENT);
        set.constrainWidth(viewContainer.getId(), ConstraintSet.WRAP_CONTENT);

        viewContainer.addView(viewsLabelTv, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT,
                Gravity.CENTER, dpToPx(2), 0, dpToPx(1), 0));
        viewContainer.addView(eyeIconTv, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        set.connect(viewContainer.getId(), ConstraintSet.LEFT, chatBloke.getId(), ConstraintSet.LEFT);
        set.connect(viewContainer.getId(), ConstraintSet.TOP, messageTimeTv.getId(), ConstraintSet.TOP);
        set.connect(viewContainer.getId(), ConstraintSet.BOTTOM, messageTimeTv.getId(), ConstraintSet.BOTTOM);

        set.connect(voteContainer.getId(), ConstraintSet.LEFT, viewContainer.getId(), ConstraintSet.RIGHT, dpToPx(8));
        set.connect(voteContainer.getId(), ConstraintSet.TOP, messageTimeTv.getId(), ConstraintSet.TOP);
        set.connect(voteContainer.getId(), ConstraintSet.BOTTOM, messageTimeTv.getId(), ConstraintSet.BOTTOM);

        set.connect(messageDetailContainer.getId(), ConstraintSet.RIGHT, messageTimeTv.getId(), ConstraintSet.LEFT);
        set.connect(messageDetailContainer.getId(), ConstraintSet.LEFT, voteContainer.getId(), ConstraintSet.RIGHT);
        set.connect(messageDetailContainer.getId(), ConstraintSet.TOP, messageTimeTv.getId(), ConstraintSet.TOP);
        set.connect(messageDetailContainer.getId(), ConstraintSet.BOTTOM, messageTimeTv.getId(), ConstraintSet.BOTTOM);

        voteUpContainer.addView(voteUpIv,
                LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT,
                        Gravity.CENTER, 0, 0, dpToPx(1), 0));
        voteUpContainer.addView(voteUpTv);

        voteDownContainer.addView(voteDownIv,
                LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT,
                        Gravity.CENTER, 0, 0, dpToPx(1), 0));
        voteDownContainer.addView(voteDownTv);

        voteContainer.addView(voteUpContainer,
                LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT,
                        Gravity.CENTER, 0, 0, dpToPx(2), 0));
        voteContainer.addView(voteDownContainer);

        chatBloke.addView(messageDetailContainer);
        chatBloke.addView(contentBloke);
        chatBloke.addView(messageTimeTv);
        chatBloke.addView(messageStatusTv);
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

    protected void setTypeFace(TextView textView) {
        textView.setTypeface(G.typeface_IRANSansMobile);
    }

    protected MessageProgress getProgressBar(int sizeSrc) {
        return ViewMaker.getProgressBar(sizeSrc);
    }

    public Context getContext() {
        context = G.context;
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

    public AppCompatTextView getEditedIndicatorTv() {
        return editedIndicatorTv;
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
        return getContext().getResources();
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
}
