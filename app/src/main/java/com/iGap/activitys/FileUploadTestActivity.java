package com.iGap.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.AsyncLayoutInflater;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.iGap.G;
import com.iGap.R;
import com.iGap.adapter.FileUploadTestAdapter;
import com.iGap.interface_package.OnFileUpload;
import com.iGap.interface_package.OnUserLogin;
import com.iGap.module.FileUploadStructure;
import com.iGap.module.FileUtils;
import com.iGap.module.Utils;
import com.iGap.proto.ProtoResponse;
import com.iGap.realm.RealmUserInfo;
import com.iGap.request.RequestFileUpload;
import com.iGap.request.RequestFileUploadInit;
import com.iGap.request.RequestUserLogin;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import io.realm.Realm;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 8/28/2016.
 */
public class FileUploadTestActivity extends ActivityEnhanced implements OnFileUpload {
    private static final int PICK_FILE_REQUEST_CODE = 0;

    private LinearLayout progressBarsLayout;
    private RecyclerView logs;
    private FileUploadTestAdapter adapter;

    // selected files (paths)
    private CopyOnWriteArrayList<FileUploadStructure> mSelectedFiles = new CopyOnWriteArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_upload_test);

        G.uploaderUtil.setActivityCallbacks(FileUploadTestActivity.this);

        // login required
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                userLogin();
            }
        }, 2000);

        // init pick files button
        Button pickFilesButton = (Button) findViewById(R.id.pickFiles);
        pickFilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
            }
        });

        progressBarsLayout = (LinearLayout) findViewById(R.id.progressViews);

        //init upload files button
        Button uploadFilesButton = (Button) findViewById(R.id.uploadFiles);
        uploadFilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateLogs("NOTE: TIMES ARE IN MILLISECONDS");

                for (FileUploadStructure fileUploadStructure : mSelectedFiles) {
                    // add progress bar
                    addProgressBar(fileUploadStructure.fileHash);

                    updateLogs("*** UPLOADING STARTED ***");
                    updateLogs("FILE NAME: " + fileUploadStructure.fileName);
                    updateLogs("FILE SIZE: " + Long.toString(fileUploadStructure.fileSize));
                    updateLogs("FILE HASH: " + fileUploadStructure.fileHash);

                    // start uploading
                    G.uploaderUtil.startUploading(fileUploadStructure.fileSize, fileUploadStructure.fileHash);
                }
            }
        });

        // init clear files button
        Button clearFiles = (Button) findViewById(R.id.clearFiles);
        clearFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adapter != null) {
                    adapter.clear();
                }
                mSelectedFiles.clear();
                progressBarsLayout.removeAllViews();
            }
        });

        // init clear logs button
        Button clearLogs = (Button) findViewById(R.id.clearLogs);
        clearLogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adapter != null) {
                    adapter.clear();
                }
            }
        });

        // init logs view and its adapter
        List<String> messages = new ArrayList<>();
        adapter = new FileUploadTestAdapter(messages);
        logs = (RecyclerView) findViewById(R.id.logs);
        logs.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        logs.setAdapter(adapter);
    }

    /**
     * create horizontal progress bar
     *
     * @param fileHash file hash
     * @return ProgressBar
     */
    private void addProgressBar(final String fileHash) {
        new AsyncLayoutInflater(getApplicationContext()).inflate(R.layout.test_progress_item, null, new AsyncLayoutInflater.OnInflateFinishedListener() {
            @Override
            public void onInflateFinished(View view, int resid, ViewGroup parent) {
                if (view instanceof ProgressBar) {
                    ((ProgressBar) view).setIndeterminate(false);
                    ((ProgressBar) view).setProgress(0);
                    view.setTag(fileHash);

                    progressBarsLayout.addView(view);
                }
            }
        });
    }

    /**
     * update progress view
     *
     * @param fileHash file hash
     * @param progress progress
     */
    private void updateProgressView(String fileHash, int progress) {
        Log.i("BreakPoint", "hash: " + fileHash + " - progress: " + Integer.toString(progress));
        for (int c = 0; c < progressBarsLayout.getChildCount(); c++) {
            ProgressBar view = (ProgressBar) progressBarsLayout.getChildAt(c);
            Log.i("BreakPoint", fileHash + " > ghable progress");
            if (view.getTag().equals(fileHash)) {
                view.setProgress(progress);
                Log.i("BreakPoint", fileHash + " > bade set shodane progress");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            String filePath = FileUtils.getPath(getApplicationContext(), data.getData());
            File file = new File(filePath);
            String fileName = file.getName();
            long fileSize = file.length();
            String fileHash = Utils.getFileHash(file, true);
            mSelectedFiles.add(new FileUploadStructure(fileName, fileSize, filePath, fileHash));

            if (adapter != null) {
                updateLogs("FILE SELECTED: " + fileName);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateLogs(String message) {
        if (adapter != null) {
            adapter.insert(message);
            logs.smoothScrollToPosition(adapter.getItemCount());
        }
    }

    /**
     * get file with hash string
     *
     * @param fileHash file hash
     * @return FileUploadStructure
     */
    @Nullable
    private FileUploadStructure getSelectedFile(String fileHash) {
        for (FileUploadStructure structure : mSelectedFiles) {
            if (structure.fileHash.equalsIgnoreCase(fileHash)) {
                return structure;
            }
        }
        return null;
    }

    private void userLogin() {
        Realm realm = Realm.getDefaultInstance();
        G.onUserLogin = new OnUserLogin() {
            @Override
            public void onLogin() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(G.context, "User Login!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onLoginError() {

            }
        };

        RealmUserInfo userInfo = realm.where(RealmUserInfo.class).findFirst();
        if (!G.userLogin && userInfo != null) { //  need login
            new RequestUserLogin().userLogin(userInfo.getToken());
        }

        realm.close();
    }

    @Override
    public void onFileUploadComplete(final String fileHashAsIdentity, ProtoResponse.Response response) {
        // remove from selected files to prevent calling this method multiple times
        // multiple calling may occurs because of the server
        try {
            removeFromSelectedFiles(fileHashAsIdentity);
        } catch (Exception e) {
            Log.i("BreakPoint", e.getMessage());
            e.printStackTrace();
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateLogs("CALLED: onFileUploadComplete, " + Long.toString(System.currentTimeMillis()));
                updateLogs("FOR HASH: " + fileHashAsIdentity);
            }
        });
    }

    /**
     * remove from selected files
     *
     * @param fileHash file hash
     */
    private boolean removeFromSelectedFiles(String fileHash) {
        for (FileUploadStructure fileUploadStructure : mSelectedFiles) {
            if (fileUploadStructure.fileHash.equalsIgnoreCase(fileHash)) {
                mSelectedFiles.remove(fileUploadStructure);
                return true;
            }
        }
        return false;
    }

    @Override
    public void OnFileUploadInit(String token, double progress, long offset, int limit, String server, String fileHashAsIdentity, ProtoResponse.Response response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateLogs("CALLED: onFileUploadInitResponse, " + Long.toString(System.currentTimeMillis()));
            }
        });
        // token needed for requesting upload
        // updating structure with new token
        FileUploadStructure fileUploadStructure = getSelectedFile(fileHashAsIdentity);
        fileUploadStructure.token = token;

        // not already uploaded
        if (progress != 100.0) {
            try {
                byte[] bytes = Utils.getNBytesFromOffset(fileUploadStructure.filePath, (int) offset, limit);
                // make third request for first time
                new RequestFileUpload().fileUpload(token, offset, bytes, fileHashAsIdentity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                // update progress
                updateProgressView(fileHashAsIdentity, (int) progress);

                if (isFileExistInList(fileHashAsIdentity)) {
                    // handle when the file has already uploaded
                    onFileUploadComplete(fileHashAsIdentity, response);
                }
            } catch (Exception e) {
                Log.i("BreakPoint", e.getMessage());
            }
        }
    }

    /**
     * does file exist in the list
     * useful for preventing from calling onFileUploadComplete() multiple for a file
     *
     * @param fileHash file hash
     * @return boolean
     */
    private boolean isFileExistInList(String fileHash) {
        for (FileUploadStructure fileUploadStructure : mSelectedFiles) {
            if (fileUploadStructure.fileHash.equalsIgnoreCase(fileHash)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void OnFileUploadOption(int firstBytesLimit, int lastBytesLimit, int maxConnection, String fileHashAsIdentity, ProtoResponse.Response response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateLogs("CALLED: onFileUploadOptionResponse, " + Long.toString(System.currentTimeMillis()));
            }
        });

        try {
            FileUploadStructure fileUploadStructure = getSelectedFile(fileHashAsIdentity);
            // getting bytes from file as server said
            byte[] bytesFromFirst = Utils.getBytesFromStart(fileUploadStructure.filePath, firstBytesLimit);
            byte[] bytesFromLast = Utils.getBytesFromEnd(fileUploadStructure.filePath, lastBytesLimit);
            // make second request
            new RequestFileUploadInit().fileUploadInit(bytesFromFirst, bytesFromLast, fileUploadStructure.fileSize, fileHashAsIdentity, fileUploadStructure.fileName);
        } catch (IOException e) {
            Log.i("BreakPoint", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onFileUpload(final double progress, long nextOffset, int nextLimit, String fileHashAsIdentity, ProtoResponse.Response response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateLogs("CALLED: onFileUploadResponse, " + Long.toString(System.currentTimeMillis()));
                updateLogs("PROGRESS: " + Double.toString(progress));
            }
        });

        // for specific views, tags must be set with files hashes
        // get the view which has provided hash string
        // then do anything you want to do wit that view

        try {
            // update progress
            Log.i("SOC", "************************************ fileHashAsIdentity : " + fileHashAsIdentity + "  ||  progress : " + progress);
            updateProgressView(fileHashAsIdentity, (int) progress);
            Log.i("BreakPoint", fileHashAsIdentity + " > bad az update progress");

            if (progress != 100.0) {
                Log.i("BreakPoint", fileHashAsIdentity + " > 100 nist");
                FileUploadStructure fileUploadStructure = getSelectedFile(fileHashAsIdentity);
                Log.i("BreakPoint", fileHashAsIdentity + " > fileUploadStructure");
                byte[] bytes = Utils.getNBytesFromOffset(fileUploadStructure.filePath, (int) nextOffset, nextLimit);
                Log.i("BreakPoint", fileHashAsIdentity + " > after bytes");
                // make request till uploading has finished
                new RequestFileUpload().fileUpload(fileUploadStructure.token, nextOffset, bytes, fileHashAsIdentity);
                Log.i("BreakPoint", fileHashAsIdentity + " > after fileUpload request");
            } else {
                if (isFileExistInList(fileHashAsIdentity)) {
                    // handle when the file has already uploaded
                    onFileUploadComplete(fileHashAsIdentity, response);
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //updateLogs("FIRST START TILL NOW: " + Long.toString(System.currentTimeMillis() - WebSocketClient.firstTime));
                }
            });
        } catch (IOException e) {
            Log.i("BreakPoint", e.getMessage());
            e.printStackTrace();
        }
    }
}
