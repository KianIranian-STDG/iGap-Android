package net.iGap.fragments.emoji.remove;


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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.vanniktech.emoji.sticker.struct.StructGroupSticker;

import net.iGap.DbManager;
import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.fragments.FragmentChat;
import net.iGap.fragments.emoji.FragmentDetailStickers;
import net.iGap.fragments.emoji.HelperDownloadSticker;
import net.iGap.fragments.emoji.api.APIEmojiService;
import net.iGap.fragments.emoji.api.ApiEmojiUtils;
import net.iGap.fragments.emoji.struct.StructStickerResult;
import net.iGap.helper.HelperFragment;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.realm.RealmStickers;
import net.iGap.request.RequestFileDownload;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentRemoveSticker extends BaseFragment {

    private APIEmojiService mAPIService;
    private AdapterSettingPage adapterSettingPage;
    private List<StructGroupSticker> data;
    private ProgressBar progressBar;

    public FragmentRemoveSticker() {
        // Required empty public constructor
    }


    public static FragmentRemoveSticker newInstance(List<StructGroupSticker> stickerList) {

        FragmentRemoveSticker fragmentDetailStickers = new FragmentRemoveSticker();
        Bundle bundle = new Bundle();
        bundle.putSerializable("GROUP_ID", (Serializable) stickerList);
        fragmentDetailStickers.setArguments(bundle);
        return fragmentDetailStickers;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_remove_sticker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        data = RealmStickers.getAllStickers(true);
        progressBar = view.findViewById(R.id.progress_stricker);
        progressBar.setVisibility(View.GONE);
        mAPIService = ApiEmojiUtils.getAPIService();
        RecyclerView rcvSettingPage = view.findViewById(R.id.rcvSettingPage);
        adapterSettingPage = new AdapterSettingPage(getActivity(), data);
        rcvSettingPage.setAdapter(adapterSettingPage);
        rcvSettingPage.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcvSettingPage.setHasFixedSize(true);

    }

    public class AdapterSettingPage extends RecyclerView.Adapter<AdapterSettingPage.ViewHolder> {
        private List<StructGroupSticker> mData;
        private Context context;
        private LayoutInflater mInflater;


        // data is passed into the constructor
        AdapterSettingPage(Context context, List<StructGroupSticker> data) {
            this.mData = data;
            this.context = context;
            this.mInflater = LayoutInflater.from(context);
        }

        // inflates the row layout from xml when needed
        @Override
        public AdapterSettingPage.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.adapter_item_setting_stickers, parent, false);
            return new AdapterSettingPage.ViewHolder(view);
        }

        // binds the data to the TextView in each row
        @Override
        public void onBindViewHolder(AdapterSettingPage.ViewHolder holder, int position) {
            holder.setIsRecyclable(false);
            StructGroupSticker item = mData.get(position);

            if (item.getUri() == null) return;

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

        public void updateAdapter() {
            this.mData = null;
            this.mData = RealmStickers.getAllStickers(true);
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

                        if (getActivity() == null) return;
                        int pos = getAdapterPosition() ;

                        new MaterialDialog.Builder(getActivity())
                                .title(getResources().getString(R.string.remove_sticker))
                                .content(getResources().getString(R.string.remove_sticker_text))
                                .positiveText(getString(R.string.yes))
                                .negativeText(getString(R.string.no))
                                .onPositive((dialog, which) -> removeStickerByApi(pos))
                                .show();
                    });
            }

            private void removeStickerByApi(int pos) {

                if ( mData.size() > pos) {

                    progressBar.setVisibility(View.VISIBLE);
                    mAPIService.removeSticker(mData.get(pos).getId()).enqueue(new Callback<StructStickerResult>() {
                        @Override
                        public void onResponse(Call<StructStickerResult> call, Response<StructStickerResult> response) {
                            if (response.body() != null && response.body().isSuccess()) {
                                DbManager.getInstance().doRealmTask(realm -> {
                                    realm.executeTransactionAsync(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            RealmStickers.updateFavorite(realm, mData.get(pos).getId(), false);
                                        }
                                    }, () -> {
                                        progressBar.setVisibility(View.GONE);
                                        mData.remove(pos);
                                        updateAdapter();
                                        FragmentChat.onUpdateSticker.update();
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
            }
        }
    }
}
