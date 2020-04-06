package net.iGap.fragments.news;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.material.snackbar.Snackbar;

import net.iGap.R;
import net.iGap.adapter.news.NewsFirstPageAdapter;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.NewsMainPageBinding;
import net.iGap.fragments.BottomNavigationFragment;
import net.iGap.fragments.discovery.DiscoveryFragment;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.HelperUrl;
import net.iGap.model.news.NewsFPList;
import net.iGap.model.news.NewsFirstPage;
import net.iGap.model.news.NewsMainBTN;
import net.iGap.module.SHP_SETTING;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.viewmodel.news.NewsMainVM;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

public class NewsMainFrag extends BaseAPIViewFrag<NewsMainVM> {

    private NewsMainPageBinding binding;

    private String specificNewsID = null;
    private String specificGroupID = null;

    public static NewsMainFrag newInstance() {
        return new NewsMainFrag();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(NewsMainVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.news_main_page, container, false);
//        binding.setViewmodel(newsMainVM);
        binding.setLifecycleOwner(this);

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        if (specificGroupID != null && !specificGroupID.equals("") && !specificGroupID.equals("showDetail"))
            openGroupNews(new NewsFPList(getResources().getString(R.string.news_mainTitle), specificGroupID, null));
        else if (specificNewsID != null && !specificNewsID.equals(""))
            openNewsDetail(specificNewsID);

        HelperToolbar mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLeftIcon(R.string.back_icon)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                })
                .setDefaultTitle(getResources().getString(R.string.news_mainTitle))
                .setLogoShown(true);

        LinearLayout toolbarLayout = binding.Toolbar;
        toolbarLayout.addView(mHelperToolbar.getView());

        binding.rcMain.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.rcMain.setLayoutManager(layoutManager);

        binding.pullToRefresh.setOnRefreshListener(() -> {
            viewModel.getNews();
            binding.noItemInListError.setVisibility(View.GONE);
        });

        viewModel.getNews();
        onErrorObserver();
        onDataChanged();
        onProgress();
    }

    private void onErrorObserver() {
        viewModel.getError().observe(getViewLifecycleOwner(), newsError -> {
            if (newsError.getState()) {
                //show the related text
                binding.noItemInListError.setVisibility(View.VISIBLE);
                // show error
                Snackbar snackbar = Snackbar.make(binding.Container, getString(newsError.getResID()), Snackbar.LENGTH_LONG);
                snackbar.setAction(getText(R.string.kuknos_Restore_Error_Snack), v -> snackbar.dismiss());
                snackbar.show();
            }
        });
    }

    private void onProgress() {
        viewModel.getProgressState().observe(getViewLifecycleOwner(), aBoolean -> binding.pullToRefresh.setRefreshing(aBoolean));
    }

    private void onDataChanged() {
        viewModel.getMainList().observe(getViewLifecycleOwner(), this::initMainRecycler);
    }

    private void initMainRecycler(List<NewsFirstPage> data) {
        List<NewsFirstPage> temp = new ArrayList<>(data);
        NewsFirstPageAdapter adapter = new NewsFirstPageAdapter(temp);
        adapter.setCallBack(new NewsFirstPageAdapter.onClickListener() {
            @Override
            public void onButtonClick(NewsMainBTN btn) {
                // open deepLink
                if (btn.getLink() == null || btn.getLink().equals(""))
                    return;
                if (btn.getLink().startsWith("igap")) {
                    BottomNavigationFragment navigationFragment = (BottomNavigationFragment) getFragmentManager().findFragmentByTag(BottomNavigationFragment.class.getName());
                    if (navigationFragment != null)
                        navigationFragment.autoLinkCrawler(btn.getLink().replace("igap://", ""), new DiscoveryFragment.CrawlerStruct.OnDeepValidLink() {
                            @Override
                            public void linkValid(String link) {
                                popBackStackFragment();
                            }

                            @Override
                            public void linkInvalid(String link) {
                                HelperError.showSnackMessage(link + " " + getResources().getString(R.string.link_not_valid), false);
                            }
                        });
                } else {
                    // open Link
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHP_SETTING.FILE_NAME, Context.MODE_PRIVATE);
                    int checkedInAppBrowser = sharedPreferences.getInt(SHP_SETTING.KEY_IN_APP_BROWSER, 1);
                    if (checkedInAppBrowser == 1 && !HelperUrl.isNeedOpenWithoutBrowser(btn.getLink())) {
                        HelperUrl.openBrowser(btn.getLink());
                    } else {
                        HelperUrl.openWithoutBrowser(btn.getLink());
                    }
                }
            }

            @Override
            public void onNewsCategoryClick(NewsFPList group) {
                openGroupNews(group);
            }

            @Override
            public void onSliderClick(NewsFPList.NewsContent slide) {
                openNewsDetail(slide.getId());
            }
        });
        binding.rcMain.setAdapter(adapter);
    }

    private void openGroupNews(NewsFPList group) {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(NewsGroupPagerFrag.class.getName());
        if (fragment == null) {
            fragment = NewsGroupPagerFrag.newInstance();
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
        }
        Bundle args = new Bundle();
        args.putString("GroupID", group.getCatID());
        args.putString("GroupTitle", group.getCategory());
        if (group.getNews() == null)
            args.putString("GroupPic", "");
        else
            args.putString("GroupPic", group.getNews().get(0).getContents().getImage().get(0).getOriginal());
        fragment.setArguments(args);
        new HelperFragment(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), fragment).setReplace(false).load();
    }

    private void openNewsDetail(String newsID) {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(NewsDetailFrag.class.getName());
        if (fragment == null) {
            fragment = NewsDetailFrag.newInstance();
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
        }
        Bundle args = new Bundle();
        args.putString("NewsID", newsID);
        fragment.setArguments(args);
        new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
    }

    public void setSpecificNewsID(String specificNewsID) {
        if (specificNewsID != null && specificNewsID.startsWith("CID:")) {
            this.specificGroupID = specificNewsID.replace("CID:", "");
        } else
            this.specificNewsID = specificNewsID;
    }

    public void setSpecificGroupID(String specificGroupID) {
        this.specificGroupID = specificGroupID;
    }
}
