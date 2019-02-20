package net.iGap.fragments.emoji;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
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
import com.hanks.library.AnimateCheckBox;
import com.vanniktech.emoji.sticker.struct.StructGroupSticker;
import com.vanniktech.emoji.sticker.struct.StructItemSticker;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.fragments.FragmentChat;
import net.iGap.fragments.emoji.api.APIEmojiService;
import net.iGap.fragments.emoji.api.ApiEmojiUtils;
import net.iGap.fragments.emoji.struct.StructStickerResult;
import net.iGap.helper.HelperFragment;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.realm.RealmStickers;
import net.iGap.request.RequestFileDownload;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentRemoveRecentSticker extends BaseFragment {

    private APIEmojiService mAPIService;
    public AdapterSettingPage adapterSettingPage;
    private List<StructItemSticker> data;
    public List<StructItemSticker> stickerList = new ArrayList<>();
    private ProgressBar progressBar;
    public static ArrayList<String> removeStickerList=  new ArrayList<>() ;
    public FragmentRemoveRecentSticker() {
        // Required empty public constructor
    }

    public static FragmentRemoveRecentSticker newInstance(List<StructItemSticker> stickerList) {
        FragmentRemoveRecentSticker fragmentDetailStickers = new FragmentRemoveRecentSticker();
        Bundle bundle = new Bundle();
        bundle.putSerializable("RECENT", (Serializable) stickerList);
        fragmentDetailStickers.setArguments(bundle);
        return fragmentDetailStickers;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_remove_recent_sticker, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        stickerList = (List<StructItemSticker>) getArguments().getSerializable("RECENT");
        progressBar = view.findViewById(R.id.progress_stricker);
        progressBar.setVisibility(View.GONE);
        if (stickerList == null) stickerList = new ArrayList<>();
        mAPIService = ApiEmojiUtils.getAPIService();
        RecyclerView rcvSettingPage = view.findViewById(R.id.rcvSettingPage);
        adapterSettingPage = new AdapterSettingPage(getActivity(), stickerList);
        rcvSettingPage.setAdapter(adapterSettingPage);
        rcvSettingPage.setLayoutManager(new GridLayoutManager(getActivity(),4));
        rcvSettingPage.setHasFixedSize(true);

    }

    public class AdapterSettingPage extends RecyclerView.Adapter<AdapterSettingPage.ViewHolder> {
        private List<StructItemSticker> mData;
        private Context context;
        private LayoutInflater mInflater;


        // data is passed into the constructor
        AdapterSettingPage(Context context, List<StructItemSticker> data) {
            this.mData = data;
            this.context = context;
            this.mInflater = LayoutInflater.from(context);
        }

        // inflates the row layout from xml when needed
        @Override
        public AdapterSettingPage.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.adapter_remove_recently_stickers, parent, false);
            return new AdapterSettingPage.ViewHolder(view);
        }

        // binds the data to the TextView in each row
        @Override
        public void onBindViewHolder(AdapterSettingPage.ViewHolder holder, int position) {
            StructItemSticker item = mData.get(position);

            if (item == null) return;

            String path = HelperDownloadSticker.createPathFile(item.getToken(), item.getName());
            if (!new File(path).exists()) {
                HelperDownloadSticker.stickerDownload(item.getToken(), item.getName(), item.getAvatarSize(), ProtoFileDownload.FileDownload.Selector.FILE, RequestFileDownload.TypeDownload.STICKER, new HelperDownloadSticker.UpdateStickerListener() {

                    @Override
                    public void OnProgress(String path, int progress) {
                        Glide.with(context)
                                .load(path)
                                .into(holder.imgSticker);
                        notifyDataSetChanged();
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

        // stores and recycles views as they are scrolled off screen
        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imgSticker;
            AnimateCheckBox animateCheckBox;

            ViewHolder(View itemView) {
                super(itemView);
                imgSticker = itemView.findViewById(R.id.imgSticker);
                animateCheckBox = itemView.findViewById(R.id.cig_checkBox_select_user);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (animateCheckBox.isChecked()){
                            animateCheckBox.setChecked(false);
                            removeStickerList.remove(mData.get(getAdapterPosition()).getId());

                        }else {
                            animateCheckBox.setChecked(true);
                            removeStickerList.add(mData.get(getAdapterPosition()).getId());

                        }
                    }
                });
            }
        }
    }
}


