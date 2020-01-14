package net.iGap.fragments.emoji.add;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.fragments.emoji.struct.StructIGStickerCategory;
import net.iGap.fragments.emoji.struct.StructIGStickerGroup;
import net.iGap.module.EndlessRecyclerViewScrollListener;
import net.iGap.viewmodel.AddStickerViewModel;

public class AddStickersFragment extends BaseFragment {

    private AddStickerFragmentAdapter adapter;
    private StructIGStickerCategory category;
    private AddStickerViewModel viewModel;

    private ProgressBar progressBar;

    public static AddStickersFragment newInstance(StructIGStickerCategory category) {
        AddStickersFragment addStickersFragment = new AddStickersFragment();
        addStickersFragment.category = category;
        return addStickersFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new AddStickerViewModel(category);
        adapter = new AddStickerFragmentAdapter();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_stickers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.subscribe();

        progressBar = view.findViewById(R.id.progress_stricker);

        RecyclerView recyclerView = view.findViewById(R.id.rcvSettingPage);
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
        });


        viewModel.getOpenStickerDetailLiveData().observe(getViewLifecycleOwner(), sticker -> openFragmentAddStickerToFavorite(sticker.getGroupId()));

        viewModel.getLoadMoreProgressLiveData().observe(getViewLifecycleOwner(), visibility -> progressBar.setVisibility(visibility));

        viewModel.getStickerGroupLiveData().observe(getViewLifecycleOwner(), stickerGroup -> adapter.addData(stickerGroup));
    }

    private void openFragmentAddStickerToFavorite(String groupId) {
        StructIGStickerGroup stickerGroup = new StructIGStickerGroup(groupId);

        StickerDialogFragment dialogFragment = StickerDialogFragment.getInstance(stickerGroup, true);

        if (getFragmentManager() != null)
            dialogFragment.show(getFragmentManager(), "dialogFragment");
    }
}
