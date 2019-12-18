package net.iGap.fragments.emoji.add;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.vanniktech.emoji.sticker.struct.StructGroupSticker;

import net.iGap.DbManager;
import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.cells.NormalStickerCell;
import net.iGap.fragments.emoji.HelperDownloadSticker;
import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.downloadFile.IGDownloadFile;
import net.iGap.helper.downloadFile.IGDownloadFileStruct;
import net.iGap.realm.RealmStickers;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AddStickerFragmentAdapter extends RecyclerView.Adapter {
    private List<StructGroupSticker> mData = new ArrayList<>();
    private AddStickerAdapterListener listener;

    public void setListener(AddStickerAdapterListener listener) {
        this.listener = listener;
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        if (viewType == StructIGSticker.ANIMATED_STICKER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_motion_sticker, parent, false);
            holder = new MotionViewHolder(view);
        } else {
            NormalStickerCell view = new NormalStickerCell(parent.getContext());
            holder = new ViewHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        StructGroupSticker item = mData.get(position);

        item.setUri(HelperDownloadSticker.downloadStickerPath(item.getAvatarToken(), item.getAvatarName()));


        DbManager.getInstance().doRealmTask(realm -> {
            RealmStickers realmStickers = RealmStickers.checkStickerExist(item.getId(), realm);
            if (realmStickers == null) {
                item.setIsFavorite(false);
            } else if (realmStickers.isFavorite()) {
                item.setIsFavorite(true);
            }
        });

        if (holder instanceof MotionViewHolder) {
//            MotionViewHolder motionViewHolder = (MotionViewHolder) holder;
//
//            String path = HelperDownloadSticker.downloadStickerPath(item.getAvatarToken(), item.getAvatarName());
//            if (!new File(path).exists()) {
//                EventManager.getInstance().addEventListener(EventManager.STICKER_DOWNLOAD, (id, message) -> {
//                    String filePath = (String) message[0];
//                    String fileToken = (String) message[1];
//
//                    G.handler.post(() -> {
//                        if (item.getAvatarToken().equals(fileToken)) {
//                            try {
//                                motionViewHolder.groupAvatar.setAnimation(new FileInputStream(filePath), fileToken);
//                            } catch (FileNotFoundException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//                });
//
//                IGDownloadFile.getInstance().startDownload(new IGDownloadFileStruct(item.getId(), item.getAvatarToken(), item.getAvatarSize(), path));
//            } else {
//                try {
//                    motionViewHolder.groupAvatar.setAnimation(new FileInputStream(path), item.getAvatarToken());
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//            }

        } else {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.bindView(item);
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

    void addData(List<StructGroupSticker> data) {
        this.mData.addAll(data);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        NormalStickerCell stickerCell;

        @SuppressLint("RtlHardcoded")
        ViewHolder(View itemView) {
            super(itemView);
            stickerCell = (NormalStickerCell) itemView;
        }

        private void bindView(StructGroupSticker sticker) {

            stickerCell.getButton().setMode(sticker.getIsFavorite() ? 0 : 1);
            stickerCell.getGroupNameTv().setText(sticker.getName());

            String size = G.selectedLanguage.equals("fa") ? HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(sticker.getStickers().size())) : String.valueOf(sticker.getStickers().size());
            stickerCell.getGroupStickerCountTv().setText(size);

            stickerCell.setOnClickListener(v -> listener.onCellClick(sticker));

            stickerCell.getButton().setOnClickListener(v -> {
                stickerCell.getButton().changeProgressTo(View.VISIBLE);
                listener.onButtonClick(sticker, isFavorite -> {
                    stickerCell.getButton().changeProgressTo(View.GONE);
                    stickerCell.getButton().setMode(isFavorite ? 0 : 1);
                });
            });

            if (new File(sticker.getUri()).exists()) {
                Glide.with(itemView.getContext()).load(sticker.getUri()).into(stickerCell.getGroupAvatarIv());
            } else {
                stickerCell.setEventListener((id, message) -> {
                    String filePath = (String) message[0];
                    String token = (String) message[1];

                    G.handler.post(() -> {
                        if (token.equals(sticker.getAvatarToken()))
                            Glide.with(itemView.getContext()).load(filePath).into(stickerCell.getGroupAvatarIv());
                    });
                });

                IGDownloadFile.getInstance().startDownload(new IGDownloadFileStruct(sticker.getId(), sticker.getAvatarToken(), sticker.getAvatarSize(), sticker.getUri()));
            }
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

//            itemView.setOnClickListener(v -> {
//                if (getActivity() != null) {
//                    StructGroupSticker structGroupSticker = mData.get(getAdapterPosition());
//                    StructIGStickerGroup stickerGroup = new StructIGStickerGroup(structGroupSticker.getId());
//                    stickerGroup.setValueWithOldStruct(structGroupSticker);
//                    new HelperFragment(getActivity().getSupportFragmentManager(), FragmentStickersDetail.getInstance(stickerGroup)).setReplace(false).load();
//                }
//            });
//
//            if (progressBar.getVisibility() == View.GONE)
//                addStickerBtn.setOnClickListener(v -> {
//                    addStickerByApi(getAdapterPosition());
//                });

        }
    }

//    private void addStickerByApi(int pos) {
//
//        if (mData.size() > pos) {
//
//            progressBar.setVisibility(View.VISIBLE);
//            StructGroupSticker item = mData.get(pos);
//            String groupId = mData.get(pos).getId();
//            mAPIService.addSticker(groupId).enqueue(new Callback<StructStickerResult>() {
//                @Override
//                public void onResponse(@NotNull Call<StructStickerResult> call, @NotNull Response<StructStickerResult> response) {
//                    if (response.body() != null && response.body().isSuccess()) {
//                        DbManager.getInstance().doRealmTask(realm -> {
//                            realm.executeTransactionAsync(realm1 -> {
//                                RealmStickers realmStickers = RealmStickers.checkStickerExist(groupId, realm1);
//                                if (realmStickers == null) {
//                                    RealmStickers.put(realm1, item.getCreatedAt(), item.getId(), item.getRefId(), item.getName(), item.getAvatarToken(), item.getAvatarSize(), item.getAvatarName(), item.getPrice(), item.getIsVip(), item.getSort(), item.getIsVip(), item.getCreatedBy(), item.getStickers(), true);
//                                } else {
//                                    RealmStickers.updateFavorite(realm1, item.getId(), true);
//                                }
//                            }, () -> {
//                                progressBar.setVisibility(View.GONE);
//                                mData.get(pos).setIsFavorite(true);
//                                if (FragmentChat.onUpdateSticker != null) {
//                                    FragmentChat.onUpdateSticker.update();
//                                }
//                                notifyDataSetChanged();
//                            });
//                        });
//                    } else {
//                        progressBar.setVisibility(View.GONE);
//                    }
//                }
//
//                @Override
//                public void onFailure(@NotNull Call<StructStickerResult> call, @NotNull Throwable t) {
//                    progressBar.setVisibility(View.GONE);
//
//                }
//            });
//        }
//    }

    public interface AddStickerAdapterListener {
        void onButtonClick(StructGroupSticker stickerGroup, ProgressStatus progressStatus);

        void onCellClick(StructGroupSticker stickerGroup);
    }

    public interface ProgressStatus {
        void setVisibility(boolean isFavorite);
    }
}
