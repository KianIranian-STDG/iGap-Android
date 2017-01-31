package com.iGap.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.R;
import com.iGap.activities.ActivityMain;
import com.iGap.activities.ActivitySetting;
import com.iGap.helper.HelperAvatar;
import com.iGap.helper.HelperCalander;
import com.iGap.helper.HelperImageBackColor;
import com.iGap.helper.HelperLogout;
import com.iGap.helper.HelperPermision;
import com.iGap.interfaces.OnAvatarGet;
import com.iGap.interfaces.OnChangeUserPhotoListener;
import com.iGap.interfaces.OnGetPermission;
import com.iGap.interfaces.OnUserInfoMyClient;
import com.iGap.interfaces.OnUserSessionLogout;
import com.iGap.libs.flowingdrawer.MenuFragment;
import com.iGap.module.AndroidUtils;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmUserInfo;
import com.iGap.request.RequestUserInfo;
import com.iGap.request.RequestUserSessionLogout;
import com.nostra13.universalimageloader.core.ImageLoader;
import io.realm.Realm;
import java.io.File;
import java.io.IOException;

public class FragmentDrawerMenu extends MenuFragment implements OnUserInfoMyClient {
    public static TextView txtUserName;
    Context context;
    private ImageView imgUserPhoto;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_menu, container, false);
        initLayoutMenu(view);
        G.onUserInfoMyClient = this;
        return setupReveal(view);
    }

    private void initLayoutMenu(View v) {

        Realm realm = Realm.getDefaultInstance();
        RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
        if (realmUserInfo != null) {
            String username = realmUserInfo.getUserInfo().getDisplayName();
            String phoneNumber = realmUserInfo.getUserInfo().getPhoneNumber();

            imgUserPhoto = (ImageView) v.findViewById(R.id.lm_imv_user_picture);
            txtUserName = (TextView) v.findViewById(R.id.lm_txt_user_name);
            TextView txtPhoneNumber = (TextView) v.findViewById(R.id.lm_txt_phone_number);
            txtUserName.setText(username);
            txtPhoneNumber.setText(phoneNumber);

            if (HelperCalander.isLanguagePersian) {
                txtPhoneNumber.setText(HelperCalander.convertToUnicodeFarsiNumber(txtPhoneNumber.getText().toString()));
                txtUserName.setText(HelperCalander.convertToUnicodeFarsiNumber(txtUserName.getText().toString()));
            }
            new RequestUserInfo().userInfo(realmUserInfo.getUserId());
            setImage(realmUserInfo.getUserId());
        }
        realm.close();

        RelativeLayout layoutUserPicture = (RelativeLayout) v.findViewById(R.id.lm_layout_user_picture);
        layoutUserPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    HelperPermision.getStoragePermision(getActivity(), new OnGetPermission() {
                        @Override
                        public void Allow() {
                            Intent intent = new Intent(G.context, ActivitySetting.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            G.context.startActivity(intent);
                            ActivityMain.mLeftDrawerLayout.closeDrawer();
                        }

                        @Override
                        public void deny() {

                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        LinearLayout layoutNewGroup = (LinearLayout) v.findViewById(R.id.lm_ll_new_group);
        layoutNewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentNewGroup fragment = FragmentNewGroup.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("TYPE", "NewGroup");
                fragment.setArguments(bundle);

                try {
                    getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.fragmentContainer, fragment, "newGroup_fragment")
                        .commit();
                } catch (Exception e) {
                    e.getStackTrace();
                }

            }
        });

        LinearLayout layoutNewChat = (LinearLayout) v.findViewById(R.id.lm_ll_new_chat);
        layoutNewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = RegisteredContactsFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("TITLE", "New Chat");
                fragment.setArguments(bundle);

                try {
                    getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left)
                        .addToBackStack(null)
                        .replace(R.id.fragmentContainer, fragment)
                        .commit();
                } catch (Exception e) {
                    e.getStackTrace();
                }

                ActivityMain.mLeftDrawerLayout.closeDrawer();
            }
        });

        LinearLayout layoutNewChannel = (LinearLayout) v.findViewById(R.id.lm_ll_new_channle);
        layoutNewChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentNewGroup fragment = FragmentNewGroup.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("TYPE", "NewChanel");
                fragment.setArguments(bundle);
                try {
                    getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.fragmentContainer, fragment, "newGroup_fragment")
                        .commit();
                } catch (Exception e) {
                    e.getStackTrace();
                }

            }
        });

        LinearLayout layoutContacts = (LinearLayout) v.findViewById(R.id.lm_ll_contacts);
        layoutContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Fragment fragment = RegisteredContactsFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("TITLE", "Contacts");
                fragment.setArguments(bundle);

                try {
                    getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left)
                        .addToBackStack(null)
                        .replace(R.id.fragmentContainer, fragment)
                        .commit();
                } catch (Exception e) {
                    e.getStackTrace();
                }

                ActivityMain.mLeftDrawerLayout.closeDrawer();
            }
        });

        LinearLayout layoutInviteFriends = (LinearLayout) v.findViewById(R.id.lm_ll_invite_friends);
        layoutInviteFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey Join iGap : https://www.igap.net/ I'm waiting for you !");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                ActivityMain.mLeftDrawerLayout.closeDrawer();
            }
        });

        LinearLayout layoutSetting = (LinearLayout) v.findViewById(R.id.lm_ll_setting);
        layoutSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    HelperPermision.getStoragePermision(getActivity(), new OnGetPermission() {
                        @Override
                        public void Allow() {
                            Intent intent = new Intent(G.context, ActivitySetting.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            ActivityMain.mLeftDrawerLayout.closeDrawer();
                        }

                        @Override
                        public void deny() {

                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        LinearLayout layoutiGapFAQ = (LinearLayout) v.findViewById(R.id.lm_ll_igap_faq);
        layoutiGapFAQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new MaterialDialog.Builder(getActivity()).title(getResources().getString(R.string.log_out))
                        .content(R.string.content_log_out)
                        .positiveText(getResources().getString(R.string.B_ok))
                        .negativeText(getResources().getString(R.string.B_cancel))
                        .iconRes(R.mipmap.exit_to_app_button)
                        .maxIconSize((int) getResources().getDimension(R.dimen.dp24))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                showProgressBar();
                                G.onUserSessionLogout = new OnUserSessionLogout() {
                                    @Override
                                    public void onUserSessionLogout() {

                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                HelperLogout.logout();
//                                                hideProgressBar();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onError() {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
//                                                hideProgressBar();
                                                final Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content), R.string.error, Snackbar.LENGTH_LONG);
                                                snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        snack.dismiss();
                                                    }
                                                });
                                                snack.show();
                                            }
                                        });


                                    }

                                    @Override
                                    public void onTimeOut() {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
//                                                hideProgressBar();
                                                final Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content), R.string.error, Snackbar.LENGTH_LONG);
                                                snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        snack.dismiss();
                                                    }
                                                });
                                                snack.show();
                                            }
                                        });
                                    }
                                };

                                new RequestUserSessionLogout().userSessionLogout();
                            }
                        })

                        .show();

            }
        });
    }

    public void setImage(long userId) {

        HelperAvatar.getAvatar(userId, HelperAvatar.AvatarType.USER, new OnAvatarGet() {
            @Override
            public void onAvatarGet(final String avatarPath, long ownerId) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(avatarPath), imgUserPhoto);
                    }
                });
            }

            @Override
            public void onShowInitials(final String initials, final String color) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        imgUserPhoto.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture((int) imgUserPhoto.getContext().getResources().getDimension(R.dimen.dp100), initials, color));
                    }
                });
            }
        });

        G.onChangeUserPhotoListener = new OnChangeUserPhotoListener() {
            @Override
            public void onChangePhoto(final String imagePath) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (imagePath == null || !new File(imagePath).exists()) {
                            Realm realm1 = Realm.getDefaultInstance();
                            RealmUserInfo realmUserInfo = realm1.where(RealmUserInfo.class).findFirst();
                            imgUserPhoto.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture((int) imgUserPhoto.getContext().getResources().getDimension(R.dimen.dp100), realmUserInfo.getUserInfo().getInitials(), realmUserInfo.getUserInfo().getColor()));
                            realm1.close();
                        } else {
                            ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(imagePath), imgUserPhoto);
                        }
                    }
                });
            }

            @Override
            public void onChangeInitials(final String initials, final String color) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        imgUserPhoto.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture((int) imgUserPhoto.getContext().getResources().getDimension(R.dimen.dp100), initials, color));
                    }
                });
            }
        };
        //realm.close();
    }
    //**********

    @Override
    public void onUserInfoMyClient(ProtoGlobal.RegisteredUser user, String identity) {
        setImage(user.getId());
    }

    @Override
    public void onUserInfoTimeOut() {

    }

    /*private void sendContactToServer() {
        G.onContactImport = new OnUserContactImport() {
            @Override public void onContactImport() {
                new RequestUserContactsGetList().userContactGetList();
                G.isImportContactToServer = true;
            }
        };

        Contacts.getListOfContact(true);

    }*/

    @Override
    public void onUserInfoError(int majorCode, int minorCode) {

    }
}
