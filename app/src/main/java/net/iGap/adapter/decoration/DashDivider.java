/*
 * Copyright (c) 2017 Zaccc (http://github.com/zac4j).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.iGap.adapter.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.view.View;
import android.widget.LinearLayout;

import net.iGap.G;
import net.iGap.fragments.RegisteredContactsFragment;

import org.jetbrains.annotations.NotNull;

/**
 * Dash divider for linear layout manager
 * Created by Zaccc on 2017/8/15.
 */

public class DashDivider extends RecyclerView.ItemDecoration {

    public static final int HORIZONTAL = LinearLayout.HORIZONTAL;
    public static final int VERTICAL = LinearLayout.VERTICAL;

    private Paint mPaint;
    private TextPaint mTextPain;

    /**
     * Current orientation. Either {@link #HORIZONTAL} or {@link #VERTICAL}.
     */
    private int mOrientation;

    /**
     * Creates a divider {@link RecyclerView.ItemDecoration} that can be used with a
     * {@link LinearLayoutManager}.
     *
     * @param orientation Divider orientation. Should be {@link #HORIZONTAL} or {@link #VERTICAL}.
     */
    private DashDivider(int dashGap, int dashLength, int dashThickness, int dashColor, int textColor, float textSize, int orientation) {

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(dashColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(dashThickness);
        mPaint.setPathEffect(new DashPathEffect(new float[]{dashLength, dashGap}, 0));

        mTextPain = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPain.setColor(textColor);
        mTextPain.setTextSize(textSize);
        mTextPain.setTypeface(G.typeface_IRANSansMobile);

        setOrientation(orientation);
    }

    /**
     * Sets the orientation for this divider. This should be called if
     * {@link RecyclerView.LayoutManager} changes orientation.
     *
     * @param orientation {@link #HORIZONTAL} or {@link #VERTICAL}
     */
    public void setOrientation(int orientation) {
        if (orientation != HORIZONTAL && orientation != VERTICAL) {
            throw new IllegalArgumentException(
                    "Invalid orientation. It should be either HORIZONTAL or VERTICAL");
        }
        mOrientation = orientation;
    }

    @Override
    public void onDraw(@NotNull Canvas c, @NotNull RecyclerView parent, @NotNull RecyclerView.State state) {
        if (parent.getLayoutManager() == null) {
            return;
        }
        if (mOrientation == VERTICAL) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }
    }

    private void drawVertical(Canvas canvas, RecyclerView parent) {
        canvas.save();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int startX = child.getLeft() - params.leftMargin;
            final int stopX = child.getRight() + params.rightMargin;
            final int y = child.getBottom() + params.bottomMargin;

            canvas.drawLine(startX, y, stopX, y, mPaint);
            canvas.drawText("A", startX, y, stopX, y, mPaint);
        }
        canvas.restore();
    }

    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        canvas.save();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            RegisteredContactsFragment.ContactListAdapter adapter = (RegisteredContactsFragment.ContactListAdapter) parent.getAdapter();
            float[] t = new float[1];
            String s = adapter.getItemCharacter(parent.getChildLayoutPosition(child));
            mTextPain.getTextWidths("ب", t);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final float rightX = child.getRight() + params.rightMargin - (t[0] / 2) - (mPaint.getStrokeWidth() / 2);
            final int startY = child.getTop() - params.topMargin;
            final int stopY = child.getBottom() + params.bottomMargin;
            if (adapter.showCharacter(parent.getChildLayoutPosition(child))) {
                canvas.drawText(s, child.getRight() - t[0], child.getTop() + (mTextPain.getFontMetricsInt(null)*3/4f), mTextPain);
                canvas.drawLine(rightX, startY + mTextPain.getFontMetricsInt(null), rightX, stopY, mPaint);
            } else {
                canvas.drawLine(rightX, startY, rightX, stopY, mPaint);
            }
        }
        canvas.restore();
    }

    public static Builder with(@NonNull Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context == null");
        }
        return new Builder(context);
    }

    public static class Builder {
        private Context context;
        private int dashGap;
        private int dashLength;
        private int dashThickness;
        private int color;
        private int textColor;
        private int orientation = VERTICAL;
        private float textSize;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder dashGap(int gap) {
            if (gap <= 0) {
                throw new IllegalArgumentException("Dash gap must be greater than 0.");
            }
            this.dashGap = gap;
            return this;
        }

        public Builder dashLength(int length) {
            if (length <= 0) {
                throw new IllegalArgumentException("Dash length must be greater than 0.");
            }
            this.dashLength = length;
            return this;
        }

        public Builder dashThickness(int thickness) {
            if (thickness <= 0) {
                throw new IllegalArgumentException("Dash thickness must be greater than 0.");
            }
            this.dashThickness = thickness;
            return this;
        }

        public Builder color(@ColorInt int color) {
            this.color = color;
            return this;
        }

        public Builder textColor(@ColorInt int color) {
            this.textColor = color;
            return this;
        }

        public Builder textSize(float textSize) {
            this.textSize = textSize;
            return this;
        }

        public Builder orientation(int orientation) {
            if (orientation != HORIZONTAL && orientation != VERTICAL) {
                throw new IllegalArgumentException("Illegal dashed divider orientation!");
            }
            this.orientation = orientation;
            return this;
        }

        public DashDivider build() {
            if (dashGap <= 0) {
                throw new IllegalArgumentException("Dash gap must be greater than 0.");
            }
            if (dashLength <= 0) {
                throw new IllegalArgumentException("Dash length must be greater than 0.");
            }
            if (dashThickness <= 0) {
                throw new IllegalArgumentException("Dash thickness must be greater than 0.");
            }
            if (orientation != HORIZONTAL && orientation != VERTICAL) {
                throw new IllegalArgumentException("Illegal dashed divider orientation!");
            }
            return new DashDivider(dashGap, dashLength, dashThickness, color, textColor, textSize, orientation);
        }
    }
}
