package net.iGap.structs;

import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoomMessageContact;

import java.util.ArrayList;
import java.util.List;

public class RoomContactObject {
    public String firstName;
    public String lastName;
    public String nickName;
    public String lastPhoneNumber;
    public List<String> phones = new ArrayList<>();
    public List<String> emails = new ArrayList<>();

    public static RoomContactObject create(ProtoGlobal.RoomMessageContact contact) {
        return null;
    }

    public static RoomContactObject create(RealmRoomMessageContact roomMessageContact) {
        if (roomMessageContact == null) {
            return null;
        }

        RoomContactObject roomContactObject = new RoomContactObject();
        roomContactObject.firstName = roomMessageContact.getFirstName();
        roomContactObject.lastName = roomMessageContact.getLastName();
        roomContactObject.nickName = roomMessageContact.getNickName();
        roomContactObject.lastPhoneNumber = roomMessageContact.getLastPhoneNumber();
        for (int i = 0; i < roomMessageContact.getPhones().size(); i++) {
            roomContactObject.phones.add(roomMessageContact.getPhones().get(i).getString());
        }

        for (int i = 0; i < roomMessageContact.getEmails().size(); i++) {
            roomContactObject.emails.add(roomMessageContact.getEmails().get(i).getString());
        }

        return roomContactObject;
    }
}
