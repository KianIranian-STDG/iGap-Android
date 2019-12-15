package net.iGap.fragments.emoji.add;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.vanniktech.emoji.sticker.struct.StructGroupSticker;
import com.vanniktech.emoji.sticker.struct.StructSticker;

import net.iGap.DbManager;
import net.iGap.G;
import net.iGap.R;
import net.iGap.eventbus.EventManager;
import net.iGap.fragments.BaseFragment;
import net.iGap.fragments.FragmentChat;
import net.iGap.fragments.emoji.HelperDownloadSticker;
import net.iGap.fragments.emoji.api.APIEmojiService;
import net.iGap.fragments.emoji.api.ApiEmojiUtils;
import net.iGap.fragments.emoji.stickerDetail.FragmentStickersDetail;
import net.iGap.fragments.emoji.struct.StickerCategory;
import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.fragments.emoji.struct.StructIGStickerGroup;
import net.iGap.fragments.emoji.struct.StructStickerResult;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.downloadFile.IGDownloadFile;
import net.iGap.helper.downloadFile.IGDownloadFileStruct;
import net.iGap.module.EndlessRecyclerViewScrollListener;
import net.iGap.realm.RealmStickers;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// TODO: 12/15/19 must fix this for better performance and Architectural redesign
public class AddStickersFragment extends BaseFragment {

    private APIEmojiService mAPIService;
    private AdapterSettingPage adapterSettingPage;
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
        return inflater.inflate(R.layout.fragment_add_stickers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Gson gson = new Gson();
        stickerCategory = gson.fromJson(getArguments().getString("tab"), StickerCategory.class);

        getDataStickers();
        progressBar = view.findViewById(R.id.progress_stricker);
        progressBar.setVisibility(View.VISIBLE);

        RecyclerView rcvSettingPage = view.findViewById(R.id.rcvSettingPage);
        adapterSettingPage = new AdapterSettingPage();
        rcvSettingPage.setAdapter(adapterSettingPage);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rcvSettingPage.setLayoutManager(layoutManager);
        // Triggered only when new data needs to be appended to the list
        // Add whatever code is needed to append new items to the bottom of the list
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list

                if (progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE);
                }

                getDataStickers();
            }
        };

        rcvSettingPage.addOnScrollListener(scrollListener);

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
                        adapterSettingPage.addData(data);
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

    public class AdapterSettingPage extends RecyclerView.Adapter {
        private List<StructGroupSticker> mData = new ArrayList<>();
        HashMap<String, StructGroupSticker> capitalCities = new HashMap<>();

        @NotNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder holder;
            if (viewType == StructIGSticker.ANIMATED_STICKER) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_motion_sticker, parent, false);
                holder = new MotionViewHolder(view);
            } else {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_normal_sticker, parent, false);
                holder = new ViewHolder(view);
            }
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            holder.setIsRecyclable(false);
            StructGroupSticker item = mData.get(position);
            if (!capitalCities.containsKey(item.getAvatarToken())) {
                capitalCities.put(item.getAvatarToken(), item);
            }

            if (holder instanceof MotionViewHolder) {
                MotionViewHolder motionViewHolder = (MotionViewHolder) holder;

                DbManager.getInstance().doRealmTask(realm -> {
                    RealmStickers realmStickers = RealmStickers.checkStickerExist(item.getId(), realm);
                    if (realmStickers == null) {
                        motionViewHolder.addStickerBtn.setVisibility(View.VISIBLE);
                    } else if (realmStickers.isFavorite()) {
                        motionViewHolder.addStickerBtn.setVisibility(View.INVISIBLE);
                    }
                });

                motionViewHolder.stickerNameTv.setText(item.getName());
                motionViewHolder.StickerCountTv.setText(item.getStickers().size() + " " + "Stickers");

                String path = HelperDownloadSticker.downloadStickerPath(item.getAvatarToken(), item.getAvatarName());
                if (!new File(path).exists()) {
                    EventManager.getInstance().addEventListener(EventManager.STICKER_DOWNLOAD, (id, message) -> {
                        String filePath = (String) message[0];
                        String fileToken = (String) message[1];

                        G.handler.post(() -> {
                            if (item.getAvatarToken().equals(fileToken)) {
                                try {
                                    motionViewHolder.groupAvatar.setAnimation(new FileInputStream(filePath), fileToken);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    });

                    IGDownloadFile.getInstance().startDownload(new IGDownloadFileStruct(item.getId(), item.getAvatarToken(), item.getAvatarSize(), path));
                } else {
                    try {
                        motionViewHolder.groupAvatar.setAnimation(new FileInputStream(path), item.getAvatarToken());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }

            } else {
                ViewHolder viewHolder = (ViewHolder) holder;

                DbManager.getInstance().doRealmTask(realm -> {
                    RealmStickers realmStickers = RealmStickers.checkStickerExist(item.getId(), realm);
                    if (realmStickers == null) {
                        viewHolder.addStickerBtn.setVisibility(View.VISIBLE);
                    } else if (realmStickers.isFavorite()) {
                        viewHolder.addStickerBtn.setVisibility(View.INVISIBLE);
                    }
                });

                String path = HelperDownloadSticker.downloadStickerPath(item.getAvatarToken(), item.getAvatarName());
                if (!new File(path).exists()) {
                    EventManager.getInstance().addEventListener(EventManager.STICKER_DOWNLOAD, (id, message) -> {
                        String filePath = (String) message[0];
                        String fileToken = (String) message[1];

                        G.handler.post(() -> {
                            if (item.getAvatarToken().equals(fileToken)) {
                                Glide.with(holder.itemView.getContext()).load(filePath).into(viewHolder.groupAvatar);
                            }
                        });
                    });

                    IGDownloadFile.getInstance().startDownload(new IGDownloadFileStruct(item.getId(), item.getAvatarToken(), item.getAvatarSize(), path));
                } else {
                    Glide.with(holder.itemView.getContext()).load(path).into(viewHolder.groupAvatar);
                }

                viewHolder.stickerNameTv.setText(item.getName());
                viewHolder.StickerCountTv.setText(item.getStickers().size() + " " + "Stickers");
            }
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (mData.get(position).getAvatarName().endsWith(".json"))
                return StructIGSticker.ANIMATED_STICKER;
            else
                return StructIGSticker.NORMAL_STICKER;
        }

        public void addData(List<StructGroupSticker> data) {
            this.mData.addAll(data);
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView groupAvatar;
            private Button addStickerBtn;
            private TextView stickerNameTv;
            private TextView StickerCountTv;

            @SuppressLint("RtlHardcoded")
            ViewHolder(View itemView) {
                super(itemView);
                groupAvatar = itemView.findViewById(R.id.iv_itemAddSticker_stickerAvatar);
                addStickerBtn = itemView.findViewById(R.id.btn_itemAddSticker);
                stickerNameTv = itemView.findViewById(R.id.tv_itemAddSticker_stickerName);
                StickerCountTv = itemView.findViewById(R.id.tv_itemAddSticker_stickerCount);

                stickerNameTv.setGravity(G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT);
                StickerCountTv.setGravity(G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT);

                itemView.setOnClickListener(v -> {
                    if (getActivity() != null) {
                        StructGroupSticker structGroupSticker = mData.get(getAdapterPosition());
                        StructIGStickerGroup stickerGroup = new StructIGStickerGroup(structGroupSticker.getId());
                        stickerGroup.setValueWithOldStruct(structGroupSticker);
                        new HelperFragment(getActivity().getSupportFragmentManager(), FragmentStickersDetail.newInstance(stickerGroup)).setReplace(false).load();
                    }
                });

                if (progressBar.getVisibility() == View.GONE)
                    addStickerBtn.setOnClickListener(v -> {
                        addStickerByApi(getAdapterPosition());

//                        if (getActivity() != null)
//                            new MaterialDialog.Builder(getActivity())
//                                    .title(getResources().getString(R.string.add_sticker))
//                                    .content(getResources().getString(R.string.add_sticker_text))
//                                    .positiveText(getString(R.string.yes))
//                                    .negativeText(getString(R.string.no))
//                                    .onPositive((dialog, which) -> addStickerByApi(getAdapterPosition())).show();
                    });

            }
        }

        public class MotionViewHolder extends RecyclerView.ViewHolder {
            private LottieAnimationView groupAvatar;
            private Button addStickerBtn;
            private TextView stickerNameTv;
            private TextView StickerCountTv;

            @SuppressLint("RtlHardcoded")
            MotionViewHolder(View itemView) {
                super(itemView);
                groupAvatar = itemView.findViewById(R.id.iv_itemAddSticker_stickerAvatar);
                addStickerBtn = itemView.findViewById(R.id.btn_itemAddSticker);
                stickerNameTv = itemView.findViewById(R.id.tv_itemAddSticker_stickerName);
                StickerCountTv = itemView.findViewById(R.id.tv_itemAddSticker_stickerCount);

                stickerNameTv.setGravity(G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT);
                StickerCountTv.setGravity(G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT);

                groupAvatar.setFailureListener(result -> Log.e(getClass().getName(), "onResult: ", result));

                itemView.setOnClickListener(v -> {
                    if (getActivity() != null) {
                        StructGroupSticker structGroupSticker = mData.get(getAdapterPosition());
                        StructIGStickerGroup stickerGroup = new StructIGStickerGroup(structGroupSticker.getId());
                        stickerGroup.setValueWithOldStruct(structGroupSticker);
                        new HelperFragment(getActivity().getSupportFragmentManager(), FragmentStickersDetail.newInstance(stickerGroup)).setReplace(false).load();
                    }
                });

                if (progressBar.getVisibility() == View.GONE)
                    addStickerBtn.setOnClickListener(v -> {
                        addStickerByApi(getAdapterPosition());

//                        if (getActivity() != null)
//                            new MaterialDialog.Builder(getActivity())
//                                    .title(getResources().getString(R.string.add_sticker))
//                                    .content(getResources().getString(R.string.add_sticker_text))
//                                    .positiveText(getString(R.string.yes))
//                                    .negativeText(getString(R.string.no))
//                                    .onPositive((dialog, which) -> addStickerByApi(getAdapterPosition())).show();
                    });

            }
        }

        private void addStickerByApi(int pos) {

            if (mData.size() > pos) {

                progressBar.setVisibility(View.VISIBLE);
                StructGroupSticker item = mData.get(pos);
                String groupId = mData.get(pos).getId();
                mAPIService.addSticker(groupId).enqueue(new Callback<StructStickerResult>() {
                    @Override
                    public void onResponse(@NotNull Call<StructStickerResult> call, @NotNull Response<StructStickerResult> response) {
                        if (response.body() != null && response.body().isSuccess()) {
                            DbManager.getInstance().doRealmTask(realm -> {
                                realm.executeTransactionAsync(realm1 -> {
                                    RealmStickers realmStickers = RealmStickers.checkStickerExist(groupId, realm1);
                                    if (realmStickers == null) {
                                        RealmStickers.put(realm1, item.getCreatedAt(), item.getId(), item.getRefId(), item.getName(), item.getAvatarToken(), item.getAvatarSize(), item.getAvatarName(), item.getPrice(), item.getIsVip(), item.getSort(), item.getIsVip(), item.getCreatedBy(), item.getStickers(), true);
                                    } else {
                                        RealmStickers.updateFavorite(realm1, item.getId(), true);
                                    }
                                }, () -> {
                                    progressBar.setVisibility(View.GONE);
                                    mData.get(pos).setIsFavorite(true);
                                    if (FragmentChat.onUpdateSticker != null) {
                                        FragmentChat.onUpdateSticker.update();
                                    }
                                    notifyDataSetChanged();
                                });
                            });
                        } else {
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<StructStickerResult> call, @NotNull Throwable t) {
                        progressBar.setVisibility(View.GONE);

                    }
                });
            }
        }
    }
}
