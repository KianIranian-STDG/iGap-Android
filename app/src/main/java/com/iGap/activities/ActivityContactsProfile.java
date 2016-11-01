package com.iGap.activities;

import android.Manifest;
import android.app.Fragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.R;
import com.iGap.adapter.AdapterShearedMedia;
import com.iGap.fragments.FragmentNotification;
import com.iGap.fragments.FragmentShowImage;
import com.iGap.interfaces.OnChatDelete;
import com.iGap.interfaces.OnChatGetRoom;
import com.iGap.interfaces.OnUserAvatarGetList;
import com.iGap.interfaces.OnUserContactDelete;
import com.iGap.interfaces.OnUserContactEdit;
import com.iGap.interfaces.OnUserInfoResponse;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.module.StructListOfContact;
import com.iGap.module.StructMessageAttachment;
import com.iGap.module.StructMessageInfo;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoResponse;
import com.iGap.realm.RealmAvatar;
import com.iGap.realm.RealmClientCondition;
import com.iGap.realm.RealmClientConditionFields;
import com.iGap.realm.RealmContacts;
import com.iGap.realm.RealmContactsFields;
import com.iGap.realm.RealmOfflineDelete;
import com.iGap.realm.RealmOfflineDeleteFields;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmRegisteredInfoFields;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;
import com.iGap.request.RequestChatDelete;
import com.iGap.request.RequestChatGetRoom;
import com.iGap.request.RequestUserAvatarGetList;
import com.iGap.request.RequestUserContactImport;
import com.iGap.request.RequestUserContactsDelete;
import com.iGap.request.RequestUserContactsEdit;
import com.iGap.request.RequestUserInfo;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;
import java.io.File;
import java.util.ArrayList;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.iGap.G.context;

public class ActivityContactsProfile extends ActivityEnhanced {
    private long userId = 0;
    private long roomId;
    private long phone = 0;
    private String displayName = "";
    private String username = "";
    private String firstName;
    private String lastName;
    private long lastSeen;
    private String initials;
    private String color;
    private String enterFrom;

    private boolean showNumber = true;

    private AppBarLayout appBarLayout;

    private TextView txtLastSeen, txtUserName, titleToolbar, titleLastSeen, txtBlockContact,
        txtClearChat, txtPhoneNumber, txtNotifyAndSound, txtNickname;
    private ViewGroup vgPhoneNumber, vgSharedMedia, layoutNickname;
    private CircleImageView imgUser;
    private MaterialDesignTextView imgMenu, txtBack;

    private FloatingActionButton fab;
    private PopupWindow popupWindow;
    private PopupWindow popupWindowPhoneNumber;
    private int screenWidth;

    private String avatarPath;
    private RealmList<RealmAvatar> avatarList;

    @Override protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_profile);
        final Realm realm = Realm.getDefaultInstance();

        Bundle extras = getIntent().getExtras();
        userId = extras.getLong("peerId");
        roomId = extras.getLong("RoomId");
        enterFrom = extras.getString("enterFrom");

        RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class)
            .equalTo(RealmRegisteredInfoFields.ID, userId)
            .findFirst();
        if (realmRegisteredInfo.getLastAvatar() != null) {

            String mainFilePath = realmRegisteredInfo.getLastAvatar().getFile().getLocalFilePath();

            if (mainFilePath != null && new File(
                mainFilePath).exists()) { // if main image is exist showing that
                avatarPath = mainFilePath;
            } else {
                avatarPath = realmRegisteredInfo.getLastAvatar().getFile().getLocalThumbnailPath();
            }

            avatarList = realmRegisteredInfo.getAvatar();
        }

        RealmContacts realmUser =
            realm.where(RealmContacts.class).equalTo(RealmContactsFields.ID, userId).findFirst();

        if (realmUser != null) {
            phone = realmUser.getPhone();
            displayName = realmUser.getDisplay_name();
            firstName = realmUser.getFirst_name();
            lastName = realmUser.getLast_name();
            username = realmUser.getUsername();
            lastSeen = realmUser.getLast_seen();
            color = realmUser.getColor();
            initials = realmUser.getInitials();
        } else {
            phone = realmRegisteredInfo.getPhone();
            displayName = realmRegisteredInfo.getDisplayName();
            firstName = realmRegisteredInfo.getFirstName();
            lastName = realmRegisteredInfo.getLastName();
            username = realmRegisteredInfo.getUsername();
            lastSeen = realmRegisteredInfo.getLastSeen();
            color = realmRegisteredInfo.getColor();
            initials = realmRegisteredInfo.getInitials();
        }

        RealmContacts realmContacts =
            realm.where(RealmContacts.class).equalTo(RealmContactsFields.PHONE, phone).findFirst();

        // agar ba click roye karbar dar safheye goruh vared in ghesmat shodim va karbar dar list contact haye ma vojud nadasht shomareye karbar namyesh dade nemishavad
        if (realmContacts == null && enterFrom.equals(ProtoGlobal.Room.Type.GROUP.toString())) {
            showNumber = false;
        }

        imgUser = (CircleImageView) findViewById(R.id.chi_img_circleImage);

        //Set ContactAvatar
        if (avatarPath != null) {
            File imgFile = new File(avatarPath);
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imgUser.setImageBitmap(myBitmap);
            } else {
                imgUser.setImageBitmap(com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture(
                    (int) imgUser.getContext().getResources().getDimension(R.dimen.dp100), initials,
                    color));
            }
        } else {
            imgUser.setImageBitmap(com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture(
                (int) imgUser.getContext().getResources().getDimension(R.dimen.dp100), initials,
                color));
        }

        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                if (avatarList != null) {
                    Fragment fragment = FragmentShowImage.newInstance();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("listPic", setItem());
                    bundle.putInt("SelectedImage", 0);
                    bundle.putLong("PeedId", userId);
                    fragment.setArguments(bundle);
                    ActivityContactsProfile.this.getFragmentManager()
                        .beginTransaction()
                        .add(R.id.chi_layoutParent, fragment, "Show_Image_fragment")
                        .commit();
                } else {
                    Toast.makeText(context, "Avatar Not exist!", Toast.LENGTH_SHORT).show();
                }
                //                ActivityContactsProfile.this.getFragmentManager().beginTransaction().replace(R.id.chi_layoutParent, fragment).commit();

            }
        });

        txtBack = (MaterialDesignTextView) findViewById(R.id.chi_txt_back);
        RippleView rippleBack = (RippleView) findViewById(R.id.chi_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override public void onComplete(RippleView rippleView) {
                finish();
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.chi_fab_setPic);
        fab.setOnClickListener(new View.OnClickListener() { //fab button
            @Override public void onClick(View view) {

                if (enterFrom.equals(ProtoGlobal.Room.Type.GROUP.toString())) {

                    final Realm realm = Realm.getDefaultInstance();
                    final RealmRoom realmRoom = realm.where(RealmRoom.class)
                        .equalTo(RealmRoomFields.CHAT_ROOM.PEER_ID, userId)
                        .findFirst();

                    if (realmRoom != null) {
                        Intent intent = new Intent(context, ActivityChat.class);
                        intent.putExtra("RoomId", realmRoom.getId());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        finish();
                    } else {
                        G.onChatGetRoom = new OnChatGetRoom() {
                            @Override public void onChatGetRoom(final long roomId) {
                                G.currentActivity.runOnUiThread(new Runnable() {
                                    @Override public void run() {
                                        Realm realm = Realm.getDefaultInstance();
                                        Intent intent = new Intent(context, ActivityChat.class);
                                        intent.putExtra("peerId", userId);
                                        intent.putExtra("RoomId", roomId);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        realm.close();
                                        context.startActivity(intent);
                                        finish();
                                    }
                                });
                            }

                            @Override public void onChatGetRoomTimeOut() {

                            }

                            @Override public void onChatGetRoomError(int majorCode, int minorCode) {
                                if (majorCode == 200) {
                                    runOnUiThread(new Runnable() {
                                        @Override public void run() {
                                            final Snackbar snack =
                                                Snackbar.make(findViewById(android.R.id.content),
                                                    getResources().getString(R.string.E_200),
                                                    Snackbar.LENGTH_LONG);

                                            snack.setAction("CANCEL", new View.OnClickListener() {
                                                @Override public void onClick(View view) {
                                                    snack.dismiss();
                                                }
                                            });
                                            snack.show();
                                        }
                                    });
                                }
                                if (majorCode == 201) {
                                    runOnUiThread(new Runnable() {
                                        @Override public void run() {
                                            final Snackbar snack =
                                                Snackbar.make(findViewById(android.R.id.content),
                                                    getResources().getString(R.string.E_201),
                                                    Snackbar.LENGTH_LONG);

                                            snack.setAction("CANCEL", new View.OnClickListener() {
                                                @Override public void onClick(View view) {
                                                    snack.dismiss();
                                                }
                                            });
                                            snack.show();
                                        }
                                    });
                                }
                                if (majorCode == 202) {
                                    runOnUiThread(new Runnable() {
                                        @Override public void run() {
                                            final Snackbar snack =
                                                Snackbar.make(findViewById(android.R.id.content),
                                                    getResources().getString(R.string.E_202),
                                                    Snackbar.LENGTH_LONG);

                                            snack.setAction("CANCEL", new View.OnClickListener() {
                                                @Override public void onClick(View view) {
                                                    snack.dismiss();
                                                }
                                            });
                                            snack.show();
                                        }
                                    });
                                }
                            }
                        };

                        new RequestChatGetRoom().chatGetRoom(userId);
                    }
                    realm.close();
                } else {
                    finish();
                }
            }
        });

        txtNickname = (TextView) findViewById(R.id.chi_txt_nikName);//set nickname
        if (displayName != null && !displayName.equals("")) {
            txtNickname.setText(displayName);
        } else {
            txtNickname.setText("nick name not exist");
        }

        layoutNickname = (ViewGroup) findViewById(R.id.chi_layout_nickname);
        layoutNickname.setOnClickListener(new View.OnClickListener() {
                                              @Override public void onClick(View view) {

                                                  final LinearLayout layoutNickname = new LinearLayout(ActivityContactsProfile.this);
                                                  layoutNickname.setOrientation(LinearLayout.VERTICAL);

                                                  String splitNickname[] = txtNickname.getText().toString().split(" ");
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
                                                  View viewFirstName = new View(ActivityContactsProfile.this);
                                                  viewFirstName.setBackgroundColor(
                                                      getResources().getColor(R.color.toolbar_background));
                                                  LinearLayout.LayoutParams viewParams =
                                                      new AppBarLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2);

                                                  TextInputLayout inputFirstName = new TextInputLayout(ActivityContactsProfile.this);
                                                  final EditText edtFirstName = new EditText(ActivityContactsProfile.this);
                                                  edtFirstName.setHint("First Name");
                                                  edtFirstName.setText(firsName);
                                                  edtFirstName.setPadding(0, 8, 0, 8);

                                                  edtFirstName.setSingleLine(true);
                                                  inputFirstName.addView(edtFirstName);
                                                  inputFirstName.addView(viewFirstName, viewParams);
                                                  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                                      edtFirstName.setBackground(
                                                          getResources().getDrawable(android.R.color.transparent));
                                                  }

                                                  View viewLastName = new View(ActivityContactsProfile.this);
                                                  viewLastName.setBackgroundColor(
                                                      getResources().getColor(R.color.toolbar_background));

                                                  TextInputLayout inputLastName = new TextInputLayout(ActivityContactsProfile.this);
                                                  final EditText edtLastName = new EditText(ActivityContactsProfile.this);
                                                  edtLastName.setHint("Last Name");
                                                  edtLastName.setText(lastName);
                                                  edtLastName.setPadding(0, 8, 0, 8);
                                                  edtLastName.setSingleLine(true);
                                                  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                                      edtLastName.setBackground(
                                                          getResources().getDrawable(android.R.color.transparent));
                                                  }
                                                  inputLastName.addView(edtLastName);
                                                  inputLastName.addView(viewLastName, viewParams);

                                                  LinearLayout.LayoutParams layoutParams =
                                                      new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                          ViewGroup.LayoutParams.WRAP_CONTENT);
                                                  layoutParams.setMargins(0, 0, 0, 30);

                                                  layoutNickname.addView(inputFirstName, layoutParams);
                                                  layoutNickname.addView(inputLastName, layoutParams);

                                                  final MaterialDialog dialog =
                                                      new MaterialDialog.Builder(ActivityContactsProfile.this).title("Nickname")
                                                          .positiveText("SAVE")
                                                          .customView(layoutNickname, true)
                                                          .widgetColor(getResources().getColor(R.color.toolbar_background))
                                                          .negativeText("CANCEL")
                                                          .build();

                                                  final View positive = dialog.getActionButton(DialogAction.POSITIVE);
                                                  positive.setClickable(false);
                                                  positive.setAlpha(0.5f);

                                                  final String finalFirsName = firsName;
                                                  edtFirstName.addTextChangedListener(new TextWatcher() {
                                                      @Override
                                                      public void beforeTextChanged(CharSequence charSequence, int i, int i1,
                                                          int i2) {

                                                      }

                                                      @Override
                                                      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                                      }

                                                      @Override public void afterTextChanged(Editable editable) {

                                                          if (!edtFirstName.getText().toString().equals(finalFirsName)) {
                                                              positive.setClickable(true);
                                                              positive.setAlpha(1.0f);
                                                          } else {
                                                              positive.setClickable(false);
                                                              positive.setAlpha(0.5f);
                                                          }
                                                      }
                                                  });

                                                  final String finalLastName = lastName;
                                                  edtLastName.addTextChangedListener(new TextWatcher() {
                                                      @Override
                                                      public void beforeTextChanged(CharSequence charSequence, int i, int i1,
                                                          int i2) {

                                                      }

                                                      @Override
                                                      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                                      }

                                                      @Override public void afterTextChanged(Editable editable) {
                                                          if (!edtLastName.getText().toString().equals(finalLastName)) {
                                                              positive.setClickable(true);
                                                              positive.setAlpha(1.0f);
                                                          } else {
                                                              positive.setClickable(false);
                                                              positive.setAlpha(0.5f);
                                                          }
                                                      }
                                                  });

                                                  positive.setOnClickListener(new View.OnClickListener() {
                                                                                  @Override public void onClick(View view) {

                                                                                      long po = Long.parseLong(txtPhoneNumber.getText().toString());
                                                                                      String firstName = edtFirstName.getText().toString();
                                                                                      String lastName = edtLastName.getText().toString();
                                                                                      new RequestUserContactsEdit().contactsEdit(po, firstName, lastName);
                                                                                      dialog.dismiss();
                                                                                  }
                                                                              }

                                                  );

                                                  dialog.show();
                                                  G.onUserContactEdit = new OnUserContactEdit() {
                                                      @Override
                                                      public void onContactEdit(final String firstName, final String lastName) {
                                                          Realm realm1 = Realm.getDefaultInstance();
                                                          final RealmContacts realmUser = realm1.where(RealmContacts.class)
                                                              .equalTo(RealmContactsFields.ID, userId)
                                                              .findFirst();
                                                          realm1.executeTransaction(new Realm.Transaction() {
                                                              @Override public void execute(Realm realm) {
                                                                  realmUser.setFirst_name(firstName);
                                                                  realmUser.setLast_name(lastName);
                                                              }
                                                          });
                                                          realm1.close();
                                                          runOnUiThread(new Runnable() {
                                                              @Override public void run() {
                                                                  txtNickname.setText(firstName + " " + lastName);
                                                              }
                                                          });
                                                      }

                                                      @Override public void onContactEditTimeOut() {

                                                      }

                                                      @Override
                                                      public void onContactEditError(int majorCode,
                                                          int minorCode) {
                                                          if (majorCode == 124 && minorCode == 1) {
                                                              runOnUiThread(new Runnable() {
                                                                  @Override public void run() {
                                                                      final Snackbar snack =
                                                                          Snackbar.make(
                                                                              findViewById(
                                                                                  android.R.id.content),
                                                                              getResources().getString(
                                                                                  R.string.E_124_1),
                                                                              Snackbar.LENGTH_LONG);

                                                                      snack.setAction("CANCEL",
                                                                          new View.OnClickListener() {
                                                                              @Override
                                                                              public void onClick(
                                                                                  View view) {
                                                                                  snack.dismiss();
                                                                              }
                                                                          });
                                                                      snack.show();
                                                                  }
                                                              });
                                                          } else if (majorCode == 124
                                                              && minorCode == 2) {
                                                              runOnUiThread(new Runnable() {
                                                                  @Override public void run() {
                                                                      final Snackbar snack =
                                                                          Snackbar.make(
                                                                              findViewById(
                                                                                  android.R.id.content),
                                                                              getResources().getString(
                                                                                  R.string.E_124_2),
                                                                              Snackbar.LENGTH_LONG);

                                                                      snack.setAction("CANCEL",
                                                                          new View.OnClickListener() {
                                                                              @Override
                                                                              public void onClick(
                                                                                  View view) {
                                                                                  snack.dismiss();
                                                                              }
                                                                          });
                                                                      snack.show();
                                                                  }
                                                              });
                                                          } else if (majorCode == 124
                                                              && minorCode == 3) {
                                                              runOnUiThread(new Runnable() {
                                                                  @Override public void run() {
                                                                      final Snackbar snack =
                                                                          Snackbar.make(
                                                                              findViewById(
                                                                                  android.R.id.content),
                                                                              getResources().getString(
                                                                                  R.string.E_124_3),
                                                                              Snackbar.LENGTH_LONG);

                                                                      snack.setAction("CANCEL",
                                                                          new View.OnClickListener() {
                                                                              @Override
                                                                              public void onClick(
                                                                                  View view) {
                                                                                  snack.dismiss();
                                                                              }
                                                                          });
                                                                      snack.show();
                                                                  }
                                                              });
                                                          } else if (majorCode == 124
                                                              && minorCode == 4) {
                                                              runOnUiThread(new Runnable() {
                                                                  @Override public void run() {
                                                                      final Snackbar snack =
                                                                          Snackbar.make(
                                                                              findViewById(
                                                                                  android.R.id.content),
                                                                              getResources().getString(
                                                                                  R.string.E_124_4),
                                                                              Snackbar.LENGTH_LONG);

                                                                      snack.setAction("CANCEL",
                                                                          new View.OnClickListener() {
                                                                              @Override
                                                                              public void onClick(
                                                                                  View view) {
                                                                                  snack.dismiss();
                                                                              }
                                                                          });
                                                                      snack.show();
                                                                  }
                                                              });
                                                          } else if (majorCode == 125) {
                                                              runOnUiThread(new Runnable() {
                                                                  @Override public void run() {
                                                                      final Snackbar snack =
                                                                          Snackbar.make(
                                                                              findViewById(
                                                                                  android.R.id.content),
                                                                              getResources().getString(
                                                                                  R.string.E_125),
                                                                              Snackbar.LENGTH_LONG);

                                                                      snack.setAction("CANCEL",
                                                                          new View.OnClickListener() {
                                                                              @Override
                                                                              public void onClick(
                                                                                  View view) {
                                                                                  snack.dismiss();
                                                                              }
                                                                          });
                                                                      snack.show();
                                                                  }
                                                              });
                                                          }

                                                      }
                                                  };
                                              }
                                          }

        );
        //        layoutNickname.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View view) {

        txtLastSeen = (TextView)

            findViewById(R.id.chi_txt_lastSeen_title);

        txtLastSeen.setText("Last seen at " + lastSeen);

        txtUserName = (TextView)

            findViewById(R.id.chi_txt_userName);

        txtUserName.setText(username);

        txtPhoneNumber = (TextView)

            findViewById(R.id.chi_txt_phoneNumber);

        txtPhoneNumber.setText("" + phone);

        vgPhoneNumber = (ViewGroup)

            findViewById(R.id.chi_layout_phoneNumber);

        if (!showNumber)

        {
            vgPhoneNumber.setVisibility(View.GONE);
        }

        TextView txtCountOfShearedMedia =
            (TextView) findViewById(R.id.chi_txt_count_of_sharedMedia);
        txtCountOfShearedMedia.setText(AdapterShearedMedia.getCountOfSheareddMedia(roomId) + "");

        titleToolbar = (TextView)

            findViewById(R.id.chi_txt_titleToolbar_DisplayName);

        titleToolbar.setText("nickname");

        titleLastSeen = (TextView)

            findViewById(R.id.chi_txt_titleToolbar_LastSeen);

        titleLastSeen.setText("Last Seen at " + lastSeen);

        appBarLayout = (AppBarLayout)

            findViewById(R.id.chi_appbar);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener()

        {
            @Override public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                ViewGroup viewGroup = (ViewGroup) findViewById(R.id.chi_root_circleImage);
                if (verticalOffset < -5) {
                    viewGroup.animate().alpha(0).setDuration(700);
                    viewGroup.setVisibility(View.GONE);
                    titleToolbar.setVisibility(View.VISIBLE);
                    titleToolbar.animate().alpha(1).setDuration(300);
                    titleLastSeen.setVisibility(View.VISIBLE);
                    titleLastSeen.animate().alpha(1).setDuration(300);
                } else {
                    viewGroup.setVisibility(View.VISIBLE);
                    viewGroup.animate().alpha(1).setDuration(700);
                    titleToolbar.setVisibility(View.GONE);
                    titleToolbar.animate().alpha(0).setDuration(500);
                    titleLastSeen.setVisibility(View.GONE);
                    titleLastSeen.animate().alpha(0).setDuration(500);
                }
            }
        });

        screenWidth = (int) (

            getResources()

                .

                    getDisplayMetrics()

                .widthPixels / 1.7);
        imgMenu = (MaterialDesignTextView)

            findViewById(R.id.chi_img_menuPopup);

        RippleView rippleMenu = (RippleView) findViewById(R.id.chi_ripple_menuPopup);

        rippleMenu.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener()

                                               {
                                                   @Override public void onComplete(RippleView rippleView) {

                                                       showPopUp();
                                                   }
                                               }

        );
        vgPhoneNumber.setOnClickListener(new View.OnClickListener()

                                         {
                                             @Override public void onClick(View v) {

                                                 String phoneNumber = txtPhoneNumber.getText().toString();
                                                 showPopupPhoneNumber(vgPhoneNumber, phoneNumber);
                                                 //popUpMenu(R.menu.chi_popup_phone_number, v);
                                             }
                                         }

        );

        vgSharedMedia = (ViewGroup)

            findViewById(R.id.chi_layout_SharedMedia);

        vgSharedMedia.setOnClickListener(new View.OnClickListener()

                                         {// go to the ActivityMediaChanel
                                             @Override public void onClick(View view) {

                                                 Intent intent =
                                                     new Intent(ActivityContactsProfile.this, ActivityShearedMedia.class);
                                                 intent.putExtra("RoomID", roomId);
                                                 startActivity(intent);
                                             }
                                         }

        );

        txtBlockContact = (TextView)

            findViewById(R.id.chi_txt_blockContact);

        txtBlockContact.setOnClickListener(new View.OnClickListener()

                                           {
                                               @Override public void onClick(View view) {
                                                   showAlertDialog("Block This Contact?", "BLOCK", "CANCEL");
                                               }
                                           }

        );

        txtClearChat = (TextView)

            findViewById(R.id.chi_txt_clearChat);

        txtClearChat.setOnClickListener(new View.OnClickListener()

                                        {
                                            @Override public void onClick(View view) {
                                                showAlertDialog("clear This chat?", "ClEAR", "CANCEL");
                                            }
                                        }

        );

        txtNotifyAndSound = (TextView)

            findViewById(R.id.chi_txtNotifyAndSound);

        txtNotifyAndSound.setOnClickListener(new View.OnClickListener()

                                             {
                                                 @Override public void onClick(View view) {

                                                     // TODO: 9/3/2016 (molareza) go to NotifyAndSound page

                                                     FragmentNotification fragmentNotification = new FragmentNotification();
                                                     Bundle bundle = new Bundle();
                                                     bundle.putString("PAGE", "CONTACT");
                                                     bundle.putLong("ID", roomId);
                                                     fragmentNotification.setArguments(bundle);
                                                     getSupportFragmentManager().beginTransaction()
                                                         .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                                                             R.anim.slide_in_right, R.anim.slide_out_left)
                                                         .replace(R.id.chi_layoutParent, fragmentNotification)
                                                         .commit();
                                                 }
                                             }

        );

        realm.close();

        getAvatarList();
    }

    private void showPopupPhoneNumber(View v, String number) {

        boolean isExist;
        Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(number));
        String[] mPhoneNumberProjection = {
            ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER,
            ContactsContract.PhoneLookup.DISPLAY_NAME
        };
        Cursor cur =
            context.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
        try {
            if (cur.moveToFirst()) {

                isExist = true;
            } else {

                isExist = false;
            }
        } finally {
            if (cur != null) cur.close();
        }

        if (isExist) {
            new MaterialDialog.Builder(this).title(R.string.phone_number)
                .items(R.array.phone_number2)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override public void onSelection(MaterialDialog dialog, View view, int which,
                        CharSequence text) {
                        switch (which) {
                            case 0:
                                long call = Long.parseLong(txtPhoneNumber.getText().toString());
                                try {
                                    Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                                    phoneIntent.setData(Uri.parse("tel:" + call));
                                    //startActivity(phoneIntent); //TODO [Saeed Mozaffari] [2016-09-07 11:31 AM] - phone intent permission

                                } catch (Exception ex) {

                                    ex.getStackTrace();
                                    Log.i("TAG", "onMenuItemClick: " + ex.getMessage());
                                }
                                break;
                            case 1:
                                String copy;
                                copy = txtPhoneNumber.getText().toString();

                                ClipboardManager clipboard =
                                    (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("PHONE_NUMBER", copy);
                                clipboard.setPrimaryClip(clip);
                                break;
                        }
                    }
                })
                .show();
        } else {
            new MaterialDialog.Builder(this).title(R.string.phone_number)
                .items(R.array.phone_number)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override public void onSelection(MaterialDialog dialog, View view, int which,
                        CharSequence text) {
                        switch (which) {
                            case 0:

                                String name = txtNickname.getText().toString();
                                String phone = txtPhoneNumber.getText().toString();

                                ArrayList<ContentProviderOperation> ops =
                                    new ArrayList<ContentProviderOperation>();

                                ops.add(ContentProviderOperation.newInsert(
                                    ContactsContract.RawContacts.CONTENT_URI)
                                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                                    .build());

                                //------------------------------------------------------ Names

                                ops.add(ContentProviderOperation.newInsert(
                                    ContactsContract.Data.CONTENT_URI)
                                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                    .withValue(ContactsContract.Data.MIMETYPE,
                                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                                    .withValue(
                                        ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                                        name)
                                    .build());

                                //------------------------------------------------------ Mobile Number

                                ops.add(ContentProviderOperation.
                                    newInsert(ContactsContract.Data.CONTENT_URI)
                                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                    .withValue(ContactsContract.Data.MIMETYPE,
                                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                                    .build());

                                try {
                                    G.context.getContentResolver()
                                        .applyBatch(ContactsContract.AUTHORITY, ops);
                                    addContactToServer();
                                    Toast.makeText(G.context, R.string.save_ok, Toast.LENGTH_SHORT)
                                        .show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(G.context,
                                        getString(R.string.exception) + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                                }

                                break;
                            case 1:

                                long call = Long.parseLong(txtPhoneNumber.getText().toString());
                                try {
                                    Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                                    phoneIntent.setData(Uri.parse("tel:" + call));
                                    if (ActivityCompat.checkSelfPermission(
                                        ActivityContactsProfile.this,
                                        Manifest.permission.CALL_PHONE)
                                        != PackageManager.PERMISSION_GRANTED) {
                                        // TODO: Consider calling
                                        //    ActivityCompat#requestPermissions
                                        // here to request the missing permissions, and then overriding
                                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                        //                                          int[] grantResults)
                                        // to handle the case where the user grants the permission. See the documentation
                                        // for ActivityCompat#requestPermissions for more details.
                                        return;
                                    }
                                    startActivity(
                                        phoneIntent); //TODO [Saeed Mozaffari] [2016-09-07 11:31 AM] - phone intent permission
                                } catch (Exception ex) {

                                    ex.getStackTrace();
                                    Log.i("TAG", "onMenuItemClick: " + ex.getMessage());
                                }

                                break;
                            case 2:

                                String copy;
                                copy = txtPhoneNumber.getText().toString();

                                ClipboardManager clipboard =
                                    (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("PHONE_NUMBER", copy);
                                clipboard.setPrimaryClip(clip);

                                break;
                        }
                    }
                })
                .show();
        }
    }

    /**
     * import contact to server with True force
     */
    private void addContactToServer() {
        ArrayList<StructListOfContact> contacts = new ArrayList<>();
        StructListOfContact contact = new StructListOfContact();
        contact.firstName = firstName;
        contact.lastName = lastName;
        contact.phone = phone + "";

        contacts.add(contact);

        new RequestUserContactImport().contactImport(contacts, true);
    }

    private void showPopUp() {
        LinearLayout layoutDialog = new LinearLayout(ActivityContactsProfile.this);
        ViewGroup.LayoutParams params =
            new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutDialog.setOrientation(LinearLayout.VERTICAL);
        layoutDialog.setBackgroundColor(getResources().getColor(android.R.color.white));
        TextView text1 = new TextView(ActivityContactsProfile.this);
        TextView text2 = new TextView(ActivityContactsProfile.this);
        TextView text3 = new TextView(ActivityContactsProfile.this);

        text1.setTextColor(getResources().getColor(android.R.color.black));
        text2.setTextColor(getResources().getColor(android.R.color.black));
        text3.setTextColor(getResources().getColor(android.R.color.black));

        text1.setText(getResources().getString(R.string.Search));
        text2.setText(getResources().getString(R.string.clear_history));
        text3.setText(getResources().getString(R.string.delete_contact));

        int dim20 = (int) getResources().getDimension(R.dimen.dp20);
        int dim12 = (int) getResources().getDimension(R.dimen.dp12);

        text1.setTextSize(16);
        text2.setTextSize(16);
        text3.setTextSize(16);

        text1.setPadding(dim20, dim12, dim12, dim20);
        text2.setPadding(dim20, 0, dim12, dim20);
        text3.setPadding(dim20, 0, dim12, dim20);

        layoutDialog.addView(text1, params);
        layoutDialog.addView(text2, params);
        layoutDialog.addView(text3, params);

        popupWindow =
            new PopupWindow(layoutDialog, screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.setBackgroundDrawable(getResources().getDrawable(R.mipmap.shadow3,
                ActivityContactsProfile.this.getTheme()));
        } else {
            popupWindow.setBackgroundDrawable((getResources().getDrawable(R.mipmap.shadow3)));
        }
        if (popupWindow.isOutsideTouchable()) {
            popupWindow.dismiss();
        }
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override public void onDismiss() {
                //TODO do sth here on dismiss
            }
        });

        popupWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
        popupWindow.showAtLocation(layoutDialog, Gravity.RIGHT | Gravity.TOP,
            (int) getResources().getDimension(R.dimen.dp16),
            (int) getResources().getDimension(R.dimen.dp32));
        //                popupWindow.showAsDropDown(v);

        text1.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
        text2.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                new MaterialDialog.Builder(ActivityContactsProfile.this).title(
                    R.string.clear_history)
                    .content(R.string.clear_history_content)
                    .positiveText(R.string.B_ok)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override public void onClick(@NonNull MaterialDialog dialog,
                            @NonNull DialogAction which) {

                        }
                    })
                    .negativeText(R.string.B_cancel)
                    .show();

                popupWindow.dismiss();
            }
        });
        text3.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                new MaterialDialog.Builder(ActivityContactsProfile.this).title(
                    R.string.delete_contact)
                    .content(R.string.delete_text)
                    .positiveText(R.string.B_ok)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override public void onClick(@NonNull MaterialDialog dialog,
                            @NonNull DialogAction which) {

                            deleteContact();
                        }
                    })
                    .negativeText(R.string.B_cancel)
                    .show();

                popupWindow.dismiss();
            }
        });
    }

    private void getAvatarList() {
        G.onUserAvatarGetList = new OnUserAvatarGetList() {
            @Override public void onUserAvatarGetList() {
                G.handler.post(new Runnable() {
                    @Override public void run() {
                        Toast.makeText(context, "onUserAvatarGetList", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };

        new RequestUserAvatarGetList().userAddGetList(userId);
    }

    private void showAlertDialog(String message, String positive,
        String negitive) { // alert dialog for block or clear user

        final AlertDialog.Builder builder = new AlertDialog.Builder(ActivityContactsProfile.this);

        builder.setMessage(message);
        builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialogInterface, int i) {
                clearHistory();
                dialogInterface.dismiss();
            }
        });

        builder.setMessage(message);
        builder.setNegativeButton(negitive, new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Button nButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        nButton.setTextColor(getResources().getColor(R.color.toolbar_background));
        nButton.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        Button pButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        pButton.setTextColor(getResources().getColor(R.color.toolbar_background));
        pButton.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
    }

    public ArrayList<StructMessageInfo> setItem() {
        ArrayList<StructMessageInfo> items = new ArrayList<>();

        ArrayList<String> currentTokenAdded = new ArrayList<>();

        for (int i = 0; i < avatarList.size(); i++) {
            if (avatarList.get(i).getFile() != null) {
                StructMessageInfo item = new StructMessageInfo();
                RealmAvatar avatar = avatarList.get(i);
                if (!currentTokenAdded.contains(avatar.getFile().getToken())) {
                    currentTokenAdded.add(avatar.getFile().getToken());
                    item.attachment = new StructMessageAttachment(avatarList.get(i).getFile());
                    items.add(item);
                }
            }
        }
        return items;
    }

    //TODO [Saeed Mozaffari] [2016-10-15 3:31 PM] - clearHistory , DeleteChat , use in ActivityMain , ActivityChat , ActivityContactsProfile . mitunim method ha ro tekrar nakonim va ye ja bashe va az chand ja farakhani konim
    private void clearHistory() {

        // make request for clearing messages
        final Realm realm = Realm.getDefaultInstance();

        final RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class)
            .equalTo(RealmClientConditionFields.ROOM_ID, roomId)
            .findFirstAsync();
        realmClientCondition.addChangeListener(new RealmChangeListener<RealmClientCondition>() {
            @Override public void onChange(final RealmClientCondition element) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override public void execute(Realm realm) {

                        final RealmRoom realmRoom = realm.where(RealmRoom.class)
                            .equalTo(RealmRoomFields.ID, roomId)
                            .findFirst();

                        if (realmRoom.getLastMessageId() != -1) {
                            element.setClearId(realmRoom.getLastMessageId());
                            G.clearMessagesUtil.clearMessages(roomId, realmRoom.getLastMessageId());
                        }

                        RealmResults<RealmRoomMessage> realmRoomMessages =
                            realm.where(RealmRoomMessage.class)
                                .equalTo(RealmRoomMessageFields.ROOM_ID, roomId)
                                .findAll();
                        for (RealmRoomMessage realmRoomMessage : realmRoomMessages) {
                            if (realmRoomMessage != null) {
                                // delete chat history message
                                realmRoomMessage.deleteFromRealm();
                            }
                        }

                        RealmRoom room = realm.where(RealmRoom.class)
                            .equalTo(RealmRoomFields.ID, roomId)
                            .findFirst();
                        if (room != null) {
                            room.setUnreadCount(0);
                            room.setLastMessageId(0);
                            room.setLastMessageTime(0);
                            room.setLastMessage("");

                            realm.copyToRealmOrUpdate(room);
                        }
                        // finally delete whole chat history
                        realmRoomMessages.deleteAllFromRealm();
                    }
                });

                element.removeChangeListeners();
                realm.close();
            }
        });

        if (G.onClearChatHistory != null) {
            G.onClearChatHistory.onClearChatHistory();
        }
    }

    private void deleteContact() {
        G.onUserContactdelete = new OnUserContactDelete() {
            @Override public void onContactDelete() {
                // get user info after delete it for show nickname
                getUserInfo();
            }

            @Override public void onError(int majorCode, int minorCode) {
                if (majorCode == 122 && minorCode == 1) {
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            final Snackbar snack = Snackbar.make(findViewById(android.R.id.content),
                                getResources().getString(R.string.E_122), Snackbar.LENGTH_LONG);

                            snack.setAction("CANCEL", new View.OnClickListener() {
                                @Override public void onClick(View view) {
                                    snack.dismiss();
                                }
                            });
                            snack.show();
                        }
                    });
                } else if (majorCode == 123) {
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            final Snackbar snack = Snackbar.make(findViewById(android.R.id.content),
                                getResources().getString(R.string.E_123), Snackbar.LENGTH_LONG);

                            snack.setAction("CANCEL", new View.OnClickListener() {
                                @Override public void onClick(View view) {
                                    snack.dismiss();
                                }
                            });
                            snack.show();
                        }
                    });
                }
            }
        };
        new RequestUserContactsDelete().contactsDelete(phone);
    }

    private void getUserInfo() {

        G.onUserInfoResponse = new OnUserInfoResponse() {
            @Override public void onUserInfo(final ProtoGlobal.RegisteredUser user,
                ProtoResponse.Response response) {

                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        txtNickname.setText(user.getDisplayName());
                    }
                });
            }

            @Override public void onUserInfoTimeOut() {

            }

            @Override public void onUserInfoError(int majorCode, int minorCode) {

            }
        };

        new RequestUserInfo().userInfo(userId);
    }

    private void deleteChat() {
        G.onChatDelete = new OnChatDelete() {
            @Override public void onChatDelete(long roomId) {
            }

            @Override public void onChatDeleteError(int majorCode, int minorCode) {

                if (majorCode == 218 && minorCode == 1) {
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            final Snackbar snack = Snackbar.make(findViewById(android.R.id.content),
                                getResources().getString(R.string.E_218), Snackbar.LENGTH_LONG);

                            snack.setAction("CANCEL", new View.OnClickListener() {
                                @Override public void onClick(View view) {
                                    snack.dismiss();
                                }
                            });

                        }
                    });
                } else if (majorCode == 219) {
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            final Snackbar snack = Snackbar.make(findViewById(android.R.id.content),
                                getResources().getString(R.string.E_219), Snackbar.LENGTH_LONG);

                            snack.setAction("CANCEL", new View.OnClickListener() {
                                @Override public void onClick(View view) {
                                    snack.dismiss();
                                }
                            });

                        }
                    });
                } else if (majorCode == 220) {
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            final Snackbar snack = Snackbar.make(findViewById(android.R.id.content),
                                getResources().getString(R.string.E_220), Snackbar.LENGTH_LONG);

                            snack.setAction("CANCEL", new View.OnClickListener() {
                                @Override public void onClick(View view) {
                                    snack.dismiss();
                                }
                            });

                        }
                    });
                }
            }
        };
        final Realm realm = Realm.getDefaultInstance();
        final RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class)
            .equalTo(RealmClientConditionFields.ROOM_ID, roomId)
            .findFirstAsync();
        realmClientCondition.addChangeListener(new RealmChangeListener<RealmClientCondition>() {
            @Override public void onChange(final RealmClientCondition element) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override public void execute(final Realm realm) {
                        if (realm.where(RealmOfflineDelete.class)
                            .equalTo(RealmOfflineDeleteFields.OFFLINE_DELETE, roomId)
                            .findFirst() == null) {
                            RealmOfflineDelete realmOfflineDelete =
                                realm.createObject(RealmOfflineDelete.class);
                            realmOfflineDelete.setId(System.nanoTime());
                            realmOfflineDelete.setOfflineDelete(userId);

                            element.getOfflineDeleted().add(realmOfflineDelete);

                            realm.where(RealmRoom.class)
                                .equalTo(RealmRoomFields.ID, roomId)
                                .findFirst()
                                .deleteFromRealm();
                            realm.where(RealmRoomMessage.class)
                                .equalTo(RealmRoomMessageFields.ROOM_ID, roomId)
                                .findAll()
                                .deleteAllFromRealm();

                            new RequestChatDelete().chatDelete(roomId);
                        }
                    }
                });

                element.removeChangeListeners();
                realm.close();
                finish();
                // call this for finish activity chat when delete chat
                if (G.onDeleteChatFinishActivity != null) {
                    G.onDeleteChatFinishActivity.onFinish();
                }
            }
        });
    }
}


