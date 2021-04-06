package net.iGap.camera;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.helper.LayoutCreator;

import java.util.ArrayList;
import java.util.List;

public class TextEditorDialogFragment extends DialogFragment {
    public static final String TAG = TextEditorDialogFragment.class.getSimpleName();
    private FrameLayout rootView;
    private TextView doneTextView;
    private EditText addTextEditTExt;
    private RecyclerView colorPickerRecylerView;
    private int colorCode;
    private InputMethodManager inputMethodManager;
    private OnTextEditorListener onTextEditorListener;
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

        addTextEditTExt = new EditText(getContext());
        addTextEditTExt.setGravity(Gravity.CENTER);
        addTextEditTExt.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        addTextEditTExt.setTextColor(getArguments() != null ? colorCode = getArguments().getInt(EXTRA_COLOR_CODE, Color.WHITE) : Color.WHITE);
        addTextEditTExt.setText(getArguments() != null ? getArguments().getString(EXTRA_INPUT_TEXT, "") : "");
        addTextEditTExt.setTextSize(40);
        addTextEditTExt.setSingleLine(false);
        addTextEditTExt.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        rootView.addView(addTextEditTExt, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.CENTER));

        doneTextView = new TextView(getContext());
        doneTextView.setBackground(getResources().getDrawable(R.drawable.background_border));
        doneTextView.setPadding(10, 10, 10, 10);
        doneTextView.setText("Done");
        doneTextView.setAllCaps(false);
        doneTextView.setTextColor(Color.WHITE);
        doneTextView.setTextSize(16);
        rootView.addView(doneTextView, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.TOP | Gravity.RIGHT, 20, 20, 20, 20));


        colorPickerRecylerView = new RecyclerView(getContext());
        colorPickerRecylerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        colorPickerRecylerView.setBackgroundColor(Color.BLACK);
        rootView.addView(colorPickerRecylerView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.BOTTOM));

        return rootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        ColorPickerAdapter colorPickerAdapter = new ColorPickerAdapter(getContext());

        colorPickerAdapter.setOnColorPickerClickListener(colorCode -> {
            this.colorCode = colorCode;
            addTextEditTExt.setTextColor(colorCode);
        });
        addTextEditTExt.setFocusable(true);
        colorPickerRecylerView.setAdapter(colorPickerAdapter);

        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        doneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputMethodManager.hideSoftInputFromWindow(addTextEditTExt.getWindowToken(), 0);
                String inputText = addTextEditTExt.getText().toString();
                if (!TextUtils.isEmpty(inputText) && onTextEditorListener != null) {
                    onTextEditorListener.onDone(inputText, colorCode);
                }
                dismiss();
            }
        });
    }

    public void setOnTextEditorListener(OnTextEditorListener onTextEditorListener) {
        this.onTextEditorListener = onTextEditorListener;
    }

    public interface OnTextEditorListener {
        void onDone(String inputText, int colorCode);
    }
}
