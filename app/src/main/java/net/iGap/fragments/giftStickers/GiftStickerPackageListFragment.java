package net.iGap.fragments.giftStickers;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.fragments.chatMoneyTransfer.ParentChatMoneyTransferFragment;
import net.iGap.observers.rx.ObserverFragment;

public class GiftStickerPackageListFragment extends ObserverFragment<GiftStickerPackageListViewModel> {
    boolean fromChat;

    private GiftStickerPackageListFragment() {
    }

    public static GiftStickerPackageListFragment getInstance(boolean fromChat) {
        GiftStickerPackageListFragment fragment = new GiftStickerPackageListFragment();
        fragment.fromChat = fromChat;
        return fragment;
    }

    @Override
    public void setupViews() {
        RecyclerView recyclerView = rootView.findViewById(R.id.giftStickerPackageList);
        recyclerView.setAdapter(new GiftStickerPackageListAdapter(stickerGroup -> viewModel.onGiftStickerPackageClicked(stickerGroup)));

        if (getArguments() != null) {
            rootView.findViewById(R.id.pageTitle).setVisibility(getArguments().getBoolean("showTitle", true) ? View.VISIBLE : View.GONE);
        }

        rootView.findViewById(R.id.cancelButton).setVisibility(fromChat ? View.VISIBLE : View.GONE);

        viewModel.getGoBack().observe(getViewLifecycleOwner(), isGoBack -> {
            if (getParentFragment() instanceof ParentChatMoneyTransferFragment && isGoBack != null && isGoBack) {
                ((ParentChatMoneyTransferFragment) getParentFragment()).dismissDialog();
            }
        });

        viewModel.getLoadData().observe(getViewLifecycleOwner(), giftStickerList -> {
            if (recyclerView.getAdapter() instanceof GiftStickerPackageListAdapter && giftStickerList != null) {
                ((GiftStickerPackageListAdapter) recyclerView.getAdapter()).setItems(giftStickerList);
            }
        });

        viewModel.getGoToPackageItemPage().observe(getViewLifecycleOwner(), giftStickerPackage -> {
            if (getParentFragment() instanceof ParentChatMoneyTransferFragment && giftStickerPackage != null) {
                ((ParentChatMoneyTransferFragment) getParentFragment()).loadStickerPackageItemPage(giftStickerPackage);
            } else if (getParentFragment() instanceof GiftStickerMainFragment) {
                ((GiftStickerMainFragment) getParentFragment()).loadStickerPackageItemPage(giftStickerPackage);
            }
        });

        rootView.findViewById(R.id.cancelButton).setOnClickListener(v -> viewModel.onCancelButtonClick());
        rootView.findViewById(R.id.retryView).setOnClickListener(v -> viewModel.onRetryButtonClicked());

        viewModel.getIsShowLoadingLiveData().observe(getViewLifecycleOwner(), visibility -> rootView.findViewById(R.id.loadingView).setVisibility(visibility));
        viewModel.getIsShowRetryViewLiveData().observe(getViewLifecycleOwner(), visibility -> rootView.findViewById(R.id.retryView).setVisibility(visibility));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_gift_sticker_package;
    }

    @Override
    public GiftStickerPackageListViewModel getObserverViewModel() {
        return ViewModelProviders.of(this).get(GiftStickerPackageListViewModel.class);
    }
}
