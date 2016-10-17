package com.iGap.fragments;

import android.content.ContentProviderOperation;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.iGap.G;
import com.iGap.R;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.MaterialDesignTextView;

import java.util.ArrayList;


public class FragmentAddContact extends android.support.v4.app.Fragment {

    private TextView txtFirstName;
    private TextView txtLastName;
    private TextView txtPhoneNumber;
    private ViewGroup parent;

    public static FragmentAddContact newInstance() {
        return new FragmentAddContact();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_contact, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initComponent(view);

    }


    private void initComponent(View view) {

        TextView btnBack = (TextView) view.findViewById(R.id.ac_txt_back);
        btnBack.setTypeface(G.fontawesome);
        RippleView rippleBack = (RippleView) view.findViewById(R.id.ac_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(FragmentAddContact.this).commit();
            }
        });
//        btnBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                getActivity().getSupportFragmentManager().beginTransaction().remove(FragmentAddContact.this).commit();
//            }
//        });


        MaterialDesignTextView txtSet = (MaterialDesignTextView) view.findViewById(R.id.ac_txt_set);

        txtSet.setTypeface(G.flaticon);

        parent = (ViewGroup) view.findViewById(R.id.ac_layoutParent);
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        txtFirstName = (TextView) view.findViewById(R.id.ac_edt_firstName);
        txtLastName = (TextView) view.findViewById(R.id.ac_edt_lastName);
        txtPhoneNumber = (TextView) view.findViewById(R.id.ac_edt_phoneNumber);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        RippleView rippleSet = (RippleView) view.findViewById(R.id.ac_ripple_set);
        rippleSet.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if (txtFirstName.getText().toString().length() > 0 || txtLastName.getText().toString().length() > 0) {
                    if (txtPhoneNumber.getText().toString().length() > 0) {

                        String displayName = txtFirstName.getText().toString() + " " + txtLastName.getText().toString();
                        String phone = txtPhoneNumber.getText().toString();

                        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

                        ops.add(ContentProviderOperation.newInsert(
                                ContactsContract.RawContacts.CONTENT_URI)
                                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                                .build());

                        //------------------------------------------------------ Names
                        if (displayName != null) {
                            ops.add(ContentProviderOperation.newInsert(
                                    ContactsContract.Data.CONTENT_URI)
                                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                    .withValue(ContactsContract.Data.MIMETYPE,
                                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                                    .withValue(
                                            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                                            displayName).build());
                        }
                        //------------------------------------------------------ Mobile Number
                        if (phone != null) {
                            ops.add(ContentProviderOperation.
                                    newInsert(ContactsContract.Data.CONTENT_URI)
                                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                    .withValue(ContactsContract.Data.MIMETYPE,
                                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                                    .build());
                        }

                        try {
                            G.context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                            Toast.makeText(G.context, "Save Ok: ", Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(G.context, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        getActivity().getSupportFragmentManager().beginTransaction().remove(FragmentAddContact.this).commit();

                    } else {
                        Toast.makeText(G.context, "Please Enter PhoneNumber", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(G.context, "Please Enter FirstName or LastName", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    //***************************************************************************************

}
