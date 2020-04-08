package net.iGap.fragments.filterImage;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.divyanshu.colorseekbar.ColorSeekBar;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.fragments.FragmentEditImage;
import net.iGap.helper.ImageHelper;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AttachFile;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPaintImage extends Fragment {
    private static String PATH = "net.iGap.fragments.filterImage.path";
    private String path;
    private Bitmap originalImage;
    private Bitmap finalImage;
    private ImageView imageFilter;
    // to backup image with filter applied
    private Bitmap filteredImage;

    public boolean isChange = false;
    boolean onstartPainting = false;
    ////
    AttachFile attachFile;
    private int minBrushSize = 12;
    private int brushSize = minBrushSize;
    private int paintColor = Color.BLACK;
    private boolean captureimage = false;
    private Bitmap mBitmap;
    private Dialog dialogBrush;
    private DrawingView drawingView;
    private Paint paint;
    private FrameLayout frameLayout;
    ColorSeekBar colorSeekBar;
    // TextView tvBrushSize;
    SeekBar skBrushSize;

    public FragmentPaintImage() {
        // Required empty public constructor


    }

    public static FragmentPaintImage newInstance(String path) {
        Bundle args = new Bundle();
        args.putString(PATH, path);
        FragmentPaintImage fragment = new FragmentPaintImage();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_paint_image, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() != null)
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Bundle bundle = getArguments();
        if (bundle != null) {
            path = bundle.getString(PATH);
            if (path == null) {
                return;
            }
        } else {
            return;
        }

        //  tvBrushSize = view.findViewById(R.id.textView_brush_size);
        colorSeekBar = view.findViewById(R.id.color_seek_bar);
//        colorSeekBar.set


        colorSeekBar.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int i) {
                Log.e("touching",colorSeekBar.getX()+"\n0"+
                        colorSeekBar.getBaseline()+"\n1"+
                        colorSeekBar.getDrawableState()+"\n2"+
                        colorSeekBar.getTranslationX()+"\n3"+
                        colorSeekBar.getScrollX()+"4");
                paintColor = i;
                setPaintColor();



            }
        });
        skBrushSize = view.findViewById(R.id.seekbar_brushSize);
        skBrushSize.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN));
        drawingView = new DrawingView(getContext(), false, null);
        frameLayout = (FrameLayout) view.findViewById(R.id.framexx);

        frameLayout.addView(drawingView);
        Log.e("pathingnng", path);
        imageFilter = view.findViewById(R.id.imageFilter);
        frameLayout = view.findViewById(R.id.framexx);
        loadImage();

        setPaintColor();
        setImageToBitmap(path);


        initDialogBrush();
        setPaintColor();


        view.findViewById(R.id.pu_txt_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // savePicToFile(false);
//                finalImage   = getBitmapFile(getActivity(), path, 300, 300);
                finalImage = mBitmap;
                final String path = BitmapUtils.insertImage(getActivity().getContentResolver(), finalImage, System.currentTimeMillis() + "_profile.jpg", null);
                Log.e("paintingxx", path);

                if (FragmentEditImage.updateImage != null && path != null) {
                    FragmentEditImage.updateImage.result(AttachFile.getFilePathFromUri(Uri.parse(path)));
                    Log.e("paintingxx", "entered");
                }

                popBackStackFragment();
            }
        });

        view.findViewById(R.id.pu_txt_agreeImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isChange) {
                    if (!AndroidUtils.canOpenDialog()) {
                        return;
                    }
                    new MaterialDialog.Builder(G.fragmentActivity)
                            .title(R.string.tab_filters)
                            .content(R.string.filter_cancel_content)
                            .positiveText(R.string.save)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    if (getActivity() != null) {
                                        finalImage = mBitmap;
                                        final String path = BitmapUtils.insertImage(getActivity().getContentResolver(), finalImage, System.currentTimeMillis() + "_profile.jpg", null);
                                        if (FragmentEditImage.updateImage != null && path != null) {
                                            FragmentEditImage.updateImage.result(AttachFile.getFilePathFromUri(Uri.parse(path)));
                                        }
                                    }
                                    popBackStackFragment();
                                }
                            }).onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            popBackStackFragment();
                        }
                    })
                            .negativeText(R.string.close)
                            .show();
                } else {
                    popBackStackFragment();
                }

            }
        });

    }

    void initDialogBrush() {


        // lp.height = WindowManager.LayoutParams.MATCH_PARENT;


        skBrushSize.setProgress(brushSize - minBrushSize);
        skBrushSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setPaintColor();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // tvBrushSize.setText(brushSize + "");
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                brushSize = progress + minBrushSize;
                // tvBrushSize.setText(brushSize + "");
            }
        });
    }

    public void popBackStackFragment() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (getActivity() != null) {
                        Log.wtf(this.getClass().getName(), "popBackStackFragment");
                        getActivity().onBackPressed();

                        if (G.iTowPanModDesinLayout != null) {
                            G.iTowPanModDesinLayout.onLayout(ActivityMain.chatLayoutMode.none);
                        }
                    }
                } catch (Exception empty) {
                    empty.printStackTrace();
                }
            }
        });
    }

    private void setImageToBitmap(String path) {

        //   Uri selectedImageUri =Uri.parse(path);


        Uri selectedImageUri = Uri.fromFile(new File(path));

        File file = new File(path);



        drawingView = new DrawingView(getContext(), true, getImageContentUri(getContext(), file));

        frameLayout.removeAllViews();
        frameLayout.addView(drawingView);


    }


    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    // load the default image from assets on app launch
    private void loadImage() {
        originalImage = getBitmapFile(getActivity(), path, 300, 300);
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        finalImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        imageFilter.setImageBitmap(originalImage);
    }



    public static Bitmap getBitmapFile(Context context, String fileName, int width, int height) {
        File image = new File(fileName);
        return ImageHelper.decodeFile(image);
    }

    public class DrawingView extends View {

        private static final float TOUCH_TOLERANCE = 4;
        public int width;
        public int height;
        Context context;
        private Canvas mCanvas;
        private Path mPath;
        private Paint mBitmapPaint;
        private Paint circlePaint;
        private Path circlePath;
        private Boolean fromGallery;
        private Uri PicAddress;
        private float mX, mY;

        public DrawingView(Context c, boolean FromGallery, Uri picAddress) {
            super(c);
            context = c;
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
            circlePaint = new Paint();
            circlePath = new Path();
            circlePaint.setAntiAlias(true);
            circlePaint.setColor(Color.BLUE);
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setStrokeJoin(Paint.Join.MITER);
            circlePaint.setStrokeWidth(4f);
            this.fromGallery = FromGallery;
            this.PicAddress = picAddress;
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);

            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mBitmap.eraseColor(Color.WHITE);
            mCanvas = new Canvas(mBitmap);

            if (fromGallery) {

                if (!captureimage) {
                    String[] projection = {MediaStore.MediaColumns.DATA};
                    Cursor cursor = getContext().getContentResolver().query(PicAddress, projection, null, null, null);
                    if (cursor != null) {

                        try {

                            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                            cursor.moveToFirst();
                            String selectedImagePath = cursor.getString(column_index);

                            File imgFile = new File(selectedImagePath);
                            Bitmap b = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                            if (b != null) {
                                b = Bitmap.createScaledBitmap(b, w, h, false);
                                Paint paint = new Paint();
                                mCanvas.drawBitmap(b, 0, 0, paint);
                            }
                        } finally {
                            cursor.close();
                        }
                    }
                } else {

                    String filePath = AttachFile.imagePath;
                    File imgFile = new File(filePath);
                    Bitmap b = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    if (b != null) {
                        b = Bitmap.createScaledBitmap(b, w, h, false);
                        Paint paint = new Paint();
                        mCanvas.drawBitmap(b, 0, 0, paint);
                    }
                }
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
            canvas.drawPath(mPath, paint);
            canvas.drawPath(circlePath, circlePaint);


        }

        private void touch_start(float x, float y) {
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
            isChange=true;
        }

        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                mX = x;
                mY = y;

                circlePath.reset();
                circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
            }
        }

        private void touch_up() {

            mPath.lineTo(mX, mY);

            circlePath.reset();
            // commit the path to our offscreen
            mCanvas.drawPath(mPath, paint);
            // kill this so we don't double draw
            mPath.reset();

            mCanvas.drawPoint(mX, mY, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
            return true;
        }
    }

    void setPaintColor() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(paintColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(brushSize);
    }

    void setPaintClear() {

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(Color.parseColor("#ffffff"));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(50);
    }
}
