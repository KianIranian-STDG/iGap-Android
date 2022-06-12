package net.iGap.messenger.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.BuildConfig;
import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.fragments.FragmentContactsProfile;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.LayoutCreator;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.messenger.theme.Theme;
import net.iGap.messenger.theme.ThemeDescriptor;
import net.iGap.messenger.ui.cell.HeaderCell;
import net.iGap.messenger.ui.cell.ManageChatTextCell;
import net.iGap.messenger.ui.cell.ManageChatUserCell;
import net.iGap.messenger.ui.cell.TextInfoPrivacyCell;
import net.iGap.messenger.ui.toolBar.Toolbar;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.customView.RecyclerListView;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.request.RequestUserContactsUnblock;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;

public class BlockedUserFragment extends BaseFragment {

    private final int addBlockUserRow = 0;
    private final int sectionOneDividerRow = 1;
    private final int sectionTwoHeaderRow = 2;
    private RealmResults<RealmRegisteredInfo> registeredInfoRealmResults;
    private int blockUserCount;
    private int sectionTwoDividerRow;
    private int rowCount;
    private ListAdapter listAdapter;
    private RecyclerListView listView;

    @Override
    public View createView(Context context) {
        getRegisteredInfoRealmResults();
        fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_window_background));
        listView = new RecyclerListView(context);
        listView.setItemAnimator(null);
        listView.setLayoutAnimation(null);
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        });
        listView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(listView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT));
        listView.setAdapter(listAdapter = new ListAdapter());
        listView.setOnItemClickListener((view, position, x, y) -> {
            if (view instanceof ManageChatTextCell) {
                new HelperFragment(getActivity().getSupportFragmentManager(), new AddBlockContactFragment()).setReplace(true).load();
            }
        });
        fragmentView.setBackgroundColor(Theme.getColor(Theme.key_window_background));
        return fragmentView;
    }

    private void getRegisteredInfoRealmResults() {
        registeredInfoRealmResults = DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmRegisteredInfo.class).equalTo("blockUser", true).findAll();
        });
        blockUserCount = registeredInfoRealmResults.size();
        if (blockUserCount == 0){

        }
        sectionTwoDividerRow = blockUserCount + 3;
        rowCount = blockUserCount + 4;
    }

    public void showUnblockAlert(ManageChatUserCell cell, int position) {
        RealmRegisteredInfo realmRegisteredInfo = cell.getRealmRegisteredInfo();
        new MaterialDialog.Builder(G.currentActivity)
                .content(R.string.un_block_user)
                .positiveText(R.string.B_ok)
                .neutralText(getString(R.string.view_profile))
                .onPositive((dialog12, which) -> {
                    G.onUserContactsUnBlock = userId -> G.handler.post(() -> {
                        G.onUserContactsUnBlock = null;
                        blockUserCount = blockUserCount - 1;
                        rowCount = rowCount - 1;
                        sectionTwoDividerRow = sectionTwoDividerRow - 1;
                        listAdapter.notifyItemRemoved(position);
                        listAdapter.notifyItemChanged(sectionTwoHeaderRow);
                    });
                    new RequestUserContactsUnblock().userContactsUnblock(realmRegisteredInfo.getId());
                })
                .onNeutral((dialog1, which) -> {
                    if (getActivity() != null) {
                        new HelperFragment(getActivity().getSupportFragmentManager(), FragmentContactsProfile.newInstance(0, realmRegisteredInfo.getId(), "Others")).setReplace(false).load();
                    }
                })
                .negativeText(R.string.B_cancel)
                .build().show();
    }

    @SuppressLint("ResourceType")
    @Override
    public View createToolBar(Context context) {
        toolbar = new Toolbar(context);
        toolbar.setTitle(context.getString(R.string.block_users));
        toolbar.setBackIcon(R.drawable.ic_ab_back);
        toolbar.setListener(id -> {
            if (id == -1) {
                finish();
            }
        });
        return toolbar;
    }

    @Override
    public List<ThemeDescriptor> getThemeDescriptor() {
        List<ThemeDescriptor> themeDescriptors = new ArrayList<>();
        themeDescriptors.add(new ThemeDescriptor(listView, ThemeDescriptor.FLAG_CELLBACKGROUNDCOLOR, Theme.key_window_background));
        themeDescriptors.add(new ThemeDescriptor(listView, ThemeDescriptor.FLAG_BACKGROUND, Theme.key_window_background));
        themeDescriptors.add(new ThemeDescriptor(listView, ThemeDescriptor.FLAG_TEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(toolbar, ThemeDescriptor.FLAG_BACKGROUND, Theme.key_toolbar_background));
        return themeDescriptors;
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return type != 1 && type != 2;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 0:
                    view = new ManageChatTextCell(context);
                    break;
                case 1:
                    view = new HeaderCell(context);
                    break;
                case 2:
                    TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context, 10);
                    textInfoPrivacyCell.getTextView().setGravity(Gravity.CENTER_HORIZONTAL);
                    textInfoPrivacyCell.getTextView().setTextColor(Theme.getColor(Theme.key_icon));
                    textInfoPrivacyCell.getTextView().setMovementMethod(null);
                    textInfoPrivacyCell.getTextView().setPadding(0, LayoutCreator.dp(14), 0, LayoutCreator.dp(14));
                    view = textInfoPrivacyCell;
                    view.setBackground(Theme.getThemedDrawable(context, R.drawable.greydivider_bottom, Theme.key_line));
                    view.setBackgroundColor(Theme.getColor(Theme.key_line));
                    break;
                case 3:
                    view = new ManageChatUserCell(context, 7, 6, true);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @SuppressLint("DefaultLocale")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            int viewType = holder.getItemViewType();
            switch (viewType) {
                case 0:
                    ManageChatTextCell manageChatTextCell = (ManageChatTextCell) holder.itemView;
                    manageChatTextCell.setColors(Theme.key_default_text, Theme.key_icon);
                    manageChatTextCell.setText(getString(R.string.BlockUser), null, R.drawable.actions_addmember2, false);
                    break;
                case 1:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    headerCell.setText(String.format(getString(R.string.blocked_users), blockUserCount));
                    break;
                case 2:
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == sectionOneDividerRow) {
                        textInfoPrivacyCell.setText(getString(R.string.block_desc));
                    }
                    break;
                case 3:
                    ManageChatUserCell manageChatUserCell = (ManageChatUserCell) holder.itemView;
                    final RealmRegisteredInfo realmRegisteredInfo = registeredInfoRealmResults.get(position - 3);
                    manageChatUserCell.setData(realmRegisteredInfo, true);
                    avatarHandler.getAvatar(new ParamWithAvatarType(manageChatUserCell.getAvatarImageView(), realmRegisteredInfo.getId()).avatarType(AvatarHandler.AvatarType.USER));
                    manageChatUserCell.setDelegate((ManageChatUserCell cell, boolean click) -> {
                        if (click) {
                            showUnblockAlert(cell, position);
                        }
                        return true;
                    });
                    break;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == addBlockUserRow) {
                return 0;
            } else if (position == sectionTwoHeaderRow) {
                return 1;
            } else if (position == sectionOneDividerRow || position == sectionTwoDividerRow) {
                return 2;
            } else {
                return 3;
            }
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }
    }
}