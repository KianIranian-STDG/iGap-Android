package net.iGap.news.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;

import net.iGap.R;
import net.iGap.databinding.NewsDetailPageBinding;
import net.iGap.databinding.NewsMainPageBinding;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.libs.bottomNavigation.Util.Utils;
import net.iGap.news.repository.model.NewsFPList;
import net.iGap.news.repository.model.NewsFirstPage;
import net.iGap.news.repository.model.NewsMainBTN;
import net.iGap.news.repository.model.NewsSlider;
import net.iGap.news.view.Adapter.NewsFirstPageAdapter;
import net.iGap.news.viewmodel.NewsMainVM;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NewsDetailFrag extends BaseFragment {

    private NewsDetailPageBinding binding;
    private NewsMainVM newsMainVM;

    public static NewsDetailFrag newInstance() {
        return new NewsDetailFrag();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        newsMainVM = ViewModelProviders.of(this).get(NewsMainVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.news_detail_page, container, false);
//        binding.setViewmodel(newsMainVM);
        binding.setLifecycleOwner(this);

        return attachToSwipeBack(binding.getRoot());
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        HelperToolbar mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLeftIcon(R.string.back_icon)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                })
                .setLogoShown(true);

        LinearLayout toolbarLayout = binding.Toolbar;
        Utils.darkModeHandler(toolbarLayout);
        toolbarLayout.addView(mHelperToolbar.getView());

        /*binding.rcMain.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.rcMain.setLayoutManager(layoutManager);

        binding.pullToRefresh.setOnRefreshListener(() -> {
            newsMainVM.getData();
            binding.noItemInListError.setVisibility(View.GONE);
        });

        newsMainVM.getData();
        onErrorObserver();
        onDataChanged();
        onProgress();*/
    }

    private void onErrorObserver() {
        newsMainVM.getError().observe(getViewLifecycleOwner(), newsError -> {
            if (newsError.getState()) {
                //show the related text
//                binding.noItemInListError.setVisibility(View.VISIBLE);
                // show error
                Snackbar snackbar = Snackbar.make(binding.Container, getString(newsError.getResID()), Snackbar.LENGTH_LONG);
                snackbar.setAction(getText(R.string.kuknos_Restore_Error_Snack), v -> snackbar.dismiss());
                snackbar.show();
            }
        });
    }

    private void onProgress() {
//        newsMainVM.getProgressState().observe(getViewLifecycleOwner(), aBoolean -> binding.pullToRefresh.setRefreshing(aBoolean));
    }

    private void onDataChanged() {
        newsMainVM.getMainList().observe(getViewLifecycleOwner(), this::initMainRecycler);
    }

    private void initMainRecycler(List<NewsFirstPage> data) {
        List<NewsFirstPage> temp = new ArrayList<>(data);
        /*Gson gson = new Gson();
        Log.d("amini", "onDataChanged: in here " + gson.toJson(temp));*/
        NewsFirstPageAdapter adapter = new NewsFirstPageAdapter(temp);
        adapter.setCallBack(new NewsFirstPageAdapter.onClickListener() {
            @Override
            public void onButtonClick(NewsMainBTN btn) {
                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment fragment = null;
                switch (btn.getId()) {
                    case 102:
                        fragment = fragmentManager.findFragmentByTag(NewsGroupListFrag.class.getName());
                        if (fragment == null) {
                            fragment = NewsGroupListFrag.newInstance();
                            fragmentTransaction.addToBackStack(fragment.getClass().getName());
                        }
                        break;
                    case 103:
                        fragment = fragmentManager.findFragmentByTag(NewsPublisherListFrag.class.getName());
                        if (fragment == null) {
                            fragment = NewsPublisherListFrag.newInstance();
                            fragmentTransaction.addToBackStack(fragment.getClass().getName());
                        }
                        break;
                }
                new HelperFragment(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), fragment).setReplace(false).load();
            }

            @Override
            public void onNewsCategoryClick(NewsFPList channel) {

            }

            @Override
            public void onSliderClick(NewsSlider slide) {

            }
        });
//        binding.rcMain.setAdapter(adapter);
    }

}
