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

import com.google.gson.Gson;
import com.vanniktech.emoji.sticker.struct.StructGroupSticker;
import com.vanniktech.emoji.sticker.struct.StructSticker;

import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.fragments.emoji.api.APIEmojiService;
import net.iGap.fragments.emoji.api.ApiEmojiUtils;
import net.iGap.fragments.emoji.struct.StickerCategory;
import net.iGap.fragments.emoji.struct.StructIGStickerGroup;
import net.iGap.module.EndlessRecyclerViewScrollListener;
import net.iGap.viewmodel.AddStickerViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddStickersFragment extends BaseFragment {

    private APIEmojiService mAPIService;
    private AddStickerFragmentAdapter adapter;
    private AddStickerViewModel viewModel;

    private ProgressBar progressBar;
    private int page = 0;
    private int limit = 20;
    private StickerCategory stickerCategory;

    public static AddStickersFragment newInstance(String tabJson) {
        AddStickersFragment addStickersFragment = new AddStickersFragment();
        Bundle bundle = new Bundle();
        bundle.putString("tab", tabJson);
        addStickersFragment.setArguments(bundle);
        return addStickersFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewModel = new AddStickerViewModel();
        adapter = new AddStickerFragmentAdapter();
        return inflater.inflate(R.layout.fragment_add_stickers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            Gson gson = new Gson();
            stickerCategory = gson.fromJson(getArguments().getString("tab"), StickerCategory.class);
        }

        getDataStickers();

        progressBar = view.findViewById(R.id.progress_stricker);
        progressBar.setVisibility(View.VISIBLE);

        RecyclerView recyclerView = view.findViewById(R.id.rcvSettingPage);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);

        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE);
                }
                getDataStickers();
            }
        };

        recyclerView.addOnScrollListener(scrollListener);

        adapter.setListener(new AddStickerFragmentAdapter.AddStickerAdapterListener() {

            @Override
            public void onButtonClick(StructGroupSticker stickerGroup, AddStickerFragmentAdapter.ProgressStatus progressStatus) {
                viewModel.onItemButtonClicked(stickerGroup, sticker -> {
                    if (sticker.getId().equals(stickerGroup.getId())) {
                        progressStatus.setVisibility(sticker.getIsFavorite());
                    }
                });
            }

            @Override
            public void onCellClick(StructGroupSticker stickerGroup) {
                viewModel.onItemCellClicked(stickerGroup);
            }
        });


        viewModel.getOpenStickerDetailLiveData().observe(getViewLifecycleOwner(), sticker -> {

            StructIGStickerGroup stickerGroup = new StructIGStickerGroup(sticker.getId());
            stickerGroup.setValueWithOldStruct(sticker);

            StickerDialogFragment dialogFragment = StickerDialogFragment.getInstance(stickerGroup);

            if (getFragmentManager() != null)
                dialogFragment.show(getFragmentManager(), "dialogFragment");

        });

    }

    private void getDataStickers() {

        mAPIService = ApiEmojiUtils.getAPIService();

        mAPIService.getCategoryStickers(stickerCategory.getId(), page * limit, limit).enqueue(new Callback<StructSticker>() {
            @Override
            public void onResponse(@NotNull Call<StructSticker> call, @NotNull Response<StructSticker> response) {
                progressBar.setVisibility(View.GONE);
                if (response.body() != null) {
                    if (response.body().getOk() && response.body().getData().size() > 0) {
                        List<StructGroupSticker> data = response.body().getData();
                        adapter.addData(data);
                        page++;
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<StructSticker> call, @NotNull Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });

    }
}
