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
import android.net.Uri;
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

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.fragments.FragmentEditImage;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.ImageHelper;
import net.iGap.libs.ColorSeekBar;
import net.iGap.libs.photoEdit.PhotoEditor;
import net.iGap.libs.photoEdit.PhotoEditorView;
import net.iGap.libs.photoEdit.SaveSettings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FragmentPaintImage extends Fragment {
    private static String PATH = "net.iGap.fragments.filterImage.path";
    private String path;
    private int minBrushSize = 6;
    private int brushSize = minBrushSize;
    private int paintColor = Color.BLACK;
    private SeekBar skBrushSize;
    private PhotoEditor photoEditor;
    private View btnOk;

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
        PhotoEditorView paintImageView = view.findViewById(R.id.paintView);
        TextView closeRevertBtn = view.findViewById(R.id.pu_txt_agreeImage);

        photoEditor = new PhotoEditor.Builder(paintImageView).build();

        photoEditor.setBrushDrawingMode(true);
        photoEditor.setBrushSize(brushSize);
        photoEditor.setBrushColor(paintColor);

        colorSeekBar.setOnColorChangeListener(i -> {
            paintColor = i;
            photoEditor.setBrushColor(paintColor);
        });

        photoEditor.getOnPaintChanged().observe(getViewLifecycleOwner(), count -> {
            if (count == null) return;
            if (count > 0) {
                closeRevertBtn.setText(R.string.icon_forward);
            } else {
                closeRevertBtn.setText(R.string.icon_close);
            }
        });

        skBrushSize = view.findViewById(R.id.seekbar_brushSize);
        skBrushSize.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN));

        paintImageView.getSource().setImageURI(Uri.parse(path));
        initDialogBrush();

        btnOk = view.findViewById(R.id.pu_txt_ok);
        btnOk.setOnClickListener(v -> {
            saveImageAndFinish();
        });

        closeRevertBtn.setOnClickListener(v -> {
            if (photoEditor.getOnPaintChanged().getValue() != null) {
                if (photoEditor.getOnPaintChanged().getValue() > 0) {
                    photoEditor.undo();
                } else {
                    popBackStackFragment();
                }
            } else {
                popBackStackFragment();
            }
        });

        closeRevertBtn.setOnLongClickListener(v -> {
            popBackStackFragment();
            return false;
        });

    }

    @SuppressLint("MissingPermission")
    private void saveImageAndFinish() {
        if (getActivity() == null) return;
        btnOk.setEnabled(false);
        try {

            String savedPath = G.DIR_TEMP + "/" + System.currentTimeMillis() + "_Painted.png";
            File imageFile = new File(savedPath);

            imageFile.createNewFile();

            SaveSettings saveSettings = new SaveSettings.Builder()
                    .setTransparencyEnabled(true)
                    .build();

            if (!HelperPermission.grantedUseStorage()) return;

            photoEditor.saveAsFile(imageFile.getAbsolutePath(), saveSettings, new PhotoEditor.OnSaveListener() {
                @Override
                public void onSuccess(@NonNull String imagePath) {
                    if (FragmentEditImage.updateImage != null) {
                        FragmentEditImage.updateImage.result(imagePath);
                    }
                    popBackStackFragment();
                }

                @Override
                public void onFailure(@NonNull Exception exception) {
                    G.runOnUiThread(() -> {
                        btnOk.setEnabled(true);
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), R.string.error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } catch (IOException e) {
            btnOk.setEnabled(true);
            e.printStackTrace();
        }

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
                brushSize = seekBar.getProgress() + minBrushSize;
                photoEditor.setBrushSize(brushSize);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

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
        ImageHelper imageHelper = new ImageHelper();
        return imageHelper.decodeFile(image);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unlockScreen();
    }
}
