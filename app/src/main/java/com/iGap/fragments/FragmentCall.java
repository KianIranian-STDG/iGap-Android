package com.iGap.fragments;

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
import com.iGap.G;
import com.iGap.R;
import com.iGap.helper.HelperPublicMethod;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.CircleImageView;
import com.iGap.module.MaterialDesignTextView;
import java.io.IOException;
import java.util.ArrayList;

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

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.fc_recycler_view_call);
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

    public static void call(long userID, FragmentActivity activity, int resContainer) {

        FragmentCallResponse fragmentCallResponse = FragmentCallResponse.newInstance(userID);

        try {

            activity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left)
                .addToBackStack(null)
                .replace(resContainer, fragmentCallResponse)
                .commit();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    private ArrayList<StructCall> fillLogListCall() {

        ArrayList<StructCall> list = new ArrayList<>();

        StructCall _item1 = new StructCall();
        _item1.userId = 365;
        _item1.callMode = CallMode.call_made;
        _item1.timeAndInfo = "(4) 9.24 am";

        StructCall _item2 = new StructCall();
        _item2.userId = 365;
        _item2.callMode = CallMode.call_missed;
        _item2.timeAndInfo = "(4) 9.24 am";

        StructCall _item3 = new StructCall();
        _item3.userId = 365;
        _item3.callMode = CallMode.call_missed_outgoing;
        _item3.timeAndInfo = "(4) 9.24 am";

        StructCall _item4 = new StructCall();
        _item4.userId = 365;
        _item4.callMode = CallMode.call_received;
        _item4.timeAndInfo = "(4) 9.24 am";

        list.add(_item1);
        list.add(_item2);
        list.add(_item3);
        list.add(_item4);

        return list;
    }

    //*************************************************************************************************************

    enum CallMode {
        call_made, call_received, call_missed, call_missed_outgoing
    }

    private class StructCall {

        long userId;
        String timeAndInfo = "";
        CallMode callMode = CallMode.call_made;
    }

    //***************************************** adapater call ***************************************************

    public class CallAdapter extends RecyclerView.Adapter {

        ArrayList<StructCall> callList;

        public CallAdapter(ArrayList<StructCall> list) {
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

        @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            CallHolder _holder = (CallHolder) holder;

            // set icon and icon color
            switch (callList.get(position).callMode) {
                case call_made:
                    _holder.icon.setText(R.string.md_call_made);
                    _holder.icon.setTextColor(G.context.getResources().getColor(R.color.green));
                    break;
                case call_missed:
                    _holder.icon.setText(R.string.md_call_missed);
                    _holder.icon.setTextColor(G.context.getResources().getColor(R.color.red));
                    break;
                case call_missed_outgoing:
                    _holder.icon.setText(R.string.md_call_missed_outgoing);
                    _holder.icon.setTextColor(G.context.getResources().getColor(R.color.red));
                    break;
                case call_received:
                    _holder.icon.setText(R.string.md_call_received);
                    _holder.icon.setTextColor(G.context.getResources().getColor(R.color.green));
                    break;
            }
        }

        //**********************************

        public class CallHolder extends RecyclerView.ViewHolder {

            protected CircleImageView image;
            protected TextView name;
            protected MaterialDesignTextView icon;
            protected TextView timeAndInfo;
            protected RippleView rippleCall;

            public CallHolder(View itemView) {
                super(itemView);

                image = (CircleImageView) itemView.findViewById(R.id.fcsl_imv_picture);
                name = (TextView) itemView.findViewById(R.id.fcsl_txt_name);
                icon = (MaterialDesignTextView) itemView.findViewById(R.id.fcsl_txt_icon);
                timeAndInfo = (TextView) itemView.findViewById(R.id.fcsl_txt_time_info);
                rippleCall = (RippleView) itemView.findViewById(R.id.fcsl_ripple_call);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {

                        HelperPublicMethod.goToChatRoom(callList.get(getPosition()).userId, null, null);
                    }
                });

                rippleCall.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                    @Override public void onComplete(RippleView rippleView) throws IOException {
                        call(callList.get(getPosition()).userId, getActivity(), R.id.fragmentContainer);
                    }
                });
            }
        }
    }
}
