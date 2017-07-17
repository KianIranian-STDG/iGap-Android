package net.iGap.module;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;
import com.afollestad.materialdialogs.MaterialDialog;
import io.realm.Realm;
import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentiGapMap;
import net.iGap.helper.HelperAvatar;
import net.iGap.helper.HelperPublicMethod;
import net.iGap.interfaces.OnAvatarGet;
import net.iGap.interfaces.OnGeoGetComment;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRegisteredInfoFields;
import net.iGap.request.RequestGeoGetComment;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

public class MyInfoWindow extends InfoWindow {

    private long userId;
    private boolean hasComment;
    private MapView map;
    private FragmentActivity mActivity;
    private FragmentiGapMap fragmentiGapMap;

    public MyInfoWindow(int layoutResId, MapView mapView, long userId, boolean hasComment, FragmentiGapMap fragmentiGapMap, FragmentActivity mActivity) {
        super(R.layout.empty_info_map, mapView);
        this.map = mapView;
        this.userId = 449;
        this.hasComment = hasComment;
        this.fragmentiGapMap = fragmentiGapMap;
        this.mActivity = mActivity;
    }

    public MyInfoWindow(int layoutResId, MapView mapView) {
        super(layoutResId, mapView);
    }

    public void onClose() {
    }

    public void onOpen(Object arg0) {

        final MaterialDialog dialog = new MaterialDialog.Builder(mActivity).customView(R.layout.map_user_info, true).build();
        View view = dialog.getCustomView();
        if (view == null) {
            return;
        }
        DialogAnimation.animationDown(dialog);
        dialog.show();

        Realm realm = Realm.getDefaultInstance();
        RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, userId).findFirst();
        if (realmRegisteredInfo == null) {
            return;
        }

        final CircleImageView avatar = (CircleImageView) view.findViewById(R.id.img_info_avatar_map);
        TextView txtClose = (TextView) view.findViewById(R.id.txt_close_map);
        TextView txtChat = (TextView) view.findViewById(R.id.txt_chat_map);
        TextView txtCall = (TextView) view.findViewById(R.id.txt_call_map);
        TextView txtName = (TextView) view.findViewById(R.id.txt_name_info_map);
        final TextView txtComment = (TextView) view.findViewById(R.id.txt_info_comment);
        TextView txtDistance = (TextView) view.findViewById(R.id.txt_info_distance);

        txtName.setText(realmRegisteredInfo.getDisplayName());
        txtDistance.setText("Distance");


        txtClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        txtChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HelperPublicMethod.goToChatRoom(false, userId, new HelperPublicMethod.Oncomplet() {
                    @Override
                    public void complete() {
                        mActivity.getSupportFragmentManager().beginTransaction().remove(fragmentiGapMap).commit();
                    }
                }, null);
            }
        });

        txtCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO [Saeed Mozaffari] [2017-07-17 4:21 PM] - DO Actions!!!
            }
        });

        HelperAvatar.getAvatar(null, userId, HelperAvatar.AvatarType.USER, true, realm, new OnAvatarGet() {
            @Override
            public void onAvatarGet(final String avatarPath, long roomId) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), avatar);
                    }
                });
            }

            @Override
            public void onShowInitials(final String initials, final String color) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        avatar.setImageBitmap(net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) avatar.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
                    }
                });
            }
        });

        if (hasComment) {
            G.onGeoGetComment = new OnGeoGetComment() {
                @Override
                public void onGetComment(final String comment) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            txtComment.setText(comment);
                        }
                    });
                }
            };

            new RequestGeoGetComment().getComment(userId);
        }
    }

    /*public void onOpen(Object arg0) {
        Realm realm = Realm.getDefaultInstance();
        RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, userId).findFirst();
        if (realmRegisteredInfo == null) {
            return;
        }
        LinearLayout lytMapInfo = (LinearLayout) mView.findViewById(R.id.lyt_map_info);
        final TextView txtComment = (TextView) mView.findViewById(R.id.txt_map_comment);
        TextView txtMapName = (TextView) mView.findViewById(R.id.txt_map_name);
        TextView txtMapStatus = (TextView) mView.findViewById(R.id.txt_map_status);
        final CircleImageView imgMapUser = (CircleImageView) mView.findViewById(R.id.img_map_user);

        txtMapName.setText(realmRegisteredInfo.getDisplayName());
        if (realmRegisteredInfo.getStatus().equals(ProtoGlobal.RegisteredUser.Status.EXACTLY.toString())) {
            txtMapStatus.setText(LastSeenTimeUtil.computeTime(userId, realmRegisteredInfo.getLastSeen(), false));
        } else {
            txtMapStatus.setText(realmRegisteredInfo.getStatus());
        }

        HelperAvatar.getAvatar(null, userId, HelperAvatar.AvatarType.USER, true, realm, new OnAvatarGet() {
            @Override
            public void onAvatarGet(final String avatarPath, long roomId) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), imgMapUser);
                    }
                });
            }

            @Override
            public void onShowInitials(final String initials, final String color) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        imgMapUser.setImageBitmap(net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) imgMapUser.getContext().getResources().getDimension(R.dimen.dp48), initials, color));
                    }
                });
            }
        });

        imgMapUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HelperPublicMethod.goToChatRoom(false, userId, new HelperPublicMethod.Oncomplet() {
                    @Override
                    public void complete() {
                        mActivity.getSupportFragmentManager().beginTransaction().remove(fragmentiGapMap).commit();
                    }
                }, null);
            }
        });

        lytMapInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InfoWindow.closeAllInfoWindowsOn(map);
            }
        });

        if (hasComment) {
            G.onGeoGetComment = new OnGeoGetComment() {
                @Override
                public void onGetComment(final String comment) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            txtComment.setText(comment);
                        }
                    });
                }
            };

            new RequestGeoGetComment().getComment(userId);
        }

        realm.close();
    }*/

}