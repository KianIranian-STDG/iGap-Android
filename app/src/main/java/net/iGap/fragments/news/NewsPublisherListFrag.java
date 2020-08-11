package net.iGap.fragments.news;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;

import net.iGap.R;
import net.iGap.adapter.news.NewsPublisherAdapter;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.NewsPublisherlistFragBinding;
import net.iGap.helper.HelperToolbar;
import net.iGap.model.news.NewsPublisher;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.viewmodel.news.NewsPublisherListVM;

import java.util.List;

public class NewsPublisherListFrag extends BaseAPIViewFrag<NewsPublisherListVM> {

    private NewsPublisherlistFragBinding binding;


    public static NewsPublisherListFrag newInstance() {
        return new NewsPublisherListFrag();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(NewsPublisherListVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.news_publisherlist_frag, container, false);
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
                .setLeftIcon(R.string.back_icon)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                })
                .setLogoShown(true);

        binding.toolbar.addView(mHelperToolbar.getView());

        binding.pullToRefresh.setOnRefreshListener(() -> {
            viewModel.getData();
            binding.noItemInListError.setVisibility(View.GONE);
        });

        binding.rcGroup.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.rcGroup.setLayoutManager(layoutManager);

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
        viewModel.getmData().observe(getViewLifecycleOwner(), this::initMainRecycler);
    }

    private void initMainRecycler(List<NewsPublisher> data) {
        NewsPublisherAdapter adapter = new NewsPublisherAdapter(data);
        binding.rcGroup.setAdapter(adapter);
    }

}
