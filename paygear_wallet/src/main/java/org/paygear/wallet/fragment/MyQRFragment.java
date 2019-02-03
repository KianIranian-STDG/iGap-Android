package org.paygear.wallet.fragment;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import org.paygear.wallet.R;
import org.paygear.wallet.RaadApp;
import org.paygear.wallet.WalletActivity;
import org.paygear.wallet.utils.QRUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;

import ir.radsense.raadcore.app.NavigationBarActivity;
import ir.radsense.raadcore.app.RaadToolBar;
import ir.radsense.raadcore.model.Account;
import ir.radsense.raadcore.model.QR;
import ir.radsense.raadcore.utils.RaadCommonUtils;
import ir.radsense.raadcore.utils.Typefaces;


public class MyQRFragment extends Fragment {
    File sdDir;
    File file;

    private RaadToolBar appBar;

    public MyQRFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_qr, container, false);
        ViewGroup rootView = view.findViewById(R.id.rootView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            rootView.setBackgroundColor(Color.parseColor(WalletActivity.backgroundTheme_2));
        }
        appBar = view.findViewById(R.id.app_bar);
        appBar.setToolBarBackgroundRes(R.drawable.app_bar_back_shape, true);
        appBar.setToolBarBackgroundRes(R.drawable.app_bar_back_shape, true);
        appBar.getBack().getBackground().setColorFilter(new PorterDuffColorFilter(Color.parseColor(WalletActivity.primaryColor), PorterDuff.Mode.SRC_IN));
        if (RaadApp.selectedMerchant==null) {
            appBar.setTitle(getString(R.string.my_qr));
        }else {
            if (RaadApp.selectedMerchant.getName() != null && !RaadApp.selectedMerchant.getName().equals("")) {
                appBar.setTitle(getString(R.string.qr) + " " + RaadApp.selectedMerchant.getName());
            } else {
                appBar.setTitle(getString(R.string.qr) + " " + RaadApp.selectedMerchant.getUsername());
            }
        }
        appBar.addRightButton(R.drawable.ic_share_whire_24dp, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (isStoragePermissionGranted()) {

                   Bitmap qrBitmap = QRUtils.getQR(getContext(), QRUtils.generateQRContent(QR.QR_TYPE_DIRECT_PAY, RaadApp.me), RaadCommonUtils.getPx(200, getContext()));
                   share(qrBitmap);
               }


            }
        });


        ViewGroup root_current = view.findViewById(R.id.root_current);
        root_current.setBackgroundColor(Color.parseColor(WalletActivity.backgroundTheme));

        appBar.showBack();



        ImageView qrImage = view.findViewById(R.id.image);
        TextView hint = view.findViewById(R.id.hint);
        Typefaces.setTypeface(getContext(), Typefaces.IRAN_YEKAN_BOLD, hint);

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
        ((NavigationBarActivity) getActivity()).broadcastMessageToPreviousFragment(
                MyQRFragment.this, null, ScannerFragment.class);
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void share(Bitmap bitmap) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, getImageUri(getContext(),bitmap));
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
