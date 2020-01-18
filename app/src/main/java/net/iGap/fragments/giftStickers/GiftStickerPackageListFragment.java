package net.iGap.fragments.giftStickers;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import net.iGap.R;
import net.iGap.databinding.FragmentGiftStickerPackageBinding;
import net.iGap.fragments.chatMoneyTransfer.ParentChatMoneyTransferFragment;

public class GiftStickerPackageListFragment extends Fragment {

    private GiftStickerPackageListViewModel viewModel;
    private FragmentGiftStickerPackageBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(GiftStickerPackageListViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_gift_sticker_package, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.giftStickerPackageList.setAdapter(new GiftStickerPackageListAdapter(position -> viewModel.onGiftStickerPackageClicked(position)));

        viewModel.getGoBack().observe(getViewLifecycleOwner(), isGoBack -> {
            if (getParentFragment() instanceof ParentChatMoneyTransferFragment && isGoBack != null && isGoBack) {
                ((ParentChatMoneyTransferFragment) getParentFragment()).dismissDialog();
            }
        });

        viewModel.getLoadData().observe(getViewLifecycleOwner(), giftStickerList -> {
            if (binding.giftStickerPackageList.getAdapter() instanceof GiftStickerPackageListAdapter && giftStickerList != null) {
                ((GiftStickerPackageListAdapter) binding.giftStickerPackageList.getAdapter()).setItems(giftStickerList);
            }
        });

        viewModel.getGoToPackageItemPage().observe(getViewLifecycleOwner(), giftStickerPackage -> {
            if (getParentFragment() instanceof ParentChatMoneyTransferFragment && giftStickerPackage != null) {
                ((ParentChatMoneyTransferFragment) getParentFragment()).loadStickerPackageItemPage();
            }
        });
    }
}
