package net.iGap.module.structs;

import net.iGap.realm.RealmAdditional;
import net.iGap.realm.RealmChannelExtra;
import net.iGap.realm.RealmRoomMessageWallet;

import org.parceler.Parcel;

@Parcel
public class StructAdditionalData {
    public long id;
    public String additionalData;
    public int AdditionalType;

    public static StructAdditionalData convert(RealmAdditional realmAdditional) {
        StructAdditionalData structAdditionalData = new StructAdditionalData();

        structAdditionalData.id = realmAdditional.getId();
        structAdditionalData.additionalData = realmAdditional.getAdditionalData();
        structAdditionalData.AdditionalType = realmAdditional.getAdditionalType();

        return structAdditionalData;
    }
}
