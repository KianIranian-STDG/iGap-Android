package net.iGap.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.theme.Theme;
import net.iGap.messenger.theme.ThemeDescriptor;
import net.iGap.messenger.ui.cell.EmptyCell;
import net.iGap.messenger.ui.cell.SearchRoomCell;
import net.iGap.messenger.ui.cell.SelectRoomCell;
import net.iGap.messenger.ui.toolBar.Toolbar;
import net.iGap.module.AndroidUtils;
import net.iGap.module.customView.RecyclerListView;

import java.util.ArrayList;
import java.util.List;


public class SelectRoomFragment extends BaseFragment {

    private final ArrayList<SearchRoomCell> allSpans = new ArrayList<>();
    private final SparseArray<SearchRoomCell> selectedContacts = new SparseArray<>();
    private RecyclerListView recyclerListView;
    private EditText editText;
    private int fieldY;
    private int containerHeight;
    private boolean ignoreScrollEvent;
    private int selectedCount;
    private LinearLayout linearLayout;
    private SpansContainer spanContainer;

    @Override
    public View createView(Context context) {
        allSpans.clear();
        selectedContacts.clear();

        fragmentView = new FrameLayout(context);
        fragmentView.setBackgroundColor(Theme.getColor(Theme.key_window_background));
        FrameLayout frameLayout = (FrameLayout) fragmentView;

        linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        editText = new EditText(context);
        editText.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        editText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 8);
        editText.setBackgroundColor(Theme.getColor(Theme.key_window_background));
        editText.setHint(R.string.search_users_groups);
        editText.setTextColor(Theme.getColor(Theme.key_default_text));
        editText.requestFocus();

        spanContainer = new SpansContainer(context);
        spanContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.clearFocus();
                editText.requestFocus();
            }
        });
        spanContainer.addView(editText);

        recyclerListView = new RecyclerListView(context);
        recyclerListView.setClipToPadding(false);
        recyclerListView.setPadding(0, 0, 0, LayoutCreator.dp(20));
        recyclerListView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        SelectRoomAdapter selectRoomAdapter = new SelectRoomAdapter();
        recyclerListView.setAdapter(selectRoomAdapter);
        recyclerListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (view instanceof SelectRoomCell) {
                    SelectRoomCell selectRoomCell = (SelectRoomCell) view;
                    selectRoomCell.setCheck();
                    AddSelectRoomCell(position);
                }
            }
        });
        linearLayout.addView(spanContainer, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT));
        linearLayout.addView(recyclerListView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT));
        frameLayout.addView(linearLayout, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.TOP));
        return fragmentView;
    }

    @Override
    public View createToolBar(Context context) {
        Toolbar toolbar = new Toolbar(context);
        toolbar.setTitle("Select Room");
        return toolbar;
    }

    /*@Override
    public List<ThemeDescriptor> getThemeDescriptor() {
        List<ThemeDescriptor> result = new ArrayList<>();
        result.add(new ThemeDescriptor(recyclerListView, ThemeDescriptor.FLAG_CELLBACKGROUNDCOLOR, Theme.key_window_background_white));
        result.add(new ThemeDescriptor(recyclerListView, ThemeDescriptor.FLAG_BACKGROUND, Theme.key_window_background_grey));
        result.add(new ThemeDescriptor(recyclerListView, ThemeDescriptor.FLAG_TEXTCOLOR, Theme.key_menu_item));
        result.add(new ThemeDescriptor(recyclerListView, ThemeDescriptor.FLAG_IMAGECOLOR, Theme.key_menu_item));
        result.add(new ThemeDescriptor(toolbar, ThemeDescriptor.FLAG_BACKGROUND, Theme.key_tool_bar_default));
        return result;
    }*/

    private void AddSelectRoomCell(int position) {
        SearchRoomCell searchRoomCell = new SearchRoomCell(context);
        searchRoomCell.setId(position);
        searchRoomCell.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                spanContainer.removeSpan((SearchRoomCell) view);
                return true;
            }
        });
        spanContainer.addSpan(searchRoomCell, true);
    }

    @Keep
    public int getContainerHeight() {
        return containerHeight;
    }

    @Keep
    public void setContainerHeight(int value) {
        containerHeight = value;
        if (spanContainer != null) {
            spanContainer.requestLayout();
        }
    }

    public class SelectRoomAdapter extends RecyclerListView.SelectionAdapter {

        private final int chatsListCount = 3;

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int viewType = holder.getItemViewType();
            return viewType != 0;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View cellView = null;
            switch (viewType) {
                case 0:
                    EmptyCell emptyCell = new EmptyCell(context);
                    emptyCell.setBackgroundColor(Theme.getColor(Theme.key_window_background));
                    cellView = emptyCell;
                    break;
                case 1:
                    SelectRoomCell selectRoomCell = new SelectRoomCell(context);
                    selectRoomCell.setBackgroundColor(Theme.getColor(Theme.key_window_background));
                    cellView = selectRoomCell;
                    break;
            }
            return new RecyclerListView.Holder(cellView);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            int viewType = holder.getItemViewType();
            switch (viewType) {
                case 0:
                    EmptyCell emptyCell = (EmptyCell) holder.itemView;
                    break;
                case 1:
                    SelectRoomCell selectRoomCell = (SelectRoomCell) holder.itemView;
                    if (position == 1) {
                        selectRoomCell.setText(getString(R.string.contacts));
                    } else if (position == 2) {
                        selectRoomCell.setText(getString(R.string.non_contacts));
                    } else if (position == 3) {
                        selectRoomCell.setText(getString(R.string.groups));
                    } else if (position == 4) {
                        selectRoomCell.setText(getString(R.string.channels));
                    } else if (position == 5) {
                        selectRoomCell.setText(getString(R.string.bots));
                    }
                    break;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0 || position == 6) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public int getItemCount() {
            return chatsListCount + 7;
        }
    }

    private class SpansContainer extends ViewGroup {

        private final ArrayList<Animator> animators = new ArrayList<>();
        private AnimatorSet currentAnimation;
        private boolean animationStarted;
        private View addingSpan;
        private View removingSpan;

        public SpansContainer(Context context) {
            super(context);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int count = getChildCount();
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int maxWidth = width - LayoutCreator.dp(26);
            int currentLineWidth = 0;
            int y = LayoutCreator.dp(10);
            int allCurrentLineWidth = 0;
            int allY = LayoutCreator.dp(10);
            int x;
            for (int a = 0; a < count; a++) {
                View child = getChildAt(a);
                if (!(child instanceof SearchRoomCell)) {
                    continue;
                }
                child.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(32), MeasureSpec.EXACTLY));
                if (child != removingSpan && currentLineWidth + child.getMeasuredWidth() > maxWidth) {
                    y += child.getMeasuredHeight() + LayoutCreator.dp(8);
                    currentLineWidth = 0;
                }
                if (allCurrentLineWidth + child.getMeasuredWidth() > maxWidth) {
                    allY += child.getMeasuredHeight() + LayoutCreator.dp(8);
                    allCurrentLineWidth = 0;
                }
                x = LayoutCreator.dp(13) + currentLineWidth;
                if (!animationStarted) {
                    if (child == removingSpan) {
                        child.setTranslationX(LayoutCreator.dp(13) + allCurrentLineWidth);
                        child.setTranslationY(allY);
                    } else if (removingSpan != null) {
                        if (child.getTranslationX() != x) {
                            animators.add(ObjectAnimator.ofFloat(child, View.TRANSLATION_X, x));
                        }
                        if (child.getTranslationY() != y) {
                            animators.add(ObjectAnimator.ofFloat(child, View.TRANSLATION_Y, y));
                        }
                    } else {
                        child.setTranslationX(x);
                        child.setTranslationY(y);
                    }
                }
                if (child != removingSpan) {
                    currentLineWidth += child.getMeasuredWidth() + LayoutCreator.dp(9);
                }
                allCurrentLineWidth += child.getMeasuredWidth() + LayoutCreator.dp(9);
            }
            int minWidth = (Math.min(AndroidUtils.displaySize.x, AndroidUtils.displaySize.y) - LayoutCreator.dp(26 + 18 + 57 * 2)) / 3;
            if (maxWidth - currentLineWidth < minWidth) {
                currentLineWidth = 0;
                y += LayoutCreator.dp(32 + 8);
            }
            if (maxWidth - allCurrentLineWidth < minWidth) {
                allY += LayoutCreator.dp(32 + 8);
            }
            editText.measure(MeasureSpec.makeMeasureSpec(maxWidth - currentLineWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(32), MeasureSpec.EXACTLY));
            if (!animationStarted) {
                int currentHeight = allY + LayoutCreator.dp(32 + 10);
                int fieldX = currentLineWidth + LayoutCreator.dp(16);
                fieldY = y;
                if (currentAnimation != null) {
                    int resultHeight = y + LayoutCreator.dp(32 + 10);
                    if (containerHeight != resultHeight) {
                        animators.add(ObjectAnimator.ofInt(SelectRoomFragment.this, "containerHeight", resultHeight));
                    }
                    if (editText.getTranslationX() != fieldX) {
                        animators.add(ObjectAnimator.ofFloat(editText, View.TRANSLATION_X, fieldX));
                    }
                    if (editText.getTranslationY() != fieldY) {
                        animators.add(ObjectAnimator.ofFloat(editText, View.TRANSLATION_Y, fieldY));
                    }
                    currentAnimation.playTogether(animators);
                    currentAnimation.start();
                    animationStarted = true;
                } else {
                    containerHeight = currentHeight;
                    editText.setTranslationX(fieldX);
                    editText.setTranslationY(fieldY);
                }
            } else if (currentAnimation != null) {
                if (!ignoreScrollEvent && removingSpan == null) {
                    editText.bringPointIntoView(editText.getSelectionStart());
                }
            }
            setMeasuredDimension(width, containerHeight);
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            int count = getChildCount();
            for (int a = 0; a < count; a++) {
                View child = getChildAt(a);
                child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
            }
        }

        public void addSpan(final SearchRoomCell span, boolean animated) {
            allSpans.add(span);
            int uid = span.getId();
            if (uid > Integer.MIN_VALUE + 7) {
                selectedCount++;
            }
            selectedContacts.put(uid, span);

            editText.setHint("");
            if (currentAnimation != null) {
                currentAnimation.setupEndValues();
                currentAnimation.cancel();
            }
            animationStarted = false;
            if (animated) {
                currentAnimation = new AnimatorSet();
                currentAnimation.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        addingSpan = null;
                        currentAnimation = null;
                        animationStarted = false;
                    }
                });
                currentAnimation.setDuration(150);
                addingSpan = span;
                animators.clear();
                animators.add(ObjectAnimator.ofFloat(addingSpan, View.SCALE_X, 0.01f, 1.0f));
                animators.add(ObjectAnimator.ofFloat(addingSpan, View.SCALE_Y, 0.01f, 1.0f));
                animators.add(ObjectAnimator.ofFloat(addingSpan, View.ALPHA, 0.0f, 1.0f));
            }
            addView(span);
        }

        public void removeSpan(final SearchRoomCell span) {
            ignoreScrollEvent = true;
            int uid = span.getId();
            if (uid > Integer.MIN_VALUE + 7) {
                selectedCount--;
            }
            selectedContacts.remove(uid);
            allSpans.remove(span);
            span.setOnClickListener(null);

            if (currentAnimation != null) {
                currentAnimation.setupEndValues();
                currentAnimation.cancel();
            }
            animationStarted = false;
            currentAnimation = new AnimatorSet();
            currentAnimation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    removeView(span);
                    removingSpan = null;
                    currentAnimation = null;
                    animationStarted = false;
                    if (allSpans.isEmpty()) {
                        editText.setHint("جستجو کاربران و گروه ها ...");
                    }
                }
            });
            currentAnimation.setDuration(150);
            removingSpan = span;
            animators.clear();
            animators.add(ObjectAnimator.ofFloat(removingSpan, View.SCALE_X, 1.0f, 0.01f));
            animators.add(ObjectAnimator.ofFloat(removingSpan, View.SCALE_Y, 1.0f, 0.01f));
            animators.add(ObjectAnimator.ofFloat(removingSpan, View.ALPHA, 1.0f, 0.0f));
            requestLayout();
        }
    }
}

