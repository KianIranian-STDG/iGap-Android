package net.iGap.fragments.discovery;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.adapter.items.discovery.DiscoveryAdapter;
import net.iGap.adapter.items.discovery.DiscoveryItem;
import net.iGap.adapter.items.discovery.DiscoveryItemField;
import net.iGap.adapter.items.discovery.holder.BaseViewHolder;
import net.iGap.fragments.BaseMainFragments;
import net.iGap.fragments.BottomNavigationFragment;
import net.iGap.fragments.qrCodePayment.fragments.ScanCodeQRCodePaymentFragment;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperPreferences;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.HelperTracker;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.ui.toolBar.BackDrawable;
import net.iGap.messenger.ui.toolBar.Toolbar;
import net.iGap.messenger.ui.toolBar.ToolbarItem;
import net.iGap.model.PassCode;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.StatusBarUtil;
import net.iGap.module.Theme;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.request.RequestClientGetDiscovery;

import java.util.ArrayList;
import java.util.List;


public class DiscoveryFragment extends BaseMainFragments implements ToolbarListener {

    private RecyclerView rcDiscovery;
    private TextView emptyRecycle;
    private SwipeRefreshLayout pullToRefresh;
    private int page;
    private boolean isSwipeBackEnable = true;
    private HelperToolbar mHelperToolbar;
    private boolean needToCrawl = false;
    private boolean listLoaded = false;
    private boolean needToReload = false;
    private MaterialDialog materialDialog;
    private ArrayList<DiscoveryItem> discoveryArrayList;
    private final int codeScannerTag = 1;
    private final int passCodeTag = 2;
    private Toolbar discoveryToolbar;
    private ToolbarItem passCodeItem;
    private int scroll = 0;

    public static DiscoveryFragment newInstance(int page) {
        DiscoveryFragment discoveryFragment = new DiscoveryFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("page", page);
        if (page == 0) {
            discoveryFragment.isSwipeBackEnable = false;
        }
        discoveryFragment.setArguments(bundle);
        return discoveryFragment;
    }

    public void setNeedToCrawl(boolean needToCrawl) {
        this.needToCrawl = needToCrawl;
    }

    public void setNeedToReload(boolean needToRelaod) {
        this.needToReload = needToRelaod;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        G.updateResources(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discovery, container, false);
        if (isSwipeBackEnable) {
            return attachToSwipeBack(view);
        } else {
            return view;
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        rcDiscovery.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Ensure you call it only once
                rcDiscovery.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                // Here you can get the size :)
                if (rcDiscovery.getAdapter() instanceof DiscoveryAdapter) {
                    ((DiscoveryAdapter) rcDiscovery.getAdapter()).setWidth(rcDiscovery.getWidth());
                }
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        HelperTracker.sendTracker(HelperTracker.TRACKER_DISCOVERY_PAGE);
        page = getArguments().getInt("page");

        if (getContext() != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            StatusBarUtil.setColor(getActivity(), new Theme().getPrimaryDarkColor(getContext()), 50);
        }

        //uncomment this lines after added small avatar and discovery setting

        discoveryToolbar = new Toolbar(getContext());
        discoveryToolbar.setTitle(G.isAppRtl ? R.string.logo_igap_fa : R.string.logo_igap_en);
        if (page != 0) {
            discoveryToolbar.setBackIcon(new BackDrawable(false));

        } else {
            discoveryToolbar.addItem(codeScannerTag, R.string.icon_QR_code, Color.WHITE);
            passCodeItem = discoveryToolbar.addItem(passCodeTag, R.string.icon_lock, Color.WHITE);
        }
        checkPassCodeVisibility();
        discoveryToolbar.setListener(i -> {
            switch (i) {
                case -1:
                    popBackStackFragment();
                    break;
                case codeScannerTag:
                    onCodeScannerClickListener();
                    break;
                case passCodeTag:
                    if (passCodeItem == null) {
                        return;
                    }
                    if (ActivityMain.isLock) {
                        passCodeItem.setIcon(R.string.icon_unlock);
                        ActivityMain.isLock = false;
                        HelperPreferences.getInstance().putBoolean(SHP_SETTING.FILE_NAME, SHP_SETTING.KEY_LOCK_STARTUP_STATE, false);
                    } else {
                        passCodeItem.setIcon(R.string.icon_lock);
                        ActivityMain.isLock = true;
                        HelperPreferences.getInstance().putBoolean(SHP_SETTING.FILE_NAME, SHP_SETTING.KEY_LOCK_STARTUP_STATE, true);
                    }

                    checkPassCodeVisibility();
                    break;
            }
        });
        ViewGroup layoutToolbar = view.findViewById(R.id.fd_layout_toolbar);
        layoutToolbar.addView(discoveryToolbar, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.dp(56), Gravity.TOP));

        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        emptyRecycle = view.findViewById(R.id.emptyRecycle);
        rcDiscovery = view.findViewById(R.id.rcDiscovery);

        rcDiscovery.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Ensure you call it only once
                rcDiscovery.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                // Here you can get the size :)
                if (rcDiscovery.getAdapter() instanceof DiscoveryAdapter) {
                    ((DiscoveryAdapter) rcDiscovery.getAdapter()).setWidth(rcDiscovery.getWidth());
                }
            }
        });
/**detect scroll down or up for tapcell send request*/

        pullToRefresh.setOnRefreshListener(() -> {
            scroll = 1;
            setRefreshing(true);
            boolean isSend = updateOrFetchRecycleViewData();
            rcDiscovery.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    if (dy <= 0) {
                        return;
                    } else {
                        if (scroll == 1)
                            BottomNavigationFragment.isShowedAdd = false;
                    }
                    scroll++;
                }
            });
            if (!isSend) {
                setRefreshing(false);
                HelperError.showSnackMessage(getString(R.string.wallet_error_server), false);
            }
        });

        emptyRecycle.setOnClickListener(v -> {
            boolean isSend = updateOrFetchRecycleViewData();
            if (!isSend) {
                HelperError.showSnackMessage(getString(R.string.wallet_error_server), false);
            }
        });

        //load user avatar in toolbar
        //avatarHandler.getAvatar(new ParamWithAvatarType(mHelperToolbar.getAvatarSmall(), G.userId).avatarType(AvatarHandler.AvatarType.USER).showMain());

        rcDiscovery.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rcDiscovery.setAdapter(new DiscoveryAdapter(getActivity(), rcDiscovery.getWidth(), discoveryArrayList));
        if (discoveryArrayList == null) {
            tryToUpdateOrFetchRecycleViewData(0);
        }

        if (needToReload) {
            updateOrFetchRecycleViewData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkPassCodeVisibility();
    }

    private void setRefreshing(boolean value) {
        pullToRefresh.setRefreshing(value);
        if (value) {
            emptyRecycle.setVisibility(View.GONE);
        } else {
            if (rcDiscovery.getAdapter() != null && rcDiscovery.getAdapter().getItemCount() > 0) {
                emptyRecycle.setVisibility(View.GONE);
            } else {
                emptyRecycle.setVisibility(View.VISIBLE);
            }
            BottomNavigationFragment.isShowedAdd = false;
        }
    }

    private void onCodeScannerClickListener() {
        if (getActivity() != null) {
            new HelperFragment(getActivity().getSupportFragmentManager(), ScanCodeQRCodePaymentFragment.newInstance()).setReplace(false).load();
        }

//        DbManager.getInstance().doRealmTask(realm -> {
//            String phoneNumber = "";
//            RealmUserInfo userInfo = realm.where(RealmUserInfo.class).findFirst();
//            try {
//                if (userInfo != null) {
//                    phoneNumber = userInfo.getUserInfo().getPhoneNumber().substring(2);
//                } else {
//                    phoneNumber = AccountManager.getInstance().getCurrentUser().getPhoneNumber().substring(2);
//                }
//            } catch (Exception e) {
//                //maybe exception was for realm substring
//                try {
//                    phoneNumber = AccountManager.getInstance().getCurrentUser().getPhoneNumber().substring(2);
//                } catch (Exception ex) {
//                    //nothing
//                }
//            }
//
//            if (userInfo == null || !userInfo.isWalletRegister()) {
//                new HelperFragment(getActivity().getSupportFragmentManager(), FragmentWalletAgrement.newInstance(phoneNumber)).load();
//            } else {
//                getActivity().startActivityForResult(new HelperWallet().goToWallet(getContext(), new Intent(getActivity(), WalletActivity.class), "0" + phoneNumber, true), WALLET_REQUEST_CODE);
//            }
//
//        });
    }

    private void tryToUpdateOrFetchRecycleViewData(int count) {
        setRefreshing(true);
        boolean isSend = updateOrFetchRecycleViewData();

        if (!isSend && page == 0) {

            loadOfflinePageZero();

            if (count < 3) {
                G.handler.postDelayed(() -> tryToUpdateOrFetchRecycleViewData(count + 1), 1000);
            } else {
                setRefreshing(false);
            }
        } else if (!isSend) {
            setRefreshing(false);
        }
    }

    public void checkPassCodeVisibility() {
        if (PassCode.getInstance().isPassCode()) {
            if (passCodeItem == null) {
                passCodeItem = discoveryToolbar.addItem(passCodeTag, R.string.icon_unlock, Color.WHITE);
            }

            ActivityMain.isLock = HelperPreferences.getInstance().readBoolean(SHP_SETTING.FILE_NAME, SHP_SETTING.KEY_LOCK_STARTUP_STATE);
            if (ActivityMain.isLock) {
                passCodeItem.setIcon(R.string.icon_lock);
            } else {
                passCodeItem.setIcon(R.string.icon_unlock);
            }
        } else if (passCodeItem != null) {
            passCodeItem.setVisibility(View.GONE);
        }
    }

    private boolean updateOrFetchRecycleViewData() {
        return new RequestClientGetDiscovery().getDiscovery(page, new OnDiscoveryList() {
            @Override
            public void onDiscoveryListReady(ArrayList<DiscoveryItem> discoveryArrayList, String title) {
                if (page == 0) {
                    GsonBuilder builder = new GsonBuilder();
                    Gson gson = builder.create();
                    SharedPreferences pref = G.context.getSharedPreferences("DiscoveryPages", Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit = pref.edit();
                    String cache = gson.toJson(discoveryArrayList);
                    edit.putString("page0", cache).apply();
                    edit.putString("title", title).apply();

                    /*DiscoveryItem a = new DiscoveryItem(ProtoGlobal.Discovery.newBuilder().setScale("4:1").build());
                    a.model=null;
                discoveryArrayList.add(0,a);*/

                }

                listLoaded = true;

                G.handler.post(() -> {
                    setAdapterData(discoveryArrayList, title);
                    setRefreshing(false);

                    if (needToCrawl && getActivity() != null) {
                        discoveryCrawler(getActivity());
                    }
                });
            }

            @Override
            public void onError() {
                G.handler.post(() -> {
                    if (!listLoaded) {
                        updateOrFetchRecycleViewData();
                        listLoaded = true;
                        return;
                    }

                    if (page == 0) {
                        loadOfflinePageZero();
                    }

                    setRefreshing(false);
                });
            }
        });
    }

    public void discoveryCrawler(FragmentActivity activity) {
        BottomNavigationFragment bottomNavigationFragment = (BottomNavigationFragment) activity.
                getSupportFragmentManager().findFragmentByTag(BottomNavigationFragment.class.getName());

        try {
            if (bottomNavigationFragment != null) {
                for (int i = 0; i < discoveryArrayList.size(); i++) {
                    ArrayList<DiscoveryItemField> discoveryFields = discoveryArrayList.get(i).discoveryFields;
                    for (int j = 0; j < discoveryFields.size(); j++) {
                        if (discoveryFields.get(j).id == bottomNavigationFragment.getCrawlerStruct().getPages()
                                .get(bottomNavigationFragment.getCrawlerStruct().getCurrentPage())) {

                            if (bottomNavigationFragment.getCrawlerStruct().getPageSum() > 0) {

                                int currentPage = bottomNavigationFragment.getCrawlerStruct().getCurrentPage();
                                currentPage++;

                                bottomNavigationFragment.getCrawlerStruct().setCurrentPage(currentPage);
                                bottomNavigationFragment.getCrawlerStruct().setCurrentPageId(discoveryFields.get(j).id);
                                BaseViewHolder.handleDiscoveryFieldsClickStatic(discoveryFields.get(j), getActivity(), bottomNavigationFragment.getCrawlerStruct().getHaveNext());

                            } else
                                BaseViewHolder.handleDiscoveryFieldsClickStatic(discoveryFields.get(j), getActivity(), false);

                            needToCrawl = false;
                            return;
                        }
                    }
                }
            }
        } catch (Exception e) {
            HelperError.showSnackMessage(getResources().getString(R.string.link_not_valid), false);
        }
    }

    private void setAdapterData(ArrayList<DiscoveryItem> discoveryArrayList, String title) {
        this.discoveryArrayList = discoveryArrayList;
        if (rcDiscovery.getAdapter() instanceof DiscoveryAdapter) {
            ((DiscoveryAdapter) rcDiscovery.getAdapter()).setDiscoveryList(discoveryArrayList, rcDiscovery.getWidth());
            if (page != 0) discoveryToolbar.setTitle(title);
            rcDiscovery.getAdapter().notifyDataSetChanged();
        }
    }

    private void loadOfflinePageZero() {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        SharedPreferences pref = G.context.getSharedPreferences("DiscoveryPages", Context.MODE_PRIVATE);
        String json = pref.getString("page0", "");
        //String title = pref.getString("title", "");
        if (json != null && !json.equals("")) {
            try {
                ArrayList<DiscoveryItem> discoveryArrayList = gson.fromJson(json, new TypeToken<ArrayList<DiscoveryItem>>() {
                }.getType());
                for (DiscoveryItem discoveryItem : discoveryArrayList) {
                    if (discoveryItem.scale == null || discoveryItem.model == null) {
                        return;
                    }
                }
                setAdapterData(discoveryArrayList, "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onLeftIconClickListener(View view) {

    }

    @Override
    public void onSmallAvatarClickListener(View view) {

    }

    @Override
    public void onSearchClickListener(View view) {

    }

    @Override
    public boolean isAllowToBackPressed() {
        return true;
    }

    @Override
    public void scrollToTopOfList() {
        if (rcDiscovery != null) rcDiscovery.smoothScrollToPosition(0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        needToCrawl = false;
        needToReload = false;
        BottomNavigationFragment.isShowedAdd = false;
        if (materialDialog != null) {
            materialDialog.dismiss();
        }
    }

    public static class CrawlerStruct {
        private int currentPage;
        private int currentPageId;
        private boolean workDone = false;
        private boolean haveNext = false;
        private List<Integer> pages;

        public interface OnDeepValidLink {
            void linkValid(String link);

            void linkInvalid(String link);
        }

        public CrawlerStruct(int currentPage, List<Integer> pages) {
            this.currentPage = currentPage;
            this.pages = pages;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public int getPageSum() {
            return pages.size();
        }

        public List<Integer> getPages() {
            return pages;
        }

        public int getCurrentPageId() {
            return currentPageId;
        }

        public void setCurrentPageId(int currentPageId) {
            workDone = currentPageId == pages.get(pages.size() - 1);
            haveNext = currentPageId != pages.get(pages.size() - 1);
            this.currentPageId = currentPageId;
        }

        public boolean isWorkDone() {
            return workDone;
        }

        public boolean getHaveNext() {
            return haveNext;
        }
    }
}
