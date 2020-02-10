package net.iGap.fragments.giftStickers;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.iGap.R;
import net.iGap.fragments.emoji.struct.StructIGStickerGroup;
import net.iGap.fragments.giftStickers.enterNationalCode.EnterNationalCodeFragment;
import net.iGap.rx.ObserverFragment;
import net.iGap.services.imageLoaderService.ImageLoadingServiceInjector;

public class GiftStickerHomeFragment extends ObserverFragment<GiftStickerHomeViewModel> {
    private ImageView sliderIv;

    @Override
    public void setupViews() {
        getChildFragmentManager().beginTransaction().replace(R.id.header, EnterNationalCodeFragment.getInstance(false), EnterNationalCodeFragment.class.getName()).commit();

        sliderIv = rootView.findViewById(R.id.iv_giftStickerHome);

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
            Log.i("abbasiPro", "getSliderMutableLiveData: START " + (dataModel == null));
            if (dataModel.getData().get(0).getImageUrl() != null) {
                String[] scales = dataModel.getInfo().getScale().split(":");
                float height = rootView.getWidth() * 1.0f * Integer.parseInt(scales[1]) / Integer.parseInt(scales[0]);
                sliderIv.getLayoutParams().height = Math.round(height);

                ImageLoadingServiceInjector.inject().loadImage(sliderIv, dataModel.getData().get(0).getImageUrl());
            } else {
                sliderIv.setVisibility(View.GONE);
            }
            Log.i("abbasiPro", "onViewCreated: END");
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
