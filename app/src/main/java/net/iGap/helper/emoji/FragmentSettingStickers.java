package net.iGap.helper.emoji;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vanniktech.emoji.sticker.struct.StructGroupSticker;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.fragments.FragmentChat;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.emoji.api.APIEmojiService;
import net.iGap.helper.emoji.api.ApiEmojiUtils;
import net.iGap.helper.emoji.struct.StructStickerResult;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.AndroidUtils;
import net.iGap.realm.RealmStickers;

import java.io.Serializable;
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
public class FragmentSettingStickers extends BaseFragment {

    private APIEmojiService mAPIService;
    private AdapterSettingPage adapterSettingPage;
    private List<StructGroupSticker> data;
    List<StructGroupSticker> stickerList = new ArrayList<>();
    private ProgressBar progressBar;

    public FragmentSettingStickers() {
        // Required empty public constructor
    }

    public static FragmentSettingStickers newInstance(List<StructGroupSticker> stickerList) {

        FragmentSettingStickers fragmentDetailStickers = new FragmentSettingStickers();
        Bundle bundle = new Bundle();
        bundle.putSerializable("GROUP_ID", (Serializable) stickerList);
        fragmentDetailStickers.setArguments(bundle);
        return fragmentDetailStickers;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_remove_stickers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        stickerList = data = RealmStickers.getAllStickers(true);
        progressBar = view.findViewById(R.id.progress_stricker);
        progressBar.setVisibility(View.GONE);
        if (stickerList == null) stickerList = new ArrayList<>();
        mAPIService = ApiEmojiUtils.getAPIService();
        RippleView rippleBack = (RippleView) view.findViewById(R.id.fc_sticker_ripple_txtBack);
        rippleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStackFragment();
            }
        });

        RecyclerView rcvSettingPage = view.findViewById(R.id.rcvSettingPage);
        adapterSettingPage = new AdapterSettingPage(getActivity(), stickerList);
        rcvSettingPage.setAdapter(adapterSettingPage);
        rcvSettingPage.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcvSettingPage.setHasFixedSize(true);

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
            View view = mInflater.inflate(R.layout.adapter_item_setting_stickers, parent, false);
            return new ViewHolder(view);
        }

        // binds the data to the TextView in each row
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            StructGroupSticker item = mData.get(position);

            if (!capitalCities.containsKey(item.getAvatarToken())) {
                capitalCities.put(item.getAvatarToken(), item);
            }
            if (item.getUri() == null) return;
            G.imageLoader.displayImage(AndroidUtils.suitablePath(item.getUri()), holder.imgSticker);
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
            TextView txtRemove;
            TextView txtName;
            TextView txtCount;

            ViewHolder(View itemView) {
                super(itemView);
                imgSticker = itemView.findViewById(R.id.imgSticker);
                txtRemove = itemView.findViewById(R.id.txtRemoveSticker);
                txtName = itemView.findViewById(R.id.txtName);
                txtCount = itemView.findViewById(R.id.txtCount);

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

                            progressBar.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
                            } else {
                                builder = new AlertDialog.Builder(context);
                            }
                            builder.setTitle("Remove Sticker")
                                    .setMessage("Are you sure you want to remove this stickers?")
                                    .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            mAPIService.removeSticker(mData.get(getAdapterPosition()).getId()).enqueue(new Callback<StructStickerResult>() {
                                                @Override
                                                public void onResponse(Call<StructStickerResult> call, Response<StructStickerResult> response) {

                                                    progressBar.setVisibility(View.GONE);
                                                    if (response.body() != null && response.body().isSuccess()) {
                                                        Log.i("CCCCCC", "onResponse: " + response.body().isSuccess());
                                                        RealmStickers.updateFavorite(mData.get(getAdapterPosition()).getId(), false);
                                                        mData.remove(getAdapterPosition());
                                                        updateAdapter();
                                                        FragmentChat.onUpdateSticker.update();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<StructStickerResult> call, Throwable t) {
                                                    progressBar.setVisibility(View.GONE);

                                                    Log.i("CCCCCC", "onFailure: " + t.getMessage());
                                                }
                                            });
                                        }
                                    })
                                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }
                    });
            }
        }
    }


}
