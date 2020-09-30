/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.helper;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.webkit.MimeTypeMap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import net.iGap.G;
import net.iGap.activities.ActivityMain;
import net.iGap.module.StartupActions;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import static net.iGap.G.context;
import static net.iGap.module.AttachFile.getFilePathFromUri;
import static net.iGap.module.AttachFile.getPathN;

/**
 * this class use when get share file in another app
 */

public class HelperGetDataFromOtherApp {

    public static boolean hasSharedData = false;
    public static ArrayList<SharedData> sharedList = new ArrayList<SharedData>();
    private Intent intent;

    public class SharedData {
        public String message = "";
        public String address = "";
        public FileType fileType;
    }

    public HelperGetDataFromOtherApp(AppCompatActivity activityCompat, Intent intent) {

        this.intent = intent;

        if (intent == null) {
            return;
        }

        if (new File(G.DIR_APP).exists() && new File(G.DIR_IMAGES).exists() && new File(G.DIR_VIDEOS).exists() && new File(G.DIR_AUDIOS).exists() && new File(G.DIR_DOCUMENT).exists() && new File(G.DIR_CHAT_BACKGROUND).exists() && new File(G.DIR_IMAGE_USER).exists() && new File(G.DIR_TEMP).exists()) {
            checkData(activityCompat, intent);
        } else {
            StartupActions.makeFolder();
        }
        checkData(activityCompat, intent);
    }

    public static FileType getMimeType(Uri uri) {
        String extension;
        FileType fileType = FileType.file;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(G.context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
        }

        if (extension == null) {
            return null;
        }
        extension = extension.toLowerCase();

        if (extension.endsWith("jpg") || extension.endsWith("jpeg") || extension.endsWith("png") || extension.endsWith("bmp") || extension.endsWith(".tiff")) {
            fileType = FileType.image;
        } else if (extension.endsWith("mp3") || extension.endsWith("ogg") || extension.endsWith("wma") || extension.endsWith("m4a") || extension.endsWith("amr") || extension.endsWith("wav") || extension.endsWith(".mid") || extension.endsWith(".midi")) {
            fileType = FileType.audio;
        } else if (extension.endsWith("mp4") || extension.endsWith("3gp") || extension.endsWith("avi") || extension.endsWith("mpg") || extension.endsWith("flv") || extension.endsWith("wmv") || extension.endsWith("m4v") || extension.endsWith(".mpeg")) {
            fileType = FileType.video;
        }

        return fileType;
    }

    /**
     * check intent data and get type and address message
     */
    private void checkData(AppCompatActivity activityCompat, Intent intent) {

        sharedList.clear();

        String action = intent.getAction();
        String type = intent.getType();
        if (action == null || type == null) return;

        if (Intent.ACTION_SEND.equals(action)) {

            if (type.equals("text/plain")) {

                if (intent.getParcelableExtra(Intent.EXTRA_STREAM) != null)
                    SetOutPutSingleFile(FileType.file);
                else
                    handleSendText(intent);
            } else if (type.startsWith("image/")) {

                SetOutPutSingleFile(FileType.image);
            } else if (type.startsWith("video/")) {

                SetOutPutSingleFile(FileType.video);
            } else if (type.startsWith("audio/")) {

                SetOutPutSingleFile(FileType.audio);
            } else {

                SetOutPutSingleFile(FileType.file);
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action)) {

            if (type.startsWith("image/")) {

                SetOutPutMultipleFile(FileType.image);
            } else if (type.startsWith("video/")) {

                SetOutPutMultipleFile(FileType.video);
            } else if (type.startsWith("audio/")) {

                SetOutPutMultipleFile(FileType.audio);
            } else {

                SetOutPutMultipleFile(FileType.file);
            }
        }

        if (hasSharedData && ActivityMain.isOpenChatBeforeSheare) {
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    new HelperFragment(activityCompat.getSupportFragmentManager()).removeAll(true);
                }
            });
        }
    }

    //*****************************************************************************************************

    private void SetOutPutSingleFile(FileType type) {
        Uri fileAddressUri = intent.getParcelableExtra(Intent.EXTRA_STREAM); // get file attachment
        //String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT); get text
        if (fileAddressUri != null) {
            String extension = HelperString.dotSplit(fileAddressUri.getPath());
            /**
             * check mp4 because telegram sometimes send mp4 format with image type!!!
             */
            if (extension != null && extension.equals("mp4")) {
                type = FileType.video;
            }

            String _path = null;
            _path = getFilePathFromUri(fileAddressUri);

            if (fileAddressUri.getScheme() != null && fileAddressUri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
                if (_path == null) {
                    _path = getPathN(fileAddressUri, type, intent.getType());
                } else {
                    try {
                        FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", new File(_path));
                    } catch (IllegalArgumentException e) {
                        _path = getPathN(fileAddressUri, type, intent.getType());
                    }
                }
            }

            if (_path != null) {
                hasSharedData = true;

                SharedData _SharedData = new SharedData();
                _SharedData.address = _path;
                _SharedData.fileType = type;

                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                if (sharedText != null) {
                    _SharedData.message = sharedText;
                }

                sharedList.add(_SharedData);

            }

        }
    }

    //*****************************************************************************************************

    private void SetOutPutMultipleFile(FileType type) {

        ArrayList<Uri> fileAddressUri = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (fileAddressUri != null) {

            for (int i = 0; i < fileAddressUri.size(); i++) {
                Uri _Uri = fileAddressUri.get(i);
                String _path = null;

                _path = getFilePathFromUri(_Uri);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && _Uri.getScheme() != null && _Uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
                    if (_path == null) {
                        _path = getPathN(_Uri, type, intent.getType());
                    } else {
                        try {
                            FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", new File(_path));
                        } catch (IllegalArgumentException e) {
                            _path = getPathN(_Uri, type, intent.getType());
                        }
                    }
                }

                if (_path != null) {
                    FileType fileType = getMimeType(fileAddressUri.get(i));
                    if (fileType != null) {
                        SharedData _SharedData = new SharedData();
                        _SharedData.address = _path;
                        _SharedData.fileType = fileType;
                        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                        if (sharedText != null) {
                            _SharedData.message = sharedText;
                        }
                        sharedList.add(_SharedData);
                    }
                }
            }

            if (sharedList.size() > 0) {
                hasSharedData = true;
            }
        }
    }

    //*****************************************************************************************************

    private void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            hasSharedData = true;
            SharedData _SharedData = new SharedData();
            _SharedData.fileType = FileType.message;
            _SharedData.message = sharedText;
            sharedList.add(_SharedData);
        } else {
            SetOutPutSingleFile(FileType.file);
        }
    }

    //*****************************************************************************************************

    /**
     * get every data in bundle from intent
     */
    private void getAllDAtaInIntent(Intent intent) {

        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            Set<String> keys = bundle.keySet();
            Iterator<String> it = keys.iterator();

            while (it.hasNext()) {
                String key = it.next();
            }
        }
    }

    //*****************************************************************************************************

    public enum FileType {
        message, video, file, audio, image, gif
    }
}
