package net.iGap.story.storyviews;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.ui.components.IconView;
import net.iGap.module.CircleImageView;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.Theme;
import net.iGap.story.liststories.ImageLoadingView;

import static android.widget.Toast.LENGTH_SHORT;

public class StoryCell extends FrameLayout {

    private CircleImageView circleImage;
    private ImageLoadingView circleImageLoading;
    private TextView topText;
    private TextView bottomText;
    private IconView icon;
    private IconView icon2;
    private MaterialDesignTextView addIcon;
    private Context context;
    private int padding = 16;
    private boolean isRtl = G.isAppRtl;
    private boolean needDivider;
    private IconClicked iconClicked;

    public enum Status {CIRCLE_IMAGE, LOADING_CIRCLE_IMAGE}

    private Status status;

    public StoryCell(@NonNull Context context, boolean needDivider, Status status) {
        this(context, needDivider, status, null);
    }

    public StoryCell(@NonNull Context context, boolean needDivider, Status status, IconClicked iconClicked) {
        super(context);
        this.status = status;
        this.needDivider = needDivider;
        this.iconClicked = iconClicked;
        this.context = context;
        setWillNotDraw(!needDivider);
        View view;

        switch (this.status) {
            case CIRCLE_IMAGE:
                circleImage = new CircleImageView(getContext());
                circleImage.setLayoutParams(LayoutCreator.createFrame(56, 56, (isRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL, isRtl ? 0 : padding, 8, isRtl ? padding : 0, 8));
                view = circleImage;
                break;
            case LOADING_CIRCLE_IMAGE:
                circleImageLoading = new ImageLoadingView(context);
                circleImageLoading.setStatus(ImageLoadingView.Status.LOADING);
                circleImageLoading.setLayoutParams(LayoutCreator.createFrame(72, 72, (isRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL, isRtl ? 0 : 8, 8, isRtl ? 8 : 0, 8));
                view = circleImageLoading;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + this.status);
        }
        addView(view);

        addIcon = new MaterialDesignTextView(context);
        addIcon.setText(R.string.add_icon_2);
        addIcon.setTextColor(getResources().getColor(R.color.green));
        addIcon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        addIcon.setGravity(isRtl ? Gravity.LEFT : Gravity.RIGHT);
        addIcon.setVisibility(GONE);
        addView(addIcon, LayoutCreator.createFrame(18, 18, (isRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.BOTTOM, isRtl ? 0 : padding, 8, isRtl ? padding : 0, 8));

        topText = new TextView(context);
        topText.setSingleLine();
        topText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        topText.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        topText.setGravity((isRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP);
        addView(topText, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, (isRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, isRtl ? padding : ((padding * 2) + 56), 11.5f, isRtl ? ((padding * 2) + 56) : padding, 0));

        bottomText = new TextView(context);
        bottomText.setSingleLine();
        bottomText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        bottomText.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        bottomText.setGravity((isRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP);
        addView(bottomText, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, (isRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, isRtl ? padding : ((padding * 2) + 56), 34.5f, isRtl ? ((padding * 2) + 56) : padding, 0));

        icon = new IconView(getContext());
        icon.setTypeface(ResourcesCompat.getFont(context, R.font.font_icon));
        icon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        addView(icon, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, (isRtl ? Gravity.LEFT : Gravity.RIGHT) | Gravity.CENTER_VERTICAL, padding, 0, padding, 0));

        icon2 = new IconView(getContext());
        icon2.setTypeface(ResourcesCompat.getFont(context, R.font.font_icon));
        icon2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        addView(icon2, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, (isRtl ? Gravity.LEFT : Gravity.RIGHT) | Gravity.CENTER_VERTICAL, isRtl ? (padding + 30) : padding, 0, isRtl ? padding : (30 + padding), 0));

        // this.iconClicked.clickedIcon(icon, icon2);


        if (status == Status.LOADING_CIRCLE_IMAGE) {
            setOnClickListener(v -> {
                Log.i("nazanin", "StoryCell: ");
                switch (circleImageLoading.getStatus()) {
                    case UNCLICKED:
                        circleImageLoading.setStatus(ImageLoadingView.Status.LOADING);
                        break;
                    case LOADING:
                        circleImageLoading.setStatus(ImageLoadingView.Status.CLICKED);
                        break;
                    case CLICKED:
                        circleImageLoading.setStatus(ImageLoadingView.Status.UNCLICKED);
                }
                Toast.makeText(getContext(), "click !", LENGTH_SHORT).show();
            });
            circleImageLoading.setOnLongClickListener(v -> {
                Toast.makeText(getContext(), "long click !", LENGTH_SHORT).show();
                return true;
            });
        }

    }

    public void setText(String topText, String bottomText) {
        this.topText.setText(topText);
        this.bottomText.setText(bottomText);
    }

    public void setTextColor(int colorLeftText, int colorBottomText) {
        this.topText.setTextColor(colorLeftText);
        this.bottomText.setTextColor(colorBottomText);
    }

    public void setTextSize(int topTextSize, int bottomTextSize) {
        this.topText.setTextSize(topTextSize);
        this.bottomText.setTextSize(bottomTextSize);
    }

    public void setTextStyle(int typeFace) {
        topText.setTypeface(ResourcesCompat.getFont(context, typeFace));
    }

    public void setIcons(int icon, int icon2) {
        this.icon.setIcon(icon);
        this.icon2.setIcon(icon2);
    }

    public void setIconsColor(int color, int color2) {
        this.icon.setTextColor(color);
        this.icon2.setTextColor(color2);
    }

    public void setImage(int imageId) {
        switch (status) {
            case CIRCLE_IMAGE:
                this.circleImage.setImageResource(imageId);
                break;
            case LOADING_CIRCLE_IMAGE:
                this.circleImageLoading.setImageResource(imageId);
        }
    }

    public void addIconVisibility(boolean visible) {
        addIcon.setVisibility(visible ? VISIBLE : GONE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(72) + (needDivider ? 1 : 0), MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(isRtl ? 0 : LayoutCreator.dp(21), getMeasuredHeight() - 1, getMeasuredWidth() - (isRtl ? LayoutCreator.dp(21) : 0), getMeasuredHeight() - 1, Theme.getInstance().getDividerPaint(getContext()));
        }
    }

    public interface IconClicked {
        void clickedIcon(View icon, View icon2);
    }

}
