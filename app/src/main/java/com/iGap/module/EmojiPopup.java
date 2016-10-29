/*
 * Copyright 2014 Ankush Sachdeva
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iGap.module;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.PopupWindow;
import com.iGap.R;
import com.iGap.adapter.EmojiPagerAdapter;
import com.iGap.emoji.Food;
import com.iGap.emoji.Nature;
import com.iGap.emoji.Objects;
import com.iGap.emoji.People;
import com.iGap.emoji.Places;
import com.iGap.interfaces.IEmojiBackspaceClick;
import com.iGap.interfaces.IEmojiClickListener;
import com.iGap.interfaces.IEmojiLongClickListener;
import com.iGap.interfaces.IEmojiStickerClick;
import com.iGap.interfaces.IEmojiViewCreate;
import com.iGap.interfaces.IRecents;
import com.iGap.interfaces.IRecentsLongClick;
import com.iGap.interfaces.ISoftKeyboardOpenClose;
import java.util.Arrays;

public class EmojiPopup extends PopupWindow implements ViewPager.OnPageChangeListener, IRecents {
    private final View mRootView;
    private final Context mContext;
    private final IEmojiViewCreate mEmojiViewCreateListener;
    private IEmojiClickListener mEmojiClickListener;
    private IEmojiLongClickListener mEmojiLongClickListener;
    private IEmojiBackspaceClick mEmojiBackspaceClickListener;
    private ISoftKeyboardOpenClose mSoftKeyboardOpenCloseListener;
    private IEmojiStickerClick mEmojiStickerClickListener;
    private int mEmojiTabLastSelectedIndex = -1;
    private View[] mEmojiCategories;
    private EmojiRecentsManager mRecentsManager;
    private int mKeyboardHeight = 0;
    private boolean mPendingOpen = false;
    private boolean mIsOpened = false;
    private ViewPager mEmojiPager;
    private IRecentsLongClick mRecentsLongClickListener;

    public void setEmojiStickerClickListener(IEmojiStickerClick listener) {
        this.mEmojiStickerClickListener = listener;
    }

    /**
     * Constructor
     *
     * @param rootView The top most layout in your view hierarchy. The difference of this view and the screen height will be used to calculate the keyboard height.
     * @param context  The context of current activity.
     */
    public EmojiPopup(View rootView, Context context, IEmojiViewCreate emojiViewCreateListener) {
        super(context);
        this.mContext = context;
        this.mRootView = rootView;
        mEmojiViewCreateListener = emojiViewCreateListener;
        View customView = createPopupView();
        setContentView(customView);
        setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //default size
        setSize((int) context.getResources().getDimension(R.dimen.keyboard_height), LayoutParams.MATCH_PARENT);
    }

    public IEmojiLongClickListener getEmojiLongClickListener() {
        return mEmojiLongClickListener;
    }

    public void setEmojiLongClickListener(IEmojiLongClickListener listener) {
        this.mEmojiLongClickListener = listener;
    }

    public void setRecentsLongClick(IRecentsLongClick listener) {
        this.mRecentsLongClickListener = listener;
    }

    private View createPopupView() {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.emoji_popup, null, false);
        mRecentsManager = EmojiRecentsManager.getInstance(view.getContext());
        mEmojiPager = (ViewPager) view.findViewById(R.id.emojiPager);
        mEmojiPager.addOnPageChangeListener(EmojiPopup.this);
        EmojiPagerAdapter adapter = new EmojiPagerAdapter(
                Arrays.asList(
                        new EmojiRecentsGridView(mContext, null, null, EmojiPopup.this),
                        new EmojiGridView(mContext, People.DATA, EmojiPopup.this, EmojiPopup.this),
                        new EmojiGridView(mContext, Nature.DATA, EmojiPopup.this, EmojiPopup.this),
                        new EmojiGridView(mContext, Food.DATA, EmojiPopup.this, EmojiPopup.this),
                        new EmojiGridView(mContext, Places.DATA, EmojiPopup.this, EmojiPopup.this),
                        new EmojiGridView(mContext, Objects.DATA, EmojiPopup.this, EmojiPopup.this)
                )
        );
        mEmojiPager.setAdapter(adapter);

        mEmojiCategories = new View[7];
        mEmojiCategories[0] = view.findViewById(R.id.emojiCategoryRecents);
        mEmojiCategories[1] = view.findViewById(R.id.emojiCategoryPeople);
        mEmojiCategories[2] = view.findViewById(R.id.emojiCategoryNature);
        mEmojiCategories[3] = view.findViewById(R.id.emojiCategoryFood);
        mEmojiCategories[4] = view.findViewById(R.id.emojiCategoryPlaces);
        mEmojiCategories[5] = view.findViewById(R.id.emojiCategoryObjects);
        mEmojiCategories[6] = view.findViewById(R.id.emojiCategoryStickers);
        for (int i = 0; i < mEmojiCategories.length; i++) {
            final int position = i;
            mEmojiCategories[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEmojiPager.setCurrentItem(position);
                }
            });
            if (i == 0) {
                mEmojiCategories[i].setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        return mRecentsLongClickListener != null && mRecentsLongClickListener.onRecentsLongClick(view, mRecentsManager);
                    }
                });
            }
            // last category is the sticker emoji
            else if (i == mEmojiCategories.length - 1) {
                mEmojiCategories[6].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mEmojiStickerClickListener != null) {
                            mEmojiStickerClickListener.onEmojiStickerClick(view);
                        }
                    }
                });
            }
        }
        view.findViewById(R.id.backspace).setOnTouchListener(new RepeatListener(1000, 50, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmojiBackspaceClickListener != null) {
                    mEmojiBackspaceClickListener.onEmojiBackspaceClick(view);
                }
            }
        }));

        // get last selected page
        int page = 0;
        // last page was recents, check if there are recents to use
        // if none was found, go to page 1
        if (mRecentsManager.size() == 0) {
            page = 1;
        }

        if (page == 0) {
            onPageSelected(page);
        } else {
            mEmojiPager.setCurrentItem(page, false);
        }

        return view;
    }

    @Override
    public void setContentView(View contentView) {
        super.setContentView(contentView);
        if (mEmojiViewCreateListener != null) {
            mEmojiViewCreateListener.onEmojiViewCreate(contentView, this);
        }
    }

    /**
     * Manually set the popup window size
     *
     * @param width  Width of the popup
     * @param height Height of the popup
     */
    private void setSize(int width, int height) {
        setWidth(width);
        setHeight(height);
    }

    /**
     * Dismiss the popup
     */
    @Override
    public void dismiss() {
        super.dismiss();
        EmojiRecentsManager
                .getInstance(mContext).saveRecents();
    }

    public IEmojiClickListener getEmojiClickListener() {
        return mEmojiClickListener;
    }

    /**
     * Set the listener for the event of keyboard opening or closing.
     */
    public void setOnSoftKeyboardOpenCloseListener(ISoftKeyboardOpenClose listener) {
        this.mSoftKeyboardOpenCloseListener = listener;
    }

    /**
     * Set the listener for the event when any of the emoji is clicked
     */
    public void setOnEmojiClickListener(IEmojiClickListener listener) {
        this.mEmojiClickListener = listener;
    }

    /**
     * Set the listener for the event when backspace on emoji popup is clicked
     */
    public void setOnEmojiBackspaceClickListener(IEmojiBackspaceClick listener) {
        this.mEmojiBackspaceClickListener = listener;
    }

    /**
     * Use this function when the soft keyboard has not been opened yet. This
     * will show the emoji popup after the keyboard is up next time.
     * Generally, you will be calling InputMethodManager.showSoftInput function after
     * calling this function.
     */
    public void showAtBottomPending() {
        if (isKeyboardOpen())
            showAtBottom();
        else
            mPendingOpen = true;
    }

    /**
     * @return Returns true if the soft keyboard is open, false otherwise.
     */
    public boolean isKeyboardOpen() {
        return mIsOpened;
    }

    /**
     * Use this function to show the emoji popup.
     * NOTE: Since, the soft keyboard sizes are variable on different android devices, the
     * library needs you to open the soft keyboard atleast once before calling this function.
     * If that is not possible see showAtBottomPending() function.
     */
    public void showAtBottom() {
        showAtLocation(mRootView, Gravity.BOTTOM, 0, 0);
    }

    /**
     * Call this function to resize the emoji popup according to your soft keyboard size
     */
    public void setSizeForSoftKeyboard() {
        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                mRootView.getWindowVisibleDisplayFrame(r);

                int screenHeight = getUsableScreenHeight();
                int heightDifference = screenHeight
                        - (r.bottom - r.top);
                int resourceId = mContext.getResources()
                        .getIdentifier("status_bar_height",
                                "dimen", "android");
                if (resourceId > 0) {
                    heightDifference -= mContext.getResources()
                            .getDimensionPixelSize(resourceId);
                }
                if (heightDifference > 100) {
                    mKeyboardHeight = heightDifference;
                    setSize(LayoutParams.MATCH_PARENT, mKeyboardHeight);
                    if (!mIsOpened) {
                        if (mSoftKeyboardOpenCloseListener != null)
                            mSoftKeyboardOpenCloseListener.onKeyboardOpen(mKeyboardHeight);
                    }
                    mIsOpened = true;
                    if (mPendingOpen) {
                        showAtBottom();
                        mPendingOpen = false;
                    }
                } else {
                    mIsOpened = false;
                    if (mSoftKeyboardOpenCloseListener != null)
                        mSoftKeyboardOpenCloseListener.onKeyboardClose();
                }
            }
        });
    }

    private int getUsableScreenHeight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metrics = new DisplayMetrics();

            WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(metrics);

            return metrics.heightPixels;

        } else {
            return mRootView.getRootView().getHeight();
        }
    }

    @Override
    public void addRecent(Context context, String emoji) {
        EmojiRecentsGridView fragment = ((EmojiPagerAdapter) mEmojiPager.getAdapter()).getRecentFragment();
        fragment.addRecent(context, emoji);
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {
    }

    @Override
    public void onPageSelected(int i) {
        if (mEmojiTabLastSelectedIndex == i) {
            return;
        }
        if (mEmojiTabLastSelectedIndex >= 0 && mEmojiTabLastSelectedIndex < mEmojiCategories.length) {
            mEmojiCategories[mEmojiTabLastSelectedIndex].setSelected(false);
        }
        mEmojiCategories[i].setSelected(true);
        mEmojiTabLastSelectedIndex = i;
        mRecentsManager.setRecentPage(i);
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }

    /**
     * <p>
     * A class, that can be used as a TouchListener on any view (e.g. a Button).
     * It cyclically runs a clickListener, emulating keyboard-like behaviour. First
     * click is fired immediately, next before initialInterval, and subsequent before
     * normalInterval.
     * </p>
     * <p>
     * Interval is scheduled before the onClick completes, so it has to run fast.
     * If it runs slow, it does not generate skipped onClicks.
     * </p>
     */
    public static class RepeatListener implements View.OnTouchListener {

        private final int normalInterval;
        private final View.OnClickListener clickListener;
        private Handler handler = new Handler();
        private int initialInterval;
        private View downView;
        private Runnable handlerRunnable = new Runnable() {
            @Override
            public void run() {
                if (downView == null) {
                    return;
                }
                handler.removeCallbacksAndMessages(downView);
                handler.postAtTime(this, downView, SystemClock.uptimeMillis() + normalInterval);
                clickListener.onClick(downView);
            }
        };

        /**
         * @param initialInterval The interval before first click event
         * @param normalInterval  The interval before second and subsequent click
         *                        events
         * @param clickListener   The OnClickListener, that will be called
         *                        periodically
         */
        public RepeatListener(int initialInterval, int normalInterval, View.OnClickListener clickListener) {
            if (clickListener == null)
                throw new IllegalArgumentException("null runnable");
            if (initialInterval < 0 || normalInterval < 0)
                throw new IllegalArgumentException("negative interval");

            this.initialInterval = initialInterval;
            this.normalInterval = normalInterval;
            this.clickListener = clickListener;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downView = view;
                    handler.removeCallbacks(handlerRunnable);
                    handler.postAtTime(handlerRunnable, downView, SystemClock.uptimeMillis() + initialInterval);
                    clickListener.onClick(view);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_OUTSIDE:
                    handler.removeCallbacksAndMessages(downView);
                    downView = null;
                    return true;
            }
            return false;
        }
    }
}
