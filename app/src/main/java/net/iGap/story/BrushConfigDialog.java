package net.iGap.story;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.libs.ColorSeekBar;

public class BrushConfigDialog extends BottomSheetDialogFragment implements SeekBar.OnSeekBarChangeListener {
    public static final String TAG = BrushConfigDialog.class.getSimpleName();
    private LinearLayout rootView;
    private TextView opacityTextView;
    private TextView brushSizeTextView;
    private ColorSeekBar rvColors;
    private SeekBar opacitySeekBar;
    private SeekBar sizeSeekBar;
    private float brushSize;
    float brushOpacity;
    int brushColor;
    public BrushConfigDialog() {
        // Required empty public constructor
    }

    public void setBrushSize(float brushSize) {
        this.brushSize = brushSize;
    }

    public void setBrushOpacity(float brushOpacity) {
        this.brushOpacity = brushOpacity;
    }

    public void setBrushColor(int brushColor) {
        this.brushColor = brushColor;
    }


    private Properties mProperties;

    public interface Properties {
        void onColorChanged(int colorCode);

        void onOpacityChanged(int opacity);

        void onBrushSizeChanged(int brushSize);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = new LinearLayout(getContext());
        rootView.setOrientation(LinearLayout.VERTICAL);

        brushSizeTextView = new TextView(getContext());
        brushSizeTextView.setText(getString(R.string.lbl_magnify));
        rootView.addView(brushSizeTextView, LayoutCreator.createLinear(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, 8, 8, 8, 0));

        sizeSeekBar = new SeekBar(getContext());
        sizeSeekBar.setProgress((int) brushSize);
        rootView.addView(sizeSeekBar, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, 8, 8, 8, 0));

        opacityTextView = new TextView(getContext());
        opacityTextView.setText("Opacity");
        rootView.addView(opacityTextView, LayoutCreator.createLinear(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, 8, 22, 8, 0));

        opacitySeekBar = new SeekBar(getContext());
        opacitySeekBar.setProgress((int) brushOpacity);
        rootView.addView(opacitySeekBar, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, 8, 8, 8, 0));

        rvColors = new ColorSeekBar(getContext());
        rvColors.setThumbBorderColor(Color.WHITE);

//        rvColors.setOutlineAmbientShadowColor(Color.WHITE);
        rootView.addView(rvColors, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, 8, 32, 8, 8));

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        opacitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (mProperties != null) {
                    mProperties.onOpacityChanged(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (mProperties != null) {
                    mProperties.onBrushSizeChanged(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        rvColors.setOnColorChangeListener(colorCode -> {
            if (mProperties != null) {
                mProperties.onColorChanged(colorCode);
            }
        });

    }

    void setPropertiesChangeListener(Properties properties) {
        mProperties = properties;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
