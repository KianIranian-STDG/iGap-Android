package net.iGap.story.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Build;

import net.iGap.G;
import net.iGap.helper.FileLog;
import net.iGap.module.AndroidUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CameraController {
    private static final int CORE_POOL_SIZE = 1;
    private static final int MAX_POOL_SIZE = 1;
    private static final int KEEP_ALIVE_SECONDS = 60;
    private static CameraController instance;
    private ThreadPoolExecutor threadPool;
    protected ArrayList<String> availableFlashModes = new ArrayList<>();
    private MediaRecorder recorder;
    private String recordedFile;
    private boolean mirrorRecorderVideo;
    protected volatile ArrayList<CameraInfo> cameraInfos;
    private boolean cameraInitied;
    private boolean loadingCameras;


    public static CameraController getInstance() {
        if (instance == null) {
            instance = new CameraController();
        }
        return instance;
    }


    private CameraController() {
        threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    }


    public ArrayList<CameraInfo> getCameras() {
        return cameraInfos;
    }

    public static Size chooseOptimalSize(List<Size> choices, int width, int height, Size aspectRatio) {
        List<Size> bigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (int a = 0; a < choices.size(); a++) {
            Size option = choices.get(a);
            if (option.getHeight() == option.getWidth() * h / w && option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else {
            return Collections.max(choices, new CompareSizesByArea());
        }
    }

    static class CompareSizesByArea implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() - (long) rhs.getWidth() * rhs.getHeight());
        }
    }

    public void openRound(final CameraSession session, final SurfaceTexture texture, final Runnable callback, final Runnable configureCallback) {
        if (session == null || texture == null) {

            return;
        }
        threadPool.execute(() -> {
            Camera camera = session.cameraInfo.camera;
            try {

                if (camera == null) {
                    camera = session.cameraInfo.camera = Camera.open(session.cameraInfo.cameraId);
                }
                Camera.Parameters params = camera.getParameters();

                session.configureRoundCamera();
                if (configureCallback != null) {
                    configureCallback.run();
                }
                camera.setPreviewTexture(texture);
                camera.startPreview();
                if (callback != null) {
                    G.runOnUiThread(callback);
                }

            } catch (Exception e) {
                session.cameraInfo.camera = null;
                if (camera != null) {
                    camera.release();
                }
                FileLog.e(e);
            }
        });
    }

    private static void checkXYSign(int x, int y) {
        if (x < 0) {
            throw new IllegalArgumentException("x must be >= 0");
        }
        if (y < 0) {
            throw new IllegalArgumentException("y must be >= 0");
        }
    }

    private static void checkWidthHeight(int width, int height) {
        if (width <= 0) {
            throw new IllegalArgumentException("width must be > 0");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("height must be > 0");
        }
    }

    public static Bitmap createBitmap(Bitmap source, int x, int y, int width, int height, Matrix m, boolean filter) {
        if (Build.VERSION.SDK_INT >= 21) {
            return Bitmap.createBitmap(source, x, y, width, height, m, filter);
        }
        checkXYSign(x, y);
        checkWidthHeight(width, height);
        if (x + width > source.getWidth()) {
            throw new IllegalArgumentException("x + width must be <= bitmap.width()");
        }
        if (y + height > source.getHeight()) {
            throw new IllegalArgumentException("y + height must be <= bitmap.height()");
        }
        if (!source.isMutable() && x == 0 && y == 0 && width == source.getWidth() && height == source.getHeight() && (m == null || m.isIdentity())) {
            return source;
        }

        int neww = width;
        int newh = height;
        Canvas canvas = new Canvas();
        Bitmap bitmap;
        Paint paint;

        Rect srcR = new Rect(x, y, x + width, y + height);
        RectF dstR = new RectF(0, 0, width, height);

        Bitmap.Config newConfig = Bitmap.Config.ARGB_8888;
        final Bitmap.Config config = source.getConfig();
        if (config != null) {
            switch (config) {
                case RGB_565:
                    newConfig = Bitmap.Config.ARGB_8888;
                    break;
                case ALPHA_8:
                    newConfig = Bitmap.Config.ALPHA_8;
                    break;
                case ARGB_4444:
                case ARGB_8888:
                default:
                    newConfig = Bitmap.Config.ARGB_8888;
                    break;
            }
        }

        if (m == null || m.isIdentity()) {
            bitmap = createBitmap(neww, newh, newConfig);
            paint = null;
        } else {
            final boolean transformed = !m.rectStaysRect();
            RectF deviceR = new RectF();
            m.mapRect(deviceR, dstR);
            neww = Math.round(deviceR.width());
            newh = Math.round(deviceR.height());
            bitmap = createBitmap(neww, newh, transformed ? Bitmap.Config.ARGB_8888 : newConfig);
            canvas.translate(-deviceR.left, -deviceR.top);
            canvas.concat(m);
            paint = new Paint();
            paint.setFilterBitmap(filter);
            if (transformed) {
                paint.setAntiAlias(true);
            }
        }
        bitmap.setDensity(source.getDensity());
        bitmap.setHasAlpha(source.hasAlpha());
        if (Build.VERSION.SDK_INT >= 19) {
            bitmap.setPremultiplied(source.isPremultiplied());
        }
        canvas.setBitmap(bitmap);
        canvas.drawBitmap(source, srcR, dstR, paint);
        try {
            canvas.setBitmap(null);
        } catch (Exception e) {
            //don't promt, this will crash on 2.x
        }
        return bitmap;
    }

    public static Bitmap createBitmap(int width, int height, Bitmap.Config config) {
        Bitmap bitmap;
//        if (Build.VERSION.SDK_INT < 21) {
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inDither = true;
//            options.inPreferredConfig = config;
//            options.inPurgeable = true;
//            options.inSampleSize = 1;
//            options.inMutable = true;
//            byte[] array = jpegData.get();
//            array[76] = (byte) (height >> 8);
//            array[77] = (byte) (height & 0x00ff);
//            array[78] = (byte) (width >> 8);
//            array[79] = (byte) (width & 0x00ff);
//            bitmap = BitmapFactory.decodeByteArray(array, 0, array.length, options);
//            bitmap.setHasAlpha(true);
//            bitmap.eraseColor(0);
//        }

            bitmap = Bitmap.createBitmap(width, height, config);

        if (config == Bitmap.Config.ARGB_8888 || config == Bitmap.Config.ARGB_4444) {
            bitmap.eraseColor(Color.TRANSPARENT);
        }
        return bitmap;
    }

    public boolean takePicture(final File path, final CameraSession session, final Runnable callback) {
        if (session == null) {
            return false;
        }
        final CameraInfo info = session.cameraInfo;
        final boolean flipFront = session.isFlipFront();
        Camera camera = info.camera;
        try {
            camera.takePicture(null, null, (data, camera1) -> {
                Bitmap bitmap = null;
                int size = (int) (1280 / AndroidUtils.density);
                String key = String.format(Locale.US, "%s@%d_%d", AndroidUtils.MD5(path.getAbsolutePath()), size, size);
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeByteArray(data, 0, data.length, options);
                    float scaleFactor = Math.max((float) options.outWidth / 1280, (float) options.outHeight / 1280);
                    if (scaleFactor < 1) {
                        scaleFactor = 1;
                    }
                    options.inJustDecodeBounds = false;
                    options.inSampleSize = (int) scaleFactor;
                    options.inPurgeable = true;
                    bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
                } catch (Throwable e) {
                    FileLog.e(e);
                }
                try {
                    if (info.frontCamera != 0 && flipFront) {
                        try {
                            Matrix matrix = new Matrix();
                            matrix.setRotate(getOrientation(data));
                            matrix.postScale(-1, 1);
                            Bitmap scaled = createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                            if (scaled != bitmap) {
                                bitmap.recycle();
                            }
                            FileOutputStream outputStream = new FileOutputStream(path);
                            scaled.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                            outputStream.flush();
                            outputStream.getFD().sync();
                            outputStream.close();
                            if (scaled != null) {
//                                ImageLoader.getInstance().putImageToCache(new BitmapDrawable(scaled), key);
                            }
                            if (callback != null) {
                                callback.run();
                            }
                            return;
                        } catch (Throwable e) {
                            FileLog.e(e);
                        }
                    }
                    FileOutputStream outputStream = new FileOutputStream(path);
                    outputStream.write(data);
                    outputStream.flush();
                    outputStream.getFD().sync();
                    outputStream.close();
                    if (bitmap != null) {
//                        ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmap), key);
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
                if (callback != null) {
                    callback.run();
                }
            });
            return true;
        } catch (Exception e) {
            FileLog.e(e);
        }
        return false;
    }

    private static int getOrientation(byte[] jpeg) {
        if (jpeg == null) {
            return 0;
        }

        int offset = 0;
        int length = 0;

        while (offset + 3 < jpeg.length && (jpeg[offset++] & 0xFF) == 0xFF) {
            int marker = jpeg[offset] & 0xFF;

            if (marker == 0xFF) {
                continue;
            }
            offset++;

            if (marker == 0xD8 || marker == 0x01) {
                continue;
            }
            if (marker == 0xD9 || marker == 0xDA) {
                break;
            }

            length = pack(jpeg, offset, 2, false);
            if (length < 2 || offset + length > jpeg.length) {
                return 0;
            }

            // Break if the marker is EXIF in APP1.
            if (marker == 0xE1 && length >= 8 &&
                    pack(jpeg, offset + 2, 4, false) == 0x45786966 &&
                    pack(jpeg, offset + 6, 2, false) == 0) {
                offset += 8;
                length -= 8;
                break;
            }

            offset += length;
            length = 0;
        }

        if (length > 8) {
            int tag = pack(jpeg, offset, 4, false);
            if (tag != 0x49492A00 && tag != 0x4D4D002A) {
                return 0;
            }
            boolean littleEndian = (tag == 0x49492A00);

            int count = pack(jpeg, offset + 4, 4, littleEndian) + 2;
            if (count < 10 || count > length) {
                return 0;
            }
            offset += count;
            length -= count;

            count = pack(jpeg, offset - 2, 2, littleEndian);
            while (count-- > 0 && length >= 12) {
                tag = pack(jpeg, offset, 2, littleEndian);
                if (tag == 0x0112) {
                    int orientation = pack(jpeg, offset + 8, 2, littleEndian);
                    switch (orientation) {
                        case 1:
                            return 0;
                        case 3:
                            return 180;
                        case 6:
                            return 90;
                        case 8:
                            return 270;
                    }
                    return 0;
                }
                offset += 12;
                length -= 12;
            }
        }
        return 0;
    }

    private static int pack(byte[] bytes, int offset, int length, boolean littleEndian) {
        int step = 1;
        if (littleEndian) {
            offset += length - 1;
            step = -1;
        }

        int value = 0;
        while (length-- > 0) {
            value = (value << 8) | (bytes[offset] & 0xFF);
            offset += step;
        }
        return value;
    }

    public void close(final CameraSession session, final CountDownLatch countDownLatch, final Runnable beforeDestroyRunnable) {
        session.destroy();
        threadPool.execute(() -> {
            if (beforeDestroyRunnable != null) {
                beforeDestroyRunnable.run();
            }
            if (session.cameraInfo.camera != null) {
                try {
                    session.cameraInfo.camera.stopPreview();
                    session.cameraInfo.camera.setPreviewCallbackWithBuffer(null);
                } catch (Exception e) {
                    FileLog.e(e);
                }
                try {
                    session.cameraInfo.camera.release();
                } catch (Exception e) {
                    FileLog.e(e);
                }
                session.cameraInfo.camera = null;
            }
            if (countDownLatch != null) {
                countDownLatch.countDown();
            }
        });
        if (countDownLatch != null) {
            try {
                countDownLatch.await();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public void open(final CameraSession session, final SurfaceTexture texture, final Runnable callback, final Runnable prestartCallback) {
        if (session == null || texture == null) {
            return;
        }
        threadPool.execute(() -> {
            Camera camera = session.cameraInfo.camera;
            try {
                if (camera == null) {
                    camera = session.cameraInfo.camera = Camera.open(session.cameraInfo.cameraId);
                }
                Camera.Parameters params = camera.getParameters();
                List<String> rawFlashModes = params.getSupportedFlashModes();

                availableFlashModes.clear();
                if (rawFlashModes != null) {
                    for (int a = 0; a < rawFlashModes.size(); a++) {
                        String rawFlashMode = rawFlashModes.get(a);
                        if (rawFlashMode.equals(Camera.Parameters.FLASH_MODE_OFF) || rawFlashMode.equals(Camera.Parameters.FLASH_MODE_ON)) {
                            availableFlashModes.add(rawFlashMode);
                        }
                    }
                    session.checkFlashMode(availableFlashModes.get(0));
                }

                if (prestartCallback != null) {
                    prestartCallback.run();
                }

                session.configurePhotoCamera();
                camera.setPreviewTexture(texture);
                camera.startPreview();
                if (callback != null) {
                    G.runOnUiThread(callback);
                }
            } catch (Exception e) {
                session.cameraInfo.camera = null;
                if (camera != null) {
                    camera.release();
                }
                FileLog.e(e);
            }
        });
    }

    public void initCamera(final Runnable onInitRunnable) {
        initCamera(onInitRunnable, false);
    }

    private void initCamera(final Runnable onInitRunnable, boolean withDelay) {

        loadingCameras = true;
        threadPool.execute(() -> {
            try {
                if (cameraInfos == null) {
//                    SharedPreferences preferences = MessagesController.getGlobalMainSettings();
//                    String cache = preferences.getString("cameraCache", null);
                    Comparator<Size> comparator = (o1, o2) -> {
                        if (o1.mWidth < o2.mWidth) {
                            return 1;
                        } else if (o1.mWidth > o2.mWidth) {
                            return -1;
                        } else {
                            if (o1.mHeight < o2.mHeight) {
                                return 1;
                            } else if (o1.mHeight > o2.mHeight) {
                                return -1;
                            }
                            return 0;
                        }
                    };
                    ArrayList<CameraInfo> result = new ArrayList<>();

                    int count = Camera.getNumberOfCameras();
                    Camera.CameraInfo info = new Camera.CameraInfo();

                    int bufferSize = 4;
                    for (int cameraId = 0; cameraId < count; cameraId++) {
                        Camera.getCameraInfo(cameraId, info);
                        CameraInfo cameraInfo = new CameraInfo(cameraId, info.facing);

                        Camera camera = Camera.open(cameraInfo.getCameraId());
                        Camera.Parameters params = camera.getParameters();

                        List<Camera.Size> list = params.getSupportedPreviewSizes();
                        for (int a = 0; a < list.size(); a++) {
                            Camera.Size size = list.get(a);
                            if (size.width == 1280 && size.height != 720) {
                                continue;
                            }
                            if (size.height < 2160 && size.width < 2160) {
                                cameraInfo.previewSizes.add(new Size(size.width, size.height));
//                                if (BuildVars.LOGS_ENABLED) {
//                                    FileLog.d("preview size = " + size.width + " " + size.height);
//                                }
                            }
                        }

                        list = params.getSupportedPictureSizes();
                        for (int a = 0; a < list.size(); a++) {
                            Camera.Size size = list.get(a);
                            if (size.width == 1280 && size.height != 720) {
                                continue;
                            }
                            if (!"samsung".equals(Build.MANUFACTURER) || !"jflteuc".equals(Build.PRODUCT) || size.width < 2048) {
                                cameraInfo.pictureSizes.add(new Size(size.width, size.height));
//                                if (BuildVars.LOGS_ENABLED) {
//                                    FileLog.d("picture size = " + size.width + " " + size.height);
//                                }
                            }
                        }

                        camera.release();
                        result.add(cameraInfo);

                        Collections.sort(cameraInfo.previewSizes, comparator);
                        Collections.sort(cameraInfo.pictureSizes, comparator);

                        bufferSize += 4 + 4 + 8 * (cameraInfo.previewSizes.size() + cameraInfo.pictureSizes.size());
                    }


                    cameraInfos = result;
                }

            } catch (Exception e) {


            }
        });
    }
}
