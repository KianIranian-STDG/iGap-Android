package net.iGap.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.listeners.OnEmojiBackspaceClickListener;
import com.vanniktech.emoji.listeners.OnEmojiPopupDismissListener;
import com.vanniktech.emoji.listeners.OnEmojiPopupShownListener;
import com.vanniktech.emoji.listeners.OnSoftKeyboardCloseListener;
import com.vanniktech.emoji.listeners.OnSoftKeyboardOpenListener;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.filterImage.FragmentFilterImage;
import net.iGap.helper.HelperFragment;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AttachFile;
import net.iGap.module.EmojiEditTextE;
import net.iGap.module.MaterialDesignTextView;

import static android.app.Activity.RESULT_OK;
import static net.iGap.R.id.ac_ll_parent;
import static net.iGap.module.AndroidUtils.suitablePath;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentEditImage extends Fragment {

    private final static String PATH = "PATH";
    private String path;
    private ImageView imgEditImage;
    public static UpdateImage updateImage;
    private EmojiEditTextE edtChat;
    private MaterialDesignTextView imvSmileButton;
    private boolean isEmojiSHow = false;
    private boolean initEmoji = false;
    private EmojiPopup emojiPopup;

    public FragmentEditImage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_image, container, false);
    }

    public static FragmentEditImage newInstance(String path) {
        Bundle args = new Bundle();
        args.putString(PATH, path);
        FragmentEditImage fragment = new FragmentEditImage();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            path = bundle.getString(PATH);
        }

        imgEditImage = (ImageView) view.findViewById(R.id.imgEditImage);

        TextView txtEditImage = (TextView) view.findViewById(R.id.txtEditImage);
        txtEditImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidUtils.closeKeyboard(v);
                new HelperFragment(FragmentFilterImage.newInstance(path)).setReplace(false).load();

            }
        });

        G.imageLoader.displayImage(suitablePath(path), imgEditImage);

        updateImage = new UpdateImage() {
            @Override
            public void result(String pathImageFilter) {

                path = pathImageFilter;
                G.imageLoader.displayImage(suitablePath(path), imgEditImage);
                Log.i("SSSSSSSSSSSS", "result: " + pathImageFilter);
            }
        };


        view.findViewById(R.id.pu_ripple_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidUtils.closeKeyboard(v);
                new HelperFragment(FragmentEditImage.this).remove();
            }
        });

        view.findViewById(R.id.pu_txt_crop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidUtils.closeKeyboard(v);

                String newPath = "file://" + path;
                Uri uri = Uri.parse(newPath);

                CropImage.activity(uri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(120, 120)
                        .setAutoZoomEnabled(false)
                        .setInitialCropWindowPaddingRatio(.08f) // padding window from all
                        .setBorderCornerLength(50)
                        .setBorderCornerOffset(0)
                        .setAllowCounterRotation(true)
                        .setBorderCornerThickness(8.0f)
                        .setShowCropOverlay(true)
                        .setAspectRatio(1, 1)
                        .setFixAspectRatio(false)
                        .setBorderCornerColor(getResources().getColor(R.color.whit_background))
                        .setBackgroundColor(getResources().getColor(R.color.ou_background_crop))
                        .setScaleType(CropImageView.ScaleType.FIT_CENTER)
                        .start(G.fragmentActivity, FragmentEditImage.this);
            }
        });

        imvSmileButton = (MaterialDesignTextView) view.findViewById(R.id.chl_imv_smile_button);

        edtChat = (EmojiEditTextE) view.findViewById(R.id.chl_edt_chat);
        edtChat.requestFocus();

        edtChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmojiSHow) {

                    imvSmileButton.performClick();
                }
            }
        });


        imvSmileButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!initEmoji) {
                    initEmoji = true;
                    setUpEmojiPopup(view);
                }

                emojiPopup.toggle();
            }
        });


        MaterialDesignTextView imvSendButton = (MaterialDesignTextView) view.findViewById(R.id.pu_txt_sendImage);

        imvSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HelperFragment(FragmentEditImage.this).remove();
                FragmentChat.completeEditImage.result(path, edtChat.getText().toString());
                AndroidUtils.closeKeyboard(v);
            }
        });


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) { // result for crop
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imgEditImage.setImageURI(result.getUri());
                path = AttachFile.getFilePathFromUri(result.getUri());
            }
        }

    }

    public interface UpdateImage {
        void result(String path);
    }

    private void setUpEmojiPopup(View view) {
        emojiPopup = EmojiPopup.Builder.fromRootView(view.findViewById(ac_ll_parent)).setOnEmojiBackspaceClickListener(new OnEmojiBackspaceClickListener() {

            @Override
            public void onEmojiBackspaceClick(View v) {

            }
        }).setOnEmojiPopupShownListener(new OnEmojiPopupShownListener() {
            @Override
            public void onEmojiPopupShown() {
                changeEmojiButtonImageResource(R.string.md_black_keyboard_with_white_keys);
                isEmojiSHow = true;
            }
        }).setOnSoftKeyboardOpenListener(new OnSoftKeyboardOpenListener() {
            @Override
            public void onKeyboardOpen(final int keyBoardHeight) {

            }
        }).setOnEmojiPopupDismissListener(new OnEmojiPopupDismissListener() {
            @Override
            public void onEmojiPopupDismiss() {
                changeEmojiButtonImageResource(R.string.md_emoticon_with_happy_face);
                isEmojiSHow = false;
            }
        }).setOnSoftKeyboardCloseListener(new OnSoftKeyboardCloseListener() {
            @Override
            public void onKeyboardClose() {
                emojiPopup.dismiss();
            }
        }).build(edtChat);
    }

    private void changeEmojiButtonImageResource(@StringRes int drawableResourceId) {
        imvSmileButton.setText(drawableResourceId);
    }
}
