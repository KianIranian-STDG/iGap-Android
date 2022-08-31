package net.iGap.messenger.ui.cell;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.theme.Theme;
import net.iGap.messenger.ui.components.RadioButton;
import net.iGap.module.customView.RecyclerListView;

import static net.iGap.messenger.theme.Theme.THEME_TYPE_DARK;
import static net.iGap.messenger.theme.Theme.THEME_TYPE_DAY;
import static net.iGap.messenger.theme.Theme.getColor;

public class ThemesHorizontalListCell extends RecyclerListView {

    public static byte[] bytes = new byte[1024];

    private boolean drawDivider;
    private LinearLayoutManager horizontalLayoutManager;
    private ThemesListAdapter adapter;

    public class ThemesListAdapter extends RecyclerListView.SelectionAdapter {

        private Context context;

        public ThemesListAdapter(Context context) {
            this.context = context;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case THEME_TYPE_DAY:
                    view = new InnerThemeView(context, context.getString(R.string.day), Theme.getColor(Theme.key_light_gray), Theme.getColor(Theme.key_light_theme_color), Theme.getColor(Theme.key_white), Theme.getCurrentThemeType() == THEME_TYPE_DAY);
                    break;
                case THEME_TYPE_DARK:
                    view = new InnerThemeView(context,context.getString(R.string.dark),Theme.getColor(Theme.key_gray),Theme.getColor(Theme.key_theme_color),Theme.getColor(Theme.key_dark_gray),Theme.getCurrentThemeType() == THEME_TYPE_DARK);
                    break;
                /*case THEME_TYPE_NIGHT:
                    view = new InnerThemeView(context,context.getString(R.string.night),Theme.getColor(Theme.key_dark_gray),Theme.getColor(Theme.key_dark_theme_color),Theme.getColor(Theme.key_black),Theme.getCurrentThemeType() == THEME_TYPE_NIGHT);
                    break;*/
            }
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            InnerThemeView innerThemeView = (InnerThemeView) holder.itemView;
            innerThemeView.setCellType(position);
            switch (position) {
                case THEME_TYPE_DAY:
                    innerThemeView.setColorsAndValue(Theme.getColor(Theme.key_light_gray), Theme.getColor(Theme.key_light_theme_color), Theme.getColor(Theme.key_white), Theme.getCurrentThemeType() == THEME_TYPE_DAY);
                    break;
                case THEME_TYPE_DARK:
                    innerThemeView.setColorsAndValue(Theme.getColor(Theme.key_gray),Theme.getColor(Theme.key_theme_color),Theme.getColor(Theme.key_dark_gray),Theme.getCurrentThemeType() == THEME_TYPE_DARK);
                    break;
                /*case THEME_TYPE_NIGHT:
                    innerThemeView.setColorsAndValue(Theme.getColor(Theme.key_dark_gray),Theme.getColor(Theme.key_dark_theme_color),Theme.getColor(Theme.key_black),Theme.getCurrentThemeType() == THEME_TYPE_NIGHT);
                    break;*/
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }
    }

    private class InnerThemeView extends FrameLayout {

        private RadioButton button;
        private RectF rect = new RectF();
        private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        private TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        private Drawable inDrawable;
        private Drawable outDrawable;
        private boolean isLast;
        private boolean isFirst;
        private boolean pressed;
        private Context context;
        private String text;
        private int inDrawableColor;
        private int outDrawableColor;
        private int backgroundColor;
        private boolean isChecked;
        private int cellType;

        public InnerThemeView(Context context,String text,int inDrawableColor,int outDrawableColor,int backgroundColor,boolean isChecked) {
            super(context);
            setWillNotDraw(false);

            this.context = context;
            this.text = text;
            this.inDrawableColor = inDrawableColor;
            this.outDrawableColor = outDrawableColor;
            this.backgroundColor = backgroundColor;
            this.isChecked = isChecked;

            inDrawable = context.getResources().getDrawable(R.drawable.minibubble_in).mutate();
            outDrawable = context.getResources().getDrawable(R.drawable.minibubble_out).mutate();

            inDrawable.setColorFilter(new PorterDuffColorFilter(inDrawableColor, PorterDuff.Mode.MULTIPLY));
            outDrawable.setColorFilter(new PorterDuffColorFilter(outDrawableColor, PorterDuff.Mode.MULTIPLY));

            textPaint.setTextSize(LayoutCreator.dp(13));
            textPaint.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));

            button = new RadioButton(context);
            button.setColor(getColor(Theme.key_light_theme_color),getColor(Theme.key_dark_theme_color));
            button.setChecked(isChecked,true);
            button.setSize(LayoutCreator.dp(20));
            addView(button, LayoutCreator.createFrame(22, 22, Gravity.LEFT | Gravity.TOP, 27, 75, 0, 0));
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(LayoutCreator.dp(106 + (isLast ? 22 : 15) + (isFirst ? 22 : 0)), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(148), MeasureSpec.EXACTLY));
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_UP) {
                float x = event.getX();
                float y = event.getY();
                if (x > rect.centerX() / 2 && y > rect.centerY() - LayoutCreator.dp(30)) {
                    if (action == MotionEvent.ACTION_DOWN) {
                        pressed = true;
                    } else {
                        performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
                        int colors = 7;
                        Theme.setCurrentThemeType(getCellType());
                        Theme.ThemeInfo themeInfo = Theme.themes.get((colors * Theme.getCurrentThemeType()) + Theme.findCurrentAccent());
                        Theme.setCurrentTheme(themeInfo);
                        setTheme();
                    }
                }
                if (action == MotionEvent.ACTION_UP) {
                    pressed = false;
                }
            }
            return pressed;
        }

        public int getCellType() {
            return cellType;
        }

        public void setCellType(int cellType) {
            this.cellType = cellType;
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
        }

        @Override
        protected void onDraw(Canvas canvas) {

            int x = isFirst ? LayoutCreator.dp(22) : 0;
            int y = LayoutCreator.dp(11);
            rect.set(x, y, x + LayoutCreator.dp(76), y + LayoutCreator.dp(97));

            int maxWidth = getMeasuredWidth() - LayoutCreator.dp(isFirst ? 10 : 15) - (isLast ? LayoutCreator.dp(7) : 0);
            text = TextUtils.ellipsize(text, textPaint, maxWidth, TextUtils.TruncateAt.END).toString();
            int width = (int) Math.ceil(textPaint.measureText(text));
            textPaint.setColor(Theme.getColor(Theme.key_default_text));
            canvas.drawText(text, x + (LayoutCreator.dp(76) - width) / 2, LayoutCreator.dp(131), textPaint);

            paint.setColor(backgroundColor);
            canvas.drawRoundRect(rect, LayoutCreator.dp(6), LayoutCreator.dp(6), paint);

            inDrawable.setBounds(x + LayoutCreator.dp(6), LayoutCreator.dp(22), x + LayoutCreator.dp(6 + 43), LayoutCreator.dp(22 + 14));
            inDrawable.draw(canvas);

            outDrawable.setBounds(x + LayoutCreator.dp(27), LayoutCreator.dp(41), x + LayoutCreator.dp(27 + 43), LayoutCreator.dp(41 + 14));
            outDrawable.draw(canvas);

            button.setAlpha(1.0f);
        }

        private String getTypeName() {
            return text;
        }

        @Override
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setText(getTypeName());
            info.setClassName(Button.class.getName());
            info.setChecked(button.isChecked());
            info.setCheckable(true);
            info.setEnabled(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK);
                info.addAction(new AccessibilityNodeInfo.AccessibilityAction(AccessibilityNodeInfo.ACTION_LONG_CLICK, getContext().getString( R.string.AccDescrMoreOptions)));
            }
        }

        public void setColorsAndValue(int inDrawableColor,int outDrawableColor,int backgroundColor,boolean isChecked) {
            inDrawable.setColorFilter(new PorterDuffColorFilter(inDrawableColor, PorterDuff.Mode.MULTIPLY));
            outDrawable.setColorFilter(new PorterDuffColorFilter(outDrawableColor, PorterDuff.Mode.MULTIPLY));
            button.setColor(getColor(Theme.key_light_theme_color),getColor(Theme.key_dark_theme_color));
            button.setChecked(isChecked,true);
            paint.setColor(backgroundColor);
            invalidateViews();
        }
    }

    public ThemesHorizontalListCell(Context context) {
        super(context);

        setBackgroundColor(Theme.getColor(Theme.key_window_background));
        setItemAnimator(null);
        setLayoutAnimation(null);
        horizontalLayoutManager = new LinearLayoutManager(context) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        setPadding(0, 0, 0, 0);
        setClipToPadding(false);
        horizontalLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        setLayoutManager(horizontalLayoutManager);
        setAdapter(adapter = new ThemesListAdapter(context));
        setOnItemClickListener((view1, position) -> {
            int left = view1.getLeft();
            int right = view1.getRight();
            if (left < 0) {
                smoothScrollBy(left - LayoutCreator.dp(8), 0);
            } else if (right > getMeasuredWidth()) {
                smoothScrollBy(right - getMeasuredWidth(), 0);
            }
        });
    }

    public void setDrawDivider(boolean draw) {
        drawDivider = draw;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (getParent() != null && getParent().getParent() != null) {
            getParent().getParent().requestDisallowInterceptTouchEvent(canScrollHorizontally(-1));
        }
        return super.onInterceptTouchEvent(e);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (drawDivider && Theme.dividerPaint != null) {
            canvas.drawLine(G.isAppRtl ? 0 : LayoutCreator.dp(20), getMeasuredHeight() - 1, getMeasuredWidth() - (G.isAppRtl  ? LayoutCreator.dp(20) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
        }
    }

    @Override
    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
        invalidateViews();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void setTheme() { }
}
