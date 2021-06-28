package net.iGap.fragments.news;

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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.NewsGrouplistFragBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.model.news.NewsGroup;
import net.iGap.adapter.news.NewsGroupAdapter;
import net.iGap.viewmodel.news.NewsGroupListVM;

import java.util.Objects;

public class NewsGroupListFrag extends BaseAPIViewFrag<NewsGroupListVM> {

    private NewsGrouplistFragBinding binding;


    public static NewsGroupListFrag newInstance() {
        return new NewsGroupListFrag();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(NewsGroupListVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.news_grouplist_frag, container, false);
//        binding.setViewmodel(newsVM);
        binding.setLifecycleOwner(this);

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        HelperToolbar mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLeftIcon(R.string.icon_back)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                })
                .setDefaultTitle(getResources().getString(R.string.news_groupTitle))
                .setLogoShown(true);

        LinearLayout toolbarLayout = binding.toolbar;
        toolbarLayout.addView(mHelperToolbar.getView());

        binding.rcGroup.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.rcGroup.setLayoutManager(layoutManager);

        binding.pullToRefresh.setOnRefreshListener(() -> {
            viewModel.getData();
            binding.noItemInListError.setVisibility(View.GONE);
        });

        viewModel.getData();
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
        viewModel.getmGroups().observe(getViewLifecycleOwner(), this::initMainRecycler);
    }

    private void initMainRecycler(NewsGroup data) {
        NewsGroupAdapter adapter = new NewsGroupAdapter(data);
        adapter.setCallBack(news -> {
            FragmentManager fragmentManager = getChildFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment fragment = fragmentManager.findFragmentByTag(NewsGroupPagerFrag.class.getName());
            if (fragment == null) {
                fragment = NewsGroupPagerFrag.newInstance();
                fragmentTransaction.addToBackStack(fragment.getClass().getName());
            }
            Bundle args = new Bundle();
            args.putString("GroupID", news.getId());
            args.putString("GroupTitle", news.getTitle());
            args.putString("GroupPic", news.getImage());
            fragment.setArguments(args);
            new HelperFragment(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), fragment).setReplace(false).load();
        });
        binding.rcGroup.setAdapter(adapter);
    }

}
