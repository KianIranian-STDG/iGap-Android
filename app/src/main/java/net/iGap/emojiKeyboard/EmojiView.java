package net.iGap.emojiKeyboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
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
import net.iGap.emojiKeyboard.adapter.StickerAdapter;
import net.iGap.emojiKeyboard.adapter.ViewPagerAdapter;
import net.iGap.helper.LayoutCreator;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ViewConstructor")
public class EmojiView extends FrameLayout {

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
    private StickerAdapter stickerCategoryAdapter;
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
            emojiCategoryRecyclerView.setBackgroundColor(Color.parseColor("#B0BEC5"));
            emojiCategoryRecyclerView.setAdapter(emojiCategoryAdapter);
            emojiCategoryRecyclerView.setLayoutManager(emojiCategoryLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

            emojiGridView = new RecyclerView(getContext());
            emojiGridView.setAdapter(emojiAdapter);
            emojiGridView.setLayoutManager(emojiLayoutManager = new GridLayoutManager(context, 20));
            views.add(emojiContainer);


            emojiContainer.addView(emojiCategoryRecyclerView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 45, Gravity.TOP));
        }

        if (hasSticker) {
            stickerContainer = new FrameLayout(getContext());

            stickerAdapter = new StickerAdapter();

            stickerCategoryAdapter = new StickerAdapter();

            stickerCategoryContainer = new FrameLayout(getContext());

            stickerCategoryRecyclerView = new RecyclerView(getContext());
            stickerCategoryRecyclerView.setAdapter(stickerCategoryAdapter);
            stickerCategoryRecyclerView.setLayoutManager(stickerCategoryLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

            stickerGridView = new RecyclerView(getContext());
            stickerGridView.setLayoutManager(stickersLayoutManager = new GridLayoutManager(context, 5));

            views.add(stickerContainer);
        }

        stickerIv = new AppCompatImageView(getContext());
        stickerIv.setImageResource(R.drawable.ic_sticker);
        stickerIv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        emojiIv = new AppCompatImageView(getContext());
        emojiIv.setImageResource(R.drawable.ic_emoji);
        emojiIv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        settingIv = new AppCompatImageView(getContext());
        settingIv.setImageResource(R.drawable.ic_backspace);
        settingIv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);


        bottomContainer = new FrameLayout(getContext());

        int emojiX = (layoutWidth / 2) - 23;
        int stickerX = (layoutWidth / 2) + 23;

        bottomContainer.addView(stickerIv, LayoutCreator.createFrame(45, 45, Gravity.CENTER, stickerX, 0, 0, 0));
        bottomContainer.addView(emojiIv, LayoutCreator.createFrame(45, 45, Gravity.CENTER, emojiX, 0, 0, 0));
        bottomContainer.addView(settingIv, LayoutCreator.createFrame(45, 45, Gravity.RIGHT));


        bottomContainer.setBackgroundColor(Color.parseColor("#B0BEC5"));
        addView(bottomContainer, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 45, Gravity.BOTTOM));


        pagerAdapter = new ViewPagerAdapter(views);

        viewPager = new ViewPager(getContext());
        viewPager.setAdapter(pagerAdapter);

        addView(viewPager, 0, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT));
    }

    public void onOpen() {
        setVisibility(VISIBLE);
        listener.onTabOpened(currentPage == 0 ? EMOJI : STICKER);
    }


    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setBackgroundColor(backgroundColor);
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        layoutWidth = right - left;
        layoutHeight = bottom - top;
        Log.i("abbasiTag", "onLayout: " + layoutWidth + " H " + layoutHeight);
        super.onLayout(changed, left, top, right, bottom);
    }

    public void setContentView(int contentView) {
        viewPager.setCurrentItem(contentView);
    }

    public interface Listener {
        void onTabOpened(int type);
    }
}
