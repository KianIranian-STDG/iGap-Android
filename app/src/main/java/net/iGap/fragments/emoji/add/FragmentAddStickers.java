package net.iGap.fragments.emoji.add;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.vanniktech.emoji.sticker.struct.StructGroupSticker;
import com.vanniktech.emoji.sticker.struct.StructSticker;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.fragments.FragmentChat;
import net.iGap.fragments.emoji.FragmentDetailStickers;
import net.iGap.fragments.emoji.HelperDownloadSticker;
import net.iGap.fragments.emoji.api.APIEmojiService;
import net.iGap.fragments.emoji.api.ApiEmojiUtils;
import net.iGap.fragments.emoji.struct.StickerCategory;
import net.iGap.fragments.emoji.struct.StructStickerResult;
import net.iGap.helper.HelperFragment;
import net.iGap.module.EndlessRecyclerViewScrollListener;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.realm.RealmStickers;
import net.iGap.request.RequestFileDownload;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentAddStickers extends BaseFragment {

    private APIEmojiService mAPIService;
    private AdapterSettingPage adapterSettingPage;
    private List<StructGroupSticker> data;
    private ProgressBar progressBar;
    private EndlessRecyclerViewScrollListener scrollListener;
    private int page = 0;
    private int limit = 20;
    StickerCategory stickerCategory;

    public FragmentAddStickers() {
        // Required empty public constructor
    }

    public static FragmentAddStickers newInstance(String tabJson) {

        FragmentAddStickers fragmentAddStickers = new FragmentAddStickers();
        Bundle bundle = new Bundle();
        bundle.putString("tab", tabJson);
        fragmentAddStickers.setArguments(bundle);
        return fragmentAddStickers;
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
        adapterSettingPage = new AdapterSettingPage(getActivity(), new ArrayList<>());
        rcvSettingPage.setAdapter(adapterSettingPage);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rcvSettingPage.setLayoutManager(layoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
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
            public void onResponse(Call<StructSticker> call, Response<StructSticker> response) {
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
            public void onFailure(Call<StructSticker> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    public class AdapterSettingPage extends RecyclerView.Adapter<AdapterSettingPage.ViewHolder> {
        private List<StructGroupSticker> mData;
        private Context context;
        private LayoutInflater mInflater;
        HashMap<String, StructGroupSticker> capitalCities = new HashMap<String, StructGroupSticker>();


        // data is passed into the constructor
        AdapterSettingPage(Context context, List<StructGroupSticker> data) {
            this.mData = data;
            this.context = context;
            this.mInflater = LayoutInflater.from(context);
        }

        // inflates the row layout from xml when needed
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.adapter_item_stickers, parent, false);
            return new ViewHolder(view);
        }

        // binds the data to the TextView in each row
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.setIsRecyclable(false);

            StructGroupSticker item = mData.get(position);

            if (!capitalCities.containsKey(item.getAvatarToken())) {
                capitalCities.put(item.getAvatarToken(), item);
            }
            try (Realm realm = Realm.getDefaultInstance()) {
                RealmStickers realmStickers = RealmStickers.checkStickerExist(item.getId(), realm);
                if (realmStickers == null) {
                    holder.txtRemove.setVisibility(View.VISIBLE);
                } else if (realmStickers.isFavorite()) {
                    holder.txtRemove.setVisibility(View.GONE);
                }
            }
            String path = HelperDownloadSticker.createPathFile(item.getAvatarToken(), item.getAvatarName());
            if (!new File(path).exists()) {
                HelperDownloadSticker.stickerDownload(item.getAvatarToken(), item.getName(), item.getAvatarSize(), ProtoFileDownload.FileDownload.Selector.FILE, RequestFileDownload.TypeDownload.STICKER, new HelperDownloadSticker.UpdateStickerListener() {

                    @Override
                    public void OnProgress(String path, String token, int progress) {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (context != null && !((Activity) context).isFinishing() && isAdded()) {
                                    Glide.with(context)
                                            .load(path)
                                            .into(holder.imgSticker);
                                }
                            }
                        });
                    }

                    @Override
                    public void OnError(String token) {

                    }
                });
            } else {
                Glide.with(context)
                        .load(path)
                        .into(holder.imgSticker);
            }


            holder.txtName.setText(item.getName());
            holder.txtCount.setText(item.getStickers().size() + " " + "Stickers");
        }

        // total number of rows
        @Override
        public int getItemCount() {
            return mData.size();
        }

        public void addData(List<StructGroupSticker> data) {
            this.mData.addAll(data);
            notifyDataSetChanged();
        }

        // stores and recycles views as they are scrolled off screen
        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imgSticker;
            Button txtRemove;
            TextView txtName;
            TextView txtCount;

            ViewHolder(View itemView) {
                super(itemView);
                imgSticker = itemView.findViewById(R.id.imgSticker);
                txtRemove = itemView.findViewById(R.id.txtRemoveSticker);
                txtName = itemView.findViewById(R.id.txtName);
                txtCount = itemView.findViewById(R.id.txtCount);

                //GradientDrawable backgroundGradient = (GradientDrawable) txtRemove.getBackground();
                //backgroundGradient.setColor(Color.parseColor(G.appBarColor));

                itemView.setOnClickListener(v -> {
                    if (getActivity() != null) {
                        new HelperFragment(getActivity().getSupportFragmentManager(), FragmentDetailStickers.newInstance(mData.get(getAdapterPosition()).getStickers())).setReplace(false).load();
                    }
                });

                if (progressBar.getVisibility() == View.GONE)

                    txtRemove.setOnClickListener(v -> {

                        int pos = getAdapterPosition();

                        if (getActivity() == null) return;
                        new MaterialDialog.Builder(getActivity())
                                .title(getResources().getString(R.string.add_sticker))
                                .content(getResources().getString(R.string.add_sticker_text))
                                .positiveText(getString(R.string.yes))
                                .negativeText(getString(R.string.no))
                                .onPositive((dialog, which) -> addStickerByApi(pos)).show();
                    });

            }

            private void addStickerByApi(int pos){

                if (mData.size() > pos) {

                    progressBar.setVisibility(View.VISIBLE);
                    StructGroupSticker item = mData.get(pos);
                    String groupId = mData.get(pos).getId();
                    mAPIService.addSticker(groupId).enqueue(new Callback<StructStickerResult>() {
                        @Override
                        public void onResponse(Call<StructStickerResult> call, Response<StructStickerResult> response) {
                            if (response.body() != null && response.body().isSuccess()) {
                                try (Realm realm = Realm.getDefaultInstance()) {
                                    RealmStickers realmStickers = RealmStickers.checkStickerExist(groupId, realm);
                                    if (realmStickers == null) {
                                        realm.executeTransaction(new Realm.Transaction() {
                                            @Override
                                            public void execute(Realm realm) {
                                                RealmStickers.put(realm, item.getCreatedAt(), item.getId(), item.getRefId(), item.getName(), item.getAvatarToken(), item.getAvatarSize(), item.getAvatarName(), item.getPrice(), item.getIsVip(), item.getSort(), item.getIsVip(), item.getCreatedBy(), item.getStickers(), true);
                                            }
                                        });

                                    } else {
                                        RealmStickers.updateFavorite(item.getId(), true);
                                    }

                                }
                            }

                            if (getActivity() == null || getActivity().isFinishing() || !isAdded()) {
                                return;
                            }

                            progressBar.setVisibility(View.GONE);
                            if (response.body() != null && response.body().isSuccess()) {
                                mData.get(pos).setIsFavorite(true);
                                if (FragmentChat.onUpdateSticker != null) {
                                    FragmentChat.onUpdateSticker.update();
                                }
                                notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onFailure(Call<StructStickerResult> call, Throwable t) {
                            progressBar.setVisibility(View.GONE);

                        }
                    });

                }
            }
        }
    }
}
