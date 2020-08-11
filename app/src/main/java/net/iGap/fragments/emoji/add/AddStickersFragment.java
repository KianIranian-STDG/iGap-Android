package net.iGap.fragments.emoji.add;


import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.fragments.emoji.struct.StructIGStickerCategory;
import net.iGap.fragments.emoji.struct.StructIGStickerGroup;
import net.iGap.module.EndlessRecyclerViewScrollListener;
import net.iGap.observers.rx.ObserverFragment;
import net.iGap.viewmodel.AddStickerViewModel;

public class AddStickersFragment extends ObserverFragment<AddStickerViewModel> {

    private AddStickerFragmentAdapter adapter;
    private StructIGStickerCategory category;

    private ProgressBar progressBar;

    public static AddStickersFragment newInstance(StructIGStickerCategory category) {
        AddStickersFragment addStickersFragment = new AddStickersFragment();
        addStickersFragment.category = category;
        return addStickersFragment;
    }

    @Override
    public AddStickerViewModel getObserverViewModel() {
        return ViewModelProviders.of(this).get(AddStickerViewModel.class);
    }

    @Override
    public void setupViews() {
        progressBar = rootView.findViewById(R.id.progress_stricker);

        RecyclerView recyclerView = rootView.findViewById(R.id.rcvSettingPage);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);

        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int iPage, int totalItemsCount, RecyclerView view) {
                viewModel.onPageEnded();
            }
        };

        recyclerView.addOnScrollListener(scrollListener);

        adapter.setListener(new AddStickerFragmentAdapter.AddStickerAdapterListener() {
            @Override
            public void onButtonClick(StructIGStickerGroup stickerGroup, AddStickerFragmentAdapter.ProgressStatus progressStatus) {
                viewModel.onItemButtonClicked(stickerGroup, sticker -> {
                    if (sticker.getGroupId().equals(stickerGroup.getGroupId())) {
                        progressStatus.setVisibility(sticker.isInUserList());
                    }
                });
            }

            @Override
            public void onCellClick(StructIGStickerGroup stickerGroup) {
                viewModel.onItemCellClicked(stickerGroup);
            }

            @Override
            public void onButtonStatusChange(AddStickerFragmentAdapter.ButtonsStatus buttonsStatus) {
                viewModel.getButtonStatusChangedLiveData().observe(getViewLifecycleOwner(), buttonsStatus::changed);
            }
        });


        viewModel.getOpenStickerDetailLiveData().observe(getViewLifecycleOwner(), sticker -> openFragmentAddStickerToFavorite(sticker.getGroupId()));

        viewModel.getLoadMoreProgressLiveData().observe(getViewLifecycleOwner(), visibility -> progressBar.setVisibility(visibility));

        viewModel.getStickerGroupLiveData().observe(getViewLifecycleOwner(), stickerGroup -> adapter.addData(stickerGroup));
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_add_stickers;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel.setCategory(category);
        adapter = new AddStickerFragmentAdapter();
    }

    private void openFragmentAddStickerToFavorite(String groupId) {
        StructIGStickerGroup stickerGroup = new StructIGStickerGroup(groupId);

        StickerDialogFragment dialogFragment = StickerDialogFragment.getInstance(stickerGroup, true);

        if (getFragmentManager() != null)
            dialogFragment.show(getFragmentManager(), "dialogFragment");
    }
}
