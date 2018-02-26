package net.iGap.fragments.filterImage;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperFragment;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import it.chengdazhi.styleimageview.StyleImageView;
import it.chengdazhi.styleimageview.Styler;

import static net.iGap.module.AndroidUtils.suitablePath;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentFilterImage extends Fragment {

    private ListView listView;
    private List<Integer> options;
    private List<String> optionTexts;

    private StyleImageView image;
    private View lastChosenOptionView;
    private CheckBox enableAnimationCheckBox;
    private EditText animationDurationEditText;
    private SeekBar brightnessBar;
    private SeekBar contrastBar;
    private SeekBar saturationBar;
    private final static String PATH = "PATH";
    private String path;


    public FragmentFilterImage() {
        // Required empty public constructor
    }

    public static FragmentFilterImage newInstance(String path) {
        Bundle args = new Bundle();
        args.putString(PATH, path);
        FragmentFilterImage fragment = new FragmentFilterImage();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_filter_image, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        image = (StyleImageView) view.findViewById(R.id.image);
        listView = (ListView) view.findViewById(R.id.list);
        initOptions();
        listView.setAdapter(new ListAdapter());

        Bundle bundle = getArguments();
        if (bundle != null) {
            path = bundle.getString(PATH);
        }

        G.imageLoader.displayImage(suitablePath(path), image);

        view.findViewById(R.id.pu_ripple_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HelperFragment(FragmentFilterImage.this).remove();
            }
        });


        view.findViewById(R.id.pu_txt_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(G.fragmentActivity)
                        .title("Clear")
                        .content("Are you sure")
                        .positiveText("ok")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                image.clearStyle();
                                if (saturationBar != null) {
                                    saturationBar.setProgress(100);
                                }
                            }
                        })
                        .negativeText("cancel")
                        .show();
            }
        });

        enableAnimationCheckBox = (CheckBox) view.findViewById(R.id.animation_checkbox);
        animationDurationEditText = (EditText) view.findViewById(R.id.duration_edittext);
        enableAnimationCheckBox.setChecked(image.isAnimationEnabled());
        animationDurationEditText.setText(String.valueOf(image.getAnimationDuration()));
        if (image.isAnimationEnabled()) {
            animationDurationEditText.setEnabled(true);
            animationDurationEditText.setTextColor(Color.BLACK);
        } else {
            animationDurationEditText.setEnabled(false);
            animationDurationEditText.setTextColor(Color.GRAY);
        }
        enableAnimationCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b) {
                    animationDurationEditText.setText("0");
                    animationDurationEditText.setEnabled(false);
                    animationDurationEditText.setTextColor(Color.GRAY);
                } else {
                    animationDurationEditText.setEnabled(true);
                    animationDurationEditText.setTextColor(Color.BLACK);
                }
                if (b) {
                    image.enableAnimation(Long.parseLong(animationDurationEditText.getText().toString()));
                } else {
                    image.disableAnimation();
                }
            }
        });
        animationDurationEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    image.enableAnimation(Long.parseLong(charSequence.toString()));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        brightnessBar = (SeekBar) view.findViewById(R.id.seekbar_brightness);
        contrastBar = (SeekBar) view.findViewById(R.id.seekbar_contrast);
        brightnessBar.setProgress(image.getBrightness() + 255);
        contrastBar.setProgress((int) (image.getContrast() * 100));
        brightnessBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                image.setBrightness(i - 255).updateStyle();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        contrastBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                image.setContrast(i / 100F).updateStyle();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

    }

    public void initOptions() {
        options = new ArrayList<>();
        optionTexts = new ArrayList<>();
        options.add(Styler.Mode.GREY_SCALE);
        optionTexts.add("Grey Scale");
        options.add(Styler.Mode.INVERT);
        optionTexts.add("Invert");
        options.add(Styler.Mode.RGB_TO_BGR);
        optionTexts.add("RGB to BGR");
        options.add(Styler.Mode.SEPIA);
        optionTexts.add("Sepia");
        options.add(Styler.Mode.BLACK_AND_WHITE);
        optionTexts.add("Black & White");
        options.add(Styler.Mode.BRIGHT);
        optionTexts.add("Bright");
        options.add(Styler.Mode.VINTAGE_PINHOLE);
        optionTexts.add("Vintage Pinhole");
        options.add(Styler.Mode.KODACHROME);
        optionTexts.add("Kodachrome");
        options.add(Styler.Mode.TECHNICOLOR);
        optionTexts.add("Technicolor");
        options.add(Styler.Mode.SATURATION);
        optionTexts.add("Saturation");
    }

    class ListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return options.size() + 1;
        }

        @Override
        public Object getItem(int i) {
            if (i >= options.size()) {
                return 100;
            }
            return options.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            final View result = getLayoutInflater().inflate(R.layout.option_item, null);
            if (i < options.size() && image.getMode() == options.get(i) || i >= options.size() && image.getMode() == Styler.Mode.NONE) {
                result.setBackgroundColor(G.context.getResources().getColor(R.color.gray_3c));
                lastChosenOptionView = result;
            }
            TextView title = (TextView) result.findViewById(R.id.text);
            title.setText(optionTexts.get(i));
            if (options.get(i) == Styler.Mode.SATURATION) {
                saturationBar = (SeekBar) result.findViewById(R.id.seekbar_saturation);
                saturationBar.setVisibility(View.VISIBLE);
                saturationBar.setProgress((int) (image.getSaturation() * 100));
                saturationBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        image.setSaturation(i / 100F).updateStyle();
                        if (lastChosenOptionView != result) {
                            if (lastChosenOptionView != null) {
                                lastChosenOptionView.setBackgroundColor(G.context.getResources().getColor(R.color.gray_3c));
                            }
                            result.setBackgroundColor(G.context.getResources().getColor(R.color.gray_3c));
                            lastChosenOptionView = result;
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });
            }

            result.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (lastChosenOptionView != null) {
                        lastChosenOptionView.setBackgroundColor(G.context.getResources().getColor(R.color.black));
                    }
                    view.setBackgroundColor(G.context.getResources().getColor(R.color.gray_3c));

                    image.setMode(options.get(i)).updateStyle();
                    if (saturationBar != null && options.get(i) != Styler.Mode.SATURATION) {
                        saturationBar.setProgress(100);
                    }

                    lastChosenOptionView = view;
                }
            });
            return result;
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}
