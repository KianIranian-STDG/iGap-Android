package net.iGap.helper.emoji;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vanniktech.emoji.sticker.struct.StructGroupSticker;
import com.vanniktech.emoji.sticker.struct.StructItemSticker;

import net.iGap.R;
import net.iGap.fragments.FragmentChat;
import net.iGap.helper.emoji.api.APIEmojiService;
import net.iGap.helper.emoji.api.ApiEmojiUtils;
import net.iGap.helper.emoji.struct.StructEachSticker;
import net.iGap.helper.emoji.struct.StructStickerResult;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.realm.RealmStickers;

import java.util.ArrayList;
import java.util.List;

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


        RealmStickers realmStickers = RealmStickers.checkStickerExist(groupId);
        if (realmStickers == null) {
            mAPIService.getSticker(groupID).enqueue(new Callback<StructEachSticker>() {
                @Override
                public void onResponse(Call<StructEachSticker> call, Response<StructEachSticker> response) {
                    progressBar.setVisibility(View.GONE);
                    if (response.body() != null) {
                        if (response.body().getOk()) {
                            StructGroupSticker item = response.body().getData();
                            RealmStickers.put(item.getCreatedAt(), item.getId(), item.getRefId(), item.getName(), item.getAvatarToken(), item.getAvatarSize(), item.getAvatarName(), item.getPrice(), item.getIsVip(), item.getSort(), item.getIsVip(), item.getCreatedBy(), item.getStickers(), false);
                            mAdapterAddDialogSticker.updateAdapter(item.getStickers());
                        }
                    }
                }

                @Override
                public void onFailure(Call<StructEachSticker> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                }
            });
        }else {
            progressBar.setVisibility(View.GONE);
            StructGroupSticker eachSticker = RealmStickers.getEachSticker(groupID);
            if (eachSticker != null) {
                mAdapterAddDialogSticker.updateAdapter(eachSticker.getStickers());
            }
        }


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

        mAPIService = ApiEmojiUtils.getAPIService();
        progressBar = view.findViewById(R.id.progress_stricker);
        RecyclerView rcvAdapter = view.findViewById(R.id.rcvAddStickerDialog);
        mAdapterAddDialogSticker = new AdapterAddDialogSticker(getContext(), new ArrayList<>());
        rcvAdapter.setAdapter(mAdapterAddDialogSticker);
        rcvAdapter.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        rcvAdapter.setHasFixedSize(true);
        getSticker(groupId);
        RippleView btnBack = view.findViewById(R.id.fc_sticker_ripple_txtBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        TextView txtAddSticker = view.findViewById(R.id.txtAddSticker);
        txtAddSticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                mAPIService.addSticker(groupId).enqueue(new Callback<StructStickerResult>() {
                    @Override
                    public void onResponse(Call<StructStickerResult> call, Response<StructStickerResult> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.body() != null && response.body().isSuccess()) {
                            RealmStickers.updateFavorite(groupId, true);
                            if (FragmentChat.onUpdateSticker != null) {
                                    FragmentChat.onUpdateSticker.update();
                                    getDialog().dismiss();
                                }
                        }
                    }

                    @Override
                    public void onFailure(Call<StructStickerResult> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);

                    }
                });
            }
        });

    }

//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle("Add sticker");
//        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dismiss();
//            }
//        });
//
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dismiss();
//            }
//        });
//        return builder.create();
//    }

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