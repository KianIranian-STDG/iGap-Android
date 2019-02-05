package net.iGap.helper.emoji;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperDownloadFile;
import net.iGap.helper.emoji.api.APIEmojiService;
import net.iGap.helper.emoji.api.ApiEmojiUtils;
import net.iGap.helper.emoji.struct.StructItemSticker;
import net.iGap.helper.emoji.struct.StructSticker;
import net.iGap.interfaces.OnDownload;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.proto.ProtoFileDownload;

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
public class FragmentDetailStickers extends BaseFragment {

    private APIEmojiService mAPIService;

    private String groupId = "";
    List<StructItemSticker> stickerList;
    private AdapterSettingPage adapterSettingPage;

    public FragmentDetailStickers() {
        // Required empty public constructor
    }


    public static FragmentDetailStickers newInstance(List<StructItemSticker> idGroup) {

        FragmentDetailStickers fragmentDetailStickers = new FragmentDetailStickers();
        Bundle bundle = new Bundle();
        bundle.putSerializable("GROUP_ID", (Serializable) idGroup);
        fragmentDetailStickers.setArguments(bundle);
        return fragmentDetailStickers;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_stickers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        if (getArguments() != null) {
            stickerList = (List<StructItemSticker>) getArguments().getSerializable("GROUP_ID");
        }

//        if (!groupId.isEmpty()) {
//            getDataStickers();
//        }


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
        rcvSettingPage.setLayoutManager(new GridLayoutManager(getActivity(), 3));
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
            View view = mInflater.inflate(R.layout.adapter_item_detail_stickers, parent, false);
            return new FragmentDetailStickers.AdapterSettingPage.ViewHolder(view);
        }

        // binds the data to the TextView in each row
        @Override
        public void onBindViewHolder(AdapterSettingPage.ViewHolder holder, int position) {
            StructItemSticker item = mData.get(position);

            Uri uri = Uri.parse(item.getUri());
            holder.imgSticker.setImageURI(uri);
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
