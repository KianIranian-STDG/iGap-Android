package net.iGap.fragments.giftStickers;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.emoji.struct.StructIGStickerGroup;
import net.iGap.fragments.giftStickers.enterNationalCode.EnterNationalCodeFragment;
import net.iGap.rx.ObserverFragment;

public class GiftStickerHomeFragment extends ObserverFragment<GiftStickerHomeViewModel> {
    private ImageView sliderIv;
    private TextView sliderTitleTv;

    @Override
    public void setupViews() {
        getChildFragmentManager().beginTransaction().replace(R.id.header, EnterNationalCodeFragment.getInstance(false), EnterNationalCodeFragment.class.getName()).commit();

        sliderIv = rootView.findViewById(R.id.iv_giftStickerHome);
        sliderTitleTv = rootView.findViewById(R.id.tv_giftStickerHome_sliderTitle);

        rootView.findViewById(R.id.myGiftStickerPage).setOnClickListener(v -> {
            if (getParentFragment() instanceof GiftStickerMainFragment) {
                ((GiftStickerMainFragment) getParentFragment()).loadBuyMySticker();

            }
        });

        rootView.findViewById(R.id.receivedGiftStickerPage).setOnClickListener(v -> {
            if (getParentFragment() instanceof GiftStickerMainFragment) {
                ((GiftStickerMainFragment) getParentFragment()).loadReceivedGiftStickerPage();
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.getSliderVisibilityLiveData().observe(getViewLifecycleOwner(), visibility -> sliderIv.setVisibility(visibility));

        viewModel.getSliderMutableLiveData().observe(getViewLifecycleOwner(), dataModel -> {
            if (dataModel.getData().get(0).getImageUrl() != null) {
                String[] scales = dataModel.getInfo().getScale().split(":");
                float height = rootView.getWidth() * 1.0f * Integer.parseInt(scales[1]) / Integer.parseInt(scales[0]);
                sliderIv.getLayoutParams().height = Math.round(height);
                Glide.with(getContext())
                        .load(dataModel.getData().get(0).getImageUrl())
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(sliderIv);
                if (dataModel.getInfo().getTitle() != null && dataModel.getInfo().getTitleEn() != null) {
                    sliderTitleTv.setVisibility(View.VISIBLE);
                    sliderTitleTv.setGravity(Gravity.BOTTOM | (G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT));
                    sliderTitleTv.setText(G.selectedLanguage.equals("fa") ? dataModel.getInfo().getTitle() : dataModel.getInfo().getTitle());
                }
            } else {
                sliderIv.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_gift_sticker_home;
    }

    @Override
    public GiftStickerHomeViewModel getObserverViewModel() {
        return new GiftStickerHomeViewModel();
    }

    public void loadStickerPackagePage() {
        if (getParentFragment() instanceof GiftStickerMainFragment) {
            ((GiftStickerMainFragment) getParentFragment()).loadStickerPackagePage();
        }
    }

    public void loadStickerPackageItemPage(StructIGStickerGroup giftStickerPackage) {
        if (getParentFragment() instanceof GiftStickerMainFragment) {
            ((GiftStickerMainFragment) getParentFragment()).loadStickerPackageItemPage(giftStickerPackage);
        }
    }
}
