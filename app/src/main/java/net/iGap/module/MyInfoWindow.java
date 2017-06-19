package net.iGap.module;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import io.realm.Realm;
import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperAvatar;
import net.iGap.interfaces.OnAvatarGet;
import net.iGap.interfaces.OnGeoGetComment;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRegisteredInfoFields;
import net.iGap.request.RequestGeoGetComment;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

public class MyInfoWindow extends InfoWindow implements OnGeoGetComment {

    private long userId;
    private boolean hasComment;
    private TextView txtComment;
    private MapView map;

    public MyInfoWindow(int layoutResId, MapView mapView, long userId, boolean hasComment) {
        super(layoutResId, mapView);
        this.map = mapView;
        this.userId = userId;
        this.hasComment = hasComment;
        G.onGeoGetComment = this;
    }

    public MyInfoWindow(int layoutResId, MapView mapView) {
        super(layoutResId, mapView);
    }

    public void onClose() {
    }

    public void onOpen(Object arg0) {
        Realm realm = Realm.getDefaultInstance();
        RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, userId).findFirst();
        if (realmRegisteredInfo == null) {
            return;
        }
        LinearLayout lytMapInfo = (LinearLayout) mView.findViewById(R.id.lyt_map_info);
        txtComment = (TextView) mView.findViewById(R.id.txt_map_comment);
        TextView txtMapName = (TextView) mView.findViewById(R.id.txt_map_name);
        TextView txtMapStatus = (TextView) mView.findViewById(R.id.txt_map_status);
        final CircleImageView imgMapUser = (CircleImageView) mView.findViewById(R.id.img_map_user);

        txtMapName.setText(realmRegisteredInfo.getDisplayName());
        if (realmRegisteredInfo.getStatus().equals(ProtoGlobal.RegisteredUser.Status.EXACTLY.toString())) {
            txtMapStatus.setText(LastSeenTimeUtil.computeTime(userId, realmRegisteredInfo.getLastSeen(), false));
        } else {
            txtMapStatus.setText(realmRegisteredInfo.getStatus());
        }

        HelperAvatar.getAvatar(userId, HelperAvatar.AvatarType.USER, true, realm, new OnAvatarGet() {
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

        lytMapInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InfoWindow.closeAllInfoWindowsOn(map);
            }
        });

        if (hasComment) {
            new RequestGeoGetComment().getComment(userId);
        }

        realm.close();
    }

    @Override
    public void onGetComment(final String comment) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                txtComment.setText(comment);
            }
        });
    }
}