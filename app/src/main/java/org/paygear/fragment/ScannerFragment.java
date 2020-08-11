package org.paygear.fragment;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.BarcodeView;
import com.squareup.picasso.Picasso;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.CongratulationsDialogBinding;
import net.iGap.databinding.QrVoucherDialogBinding;
import net.iGap.databinding.UnsuccessfulDialogBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.realm.RealmUserInfo;

import org.paygear.RaadApp;
import org.paygear.WalletActivity;
import org.paygear.model.ConfirmVoucherQr_Result;
import org.paygear.model.QRResponse;
import org.paygear.utils.QRUtils;
import org.paygear.utils.SettingHelper;
import org.paygear.web.Web;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ir.radsense.raadcore.OnFragmentInteraction;
import ir.radsense.raadcore.app.AlertDialog;
import ir.radsense.raadcore.app.NavigationBarActivity;
import ir.radsense.raadcore.model.Account;
import ir.radsense.raadcore.model.QR;
import ir.radsense.raadcore.utils.RaadCommonUtils;
import ir.radsense.raadcore.utils.Typefaces;
import ir.radsense.raadcore.widget.CircleImageTransform;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScannerFragment extends Fragment implements OnFragmentInteraction {

    private static final int GALLERY_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int CAPTURE_REQUEST_CODE = 300;
    private static final int LOCATION_REQUEST_CODE = 400;
    QrVoucherDialogBinding qrVoucherDialogBinding;
    CongratulationsDialogBinding successfulDialogBinding;
    UnsuccessfulDialogBinding unsuccessfulDialogBinding;
    private View rootView;
    private ImageView appBarImage;
    private TextView appBarTitle;
    private RelativeLayout contentLayout;
    private BarcodeView barcodeScannerView;
    private BeepManager beepManager;
    private ImageView codeImage;
    private ImageView flashImage;
    private TextView balanceText;
    private ImageView scanFrame;
    private TextView scanText;
    private FrameLayout progressLayout;
    private LinearLayout mCodeLayout;
    private EditText mCodeText;
    private View showingTipText;
    private int tipTextNumber = -1;
    private boolean isTorchOn;
    private Handler mHandler;
    private boolean isVisible = true;
    private Dialog qrVoucherDialog;
    private Dialog unsuccessfulDialog;
    private Dialog successfulDialog;

    public ScannerFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //setupTheme();

        View view = rootView = inflater.inflate(R.layout.fragment_scanner, container, false);
        ViewGroup rootView = view.findViewById(R.id.rootView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            rootView.setBackgroundColor(Color.parseColor(WalletActivity.backgroundTheme));
        }

        HelperToolbar toolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLogoShown(true)
                .setLeftIcon(R.string.back_icon)
                .setRightIcons(R.string.scan_qr_code_icon)
                .setDefaultTitle(getString(R.string.scanner))
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        if (getActivity() != null)
                            getActivity().onBackPressed();
                    }

                    @Override
                    public void onRightIconClickListener(View view) {
                        if (getActivity() instanceof NavigationBarActivity) {
                            isVisible = false;
                            ((NavigationBarActivity) getActivity()).pushFullFragment(new MyQRFragment(), "MyQRFragment");
                        } else {
                            isVisible = false;
                            new HelperFragment(getActivity().getSupportFragmentManager(), new MyQRFragment()).setReplace(false).load();
                        }
                    }
                });

        LinearLayout lytToolbar = view.findViewById(R.id.toolbarLayout);
        lytToolbar.addView(toolbar.getView());


        //ViewGroup root_current = view.findViewById(R.id.root_current);
        //root_current.setBackgroundColor(Color.parseColor(WalletActivity.backgroundTheme));


        balanceText = view.findViewById(R.id.balance);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            balanceText.setBackgroundColor(Color.parseColor(WalletActivity.primaryColor));
        }
        scanText = view.findViewById(R.id.scan);
        contentLayout = view.findViewById(R.id.content_layout);
        codeImage = view.findViewById(R.id.code);
        flashImage = view.findViewById(R.id.flash);
        scanFrame = view.findViewById(R.id.scan_frame);

        progressLayout = view.findViewById(R.id.progress_layout);
        ViewCompat.setBackground(progressLayout, RaadCommonUtils.getRectShape(getContext(),
                android.R.color.white, 8, 0));

        Typefaces.setTypeface(getContext(), Typefaces.IRAN_MEDIUM, balanceText);
        Typefaces.setTypeface(getContext(), Typefaces.IRAN_LIGHT, scanText);

        boolean hasCamera = getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);

        if (hasCamera) {
            beepManager = new BeepManager(getActivity());
            barcodeScannerView = view.findViewById(R.id.zxing_barcode_surface);
            onCameraPermission();
        } else {
            showCameraFeatures(false);
            Toast.makeText(getContext(), R.string.no_camera_found, Toast.LENGTH_SHORT).show();
        }

        //updateAppBar();

        if (RaadApp.paygearCard != null) {
            balanceText.setText(getString(R.string.paygear_card_balance) + "\n" +
                    RaadCommonUtils.formatPrice(RaadApp.paygearCard.balance, true));
        } else {
            balanceText.setText(getString(R.string.paygear_card_balance) + "\n" +
                    String.format(getString(R.string.wallet_Reial), RealmUserInfo.queryWalletAmount()));
        }

        flashImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (barcodeScannerView != null && barcodeScannerView.getCameraInstance() != null) {
                    isTorchOn = !isTorchOn;
                    barcodeScannerView.getCameraInstance().setTorch(isTorchOn);
                }
            }
        });

        codeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCodeInput();
            }
        });

        return view;
    }

    private void setupTheme() {

        try {
            //     WalletActivity.backgroundTheme = G.backgroundTheme;
            //  WalletActivity.backgroundTheme_2 = G.backgroundTheme_2;

            if (WalletActivity.backgroundTheme.length() == 9) {
                WalletActivity.backgroundTheme = "#FF" + WalletActivity.backgroundTheme.substring(3);
            }

            if (WalletActivity.backgroundTheme_2.length() == 9) {
                WalletActivity.backgroundTheme_2 = "#FF" + WalletActivity.backgroundTheme_2.substring(3);
            }

            //   WalletActivity.primaryColor = G.appBarColor ;

        } catch (Exception e) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isVisible = true;
        if (barcodeScannerView != null) {
            barcodeScannerView.resume();
            startDecoding();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isVisible = false;
        if (barcodeScannerView != null) {
            barcodeScannerView.pause();
            stopDecoding();
        }
    }

    @Override
    public void onFragmentResult(Fragment fragment, Bundle bundle) {
        if (fragment instanceof AccountPaymentDialog || fragment instanceof FactorPaymentDialog || fragment instanceof CreditPaymentDialog || fragment instanceof NewAccountPaymentDialog
                || fragment instanceof MyQRFragment) {
            if (bundle != null) {
                isVisible = bundle.getBoolean("Visible");
            } else {
                isVisible = true;
            }
        }
    }

    private void checkTips() {
        boolean showTips = SettingHelper.getBoolean(getContext(), SettingHelper.SCANNER_TIPS, true);
        if (showTips) {
            SettingHelper.putBoolean(getContext(), SettingHelper.SCANNER_TIPS, false);
            tipTextNumber = 0;
            addTipText(getString(R.string.scan_driver_or_store_qr), Gravity.CENTER_HORIZONTAL, false, scanFrame);
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (tipTextNumber == 0) {
                        tipTextNumber = 1;
                        contentLayout.removeView(showingTipText);
                        addTipText(getString(R.string.flash_light), Gravity.LEFT, true, flashImage);
                    } else if (tipTextNumber == 1) {
                        tipTextNumber = 2;
                        contentLayout.removeView(showingTipText);
                        addTipText(getString(R.string.entering_driver_code), Gravity.RIGHT, true, codeImage);
                    } else if (tipTextNumber == 2) {
                        tipTextNumber = -1;
                        contentLayout.removeView(showingTipText);
                        showingTipText = null;
                    }
                }
            });
        }
    }

    private void showCodeInput() {
        isVisible = false;
        AlertDialog d = new ir.radsense.raadcore.app.AlertDialog()
                .setAlertCancelable(false)
                .setAlertCancelableOnTouchOutside(false)
                .setMode(ir.radsense.raadcore.app.AlertDialog.MODE_INPUT)
                .setInputType(InputType.TYPE_CLASS_NUMBER)
                .setTitle(getString(R.string.receiver_code))
                .setPositiveAction(getString(R.string.ok))
                .setNegativeAction(getString(R.string.cancel))
                .setOnActionListener(new ir.radsense.raadcore.app.AlertDialog.OnAlertActionListener() {
                    @Override
                    public boolean onAction(int i, Object o) {
                        if (i == 1 && o instanceof String) {
                            String code = (String) o;
                            if (!TextUtils.isEmpty(code.trim())) {
                                loadQrData(code);
                                return true;
                            }
                        } else if (i == 0) {
                            isVisible = true;
                            return true;
                        }
                        return false;
                    }
                });
        d.show(getActivity().getSupportFragmentManager());
    }

    private void startDecoding() {
        try {
            barcodeScannerView.decodeSingle(new BarcodeCallback() {
                @Override
                public void barcodeResult(BarcodeResult result) {
                    //Log.i("ScannerFragment", "barcodeResult: " + result.getText());
                    if (!isVisible) {
                        restartDecoding();
                        return;
                    }

                    beepManager.playBeepSoundAndVibrate();
                    String content = result.getText();
                    if (content != null && content.startsWith("http")) {
                        int jjIndex = content.indexOf("jj=");
                        if (jjIndex > -1) {
                            String jjValue = content.substring(jjIndex + 3);
                            QR qr = QRUtils.getQRModel(jjValue);
                            if (qr != null) {
                                if (QRUtils.handleBarcode(getActivity(), qr)) {
                                    isVisible = false;
                                }
                            } else {
                                loadQrData(jjValue);
                            }
                        } else {
                            if (URLUtil.isValidUrl(content)) {
                                if (content.contains("paygear.ir") || content.contains("paygear.org")) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse(content));
                                    startActivity(intent);
                                } else if (content.contains("hyperme.ir")) {
                                    try {
                                        Uri uri = Uri.parse(content);
                                        List<String> pathSegments = uri.getPathSegments();
                                        loadHyperMeQrData(uri.getLastPathSegment(), Long.parseLong(pathSegments.get(0)), pathSegments.get(1));
                                    } catch (Exception e) {
                                        Toast.makeText(getContext(), R.string.data_unknown, Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(getContext(), R.string.data_unknown, Toast.LENGTH_LONG).show();
                                }

                            } else {
                                String value = content.substring(content.lastIndexOf("/"));
                                Matcher matcher = Pattern.compile("\\d+").matcher(value);
                                matcher.find();
                                loadQrData(matcher.group());
                            }
                            //int i = Integer.valueOf(matcher.group());

                            //Log.i("PostCameraFragment", "barcodeResult: data: " + matcher.group());

                        }
                    } else {
                        Toast.makeText(getContext(), R.string.data_unknown, Toast.LENGTH_LONG).show();
                    }

                    restartDecoding();
                }

                @Override
                public void possibleResultPoints(List<ResultPoint> resultPoints) {

                }
            });
        } catch (Exception exception) {
            Toast.makeText(getContext(), R.string.data_unknown, Toast.LENGTH_LONG).show();
        }
    }

    private void restartDecoding() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startDecoding();
            }
        }, 4000);
    }

    private void stopDecoding() {
        barcodeScannerView.stopDecoding();
    }

    private void onCameraPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            int result = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA);
            if (result != PackageManager.PERMISSION_GRANTED) {
                showCameraFeatures(false);
                requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                return;
            }
        }

        showCameraFeatures(true);
        checkTips();
        barcodeScannerView.pause();
        barcodeScannerView.resume();
        startDecoding();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onCameraPermission();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    private void showCameraFeatures(boolean show) {
        scanText.setVisibility(show ? View.VISIBLE : View.GONE);
        scanFrame.setVisibility(show ? View.VISIBLE : View.GONE);
        codeImage.setVisibility(show ? View.VISIBLE : View.GONE);
        flashImage.setVisibility(show ? View.VISIBLE : View.GONE);
    }


    private void addTipText(String text, int gravity, boolean above, View anchorView) {
        Context context = getContext();
        int dp8 = RaadCommonUtils.getPx(8, context);
        int dp6 = RaadCommonUtils.getPx(6, context);
        int dp16 = RaadCommonUtils.getPx(16, context);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        RelativeLayout.LayoutParams titleLayoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        titleLayoutParams.addRule(above ? RelativeLayout.ABOVE : RelativeLayout.BELOW, anchorView.getId());
        layout.setLayoutParams(titleLayoutParams);
        layout.setPadding(dp16, 0, dp16, 0);
        contentLayout.addView(layout);
        showingTipText = layout;

        TextView tipText = new AppCompatTextView(context);
        LinearLayout.LayoutParams tipTextParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tipTextParams.gravity = gravity;
        tipText.setLayoutParams(tipTextParams);
        tipText.setTextColor(Color.WHITE);
        tipText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tipText.setTypeface(Typefaces.get(context, Typefaces.IRAN_LIGHT));
        ViewCompat.setBackground(tipText, RaadCommonUtils.getRectShape(context, R.color.tip_text_back, 8, 0));
        tipText.setPadding(dp16, dp8, dp16, dp8);
        tipText.setText(text);

        View view = new View(context);
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(dp8 * 3, dp8);
        viewParams.rightMargin = dp6;
        viewParams.leftMargin = dp6;
        viewParams.gravity = gravity;
        view.setLayoutParams(viewParams);
        view.setBackgroundResource(R.drawable.tip_text_indicator);

        if (above) {
            layout.addView(tipText);
            layout.addView(view);
        } else {
            layout.addView(view);
            layout.addView(tipText);
            view.setRotation(180.0f);
        }
    }

    private void updateAppBar() {
        if (RaadApp.me == null)
            return;
        Account myAccount = RaadApp.me;
        appBarTitle.setText(getString(R.string.scanner));
        Picasso.with(G.context)
                .load(RaadCommonUtils.getImageUrl(myAccount.profilePicture))
                .transform(new CircleImageTransform())
                .error(R.drawable.ic_person_outline_black_24dp)
                .placeholder(R.drawable.ic_person_outline_black_24dp)
                .fit()
                .into(appBarImage);
    }

    private void loadQrData(String data) {
        isVisible = false;
        progressLayout.setVisibility(View.VISIBLE);

        Web.getInstance().getWebService().getQRData(data).enqueue(new Callback<QRResponse>() {
            @Override
            public void onResponse(Call<QRResponse> call, Response<QRResponse> response) {
                Boolean success = Web.checkResponse(ScannerFragment.this, call, response);
                if (success == null)
                    return;

                if (success) {
                    QRResponse qrData = response.body();

                    if (qrData.type != 10) {

                        if (!TextUtils.isEmpty(qrData.accountId)) {
                            NewAccountPaymentDialog.newInstance(qrData).show(
                                    getActivity().getSupportFragmentManager(), "NewAccountPaymentDialog");
                        } else {
                            Toast.makeText(getContext(), R.string.data_unknown, Toast.LENGTH_LONG).show();
                            isVisible = true;
                        }
                    } else {
                        showVoucherQrDialog(qrData);
                    }
                } else {
                    isVisible = true;
                }
                progressLayout.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<QRResponse> call, Throwable t) {
                if (Web.checkFailureResponse(ScannerFragment.this, call, t)) {
                    isVisible = true;
                    progressLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    private void loadHyperMeQrData(String data, final long amount, final String invoiceNumber) {
        isVisible = false;
        progressLayout.setVisibility(View.VISIBLE);

        Web.getInstance().getWebService().getQRData(data).enqueue(new Callback<QRResponse>() {
            @Override
            public void onResponse(Call<QRResponse> call, Response<QRResponse> response) {
                Boolean success = Web.checkResponse(ScannerFragment.this, call, response);
                if (success == null)
                    return;

                if (success) {
                    QRResponse qrData = response.body();

                    if (qrData.type != 10) {

                        if (!TextUtils.isEmpty(qrData.accountId)) {
                            NewAccountPaymentDialog.newInstance(qrData, amount, invoiceNumber).show(
                                    getActivity().getSupportFragmentManager(), "NewAccountPaymentDialog");
                        } else {
                            Toast.makeText(getContext(), R.string.data_unknown, Toast.LENGTH_LONG).show();
                            isVisible = true;
                        }
                    } else {
                        showVoucherQrDialog(qrData);
                    }
                } else {
                    isVisible = true;
                }
                progressLayout.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<QRResponse> call, Throwable t) {
                if (Web.checkFailureResponse(ScannerFragment.this, call, t)) {
                    isVisible = true;
                    progressLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    private void showVoucherQrDialog(final QRResponse qrData) {
        isVisible = false;
        qrVoucherDialog = new Dialog(getContext());
        WindowManager.LayoutParams params = qrVoucherDialog.getWindow().getAttributes();
        qrVoucherDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        qrVoucherDialog.getWindow().setAttributes(params);
        qrVoucherDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        qrVoucherDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        qrVoucherDialogBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.qr_voucher_dialog, null, false);
        qrVoucherDialog.setContentView(qrVoucherDialogBinding.getRoot());
        qrVoucherDialog.setCanceledOnTouchOutside(false);
        String message = getString(R.string.gift_message);
        try {
            message = message.replace("*", RaadCommonUtils.formatPrice(Long.parseLong(qrData.value), true));
        } catch (Exception e) {

        }
        qrVoucherDialogBinding.message.setText(message);


        qrVoucherDialogBinding.message.setTypeface(Typefaces.get(getContext(), "IRANYekanRegularMobile(FaNum)"));
        qrVoucherDialogBinding.confirmQr.setTypeface(Typefaces.get(getContext(), "IRANYekanRegularMobile(FaNum)"));
        qrVoucherDialogBinding.ignore.setTypeface(Typefaces.get(getContext(), "IRANYekanRegularMobile(FaNum)"));
        qrVoucherDialogBinding.title.setTypeface(Typefaces.get(getContext(), "IRANYekanRegularMobile(FaNum)"));

        WindowManager.LayoutParams wmlp = qrVoucherDialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.CENTER;
        wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmlp.height = WindowManager.LayoutParams.MATCH_PARENT;
        qrVoucherDialog.show();
        qrVoucherDialogBinding.ignore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isVisible = true;
                qrVoucherDialog.dismiss();
            }
        });
        qrVoucherDialogBinding.confirmQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qrVoucherDialog.dismiss();
                if (qrData.isDisabled) {
                    showUnsuccessfulDialog(getString(R.string.disabled_qr_voucher));
                } else {
                    progressLayout.setVisibility(View.VISIBLE);
                    Web.getInstance().getWebService().confirmVoucherQr(qrData.sequenceNumber).enqueue(new Callback<ConfirmVoucherQr_Result>() {
                        @Override
                        public void onResponse(Call<ConfirmVoucherQr_Result> call, Response<ConfirmVoucherQr_Result> response) {
                            Boolean success = Web.checkResponse(ScannerFragment.this, call, response);
                            if (success == null) {
                                isVisible = true;
                                return;
                            }

                            if (success) {
                                try {
                                    showSuccessfulDialog(response.body().message, qrData);
                              /*      if (getActivity() instanceof NavigationBarActivity) {
                                        ((NavigationBarActivity) getActivity()).broadcastMessage(
                                                ScannerFragment.this, null, CardsFragment.class);
                                    }*/
                                } catch (Exception e) {

                                }

                            } else {
                                showUnsuccessfulDialog(getString(R.string.disabled_qr_voucher));
                            }

                            progressLayout.setVisibility(View.GONE);


                        }

                        @Override
                        public void onFailure(Call<ConfirmVoucherQr_Result> call, Throwable t) {
                            if (Web.checkFailureResponse(ScannerFragment.this, call, t)) {
                                progressLayout.setVisibility(View.GONE);
                                showUnsuccessfulDialog(getString(R.string.disabled_qr_voucher));
                            }
                        }
                    });
                }

            }
        });

        qrVoucherDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(android.content.DialogInterface dialog, int keyCode, android.view.KeyEvent event) {
                return (keyCode == android.view.KeyEvent.KEYCODE_BACK);
            }
        });


    }

    private void showSuccessfulDialog(String message, QRResponse qrData) {
        isVisible = false;
        final Context context = getContext();
        if (context == null)
            return;
        successfulDialog = new Dialog(context);
        WindowManager.LayoutParams params = successfulDialog.getWindow().getAttributes();
        successfulDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        successfulDialog.getWindow().setAttributes(params);
        successfulDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        successfulDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        successfulDialogBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.congratulations_dialog, null, false);
        successfulDialog.setContentView(successfulDialogBinding.getRoot());
        successfulDialog.setCanceledOnTouchOutside(false);

        successfulDialogBinding.message.setText(message);
        successfulDialogBinding.message.setTypeface(Typefaces.get(context, "IRANYekanRegularMobile(FaNum)"));
        successfulDialogBinding.confirm.setTypeface(Typefaces.get(context, "IRANYekanRegularMobile(FaNum)"));
        successfulDialogBinding.title.setTypeface(Typefaces.get(context, "IRANYekanRegularMobile(FaNum)"));
        WindowManager.LayoutParams wmlp = successfulDialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.CENTER;
        wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmlp.height = WindowManager.LayoutParams.MATCH_PARENT;
        successfulDialog.show();
        successfulDialogBinding.confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, getString(R.string.toast_success_gift), Toast.LENGTH_LONG).show();
                if (getActivity() instanceof NavigationBarActivity) {
                 /*     for (int i = 0; i < RaadApp.cards.size(); i++) {

                          if (qrData.accountId.equals(RaadApp.cards.get(i).clubId)) {
                              ((NavigationBarActivity) getActivity()).pushFullFragment(CardsFragment.newInstance(true,i),"CardsFragment");
                              break;
                          }else {

                                  if (getActivity() instanceof NavigationBarActivity) {
                                        ((NavigationBarActivity) getActivity()).broadcastMessage(
                                                ScannerFragment.this, null, CardsFragment.class);
                                    }

                          }

                      }*/

                    ((NavigationBarActivity) getActivity()).pushFullFragment(CardsFragment.newInstance(true, qrData.accountId), "CardsFragment");


                }
                isVisible = false;
                successfulDialog.dismiss();
            }
        });


        successfulDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(android.content.DialogInterface dialog, int keyCode, android.view.KeyEvent event) {
                return (keyCode == android.view.KeyEvent.KEYCODE_BACK);
            }
        });
    }

    private void showUnsuccessfulDialog(String message) {
        isVisible = false;
        Context context = getContext();
        if (context == null)
            return;
        unsuccessfulDialog = new Dialog(context);
        WindowManager.LayoutParams params = unsuccessfulDialog.getWindow().getAttributes();
        unsuccessfulDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        unsuccessfulDialog.getWindow().setAttributes(params);
        unsuccessfulDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        unsuccessfulDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        unsuccessfulDialogBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.unsuccessful_dialog, null, false);
        unsuccessfulDialog.setContentView(unsuccessfulDialogBinding.getRoot());
        unsuccessfulDialog.setCanceledOnTouchOutside(false);

        unsuccessfulDialogBinding.message.setText(message);
        unsuccessfulDialogBinding.message.setTypeface(Typefaces.get(context, "IRANYekanRegularMobile(FaNum)"));
        unsuccessfulDialogBinding.confirm.setTypeface(Typefaces.get(context, "IRANYekanRegularMobile(FaNum)"));
        unsuccessfulDialogBinding.title.setTypeface(Typefaces.get(context, "IRANYekanRegularMobile(FaNum)"));
        WindowManager.LayoutParams wmlp = unsuccessfulDialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.CENTER;
        wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmlp.height = WindowManager.LayoutParams.MATCH_PARENT;
        unsuccessfulDialog.show();
        unsuccessfulDialogBinding.confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isVisible = true;
                unsuccessfulDialog.dismiss();
            }
        });

        unsuccessfulDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(android.content.DialogInterface dialog, int keyCode, android.view.KeyEvent event) {
                return (keyCode == android.view.KeyEvent.KEYCODE_BACK);
            }
        });
    }

}
