package net.iGap.fragments;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import io.realm.Realm;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityCall;
import net.iGap.helper.HelperAvatar;
import net.iGap.helper.HelperPublicMethod;
import net.iGap.interfaces.OnAvatarGet;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.AndroidUtils;
import net.iGap.module.CircleImageView;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.proto.ProtoSignalingGetLog;
import net.iGap.realm.RealmCallConfig;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRegisteredInfoFields;
import net.iGap.request.RequestUserInfo;

/**
 * Created by android3 on 4/18/2017.
 */

public class FragmentCall extends Fragment {

    public static FragmentCall newInstance() {
        return new FragmentCall();
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_call, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.fc_layot_title).setBackgroundColor(Color.parseColor(G.appBarColor));  //set title bar color

        RippleView rippleBack = (RippleView) view.findViewById(R.id.fc_call_ripple_txtBack);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override public void onComplete(RippleView rippleView) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        final RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.fc_recycler_view_call);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(new CallAdapter(fillLogListCall()));

        FloatingActionButton fabContactList = (FloatingActionButton) view.findViewById(R.id.fc_fab_contact_list);
        fabContactList.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(G.appBarColor)));

        fabContactList.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

                final Fragment fragment = RegisteredContactsFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("TITLE", "Contacts");
                bundle.putBoolean("ACTION", true);
                fragment.setArguments(bundle);

                try {
                    getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left)
                        .addToBackStack(null)
                        .replace(R.id.fragmentContainer, fragment)
                        .commit();
                } catch (Exception e) {
                    e.getStackTrace();
                }
            }
        });
    }

    //*************************************************************************************************************

    public static void call(long userID, FragmentActivity activity) {

        Intent intent = new Intent(activity, ActivityCall.class);
        intent.putExtra(ActivityCall.UserIdStr, userID);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        activity.startActivity(intent);

    }

    private List<ProtoSignalingGetLog.SignalingGetLogResponse.SignalingLog> fillLogListCall() {

        List<ProtoSignalingGetLog.SignalingGetLogResponse.SignalingLog> _list = null;

        Realm realm = Realm.getDefaultInstance();

        RealmCallConfig realmCallConfig = realm.where(RealmCallConfig.class).findFirst();
        if (realmCallConfig != null) {
            _list = realmCallConfig.getLogList();
        }

        if (_list == null) {
            _list = new ArrayList<>();
        }

        return _list;
    }

    //*************************************************************************************************************

    enum CallMode {
        call_made, call_received, call_missed, call_missed_outgoing
    }


    //***************************************** adapater call ***************************************************

    public class CallAdapter extends RecyclerView.Adapter {

        List<ProtoSignalingGetLog.SignalingGetLogResponse.SignalingLog> callList;

        public CallAdapter(List<ProtoSignalingGetLog.SignalingGetLogResponse.SignalingLog> list) {
            callList = list;
        }

        @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_call_sub_layout, null);
            CallHolder callHolder = new CallHolder(view);

            return callHolder;
        }

        @Override public int getItemCount() {
            return callList.size();
        }

        @Override public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

            final CallHolder _holder = (CallHolder) holder;


            // set icon and icon color
            switch (callList.get(position).getStatus()) {
                case OUTGOING:
                    _holder.icon.setText(R.string.md_call_made);
                    _holder.icon.setTextColor(G.context.getResources().getColor(R.color.green));
                    break;
                case MISSED:
                    _holder.icon.setText(R.string.md_call_missed);
                    _holder.icon.setTextColor(G.context.getResources().getColor(R.color.red));
                    break;
                case CANCELED:
                    _holder.icon.setText(R.string.md_call_missed_outgoing);
                    _holder.icon.setTextColor(G.context.getResources().getColor(R.color.red));
                    break;
                case INCOMING:
                    _holder.icon.setText(R.string.md_call_received);
                    _holder.icon.setTextColor(G.context.getResources().getColor(R.color.green));
                    break;
            }

            _holder.callType.setText(callList.get(position).getType().toString());
            _holder.timeAndInfo.setText(callList.get(position).getDuration() + "  " + callList.get(position).getOfferTime());

            Realm realm = Realm.getDefaultInstance();
            RealmRegisteredInfo registeredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, callList.get(position).getId()).findFirst();

            if (registeredInfo != null) {

                _holder.name.setText(registeredInfo.getDisplayName());

                HelperAvatar.getAvatar(callList.get(position).getId(), HelperAvatar.AvatarType.USER, new OnAvatarGet() {
                    @Override public void onAvatarGet(final String avatarPath, long ownerId) {
                        G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), _holder.image);
                    }

                    @Override public void onShowInitials(final String initials, final String color) {
                        _holder.image.setImageBitmap(
                            net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) _holder.image.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
                    }
                });
            } else {

                new RequestUserInfo().userInfo(callList.get(position).getId());
            }

            realm.close();


        }

        //**********************************

        public class CallHolder extends RecyclerView.ViewHolder {

            protected CircleImageView image;
            protected TextView name;
            protected MaterialDesignTextView icon;
            protected TextView timeAndInfo;
            protected RippleView rippleCall;
            protected TextView callType;

            public CallHolder(View itemView) {
                super(itemView);

                image = (CircleImageView) itemView.findViewById(R.id.fcsl_imv_picture);
                name = (TextView) itemView.findViewById(R.id.fcsl_txt_name);
                icon = (MaterialDesignTextView) itemView.findViewById(R.id.fcsl_txt_icon);
                timeAndInfo = (TextView) itemView.findViewById(R.id.fcsl_txt_time_info);
                rippleCall = (RippleView) itemView.findViewById(R.id.fcsl_ripple_call);
                callType = (TextView) itemView.findViewById(R.id.fcsl_txt_type_call);


                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {

                        HelperPublicMethod.goToChatRoom(callList.get(getPosition()).getId(), null, null);
                    }
                });

                rippleCall.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                    @Override public void onComplete(RippleView rippleView) throws IOException {
                        call(callList.get(getPosition()).getId(), getActivity());
                    }
                });
            }
        }
    }
}
