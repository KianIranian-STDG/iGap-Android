package com.iGap.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
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
import com.iGap.activities.ActivityNewChanelFinish;
import com.iGap.interfaces.OnChatConvertToGroup;
import com.iGap.interfaces.OnClientGetRoomResponse;
import com.iGap.interfaces.OnGroupCreate;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.CircleImageView;
import com.iGap.module.LinedEditText;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.proto.ProtoClientGetRoom;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmGroupRoom;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.iGap.realm.enums.GroupChatRole;
import com.iGap.realm.enums.RoomType;
import com.iGap.request.RequestChatConvertToGroup;
import com.iGap.request.RequestClientGetRoom;
import com.iGap.request.RequestGroupCreate;

import java.io.File;

import io.realm.Realm;

import static com.iGap.R.id.fragmentContainer;

public class FragmentNewGroup extends android.support.v4.app.Fragment {

    public static Bitmap decodeBitmapProfile = null;
    private MaterialDesignTextView txtBack;
    private CircleImageView imgCircleImageView;
    private Uri uriIntent;
    private TextView txtNextStep, txtCancel, txtTitleToolbar;
    private String prefix = "NewGroup";
    private long roomId = 0;
    private String path;
    private RelativeLayout parent;

    private EditText edtGroupName;
    private LinedEditText edtDescription;

    private int lastSpecialRequestsCursorPosition = 0;
    private String specialRequests;

    private ProgressBar prgWaiting;

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
                roomId = bundle.getLong("ROOMID");
            }
        }
    }

    public void initComponent(View view) {


        Log.i("ZZZZZZCCC", "initComponent: " + roomId);

        prgWaiting = (ProgressBar) view.findViewById(R.id.prgWaiting);
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
                txtInputNewGroup.setHint(getResources().getString(R.string.Channel_name));
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

                    boolean success;
                    String newName = edtGroupName.getText().toString().replace(" ", "_");
                    File file2 = new File(path, prefix + "_" + newName + Math.random() * 10000 + 1 + ".png");
                    if (prefix.equals("NewChanel")) {
                        success = G.IMAGE_NEW_CHANEL.renameTo(file2);
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        startActivity(new Intent(G.context, ActivityNewChanelFinish.class));
                        getActivity().getSupportFragmentManager().beginTransaction().remove(FragmentNewGroup.this).commit();
                    } else if (prefix.equals("ConvertToGroup")) {
                        chatToGroup();
                    } else {
                        success = G.IMAGE_NEW_GROUP.renameTo(file2);
                        createGroup();
                    }
                                               } else {
                    Toast.makeText(G.context, R.string.please_enter_group_name, Toast.LENGTH_SHORT).show();
                                               }
                                           }
                                       }

        );
        //=======================button cancel
        txtCancel = (TextView) view.findViewById(R.id.ng_txt_cancel);
        txtCancel.setOnClickListener(new View.OnClickListener()

                                     {
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
                                     }

        );
    }

    private void chatToGroup() {


        G.onChatConvertToGroup = new OnChatConvertToGroup() {
            @Override
            public void onChatConvertToGroup(final long roomId, final String name, final String description, ProtoGlobal.GroupRoom.Role role) {

                Realm realm = Realm.getDefaultInstance();

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                        realmRoom.setType(RoomType.GROUP);
                        realmRoom.setTitle(name);

                        RealmGroupRoom realmGroupRoom = realm.createObject(RealmGroupRoom.class);

                        realmGroupRoom.setRole(GroupChatRole.OWNER);
                        realmGroupRoom.setDescription(description);
                        realmGroupRoom.setParticipantsCountLabel("2");
                        realmRoom.setGroupRoom(realmGroupRoom);

                    }
                });
                realm.close();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                });
                getRoom(roomId);
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

        new RequestChatConvertToGroup().chatConvertToGroup(roomId, edtGroupName.getText().toString(), edtDescription.getText().toString());
    }

    private void createGroup() {
        G.onGroupCreate = new OnGroupCreate() {
            @Override
            public void onGroupCreate(long roomId) {
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                getRoom(roomId);
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

            @Override
            public void onError(int majorCode, int minorCode) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        prgWaiting.setVisibility(View.GONE);
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                });

                if (majorCode == 300 && minorCode == 1) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content), getResources().getString(R.string.E_302_1), Snackbar.LENGTH_LONG);

                            snack.setAction("CANCEL", new View.OnClickListener() {
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

                            snack.setAction("CANCEL", new View.OnClickListener() {
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

                            snack.setAction("CANCEL", new View.OnClickListener() {
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

    private void getRoom(final long roomId) {

        G.onClientGetRoomResponse = new OnClientGetRoomResponse() {
            @Override
            public void onClientGetRoomResponse(ProtoGlobal.Room room, ProtoClientGetRoom.ClientGetRoomResponse.Builder builder) {
                getFragmentManager().popBackStack();
                android.support.v4.app.Fragment fragment = ContactGroupFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putLong("RoomId", roomId);
                bundle.putBoolean("NewRoom", true);
                fragment.setArguments(bundle);
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left)
                        .addToBackStack(null)
                        .replace(fragmentContainer, fragment)
                        .commit();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ActivityMain.mLeftDrawerLayout.closeDrawer();
                        prgWaiting.setVisibility(View.GONE);
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                });
                getActivity().getSupportFragmentManager().beginTransaction().remove(FragmentNewGroup.this).commit();
            }

            @Override
            public void onError(int majorCode, int minorCode) {
                if (majorCode == 610) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content), getResources().getString(R.string.E_610), Snackbar.LENGTH_LONG);

                            snack.setAction("CANCEL", new View.OnClickListener() {
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

                            snack.setAction("CANCEL", new View.OnClickListener() {
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

                            snack.setAction("CANCEL", new View.OnClickListener() {
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

                            snack.setAction("CANCEL", new View.OnClickListener() {
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

                            snack.setAction("CANCEL", new View.OnClickListener() {
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

    //=======================result for picture

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IntentRequests.REQ_CAMERA && resultCode == getActivity().RESULT_OK) {// result for camera

            Intent intent = new Intent(getActivity(), ActivityCrop.class);
            if (uriIntent != null) {
                intent.putExtra("IMAGE_CAMERA", uriIntent.toString());
                intent.putExtra("TYPE", "camera");
                intent.putExtra("PAGE", prefix);
                startActivityForResult(intent, IntentRequests.REQ_CROP);
            } else {
                Toast.makeText(G.context, R.string.can_not_save_picture_pleas_try_again, Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == IntentRequests.REQ_GALLERY && resultCode == getActivity().RESULT_OK) {// result for gallery
            if (data != null) {
                Intent intent = new Intent(getActivity(), ActivityCrop.class);
                intent.putExtra("IMAGE_CAMERA", data.getData().toString());
                intent.putExtra("TYPE", "gallery");
                intent.putExtra("PAGE", prefix);
                startActivityForResult(intent, IntentRequests.REQ_CROP);
            }
        } else if (requestCode == IntentRequests.REQ_CROP) {

            if (data != null) {
                data.getData().toString();
                Bitmap bitmap = BitmapFactory.decodeFile(data.getData().toString());
                imgCircleImageView.setImageBitmap(bitmap);
                imgCircleImageView.setPadding(0, 0, 0, 0);
                imgCircleImageView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Toast.makeText(getActivity(), "ddd", Toast.LENGTH_SHORT).show();
    }
}
