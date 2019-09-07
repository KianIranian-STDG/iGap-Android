package net.iGap.module;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.ContactItemGroup;
import net.iGap.fragments.FragmentBlockedUser;
import net.iGap.fragments.FragmentSyncRegisteredContacts;
import net.iGap.fragments.RegisteredContactsFragment;

import org.jetbrains.annotations.NotNull;

public class FastScroller extends LinearLayout {

    private AppCompatImageView bubble;
    private AppCompatTextView handle;
    private RecyclerView recyclerView;

    private static final int HANDLE_ANIMATION_DURATION = 100;
    private static final int HANDLE_HIDE_DELAY = 500;
    private static final int TRACK_SNAP_RANGE = 5;

    private final HandleHider handleHider = new HandleHider();

    private static final String SCALE_X = "scaleX";
    private static final String SCALE_Y = "scaleY";
    private static final String ALPHA = "alpha";

    private AnimatorSet currentAnimator = null;

    private int height;
    /*private int totalItemCount;*/

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
            if (handle.getVisibility() == INVISIBLE) {
                showHandle();
            }
            recyclerView.setOnScrollListener(null);
            setRecyclerViewPosition(event.getY());
            bubble.setSelected(true);
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            recyclerView.setOnScrollListener(scrollListener);
            getHandler().postDelayed(handleHider, HANDLE_HIDE_DELAY);
            return true;
        }
        return super.onTouchEvent(event);
    }

    private class HandleHider implements Runnable {
        @Override
        public void run() {
            bubble.setSelected(false);
            hideHandle();
        }
    }

    private void setRecyclerViewPosition(float y) {
        if (recyclerView != null) {
            float proportion;
            if (bubble.getY() == 0) {
                proportion = 0f;
            } else if (bubble.getY() + bubble.getHeight() >= height - TRACK_SNAP_RANGE) {
                proportion = 1f;
            } else {
                proportion = y / (float) height;
            }
            int targetPos = getValueInRange(0, recyclerView.getAdapter().getItemCount() - 1, (int) (proportion * (float) recyclerView.getAdapter().getItemCount()));
            if (recyclerView.getAdapter() instanceof RegisteredContactsFragment.ContactListAdapter) {
                handle.setText(((RegisteredContactsFragment.ContactListAdapter) recyclerView.getAdapter()).getBubbleText(((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition()));
            }else if (recyclerView.getAdapter() instanceof FastAdapter){
                IItem iItem = ((FastAdapter) recyclerView.getAdapter()).getItem(((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition());
                if (iItem instanceof ContactItemGroup){
                    handle.setText(((ContactItemGroup) iItem).getBubbleText());
                }
            }else if (recyclerView.getAdapter() instanceof FragmentBlockedUser.BlockListAdapter){
                handle.setText(((FragmentBlockedUser.BlockListAdapter) recyclerView.getAdapter()).getBubbleText(((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition()));
            } else if(recyclerView.getAdapter() instanceof FragmentSyncRegisteredContacts.ContactListAdapter2) {
                handle.setText(((FragmentSyncRegisteredContacts.ContactListAdapter2) recyclerView.getAdapter()).getBubbleText(((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition()));
            }

            recyclerView.scrollToPosition(targetPos);
        }
    }

    private void initialise(Context context) {
        setOrientation(HORIZONTAL);
        setClipChildren(false);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.fastscroller, this);
        bubble = findViewById(R.id.fastscroller_bubble);
        handle = findViewById(R.id.fastscroller_handle);

        int bubbleResId;
        int handleResId;
        if (G.isDarkTheme) {
            handle.setTextColor(context.getResources().getColor(R.color.gray));
            bubbleResId = R.drawable.fastscroll_handle_dark;
            if (G.isAppRtl) {
                handleResId = R.drawable.fastscroll_bubble_dark_left;
            } else {
                handleResId = R.drawable.fastscroll_bubble_dark_right;
            }
        } else {
            handle.setTextColor(context.getResources().getColor(R.color.white));
            bubbleResId = R.drawable.fastscroll_handle_light;
            if (G.isAppRtl) {
                handleResId = R.drawable.fastscroll_bubble_light_left;
            } else {
                handleResId = R.drawable.fastscroll_bubble_light_right;
            }
        }
        bubble.setImageResource(bubbleResId);
        handle.setBackgroundResource(handleResId);
    }

    private void setPosition(float y) {
        float position = y / height;
        int bubbleHeight = bubble.getHeight();
        bubble.setY(getValueInRange(0, height - bubbleHeight, (int) ((height - bubbleHeight) * position)));
        int handleHeight = handle.getHeight();
        handle.setY(getValueInRange(0, height - handleHeight, (int) ((height - handleHeight) * position)));
    }

    private int getValueInRange(int min, int max, int value) {
        int minimum = Math.max(min, value);
        return Math.min(minimum, max);
    }

    private void showHandle() {
        AnimatorSet animatorSet = new AnimatorSet();
        handle.setPivotX(handle.getWidth());
        handle.setPivotY(handle.getHeight());
        handle.setVisibility(VISIBLE);
        Animator growerX = ObjectAnimator.ofFloat(handle, SCALE_X, 0f, 1f).setDuration(HANDLE_ANIMATION_DURATION);
        Animator growerY = ObjectAnimator.ofFloat(handle, SCALE_Y, 0f, 1f).setDuration(HANDLE_ANIMATION_DURATION);
        Animator alpha = ObjectAnimator.ofFloat(handle, ALPHA, 0f, 1f).setDuration(HANDLE_ANIMATION_DURATION);
        animatorSet.playTogether(growerX, growerY, alpha);
        animatorSet.start();
    }

    private void hideHandle() {
        currentAnimator = new AnimatorSet();
        if (G.isAppRtl) {
            handle.setPivotX(0);
        } else {
            handle.setPivotX(handle.getWidth());
        }
        handle.setPivotY(handle.getHeight());
        Animator shrinkerX = ObjectAnimator.ofFloat(handle, SCALE_X, 1f, 0f).setDuration(HANDLE_ANIMATION_DURATION);
        Animator shrinkerY = ObjectAnimator.ofFloat(handle, SCALE_Y, 1f, 0f).setDuration(HANDLE_ANIMATION_DURATION);
        Animator alpha = ObjectAnimator.ofFloat(handle, ALPHA, 1f, 0f).setDuration(HANDLE_ANIMATION_DURATION);
        currentAnimator.playTogether(shrinkerX, shrinkerY, alpha);
        currentAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                handle.setVisibility(INVISIBLE);
                currentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                handle.setVisibility(INVISIBLE);
                currentAnimator = null;
            }
        });
        currentAnimator.start();
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
