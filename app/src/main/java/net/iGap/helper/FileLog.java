
package net.iGap.helper;

import android.util.Log;

import net.iGap.BuildConfig;
import net.iGap.Config;
import net.iGap.G;
import net.iGap.helper.downloadFile.time.FastDateFormat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Locale;

public class FileLog {
    private static final String TAG = "iGapMessenger";
    private OutputStreamWriter streamWriter = null;
    private FastDateFormat dateFormat = null;
    private DispatchQueue logQueue = null;
    private File currentFile = null;
    private boolean inInitial;

    private static volatile FileLog instance = null;

    public static FileLog getInstance() {
        FileLog localInstance = instance;
        if (localInstance == null) {
            synchronized (FileLog.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new FileLog();
                }
            }
        }
        return localInstance;
    }

    public FileLog() {
        init();
    }

    public void init() {
        if (inInitial) {
            return;
        }
        dateFormat = FastDateFormat.getInstance("dd-MM-yyyy HH:mm:ss", Locale.US);
        try {
            File filesDir = G.context.getExternalFilesDir(null);
            if (filesDir == null) {
                return;
            }
            File dir = new File(filesDir.getAbsolutePath() + "/logs");
            dir.mkdirs();
            currentFile = new File(dir, dateFormat.format(System.currentTimeMillis()) + ".txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            logQueue = new DispatchQueue("logQueue");
            currentFile.createNewFile();
            FileOutputStream stream = new FileOutputStream(currentFile);
            streamWriter = new OutputStreamWriter(stream);
            streamWriter.write("******* start " + dateFormat.format(System.currentTimeMillis()) + " *******\n\n\n");
            streamWriter.write("----------- APPLICATION ----------- \n");
            streamWriter.write("APP_NAME: " + BuildConfig.APP_NAME + "\n");
            streamWriter.write("APPLICATION_ID: " + BuildConfig.APPLICATION_ID + "\n");
            streamWriter.write("BUILD_TYPE: " + BuildConfig.BUILD_TYPE + "\n");
            streamWriter.write("VERSION_NAME: " + BuildConfig.VERSION_NAME + "\n");
            streamWriter.write("VERSION_CODE: " + BuildConfig.VERSION_CODE + "\n\n\n");
            streamWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        inInitial = true;
    }

    public static void initied() {
        getInstance().init();
    }

    public static void e(final String message, final Throwable exception) {
        if (!Config.FILE_LOG_ENABLE) {
            return;
        }

        initied();
        Log.e(TAG, message, exception);
        if (getInstance().streamWriter != null) {
            getInstance().logQueue.postRunnable(() -> {
                try {
                    getInstance().streamWriter.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " E -> : " + message + "\n");
                    getInstance().streamWriter.write(exception.toString());
                    getInstance().streamWriter.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static void e(final String TAG, final String message) {
        if (!Config.FILE_LOG_ENABLE) {
            return;
        }

        initied();
        Log.e(TAG, message);
        if (getInstance().streamWriter != null) {
            getInstance().logQueue.postRunnable(() -> {
                try {
                    getInstance().streamWriter.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " E -> : " + message + "\n");
                    getInstance().streamWriter.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static void e(final String message) {
        if (!Config.FILE_LOG_ENABLE) {
            return;
        }

        initied();
        Log.e(TAG, message);
        if (getInstance().streamWriter != null) {
            getInstance().logQueue.postRunnable(() -> {
                try {
                    getInstance().streamWriter.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " E -> : " + message + "\n");
                    getInstance().streamWriter.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static void i(final String message) {
        if (!Config.FILE_LOG_ENABLE) {
            return;
        }

        initied();
        Log.i(TAG, message);
        if (getInstance().streamWriter != null) {
            getInstance().logQueue.postRunnable(() -> {
                try {
                    getInstance().streamWriter.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " I -> : " + message + "\n");
                    getInstance().streamWriter.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static void i(final String TAG, final String message) {
        if (!Config.FILE_LOG_ENABLE) {
            return;
        }

        initied();
        Log.i(TAG, message);
        if (getInstance().streamWriter != null) {
            getInstance().logQueue.postRunnable(() -> {
                try {
                    getInstance().streamWriter.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " " + TAG + " I -> : " + message + "\n");
                    getInstance().streamWriter.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static void e(final Throwable e) {
        if (!Config.FILE_LOG_ENABLE) {
            return;
        }

        initied();
        Log.e(TAG, "", e);
        if (getInstance().streamWriter != null) {
            getInstance().logQueue.postRunnable(() -> {
                try {
                    getInstance().streamWriter.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " E -> : " + e + "\n");
                    StackTraceElement[] stack = e.getStackTrace();
                    for (StackTraceElement stackTraceElement : stack) {
                        getInstance().streamWriter.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " E -> : " + stackTraceElement + "\n");
                    }
                    getInstance().streamWriter.flush();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            });
        } else {
            e.printStackTrace();
        }
    }
}
