package net.iGap.messenger.ui.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.theme.Theme;
import net.iGap.module.customView.RecyclerListView;

public class TintRecyclerListView extends RecyclerListView {

    private Context context;

    private class ThemeAccentsCell extends FrameLayout {
        private final TextView textView;
        private final TextView icon;
        private final Paint paint;
        private final int circleWidth = LayoutCreator.dp(38);
        private final int padding = LayoutCreator.dp(12);
        private final boolean isRtl = G.isAppRtl;
        private boolean needDivider;
        private boolean pressed;
        private RectF rectF;
        private int cellPosition;


        public ThemeAccentsCell(@NonNull Context context) {
            super(context);

            setWillNotDraw(false);

            icon = new TextView(context);
            icon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
            icon.setTypeface(ResourcesCompat.getFont(context, R.font.font_icon_new));
            icon.setGravity(Gravity.CENTER);
            icon.setVisibility(INVISIBLE);
            addView(icon, LayoutCreator.createFrame(LayoutCreator.dp(30), LayoutCreator.WRAP_CONTENT, Gravity.TOP | Gravity.CENTER_HORIZONTAL, padding, 16, padding, 8));

            textView = new TextView(context);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
            textView.setSingleLine();
            textView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setGravity(Gravity.CENTER);
            addView(textView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, isRtl ? Gravity.RIGHT : Gravity.LEFT, 0, 58, 0, 12));

            paint = new Paint(Paint.UNDERLINE_TEXT_FLAG);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(LayoutCreator.dp(76), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(90), MeasureSpec.EXACTLY));
        }

        @SuppressLint("DrawAllocation")
        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            int layoutWidth = right - left;
            int circleX = layoutWidth / 2 - circleWidth / 2;
            int circleY = padding;
            rectF = new RectF(circleX, circleY, circleX + circleWidth, circleY + circleWidth);
            super.onLayout(changed, left, top, right, bottom);
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_UP) {
                float x = event.getX();
                float y = event.getY();
                float centerX = rectF.centerX();
                float centerY = rectF.centerY();
                if (x > centerX / 2 && y < centerY + LayoutCreator.dp(30)) {
                    if (action == MotionEvent.ACTION_DOWN) {
                        pressed = true;
                    } else {
                        performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
                        int colors = 7;
                        Theme.ThemeInfo themeInfo = Theme.themes.get((colors * Theme.getCurrentThemeType()) + getCellPosition());
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

        @Override
        @SuppressLint("DrawAllocation")
        public void onDraw(Canvas canvas) {
            canvas.drawArc(rectF, 0, 360, true, paint);
            if (needDivider) {
                canvas.drawLine(isRtl ? 0 : LayoutCreator.dp(20), getMeasuredHeight() - 1, getMeasuredWidth() - (isRtl ? LayoutCreator.dp(20) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
            }
        }

        public void setValues(String text, String icon, boolean selected) {
            setValues(text, false, icon, selected);
        }

        public void setValues(String text, boolean needDivider, String icon, boolean selected) {
            this.needDivider = needDivider;
            textView.setText(text);
            this.icon.setVisibility(selected ? VISIBLE : INVISIBLE);
            this.icon.setText(icon);
            invalidate();
        }

        public void setValuesColor(int textColor, int iconColor, int circleColor) {
            textView.setTextColor(textColor);
            icon.setTextColor(iconColor);
            paint.setColor(circleColor);
        }

        public int getCellPosition() {
            return cellPosition;
        }

        public void setCellPosition(int cellPosition) {
            this.cellPosition = cellPosition;
        }

        public void setTextColor(int color) {
            textView.setTextColor(color);
            invalidate();
        }
    }

    public class ThemeAccentsListAdapter extends RecyclerListView.SelectionAdapter {

        private final Context context;
        private final int green = 0;
        private final int yellow = 1;
        private final int red = 2;
        private final int pink = 3;
        private final int cyan = 4;
        private final int blue = 5;
        private final int orange = 6;
        private int colors = 7;

        public ThemeAccentsListAdapter(Context context) {
            this.context = context;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        @Override
        public int getItemViewType(int position) {
            return position == getItemCount() - 1 ? 1 : 0;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ThemeAccentsCell themeAccentsCell = new ThemeAccentsCell(context);
            return new RecyclerListView.Holder(themeAccentsCell);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ThemeAccentsCell themeAccentsCell = (ThemeAccentsCell) holder.itemView;
            themeAccentsCell.setCellPosition(position);
            int iconColor = Theme.getColor(Theme.key_white);
            if (position == green) {
                themeAccentsCell.setValues(getContext().getString(R.string.green), getResources().getString(R.string.ic_check), Theme.findCurrentAccent() == green);
                themeAccentsCell.setValuesColor(Theme.getColor(Theme.key_default_text), iconColor, getResources().getColor(R.color.dayGreenTheme));
            }  else if (position == red) {
                themeAccentsCell.setValues(getContext().getString(R.string.red), getResources().getString(R.string.ic_check), Theme.findCurrentAccent() == red);
                themeAccentsCell.setValuesColor(Theme.getColor(Theme.key_default_text), iconColor, getResources().getColor(R.color.dayRedTheme));
            } else if (position == pink) {
                themeAccentsCell.setValues(getContext().getString(R.string.pink), getResources().getString(R.string.ic_check), Theme.findCurrentAccent() == pink);
                themeAccentsCell.setValuesColor(Theme.getColor(Theme.key_default_text), iconColor, getResources().getColor(R.color.dayPinkTheme));
            } else if (position == cyan) {
                themeAccentsCell.setValues(getContext().getString(R.string.cyan), getResources().getString(R.string.ic_check), Theme.findCurrentAccent() == cyan);
                themeAccentsCell.setValuesColor(Theme.getColor(Theme.key_default_text), iconColor, getResources().getColor(R.color.dayCyanTheme));
            } else if (position == blue) {
                themeAccentsCell.setValues(getContext().getString(R.string.blue), getResources().getString(R.string.ic_check), Theme.findCurrentAccent() == blue);
                themeAccentsCell.setValuesColor(Theme.getColor(Theme.key_default_text), iconColor, getResources().getColor(R.color.dayBlueTheme));
            } else if (position == yellow) {
                themeAccentsCell.setValues(getContext().getString(R.string.yellow), getResources().getString(R.string.ic_check), Theme.findCurrentAccent() == yellow);
                themeAccentsCell.setValuesColor(Theme.getColor(Theme.key_default_text), iconColor, getResources().getColor(R.color.dayYellowTheme));
            } else if (position == orange) {
                themeAccentsCell.setValues(getContext().getString(R.string.orange), getResources().getString(R.string.ic_check), Theme.findCurrentAccent() == orange);
                themeAccentsCell.setValuesColor(Theme.getColor(Theme.key_default_text), iconColor, getResources().getColor(R.color.dayOrangeTheme));
            }
        }

        @Override
        public int getItemCount() {
            return colors;
        }
    }

    public TintRecyclerListView(Context context) {
        super(context);
        this.context = context;
        setFocusable(false);
        setBackgroundColor(Theme.getColor(Theme.key_window_background));
        setItemAnimator(null);
        setLayoutAnimation(null);
        setPadding(LayoutCreator.dp(11), 0, LayoutCreator.dp(11), 0);
        setClipToPadding(false);
        LinearLayoutManager accentsLayoutManager = new LinearLayoutManager(context);
        accentsLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        setLayoutManager(accentsLayoutManager);
        setAdapter(new ThemeAccentsListAdapter(context));
    }

    public void setTheme() { }
}
