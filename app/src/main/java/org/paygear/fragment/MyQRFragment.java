package org.paygear.fragment;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import net.iGap.R;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;

import org.paygear.RaadApp;
import org.paygear.WalletActivity;
import org.paygear.utils.QRUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;

import ir.radsense.raadcore.app.NavigationBarActivity;
import ir.radsense.raadcore.model.Account;
import ir.radsense.raadcore.model.QR;
import ir.radsense.raadcore.utils.RaadCommonUtils;
import ir.radsense.raadcore.utils.Typefaces;


public class MyQRFragment extends Fragment {
    File sdDir;
    File file;


    public MyQRFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_qr, container, false);
        ViewGroup rootView = view.findViewById(R.id.rootView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            rootView.setBackgroundColor(Color.parseColor(WalletActivity.backgroundTheme));
        }


        HelperToolbar toolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLogoShown(true)
                .setLeftIcon(R.string.icon_back)
                .setRightIcons(R.string.icon_share)
                .setDefaultTitle(getString(R.string.my_qr))
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        if (getActivity() != null)
                            getActivity().onBackPressed();
                    }

                    @Override
                    public void onRightIconClickListener(View view) {
                        if (isStoragePermissionGranted()) {

                            Bitmap qrBitmap = QRUtils.getQR(getContext(), QRUtils.generateQRContent(QR.QR_TYPE_DIRECT_PAY, RaadApp.me), RaadCommonUtils.getPx(200, getContext()));
                            share(qrBitmap);
                        }
                    }
                });

        LinearLayout lytToolbar = view.findViewById(R.id.toolbarLayout);
        lytToolbar.addView(toolbar.getView());

        if (RaadApp.selectedMerchant==null) {
            toolbar.setDefaultTitle(getString(R.string.my_qr));
        }else {
            if (RaadApp.selectedMerchant.getName() != null && !RaadApp.selectedMerchant.getName().equals("")) {
                toolbar.setDefaultTitle(getString(R.string.qr) + " " + RaadApp.selectedMerchant.getName());
            } else {
                toolbar.setDefaultTitle(getString(R.string.qr) + " " + RaadApp.selectedMerchant.getUsername());
            }
        }


        ViewGroup root_current = view.findViewById(R.id.root_current);
        root_current.setBackgroundColor(Color.parseColor(WalletActivity.backgroundTheme));




        ImageView qrImage = view.findViewById(R.id.image);
        TextView hint = view.findViewById(R.id.hint);
        Typefaces.setTypeface(getContext(), Typefaces.IRAN_LIGHT, hint);

        ViewCompat.setBackground(qrImage,
                RaadCommonUtils.getRectShape(getContext(), android.R.color.white, 8, 0));

        if (RaadApp.selectedMerchant==null) {
            qrImage.setImageBitmap(QRUtils.getQR(getContext(),
                    QRUtils.generateQRContent(QR.QR_TYPE_DIRECT_PAY, RaadApp.me),
                    RaadCommonUtils.getPx(200, getContext())));
        }else {
            Account account=new Account();
            account.id=RaadApp.selectedMerchant.get_id();
            account.name=RaadApp.selectedMerchant.getName();
            account.username=RaadApp.selectedMerchant.getUsername();
            account.profilePicture=RaadApp.selectedMerchant.getProfile_picture();
            qrImage.setImageBitmap(QRUtils.getQR(getContext(),
                    QRUtils.generateQRContent(QR.QR_TYPE_DIRECT_PAY,account),
                    RaadCommonUtils.getPx(200, getContext())));
        }


        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getActivity() instanceof  NavigationBarActivity)
        ((NavigationBarActivity) getActivity()).broadcastMessageToPreviousFragment(
                MyQRFragment.this, null, ScannerFragment.class);
    }

    private Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), inImage, "Title", null);
        return path != null ? Uri.parse(path) : null;
    }

    private void share(Bitmap bitmap) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, getImageUri(bitmap));
        shareIntent.setType("image/jpeg");
        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.my_qr)));

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            //resume tasks needing this permission
            Bitmap qrBitmap = QRUtils.getQR(getContext(), QRUtils.generateQRContent(QR.QR_TYPE_DIRECT_PAY, RaadApp.me), RaadCommonUtils.getPx(200, getContext()));
            share(qrBitmap);
        }
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {

                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }
}
