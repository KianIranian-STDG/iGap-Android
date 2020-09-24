package net.iGap.module;

import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.CallSelectFragment;
import net.iGap.fragments.FragmentiGapMap;
import net.iGap.helper.HelperPublicMethod;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.observers.interfaces.OnGeoGetComment;
import net.iGap.observers.interfaces.OnInfo;
import net.iGap.proto.ProtoSignalingOffer;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.request.RequestGeoGetComment;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

public class MyInfoWindow extends InfoWindow {

    private static Marker latestClickedMarker;
    private static long latestClickedUserId;
    private long userId;
    private boolean hasComment;
    private MapView map;
    private FragmentActivity mActivity;
    private FragmentiGapMap fragmentiGapMap;
    private String comment;
    private Marker marker;
    private boolean isCallEnable = false;
    private boolean isVideoCallEnable = false;
    private AvatarHandler avatarHandler;

    public MyInfoWindow(MapView mapView, Marker marker, long userId, boolean hasComment, FragmentiGapMap fragmentiGapMap, FragmentActivity mActivity, AvatarHandler avatarHandler) {
        super(R.layout.empty_info_map, mapView);
        this.map = mapView;
        this.marker = marker;
        this.userId = userId;
        this.hasComment = hasComment;
        this.fragmentiGapMap = fragmentiGapMap;
        this.mActivity = mActivity;
        this.avatarHandler = avatarHandler;
    }

    public MyInfoWindow(int layoutResId, MapView mapView) {
        super(layoutResId, mapView);
    }

    public void onClose() {
    }

    public void onOpen(final Object arg) {
        /**
         * change latest clicked user marker color to GRAY and new clicker to GREEN
         */
        if (latestClickedMarker != null) {
            latestClickedMarker.setIcon(FragmentiGapMap.avatarMark(latestClickedUserId, FragmentiGapMap.MarkerColor.GRAY));
        }
        marker.setIcon(FragmentiGapMap.avatarMark(userId, FragmentiGapMap.MarkerColor.GREEN));
        latestClickedMarker = marker;
        latestClickedUserId = userId;

        /**
         * don't show dialog for mine user
         */
        if (userId == AccountManager.getInstance().getCurrentUser().getId()) {
            return;
        }

        String displayName = DbManager.getInstance().doRealmTask(realm2 -> {
            RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realm2, userId);
            if (realmRegisteredInfo == null) {
                RealmRegisteredInfo.getRegistrationInfo(userId, new OnInfo() {
                    @Override
                    public void onInfo(Long registeredId) {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onOpen(arg);
                            }
                        });
                    }
                });
                return null;
            }
            return realmRegisteredInfo.getDisplayName();
        });
        if (displayName == null) {
            return;
        }


     /*   RealmCallConfig callConfig = realm.where(RealmCallConfig.class).findFirst();
        if (callConfig != null) {
            isCallEnable = callConfig.isVoice_calling();
            isVideoCallEnable = callConfig.isVideo_calling();
        }*/

        final MaterialDialog dialog = new MaterialDialog.Builder(mActivity).customView(R.layout.map_user_info, true).build();
        View view = dialog.getCustomView();
        if (view == null) {
            return;
        }
        DialogAnimation.animationDown(dialog);
        dialog.show();

        final CircleImageView avatar = view.findViewById(R.id.img_info_avatar_map);
        final TextView txtClose = view.findViewById(R.id.txt_close_map);
        final TextView txtBack = view.findViewById(R.id.txt_info_back_map);
        final TextView txtOpenComment = view.findViewById(R.id.txt_open_comment_map);
        final TextView txtChat = view.findViewById(R.id.txt_chat_map);
        final TextView txtCall = view.findViewById(R.id.txt_call_map);
        txtCall.setVisibility(isCallEnable ? View.VISIBLE : View.GONE);
        final TextView txtVideoCall = view.findViewById(R.id.txt_video_call_map);
        txtVideoCall.setVisibility(isVideoCallEnable ? View.VISIBLE : View.GONE);
        TextView txtName = view.findViewById(R.id.txt_name_info_map);
        final TextView txtComment = view.findViewById(R.id.txt_info_comment);

        txtName.setText(displayName);
        txtName.setTypeface(ResourcesCompat.getFont(txtName.getContext(), R.font.main_font_bold), Typeface.BOLD);

        if (!G.isAppRtl) {
            txtComment.setGravity(Gravity.RIGHT);
            txtOpenComment.setRotation(90);
        } else {
            txtComment.setGravity(Gravity.LEFT);
            txtOpenComment.setRotation(270);
        }

        txtClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtBack.setVisibility(View.GONE);
                txtClose.setVisibility(View.VISIBLE);
                txtChat.setVisibility(View.VISIBLE);
                if (isCallEnable) {
                    txtCall.setVisibility(View.VISIBLE);
                }

                if (isVideoCallEnable) {
                    txtVideoCall.setVisibility(View.VISIBLE);
                }

                txtOpenComment.setVisibility(View.VISIBLE);
                txtComment.setMaxLines(1);
                txtComment.setEllipsize(TextUtils.TruncateAt.END);
            }
        });

        txtChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                HelperPublicMethod.goToChatRoom(userId, new HelperPublicMethod.OnComplete() {
                    @Override
                    public void complete() {
                        //  new HelperFragment(mActivity.getSupportFragmentManager(),fragmentiGapMap).remove();
                    }
                }, null);
            }
        });

        txtCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CallSelectFragment.call(userId, false, ProtoSignalingOffer.SignalingOffer.Type.VOICE_CALLING);
            }
        });


        txtVideoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CallSelectFragment.call(userId, false, ProtoSignalingOffer.SignalingOffer.Type.VIDEO_CALLING);
            }
        });


        txtComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasComment && comment != null) {
                    txtClose.setVisibility(View.GONE);
                    txtChat.setVisibility(View.GONE);
                    txtCall.setVisibility(View.GONE);
                    txtVideoCall.setVisibility(View.GONE);
                    txtOpenComment.setVisibility(View.GONE);
                    txtBack.setVisibility(View.VISIBLE);
                    txtComment.setMaxLines(Integer.MAX_VALUE);
                    txtComment.setEllipsize(null);
                }
            }
        });

        txtOpenComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtComment.performClick();
            }
        });
        avatarHandler.getAvatar(new ParamWithAvatarType(avatar, userId).avatarType(AvatarHandler.AvatarType.USER).showMain());

        if (hasComment) {
            G.onGeoGetComment = new OnGeoGetComment() {
                @Override
                public void onGetComment(long userId, final String commentR) {
                    comment = commentR;
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            txtComment.setText(commentR);
                        }
                    });
                }
            };
            txtComment.setText(G.fragmentActivity.getResources().getString(R.string.comment_waiting));
            new RequestGeoGetComment().getComment(userId);
        } else {
            txtComment.setText(G.fragmentActivity.getResources().getString(R.string.comment_no));
        }

        //for show old comment
        //
        //RealmGeoNearbyDistance realmGeoNearbyDistance = realm.where(RealmGeoNearbyDistance.class).equalTo("userId", userId).findFirst();
        //if (realmGeoNearbyDistance != null && hasComment) {
        //    if (realmGeoNearbyDistance.getComment() != null && !realmGeoNearbyDistance.getComment().isEmpty()) {
        //        txtComment.setText(realmGeoNearbyDistance.getComment());
        //    }
        //}
        //
        //if (hasComment) {
        //    txtComment.setText(G.fragmentActivity.getResources().getString(R.string.comment_waiting));
        //    new RequestGeoGetComment().getComment(userId);
        //} else {
        //    txtComment.setText(G.fragmentActivity.getResources().getString(R.string.comment_no));
        //}
    }

    /*public void onOpen(Object arg0) {
    DbManager.getInstance().doRealmTask(realm-> {
     RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo("id", userId).findFirst();
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
                HelperPublicMethod.goToChatRoom(false, userId, new HelperPublicMethod.OnComplete() {
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
}


    }*/

}