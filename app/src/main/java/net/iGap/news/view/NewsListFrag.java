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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import net.iGap.R;
import net.iGap.databinding.NewsGrouplistFragBinding;
import net.iGap.databinding.NewsListFragBinding;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.libs.bottomNavigation.Util.Utils;
import net.iGap.news.repository.model.NewsApiArg;
import net.iGap.news.repository.model.NewsGroup;
import net.iGap.news.repository.model.NewsList;
import net.iGap.news.view.Adapter.NewsGroupAdapter;
import net.iGap.news.view.Adapter.NewsListAdapter;
import net.iGap.news.viewmodel.NewsGroupListVM;
import net.iGap.news.viewmodel.NewsListVM;

public class NewsListFrag extends BaseFragment {

    private NewsListFragBinding binding;
    private NewsListVM newsVM;
    private NewsApiArg apiArg;

    public static NewsListFrag newInstance() {
        NewsListFrag kuknosLoginFrag = new NewsListFrag();
        return kuknosLoginFrag;
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
        binding.setViewmodel(newsVM);
        binding.setLifecycleOwner(this);

        return binding.getRoot();
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        binding.rcGroup.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.rcGroup.setLayoutManager(layoutManager);

        newsVM.getData(apiArg);
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
        newsVM.getmData().observe(getViewLifecycleOwner(), newsList -> initMainRecycler(newsList));
    }

    private void initMainRecycler(NewsList data) {
        NewsListAdapter adapter = new NewsListAdapter(data);
        adapter.setCallback(new NewsListAdapter.onClickListener() {
            @Override
            public void onNewsGroupClick(NewsList.News slide) {
                /*FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment fragment = fragmentManager.findFragmentByTag(NewsGroupPagerFrag.class.getName());
                if (fragment == null) {
                    fragment = NewsGroupPagerFrag.newInstance();
                    fragmentTransaction.addToBackStack(fragment.getClass().getName());
                }
                new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();*/
            }
        });
        binding.rcGroup.setAdapter(adapter);
    }

    public NewsApiArg getApiArg() {
        return apiArg;
    }

    public void setApiArg(NewsApiArg apiArg) {
        this.apiArg = apiArg;
    }
}
