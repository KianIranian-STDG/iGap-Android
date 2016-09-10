package com.iGap.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.interface_package.IActionClick;
import com.iGap.libs.flowingdrawer.MenuFragment;


public class FragmentDrawerMenu extends MenuFragment {

    Context context;
    private IActionClick mActionClickListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }


    public void setActionClickListener(IActionClick listener) {
        this.mActionClickListener = listener;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_menu, container, false);
        initLayoutMenu(view);

        return setupReveal(view, true);
    }


    private void initLayoutMenu(View v) {

        // init icon
        TextView txtIconNewGroup = (TextView) v.findViewById(R.id.lm_txt_icon_group);
        txtIconNewGroup.setTypeface(G.fontawesome);

        TextView txtIconNewChat = (TextView) v.findViewById(R.id.lm_txt_icon_new_chat);
        txtIconNewChat.setTypeface(G.fontawesome);

        TextView txtIconNewChannel = (TextView) v.findViewById(R.id.lm_txt_icon_channel);
        txtIconNewChannel.setTypeface(G.fontawesome);

        TextView txtIconContacts = (TextView) v.findViewById(R.id.lm_txt_icon_contacts);
        txtIconContacts.setTypeface(G.fontawesome);

        TextView txtIconInviteFriends = (TextView) v.findViewById(R.id.lm_txt_icon_invite_friends);
        txtIconInviteFriends.setTypeface(G.fontawesome);

        TextView txtIconSetting = (TextView) v.findViewById(R.id.lm_txt_icon_setting);
        txtIconSetting.setTypeface(G.fontawesome);

        TextView txtIconiGapFAQ = (TextView) v.findViewById(R.id.lm_txt_icon_igap_faq);
        txtIconiGapFAQ.setTypeface(G.fontawesome);

        LinearLayout layoutNewGroup = (LinearLayout) v.findViewById(R.id.lm_ll_new_group);
        layoutNewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });


        LinearLayout layoutNewChat = (LinearLayout) v.findViewById(R.id.lm_ll_new_chat);
        layoutNewChat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (mActionClickListener != null) {
                    mActionClickListener.onActionSearchClick();
                }
            }
        });


        LinearLayout layoutNewChannel = (LinearLayout) v.findViewById(R.id.lm_ll_new_channle);
        layoutNewChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });


        LinearLayout layoutContacts = (LinearLayout) v.findViewById(R.id.lm_ll_contacts);
        layoutContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // click on search action btn event
                if (mActionClickListener != null) {
                    mActionClickListener.onActionSearchClick();
                }
            }
        });


        LinearLayout layoutInviteFriends = (LinearLayout) v.findViewById(R.id.lm_ll_invite_friends);
        layoutInviteFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });


        LinearLayout layoutSetting = (LinearLayout) v.findViewById(R.id.lm_ll_setting);
        layoutSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(G.context, ActivitySetting.class);
                startActivity(intent);
                ActivityMain.mLeftDrawerLayout.closeDrawer();

            }
        });


        LinearLayout layoutiGapFAQ = (LinearLayout) v.findViewById(R.id.lm_ll_igap_faq);
        layoutiGapFAQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

    }


}
