package net.iGap.emojiKeyboard;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import net.iGap.DbManager;
import net.iGap.R;
import net.iGap.Theme;
import net.iGap.emojiKeyboard.View.CubicBezierInterpolator;
import net.iGap.emojiKeyboard.View.ScrollTabView;
import net.iGap.emojiKeyboard.adapter.EmojiAdapter;
import net.iGap.emojiKeyboard.adapter.ViewPagerAdapter;
import net.iGap.emojiKeyboard.sticker.StickerGroupAdapter;
import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.fragments.emoji.struct.StructIGStickerGroup;
import net.iGap.helper.LayoutCreator;
import net.iGap.realm.RealmStickersDetails;
import net.iGap.repository.sticker.StickerRepository;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;

@SuppressLint("ViewConstructor")
public class EmojiView extends FrameLayout implements ViewPager.OnPageChangeListener, StickerRepository.Listener {

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
    private EmojiAdapter emojiAdapter;
    private RecyclerView emojiTopLinearView;
    private EmojiAdapter emojiTopAdapter;
    private GridLayoutManager emojiLayoutManager;
    private LinearLayoutManager emojiCategoryLayoutManager;

    private FrameLayout stickerContainer;
    private RecyclerView stickerGridView;
    private StickerGroupAdapter stickerGridAdapter;
    private LinearLayoutManager stickersLayoutManager;
    private AnimatorSet bottomTabContainerAnimation;
    private float lastBottomScrollY;
    private int stickerOffset;
    private AppCompatImageView addStickerIv;
    private ScrollTabView stickerTabView;
    private int stickersTabViewY;

    private int layoutHeight;
    private int layoutWidth;

    private Listener listener;
    private String TAG = "abbasiEmoji";

    private Drawable stickerTabDrawable[];

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private int backgroundColor = Color.parseColor("#FFFFFF");

    private boolean hasSticker;
    private boolean hasEmoji;

    public EmojiView(@NonNull Context context, boolean needSticker, boolean needEmoji) {
        super(context);
        hasEmoji = needEmoji;
        hasSticker = needSticker;

        if (hasEmoji) {
            emojiContainer = new FrameLayout(getContext());

            emojiAdapter = new EmojiAdapter();

            emojiTopAdapter = new EmojiAdapter();

            emojiTopLinearView = new RecyclerView(getContext());
            emojiTopLinearView.setBackgroundColor(Color.parseColor("#E0E0E0"));
            emojiTopLinearView.setAdapter(emojiTopAdapter);
            emojiTopLinearView.setLayoutManager(emojiCategoryLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

            emojiGridView = new RecyclerView(getContext());
            emojiGridView.setAdapter(emojiAdapter);
            emojiGridView.setLayoutManager(emojiLayoutManager = new GridLayoutManager(context, 20));
            views.add(emojiContainer);

            emojiContainer.addView(emojiTopLinearView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 48, Gravity.TOP));
        }

        if (hasSticker) {

            stickerContainer = new FrameLayout(getContext());

            stickerGridView = new RecyclerView(getContext());
            stickerGridView.setLayoutManager(stickersLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
            stickerGridView.setClipToPadding(false);
            stickerGridView.setPadding(0, LayoutCreator.dpToPx(48), 0, LayoutCreator.dpToPx(50));

            stickerGridAdapter = new StickerGroupAdapter();

            stickerGridView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    checkBottomTabScroll(dy);
                    checkStickersTabY(recyclerView, dy);
                    super.onScrolled(recyclerView, dx, dy);
                }
            });

            stickerGridView.setAdapter(stickerGridAdapter);

            stickerGridAdapter.setListener(structIGSticker -> listener.onStickerClick(structIGSticker));

            stickerTabDrawable = new Drawable[]{
                    getResources().getDrawable(R.drawable.ic_emoji_history),
                    getResources().getDrawable(R.drawable.ic_add_sticker)
            };

            stickerTabView = new ScrollTabView(getContext());
            stickerTabView.setIndicatorHeight(LayoutCreator.dp(2));
            stickerTabView.setIndicatorColor(Theme.getInstance().getAccentColor(getContext()));
            stickerTabView.setBackgroundColor(Color.parseColor("#E0E0E0"));
            stickerTabView.setListener(page -> {

                stickerGridView.stopScroll();
                stickersLayoutManager.scrollToPositionWithOffset(stickerGridAdapter.getPositionForGroup(stickerGridAdapter.getGroups().get(page)), 0);

                stickerTabView.onPageScrolled(page, page);

            });

            RealmResults<RealmStickersDetails> realmStickersDetails = DbManager.getInstance().doRealmTask(realm -> {
                return realm.where(RealmStickersDetails.class).sort("recentTime").findAll();
            });

            if (realmStickersDetails != null)
                for (int i = 0; i < realmStickersDetails.size(); i++) {
                    Log.i(TAG, "EmojiView: " + realmStickersDetails.get(i).getSt_id());
                }

            stickerTabView.addIconTab(stickerTabDrawable[0]);

            addStickerIv = new AppCompatImageView(getContext());
            addStickerIv.setImageDrawable(stickerTabDrawable[1]);
            addStickerIv.setBackgroundColor(Color.parseColor("#E0E0E0"));
            addStickerIv.setScaleType(ImageView.ScaleType.CENTER);
            addStickerIv.setOnClickListener(v -> listener.onAddStickerClicked());

            stickerContainer.addView(stickerGridView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.CENTER));
            stickerContainer.addView(stickerTabView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 48, Gravity.TOP, 0, 0, 48, 0));
            stickerContainer.addView(addStickerIv, LayoutCreator.createFrame(48, 48, Gravity.TOP | Gravity.RIGHT));
            views.add(stickerContainer);

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

        addView(bottomContainer, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 38, Gravity.BOTTOM));

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
        setBackgroundColor(backgroundColor);
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

    public void setContentView(int contentView) {
        viewPager.setCurrentItem(contentView);
        viewPagerItemChanged(contentView);
    }

    private void setToSticker() {
        if (hasSticker)
            viewPager.setCurrentItem(1, true);
    }

    private void setToEmoji() {
        if (hasSticker)
            viewPager.setCurrentItem(0, true);
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

    @Override
    public void onAddSticker(StructIGStickerGroup stickerGroup, int position) {

    }

    @Override
    public void onDeletedSticker(StructIGStickerGroup stickerGroup, int position) {

    }

    @Override
    public void onUpdatedSticker(StructIGStickerGroup stickerGroup, int position) {

    }

    @Override
    public void dataChange() {
        Log.i(TAG, "dataChange: ");
    }

    public interface Listener {

        void onBackSpace();

        void onStickerClick(StructIGSticker structIGSticker);

        void onStickerSettingClick();

        void onAddStickerClicked();
    }
}
