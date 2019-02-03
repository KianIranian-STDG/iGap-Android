package net.iGap.adapter.items.chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import net.iGap.R;
import net.iGap.messageprogress.MessageProgress;

import static android.widget.LinearLayout.HORIZONTAL;
import static android.widget.LinearLayout.VERTICAL;
import static net.iGap.G.context;

public class ChatItemHolder extends RecyclerView.ViewHolder {
    MessageProgress progress;
    View messageSenderAvatar;
    View messageSenderName;
    View messageSenderTextMessage;
    LinearLayout contentContainer;
    LinearLayout mainContainer;
    LinearLayout m_container;
    View csl_ll_time;

    public ChatItemHolder(View view) {
        super(view);

        mainContainer = itemView.findViewById(R.id.mainContainer);
        contentContainer = itemView.findViewById(R.id.contentContainer);
        m_container = itemView.findViewById(R.id.m_container);

        LinearLayout linearLayout_683;
        if (mainContainer == null) {
            mainContainer = new LinearLayout(context);
            mainContainer.setId(R.id.mainContainer);
            mainContainer.setOrientation(HORIZONTAL);
            LinearLayout.LayoutParams layout_216 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mainContainer.setLayoutParams(layout_216);

            linearLayout_683 = new LinearLayout(context);
            linearLayout_683.setOrientation(VERTICAL);
            LinearLayout.LayoutParams layout_584 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            linearLayout_683.setLayoutParams(layout_584);
            mainContainer.addView(linearLayout_683);

            ((ViewGroup) itemView).addView(mainContainer);

            contentContainer = new LinearLayout(context);
            contentContainer.setId(R.id.contentContainer);
            contentContainer.setOrientation(VERTICAL);
            LinearLayout.LayoutParams layout_617 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            contentContainer.setLayoutParams(layout_617);
            contentContainer.setPadding(ViewMaker.i_Dp(R.dimen.dp4), ViewMaker.i_Dp(R.dimen.dp4), ViewMaker.i_Dp(R.dimen.dp4), ViewMaker.i_Dp(R.dimen.dp4));
            linearLayout_683.addView(contentContainer);

            m_container = new LinearLayout(context);
            m_container.setId(R.id.m_container);
            m_container.setOrientation(VERTICAL);
            LinearLayout.LayoutParams layout_842 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            m_container.setLayoutParams(layout_842);
            contentContainer.addView(m_container);
        }


        progress = itemView.findViewById(R.id.progress);
        messageSenderAvatar = itemView.findViewById(R.id.messageSenderAvatar);
        messageSenderName = itemView.findViewById(R.id.messageSenderName);
        messageSenderTextMessage = itemView.findViewById(R.id.messageSenderTextMessage);
        csl_ll_time = itemView.findViewById(R.id.csl_ll_time);

    }
}

