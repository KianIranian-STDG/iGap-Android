package net.iGap.story.liststories.cells;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.module.MaterialDesignTextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddStoryCell extends FrameLayout {
    private final CircleImageView avatarImage;
    private final TextView storyName;
    private final TextView description;
    private final MaterialDesignTextView addIcon;

    private final boolean isRtl = G.isAppRtl;

    public AddStoryCell(Context context) {
        super(context);

        avatarImage = new CircleImageView(context);
        avatarImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.avatar));
        avatarImage.setPadding(2, 2, 2, 2);
        addView(avatarImage);

        storyName = new TextView(context);
        storyName.setGravity(isRtl ? Gravity.RIGHT : Gravity.LEFT);
        storyName.setPadding(0, 0, 0, LayoutCreator.dp(1));
        storyName.setText("داستان من");
        storyName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        storyName.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        storyName.setSingleLine();
        addView(storyName);

        description = new TextView(context);
        description.setGravity(isRtl ? Gravity.RIGHT : Gravity.LEFT);
        description.setSingleLine();
        description.setText("برای افزودن به روزرسانی داستان ، ضربه بزنید");
        description.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        description.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        description.setTextColor(net.iGap.module.Theme.getInstance().getSubTitleColor(context));
        addView(description);

        addIcon = new MaterialDesignTextView(context);
        addIcon.setText(R.string.add_icon_2);
        addIcon.setTextColor(getResources().getColor(R.color.green));
        addIcon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        addIcon.setGravity(isRtl ? Gravity.LEFT : Gravity.RIGHT);
        addView(addIcon);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        avatarImage.measure(MeasureSpec.makeMeasureSpec(LayoutCreator.dp(55), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(55), MeasureSpec.EXACTLY));
        storyName.measure(MeasureSpec.makeMeasureSpec((MeasureSpec.getSize(widthMeasureSpec) - avatarImage.getMeasuredWidth() - LayoutCreator.dp(16)), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(65) / 2 - LayoutCreator.dp(8), MeasureSpec.EXACTLY));
        description.measure(MeasureSpec.makeMeasureSpec((MeasureSpec.getSize(widthMeasureSpec) - avatarImage.getMeasuredWidth() - addIcon.getMeasuredWidth() - LayoutCreator.dp(16)), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(65) / 2 - LayoutCreator.dp(8), MeasureSpec.EXACTLY));
        addIcon.measure(MeasureSpec.makeMeasureSpec(LayoutCreator.dp(18), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(18), MeasureSpec.EXACTLY));
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), LayoutCreator.dp(65));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        int avatarImageTop = (getMeasuredHeight() - avatarImage.getMeasuredHeight()) / 2;
        int avatarImageBottom = avatarImageTop + avatarImage.getMeasuredHeight();
        int avatarImageLeft = isRtl ? getMeasuredWidth() - avatarImage.getMeasuredWidth() - LayoutCreator.dp(8) : LayoutCreator.dp(8);
        int avatarImageRight = isRtl ? getMeasuredWidth() - LayoutCreator.dp(8) : LayoutCreator.dp(8) + avatarImage.getMeasuredWidth();

        avatarImage.layout(avatarImageLeft, avatarImageTop, avatarImageRight, avatarImageBottom);

        int storyNameTop = LayoutCreator.dp(8);
        int storyNameBottom = storyNameTop + storyName.getMeasuredHeight();
        int storyNameLeft = isRtl ? avatarImageLeft - storyName.getMeasuredWidth() - LayoutCreator.dp(8) : avatarImageRight + LayoutCreator.dp(8);
        int storyNameRight = isRtl ? avatarImageLeft - LayoutCreator.dp(8) : storyNameLeft + storyName.getMeasuredWidth();
        storyName.layout(storyNameLeft, storyNameTop, storyNameRight, storyNameBottom);

        int iconViewBottom = getMeasuredHeight() - LayoutCreator.dp(4);
        int iconViewTop = iconViewBottom - addIcon.getMeasuredHeight();
        int iconViewLeft = isRtl ? getMeasuredWidth() - addIcon.getMeasuredWidth() - LayoutCreator.dp(4) : LayoutCreator.dp(4);
        int iconViewRight = isRtl ? getMeasuredWidth() - LayoutCreator.dp(4) : LayoutCreator.dp(4) + addIcon.getMeasuredWidth();
        addIcon.layout(iconViewLeft, iconViewTop, iconViewRight, iconViewBottom);

        int descriptionBottom = getMeasuredHeight() - LayoutCreator.dp(8);
        int descriptionTop = descriptionBottom - description.getMeasuredHeight();
        int descriptionLeft = isRtl ? avatarImageLeft - description.getMeasuredWidth() - LayoutCreator.dp(8) : avatarImageRight + LayoutCreator.dp(8);
        int descriptionRight = isRtl ? avatarImageLeft - LayoutCreator.dp(8) : storyNameLeft + storyName.getMeasuredWidth();
        description.layout(descriptionLeft, descriptionTop, descriptionRight, descriptionBottom);
    }
}
