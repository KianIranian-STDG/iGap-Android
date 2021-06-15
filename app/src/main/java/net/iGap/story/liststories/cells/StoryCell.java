package net.iGap.story.liststories.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.story.liststories.ImageLoadingView;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.widget.Toast.LENGTH_SHORT;

public class StoryCell extends FrameLayout {
    private final ImageLoadingView imageView;
    private final TextView accountName;
    private final TextView createTime;
    private Context context;
    private boolean hasStroke;

    private final boolean isRtl = G.isAppRtl;

    public StoryCell(Context context) {
        super(context);

        setWillNotDraw(false);

        imageView = new ImageLoadingView(context);
        imageView.setStatus(ImageLoadingView.Status.LOADING);
        imageView.setPadding(10, 10, 10, 10);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (imageView.getStatus()) {
                    case UNCLICKED:
                        imageView.setStatus(ImageLoadingView.Status.LOADING);
                        break;
                    case LOADING:
                        imageView.setStatus(ImageLoadingView.Status.CLICKED);
                        break;
                    case CLICKED:
                        imageView.setStatus(ImageLoadingView.Status.UNCLICKED);
                }
                Toast.makeText(getContext(), "click !", LENGTH_SHORT).show();
            }
        });
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getContext(), "long click !", LENGTH_SHORT).show();
                return true;
            }
        });
        addView(imageView);


        accountName = new TextView(context);
        accountName.setGravity(isRtl ? Gravity.RIGHT : Gravity.LEFT);
        accountName.setPadding(0, 0, 0, LayoutCreator.dp(1));
        accountName.setText("نام اکانت کاربری");
        accountName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        accountName.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        accountName.setSingleLine();
        addView(accountName);

        createTime = new TextView(context);
        createTime.setGravity(isRtl ? Gravity.RIGHT : Gravity.LEFT);
        createTime.setSingleLine();
        createTime.setText("امروز 13:40");
        createTime.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        createTime.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        createTime.setTextColor(net.iGap.module.Theme.getInstance().getSubTitleColor(context));
        addView(createTime);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        imageView.measure(MeasureSpec.makeMeasureSpec(LayoutCreator.dp(64), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(65), MeasureSpec.EXACTLY));
        accountName.measure(MeasureSpec.makeMeasureSpec((MeasureSpec.getSize(widthMeasureSpec) - imageView.getMeasuredWidth() - LayoutCreator.dp(16)), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(74) / 2 - LayoutCreator.dp(8), MeasureSpec.EXACTLY));
        createTime.measure(MeasureSpec.makeMeasureSpec((MeasureSpec.getSize(widthMeasureSpec) - imageView.getMeasuredWidth() - LayoutCreator.dp(16)), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(74) / 2 - LayoutCreator.dp(8), MeasureSpec.EXACTLY));
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), LayoutCreator.dp(74));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        int imageViewTop = (getMeasuredHeight() - LayoutCreator.dp(64)) / 2;
        int imageViewBottom = imageViewTop + LayoutCreator.dp(64);
        int imageViewLeft = isRtl ? getMeasuredWidth() - LayoutCreator.dp(64) - LayoutCreator.dp(8) : LayoutCreator.dp(8);
        int imageViewRight = isRtl ? getMeasuredWidth() - LayoutCreator.dp(8) : LayoutCreator.dp(8) + LayoutCreator.dp(64);
        imageView.layout(imageViewLeft, imageViewTop, imageViewRight, imageViewBottom);

        int accountNameTop = LayoutCreator.dp(8);
        int accountNameBottom = accountNameTop + accountName.getMeasuredHeight();
        int accountNameLeft = isRtl ? imageViewLeft - accountName.getMeasuredWidth() - LayoutCreator.dp(8) : imageViewRight + LayoutCreator.dp(8);
        int accountNameRight = isRtl ? imageViewLeft - LayoutCreator.dp(8) : accountNameLeft + accountName.getMeasuredWidth();
        accountName.layout(accountNameLeft, accountNameTop, accountNameRight, accountNameBottom);

        int createTimeBottom = getMeasuredHeight() - LayoutCreator.dp(8);
        int createTimeTop = createTimeBottom - createTime.getMeasuredHeight();
        int createTimeLeft = isRtl ? imageViewLeft - createTime.getMeasuredWidth() - LayoutCreator.dp(8) : imageViewRight + LayoutCreator.dp(8);
        int createTimeRight = isRtl ? imageViewLeft - LayoutCreator.dp(8) : accountNameLeft + accountName.getMeasuredWidth();
        createTime.layout(createTimeLeft, createTimeTop, createTimeRight, createTimeBottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawLine(isRtl ? 4 : imageView.getRight(), getMeasuredHeight() - 1, isRtl ? imageView.getLeft() : getWidth(), getMeasuredHeight(), net.iGap.module.Theme.getInstance().getDividerPaint(getContext()));
        super.onDraw(canvas);
    }

}
