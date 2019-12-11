package net.iGap.fragments.emoji.add;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vanniktech.emoji.sticker.struct.StructGroupSticker;
import com.vanniktech.emoji.sticker.struct.StructItemSticker;

import net.iGap.DbManager;
import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentChat;
import net.iGap.fragments.emoji.HelperDownloadSticker;
import net.iGap.fragments.emoji.api.APIEmojiService;
import net.iGap.fragments.emoji.api.ApiEmojiUtils;
import net.iGap.fragments.emoji.struct.StructEachSticker;
import net.iGap.fragments.emoji.struct.StructStickerResult;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.realm.RealmStickers;
import net.iGap.request.RequestFileDownload;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DialogAddSticker extends DialogFragment {
    private ProgressBar progressBar;
    private AdapterAddDialogSticker mAdapterAddDialogSticker;
    private APIEmojiService mAPIService;
    private String groupId = "";
    private String token = "";
    private List<StructGroupSticker> data = new ArrayList<>();

    public DialogAddSticker newInstance(String groupId, String token) {
        DialogAddSticker dialogAddSticker = new DialogAddSticker();
        Bundle args = new Bundle();
        args.putString("GROUP_ID", groupId);
        args.putString("TOKEN", token);
        dialogAddSticker.setArguments(args);

        return dialogAddSticker;
    }

    private void getSticker(String groupID) {
        DbManager.getInstance().doRealmTask(realm -> {
            RealmStickers realmStickers = RealmStickers.checkStickerExist(groupId, realm);
            if (realmStickers == null) {
                mAPIService.getSticker(groupID).enqueue(new Callback<StructEachSticker>() {
                    @Override
                    public void onResponse(Call<StructEachSticker> call, Response<StructEachSticker> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.body() != null) {

                            if (response.body().getOk() && response.body().getData() != null) {

                                StructGroupSticker item = response.body().getData();
                                DbManager.getInstance().doRealmTask(realm -> {
                                    realm.executeTransaction(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            RealmStickers.put(realm, item.getCreatedAt(), item.getId(), item.getRefId(), item.getName(), item.getAvatarToken(), item.getAvatarSize(), item.getAvatarName(), item.getPrice(), item.getIsVip(), item.getSort(), item.getIsVip(), item.getCreatedBy(), item.getStickers(), false);
                                        }
                                    });
                                });
                                mAdapterAddDialogSticker.updateAdapter(item.getStickers());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<StructEachSticker> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            } else {
                progressBar.setVisibility(View.GONE);
                StructGroupSticker eachSticker = RealmStickers.getEachSticker(groupID);
                if (eachSticker != null) {
                    mAdapterAddDialogSticker.updateAdapter(eachSticker.getStickers());
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_add_sticker, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            groupId = getArguments().getString("GROUP_ID");
            token = getArguments().getString("TOKEN");
        }

        HelperToolbar toolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLeftIcon(R.string.back_icon)
                .setRightIcons(R.string.add_icon_without_circle_font)
                .setLogoShown(true)
                .setDefaultTitle(getString(R.string.add_sticker))
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        getDialog().dismiss();
                    }

                    @Override
                    public void onRightIconClickListener(View view) {
                        progressBar.setVisibility(View.VISIBLE);
                        mAPIService.addSticker(groupId).enqueue(new Callback<StructStickerResult>() {
                            @Override
                            public void onResponse(Call<StructStickerResult> call, Response<StructStickerResult> response) {

                                if (response.body() != null && response.body().isSuccess()) {
                                    DbManager.getInstance().doRealmTask(realm -> {
                                        realm.executeTransactionAsync(new Realm.Transaction() {
                                            @Override
                                            public void execute(Realm realm) {
                                                RealmStickers.updateFavorite(realm, groupId, true);
                                            }
                                        }, () -> {
                                            progressBar.setVisibility(View.GONE);
                                            if (FragmentChat.onUpdateSticker != null) {
                                                FragmentChat.onUpdateSticker.update();
                                                DialogAddSticker.this.dismiss();

                                                HelperError.showSnackMessage(G.context.getResources().getString(R.string.Sticker_added_successfully), false);
                                            }
                                        });
                                    });

                                } else {
                                    progressBar.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onFailure(Call<StructStickerResult> call, Throwable t) {
                                progressBar.setVisibility(View.GONE);

                            }
                        });
                    }
                });

        ViewGroup layoutToolbar = view.findViewById(R.id.add_sticker_toolbar);
        layoutToolbar.addView(toolbar.getView());

        mAPIService = ApiEmojiUtils.getAPIService();
        progressBar = view.findViewById(R.id.progress_stricker);
        RecyclerView rcvAdapter = view.findViewById(R.id.rcvAddStickerDialog);
        mAdapterAddDialogSticker = new AdapterAddDialogSticker(getContext(), new ArrayList<>());
        rcvAdapter.setAdapter(mAdapterAddDialogSticker);
        rcvAdapter.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        rcvAdapter.setHasFixedSize(true);
        getSticker(groupId);

    }

    public class AdapterAddDialogSticker extends RecyclerView.Adapter<AdapterAddDialogSticker.ViewHolder> {
        private List<StructItemSticker> mData;
        private Context context;
        private LayoutInflater mInflater;


        // data is passed into the constructor
        AdapterAddDialogSticker(Context context, List<StructItemSticker> data) {
            this.mData = data;
            this.context = context;
            this.mInflater = LayoutInflater.from(context);
        }

        // inflates the row layout from xml when needed
        @Override
        public AdapterAddDialogSticker.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.adapter_item_detail_stickers, parent, false);
            return new ViewHolder(view);
        }

        // binds the data to the TextView in each row
        @Override
        public void onBindViewHolder(AdapterAddDialogSticker.ViewHolder holder, int position) {
            StructItemSticker item = mData.get(position);

            String path = HelperDownloadSticker.createPathFile(item.getToken(), item.getAvatarName());
            if (!new File(path).exists()) {
                HelperDownloadSticker.stickerDownload(item.getToken(), item.getName(), item.getAvatarSize(), ProtoFileDownload.FileDownload.Selector.FILE, RequestFileDownload.TypeDownload.STICKER, new HelperDownloadSticker.UpdateStickerListener() {
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
        }

        // total number of rows
        @Override
        public int getItemCount() {
            return mData.size();
        }


        public void updateAdapter(List<StructItemSticker> data) {
            this.mData = data;
            notifyDataSetChanged();
        }

        // stores and recycles views as they are scrolled off screen
        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imgSticker;


            ViewHolder(View itemView) {
                super(itemView);
                imgSticker = itemView.findViewById(R.id.imgSticker);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        }
    }


}