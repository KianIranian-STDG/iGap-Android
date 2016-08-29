package com.iGap.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.adapter.AdapterDrawerMenu;
import com.iGap.interface_package.IActionClick;
import com.iGap.libs.flowingdrawer.MenuFragment;
import com.iGap.module.CircleImageView;
import com.iGap.module.MyType;
import com.iGap.module.StructUserInfo;

import java.util.ArrayList;


public class FragmentDrawerMenu extends MenuFragment {

    Context context;
    RecyclerView recyclerView;
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

        View view = inflater.inflate(R.layout.drawer_layout, container, false);

        initComponent(view);

        return setupReveal(view, true);
    }

    private void initComponent(View view) {

        initRecycleView(view);

        CircleImageView imvUserPicture = (CircleImageView) view.findViewById(R.id.dl_imv_user_picture);
        imvUserPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", "user picture click");
            }
        });

        TextView txtName = (TextView) view.findViewById(R.id.dl_txt_name);
        txtName.setTypeface(G.arialBold);

        TextView txtPhone = (TextView) view.findViewById(R.id.dl_txt_phone_number);
        txtPhone.setTypeface(G.arial);


        ImageButton btnAboutUs = (ImageButton) view.findViewById(R.id.dl_btn_about_us);
        btnAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", "about us");
            }
        });

        ImageButton btnSetting = (ImageButton) view.findViewById(R.id.dl_btn_setting);
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(G.context, ActivitySetting.class);
                startActivity(intent);
            }
        });

        ImageButton btnFAQ = (ImageButton) view.findViewById(R.id.dl_btn_faq);
        btnFAQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", "FAQ");
            }
        });

        final EditText edtSearch = (EditText) view.findViewById(R.id.dl_edt_search);
        edtSearch.setTypeface(G.arialBold);

        ImageButton btnSearch = (ImageButton) view.findViewById(R.id.dl_btn_search);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtSearch.requestFocus();

                // click on search action btn event
                if (mActionClickListener != null) {
                    mActionClickListener.onActionSearchClick();
                }
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.e("ddd", "edt text change   " + charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }


    private void initRecycleView(View view) {

        recyclerView = (RecyclerView) view.findViewById(R.id.dl_recycleview_contacts);

        AdapterDrawerMenu mAdapter = new AdapterDrawerMenu(getUserList(), context);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

    }


    private ArrayList<StructUserInfo> getUserList() {

        ArrayList<StructUserInfo> list = new ArrayList<>();

        StructUserInfo u1 = new StructUserInfo();
        u1.contactState = MyType.ContactState.registered;
        list.add(u1);

        StructUserInfo u2 = new StructUserInfo();
        u2.contactState = MyType.ContactState.registered;
        list.add(u2);

        StructUserInfo u5 = new StructUserInfo();
        u5.contactState = MyType.ContactState.registered;
        list.add(u5);
        list.add(u5);
        list.add(u5);
        list.add(u5);
        list.add(u5);
        list.add(u5);
        list.add(u5);
        list.add(u5);

        StructUserInfo u6 = new StructUserInfo();
        u6.contactState = MyType.ContactState.line;
        list.add(u6);

        StructUserInfo u3 = new StructUserInfo();
        u3.contactState = MyType.ContactState.notRegistered;
        list.add(u3);
        list.add(u3);
        list.add(u3);
        list.add(u3);
        list.add(u3);
        list.add(u3);
        list.add(u3);
        list.add(u3);
        list.add(u3);

        StructUserInfo u4 = new StructUserInfo();
        u4.contactState = MyType.ContactState.notRegistered;
        list.add(u4);


        return list;
    }
}
