/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.swipe.SwipeLayout;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.interfaces.OnBlockStateChanged;
import net.iGap.interfaces.OnSelectedList;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.module.CircleImageView;
import net.iGap.module.Contacts;
import net.iGap.module.CustomTextViewMedium;
import net.iGap.module.FastScroller;
import net.iGap.module.LastSeenTimeUtil;
import net.iGap.module.structs.StructContactInfo;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRegisteredInfoFields;
import net.iGap.request.RequestUserContactsBlock;
import net.iGap.request.RequestUserContactsUnblock;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

import static net.iGap.G.inflater;

public class FragmentBlockedUser extends BaseFragment implements OnBlockStateChanged, ToolbarListener {

    private Realm realmBlockedUser;
    private StickyRecyclerHeadersDecoration decoration;
    private HelperToolbar mHelperToolbar;
    private FastScroller fastScroller ;

    private Realm getRealmBlockedUser() {
        if (realmBlockedUser == null || realmBlockedUser.isClosed()) {
            realmBlockedUser = Realm.getDefaultInstance();
        }
        return realmBlockedUser;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        realmBlockedUser = Realm.getDefaultInstance();
        return attachToSwipeBack(inflater.inflate(R.layout.fragment_blocked_user, container, false));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realmBlockedUser.close();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setDefaultTitle(G.context.getResources().getString(R.string.Block_Users))
                .setLeftIcon(R.string.back_icon)
                .setLogoShown(true)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                });

        ViewGroup layoutToolbar = view.findViewById(R.id.fbu_layout_toolbar);
        layoutToolbar.addView(mHelperToolbar.getView());

        RecyclerView realmRecyclerView = view.findViewById(R.id.fbu_realm_recycler_view);
        realmRecyclerView.setItemViewCacheSize(100);
        realmRecyclerView.setItemAnimator(null);
        realmRecyclerView.setLayoutManager(new LinearLayoutManager(G.fragmentActivity));

        RealmResults<RealmRegisteredInfo> results = getRealmBlockedUser().where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.BLOCK_USER, true).findAll();
        BlockListAdapter blockListAdapter = new BlockListAdapter(results.sort(RealmRegisteredInfoFields.DISPLAY_NAME));
        realmRecyclerView.setAdapter(blockListAdapter);

        fastScroller = view.findViewById(R.id.fast_scroller);

        if (realmRecyclerView.getAdapter().getItemCount() > 0) {
            fastScroller.setRecyclerView(realmRecyclerView);
        } else {
            fastScroller.setVisibility(View.GONE);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        G.onBlockStateChanged = null;
    }

    @Override
    public void onBlockStateChanged(final boolean blocked, final long userId) {
    }

    public class BlockListAdapter extends RealmRecyclerViewAdapter<RealmRegisteredInfo, BlockListAdapter.ViewHolder> {

        BlockListAdapter(RealmResults<RealmRegisteredInfo> realmResults) {
            super(realmResults, true);
        }

        @Override
        public BlockListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = inflater.inflate(R.layout.contact_item, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final BlockListAdapter.ViewHolder viewHolder, int i) {

            final RealmRegisteredInfo registeredInfo = viewHolder.realmRegisteredInfo = getItem(i);
            if (registeredInfo == null) {
                return;
            }

            viewHolder.swipeLayout.getSurfaceView().setOnClickListener(v -> unblock(viewHolder, registeredInfo.getId()));

            viewHolder.title.setText(registeredInfo.getDisplayName());

            viewHolder.title.setTextColor(Color.parseColor(G.textTitleTheme));
            viewHolder.subtitle.setTextColor(Color.parseColor(G.textSubTheme));
            viewHolder.subtitle.setText(LastSeenTimeUtil.computeTime(registeredInfo.getId(), registeredInfo.getLastSeen(), false));
            if (HelperCalander.isPersianUnicode) {
                viewHolder.subtitle.setText(viewHolder.subtitle.getText().toString());
            }
            avatarHandler.getAvatar(new ParamWithAvatarType(viewHolder.image, registeredInfo.getId()).avatarType(AvatarHandler.AvatarType.USER));

        }


        public String getBubbleText(int position) {
            if (getItem(position) == null)
                return "-";

            return getItem(position).getDisplayName().substring(0, 1).toUpperCase();
        }

        private void unblock(ViewHolder viewHolder, long id) {
            if (!viewHolder.isOpenDialog) {
                viewHolder.isOpenDialog = true;
                MaterialDialog dialog = new MaterialDialog.Builder(G.currentActivity).content(R.string.un_block_user).positiveText(R.string.B_ok).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        new RequestUserContactsUnblock().userContactsUnblock(id);

                        if (getItemCount() == 1){
                            fastScroller.setVisibility(View.GONE);
                        }
                    }
                }).negativeText(R.string.B_cancel).build();

                dialog.setOnDismissListener(dialog1 -> {
                    viewHolder.swipeLayout.close();
                    viewHolder.isOpenDialog = false;
                });
                dialog.show();
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            protected CircleImageView image;
            protected CustomTextViewMedium title;
            protected CustomTextViewMedium subtitle;
            RealmRegisteredInfo realmRegisteredInfo;
            View bottomLine;
            private SwipeLayout swipeLayout;
            private boolean isOpenDialog = false;


            public ViewHolder(View view) {
                super(view);

                image = view.findViewById(R.id.imageView);
                title = view.findViewById(R.id.title);
                subtitle = view.findViewById(R.id.subtitle);
                bottomLine = view.findViewById(R.id.bottomLine);
                bottomLine.setVisibility(View.VISIBLE);
                view.findViewById(R.id.topLine).setVisibility(View.GONE);
                swipeLayout = itemView.findViewById(R.id.swipeRevealLayout);
                swipeLayout.setSwipeEnabled(false);
            }
        }
    }
}
