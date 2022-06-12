package net.iGap.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperDataUsage;
import net.iGap.helper.LayoutCreator;
import net.iGap.libs.emojiKeyboard.View.TopScrollView;
import net.iGap.libs.emojiKeyboard.adapter.ScrollTabPagerAdapter;
import net.iGap.messenger.theme.Theme;
import net.iGap.messenger.ui.cell.HeaderCell;
import net.iGap.messenger.ui.cell.ShadowSectionCell;
import net.iGap.messenger.ui.cell.TextSettingsCell;
import net.iGap.messenger.ui.toolBar.Toolbar;
import net.iGap.module.AndroidUtils;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.customView.RecyclerListView;
import net.iGap.module.structs.DataUsageStruct;
import net.iGap.realm.RealmDataUsage;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;

public class DataUsageFragment extends BaseFragment implements ViewPager.OnPageChangeListener {

    private final int pageCount = 2;
    private ViewPager viewPager;
    private TopScrollView topScrollView;
    private int currentTabIndex;

    @Override
    public View createView(Context context) {
        fragmentView = new LinearLayout(context);
        fragmentView.setBackgroundColor(Theme.getColor(Theme.key_window_background));
        LinearLayout layout = (LinearLayout) fragmentView;
        layout.setOrientation(LinearLayout.VERTICAL);
        List<View> viewList = new ArrayList<>();
        for (int i = 0; i < pageCount; i++) {
            final DataUsagePage page = new DataUsagePage(context, i);
            page.addView(page.listView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT));
            viewList.add(page);
        }
        ScrollTabPagerAdapter scrollTabPagerAdapter = new ScrollTabPagerAdapter(viewList);
        viewPager = new ViewPager(context);
        viewPager.setAdapter(scrollTabPagerAdapter);
        viewPager.addOnPageChangeListener(this);
        topScrollView = new TopScrollView(context);
        topScrollView.setBackgroundColor(Theme.getColor(Theme.key_toolbar_background));
        topScrollView.addTab(getResources().getString(R.string.wifi_data_usage));
        topScrollView.addTab(getResources().getString(R.string.mobile_data_usage));
        topScrollView.setPadding(0, 0, 100, 0);
        topScrollView.setListener(index -> {
            viewPager.setCurrentItem(sortIndexes(index));
            topScrollView.changeItemStyle(index);
        });
        layout.addView(topScrollView, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.TOP | Gravity.CENTER));
        layout.addView(viewPager, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT));
        if (G.isAppRtl) {
            viewPager.setCurrentItem(1);
            topScrollView.changeItemStyle(0);
        } else {
            topScrollView.changeItemStyle(0);
            viewPager.setCurrentItem(0);
        }
        return fragmentView;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        topScrollView.onScroll(sortIndexes(position));
        topScrollView.changeItemStyle(sortIndexes(position));
        currentTabIndex = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    private int sortIndexes(int position) {
        if (!G.isAppRtl)
            return position;

        switch (position) {
            case 0:
                return 1;
            case 1:
                return 0;
            default:
                return -1;
        }
    }

    @SuppressLint("ResourceType")
    @Override
    public View createToolBar(Context context) {
        toolbar = new Toolbar(context);
        toolbar.setTitle(context.getString(R.string.data_usage));
        toolbar.setBackIcon(R.drawable.ic_ab_back);
        toolbar.setListener(id -> {
            if (id == -1) {
                finish();
            }
        });
        return toolbar;
    }

    private interface setOnLoadListener {
        void onLoadData();
    }

    public class DataUsagePage extends FrameLayout {

        private final RecyclerListView listView;
        private final Context context;
        private final ListAdapter listAdapter;
        private final setOnLoadListener setOnLoadListener;
        private final int sectionOneHeaderRow = 0;
        private final int imageReceivedRow = 1;
        private final int imageSendRow = 2;
        private final int imageReceiveSizeRow = 3;
        private final int imageSendSizeRow = 4;
        private final int sectionOneDividerRow = 5;
        private final int sectionTwoHeaderRow = 6;
        private final int videoReceivedRow = 7;
        private final int videoSendRow = 8;
        private final int videoReceiveSizeRow = 9;
        private final int videoSendSizeRow = 10;
        private final int sectionTwoDividerRow = 11;
        private final int sectionThreeHeaderRow = 12;
        private final int audioReceivedRow = 13;
        private final int audioSendRow = 14;
        private final int audioReceiveSizeRow = 15;
        private final int audioSendSizeRow = 16;
        private final int sectionThreeDividerRow = 17;
        private final int sectionFourHeaderRow = 18;
        private final int fileReceivedRow = 19;
        private final int fileSendRow = 20;
        private final int fileReceiveSizeRow = 21;
        private final int fileSendSizeRow = 22;
        private final int sectionFourDividerRow = 23;
        private final int sectionFiveHeaderRow = 24;
        private final int otherReceivedRow = 25;
        private final int otherSendRow = 26;
        private final int otherReceiveSizeRow = 27;
        private final int otherSendSizeRow = 28;
        private final int sectionFiveDividerRow = 29;
        private final int sectionSixHeaderRow = 30;
        private final int totalReceiveSizeRow = 31;
        private final int totalSendSizeRow = 32;
        private final int sectionSixDividerRow = 33;
        private final int clearDataUsageHistory = 34;
        private final int sectionSevenDividerRow = 35;
        private final int rowCount = 36;
        private ArrayList<DataUsageStruct> dataUsageStructList;
        private long totalReceivedByte;
        private long totalSendByte;
        private boolean loading = true;

        public DataUsagePage(@NonNull Context context, int type) {
            super(context);
            this.context = context;
            listView = new RecyclerListView(context);
            listView.setItemAnimator(null);
            listView.setLayoutAnimation(null);
            listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false) {
                @Override
                public boolean supportsPredictiveItemAnimations() {
                    return false;
                }
            });
            listView.setVerticalScrollBarEnabled(true);
            listView.setPadding(0, 0, 0, LayoutCreator.dp(25));
            listView.setClipToPadding(false);
            listView.setBackgroundColor(Theme.getColor(Theme.key_window_background));
            listView.setOnItemClickListener((view, position) -> {
                if (position == clearDataUsageHistory) {
                    showClearDataUsageHistoryDialog(type);
                }
            });
            listView.setAdapter(listAdapter = new ListAdapter());
            setOnLoadListener = () -> {
                loading = false;
                listAdapter.notifyDataSetChanged();
            };
            getRealmDataUsage(type);
        }

        private void getRealmDataUsage(int type) {
            DbManager.getInstance().doRealmTask(realm -> {
                totalReceivedByte = 0;
                totalSendByte = 0;
                RealmResults<RealmDataUsage> wifiRealmDataUsages;
                RealmResults<RealmDataUsage> dataRealmDataUsages;
                dataUsageStructList = new ArrayList<>();
                if (type == 0) {
                    wifiRealmDataUsages = realm.where(RealmDataUsage.class).equalTo("connectivityType", true).findAll();
                    if (wifiRealmDataUsages.size() == 0)
                        wifiRealmDataUsages = realm.where(RealmDataUsage.class).findAll();
                    for (RealmDataUsage usage : wifiRealmDataUsages) {
                        dataUsageStructList.add(new DataUsageStruct(0, usage.getDownloadSize(), usage.getUploadSize(), usage.getNumUploadedFiles(), usage.getNumDownloadedFile(), usage.getType()));
                        totalReceivedByte += usage.getDownloadSize();
                        totalSendByte += usage.getUploadSize();
                    }
                } else {
                    dataRealmDataUsages = realm.where(RealmDataUsage.class).equalTo("connectivityType", false).findAll();
                    if (dataRealmDataUsages.size() == 0)
                        dataRealmDataUsages = realm.where(RealmDataUsage.class).findAll();
                    for (RealmDataUsage usage : dataRealmDataUsages) {
                        dataUsageStructList.add(new DataUsageStruct(0, usage.getDownloadSize(), usage.getUploadSize(), usage.getNumUploadedFiles(), usage.getNumDownloadedFile(), usage.getType()));
                        totalReceivedByte += usage.getDownloadSize();
                        totalSendByte += usage.getUploadSize();
                    }
                }
                setOnLoadListener.onLoadData();
            });
        }

        private void showClearDataUsageHistoryDialog(int type) {
            new MaterialDialog.Builder(context).title(R.string.clearDataUsage)
                    .positiveText(R.string.yes)
                    .onPositive((dialog, which) -> {
                        HelperDataUsage.clearUsageRealm(type == 0);
                        getRealmDataUsage(currentTabIndex);
                        dialog.dismiss();
                    }).negativeText(R.string.no).show();
        }

        private class ListAdapter extends RecyclerListView.SelectionAdapter {

            @Override
            public boolean isEnabled(RecyclerView.ViewHolder holder) {
                int type = holder.getItemViewType();
                return type != 0 && type != 2;
            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = null;
                switch (viewType) {
                    case 0:
                        view = new HeaderCell(context);
                        view.setBackgroundColor(Theme.getColor(Theme.key_window_background));
                        break;
                    case 1:
                        view = new TextSettingsCell(context);
                        view.setBackgroundColor(Theme.getColor(Theme.key_window_background));
                        break;
                    case 2:
                        view = new ShadowSectionCell(context, 12, Theme.getColor(Theme.key_line));
                        break;
                }
                return new RecyclerListView.Holder(view);
            }

            @SuppressLint("DefaultLocale")
            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                int viewType = holder.getItemViewType();
                switch (viewType) {
                    case 0:
                        HeaderCell headerCell = (HeaderCell) holder.itemView;
                        if (position == sectionOneHeaderRow) {
                            headerCell.setText(getString(R.string.image_message));
                        } else if (position == sectionTwoHeaderRow) {
                            headerCell.setText(getString(R.string.video_message));
                        } else if (position == sectionThreeHeaderRow) {
                            headerCell.setText(getString(R.string.audio_message));
                        } else if (position == sectionFourHeaderRow) {
                            headerCell.setText(getString(R.string.file_message));
                        } else if (position == sectionFiveHeaderRow) {
                            headerCell.setText(getString(R.string.st_Other));
                        } else if (position == sectionSixHeaderRow) {
                            headerCell.setText(getString(R.string.total));
                        }
                        break;
                    case 1:
                        TextSettingsCell textSettingsCell = (TextSettingsCell) holder.itemView;
                        textSettingsCell.setTextColor(Theme.getColor(Theme.key_default_text));
                        if (position == imageReceivedRow) {
                            textSettingsCell.setTextAndValue(context.getString(R.string.received), AndroidUtils.humanReadableByteCount(dataUsageStructList.get(0).getByteReceived(), true), true);
                        } else if (position == imageSendRow) {
                            textSettingsCell.setTextAndValue(context.getString(R.string.sent), AndroidUtils.humanReadableByteCount(dataUsageStructList.get(0).getByteSend(), true), true);
                        } else if (position == imageReceiveSizeRow) {
                            textSettingsCell.setTextAndValue(context.getString(R.string.bytes_received), AndroidUtils.humanReadableByteCount(dataUsageStructList.get(0).getByteReceived(), true), true);
                        } else if (position == imageSendSizeRow) {
                            textSettingsCell.setTextAndValue(context.getString(R.string.bytes_sent), AndroidUtils.humanReadableByteCount(dataUsageStructList.get(0).getByteSend(), true), true);
                        } else if (position == videoReceivedRow) {
                            textSettingsCell.setTextAndValue(context.getString(R.string.received), AndroidUtils.humanReadableByteCount(dataUsageStructList.get(1).getByteReceived(), true), true);
                        } else if (position == videoSendRow) {
                            textSettingsCell.setTextAndValue(context.getString(R.string.sent), AndroidUtils.humanReadableByteCount(dataUsageStructList.get(1).getByteSend(), true), true);
                        } else if (position == videoReceiveSizeRow) {
                            textSettingsCell.setTextAndValue(context.getString(R.string.bytes_received), AndroidUtils.humanReadableByteCount(dataUsageStructList.get(1).getByteReceived(), true), true);
                        } else if (position == videoSendSizeRow) {
                            textSettingsCell.setTextAndValue(context.getString(R.string.bytes_sent), AndroidUtils.humanReadableByteCount(dataUsageStructList.get(1).getByteSend(), true), true);
                        } else if (position == audioReceivedRow) {
                            textSettingsCell.setTextAndValue(context.getString(R.string.received), AndroidUtils.humanReadableByteCount(dataUsageStructList.get(2).getByteReceived(), true), true);
                        } else if (position == audioSendRow) {
                            textSettingsCell.setTextAndValue(context.getString(R.string.sent), AndroidUtils.humanReadableByteCount(dataUsageStructList.get(2).getByteSend(), true), true);
                        } else if (position == audioReceiveSizeRow) {
                            textSettingsCell.setTextAndValue(context.getString(R.string.bytes_received), AndroidUtils.humanReadableByteCount(dataUsageStructList.get(2).getByteReceived(), true), true);
                        } else if (position == audioSendSizeRow) {
                            textSettingsCell.setTextAndValue(context.getString(R.string.bytes_sent), AndroidUtils.humanReadableByteCount(dataUsageStructList.get(2).getByteSend(), true), true);
                        } else if (position == fileReceivedRow) {
                            textSettingsCell.setTextAndValue(context.getString(R.string.received), AndroidUtils.humanReadableByteCount(dataUsageStructList.get(3).getByteReceived(), true), true);
                        } else if (position == fileSendRow) {
                            textSettingsCell.setTextAndValue(context.getString(R.string.sent), AndroidUtils.humanReadableByteCount(dataUsageStructList.get(3).getByteSend(), true), true);
                        } else if (position == fileReceiveSizeRow) {
                            textSettingsCell.setTextAndValue(context.getString(R.string.bytes_received), AndroidUtils.humanReadableByteCount(dataUsageStructList.get(3).getByteReceived(), true), true);
                        } else if (position == fileSendSizeRow) {
                            textSettingsCell.setTextAndValue(context.getString(R.string.bytes_sent), AndroidUtils.humanReadableByteCount(dataUsageStructList.get(3).getByteSend(), true), true);
                        } else if (position == otherReceivedRow) {
                            textSettingsCell.setTextAndValue(context.getString(R.string.received), AndroidUtils.humanReadableByteCount(dataUsageStructList.get(4).getByteReceived(), true), true);
                        } else if (position == otherSendRow) {
                            textSettingsCell.setTextAndValue(context.getString(R.string.sent), AndroidUtils.humanReadableByteCount(dataUsageStructList.get(4).getByteSend(), true), true);
                        } else if (position == otherReceiveSizeRow) {
                            textSettingsCell.setTextAndValue(context.getString(R.string.bytes_received), AndroidUtils.humanReadableByteCount(dataUsageStructList.get(4).getByteReceived(), true), true);
                        } else if (position == otherSendSizeRow) {
                            textSettingsCell.setTextAndValue(context.getString(R.string.bytes_sent), AndroidUtils.humanReadableByteCount(dataUsageStructList.get(4).getByteSend(), true), true);
                        } else if (position == totalReceiveSizeRow) {
                            textSettingsCell.setTextAndValue(context.getString(R.string.total_received), AndroidUtils.humanReadableByteCount(totalReceivedByte, true), true);
                        } else if (position == totalSendSizeRow) {
                            textSettingsCell.setTextAndValue(context.getString(R.string.total_sent), AndroidUtils.humanReadableByteCount(totalSendByte, true), true);
                        } else if (position == clearDataUsageHistory) {
                            textSettingsCell.setTextAndValue(context.getString(R.string.clear_data_usage), "", true);
                            textSettingsCell.setTextColor(Theme.getColor(Theme.key_dark_red));
                        }
                        break;
                }
            }

            @Override
            public int getItemViewType(int position) {
                if (position == sectionOneHeaderRow || position == sectionTwoHeaderRow || position == sectionThreeHeaderRow || position == sectionFourHeaderRow ||
                        position == sectionFiveHeaderRow || position == sectionSixHeaderRow) {
                    return 0;
                } else if (position == sectionOneDividerRow || position == sectionTwoDividerRow || position == sectionThreeDividerRow || position == sectionFourDividerRow ||
                        position == sectionFiveDividerRow || position == sectionSixDividerRow || position == sectionSevenDividerRow) {
                    return 2;
                } else {
                    return 1;
                }
            }

            @Override
            public int getItemCount() {
                return loading ? 0 : rowCount;
            }
        }
    }
}
