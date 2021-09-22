package net.iGap.messenger.ui.toolBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EdgeEffect;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.FileLog;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.ui.components.LineProgressView;
import net.iGap.module.customView.RadialProgressView;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static net.iGap.G.context;
import static net.iGap.module.AndroidUtils.displaySize;

public class AlertDialog extends Dialog implements Drawable.Callback {

    private View customView;
    private int customViewHeight = LayoutCreator.WRAP_CONTENT;
    private TextView titleTextView;
    private TextView secondTitleTextView;
    private TextView subtitleTextView;
    private TextView messageTextView;
    private FrameLayout progressViewContainer;
    private FrameLayout titleContainer;
    private TextView progressViewTextView;
    private ScrollView contentScrollView;
    private LinearLayout scrollContainer;
    private ViewTreeObserver.OnScrollChangedListener onScrollChangedListener;
    private BitmapDrawable[] shadow = new BitmapDrawable[2];
    private boolean[] shadowVisibility = new boolean[2];
    private AnimatorSet[] shadowAnimation = new AnimatorSet[2];
    private int customViewOffset = 20;

    private String dialogButtonColorKey = Theme.key_dialogButton;

    private OnCancelListener onCancelListener;

    private AlertDialog cancelDialog;

    private int lastScreenWidth;

    private OnClickListener onClickListener;
    private OnDismissListener onDismissListener;

    private CharSequence[] items;
    private int[] itemIcons;
    private CharSequence title;
    private CharSequence secondTitle;
    private CharSequence subtitle;
    private CharSequence message;
    private int topResId;
    private View topView;
    private int topAnimationId;
    private int topHeight = 132;
    private Drawable topDrawable;
    private int topBackgroundColor;
    private int progressViewStyle;
    private int currentProgress;

    private boolean messageTextViewClickable = true;

    private boolean canCacnel = true;

    private boolean dismissDialogByButtons = true;
    private boolean drawBackground;
    private boolean notDrawBackgroundOnTopView;
    private CharSequence positiveButtonText;
    private OnClickListener positiveButtonListener;
    private CharSequence negativeButtonText;
    private OnClickListener negativeButtonListener;
    private CharSequence neutralButtonText;
    private OnClickListener neutralButtonListener;
    protected ViewGroup buttonsLayout;
    private LineProgressView lineProgressView;
    private TextView lineProgressViewPercent;
    private OnClickListener onBackButtonListener;

    private boolean checkFocusable = true;

    private Drawable shadowDrawable;
    private Rect backgroundPaddings;

    private boolean focusable;

    private boolean verticalButtons;

    private Runnable dismissRunnable = this::dismiss;
    private Runnable showRunnable = () -> {
        if (isShowing()) {
            return;
        }
        try {
            show();
        } catch (Exception ignore) {

        }
    };

    private ArrayList<AlertDialogCell> itemViews = new ArrayList<>();
    private float aspectRatio;
    private boolean dimEnabled = true;

    public static class AlertDialogCell extends FrameLayout {

        private TextView textView;
        private ImageView imageView;

        public AlertDialogCell(Context context) {
            super(context);

            setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getDefaultColor(Theme.key_dialogButtonSelector), 2));
            setPadding(LayoutCreator.dp(23), 0, LayoutCreator.dp(23), 0);

            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            imageView.setColorFilter(new PorterDuffColorFilter(Theme.getDefaultColor(Theme.key_dialogIcon), PorterDuff.Mode.MULTIPLY));
            addView(imageView, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, 40, Gravity.CENTER_VERTICAL | (G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT)));

            textView = new TextView(context);
            textView.setLines(1);
            textView.setSingleLine(true);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setTextColor(Theme.getDefaultColor(Theme.key_dialogTextBlack));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            addView(textView, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, (G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL));
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(LayoutCreator.dp(48), MeasureSpec.EXACTLY));
        }

        public void setTextColor(int color) {
            textView.setTextColor(color);
        }

        public void setGravity(int gravity) {
            textView.setGravity(gravity);
        }

        public void setTextAndIcon(CharSequence text, int icon) {
            textView.setText(text);
            if (icon != 0) {
                imageView.setImageResource(icon);
                imageView.setVisibility(VISIBLE);
                textView.setPadding(G.isAppRtl ? 0 : LayoutCreator.dp(56), 0, G.isAppRtl ? LayoutCreator.dp(56) : 0, 0);
            } else {
                imageView.setVisibility(INVISIBLE);
                textView.setPadding(0, 0, 0, 0);
            }
        }
    }

    public AlertDialog(Context context, int progressStyle) {
        super(context, R.style.TransparentDialog);

        backgroundPaddings = new Rect();
        if (progressStyle != 3) {
            shadowDrawable = context.getResources().getDrawable(R.drawable.popup_fixed_alert).mutate();
            shadowDrawable.setColorFilter(new PorterDuffColorFilter(getThemeColor(Theme.key_dialogBackground), PorterDuff.Mode.MULTIPLY));
            shadowDrawable.getPadding(backgroundPaddings);
        }

        progressViewStyle = progressStyle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout containerView = new LinearLayout(getContext()) {

            private boolean inLayout;

            @Override
            public boolean onTouchEvent(MotionEvent event) {
                if (progressViewStyle == 3) {
                    showCancelAlert();
                    return false;
                }
                return super.onTouchEvent(event);
            }

            @Override
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (progressViewStyle == 3) {
                    showCancelAlert();
                    return false;
                }
                return super.onInterceptTouchEvent(ev);
            }

            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                if (progressViewStyle == 3) {
                    progressViewContainer.measure(MeasureSpec.makeMeasureSpec(LayoutCreator.dp(86), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(86), MeasureSpec.EXACTLY));
                    setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
                } else {
                    inLayout = true;
                    int width = MeasureSpec.getSize(widthMeasureSpec);
                    int height = MeasureSpec.getSize(heightMeasureSpec);
                    int maxContentHeight;
                    int availableHeight = maxContentHeight = height - getPaddingTop() - getPaddingBottom();
                    int availableWidth = width - getPaddingLeft() - getPaddingRight();

                    int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(availableWidth - LayoutCreator.dp(48), MeasureSpec.EXACTLY);
                    int childFullWidthMeasureSpec = MeasureSpec.makeMeasureSpec(availableWidth, MeasureSpec.EXACTLY);
                    LayoutParams layoutParams;

                    if (buttonsLayout != null) {
                        int count = buttonsLayout.getChildCount();
                        for (int a = 0; a < count; a++) {
                            View child = buttonsLayout.getChildAt(a);
                            if (child instanceof TextView) {
                                TextView button = (TextView) child;
                                button.setMaxWidth(LayoutCreator.dp((availableWidth - LayoutCreator.dp(24)) / 2));
                            }
                        }
                        buttonsLayout.measure(childFullWidthMeasureSpec, heightMeasureSpec);
                        layoutParams = (LayoutParams) buttonsLayout.getLayoutParams();
                        availableHeight -= buttonsLayout.getMeasuredHeight() + layoutParams.bottomMargin + layoutParams.topMargin;
                    }
                    if (secondTitleTextView != null) {
                        secondTitleTextView.measure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(childWidthMeasureSpec), MeasureSpec.AT_MOST), heightMeasureSpec);
                    }
                    if (titleTextView != null) {
                        if (secondTitleTextView != null) {
                            titleTextView.measure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(childWidthMeasureSpec) - secondTitleTextView.getMeasuredWidth() - LayoutCreator.dp(8), MeasureSpec.EXACTLY), heightMeasureSpec);
                        } else {
                            titleTextView.measure(childWidthMeasureSpec, heightMeasureSpec);
                        }
                    }
                    if (titleContainer != null) {
                        titleContainer.measure(childWidthMeasureSpec, heightMeasureSpec);
                        layoutParams = (LayoutParams) titleContainer.getLayoutParams();
                        availableHeight -= titleContainer.getMeasuredHeight() + layoutParams.bottomMargin + layoutParams.topMargin;
                    }
                    if (subtitleTextView != null) {
                        subtitleTextView.measure(childWidthMeasureSpec, heightMeasureSpec);
                        layoutParams = (LayoutParams) subtitleTextView.getLayoutParams();
                        availableHeight -= subtitleTextView.getMeasuredHeight() + layoutParams.bottomMargin + layoutParams.topMargin;
                    }
                    if (topView != null) {
                        int w = width - LayoutCreator.dp(16);
                        int h;
                        if (aspectRatio == 0) {
                            float scale = w / 936.0f;
                            h = (int) (354 * scale);
                        } else {
                            h = (int) (w * aspectRatio);
                        }
                        topView.measure(MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY));
                        topView.getLayoutParams().height = h;
                        availableHeight -= topView.getMeasuredHeight();
                    }
                    if (progressViewStyle == 0) {
                        layoutParams = (LayoutParams) contentScrollView.getLayoutParams();

                        if (customView != null) {
                            layoutParams.topMargin = titleTextView == null && messageTextView.getVisibility() == GONE && items == null ? LayoutCreator.dp(16) : 0;
                            layoutParams.bottomMargin = buttonsLayout == null ? LayoutCreator.dp(8) : 0;
                        } else if (items != null) {
                            layoutParams.topMargin = titleTextView == null && messageTextView.getVisibility() == GONE ? LayoutCreator.dp(8) : 0;
                            layoutParams.bottomMargin = LayoutCreator.dp(8);
                        } else if (messageTextView.getVisibility() == VISIBLE) {
                            layoutParams.topMargin = titleTextView == null ? LayoutCreator.dp(19) : 0;
                            layoutParams.bottomMargin = LayoutCreator.dp(20);
                        }

                        availableHeight -= layoutParams.bottomMargin + layoutParams.topMargin;
                        contentScrollView.measure(childFullWidthMeasureSpec, MeasureSpec.makeMeasureSpec(availableHeight, MeasureSpec.AT_MOST));
                        availableHeight -= contentScrollView.getMeasuredHeight();
                    } else {
                        if (progressViewContainer != null) {
                            progressViewContainer.measure(childWidthMeasureSpec, MeasureSpec.makeMeasureSpec(availableHeight, MeasureSpec.AT_MOST));
                            layoutParams = (LayoutParams) progressViewContainer.getLayoutParams();
                            availableHeight -= progressViewContainer.getMeasuredHeight() + layoutParams.bottomMargin + layoutParams.topMargin;
                        } else if (messageTextView != null) {
                            messageTextView.measure(childWidthMeasureSpec, MeasureSpec.makeMeasureSpec(availableHeight, MeasureSpec.AT_MOST));
                            if (messageTextView.getVisibility() != GONE) {
                                layoutParams = (LayoutParams) messageTextView.getLayoutParams();
                                availableHeight -= messageTextView.getMeasuredHeight() + layoutParams.bottomMargin + layoutParams.topMargin;
                            }
                        }
                        if (lineProgressView != null) {
                            lineProgressView.measure(childWidthMeasureSpec, MeasureSpec.makeMeasureSpec(LayoutCreator.dp(4), MeasureSpec.EXACTLY));
                            layoutParams = (LayoutParams) lineProgressView.getLayoutParams();
                            availableHeight -= lineProgressView.getMeasuredHeight() + layoutParams.bottomMargin + layoutParams.topMargin;

                            lineProgressViewPercent.measure(childWidthMeasureSpec, MeasureSpec.makeMeasureSpec(availableHeight, MeasureSpec.AT_MOST));
                            layoutParams = (LayoutParams) lineProgressViewPercent.getLayoutParams();
                            availableHeight -= lineProgressViewPercent.getMeasuredHeight() + layoutParams.bottomMargin + layoutParams.topMargin;
                        }
                    }

                    setMeasuredDimension(width, maxContentHeight - availableHeight + getPaddingTop() + getPaddingBottom());
                    inLayout = false;

                    if (lastScreenWidth != displaySize.x) {
                        G.runOnUiThread(() -> {
                            lastScreenWidth = displaySize.x;
                            final int calculatedWidth = displaySize.x - LayoutCreator.dp(56);
                            int maxWidth;
                            maxWidth = LayoutCreator.dp(356);
                            Window window = getWindow();
                            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                            params.copyFrom(window.getAttributes());
                            params.width = Math.min(maxWidth, calculatedWidth) + backgroundPaddings.left + backgroundPaddings.right;
                            try {
                                window.setAttributes(params);
                            } catch (Throwable e) {
                                FileLog.e(e);
                            }
                        });
                    }
                }
            }

            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                super.onLayout(changed, l, t, r, b);
                if (progressViewStyle == 3) {
                    int x = (r - l - progressViewContainer.getMeasuredWidth()) / 2;
                    int y = (b - t - progressViewContainer.getMeasuredHeight()) / 2;
                    progressViewContainer.layout(x, y, x + progressViewContainer.getMeasuredWidth(), y + progressViewContainer.getMeasuredHeight());
                } else if (contentScrollView != null) {
                    if (onScrollChangedListener == null) {
                        onScrollChangedListener = () -> {
                            runShadowAnimation(0, titleTextView != null && contentScrollView.getScrollY() > scrollContainer.getTop());
                            runShadowAnimation(1, buttonsLayout != null && contentScrollView.getScrollY() + contentScrollView.getHeight() < scrollContainer.getBottom());
                            contentScrollView.invalidate();
                        };
                        contentScrollView.getViewTreeObserver().addOnScrollChangedListener(onScrollChangedListener);
                    }
                    onScrollChangedListener.onScrollChanged();
                }
            }

            @Override
            public void requestLayout() {
                if (inLayout) {
                    return;
                }
                super.requestLayout();
            }

            @Override
            public boolean hasOverlappingRendering() {
                return false;
            }

            @Override
            protected void dispatchDraw(Canvas canvas) {
                if (drawBackground) {
                    shadowDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                    if (topView != null && notDrawBackgroundOnTopView) {
                        int clipTop = topView.getBottom();
                        canvas.save();
                        canvas.clipRect(0, clipTop, getMeasuredWidth(), getMeasuredHeight());
                        shadowDrawable.draw(canvas);
                        canvas.restore();
                    } else {
                        shadowDrawable.draw(canvas);
                    }
                }
                super.dispatchDraw(canvas);
            }
        };
        containerView.setOrientation(LinearLayout.VERTICAL);
        if (progressViewStyle == 3) {
            containerView.setBackgroundDrawable(null);
            containerView.setPadding(0, 0, 0, 0);
            drawBackground = false;
        } else {
            if (notDrawBackgroundOnTopView) {
                Rect rect = new Rect();
                shadowDrawable.getPadding(rect);
                containerView.setPadding(rect.left, rect.top, rect.right, rect.bottom);
                drawBackground = true;
            } else {
                containerView.setBackground(null);
                containerView.setPadding(0, 0, 0, 0);
                containerView.setBackground(shadowDrawable);
                drawBackground = false;
            }
        }
        containerView.setFitsSystemWindows(Build.VERSION.SDK_INT >= 21);
        setContentView(containerView);

        final boolean hasButtons = positiveButtonText != null || negativeButtonText != null || neutralButtonText != null;

        if (topView != null) {
            topView.setPadding(0, 0, 0, 0);
            containerView.addView(topView, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, topHeight, Gravity.LEFT | Gravity.TOP, 0, 0, 0, 0));
        }

        if (title != null) {
            titleContainer = new FrameLayout(getContext());
            containerView.addView(titleContainer, LayoutCreator.createLinear(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, 24, 0, 24, 0));

            titleTextView = new TextView(getContext());
            titleTextView.setText(title);
            titleTextView.setTextColor(getThemeColor(Theme.key_dialogTextBlack));
            titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            titleTextView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
            titleTextView.setGravity((G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP);
            titleContainer.addView(titleTextView, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, (G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, 0, 19, 0, (subtitle != null ? 2 : (items != null ? 14 : 10))));
        }

        if (secondTitle != null && title != null) {
            secondTitleTextView = new TextView(getContext());
            secondTitleTextView.setText(secondTitle);
            secondTitleTextView.setTextColor(getThemeColor(Theme.key_dialogTextGray3));
            secondTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            secondTitleTextView.setGravity((G.isAppRtl ? Gravity.LEFT : Gravity.RIGHT) | Gravity.TOP);
            titleContainer.addView(secondTitleTextView, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, (G.isAppRtl ? Gravity.LEFT : Gravity.RIGHT) | Gravity.TOP, 0, 21, 0, 0));
        }

        if (subtitle != null) {
            subtitleTextView = new TextView(getContext());
            subtitleTextView.setText(subtitle);
            subtitleTextView.setTextColor(getThemeColor(Theme.key_dialogIcon));
            subtitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            subtitleTextView.setGravity((G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP);
            containerView.addView(subtitleTextView, LayoutCreator.createLinear(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, (G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, 24, 0, 24, items != null ? 14 : 10));
        }

        if (progressViewStyle == 0) {
            shadow[0] = (BitmapDrawable) getContext().getResources().getDrawable(R.drawable.header_shadow).mutate();
            shadow[1] = (BitmapDrawable) getContext().getResources().getDrawable(R.drawable.header_shadow_reverse).mutate();
            shadow[0].setAlpha(0);
            shadow[1].setAlpha(0);
            shadow[0].setCallback(this);
            shadow[1].setCallback(this);

            contentScrollView = new ScrollView(getContext()) {
                @Override
                protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
                    boolean result = super.drawChild(canvas, child, drawingTime);
                    if (shadow[0].getPaint().getAlpha() != 0) {
                        shadow[0].setBounds(0, getScrollY(), getMeasuredWidth(), getScrollY() + LayoutCreator.dp(3));
                        shadow[0].draw(canvas);
                    }
                    if (shadow[1].getPaint().getAlpha() != 0) {
                        shadow[1].setBounds(0, getScrollY() + getMeasuredHeight() - LayoutCreator.dp(3), getMeasuredWidth(), getScrollY() + getMeasuredHeight());
                        shadow[1].draw(canvas);
                    }
                    return result;
                }
            };
            contentScrollView.setVerticalScrollBarEnabled(false);
            setScrollViewEdgeEffectColor(contentScrollView, getThemeColor(Theme.key_dialogScrollGlow));
            containerView.addView(contentScrollView, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, 0, 0, 0, 0));

            scrollContainer = new LinearLayout(getContext());
            scrollContainer.setOrientation(LinearLayout.VERTICAL);
            contentScrollView.addView(scrollContainer, new ScrollView.LayoutParams(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT));
        }

        messageTextView = new TextView(getContext());
        messageTextView.setTextColor(getThemeColor(Theme.key_dialogTextBlack));
        messageTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        messageTextView.setMovementMethod(new LinkMovementMethodMy());
        messageTextView.setLinkTextColor(getThemeColor(Theme.key_dialogTextLink));
        if (!messageTextViewClickable) {
            messageTextView.setClickable(false);
            messageTextView.setEnabled(false);
        }
        messageTextView.setGravity((G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP);
        if (progressViewStyle == 1) {
            progressViewContainer = new FrameLayout(getContext());
            containerView.addView(progressViewContainer, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, 44, Gravity.LEFT | Gravity.TOP, 23, title == null ? 24 : 0, 23, 24));

            RadialProgressView progressView = new RadialProgressView(getContext());
            progressView.setProgressColor(getThemeColor(Theme.key_dialogProgressCircle));
            progressViewContainer.addView(progressView, LayoutCreator.createFrame(44, 44, (G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP));

            messageTextView.setLines(1);
            messageTextView.setEllipsize(TextUtils.TruncateAt.END);
            progressViewContainer.addView(messageTextView, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, (G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL, (G.isAppRtl ? 0 : 62), 0, (G.isAppRtl ? 62 : 0), 0));
        } else if (progressViewStyle == 2) {
            containerView.addView(messageTextView, LayoutCreator.createLinear(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, (G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, 24, title == null ? 19 : 0, 24, 20));

            lineProgressView = new LineProgressView(getContext());
            lineProgressView.setProgress(currentProgress / 100.0f, false);
            lineProgressView.setProgressColor(getThemeColor(Theme.key_dialogLineProgress));
            lineProgressView.setBackColor(getThemeColor(Theme.key_dialogLineProgressBackground));
            containerView.addView(lineProgressView, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, 4, Gravity.LEFT | Gravity.CENTER_VERTICAL, 24, 0, 24, 0));

            lineProgressViewPercent = new TextView(getContext());
            lineProgressViewPercent.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
            lineProgressViewPercent.setGravity((G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP);
            lineProgressViewPercent.setTextColor(getThemeColor(Theme.key_dialogTextGray2));
            lineProgressViewPercent.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            containerView.addView(lineProgressViewPercent, LayoutCreator.createLinear(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, (G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, 23, 4, 23, 24));
            updateLineProgressTextView();
        } else if (progressViewStyle == 3) {
            setCanceledOnTouchOutside(false);
            setCancelable(false);

            progressViewContainer = new FrameLayout(getContext());
            progressViewContainer.setBackground(Theme.createRoundRectDrawable(LayoutCreator.dp(18), Theme.getDefaultColor(Theme.key_dialog_inlineProgressBackground)));
            containerView.addView(progressViewContainer, LayoutCreator.createLinear(86, 86, Gravity.CENTER));

            RadialProgressView progressView = new RadialProgressView(getContext());
            progressView.setProgressColor(getThemeColor(Theme.key_dialog_inlineProgress));
            progressViewContainer.addView(progressView, LayoutCreator.createLinear(86, 86));
        } else {
            scrollContainer.addView(messageTextView, LayoutCreator.createLinear(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, (G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, 24, 0, 24, customView != null || items != null ? customViewOffset : 0));
        }
        if (!TextUtils.isEmpty(message)) {
            messageTextView.setText(message);
            messageTextView.setVisibility(View.VISIBLE);
        } else {
            messageTextView.setVisibility(View.GONE);
        }

        if (items != null) {
            FrameLayout rowLayout = null;
            int lastRowLayoutNum = 0;
            for (int a = 0; a < items.length; a++) {
                if (items[a] == null) {
                    continue;
                }
                AlertDialogCell cell = new AlertDialogCell(getContext());
                cell.setTextAndIcon(items[a], itemIcons != null ? itemIcons[a] : 0);
                cell.setTag(a);
                itemViews.add(cell);
                scrollContainer.addView(cell, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, 50));
                cell.setOnClickListener(v -> {
                    if (onClickListener != null) {
                        onClickListener.onClick(AlertDialog.this, (Integer) v.getTag());
                    }
                    dismiss();
                });
            }
        }
        if (customView != null) {
            if (customView.getParent() != null) {
                ViewGroup viewGroup = (ViewGroup) customView.getParent();
                viewGroup.removeView(customView);
            }
            scrollContainer.addView(customView, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, customViewHeight));
        }
        if (hasButtons) {
            if (!verticalButtons) {
                int buttonsWidth = 0;
                TextPaint paint = new TextPaint();
                paint.setTextSize(LayoutCreator.dp(14));
                if (positiveButtonText != null) {
                    buttonsWidth += paint.measureText(positiveButtonText, 0, positiveButtonText.length()) + LayoutCreator.dp(10);
                }
                if (negativeButtonText != null) {
                    buttonsWidth += paint.measureText(negativeButtonText, 0, negativeButtonText.length()) + LayoutCreator.dp(10);
                }
                if (neutralButtonText != null) {
                    buttonsWidth += paint.measureText(neutralButtonText, 0, neutralButtonText.length()) + LayoutCreator.dp(10);
                }
                if (buttonsWidth > LayoutCreator.dp(320)) {
                    verticalButtons = true;
                }
            }
            if (verticalButtons) {
                LinearLayout linearLayout = new LinearLayout(getContext());
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                buttonsLayout = linearLayout;
            } else {
                buttonsLayout = new FrameLayout(getContext()) {
                    @Override
                    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                        int count = getChildCount();
                        View positiveButton = null;
                        int width = right - left;
                        for (int a = 0; a < count; a++) {
                            View child = getChildAt(a);
                            Integer tag = (Integer) child.getTag();
                            if (tag != null) {
                                if (tag == Dialog.BUTTON_POSITIVE) {
                                    positiveButton = child;
                                    if (G.isAppRtl) {
                                        child.layout(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + child.getMeasuredWidth(), getPaddingTop() + child.getMeasuredHeight());
                                    } else {
                                        child.layout(width - getPaddingRight() - child.getMeasuredWidth(), getPaddingTop(), width - getPaddingRight(), getPaddingTop() + child.getMeasuredHeight());
                                    }
                                } else if (tag == Dialog.BUTTON_NEGATIVE) {
                                    if (G.isAppRtl) {
                                        int x = getPaddingLeft();
                                        if (positiveButton != null) {
                                            x += positiveButton.getMeasuredWidth() + LayoutCreator.dp(8);
                                        }
                                        child.layout(x, getPaddingTop(), x + child.getMeasuredWidth(), getPaddingTop() + child.getMeasuredHeight());
                                    } else {
                                        int x = width - getPaddingRight() - child.getMeasuredWidth();
                                        if (positiveButton != null) {
                                            x -= positiveButton.getMeasuredWidth() + LayoutCreator.dp(8);
                                        }
                                        child.layout(x, getPaddingTop(), x + child.getMeasuredWidth(), getPaddingTop() + child.getMeasuredHeight());
                                    }
                                } else if (tag == Dialog.BUTTON_NEUTRAL) {
                                    if (G.isAppRtl) {
                                        child.layout(width - getPaddingRight() - child.getMeasuredWidth(), getPaddingTop(), width - getPaddingRight(), getPaddingTop() + child.getMeasuredHeight());
                                    } else {
                                        child.layout(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + child.getMeasuredWidth(), getPaddingTop() + child.getMeasuredHeight());
                                    }
                                }
                            } else {
                                int w = child.getMeasuredWidth();
                                int h = child.getMeasuredHeight();
                                int l;
                                int t;
                                if (positiveButton != null) {
                                    l = positiveButton.getLeft() + (positiveButton.getMeasuredWidth() - w) / 2;
                                    t = positiveButton.getTop() + (positiveButton.getMeasuredHeight() - h) / 2;
                                } else {
                                    l = t = 0;
                                }
                                child.layout(l, t, l + w, t + h);
                            }
                        }
                    }

                    @Override
                    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

                        int totalWidth = 0;
                        int availableWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
                        int count = getChildCount();
                        for (int a = 0; a < count; a++) {
                            View child = getChildAt(a);
                            if (child instanceof TextView && child.getTag() != null) {
                                totalWidth += child.getMeasuredWidth();
                            }
                        }
                        if (totalWidth > availableWidth) {
                            View negative = findViewWithTag(BUTTON_NEGATIVE);
                            View neuntral = findViewWithTag(BUTTON_NEUTRAL);
                            if (negative != null && neuntral != null) {
                                if (negative.getMeasuredWidth() < neuntral.getMeasuredWidth()) {
                                    neuntral.measure(MeasureSpec.makeMeasureSpec(neuntral.getMeasuredWidth() - (totalWidth - availableWidth), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(neuntral.getMeasuredHeight(), MeasureSpec.EXACTLY));
                                } else {
                                    negative.measure(MeasureSpec.makeMeasureSpec(negative.getMeasuredWidth() - (totalWidth - availableWidth), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(negative.getMeasuredHeight(), MeasureSpec.EXACTLY));
                                }
                            }
                        }
                    }
                };
            }
            buttonsLayout.setPadding(LayoutCreator.dp(8), LayoutCreator.dp(8), LayoutCreator.dp(8), LayoutCreator.dp(8));
            containerView.addView(buttonsLayout, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, 52));

            if (positiveButtonText != null) {
                TextView textView = new androidx.appcompat.widget.AppCompatTextView(getContext()) {
                    @Override
                    public void setEnabled(boolean enabled) {
                        super.setEnabled(enabled);
                        setAlpha(enabled ? 1.0f : 0.5f);
                    }

                    @Override
                    public void setTextColor(int color) {
                        super.setTextColor(color);
                        setBackgroundDrawable(Theme.getRoundRectSelectorDrawable(color));
                    }
                };
                textView.setMinWidth(LayoutCreator.dp(64));
                textView.setTag(Dialog.BUTTON_POSITIVE);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                textView.setTextColor(getThemeColor(dialogButtonColorKey));
                textView.setGravity(Gravity.CENTER);
                textView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
                textView.setText(positiveButtonText.toString().toUpperCase());
                textView.setBackground(Theme.getRoundRectSelectorDrawable(getThemeColor(dialogButtonColorKey)));
                textView.setPadding(LayoutCreator.dp(10), 0, LayoutCreator.dp(10), 0);
                if (verticalButtons) {
                    buttonsLayout.addView(textView, LayoutCreator.createLinear(LayoutCreator.WRAP_CONTENT, 36, G.isAppRtl ? Gravity.LEFT : Gravity.RIGHT));
                } else {
                    buttonsLayout.addView(textView, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, 36, Gravity.TOP | Gravity.RIGHT));
                }
                textView.setOnClickListener(v -> {
                    if (positiveButtonListener != null) {
                        positiveButtonListener.onClick(AlertDialog.this, Dialog.BUTTON_POSITIVE);
                    }
                    if (dismissDialogByButtons) {
                        dismiss();
                    }
                });
            }

            if (negativeButtonText != null) {
                TextView textView = new androidx.appcompat.widget.AppCompatTextView(getContext()) {
                    @Override
                    public void setEnabled(boolean enabled) {
                        super.setEnabled(enabled);
                        setAlpha(enabled ? 1.0f : 0.5f);
                    }

                    @Override
                    public void setTextColor(int color) {
                        super.setTextColor(color);
                        setBackgroundDrawable(Theme.getRoundRectSelectorDrawable(color));
                    }
                };
                textView.setMinWidth(LayoutCreator.dp(64));
                textView.setTag(Dialog.BUTTON_NEGATIVE);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                textView.setTextColor(getThemeColor(dialogButtonColorKey));
                textView.setGravity(Gravity.CENTER);
                textView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
                textView.setEllipsize(TextUtils.TruncateAt.END);
                textView.setSingleLine(true);
                textView.setText(negativeButtonText.toString().toUpperCase());
                textView.setBackground(Theme.getRoundRectSelectorDrawable(getThemeColor(dialogButtonColorKey)));
                textView.setPadding(LayoutCreator.dp(10), 0, LayoutCreator.dp(10), 0);
                if (verticalButtons) {
                    buttonsLayout.addView(textView, 0, LayoutCreator.createLinear(LayoutCreator.WRAP_CONTENT, 36, G.isAppRtl ? Gravity.LEFT : Gravity.RIGHT));
                } else {
                    buttonsLayout.addView(textView, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, 36, Gravity.TOP | Gravity.RIGHT));
                }
                textView.setOnClickListener(v -> {
                    if (negativeButtonListener != null) {
                        negativeButtonListener.onClick(AlertDialog.this, Dialog.BUTTON_NEGATIVE);
                    }
                    if (dismissDialogByButtons) {
                        cancel();
                    }
                });
            }

            if (neutralButtonText != null) {
                TextView textView = new androidx.appcompat.widget.AppCompatTextView(getContext()) {
                    @Override
                    public void setEnabled(boolean enabled) {
                        super.setEnabled(enabled);
                        setAlpha(enabled ? 1.0f : 0.5f);
                    }

                    @Override
                    public void setTextColor(int color) {
                        super.setTextColor(color);
                        setBackgroundDrawable(Theme.getRoundRectSelectorDrawable(color));
                    }
                };
                textView.setMinWidth(LayoutCreator.dp(64));
                textView.setTag(Dialog.BUTTON_NEUTRAL);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                textView.setTextColor(getThemeColor(dialogButtonColorKey));
                textView.setGravity(Gravity.CENTER);
                textView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
                textView.setEllipsize(TextUtils.TruncateAt.END);
                textView.setSingleLine(true);
                textView.setText(neutralButtonText.toString().toUpperCase());
                textView.setBackground(Theme.getRoundRectSelectorDrawable(getThemeColor(dialogButtonColorKey)));
                textView.setPadding(LayoutCreator.dp(10), 0, LayoutCreator.dp(10), 0);
                if (verticalButtons) {
                    buttonsLayout.addView(textView, 1, LayoutCreator.createLinear(LayoutCreator.WRAP_CONTENT, 36, G.isAppRtl ? Gravity.LEFT : Gravity.RIGHT));
                } else {
                    buttonsLayout.addView(textView, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, 36, Gravity.TOP | Gravity.LEFT));
                }
                textView.setOnClickListener(v -> {
                    if (neutralButtonListener != null) {
                        neutralButtonListener.onClick(AlertDialog.this, Dialog.BUTTON_NEGATIVE);
                    }
                    if (dismissDialogByButtons) {
                        dismiss();
                    }
                });
            }

            if (verticalButtons) {
                for (int i = 1; i < buttonsLayout.getChildCount(); i++) {
                    ((ViewGroup.MarginLayoutParams) buttonsLayout.getChildAt(i).getLayoutParams()).topMargin = LayoutCreator.dp(6);
                }
            }
        }

        Window window = getWindow();
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(window.getAttributes());
        if (progressViewStyle == 3) {
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
        } else {
            if (dimEnabled) {
                params.dimAmount = 0.6f;
                params.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            }

            lastScreenWidth = displaySize.x;
            final int calculatedWidth = displaySize.x - LayoutCreator.dp(48);
            int maxWidth;
            maxWidth = LayoutCreator.dp(356);
            params.width = Math.min(maxWidth, calculatedWidth) + backgroundPaddings.left + backgroundPaddings.right;
        }
        if (customView == null || !checkFocusable || !canTextInput(customView)) {
            params.flags |= WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
        } else {
            params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE;
        }
        if (Build.VERSION.SDK_INT >= 28) {
            params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT;
        }
        window.setAttributes(params);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (onBackButtonListener != null) {
            onBackButtonListener.onClick(AlertDialog.this, AlertDialog.BUTTON_NEGATIVE);
        }
    }

    public void setFocusable(boolean value) {
        if (focusable == value) {
            return;
        }
        focusable = value;
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        if (focusable) {
            params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
            params.flags &=~ WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
        } else {
            params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING;
            params.flags |= WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
        }
        window.setAttributes(params);
    }

    public void setBackgroundColor(int color) {
        shadowDrawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
    }

    public void setTextColor(int color) {
        if (titleTextView != null) {
            titleTextView.setTextColor(color);
        }
        if (messageTextView != null) {
            messageTextView.setTextColor(color);
        }
    }

    private void showCancelAlert() {
        if (!canCacnel || cancelDialog != null) {
            return;
        }
        Builder builder = new Builder(getContext());
        builder.setTitle(context.getString(R.string.AppName));
        builder.setMessage(context.getString(R.string.StopLoading));
        builder.setPositiveButton(context.getString(R.string.WaitMore), null);
        builder.setNegativeButton(context.getString(R.string.Stop), (dialogInterface, i) -> {
            if (onCancelListener != null) {
                onCancelListener.onCancel(AlertDialog.this);
            }
            dismiss();
        });
        builder.setOnDismissListener(dialog -> cancelDialog = null);
        try {
            cancelDialog = builder.show();
        } catch (Exception ignore) {

        }
    }

    private void runShadowAnimation(final int num, final boolean show) {
        if (show && !shadowVisibility[num] || !show && shadowVisibility[num]) {
            shadowVisibility[num] = show;
            if (shadowAnimation[num] != null) {
                shadowAnimation[num].cancel();
            }
            shadowAnimation[num] = new AnimatorSet();
            if (shadow[num] != null) {
                shadowAnimation[num].playTogether(ObjectAnimator.ofInt(shadow[num], "alpha", show ? 255 : 0));
            }
            shadowAnimation[num].setDuration(150);
            shadowAnimation[num].addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (shadowAnimation[num] != null && shadowAnimation[num].equals(animation)) {
                        shadowAnimation[num] = null;
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    if (shadowAnimation[num] != null && shadowAnimation[num].equals(animation)) {
                        shadowAnimation[num] = null;
                    }
                }
            });
            try {
                shadowAnimation[num].start();
            } catch (Exception e) {
                FileLog.e(e);
            }

        }
    }

    public void setProgressStyle(int style) {
        progressViewStyle = style;
    }

    public void setDismissDialogByButtons(boolean value) {
        dismissDialogByButtons = value;
    }

    public void setProgress(int progress) {
        currentProgress = progress;
        if (lineProgressView != null) {
            lineProgressView.setProgress(progress / 100.0f, true);
            updateLineProgressTextView();
        }
    }

    private void updateLineProgressTextView() {
        lineProgressViewPercent.setText(String.format("%d%%", currentProgress));
    }

    public void setCanCacnel(boolean value) {
        canCacnel = value;
    }

    private boolean canTextInput(View v) {
        if (v.onCheckIsTextEditor()) {
            return true;
        }
        if (!(v instanceof ViewGroup)) {
            return false;
        }
        ViewGroup vg = (ViewGroup) v;
        int i = vg.getChildCount();
        while (i > 0) {
            i--;
            v = vg.getChildAt(i);
            if (canTextInput(v)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void dismiss() {
        if (onDismissListener != null) {
            onDismissListener.onDismiss(this);
        }
        if (cancelDialog != null) {
            cancelDialog.dismiss();
        }
        try {
            super.dismiss();
        } catch (Throwable ignore) {

        }
        G.cancelRunOnUiThread(showRunnable);
    }

    @Override
    public void setCanceledOnTouchOutside(boolean cancel) {
        super.setCanceledOnTouchOutside(cancel);
    }

    public void setTopImage(int resId, int backgroundColor) {
        topResId = resId;
        topBackgroundColor = backgroundColor;
    }

    public void setTopAnimation(int resId, int backgroundColor) {
        topAnimationId = resId;
        topBackgroundColor = backgroundColor;
    }

    public void setTopHeight(int value) {
        topHeight = value;
    }

    public void setTopImage(Drawable drawable, int backgroundColor) {
        topDrawable = drawable;
        topBackgroundColor = backgroundColor;
    }

    public void setTitle(CharSequence text) {
        title = text;
        if (titleTextView != null) {
            titleTextView.setText(text);
        }
    }

    public void setSecondTitle(CharSequence text) {
        secondTitle = text;
    }

    public void setPositiveButton(CharSequence text, final OnClickListener listener) {
        positiveButtonText = text;
        positiveButtonListener = listener;
    }

    public void setNegativeButton(CharSequence text, final OnClickListener listener) {
        negativeButtonText = text;
        negativeButtonListener = listener;
    }

    public void setNeutralButton(CharSequence text, final OnClickListener listener) {
        neutralButtonText = text;
        neutralButtonListener = listener;
    }

    public void setItemColor(int item, int color, int icon) {
        if (item < 0 || item >= itemViews.size()) {
            return;
        }
        AlertDialogCell cell = itemViews.get(item);
        cell.textView.setTextColor(color);
        cell.imageView.setColorFilter(new PorterDuffColorFilter(icon, PorterDuff.Mode.MULTIPLY));
    }

    public int getItemsCount() {
        return itemViews.size();
    }

    public void setMessage(CharSequence text) {
        message = text;
        if (messageTextView != null) {
            if (!TextUtils.isEmpty(message)) {
                messageTextView.setText(message);
                messageTextView.setVisibility(View.VISIBLE);
            } else {
                messageTextView.setVisibility(View.GONE);
            }
        }
    }

    public void setMessageTextViewClickable(boolean value) {
        messageTextViewClickable = value;
    }

    public void setButton(int type, CharSequence text, final OnClickListener listener) {
        switch (type) {
            case BUTTON_NEUTRAL:
                neutralButtonText = text;
                neutralButtonListener = listener;
                break;
            case BUTTON_NEGATIVE:
                negativeButtonText = text;
                negativeButtonListener = listener;
                break;
            case BUTTON_POSITIVE:
                positiveButtonText = text;
                positiveButtonListener = listener;
                break;
        }
    }

    public View getButton(int type) {
        if (buttonsLayout != null) {
            return buttonsLayout.findViewWithTag(type);
        }
        return null;
    }

    @Override
    public void invalidateDrawable(Drawable who) {
        contentScrollView.invalidate();
        scrollContainer.invalidate();
    }

    @Override
    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        if (contentScrollView != null) {
            contentScrollView.postDelayed(what, when);
        }
    }

    @Override
    public void unscheduleDrawable(Drawable who, Runnable what) {
        if (contentScrollView != null) {
            contentScrollView.removeCallbacks(what);
        }
    }

    @Override
    public void setOnCancelListener(OnCancelListener listener) {
        onCancelListener = listener;
        super.setOnCancelListener(listener);
    }

    public void setPositiveButtonListener(final OnClickListener listener) {
        positiveButtonListener = listener;
    }

    protected int getThemeColor(String key) {
        return Theme.getDefaultColor(key);
    }

    public void showDelayed(long delay) {
        G.cancelRunOnUiThread(showRunnable);
        G.runOnUiThread(showRunnable, delay);
    }

    public static class Builder {

        private AlertDialog alertDialog;

        protected Builder(AlertDialog alert) {
            alertDialog = alert;
        }

        public Builder(Context context) {
            alertDialog = new AlertDialog(context, 0);
        }

        public Builder(Context context, int progressViewStyle) {
            alertDialog = new AlertDialog(context, progressViewStyle);
        }

        public Context getContext() {
            return alertDialog.getContext();
        }

        public Builder setItems(CharSequence[] items, final OnClickListener onClickListener) {
            alertDialog.items = items;
            alertDialog.onClickListener = onClickListener;
            return this;
        }

        public Builder setCheckFocusable(boolean value) {
            alertDialog.checkFocusable = value;
            return this;
        }

        public Builder setItems(CharSequence[] items, int[] icons, final OnClickListener onClickListener) {
            alertDialog.items = items;
            alertDialog.itemIcons = icons;
            alertDialog.onClickListener = onClickListener;
            return this;
        }

        public Builder setView(View view) {
            return setView(view, LayoutCreator.WRAP_CONTENT);
        }

        public Builder setView(View view, int height) {
            alertDialog.customView = view;
            alertDialog.customViewHeight = height;
            return this;
        }

        public Builder setTitle(CharSequence title) {
            alertDialog.title = title;
            return this;
        }

        public Builder setSubtitle(CharSequence subtitle) {
            alertDialog.subtitle = subtitle;
            return this;
        }

        public Builder setTopImage(int resId, int backgroundColor) {
            alertDialog.topResId = resId;
            alertDialog.topBackgroundColor = backgroundColor;
            return this;
        }

        public Builder setTopView(View view) {
            alertDialog.topView = view;
            return this;
        }

        public Builder setDialogButtonColorKey(String key) {
            alertDialog.dialogButtonColorKey = key;
            return this;
        }

        public Builder setTopAnimation(int resId, int backgroundColor) {
            alertDialog.topAnimationId = resId;
            alertDialog.topBackgroundColor = backgroundColor;
            return this;
        }

        public Builder setTopImage(Drawable drawable, int backgroundColor) {
            alertDialog.topDrawable = drawable;
            alertDialog.topBackgroundColor = backgroundColor;
            return this;
        }

        public Builder setMessage(CharSequence message) {
            alertDialog.message = message;
            return this;
        }

        public Builder setPositiveButton(CharSequence text, final OnClickListener listener) {
            alertDialog.positiveButtonText = text;
            alertDialog.positiveButtonListener = listener;
            return this;
        }

        public Builder setNegativeButton(CharSequence text, final OnClickListener listener) {
            alertDialog.negativeButtonText = text;
            alertDialog.negativeButtonListener = listener;
            return this;
        }

        public Builder setNeutralButton(CharSequence text, final OnClickListener listener) {
            alertDialog.neutralButtonText = text;
            alertDialog.neutralButtonListener = listener;
            return this;
        }

        public Builder setOnBackButtonListener(final OnClickListener listener) {
            alertDialog.onBackButtonListener = listener;
            return this;
        }

        public Builder setOnCancelListener(OnCancelListener listener) {
            alertDialog.setOnCancelListener(listener);
            return this;
        }

        public Builder setCustomViewOffset(int offset) {
            alertDialog.customViewOffset = offset;
            return this;
        }

        public Builder setMessageTextViewClickable(boolean value) {
            alertDialog.messageTextViewClickable = value;
            return this;
        }

        public AlertDialog create() {
            return alertDialog;
        }

        public AlertDialog show() {
            alertDialog.show();
            return alertDialog;
        }

        public Runnable getDismissRunnable() {
            return alertDialog.dismissRunnable;
        }

        public Builder setOnDismissListener(OnDismissListener onDismissListener) {
            alertDialog.setOnDismissListener(onDismissListener);
            return this;
        }

        public void setTopViewAspectRatio(float aspectRatio) {
            alertDialog.aspectRatio = aspectRatio;
        }

        public void setDimEnabled(boolean dimEnabled) {
            alertDialog.dimEnabled = dimEnabled;
        }

        public void notDrawBackgroundOnTopView(boolean b) {
            alertDialog.notDrawBackgroundOnTopView = b;
        }

        public void setButtonsVertical(boolean vertical) {
            alertDialog.verticalButtons = vertical;
        }

        public Builder setOnPreDismissListener(OnDismissListener onDismissListener) {
            alertDialog.onDismissListener = onDismissListener;
            return this;
        }
    }

    public static void setScrollViewEdgeEffectColor(ScrollView scrollView, int color) {
        if (Build.VERSION.SDK_INT >= 29) {
            scrollView.setTopEdgeEffectColor(color);
            scrollView.setBottomEdgeEffectColor(color);
        } else if (Build.VERSION.SDK_INT >= 21) {
            try {
                Field field = ScrollView.class.getDeclaredField("mEdgeGlowTop");
                field.setAccessible(true);
                EdgeEffect mEdgeGlowTop = (EdgeEffect) field.get(scrollView);
                if (mEdgeGlowTop != null) {
                    mEdgeGlowTop.setColor(color);
                }

                field = ScrollView.class.getDeclaredField("mEdgeGlowBottom");
                field.setAccessible(true);
                EdgeEffect mEdgeGlowBottom = (EdgeEffect) field.get(scrollView);
                if (mEdgeGlowBottom != null) {
                    mEdgeGlowBottom.setColor(color);
                }
            } catch (Exception ignore) {

            }
        }
    }

    public static class LinkMovementMethodMy extends LinkMovementMethod {
        @Override
        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            try {
                boolean result = super.onTouchEvent(widget, buffer, event);
                if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    Selection.removeSelection(buffer);
                }
                return result;
            } catch (Exception e) {
                FileLog.e(e);
            }
            return false;
        }
    }
}