package net.iGap.messenger.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.LayoutCreator;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.messenger.theme.Theme;
import net.iGap.messenger.theme.ThemeDescriptor;
import net.iGap.messenger.ui.cell.ManageChatUserCell;
import net.iGap.messenger.ui.toolBar.Toolbar;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.customView.RecyclerListView;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.request.RequestUserContactsBlock;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;

public class AddBlockContactFragment extends BaseFragment {

    private RealmResults<RealmRegisteredInfo> registeredInfoRealmResults;
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
        });
        return fragmentView;
    }

    @SuppressLint("ResourceType")
    @Override
    public View createToolBar(Context context) {
        toolbar = new Toolbar(context);
        toolbar.setTitle(context.getString(R.string.block_user));
        toolbar.setBackIcon(R.drawable.ic_ab_back);
        toolbar.setListener(id -> {
            if (id == -1) {
                finish();
            }
        });
        return toolbar;
    }

    public void getRegisteredInfoRealmResults() {
        registeredInfoRealmResults = DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmRegisteredInfo.class).equalTo("blockUser", false).notEqualTo("id", AccountManager.getInstance().getCurrentUser().getId()).findAll();
        });
    }

    private void showBlockAlert(ManageChatUserCell cell, int position) {
        RealmRegisteredInfo realmRegisteredInfo = cell.getRealmRegisteredInfo();
        new MaterialDialog.Builder(G.currentActivity)
                .title(R.string.block_the_user)
                .content(R.string.block_the_user_text)
                .positiveText(R.string.B_ok)
                .onPositive((dialog12, which) -> {
                    G.onUserContactsBlock = userId -> G.handler.post(() -> {
                        G.onUserContactsBlock = null;
                        listAdapter.notifyItemRemoved(position);
                    });
                    new RequestUserContactsBlock().userContactsBlock(realmRegisteredInfo.getId());
                })
                .negativeText(R.string.B_cancel)
                .build().show();
    }

    @Override
    public List<ThemeDescriptor> getThemeDescriptor() {
        List<ThemeDescriptor> themeDescriptors = new ArrayList<>();
        themeDescriptors.add(new ThemeDescriptor(listView, ThemeDescriptor.FLAG_CELLBACKGROUNDCOLOR, Theme.key_window_background));
        themeDescriptors.add(new ThemeDescriptor(listView, ThemeDescriptor.FLAG_BACKGROUND, Theme.key_window_background));
        themeDescriptors.add(new ThemeDescriptor(listView, ThemeDescriptor.FLAG_TEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(listView, ThemeDescriptor.FLAG_IMAGECOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(toolbar, ThemeDescriptor.FLAG_BACKGROUND, Theme.key_toolbar_background));
        return themeDescriptors;
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 0:
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
                    ManageChatUserCell manageChatUserCell = (ManageChatUserCell) holder.itemView;
                    RealmRegisteredInfo realmRegisteredInfo = registeredInfoRealmResults.get(position);
                    manageChatUserCell.setData(realmRegisteredInfo, true);
                    avatarHandler.getAvatar(new ParamWithAvatarType(manageChatUserCell.getAvatarImageView(), realmRegisteredInfo.getId()).avatarType(AvatarHandler.AvatarType.USER));
                    manageChatUserCell.setDelegate((ManageChatUserCell cell, boolean click) -> {
                        if (click) {
                            showBlockAlert(cell, position);
                        }
                        return true;
                    });
                    break;
            }
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getItemCount() {
            return registeredInfoRealmResults.size();
        }
    }
}
