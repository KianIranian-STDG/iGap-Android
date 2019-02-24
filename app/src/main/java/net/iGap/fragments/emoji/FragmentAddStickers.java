package net.iGap.fragments.emoji;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.vanniktech.emoji.sticker.struct.StructGroupSticker;
import com.vanniktech.emoji.sticker.struct.StructSticker;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.fragments.FragmentChat;
import net.iGap.helper.HelperFragment;
import net.iGap.fragments.emoji.api.APIEmojiService;
import net.iGap.fragments.emoji.api.ApiEmojiUtils;
import net.iGap.fragments.emoji.struct.StructStickerResult;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.AndroidUtils;
import net.iGap.module.DeviceUtils;
import net.iGap.module.EndlessRecyclerViewScrollListener;
import net.iGap.module.PreCachingLayoutManager;
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

    public FragmentAddStickers() {
        // Required empty public constructor
    }

    public static FragmentAddStickers newInstance() {

        FragmentAddStickers fragmentAddStickers = new FragmentAddStickers();
        Bundle bundle = new Bundle();
        fragmentAddStickers.setArguments(bundle);
        return fragmentAddStickers;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_stickers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.fc_layot_title).setBackgroundColor(Color.parseColor(G.appBarColor));

        getDataStickers();
        progressBar = view.findViewById(R.id.progress_stricker);
        progressBar.setVisibility(View.VISIBLE);
        RippleView rippleBack = (RippleView) view.findViewById(R.id.fc_sticker_ripple_txtBack);
        rippleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStackFragment();
            }
        });

        RecyclerView rcvSettingPage = view.findViewById(R.id.rcvSettingPage);
        adapterSettingPage = new AdapterSettingPage(getActivity(), new ArrayList<>());
        rcvSettingPage.setAdapter(adapterSettingPage);

        final PreCachingLayoutManager preCachingLayoutManager = new PreCachingLayoutManager(G.fragmentActivity, DeviceUtils.getScreenHeight(G.fragmentActivity));
        rcvSettingPage.setLayoutManager(preCachingLayoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(preCachingLayoutManager) {
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
        rcvSettingPage.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcvSettingPage.setHasFixedSize(true);

    }

    private void getDataStickers() {

        mAPIService = ApiEmojiUtils.getAPIService();

        mAPIService.getAllSticker(page, limit).enqueue(new Callback<StructSticker>() {
            @Override
            public void onResponse(Call<StructSticker> call, Response<StructSticker> response) {
                progressBar.setVisibility(View.GONE);
                if (response.body() != null) {
                    if (response.body().getOk() && response.body().getData().size() > 0) {
                        List<StructGroupSticker> data = response.body().getData();
                        adapterSettingPage.updateAdapter(data);
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

            RealmStickers realmStickers = RealmStickers.checkStickerExist(item.getId());
            if (realmStickers == null) {
                holder.txtRemove.setVisibility(View.VISIBLE);
            } else if (realmStickers.isFavorite()) {
                holder.txtRemove.setVisibility(View.GONE);
            }

            String path = HelperDownloadSticker.createPathFile(item.getAvatarToken(), item.getAvatarName());
            if (!new File(path).exists()) {
                HelperDownloadSticker.stickerDownload(item.getAvatarToken(), item.getName(), item.getAvatarSize(), ProtoFileDownload.FileDownload.Selector.FILE, RequestFileDownload.TypeDownload.STICKER, new HelperDownloadSticker.UpdateStickerListener() {

                    @Override
                    public void OnProgress(String path, String token, int progress) {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Glide.with(context)
                                        .load(path)
                                        .into(holder.imgSticker);
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

        public void updateAdapter(List<StructGroupSticker> data) {
            this.mData = data;
            notifyDataSetChanged();
        }

        // stores and recycles views as they are scrolled off screen
        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imgSticker;
            TextView txtRemove;
            TextView txtName;
            TextView txtCount;

            ViewHolder(View itemView) {
                super(itemView);
                imgSticker = itemView.findViewById(R.id.imgSticker);
                txtRemove = itemView.findViewById(R.id.txtRemoveSticker);
                txtName = itemView.findViewById(R.id.txtName);
                txtCount = itemView.findViewById(R.id.txtCount);

                GradientDrawable backgroundGradient = (GradientDrawable) txtRemove.getBackground();
                backgroundGradient.setColor(Color.parseColor(G.appBarColor));

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new HelperFragment(FragmentDetailStickers.newInstance(mData.get(getAdapterPosition()).getStickers())).setReplace(false).load();
                    }
                });
                if (progressBar.getVisibility() == View.GONE)
                    txtRemove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            new MaterialDialog.Builder(getActivity())
                                    .title(getResources().getString(R.string.add_sticker))
                                    .content(getResources().getString(R.string.add_sticker_text))
                                    .positiveText(getString(org.paygear.wallet.R.string.yes))
                                    .negativeText(getString(org.paygear.wallet.R.string.no))
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            progressBar.setVisibility(View.VISIBLE);
                                            StructGroupSticker item = mData.get(getAdapterPosition());
                                            String groupId = mData.get(getAdapterPosition()).getId();
                                            mAPIService.addSticker(groupId).enqueue(new Callback<StructStickerResult>() {
                                                @Override
                                                public void onResponse(Call<StructStickerResult> call, Response<StructStickerResult> response) {
                                                    progressBar.setVisibility(View.GONE);
                                                    if (response.body() != null && response.body().isSuccess()) {
                                                        mData.get(getAdapterPosition()).setIsFavorite(true);
                                                        RealmStickers realmStickers = RealmStickers.checkStickerExist(groupId);
                                                        if (realmStickers == null) {
                                                            Realm realm = Realm.getDefaultInstance();
                                                            realm.executeTransaction(new Realm.Transaction() {
                                                                @Override
                                                                public void execute(Realm realm) {
                                                                    RealmStickers.put(item.getCreatedAt(), item.getId(), item.getRefId(), item.getName(), item.getAvatarToken(), item.getAvatarSize(), item.getAvatarName(), item.getPrice(), item.getIsVip(), item.getSort(), item.getIsVip(), item.getCreatedBy(), item.getStickers(), true);
                                                                }
                                                            });
                                                            realm.close();

                                                        } else {
                                                            RealmStickers.updateFavorite(mData.get(getAdapterPosition()).getId(), true);
                                                        }
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
                                    }).show();
                        }
                    });
            }
        }
    }
}
