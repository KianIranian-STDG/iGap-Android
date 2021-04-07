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
        if (contact == null) {
            return null;
        }
        RoomContactObject roomContactObject = new RoomContactObject();
        roomContactObject.firstName = contact.getFirstName();
        roomContactObject.lastName = contact.getLastName();
        roomContactObject.nickName = contact.getNickname();
        for (int i = 0; i < contact.getPhoneCount(); i++) {
            roomContactObject.phones.add(contact.getPhone(i));
        }
        roomContactObject.lastPhoneNumber = roomContactObject.phones.get(roomContactObject.phones.size() - 1);
        for (int i = 0; i < contact.getEmailCount(); i++) {
            roomContactObject.emails.add(contact.getEmail(i));
        }
        return roomContactObject;
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
