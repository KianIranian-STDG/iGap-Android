package net.iGap.story.camera;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import net.iGap.R;
import net.iGap.helper.LayoutCreator;

import java.util.ArrayList;

public class EmojiDialogFrag extends BottomSheetDialogFragment {
    private FrameLayout rootView;
    private RecyclerView rvEmoji;

    public EmojiDialogFrag() {
        // Required empty public constructor
    }

    private EmojiListener mEmojiListener;

    public interface EmojiListener {
        void onEmojiClick(String emojiUnicode);
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        rootView = new FrameLayout(getContext());
        rootView.setBackgroundColor(Color.TRANSPARENT);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 5);
        rvEmoji = new RecyclerView(getContext());
        rvEmoji.setLayoutManager(gridLayoutManager);
        rootView.addView(rvEmoji, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER, 8, 8, 8, 8));


        dialog.setContentView(rootView);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) rootView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
        ((View) rootView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

        EmojiAdapter emojiAdapter = new EmojiAdapter();
        rvEmoji.setAdapter(emojiAdapter);
    }

    public void setEmojiListener(EmojiListener emojiListener) {
        mEmojiListener = emojiListener;
    }


    public class EmojiAdapter extends RecyclerView.Adapter<EmojiAdapter.ViewHolder> {

        ArrayList<String> emojisList = getEmojis(getActivity());
        TextView emojiTextView;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LinearLayout rootView = new LinearLayout(parent.getContext());

            emojiTextView = new TextView(parent.getContext());
            emojiTextView.setTextColor(Color.WHITE);
            emojiTextView.setTextSize(35);
            emojiTextView.setPadding(2, 2, 2, 2);
            rootView.addView(emojiTextView, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, 4, 4, 4, 4));


            return new ViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.txtEmoji.setText(emojisList.get(position));
        }

        @Override
        public int getItemCount() {
            return emojisList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView txtEmoji;

            ViewHolder(View itemView) {
                super(itemView);
                txtEmoji = emojiTextView;

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mEmojiListener != null) {
                            mEmojiListener.onEmojiClick(emojisList.get(getLayoutPosition()));
                        }
                        dismiss();
                    }
                });
            }
        }
    }

    private ArrayList<String> getEmojis(Context context) {
        ArrayList<String> convertedEmojiList = new ArrayList<>();
        String[] emojiList = context.getResources().getStringArray(R.array.photo_editor_emoji);
        for (String emojiUnicode : emojiList) {
            convertedEmojiList.add(convertEmoji(emojiUnicode));
        }
        return convertedEmojiList;
    }

    private static String convertEmoji(String emoji) {
        String returnedEmoji;
        try {
            int convertEmojiToInt = Integer.parseInt(emoji.substring(2), 16);
            returnedEmoji = new String(Character.toChars(convertEmojiToInt));
        } catch (NumberFormatException e) {
            returnedEmoji = "";
        }
        return returnedEmoji;
    }

}
