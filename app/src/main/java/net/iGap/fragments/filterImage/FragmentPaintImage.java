package net.iGap.fragments.filterImage;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.divyanshu.colorseekbar.ColorSeekBar;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.fragments.FragmentEditImage;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.ImageHelper;
import net.iGap.module.AttachFile;
import net.iGap.module.PaintImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FragmentPaintImage extends Fragment {
    private static String PATH = "net.iGap.fragments.filterImage.path";
    private String path;
    private Bitmap finalImage;

    private boolean isChange = false;
    private int minBrushSize = 12;
    private int brushSize = minBrushSize;
    private int paintColor = Color.BLACK;
    private SeekBar skBrushSize;
    private PaintImageView paintImageView;
    private TextView closeRevertBtn;

    public FragmentPaintImage() {
    }

    public static FragmentPaintImage newInstance(String path) {
        Bundle args = new Bundle();
        args.putString(PATH, path);
        FragmentPaintImage fragment = new FragmentPaintImage();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_paint_image, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lockScreenToPortrait();
        Bundle bundle = getArguments();
        if (bundle != null) {
            path = bundle.getString(PATH);
            if (path == null) {
                return;
            }
        } else {
            return;
        }

        ColorSeekBar colorSeekBar = view.findViewById(R.id.color_seek_bar);
        paintImageView = view.findViewById(R.id.paintView);
        closeRevertBtn = view.findViewById(R.id.pu_txt_agreeImage);
        paintImageView.setStrokeColor(paintColor);

        colorSeekBar.setOnColorChangeListener(i -> {
            paintColor = i;
            paintImageView.setStrokeColor(paintColor);
        });

        skBrushSize = view.findViewById(R.id.seekbar_brushSize);
        skBrushSize.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN));

        paintImageView.setImageURI(Uri.parse(path));
        initDialogBrush();

        view.findViewById(R.id.pu_txt_ok).setOnClickListener(v -> {
            saveImageAndFinish();
        });

        closeRevertBtn.setOnClickListener(v -> {
            if (paintImageView.getPaths().size() > 0) {
                paintImageView.undo();
            } else {
                popBackStackFragment();
            }
        });

        closeRevertBtn.setOnLongClickListener(v -> {
            popBackStackFragment();
            return false;
        });

        paintImageView.getOnPathChanged().observe(getViewLifecycleOwner(), count -> {
            closeRevertBtn.setText(count == 0 ? R.string.close_icon : R.string.forward_icon);
        });

    }

    private void saveImageAndFinish() {
        if (getActivity() == null) return;
        BitmapDrawable bd = (BitmapDrawable) paintImageView.getDrawable();
        if (bd == null || bd.getBitmap() == null) {
            Toast.makeText(getActivity(), R.string.error, Toast.LENGTH_SHORT).show();
            return;
        }
        finalImage = bd.getBitmap();
        final String path;
        if (Build.VERSION.SDK_INT < 29) {
            path = BitmapUtils.insertImage(getActivity().getContentResolver(), finalImage, System.currentTimeMillis() + "_profile.jpg", null);
        } else {
            path = storeBitmapInCache(finalImage);
        }
        if (FragmentEditImage.updateImage != null && path != null) {
            FragmentEditImage.updateImage.result(AttachFile.getFilePathFromUri(Uri.parse(path)));
        }
        popBackStackFragment();
    }

    private String storeBitmapInCache(Bitmap bitmap) {
        if (!HelperPermission.grantedUseStorage()) return null;

        String savedPath = G.DIR_TEMP + "/" + System.currentTimeMillis() + "_Painted.png";
        File imageFile = new File(savedPath);
//        imageFile.mkdir();
        try (OutputStream out = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            bitmap.recycle();
            return savedPath;
        } catch (IOException ignored) {
        }
        return null;
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private void lockScreenToPortrait() {
        if (getActivity() != null)
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void unlockScreen() {
        if (getActivity() != null)
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    private void initDialogBrush() {

        skBrushSize.setProgress(brushSize - minBrushSize);
        skBrushSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                brushSize = progress + minBrushSize;
                paintImageView.setStrokeWidth(brushSize);
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

    private Uri getImageContentUri(Context context, File imageFile) {
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

    private Bitmap getBitmapFile(Context context, String fileName) {
        File image = new File(fileName);
        return ImageHelper.decodeFile(image);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unlockScreen();
    }
}
