package com.iGap.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.IntentRequests;
import com.iGap.R;
import com.iGap.activities.ActivityCrop;
import com.iGap.activities.ActivityMain;
import com.iGap.helper.HelperAvatar;
import com.iGap.helper.HelperImageBackColor;
import com.iGap.helper.HelperPermision;
import com.iGap.interfaces.OnAvatarAdd;
import com.iGap.interfaces.OnChannelAvatarAdd;
import com.iGap.interfaces.OnChannelCreate;
import com.iGap.interfaces.OnChatConvertToGroup;
import com.iGap.interfaces.OnClientGetRoomResponse;
import com.iGap.interfaces.OnFileUploadForActivities;
import com.iGap.interfaces.OnGetPermision;
import com.iGap.interfaces.OnGroupAvatarResponse;
import com.iGap.interfaces.OnGroupCreate;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.AndroidUtils;
import com.iGap.module.CircleImageView;
import com.iGap.module.FileUploadStructure;
import com.iGap.module.LinedEditText;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.proto.ProtoClientGetRoom;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmAvatar;
import com.iGap.realm.RealmAvatarFields;
import com.iGap.realm.RealmChannelRoom;
import com.iGap.realm.RealmGroupRoom;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.iGap.realm.RealmUserInfo;
import com.iGap.realm.enums.GroupChatRole;
import com.iGap.realm.enums.RoomType;
import com.iGap.request.RequestChannelAvatarAdd;
import com.iGap.request.RequestChannelCreate;
import com.iGap.request.RequestChatConvertToGroup;
import com.iGap.request.RequestClientGetRoom;
import com.iGap.request.RequestGroupAvatarAdd;
import com.iGap.request.RequestGroupCreate;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import io.realm.Realm;

import static com.iGap.R.id.fragmentContainer;
import static com.iGap.module.MusicPlayer.roomId;

public class FragmentNewGroup extends Fragment implements OnFileUploadForActivities, OnGroupAvatarResponse, OnChannelAvatarAdd {

    private MaterialDesignTextView txtBack;
    private CircleImageView imgCircleImageView;
    private Uri uriIntent;
    private TextView txtNextStep, txtCancel, txtTitleToolbar;
    private static String prefix = "NewGroup";
    private long groomId = 0;
    private String path;
    private RelativeLayout parent;

    private EditText edtGroupName;
    private LinedEditText edtDescription;

    private int lastSpecialRequestsCursorPosition = 0;
    private String specialRequests;

    private static ProgressBar prgWaiting;
    private static long avatarId = 0;
    private static ProtoGlobal.Room.Type type;
    private String token;
    private boolean existAvatar = false;

    public static FragmentNewGroup newInstance() {
        return new FragmentNewGroup();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_new_group, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getIntentData(this.getArguments());
        initComponent(view);
    }

    private void getIntentData(Bundle bundle) {

        if (bundle != null) { // get a list of image
            prefix = bundle.getString("TYPE");
            if (bundle.getLong("ROOMID") != 0) {
                groomId = bundle.getLong("ROOMID");
            }
        }
    }

    private void showDialogSelectGallary() {
        new MaterialDialog.Builder(getActivity()).title(getString(R.string.choose_picture))
                .negativeText(getString(R.string.cancel))
                .items(R.array.profile)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                        switch (which) {
                            case 0: {
                                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(intent, IntentRequests.REQ_GALLERY);
                                dialog.dismiss();
                                break;

                            }
                            case 1: {

                                if (G.context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {

                                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    if (prefix.equals("NewChanel")) {
                                        uriIntent = Uri.fromFile(G.IMAGE_NEW_CHANEL);
                                    } else {
                                        uriIntent = Uri.fromFile(G.IMAGE_NEW_GROUP);
                                    }

                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uriIntent);
                                    startActivityForResult(intent, IntentRequests.REQ_CAMERA);
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(G.context, R.string.please_check_your_camera, Toast.LENGTH_SHORT).show();
                                }
                                break;
                            }
                        }

                    }
                })
                .show();
    }

    public void initComponent(View view) {
        G.uploaderUtil.setActivityCallbacks(this);
        G.onGroupAvatarResponse = this;
        G.onChannelAvatarAdd = this;

        prgWaiting = (ProgressBar) view.findViewById(R.id.ng_prgWaiting);
        prgWaiting.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.toolbar_background), android.graphics.PorterDuff.Mode.MULTIPLY);

        prgWaiting.setVisibility(View.GONE);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        txtBack = (MaterialDesignTextView) view.findViewById(R.id.ng_txt_back);
        RippleView rippleBack = (RippleView) view.findViewById(R.id.ng_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                InputMethodManager imm = (InputMethodManager) G.context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(rippleView.getWindowToken(), 0);
                if (G.IMAGE_NEW_GROUP.exists()) {
                    G.IMAGE_NEW_GROUP.delete();
                } else {
                    G.IMAGE_NEW_CHANEL.delete();
                }
                getActivity().getSupportFragmentManager().beginTransaction().remove(FragmentNewGroup.this).commit();
            }
        });

        txtTitleToolbar = (TextView) view.findViewById(R.id.ng_txt_titleToolbar);
        if (prefix.equals("NewChanel")) {
            txtTitleToolbar.setText(getResources().getString(R.string.New_Chanel));
        } else if (prefix.equals("ConvertToGroup")) {
            txtTitleToolbar.setText(getResources().getString(R.string.chat_to_group));
        }

        parent = (RelativeLayout) view.findViewById(R.id.ng_fragmentContainer);
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //=======================set image for group
        imgCircleImageView = (CircleImageView) view.findViewById(R.id.ng_profile_circle_image);

        RippleView rippleCircleImage = (RippleView) view.findViewById(R.id.ng_ripple_circle_image);
        rippleCircleImage.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {

                HelperPermision.getStoragePermision(getActivity(), new OnGetPermision() {
                    @Override
                    public void Allow() {
                        showDialogSelectGallary();
                    }
                });

            }
        });

        if (prefix.equals("NewChanel")) {
            path = G.DIR_NEW_CHANEL;
        } else {
            path = G.DIR_NEW_GROUP;
        }

        //=======================name of group
        TextInputLayout txtInputNewGroup = (TextInputLayout) view.findViewById(R.id.ng_txtInput_newGroup);

        edtGroupName = (EditText) view.findViewById(R.id.ng_edt_newGroup);
        final View ViewGroupName = view.findViewById(R.id.ng_view_newGroup);
        edtGroupName.setPadding(0, 8, 0, 8);
        edtGroupName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (b) {
                    ViewGroupName.setBackgroundColor(getResources().getColor(R.color.toolbar_background));
                } else {
                    ViewGroupName.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
                }
            }
        });

        switch (prefix) {
            case "NewChanel":
                txtInputNewGroup.setHint(getResources().getString(R.string.new_channel));
                break;
            case "ConvertToGroup":
                txtInputNewGroup.setHint(getResources().getString(R.string.chat_to_group));
                break;
            default:
                txtInputNewGroup.setHint(getResources().getString(R.string.group_name));
                break;
        }

        //=======================description group
        edtDescription = (LinedEditText) view.findViewById(R.id.ng_edt_description);
        edtDescription.setPadding(0, 8, 0, 8);

        edtDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                lastSpecialRequestsCursorPosition = edtDescription.getSelectionStart();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                edtDescription.removeTextChangedListener(this);

                if (edtDescription.getLineCount() > 4) {
                    edtDescription.setText(specialRequests);
                    edtDescription.setSelection(lastSpecialRequestsCursorPosition);
                } else {
                    specialRequests = edtDescription.getText().toString();
                }

                edtDescription.addTextChangedListener(this);
            }
        });

        edtDescription.setSingleLine(false);
        edtDescription.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        edtDescription.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        edtDescription.setLines(4);
        edtDescription.setMaxLines(4);
        //=======================button next step

        txtNextStep = (TextView) view.findViewById(R.id.ng_txt_nextStep);
        txtNextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (edtGroupName.getText().toString().length() > 0) {
                    prgWaiting.setVisibility(View.VISIBLE);
                    getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    InputMethodManager imm = (InputMethodManager) G.context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    String newName = edtGroupName.getText().toString().replace(" ", "_");
                    File file2 = new File(path, prefix + "_" + newName + Math.random() * 10000 + 1 + ".png");
                    if (prefix.equals("NewChanel")) {
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        createChannel();
                    } else if (prefix.equals("ConvertToGroup")) {
                        chatToGroup();
                    } else {
                        createGroup();
                    }
                } else {
                    if (prefix.equals("NewChanel")) {
                        Toast.makeText(G.context, R.string.please_enter_channel_name, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(G.context, R.string.please_enter_group_name, Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
        //=======================button cancel
        txtCancel = (TextView) view.findViewById(R.id.ng_txt_cancel);
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) G.context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                if (G.IMAGE_NEW_GROUP.exists()) {
                    G.IMAGE_NEW_GROUP.delete();
                } else {
                    G.IMAGE_NEW_CHANEL.delete();
                }
                getActivity().getSupportFragmentManager().beginTransaction().remove(FragmentNewGroup.this).commit();
            }
        });
    }

    /**
     * create room with empty info , just Id and inviteLink
     *
     * @param roomId     roomId
     * @param inviteLink inviteLink
     */

    public static void createChannelRoom(final long roomId, final String inviteLink) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoom realmRoom = realm.createObject(RealmRoom.class);

                RealmChannelRoom realmChannelRoom = realm.createObject(RealmChannelRoom.class);
                realmChannelRoom.setInviteLink(inviteLink);

                realmRoom.setId(roomId);
                realmRoom.setChannelRoom(realmChannelRoom);
            }
        });
        realm.close();
    }

    private void createChannel() {

        G.onChannelCreate = new OnChannelCreate() {
            @Override
            public void onChannelCreate(final long roomIdR, final String inviteLink) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        roomId = roomIdR;
                        createChannelRoom(roomIdR, inviteLink);
                        hideProgressBar();
                        FragmentCreateChannel fragmentCreateChannel = new FragmentCreateChannel();

                        Bundle bundle = new Bundle();
                        bundle.putLong("ROOMID", roomIdR);
                        bundle.putString("INVITE_LINK", inviteLink);
                        bundle.putString("TOKEN", token);
                        bundle.putBoolean("AVATAR", existAvatar);
                        fragmentCreateChannel.setArguments(bundle);
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left)
                                .replace(fragmentContainer, fragmentCreateChannel, "createChannel_fragment")
                                .commitAllowingStateLoss();
                        getActivity().getSupportFragmentManager().beginTransaction().remove(FragmentNewGroup.this).commit();
//                        getRoom(roomIdR, ProtoGlobal.Room.Type.CHANNEL);
                    }
                });
            }

            @Override
            public void onError(int majorCode, int minorCode) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        prgWaiting.setVisibility(View.GONE);
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                });
            }

            @Override
            public void onTimeOut() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        prgWaiting.setVisibility(View.GONE);
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                });
            }
        };

        new RequestChannelCreate().channelCreate(edtGroupName.getText().toString(), edtDescription.getText().toString());
    }

    private void chatToGroup() {
        G.onChatConvertToGroup = new OnChatConvertToGroup() {
            @Override
            public void onChatConvertToGroup(final long roomId, final String name, final String description, ProtoGlobal.GroupRoom.Role role) {

                if (avatarExist) {
                    new RequestGroupAvatarAdd().groupAvatarAdd(roomId, fileUploadStructure.token);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Realm realm = Realm.getDefaultInstance();

                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, groomId).findFirst();
                                    realmRoom.setId(roomId);
                                    realmRoom.setType(RoomType.GROUP);
                                    realmRoom.setTitle(name);
                                    RealmGroupRoom realmGroupRoom = realm.createObject(RealmGroupRoom.class);
                                    realmGroupRoom.setRole(GroupChatRole.OWNER);
                                    realmGroupRoom.setDescription(description);
                                    realmGroupRoom.setParticipantsCountLabel("2");
                                    realmRoom.setGroupRoom(realmGroupRoom);
                                    realmRoom.setChatRoom(null);
                                }
                            });
                            realm.close();
                        }
                    });
                } else {
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    getRoom(roomId, ProtoGlobal.Room.Type.GROUP);
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Realm realm = Realm.getDefaultInstance();

                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, groomId).findFirst();
                                realmRoom.setId(roomId);
                                realmRoom.setType(RoomType.GROUP);
                                realmRoom.setTitle(name);
                                RealmGroupRoom realmGroupRoom = realm.createObject(RealmGroupRoom.class);
                                realmGroupRoom.setRole(GroupChatRole.OWNER);
                                realmGroupRoom.setDescription(description);
                                realmGroupRoom.setParticipantsCountLabel("2");
                                realmRoom.setGroupRoom(realmGroupRoom);
                                realmRoom.setChatRoom(null);
                            }
                        });
                        realm.close();
                        getRoom(roomId, ProtoGlobal.Room.Type.GROUP);
                    }
                });
            }

            @Override
            public void Error(int majorCode, int minorCode) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        prgWaiting.setVisibility(View.GONE);
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                });
            }

            @Override
            public void timeOut() {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        prgWaiting.setVisibility(View.GONE);
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                });

            }
        };

        new RequestChatConvertToGroup().chatConvertToGroup(groomId, edtGroupName.getText().toString(), edtDescription.getText().toString());
    }

    private void createGroup() {
        G.onGroupCreate = new OnGroupCreate() {
            @Override
            public void onGroupCreate(final long roomIdR) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        roomId = roomIdR;
                        hideProgressBar();
                        getRoom(roomIdR, ProtoGlobal.Room.Type.GROUP);
                    }
                });


               /* getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (avatarExist) {
                            new RequestGroupAvatarAdd().groupAvatarAdd(roomId, fileUploadStructure.token);
                        } else {
                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            getRoom(roomId, ProtoGlobal.Room.Type.GROUP);
                        }
                    }
                });*/

            }

            @Override
            public void onTimeOut() {
                hideProgressBar();
            }

            @Override
            public void onError(int majorCode, int minorCode) {
                hideProgressBar();

                if (majorCode == 300 && minorCode == 1) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content), getResources().getString(R.string.E_302_1), Snackbar.LENGTH_LONG);

                            snack.setAction(R.string.cancel, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    snack.dismiss();
                                }
                            });
                            snack.show();
                        }
                    });
                } else if (majorCode == 300 && minorCode == 2) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content), getResources().getString(R.string.E_300_2), Snackbar.LENGTH_LONG);

                            snack.setAction(R.string.cancel, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    snack.dismiss();
                                }
                            });
                            snack.show();
                        }
                    });
                } else if (majorCode == 301) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content), getResources().getString(R.string.E_301), Snackbar.LENGTH_LONG);

                            snack.setAction(R.string.cancel, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    snack.dismiss();
                                }
                            });
                            snack.show();
                        }
                    });
                }
            }
        };

        new RequestGroupCreate().groupCreate(edtGroupName.getText().toString(), edtDescription.getText().toString());
    }

    private void getRoom(final long roomId, final ProtoGlobal.Room.Type typeCreate) {

        G.onClientGetRoomResponse = new OnClientGetRoomResponse() {
            @Override
            public void onClientGetRoomResponse(final ProtoGlobal.Room room, ProtoClientGetRoom.ClientGetRoomResponse.Builder builder) {
                try {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                hideProgressBar();
                                if (existAvatar) {
                                    showProgressBar();
                                    if (room.getType() == ProtoGlobal.Room.Type.GROUP) {
                                        new RequestGroupAvatarAdd().groupAvatarAdd(roomId, token);
                                    } else {
                                        new RequestChannelAvatarAdd().channelAvatarAdd(roomId, token);
                                    }

                                } else {
                                    Fragment fragment = ContactGroupFragment.newInstance();
                                    Bundle bundle = new Bundle();
                                    bundle.putLong("RoomId", roomId);

                                    if (room.getType() == ProtoGlobal.Room.Type.GROUP) {
                                        bundle.putString("LIMIT", room.getGroupRoomExtra().getParticipantsCountLimitLabel());
                                    } else {
                                        bundle.putString("LIMIT", room.getGroupRoomExtra().getParticipantsCountLimitLabel());
                                    }
                                    bundle.putString("TYPE", typeCreate.toString());
                                    bundle.putBoolean("NewRoom", true);
                                    fragment.setArguments(bundle);
                                    getActivity().getSupportFragmentManager()
                                            .beginTransaction()
                                            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left)
                                            .replace(fragmentContainer, fragment, "contactGroup_fragment")
                                            .commitAllowingStateLoss();
                                    getActivity().getSupportFragmentManager().beginTransaction().remove(FragmentNewGroup.this).commit();
                                    ActivityMain.mLeftDrawerLayout.closeDrawer();
                                }
                                /*if (typeCreate.toString().equals(ProtoGlobal.Room.Type.CHANNEL.toString())) {

                                    prgWaiting.setVisibility(View.GONE);
                                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    Fragment fragment = ContactGroupFragment.newInstance();
                                    Bundle bundle = new Bundle();
                                    bundle.putLong("RoomId", roomId);

                                    if (room.getType() == ProtoGlobal.Room.Type.GROUP) {
                                        bundle.putString("LIMIT", room.getGroupRoomExtra().getParticipantsCountLimitLabel());
                                    }

                                    if (existAvatar) {
                                        new RequestChannelAvatarAdd().channelAvatarAdd(roomId, token);
                                    } else {
                                        bundle.putString("TYPE", typeCreate.toString());
                                        bundle.putBoolean("NewRoom", true);
                                        fragment.setArguments(bundle);
                                        getActivity().getSupportFragmentManager()
                                                .beginTransaction()
                                                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left)
                                                .addToBackStack(null)
                                                .replace(fragmentContainer, fragment)
                                                .commitAllowingStateLoss();
                                        getActivity().getSupportFragmentManager().beginTransaction().remove(FragmentNewGroup.this).commit();
                                        ActivityMain.mLeftDrawerLayout.closeDrawer();
                                    }


                                } else {
                                    prgWaiting.setVisibility(View.GONE);
                                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    Fragment fragment = ContactGroupFragment.newInstance();
                                    Bundle bundle = new Bundle();
                                    bundle.putLong("RoomId", roomId);

                                    if (room.getType() == ProtoGlobal.Room.Type.GROUP) {
                                        bundle.putString("LIMIT", room.getGroupRoomExtra().getParticipantsCountLimitLabel());
                                    }

                                    bundle.putString("TYPE", typeCreate.toString());
                                    bundle.putBoolean("NewRoom", true);
                                    fragment.setArguments(bundle);
                                    getActivity().getSupportFragmentManager()
                                            .beginTransaction()
                                            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left)
                                            .addToBackStack(null)
                                            .replace(fragmentContainer, fragment)
                                            .commitAllowingStateLoss();
                                    getActivity().getSupportFragmentManager().beginTransaction().remove(FragmentNewGroup.this).commit();
                                    ActivityMain.mLeftDrawerLayout.closeDrawer();
                                }*/

                            }
                        });
                    }
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int majorCode, int minorCode) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        prgWaiting.setVisibility(View.GONE);
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                });

                if (majorCode == 610) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content), getResources().getString(R.string.E_610), Snackbar.LENGTH_LONG);

                            snack.setAction(R.string.cancel, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    snack.dismiss();
                                }
                            });
                            snack.show();
                        }
                    });
                } else if (majorCode == 611) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content), getResources().getString(R.string.E_611), Snackbar.LENGTH_LONG);

                            snack.setAction(R.string.cancel, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    snack.dismiss();
                                }
                            });
                            snack.show();
                        }
                    });
                } else if (majorCode == 612) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content), getResources().getString(R.string.E_612), Snackbar.LENGTH_LONG);

                            snack.setAction(R.string.cancel, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    snack.dismiss();
                                }
                            });
                            snack.show();
                        }
                    });
                } else if (majorCode == 613) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content), getResources().getString(R.string.E_613), Snackbar.LENGTH_LONG);

                            snack.setAction(R.string.cancel, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    snack.dismiss();
                                }
                            });
                            snack.show();
                        }
                    });
                } else if (majorCode == 614) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content), getResources().getString(R.string.E_614), Snackbar.LENGTH_LONG);

                            snack.setAction(R.string.cancel, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    snack.dismiss();
                                }
                            });
                            snack.show();
                        }
                    });
                }
            }

            @Override
            public void onTimeOut() {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        prgWaiting.setVisibility(View.GONE);
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                });

            }
        };

        new RequestClientGetRoom().clientGetRoom(roomId);
    }

    private void showInitials() {
        Realm realm = Realm.getDefaultInstance();
        RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
        imgCircleImageView.setImageBitmap(
                HelperImageBackColor.drawAlphabetOnPicture(
                        (int) imgCircleImageView.getContext().getResources().getDimension(R.dimen.dp100)
                        , realmUserInfo.getUserInfo().getInitials()
                        , realmUserInfo.getUserInfo().getColor()));

        realm.close();
    }

    private void setImage(long roomId) {
        final Realm realm = Realm.getDefaultInstance();

        RealmAvatar realmAvatar = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, roomId).findFirst();
        if (realmAvatar != null) {
            imgCircleImageView.setPadding(0, 0, 0, 0);
            if (realmAvatar.getFile().isFileExistsOnLocal()) {
                ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(realmAvatar.getFile().getLocalFilePath()), imgCircleImageView);
            } else if (realmAvatar.getFile().isThumbnailExistsOnLocal()) {
                ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(realmAvatar.getFile().getLocalThumbnailPath()), imgCircleImageView);
            } else {
                showInitials();
            }
        } else {
            showInitials();
            imgCircleImageView.setPadding(0, 0, 0, 0);
        }
    }

    private boolean avatarExist = false;
    private FileUploadStructure fileUploadStructure;

    private void setImage(final String imagePath) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (new File(imagePath).exists()) {
                    imgCircleImageView.setPadding(0, 0, 0, 0);
                    ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(imagePath), imgCircleImageView);
                } else {
                    showInitials();
                    imgCircleImageView.setPadding(0, 0, 0, 0);
                }
            }
        });
    }

    @Override
    public void onFileUploaded(final FileUploadStructure uploadStructure, String identity) {
        hideProgressBar();
        existAvatar = true;
        token = uploadStructure.token;
        // disable progress and show snack bar for retry upload avatar
//        if (prefix.equals("NewChanel")) {
//            new RequestChannelAvatarAdd().channelAvatarAdd(roomId, uploadStructure.token);
//        } else {
//            new RequestGroupAvatarAdd().groupAvatarAdd(roomId, uploadStructure.token);
//        }

        setImage(pathSaveImage);

        /*getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (prefix.equals("NewChanel")) {
                    existAvatar = true;
                    token = uploadStructure.token;
                    prgWaiting.setVisibility(View.GONE);
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    txtNextStep.setEnabled(true);
                    setImage(pathSaveImage);

                } else {
                    avatarExist = true;
                    fileUploadStructure = uploadStructure;
                    imgCircleImageView.setTag(uploadStructure.token);
                    ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(pathSaveImage), imgCircleImageView);
                    imgCircleImageView.setPadding(0, 0, 0, 0);
                    txtNextStep.setEnabled(true);
                }
            }
        });*/
    }

    @Override
    public void onFileUploading(FileUploadStructure uploadStructure, String identity, double progress) {
        // TODO: 10/20/2016 [Alireza] update view something like updating progress
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtNextStep.setEnabled(false);
            }
        });
    }

    @Override
    public void onFileTimeOut(String identity) {

        if (Long.parseLong(identity) == avatarId) {
            hideProgressBar();
        }
    }

    @Override
    public void onAvatarAdd(final long roomId, final ProtoGlobal.Avatar avatar) {

        HelperAvatar.avatarAdd(roomId, pathSaveImage, avatar, new OnAvatarAdd() {
            @Override
            public void onAvatarAdd(final String avatarPath) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressBar();
                        setImage(avatarPath);
                        startRoom();
                    }
                });
            }
        });


        /*if (prefix.equals("NewChanel")) {
            HelperAvatar.avatarAdd(roomId, pathSaveImage, avatar, new OnAvatarAdd() {
                @Override
                public void onAvatarAdd(final String avatarPath) {

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //TODO [Saeed Mozaffari] [2016-12-07 3:50 PM] - also for avatar timeout do this actions

                            txtNextStep.setEnabled(true);
                            prgWaiting.setVisibility(View.GONE);
                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            setImage(avatarPath);
                            startRoom();
                        }
                    });

                }
            });

        } else {
            Realm realm = Realm.getDefaultInstance();

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmAvatar realmAvatar = realm.createObject(RealmAvatar.class, avatar.getId());
                    realmAvatar.setOwnerId(roomId);
                    realmAvatar.setFile(RealmAttachment.build(avatar.getFile(), AttachmentFor.AVATAR, null));

                    try {
                        AndroidUtils.copyFile(new File(pathSaveImage), new File(G.DIR_IMAGE_USER + "/" + avatar.getFile().getToken() + "_" + avatar.getFile().getName()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            realm.close();

            // have to be inside a delayed handler
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setImage(roomId);
                }
            }, 500);

            getRoom(roomId, ProtoGlobal.Room.Type.GROUP);
        }*/
    }

    @Override
    public void onAvatarAddError() {
        hideProgressBar();
    }

    private void startRoom() {
        Fragment fragment = ContactGroupFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putLong("RoomId", roomId);

        if (prefix.equals("NewChanel")) {
            bundle.putString("TYPE", ProtoGlobal.Room.Type.CHANNEL.toString());
        } else {
            bundle.putString("TYPE", ProtoGlobal.Room.Type.GROUP.toString());
        }

        bundle.putBoolean("NewRoom", true);
        fragment.setArguments(bundle);
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left)
                .replace(fragmentContainer, fragment, "contactGroup_fragment")
                .commitAllowingStateLoss();
        getActivity().getSupportFragmentManager().beginTransaction().remove(FragmentNewGroup.this).commit();
        ActivityMain.mLeftDrawerLayout.closeDrawer();
    }

    private static class UploadTask extends AsyncTask<Object, FileUploadStructure, FileUploadStructure> {

        private ProgressBar prg;
        private Activity myActivityReference;

        public UploadTask(ProgressBar prg, Activity myActivityReference) {
            this.prg = prg;
            this.myActivityReference = myActivityReference;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected FileUploadStructure doInBackground(Object... params) {
            try {
                String filePath = (String) params[0];
                long avatarId = (long) params[1];
                File file = new File(filePath);
                String fileName = file.getName();
                long fileSize = file.length();
                FileUploadStructure fileUploadStructure = new FileUploadStructure(fileName, fileSize, filePath, avatarId);
                fileUploadStructure.openFile(filePath);

                byte[] fileHash = AndroidUtils.getFileHash(fileUploadStructure);
                fileUploadStructure.setFileHash(fileHash);

                return fileUploadStructure;
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(FileUploadStructure result) {
            super.onPostExecute(result);
            avatarId = result.messageId;
            G.uploaderUtil.startUploading(result, Long.toString(result.messageId));
        }
    }

    @Override
    public void onUploadStarted(FileUploadStructure struct) {
        showProgressBar();
    }

    @Override
    public void onBadDownload(String token) {
        // empty
    }

    //=======================result for picture

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IntentRequests.REQ_CAMERA && resultCode == Activity.RESULT_OK) {// result for camera

            Intent intent = new Intent(getActivity(), ActivityCrop.class);
            if (uriIntent != null) {
                intent.putExtra("IMAGE_CAMERA", uriIntent.toString());
                intent.putExtra("TYPE", "camera");
                intent.putExtra("PAGE", prefix);
                startActivityForResult(intent, IntentRequests.REQ_CROP);
            } else {
                Toast.makeText(G.context, R.string.can_not_save_picture_pleas_try_again, Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == IntentRequests.REQ_GALLERY && resultCode == Activity.RESULT_OK) {// result for gallery
            if (data != null) {
                Intent intent = new Intent(getActivity(), ActivityCrop.class);
                intent.putExtra("IMAGE_CAMERA", data.getData().toString());
                intent.putExtra("TYPE", "gallery");
                intent.putExtra("PAGE", prefix);
                startActivityForResult(intent, IntentRequests.REQ_CROP);
            }
        } else if (requestCode == IntentRequests.REQ_CROP) {

            if (data != null) {
                pathSaveImage = data.getData().toString();
                avatarId = System.nanoTime();
                new UploadTask(prgWaiting, getActivity()).execute(pathSaveImage, avatarId);
            }
        }
    }

    private String pathSaveImage;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    //***Show And Hide ProgressBar
    private void showProgressBar() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                txtNextStep.setEnabled(false);
                prgWaiting.setVisibility(View.VISIBLE);
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
    }

    private void hideProgressBar() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                txtNextStep.setEnabled(true);
                prgWaiting.setVisibility(View.GONE);
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
    }
}
