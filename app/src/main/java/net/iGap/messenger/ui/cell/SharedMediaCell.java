package net.iGap.messenger.ui.cell;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.libs.emojiKeyboard.View.TopScrollView;
import net.iGap.libs.emojiKeyboard.adapter.ScrollTabPagerAdapter;
import net.iGap.messenger.theme.Theme;
import net.iGap.messenger.theme.ThemeDescriptor;
import net.iGap.messenger.ui.components.ExtendedGridLayoutManager;
import net.iGap.messenger.ui.components.RecyclerAnimationScrollHelper;
import net.iGap.messenger.ui.components.Size;
import net.iGap.module.customView.RecyclerListView;

import java.util.ArrayList;
import java.util.List;

public class SharedMediaCell extends FrameLayout implements ViewPager.OnPageChangeListener {

    private final String[] tabs = {"MEDIA", "FILES", "LINKS", "MUSIC", "VOICE", "GIF"};
    private final ViewPager viewPager;
    private final TopScrollView topScrollView;
    private final SharedMediaPage[] mediaPages = new SharedMediaPage[tabs.length];
    private final ScrollTabPagerAdapter tabPagerAdapter;
    public int listDataSize = 50;
    private RecyclerListView currentListView;

    public SharedMediaCell(@NonNull Context context) {
        super(context);

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        setWillNotDraw(false);
        List<View> viewList = new ArrayList<>();

        for (int a = 0; a < mediaPages.length; a++) {

            final SharedMediaPage mediaPage = new SharedMediaPage(context) {
                @Override
                public void setTranslationX(float translationX) {
                    super.setTranslationX(translationX);
                }
            };

            mediaPages[a] = mediaPage;

            final ExtendedGridLayoutManager layoutManager = mediaPages[a].layoutManager = new ExtendedGridLayoutManager(context, listDataSize) {
                private final Size size = new Size();

                @Override
                public boolean supportsPredictiveItemAnimations() {
                    return false;
                }

                @Override
                protected Size getSizeForItem(int i) {
                    return size;
                }

                @Override
                protected int getFlowItemCount() {
                    return getItemCount();
                }

                @Override
                public void onInitializeAccessibilityNodeInfoForItem(RecyclerView.Recycler recycler, RecyclerView.State state, View host, AccessibilityNodeInfoCompat info) {
                    super.onInitializeAccessibilityNodeInfoForItem(recycler, state, host, info);
                    final AccessibilityNodeInfoCompat.CollectionItemInfoCompat itemInfo = info.getCollectionItemInfo();
                    if (itemInfo != null && itemInfo.isHeading()) {
                        info.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(itemInfo.getRowIndex(), itemInfo.getRowSpan(), itemInfo.getColumnIndex(), itemInfo.getColumnSpan(), false));
                    }
                }
            };

            layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return mediaPage.layoutManager.getSpanSizeForItem(position);
                }
            });

            mediaPages[a].listView = new RecyclerListView(context);
            mediaPages[a].listView.setScrollingTouchSlop(RecyclerView.TOUCH_SLOP_PAGING);
            mediaPages[a].listView.setPinnedSectionOffsetY(-LayoutCreator.dp(2));
            mediaPages[a].listView.setPadding(LayoutCreator.dp(2), LayoutCreator.dp(2), LayoutCreator.dp(2), LayoutCreator.dp(2));
            mediaPages[a].listView.setItemAnimator(null);
            mediaPages[a].listView.setClipToPadding(false);
            mediaPages[a].listView.setSectionsType(2);
            if (a == 0)
                setCurrentListView(mediaPages[0].listView);
            if (tabs[a].equals("MEDIA") || tabs[a].equals("GIF")) {
                mediaPages[a].listView.setLayoutManager(layoutManager);
            } else {
                mediaPages[a].listView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
            }
            mediaPages[a].listView.setAdapter(mediaPages[a].adapter = new SharedMediaAdapter(context, tabs[a], listDataSize));
            mediaPages[a].addView(mediaPages[a].listView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT));
            mediaPages[a].listView.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(android.graphics.Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    outRect.left = 0;
                    outRect.top = 0;
                    outRect.bottom = 0;
                    outRect.right = 0;
                }
            });
            mediaPages[a].listView.setOnItemClickListener((view, position) -> {
            });
            mediaPages[a].listView.setOnItemLongClickListener((view, position) -> {
                mediaPage.listView.getOnItemClickListener().onItemClick(view, position);
                return true;
            });
            mediaPages[a].scrollHelper = new RecyclerAnimationScrollHelper(mediaPages[a].listView, mediaPages[a].layoutManager);
            final RecyclerListView listView = mediaPages[a].listView;
            viewList.add(mediaPages[a]);
        }

        tabPagerAdapter = new ScrollTabPagerAdapter(viewList);
        viewPager = new ViewPager(context);
        viewPager.setAdapter(tabPagerAdapter);
        viewPager.addOnPageChangeListener(this);

        topScrollView = new TopScrollView(context);
        topScrollView.setBackgroundColor(net.iGap.messenger.theme.Theme.getColor(net.iGap.messenger.theme.Theme.key_toolbar_background));
        topScrollView.addTab("مدیا");
        topScrollView.addTab("فایل");
        topScrollView.addTab("لینک");
        topScrollView.addTab("موسیفی");
        topScrollView.addTab("صدا");
        topScrollView.addTab("گیف");
        topScrollView.setListener(index -> {
            setCurrentListView(mediaPages[index].listView);
            viewPager.setCurrentItem(sortIndexes(index));
            topScrollView.changeItemStyle(index);
        });

        linearLayout.addView(topScrollView, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.TOP));
        linearLayout.addView(viewPager, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT));
        if (G.isAppRtl) {
            viewPager.setCurrentItem(tabs.length - 1);
            topScrollView.changeItemStyle(0);
        } else {
            topScrollView.changeItemStyle(0);
            viewPager.setCurrentItem(0);
        }

        addView(linearLayout, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.TOP, 5, 5, 5, 5));
    }

    private int sortIndexes(int position) {
        if (!G.isAppRtl)
            return position;
        else
            return tabs.length - (position + 1);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        topScrollView.onScroll(sortIndexes(position));
        topScrollView.changeItemStyle(sortIndexes(position));
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    /*public ArrayList<ThemeDescriptor> getThemeDescriptions() {
        ArrayList<ThemeDescriptor> result = new ArrayList<>();
        if (mediaPages != null) {
            for (SharedMediaPage page : mediaPages) {
                if (page != null) {
                    result.add(new ThemeDescriptor(page.listView, ThemeDescriptor.FLAG_CELLBACKGROUNDCOLOR, Theme.key_window_background_white));
                    result.add(new ThemeDescriptor(page.listView, ThemeDescriptor.FLAG_BACKGROUND, Theme.key_window_background_grey));
                    result.add(new ThemeDescriptor(page.listView, ThemeDescriptor.FLAG_TEXTCOLOR, Theme.key_menu_item));
                    result.add(new ThemeDescriptor(page.listView, ThemeDescriptor.FLAG_IMAGECOLOR, Theme.key_menu_item));
                }
            }
        }
        return result;
    }*/

    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        for (int a = 0; a < mediaPages.length; a++) {
            if (mediaPages[a].listView != null) {
                final int num = a;
                ViewTreeObserver obs = mediaPages[a].listView.getViewTreeObserver();
                obs.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        mediaPages[num].getViewTreeObserver().removeOnPreDrawListener(this);
                        return true;
                    }
                });
            }
        }
    }

    public RecyclerListView getCurrentListView() {
        return this.currentListView;
    }

    public void setCurrentListView(RecyclerListView currentListView) {
        this.currentListView = currentListView;
    }

    public static class SharedMediaPage extends FrameLayout {
        public RecyclerListView listView;
        public SharedMediaAdapter adapter;
        public ExtendedGridLayoutManager layoutManager;
        public RecyclerAnimationScrollHelper scrollHelper;
        public String type;

        public SharedMediaPage(@NonNull Context context) {
            super(context);
        }
    }

    private static class SharedMediaAdapter extends RecyclerListView.SelectionAdapter {
        private final String type;
        private final Context context;
        private final int listDataSize;

        public SharedMediaAdapter(Context context, String type, int listDataSize) {
            this.context = context;
            this.type = type;
            this.listDataSize = listDataSize;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View cellView = null;
            switch (type) {
                case "MEDIA":
                case "GIF":
                    ImageView imageView = new ImageView(context);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.drawable_rounded_corners_stroke));
                    imageView.setPadding(2, 2, 2, 2);
                    imageView.setAdjustViewBounds(true);
                    cellView = imageView;
                    break;
                case "FILES":
                    FileCell fileCell = new FileCell(context);
                    cellView = fileCell;
                    break;
                case "MUSIC":
                    MusicCell musicCell = new MusicCell(context);
                    cellView = musicCell;
                    break;
                case "LINKS":
                    LinkCell linkCell = new LinkCell(context);
                    cellView = linkCell;
                    break;
                case "VOICE":
                    VoiceCell voiceCell = new VoiceCell(context);
                    cellView = voiceCell;
                    break;
            }
            return new RecyclerListView.Holder(cellView);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getItemCount() {
            return listDataSize;
        }
    }
}
