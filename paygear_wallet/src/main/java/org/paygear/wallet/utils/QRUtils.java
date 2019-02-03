package org.paygear.wallet.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.HybridBinarizer;
import com.journeyapps.barcodescanner.BarcodeEncoder;


import org.paygear.wallet.R;
import org.paygear.wallet.fragment.AccountPaymentDialog;

import ir.radsense.raadcore.model.Account;
import ir.radsense.raadcore.model.Coupon;
import ir.radsense.raadcore.model.QR;
import ir.radsense.raadcore.utils.RaadCommonUtils;

import static ir.radsense.raadcore.model.QR.QR_TYPE_ACCOUNT;
import static ir.radsense.raadcore.model.QR.QR_TYPE_COUPON;
import static ir.radsense.raadcore.model.QR.QR_TYPE_DIRECT_PAY;

/**
 * Created by Software1 on 9/28/2016.
 */
public class QRUtils {

    public static final String APP_LINK_QUERY = "https://paygear.ir/dl?jj=";

    public static Bitmap getOfflinePaymentQR(Context context, String qrData) {

        int size = RaadCommonUtils.getPx(250, context);
        Bitmap bitmap = null;
        BarcodeEncoder qrEncoder = new BarcodeEncoder();
        try {
            bitmap = qrEncoder.encodeBitmap(qrData, BarcodeFormat.QR_CODE, size, size);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public static String readQRImage(Bitmap bMap) {
        String contents = null;

        int width = bMap.getWidth();
        int height = bMap.getHeight();
        int[] intArray = new int[width * height];
        //copy pixel data from the Bitmap into the 'intArray' array
        bMap.getPixels(intArray, 0, width, 0, 0, width, height);

        LuminanceSource source = new RGBLuminanceSource(width, height, intArray);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Reader reader = new MultiFormatReader();// use this otherwise ChecksumException
        try {
            Result result = reader.decode(bitmap);
            contents = result.getText();
            //byte[] rawBytes = result.getRawBytes();
            //BarcodeFormat format = result.getBarcodeFormat();
            //ResultPoint[] points = result.getResultPoints();
        } catch (NotFoundException | ChecksumException | FormatException e) { e.printStackTrace(); }
        return contents;
    }

    public static Bitmap getQR(Context context, QR qr) {
        String qrData;

        /*QR qr = new QR();
        qr.code = Auth.hash;
        qr.type = "3";
        qr.content = "";*/
        Gson gson = new Gson();
        qrData = gson.toJson(qr);

        int size = 0;
        if (qr.type.equals("1") || qr.type.equals("2"))
            size = (int)context.getResources().getDimension(R.dimen.coupon_qr_size);
        else
            size = RaadCommonUtils.getPx(200, context);

        return getQR(context, qrData, size);
    }

    public static Bitmap getQR(Context context, QR qr, int pxSize) {
        String qrData;

        Gson gson = new Gson();
        qrData = gson.toJson(qr);

        return getQR(context, qrData, pxSize);
    }


    public static Bitmap getQR(Context context, String qrData, int pxSize) {
        //Log.i("GH_QR_size", String.valueOf(RaadCommonUtils.getDp(pxSize, context)));

        Bitmap bitmap = null;
        BarcodeEncoder qrEncoder = new BarcodeEncoder();
        try {
            bitmap = qrEncoder.encodeBitmap(qrData, BarcodeFormat.QR_CODE, pxSize, pxSize);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        return bitmap;
    }


    public static String generateQRContent(int type, Object data) {
        switch (type) {
            case QR_TYPE_COUPON:
                QR qr = new QR();
                qr.type = String.valueOf(type);
                qr.code = ((Coupon) data).refCode;
                Gson gson = new Gson();
                return APP_LINK_QUERY + gson.toJson(qr);
            case QR_TYPE_ACCOUNT:
                qr = new QR();
                qr.type = String.valueOf(type);
                qr.code = ((Account) data).id;
                qr.AT = String.valueOf(((Account)data).type);
                gson = new Gson();
                return APP_LINK_QUERY + gson.toJson(qr);
            case QR_TYPE_DIRECT_PAY:
                qr = new QR();
                qr.type = String.valueOf(type);
                qr.code = ((Account) data).id;
                //qr.AT = String.valueOf(((Account)data).type);
                gson = new Gson();
                return APP_LINK_QUERY + gson.toJson(qr);
            case 9:
                qr = new QR();
                qr.type = String.valueOf(type);
                qr.code = ((Account) data).id;
                //qr.AT = String.valueOf(((Account)data).type);
                gson = new Gson();
                return APP_LINK_QUERY + gson.toJson(qr);
        }
        return null;
    }

    public static QR getQRModel(String content) {
        if (TextUtils.isEmpty(content)) {
            return null;
        }

        content = content.replace("'", "\"");

        if (content.startsWith(APP_LINK_QUERY)) {
            content = content.replace(APP_LINK_QUERY, "");
        }

        QR qr;
        try {
            Gson gson = new Gson();
            qr = gson.fromJson(content, QR.class);
            return qr;
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean handleBarcode(final Activity activity, String scanContent) {
        return handleBarcode(activity, getQRModel(scanContent));
    }

    public static boolean handleBarcode(final Activity activity, QR qr) {

        if (qr == null || qr.type == null) {
            Toast.makeText(activity.getBaseContext(), R.string.qr_not_detected, Toast.LENGTH_SHORT).show();
            return false;
        }


        if (String.valueOf(QR_TYPE_DIRECT_PAY).equals(qr.type)) { //payment (without price)

            AccountPaymentDialog.newInstance(qr.code, null)
                    .show(((AppCompatActivity)activity).getSupportFragmentManager(), "AccountPaymentDialog");
            return true;
        }
        else if ("9".equals(qr.type)) { //payment (with price)
            long price;
            try {
                price = Long.parseLong(qr.price);
            } catch (Exception e) {
                return false;
            }

            //Payment payment = new Payment();
            //payment.account = new Account();
            //payment.account.id = qr.code;
            //payment.price = price;

            AccountPaymentDialog.newInstance(qr.code, null)
                    .show(((AppCompatActivity)activity).getSupportFragmentManager(), "AccountPaymentDialog");
            return true;
        }

        return false;
    }

}
