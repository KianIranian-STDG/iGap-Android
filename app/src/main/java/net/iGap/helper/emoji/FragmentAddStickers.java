package net.iGap.helper.emoji;


import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
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

import com.squareup.picasso.Picasso;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.fragments.FragmentChooseCountry;
import net.iGap.helper.HelperDownloadFile;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.emoji.api.APIEmojiService;
import net.iGap.helper.emoji.api.ApiEmojiUtils;
import net.iGap.helper.emoji.struct.StructGroupSticker;
import net.iGap.helper.emoji.struct.StructSticker;
import net.iGap.interfaces.OnDownload;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.AndroidUtils;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.realm.RealmStickers;

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
        rcvSettingPage.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcvSettingPage.setHasFixedSize(true);

    }

    private void getDataStickers() {

        mAPIService = ApiEmojiUtils.getAPIService();
        mAPIService.getAllSticker().enqueue(new Callback<StructSticker>() {
            @Override
            public void onResponse(Call<StructSticker> call, Response<StructSticker> response) {
                progressBar.setVisibility(View.GONE);
                data = response.body().getData();

                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        for (StructGroupSticker item : data){
                            RealmStickers.put(item.getCreatedAt(),item.getId() , item.getRefId(),item.getName(),item.getAvatarToken(),item.getAvatarSize(),item.getAvatarName(),item.getPrice(),item.getIsVip(),item.getSort(),item.getIsVip() ,item.getCreatedBy() , item.getStickers());
                        }
                    }
                });

                realm.close();

                G.handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("CCCCCCCCCC", "list final: " + RealmStickers.getStructGroupSticker().size());

                    }
                },2000);



                adapterSettingPage.updateAdapter(RealmStickers.getStructGroupSticker());
            }

            @Override
            public void onFailure(Call<StructSticker> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.i("CCCCCC", "error message url: " + t.getMessage());
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
            StructGroupSticker item = mData.get(position);

            if (!capitalCities.containsKey(item.getAvatarToken())) {
                capitalCities.put(item.getAvatarToken(), item);
            }
//            Glide.with(context)
//                    .load(new File(item.getUri())) // Uri of the picture
//                    .into(holder.imgSticker);
            G.imageLoader.displayImage(AndroidUtils.suitablePath(item.getUri()) ,holder.imgSticker);
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

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new HelperFragment(FragmentDetailStickers.newInstance(mData.get(getAdapterPosition()).getStickers())).setReplace(false).load();
                    }
                });
                txtRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            builder = new AlertDialog.Builder(context);
                        }
                        builder.setTitle("Add Sticker")
                                .setMessage("Are you sure you want to install this stickers?")
                                .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        mAPIService.addSticker(itemView.getId()).enqueue(new Callback<String>() {
                                            @Override
                                            public void onResponse(Call<String> call, Response<String> response) {
                                                Log.i("CCCCCC", "error message url: " + response.body());
                                            }

                                            @Override
                                            public void onFailure(Call<String> call, Throwable t) {
                                                Log.i("CCCCCC", "error message url: " + t.getMessage());

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
