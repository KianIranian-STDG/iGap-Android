package com.iGap.activitys;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.AsyncLayoutInflater;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
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
    private CheckBox logAll;
    private CheckBox logSectional;
    private CheckBox logFinally;
    private CheckBox logFileDetails;
    private TextView ofuTxt;
    private TextView gnbTxt;
    private TextView rTxt;

    public class LogStruct {
        public CharSequence message;
        public LogType logType;

        public LogStruct(CharSequence message, LogType logType) {
            this.message = message;
            this.logType = logType;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
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
                intent.setType("*/*");
                startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
            }
        });

        progressBarsLayout = (LinearLayout) findViewById(R.id.progressViews);
        logAll = (CheckBox) findViewById(R.id.logAll);
        logSectional = (CheckBox) findViewById(R.id.logSectional);
        logFinally = (CheckBox) findViewById(R.id.logFinally);
        logFileDetails = (CheckBox) findViewById(R.id.logFileDetails);

        ofuTxt = (TextView) findViewById(R.id.ofuTxt);
        gnbTxt = (TextView) findViewById(R.id.gnbTxt);
        rTxt = (TextView) findViewById(R.id.rTxt);

        //init upload files button
        Button uploadFilesButton = (Button) findViewById(R.id.uploadFiles);
        uploadFilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (FileUploadStructure fileUploadStructure : mSelectedFiles) {
                    // add progress bar
                    addProgressBar(fileUploadStructure.fileHash);

                    updateLogs("*** UPLOADING STARTED ***", "", LogType.FILE_DETAILS);
                    updateLogs("FILE NAME:", fileUploadStructure.fileName, LogType.FILE_DETAILS);
                    updateLogs("FILE SIZE:", Long.toString(fileUploadStructure.fileSize), LogType.FILE_DETAILS);
                    updateLogs("FILE HASH:", new String(fileUploadStructure.fileHash), LogType.FILE_DETAILS);

                    // start uploading
                    fileUploadStructure.uploadStartTime = System.currentTimeMillis();
                    G.uploaderUtil.startUploading(fileUploadStructure.fileSize, Long.toString(fileUploadStructure.messageId));
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
                findViewById(R.id.minsSection).setVisibility(View.INVISIBLE);
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

                findViewById(R.id.minsSection).setVisibility(View.INVISIBLE);
            }
        });

        // init logs view and its adapter
        adapter = new FileUploadTestAdapter();
        logs = (RecyclerView) findViewById(R.id.logs);
        logs.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        logs.setAdapter(adapter);

        logAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    adapter.showLogType(LogType.ALL);

                    logSectional.setChecked(false);
                    logFinally.setChecked(false);
                    logFileDetails.setChecked(false);
                }
            }
        });
        logAll.setChecked(true);
        logSectional.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    adapter.showLogType(LogType.SECTIONAL);

                    logAll.setChecked(false);
                    logFinally.setChecked(false);
                    logFileDetails.setChecked(false);
                }
            }
        });
        logFinally.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    adapter.showLogType(LogType.FINALLY);

                    logAll.setChecked(false);
                    logSectional.setChecked(false);
                    logFileDetails.setChecked(false);
                }
            }
        });
        logFileDetails.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    adapter.showLogType(LogType.FILE_DETAILS);

                    logAll.setChecked(false);
                    logSectional.setChecked(false);
                    logFinally.setChecked(false);
                }
            }
        });
    }

    /**
     * create horizontal progress bar
     *
     * @param fileHash file hash
     * @return ProgressBar
     */
    private void addProgressBar(final byte[] fileHash) {
        new AsyncLayoutInflater(getApplicationContext()).inflate(R.layout.msg_progress_item, null, new AsyncLayoutInflater.OnInflateFinishedListener() {
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

            FileUploadStructure fileUploadStructure = new FileUploadStructure(fileName, fileSize, filePath);
            fileUploadStructure.openFile(filePath);

            long fileHashStarted = System.currentTimeMillis();
            byte[] fileHash = Utils.getFileHash(fileUploadStructure);
            if (adapter != null) {
                updateLogs("FILE SELECTED:", fileName, LogType.ALL);
                updateLogs("ELAPSED FOR HASHING:", Long.toString(System.currentTimeMillis() - fileHashStarted), LogType.ALL);
            }
            fileUploadStructure.setFileHash(fileHash);

            mSelectedFiles.add(fileUploadStructure);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public enum LogType {
        ALL, SECTIONAL, FINALLY, FILE_DETAILS
    }

    private void updateLogs(String subject, String message, LogType logType) {
        if (adapter != null) {
            // make subject bold and colorized
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder(subject);
            stringBuilder.append(" ");
            stringBuilder.append(message);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.RED);
            StyleSpan styleSpan = new StyleSpan(android.graphics.Typeface.BOLD);
            stringBuilder.setSpan(colorSpan, 0, subject.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            stringBuilder.setSpan(styleSpan, 0, subject.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

            adapter.insert(new LogStruct(stringBuilder, logType));
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
    private FileUploadStructure getSelectedFile(byte[] fileHash) {
        for (FileUploadStructure structure : mSelectedFiles) {
            if (structure.fileHash == fileHash) {
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
            public void onLoginError(int majorCode, int minorCode) {

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
        final FileUploadStructure fileUploadStructure = getSelectedFile(fileHashAsIdentity.getBytes());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateLogs("CALLED (onFileUploadComplete) FOR HASH:", fileHashAsIdentity, LogType.ALL);

                updateLogs("ELAPSED TILL UPLOAD COMPLETED:", Long.toString(System.currentTimeMillis() - fileUploadStructure.uploadStartTime), LogType.FINALLY);
                updateLogs("ELAPSED FOR MAKING BYTES:", Long.toString(fileUploadStructure.getNBytesTime), LogType.FINALLY);
                updateLogs("ELAPSED FOR REQUESTING:", Long.toString(fileUploadStructure.sendRequestsTime), LogType.FINALLY);
                updateLogs("ELAPSED INSIDE ON_FILE_UPLOAD:", Long.toString(fileUploadStructure.elapsedInOnFileUpload), LogType.FINALLY);

                int avgOFU = adapter.average(FileUploadTestAdapter.AverageType.OFU);
                int avgGNB = adapter.average(FileUploadTestAdapter.AverageType.GNB);
                int avgR = adapter.average(FileUploadTestAdapter.AverageType.R);

                ofuTxt.setText("OFU: " + avgOFU);
                gnbTxt.setText("GNB: " + avgGNB);
                rTxt.setText("R: " + avgR);
                findViewById(R.id.minsSection).setVisibility(View.VISIBLE);
            }
        });

        // remove from selected files to prevent calling this method multiple times
        // multiple calling may occurs because of the server
        try {
            // FIXME: 9/19/2016 [Alireza Eskandarpour Shoferi] uncomment plz
            //removeFromSelectedFiles(fileHashAsIdentity);
        } catch (Exception e) {
            Log.i("BreakPoint", e.getMessage());
            e.printStackTrace();
        }

        // close file into structure
        try {
            if (fileUploadStructure != null) {
                fileUploadStructure.closeFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * remove from selected files
     *
     * @param fileHash file hash
     */
    private boolean removeFromSelectedFiles(byte[] fileHash) {
        for (FileUploadStructure fileUploadStructure : mSelectedFiles) {
            if (fileUploadStructure.fileHash == fileHash) {
                mSelectedFiles.remove(fileUploadStructure);
                return true;
            }
        }
        return false;
    }

    @Override
    public void OnFileUploadInit(String token, double progress, long offset, int limit, String identity, ProtoResponse.Response response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateLogs("CALLED: onFileUploadInitResponse, ", Long.toString(System.currentTimeMillis()), LogType.ALL);
            }
        });
        // token needed for requesting upload
        // updating structure with new token
        FileUploadStructure fileUploadStructure = getSelectedFile(identity.getBytes());
        fileUploadStructure.token = token;

        // not already uploaded
        if (progress != 100.0) {
            try {
                byte[] bytes = Utils.getNBytesFromOffset(fileUploadStructure, (int) offset, limit);
                // make third request for first time
                new RequestFileUpload().fileUpload(token, offset, bytes, identity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                // update progress
                updateProgressView(identity, (int) progress);

                if (isFileExistInList(identity.getBytes())) {
                    // handle when the file has already uploaded
                    onFileUploadComplete(identity, response);
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
    private boolean isFileExistInList(byte[] fileHash) {
        for (FileUploadStructure fileUploadStructure : mSelectedFiles) {
            if (fileUploadStructure.fileHash == fileHash) {
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
                updateLogs("CALLED: onFileUploadOptionResponse, ", Long.toString(System.currentTimeMillis()), LogType.ALL);
            }
        });

        try {
            FileUploadStructure fileUploadStructure = getSelectedFile(fileHashAsIdentity.getBytes());
            // getting bytes from file as server said
            byte[] bytesFromFirst = Utils.getBytesFromStart(fileUploadStructure, firstBytesLimit);
            byte[] bytesFromLast = Utils.getBytesFromEnd(fileUploadStructure, lastBytesLimit);
            // make second request
            new RequestFileUploadInit().fileUploadInit(bytesFromFirst, bytesFromLast, fileUploadStructure.fileSize, fileUploadStructure.fileHash, Long.toString(fileUploadStructure.messageId), fileUploadStructure.fileName);
        } catch (IOException e) {
            Log.i("BreakPoint", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onFileUpload(final double progress, long nextOffset, int nextLimit, String identity, ProtoResponse.Response response) {
        final long startOnFileUploadTime = System.currentTimeMillis();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateLogs("PROGRESS:", Double.toString(progress), LogType.ALL);
            }
        });

        // for specific views, tags must be set with files hashes
        // get the view which has provided hash string
        // then do anything you want to do wit that view

        try {
            // update progress
            Log.i("SOC", "************************************ identity : " + identity + "  ||  progress : " + progress);
            updateProgressView(identity, (int) progress);
            Log.i("BreakPoint", identity + " > bad az update progress");

            if (progress != 100.0) {
                Log.i("BreakPoint", identity + " > 100 nist");
                FileUploadStructure fileUploadStructure = getSelectedFile(identity.getBytes());
                Log.i("BreakPoint", identity + " > fileUploadStructure");
                final long startGetNBytesTime = System.currentTimeMillis();
                byte[] bytes = Utils.getNBytesFromOffset(fileUploadStructure, (int) nextOffset, nextLimit);

                fileUploadStructure.getNBytesTime += System.currentTimeMillis() - startGetNBytesTime;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateLogs("ELAPSED FOR GETTING N BYTES:", Long.toString(System.currentTimeMillis() - startGetNBytesTime), LogType.SECTIONAL);
                    }
                });
                Log.i("BreakPoint", identity + " > after bytes");
                // make request till uploading has finished
                final long startSendReqTime = System.currentTimeMillis();

                new RequestFileUpload().fileUpload(fileUploadStructure.token, nextOffset, bytes, identity);
                fileUploadStructure.sendRequestsTime += System.currentTimeMillis() - startSendReqTime;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateLogs("ELAPSED FOR REQUESTING:", Long.toString(System.currentTimeMillis() - startSendReqTime), LogType.SECTIONAL);
                    }
                });
                Log.i("BreakPoint", identity + " > after fileUpload request");

                fileUploadStructure.elapsedInOnFileUpload += System.currentTimeMillis() - startOnFileUploadTime;
            } else {
                if (isFileExistInList(identity.getBytes())) {
                    // handle when the file has already uploaded
                    onFileUploadComplete(identity, response);
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateLogs("ELAPSED IN ON_FILE_UPLOAD:", Long.toString(System.currentTimeMillis() - startOnFileUploadTime), LogType.SECTIONAL);
                }
            });
        } catch (IOException e) {
            Log.i("BreakPoint", e.getMessage());
            e.printStackTrace();
        }
    }
}