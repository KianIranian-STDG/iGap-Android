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

import android.arch.lifecycle.Observer;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentProviderOperation;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.FragmentContactsProfileBinding;
import net.iGap.dialog.bottomsheet.BottomSheetFragment;
import net.iGap.dialog.topsheet.TopSheetDialog;
import net.iGap.helper.GoToChatActivity;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.module.CircleImageView;
import net.iGap.module.DialogAnimation;
import net.iGap.module.structs.StructListOfContact;
import net.iGap.proto.ProtoUserReport;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestClientMuteRoom;
import net.iGap.request.RequestUserContactImport;
import net.iGap.request.RequestUserReport;
import net.iGap.viewmodel.FragmentContactsProfileViewModel;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.CLIPBOARD_SERVICE;
import static net.iGap.G.context;
import static net.iGap.module.Contacts.showLimitDialog;

//todo : fixed view mode and view and remove logic code from view
public class FragmentContactsProfile extends BaseFragment {

    private static final String ROOM_ID = "RoomId";
    private static final String PEER_ID = "peerId";
    private static final String ENTER_FROM = "enterFrom";
    /*private long userId = 0;
    private long roomId = 0;*/
    private String report;

    private FragmentContactsProfileBinding binding;
    private FragmentContactsProfileViewModel viewModel;
    private CircleImageView userAvatarImageView;

    public static FragmentContactsProfile newInstance(long roomId, long peerId, String enterFrom) {
        Bundle args = new Bundle();
        args.putLong(ROOM_ID, roomId);
        args.putLong(PEER_ID, peerId);
        args.putString(ENTER_FROM, enterFrom);
        FragmentContactsProfile fragment = new FragmentContactsProfile();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_contacts_profile, container, false);
        return attachToSwipeBack(binding.getRoot());
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        long userId = 0;
        long roomId = 0;
        String enterFrom = "";
        if (getArguments() != null) {
            userId = getArguments().getLong(PEER_ID);
            roomId = getArguments().getLong(ROOM_ID);
            enterFrom = getArguments().getString(ENTER_FROM);
        }
        viewModel = new FragmentContactsProfileViewModel(roomId, userId, enterFrom, avatarHandler);
        binding.setViewModel(viewModel);

        HelperToolbar t = HelperToolbar.create().setContext(getContext())
                .setLeftIcon(R.string.back_icon)
                .setRightIcons(R.string.more_icon, R.string.video_call_icon, R.string.voice_call_icon)
                .setContactProfile(true)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }

                    @Override
                    public void onRightIconClickListener(View view) {
                        viewModel.onMoreButtonClick();
                    }

                    @Override
                    public void onSecondRightIconClickListener(View view) {
                        viewModel.onVideoCallClick();
                    }

                    @Override
                    public void onThirdRightIconClickListener(View view) {
                        viewModel.onVoiceCallButtonClick();
                    }
                });

        binding.toolbar.addView(t.getView());

        userAvatarImageView = t.getGroupAvatar();

        userAvatarImageView.setOnClickListener(v -> viewModel.onImageClick());

        viewModel.menuVisibility.observe(this, visibility -> {
            if (visibility != null) {
                t.getRightButton().setVisibility(visibility);
            }
        });

        viewModel.videoCallVisibility.observe(this, visibility -> {
            if (visibility != null) {
                t.getSecondRightButton().setVisibility(visibility);
            }
        });

        viewModel.callVisibility.observe(this, visibility -> {
            if (visibility != null) {
                t.getThirdRightButton().setVisibility(visibility);
            }
        });

        //todo: fixed it and move to viewModel
        viewModel.isMuteNotificationChangeListener.observe(this, isChecked -> {
            binding.enableNotification.setChecked(isChecked);
            new RequestClientMuteRoom().muteRoom(viewModel.roomId, isChecked);
        });

        viewModel.contactName.observe(this,name->{
            if (name!=null){
                t.getGroupName().setText(name);
            }
        });

        viewModel.lastSeen.observe(this,lastSeen->{
            if (lastSeen!=null){
                t.getGroupMemberCount().setText(HelperCalander.unicodeManage(lastSeen));
            }
        });

        viewModel.goToChatPage.observe(this,userRoomId->{
            if (getActivity() != null && userRoomId != null) {
                new GoToChatActivity(userRoomId).startActivity(getActivity());
            }
        });

        if (viewModel.phone != null && (!viewModel.phone.get().equals("0")|| viewModel.showNumber.get())){
            t.getProfileTell().setText(viewModel.phone.get());
            t.getProfileTell().setOnClickListener(v -> viewModel.onPhoneNumberClick());
        }else {
            t.getProfileTell().setVisibility(View.GONE);
        }

        t.getProfileStatus().setText(viewModel.username.get());

        t.getProfileFabChat().setOnClickListener(v -> {
            viewModel.onClickGoToChat();
        });

        /*binding.chiFabSetPic.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(G.fabBottom)));
        binding.chiFabSetPic.setColorFilter(Color.WHITE);
        binding.chiFabSetPic.setOnClickListener(new View.OnClickListener() { //fab button
            @Override
            public void onClick(View view) {

                if (enterFrom.equals(ProtoGlobal.Room.Type.GROUP.toString()) || enterFrom.equals("Others")) { // Others is from FragmentMapUsers adapter

                    Realm realm = Realm.getDefaultInstance();
                    final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.CHAT_ROOM.PEER_ID, userId).findFirst();

                    if (realmRoom != null) {
                        new HelperFragment().removeAll(true);
                        new GoToChatActivity(realmRoom.getId()).startActivity();
                    } else {
                        G.onChatGetRoom = new OnChatGetRoom() {
                            @Override
                            public void onChatGetRoom(final ProtoGlobal.Room room) {
                                G.handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        new HelperFragment().removeAll(true);
                                        new GoToChatActivity(room.getId()).setPeerID(userId).startActivity();
                                        G.onChatGetRoom = null;
                                    }
                                });
                            }

                            @Override
                            public void onChatGetRoomTimeOut() {

                            }

                            @Override
                            public void onChatGetRoomError(int majorCode, int minorCode) {

                            }
                        };

                        new RequestChatGetRoom().chatGetRoom(userId);
                    }
                    realm.close();
                } else {
                    popBackStackFragment();
                }
            }
        });*/

        /*if (viewModel.showNumber.get()) {
            binding.chiLayoutNickname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (viewModel.contactName.get() == null) {
                        return;
                    }

                    final LinearLayout layoutNickname = new LinearLayout(G.fragmentActivity);
                    layoutNickname.setOrientation(LinearLayout.VERTICAL);

                    String splitNickname[] = viewModel.contactName.get().split(" ");
                    String firsName = "";
                    String lastName = "";
                    StringBuilder stringBuilder = null;
                    if (splitNickname.length > 1) {

                        lastName = splitNickname[splitNickname.length - 1];
                        stringBuilder = new StringBuilder();
                        for (int i = 0; i < splitNickname.length - 1; i++) {

                            stringBuilder.append(splitNickname[i]).append(" ");
                        }
                        firsName = stringBuilder.toString();
                    } else {
                        firsName = splitNickname[0];
                    }
                    final View viewFirstName = new View(G.fragmentActivity);
                    viewFirstName.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));

                    LinearLayout.LayoutParams viewParams = new AppBarLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2);

                    TextInputLayout inputFirstName = new TextInputLayout(G.fragmentActivity);
                    final EmojiEditTextE edtFirstName = new EmojiEditTextE(G.fragmentActivity);
                    edtFirstName.setHint(R.string.first_name);
                    edtFirstName.setImeOptions(EditorInfo.IME_ACTION_DONE | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
                    edtFirstName.setTypeface(G.typeface_IRANSansMobile);
                    edtFirstName.setText(firsName);
                    edtFirstName.setTextSize(TypedValue.COMPLEX_UNIT_PX, G.context.getResources().getDimension(R.dimen.dp14));
                    edtFirstName.setTextColor(G.context.getResources().getColor(R.color.text_edit_text));
                    edtFirstName.setHintTextColor(G.context.getResources().getColor(R.color.hint_edit_text));
                    edtFirstName.setPadding(0, 8, 0, 8);
                    edtFirstName.setSingleLine(true);
                    inputFirstName.addView(edtFirstName);
                    inputFirstName.addView(viewFirstName, viewParams);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        edtFirstName.setBackground(G.context.getResources().getDrawable(android.R.color.transparent));
                    }

                    final View viewLastName = new View(G.fragmentActivity);
                    viewLastName.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));

                    TextInputLayout inputLastName = new TextInputLayout(G.fragmentActivity);
                    final MEditText edtLastName = new MEditText(G.fragmentActivity);
                    edtLastName.setHint(R.string.last_name);
                    edtLastName.setTypeface(G.typeface_IRANSansMobile);
                    edtLastName.setText(lastName);
                    edtLastName.setTextSize(TypedValue.COMPLEX_UNIT_PX, G.context.getResources().getDimension(R.dimen.dp14));
                    edtLastName.setTextColor(G.context.getResources().getColor(R.color.text_edit_text));
                    edtLastName.setHintTextColor(G.context.getResources().getColor(R.color.hint_edit_text));
                    edtLastName.setPadding(0, 8, 0, 8);
                    edtLastName.setSingleLine(true);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        edtLastName.setBackground(G.context.getResources().getDrawable(android.R.color.transparent));
                    }
                    inputLastName.addView(edtLastName);
                    inputLastName.addView(viewLastName, viewParams);

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(0, 0, 0, 15);
                    LinearLayout.LayoutParams lastNameLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    lastNameLayoutParams.setMargins(0, 15, 0, 10);

                    layoutNickname.addView(inputFirstName, layoutParams);
                    layoutNickname.addView(inputLastName, lastNameLayoutParams);

                    final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.pu_nikname_profileUser))
                            .positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok))
                            .customView(layoutNickname, true)
                            .widgetColor(Color.parseColor(G.appBarColor))
                            .negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel))
                            .build();

                    final View positive = dialog.getActionButton(DialogAction.POSITIVE);
                    positive.setEnabled(false);

                    edtFirstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean b) {
                            if (b) {
                                viewFirstName.setBackgroundColor(Color.parseColor(G.appBarColor));
                            } else {
                                viewFirstName.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));
                            }
                        }
                    });

                    edtLastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean b) {
                            if (b) {
                                viewLastName.setBackgroundColor(Color.parseColor(G.appBarColor));
                            } else {
                                viewLastName.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));
                            }
                        }
                    });

                    final String finalFirsName = firsName;
                    edtFirstName.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                            if (!edtFirstName.getText().toString().equals(finalFirsName)) {
                                positive.setEnabled(true);
                            } else {
                                positive.setEnabled(false);
                            }
                        }
                    });

                    final String finalLastName = lastName;
                    edtLastName.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            if (!edtLastName.getText().toString().equals(finalLastName)) {
                                positive.setEnabled(true);
                            } else {
                                positive.setEnabled(false);
                            }
                        }
                    });

                    positive.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            long po = Long.parseLong(viewModel.phone.get());
                            String firstName = edtFirstName.getText().toString().trim();
                            String lastName = edtLastName.getText().toString().trim();
                            new RequestUserContactsEdit().contactsEdit(userId, po, firstName, lastName);
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }
            });
        }*/

        /*binding.chiAppbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                ViewGroup viewGroup = binding.chiRootCircleImage;
                if (verticalOffset < -5) {
                    viewGroup.animate().alpha(0).setDuration(700);
                    viewGroup.setVisibility(View.GONE);
                    binding.chiTxtTitleToolbarDisplayName.setVisibility(View.VISIBLE);
                    binding.chiTxtTitleToolbarDisplayName.animate().alpha(1).setDuration(300);
                    binding.chiTxtTitleToolbarLastSeen.setVisibility(View.VISIBLE);
                    binding.chiTxtTitleToolbarLastSeen.animate().alpha(1).setDuration(300);
                } else {
                    viewGroup.setVisibility(View.VISIBLE);
                    viewGroup.animate().alpha(1).setDuration(700);
                    binding.chiTxtTitleToolbarDisplayName.setVisibility(View.GONE);
                    binding.chiTxtTitleToolbarDisplayName.animate().alpha(0).setDuration(500);
                    binding.chiTxtTitleToolbarLastSeen.setVisibility(View.GONE);
                    binding.chiTxtTitleToolbarLastSeen.animate().alpha(0).setDuration(500);
                }
            }
        });*/

        viewModel.showMenu.observe(this, aBoolean -> {
            if (aBoolean != null && aBoolean) {
                showPopUp();
            }
        });

        viewModel.showPhoneNumberDialog.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean != null && aBoolean) {
                    try {
                        HelperPermission.getContactPermision(G.fragmentActivity, new OnGetPermission() {
                            @Override
                            public void Allow() {
                                showPopupPhoneNumber(t.getProfileTell(), viewModel.phone.get());
                            }

                            @Override
                            public void deny() {

                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        /*binding.chiLayoutSharedMedia.setOnClickListener(new View.OnClickListener() {// go to the ActivityMediaChanel
            @Override
            public void onClick(View view) {
                new HelperFragment(FragmentShearedMedia.newInstance(viewModel.shearedId)).setReplace(false).load();
            }
        });*/

        viewModel.showClearChatDialog.observe(this, aBoolean -> {
            if (aBoolean != null && aBoolean) {
                showAlertDialog(getString(R.string.clear_this_chat), getString(R.string.clear), getString(R.string.cancel));
            }
        });

        viewModel.goToCustomNotificationPage.observe(this, aBoolean -> {
            if (getActivity() != null && aBoolean != null && aBoolean) {
                FragmentNotification fragmentNotification = new FragmentNotification();
                Bundle bundle = new Bundle();
                bundle.putString("PAGE", "CONTACT");
                bundle.putLong("ID", viewModel.roomId);
                fragmentNotification.setArguments(bundle);
                new HelperFragment(getActivity().getSupportFragmentManager(), fragmentNotification).setReplace(false).load();
            }
        });

        viewModel.setAvatar.observe(this, aBoolean -> {
            if (aBoolean != null && aBoolean) {
                avatarHandler.getAvatar(new ParamWithAvatarType(userAvatarImageView, viewModel.userId).avatarSize(R.dimen.dp100).avatarType(AvatarHandler.AvatarType.USER).showMain());
            }
        });

        viewModel.showDeleteContactDialog.observe(this, aBoolean -> {
            if (aBoolean != null && aBoolean) {
                new MaterialDialog.Builder(G.fragmentActivity).title(R.string.to_delete_contact).content(R.string.delete_text).positiveText(R.string.B_ok).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        viewModel.deleteContact();
                    }
                }).negativeText(R.string.B_cancel).show();
            }
        });

        viewModel.showDialogReportContact.observe(this, aBoolean -> {
            if (aBoolean != null && aBoolean) {
                openDialogReport();
            }
        });

        viewModel.showDialogStartSecretChat.observe(this, aBoolean -> {
            if (aBoolean != null && aBoolean) {
                Toast.makeText(getContext(), "secret chat", Toast.LENGTH_LONG).show();
            }
        });

        viewModel.goToShowAvatarPage.observe(this, isCurrentUser -> {
            if (getActivity() != null && isCurrentUser != null) {
                FragmentShowAvatars fragment;
                if (isCurrentUser) {
                    fragment = FragmentShowAvatars.newInstance(viewModel.userId, FragmentShowAvatars.From.setting);
                } else {
                    fragment = FragmentShowAvatars.newInstance(viewModel.userId, FragmentShowAvatars.From.chat);
                }
                new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        viewModel.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        viewModel.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
    }

    /**
     * ************************************ methods ************************************
     */
    private void showPopupPhoneNumber(View v, String number) {
        try {
            boolean isExist = false;
            Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
            String[] mPhoneNumberProjection = {ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME};
            Cursor cur = context.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
            try {
                if (cur != null) {
                    isExist = cur.moveToFirst();
                }
            } finally {
                if (cur != null) cur.close();
            }

            if (isExist) {
                new MaterialDialog.Builder(G.fragmentActivity).title(R.string.phone_number).items(R.array.phone_number2).itemsCallback((dialog, view, which, text) -> {
                    switch (which) {
                        case 0:
                            String call = "+" + Long.parseLong(viewModel.phone.get());
                            try {
                                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                callIntent.setData(Uri.parse("tel:" + Uri.encode(call.trim())));
                                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(callIntent);
                            } catch (Exception ex) {
                                ex.getStackTrace();
                            }
                            break;
                        case 1:
                            String copy;
                            copy = viewModel.phone.get();
                            ClipboardManager clipboard = (ClipboardManager) G.fragmentActivity.getSystemService(CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("PHONE_NUMBER", copy);
                            clipboard.setPrimaryClip(clip);
                            break;
                    }
                }).show();
            } else {
                new MaterialDialog.Builder(G.fragmentActivity).title(R.string.phone_number).items(R.array.phone_number).itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which) {
                            case 0:

                                String name = viewModel.contactName.getValue();
                                String phone = "+" + viewModel.phone.get();

                                ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

                                ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI).withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null).withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());

                                //------------------------------------------------------ Names

                                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0).withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE).withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name).build());

                                //------------------------------------------------------ Mobile Number

                                ops.add(ContentProviderOperation.
                                        newInsert(ContactsContract.Data.CONTENT_URI)
                                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                                        .build());

                                try {
                                    G.context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                                    addContactToServer();
                                    Toast.makeText(G.context, R.string.save_ok, Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(G.context, G.fragmentActivity.getResources().getString(R.string.exception) + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                break;
                            case 1:

                                String call = "+" + Long.parseLong(viewModel.phone.get());
                                try {
                                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                    callIntent.setData(Uri.parse("tel:" + Uri.encode(call.trim())));
                                    callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(callIntent);
                                } catch (Exception ex) {

                                    ex.getStackTrace();
                                }
                                break;
                            case 2:

                                ClipboardManager clipboard = (ClipboardManager) G.fragmentActivity.getSystemService(CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("PHONE_NUMBER", viewModel.phone.get());
                                clipboard.setPrimaryClip(clip);

                                break;
                        }
                    }
                }).show();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * import contact to server with True force
     */
    private void addContactToServer() {

        if (RealmUserInfo.isLimitImportContacts()) {
            showLimitDialog();
            return;
        }

        List<StructListOfContact> contacts = new ArrayList<>();
        StructListOfContact contact = new StructListOfContact();
        contact.firstName = viewModel.firstName;
        contact.lastName = viewModel.lastName;
        contact.phone = viewModel.phone.get() + "";

        contacts.add(contact);

        new RequestUserContactImport().contactImport(contacts, true);
    }

    private void showPopUp() {
        new TopSheetDialog(getContext()).setListData(viewModel.items, -1, position -> viewModel.onMenuItemClick(position)).show();
    }

    private void openDialogReport() {
        //todo: fixed on click and handle in viewModel get list menu from viewModel
        List<String> items = new ArrayList<>();
        items.add(getString(R.string.st_Spam));
        items.add(getString(R.string.st_Abuse));
        items.add(getString(R.string.st_FakeAccount));
        items.add(getString(R.string.st_Other));
        new BottomSheetFragment().setData(items, -1, position -> {
            if (items.get(position).equals(getString(R.string.st_Spam))) {
                new RequestUserReport().userReport(viewModel.userId, ProtoUserReport.UserReport.Reason.SPAM, "");
            } else if (items.get(position).equals(getString(R.string.st_Abuse))) {
                new RequestUserReport().userReport(viewModel.userId, ProtoUserReport.UserReport.Reason.ABUSE, "");
            } else if (items.get(position).equals(getString(R.string.st_FakeAccount))) {
                new RequestUserReport().userReport(viewModel.userId, ProtoUserReport.UserReport.Reason.FAKE_ACCOUNT, "");
            } else if (items.get(position).equals(getString(R.string.st_Other))) {
                final MaterialDialog dialogReport = new MaterialDialog.Builder(G.fragmentActivity).title(R.string.report).inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE).alwaysCallInputCallback().input(G.context.getString(R.string.description), "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        // Do something
                        report = input.toString();
                        if (input.length() > 0) {
                            View positive = dialog.getActionButton(DialogAction.POSITIVE);
                            positive.setEnabled(true);
                        } else {
                            View positive = dialog.getActionButton(DialogAction.POSITIVE);
                            positive.setEnabled(false);
                        }
                    }
                }).positiveText(R.string.ok).onPositive((dialog, which) -> new RequestUserReport().userReport(viewModel.roomId, ProtoUserReport.UserReport.Reason.OTHER, report)).negativeText(R.string.cancel).build();

                final View positive = dialogReport.getActionButton(DialogAction.POSITIVE);
                positive.setEnabled(false);

                DialogAnimation.animationDown(dialogReport);

                dialogReport.show();
            }
        }).show(getFragmentManager(), "bottom sheet");

        G.onReport = () -> error(G.fragmentActivity.getResources().getString(R.string.st_send_report));

    }

    private void showAlertDialog(String message, String positive, String negative) { // alert dialog for block or clear user
        new MaterialDialog.Builder(G.fragmentActivity).title(R.string.clear_history).content(message).positiveText(positive).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
                clearHistory();
                if (FragmentChat.onComplete != null) {
                    FragmentChat.onComplete.complete(false, viewModel.roomId + "", "");
                }
            }
        }).negativeText(negative).show();
    }

    private void clearHistory() {
        RealmRoomMessage.clearHistoryMessage(viewModel.shearedId);
    }

    private void error(String error) {
        if (isAdded()) {
            try {
                final Snackbar snack = Snackbar.make(G.fragmentActivity.findViewById(android.R.id.content), error, Snackbar.LENGTH_LONG);
                snack.setAction(G.fragmentActivity.getResources().getString(R.string.cancel), view -> snack.dismiss());
                snack.show();
            } catch (IllegalStateException e) {
                e.getStackTrace();
            }
        }
    }
}
