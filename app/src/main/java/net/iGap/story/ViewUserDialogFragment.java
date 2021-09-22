package net.iGap.story;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import net.iGap.G;
import net.iGap.R;
import net.iGap.controllers.MessageDataStorage;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.LayoutCreator;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.Theme;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.customView.RecyclerListView;
import net.iGap.module.dialog.bottomsheet.BottomSheetFragment;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.observers.interfaces.OnUserInfoResponse;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmStoryViewInfo;
import net.iGap.request.RequestUserInfo;
import net.iGap.story.liststories.ImageLoadingView;
import net.iGap.story.storyviews.StoryCell;
import net.iGap.story.viewPager.StoryDisplayFragment;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.RealmList;

public class ViewUserDialogFragment extends BottomSheetDialogFragment implements RecyclerListView.OnItemClickListener, EventManager.EventDelegate, ToolbarListener {

    private int rowSize = 0;
    private int userRow = 0;
    private int count = 0;
    private RecyclerView recyclerView;
    private LinearLayout rootView;
    private FrameLayout toolbarView;
    private AppCompatTextView toolbarTitleTextView;
    private Typeface tfMain;
    private List<StoryViewInfoObject> userIdList;
    private List<List<String>> displayNameList;
    private List<Long> userId;
    public AvatarHandler avatarHandler;
    private List<Long> createdAtList;
    private ListAdapter listAdapter;
    private ViewUserDialogState viewUserDialogState;
    private CoordinatorLayout coordinatorLayout;
    public static boolean isInShowViewUser = false;

    public void setViewUserDialogState(ViewUserDialogState viewUserDialogState) {
        this.viewUserDialogState = viewUserDialogState;
    }

    public ViewUserDialogFragment(int count, List<StoryViewInfoObject> userIdList) {
        this.count = count;
        this.userIdList = userIdList;
    }

    @Override
    public void onStart() {
        super.onStart();
        avatarHandler.registerChangeFromOtherAvatarHandler();
    }

    @Override
    public void onStop() {
        super.onStop();
        avatarHandler.unregisterChangeFromOtherAvatarHandler();
        isInShowViewUser = false;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        avatarHandler = new AvatarHandler();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventManager.getInstance(AccountManager.selectedAccount).removeObserver(EventManager.STORY_USER_INFO, this);
        isInShowViewUser = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isInShowViewUser = false;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        isInShowViewUser = true;
        if (tfMain == null)
            tfMain = ResourcesCompat.getFont(getContext(), R.font.main_font);


        rootView = new LinearLayout(getContext());
        rootView.setOrientation(LinearLayout.VERTICAL);
        if (G.themeColor == Theme.DARK) {
            rootView.setBackgroundColor(new Theme().getPrimaryDarkColor(getContext()));
        } else {
            rootView.setBackgroundColor(Theme.getInstance().getDividerColor(getContext()));
        }

        HelperToolbar helperToolbar = HelperToolbar.create();
        View toolBar = helperToolbar
                .setContext(getContext())
                .setLogoShown(true)
                .setListener(this)
                .setDefaultTitle(G.selectedLanguage.equals("fa") ? getString(R.string.story_viewed_by) + " " + HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(count)) + " " + getString(R.string.story_person) : getString(R.string.story_viewed_by) + " " + count)
                .getView();

        rootView.addView(toolBar, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER | Gravity.TOP, 0, 0, 0, 0));

        recyclerView = new RecyclerView(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        rootView.addView(recyclerView, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, 0, 1F, Gravity.CENTER));

        listAdapter = new ListAdapter();
        listAdapter.setContext(getContext());

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventManager.getInstance(AccountManager.selectedAccount).removeObserver(EventManager.STORY_USER_INFO, this);
        userId = new ArrayList<>();
        createdAtList = new ArrayList<>();
        for (int i = userIdList.size() - 1; i >= 0; i--) {
            userId.add(userIdList.get(i).userId);
            createdAtList.add(userIdList.get(i).createdTime);
        }

        displayNameList = MessageDataStorage.getInstance(AccountManager.selectedAccount).getDisplayNameWithUserId(userId);
        G.onUserInfoResponse = new OnUserInfoResponse() {

            @Override
            public void onUserInfo(ProtoGlobal.RegisteredUser user, String identity) {

            }

            @Override
            public void onUserInfoTimeOut() {

            }

            @Override
            public void onUserInfoError(int majorCode, int minorCode) {

            }
        };
        recyclerView.setAdapter(listAdapter);
        listAdapter.addRow();
    }

    @Override
    public void onClick(View view, int position) {

    }

    @Override
    public void onLongClick(View view, int position) {

    }


    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        Log.e("fdksdhfksdfhs", "onCancel: ");
        viewUserDialogState.onCancel();
    }

    @Override
    public void receivedEvent(int id, int account, Object... args) {
        if (id == EventManager.STORY_USER_INFO) {
            userId = new ArrayList<>();
            createdAtList = new ArrayList<>();
            for (int i = userIdList.size() - 1; i >= 0; i--) {
                userId.add(userIdList.get(i).userId);
                createdAtList.add(userIdList.get(i).createdTime);
            }

            displayNameList = MessageDataStorage.getInstance(AccountManager.selectedAccount).getDisplayNameWithUserId(userId);
            G.runOnUiThread(() -> {
                listAdapter = new ListAdapter();
                listAdapter.setContext(getContext());
                recyclerView.setAdapter(listAdapter);
                listAdapter.addRow();
            });

        }
    }

    public interface ViewUserDialogState {
        void onCancel();
    }

    private class ListAdapter extends RecyclerListView.ItemAdapter {
        Context context;

        public void addRow() {
            rowSize = 0;

            for (int i = 0; i < count; i++) {
                userRow = rowSize++;
            }
            notifyDataSetChanged();
        }

        public void setContext(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View cellView;
            cellView = new StoryCell(parent.getContext());
            return new RecyclerListView.ItemViewHolder(cellView, ViewUserDialogFragment.this);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            StoryCell storyCell = (StoryCell) holder.itemView;
            storyCell.initView(context, (position + 1) != userIdList.size(), StoryCell.CircleStatus.CIRCLE_IMAGE, ImageLoadingView.Status.LOADING, null, 0);
            storyCell.setStatus(StoryCell.CircleStatus.CIRCLE_IMAGE);
            if (position < displayNameList.size()) {
                storyCell.setText(displayNameList.get(position) != null ? displayNameList.get(position).get(0) : "", HelperCalander.getTimeForMainRoom(createdAtList.get(position) * 1000L));
                storyCell.setUserColorId(displayNameList.get(position) != null ? displayNameList.get(position).get(1) : "#4aca69", displayNameList.get(position).get(0));
                storyCell.setImage(avatarHandler, userId.get(position));
            }

            storyCell.addIconVisibility(false);
            storyCell.deleteIconVisibility(false);

        }

        @Override
        public int getItemCount() {
            return rowSize;
        }


        @Override
        public boolean isEnable(RecyclerView.ViewHolder holder, int viewType, int position) {
            return viewType != 2;
        }
    }
}
