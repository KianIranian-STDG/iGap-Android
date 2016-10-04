package com.iGap.fragments;

import android.content.Intent;
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
import com.iGap.module.MaterialDesignTextView;


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
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(FragmentAddContact.this).commit();
            }
        });


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
        txtSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtFirstName.getText().toString().length() > 0 || txtLastName.getText().toString().length() > 0) {
                    if (txtPhoneNumber.getText().toString().length() > 0) {

                        String displayName = txtFirstName.getText().toString() + " " + txtLastName.getText().toString();
                        String phone = txtPhoneNumber.getText().toString();

                        Intent intent = new Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI);
                        intent.putExtra(ContactsContract.Intents.Insert.NAME, displayName);
                        intent.putExtra(ContactsContract.Intents.Insert.PHONE, phone);
                        startActivity(intent);
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
