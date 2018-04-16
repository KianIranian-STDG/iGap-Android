package net.iGap.fragments.filterImage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.SeekBar;


public class EditImageFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {

    private EditImageFragmentListener listener;

    private SeekBar seekBarBrightness;

    private SeekBar seekBarContrast;

    private SeekBar seekBarSaturation;

    public void setListener(EditImageFragmentListener listener) {
        this.listener = listener;
    }

    public EditImageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_edit_image, container, false);
//
//
//        seekBarBrightness = view.findViewById(R.id.seekbar_brightness);
//        seekBarContrast = view.findViewById(R.id.seekbar_contrast);
//        seekBarSaturation = view.findViewById(R.id.seekbar_saturation);
//
//        // keeping brightness value b/w -100 / +100
//        seekBarBrightness.setMax(200);
//        seekBarBrightness.setProgress(100);
//
//        // keeping contrast value b/w 1.0 - 3.0
//        seekBarContrast.setMax(20);
//        seekBarContrast.setProgress(0);
//
//        // keeping saturation value b/w 0.0 - 3.0
//        seekBarSaturation.setMax(30);
//        seekBarSaturation.setProgress(10);
//
//        seekBarBrightness.setOnSeekBarChangeListener(this);
//        seekBarContrast.setOnSeekBarChangeListener(this);
//        seekBarSaturation.setOnSeekBarChangeListener(this);
//
//        return view;
//    }

//    @Override
//    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
//        if (listener != null) {
//
//            if (seekBar.getId() == R.id.seekbar_brightness) {
//                // brightness values are b/w -100 to +100
//                listener.onBrightnessChanged(progress - 100);
//            }
//
//            if (seekBar.getId() == R.id.seekbar_contrast) {
//                // converting int value to float
//                // contrast values are b/w 1.0f - 3.0f
//                // progress = progress > 10 ? progress : 10;
//                progress += 10;
//                float floatVal = .10f * progress;
//                listener.onContrastChanged(floatVal);
//            }
//
//            if (seekBar.getId() == R.id.seekbar_saturation) {
//                // converting int value to float
//                // saturation values are b/w 0.0f - 3.0f
//                float floatVal = .10f * progress;
//                listener.onSaturationChanged(floatVal);
//            }
//        }
//    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (listener != null)
            listener.onEditStarted();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (listener != null)
            listener.onEditCompleted();
    }

    public void resetControls() {
        seekBarBrightness.setProgress(100);
        seekBarContrast.setProgress(0);
        seekBarSaturation.setProgress(10);
    }

    public interface EditImageFragmentListener {
        void onBrightnessChanged(int brightness);

        void onSaturationChanged(float saturation);

        void onContrastChanged(float contrast);

        void onEditStarted();

        void onEditCompleted();
    }
}
