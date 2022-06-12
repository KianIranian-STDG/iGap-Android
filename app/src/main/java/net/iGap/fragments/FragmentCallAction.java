package net.iGap.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.iGap.R;
import net.iGap.messenger.theme.Theme;
import net.iGap.module.dialog.BaseBottomSheet;

public class FragmentCallAction extends BaseBottomSheet {
    private View rootView;
    private String phoneNumber;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_call_action, container);
        TextView call = rootView.findViewById(R.id.call);
        call.setTextColor(Theme.getColor(Theme.key_title_text));
        TextView message = rootView.findViewById(R.id.message);
        message.setTextColor(Theme.getColor(Theme.key_title_text));
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        View callAction = rootView.findViewById(R.id.ll_callAction_call);
        callAction.setOnClickListener(v -> {
            if (phoneNumber != null) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phoneNumber));
                getContext().startActivity(intent);
                dismiss();
            }
        });

        View messageAction = rootView.findViewById(R.id.ll_callAction_textMessage);
        messageAction.setOnClickListener(v -> {
            if (phoneNumber != null) {
                dismiss();
                Uri uri = Uri.parse("smsto:" + phoneNumber);
                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                intent.putExtra("sms_body", "");
                startActivity(intent);
            }
        });
    }


    @Override
    public int getTheme() {
        return R.style.BaseBottomSheetDialog;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
