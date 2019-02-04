package net.iGap.adapter.items.chat;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iGap.R;
import net.iGap.fragments.FragmentChat;
import net.iGap.messageprogress.MessageProgress;

import static android.widget.LinearLayout.HORIZONTAL;
import static android.widget.LinearLayout.VERTICAL;
import static android.support.design.R.id.center;
import static net.iGap.G.context;

public class ChatItemHolder extends RecyclerView.ViewHolder {
    MessageProgress progress;
    View messageSenderAvatar;
    View messageSenderName;
    TextView messageSenderTextMessage;
    LinearLayout contentContainer;
    LinearLayout mainContainer;
    LinearLayout m_container;
    LinearLayout csl_ll_time;
    LinearLayout lyt_see;
    LinearLayout lyt_vote;

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

        csl_ll_time = ViewMaker.getViewTime();
        lyt_vote = ViewMaker.getViewVote();
        lyt_vote.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        lyt_see = ViewMaker.getViewSeen();
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

        mainContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.d("bagi" , "itemViewLongClick");
                if (!FragmentChat.isInSelectionMode && (
                        ChatItemHolder.this instanceof VoiceItem.ViewHolder)
                ) {
                    return true;
                }

                itemView.performLongClick();
                return true;
            }
        });

        progress = itemView.findViewById(R.id.progress);
        messageSenderAvatar = itemView.findViewById(R.id.messageSenderAvatar);
        messageSenderName = itemView.findViewById(R.id.messageSenderName);
        messageSenderTextMessage = itemView.findViewById(R.id.messageSenderTextMessage);
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

    protected View getProgressBar(int sizeSrc){
        return ViewMaker.getProgressBar(sizeSrc);
    }

    protected View getHorizontalSpace(int size) {
        return ViewMaker.getHorizontalSpace(size);
    }

    protected View getVerticalSpace(int size) {
        return ViewMaker.getVerticalSpace(size);
    }

}

