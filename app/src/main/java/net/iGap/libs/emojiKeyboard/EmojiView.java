package net.iGap.libs.emojiKeyboard;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import net.iGap.R;
import net.iGap.fragments.emoji.add.StickerAdapter;
import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.fragments.emoji.struct.StructIGStickerGroup;
import net.iGap.helper.LayoutCreator;
import net.iGap.libs.emojiKeyboard.View.CubicBezierInterpolator;
import net.iGap.libs.emojiKeyboard.View.ScrollTabView;
import net.iGap.libs.emojiKeyboard.adapter.EmojiAdapter;
import net.iGap.libs.emojiKeyboard.adapter.EmojiGridAdapter;
import net.iGap.libs.emojiKeyboard.adapter.ViewPagerAdapter;
import net.iGap.libs.emojiKeyboard.emoji.EmojiManager;
import net.iGap.libs.emojiKeyboard.sticker.StickerGroupAdapter;
import net.iGap.libs.emojiKeyboard.struct.StructIGEmojiGroup;
import net.iGap.messenger.theme.Theme;
import net.iGap.repository.StickerRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

@SuppressLint("ViewConstructor")
public class EmojiView extends FrameLayout implements ViewPager.OnPageChangeListener {

    public static final int EMOJI = 0;
    public static final int STICKER = 1;

    public boolean isShow;
    private boolean hasStickerPermission;
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
    private CompositeDisposable compositeDisposable;
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

        if (hasEmoji) {

            if (!EmojiManager.getInstance().isRecentEmojiLoaded()) {
                EmojiManager.getInstance().loadRecentEmoji();
            }

            emojiContainer = new FrameLayout(getContext());
            emojiContainer.setBackgroundColor(Theme.getColor(Theme.key_window_background));

            emojiGridAdapter = new EmojiGridAdapter();

            emojiGridView = new RecyclerView(getContext());
            emojiGridView.setBackgroundColor(Theme.getColor(Theme.key_window_background));
            emojiGridView.setAdapter(emojiGridAdapter);
            emojiGridView.setLayoutManager(emojiLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
            emojiGridView.setClipToPadding(false);
            emojiGridView.setPadding(0, LayoutCreator.dpToPx(40), 0, LayoutCreator.dpToPx(40));

            emojiGridView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    checkBottomTabScroll(dy);
                    checkEmojiTabY(recyclerView, dy);
                    checkScroll();
                    super.onScrolled(recyclerView, dx, dy);
                }
            });

            emojiTopView = new ScrollTabView(getContext());
            emojiTopView.setBackgroundColor(Theme.getColor(Theme.key_window_background));
            emojiTopView.setIndicatorHeight(LayoutCreator.dp(1.5f));
            emojiTopView.setIndicatorColor(Theme.getColor(Theme.key_theme_color));
            emojiTopView.setShouldExpand(true);
            emojiTopView.setListener(page -> {

                emojiGridView.stopScroll();
                emojiLayoutManager.scrollToPositionWithOffset(/*stickerGridAdapter.getPositionForGroup(stickerGridAdapter.getGroups().get(page))*/page, 0);
                checkScroll();
                emojiTopView.onPageScrolled(page, page);
                checkEmojiTabY(null, 0);
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

            StructIGEmojiGroup structIGEmojiGroup = new StructIGEmojiGroup();
            structIGEmojiGroup.setCategoryName(R.string.recently);
            structIGEmojiGroup.setStrings(EmojiManager.getInstance().getRecentEmoji());
            structIGEmojiGroups.add(0, structIGEmojiGroup);

            emojiGridAdapter.setStructIGEmojiGroups(structIGEmojiGroups);

            emojiGridAdapter.setListener(new EmojiAdapter.Listener() {
                @Override
                public void onClick(String emojiCode) {
                    showBottomTab(true);
                    EmojiManager.getInstance().addRecentEmoji(emojiCode);
                    EmojiManager.getInstance().saveRecentEmoji();

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

            emojiContainer.addView(emojiGridView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.TOP));
            emojiContainer.addView(emojiTopView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 35, Gravity.TOP));

            views.add(emojiContainer);
        }

        if (hasSticker) {

            stickerContainer = new FrameLayout(getContext());

            stickerRepository = StickerRepository.getInstance();
            compositeDisposable = new CompositeDisposable();

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

            stickerGridAdapter.setListener(new StickerAdapter.AddStickerDialogListener() {
                @Override
                public void onStickerClick(StructIGSticker structIGSticker) {
                    listener.onStickerClick(structIGSticker);
                }

                @Override
                public void onStickerLongClick(StructIGSticker structIGSticker) {
                    stickerRepository.addStickerToFavorite(structIGSticker.getId()).subscribe();
                }
            });

            stickerTabDrawable = new Drawable[]{
                    getResources().getDrawable(R.drawable.ic_sticker_history),
                    getResources().getDrawable(R.drawable.ic_add_sticker),
                    getResources().getDrawable(R.drawable.ic_favourite_sticker)
            };

            stickerTabView = new ScrollTabView(getContext());
            stickerTabView.setIndicatorHeight(LayoutCreator.dp(2.5f));
            stickerTabView.setIndicatorColor(Theme.getColor(Theme.key_theme_color));
            stickerTabView.setBackgroundColor(Theme.getColor(Theme.key_window_background));
            stickerTabView.setListener(page -> {

                stickerGridView.stopScroll();
                stickersLayoutManager.scrollToPositionWithOffset(/*stickerGridAdapter.getPositionForGroup(stickerGridAdapter.getGroups().get(page))*/page, 0);

                stickerTabView.onPageScrolled(page, page);
                checkScroll();
                checkStickersTabY(null, 0);
            });


            addStickerIv = new AppCompatImageView(getContext());
            addStickerIv.setImageDrawable(stickerTabDrawable[1]);
            addStickerIv.setBackgroundColor(Theme.getColor(Theme.key_window_background));
            addStickerIv.setScaleType(ImageView.ScaleType.CENTER);
            addStickerIv.setOnClickListener(v -> listener.onAddStickerClicked());
            addStickerIv.setColorFilter(Theme.getColor(Theme.key_title_text));

            stickerContainer.addView(stickerGridView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.CENTER));
            stickerContainer.addView(stickerTabView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 40, Gravity.TOP, 0, 0, 40, 0));
            stickerContainer.addView(addStickerIv, LayoutCreator.createFrame(40, 40, Gravity.TOP | Gravity.RIGHT));
            views.add(stickerContainer);

            Disposable disposable = stickerRepository.getStickerGroupWithRecentTabForEmojiView()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::onStickerChanged);

            compositeDisposable.add(disposable);
        }

        if (hasEmoji && hasSticker) {

            stickerIv = new AppCompatImageView(getContext());
            stickerIv.setImageResource(R.drawable.ic_sticker);
            stickerIv.setScaleType(ImageView.ScaleType.CENTER);
            stickerIv.setColorFilter(Theme.getColor(Theme.key_title_text));

            emojiIv = new AppCompatImageView(getContext());
            emojiIv.setImageResource(R.drawable.ic_emoji);
            emojiIv.setScaleType(ImageView.ScaleType.CENTER);
            emojiIv.setColorFilter(Theme.getColor(Theme.key_title_text));

            settingIv = new AppCompatImageView(getContext());
            settingIv.setScaleType(ImageView.ScaleType.CENTER);
            settingIv.setColorFilter(Theme.getColor(Theme.key_title_text));

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
            bottomContainer.setOnClickListener(v -> {
            });
            bottomContainer.setBackgroundColor(Theme.getColor(Theme.key_window_background));
            bottomContainer.addView(bottomViewShadow, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 1, Gravity.TOP));

            addView(bottomContainer, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 42, Gravity.BOTTOM));

            stickerIv.setOnClickListener(v -> setToSticker());
            emojiIv.setOnClickListener(v -> setToEmoji());

        } else if (hasEmoji) {

            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Theme.getColor(Theme.key_white));

            bottomContainer = new FrameLayout(getContext()) {
                @Override
                protected void dispatchDraw(Canvas canvas) {
                    canvas.drawArc(new RectF(0, 0, LayoutCreator.dp(45), LayoutCreator.dp(45)), 0f, 360f, true, paint);
                    super.dispatchDraw(canvas);
                }
            };

            settingIv = new AppCompatImageView(getContext());
            settingIv.setScaleType(ImageView.ScaleType.CENTER);
            settingIv.setImageResource(R.drawable.ic_backspace);
            settingIv.setColorFilter(Theme.getColor(Theme.key_title_text));

            bottomContainer.setOnClickListener(v -> listener.onBackSpace());

            bottomContainer.addView(settingIv, LayoutCreator.createFrame(30, 30, Gravity.CENTER));
            addView(bottomContainer, LayoutCreator.createFrame(45, 45, Gravity.BOTTOM | Gravity.RIGHT, 0, 0, 16, 10));

        }

        pagerAdapter = new ViewPagerAdapter(views);

        viewPager = new ViewPager(getContext());
        viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(this);

        addView(viewPager, 0, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT));
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setBackgroundColor(Theme.getColor(Theme.key_window_background));
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
        if (currentPage == EMOJI) {
            int firstVisibleItem = emojiLayoutManager.findFirstVisibleItemPosition();
            if (firstVisibleItem == RecyclerView.NO_POSITION) {
                return;
            }
            if (emojiGridView == null) {
                return;
            }
            int firstTab = 0;
            emojiTopView.onPageScrolled(firstVisibleItem, firstTab);
        } else if (currentPage == STICKER) {
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
        if (hasEmoji && hasSticker) {
            bottomTabContainerAnimation.playTogether(
                    ObjectAnimator.ofFloat(bottomContainer, View.TRANSLATION_Y, show ? 0 : LayoutCreator.dp(54)),
                    ObjectAnimator.ofFloat(bottomViewShadow, View.TRANSLATION_Y, show ? 0 : LayoutCreator.dp(49)));
        } else {
            bottomTabContainerAnimation.playTogether(ObjectAnimator.ofFloat(bottomContainer, View.TRANSLATION_Y, show ? 0 : LayoutCreator.dp(54)));
        }
        bottomTabContainerAnimation.setDuration(200);
        bottomTabContainerAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        bottomTabContainerAnimation.start();
    }

    private void viewPagerItemChanged(int position) {
        if (hasEmoji && hasSticker)
            if (position == EMOJI) {
                stickerIv.setColorFilter(Theme.getColor(Theme.key_subtitle_text), PorterDuff.Mode.SRC_IN);
                emojiIv.setColorFilter(Theme.getColor(Theme.key_title_text), PorterDuff.Mode.SRC_IN);
                settingIv.setImageResource(R.drawable.ic_backspace);
                checkEmojiTabY(null, 0);
            } else if (position == STICKER) {
                emojiIv.setColorFilter(Theme.getColor(Theme.key_subtitle_text), PorterDuff.Mode.SRC_IN);
                stickerIv.setColorFilter(Theme.getColor(Theme.key_title_text), PorterDuff.Mode.SRC_IN);
                settingIv.setImageResource(R.drawable.ic_settings);
                checkStickersTabY(null, 0);
            }
        currentPage = position;
    }

    public void onStickerChanged(List<StructIGStickerGroup> structIGStickerGroups) {

        if (structIGStickerGroups.size() != groupSize) {
            stickerTabView.removeTabs();

            boolean hasRecent = false;
            boolean hasFavorite = false;

            if (structIGStickerGroups.size() > 0 && structIGStickerGroups.get(0).getGroupId().equals(StructIGStickerGroup.RECENT_GROUP)) {
                stickerTabView.addIconTab(stickerTabDrawable[0]);
                hasRecent = true;
            }

            if (structIGStickerGroups.size() > 1 && structIGStickerGroups.get(hasRecent ? 1 : 0).getGroupId().equals(StructIGStickerGroup.FAVORITE_GROUP)) {
                stickerTabView.addIconTab(stickerTabDrawable[2]);
                hasFavorite = true;
            }

            for (int i = hasRecent ? hasFavorite ? 2 : 1 : hasFavorite ? 1 : 0; i < structIGStickerGroups.size(); i++) {
                stickerTabView.addStickerTab(structIGStickerGroups.get(i));
            }
        }
        stickerTabView.updateTabStyles();

        groupSize = structIGStickerGroups.size();
        stickerGridAdapter.setGroups(structIGStickerGroups);

        checkStickerEmptyView(groupSize);
    }

    private void checkStickerEmptyView(int groupSize) {
        if (groupSize == 0 && hasStickerPermission) {
            emptyIv = new AppCompatImageView(getContext());
            emptyIv.setImageResource(R.drawable.empty_chat);
            emptyIv.setOnClickListener(v -> listener.onAddStickerClicked());
            stickerContainer.addView(emptyIv, LayoutCreator.createFrame(120, 120, Gravity.CENTER, 0, 0, 0, 50));

            emptyTv = new AppCompatTextView(getContext());
            emptyTv.setTextColor(Theme.getColor(Theme.key_title_text));
            emptyTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
            emptyTv.setTypeface(ResourcesCompat.getFont(getContext(), R.font.main_font));
            emptyTv.setLines(1);
            emptyTv.setTextColor(Theme.getColor(Theme.key_title_text));
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

    private void checkStickerPermission(boolean canSendSticker) {
        hasStickerPermission = canSendSticker;
        if (!canSendSticker) {
            emptyTv = new AppCompatTextView(getContext());
            emptyTv.setTextColor(Theme.getColor(Theme.key_title_text));
            emptyTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            emptyTv.setTypeface(ResourcesCompat.getFont(getContext(), R.font.main_font));
            emptyTv.setLines(1);
            emptyTv.setTextColor(Theme.getColor(Theme.key_title_text));
            emptyTv.setMaxLines(1);
            emptyTv.setSingleLine(true);
            emptyTv.setEllipsize(TextUtils.TruncateAt.END);
            emptyTv.setGravity(Gravity.CENTER);
            emptyTv.setText(R.string.restrictions_on_sending_stickers);
            stickerContainer.addView(emptyTv, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER, 0, 0, 0, 42));

            stickerTabView.setVisibility(GONE);
            addStickerIv.setVisibility(GONE);
            stickerGridView.setVisibility(GONE);
        } else if (emptyTv != null) {
            stickerContainer.removeView(emptyTv);
            emptyTv = null;

            stickerTabView.setVisibility(VISIBLE);
            addStickerIv.setVisibility(VISIBLE);
            stickerGridView.setVisibility(VISIBLE);
        }
    }

    public void onDestroyParentFragment() {
        if (compositeDisposable != null && !compositeDisposable.isDisposed())
            compositeDisposable.dispose();
    }

    public void setStickerPermission(boolean stickerPermission) {
        if (hasSticker)
            checkStickerPermission(stickerPermission);
    }

    public interface Listener {

        void onBackSpace();

        void onStickerClick(StructIGSticker structIGSticker);

        void onStickerSettingClick();

        void onAddStickerClicked();

        void onEmojiSelected(String unicode);
    }
}
