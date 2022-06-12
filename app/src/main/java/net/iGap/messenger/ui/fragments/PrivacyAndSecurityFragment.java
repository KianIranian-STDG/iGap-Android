package net.iGap.messenger.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.fragments.FragmentDeleteAccount;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.theme.Theme;
import net.iGap.messenger.theme.ThemeDescriptor;
import net.iGap.messenger.ui.cell.HeaderCell;
import net.iGap.messenger.ui.cell.TextInfoPrivacyCell;
import net.iGap.messenger.ui.cell.TextSettingsCell;
import net.iGap.messenger.ui.toolBar.Toolbar;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.customView.RecyclerListView;
import net.iGap.observers.interfaces.TwoStepVerificationGetPasswordDetail;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmPrivacy;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestUserContactsGetBlockedList;
import net.iGap.request.RequestUserProfileSetSelfRemove;
import net.iGap.request.RequestUserTwoStepVerificationGetPasswordDetail;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmChangeListener;
import io.realm.RealmModel;

public class PrivacyAndSecurityFragment extends BaseFragment {

    private final int sectionOneHeaderRow = 0;
    private final int blockUserListRow = 1;
    private final int seenAvatarRow = 2;
    private final int inviteChannelRow = 3;
    private final int inviteGroupRow = 4;
    private final int voiceCallRow = 5;
    private final int videoCallRow = 6;
    private final int lastOnlineRow = 7;
    private final int sectionOneDividerRow = 8;
    private final int sectionTwoHeaderRow = 9;
    private final int passCodeLockRow = 10;
    private final int twoStepVerificationRow = 11;
    private final int activeSessionRow = 12;
    private final int sectionTwoDividerRow = 13;
    private final int sectionThreeHeaderRow = 14;
    private final int autoDeleteAccountRow = 15;
    private final int deleteAccountRow = 16;
    private final int sectionThreeDividerRow = 17;
    private final int rowCount = 18;

    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private RealmPrivacy realmPrivacy;
    private RealmUserInfo realmUserInfo;
    private int blockUserCount;
    private String twoStepVerification;
    private boolean loading = true;
    private RealmChangeListener<RealmModel> userInfoListener;
    private RealmChangeListener<RealmModel> privacyListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        getPrivacyAndSecurityData();
    }

    @Override
    public View createView(Context context) {
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
            if (view instanceof TextSettingsCell) {
                if (position == blockUserListRow) {
                    new HelperFragment(getActivity().getSupportFragmentManager(), new BlockedUserFragment()).setReplace(true).load();
                } else if (position == seenAvatarRow) {
                    showSettingDialog(ProtoGlobal.PrivacyType.AVATAR, R.string.title_who_can_see_my_avatar, view);
                } else if (position == inviteChannelRow) {
                    showSettingDialog(ProtoGlobal.PrivacyType.CHANNEL_INVITE, R.string.title_who_can_invite_you_to_channel_s, view);
                } else if (position == inviteGroupRow) {
                    showSettingDialog(ProtoGlobal.PrivacyType.GROUP_INVITE, R.string.title_who_can_invite_you_to_group_s, view);
                } else if (position == voiceCallRow) {
                    showSettingDialog(ProtoGlobal.PrivacyType.VOICE_CALLING, R.string.title_who_is_allowed_to_call, view);
                } else if (position == videoCallRow) {
                    showSettingDialog(ProtoGlobal.PrivacyType.VIDEO_CALLING, R.string.title_who_is_allowed_to_call, view);
                } else if (position == lastOnlineRow) {
                    showSettingDialog(ProtoGlobal.PrivacyType.USER_STATUS, R.string.title_Last_Seen, view);
                } else if (position == passCodeLockRow) {
                    new HelperFragment(getActivity().getSupportFragmentManager(), new LockSettingFragment()).setReplace(false).load();
                } else if (position == twoStepVerificationRow) {
                    new HelperFragment(getActivity().getSupportFragmentManager(), new TwoStepVerificationFragment(twoStepVerification)).setReplace(false).load();
                } else if (position == activeSessionRow) {
                    new HelperFragment(getActivity().getSupportFragmentManager(), new ActiveSessionsFragment()).setReplace(false).load();
                } else if (position == autoDeleteAccountRow) {
                    showAutoDeleteAccount(view);
                } else if (position == deleteAccountRow) {
                    showDeleteAccountDialog();
                }
            }
        });
        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (realmUserInfo != null && userInfoListener != null) {
            realmUserInfo.addChangeListener(userInfoListener);
        }

        if (realmPrivacy == null) {
            RealmPrivacy.fillWithDefaultValues(() -> {
                realmPrivacy = DbManager.getInstance().doRealmTask(realm -> {
                    return realm.where(RealmPrivacy.class).findFirst();
                });
                if (privacyListener != null && realmPrivacy != null) {
                    realmPrivacy.addChangeListener(privacyListener);
                }
            });
        }

        if (privacyListener != null && realmPrivacy != null) {
            realmPrivacy.addChangeListener(privacyListener);
        }

        userInfoListener = element -> {
            if (listAdapter != null){
                listAdapter.notifyDataSetChanged();
            }
        };

        privacyListener = element -> {
            if (listAdapter != null){
                listAdapter.notifyDataSetChanged();
            }
        };

        loading = false;
        if (listAdapter != null){
            listAdapter.notifyDataSetChanged();
        }
    }

    private void showDeleteAccountDialog() {
        String title = G.context.getString(R.string.delete_account);
        String content = G.context.getString(R.string.delete_account_text) + "\n" + G.context.getString(R.string.delete_account_text_desc);
        int icon = R.string.icon_delete_minus;
        View.OnClickListener onOk = v -> {
            if (getActivity() != null) {
                FragmentDeleteAccount fragmentDeleteAccount = FragmentDeleteAccount.getInstance(AccountManager.getInstance().getCurrentUser().getPhoneNumber());
                new HelperFragment(getActivity().getSupportFragmentManager(), fragmentDeleteAccount).setReplace(false).load();
            }
        };
        View.OnClickListener onCancel = null;

        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }

        MaterialDialog inDialog = new MaterialDialog.Builder(getActivity())
                .backgroundColor(Theme.getColor(Theme.key_popup_background))
                .customView(R.layout.dialog_content_custom, true)
                .negativeColor(Theme.getColor(Theme.key_button_background))
                .positiveColor(Theme.getColor(Theme.key_button_background)).build();
        View v = inDialog.getCustomView();

        inDialog.show();

        TextView txtTitle = v.findViewById(R.id.txtDialogTitle);
        txtTitle.setText(title);

        TextView iconTitle = v.findViewById(R.id.iconDialogTitle);
        iconTitle.setText(icon);

        TextView txtContent = v.findViewById(R.id.txtDialogContent);
        txtContent.setText(content);

        TextView txtCancel = v.findViewById(R.id.txtDialogCancel);
        TextView txtOk = v.findViewById(R.id.txtDialogOk);


        txtOk.setOnClickListener(v1 -> {
            inDialog.dismiss();
            onOk.onClick(v1);
        });

        txtCancel.setOnClickListener(v12 -> {
            inDialog.dismiss();
        });
    }

    private void getPrivacyAndSecurityData() {
        new RequestUserContactsGetBlockedList().userContactsGetBlockedList();
        RealmPrivacy.getUpdatePrivacyFromServer();
        new RequestUserTwoStepVerificationGetPasswordDetail().getPasswordDetail(new TwoStepVerificationGetPasswordDetail() {
            @Override
            public void getDetailPassword(String questionOne, String questionTwo, String hint, boolean hasConfirmedEmail, String unconfirmedEmailPattern) {
                twoStepVerification = getString(R.string.Enable);
            }

            @Override
            public void errorGetPasswordDetail(int majorCode, int minorCode) {
                if (majorCode == 188 && minorCode == 1) {
                    twoStepVerification = getString(R.string.Disable);
                }
            }
        });
        realmPrivacy = DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmPrivacy.class).findFirst();
        });
        realmUserInfo = DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmUserInfo.class).findFirst();
        });
        blockUserCount = DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmRegisteredInfo.class).equalTo("blockUser", true).findAll().size();
        });
    }

    private void showSettingDialog(final ProtoGlobal.PrivacyType privacyType, int title, View view) {
        new MaterialDialog.Builder(context)
                .backgroundColor(Theme.getColor(Theme.key_popup_background))
                .title(title)
                .titleGravity(GravityEnum.START)
                .items(R.array.privacy_setting_array)
                .negativeColor(Theme.getColor(Theme.key_button_background))
                .positiveColor(Theme.getColor(Theme.key_button_background))
                .choiceWidgetColor(ColorStateList.valueOf(Theme.getColor(Theme.key_button_background)))
                .alwaysCallSingleChoiceCallback()
                .positiveText(R.string.B_ok).negativeText(R.string.B_cancel)
                .itemsCallback((dialog, itemView, position, text) -> {
                    switch (position) {
                        case 0: {
                            RealmPrivacy.sendUpdatePrivacyToServer(privacyType, ProtoGlobal.PrivacyLevel.ALLOW_ALL);
                            ((TextSettingsCell) view).setTextAndValue(context.getString(title), G.fragmentActivity.getResources().getString(R.string.everybody), true);
                            break;
                        }
                        case 1: {
                            RealmPrivacy.sendUpdatePrivacyToServer(privacyType, ProtoGlobal.PrivacyLevel.ALLOW_CONTACTS);
                            ((TextSettingsCell) view).setTextAndValue(context.getString(title), G.fragmentActivity.getResources().getString(R.string.my_contacts), true);
                            break;
                        }
                        case 2: {
                            RealmPrivacy.sendUpdatePrivacyToServer(privacyType, ProtoGlobal.PrivacyLevel.DENY_ALL);
                            ((TextSettingsCell) view).setTextAndValue(context.getString(title), G.fragmentActivity.getResources().getString(R.string.no_body), true);
                            break;
                        }
                    }
                })
                .show();
    }

    private String getPrivacyTypeString(String str) {
        if (str == null || str.length() == 0) {
            return G.fragmentActivity.getResources().getString(R.string.everybody);
        }
        int resString = 0;
        if (str.equals(ProtoGlobal.PrivacyLevel.ALLOW_ALL.toString())) {
            resString = R.string.everybody;
        } else if (str.equals(ProtoGlobal.PrivacyLevel.ALLOW_CONTACTS.toString())) {
            resString = R.string.my_contacts;
        } else {
            resString = R.string.no_body;
        }
        return G.fragmentActivity.getResources().getString(resString);
    }

    private void showAutoDeleteAccount(View view) {
        new MaterialDialog.Builder(context)
                .backgroundColor(Theme.getColor(Theme.key_popup_background))
                .title(G.fragmentActivity.getResources().getString(R.string.self_destructs))
                .titleGravity(GravityEnum.START)
                .items(R.array.account_self_destruct)
                .negativeColor(Theme.getColor(Theme.key_button_background))
                .positiveColor(Theme.getColor(Theme.key_button_background))
                .choiceWidgetColor(ColorStateList.valueOf(Theme.getColor(Theme.key_button_background)))
                .alwaysCallSingleChoiceCallback()
                .positiveText(R.string.B_ok).negativeText(R.string.B_cancel)
                .itemsCallback((dialog, itemView, position, text) -> {
                    switch (position) {
                        case 0: {
                            new RequestUserProfileSetSelfRemove().userProfileSetSelfRemove(1);
                            ((TextSettingsCell) view).setTextAndValue(context.getString(R.string.if_you_are_away_for), G.fragmentActivity.getResources().getString(R.string.month_1), true);
                            break;
                        }
                        case 1: {
                            new RequestUserProfileSetSelfRemove().userProfileSetSelfRemove(3);
                            ((TextSettingsCell) view).setTextAndValue(context.getString(R.string.if_you_are_away_for), G.fragmentActivity.getResources().getString(R.string.month_3), true);
                            break;
                        }
                        case 2: {
                            new RequestUserProfileSetSelfRemove().userProfileSetSelfRemove(6);
                            ((TextSettingsCell) view).setTextAndValue(context.getString(R.string.if_you_are_away_for), G.fragmentActivity.getResources().getString(R.string.month_6), true);
                            break;
                        }
                        case 3: {
                            new RequestUserProfileSetSelfRemove().userProfileSetSelfRemove(12);
                            ((TextSettingsCell) view).setTextAndValue(context.getString(R.string.if_you_are_away_for), G.fragmentActivity.getResources().getString(R.string.year_1), true);
                            break;
                        }
                    }
                })
                .show();
    }

    private String getSelfRemoveString(int selfRemove) {
        if (selfRemove != 0) {
            switch (selfRemove) {
                case 1:
                    return G.fragmentActivity.getResources().getString(R.string.month_1);
                case 3:
                    return G.fragmentActivity.getResources().getString(R.string.month_3);
                case 6:
                    return G.fragmentActivity.getResources().getString(R.string.month_6);
                case 12:
                    return G.fragmentActivity.getResources().getString(R.string.year_1);
            }
        }
        return G.fragmentActivity.getResources().getString(R.string.month_6);
    }

    @SuppressLint("ResourceType")
    @Override
    public View createToolBar(Context context) {
        toolbar = new Toolbar(context);
        toolbar.setTitle(context.getString(R.string.PrivacySettings));
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
            return type != 0 && type != 2;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 0:
                    view = new HeaderCell(context);
                    break;
                case 1:
                    view = new TextSettingsCell(context);
                    break;
                case 2:
                    view = new TextInfoPrivacyCell(context);
                    view.setBackground(Theme.getThemedDrawable(context, R.drawable.greydivider_bottom, Theme.key_line));
                    view.setBackgroundColor(Theme.getColor(Theme.key_line));
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
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == sectionOneHeaderRow) {
                        headerCell.setText(getString(R.string.privacy));
                    } else if (position == sectionTwoHeaderRow) {
                        headerCell.setText(getString(R.string.Security));
                    } else if (position == sectionThreeHeaderRow) {
                        headerCell.setText(getString(R.string.Account_Self_destruction));
                    }
                    break;
                case 1:
                    TextSettingsCell textSettingsCell = (TextSettingsCell) holder.itemView;
                    textSettingsCell.setTextColor(Theme.getColor(Theme.key_default_text));
                    if (position == blockUserListRow) {
                        textSettingsCell.setTextAndValue(context.getString(R.string.Block_Users), String.format("%d", blockUserCount), true);
                    } else if (position == seenAvatarRow) {
                        textSettingsCell.setTextAndValue(context.getString(R.string.who_can_see_my_avatar), (realmPrivacy != null) ? getPrivacyTypeString(realmPrivacy.getWhoCanSeeMyAvatar()) : getPrivacyTypeString("") , true);
                    } else if (position == inviteChannelRow) {
                        textSettingsCell.setTextAndValue(context.getString(R.string.who_can_invite_you_to_channel_s), (realmPrivacy != null) ? getPrivacyTypeString(realmPrivacy.getWhoCanInviteMeToChannel()) : getPrivacyTypeString("") , true);
                    } else if (position == inviteGroupRow) {
                        textSettingsCell.setTextAndValue(context.getString(R.string.who_can_invite_you_to_group_s), (realmPrivacy != null) ? getPrivacyTypeString(realmPrivacy.getWhoCanInviteMeToGroup()) : getPrivacyTypeString("") , true);
                    } else if (position == voiceCallRow) {
                        textSettingsCell.setTextAndValue(context.getString(R.string.who_is_allowed_to_voice_call), (realmPrivacy != null) ? getPrivacyTypeString(realmPrivacy.getWhoCanVoiceCallToMe()) : getPrivacyTypeString("") , true);
                    } else if (position == videoCallRow) {
                        textSettingsCell.setTextAndValue(context.getString(R.string.who_is_allowed_to_video_call), (realmPrivacy != null) ? getPrivacyTypeString(realmPrivacy.getWhoCanVideoCallToMe()) : getPrivacyTypeString("") , true);
                    } else if (position == lastOnlineRow) {
                        textSettingsCell.setTextAndValue(context.getString(R.string.Last_Seen), (realmPrivacy != null) ? getPrivacyTypeString(realmPrivacy.getWhoCanSeeMyLastSeen()) : getPrivacyTypeString("") , false);
                    } else if (position == passCodeLockRow) {
                        textSettingsCell.setTextAndValue(context.getString(R.string.PassCode_Lock), "", true);
                    } else if (position == twoStepVerificationRow) {
                        textSettingsCell.setTextAndValue(context.getString(R.string.Two_Step_Verification), twoStepVerification, true);
                    } else if (position == activeSessionRow) {
                        textSettingsCell.setTextAndValue(context.getString(R.string.ActiveSessions), "", false);
                    } else if (position == autoDeleteAccountRow) {
                        textSettingsCell.setTextAndValue(context.getString(R.string.if_you_are_away_for), getSelfRemoveString(realmUserInfo.getSelfRemove()), false);
                    } else if (position == deleteAccountRow) {
                        textSettingsCell.setTextAndValue(context.getString(R.string.delete_account),"", false);
                    }
                    break;
                case 2:
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == sectionOneDividerRow) {
                        textInfoPrivacyCell.setText(getString(R.string.privacy_group_channel));
                    } else if (position == sectionTwoDividerRow) {
                        textInfoPrivacyCell.setText(getString(R.string.privacy_active_session));
                    } else if (position == sectionThreeDividerRow) {
                        textInfoPrivacyCell.setText(context.getString(R.string.desc_self_destroy));
                    }
                    break;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == sectionOneHeaderRow || position == sectionTwoHeaderRow || position == sectionThreeHeaderRow) {
                return 0;
            } else if (position == blockUserListRow || position == seenAvatarRow || position == inviteChannelRow || position == inviteGroupRow || position == voiceCallRow || position == videoCallRow ||
                    position == lastOnlineRow || position == passCodeLockRow || position == twoStepVerificationRow || position == activeSessionRow || position == autoDeleteAccountRow || position == deleteAccountRow) {
                return 1;
            } else if (position == sectionOneDividerRow || position == sectionTwoDividerRow || position == sectionThreeDividerRow) {
                return 2;
            }
            return position;
        }

        @Override
        public int getItemCount() {
            return loading ? 0 : rowCount;
        }
    }
}
