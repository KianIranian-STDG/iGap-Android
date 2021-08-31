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

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.module.CircleImageView;
import net.iGap.module.LastSeenTimeUtil;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.scrollbar.FastScroller;
import net.iGap.module.scrollbar.FastScrollerBarBaseAdapter;
import net.iGap.observers.interfaces.OnBlockStateChanged;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.request.RequestUserContactsUnblock;
import net.iGap.request.RequestUserInfo;

import org.jetbrains.annotations.NotNull;

import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;


public class FragmentBlockedUser extends BaseFragment implements OnBlockStateChanged, ToolbarListener {

    private StickyRecyclerHeadersDecoration decoration;
    private HelperToolbar mHelperToolbar;
    private FastScroller fastScroller;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return attachToSwipeBack(inflater.inflate(R.layout.fragment_blocked_user, container, false));
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setDefaultTitle(G.context.getResources().getString(R.string.Block_Users))
                .setLeftIcon(R.string.icon_back)
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

        RealmResults<RealmRegisteredInfo> results = DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmRegisteredInfo.class).equalTo("blockUser", true).findAll();
        });
        BlockListAdapter blockListAdapter = new BlockListAdapter(results.sort("displayName"));
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

    public class BlockListAdapter extends RealmRecyclerViewAdapter<RealmRegisteredInfo, BlockListAdapter.ViewHolder> implements FastScrollerBarBaseAdapter {

        BlockListAdapter(RealmResults<RealmRegisteredInfo> realmResults) {
            super(realmResults, true);
        }

        @NotNull
        @Override
        public BlockListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_block_list, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final BlockListAdapter.ViewHolder viewHolder, int i) {

            final RealmRegisteredInfo registeredInfo = viewHolder.realmRegisteredInfo = getItem(i);
            if (registeredInfo == null)
                return;

            new RequestUserInfo().userInfo(registeredInfo.getId());

            viewHolder.root.setOnClickListener(v -> unblock(viewHolder, registeredInfo.getId()));
            viewHolder.title.setText(registeredInfo.getDisplayName());

            if (registeredInfo.getStatus().equals(ProtoGlobal.RegisteredUser.Status.EXACTLY.toString())) {
                String status = LastSeenTimeUtil.computeTime(G.context, registeredInfo.getId(), registeredInfo.getLastSeen(), false);
                viewHolder.subtitle.setText(status);
            } else {
                viewHolder.subtitle.setText(registeredInfo.getStatus());
            }

            if (HelperCalander.isPersianUnicode) {
                viewHolder.subtitle.setText(viewHolder.subtitle.getText().toString());
            }

            avatarHandler.getAvatar(new ParamWithAvatarType(viewHolder.image, registeredInfo.getId()).avatarType(AvatarHandler.AvatarType.USER));
        }

        @Override
        public String getBubbleText(int position) {
            if (getItem(position) == null)
                return "-";

            return getItem(position).getDisplayName().substring(0, 1).toUpperCase();
        }

        private void unblock(ViewHolder viewHolder, long id) {
            if (!viewHolder.isOpenDialog) {
                viewHolder.isOpenDialog = true;
                MaterialDialog dialog = new MaterialDialog.Builder(G.currentActivity)
                        .content(R.string.un_block_user)
                        .positiveText(R.string.B_ok)
                        .neutralText(getString(R.string.view_profile))
                        .onPositive((dialog12, which) -> {
                            new RequestUserContactsUnblock().userContactsUnblock(id);

                            if (getItemCount() == 1) {
                                fastScroller.setVisibility(View.GONE);
                            }
                        })
                        .onNeutral((dialog1, which) -> {
                            if (getActivity() != null) {
                                new HelperFragment(getActivity().getSupportFragmentManager(), FragmentContactsProfile.newInstance(0, id, "Others")).setReplace(false).load();
                            }
                        })
                        .negativeText(R.string.B_cancel).build();

                dialog.setOnDismissListener(dialog1 -> {
                    viewHolder.isOpenDialog = false;
                });
                dialog.show();
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            protected CircleImageView image;
            protected TextView title;
            protected TextView subtitle;
            RealmRegisteredInfo realmRegisteredInfo;
            private boolean isOpenDialog = false;
            private View root;

            public ViewHolder(View view) {
                super(view);

                root = view.findViewById(R.id.item);
                image = view.findViewById(R.id.imageView);
                title = view.findViewById(R.id.title);
                subtitle = view.findViewById(R.id.subtitle);

                if (!G.isAppRtl) {
                    title.setGravity(Gravity.LEFT);
                    subtitle.setGravity(Gravity.LEFT);
                } else {
                    title.setGravity(Gravity.RIGHT);
                    subtitle.setGravity(Gravity.RIGHT);
                }
            }
        }
    }
}
