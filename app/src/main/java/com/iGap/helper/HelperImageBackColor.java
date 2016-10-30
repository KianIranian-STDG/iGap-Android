package com.iGap.helper;

import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

public class HelperImageBackColor {

    /**
     * this methode get a name and return a random color by this name
     */
    public static String getColor(String name) {

        int sum = 0;
        int color = 0;
        String str = "#dd3333";

        if (name == null) return str;

        for (int i = 0; i < name.length(); i++) {

            sum += name.codePointAt(i);
        }

        if (sum > 0) {
            color = sum % 12;
        }

        if (color == 0) {
            str = "#dd3333";
        } else if (color == 1) {
            str = "#df2592";
        } else if (color == 2) {
            str = "#df2551";
        } else if (color == 3) {
            str = "#9448e1";
        } else if (color == 4) {
            str = "#7200ff";
        } else if (color == 5) {
            str = "#5c9dff";
        } else if (color == 6) {
            str = "#0ca3b9";
        } else if (color == 7) {
            str = "#0cb99f";
        } else if (color == 8) {
            str = "#4fb559";
        } else if (color == 9) {
            str = "#b9d242";
        } else if (color == 10) {
            str = "#ff8a00";
        } else if (color == 11) str = "#f8b500";

        return str;
    }

    /**
     * get letter and color and size finally draw bitmap with this info
     *
     * @param with size of bitmap
     * @param text letter for drawing
     * @param color color of bitmap
     * @return bitmap that painted alphabet
     */

    public static Bitmap drawAlphabetOnPicture(int with, String text, String color) {

        //        String alphabetName = getFirstAlphabetName(text);
        String alphabetName = text;

        String mColor = "#f4f4f4";
        //
        //        if (color != "" && color != null) {
        //            mColor = color;
        //        }

        Log.i("CCC", "color : " + color);
        Log.i("CCC", "text : " + text);

        if (color == null || color.equals("")) {
            color = "#7f7f7f7f";
        }
        if (text == null || text.equals("")) {
            alphabetName = " ";
        } else {
            alphabetName = text.replace(" ", "");
        }

        if (alphabetName.length() >= 2) {
            alphabetName = alphabetName.substring(0, 1) + "\u200b" + alphabetName.substring(1, 2);
        }

        Bitmap bitmap = Bitmap.createBitmap(with, with, Bitmap.Config.ARGB_8888);
        //        bitmap.eraseColor(Color.parseColor(getColor(alphabetName)));
        bitmap.eraseColor(Color.parseColor(color));

        int fontsize = with / 3;
        Canvas cs = new Canvas(bitmap);

        Paint textPaint =
            new Paint(Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        textPaint.setMaskFilter(new BlurMaskFilter(1, BlurMaskFilter.Blur.SOLID));
        textPaint.setColor(Color.parseColor(mColor));
        //        textPaint.setTypeface(G.robotoRegular);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(fontsize);
        textPaint.setStyle(Paint.Style.FILL);
        //        textPaint.setTypeface(G.arialBold);
        cs.drawText(alphabetName, with / 2, with / 2 + fontsize / 3, textPaint);

        return bitmap;
    }

    /**
     * this method get a text and return first character of text and first character of last word of
     * text
     */
    public static String getFirstAlphabetName(String name) {
        name = name.trim();
        String[] splited = name.split("\\s+");

        int size = splited.length;
        String charfirst, mname, Upname;

        if (!splited[0].equals("") && splited[0] != null && !splited[0].isEmpty()) {
            if (size == 1) {
                charfirst = splited[0].trim();
                mname = charfirst.substring(0, 1);
            } else {
                charfirst = splited[0].trim();
                String charlast = splited[size - 1].trim();
                mname = charfirst.substring(0, 1) + charlast.substring(0, 1);
            }
            Upname = mname.toUpperCase();
        } else {
            Upname = " ";
        }

        return Upname;
    }
}
