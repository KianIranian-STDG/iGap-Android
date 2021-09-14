package net.iGap.story;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.TextViewCompat;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.fragments.filterImage.BitmapUtils;
import net.iGap.helper.LayoutCreator;
import net.iGap.libs.emojiKeyboard.EmojiView;
import net.iGap.libs.emojiKeyboard.KeyboardView;
import net.iGap.libs.emojiKeyboard.NotifyFrameLayout;
import net.iGap.libs.emojiKeyboard.emoji.EmojiManager;
import net.iGap.libs.photoEdit.SaveSettings;
import net.iGap.messenger.ui.components.IconView;
import net.iGap.module.AndroidUtils;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.Theme;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.customView.EventEditText;
import net.iGap.module.structs.StructBottomSheet;
import net.iGap.observers.eventbus.EventManager;

import java.io.File;
import java.io.FileOutputStream;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.VISIBLE;

public class StatusTextFragment extends BaseFragment implements NotifyFrameLayout.Listener {

    private NotifyFrameLayout rootView;
    private EventEditText addTextEditTExt;
    private LinearLayout bottomLyoutPannel;
    private MaterialDesignTextView emoji;
    private MaterialDesignTextView addTextView;
    private MaterialDesignTextView palletTextView;
    private FrameLayout chatKeyBoardContainer;
    private EmojiView emojiView;
    private FrameLayout stickerBorder;
    private int emojiPadding;
    private boolean keyboardVisible;
    private int lastSizeChangeValue1;
    private boolean lastSizeChangeValue2;
    private FrameLayout floatActionLayout;
    private FrameLayout layoutRootView;
    private FrameLayout textLayoutRoot;
    private SharedPreferences emojiSharedPreferences;
    private LinearLayout bottomPanelRootView;
    private int keyboardHeight;
    private int keyboardHeightLand;
    private TextView textTv;
    private int editTextSize = 40;
    private int[] colors;
    private int counter = 0;
    private int random_int;
    private int colorCode;
    private Bitmap finalBitmap;
    private FrameLayout addTextRootView;
    private int typingState = 0;


    @Override
    public void onInflate(@NonNull Context context, @NonNull AttributeSet attrs, @Nullable Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);
        TypedArray a = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.VerticalSlideColorPicker, 0, 0);
        int colorsResourceId = a.getResourceId(R.styleable.VerticalSlideColorPicker_colors, R.array.default_status_color);
        colors = a.getResources().getIntArray(colorsResourceId);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        colors = context.getResources().getIntArray(R.array.default_colors);
        if (getActivity() != null) {
            emojiSharedPreferences = getActivity().getSharedPreferences(SHP_SETTING.EMOJI, MODE_PRIVATE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = new NotifyFrameLayout(context) {
            @Override
            public boolean dispatchKeyEventPreIme(KeyEvent event) {
                if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    if (isKeyboardVisible() || isPopupShowing()) {
                        showPopUPView(-1);
                        return true;
                    }
                    return false;
                }
                return super.dispatchKeyEventPreIme(event);
            }
        };
        rootView.setListener(this);
        rootView.setClickable(true);
        random_int = (int) Math.floor(Math.random() * ((colors.length - 1) + 1));


        textLayoutRoot = new FrameLayout(context);
        textLayoutRoot.setBackgroundColor(colorCode = colors[random_int]);
        rootView.addView(textLayoutRoot, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.CENTER));

        addTextRootView = new FrameLayout(getContext());
        stickerBorder = new FrameLayout(getContext());
        stickerBorder.setPadding(10, 10, 10, 10);
        addTextRootView.addView(stickerBorder, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.CENTER, 8, 8, 8, 8));

        textTv = new TextView(getContext());
        textTv.setId(R.id.story_added_text);
        textTv.setTextColor(Color.WHITE);
        textTv.setGravity(Gravity.CENTER);
        textTv.setTypeface(ResourcesCompat.getFont(getContext(), R.font.main_font_bold));
        textTv.setTextSize(30);
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(textTv, 22, 30, 1,
                TypedValue.COMPLEX_UNIT_DIP);
        stickerBorder.addView(textTv, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER, 14, 14, 14, 14));

        textLayoutRoot.addView(addTextRootView, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER));


        addTextEditTExt = new EventEditText(context);
        addTextEditTExt.setGravity(Gravity.CENTER);
        addTextEditTExt.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        addTextEditTExt.setBackground(null);
        addTextEditTExt.setTextColor(Color.WHITE);
        addTextEditTExt.setHint(getString(R.string.type_a_moment));
        addTextEditTExt.setTextSize(editTextSize);
        addTextEditTExt.setSingleLine(false);
        addTextEditTExt.setTypeface(ResourcesCompat.getFont(getContext(), R.font.main_font_bold));
        addTextEditTExt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(501)});
        addTextEditTExt.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);

        bottomPanelRootView = new LinearLayout(context);
        bottomPanelRootView.setOrientation(LinearLayout.VERTICAL);
        rootView.addView(bottomPanelRootView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.CENTER, 0, 0, 0, 0));

        bottomPanelRootView.addView(addTextEditTExt, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, 0, 1F, Gravity.CENTER, 5, 0, 5, 0));

        layoutRootView = new FrameLayout(context);
        bottomPanelRootView.addView(layoutRootView, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER, 0, 0, 0, 0));


        bottomLyoutPannel = new LinearLayout(context);
        bottomLyoutPannel.setOrientation(LinearLayout.HORIZONTAL);
        layoutRootView.addView(bottomLyoutPannel, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.MATCH_PARENT, Gravity.LEFT | Gravity.CENTER, 16, 0, 0, 0));

        floatActionLayout = new FrameLayout(context);
        Drawable drawable = Theme.createSimpleSelectorCircleDrawable(LayoutCreator.dp(56), Theme.getInstance().getToolbarBackgroundColor(context), Theme.getInstance().getAccentColor(context));
        floatActionLayout.setBackground(drawable);
        IconView addButton = new IconView(context);
        addButton.setIcon(R.string.icon_send);
        addButton.setIconColor(Color.WHITE);
        floatActionLayout.setVisibility(View.GONE);
        floatActionLayout.addView(addButton);
        layoutRootView.addView(floatActionLayout, LayoutCreator.createFrame(52, 52, Gravity.RIGHT | Gravity.CENTER, 0, 0, 16, 0));


        palletTextView = new MaterialDesignTextView(context);
        palletTextView.setGravity(Gravity.CENTER);
        palletTextView.setBackground(context.getResources().getDrawable(R.drawable.ic_palete));
        palletTextView.setTextColor(context.getResources().getColor(R.color.whit_background));
        palletTextView.setTypeface(ResourcesCompat.getFont(context, R.font.font_icons));
        palletTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 32);
        bottomLyoutPannel.addView(palletTextView, LayoutCreator.createLinear(32, 32, 0, 0, 0, 10));



        emoji = new MaterialDesignTextView(context);
        emoji.setGravity(Gravity.CENTER);
        emoji.setText(R.string.icon_emoji_smile);
        emoji.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 32);
        emoji.setTypeface(ResourcesCompat.getFont(context, R.font.font_icons));
        emoji.setTextColor(context.getResources().getColor(R.color.white));
        bottomLyoutPannel.addView(emoji, LayoutCreator.createLinear(30, 30, 0, 0, 12, 10));


        chatKeyBoardContainer = new FrameLayout(context);

        bottomPanelRootView.addView(chatKeyBoardContainer, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER));


        return rootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        palletTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (counter != StatusTextFragment.this.random_int) {
                    textLayoutRoot.setBackgroundColor(colorCode = colors[(++counter % colors.length)]);
                } else {
                    textLayoutRoot.setBackgroundColor(colorCode = colors[(++counter % colors.length)]);
                }

            }
        });

        addTextEditTExt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPopupShowing()) {
                    Log.e("fslkfjsdklfjh", "onClick2: ");
                    emoji.performClick();
                }
            }
        });
        emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isPopupShowing()) {
                    showPopUPView(KeyboardView.MODE_EMOJI);
                } else {
                    showPopUPView(KeyboardView.MODE_KEYBOARD);
                }

            }
        });

        addTextEditTExt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        addTextEditTExt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (typingState > charSequence.length()) {
                    if (charSequence.length() >= 17 && charSequence.length() <= 29) {
                        addTextEditTExt.setTextSize(editTextSize++);
                    }
                    if (charSequence.length() == 0) {
                        editTextSize = 40;
                        addTextEditTExt.setTextSize(editTextSize);
                    }
                } else {
                    if (charSequence.length() >= 17 && charSequence.length() <= 29) {
                        addTextEditTExt.setTextSize(editTextSize--);
                    }
                    if (charSequence.length() == 0) {
                        editTextSize = 40;
                        addTextEditTExt.setTextSize(editTextSize);
                    }
                }
                typingState = charSequence.length();
                if (charSequence.length() > 500) {
                    showPopUPView(-1);
                    new MaterialDialog.Builder(getContext()).title(getString(R.string.your_status_characters))
                            .titleGravity(GravityEnum.START)
                            .positiveText(R.string.ok)
                            .onNegative((dialog1, which) -> dialog1.dismiss()).show();
                }
                if (charSequence.length() > 0) {
                    floatActionLayout.setVisibility(VISIBLE);
                } else if (charSequence.length() == 0) {
                    floatActionLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        floatActionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopUPView(-1);
                String text = addTextEditTExt.getText().toString();
                //layoutRootView.setVisibility(View.GONE);
                addTextRootView.setVisibility(VISIBLE);
                textTv.setText(text);
                if (text.length() >= 300) {
                    TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(textTv, 15, 20, 1,
                            TypedValue.COMPLEX_UNIT_DIP);
                } else if (text.length() <= 100) {
                    TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(textTv, 22, 34, 1,
                            TypedValue.COMPLEX_UNIT_DIP);
                }
                bottomPanelRootView.setVisibility(View.GONE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        G.runOnUiThread(() -> {
                            Bitmap bitmap = writeTextOnDrawable(text);
                        });
                    }
                }, 100);


            }
        });
    }

    private float getTextHeight(String text, Paint paint) {

        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect.height();
    }

    private Bitmap writeTextOnDrawable(String text) {
        Rect bounds = new Rect(textLayoutRoot.getLeft(), textLayoutRoot.getTop(), textLayoutRoot.getRight(), textLayoutRoot.getBottom());


        textLayoutRoot.setDrawingCacheEnabled(true);
        Bitmap b = Bitmap.createBitmap(textLayoutRoot.getDrawingCache());
        Bitmap alteredBitmap = b.copy(Bitmap.Config.ARGB_8888, true);

        try {
            String savedPath = G.DIR_TEMP + "/" + System.currentTimeMillis() + "_edited_image.jpg";
            File imageFile = new File(savedPath);

            imageFile.createNewFile();
            File file = new File(imageFile.getAbsolutePath());
            FileOutputStream out = new FileOutputStream(file, false);
            alteredBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            alteredBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());

            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
            EventManager.getInstance(AccountManager.selectedAccount).postEvent(EventManager.STORY_STATUS_UPLOAD, imageFile.getAbsolutePath());


        } catch (Exception e) {

        }

        return alteredBitmap;
    }


    private void createEmojiView() {
        if (emojiView == null) {
            emojiView = new EmojiView(rootView.getContext(), false, true);
            emojiView.setVisibility(View.GONE);
            emojiView.setContentView(EmojiView.EMOJI);
            emojiView.setListener(new EmojiView.Listener() {
                @Override
                public void onBackSpace() {
                    if (addTextEditTExt.length() == 0) {
                        return;
                    }
                    addTextEditTExt.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                }

                @Override
                public void onStickerClick(StructIGSticker structIGSticker) {

                }

                @Override
                public void onStickerSettingClick() {

                }

                @Override
                public void onAddStickerClicked() {

                }

                @Override
                public void onEmojiSelected(String unicode) {
                    int i = addTextEditTExt.getSelectionEnd();

                    if (i < 0) i = 0;

                    try {
                        CharSequence sequence = EmojiManager.getInstance().replaceEmoji(unicode, addTextEditTExt.getPaint().getFontMetricsInt(), LayoutCreator.dp(22), false);
                        if (addTextEditTExt.getText() != null)
                            addTextEditTExt.setText(addTextEditTExt.getText().insert(i, sequence));
                        int j = i + sequence.length();
                        addTextEditTExt.setSelection(j, j);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        chatKeyBoardContainer.addView(emojiView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.BOTTOM));
    }

    private void showPopUPView(int event) {


        if (event == KeyboardView.MODE_KEYBOARD) {
            emoji.setText(getActivity().getString(R.string.icon_emoji_smile));
            chatKeyBoardContainer.setVisibility(VISIBLE);

            if (keyboardHeight <= 0) {
                keyboardHeight = emojiSharedPreferences.getInt(SHP_SETTING.KEY_KEYBOARD_HEIGHT, LayoutCreator.dp(300));
            }

            if (keyboardHeightLand <= 0) {
                keyboardHeightLand = emojiSharedPreferences.getInt(SHP_SETTING.KEY_KEYBOARD_HEIGHT_LAND, LayoutCreator.dp(300));
            }

            ViewGroup.LayoutParams layoutParams = chatKeyBoardContainer.getLayoutParams();
            layoutParams.width = AndroidUtils.displaySize.x;
            layoutParams.height = keyboardHeight;
            chatKeyBoardContainer.setLayoutParams(layoutParams);

            openKeyboardInternal();

            if (emojiView != null) {
                emojiView.setVisibility(View.GONE);
            }


            if (rootView != null) {
                rootView.requestLayout();

            }
        } else if (event == KeyboardView.MODE_EMOJI) {
            if (emojiView == null) {
                createEmojiView();
            }

            addTextEditTExt.requestFocus();
            emojiView.setVisibility(VISIBLE);
            chatKeyBoardContainer.setVisibility(VISIBLE);

            if (keyboardHeight <= 0) {
                keyboardHeight = emojiSharedPreferences.getInt(SHP_SETTING.KEY_KEYBOARD_HEIGHT, LayoutCreator.dp(300));
            }
            if (keyboardHeightLand <= 0) {
                keyboardHeightLand = emojiSharedPreferences.getInt(SHP_SETTING.KEY_KEYBOARD_HEIGHT_LAND, LayoutCreator.dp(300));
            }

            int currentHeight = AndroidUtils.displaySize.x > AndroidUtils.displaySize.y ? keyboardHeightLand : keyboardHeight;

            ViewGroup.LayoutParams layoutParams = chatKeyBoardContainer.getLayoutParams();
            layoutParams.width = AndroidUtils.displaySize.x;
            layoutParams.height = currentHeight;
            chatKeyBoardContainer.setLayoutParams(layoutParams);

            if (keyboardVisible) {
                closeKeyboard();
            }

            if (rootView != null) {
                emojiPadding = currentHeight;
                rootView.requestLayout();
                changeEmojiButtonImageResource(R.string.icon_keyboard);
            }
        } else {
            changeEmojiButtonImageResource(R.string.icon_emoji_smile);
            chatKeyBoardContainer.setVisibility(View.GONE);
            if (emojiView != null)
                emojiView.setVisibility(View.GONE);
            closeKeyboard();
        }

    }

    private void changeEmojiButtonImageResource(@StringRes int drawableResourceId) {
        emoji.setText(drawableResourceId);
    }

    private boolean isPopupShowing() {
        return emojiView != null && emojiView.getVisibility() == VISIBLE;
    }

    public void closeKeyboard() {
        addTextEditTExt.clearFocus();
        AndroidUtils.hideKeyboard(addTextEditTExt);
    }

    private boolean isKeyboardVisible() {
        return AndroidUtils.usingKeyboardInput || keyboardVisible;
    }

    private void openKeyboardInternal() {
        addTextEditTExt.requestFocus();
        AndroidUtils.showKeyboard(addTextEditTExt);
    }

    @Override
    public void onSizeChanged(int keyboardSize, boolean land) {
        if (keyboardSize > LayoutCreator.dp(50) && keyboardVisible /*&& !AndroidUtilities.isInMultiwindow && !forceFloatingEmoji*/) {
            if (land) {
                keyboardHeightLand = keyboardSize;
                if (emojiSharedPreferences != null)
                    emojiSharedPreferences.edit().putInt(SHP_SETTING.KEY_KEYBOARD_HEIGHT_LAND, keyboardHeightLand).apply();
            } else {
                keyboardHeight = keyboardSize;
                if (emojiSharedPreferences != null)
                    emojiSharedPreferences.edit().putInt(SHP_SETTING.KEY_KEYBOARD_HEIGHT, keyboardHeight).apply();
            }
        }

        if (isPopupShowing()) {
            int newHeight;
            if (land) {
                newHeight = keyboardHeightLand;
            } else {
                newHeight = keyboardHeight;
            }

            ViewGroup.LayoutParams layoutParams = chatKeyBoardContainer.getLayoutParams();
            if (layoutParams.width != AndroidUtils.displaySize.x || layoutParams.height != newHeight) {
                layoutParams.width = AndroidUtils.displaySize.x;
                layoutParams.height = newHeight;
                chatKeyBoardContainer.setLayoutParams(layoutParams);

                if (rootView != null) {
                    emojiPadding = layoutParams.height;
                    rootView.requestLayout();
                }
            }
        }
        if (lastSizeChangeValue1 == keyboardSize && lastSizeChangeValue2 == land) {
            return;
        }
        lastSizeChangeValue1 = keyboardSize;
        lastSizeChangeValue2 = land;

        boolean oldValue = keyboardVisible;
        keyboardVisible = keyboardSize > 0;
        if (keyboardVisible && isPopupShowing()) {
            showPopUPView(-1);
        }
        if (emojiPadding != 0 && !keyboardVisible && keyboardVisible != oldValue && !isPopupShowing()) {
            emojiPadding = 0;
            rootView.requestLayout();
        }
    }
}
