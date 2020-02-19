package net.iGap.helper;

import net.iGap.module.accountManager.DbManager;
import net.iGap.realm.RealmContacts;
import net.iGap.realm.RealmContactsFields;

import java.util.List;

import io.realm.RealmList;

public class ContactManager {
    public static final int LOAD_AVG = 30;
    public static final int CONTACT_LIMIT = 5000;

    public static final String FIRST = "FIRST";
    public static final String OVER_LOAD = "OVERLOAD";
    private static List<RealmContacts> results;

    private static int first = 0;
    private static int loadMore = LOAD_AVG;
    /*private static int contactSize;*/

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

    // todo: fixed it and handle contact paging
    private static RealmList<RealmContacts> overLoadContact() {
        RealmList<RealmContacts> contacts = new RealmList<>();

        if (results == null) {
            // contact paging and remove it for crash
            /*getIgapContact();*/
            results = DbManager.getInstance().doRealmTask(realm -> {
                return realm.copyFromRealm(realm.where(RealmContacts.class).limit(CONTACT_LIMIT).sort(RealmContactsFields.DISPLAY_NAME).findAll());
            });
            /*contactSize = results.size();*/
        }

        // contact paging and remove it for crash
        /*if (loadMore < contactSize)
            contacts.addAll(results.subList(first, loadMore));
        else if (first < contactSize)
            contacts.addAll(results.subList(first, contactSize));

        first = loadMore;
        loadMore = loadMore + LOAD_AVG;*/
        contacts.addAll(results);
        return contacts;
    }

    private static void getIgapContact() {
        if (results == null) {
            //todo : fixed query
            results = DbManager.getInstance().doRealmTask(realm -> {
                return realm.copyFromRealm(realm.where(RealmContacts.class).limit(CONTACT_LIMIT).sort(RealmContactsFields.DISPLAY_NAME).findAll());
            });
        }
    }

    /*public static int getContactSize() {
        return results.size();
    }*/

}
