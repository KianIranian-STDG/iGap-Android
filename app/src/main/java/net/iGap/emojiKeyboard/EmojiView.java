package net.iGap.emojiKeyboard;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import net.iGap.R;
import net.iGap.Theme;
import net.iGap.emojiKeyboard.View.CubicBezierInterpolator;
import net.iGap.emojiKeyboard.View.ScrollTabView;
import net.iGap.emojiKeyboard.adapter.EmojiAdapter;
import net.iGap.emojiKeyboard.adapter.EmojiGridAdapter;
import net.iGap.emojiKeyboard.adapter.ViewPagerAdapter;
import net.iGap.emojiKeyboard.emoji.EmojiManager;
import net.iGap.emojiKeyboard.sticker.StickerGroupAdapter;
import net.iGap.emojiKeyboard.struct.StructIGEmojiGroup;
import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.fragments.emoji.struct.StructIGStickerGroup;
import net.iGap.helper.LayoutCreator;
import net.iGap.repository.sticker.StickerRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

@SuppressLint("ViewConstructor")
public class EmojiView extends FrameLayout implements ViewPager.OnPageChangeListener {

    public static final int EMOJI = 0;
    public static final int STICKER = 1;

    public boolean isShow;
    public int currentPage;

    private ViewPager viewPager;
    private ViewPagerAdapter pagerAdapter;
    private List<View> views = new ArrayList<>();

    private View bottomViewShadow;
    private FrameLayout bottomContainer;
    private AppCompatImageView emojiIv;
    private AppCompatImageView stickerIv;
    private AppCompatImageView settingIv;

    private FrameLayout emojiContainer;
    private RecyclerView emojiGridView;
    private EmojiGridAdapter emojiGridAdapter;
    private LinearLayoutManager emojiLayoutManager;
    private ScrollTabView emojiTopView;
    private Drawable[] emojiTabDrawables;
    private int emojiTabViewY;


    private FrameLayout stickerContainer;
    private RecyclerView stickerGridView;
    private StickerGroupAdapter stickerGridAdapter;
    private LinearLayoutManager stickersLayoutManager;
    private AnimatorSet bottomTabContainerAnimation;
    private float lastBottomScrollY;
    private int stickerOffset;
    private AppCompatImageView addStickerIv;
    private ScrollTabView stickerTabView;
    private AppCompatImageView emptyIv;
    private int stickersTabViewY;
    private AppCompatTextView emptyTv;
    private Drawable[] stickerTabDrawable;

    private StickerRepository stickerRepository;
    private int groupSize = -1;

    private int layoutHeight;
    private int layoutWidth;

    private Listener listener;
    private String TAG = "abbasiEmoji";

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private boolean hasSticker;
    private boolean hasEmoji;

    public EmojiView(@NonNull Context context, boolean needSticker, boolean needEmoji) {
        super(context);
        hasEmoji = needEmoji;
        hasSticker = needSticker;

        Log.i(TAG, "EmojiView: ");

        if (hasEmoji) {
            emojiContainer = new FrameLayout(getContext());

            emojiGridAdapter = new EmojiGridAdapter();

            emojiTopView = new ScrollTabView(getContext());
            emojiTopView.setIndicatorHeight(LayoutCreator.dp(1.5f));
            emojiTopView.setIndicatorColor(Theme.getInstance().getAccentColor(getContext()));
            emojiTopView.setBackgroundColor(Color.parseColor("#E0E0E0"));
            emojiTopView.setShouldExpand(true);
            emojiTopView.setListener(page -> {

            });

            emojiTabDrawables = new Drawable[]{
                    getResources().getDrawable(R.drawable.ic_emoji_history),
                    getResources().getDrawable(R.drawable.ic_emoji_emojis),
                    getResources().getDrawable(R.drawable.ic_emoji_animals),
                    getResources().getDrawable(R.drawable.ic_emoji_food),
                    getResources().getDrawable(R.drawable.ic_emoji_sports),
                    getResources().getDrawable(R.drawable.ic_emoji_vehicle),
                    getResources().getDrawable(R.drawable.ic_emoji_light),
                    getResources().getDrawable(R.drawable.ic_emoji_signs),
                    getResources().getDrawable(R.drawable.ic_emoji_flags),
            };


            int packCount = EmojiManager.getInstance().getCategoryManager().getCategorySize();

            List<StructIGEmojiGroup> structIGEmojiGroups = new ArrayList<>();

            for (int i = 0; i < packCount; i++) {

                StructIGEmojiGroup structIGEmojiGroup = new StructIGEmojiGroup();
                structIGEmojiGroup.setCategoryName(EmojiManager.getInstance().getCategoryManager().getEmojiCategory()[i].getName());
                if (EmojiManager.getInstance().getCategoryManager().getEmojiCategory()[i].hasColored())
                    for (int j = 0; j < EmojiManager.getInstance().getCategoryManager().getEmojiCategory()[i].getColoredEmojiSize(); j++)
                        structIGEmojiGroup.setStrings(Arrays.asList(EmojiManager.getInstance().getCategoryManager().getEmojiCategory()[i].getColoredEmojies()));
                else
                    for (int j = 0; j < EmojiManager.getInstance().getCategoryManager().getEmojiCategory()[i].getCategorySize(); j++)
                        structIGEmojiGroup.setStrings(Arrays.asList(EmojiManager.getInstance().getCategoryManager().getEmojiCategory()[i].getEmojies()));

                structIGEmojiGroups.add(structIGEmojiGroup);
            }

            EmojiManager.getInstance().loadRecentEmoji();
            EmojiManager.getInstance().sortEmoji();

            Log.i(TAG, "recent emoji size -> " + EmojiManager.getInstance().getRecentEmoji());

            for (int i = 0; i < EmojiManager.getInstance().getRecentEmoji().size(); i++) {
                Log.i(TAG, "recent: " + i + " -> " + EmojiManager.getInstance().getRecentEmoji().get(i));
            }

            emojiGridAdapter.setStructIGEmojiGroups(structIGEmojiGroups);

            emojiGridAdapter.setListener(new EmojiAdapter.Listener() {
                @Override
                public void onClick(String emojiCode) {
                    EmojiManager.getInstance().addRecentEmoji(emojiCode);
                    EmojiManager.getInstance().saveRecentEmoji();

                    EmojiManager.getInstance().loadRecentEmoji();
                    EmojiManager.getInstance().sortEmoji();

                    Log.i(TAG, "recent emoji size -> " + EmojiManager.getInstance().getRecentEmoji());

                    for (int i = 0; i < EmojiManager.getInstance().getRecentEmoji().size(); i++) {
                        Log.i(TAG, "recent: " + i + " -> " + EmojiManager.getInstance().getRecentEmoji().get(i));
                    }


                    if (listener != null)
                        listener.onEmojiSelected(emojiCode);
                }

                @Override
                public boolean onLongClick(String emojiCode) {
                    return false;
                }
            });

            for (Drawable emojiTabDrawable : emojiTabDrawables) {
                emojiTopView.addIconTab(emojiTabDrawable);
            }
            emojiTopView.updateTabStyles();

            emojiGridView = new RecyclerView(getContext());
            emojiGridView.setAdapter(emojiGridAdapter);
            emojiGridView.setLayoutManager(emojiLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
            emojiGridView.setClipToPadding(false);
            emojiGridView.setPadding(0, LayoutCreator.dpToPx(40), 0, LayoutCreator.dpToPx(40));

            emojiGridView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    checkBottomTabScroll(dy);
                    checkEmojiTabY(recyclerView, dy);
                    super.onScrolled(recyclerView, dx, dy);
                }
            });

            emojiContainer.addView(emojiGridView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.TOP));
            emojiContainer.addView(emojiTopView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 35, Gravity.TOP));

            views.add(emojiContainer);
        }

        if (hasSticker) {

            stickerContainer = new FrameLayout(getContext());

            stickerRepository = new StickerRepository();

            stickerGridView = new RecyclerView(getContext());
            stickerGridView.setLayoutManager(stickersLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
            stickerGridView.setClipToPadding(false);
            stickerGridView.setPadding(0, LayoutCreator.dpToPx(40), 0, LayoutCreator.dpToPx(40));

            stickerGridAdapter = new StickerGroupAdapter();

            stickerGridView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    checkBottomTabScroll(dy);
                    checkStickersTabY(recyclerView, dy);
                    checkScroll();
                    super.onScrolled(recyclerView, dx, dy);
                }
            });

            stickerGridView.setAdapter(stickerGridAdapter);

            stickerGridAdapter.setListener(structIGSticker -> listener.onStickerClick(structIGSticker));

            stickerTabDrawable = new Drawable[]{
                    getResources().getDrawable(R.drawable.ic_sticker_history),
                    getResources().getDrawable(R.drawable.ic_add_sticker)
            };

            stickerTabView = new ScrollTabView(getContext());
            stickerTabView.setIndicatorHeight(LayoutCreator.dp(2.5f));
            stickerTabView.setIndicatorColor(Theme.getInstance().getAccentColor(getContext()));
            stickerTabView.setBackgroundColor(Color.parseColor("#E0E0E0"));
            stickerTabView.setListener(page -> {

                stickerGridView.stopScroll();
                stickersLayoutManager.scrollToPositionWithOffset(/*stickerGridAdapter.getPositionForGroup(stickerGridAdapter.getGroups().get(page))*/page, 0);

                stickerTabView.onPageScrolled(page, page);
                checkScroll();
                checkStickersTabY(null, 0);
            });


            addStickerIv = new AppCompatImageView(getContext());
            addStickerIv.setImageDrawable(stickerTabDrawable[1]);
            addStickerIv.setBackgroundColor(Color.parseColor("#E0E0E0"));
            addStickerIv.setScaleType(ImageView.ScaleType.CENTER);
            addStickerIv.setOnClickListener(v -> listener.onAddStickerClicked());

            stickerContainer.addView(stickerGridView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.CENTER));
            stickerContainer.addView(stickerTabView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 40, Gravity.TOP, 0, 0, 40, 0));
            stickerContainer.addView(addStickerIv, LayoutCreator.createFrame(40, 40, Gravity.TOP | Gravity.RIGHT));
            views.add(stickerContainer);

            Disposable disposable = stickerRepository.getStickerGroupWithRecentTabForEmojiView()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::onStickerChanged);

            stickerRepository.getCompositeDisposable().add(disposable);
        }

        stickerIv = new AppCompatImageView(getContext());
        stickerIv.setImageResource(R.drawable.ic_sticker);
        stickerIv.setScaleType(ImageView.ScaleType.CENTER);

        emojiIv = new AppCompatImageView(getContext());
        emojiIv.setImageResource(R.drawable.ic_emoji);
        emojiIv.setScaleType(ImageView.ScaleType.CENTER);

        settingIv = new AppCompatImageView(getContext());
        settingIv.setScaleType(ImageView.ScaleType.CENTER);

        settingIv.setOnClickListener(v -> {
            if (currentPage == EMOJI)
                listener.onBackSpace();
            else if (currentPage == STICKER)
                listener.onStickerSettingClick();
        });

        bottomContainer = new FrameLayout(getContext());
        bottomViewShadow = new View(getContext());

        bottomViewShadow.setBackgroundColor(Color.parseColor("#BDBDBD"));

        int emojiX = (layoutWidth / 2) - 20;
        int stickerX = (layoutWidth / 2) + 20;

        bottomContainer.addView(stickerIv, LayoutCreator.createFrame(30, 30, Gravity.CENTER, stickerX, 0, 0, 0));
        bottomContainer.addView(emojiIv, LayoutCreator.createFrame(30, 30, Gravity.CENTER, emojiX, 0, 0, 0));
        bottomContainer.addView(settingIv, LayoutCreator.createFrame(30, 30, Gravity.RIGHT | Gravity.CENTER_VERTICAL, 0, 0, 8, 0));

        bottomContainer.setBackgroundColor(Color.parseColor("#E0E0E0"));
        bottomContainer.addView(bottomViewShadow, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 1, Gravity.TOP));

        addView(bottomContainer, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 48, Gravity.BOTTOM));

        pagerAdapter = new ViewPagerAdapter(views);

        viewPager = new ViewPager(getContext());
        viewPager.setAdapter(pagerAdapter);

        stickerIv.setOnClickListener(v -> setToSticker());
        emojiIv.setOnClickListener(v -> setToEmoji());

        viewPager.addOnPageChangeListener(this);

        addView(viewPager, 0, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT));
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setBackgroundColor(Theme.getInstance().getDividerColor(getContext()));
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY));
    }

    private void checkStickersTabY(View list, int dy) {
        if (list == null) {
            stickerTabView.setTranslationY(stickersTabViewY = 0);
            addStickerIv.setTranslationY(stickersTabViewY = 0);
            return;
        }
        if (list.getVisibility() != VISIBLE) {
            return;
        }

        if (dy > 0 && stickerGridView != null && stickerGridView.getVisibility() == VISIBLE) {
            RecyclerView.ViewHolder holder = stickerGridView.findViewHolderForAdapterPosition(0);
            if (holder != null && holder.itemView.getTop() >= stickerGridView.getPaddingTop()) {
                return;
            }
        }
        stickersTabViewY -= dy;
        if (stickersTabViewY > 0) {
            stickersTabViewY = 0;
        } else if (stickersTabViewY < -LayoutCreator.dp(48 * 6)) {
            stickersTabViewY = -LayoutCreator.dp(48 * 6);
        }
        stickerTabView.setTranslationY(Math.max(-LayoutCreator.dp(48), stickersTabViewY));
        addStickerIv.setTranslationY(Math.max(-LayoutCreator.dp(48), stickersTabViewY));
    }

    private void checkEmojiTabY(View list, int dy) {
        if (list == null) {
            emojiTopView.setTranslationY(stickersTabViewY = 0);
            return;
        }
        if (list.getVisibility() != VISIBLE) {
            return;
        }

        if (dy > 0 && emojiGridView != null && emojiGridView.getVisibility() == VISIBLE) {
            RecyclerView.ViewHolder holder = emojiGridView.findViewHolderForAdapterPosition(0);
            if (holder != null && holder.itemView.getTop() >= emojiGridView.getPaddingTop()) {
                return;
            }
        }
        emojiTabViewY -= dy;
        if (emojiTabViewY > 0) {
            emojiTabViewY = 0;
        } else if (emojiTabViewY < -LayoutCreator.dp(48 * 6)) {
            emojiTabViewY = -LayoutCreator.dp(48 * 6);
        }
        emojiTopView.setTranslationY(Math.max(-LayoutCreator.dp(48), emojiTabViewY));
    }

    public void setContentView(int contentView) {
        viewPager.setCurrentItem(contentView);
        viewPagerItemChanged(contentView);
    }

    private void checkScroll() {
        int firstVisibleItem = stickersLayoutManager.findFirstVisibleItemPosition();
        if (firstVisibleItem == RecyclerView.NO_POSITION) {
            return;
        }
        if (stickerGridView == null) {
            return;
        }
        int firstTab = 0;
        stickerTabView.onPageScrolled(firstVisibleItem, firstTab);
    }

    private void setToSticker() {
        if (hasSticker)
            viewPager.setCurrentItem(STICKER, true);
    }

    private void setToEmoji() {
        if (hasSticker)
            viewPager.setCurrentItem(EMOJI, true);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        layoutWidth = right - left;
        layoutHeight = bottom - top;
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        viewPagerItemChanged(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void checkBottomTabScroll(float dy) {
        lastBottomScrollY += dy;
        int offset = LayoutCreator.dp(38);
        if (lastBottomScrollY >= offset) {
            showBottomTab(false);
        } else if (lastBottomScrollY <= -offset) {
            showBottomTab(true);
        } else if (bottomContainer.getTag() == null && lastBottomScrollY < 0 || bottomContainer.getTag() != null && lastBottomScrollY > 0) {
            lastBottomScrollY = 0;
        }
    }

    private void showBottomTab(boolean show) {
        lastBottomScrollY = 0;

        if (show && bottomContainer.getTag() == null || !show && bottomContainer.getTag() != null) {
            return;
        }

        if (bottomTabContainerAnimation != null) {
            bottomTabContainerAnimation.cancel();
            bottomTabContainerAnimation = null;
        }

        bottomContainer.setTag(show ? null : 1);

        bottomTabContainerAnimation = new AnimatorSet();
        bottomTabContainerAnimation.playTogether(
                ObjectAnimator.ofFloat(bottomContainer, View.TRANSLATION_Y, show ? 0 : LayoutCreator.dp(54)),
                ObjectAnimator.ofFloat(bottomViewShadow, View.TRANSLATION_Y, show ? 0 : LayoutCreator.dp(49)));
        bottomTabContainerAnimation.setDuration(200);
        bottomTabContainerAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        bottomTabContainerAnimation.start();
    }

    private void viewPagerItemChanged(int position) {
        if (position == EMOJI) {
            stickerIv.setColorFilter(Color.parseColor("#BDBDBD"), PorterDuff.Mode.SRC_IN);
            emojiIv.setColorFilter(Color.parseColor("#434343"), PorterDuff.Mode.SRC_IN);
            settingIv.setImageResource(R.drawable.ic_backspace);
        } else if (position == STICKER) {
            emojiIv.setColorFilter(Color.parseColor("#BDBDBD"), PorterDuff.Mode.SRC_IN);
            stickerIv.setColorFilter(Color.parseColor("#434343"), PorterDuff.Mode.SRC_IN);
            settingIv.setImageResource(R.drawable.ic_settings);
        }
        currentPage = position;
    }

    public void onStickerChanged(List<StructIGStickerGroup> structIGStickerGroups) {

        if (structIGStickerGroups.size() != groupSize) {
            stickerTabView.removeTabs();

            boolean hasRecent = false;
            if (structIGStickerGroups.size() > 0 && structIGStickerGroups.get(0).getGroupId().equals(StructIGStickerGroup.RECENT_GROUP)) {
                stickerTabView.addIconTab(stickerTabDrawable[0]);
                hasRecent = true;
            }

            for (int i = hasRecent ? 1 : 0; i < structIGStickerGroups.size(); i++) {
                stickerTabView.addStickerTab(structIGStickerGroups.get(i));
            }
        }
        stickerTabView.updateTabStyles();

        groupSize = structIGStickerGroups.size();
        stickerGridAdapter.setGroups(structIGStickerGroups);

        checkStickerEmptyView(groupSize);
    }

    private void checkStickerEmptyView(int groupSize) {
        if (groupSize == 0) {
            emptyIv = new AppCompatImageView(getContext());
            emptyIv.setImageDrawable(getResources().getDrawable(R.drawable.empty_chat));
            emptyIv.setOnClickListener(v -> listener.onAddStickerClicked());
            stickerContainer.addView(emptyIv, LayoutCreator.createFrame(120, 120, Gravity.CENTER, 0, 0, 0, 50));

            emptyTv = new AppCompatTextView(getContext());
            emptyTv.setTextColor(Theme.getInstance().getTitleTextColor(getContext()));
            emptyTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
            emptyTv.setTypeface(ResourcesCompat.getFont(getContext(), R.font.main_font));
            emptyTv.setLines(1);
            emptyTv.setTextColor(Theme.getInstance().getTitleTextColor(getContext()));
            emptyTv.setMaxLines(1);
            emptyTv.setSingleLine(true);
            emptyTv.setEllipsize(TextUtils.TruncateAt.END);
            emptyTv.setGravity(Gravity.CENTER);
            emptyTv.setText(getContext().getResources().getString(R.string.empty_sticker));
            emptyTv.setOnClickListener(v -> listener.onAddStickerClicked());
            stickerContainer.addView(emptyTv, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER, 0, 35, 0, 0));

            stickerTabView.setVisibility(GONE);
            addStickerIv.setVisibility(GONE);
        } else if (emptyIv != null && emptyTv != null) {
            stickerContainer.removeView(emptyIv);
            emptyIv = null;
            stickerContainer.removeView(emptyTv);
            emptyTv = null;

            stickerTabView.setVisibility(VISIBLE);
            addStickerIv.setVisibility(VISIBLE);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (stickerRepository != null)
            stickerRepository.unsubscribe();
    }

    public interface Listener {

        void onBackSpace();

        void onStickerClick(StructIGSticker structIGSticker);

        void onStickerSettingClick();

        void onAddStickerClicked();

        void onEmojiSelected(String unicode);
    }
}
