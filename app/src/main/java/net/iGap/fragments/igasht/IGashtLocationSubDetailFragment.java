package net.iGap.fragments.igasht;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import net.iGap.R;
import net.iGap.adapter.igahst.IGashtDetailSliderAdapter;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.FragmentIgashtLocationSubDetailBinding;
import net.iGap.libs.bannerslider.BannerSlider;
import net.iGap.messenger.theme.Theme;
import net.iGap.viewmodel.igasht.IGashtLocationSubDetailViewModel;

public class IGashtLocationSubDetailFragment extends BaseAPIViewFrag<IGashtLocationSubDetailViewModel> {

    private FragmentIgashtLocationSubDetailBinding binding;
    private IGashtDetailSliderAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(IGashtLocationSubDetailViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_igasht_location_sub_detail, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AppCompatTextView null_txt = view.findViewById(R.id.null_txt);
        null_txt.setTextColor(Theme.getColor(Theme.key_title_text));
        AppCompatTextView subDetail = view.findViewById(R.id.subDetail);
        subDetail.setTextColor(Theme.getColor(Theme.key_title_text));
        AppCompatTextView subTitle = view.findViewById(R.id.subTitle);
        subTitle.setTextColor(Theme.getColor(Theme.key_title_text));
        view.getRootView().setBackgroundColor(Theme.getColor(Theme.key_window_background));
        BannerSlider banner_slider = view.findViewById(R.id.banner_slider);
        banner_slider.setBackgroundColor(Theme.getColor(Theme.key_window_background));
        if (viewModel.getLocationDetail() != null) {
            if (viewModel.getLocationDetail().getmShortDescription() != null) {
                Spanned titleText = Html.fromHtml(viewModel.getLocationDetail().getmShortDescription());
                binding.subTitle.setText(titleText);
            }

            if (viewModel.getLocationDetail().getmFullDescription() != null) {
                Spanned detailText = Html.fromHtml(viewModel.getLocationDetail().getmFullDescription());
                binding.subDetail.setText(detailText);
            }

            if (viewModel.getLocationDetail().getmGallery() != null) {
                binding.bannerSlider.postDelayed(() -> {
                    adapter = new IGashtDetailSliderAdapter(viewModel.getLocationDetail().getmGallery());
                    binding.bannerSlider.setAdapter(adapter);
                    binding.bannerSlider.setSelectedSlide(0);
                    binding.bannerSlider.setInterval(2000);
                }, 100);
            }
        }

    }
}
