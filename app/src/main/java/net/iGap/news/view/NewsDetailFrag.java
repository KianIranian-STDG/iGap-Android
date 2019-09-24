package net.iGap.news.view;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.squareup.picasso.Picasso;

import net.iGap.R;
import net.iGap.databinding.NewsDetailPageBinding;
import net.iGap.databinding.NewsMainPageBinding;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.libs.bottomNavigation.Util.Utils;
import net.iGap.news.repository.model.NewsDetail;
import net.iGap.news.repository.model.NewsFPList;
import net.iGap.news.repository.model.NewsFirstPage;
import net.iGap.news.repository.model.NewsMainBTN;
import net.iGap.news.repository.model.NewsSlider;
import net.iGap.news.view.Adapter.NewsDetailSliderAdapter;
import net.iGap.news.view.Adapter.NewsFirstPageAdapter;
import net.iGap.news.view.Adapter.NewsSliderAdapter;
import net.iGap.news.viewmodel.NewsDetailVM;
import net.iGap.news.viewmodel.NewsMainVM;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NewsDetailFrag extends BaseFragment {

    private NewsDetailPageBinding binding;
    private NewsDetailVM newsMainVM;
    private String newsID;

    public static NewsDetailFrag newInstance() {
        return new NewsDetailFrag();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        newsMainVM = ViewModelProviders.of(this).get(NewsDetailVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.news_detail_page, container, false);
        binding.setViewmodel(newsMainVM);
        binding.setLifecycleOwner(this);

        return attachToSwipeBack(binding.getRoot());
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        Bundle arg = getArguments();

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
        });*/

        newsMainVM.getDataFromServer(arg.getString("NewsID"));
        onErrorObserver();
        onDataChanged();
        onProgress();
    }

    private void onErrorObserver() {
        newsMainVM.getError().observe(getViewLifecycleOwner(), newsError -> {
            if (newsError.getState()) {
                // show error
                Snackbar snackbar = Snackbar.make(binding.Container, getString(newsError.getResID()), Snackbar.LENGTH_LONG);
                snackbar.setAction(getText(R.string.kuknos_Restore_Error_Snack), v -> snackbar.dismiss());
                snackbar.show();
            }
        });
    }

    private void onProgress() {
        newsMainVM.getProgressState().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                switch (newsMainVM.getProgressType()) {
                    case 0:
                        if (aBoolean)
                            binding.ProgressTitleV.setVisibility(View.VISIBLE);
                        else
                            binding.ProgressTitleV.setVisibility(View.GONE);
                        break;
                    case 1:
                        if (aBoolean)
                            binding.ProgressCommentV.setVisibility(View.VISIBLE);
                        else
                            binding.ProgressCommentV.setVisibility(View.GONE);
                        break;
                    case 2:
                        if (aBoolean)
                            binding.ProgressNewsV.setVisibility(View.VISIBLE);
                        else
                            binding.ProgressNewsV.setVisibility(View.GONE);
                        break;
                }
            }
        });
    }

    private void onDataChanged() {
        newsMainVM.getData().observe(getViewLifecycleOwner(), this::initMainRecycler);
    }

    private void initMainRecycler(NewsDetail data) {

        Picasso.get()
//                .load("https://images.vexels.com/media/users/3/144598/preview2/96a2d7aa32ed86c5e4bd089bdfbd341c-breaking-news-banner-header.jpg")
                .load(data.getSourceImage())
                .placeholder(R.mipmap.news_temp_banner)
                .into(binding.image);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.detailTV.setText(Html.fromHtml(data.getBody(), Html.FROM_HTML_MODE_COMPACT));
        }
        else {
            binding.detailTV.setText(Html.fromHtml(data.getBody()));
        }

        NewsDetailSliderAdapter adapter = new NewsDetailSliderAdapter();
        adapter.setData(data.getImages());
        binding.bannerSlider.setSliderAdapter(adapter);
        // set animation
        binding.bannerSlider.setIndicatorAnimation(IndicatorAnimations.THIN_WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        binding.bannerSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);

        binding.shareNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "share", Toast.LENGTH_SHORT).show();
            }
        });

        //checks
        if (binding.rootTitle.getText().equals(""))
            binding.rootTitle.setVisibility(View.GONE);

        if (binding.view.getText().equals("") || binding.view.getText().equals("0")) {
            binding.view.setVisibility(View.GONE);
            binding.viewIcon.setVisibility(View.GONE);
        }

        if (binding.tags.getText().equals(""))
            binding.tags.setVisibility(View.GONE);

    }

}
