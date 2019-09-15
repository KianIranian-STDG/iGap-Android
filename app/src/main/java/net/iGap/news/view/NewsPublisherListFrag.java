package net.iGap.news.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;

import net.iGap.R;
import net.iGap.databinding.NewsPublisherlistFragBinding;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.libs.bottomNavigation.Util.Utils;
import net.iGap.news.repository.model.NewsPublisher;
import net.iGap.news.view.Adapter.NewsGroupAdapter;
import net.iGap.news.view.Adapter.NewsPublisherAdapter;
import net.iGap.news.viewmodel.NewsPublisherListVM;

import java.util.List;

public class NewsPublisherListFrag extends BaseFragment {

    private NewsPublisherlistFragBinding binding;
    private NewsPublisherListVM newsVM;
    private HelperToolbar mHelperToolbar;


    public static NewsPublisherListFrag newInstance() {
        NewsPublisherListFrag kuknosLoginFrag = new NewsPublisherListFrag();
        return kuknosLoginFrag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        newsVM = ViewModelProviders.of(this).get(NewsPublisherListVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.news_publisherlist_frag, container, false);
        binding.setViewmodel(newsVM);
        binding.setLifecycleOwner(this);

        return binding.getRoot();
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLeftIcon(R.string.back_icon)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                })
                .setLogoShown(true);

        LinearLayout toolbarLayout = binding.toolbar;
        Utils.darkModeHandler(toolbarLayout);
        toolbarLayout.addView(mHelperToolbar.getView());

        binding.rcGroup.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.rcGroup.setLayoutManager(layoutManager);

        newsVM.getData();
        onErrorObserver();
        onDataChanged();
        onProgress();
    }


    private void onErrorObserver() {
        newsVM.getError().observe(getViewLifecycleOwner(), newsError -> {
            if (newsError.getState() == true) {
                Snackbar snackbar = Snackbar.make(binding.Container, getString(newsError.getResID()), Snackbar.LENGTH_LONG);
                snackbar.setAction(getText(R.string.kuknos_Restore_Error_Snack), v -> snackbar.dismiss());
                snackbar.show();
            }
        });
    }

    private void onProgress() {
        newsVM.getProgressState().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                binding.ProgressV.setVisibility(View.VISIBLE);
            }
            else {
                binding.ProgressV.setVisibility(View.GONE);
            }
        });
    }

    private void onDataChanged() {
        newsVM.getmData().observe(getViewLifecycleOwner(), newsGroup -> initMainRecycler(newsGroup));
    }

    private void initMainRecycler(List<NewsPublisher> data) {
        NewsPublisherAdapter adapter = new NewsPublisherAdapter(data);
        binding.rcGroup.setAdapter(adapter);
    }

}
