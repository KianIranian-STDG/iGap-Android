package org.paygear.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import net.iGap.G;

import org.paygear.RaadApp;
import org.paygear.web.Web;

import java.io.File;

import ir.radsense.raadcore.model.Auth;

/**
 * Created by Software1 on 9/19/2017.
 */

public class Utils {

    /*public static boolean isValidJson(String string) {
        try {
            Gson gson = new Gson();
            gson.fromJson(string, Object.class);
            return true;
        } catch(com.google.gson.JsonSyntaxException ex) {
            return false;
        }
    }*/


    public static void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
/*
    public static void changeLocale(Context context, String lan) {
        SettingHelper.putString(context, SettingHelper.APP_LANGUAGE, lan);
        setLocale(context, lan);
    }

    public static void setLocale(Context context) {
        String lan = SettingHelper.getString(context, SettingHelper.APP_LANGUAGE, "fa");
        setLocale(context, lan);
    }

    public static void setLocale(Context context, String lan) {
        Raad.language = lan;
        Raad.isFA = lan.toLowerCase().equals("fa");
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            conf.setLocale(new Locale(lan.toLowerCase())); // API 17+ only.}
        } else {
            conf.locale = new Locale(lan.toLowerCase());
        }
        res.updateConfiguration(conf, dm);

    }

    public static void setInstart(Context context, String lan) {
        Raad.language = lan;
        Raad.isFA = lan.toLowerCase().equals("fa");
    }

    public static Context updateResources(Context baseContext) {
        String selectedLanguage = WalletActivity.selectedLanguage;
        if (selectedLanguage == null) {
            selectedLanguage = "en";
        }

        Locale locale = new Locale(selectedLanguage);
        Locale.setDefault(locale);

        Resources res = baseContext.getResources();
        Configuration configuration = res.getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale);
        } else {
            configuration.locale = locale;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            baseContext = baseContext.createConfigurationContext(configuration);
        } else {
            res.updateConfiguration(configuration, res.getDisplayMetrics());
        }

//        G.context = baseContext;

        return baseContext;
    }
*/

    /*public static void setShadow(View view, Drawable sd) {
        RoundRectShape rss = new RoundRectShape(new float[] { 12f, 12f, 12f,
                12f, 12f, 12f, 12f, 12f }, null, null);
        ShapeDrawable sds = new ShapeDrawable(rss);
        sds.setShaderFactory(new ShapeDrawable.ShaderFactory() {

            @Override
            public Shader resize(int width, int height) {
                LinearGradient lg = new LinearGradient(0, 0, 0, height,
                        new int[] { Color.parseColor("#e5e5e5"),
                                Color.parseColor("#e5e5e5"),
                                Color.parseColor("#e5e5e5"),
                                Color.parseColor("#e5e5e5") }, new float[] { 0,
                        0.50f, 0.50f, 1 }, Shader.TileMode.REPEAT);
                return lg;
            }
        });

        LayerDrawable ld = new LayerDrawable(new Drawable[] { sds, sd });
        ld.setLayerInset(0, 5, 5, 0, 0); // inset the shadow so it doesn't start right at the left/top
        ld.setLayerInset(1, 0, 0, 5, 5); // inset the top drawable so we can leave a bit of space for the shadow to use

        view.setBackgroundDrawable(ld);
    }*/

    /*public static int getAColor() {
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        return color;
    }*/

    public static String formatCardNumber(String number) {
        int c = number.length();
        int sec = c / 4;
        int rem = c % 4;
        //if (sec > 0 && rem == 0)
        //sec -= 1;
        String formatted = "";
        for (int i = 0; i < sec; i++) {
            formatted += number.subSequence((i * 4), (i * 4) + 4);
            if (i < 3)
                formatted += " - ";
        }

        if (rem > 0) {
            formatted += number.subSequence((sec*4), (sec*4) + rem);
        }

        if (formatted.endsWith(" - "))
            formatted = formatted.substring(0, formatted.length() - 3);
        return formatted;
    }

    /*public static void showCustomTab(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            String EXTRA_CUSTOM_TABS_SESSION = "android.support.customtabs.extra.SESSION";
            Bundle extras = new Bundle();
            extras.putBinder(EXTRA_CUSTOM_TABS_SESSION, null);

            String EXTRA_CUSTOM_TABS_TOOLBAR_COLOR = "android.support.customtabs.extra.TOOLBAR_COLOR";
            //intent.putExtra(EXTRA_CUSTOM_TABS_TOOLBAR_COLOR, webPageColor);

            intent.putExtras(extras);
            intent.setPackage("com.android.chrome");

            try {
                context.startActivity(intent);
                return;
            } catch (ActivityNotFoundException e) {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            }
        }

        try {
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "No browser found", Toast.LENGTH_SHORT).show();
        }
    }*/

    /*public static void showNotification(Context context, Intent intent, String title, String message, int type) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, type, intent, PendingIntent.FLAG_UPDATE_CURRENT);



        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setContentTitle(title != null ? title : context.getString(R.string.app_name))
                .setContentText(RTL_CHAR + message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setAutoCancel(true)
                .setPriority(type == RaadApp.NOTIFICATION_TYPE_NEW_MESSAGE ? NotificationCompat.PRIORITY_MAX : NotificationCompat.PRIORITY_DEFAULT)
                .setShowWhen(true);
        //.setSmallIcon(R.drawable.ic_raad_notification)
        //.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_raad_logo));

        NotificationManager manager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(type, builder.build());
    }*/

    /*public static void cancelAllNotification(Context context) {
        NotificationManager manager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(RaadApp.NOTIFICATION_TYPE_LIKE);
        manager.cancel(RaadApp.NOTIFICATION_TYPE_COMMENT);
        manager.cancel(RaadApp.NOTIFICATION_TYPE_NEW_MESSAGE);
        manager.cancel(RaadApp.NOTIFICATION_TYPE_PAY_COMPLETE);
        manager.cancel(RaadApp.NOTIFICATION_TYPE_COUPON);
        manager.cancel(RaadApp.NOTIFICATION_TYPE_DELIVERY);
        manager.cancel(RaadApp.NOTIFICATION_TYPE_FOLLOW);
    }*/

    public static void deleteFile(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteFile(child);

        fileOrDirectory.delete();
    }

    public static void signOutAndGoLogin(Activity activity) {

        RaadApp.paygearCard = null;
        RaadApp.cards = null;
        RaadApp.me = null;

        SettingHelper.PrefsSave(activity.getApplicationContext(),SettingHelper.USER_ACCOUNT,null);


        deleteAllUserData(activity);
        Auth.release();
        Web.getInstance().release();
    }

    public static void signOutWallet() {
        RaadApp.paygearCard = null;
        RaadApp.cards = null;
        RaadApp.me = null;

        SettingHelper.PrefsSave(G.context,SettingHelper.USER_ACCOUNT,null);

        SettingHelper.remove(G.context, SettingHelper.LOGIN_STEP);
        SettingHelper.remove(G.context, SettingHelper.SCANNER_TIPS);
        SettingHelper.remove(G.context, SettingHelper.DEVICE_TOKEN);
        SettingHelper.remove(G.context, SettingHelper.TOKEN_SENT_TO_SERVER);

        SettingHelper.remove(G.context, "service_token");

        SettingHelper.remove(G.context, SettingHelper.USER_ACCOUNT);

        Auth.release();
        Web.getInstance().release();
    }

    public static void deleteAllUserData(Activity activity) {
        activity.getDatabasePath("raad").delete();
        deleteFile(activity.getFilesDir());

        SettingHelper.remove(activity.getApplicationContext(), SettingHelper.LOGIN_STEP);
        SettingHelper.remove(activity.getApplicationContext(), SettingHelper.SCANNER_TIPS);
        SettingHelper.remove(activity.getApplicationContext(), SettingHelper.DEVICE_TOKEN);
        SettingHelper.remove(activity.getApplicationContext(), SettingHelper.TOKEN_SENT_TO_SERVER);

        SettingHelper.remove(activity.getApplicationContext(), "service_token");

        SettingHelper.remove(activity.getApplicationContext(), SettingHelper.USER_ACCOUNT);
    }

    /*public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }*/


    /*private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }*/

    /*public static ArrayList<Contact> loadContacts(Context context) {
        ArrayList<Contact> contacts = new ArrayList<>();
        Cursor phones = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (phones == null)
            return contacts;
        Contact previousContact = null;
        while (phones.moveToNext()) {

            //String id = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phone = phone.replace("+", "").replace(" ", "");

            Contact contact = new Contact(name, phone);
            *//*int len = contact.phone.length();
            if (len >= 10 && contact.phone.substring(len - 10, len).equals(auth.phone))
                continue;*//*
            //Log.i("GH_Contact", "ID: " + id + "\nName: " + name + "\nNumber: " + phone);

            if (previousContact == null || !previousContact.mobile.equals(contact.mobile))
                contacts.add(contact);

            previousContact = contact;
        }
        phones.close();
        return contacts;
    }*/
}
