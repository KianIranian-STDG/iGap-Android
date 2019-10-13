package net.iGap.news.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.NewsListFragBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.news.repository.model.NewsApiArg;
import net.iGap.news.repository.model.NewsList;
import net.iGap.news.view.Adapter.NewsListAdapter;
import net.iGap.news.view.Adapter.PaginationScrollListener;
import net.iGap.news.viewmodel.NewsListVM;

public class NewsListFrag extends BaseAPIViewFrag {

    private NewsListFragBinding binding;
    private NewsListVM newsVM;
    private NewsApiArg apiArg;

    private int currentPage = 0;
    private boolean isLastPage = false;
    private int totalPage = 10;
    private boolean isLoading = false;
    private NewsListAdapter adapter;

    public static NewsListFrag newInstance() {
        return new NewsListFrag();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        newsVM = ViewModelProviders.of(this).get(NewsListVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.news_list_frag, container, false);
//        binding.setViewmodel(newsVM);
        binding.setLifecycleOwner(this);
        this.viewModel = newsVM;

        return binding.getRoot();
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        binding.rcGroup.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.rcGroup.setLayoutManager(layoutManager);
        binding.rcGroup.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage++;

                apiArg.setStart(apiArg.getStart() + 1);
                newsVM.getData(apiArg);
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
        adapter = new NewsListAdapter(new NewsList());
        adapter.setCallback(slide -> {
            FragmentManager fragmentManager = getChildFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment fragment = fragmentManager.findFragmentByTag(NewsDetailFrag.class.getName());
            if (fragment == null) {
                fragment = NewsDetailFrag.newInstance();
                fragmentTransaction.addToBackStack(fragment.getClass().getName());
            }
            Bundle args = new Bundle();
            args.putString("NewsID", slide.getId());
            fragment.setArguments(args);
            new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
        });
        binding.rcGroup.setAdapter(adapter);

        binding.pullToRefresh.setOnRefreshListener(() -> {
            currentPage = 0;
            isLastPage = false;
            adapter.clear();

            newsVM.getData(apiArg);
            binding.noItemInListError.setVisibility(View.GONE);
        });

        newsVM.getData(apiArg);
        onErrorObserver();
        onDataChanged();
        onProgress();
    }


    private void onErrorObserver() {
        newsVM.getError().observe(getViewLifecycleOwner(), newsError -> {
            if (newsError.getState()) {
                //show the related text
                binding.noItemInListError.setVisibility(View.VISIBLE);
                // show error
                /*Snackbar snackbar = Snackbar.make(binding.Container, getString(newsError.getResID()), Snackbar.LENGTH_LONG);
                snackbar.setAction(getText(R.string.kuknos_Restore_Error_Snack), v -> snackbar.dismiss());
                snackbar.show();*/
            }
        });
    }

    private void onProgress() {
        if (apiArg.getStart() == 1)
        newsVM.getProgressState().observe(getViewLifecycleOwner(), aBoolean -> binding.pullToRefresh.setRefreshing(aBoolean));
    }

    private void onDataChanged() {
        newsVM.getmData().observe(getViewLifecycleOwner(), this::initMainRecycler);
    }

    private void initMainRecycler(NewsList data) {
        if (currentPage != 0)
            adapter.removeLoading();
        adapter.addItems(data);

        // check weather is last page or not
        if (currentPage < totalPage) {
            adapter.addLoading();
        } else {
            isLastPage = true;
        }
        isLoading = false;
    }

    void setApiArg(NewsApiArg apiArg) {
        this.apiArg = apiArg;
    }
}
