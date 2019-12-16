package net.iGap.news.view;

import android.content.Intent;
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
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.squareup.picasso.Picasso;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.NewsDetailPageBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.news.repository.model.NewsComment;
import net.iGap.news.repository.model.NewsDetail;
import net.iGap.news.repository.model.NewsList;
import net.iGap.news.view.Adapter.NewsCommentAdapter;
import net.iGap.news.view.Adapter.NewsDetailRelatedCardsAdapter;
import net.iGap.news.view.Adapter.NewsDetailSliderAdapter;
import net.iGap.news.viewmodel.NewsDetailVM;

import java.util.List;

public class NewsDetailFrag extends BaseAPIViewFrag {

    private NewsDetailPageBinding binding;
    private NewsDetailVM newsMainVM;

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
        this.viewModel = newsMainVM;

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
        toolbarLayout.addView(mHelperToolbar.getView());

        binding.shareNews.setOnClickListener(v -> Toast.makeText(getContext(), "share", Toast.LENGTH_SHORT).show());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        binding.relatedNewsList.setLayoutManager(layoutManager);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.commentList.setLayoutManager(layoutManager2);

        binding.shareNewsBTN.setOnClickListener(v -> {
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, newsMainVM.getData().getValue().getTitle() + "\n" + newsMainVM.getData().getValue().getLead() + "\n" + "تاریخ انتشار: " + newsMainVM.getData().getValue().getDate() + "\n" + "قدرت گرفته از آیگپ" + "\n" + "این خبر را در آیگپ بخوانید: " + "igap://news/showDetail/" + arg.getString("NewsID") + "\n" + "لینک خبر: " + newsMainVM.getData().getValue().getLink());
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "Share via"));
        });

        binding.writeComment.setOnClickListener(v -> {
            NewsAddCommentBottomSheetFrag bottomSheetFragment = new NewsAddCommentBottomSheetFrag()
                    .setData(arg.getString("NewsID"), result -> {
                        if (result)
                            Toast.makeText(getContext(), R.string.news_add_comment_successToast, Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getContext(), R.string.news_add_comment_failToast, Toast.LENGTH_SHORT).show();
                    });
            bottomSheetFragment.show(getFragmentManager(), "AddCommentBottomSheet");
        });

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
                if (newsError.getTitle().equals("001"))
                    binding.noDataError.setVisibility(View.VISIBLE);
            }
        });
    }

    private void onProgress() {
        newsMainVM.getProgressStateContext().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean)
                binding.ProgressTitleV.setVisibility(View.VISIBLE);
            else
                binding.ProgressTitleV.setVisibility(View.GONE);
        });
        newsMainVM.getProgressStateComment().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean)
                binding.ProgressCommentV.setVisibility(View.VISIBLE);
            else
                binding.ProgressCommentV.setVisibility(View.GONE);
        });
        newsMainVM.getProgressStateRelated().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean)
                binding.ProgressNewsV.setVisibility(View.VISIBLE);
            else
                binding.ProgressNewsV.setVisibility(View.GONE);
        });
    }

    private void onDataChanged() {
        newsMainVM.getData().observe(getViewLifecycleOwner(), this::initMainRecycler);
        newsMainVM.getComments().observe(getViewLifecycleOwner(), this::initCommentRecycler);
        newsMainVM.getRelatedNews().observe(getViewLifecycleOwner(), this::initRelatedNews);
    }

    private void initCommentRecycler(List<NewsComment> data) {
        if (data == null || data.size() == 0) {
            binding.noItemInListError.setVisibility(View.VISIBLE);
            binding.commentList.setVisibility(View.GONE);
            return;
        }
        NewsCommentAdapter adapter = new NewsCommentAdapter(data);
        binding.commentList.setAdapter(adapter);
    }

    private void initRelatedNews(NewsList data) {
        if (data == null || data.getNews() == null) {
            binding.newsBack.setVisibility(View.GONE);
            return;
        }
        NewsDetailRelatedCardsAdapter adapter = new NewsDetailRelatedCardsAdapter(data);
        adapter.setCallback(slide -> {
            Fragment fragment = NewsDetailFrag.newInstance();
            Bundle args = new Bundle();
            args.putString("NewsID", slide.getId());
            fragment.setArguments(args);
            new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setTag("news detail " + slide.getId()).setReplace(false).load(false);
        });
        binding.relatedNewsList.setAdapter(adapter);
    }

    private void initMainRecycler(NewsDetail data) {

        Picasso.get()
                .load(data.getSourceImage())
                .placeholder(R.mipmap.news_temp_banner)
                .into(binding.image);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.detailTV.setText(Html.fromHtml(data.getBody(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            binding.detailTV.setText(Html.fromHtml(data.getBody()));
        }

        NewsDetailSliderAdapter adapter = new NewsDetailSliderAdapter();
        adapter.setData(data.getImages());
        binding.bannerSlider.setSliderAdapter(adapter);
        // set animation
        binding.bannerSlider.setIndicatorAnimation(IndicatorAnimations.THIN_WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        binding.bannerSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);

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
