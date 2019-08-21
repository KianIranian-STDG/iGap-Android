package net.iGap.libs;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import net.iGap.R;

public class PlaqueView extends ConstraintLayout {

    private AppCompatEditText p1, p2, pCity, pAlphabet;
    private String strP1 = "", strP2 = "", strPCity = "", strPAlphabet = "";
    private boolean isEditable = true;

    public PlaqueView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        if (attrs == null) throw new IllegalArgumentException("attribute does not set.");

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PlaqueView);
        isEditable = typedArray.getBoolean(R.styleable.PlaqueView_pv_edit_mode, true);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_plaque_layout, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        initViews();
        setEditMode(isEditable);
        setupListeners();
    }

    private void initViews() {

        p1 = this.findViewById(R.id.pv_et_p1);
        p2 = this.findViewById(R.id.pv_et_p2);
        pCity = this.findViewById(R.id.pv_et_pCity);
        pAlphabet = this.findViewById(R.id.pv_et_pAlphabet);

    }

    private void setupListeners() {

        p1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                strP1 = s.toString().trim();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (strP1.length() == 2)
                    pAlphabet.requestFocus();
            }
        });

        pAlphabet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                strPAlphabet = s.toString().trim();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (strPAlphabet.length() == 1)
                    p2.requestFocus();
                else if (strPAlphabet.length() == 0)
                    p1.requestFocus();
            }
        });

        p2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                strP2 = s.toString().trim();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (strP2.length() == 3)
                    pCity.requestFocus();
                else if (strP2.length() == 0)
                    pAlphabet.requestFocus();
            }
        });

        pCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                strPCity = s.toString().trim();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (strPCity.length() == 0)
                    p2.requestFocus();
            }
        });
    }

    public void setEditMode(boolean isEditable) {

        if (isEditable) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                p1.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
                p2.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
                pCity.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
                pAlphabet.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
            }

            p1.setEnabled(true);
            p2.setEnabled(true);
            pCity.setEnabled(true);
            pAlphabet.setEnabled(true);


        } else {

            p1.setBackgroundResource(R.color.transparent);
            p2.setBackgroundResource(R.color.transparent);
            pCity.setBackgroundResource(R.color.transparent);
            pAlphabet.setBackgroundResource(R.color.transparent);

            p1.setEnabled(false);
            p2.setEnabled(false);
            pCity.setEnabled(false);
            pAlphabet.setEnabled(false);

        }

        requestLayout();
    }

    public boolean isPlaqueCorrect() {

        if (strP1.length() == 2 && strP2.length() == 3 && pAlphabet.length() != 0 && pCity.length() == 2) {
            return true;
        }

        return false;
    }

    /**
     * @return sample : 11 ص 123 11
     */
    public String getPlaqueWithAlphabet() {
        return strP1 + " " + strPAlphabet + " " + strP2 + " " + strPCity;
    }

    /**
     * @return 110812355 -> alphabet code
     */
    public String getPlaqueWithCode() {
        return strP1 + getAlphabetCode(strPAlphabet) + strP2 + strPCity;
    }

    private String getAlphabetCode(String strPAlphabet) {
        return "00"; //todo:// return correct id by list
    }

    public void setPlauqe(String p1, String p2, String pCity, String pAlphabet) {

        this.strP1 = p1.trim();
        this.strP2 = p2.trim();
        this.strPAlphabet = pAlphabet.trim();
        this.strPCity = pCity.trim();

        this.p1.setText(strP1);
        this.p2.setText(strP2);
        this.pCity.setText(strPCity);
        this.pAlphabet.setText(strPAlphabet);

    }

    public void setPlaque1(String strP1) {
        this.strP1 = strP1.trim();
        p1.setText(this.strP1);
    }

    public void setPlaque2(String strP2) {
        this.strP2 = strP2.trim();
        p2.setText(this.strP2);
    }

    public void setPlaqueCity(String strPCity) {
        this.strPCity = strPCity.trim();
        pCity.setText(this.strPCity);
    }

    public void setPlaqueAlphabet(String strPAlphabet) {
        this.strPAlphabet = strPAlphabet.trim();
        pCity.setText(this.strPAlphabet);
    }

    public String getPlaque1() {
        return strP1;
    }

    public String getPlaque2() {
        return strP2;
    }

    public String getPlaqueCity() {
        return strPCity;
    }

    public String getPlaqueAlphabet() {
        return strPAlphabet;
    }

    public AppCompatEditText getEditTextPlaque1() {
        return p1;
    }

    public AppCompatEditText getEditTextPlaque2() {
        return p2;
    }

    public AppCompatEditText getEditTextPlaqueCity() {
        return pCity;
    }

    public AppCompatEditText getEditTextPlaqueAlphabet() {
        return pAlphabet;
    }
}
