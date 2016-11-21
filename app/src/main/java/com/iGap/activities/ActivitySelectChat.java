package com.iGap.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.R;
import com.iGap.adapter.items.RoomItem;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.module.ShouldScrolledBehavior;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.Sort;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ActivitySelectChat extends ActivityEnhanced {

    public static final String ARG_FORWARD_MESSAGE = "arg_forward_msg";
    private RecyclerView mRecyclerView;
    private FastItemAdapter<RoomItem> mAdapter;
    private ArrayList<Parcelable> mForwardMessages;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mForwardMessages = getIntent().getExtras().getParcelableArrayList(ARG_FORWARD_MESSAGE);

        findViewById(R.id.loadingContent).setVisibility(View.GONE);

        initRecycleView();
        initComponent();
    }

    private void initComponent() {
        MaterialDesignTextView btnMenu = (MaterialDesignTextView) findViewById(R.id.cl_btn_menu);

        MaterialDesignTextView btnSearch =
                (MaterialDesignTextView) findViewById(R.id.amr_btn_search);

        TextView txtIgap = (TextView) findViewById(R.id.cl_txt_igap);
        txtIgap.setTypeface(G.neuroplp);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    private void initRecycleView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.cl_recycler_view_contact);
        mAdapter = new FastItemAdapter<>();
        mAdapter.withOnClickListener(new FastAdapter.OnClickListener<RoomItem>() {
            @Override
            public boolean onClick(View v, IAdapter<RoomItem> adapter, RoomItem item,
                                   int position) {
                if (!item.mInfo.getReadOnly()) {
                    Intent intent = new Intent(ActivitySelectChat.this, ActivityChat.class);
                    intent.putExtra("RoomId", item.mInfo.getId());
                    intent.putParcelableArrayListExtra(ARG_FORWARD_MESSAGE, mForwardMessages);
                    startActivity(intent);
                    finish();
                } else {
                    new MaterialDialog.Builder(ActivitySelectChat.this).title(R.string.dialog_readonly_chat).positiveText("OK").show();
                }
                return false;
            }
        });
        LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(ActivitySelectChat.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(null);
        // set behavior to RecyclerView
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mRecyclerView.getLayoutParams();
        params.setBehavior(new ShouldScrolledBehavior(mLayoutManager, mAdapter));
        mRecyclerView.setLayoutParams(params);
        mRecyclerView.setAdapter(mAdapter);

        loadLocalChatList();
    }

    private void loadLocalChatList() {
        mAdapter.clear();

        Realm realm = Realm.getDefaultInstance();
        // FIXME: 11/17/2016 [Alireza] sort by last messa
        for (RealmRoom realmRoom : realm.where(RealmRoom.class)
                .findAllSorted(RealmRoomFields.ID, Sort.DESCENDING)) {
            final RoomItem roomItem = new RoomItem();
            roomItem.setInfo(realmRoom);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.add(roomItem);
                }
            });
        }

        realm.close();
    }
}
