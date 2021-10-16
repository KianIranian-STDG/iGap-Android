package net.iGap.controllers;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;

import net.iGap.G;
import net.iGap.adapter.payment.ContactNumber;
import net.iGap.helper.FileLog;
import net.iGap.helper.HelperPermission;
import net.iGap.module.AndroidUtils;
import net.iGap.module.Contacts;
import net.iGap.network.RequestManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PhoneContactProvider {

    private List<ContactNumber> contactsList = new ArrayList<>();
    private boolean loadingContact;

    private static volatile PhoneContactProvider instance;

    public static PhoneContactProvider getInstance() {
        PhoneContactProvider localInstance = instance;
        if (localInstance == null) {
            synchronized (RequestManager.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new PhoneContactProvider();
                }
            }
        }
        return localInstance;
    }

    public void getAllPhoneContactForPayment(Contacts.Delegate delegate) {
        if (!HelperPermission.grantedContactPermission()) {
            return;
        }

        getContactNumbers(delegate);
    }

    private void getContactNumbers(final Contacts.Delegate delegate) {

        if (contactsList.isEmpty()) {
            if (loadingContact) {
                return;
            }

            loadingContact = true;

            AndroidUtils.globalQueue.postRunnable(() -> {
                List<ContactNumber> result = new ArrayList<>();

                ContentResolver cr = G.context.getContentResolver();
                String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
                try (Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, sortOrder)) {
                    if (cur != null && cur.getCount() > 0) {
                        while (cur.moveToNext()) {
                            String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                            String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                            if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                                Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                                while (pCur != null && pCur.moveToNext()) {
                                    String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                                    ContactNumber contactNumber = normalizeContact(name, phoneNo);
                                    if (contactNumber != null)
                                        result.add(contactNumber);
                                }
                                if (pCur != null)
                                    pCur.close();
                            }
                        }
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }

                List<ContactNumber> contactNumberList = normalizeList(result);
                contactsList.addAll(contactNumberList);
                loadingContact = false;

                G.runOnUiThread(() -> delegate.onResult(contactNumberList));
            });
        } else {
            delegate.onResult(contactsList);
        }
    }

    private List<ContactNumber> normalizeList(List<ContactNumber> contactNumbers) {
        List<ContactNumber> result = new ArrayList<>();
        Collections.sort(contactNumbers);

        int lastIndex = 0;
        result.add(contactNumbers.get(lastIndex));
        for (int i = 1; i < contactNumbers.size(); i++) {
            if (!contactNumbers.get(lastIndex).displayName.equals(contactNumbers.get(i).displayName) || !contactNumbers.get(lastIndex).contactNumber.equals(contactNumbers.get(i).contactNumber)) {
                lastIndex = i;
                result.add(contactNumbers.get(lastIndex));
            }
        }
        return result;
    }

    private ContactNumber normalizeContact(String name, String phone) {
        ContactNumber contactNumber = new ContactNumber();
        try {
            if (phone != null && name != null) {
                String[] sp = name.split(" ");

                if (phone.trim().charAt(0) == '0') {
                    phone = "+98" + phone.trim().substring(1);
                }

                phone = phone.replace(" ", "");

                if (sp.length == 1) {
                    contactNumber.setFirstName(sp[0]);
                    contactNumber.lastName = "";
                    contactNumber.contactNumber = phone;
                    contactNumber.displayName = name;
                } else if (sp.length == 2) {
                    contactNumber.setFirstName(sp[0]);
                    contactNumber.setLastName(sp[1]);
                    contactNumber.setPhone(phone);
                    contactNumber.setDisplayName(name);
                } else if (sp.length == 3) {
                    contactNumber.setFirstName(sp[0]);
                    contactNumber.setLastName(sp[1] + " " + sp[2]);
                    contactNumber.setPhone(phone);
                    contactNumber.setDisplayName(name);
                } else if (sp.length >= 3) {
                    contactNumber.setFirstName(name);
                    contactNumber.setLastName("");
                    contactNumber.setPhone(phone);
                    contactNumber.setDisplayName(name);
                }
                return contactNumber;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        return null;
    }


}
