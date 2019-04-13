package net.iGap.adapter.items.discovery.holder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentWebView;
import net.iGap.fragments.discovery.DiscoveryFragment;
import net.iGap.adapter.items.discovery.DiscoveryItem;
import net.iGap.adapter.items.discovery.DiscoveryItemField;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperUrl;
import net.iGap.request.RequestClientSetDiscoveryItemClick;


public abstract class BaseViewHolder extends RecyclerView.ViewHolder{
    public static DisplayImageOptions option = new DisplayImageOptions.Builder().cacheOnDisk(true).build();


    BaseViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void bindView(DiscoveryItem item);

    void handleDiscoveryFieldsClick(DiscoveryItemField discoveryField) {
        new RequestClientSetDiscoveryItemClick().setDiscoveryClicked(discoveryField.id);
        switch (discoveryField.actionType) {
            case PAGE:
                actionPage(discoveryField.value);
                break;
            case JOIN_LINK:
                HelperUrl.checkAndJoinToRoom(discoveryField.value);
                break;
            case WEB_LINK:
                HelperUrl.openBrowser(discoveryField.value);
                break;
            case WEB_VIEW_LINK:
                new HelperFragment(FragmentWebView.newInstance(discoveryField.value)).setReplace(false).load(false);
                break;
            case USERNAME_LINK:
                HelperUrl.checkUsernameAndGoToRoomWithMessageId(discoveryField.value.replace("@", ""), HelperUrl.ChatEntry.chat, 0);
                break;
            case REQUEST_PHONE:
//                new MaterialDialog.Builder(G.currentActivity).title(R.string.access_phone_number).positiveText(R.string.ok).negativeText(R.string.cancel).onPositive(new MaterialDialog.SingleButtonCallback() {
//                    @Override
//                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                        Long identity = System.currentTimeMillis();
//                        Realm realm = Realm.getDefaultInstance();
//
//                        realm.executeTransaction(new Realm.Transaction() {
//                            @Override
//                            public void execute(Realm realm) {
//                                RealmUserInfo realmUserInfo = RealmUserInfo.getRealmUserInfo(realm);
//                                RealmRoomMessage realmRoomMessage = RealmRoomMessage.makeAdditionalData(mMessage.roomId, identity, realmUserInfo.getUserInfo().getPhoneNumber(),null, 0, realm, ProtoGlobal.RoomMessageType.TEXT);
//                                G.chatSendMessageUtil.build(type, mMessage.roomId, realmRoomMessage).sendMessage(identity + "");
//                                messageClickListener.sendFromBot(realmRoomMessage);
//                            }
//                        });
//                    }
//                }).show();
                break;
            case REQUEST_LOCATION:
                new MaterialDialog.Builder(G.currentActivity).title(R.string.access_location).positiveText(R.string.ok).negativeText(R.string.cancel).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        if (G.locationListener != null){
//                            isLocationFromBot=true;
                            G.locationListener.requestLocation();
                        }



                   /*         G.locationListenerResponse = new LocationListenerResponse() {
                                @Override
                                public void setLocationResponse(Double latitude, Double longitude) {
                                    Long identity = System.currentTimeMillis();
                                    Realm realm = Realm.getDefaultInstance();

                                    realm.executeTransaction(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            RealmUserInfo realmUserInfo = RealmUserInfo.getRealmUserInfo(realm);
                                            RealmRoomMessage realmRoomMessage = RealmRoomMessage.makeAdditionalData(mMessage.roomId, identity, latitude + "," + longitude, ((ArrayList<String>) v.getTag()).get(2).toString(), 3, realm, ProtoGlobal.RoomMessageType.TEXT);
                                            G.chatSendMessageUtil.build(type, mMessage.roomId, realmRoomMessage).sendMessage(identity + "");
                                            messageClickListener.sendFromBot(realmRoomMessage);
                                        }
                                    });
                                }
                            };*/


                    }
                }).show();
                break;

        }
    }

    private void actionPage(String value) {
        new HelperFragment(DiscoveryFragment.newInstance(Integer.valueOf(value))).setReplace(false).load(false);
    }

}
