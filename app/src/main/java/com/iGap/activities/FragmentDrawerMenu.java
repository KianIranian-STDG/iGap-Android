package com.iGap.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.iGap.fragments.FragmentNewGroup;
import com.iGap.fragments.RegisteredContactsFragment;
import com.iGap.helper.HelperImageBackColor;
import com.iGap.helper.HelperPermision;
import com.iGap.interfaces.OnChangeUserPhotoListener;
import com.iGap.interfaces.OnGetPermision;
import com.iGap.interfaces.OnUserInfoResponse;
import com.iGap.libs.flowingdrawer.MenuFragment;
import com.iGap.module.HelperDecodeFile;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoResponse;
import com.iGap.realm.RealmAvatarPath;
import com.iGap.realm.RealmUserInfo;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import java.io.File;

public class FragmentDrawerMenu extends MenuFragment {
    public static Bitmap decodeBitmapProfile = null;
    public static TextView txtUserName;
    Context context;
    private String pathImageDecode;
    private ImageView imgUserPhoto;

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_menu, container, false);
        initLayoutMenu(view);

        return setupReveal(view);
    }

    private void initLayoutMenu(View v) {

        // init icon
        TextView txtIconNewGroup = (TextView) v.findViewById(R.id.lm_txt_icon_group);
        txtIconNewGroup.setTypeface(G.flaticon);

        TextView txtIconNewChat = (TextView) v.findViewById(R.id.lm_txt_icon_new_chat);
        txtIconNewChat.setTypeface(G.flaticon);

        TextView txtIconNewChannel = (TextView) v.findViewById(R.id.lm_txt_icon_channel);
        txtIconNewChannel.setTypeface(G.fontawesome);

        TextView txtIconContacts = (TextView) v.findViewById(R.id.lm_txt_icon_contacts);
        txtIconContacts.setTypeface(G.flaticon);

        TextView txtIconInviteFriends = (TextView) v.findViewById(R.id.lm_txt_icon_invite_friends);
        txtIconInviteFriends.setTypeface(G.flaticon);

        TextView txtIconSetting = (TextView) v.findViewById(R.id.lm_txt_icon_setting);
        txtIconSetting.setTypeface(G.flaticon);

        TextView txtIconiGapFAQ = (TextView) v.findViewById(R.id.lm_txt_icon_igap_faq);
        txtIconiGapFAQ.setTypeface(G.flaticon);

        Realm realm = Realm.getDefaultInstance();
        RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
        String username = realmUserInfo.getNickName();
        String phoneNumber = realmUserInfo.getPhoneNumber();
        realm.close();

        imgUserPhoto = (ImageView) v.findViewById(R.id.lm_imv_user_picture);

        txtUserName = (TextView) v.findViewById(R.id.lm_txt_user_name);
        txtUserName.setTypeface(G.arialBold);

        TextView txtPhoneNumber = (TextView) v.findViewById(R.id.lm_txt_phone_number);
        txtPhoneNumber.setTypeface(G.fontawesome);

        txtUserName.setText(username);
        txtPhoneNumber.setText(phoneNumber);

        setImage();

        RelativeLayout layoutUserPicture =
            (RelativeLayout) v.findViewById(R.id.lm_layout_user_picture);
        layoutUserPicture.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                Intent intent = new Intent(G.context, ActivitySetting.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                G.context.startActivity(intent);
                ActivityMain.mLeftDrawerLayout.closeDrawer();
            }
        });

        LinearLayout layoutNewGroup = (LinearLayout) v.findViewById(R.id.lm_ll_new_group);
        layoutNewGroup.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                FragmentNewGroup fragment = FragmentNewGroup.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("TYPE", "NewGroup");
                fragment.setArguments(bundle);
                getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                        R.anim.slide_in_right, R.anim.slide_out_left)
                    .addToBackStack(null)
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();
            }
        });

        LinearLayout layoutNewChat = (LinearLayout) v.findViewById(R.id.lm_ll_new_chat);
        layoutNewChat.setOnClickListener(new View.OnClickListener() {

            @Override public void onClick(View view) {

                Fragment fragment = RegisteredContactsFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("TITLE", "New Chat");
                fragment.setArguments(bundle);
                getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                        R.anim.slide_in_right, R.anim.slide_out_left)
                    .addToBackStack(null)
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();

                ActivityMain.mLeftDrawerLayout.closeDrawer();
            }
        });

        LinearLayout layoutNewChannel = (LinearLayout) v.findViewById(R.id.lm_ll_new_channle);
        layoutNewChannel.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                FragmentNewGroup fragment = FragmentNewGroup.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("TYPE", "NewChanel");
                fragment.setArguments(bundle);
                getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                        R.anim.slide_in_right, R.anim.slide_out_left)
                    .addToBackStack(null)
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();
            }
        });

        LinearLayout layoutContacts = (LinearLayout) v.findViewById(R.id.lm_ll_contacts);
        layoutContacts.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Fragment fragment = RegisteredContactsFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("TITLE", "Contacts");
                fragment.setArguments(bundle);
                getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                        R.anim.slide_in_right, R.anim.slide_out_left)
                    .addToBackStack(null)
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();
                ActivityMain.mLeftDrawerLayout.closeDrawer();
            }
        });

        LinearLayout layoutInviteFriends = (LinearLayout) v.findViewById(R.id.lm_ll_invite_friends);
        layoutInviteFriends.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                    "Hey Join iGap : https://www.igap.im/iGap.apk");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                ActivityMain.mLeftDrawerLayout.closeDrawer();
            }
        });

        LinearLayout layoutSetting = (LinearLayout) v.findViewById(R.id.lm_ll_setting);
        layoutSetting.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                HelperPermision.getStoragePermision(getActivity(), new OnGetPermision() {
                    @Override public void Allow() {
                        Intent intent = new Intent(G.context, ActivitySetting.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        ActivityMain.mLeftDrawerLayout.closeDrawer();
                    }
                });
            }
        });

        LinearLayout layoutiGapFAQ = (LinearLayout) v.findViewById(R.id.lm_ll_igap_faq);
        layoutiGapFAQ.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
            }
        });
    }

    //TODO [Saeed Mozaffari] [2016-09-10 2:49 PM] -dar har vorud be barname decode kardane tasvir eshtebah ast.

    public void setImage() {
        final Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmAvatarPath> realmAvatarPaths =
            realm.where(RealmAvatarPath.class).findAll();
        realmAvatarPaths = realmAvatarPaths.sort("id", Sort.DESCENDING);

        if (realmAvatarPaths.size() > 0) {
            pathImageDecode = realmAvatarPaths.first().getPathImage();
            decodeBitmapProfile = HelperDecodeFile.decodeFile(new File(pathImageDecode));
            imgUserPhoto.setImageBitmap(decodeBitmapProfile);
        } else {
            RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
            if (realmUserInfo.getColor() == null) {
                imgUserPhoto.setImageBitmap(
                    com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture(
                        (int) imgUserPhoto.getContext().getResources().getDimension(R.dimen.dp60),
                        " ", "#117f7f7f"));
            } else {
                imgUserPhoto.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture(
                    (int) imgUserPhoto.getContext().getResources().getDimension(R.dimen.dp100),
                    realmUserInfo.getInitials(), realmUserInfo.getColor()));
            }
        }

        G.onUserInfoResponse = new OnUserInfoResponse() {
            @Override public void onUserInfo(ProtoGlobal.RegisteredUser user,
                ProtoResponse.Response response) {

            }

            @Override public void onUserInfoTimeOut() {

            }

            @Override public void onUserInfoError(int majorCode, int minorCode) {

            }
        };

        //new RequestUserInfo().userInfo(realm.where(RealmUserInfo.class).findFirst().getUserId());

        G.onChangeUserPhotoListener = new OnChangeUserPhotoListener() {
            @Override public void onChangePhoto(final String imagePath) {
                G.handler.post(new Runnable() {
                    @Override public void run() {
                        if (imagePath == null) {

                            Realm realm1 = Realm.getDefaultInstance();
                            RealmUserInfo realmUserInfo =
                                realm1.where(RealmUserInfo.class).findFirst();
                            imgUserPhoto.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture(
                                (int) imgUserPhoto.getContext()
                                    .getResources()
                                    .getDimension(R.dimen.dp100), realmUserInfo.getInitials(),
                                realmUserInfo.getColor()));
                            realm1.close();
                        } else {
                            File imgFile = new File(imagePath);
                            if (imgFile.exists()) {
                                Bitmap myBitmap =
                                    BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                                imgUserPhoto.setImageBitmap(myBitmap);
                            }
                        }
                    }
                });
            }

            @Override public void onChangeInitials(String initials, String color) {
                imgUserPhoto.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture(
                    (int) imgUserPhoto.getContext().getResources().getDimension(R.dimen.dp100),
                    initials, color));
            }
        };

        realm.close();
    }
}
