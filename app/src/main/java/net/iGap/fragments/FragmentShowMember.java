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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.interfaces.OnChannelGetMemberList;
import net.iGap.interfaces.OnComplete;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.interfaces.OnGroupGetMemberList;
import net.iGap.interfaces.OnGroupKickMember;
import net.iGap.interfaces.OnSelectedList;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.libs.bottomNavigation.Util.Utils;
import net.iGap.module.AppUtils;
import net.iGap.module.CircleImageView;
import net.iGap.module.Contacts;
import net.iGap.module.CustomTextViewMedium;
import net.iGap.module.DeviceUtils;
import net.iGap.module.EndlessRecyclerViewScrollListener;
import net.iGap.module.LastSeenTimeUtil;
import net.iGap.module.PreCachingLayoutManager;
import net.iGap.module.enums.ChannelChatRole;
import net.iGap.module.enums.GroupChatRole;
import net.iGap.module.structs.StructContactInfo;
import net.iGap.proto.ProtoChannelGetMemberList;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoGroupGetMemberList;
import net.iGap.realm.RealmChannelRoom;
import net.iGap.realm.RealmGroupRoom;
import net.iGap.realm.RealmMember;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.request.RequestChannelAddAdmin;
import net.iGap.request.RequestChannelAddMember;
import net.iGap.request.RequestChannelAddModerator;
import net.iGap.request.RequestChannelGetMemberList;
import net.iGap.request.RequestGroupAddAdmin;
import net.iGap.request.RequestGroupAddMember;
import net.iGap.request.RequestGroupAddModerator;
import net.iGap.request.RequestGroupGetMemberList;
import net.iGap.request.RequestUserInfo;
import net.iGap.viewmodel.FragmentChannelProfileViewModel;
import net.iGap.viewmodel.FragmentGroupProfileViewModel;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

import static net.iGap.G.context;
import static net.iGap.G.inflater;
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
    private Realm mRealm;

    private Realm realmGroupProfile;
    private HelperToolbar mHelperToolbar;
    private TextView mBtnAdd;
    private boolean isGroup;
    private boolean isShowAddButton = true;


    public static FragmentShowMember newInstance(long roomId, String mainrool, long userid, String selectedRole, boolean isNeedGetMemberList) {
        return newInstance2(null, roomId, mainrool, userid, selectedRole, isNeedGetMemberList, false);
    }

    public static FragmentShowMember newInstance2(Fragment frg, long roomId, String mainrool, long userid, String selectedRole, boolean isNeedGetMemberList, boolean isGroup) {
        return newInstance3(frg, roomId, mainrool, userid, selectedRole, isNeedGetMemberList, isGroup, true);
    }

    public static FragmentShowMember newInstance3(Fragment frg, long roomId, String mainrool, long userid, String selectedRole, boolean isNeedGetMemberList, boolean isGroup , boolean isShowAddMemberButton) {
        return newInstance4(frg, roomId, mainrool, userid, selectedRole, isNeedGetMemberList, isGroup, isShowAddMemberButton, ShowMemberMode.NONE);
    }

    public static FragmentShowMember newInstance4(Fragment frg, long roomId, String mainrool, long userid, String selectedRole, boolean isNeedGetMemberList, boolean isGroup , boolean isShowAddMemberButton, ShowMemberMode showMemberMode) {

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
        mRealm = Realm.getDefaultInstance();
        return attachToSwipeBack(inflater.inflate(R.layout.fragment_show_member, container, false));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRealm.close();
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

            isShowAddButton = getArguments().getBoolean(ISSHOWADDMEMBER , true);

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

                            final Realm realm = Realm.getDefaultInstance();
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
                                    realm.close();

                                }
                            }, new Realm.Transaction.OnError() {
                                @Override
                                public void onError(Throwable error) {
                                    realm.close();
                                }
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
                RealmMember.deleteAllMembers(mRoomID, selectedRole);
                if (roomType == GROUP) {
                    new RequestGroupGetMemberList().getMemberList(mRoomID, offset, limit, ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.valueOf(selectedRole));
                } else {
                    new RequestChannelGetMemberList().channelGetMemberList(mRoomID, offset, limit, ProtoChannelGetMemberList.ChannelGetMemberList.FilterRole.valueOf(selectedRole));
                }
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
                .setLeftIcon(R.string.back_icon)
                .setLogoShown(true)
                .setSearchBoxShown(true)
                .setListener(this);

        LinearLayout layoutToolbar = view.findViewById(R.id.fcm_layout_toolbar);
        layoutToolbar.addView(mHelperToolbar.getView());

        mBtnAdd = view.findViewById(R.id.fcm_lbl_add);

        RealmRoom realmRoom = getRealm().where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomID).findFirst();

        //change toolbar title and set Add button text
        if (selectedRole.equals(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ALL.toString())) {


            if (isGroup) {
                mHelperToolbar.setDefaultTitle(context.getResources().getString(R.string.member));
                mBtnAdd.setText(context.getResources().getString(R.string.add_new_member));
            } else {
                mHelperToolbar.setDefaultTitle(context.getResources().getString(R.string.subscriber));
                mBtnAdd.setText(context.getResources().getString(R.string.add_new_subscriber));
            }

            if (!isShowAddButton){
                mBtnAdd.setVisibility(View.GONE);
                view.findViewById(R.id.fcm_splitter_add).setVisibility(View.GONE);
            }

        } else if (selectedRole.equals(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ADMIN.toString())) {

            mHelperToolbar.setDefaultTitle(context.getResources().getString(R.string.list_admin));
            mBtnAdd.setText(context.getResources().getString(R.string.add_admin));
            mBtnAdd.setVisibility(View.GONE);
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
                RealmResults<RealmMember> searchMember = RealmMember.filterMember(mRealm, mRoomID, s.toString(), getUnselectRow(), selectedRole);
                mAdapter = new MemberAdapter(searchMember, roomType, mMainRole, userID);
                mRecyclerView.setAdapter(mAdapter);
            }
        });

        mRecyclerView = (RecyclerView) view.findViewById(R.id.fcm_recycler_view_show_member);
        mRecyclerView.setItemViewCacheSize(100);
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(G.fragmentActivity));

        final PreCachingLayoutManager preCachingLayoutManager = new PreCachingLayoutManager(G.fragmentActivity, DeviceUtils.getScreenHeight(G.fragmentActivity));
        mRecyclerView.setLayoutManager(preCachingLayoutManager);

        progressBar = (ProgressBar) view.findViewById(R.id.fcg_prgWaiting);
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
        RealmList<RealmMember> memberList = RealmMember.getMembers(getRealm(), mRoomID);

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

        RealmResults<RealmMember> realmMembers = RealmMember.filterMember(mRealm, mRoomID, "", getUnselectRow(), selectedRole);

        if (G.fragmentActivity != null) {
            mAdapter = new MemberAdapter(realmMembers, roomType, mMainRole, userID);
            mRecyclerView.setAdapter(mAdapter);

            //fastAdapter
            //mAdapter = new MemberAdapterA();
            //for (RealmMember member : mList) {
            //    mAdapter.add(new MemberItem(realmRoom.getType(), mMainRole, userID).setInfo(member).withIdentifier(member.getPeerId()));
            //}
        }
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
            FragmentShowMember fragment1 = FragmentShowMember.newInstance4(fragment, mRoomID, role, G.userId, SelectedRole, true , isGroup , false, showMemberMode);
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
        //G.handler.post(new Runnable() {
        //    @Override
        //    public void run() {
        //        Realm realm = Realm.getDefaultInstance();
        //        RealmMember realmMember = realm.where(RealmMember.class).equalTo(RealmMemberFields.PEER_ID, memberId).findFirst();
        //        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        //        if (realmRoom != null && realmMember != null) {
        //            mAdapter.set(mAdapter.getPosition(memberId), new MemberItem(realmRoom.getType(), mMainRole, userID).setInfo(realmMember).withIdentifier(memberId));
        //        }
        //        realm.close();
        //    }
        //});
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

        @Override
        public MemberAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = inflater.inflate(R.layout.contact_item_group_profile, viewGroup, false);
            return new ViewHolder(v);
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
                    try {
                        if (getActivity() != null) {
                            HelperPermission.getStoragePermision(getActivity(), new OnGetPermission() {
                                @Override
                                public void Allow() {
                                    switch (showMemberMode) {
                                        case NONE:
                                            if (mContact.peerId == userID) {
                                                // bagi:// dont uncomment below line it has some bug
                                                //new HelperFragment(getActivity().getSupportFragmentManager(), new FragmentSetting()).setReplace(false).load();
                                            } else {
                                                new HelperFragment(getActivity().getSupportFragmentManager(), FragmentContactsProfile.newInstance(mRoomID, mContact.peerId, GROUP.toString())).setReplace(false).load();
                                            }
                                            break;
                                        case SELECT_FOR_ADD_MODERATOR:
                                            if (isGroup) {
                                                new RequestGroupAddModerator().groupAddModerator(mRoomID, mContact.peerId);
                                            } else {
                                                new RequestChannelAddModerator().channelAddModerator(mRoomID, mContact.peerId);
                                            }
                                            getActivity().onBackPressed();
                                            break;
                                        case SELECT_FOR_ADD_ADMIN:
                                            if (isGroup) {
                                                new RequestGroupAddAdmin().groupAddAdmin(mRoomID, mContact.peerId);
                                            } else {
                                                new RequestChannelAddAdmin().channelAddAdmin(mRoomID, mContact.peerId);
                                            }
                                            getActivity().onBackPressed();
                                            break;
                                    }
                                }

                                @Override
                                public void deny() {

                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    if (fragment instanceof FragmentGroupProfile) {

                        if (role.equals(GroupChatRole.OWNER.toString())) {

                            if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {

                                ((FragmentGroupProfile) fragment).kickMember(mContact.peerId);
                            } else if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.ADMIN.toString())) {

                                ((FragmentGroupProfile) fragment).kickAdmin(mContact.peerId);
                            } else if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.MODERATOR.toString())) {

                                ((FragmentGroupProfile) fragment).kickModerator(mContact.peerId);
                            }
                        } else if (role.equals(GroupChatRole.ADMIN.toString())) {

                            if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {
                                ((FragmentGroupProfile) fragment).kickMember(mContact.peerId);
                            } else if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.MODERATOR.toString())) {
                                ((FragmentGroupProfile) fragment).kickModerator(mContact.peerId);
                            }
                        } else if (role.equals(GroupChatRole.MODERATOR.toString())) {

                            if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {
                                ((FragmentGroupProfile) fragment).kickMember(mContact.peerId);
                            }
                        }
                    } else if (fragment instanceof FragmentChannelProfile) {

                        if (role.equals(GroupChatRole.OWNER.toString())) {

                            if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {

                                ((FragmentChannelProfile) fragment).kickMember(mContact.peerId);
                            } else if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.ADMIN.toString())) {

                                ((FragmentChannelProfile) fragment).kickAdmin(mContact.peerId);
                            } else if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.MODERATOR.toString())) {

                                ((FragmentChannelProfile) fragment).kickModerator(mContact.peerId);
                            }
                        } else if (role.equals(GroupChatRole.ADMIN.toString())) {

                            if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {
                                ((FragmentChannelProfile) fragment).kickMember(mContact.peerId);
                            } else if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.MODERATOR.toString())) {
                                ((FragmentChannelProfile) fragment).kickModerator(mContact.peerId);
                            }
                        } else if (role.equals(GroupChatRole.MODERATOR.toString())) {

                            if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {
                                ((FragmentChannelProfile) fragment).kickMember(mContact.peerId);
                            }
                        }
                    }

                    return true;
                }
            });

            if (mContact.isHeader) {
                holder.topLine.setVisibility(View.VISIBLE);
            } else {
                holder.topLine.setVisibility(View.GONE);
            }

            holder.title.setText(mContact.displayName);

            checkTheme(holder);
            setRoleStarColor(holder.roleStar, mContact);

            avatarHandler.getAvatar(new ParamWithAvatarType(holder.image, mContact.peerId).avatarType(AvatarHandler.AvatarType.USER));

            if (mContact.status != null) {
                if (mContact.status.equals(ProtoGlobal.RegisteredUser.Status.EXACTLY.toString())) {
                    holder.subtitle.setText(LastSeenTimeUtil.computeTime(mContact.peerId, mContact.lastSeen, false));
                } else {
                    holder.subtitle.setText(mContact.status);
                }
            }

            if (mainRole.equals(ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {
                holder.btnMenu.setVisibility(View.INVISIBLE);
            } else if (mainRole.equals(ProtoGlobal.GroupRoom.Role.MODERATOR.toString())) {

                if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.OWNER.toString()) || (mContact.role.equals(ProtoGlobal.GroupRoom.Role.ADMIN.toString())) || (mContact.role.equals(ProtoGlobal.GroupRoom.Role.MODERATOR.toString()))) {
                    holder.btnMenu.setVisibility(View.INVISIBLE);
                } else {
                    showPopup(holder, mContact);
                }
            } else if (mainRole.equals(ProtoGlobal.GroupRoom.Role.ADMIN.toString())) {

                if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.OWNER.toString()) || (mContact.role.equals(ProtoGlobal.GroupRoom.Role.ADMIN.toString()))) {
                    holder.btnMenu.setVisibility(View.INVISIBLE);
                } else {
                    showPopup(holder, mContact);
                }
            } else if (mainRole.equals(ProtoGlobal.GroupRoom.Role.OWNER.toString())) {
                showPopup(holder, mContact);
            }

            /**
             * don't allow for use dialog if this item
             * is for own user
             */
            if (mContact.peerId == mContact.userID) {
                holder.btnMenu.setVisibility(View.INVISIBLE);
            }

            if (showMemberMode != ShowMemberMode.NONE) {
                holder.btnMenu.setVisibility(View.GONE);
            }
        }

        private void checkTheme(ViewHolder holder) {

            Utils.darkModeHandler(holder.btnMenu);
            Utils.darkModeHandler(holder.title);
            Utils.darkModeHandlerGray(holder.subtitle);
            Utils.darkModeHandlerGray(holder.topLine);

        }

        private void showPopup(ViewHolder holder, final StructContactInfo mContact) {
            holder.btnMenu.setVisibility(View.VISIBLE);

            holder.btnMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (roomType == ProtoGlobal.Room.Type.CHANNEL) {
                        if (FragmentChannelProfileViewModel.onMenuClick != null) {
                            FragmentChannelProfileViewModel.onMenuClick.clicked(v, mContact);
                        }
                    } else if (roomType == GROUP) {
                        if (FragmentGroupProfileViewModel.onMenuClick != null) {
                            FragmentGroupProfileViewModel.onMenuClick.clicked(v, mContact);
                        }
                    }
                }
            });
        }

        private void setRoleStarColor(TextView view, StructContactInfo mContact) {

            view.setVisibility(View.GONE);

            if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.MODERATOR.toString())) {
                view.setVisibility(View.VISIBLE);
                view.setTextColor(Color.CYAN);
            } else if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.ADMIN.toString())) {
                view.setVisibility(View.VISIBLE);
                view.setTextColor(Color.GREEN);
            } else if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.OWNER.toString())) {
                view.setVisibility(View.VISIBLE);
                view.setTextColor(Color.BLUE);
            }
        }

        StructContactInfo convertRealmToStruct(RealmMember realmMember) {
            Realm realm = Realm.getDefaultInstance();
            String role = realmMember.getRole();
            long id = realmMember.getPeerId();
            RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, id);
            if (realmRegisteredInfo != null) {
                StructContactInfo s = new StructContactInfo(realmRegisteredInfo.getId(), realmRegisteredInfo.getDisplayName(), realmRegisteredInfo.getStatus(), false, false, realmRegisteredInfo.getPhoneNumber() + "");
                s.role = role;
                s.avatar = realmRegisteredInfo.getLastAvatar();
                s.initials = realmRegisteredInfo.getInitials();
                s.color = realmRegisteredInfo.getColor();
                s.lastSeen = realmRegisteredInfo.getLastSeen();
                s.status = realmRegisteredInfo.getStatus();
                s.userID = userId;
                realm.close();
                return s;
            }

            realm.close();
            return null;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            protected CircleImageView image;
            protected CustomTextViewMedium title;
            protected CustomTextViewMedium subtitle;
            protected View topLine;
            protected TextView txtNumberOfSharedMedia;
            protected TextView roleStar;
            protected TextView btnMenu;

            public ViewHolder(View itemView) {
                super(itemView);

                image =  itemView.findViewById(R.id.cigp_imv_contact_avatar);
                title =  itemView.findViewById(R.id.cigp_txt_contact_name);
                subtitle =  itemView.findViewById(R.id.cigp_txt_contact_lastseen);
                topLine = itemView.findViewById(R.id.cigp_view_topLine);
                txtNumberOfSharedMedia =  itemView.findViewById(R.id.cigp_txt_nomber_of_shared_media);
                roleStar =  itemView.findViewById(R.id.cigp_txt_member_role);
                btnMenu =  itemView.findViewById(R.id.cigp_moreButton);
            }
        }
    }


    private Realm getRealm() {
        if (realmGroupProfile == null || realmGroupProfile.isClosed()) {
            realmGroupProfile = Realm.getDefaultInstance();
        }
        return realmGroupProfile;
    }

}
