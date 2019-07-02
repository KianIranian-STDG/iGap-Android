package net.iGap.helper;

import android.util.Log;

import net.iGap.realm.RealmContacts;
import net.iGap.realm.RealmContactsFields;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class ContactManager {
    public static final int LOAD_AVG = 10;
    public static final int CONTACT_LIMIT = 5000;
    public static final String FIRST = "FIRST";
    public static final String OVER_LOAD = "OVERLOAD";
    private static final String TAG = "aabolfazlContact";
    private static RealmResults<RealmContacts> results;

    private static int first = 0;
    private static int loadMore = LOAD_AVG;

    private ContactManager() {

    }

    public static RealmList<RealmContacts> getContactList(String mode) {
        if (mode.equals(FIRST)) {
            first = 0;
            loadMore = LOAD_AVG;
            return overLoadContact();
        } else if (mode.equals(OVER_LOAD))
            return overLoadContact();
        else
            return new RealmList<>();
    }

    private static RealmList<RealmContacts> overLoadContact() {
        RealmList<RealmContacts> contacts = new RealmList<>();

        if (results == null)
            getIgapContact();

        contacts.addAll(results.subList(first, loadMore));
        first = loadMore;
        loadMore = loadMore + LOAD_AVG;

        Log.i(TAG, "getContactList: " + contacts.size());

        return contacts;
    }

    private static void getIgapContact() {


        if (results == null) {
            Realm realm = Realm.getDefaultInstance();
//
//            realm.executeTransaction(realm1 -> {
//                RealmContacts contacts = realm1.where(RealmContacts.class).sort(RealmContactsFields.DISPLAY_NAME).findFirst();
//                for (int i = 0; i < 100; i++) {
//                    RealmContacts object = realm1.createObject(RealmContacts.class);
//                    object.setDisplay_name(contacts.getDisplay_name());
//                    object.setId(contacts.getId());
//                    object.setPhone(contacts.getPhone());
//                    object.setAvatar(contacts.getAvatar());
//                    Log.i(TAG, "getIgapContact: " + contacts.getDisplay_name() + " " + i);
//                }
//            });


            realm.executeTransaction(realm1 -> {
                results = realm1.where(RealmContacts.class).limit(CONTACT_LIMIT).sort(RealmContactsFields.DISPLAY_NAME).findAll();
            });
        }
        Log.i(TAG, "getIgapContact: " + results.size());
    }

}
