package net.iGap.story;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.module.downloader.HttpRequest;

public class TextEditorDialogFragment extends DialogFragment {
    public static final String TAG = TextEditorDialogFragment.class.getSimpleName();
    private FrameLayout rootView;
    private TextView doneTextView;
    private EditText addTextEditTExt;
    private int colorCode;
    private InputMethodManager inputMethodManager;
    private OnTextEditorListener onTextEditorListener;
    private VerticalSlideColorPicker verticalSlideColorPicker;
    private LinearLayout editTextRootView;
    private int editTextSize = 40;
    private boolean firstTime = true;
    private static final String EXTRA_INPUT_TEXT = "extra_input_text";
    private static final String EXTRA_COLOR_CODE = "extra_color_code";

    public static TextEditorDialogFragment newInstance(FragmentActivity appCompatActivity) {
        Bundle args = new Bundle();
        TextEditorDialogFragment fragment = new TextEditorDialogFragment();
        fragment.setArguments(args);
        fragment.show(appCompatActivity.getSupportFragmentManager(), TAG);
        return fragment;
    }

    public static TextEditorDialogFragment newInstance(@NonNull FragmentActivity appCompatActivity,
                                                       @NonNull String inputText,
                                                       @ColorInt int initialColorCode) {
        Bundle args = new Bundle();
        args.putString(EXTRA_INPUT_TEXT, inputText);
        args.putInt(EXTRA_COLOR_CODE, initialColorCode);
        TextEditorDialogFragment fragment = new TextEditorDialogFragment();
        fragment.setArguments(args);
        fragment.show(appCompatActivity.getSupportFragmentManager(), TAG);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        //Make dialog full screen with transparent background
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            Window window = dialog.getWindow();
            if (window != null) {
                dialog.getWindow().setLayout(width, height);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            }
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = new FrameLayout(getContext());
        rootView.setBackgroundColor(Color.BLACK);
        rootView.setBackgroundColor(Color.TRANSPARENT);

        editTextRootView = new LinearLayout(getContext());
        editTextRootView.setOrientation(LinearLayout.HORIZONTAL);
        rootView.addView(editTextRootView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.CENTER));


        verticalSlideColorPicker = new VerticalSlideColorPicker(getContext(), null);
        editTextRootView.addView(verticalSlideColorPicker, LayoutCreator.createLinear(13, 300, Gravity.TOP | Gravity.RIGHT, 0, 60, 20, 0));

        addTextEditTExt = new EditText(getContext());
        addTextEditTExt.setGravity(Gravity.CENTER);
        addTextEditTExt.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        addTextEditTExt.setBackground(null);
        addTextEditTExt.setTextColor(getArguments() != null ? colorCode = getArguments().getInt(EXTRA_COLOR_CODE, Color.WHITE) : Color.WHITE);
        addTextEditTExt.setText(getArguments() != null ? getArguments().getString(EXTRA_INPUT_TEXT, "") : "");
        addTextEditTExt.setTextSize(editTextSize);
        addTextEditTExt.setSingleLine(false);
        addTextEditTExt.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        editTextRootView.addView(addTextEditTExt, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.CENTER));

        doneTextView = new TextView(getContext());
        doneTextView.setBackground(getResources().getDrawable(R.drawable.background_border));
        doneTextView.setPadding(10, 10, 10, 10);
        doneTextView.setText("Done");
        doneTextView.setAllCaps(false);
        doneTextView.setTextColor(Color.WHITE);
        doneTextView.setTextSize(16);
        rootView.addView(doneTextView, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.TOP | Gravity.RIGHT, 20, 20, 20, 20));


        verticalSlideColorPicker = new VerticalSlideColorPicker(getContext(), null);
        rootView.addView(verticalSlideColorPicker, LayoutCreator.createFrame(13, 300, Gravity.TOP | Gravity.RIGHT, 0, 60, 20, 0));

        return rootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        verticalSlideColorPicker.setOnColorChangeListener(new VerticalSlideColorPicker.OnColorChangeListener() {
            @Override
            public void onColorChange(int selectedColor) {
                if (selectedColor != 0) {
                    colorCode = selectedColor;
                    addTextEditTExt.setTextColor(colorCode);
                }

            }
        });

        addTextEditTExt.setFocusable(true);


        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        doneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputMethodManager.hideSoftInputFromWindow(addTextEditTExt.getWindowToken(), 0);
                String inputText = addTextEditTExt.getText().toString();
                if (!TextUtils.isEmpty(inputText) && onTextEditorListener != null) {
                    onTextEditorListener.onDone(inputText, colorCode,addTextEditTExt.getWidth());
                }
                dismiss();
            }
        });
        addTextEditTExt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() >= 17 && charSequence.length() <= 29 && firstTime) {
                    addTextEditTExt.setTextSize(editTextSize--);
                } else if (charSequence.length() >= 17 && charSequence.length() <= 29 && !firstTime) {
                    addTextEditTExt.setTextSize(editTextSize++);
                } else if (charSequence.length() > 29) {
                    firstTime = false;
                } else if (charSequence.length() == 0) {
                    editTextSize = 40;
                    addTextEditTExt.setTextSize(editTextSize);
                    firstTime = true;
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        addTextEditTExt.requestFocus();
    }

    public void setOnTextEditorListener(OnTextEditorListener onTextEditorListener) {
        this.onTextEditorListener = onTextEditorListener;
    }

    public interface OnTextEditorListener {
        void onDone(String inputText, int colorCode,int width);
    }
}
