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
import net.iGap.helper.HelperPublicMethod;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.messenger.theme.Theme;
import net.iGap.messenger.ui.fragments.NearbyFragment;
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
    private NearbyFragment nearbyFragment;
    private String comment;
    private Marker marker;
    private boolean isCallEnable = false;
    private boolean isVideoCallEnable = false;
    private AvatarHandler avatarHandler;

    public MyInfoWindow(MapView mapView, Marker marker, long userId, boolean hasComment, NearbyFragment nearbyFragment, FragmentActivity mActivity, AvatarHandler avatarHandler) {
        super(R.layout.empty_info_map, mapView);
        this.map = mapView;
        this.marker = marker;
        this.userId = userId;
        this.hasComment = hasComment;
        this.nearbyFragment = nearbyFragment;
        this.mActivity = mActivity;
        this.avatarHandler = avatarHandler;
    }

    public MyInfoWindow(int layoutResId, MapView mapView) {
        super(layoutResId, mapView);
    }

    public void onClose() {
    }

    public void onOpen(final Object arg) {
        if (latestClickedMarker != null) {
            latestClickedMarker.setIcon(NearbyFragment.avatarMark(latestClickedUserId, NearbyFragment.MarkerColor.GRAY));
        }
        marker.setIcon(NearbyFragment.avatarMark(userId, NearbyFragment.MarkerColor.GREEN));
        latestClickedMarker = marker;
        latestClickedUserId = userId;
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
        final MaterialDialog dialog = new MaterialDialog.Builder(mActivity)
                .backgroundColor(Theme.getColor(Theme.key_popup_background))
                .customView(R.layout.map_user_info, true)
                .negativeColor(Theme.getColor(Theme.key_button_background))
                .positiveColor(Theme.getColor(Theme.key_button_background)).build();
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
        txtOpenComment.setTextColor(Theme.getColor(Theme.key_theme_color));
        final TextView txtChat = view.findViewById(R.id.txt_chat_map);
        txtChat.setTextColor(Theme.getColor(Theme.key_theme_color));
        final TextView txtCall = view.findViewById(R.id.txt_call_map);
        txtCall.setTextColor(Theme.getColor(Theme.key_theme_color));
        txtCall.setVisibility(isCallEnable ? View.VISIBLE : View.GONE);
        final TextView txtVideoCall = view.findViewById(R.id.txt_video_call_map);
        txtVideoCall.setTextColor(Theme.getColor(Theme.key_theme_color));
        txtVideoCall.setVisibility(isVideoCallEnable ? View.VISIBLE : View.GONE);
        TextView txtName = view.findViewById(R.id.txt_name_info_map);
        final TextView txtComment = view.findViewById(R.id.txt_info_comment);
        MaterialDesignTextView txt_info_back_map = view.findViewById(R.id.txt_info_back_map);
        txt_info_back_map.setTextColor(Theme.getColor(Theme.key_theme_color));
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
                    public void complete() { }
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
    }
}