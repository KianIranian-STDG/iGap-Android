package net.iGap.adapter.items.chat;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import net.iGap.G;
import net.iGap.fragments.FragmentChat;
import net.iGap.module.EmojiTextViewE;

public class ChatItemWithTextHolder extends NewChatItemHolder {
    EmojiTextViewE messageView;
    LinearLayout BtnContainer;
    private LinearLayout layoutMessageContainer;

    public ChatItemWithTextHolder(View view) {
        super(view);
    }

    protected void setLayoutMessageContainer(LinearLayout.LayoutParams layout_param) {
        initViews();
        layoutMessageContainer.setLayoutParams(layout_param);
        getContentBloke().addView(layoutMessageContainer);
    }

    protected void setLayoutMessageContainer() {
        initViews();
        getContentBloke().addView(layoutMessageContainer);
    }

    public void addButtonLayout(LinearLayout view) {
        BtnContainer.addView(view);
    }

    public void removeButtonLayout() {
        BtnContainer.removeAllViews();
    }

    private void initViews() {
        messageView = ViewMaker.makeTextViewMessage();
        layoutMessageContainer = ViewMaker.getTextView();
        layoutMessageContainer.addView(messageView);

        BtnContainer = new LinearLayout(G.context);
        BtnContainer.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layout_327 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        BtnContainer.setLayoutParams(layout_327);

        layoutMessageContainer.addView(BtnContainer);

        messageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FragmentChat.isInSelectionMode) {
                    itemView.performLongClick();
                } else {
                    getItemContainer().performClick();
                }

            }
        });

        messageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                itemView.performLongClick();
                return true;
            }
        });

    }

}

