package net.iGap.realm;

import android.util.Log;
import io.realm.Realm;
import io.realm.RealmObject;
import java.util.List;
import net.iGap.module.SerializationUtils;
import net.iGap.proto.ProtoSignalingGetConfiguration;

/**
 * Created by android3 on 5/8/2017.
 */

public class RealmCallConfig extends RealmObject {

    private boolean voice_calling;
    private boolean video_calling;
    private boolean screen_sharing;

    private byte[] IceServer;

    public boolean isVoice_calling() {
        return voice_calling;
    }

    public void setVoice_calling(boolean voice_calling) {
        this.voice_calling = voice_calling;
    }

    public boolean isVideo_calling() {
        return video_calling;
    }

    public void setVideo_calling(boolean video_calling) {
        this.video_calling = video_calling;
    }

    public boolean isScreen_sharing() {
        return screen_sharing;
    }

    public void setScreen_sharing(boolean screen_sharing) {
        this.screen_sharing = screen_sharing;
    }

    public List<ProtoSignalingGetConfiguration.SignalingGetConfigurationResponse.IceServer> getIceServer() {
        return IceServer == null ? null : (List<ProtoSignalingGetConfiguration.SignalingGetConfigurationResponse.IceServer>) SerializationUtils.deserialize(IceServer);
    }

    public void setIceServer(List<ProtoSignalingGetConfiguration.SignalingGetConfigurationResponse.IceServer> iceServer) {
        this.IceServer = SerializationUtils.serialize(iceServer);
    }

    public static void updateSignalingConfiguration(final ProtoSignalingGetConfiguration.SignalingGetConfigurationResponse.Builder builder) {

        Realm realm = Realm.getDefaultInstance();

        final RealmCallConfig realmCall = realm.where(RealmCallConfig.class).findFirst();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                RealmCallConfig item;

                if (realmCall == null) {
                    Log.i("EEE", "builder 1");
                    RealmCallConfig _rc = new RealmCallConfig();
                    item = realm.copyToRealm(_rc);
                } else {
                    Log.i("EEE", "builder 2");
                    item = realmCall;
                }
                Log.i("EEE", "builder 3");

                item.setVoice_calling(builder.getVoiceCalling());
                item.setVideo_calling(builder.getVideoCalling());
                item.setScreen_sharing(builder.getScreenSharing());

                item.setIceServer(builder.getIceServerList());
            }
        });

        realm.close();
    }
}




