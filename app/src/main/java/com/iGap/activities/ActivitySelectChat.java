package com.iGap.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.R;
import com.iGap.adapter.items.RoomItem;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.module.SUID;
import com.iGap.module.ShouldScrolledBehavior;
import com.iGap.module.SortRooms;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import io.realm.Realm;
import io.realm.Sort;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ActivitySelectChat extends ActivityEnhanced {

    public static final String ARG_FORWARD_MESSAGE = "arg_forward_msg";
    public static final String ARG_FORWARD_MESSAGE_COUNT = "arg_forward_msg_count";
    private RecyclerView mRecyclerView;
    private FastItemAdapter<RoomItem> mAdapter;
    private ArrayList<Parcelable> mForwardMessages;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mForwardMessages = getIntent().getExtras().getParcelableArrayList(ARG_FORWARD_MESSAGE);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);
        findViewById(R.id.loadingContent).setVisibility(View.GONE);

        initRecycleView();
        initComponent();
    }

    private void initComponent() {
        MaterialDesignTextView btnMenu = (MaterialDesignTextView) findViewById(R.id.cl_btn_menu);

        MaterialDesignTextView btnSearch =
                (MaterialDesignTextView) findViewById(R.id.amr_btn_search);

        TextView txtIgap = (TextView) findViewById(R.id.cl_txt_igap);

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
                    intent.putExtra(ARG_FORWARD_MESSAGE_COUNT, mForwardMessages.size());
                    startActivity(intent);
                    finish();
                } else {
                    new MaterialDialog.Builder(ActivitySelectChat.this).title(R.string.dialog_readonly_chat).positiveText(R.string.ok).show();
                }
                return false;
            }
        });
        LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(ActivitySelectChat.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(null);
        // set behavior to RecyclerView
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) swipeRefreshLayout.getLayoutParams();
        params.setBehavior(new ShouldScrolledBehavior(mLayoutManager, mAdapter));
        mRecyclerView.setLayoutParams(params);
        mRecyclerView.setAdapter(mAdapter);

        loadLocalChatList();
    }

    private void loadLocalChatList() {
        mAdapter.clear();
        Realm realm = Realm.getDefaultInstance();
        List<RoomItem> roomItems = new ArrayList<>();
        for (RealmRoom realmRoom : realm.where(RealmRoom.class).equalTo(RealmRoomFields.IS_DELETED, false).findAllSorted(RealmRoomFields.UPDATED_TIME, Sort.DESCENDING)) {
            roomItems.add(new RoomItem().setInfo(realmRoom).withIdentifier(SUID.id().get()));
        }

        Collections.sort(roomItems, SortRooms.DESC);

        mAdapter.add(roomItems);
        realm.close();
    }
}
