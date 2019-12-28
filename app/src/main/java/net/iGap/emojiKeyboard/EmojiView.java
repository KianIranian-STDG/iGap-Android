package net.iGap.emojiKeyboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
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

import net.iGap.R;
import net.iGap.emojiKeyboard.adapter.EmojiAdapter;
import net.iGap.emojiKeyboard.adapter.StickerCategoryAdapter;
import net.iGap.emojiKeyboard.adapter.ViewPagerAdapter;
import net.iGap.emojiKeyboard.struct.StructStickerCategory;
import net.iGap.fragments.emoji.add.StickerAdapter;
import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.fragments.emoji.struct.StructIGStickerGroup;
import net.iGap.helper.LayoutCreator;
import net.iGap.realm.RealmStickers;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ViewConstructor")
public class EmojiView extends FrameLayout implements ViewPager.OnPageChangeListener {

    public static final int EMOJI = 0;
    public static final int STICKER = 1;

    public boolean isShow;
    public int currentPage;

    private ViewPager viewPager;
    private ViewPagerAdapter pagerAdapter;
    private List<View> views = new ArrayList<>();

    private View bottomViewShado;
    private FrameLayout bottomContainer;
    private ImageView emojiIv;
    private ImageView stickerIv;
    private ImageView settingIv;

    private FrameLayout emojiContainer;
    private RecyclerView emojiGridView;
    private EmojiAdapter emojiAdapter;
    private FrameLayout emojiCategoryContainer;
    private RecyclerView emojiCategoryRecyclerView;
    private EmojiAdapter emojiCategoryAdapter;
    private GridLayoutManager emojiLayoutManager;
    private LinearLayoutManager emojiCategoryLayoutManager;

    private FrameLayout stickerContainer;
    private RecyclerView stickerGridView;
    private StickerAdapter stickerAdapter;
    private FrameLayout stickerCategoryContainer;
    private RecyclerView stickerCategoryRecyclerView;
    private StickerCategoryAdapter stickerCategoryAdapter;
    private GridLayoutManager stickersLayoutManager;
    private LinearLayoutManager stickerCategoryLayoutManager;

    private int layoutHeight;
    private int layoutWidth;

    private Listener listener;


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

            emojiCategoryAdapter = new EmojiAdapter();

            emojiCategoryRecyclerView = new RecyclerView(getContext());
            emojiCategoryRecyclerView.setBackgroundColor(Color.parseColor("#E0E0E0"));
            emojiCategoryRecyclerView.setAdapter(emojiCategoryAdapter);
            emojiCategoryRecyclerView.setLayoutManager(emojiCategoryLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

            emojiGridView = new RecyclerView(getContext());
            emojiGridView.setAdapter(emojiAdapter);
            emojiGridView.setLayoutManager(emojiLayoutManager = new GridLayoutManager(context, 20));
            views.add(emojiContainer);

            emojiContainer.addView(emojiCategoryRecyclerView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 38, Gravity.TOP));
        }

        if (hasSticker) {
            stickerContainer = new FrameLayout(getContext());

            stickerAdapter = new StickerAdapter();

            stickerCategoryAdapter = new StickerCategoryAdapter();

            stickerCategoryContainer = new FrameLayout(getContext());

            stickerCategoryRecyclerView = new RecyclerView(getContext());
            stickerCategoryRecyclerView.setAdapter(stickerCategoryAdapter);
            stickerCategoryRecyclerView.setPadding(0, 0, LayoutCreator.dpToPx(8), 0);
            stickerCategoryRecyclerView.setClipToPadding(false);
            stickerCategoryRecyclerView.setBackgroundColor(Color.parseColor("#E0E0E0"));
            stickerCategoryRecyclerView.setLayoutManager(stickerCategoryLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

            stickerGridView = new RecyclerView(getContext());
            stickerGridView.setLayoutManager(stickersLayoutManager = new GridLayoutManager(context, 5));

            stickerContainer.addView(stickerCategoryRecyclerView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 38, Gravity.TOP));

            views.add(stickerContainer);

            createStickers();
        }

        stickerIv = new AppCompatImageView(getContext());
        stickerIv.setImageResource(R.drawable.ic_sticker);
        stickerIv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        emojiIv = new AppCompatImageView(getContext());
        emojiIv.setImageResource(R.drawable.ic_emoji);
        emojiIv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        settingIv = new AppCompatImageView(getContext());
        settingIv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        settingIv.setOnClickListener(v -> {
            if (currentPage == EMOJI)
                listener.onBackSpace();
            else if (currentPage == STICKER)
                listener.onStickerSettingClick();
        });

        bottomContainer = new FrameLayout(getContext());

        int emojiX = (layoutWidth / 2) - 19;
        int stickerX = (layoutWidth / 2) + 19;

        bottomContainer.addView(stickerIv, LayoutCreator.createFrame(38, 38, Gravity.CENTER, stickerX, 0, 0, 0));
        bottomContainer.addView(emojiIv, LayoutCreator.createFrame(38, 38, Gravity.CENTER, emojiX, 0, 0, 0));
        bottomContainer.addView(settingIv, LayoutCreator.createFrame(38, 38, Gravity.RIGHT));

        bottomContainer.setBackgroundColor(Color.parseColor("#E0E0E0"));
        addView(bottomContainer, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 38, Gravity.BOTTOM));

        pagerAdapter = new ViewPagerAdapter(views);

        viewPager = new ViewPager(getContext());
        viewPager.setAdapter(pagerAdapter);

        stickerIv.setOnClickListener(v -> setToSticker());
        emojiIv.setOnClickListener(v -> setToEmoji());

        viewPager.addOnPageChangeListener(this);

        addView(viewPager, 0, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT));
    }

    private void createStickers() {
        stickerCategoryAdapter.setCategories(getStickerCategory());
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setBackgroundColor(backgroundColor);
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY));
    }

    public void addStickerToRecentTab(List<StructIGSticker> structIGStickers) {

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
        Log.i("abbasiTag", "onLayout: " + layoutWidth + " H " + layoutHeight);
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

    private void viewPagerItemChanged(int position) {
        if (position == EMOJI) {
            stickerIv.setColorFilter(Color.parseColor("#BDBDBD"), PorterDuff.Mode.SRC_IN);
            emojiIv.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_IN);
            settingIv.setImageResource(R.drawable.ic_backspace);
        } else if (position == STICKER) {
            emojiIv.setColorFilter(Color.parseColor("#BDBDBD"), PorterDuff.Mode.SRC_IN);
            stickerIv.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_IN);
            settingIv.setImageResource(R.drawable.ic_settings);
        }
        currentPage = position;
    }

    private List<StructStickerCategory> getStickerCategory() {
        List<StructStickerCategory> categories = new ArrayList<>();

        StructStickerCategory recentCategory = new StructStickerCategory();
        recentCategory.setType(StructStickerCategory.DRAWABLE);
        recentCategory.setResId(R.drawable.ic_recent);
        categories.add(0, recentCategory);

        List<StructIGStickerGroup> groups = RealmStickers.getAllStickers();

        for (int i = 0; i < groups.size(); i++) {
            StructStickerCategory category = new StructStickerCategory();
            category.setResId(0);
            category.setStructIGStickerGroup(groups.get(i));
            category.setType(groups.get(i).getAvatarType());
            categories.add(category);
        }

        return categories;
    }

    public interface Listener {
        void onTabOpened(int type);

        void onBackSpace();

        void onStickerClick(StructIGSticker structIGSticker);

        void onStickerSettingClick();
    }
}
