package net.iGap.module.scrollbar;

import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.ContactItemGroup;
import net.iGap.messenger.theme.Theme;

import org.jetbrains.annotations.NotNull;

public class FastScroller extends LinearLayout {

    private View handle;
    private RecyclerView recyclerView;
    private PopupWindow bubble;
    private TextView textView;

    private static final int HANDLE_ANIMATION_DURATION = 100;
    private static final int HANDLE_HIDE_DELAY = 500;
    private static final int TRACK_SNAP_RANGE = 5;

    private final HandleHider handleHider = new HandleHider();

    private static final String SCALE_X = "scaleX";
    private static final String SCALE_Y = "scaleY";
    private static final String ALPHA = "alpha";

    private AnimatorSet currentAnimator = null;

    private int height;

    private final ScrollListener scrollListener = new ScrollListener();

    public FastScroller(Context context) {
        super(context);
        initialise(context);
    }

    public FastScroller(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialise(context);
    }

    public FastScroller(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialise(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FastScroller(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialise(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        height = h;
    }

    public void setRecyclerView(RecyclerView recyclerView/*, int totalItemCount*/) {
        this.recyclerView = recyclerView;
        /*this.totalItemCount = totalItemCount;*/
        recyclerView.setOnScrollListener(scrollListener);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            setPosition(event.getY());
            if (currentAnimator != null) {
                currentAnimator.cancel();
            }
            getHandler().removeCallbacks(handleHider);
            if (!bubble.isShowing()/*kb24.getVisibility() == INVISIBLE*/) {
                showHandle();
            }
            recyclerView.setOnScrollListener(null);
            setRecyclerViewPosition(event.getY());
            handle.setSelected(true);
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            recyclerView.setOnScrollListener(scrollListener);
            getHandler().postDelayed(handleHider, HANDLE_HIDE_DELAY);
            return true;
        }
        return super.onTouchEvent(event);
    }

    private class HandleHider implements Runnable {
        @Override
        public void run() {
            handle.setSelected(false);
            hideHandle();
        }
    }

    private void setRecyclerViewPosition(float y) {
        if (recyclerView != null && recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            float proportion;
            if (handle.getY() == 0) {
                proportion = 0f;
            } else if (handle.getY() + handle.getHeight() >= height - TRACK_SNAP_RANGE) {
                proportion = 1f;
            } else {
                proportion = y / (float) height;
            }
            int targetPos = getValueInRange(0, recyclerView.getAdapter().getItemCount() - 1, (int) (proportion * (float) recyclerView.getAdapter().getItemCount()));
            if (recyclerView.getAdapter() instanceof FastAdapter) {
                IItem iItem = ((FastAdapter) recyclerView.getAdapter()).getItem(((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition());
                if (iItem instanceof FastScrollerBarBaseAdapter) {
                    textView.setText(((ContactItemGroup) iItem).getBubbleText(0));
                }
            } else if (recyclerView.getAdapter() instanceof FastScrollerBarBaseAdapter) {
                textView.setText(((FastScrollerBarBaseAdapter) recyclerView.getAdapter()).getBubbleText(((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition()));
            } else {
                textView.setText("-");
            }
            recyclerView.scrollToPosition(targetPos);
        }
    }

    private void initialise(Context context) {
        setOrientation(HORIZONTAL);
        setClipChildren(false);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.fastscroller, this);
        handle = findViewById(R.id.fastscroller_bubble);
        bubble = new PopupWindow(getContext());
        bubble.setWidth(LayoutParams.WRAP_CONTENT);
        bubble.setHeight(LayoutParams.WRAP_CONTENT);
        bubble.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        textView = new TextView(getContext());
        textView.setGravity(Gravity.CENTER);
        textView.setTypeface(ResourcesCompat.getFont(textView.getContext(), R.font.main_font));
        textView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        bubble.setContentView(textView);

        int handleResId;
        if (G.isAppRtl) {
            handleResId = R.drawable.fastscroll_bubble_light_left;
        } else {
            handleResId = R.drawable.fastscroll_bubble_light_right;
        }
        textView.setTextColor(Theme.getColor(Theme.key_default_text));
        handle.setBackground(Theme.tintDrawable(ContextCompat.getDrawable(getContext(), R.drawable.fastscroll_handle), getContext(), Theme.getColor(Theme.key_light_theme_color)));
        textView.setBackground(Theme.tintDrawable(ContextCompat.getDrawable(context, handleResId), context, Theme.getColor(Theme.key_button_background)));
    }

    private void setPosition(float y) {
        float position = y / height;
        int handleHeight = handle.getHeight();
        handle.setY(getValueInRange(0, height - handleHeight, (int) ((height - handleHeight) * position)));
    }

    private int getValueInRange(int min, int max, int value) {
        int minimum = Math.max(min, value);
        return Math.min(minimum, max);
    }

    private void showHandle() {
        if (G.isAppRtl) {
            bubble.showAsDropDown(handle, (int) getResources().getDimension(R.dimen.dp8), (int) -getResources().getDimension(R.dimen.dp52));
        } else {
            bubble.showAsDropDown(handle, (int) -(getResources().getDimension(R.dimen.dp52) + getResources().getDimension(R.dimen.dp8)), (int) -getResources().getDimension(R.dimen.dp52));
        }
        bubble.update();
        /*AnimatorSet animatorSet = new AnimatorSet();
        bubble.setPivotX(kb24.getWidth());
        bubble.setPivotY(kb24.getHeight());
        bubble.setVisibility(VISIBLE);
        Animator growerX = ObjectAnimator.ofFloat(kb24, SCALE_X, 0f, 1f).setDuration(HANDLE_ANIMATION_DURATION);
        Animator growerY = ObjectAnimator.ofFloat(kb24, SCALE_Y, 0f, 1f).setDuration(HANDLE_ANIMATION_DURATION);
        Animator alpha = ObjectAnimator.ofFloat(kb24, ALPHA, 0f, 1f).setDuration(HANDLE_ANIMATION_DURATION);
        animatorSet.playTogether(growerX, growerY, alpha);
        animatorSet.start();*/
    }

    private void hideHandle() {
        bubble.dismiss();
        /*currentAnimator = new AnimatorSet();
        if (G.isAppRtl) {
            kb24.setPivotX(0);
        } else {
            kb24.setPivotX(kb24.getWidth());
        }
        kb24.setPivotY(kb24.getHeight());
        Animator shrinkerX = ObjectAnimator.ofFloat(kb24, SCALE_X, 1f, 0f).setDuration(HANDLE_ANIMATION_DURATION);
        Animator shrinkerY = ObjectAnimator.ofFloat(kb24, SCALE_Y, 1f, 0f).setDuration(HANDLE_ANIMATION_DURATION);
        Animator alpha = ObjectAnimator.ofFloat(kb24, ALPHA, 1f, 0f).setDuration(HANDLE_ANIMATION_DURATION);
        currentAnimator.playTogether(shrinkerX, shrinkerY, alpha);
        currentAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                kb24.setVisibility(INVISIBLE);
                currentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                kb24.setVisibility(INVISIBLE);
                currentAnimator = null;
            }
        });
        currentAnimator.start();*/
    }

    private class ScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(@NotNull RecyclerView rc, int dx, int dy) {
            int firstVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition()/*recyclerView.getChildPosition(firstVisibleView)*/;
            int lastVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition()/*firstVisiblePosition + visibleRange*/;
            int range = lastVisiblePosition - firstVisiblePosition;
            int position;
            if (firstVisiblePosition == 0) {
                position = 0;
            } else if (lastVisiblePosition == recyclerView.getAdapter().getItemCount() - 1) {
                position = recyclerView.getAdapter().getItemCount() - 1;
            } else {
                position = firstVisiblePosition;
            }
            float proportion = (float) position / (float) (recyclerView.getAdapter().getItemCount() - range);
            setPosition(height * proportion);
        }
    }
}
