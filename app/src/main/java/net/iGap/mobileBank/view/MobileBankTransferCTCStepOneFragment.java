package net.iGap.mobileBank.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.MobileBankTransferCtcStepOneBinding;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.mobileBank.viewmoedel.MobileBankTransferCTCStepOneViewModel;

import java.util.Locale;

public class MobileBankTransferCTCStepOneFragment extends BaseAPIViewFrag<MobileBankTransferCTCStepOneViewModel> {

    MobileBankTransferCtcStepOneBinding binding;

    private static final String TAG = "MobileBankTransferCTCSt";

    public static MobileBankTransferCTCStepOneFragment newInstance() {
        return new MobileBankTransferCTCStepOneFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(MobileBankTransferCTCStepOneViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.mobile_bank_transfer_ctc_step_one, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        return attachToSwipeBack(binding.getRoot());

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        HelperToolbar mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLeftIcon(R.string.back_icon)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                })
                .setLogoShown(true);

        LinearLayout toolbarLayout = binding.Toolbar;
        toolbarLayout.addView(mHelperToolbar.getView());

        binding.originCard.cardNumberField1.requestFocus();

        textInputManagerOrigin();
        textInputManagerDest();
        textInputManagerValue();
    }

    private void textInputManagerValue() {
        binding.amount.addTextChangedListener(new TextWatcher() {
            boolean isSettingText;
            String mPrice;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPrice = s.toString().replaceAll(",", "");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isSettingText) return;

                isSettingText = true;
                String s1 = null;

                try {
                    s1 = String.format(Locale.US, "%,d", Long.parseLong(mPrice));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                binding.amount.setText(s1);
                binding.amount.setSelection(binding.amount.length());

                isSettingText = false;
                // ?????????
                /*String amount = s.toString().replace(",","");
                DecimalFormat df = new DecimalFormat(",###");
                binding.amount.setText(df.format(Integer.parseInt(amount)));*/
                viewModel.setAmount(s.toString().replace(",", ""));
            }
        });
    }

    private void textInputManagerOrigin() {
        // Pin 1
        binding.originCard.cardNumberField1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 4) {
                    binding.originCard.cardNumberField2.setEnabled(true);
                    binding.originCard.cardNumberField2.requestFocus();
                }
            }
        });

        // Pin 2
        binding.originCard.cardNumberField2.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                if (((AppCompatEditText) v).getEditableText().length() == 0) {
                    binding.originCard.cardNumberField1.requestFocus();
                    binding.originCard.cardNumberField2.setEnabled(false);
                    return true;
                }
            }
            return false;
        });
        binding.originCard.cardNumberField2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 1) {
                    String cardNum = binding.originCard.cardNumberField1.getText().toString()
                            + binding.originCard.cardNumberField2.getText().toString();
                    viewModel.setOriginCard(cardNum);
                } else {
                    viewModel.setOriginCard(null);
                }
                if (s.length() == 4) {
                    binding.originCard.cardNumberField3.setEnabled(true);
                    binding.originCard.cardNumberField3.requestFocus();
                }
            }
        });

        // Pin 3
        binding.originCard.cardNumberField3.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                if (((AppCompatEditText) v).getEditableText().length() == 0) {
                    binding.originCard.cardNumberField2.requestFocus();
                    binding.originCard.cardNumberField3.setEnabled(false);
                    return true;
                }
            }
            return false;
        });
        binding.originCard.cardNumberField3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 4) {
                    binding.originCard.cardNumberField4.setEnabled(true);
                    binding.originCard.cardNumberField4.requestFocus();
                }
            }
        });

        // Pin 4
        binding.originCard.cardNumberField4.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                String cardNum = binding.originCard.cardNumberField1.getText().toString()
                        + binding.originCard.cardNumberField2.getText().toString();
                viewModel.setCompleteOrigin(false, cardNum);
                if (((AppCompatEditText) v).getEditableText().length() == 0) {
                    binding.originCard.cardNumberField3.requestFocus();
                    binding.originCard.cardNumberField4.setEnabled(false);
                    return true;
                }
            }
            return false;
        });
        binding.originCard.cardNumberField4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 4) {
                    // build the Pin Code
                    String cardNum = binding.originCard.cardNumberField1.getText().toString()
                            + binding.originCard.cardNumberField2.getText().toString()
                            + binding.originCard.cardNumberField3.getText().toString()
                            + binding.originCard.cardNumberField4.getText().toString();
                    viewModel.setCompleteOrigin(true, cardNum);
                }
            }
        });
    }

    private void textInputManagerDest() {
        // Pin 1
        binding.destCard.cardNumberField1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 4) {
                    binding.destCard.cardNumberField2.setEnabled(true);
                    binding.destCard.cardNumberField2.requestFocus();
                }
            }
        });

        // Pin 2
        binding.destCard.cardNumberField2.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                if (((AppCompatEditText) v).getEditableText().length() == 0) {
                    binding.destCard.cardNumberField1.requestFocus();
                    binding.destCard.cardNumberField2.setEnabled(false);
                    return true;
                }
            }
            return false;
        });
        binding.destCard.cardNumberField2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 1) {
                    String cardNum = binding.destCard.cardNumberField1.getText().toString()
                            + binding.destCard.cardNumberField2.getText().toString();
                    viewModel.setDestCard(cardNum);
                } else {
                    viewModel.setDestCard(null);
                }
                if (s.length() == 4) {
                    binding.destCard.cardNumberField3.setEnabled(true);
                    binding.destCard.cardNumberField3.requestFocus();
                }
            }
        });

        // Pin 3
        binding.destCard.cardNumberField3.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                if (((AppCompatEditText) v).getEditableText().length() == 0) {
                    binding.destCard.cardNumberField2.requestFocus();
                    binding.destCard.cardNumberField3.setEnabled(false);
                    return true;
                }
            }
            return false;
        });
        binding.destCard.cardNumberField3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 4) {
                    binding.destCard.cardNumberField4.setEnabled(true);
                    binding.destCard.cardNumberField4.requestFocus();
                }
            }
        });

        // Pin 4
        binding.destCard.cardNumberField4.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                String cardNum = binding.destCard.cardNumberField1.getText().toString()
                        + binding.destCard.cardNumberField2.getText().toString();
                viewModel.setCompleteDest(false, cardNum);
                if (((AppCompatEditText) v).getEditableText().length() == 0) {
                    binding.destCard.cardNumberField3.requestFocus();
                    binding.destCard.cardNumberField4.setEnabled(false);
                    return true;
                }
            }
            return false;
        });
        binding.destCard.cardNumberField4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 4) {
                    // build the Pin Code
                    String cardNum = binding.destCard.cardNumberField1.getText().toString()
                            + binding.destCard.cardNumberField2.getText().toString()
                            + binding.destCard.cardNumberField3.getText().toString()
                            + binding.destCard.cardNumberField4.getText().toString();
                    viewModel.setCompleteDest(true, cardNum);
                }
            }
        });
    }
}
