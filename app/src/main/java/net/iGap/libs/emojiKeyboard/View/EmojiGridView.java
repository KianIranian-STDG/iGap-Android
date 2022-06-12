package net.iGap.libs.emojiKeyboard.View;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.libs.emojiKeyboard.adapter.EmojiAdapter;
import net.iGap.libs.emojiKeyboard.struct.StructIGEmojiGroup;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.theme.Theme;

public class EmojiGridView extends FrameLayout implements EmojiAdapter.Listener {
    private AppCompatTextView emojiGroupNameTv;
    private EmojiAdapter adapter;

    private EmojiAdapter.Listener listener;

    public void setListener(EmojiAdapter.Listener listener) {
        this.listener = listener;
    }

    public EmojiGridView(@NonNull Context context) {
        super(context);
        boolean isRtl = G.isAppRtl;

        emojiGroupNameTv = new AppCompatTextView(getContext());
        emojiGroupNameTv.setTextColor(Theme.getColor(Theme.key_subtitle_text));
        emojiGroupNameTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        emojiGroupNameTv.setTypeface(ResourcesCompat.getFont(getContext(), R.font.main_font));
        emojiGroupNameTv.setLines(1);
        emojiGroupNameTv.setMaxLines(1);
        emojiGroupNameTv.setSingleLine(true);

        addView(emojiGroupNameTv, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, isRtl ? Gravity.RIGHT : Gravity.LEFT, isRtl ? 0 : 12, 4, isRtl ? 12 : 0, 0));

        adapter = new EmojiAdapter();
        adapter.setListener(this);

        RecyclerView recyclerView = new RecyclerView(getContext());
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), G.twoPaneMode ? 13 : 10, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        recyclerView.setClipToPadding(false);

        addView(recyclerView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.CENTER, 0, 24, 0, 10));

    }

    public void setEmojiGroup(StructIGEmojiGroup emojiGroup) {
        emojiGroupNameTv.setText(emojiGroup.getCategoryName());
        adapter.setStrings(emojiGroup.getStrings());
    }

    @Override
    public void onClick(String emojiCode) {
        listener.onClick(emojiCode);
    }

    @Override
    public boolean onLongClick(String emojiCode) {
        return listener.onLongClick(emojiCode);
    }
}
