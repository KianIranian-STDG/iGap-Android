package net.iGap.activities;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import io.realm.Realm;
import java.io.File;
import net.iGap.G;
import net.iGap.R;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.FileUtils;
import net.iGap.module.SHP_SETTING;
import net.iGap.realm.RealmRoomMessage;

public class ActivityManageSpace extends ActivityEnhanced {
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_space);

        sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);

        findViewById(R.id.asn_toolbar).setBackgroundColor(Color.parseColor(G.appBarColor));

        RippleView rippleBack = (RippleView) findViewById(R.id.stns_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                finish();
            }
        });

        final TextView txtSubKeepMedia = (TextView) findViewById(R.id.st_txt_sub_keepMedia);
        ViewGroup ltKeepMedia = (ViewGroup) findViewById(R.id.st_layout_keepMedia);
        TextView txtKeepMedia = (TextView) findViewById(R.id.st_txt_keepMedia);
        boolean isForever = sharedPreferences.getBoolean(SHP_SETTING.KEY_KEEP_MEDIA, true);
        if (isForever) {
            txtSubKeepMedia.setText(getResources().getString(R.string.keep_media_forever));
        } else {
            txtSubKeepMedia.setText(getResources().getString(R.string.keep_media_1week));
        }

        ltKeepMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(ActivityManageSpace.this).title(R.string.st_keepMedia).content(R.string.st_dialog_content_keepMedia).positiveText(getResources().getString(R.string.keep_media_forever)).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(SHP_SETTING.KEY_KEEP_MEDIA, false);
                        editor.apply();
                        txtSubKeepMedia.setText(getResources().getString(R.string.keep_media_forever));
                    }
                }).negativeText(getResources().getString(R.string.keep_media_1week)).onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(SHP_SETTING.KEY_KEEP_MEDIA, true);
                        editor.apply();
                        txtSubKeepMedia.setText(getResources().getString(R.string.keep_media_1week));
                    }
                }).show();
            }
        });


        final long sizeFolderPhoto = FileUtils.getFolderSize(new File(G.DIR_IMAGES));
        final long sizeFolderVideo = FileUtils.getFolderSize(new File(G.DIR_VIDEOS));
        final long sizeFolderDocument = FileUtils.getFolderSize(new File(G.DIR_DOCUMENT));
        final long sizeFolderAudio = FileUtils.getFolderSize(new File(G.DIR_AUDIOS));

        final long total = sizeFolderPhoto + sizeFolderVideo + sizeFolderDocument + sizeFolderAudio;

        final TextView txtSizeClearCach = (TextView) findViewById(R.id.st_txt_clearCache);
        txtSizeClearCach.setText(FileUtils.formatFileSize(total));

        Realm realm = Realm.getDefaultInstance();
        final long DbTotalSize = new File(realm.getConfiguration().getPath()).length();
        realm.close();

        final TextView txtDbTotalSize = (TextView) findViewById(R.id.st_txt_cleanUp);
        txtDbTotalSize.setText(FileUtils.formatFileSize(DbTotalSize));

        final ViewGroup lyCleanUp = (ViewGroup) findViewById(R.id.st_layout_cleanUp);
        lyCleanUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final MaterialDialog inDialog = new MaterialDialog.Builder(ActivityManageSpace.this).customView(R.layout.dialog_content_custom, true).build();
                View view = inDialog.getCustomView();

                inDialog.show();

                TextView txtTitle = (TextView) view.findViewById(R.id.txtDialogTitle);
                txtTitle.setText(getResources().getString(R.string.clean_up_chat_rooms));

                TextView iconTitle = (TextView) view.findViewById(R.id.iconDialogTitle);
                iconTitle.setText(R.string.md_clean_up);

                TextView txtContent = (TextView) view.findViewById(R.id.txtDialogContent);
                txtContent.setText(R.string.do_you_want_to_clean_all_data_in_chat_rooms);

                TextView txtCancel = (TextView) view.findViewById(R.id.txtDialogCancel);
                TextView txtOk = (TextView) view.findViewById(R.id.txtDialogOk);

                txtOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RealmRoomMessage.ClearAllMessage(true, 0);
                        Realm realm = Realm.getDefaultInstance();
                        final long DbTotalSize = new File(realm.getConfiguration().getPath()).length();
                        realm.close();
                        txtDbTotalSize.setText(FileUtils.formatFileSize(DbTotalSize));
                        inDialog.dismiss();
                    }
                });

                txtCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        inDialog.dismiss();
                    }
                });

            }
        });

         /*
          clear all video , image , document cache
         */
        LinearLayout ltClearCache = (LinearLayout) findViewById(R.id.st_layout_clearCache);
        ltClearCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final long sizeFolderPhotoDialog = FileUtils.getFolderSize(new File(G.DIR_IMAGES));
                final long sizeFolderVideoDialog = FileUtils.getFolderSize(new File(G.DIR_VIDEOS));
                final long sizeFolderDocumentDialog = FileUtils.getFolderSize(new File(G.DIR_DOCUMENT));
                final long sizeFolderAudio = FileUtils.getFolderSize(new File(G.DIR_AUDIOS));

                boolean wrapInScrollView = true;
                final MaterialDialog dialog = new MaterialDialog.Builder(ActivityManageSpace.this).title(getResources().getString(R.string.st_title_Clear_Cache)).customView(R.layout.st_dialog_clear_cach, wrapInScrollView).positiveText(getResources().getString(R.string.st_title_Clear_Cache)).show();

                View view = dialog.getCustomView();

                final File filePhoto = new File(G.DIR_IMAGES);
                assert view != null;
                TextView photo = (TextView) view.findViewById(R.id.st_txt_sizeFolder_photo);
                photo.setText(FileUtils.formatFileSize(sizeFolderPhotoDialog));

                final CheckBox checkBoxPhoto = (CheckBox) view.findViewById(R.id.st_checkBox_photo);
                final File fileVideo = new File(G.DIR_VIDEOS);
                TextView video = (TextView) view.findViewById(R.id.st_txt_sizeFolder_video);
                video.setText(FileUtils.formatFileSize(sizeFolderVideoDialog));

                final CheckBox checkBoxVideo = (CheckBox) view.findViewById(R.id.st_checkBox_video_dialogClearCash);

                final File fileDocument = new File(G.DIR_DOCUMENT);
                TextView document = (TextView) view.findViewById(R.id.st_txt_sizeFolder_document_dialogClearCash);
                document.setText(FileUtils.formatFileSize(sizeFolderDocumentDialog));

                final CheckBox checkBoxDocument = (CheckBox) view.findViewById(R.id.st_checkBox_document_dialogClearCash);

                final File fileAudio = new File(G.DIR_AUDIOS);
                TextView txtAudio = (TextView) view.findViewById(R.id.st_txt_audio_dialogClearCash);
                txtAudio.setText(FileUtils.formatFileSize(sizeFolderAudio));
                final CheckBox checkBoxAudio = (CheckBox) view.findViewById(R.id.st_checkBox_audio_dialogClearCash);

                long rTotalSize = sizeFolderPhotoDialog + sizeFolderVideoDialog + sizeFolderDocumentDialog + sizeFolderAudio;
                final TextView txtTotalSize = (TextView) view.findViewById(R.id.st_txt_totalSize_dialogClearCash);
                txtTotalSize.setText(FileUtils.formatFileSize(rTotalSize));


                dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (checkBoxPhoto.isChecked()) {
                            for (File file : filePhoto.listFiles()) {
                                if (!file.isDirectory()) file.delete();
                            }
                        }
                        if (checkBoxVideo.isChecked()) {
                            for (File file : fileVideo.listFiles()) {
                                if (!file.isDirectory()) file.delete();
                            }
                        }
                        if (checkBoxDocument.isChecked()) {
                            for (File file : fileDocument.listFiles()) {
                                if (!file.isDirectory()) file.delete();
                            }
                        }
                        if (checkBoxAudio.isChecked()) {
                            for (File file : fileAudio.listFiles()) {
                                if (!file.isDirectory()) file.delete();
                            }
                        }

                        long afterClearSizeFolderPhoto = FileUtils.getFolderSize(new File(G.DIR_IMAGES));
                        long afterClearSizeFolderVideo = FileUtils.getFolderSize(new File(G.DIR_VIDEOS));
                        long afterClearSizeFolderDocument = FileUtils.getFolderSize(new File(G.DIR_DOCUMENT));
                        long afterClearSizeFolderAudio = FileUtils.getFolderSize(new File(G.DIR_AUDIOS));
                        long afterClearTotal = afterClearSizeFolderPhoto + afterClearSizeFolderVideo + afterClearSizeFolderDocument + afterClearSizeFolderAudio;
                        txtSizeClearCach.setText(FileUtils.formatFileSize(afterClearTotal));
                        txtTotalSize.setText(FileUtils.formatFileSize(afterClearTotal));
                        dialog.dismiss();
                    }
                });
            }
        });

    }
}
