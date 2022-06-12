package net.iGap.messenger.ui.cell;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.LayoutCreator;

public class EditTextCell extends FrameLayout {
    private final EditText editText;
    private final boolean isRtl = G.isAppRtl;
    private final int padding = 16;

    public EditTextCell(@NonNull Context context) {
        super(context);
        editText = new EditText(context);
        editText.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        editText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        addView(editText, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, (isRtl ? Gravity.LEFT : Gravity.RIGHT) | Gravity.TOP, isRtl ? padding : padding * 2, 0, isRtl ? padding * 2 : padding, 0));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), LayoutCreator.dp(50));
        editText.measure(MeasureSpec.makeMeasureSpec(widthMeasureSpec - LayoutCreator.dp(2 * padding), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY));
    }

    public void setValues(String hint, int textColor, int backgroundColor, boolean isSingleLine) {
        editText.setTextColor(textColor);
        editText.setHint(hint);
        if (isSingleLine)
            editText.setSingleLine();
        editText.setBackgroundColor(backgroundColor);
    }

    public void setTextColor(int color) {
        editText.setTextColor(color);
        invalidate();
    }

    public void setText(String text){
        editText.setText(text);
    }

    public String getText(){
       return editText.getText().toString();
    }

    public int length(){
       return editText.length();
    }
}