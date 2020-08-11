package net.iGap.fragments.news;

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
import net.iGap.adapter.news.NewsListAdapter;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.NewsListFragBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.model.news.NewsApiArg;
import net.iGap.model.news.NewsList;
import net.iGap.module.PaginationScrollListener;
import net.iGap.viewmodel.news.NewsListVM;

public class NewsListFrag extends BaseAPIViewFrag<NewsListVM> {

    private NewsListFragBinding binding;
    private NewsApiArg apiArg;

    private int currentPage = 0;
    private boolean isLastPage = false;
    private int totalPage = 10;
    private boolean isLoading = false;
    private NewsListAdapter adapter;
    private onImageListener handler;

    public static NewsListFrag newInstance() {
        return new NewsListFrag();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(NewsListVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.news_list_frag, container, false);
//        binding.setViewmodel(newsVM);
        binding.setLifecycleOwner(this);

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
                viewModel.getData(apiArg);
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

            viewModel.getData(apiArg);
            binding.noItemInListError.setVisibility(View.GONE);
        });

        viewModel.getData(apiArg);
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
                /*Snackbar snackbar = Snackbar.make(binding.Container, getString(newsError.getResID()), Snackbar.LENGTH_LONG);
                snackbar.setAction(getText(R.string.kuknos_Restore_Error_Snack), v -> snackbar.dismiss());
                snackbar.show();*/
            }
        });
    }

    private void onProgress() {
        if (apiArg.getStart() == 1)
            viewModel.getProgressState().observe(getViewLifecycleOwner(), aBoolean -> binding.pullToRefresh.setRefreshing(aBoolean));
    }

    private void onDataChanged() {
        viewModel.getmData().observe(getViewLifecycleOwner(), this::initMainRecycler);
    }

    private void initMainRecycler(NewsList data) {

        if (currentPage != 0)
            adapter.removeLoading();

        if (data.getNews() == null || data.getNews().size() == 0)
            return;

        if (apiArg.getStart() == 1 && handler != null) {
            handler.onImageLoader(data.getNews().get(0));
            data.getNews().remove(0);
        }

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

    public onImageListener getHandler() {
        return handler;
    }

    public void setHandler(onImageListener handler) {
        this.handler = handler;
    }

    public interface onImageListener {
        void onImageLoader(NewsList.News news);
    }
}
