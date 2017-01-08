package com.iGap.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.iGap.G;
import com.iGap.R;
import com.iGap.activities.ActivityMain;
import com.iGap.activities.ActivitySetting;
import com.iGap.helper.HelperAvatar;
import com.iGap.helper.HelperImageBackColor;
import com.iGap.helper.HelperPermision;
import com.iGap.interfaces.OnAvatarGet;
import com.iGap.interfaces.OnChangeUserPhotoListener;
import com.iGap.interfaces.OnGetPermision;
import com.iGap.interfaces.OnUserInfoMyClient;
import com.iGap.libs.flowingdrawer.MenuFragment;
import com.iGap.module.AndroidUtils;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmUserInfo;
import com.iGap.request.RequestUserInfo;
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

        // init icon
        TextView txtIconNewGroup = (TextView) v.findViewById(R.id.lm_txt_icon_group);
        //        txtIconNewGroup.setTypeface(G.flaticon);

        TextView txtIconNewChat = (TextView) v.findViewById(R.id.lm_txt_icon_new_chat);
        //        txtIconNewChat.setTypeface(G.flaticon);

        TextView txtIconNewChannel = (TextView) v.findViewById(R.id.lm_txt_icon_channel);


        TextView txtIconContacts = (TextView) v.findViewById(R.id.lm_txt_icon_contacts);
        //        txtIconContacts.setTypeface(G.flaticon);

        TextView txtIconInviteFriends = (TextView) v.findViewById(R.id.lm_txt_icon_invite_friends);
        //        txtIconInviteFriends.setTypeface(G.flaticon);

        TextView txtIconSetting = (TextView) v.findViewById(R.id.lm_txt_icon_setting);
        //        txtIconSetting.setTypeface(G.flaticon);

        TextView txtIconiGapFAQ = (TextView) v.findViewById(R.id.lm_txt_icon_igap_faq);
        //        txtIconiGapFAQ.setTypeface(G.flaticon);

        Realm realm = Realm.getDefaultInstance();
        RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
        String username = realmUserInfo.getUserInfo().getDisplayName();
        String phoneNumber = realmUserInfo.getUserInfo().getPhoneNumber();


        imgUserPhoto = (ImageView) v.findViewById(R.id.lm_imv_user_picture);
        txtUserName = (TextView) v.findViewById(R.id.lm_txt_user_name);
        TextView txtPhoneNumber = (TextView) v.findViewById(R.id.lm_txt_phone_number);


        txtUserName.setText(username);
        txtPhoneNumber.setText(phoneNumber);

        new RequestUserInfo().userInfo(realmUserInfo.getUserId());

        setImage(realmUserInfo.getUserId());
        realm.close();

        RelativeLayout layoutUserPicture = (RelativeLayout) v.findViewById(R.id.lm_layout_user_picture);
        layoutUserPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    HelperPermision.getStoragePermision(getActivity(), new OnGetPermision() {
                        @Override
                        public void Allow() {
                            Intent intent = new Intent(G.context, ActivitySetting.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            G.context.startActivity(intent);
                            ActivityMain.mLeftDrawerLayout.closeDrawer();
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
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.fragmentContainer, fragment, "newGroup_fragment").commit();
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
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).addToBackStack(null).replace(R.id.fragmentContainer, fragment).commit();
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
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.fragmentContainer, fragment, "newGroup_fragment").commit();
            }
        });

        LinearLayout layoutContacts = (LinearLayout) v.findViewById(R.id.lm_ll_contacts);
        layoutContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = RegisteredContactsFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("TITLE", "Contacts");
                fragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).addToBackStack(null).replace(R.id.fragmentContainer, fragment).commit();
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
                    HelperPermision.getStoragePermision(getActivity(), new OnGetPermision() {
                        @Override
                        public void Allow() {
                            Intent intent = new Intent(G.context, ActivitySetting.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            ActivityMain.mLeftDrawerLayout.closeDrawer();
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
                //empty
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
