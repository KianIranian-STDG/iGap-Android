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

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.cells.TextCell;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.LayoutCreator;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.module.AppUtils;
import net.iGap.module.CircleImageView;
import net.iGap.module.Contacts;
import net.iGap.module.DeviceUtils;
import net.iGap.module.EndlessRecyclerViewScrollListener;
import net.iGap.module.LastSeenTimeUtil;
import net.iGap.module.PreCachingLayoutManager;
import net.iGap.module.Theme;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.enums.ChannelChatRole;
import net.iGap.module.enums.GroupChatRole;
import net.iGap.module.structs.StructContactInfo;
import net.iGap.observers.interfaces.OnChannelGetMemberList;
import net.iGap.observers.interfaces.OnComplete;
import net.iGap.observers.interfaces.OnGroupGetMemberList;
import net.iGap.observers.interfaces.OnGroupKickMember;
import net.iGap.observers.interfaces.OnSelectedList;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.proto.ProtoChannelGetMemberList;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoGroupGetMemberList;
import net.iGap.realm.RealmChannelRoom;
import net.iGap.realm.RealmGroupRoom;
import net.iGap.realm.RealmMember;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomAccess;
import net.iGap.realm.RealmRoomAccessFields;
import net.iGap.realm.RealmRoomFields;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.request.RequestChannelAddMember;
import net.iGap.request.RequestChannelGetMemberList;
import net.iGap.request.RequestChannelKickMember;
import net.iGap.request.RequestGroupAddMember;
import net.iGap.request.RequestGroupGetMemberList;
import net.iGap.request.RequestGroupKickMember;
import net.iGap.request.RequestUserInfo;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

import static net.iGap.G.context;
import static net.iGap.proto.ProtoGlobal.Room.Type.CHANNEL;
import static net.iGap.proto.ProtoGlobal.Room.Type.GROUP;

public class FragmentShowMember extends BaseFragment implements ToolbarListener, OnGroupKickMember {


    public enum ShowMemberMode {
        NONE,
        SELECT_FOR_ADD_ADMIN,
        SELECT_FOR_ADD_MODERATOR
    }

    public static final String ROOMIDARGUMENT = "ROOMID_ARGUMENT";
    public static final String MAINROOL = "MAIN_ROOL";
    public static final String USERID = "USER_ID";
    public static final String SELECTEDROLE = "SELECTED_ROLE";
    public static final String ISNEEDGETMEMBERLIST = "IS_NEED_GET_MEMBER_LIST";
    public static final String ISGROUP = "IS_GROUP";
    public static final String ISSHOWADDMEMBER = "IS_SHOW_ADD_MEMBER";
    public static final String SHOW_MEMBER_MODE = "SHOW_MEMBER_MODE";
    private OnComplete infoUpdateListenerCount;
    private Fragment fragment;
    List<ProtoGroupGetMemberList.GroupGetMemberListResponse.Member> listMembers = new ArrayList<ProtoGroupGetMemberList.GroupGetMemberListResponse.Member>();
    List<ProtoChannelGetMemberList.ChannelGetMemberListResponse.Member> listMembersChannal = new ArrayList<ProtoChannelGetMemberList.ChannelGetMemberListResponse.Member>();
    private long mRoomID = 0;
    private RecyclerView mRecyclerView;
    //private MemberAdapterA mAdapter;
    private RealmResults<RealmMember> realmMemberMe;
    private MemberAdapter mAdapter;
    private String mMainRole = "";
    private ProgressBar progressBar;
    private Long userID = 0l;
    private String role;
    private String selectedRole = ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ALL.toString();
    private boolean isNeedGetMemberList = false;
    private int mMemberCount = 0;
    private int mCurrentUpdateCount = 0;
    private boolean isFirstFill = true;
    private ShowMemberMode showMemberMode;
    private int offset = 0;
    private int limit = 30;
    private EndlessRecyclerViewScrollListener scrollListener;
    private ProtoGlobal.Room.Type roomType;
    private boolean isOne = true;

    private HelperToolbar mHelperToolbar;
    private TextView mBtnAdd;
    private boolean isGroup;
    private boolean isShowAddButton = true;

    private RealmRoom realmRoom;
    private RealmRoomAccess realmRoomAccess;

    public static FragmentShowMember newInstance(long roomId, String mainrool, long userid, String selectedRole, boolean isNeedGetMemberList) {
        return newInstance2(null, roomId, mainrool, userid, selectedRole, isNeedGetMemberList, false);
    }

    public static FragmentShowMember newInstance2(Fragment frg, long roomId, String mainrool, long userid, String selectedRole, boolean isNeedGetMemberList, boolean isGroup) {
        return newInstance3(frg, roomId, mainrool, userid, selectedRole, isNeedGetMemberList, isGroup, true);
    }

    public static FragmentShowMember newInstance3(Fragment frg, long roomId, String mainrool, long userid, String selectedRole, boolean isNeedGetMemberList, boolean isGroup, boolean isShowAddMemberButton) {
        return newInstance4(frg, roomId, mainrool, userid, selectedRole, isNeedGetMemberList, isGroup, isShowAddMemberButton, ShowMemberMode.NONE);
    }

    public static FragmentShowMember newInstance4(Fragment frg, long roomId, String mainrool, long userid, String selectedRole, boolean isNeedGetMemberList, boolean isGroup, boolean isShowAddMemberButton, ShowMemberMode showMemberMode) {

        Bundle bundle = new Bundle();
        bundle.putLong(ROOMIDARGUMENT, roomId);
        bundle.putString(MAINROOL, mainrool);
        bundle.putLong(USERID, userid);
        bundle.putString(SELECTEDROLE, selectedRole);
        bundle.putBoolean(ISNEEDGETMEMBERLIST, isNeedGetMemberList);
        bundle.putBoolean(ISGROUP, isGroup);
        bundle.putBoolean(ISSHOWADDMEMBER, isShowAddMemberButton);
        bundle.putString(SHOW_MEMBER_MODE, showMemberMode.toString());
        FragmentShowMember fragmentShowMember = new FragmentShowMember();
        fragmentShowMember.setArguments(bundle);
        fragmentShowMember.fragment = frg;
        return fragmentShowMember;
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return attachToSwipeBack(inflater.inflate(R.layout.fragment_show_member, container, false));
    }


    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {


            view.findViewById(R.id.rootShowMember).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            mRoomID = getArguments().getLong(ROOMIDARGUMENT);
            mMainRole = getArguments().getString(MAINROOL);
            userID = getArguments().getLong(USERID);
            selectedRole = getArguments().getString(SELECTEDROLE);
            isNeedGetMemberList = true;

            isGroup = getArguments().getBoolean(ISGROUP);
            if (!getArguments().getString(SHOW_MEMBER_MODE, "").equals("")) {
                showMemberMode = ShowMemberMode.valueOf(getArguments().getString(SHOW_MEMBER_MODE, ""));
            } else {
                showMemberMode = ShowMemberMode.NONE;
            }

            realmRoomAccess = DbManager.getInstance().doRealmTask(realm -> {
                return realm.where(RealmRoomAccess.class).equalTo(RealmRoomAccessFields.ID, mRoomID + "_" + userID).findFirst();
            });

            isShowAddButton = getArguments().getBoolean(ISSHOWADDMEMBER, true);

            roomType = RealmRoom.detectType(mRoomID);

            if (mRoomID > 0) {
                initComponent(view);
            }

            if (isNeedGetMemberList) {
                if (G.userLogin) {
                    G.handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (progressBar != null) {
                                progressBar.setVisibility(View.VISIBLE);
                            }
                            new AsyncMember().execute();
                        }
                    }, 100);
                }
            }

        }
    }

    private void getMemberList() {
        mMemberCount = listMembers.size();
        infoUpdateListenerCount = new OnComplete() {
            @Override
            public void complete(boolean result, final String messageOne, String MessageTow) {

                if (MessageTow.contains("OK")) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            DbManager.getInstance().doRealmTask(realm -> {
                                realm.executeTransactionAsync(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        final RealmList<RealmMember> newMemberList = new RealmList<>();
                                        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomID).findFirst();
                                        if (realmRoom != null) {
                                            if (realmRoom.getType() == GROUP) {
                                                for (ProtoGroupGetMemberList.GroupGetMemberListResponse.Member member : listMembers) {
                                                    if (Long.parseLong(messageOne) == member.getUserId()) {
                                                        mCurrentUpdateCount++;
                                                        newMemberList.add(RealmMember.put(realm, member));
                                                        newMemberList.addAll(0, realmRoom.getGroupRoom().getMembers());
                                                        realmRoom.getGroupRoom().setMembers(newMemberList);
                                                        newMemberList.clear();
                                                        break;
                                                    }
                                                }
                                            } else {
                                                for (ProtoChannelGetMemberList.ChannelGetMemberListResponse.Member member : listMembersChannal) {
                                                    if (Long.parseLong(messageOne) == member.getUserId()) {
                                                        mCurrentUpdateCount++;
                                                        newMemberList.add(RealmMember.put(realm, member));
                                                        newMemberList.addAll(0, realmRoom.getChannelRoom().getMembers());
                                                        realmRoom.getChannelRoom().setMembers(newMemberList);
                                                        newMemberList.clear();
                                                        break;
                                                    }
                                                }
                                            }
                                        }

                                    }
                                }, new Realm.Transaction.OnSuccess() {
                                    @Override
                                    public void onSuccess() {
                                        fillItem();

                                    }
                                });
                            });
                        }
                    });
                } else {
                    mCurrentUpdateCount++;
                }
            }
        };

        G.onGroupGetMemberList = new OnGroupGetMemberList() {
            @Override
            public void onGroupGetMemberList(final List<ProtoGroupGetMemberList.GroupGetMemberListResponse.Member> members) {

                mMemberCount = members.size();

                if (mMemberCount > 0) {
                    listMembers.clear();
                    for (final ProtoGroupGetMemberList.GroupGetMemberListResponse.Member member : members) {
                        listMembers.add(member);
                        new RequestUserInfo().userInfoWithCallBack(infoUpdateListenerCount, member.getUserId(), "" + member.getUserId());
                    }
                } else {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {

                            if (progressBar != null) {
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                    isOne = true;
                    if (isFirstFill) {
                        fillAdapter();
                        isFirstFill = false;
                    }
                }
            }
        };

        G.onChannelGetMemberList = new OnChannelGetMemberList() {
            @Override
            public void onChannelGetMemberList(List<ProtoChannelGetMemberList.ChannelGetMemberListResponse.Member> members) {

                mMemberCount = members.size();

                if (mMemberCount > 0) {
                    listMembersChannal.clear();
                    for (final ProtoChannelGetMemberList.ChannelGetMemberListResponse.Member member : members) {
                        listMembersChannal.add(member);
                        new RequestUserInfo().userInfoWithCallBack(infoUpdateListenerCount, member.getUserId(), "" + member.getUserId());
                    }
                } else {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {

                            if (progressBar != null) {
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                    isOne = true;
                    if (isFirstFill) {
                        fillAdapter();
                        isFirstFill = false;
                    }
                }

            }

            @Override
            public void onError(int majorCode, int minorCode) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {

                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }

            @Override
            public void onTimeOut() {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {

                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        };
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                mCurrentUpdateCount = 0;
                DbManager.getInstance().doRealmTask(realm -> {
                    realm.executeTransactionAsync(realm1 -> RealmMember.deleteAllMembers(realm1, mRoomID, selectedRole), () -> {
                        if (roomType == GROUP) {
                            new RequestGroupGetMemberList().getMemberList(mRoomID, offset, limit, ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.valueOf(selectedRole));
                        } else {
                            new RequestChannelGetMemberList().channelGetMemberList(mRoomID, offset, limit, ProtoChannelGetMemberList.ChannelGetMemberList.FilterRole.valueOf(selectedRole));
                        }
                    });
                });

            }
        });
    }

    private void fillItem() {

        if (mCurrentUpdateCount >= mMemberCount) {
            if (!isOne && mCurrentUpdateCount > 0) isOne = true;
            try {
                if (isFirstFill) {
                    fillAdapter();
                    isFirstFill = false;
                }

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {

                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    private void initComponent(View view) {

        mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLeftIcon(R.string.back_icon)
                .setLogoShown(true)
                .setSearchBoxShown(true)
                .setListener(this);

        LinearLayout layoutToolbar = view.findViewById(R.id.fcm_layout_toolbar);
        layoutToolbar.addView(mHelperToolbar.getView());

        mBtnAdd = view.findViewById(R.id.fcm_lbl_add);

        realmRoom = DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomID).findFirst();
        });

        if (selectedRole.equals(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ALL.toString())) {
            if (isGroup) {
                mHelperToolbar.setDefaultTitle(context.getResources().getString(R.string.member));
                mBtnAdd.setText(context.getResources().getString(R.string.add_new_member));
            } else {
                mHelperToolbar.setDefaultTitle(context.getResources().getString(R.string.subscriber));
                mBtnAdd.setText(context.getResources().getString(R.string.add_new_subscriber));
            }

            if (!isShowAddButton) {
                mBtnAdd.setVisibility(View.GONE);
                view.findViewById(R.id.fcm_splitter_add).setVisibility(View.GONE);
            }

            mBtnAdd.setVisibility(realmRoomAccess != null && realmRoomAccess.isCanAddNewMember() ? View.VISIBLE : View.GONE);

        } else if (selectedRole.equals(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ADMIN.toString())) {

            mHelperToolbar.setDefaultTitle(context.getResources().getString(R.string.list_admin));
            mBtnAdd.setText(context.getResources().getString(R.string.add_admin));
            mBtnAdd.setVisibility(realmRoomAccess != null && realmRoomAccess.isCanAddNewAdmin() ? View.VISIBLE : View.GONE);
            if (realmRoom != null) {
                if (realmRoom.getGroupRoom() != null && realmRoom.getGroupRoom().getRole() == GroupChatRole.OWNER) {
                    mBtnAdd.setVisibility(View.VISIBLE);
                }

                if (realmRoom.getChannelRoom() != null && realmRoom.getChannelRoom().getRole() == ChannelChatRole.OWNER) {
                    mBtnAdd.setVisibility(View.VISIBLE);
                }
            }

        } else if (selectedRole.equals(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.MODERATOR.toString())) {

            mHelperToolbar.setDefaultTitle(context.getResources().getString(R.string.list_modereator));
            mBtnAdd.setText(context.getResources().getString(R.string.add_modereator));

            mBtnAdd.setVisibility(View.GONE);
            if (realmRoom != null) {
                if (realmRoom.getGroupRoom() != null && (realmRoom.getGroupRoom().getRole() == GroupChatRole.OWNER || realmRoom.getGroupRoom().getRole() == GroupChatRole.ADMIN)) {
                    mBtnAdd.setVisibility(View.VISIBLE);
                }

                if (realmRoom.getChannelRoom() != null && (realmRoom.getChannelRoom().getRole() == ChannelChatRole.OWNER || realmRoom.getChannelRoom().getRole() == ChannelChatRole.ADMIN)) {
                    mBtnAdd.setVisibility(View.VISIBLE);
                }
            }

        }

        mBtnAdd.setOnClickListener(v -> {

            if (selectedRole.equals(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.MODERATOR.toString())
                    || selectedRole.equals(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ADMIN.toString())) {
                openFragmentForAdd(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ALL.toString(), selectedRole);

            } else if (selectedRole.equals(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ALL.toString())) {

                goToAddMember();
            }


        });

        final EditText edtSearch = mHelperToolbar.getEditTextSearch();

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {
                RealmResults<RealmMember> searchMember = DbManager.getInstance().doRealmTask(realm -> {
                    return RealmMember.filterMember(realm, mRoomID, s.toString(), getUnselectRow(), selectedRole);
                });
                mAdapter = new MemberAdapter(searchMember, roomType, mMainRole, userID);
                mRecyclerView.setAdapter(mAdapter);
            }
        });

        mRecyclerView = view.findViewById(R.id.fcm_recycler_view_show_member);
        mRecyclerView.setItemViewCacheSize(100);
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(G.fragmentActivity));

        final PreCachingLayoutManager preCachingLayoutManager = new PreCachingLayoutManager(G.fragmentActivity, DeviceUtils.getScreenHeight(G.fragmentActivity));
        mRecyclerView.setLayoutManager(preCachingLayoutManager);

        progressBar = view.findViewById(R.id.fcg_prgWaiting);
        AppUtils.setProgresColler(progressBar);


        //TextView txtNumberOfMember = (TextView) view.findViewById(R.id.fcg_txt_member);

        //if (selectedRole.toString().equals(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.MODERATOR.toString())) {
        //    txtNumberOfMember.setText(G.fragmentActivity.getResources().getString(R.string.list_modereator));
        //} else if (selectedRole.toString().equals(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ADMIN.toString())) {
        //    txtNumberOfMember.setText(G.fragmentActivity.getResources().getString(R.string.list_admin));
        //} else {
        //    txtNumberOfMember.setText(G.fragmentActivity.getResources().getString(member));
        //}

        scrollListener = new EndlessRecyclerViewScrollListener(preCachingLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list

                if (progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE);
                }

                loadMoreMember();
            }
        };

        mRecyclerView.addOnScrollListener(scrollListener);
    }

    private void goToAddMember() {

        List<StructContactInfo> userList = Contacts.retrieve(null);
        RealmList<RealmMember> memberList = DbManager.getInstance().doRealmTask(realm -> {
            return RealmMember.getMembers(realm, mRoomID);
        });

        for (int i = 0; i < memberList.size(); i++) {
            for (int j = 0; j < userList.size(); j++) {
                if (userList.get(j).peerId == memberList.get(i).getPeerId()) {
                    userList.remove(j);
                    break;
                }
            }
        }

        if (getActivity() != null) {

            Fragment fragment = ShowCustomList.newInstance(userList, new OnSelectedList() {
                @Override
                public void getSelectedList(boolean result, String message, int countForShowLastMessage, final ArrayList<StructContactInfo> list) {

                    if (isGroup) {
                        for (int i = 0; i < list.size(); i++) {
                            new RequestGroupAddMember().groupAddMember(mRoomID, list.get(i).peerId, RealmRoomMessage.findCustomMessageId(mRoomID, countForShowLastMessage));
                        }
                    } else {
                        for (int i = 0; i < list.size(); i++) {
                            new RequestChannelAddMember().channelAddMember(mRoomID, list.get(i).peerId);
                        }
                    }
                }
            });
            Bundle bundle = new Bundle();
            bundle.putBoolean("DIALOG_SHOWING", true);
            bundle.putLong("COUNT_MESSAGE", 0);
            fragment.setArguments(bundle);

            new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
        }
    }

    private void loadMoreMember() {
        if (isOne) {
            isOne = false;
            mCurrentUpdateCount = 0;

            offset += limit;
            if (roomType == GROUP) {
                new RequestGroupGetMemberList().getMemberList(mRoomID, offset, limit, ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.valueOf(selectedRole));
            } else {
                new RequestChannelGetMemberList().channelGetMemberList(mRoomID, offset, limit, ProtoChannelGetMemberList.ChannelGetMemberList.FilterRole.valueOf(selectedRole));
            }
        }
    }

    private ArrayList<String> getUnselectRow() {
        ArrayList<String> result = new ArrayList<>();
        switch (showMemberMode) {
            case SELECT_FOR_ADD_ADMIN:
                result.add(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ADMIN.toString());
                result.add(ProtoGlobal.GroupRoom.Role.OWNER.toString());
                break;
            case SELECT_FOR_ADD_MODERATOR:
                result.add(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ADMIN.toString());
                result.add(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.MODERATOR.toString());
                result.add(ProtoGlobal.GroupRoom.Role.OWNER.toString());
                break;
        }

        return result;
    }

    private void fillAdapter() {

        if (roomType == GROUP) {
            role = RealmGroupRoom.detectMineRole(mRoomID).toString();
        } else {
            role = RealmChannelRoom.detectMineRole(mRoomID).toString();
        }

        RealmResults<RealmMember> realmMembers = DbManager.getInstance().doRealmTask(realm -> {
            return RealmMember.filterMember(realm, mRoomID, "", getUnselectRow(), selectedRole);
        });

        if (G.fragmentActivity != null) {
            mAdapter = new MemberAdapter(realmMembers, roomType, mMainRole, userID);
            mRecyclerView.setAdapter(mAdapter);

            //fastAdapter
            //mAdapter = new MemberAdapterA();
            //for (RealmMember member : mList) {
            //    mAdapter.add(new MemberItem(realmRoom.getType(), mMainRole, userID).setInfo(member).withIdentifier(member.getPeerId()));
            //}
        }

        realmMemberMe = DbManager.getInstance().doRealmTask(realm -> {
            return RealmMember.filterMember(realm, mRoomID, AccountManager.getInstance().getCurrentUser().getId());
        });

        realmMemberMe.addChangeListener((realmMembers1, changeSet) -> {
            try {
                mMainRole = realmMembers1.get(0).getRole();
                mAdapter.setMainRole(mMainRole);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

    }

    /**
     * *********************************** Callbacks ***********************************
     */

    @Override
    public void onGroupKickMember(long roomId, long memberId) {
        removeMember(roomId, memberId);
    }

    @Override
    public void onError(int majorCode, int minorCode) {

    }

    @Override
    public void onTimeOut() {

    }

    @Override
    public void onLeftIconClickListener(View view) {
        popBackStackFragment();
    }

    @Override
    public void onSearchClickListener(View view) {

        openKeyBoard();

    }

   /* @Override
    public void onSearchTextChangeListener(View view, String text) {
        RealmResults<RealmMember> searchMember = RealmMember.filterMember(mRoomID, text);
        mAdapter = new MemberAdapter(searchMember, roomType, mMainRole, userID);
        mRecyclerView.setAdapter(mAdapter);
    }*/

    private void openFragmentForAdd(String SelectedRole, String selectedRule) {
        if (getActivity() != null) {
            ShowMemberMode showMemberMode;
            if (selectedRule.equals(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ADMIN.toString())) {
                showMemberMode = ShowMemberMode.SELECT_FOR_ADD_ADMIN;
            } else {
                showMemberMode = ShowMemberMode.SELECT_FOR_ADD_MODERATOR;
            }
            FragmentShowMember fragment1 = FragmentShowMember.newInstance4(fragment, mRoomID, role, AccountManager.getInstance().getCurrentUser().getId(), SelectedRole, true, isGroup, false, showMemberMode);
            new HelperFragment(getActivity().getSupportFragmentManager(), fragment1).setReplace(false).load(false);
        }
    }


    @Override
    public void onBtnClearSearchClickListener(View view) {

        if (mHelperToolbar.getEditTextSearch().getText().toString().trim().length() > 0) {
            mHelperToolbar.getEditTextSearch().setText("");
        }

    }

    private void resetMemberState(final long roomId, final long memberId) {

    }

    private void removeMember(long roomId, final long memberId) {
        //G.handler.post(new Runnable() {
        //    @Override
        //    public void run() {
        //        mAdapter.remove(mAdapter.getPosition(memberId));
        //    }
        //});
    }

    class AsyncMember extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            getMemberList();
            return null;
        }
    }


    /**
     * **********************************************************************************
     * ********************************** RealmAdapter **********************************
     * **********************************************************************************
     */

    private class MemberAdapter extends RealmRecyclerViewAdapter<RealmMember, MemberAdapter.ViewHolder> {

        public String mainRole;
        public ProtoGlobal.Room.Type roomType;
        public long userId;

        public MemberAdapter(RealmResults<RealmMember> realmResults, ProtoGlobal.Room.Type roomType, String mainRole, long userId) {
            super(realmResults, true);
            this.roomType = roomType;
            this.mainRole = mainRole;
            this.userId = userId;
        }

        @NotNull
        @Override
        public MemberAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contact_item_group_profile, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(final MemberAdapter.ViewHolder holder, int i) {

            final StructContactInfo mContact = convertRealmToStruct(getItem(i));

            if (mContact == null) {
                return;
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity() != null)
                        switch (showMemberMode) {
                            case NONE:
                                if (mContact.peerId != userID) {
                                    long roomId = RealmRoom.getRoomIdByPeerId(mContact.peerId);
                                    if (roomId != 0) {
                                        new HelperFragment(getActivity().getSupportFragmentManager(), FragmentContactsProfile.newInstance(roomId, mContact.peerId, GROUP.toString())).setReplace(false).load();
                                    } else {
                                        new HelperFragment(getActivity().getSupportFragmentManager(), FragmentContactsProfile.newInstance(0, mContact.peerId, "Others")).setReplace(false).load();
                                    }
                                }
                                break;
                            case SELECT_FOR_ADD_ADMIN:
                                getActivity().onBackPressed();
                                openChatEditRightsFragment(realmRoom, mContact, 0);
                                break;
                        }
                }
            });

            holder.itemView.setOnLongClickListener(v -> {
                if (!role.equals(GroupChatRole.MEMBER.toString()))
                    showMemberDialog(mContact);
                return true;
            });

            if (mContact.isHeader) {
                holder.topLine.setVisibility(View.VISIBLE);
            } else {
                holder.topLine.setVisibility(View.GONE);
            }

            holder.title.setText(mContact.displayName);
            avatarHandler.getAvatar(new ParamWithAvatarType(holder.image, mContact.peerId).avatarType(AvatarHandler.AvatarType.USER));

            if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.OWNER.toString())) {
                holder.subtitle.setText("Owner");
            } else {
                holder.subtitle.setText(setUserStatus(holder.subtitle.getContext(), mContact.status, mContact.peerId, mContact.lastSeen));
            }

            if (realmRoomAccess.isCanAddNewAdmin() || realmRoomAccess.isCanBanMember() && !mContact.role.equals(ProtoGlobal.GroupRoom.Role.OWNER.toString())) {
                holder.btnMenu.setVisibility(View.VISIBLE);
            } else {
                holder.btnMenu.setVisibility(View.GONE);
            }

            holder.btnMenu.setOnClickListener(v -> {
                if (!role.equals(GroupChatRole.MEMBER.toString()))
                    showMemberDialog(mContact);
            });

            if (mContact.peerId == mContact.userID) {
                holder.btnMenu.setVisibility(View.GONE);
            }

            if (showMemberMode != ShowMemberMode.NONE) {
                holder.btnMenu.setVisibility(View.GONE);
            }
        }

        private String setUserStatus(Context context, String userStatus, long userId, long time) {

            if (userStatus != null) {
                if (userStatus.equals(ProtoGlobal.RegisteredUser.Status.EXACTLY.toString())) {
                    return LastSeenTimeUtil.computeTime(context, userId, time, false);
                } else {
                    return userStatus;
                }
            } else {
                return LastSeenTimeUtil.computeTime(context, userId, time, false);
            }
        }

        public void setMainRole(String role) {
            if (role != null) this.mainRole = role;
            notifyDataSetChanged();
        }

        StructContactInfo convertRealmToStruct(RealmMember realmMember) {
            return DbManager.getInstance().doRealmTask(realm -> {
                String role = realmMember.getRole();
                long id = realmMember.getPeerId();
                RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, id);
                if (realmRegisteredInfo != null) {
                    StructContactInfo s = new StructContactInfo(realmRegisteredInfo.getId(), realmRegisteredInfo.getDisplayName(), realmRegisteredInfo.getStatus(), false, false, realmRegisteredInfo.getPhoneNumber() + "");
                    s.role = role;
                    s.avatar = realmRegisteredInfo.getLastAvatar(realm);
                    s.initials = realmRegisteredInfo.getInitials();
                    s.color = realmRegisteredInfo.getColor();
                    s.lastSeen = realmRegisteredInfo.getLastSeen();
                    s.status = realmRegisteredInfo.getStatus();
                    s.userID = userId;
                    return s;
                }

                return null;
            });
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private CircleImageView image;
            private TextView title;
            private TextView subtitle;
            private View topLine;
            private TextView btnMenu;

            ViewHolder(View itemView) {
                super(itemView);

                image = itemView.findViewById(R.id.cigp_imv_contact_avatar);
                title = itemView.findViewById(R.id.cigp_txt_contact_name);
                subtitle = itemView.findViewById(R.id.cigp_txt_contact_lastseen);
                topLine = itemView.findViewById(R.id.cigp_view_topLine);
                btnMenu = itemView.findViewById(R.id.cigp_moreButton);

                title.setGravity(G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT);
                subtitle.setGravity(G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT);

            }
        }
    }

    private void showMemberDialog(StructContactInfo contactInfo) {

        if (realmRoomAccess != null) {
            TextCell adminRights = null;
            TextCell permission = null;
            TextCell removeView = null;
            LinearLayout dialogRootView = new LinearLayout(getContext());
            dialogRootView.setOrientation(LinearLayout.VERTICAL);
            dialogRootView.setBackgroundColor(Theme.getInstance().getRootColor(dialogRootView.getContext()));

            if (realmRoomAccess.isCanAddNewAdmin()) {
                adminRights = new TextCell(dialogRootView.getContext(), true);
                adminRights.setTextColor(Theme.getInstance().getTitleTextColor(dialogRootView.getContext()));
                adminRights.setValue(contactInfo.isAdmin() ? getResources().getString(R.string.edit_admin_rights) : getResources().getString(R.string.set_admin));
                dialogRootView.addView(adminRights, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 52));
            }

            if (!contactInfo.isAdmin() && roomType == GROUP) {
                permission = new TextCell(dialogRootView.getContext(), true);
                permission.setTextColor(Theme.getInstance().getTitleTextColor(dialogRootView.getContext()));
                permission.setValue(getResources().getString(R.string.edit_rights));
                dialogRootView.addView(permission, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 52));
            }

            if (realmRoomAccess.isCanBanMember()) {
                removeView = new TextCell(dialogRootView.getContext(), false);
                removeView.setTextColor(dialogRootView.getContext().getResources().getColor(R.color.red));
                removeView.setValue(getResources().getString(R.string.remove_user));
                dialogRootView.addView(removeView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 52));
            }

            Dialog dialog = new MaterialDialog.Builder(dialogRootView.getContext())
                    .customView(dialogRootView, false)
                    .build();

            if (removeView != null) {
                removeView.setOnClickListener(v -> {
                    kickMember(contactInfo.peerId);
                    dialog.dismiss();
                });
            }

            if (adminRights != null) {
                adminRights.setOnClickListener(v -> {
                    openChatEditRightsFragment(realmRoom, contactInfo, contactInfo.isAdmin() ? 1 : 0);
                    dialog.dismiss();
                });
            }

            if (permission != null) {
                permission.setOnClickListener(v -> {
                    openChatEditRightsFragment(realmRoom, contactInfo, 2);
                    dialog.dismiss();
                });
            }

            if (dialogRootView.getChildCount() > 0)
                dialog.show();

        }
    }

    private void openChatEditRightsFragment(RealmRoom realmRoom, StructContactInfo info, int mode) {
        if (getActivity() != null)
            new HelperFragment(getActivity().getSupportFragmentManager(), ChatRightsFragment.getIncense(realmRoom, role.equals(GroupChatRole.OWNER.toString()) ? null : realmRoomAccess, info.peerId, mode)).setReplace(false).load();
    }

    public void kickMember(long peerId) {
        if (getActivity() != null) {
            new MaterialDialog.Builder(getActivity())
                    .content(R.string.do_you_want_to_kick_this_member)
                    .positiveText(R.string.yes)
                    .negativeText(R.string.no)
                    .onPositive((dialog, which) -> {
                        if (roomType == CHANNEL) {
                            new RequestChannelKickMember().channelKickMember(mRoomID, peerId);
                        } else {
                            new RequestGroupKickMember().groupKickMember(mRoomID, peerId);
                        }
                    })
                    .show();
        }
    }

    @Override
    public void onDestroy() {
        if (realmMemberMe != null) realmMemberMe.removeAllChangeListeners();
        super.onDestroy();
    }
}
