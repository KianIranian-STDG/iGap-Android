package com.iGap.adapter.items.chat;

import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.iGap.G;
import com.iGap.R;
import com.iGap.fragments.FragmentMap;
import com.iGap.helper.HelperPermision;
import com.iGap.interfaces.IMessageItem;
import com.iGap.interfaces.OnGetPermission;
import com.iGap.module.AndroidUtils;
import com.iGap.module.ReserveSpaceRoundedImageView;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmRoomMessageLocation;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.nostra13.universalimageloader.core.ImageLoader;
import io.realm.Realm;
import java.io.IOException;
import java.util.List;

import static com.iGap.R.id.ac_ll_parent;

public class LocationItem extends AbstractMessage<LocationItem, LocationItem.ViewHolder> {
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    public LocationItem(ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(true, type, messageClickListener);
    }

    @Override
    public int getType() {
        return R.id.chatSubLayoutLocation;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_location;
    }

    @Override public void bindView(final ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        RealmRoomMessageLocation item = null;

        if (mMessage.forwardedFrom != null) {
            if (mMessage.forwardedFrom.getLocation() != null) {
                item = mMessage.forwardedFrom.getLocation();
            }
        } else {
            if (mMessage.location != null) {
                item = mMessage.location;
            }
        }

        if (item != null) {

            if (item.getImagePath() != null) {
                ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(item.getImagePath()), holder.imgMapPosition);

            } else {
                FragmentMap.loadImageFromPosition(item.getLocationLat(), item.getLocationLong(), new FragmentMap.OnGetPicture() {
                    @Override public void getBitmap(Bitmap bitmap) {
                        holder.imgMapPosition.setImageBitmap(bitmap);

                        final String savedPath = FragmentMap.saveBitmapToFile(bitmap);

                        Realm realm = Realm.getDefaultInstance();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override public void execute(Realm realm) {

                                if (mMessage.forwardedFrom != null) {
                                    if (mMessage.forwardedFrom.getLocation() != null) {
                                        mMessage.forwardedFrom.getLocation().setImagePath(savedPath);
                                    }
                                } else {
                                    if (mMessage.location != null) {
                                        mMessage.location.setImagePath(savedPath);
                                    }
                                }



                            }
                        });
                        realm.close();
                    }
                });
            }

            final RealmRoomMessageLocation finalItem = item;
            holder.imgMapPosition.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {

                    try {
                        HelperPermision.getLocationPermission(G.currentActivity, new OnGetPermission() {
                            @Override public void Allow() {

                                FragmentMap fragment = FragmentMap.getInctance(finalItem.getLocationLat(), finalItem.getLocationLong(), FragmentMap.Mode.seePosition);
                                //  if (G.currentActivity instanceof FragmentActivity) {
                                // ((AppCompatActivity) mContext).getSupportFragmentManager()
                                FragmentActivity activity = (FragmentActivity) G.currentActivity;
                                activity.getSupportFragmentManager()
                                    .beginTransaction()
                                    .addToBackStack(null)
                                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left)
                                    .replace(ac_ll_parent, fragment, FragmentMap.flagFragmentMap)
                                    .commit();
                                //   }
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }







    }

    @Override
    protected void updateLayoutForReceive(ViewHolder holder) {
        super.updateLayoutForReceive(holder);

    }

    @Override
    protected void updateLayoutForSend(ViewHolder holder) {
        super.updateLayoutForSend(holder);

    }

    @Override void OnDownLoadFileFinish(ViewHolder holder, String path) {

    }

    @Override
    protected void voteAction(ViewHolder holder) {
        super.voteAction(holder);
    }

    @Override
    public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }

    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        ReserveSpaceRoundedImageView imgMapPosition;

        public ViewHolder(View view) {
            super(view);

            imgMapPosition = (ReserveSpaceRoundedImageView) view.findViewById(R.id.thumbnail);

            imgMapPosition.reserveSpace(G.context.getResources().getDimension(R.dimen.dp240), G.context.getResources().getDimension(R.dimen.dp120));

        }
    }
}
