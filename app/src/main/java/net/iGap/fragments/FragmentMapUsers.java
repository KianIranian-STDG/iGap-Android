package net.iGap.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;
import java.util.HashMap;
import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityContactsProfile;
import net.iGap.activities.ActivityMain;
import net.iGap.helper.HelperAvatar;
import net.iGap.helper.HelperCalander;
import net.iGap.interfaces.OnAvatarGet;
import net.iGap.module.AndroidUtils;
import net.iGap.module.CircleImageView;
import net.iGap.module.CustomTextViewMedium;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.realm.RealmGeoNearbyDistance;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRegisteredInfoFields;
import net.iGap.request.RequestGeoGetComment;
import net.iGap.request.RequestGeoGetNearbyDistance;

import static net.iGap.G.context;
import static net.iGap.G.inflater;
import static net.iGap.fragments.FragmentiGapMap.btnBack;
import static net.iGap.fragments.FragmentiGapMap.isBackPress;

public class FragmentMapUsers extends Fragment implements ActivityMain.OnBackPressedListener {

    private FragmentActivity mActivity;
    private RecyclerView mRecyclerView;
    private MapUserAdapter mAdapter;
    private HashMap<Long, CircleImageView> hashMapAvatar = new HashMap<>();

    public FragmentMapUsers() {
        // Required empty public constructor
    }

    public static FragmentMapUsers newInstance() {
        return new FragmentMapUsers();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map_users, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initComponent(view);
        if (FragmentiGapMap.location != null) {
            new RequestGeoGetNearbyDistance().getNearbyDistance(FragmentiGapMap.location.getLatitude(), FragmentiGapMap.location.getLongitude());
        }
    }

    private void initComponent(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rcy_map_user);
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(RealmGeoNearbyDistance.class).findAll().deleteAllFromRealm();
            }
        });
        mAdapter = new MapUserAdapter(realm.where(RealmGeoNearbyDistance.class).findAll(), true);
        mRecyclerView.setAdapter(mAdapter);
        realm.close();
        ((ActivityMain) mActivity).setOnBackPressedListener(FragmentMapUsers.this, false);
        ViewGroup mapContainer = (ViewGroup) view.findViewById(R.id.rooFragmentUserMap);
        mapContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void doBack() {
        isBackPress = true;
        if (btnBack != null) btnBack.performClick();
    }

    private class MapUserAdapter extends RealmRecyclerViewAdapter<RealmGeoNearbyDistance, MapUserAdapter.ViewHolder> {
        MapUserAdapter(RealmResults<RealmGeoNearbyDistance> data, boolean autoUpdate) {
            super(data, autoUpdate);
        }

        @Override
        public MapUserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MapUserAdapter.ViewHolder(inflater.inflate(R.layout.map_user_item, parent, false));
        }

        @Override
        public void onBindViewHolder(final MapUserAdapter.ViewHolder holder, int i) {
            final RealmGeoNearbyDistance item = getItem(i);
            if (item == null) {
                return;
            }
            Realm realm = Realm.getDefaultInstance();
            RealmRegisteredInfo registeredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, item.getUserId()).findFirst();
            if (registeredInfo == null) {
                realm.close();
                return;
            }

            if (G.selectedLanguage.equals("en")) {
                holder.arrow.setText(G.context.getResources().getString(R.string.md_right_arrow));
            } else {
                holder.arrow.setText(G.context.getResources().getString(R.string.md_back_arrow));
            }

            holder.arrow.setTextColor(Color.parseColor(G.appBarColor));

            holder.arrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(G.currentActivity, ActivityContactsProfile.class);
                    intent.putExtra("peerId", item.getUserId());
                    intent.putExtra("enterFrom", "Others");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    G.currentActivity.startActivity(intent);

                    if (G.onMapClose != null) {
                        G.onMapClose.onClose();
                    }
                }
            });

            holder.username.setText(registeredInfo.getDisplayName());
            if (item.isHasComment()) {
                if (item.getComment() == null || item.getComment().isEmpty()) {
                    holder.comment.setText(context.getResources().getString(R.string.comment_waiting));
                    new RequestGeoGetComment().getComment(item.getUserId());
                } else {
                    holder.comment.setText(item.getComment());
                }
            } else {
                holder.comment.setText(context.getResources().getString(R.string.comment_no));
            }

            holder.distance.setText(String.format(G.context.getString(R.string.distance), item.getDistance()));
            if (HelperCalander.isLanguagePersian) {
                holder.distance.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.distance.getText().toString()));
            }

            hashMapAvatar.put(item.getUserId(), holder.avatar);
            HelperAvatar.getAvatar(item.getUserId(), HelperAvatar.AvatarType.USER, new OnAvatarGet() {
                @Override
                public void onAvatarGet(final String avatarPath, final long ownerId) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), hashMapAvatar.get(ownerId));
                        }
                    });
                }

                @Override
                public void onShowInitials(final String initials, final String color) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            holder.avatar.setImageBitmap(net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) holder.avatar.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
                        }
                    });
                }
            });

            realm.close();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            public CircleImageView avatar;
            public TextView username;
            public TextView comment;
            public MaterialDesignTextView arrow;
            public CustomTextViewMedium distance;

            public ViewHolder(View itemView) {
                super(itemView);

                avatar = (CircleImageView) itemView.findViewById(R.id.img_user_avatar_map);
                username = (TextView) itemView.findViewById(R.id.txt_user_name_map);
                comment = (TextView) itemView.findViewById(R.id.txt_user_comment_map);
                arrow = (MaterialDesignTextView) itemView.findViewById(R.id.txt_arrow_list_map);
                distance = (CustomTextViewMedium) itemView.findViewById(R.id.txt_user_distance_map);

            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (FragmentActivity) activity;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((ActivityMain) mActivity).setOnBackPressedListener(FragmentMapUsers.this, true);
    }
}
